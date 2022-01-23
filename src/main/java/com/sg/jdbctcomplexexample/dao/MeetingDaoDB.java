package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import com.sg.jdbctcomplexexample.entity.Meeting;
import com.sg.jdbctcomplexexample.entity.Room;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author: Steven Vallarsa 
 * email: stevenvallarsa@gmail.com 
 * date: 2022-01-21
 * purpose:
 */
@Repository
public class MeetingDaoDB implements MeetingDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Meeting> getAllMeetings() {
        final String GET_ALL_MEETINGS = "SELECT * FROM meeting";
        List<Meeting> meetings = jdbc.query(GET_ALL_MEETINGS, new MeetingMapper());
        addRoomAndEmployeeToMeeting(meetings);
        return meetings;
    }

    @Override
    public Meeting getMeetingByid(int id) {
        try {
            final String GET_MEETING_BY_ID = "SELECT * FROM meeting WHERE id = ?";
            Meeting meeting = jdbc.queryForObject(GET_MEETING_BY_ID, new MeetingMapper(), id);
            meeting.setRoom(getRoomForMeeting(meeting));
            meeting.setAttendees(getEmployeesForMeeting(meeting));
            return meeting;
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    private Room getRoomForMeeting(Meeting meeting) {
        final String SELECT_ROOM_FOR_MEETING = "SELECT r.* FROM room r JOIN meeting m ON r.id = m.roomID WHERE m.id = ?";
        return jdbc.queryForObject(SELECT_ROOM_FOR_MEETING, new RoomDAODB.RoomMapper(), meeting.getId());
    }
    
    private List<Employee> getEmployeesForMeeting(Meeting meeting) {
        final String SELECT_EMPLOYEES_FOR_MEETING = "SELECT e.* FROM employee e JOIN meeting_employee me ON e.id = me.employeeID WHERE me.meetingID = ?";
        return jdbc.query(SELECT_EMPLOYEES_FOR_MEETING, new EmployeeDAODB.EmployeeMapper(), meeting.getId());
    }

    @Override
    @Transactional
    public Meeting addMeeting(Meeting meeting) {
        final String INSERT_MEETING = "INSERT INTO meeting (name, time, roomID) VALUES(?,?,?)";
        jdbc.update(INSERT_MEETING, 
                meeting.getName(), 
                Timestamp.valueOf(meeting.getTime()), 
                meeting.getRoom().getId());
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        meeting.setId(newId);
        insertMeetingEmployee(meeting);
        return meeting;
    }
    
    private void insertMeetingEmployee(Meeting meeting) {
        final String INSERT_MEETING_EMPLOYEE = "INSERT INTO meeting_employee (meetingID, employeeID) VALUES(?,?)";
        for(Employee employee : meeting.getAttendees()) {
            jdbc.update(INSERT_MEETING_EMPLOYEE, 
                meeting.getId(), 
                employee.getId());
        }
    }

    @Override
    @Transactional
    public void updateMeeting(Meeting meeting) {
        final String UPDATE_MEETING = "UPDATE meeting SET name = ?, time = ?, roomID = ? WHERE id = ?";
        jdbc.update(UPDATE_MEETING, 
                meeting.getName(),
                Timestamp.valueOf(meeting.getTime()),
                meeting.getRoom().getId(),
                meeting.getId());
        
        final String DELETE_MEETING_EMPLOYEE = "DELETE FROM meeting_employee WHERE meetingID = ?";
        jdbc.update(DELETE_MEETING_EMPLOYEE, meeting.getId());
        insertMeetingEmployee(meeting);
        
    }

    @Override
    @Transactional
    public void deleteMeetingById(int id) {
        final String DELETE_MEETING_EMPLOYEE = "DELETE FROM meeting_employee WHERE meetingID = ?";
        jdbc.update(DELETE_MEETING_EMPLOYEE, id);
        
        final String DELETE_MEETING = "DELETE FROM meeting WHERE id = ?";
        jdbc.update(DELETE_MEETING, id);
    }

    @Override
    public List<Meeting> getMeetingsForRoom(Room room) {
        final String SELECT_MEETING_FOR_ROOM = "SELECT * FROM meeting WHERE roomID = ?";
        List<Meeting> meetings = jdbc.query(SELECT_MEETING_FOR_ROOM, new MeetingMapper(), room.getId());
        addRoomAndEmployeeToMeeting(meetings);
        return meetings;
    }

    @Override
    public List<Meeting> getMeetingsForEmployee(Employee employee) {
        final String SELECT_MEETINGS_FOR_EMPLOYEES = "SELECT m.* FROM meeting m JOIN meeting_employee me ON m.id = me.meetingID WHERE me.employeeID = ?";
        List<Meeting> meetings = jdbc.query(SELECT_MEETINGS_FOR_EMPLOYEES, new MeetingMapper(), employee.getId());
        addRoomAndEmployeeToMeeting(meetings);
        return meetings;
    }

    private void addRoomAndEmployeeToMeeting(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            meeting.setRoom(getRoomForMeeting(meeting));
            meeting.setAttendees(getEmployeesForMeeting(meeting));
        }
    }
    
    public static final class MeetingMapper implements RowMapper<Meeting> {

        @Override
        public Meeting mapRow(ResultSet rs, int i) throws SQLException {
            Meeting m = new Meeting();
            m.setId(rs.getInt("id"));
            m.setName(rs.getString("name"));
            m.setTime(rs.getTimestamp("time").toLocalDateTime());
            return m;
        }
        
    }

}


