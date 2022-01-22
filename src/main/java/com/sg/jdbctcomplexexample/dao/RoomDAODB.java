package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Room;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author: Steven Vallarsa email: stevenvallarsa@gmail.com date: 2022-01-21
 * purpose:
 */
@Repository
public class RoomDAODB implements RoomDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Room> getAllRooms() {
        final String SELECT_ALL_ROOMS = "SELECT * FROM room";
        return jdbc.query(SELECT_ALL_ROOMS, new RoomMapper());
    }

    @Override
    public Room getRoomById(int id) {
        try {
            final String SELECT_ROOM_BY_ID = "SELECT * FROM room WHERE room = ?";
            return jdbc.queryForObject(SELECT_ROOM_BY_ID, new RoomMapper(), id);
        } catch (DataAccessException e) {
            return null;
        }
    }

    // Room is created without ID
    // Inserting room creats ID in DB
    // Use "SELECT_LAST_INSERTED_ID() funciton to retrieve ID
    // add ID to room object and return
    // "@Transactional" groups all queries in method into one go. If one fails they all fail and DB isn't touched.
    @Override
    @Transactional
    public Room addRoom(Room room) {
        final String INSERT_ROOM = "INSERT INTO room (name, description) VALUES (?,?)";
        jdbc.update(INSERT_ROOM, room.getName(), room.getDescription());

        int newId = jdbc.queryForObject("SELECT LAST_INSERTED_ID()", Integer.class);
        room.setId(newId);
        return room;
    }

    @Override
    public void updateRoom(Room room) {
        final String UPDATE_ROOM = "UPDATE room SET name = ?, description = ? WHERE id = ?";
        jdbc.update(UPDATE_ROOM,
                room.getName(),
                room.getDescription(),
                room.getId());
    }

    @Override
    @Transactional
    public void deleteRoomById(int id) {
        final String DELETE_MEETING_EMPLOYEE_BY_ROOM = "DELETE FROM me.* FROM meeting_employee JOIN meeting m ON me.meetingID = m.id WHERE m.roomID = ?";
        jdbc.update(DELETE_MEETING_EMPLOYEE_BY_ROOM, id);
        
        final String DELETE_MEETING_BY_ROOM = "DELETE FROM meeting WHERE roomID = ?";
        jdbc.update(DELETE_MEETING_BY_ROOM, id);
        
        final String DELETE_ROOM_BY_ID = "DELETE FROM room WHERE id = ?";
        jdbc.update(DELETE_ROOM_BY_ID, id);
    }

    public static final class RoomMapper implements RowMapper<Room> {

        @Override
        public Room mapRow(ResultSet rs, int i) throws SQLException {
            Room room = new Room();
            room.setId(rs.getInt("id"));
            room.setName(rs.getString("name"));
            room.setDescription(rs.getString("description"));
            return room;
        }

    }

}
