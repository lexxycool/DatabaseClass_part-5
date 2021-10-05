package com.techelevator.projects.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;

public class JdbcEmployeeDao implements EmployeeDao {

	private final JdbcTemplate jdbcTemplate;

	public JdbcEmployeeDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> allEmployees = new ArrayList<>();
		String sqlGetAllEmployees = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date FROM employee";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllEmployees);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> allEmployees = new ArrayList<>();
		String sqlGetAllEmployeesByName = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date FROM employee "+
				"WHERE first_name ILIKE ? AND last_name ILIKE ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetAllEmployeesByName, "%" + firstNameSearch + "%", "%" + lastNameSearch + "%");
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> allEmployees = new ArrayList<>();
		String sqlGetEmployeesByProject = "SELECT e.employee_id, e.department_id, e.first_name, e.last_name, e.birth_date, e.hire_date FROM employee e "+
				"JOIN project_employee pe ON e.employee_id = pe.employee_id "+
				"WHERE pe.project_id = ?";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetEmployeesByProject, projectId);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sqlAddEmployeeToProject = "INSERT INTO project_employee (project_id, employee_id) VALUES (?, ?)";
		jdbcTemplate.update(sqlAddEmployeeToProject, projectId, employeeId);
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sqlRemoveEmployeeFromProject = "DELETE FROM project_employee WHERE project_id=? AND employee_id=?";
		jdbcTemplate.update(sqlRemoveEmployeeFromProject, projectId, employeeId);
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> allEmployees = new ArrayList<>();
		String sqlGetEmployeesByProject = "SELECT employee_id, department_id, first_name, last_name, birth_date, hire_date FROM employee e "+
				"WHERE employee_id NOT IN (SELECT DISTINCT employee_id FROM project_employee)";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlGetEmployeesByProject);
		while (results.next()) {
			Employee employeeResult = mapRowToEmployee(results);
			allEmployees.add(employeeResult);
		}
		return allEmployees;
	}

	private Employee mapRowToEmployee(SqlRowSet result) {
		Employee employee = new Employee();
		employee.setId(result.getLong("employee_id"));
		employee.setDepartmentId(result.getLong("department_id"));
		employee.setFirstName(result.getString("first_name"));
		employee.setLastName(result.getString("last_name"));
		employee.setBirthDate(result.getDate("birth_date").toLocalDate());
		employee.setHireDate(result.getDate("hire_date").toLocalDate());
		return employee;
	}

}
