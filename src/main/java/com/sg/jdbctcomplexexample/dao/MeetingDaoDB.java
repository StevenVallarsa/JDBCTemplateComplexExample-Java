package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
import com.sg.jdbctcomplexexample.entity.Meeting;
import com.sg.jdbctcomplexexample.entity.Room;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
    public Meeting addMeeting(Meeting meeting) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateMeeting(Meeting meeting) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMeetingById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Meeting> getMeetingsForRoom(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Meeting> getMeetingsForEmployee(Employee employee) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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


