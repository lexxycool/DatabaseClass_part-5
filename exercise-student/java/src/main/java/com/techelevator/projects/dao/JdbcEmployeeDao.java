package com.techelevator.projects.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.projects.model.Employee;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcEmployeeDao implements EmployeeDao {

	private final JdbcTemplate jdbcTemplate;

	public JdbcEmployeeDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> employee = new ArrayList<>();

		String sql = "select employee_id, department_id, first_name, last_name, birth_date, hire_date from employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while(results.next()) {
			Employee employee1 = mapRowToEmployee(results);
			employee.add(employee1);

		}
		return employee;

	}

	private Employee mapRowToEmployee(SqlRowSet rowSet) {
		Employee employees = new Employee();
		employees.setId(rowSet.getLong("employee_id"));
		employees.setDepartmentId(rowSet.getLong("department_id"));
		employees.setFirstName(rowSet.getString("first_name"));
		employees.setLastName(rowSet.getString("last_name"));
		Date birthDate = rowSet.getDate("birth_date");
		Date hireDate = rowSet.getDate("hire_date");

		LocalDate birthDateLocal = null;
		if(birthDate != null) {
			birthDateLocal = birthDate.toLocalDate();
		}


		LocalDate hireDateLocal = null;
		if(hireDate != null) {
			hireDateLocal = hireDate.toLocalDate();
		}
		employees.setBirthDate(birthDateLocal);
		employees.setHireDate(hireDateLocal);

		return employees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> employee = new ArrayList<>();
				String sql = "select * from employee where " +
				"first_name ilike ? and last_name ilike ? ";

		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + firstNameSearch + "%", "%" + lastNameSearch + "%");

				while(results.next()) {
			Employee searchName = mapRowToEmployee(results);
			employee.add(searchName);
		}

		return employee;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> employee = new ArrayList<>();

		String sql = "select * from employee e join project_employee p " +
				"on p.employee_id = e.employee_id" +
				"where p.project_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);

		while(results.next()) {
			Employee projectEmployee = mapRowToEmployee(results);
			employee.add(projectEmployee);
		}

		return employee;
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
				String sql = "insert into project_employee(project_id, employee_id) " +
				"values(?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);


	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "delete from project_employee where employee_id = ?";
		jdbcTemplate.update(sql, employeeId);

	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> employees = new ArrayList<>();
		String sql = "select employee where employee_id " +
				"not in (select employee_id from project_employee)";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		if(results.next()) {
			employees.add(mapRowToEmployee(results));
		}
		return employees;
	}


}
