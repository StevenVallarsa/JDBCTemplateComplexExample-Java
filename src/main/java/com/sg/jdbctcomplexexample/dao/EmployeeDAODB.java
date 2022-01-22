
package com.sg.jdbctcomplexexample.dao;

import com.sg.jdbctcomplexexample.entity.Employee;
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
 * @author: Steven Vallarsa
 *   email: stevenvallarsa@gmail.com
 *    date: 2022-01-21
 * purpose: 
 */
@Repository
public class EmployeeDAODB implements EmployeeDao {
    
    @Autowired
    JdbcTemplate jdbc;

    @Override
    public List<Employee> getAllEmployees() {
        final String SELECT_ALL_EMPLOYEES = "SELECT * FROM employee";
        return jdbc.query(SELECT_ALL_EMPLOYEES, new EmployeeMapper());
    }

    @Override
    public Employee getEmployeeById(int id) {
        try {
            final String GET_EMPLOYEE_BY_ID = "SELECT * FROM employee WHERE id = ?";
            return jdbc.queryForObject(GET_EMPLOYEE_BY_ID, new EmployeeMapper(), id);
            
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Employee addEmployee(Employee employee) {
        final String INSERT_EMPLOYEE = "INSERT INTO employee firstName = ?, lastName = ?";
        jdbc.update(INSERT_EMPLOYEE, 
                employee.getFirstName(), 
                employee.getLastName());
        
        int newID = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    @Override
    public void updateEmployee(Employee employee) {
        final String UPDATE_EMPLOYEE = "UPDATE employee SET firstName = ?, lastName = ? WHERE id = ?";
        jdbc.update(UPDATE_EMPLOYEE, 
                employee.getFirstName(), 
                employee.getLastName(), 
                employee.getId());
    }

    @Override
    @Transactional
    public void deleteEmployeeById(int id) {
        final String DELETE_MEETING_EMPLOYEE = "DELETE FROM meeting_employee WHERE employeeID = ?";
        jdbc.update(DELETE_MEETING_EMPLOYEE, id);
        
        final String DELETE_EMPLOYEE = "DELETE FROM employee WHERE id = ?";
        jdbc.update(DELETE_EMPLOYEE, id);
    }
    
    public static final class EmployeeMapper implements RowMapper<Employee> {

        @Override
        public Employee mapRow(ResultSet rs, int i) throws SQLException {
            Employee e = new Employee();
            e.setId(rs.getInt("id"));
            e.setFirstName(rs.getString("firstName"));
            e.setLastName(rs.getString("lastName"));
            return e;
        }
        
    }

}
