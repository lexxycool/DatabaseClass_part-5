package com.techelevator.projects.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.techelevator.projects.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;

public class JdbcProjectDao implements ProjectDao {

	private final JdbcTemplate jdbcTemplate;

	public JdbcProjectDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Project getProject(Long projectId) {
		Project project = null;

		String sql = "select project_id, name, from_date, to_date from project where project_id = ?";

		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, projectId);

		if(result.next()) {
			project = mapRowToProject(result);
		}

		return project;
	}

	@Override
	public List<Project> getAllProjects() {
		List<Project> project = new ArrayList<>();

		String sql = "select project_id, name, from_date, to_date from project";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

		while(results.next()) {
			Project project1 = mapRowToProject(results);

			project.add(project1);
		}


		return project;
	}

	private Project mapRowToProject(SqlRowSet rowSet) {
		Project project = new Project();
		project.setId(rowSet.getLong("project_id"));
		project.setName(rowSet.getString("name"));
		Date fromDate = rowSet.getDate("from_date");
		Date toDate = rowSet.getDate("to_date");

		LocalDate fromDateLocal = null;
		if(fromDate != null) {
			fromDateLocal = fromDate.toLocalDate();
		}


		LocalDate toDateLocal = null;
		if(toDate != null) {
			toDateLocal = toDate.toLocalDate();
		}
		project.setFromDate(fromDateLocal);
		project.setToDate(toDateLocal);

		return project;
	}


	@Override
	public Project createProject(Project newProject) {
		String sql = "insert into project(name, from_date, to_date) values(?, ?, ?) returning project_id";
		long newId = jdbcTemplate.queryForObject(sql, long.class, newProject.getName(),
					newProject.getFromDate(), newProject.getToDate());

		return getProject(newId);

		}

	@Override
	public void deleteProject(Long projectId) {
		String sql = "delete from project_employee where project_id = ?";
		jdbcTemplate.update(sql, projectId);

		sql = "delete from project where project_id = ?";
		jdbcTemplate.update(sql, projectId);
	}
	

}
