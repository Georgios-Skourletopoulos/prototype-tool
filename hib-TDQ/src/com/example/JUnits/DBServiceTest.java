/**
 * The required package.
 */
package com.example.JUnits;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.JobTitle;
import com.example.model.Project;
import com.example.model.Scenario;
import com.example.model.UserLogin;
import com.example.usecases.DBService;

/**
 * DBServiceTest is a JUnit testing class for methods included in the DBService class.
 * @author Georgios Skourletopoulos
 * @version 18 August 2013
 */
public class DBServiceTest {

	private DBService test = new DBService();

	@Before
	public void setUp() throws SQLException{
		test = new DBService();
	}

	@After
	public void tearDown(){
		test = null;
	}

	@Test
	public void getUserByEmailTest(){
		JobTitle jobTitle_id = new JobTitle();
		jobTitle_id.setName("Project Manager");
		UserLogin userResult = new UserLogin ("testUser1@example.com", "testUser1@example.com",
				"Mr", "John", "Doe", jobTitle_id, "UoB", "UK", new Timestamp(new Date().getTime()));
		assertNotNull(test.getUserByEmail("testUser1@example.com"));
		assertEquals(userResult, test.getUserByEmail("testUser1@example.com"));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getProjectByIdTest() {
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		Project project = new Project (user, "Project1", "Data Mining", "Flexibility", new java.sql.Date
				(new Date(2013-1900, 2, 10).getTime()), new java.sql.Date(new Date(2013-1900, 5, 12).getTime()));
		assertNotNull(test.getProjectById(new Long(1)));
		assertEquals(project, test.getProjectById(new Long(1)));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getProjectsByUserEmailTest() {
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		Project project = new Project (user, "Project1", "Data Mining", "Flexibility", new java.sql.Date
				(new Date(2013-1900, 2, 10).getTime()), new java.sql.Date(new Date(2013-1900, 5, 12).getTime()));
		List<Project> projectsList = new ArrayList<Project>();
		projectsList.add(project);
		assertNotNull(test.getProjectsByUserEmail("testUser1@example.com"));
		assertEquals(projectsList, test.getProjectsByUserEmail("testUser1@example.com"));
		assertTrue(test.getProjectsByUserEmail("testUser1@example.com").contains(project));
	}

	@Test
	public void getScenarioByIdTest() {
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		Project project = new Project (user, "Project1", "Data Mining", "Flexibility", new java.sql.Date
				(new Date(2013-1900, 2, 10).getTime()), new java.sql.Date(new Date(2013-1900, 5, 12).getTime()));
		Scenario scenario = new Scenario(project, "Scenario1", "Leasing", "Usability", "High");
		assertNotNull(test.getScenarioById(new BigInteger("1")));
		assertEquals(scenario, test.getScenarioById(new BigInteger("1")));
	}

	@Test
	public void getScenarioByProjectTest() {
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		Project project = test.getProjectById(new Long(1));
		Scenario scenario = new Scenario(project, "Scenario1", "Leasing", "Usability", "High");
		List<Scenario> scenariosList = new ArrayList<Scenario>();
		scenariosList.add(scenario);
		assertNotNull(test.getScenarioByProject(project));
		assertEquals(scenariosList, test.getScenarioByProject(project));
		assertTrue(test.getScenarioByProject(project).contains(scenario));
	}

	@Test
	public void getCostForImplementingSharedByEmailTest() {
		UserLogin user = test.getUserByEmail("testUser2@example.com");
		CostForImplementing cfi = new CostForImplementing(user, "Cost1", 40, 10, 20, 10, 20, new BigDecimal(71),
				new BigDecimal(1500), "High", "Moderate", "High", "High", "Switch", "Good estimation!");
		List<CostForImplementing> list = new ArrayList<CostForImplementing>();
		list.add(cfi);
		assertNotNull(test.getCostForImplementingSharedByEmail("testUser2@example.com"));
		assertEquals(list, test.getCostForImplementingSharedByEmail("testUser2@example.com"));
		assertTrue(test.getCostForImplementingSharedByEmail("testUser2@example.com").contains(cfi));
	}

	@Test
	public void checkCocomoForSubmissionTest() {
		UserLogin user = test.getUserByEmail("testUser2@example.com");
		Cocomo cocomo = new Cocomo(user, "Cocomo1", "Organic", new BigDecimal(12), "High", "Good estimation");

		assertNotNull(test.checkCocomoForSubmission("testUser2@example.com", "Cocomo1"));
		assertEquals(cocomo, test.checkCocomoForSubmission("testUser2@example.com", "Cocomo1"));		
		assertNull(test.checkCocomoForSubmission("testUser2@example.com", "Cocomo2"));
	}

	@Test
	public void checkBuyingSharedTest() {		
		assertFalse(test.checkBuyingShared("testUser1@example.com", new Long(1), "testUser2@example.com"));
		assertTrue(test.checkBuyingShared("testUser1@example.com", new Long(2), "testUser2@example.com"));
	}
}
