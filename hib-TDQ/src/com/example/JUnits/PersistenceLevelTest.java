package com.example.JUnits;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.EMF;
import com.example.model.Project;
import com.example.model.Scenario;
import com.example.model.SharedBuying;
import com.example.model.SharedCocomo;
import com.example.model.SharedImplementing;
import com.example.model.SharedProject;
import com.example.model.TDinBuying;
import com.example.model.UserLogin;
import com.example.usecases.DBService;

/**
 * PersistenceLevelTest is a JUnit testing class for the basic level of persisting (insert, delete,
 * update) the entities represented by the POJOs.
 * @author Georgios Skourletopoulos, by adapting code from "Unit test JPA Entities with in-memory database". (2007),
 * http://eskatos.wordpress.com/2007/10/15/unit-test-jpa-entities-with-in-memory-database/ [accessed 30 Aug 2013]
 * @version 18 August 2013
 */
public class PersistenceLevelTest {

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
	public void projectTest() {
		EntityManager em = EMF.get().createEntityManager();
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		@SuppressWarnings("deprecation")
		Project project = new Project (user, "Project2", "Data Mining", "Scalability", new java.sql.Date
				(new Date(2013-1900, 10, 10).getTime()), new java.sql.Date
				(new Date(2013-1900, 11, 10).getTime()));

		em.persist(project);
		assertTrue(em.contains(project));

		project.setProjectGoals("New goals");

		em.merge(project);
		assertTrue(em.contains(project));

		UserLogin user2 = test.getUserByEmail("testUser2@example.com");
		SharedProject sp = new SharedProject(user, user2, project, new Timestamp(new java.util.Date().getTime()));
		em.persist(sp);
		assertTrue(em.contains(sp));

		assertTrue(sp.getProjectId().equals(project));

		em.remove(project);
		assertFalse(em.contains(project));
	}

	@Test
	public void scenarioTest() {
		EntityManager em = EMF.get().createEntityManager();
		Project project = test.getProjectById(new Long(1));
		Scenario scenario = new Scenario(project, "Scenario1", "Leasing", "Usability", "High");

		em.persist(scenario);
		assertTrue(em.contains(scenario));

		scenario.setPriority("Low");

		em.merge(scenario);
		assertTrue(em.contains(scenario));

		em.remove(scenario);
		assertFalse(em.contains(scenario));
	}

	@Test
	public void cocomoTest() {
		EntityManager em = EMF.get().createEntityManager();
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		Cocomo cocomo = new Cocomo(user, "Cocomo1", "Organic", new BigDecimal(12), "High", "Good estimation");

		em.persist(cocomo);
		assertTrue(em.contains(cocomo));

		cocomo.setProductSize(new BigDecimal(15));

		em.merge(cocomo);
		assertTrue(em.contains(cocomo));

		UserLogin user2 = test.getUserByEmail("testUser2@example.com");
		SharedCocomo sc = new SharedCocomo(user, user2, cocomo, new Timestamp(new java.util.Date().getTime()));
		em.persist(sc);
		assertTrue(em.contains(sc));

		assertTrue(sc.getCocomoId().equals(cocomo));

		em.remove(cocomo);
		assertFalse(em.contains(cocomo));
	}

	@Test
	public void costForImplementingTest() {
		EntityManager em = EMF.get().createEntityManager();
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		CostForImplementing cfi = new CostForImplementing(user, "Cost1", 40, 10, 20, 10, 20, new BigDecimal(71),
				new BigDecimal(1500), "High", "Moderate", "High", "High", "Switch", "Good estimation!");

		em.persist(cfi);
		assertTrue(em.contains(cfi));

		cfi.setEffortApplied(new BigDecimal(90));

		em.merge(cfi);
		assertTrue(em.contains(cfi));

		UserLogin user2 = test.getUserByEmail("testUser2@example.com");
		SharedImplementing si = new SharedImplementing(user, user2, cfi, new Timestamp(new java.util.Date().getTime()));
		em.persist(si);
		assertTrue(em.contains(si));

		assertTrue(si.getImplementingId().equals(cfi));

		em.remove(cfi);
		assertFalse(em.contains(cfi));
	}

	@Test
	public void TDinBuyingTest() {
		EntityManager em = EMF.get().createEntityManager();
		UserLogin user = test.getUserByEmail("testUser1@example.com");
		TDinBuying tdb = new TDinBuying(user, "TD1", 3, new BigInteger("1000"), new BigInteger("300"), new BigDecimal(3),
				new BigDecimal(15), new BigDecimal(2), new BigDecimal(8), new BigDecimal(1), "High", "Low",
				"High", "Low", "Switch", "Good estimation");

		em.persist(tdb);
		assertTrue(em.contains(tdb));

		tdb.setMaxCapacity(new BigInteger("1200"));

		em.merge(tdb);
		assertTrue(em.contains(tdb));

		UserLogin user2 = test.getUserByEmail("testUser2@example.com");
		SharedBuying sb = new SharedBuying(user, user2, tdb, new Timestamp(new java.util.Date().getTime()));
		em.persist(sb);
		assertTrue(em.contains(sb));

		assertTrue(sb.getBuyingId().equals(tdb));

		em.remove(tdb);
		assertFalse(em.contains(tdb));
	}
}
