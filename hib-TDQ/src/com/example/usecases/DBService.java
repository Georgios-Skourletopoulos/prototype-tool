package com.example.usecases;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.EMF;
import com.example.model.JobTitle;
import com.example.model.Project;
import com.example.model.Scenario;
import com.example.model.SharedBuying;
import com.example.model.SharedCocomo;
import com.example.model.SharedImplementing;
import com.example.model.SharedProject;
import com.example.model.TDinBuying;
import com.example.model.UserLogin;
import com.google.appengine.api.users.User;

/**
 * DBService is a class that deals with any kind of interaction with the database.
 * @author Georgios Skourletopoulos
 * @version 1 August 2013
 */
public class DBService {

	private EntityManager entityManager;    // instance used to interact with the persistence context

	public DBService () {}    //the implicit constructor

	/**
	 * This method returns the user object from the database with the specified email. 
	 * @param email the email address the search is done upon
	 * @return the user object with the specified email (which will be null if it isn't found)
	 */
	public UserLogin getUserByEmail(String email) {

		UserLogin user = null;
		try {
			entityManager = EMF.get().createEntityManager();
			user = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, email).getSingleResult();
		}
		catch(NoResultException e)
		{
			System.out.println("no result");
			user = null;
		}
		finally {
			entityManager.close();
		}
		return user;
	}

	/**
	 * This method updates the login time of a user.
	 * @param usr the user whose login time will be updated
	 * @param lastLog the new login time
	 * @return the user (which will be null if the update failed)
	 */
	private UserLogin updateUserLogin(UserLogin usr, Timestamp lastLog) {
		UserLogin user = null;
		try {
			entityManager = EMF.get().createEntityManager();
			user = entityManager.find(UserLogin.class, usr.getId());
			user.setLastLog(lastLog);
			entityManager.getTransaction().begin();
			entityManager.merge(usr);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			user = null;
		}
		finally {
			entityManager.close();
		}
		return user;
	}

	/**
	 * This method deals with the user login. It updates the login time if the user exists in 
	 * the database or inserts a new user if it is the first login.
	 * @param user the user to be updated
	 * @param lastLog the login time
	 * @return true if update or insert successful, otherwise false
	 */
	public boolean userLogin(User user, Timestamp lastLog) {
		UserLogin usr = null;
		usr = getUserByEmail(user.getEmail());
		if (usr != null) {
			usr = updateUserLogin(usr, lastLog);
			System.out.println(usr.getEmail());
		}
		else
			usr = addUser(user,lastLog);
		if(usr != null) 
			return true;
		return false;
	}

	/**
	 * This method adds a new user in the database.
	 * @param user the user to be added
	 * @param lastLog the login time
	 * @return the user (which will be null if the insert failed)
	 */
	private UserLogin addUser(User user, Timestamp lastLog) {
		UserLogin usr = new UserLogin(user.getNickname(), user.getEmail(), lastLog);
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(usr);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			usr = null;
		}
		finally {
			entityManager.close();
		}
		return usr;
	}

	/**
	 * This method updates a user's information. The method is called in the ProfileServlet.
	 * @param email the new email of the user
	 * @param title the new title of the user
	 * @param firstName the new first name of the user
	 * @param lastName the new last name of the user 
	 * @param jobPos the new job position of the user
	 * @param companyName the new company name of the user
	 * @param location the new location of the user
	 * @return the user (which will be null if the update failed)
	 */
	public UserLogin updateUser(String email, String title, String firstName, String lastName, String jobPos, 
			String companyName, String location) {
		UserLogin usr = null;
		try {
			entityManager = EMF.get().createEntityManager();
			usr = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, email).getSingleResult();
			usr.setTitle(title);
			usr.setFirstName(firstName);
			usr.setLastName(lastName);
			usr.setCompanyName(companyName);
			usr.setLocation(location);
			JobTitle newJob = (JobTitle) entityManager.createQuery("SELECT jobTitle from JobTitle as jobTitle"
					+ " where jobTitle.name = ?1").setParameter(1, jobPos).getSingleResult();
			usr.setJobId(newJob);			
			entityManager.getTransaction().begin();
			entityManager.merge(usr);
			entityManager.getTransaction().commit();
		}
		catch(NoResultException e)
		{
			System.out.println("no result");
			usr = null;
		}
		finally {
			entityManager.close();
		}
		return usr;		
	}

	/**
	 * This method inserts in the database a new project.
	 * @param project the project to be inserted
	 * @return the project (which will be null if the insert failed)
	 */
	public Project persistProject(Project project) {
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(project);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return null;
		}
		finally {
			entityManager.close();
		}
		return project;		
	}

	/**
	 * This method updates a project with new details.
	 * @param id the ID of the project
	 * @param projectName the new name of the project
	 * @param projectCategory the new project category
	 * @param projectGoals the new project goals
	 * @param projectStart the new starting date of the project
	 * @param projectEnd the new ending date of the project
	 * @return the project (which will be null if update failed)
	 */
	public Project updateProject(Long id, String projectName, String projectCategory, String projectGoals,
			Date projectStart, Date projectEnd) {
		Project project = null;
		try {
			entityManager = EMF.get().createEntityManager();
			project = entityManager.find(Project.class, id);
			project.setProjectName(projectName);
			project.setProjectCategory(projectCategory);
			project.setProjectGoals(projectGoals);
			project.setProjectStart(projectStart);
			project.setProjectEnd(projectEnd);
			entityManager.getTransaction().begin();
			entityManager.merge(project);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			project = null;
		}
		finally {
			entityManager.close();
		}
		return project;		
	}

	/**
	 * This method deletes a project by its ID.
	 * @param id the ID of the project
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteProject(Long id) {
		try {
			entityManager = EMF.get().createEntityManager();
			Project project = entityManager.find(Project.class, id);
			entityManager.getTransaction().begin();
			entityManager.remove(project);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;
	}

	/**
	 * This method retrieves a project by its ID.
	 * @param id the ID of the project
	 * @return the project (which will be null if not found)
	 */
	public Project getProjectById(Long id) {
		Project project = null;
		try{
			entityManager = EMF.get().createEntityManager();
			project = entityManager.find(Project.class, id);
		}
		catch(Exception e) {
			project = null;
		}
		finally { entityManager.close(); }
		return project;
	}

	/**
	 * This method retrieves a list of projects based on the email of their creator.
	 * @param email the email of the user who created the projects
	 * @return the list of projects (which will be null if no projects found)
	 */
	public List<Project> getProjectsByUserEmail(String email) {
		List<Project> projectsList = new ArrayList<Project>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Project> query = entityManager.createQuery("SELECT project from Project as project"
					+ " where project.userId = ?1", Project.class).setParameter(1, user);
			projectsList = query.getResultList();
		} catch(NoResultException e)
		{
			projectsList = null;
		}
		finally {
			entityManager.close();
		}
		return projectsList;
	}

	/**
	 * This method retrieves a list of projects created by a user with a certain email.
	 * @param email the email of the user who created the projects
	 * @return the list of projects (which will be null if none are found)
	 */
	public List<Project> getCreatedProjectsByUserEmail(String email) {
		List<Project> projectsList = new ArrayList<Project>();
		List<Project> sharedProjects = new ArrayList<Project>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Project> query1 = entityManager.createQuery("SELECT sp.projectId from SharedProject sp"
					+ " where sp.fromUserId = ?1", Project.class).setParameter(1, user);
			sharedProjects = query1.getResultList();
			TypedQuery<Project> query = null;
			if(sharedProjects.size() != 0)
				query = entityManager.createQuery("SELECT project from Project as project"
						+ " where project NOT IN ?1 AND project.userId = ?2", Project.class)
						.setParameter(1, sharedProjects).setParameter(2, user);
			else
				query = entityManager.createQuery("SELECT project from Project as project"
						+ " where project.userId = ?1", Project.class).setParameter(1, user);
			projectsList = query.getResultList();
		} catch(NoResultException e) {
			projectsList = null;
		}
		finally {
			entityManager.close(); }
		return projectsList;
	}

	/**
	 * This method retrieves a list of projects the user with the specified email has shared with
	 * other users.
	 * @param email the email of the user who owns the projects
	 * @return the list of projects (which will be null if none are found)
	 */
	public List<Project> getSharedProjectsByUserEmail(String email) {
		List<Project> sharedProjects = new ArrayList<Project>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Project> query1 = entityManager.createQuery("SELECT sp.projectId from SharedProject sp"
					+ " where sp.fromUserId = ?1", Project.class).setParameter(1, user);
			sharedProjects = query1.getResultList();
			System.out.println(sharedProjects.size());
		} catch(NoResultException e)
		{
			sharedProjects = null;
		}
		finally {
			entityManager.close();
		}
		return filterList(sharedProjects);
	}

	/**
	 * This method removes duplicated objects from a list. It is used in the 
	 * getSharedProjectsByUserEmail, getCostForImplementingSharedByEmail, getCocomoSharedByEmail
	 * and getTDinBuyingSharedByEmail methods.
	 * @param toBeFiltered the list to be filtered
	 * @return the list without duplicated objects
	 */
	private <E> List<E> filterList(List<E> toBeFiltered) {
		List<E> filteredList = new ArrayList<E>();
		Map<E, String> auxMap = new HashMap<E, String>();
		Iterator<E> iter = toBeFiltered.iterator();
		while(iter.hasNext()) {
			auxMap.put(iter.next(), "");
		}
		filteredList.addAll(auxMap.keySet());
		return filteredList;
	}

	/**
	 * This method retrieves a scenario by its ID.
	 * @param id the ID of the scenario
	 * @return the scenario (which will be null if not found)
	 */
	public Scenario getScenarioById(BigInteger id) {
		Scenario scenario = null;
		try {
			entityManager = EMF.get().createEntityManager();
			scenario = entityManager.find(Scenario.class, id);
		} catch(NoResultException e)
		{
			scenario = null;
		} finally {
			entityManager.close();
		}
		return scenario;
	}

	/**
	 * This method retrieves the list of scenarios associated with a certain project.
	 * @param project the project whose scenarios will be retrieved
	 * @return the list of scenarios (which will be null if no scenario is found)
	 */
	public List<Scenario> getScenarioByProject(Project project) {
		List<Scenario> scenarioList = new ArrayList<Scenario>();
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Scenario> query = entityManager.createQuery("SELECT scenario from Scenario as scenario"
					+ " where scenario.projectId = ?1", Scenario.class).setParameter(1, project);
			scenarioList = query.getResultList();
		} catch(NoResultException e)
		{
			scenarioList = null;
		}
		finally {
			entityManager.close();
		}
		return scenarioList;
	}

	/**
	 * This method retrieves the list of scenarios associated with a certain user email.
	 * @param email the email of the user who owns the scenarios
	 * @return the list of scenarios
	 */
	public List<Scenario> getScenariosByEmail(String email) {
		List<Scenario> scenarioList = new ArrayList<Scenario>();
		List<Project> projectsList = getProjectsByUserEmail(email);
		List<Scenario> auxScenarioList = new ArrayList<Scenario>();
		for (Project proj : projectsList) {
			auxScenarioList = getScenarioByProject(proj);
			for (Scenario scen : auxScenarioList) {
				scenarioList.add(scen);
			}
		}
		return scenarioList;
	}

	/**
	 * This method inserts a scenario in the database.
	 * @param scenario the scenario to be inserted
	 * @return the scenario if the insert is successful, otherwise null
	 */
	public Scenario persistScenario(Scenario scenario) {
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(scenario);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return null;
		}
		finally {
			entityManager.close();
		}
		return scenario;		
	}

	/**
	 * This method updates the details of a scenario.
	 * @param id the ID of the scenario
	 * @param scenarioName the new name of the scenario
	 * @param scenarioType the new type of the scenario
	 * @param requirementType the new requirement type
	 * @param priority the new priority
	 * @return the scenario (which will be null if update fails)
	 */
	public Scenario updateScenario(BigInteger id, String scenarioName, String scenarioType, String requirementType,
			String priority) {
		Scenario scenario = null;
		try {
			entityManager = EMF.get().createEntityManager();
			scenario = entityManager.find(Scenario.class, id);
			scenario.setScenarioName(scenarioName);
			scenario.setScenarioType(scenarioType);
			scenario.setRequirementType(requirementType);
			scenario.setPriority(priority);
			entityManager.getTransaction().begin();
			entityManager.merge(scenario);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			scenario = null;
		}
		finally {
			entityManager.close();
		}
		return scenario;		
	}

	/**
	 * This method deletes a scenario based on its ID.
	 * @param id the ID of the scenario to be deleted
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteScenario(BigInteger id) {
		try {
			entityManager = EMF.get().createEntityManager();
			Scenario scenario = entityManager.find(Scenario.class, id);
			entityManager.getTransaction().begin();
			entityManager.remove(scenario);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;		
	}

	/**
	 * This method adds a shared project in the database. 
	 * @param sharedWithEmail the email of the user the project is shared with
	 * @param projectShared the ID of the project which was shared
	 * @param sharedFromEmail the email of the user who shares the project
	 * @param timestamp the time of sharing
	 * @return the object of the shared project (which will be null if insert fails)
	 */
	public SharedProject shareProject(String sharedWithEmail, Long projectShared, String sharedFromEmail, Timestamp timestamp) {
		SharedProject sharedProject = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Project project = entityManager.find(Project.class, projectShared);
			sharedProject = new SharedProject(sharedFrom, sharedWith, project, timestamp); 
			entityManager.getTransaction().begin();
			entityManager.persist(sharedProject);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			sharedProject = null;
		}
		finally {
			entityManager.close();
		}
		return sharedProject;	
	}

	/**
	 * This method checks if a project was shared with a certain user identified by its email.
	 * @param sharedWithEmail the email of the user the project was shared with
	 * @param projectShared the ID of the shared project
	 * @param sharedFromEmail the email of the use who shared the project
	 * @return true if the project was not shared with that user, otherwise false
	 */
	public boolean checkShared(String sharedWithEmail, Long projectShared, String sharedFromEmail) {
		SharedProject sharedProject = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Project project = entityManager.find(Project.class, projectShared);
			sharedProject = (SharedProject) entityManager.createQuery("SELECT sharedProject from "
					+ "SharedProject as sharedProject "
					+ "where sharedProject.fromUserId =?1 AND sharedProject.toUserId = ?2 AND sharedProject.projectId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, project).getSingleResult();
		}
		catch(NoResultException e)
		{
			return true;
		}
		finally {
			entityManager.close();
		}
		if(sharedProject != null)
			return false;
		else return true;
	}

	/**
	 * This method returns a list of shared projects in the aim of retrieving the users
	 * that project was shared with.
	 * @param projectId the ID of the project
	 * @return the list of shared projects entries for that project
	 */
	public List<SharedProject> getSharedUsersByProject(Long projectId) {
		List<SharedProject> sharedList = new ArrayList<SharedProject>();
		Project project = getProjectById(projectId);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<SharedProject> query = entityManager.createQuery("SELECT sharedProject from SharedProject as sharedProject "
					+ " where sharedProject.projectId = ?1", SharedProject.class)
					.setParameter(1, project);
			sharedList = query.getResultList();
		} catch(NoResultException e)
		{
			sharedList = null;
		}
		finally {
			entityManager.close(); 
		}
		return sharedList;
	}

	/**
	 * This method removes a user from a shared project.
	 * @param sharedWithEmail the email of the user to be removed from share
	 * @param projectShared the ID of the project that was shared
	 * @param sharedFromEmail the email of the user who shared the project
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteSharedUser(String sharedWithEmail, Long projectShared, String sharedFromEmail) {
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Project project = entityManager.find(Project.class, projectShared);
			SharedProject sharedProject = (SharedProject) entityManager.createQuery("SELECT sharedProject from SharedProject as sharedProject"
					+ " where sharedProject.fromUserId =?1 AND sharedProject.toUserId = ?2 AND sharedProject.projectId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, project).getSingleResult();
			entityManager.getTransaction().begin();
			entityManager.remove(sharedProject);
			entityManager.getTransaction().commit();
		}
		catch(Exception e)
		{
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;
	}

	/**
	 * This method retrieves a list of projects a user has received (projects that were shared
	 * with the user).
	 * @param email the email of the user
	 * @return the list of projects (which will be null if no projects found)
	 */
	public List<SharedProject> getReceivedSharedProjectsByUser (String email) {
		UserLogin user = getUserByEmail(email);
		List<SharedProject> sharedProjects = new ArrayList<SharedProject>();
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery <SharedProject> query1 = entityManager.createQuery("SELECT sharedProject "
					+ "from SharedProject as sharedProject where "
					+ "sharedProject.toUserId = ?1)", SharedProject.class).setParameter(1, user);
			sharedProjects = query1.getResultList();
		}
		catch(NoResultException e)
		{
			sharedProjects = null;
		}
		finally {
			entityManager.close();
		}
		return sharedProjects;
	}

	/**
	 * This method inserts a new cost for Implementing estimate.
	 * @param costForImplementing the cost to be inserted
	 * @return the cost inserted (which will be null if insert fails)
	 */
	public CostForImplementing persistImplementing (CostForImplementing costForImplementing) {
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(costForImplementing);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return null;
		}
		finally {
			entityManager.close();
		}
		return costForImplementing;
	}

	/**
	 * This method deals with updating the details of an existing cost for Implementing.
	 * @param id the ID of the cost
	 * @param name the new name of the cost
	 * @param weightDevelopment the new weighted priority rating for the Development process of the cost
	 * @param weightConfiguration the new weighted priority rating for the Configuration process of the cost
	 * @param weightDeployment the new weighted priority rating for the Deployment process of the cost
	 * @param weightLicences the new weighted priority rating for the Licenses process of the cost
	 * @param weightInfrastructure the new weighted priority rating for the Infrastructure process of the cost
	 * @param effortApplied the new effort applied of the cost
	 * @param avgMonthlySalary the new average monthly salary per employee of the cost
	 * @param confidence the new confidence of the cost
	 * @param productFlexibility the new product flexibility of the cost
	 * @param marketFlexibility the new market flexibility of the cost
	 * @param riskOfFutureTD the new risk for future TD of the cost
	 * @param realOptionsValuation the new real options valuation of the cost
	 * @param justification the new justification for the Project Manager of the cost
	 * @return the new cost (which will be null if update fails)
	 */
	public CostForImplementing updateCostForImplementing (Long id, String name, 
			int weightDevelopment, int weightConfiguration, int weightDeployment, 
			int weightLicences, int weightInfrastructure, BigDecimal effortApplied,
			BigDecimal avgMonthlySalary, String confidence, String productFlexibility, 
			String marketFlexibility, String riskOfFutureTD, 
			String realOptionsValuation, String justification) {
		CostForImplementing costForImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			costForImplementing = entityManager.find(CostForImplementing.class, id);
			costForImplementing.setName(name);
			costForImplementing.setWeightDevelopment(weightDevelopment);
			costForImplementing.setWeightConfiguration(weightConfiguration);
			costForImplementing.setWeightDeployment(weightDeployment);
			costForImplementing.setWeightLicences(weightLicences);
			costForImplementing.setWeightInfrastructure(weightInfrastructure);
			costForImplementing.setEffortApplied(effortApplied);
			costForImplementing.setAvgMonthlySalary(avgMonthlySalary);
			costForImplementing.setConfidence(confidence);
			costForImplementing.setProductFlexibility(productFlexibility);
			costForImplementing.setMarketFlexibility(marketFlexibility);
			costForImplementing.setRiskOfFutureTD(riskOfFutureTD);
			costForImplementing.setRealOptionsValuation(realOptionsValuation);
			costForImplementing.setJustification(justification);
			entityManager.getTransaction().begin();
			entityManager.merge(costForImplementing);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			costForImplementing = null;
		}
		finally {
			entityManager.close();
		}
		return costForImplementing;
	}

	/**
	 * This method deletes a cost for Implementing estimate.
	 * @param id the ID of the cost
	 * @return the cost (which will be null if the deletion failed)
	 */
	public CostForImplementing deleteCostForImplementing (Long id) {
		CostForImplementing costForImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			costForImplementing = entityManager.find(CostForImplementing.class, id);
			entityManager.getTransaction().begin();
			entityManager.remove(costForImplementing);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			costForImplementing = null;
		}
		finally {
			entityManager.close();
		}
		return costForImplementing;
	}

	/**
	 * This method retrieves a cost for Implementing estimate by the ID.
	 * @param id the ID of the cost to be retrieved
	 * @return the cost (which will be null if no cost is found)
	 */
	public CostForImplementing getCostForImplementingById(Long id) {
		CostForImplementing costForImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			costForImplementing = entityManager.find(CostForImplementing.class, id);

		} catch(NoResultException e) {
			costForImplementing = null;
		}
		finally {
			entityManager.close(); }
		return costForImplementing;
	}

	/**
	 * This method retrieves a list of created cost for Implementing estimates by the user's email.
	 * @param email the email of the user
	 * @return the list of created costs (which will be null if none found)
	 */
	public List<CostForImplementing> getCostForImplementingCreatedByEmail(String email) {
		List<CostForImplementing> implementingCreated = new ArrayList<CostForImplementing>();
		List<CostForImplementing> implementingShared = new ArrayList<CostForImplementing>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<CostForImplementing> query1 = entityManager.createQuery("SELECT "
					+ "implementingId from SharedImplementing"
					+ " where fromUserId = ?1", CostForImplementing.class).setParameter(1, user);
			implementingShared = query1.getResultList();
			TypedQuery<CostForImplementing> query = null;
			if(implementingShared.size() != 0)
				query = entityManager.createQuery("SELECT costForImplementing from CostForImplementing"
						+ " as costForImplementing"
						+ " where costForImplementing NOT IN ?1 AND costForImplementing.userId = ?2",
						CostForImplementing.class)
						.setParameter(1, implementingShared).setParameter(2, user);
			else
				query = entityManager.createQuery("SELECT costForImplementing from CostForImplementing as "
						+ "costForImplementing"
						+ " where costForImplementing.userId = ?1", CostForImplementing.class).setParameter(1, user);
			implementingCreated = query.getResultList();
		} catch(NoResultException e) {
			implementingCreated = null;
		}
		finally {
			entityManager.close(); }
		return implementingCreated;
	}

	/**
	 * This method retrieves a list of cost for Implementing estimates the user with the specified
	 * email has shared with other users.
	 * @param email the email of the user who created the costs
	 * @return the list of costs (which will be null if none found)
	 */
	public List<CostForImplementing> getCostForImplementingSharedByEmail(String email) {
		List<CostForImplementing> sharedCostForImplementing = new ArrayList<CostForImplementing>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<CostForImplementing> query1 = entityManager.createQuery("SELECT "
					+ "si.implementingId from SharedImplementing si"
					+ " where si.fromUserId = ?1", CostForImplementing.class).setParameter(1, user);
			sharedCostForImplementing = query1.getResultList();
			System.out.println(sharedCostForImplementing.size());
		} catch(NoResultException e)
		{
			sharedCostForImplementing = null;
		}
		finally {
			entityManager.close();
		}
		return filterList(sharedCostForImplementing);
	}

	/**
	 * This method retrieves a list of received cost for Implementing estimates by the user with
	 * the specified email.
	 * @param email the email of the user who received the costs
	 * @return the list of cost estimates (which will be null if none found)
	 */
	public List<SharedImplementing> getReceivedSharedCostForImplementingByUser (String email) {
		UserLogin user = getUserByEmail(email);
		List<SharedImplementing> sharedProjects = new ArrayList<SharedImplementing>();
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery <SharedImplementing> query1 = entityManager.createQuery("SELECT sharedImplementing "
					+ "from SharedImplementing as sharedImplementing where "
					+ "sharedImplementing.toUserId = ?1)", SharedImplementing.class).setParameter(1, user);
			sharedProjects = query1.getResultList();
		}
		catch(NoResultException e)
		{
			sharedProjects = null;
		}
		finally {
			entityManager.close();
		}
		return sharedProjects;
	}

	/**
	 * This method checks if a cost for Implementing estimate with the given name has already been
	 * submitted by the user with the specified email. 
	 * @param email the email of the user
	 * @param name the name of the cost estimate
	 * @return the cost (which will be null if no cost found)
	 */
	public CostForImplementing checkCostForImplementingForSubmission(String email, String name) {
		UserLogin user = getUserByEmail(email);
		CostForImplementing costForImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			costForImplementing = (CostForImplementing) entityManager.createQuery("SELECT costForImplementing"
					+ " from CostForImplementing as costForImplementing where "
					+ "costForImplementing.userId = ?1 AND costForImplementing.name = ?2").setParameter(1, user)
					.setParameter(2, name).getSingleResult();
		}
		catch(NoResultException e) {
			costForImplementing = null;
		}
		finally {
			entityManager.close();
		}
		return costForImplementing;	
	}

	/**
	 * This method inserts a new COCOMO estimate.
	 * @param cocomo the estimate to be inserted
	 * @return the estimate if the insert was successful, otherwise null
	 */
	public Cocomo persistCocomo (Cocomo cocomo) {
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(cocomo);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return null;
		}
		finally {
			entityManager.close();
		}
		return cocomo;
	}

	/**
	 * This method updates an existing COCOMO estimate with new details.
	 * @param id the ID of the estimate to be updated
	 * @param name the new name of the estimate
	 * @param developmentMode the new development mode of the estimate
	 * @param productSize the new product size of the estimate
	 * @param confidence the new confidence of the estimate
	 * @param justification the new justification of the estimate
	 * @return the estimate (which will be null if update fails)
	 */
	public Cocomo updateCocomo (Long id, String name, String developmentMode, 
			BigDecimal productSize, String confidence, String justification) {
		Cocomo cocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			cocomo = entityManager.find(Cocomo.class, id);
			cocomo.setName(name);
			cocomo.setDevelopmentMode(developmentMode);
			cocomo.setProductSize(productSize);
			cocomo.setConfidence(confidence);
			cocomo.setJustification(justification);
			entityManager.getTransaction().begin();
			entityManager.merge(cocomo);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			cocomo = null;
		}
		finally {
			entityManager.close();
		}
		return cocomo;
	}

	/**
	 * This method deletes a COCOMO estimate based on its ID.
	 * @param id the ID of the estimate
	 * @return the estimate (which will be null if deletion fails)
	 */
	public Cocomo deleteCocomo (Long id) {
		Cocomo cocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			cocomo = entityManager.find(Cocomo.class, id);
			entityManager.getTransaction().begin();
			entityManager.remove(cocomo);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			cocomo = null;
		}
		finally {
			entityManager.close();
		}
		return cocomo;
	}

	/**
	 * This method retrieves a COCOMO estimate by its ID.
	 * @param id the ID of the estimate
	 * @return the estimate (which will be null if none found)
	 */
	public Cocomo getCocomoById(Long id) {
		Cocomo cocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			cocomo = entityManager.find(Cocomo.class, id);

		} catch(NoResultException e) {
			cocomo = null;
		}
		finally {
			entityManager.close(); }
		return cocomo;
	}

	/**
	 * This method retrieves the created COCOMO estimates of the user with the specified email.
	 * @param email the email of the user
	 * @return the list of COCOMO estimates (which will be null if none found)
	 */
	public List<Cocomo> getCocomoCreatedByEmail(String email) {
		List<Cocomo> cocomoCreated = new ArrayList<Cocomo>();
		List<Cocomo> cocomoShared = new ArrayList<Cocomo>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Cocomo> query1 = entityManager.createQuery("SELECT sc.cocomoId from SharedCocomo sc"
					+ " where sc.fromUserId = ?1", Cocomo.class).setParameter(1, user);
			cocomoShared = query1.getResultList();
			TypedQuery<Cocomo> query = null;
			if(cocomoShared.size() != 0)
				query = entityManager.createQuery("SELECT cocomo from Cocomo as cocomo"
						+ " where cocomo NOT IN ?1 AND cocomo.userId = ?2", Cocomo.class)
						.setParameter(1, cocomoShared).setParameter(2, user);
			else
				query = entityManager.createQuery("SELECT cocomo from Cocomo as cocomo"
						+ " where cocomo.userId = ?1", Cocomo.class).setParameter(1, user);
			cocomoCreated = query.getResultList();
		} catch(NoResultException e) {
			cocomoCreated = null;
		}
		finally {
			entityManager.close(); }
		return cocomoCreated;
	}

	/**
	 * This method retrieves a list of COCOMO estimates the user has shared with other users.
	 * @param email the email of the user
	 * @return the list of COCOMO estimates (which will be null if none found)
	 */
	public List<Cocomo> getCocomoSharedByEmail(String email) {
		List<Cocomo> sharedCocomo = new ArrayList<Cocomo>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<Cocomo> query1 = entityManager.createQuery("SELECT sc.cocomoId from SharedCocomo sc"
					+ " where sc.fromUserId = ?1", Cocomo.class).setParameter(1, user);
			sharedCocomo = query1.getResultList();
			System.out.println(sharedCocomo.size());
		} catch(NoResultException e)
		{
			sharedCocomo = null;
		}
		finally {
			entityManager.close();
		}
		return filterList(sharedCocomo);
	}

	/**
	 * This method retrieves a list of COCOMO estimates the user with the specified email
	 * received.
	 * @param email the email of the user
	 * @return the list of COCOMO estimates (which will be null if none found)
	 */
	public List<SharedCocomo> getReceivedSharedCocomoByUser (String email) {
		UserLogin user = getUserByEmail(email);
		List<SharedCocomo> sharedProjects = new ArrayList<SharedCocomo>();
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery <SharedCocomo> query1 = entityManager.createQuery("SELECT sharedCocomo "
					+ "from SharedCocomo as sharedCocomo where "
					+ "sharedCocomo.toUserId = ?1)", SharedCocomo.class).setParameter(1, user);
			sharedProjects = query1.getResultList();
		}
		catch(NoResultException e)
		{
			sharedProjects = null;
		}
		finally {
			entityManager.close();
		}
		return sharedProjects;
	}

	/**
	 * This method checks if a COCOMO estimate with the specified name has already been inserted
	 * by the user with the given email.
	 * @param email the email of the user
	 * @param name the name of the estimate
	 * @return the estimate (which will be null if not found)
	 */
	public Cocomo checkCocomoForSubmission(String email, String name) {
		UserLogin user = getUserByEmail(email);
		Cocomo cocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			cocomo = (Cocomo) entityManager.createQuery("SELECT cocomo from Cocomo as cocomo where "
					+ "cocomo.userId = ?1 AND cocomo.name = ?2").setParameter(1, user)
					.setParameter(2, name).getSingleResult();
		}
		catch(NoResultException e)
		{
			cocomo = null;
		}
		finally {
			entityManager.close();
		}
		return cocomo;	
	}

	/**
	 * This method inserts a new Technical Debt (TD) for Leasing estimate in the database (DB).
	 * @param tdInBuying the TD for Leasing estimate to be inserted
	 * @return the TD if the insert was successful, otherwise null
	 */
	public TDinBuying persistTDinBuying (TDinBuying tdInBuying) {
		try {
			entityManager = EMF.get().createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(tdInBuying);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			return null;
		}
		finally {
			entityManager.close();
		}
		return tdInBuying;
	}

	/**
	 * This method updates an existing TD for Leasing estimate with the new details.
	 * @param id the ID of the TD estimate to be updated
	 * @param name the new name of the TD estimate
	 * @param roi the new ROI of the TD estimate
	 * @param maxCapacity the new users maximum capacity of the TD estimate
	 * @param currentUsers the new value for current users of the TD estimate
	 * @param demandRaise the new value for the average variation in demand of the TD estimate
	 * @param subscriptionPrice the new value for the subscription price of the TD estimate
	 * @param raiseSubscriptionPrice the new value for the average variation in the monthly subscription price of the TD estimate
	 * @param cloudCost the new value for the cost in Cloud of the TD estimate
	 * @param raiseCloudCost the new value for the average variation in the Cloud cost of the TD estimate
	 * @param confidence the new confidence of the TD estimate
	 * @param serviceScalability the new service's scalability/market flexibility of the TD estimate
	 * @param qoS the new quality of service of the TD estimate
	 * @param riskOfFutureTD the new risk of future TD of the estimate
	 * @param realOptionsValuation the new real options valuation of the TD estimate
	 * @param justification the new justification of the TD estimate
	 * @return the TD (which will be null if update fails)
	 */
	public TDinBuying updateTDInBuying (Long id, String name, int roi, BigInteger maxCapacity, BigInteger currentUsers,
			BigDecimal demandRaise, BigDecimal subscriptionPrice, BigDecimal raiseSubscriptionPrice,
			BigDecimal cloudCost, BigDecimal raiseCloudCost, String confidence, String serviceScalability,
			String qoS, String riskOfFutureTD, String realOptionsValuation, String justification) {
		TDinBuying tdInBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			tdInBuying = entityManager.find(TDinBuying.class, id);
			tdInBuying.setName(name);
			tdInBuying.setRoi(roi);
			tdInBuying.setMaxCapacity(maxCapacity);
			tdInBuying.setCurrentUsers(currentUsers);
			tdInBuying.setDemandRaise(demandRaise);
			tdInBuying.setSubscriptionPrice(subscriptionPrice);
			tdInBuying.setRaiseSubscriptionPrice(raiseSubscriptionPrice);
			tdInBuying.setCloudCost(cloudCost);
			tdInBuying.setRaiseCloudCost(raiseCloudCost);
			tdInBuying.setConfidence(confidence);
			tdInBuying.setServiceScalability(serviceScalability);
			tdInBuying.setQoS(qoS);
			tdInBuying.setRiskOfFutureTD(riskOfFutureTD);
			tdInBuying.setRealOptionsValuation(realOptionsValuation);
			tdInBuying.setJustification(justification);
			entityManager.getTransaction().begin();
			entityManager.merge(tdInBuying);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			tdInBuying = null;
		}
		finally {
			entityManager.close();
		}
		return tdInBuying;
	}

	/**
	 * This method deletes a TD for Leasing estimate by its ID.
	 * @param id the ID of the TD estimate to be deleted
	 * @return the TD estimate (which will be null if delete fails)
	 */
	public TDinBuying deleteTDInBuying (Long id) {
		TDinBuying tdInBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			tdInBuying = entityManager.find(TDinBuying.class, id);
			entityManager.getTransaction().begin();
			entityManager.remove(tdInBuying);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			tdInBuying = null;
		}
		finally {
			entityManager.close();
		}
		return tdInBuying;
	}

	/**
	 * This method retrieves a TD for Leasing estimate by its ID.
	 * @param id the ID of the sought TD estimate
	 * @return the TD estimate (which will be null if not found)
	 */
	public TDinBuying getTDinBuyingById(Long id) {
		TDinBuying tdInBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			tdInBuying = entityManager.find(TDinBuying.class, id);

		} catch(NoResultException e) {
			tdInBuying = null;
		}
		finally {
			entityManager.close(); }
		return tdInBuying;
	}

	/**
	 * This method retrieves a list of TD for Leasing estimates created by the user with the
	 * specified email.
	 * @param email the email of the user
	 * @return the list of estimates (which will be null if none found)
	 */
	public List<TDinBuying> getTDinBuyingCreatedByEmail(String email) {
		List<TDinBuying> tdInBuyingCreated = new ArrayList<TDinBuying>();
		List<TDinBuying> tdInBuyingShared = new ArrayList<TDinBuying>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<TDinBuying> query1 = entityManager.createQuery("SELECT sb.buyingId from SharedBuying sb"
					+ " where sb.fromUserId = ?1", TDinBuying.class).setParameter(1, user);
			tdInBuyingShared = query1.getResultList();
			TypedQuery<TDinBuying> query = null;
			if(tdInBuyingShared.size() != 0)
				query = entityManager.createQuery("SELECT tdInBuying from TDinBuying as tdInBuying"
						+ " where tdInBuying NOT IN ?1 AND tdInBuying.userId = ?2", TDinBuying.class)
						.setParameter(1, tdInBuyingShared).setParameter(2, user);
			else
				query = entityManager.createQuery("SELECT tdInBuying from TDinBuying as tdInBuying"
						+ " where tdInBuying.userId = ?1", TDinBuying.class).setParameter(1, user);
			tdInBuyingCreated = query.getResultList();
		} catch(NoResultException e) {
			tdInBuyingCreated = null;
		}
		finally {
			entityManager.close(); }
		return tdInBuyingCreated;
	}

	/**
	 * This method retrieves a list of TD for Leasing estimates the user has shared with other
	 * users.
	 * @param email the email of the user
	 * @return the list of estimates (which will be null if none found)
	 */
	public List<TDinBuying> getTDinBuyingSharedByEmail(String email) {
		List<TDinBuying> sharedEstimates = new ArrayList<TDinBuying>();
		UserLogin user = getUserByEmail(email);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<TDinBuying> query1 = entityManager.createQuery("SELECT sb.buyingId from SharedBuying sb"
					+ " where sb.fromUserId = ?1", TDinBuying.class).setParameter(1, user);
			sharedEstimates = query1.getResultList();
			System.out.println(sharedEstimates.size());
		} catch(NoResultException e)
		{
			sharedEstimates = null;
		}
		finally {
			entityManager.close();
		}
		return filterList(sharedEstimates);
	}

	/**
	 * The method retrieves a list of TD for Leasing estimates the user has received (they were shared with 
	 * the user).
	 * @param email the email of the user
	 * @return the list of estimates (which will be null if none found)
	 */
	public List<SharedBuying> getReceivedSharedBuyingByUser (String email) {
		UserLogin user = getUserByEmail(email);
		List<SharedBuying> sharedEstimates = new ArrayList<SharedBuying>();
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery <SharedBuying> query1 = entityManager.createQuery("SELECT sharedBuying "
					+ "from SharedBuying as sharedBuying where "
					+ "sharedBuying.toUserId = ?1)", SharedBuying.class).setParameter(1, user);
			sharedEstimates = query1.getResultList();
		}
		catch(NoResultException e)
		{
			sharedEstimates = null;
		}
		finally {
			entityManager.close();
		}
		return sharedEstimates;
	}

	/**
	 * The method checks if a TD for Leasing estimate with the given name has already been inserted
	 * by the user with the specified email.
	 * @param email the email of the user
	 * @param name the name of the estimate
	 * @return the estimate (which will be null if none found)
	 */
	public TDinBuying checkTDinBuyingForSubmission(String email, String name) {
		UserLogin user = getUserByEmail(email);
		TDinBuying tdInBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			tdInBuying = (TDinBuying) entityManager.createQuery("SELECT tdInBuying from TDinBuying "
					+ "as tdInBuying where "
					+ "tdInBuying.userId = ?1 AND tdInBuying.name = ?2").setParameter(1, user)
					.setParameter(2, name).getSingleResult();
		}
		catch(NoResultException e)
		{
			tdInBuying = null;
		}
		finally {
			entityManager.close();
		}
		return tdInBuying;	
	}

	/**
	 * The method allows the sharing of a COCOMO estimate between two users identified by their
	 * email addresses.
	 * @param sharedWithEmail the email of the receiver
	 * @param cocomoShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @param timestamp the sharing date
	 * @return the sharedCocomo estimate (which will be null if the insertion failed)
	 */
	public SharedCocomo shareCocomo(String sharedWithEmail, Long cocomoShared, String sharedFromEmail, Timestamp timestamp) {
		SharedCocomo shareCocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Cocomo cocomo = entityManager.find(Cocomo.class, cocomoShared);
			shareCocomo = new SharedCocomo(sharedFrom, sharedWith, cocomo, timestamp); 
			entityManager.getTransaction().begin();
			entityManager.persist(shareCocomo);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			shareCocomo = null;
		}
		finally {
			entityManager.close();
		}
		return shareCocomo;	
	}

	/**
	 * The method checks if a COCOMO estimate has already been shared with the specified user.
	 * @param sharedWithEmail the email of the recever
	 * @param cocomoShared the ID of the estimate
	 * @param sharedFromEmail the email of the user who shared the estimate
	 * @return true of the estimate was not shared with the designated receiver, otherwise false
	 */
	public boolean checkSharedCocomo(String sharedWithEmail, Long cocomoShared, String sharedFromEmail) {
		SharedCocomo sharedCocomo = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Cocomo cocomo = entityManager.find(Cocomo.class, cocomoShared);
			sharedCocomo = (SharedCocomo) entityManager.createQuery("SELECT sharedCocomo from "
					+ "SharedCocomo as sharedCocomo "
					+ "where sharedCocomo.fromUserId =?1 AND sharedCocomo.toUserId = ?2 AND "
					+ "sharedCocomo.cocomoId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, cocomo).getSingleResult();
		}
		catch(NoResultException e)
		{
			return true;
		}
		finally {
			entityManager.close();
		}
		if(sharedCocomo != null)
			return false;
		else return true;
	}

	/**
	 * The method retrieves a list of users the COCOMO estimate was shared with.
	 * @param cocomoId the ID of the estimate
	 * @return the sharedCocomo list (which will be null if none found)
	 */
	public List<SharedCocomo> getSharedUsersByCocomo(Long cocomoId) {
		List<SharedCocomo> sharedList = new ArrayList<SharedCocomo>();
		Cocomo cocomo = getCocomoById(cocomoId);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<SharedCocomo> query = entityManager.createQuery("SELECT sharedCocomo "
					+ "from SharedCocomo as sharedCocomo "
					+ " where sharedCocomo.cocomoId = ?1", SharedCocomo.class)
					.setParameter(1, cocomo);
			sharedList = query.getResultList();
		} catch(NoResultException e)
		{
			sharedList = null;
		}
		finally {
			entityManager.close(); 
		}
		return sharedList;
	}

	/**
	 * The method removes a receiver from a COCOMO estimate.
	 * @param sharedWithEmail the email of the recever
	 * @param cocomoShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteSharedCocomoUser(String sharedWithEmail, Long cocomoShared, String sharedFromEmail) {
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			Cocomo cocomo = entityManager.find(Cocomo.class, cocomoShared);
			SharedCocomo sharedCocomo = (SharedCocomo) entityManager.createQuery("SELECT sharedCocomo"
					+ " from SharedCocomo as sharedCocomo"
					+ " where sharedCocomo.fromUserId =?1 AND sharedCocomo.toUserId = ?2 "
					+ "AND sharedCocomo.cocomoId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, cocomo).getSingleResult();
			entityManager.getTransaction().begin();
			entityManager.remove(sharedCocomo);
			entityManager.getTransaction().commit();
		}
		catch(NoResultException e)
		{
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;
	}

	/**
	 * The method allows the sharing of a cost for Implementing estimate between two users 
	 * identified by their email addresses.
	 * @param sharedWithEmail the email of the receiver
	 * @param implementIdShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @param timestamp the sharing date
	 * @return the sharedImplementing (which will be null if the insertion failed)
	 */
	public SharedImplementing shareImplementing(String sharedWithEmail, Long implementIdShared, String sharedFromEmail, Timestamp timestamp) {
		SharedImplementing sharedImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			CostForImplementing costForImplementing = entityManager.find(CostForImplementing.class, implementIdShared);
			sharedImplementing = new SharedImplementing(sharedFrom, sharedWith, costForImplementing, timestamp); 
			entityManager.getTransaction().begin();
			entityManager.persist(sharedImplementing);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			sharedImplementing = null;
		}
		finally {
			entityManager.close();
		}
		return sharedImplementing;	
	}

	/**
	 * The method checks if a cost for Implementing estimate has already been shared with the 
	 * specified user.
	 * @param sharedWithEmail the email of the receiver
	 * @param implementingShared the ID of the estimate
	 * @param sharedFromEmail the email of the user who shared the estimate
	 * @return true of the estimate was not shared with the designated receiver, otherwise false
	 */
	public boolean checkSharedImplementing(String sharedWithEmail, Long implementingShared, String sharedFromEmail) {
		SharedImplementing sharedImplementing = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			CostForImplementing costForIplementing = entityManager.find(CostForImplementing.class, implementingShared);
			sharedImplementing = (SharedImplementing) entityManager.createQuery("SELECT sharedImplementing from "
					+ "SharedImplementing as sharedImplementing "
					+ "where sharedImplementing.fromUserId =?1 AND sharedImplementing.toUserId = ?2 "
					+ "AND sharedImplementing.implementingId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, costForIplementing).getSingleResult();
		}
		catch(NoResultException e)
		{
			return true;
		}
		finally {
			entityManager.close();
		}
		if(sharedImplementing != null)
			return false;
		else return true;
	}

	/**
	 * The method retrieves a list of users the cost for Implementing estimate was shared with.
	 * @param implementingId the ID of the estimate
	 * @return the sharedImplementing list (which will be null if none found)
	 */
	public List<SharedImplementing> getSharedUsersByImplementing(Long implementingId) {
		List<SharedImplementing> sharedList = new ArrayList<SharedImplementing>();
		CostForImplementing costForImplementing = getCostForImplementingById(implementingId);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<SharedImplementing> query = entityManager.createQuery("SELECT sharedImplementing from "
					+ "SharedImplementing as sharedImplementing "
					+ " where sharedImplementing.implementingId = ?1", SharedImplementing.class)
					.setParameter(1, costForImplementing);
			sharedList = query.getResultList();
		} catch(NoResultException e)
		{
			sharedList = null;
		}
		finally {
			entityManager.close(); 
		}
		return sharedList;
	}

	/**
	 * The method removes a receiver from a cost for Implementing estimate.
	 * @param sharedWithEmail the email of the receiver
	 * @param implementingShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteSharedImplementingUser(String sharedWithEmail, Long implementingShared, String sharedFromEmail) {
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			CostForImplementing costForImplementing = entityManager.find(CostForImplementing.class, implementingShared);
			SharedImplementing sharedImplementing = (SharedImplementing) entityManager.createQuery("SELECT sharedImplementing"
					+ " from SharedImplementing as sharedImplementing"
					+ " where sharedImplementing.fromUserId =?1 AND sharedImplementing.toUserId = ?2 "
					+ "AND sharedImplementing.implementingId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, costForImplementing)
					.getSingleResult();
			entityManager.getTransaction().begin();
			entityManager.remove(sharedImplementing);
			entityManager.getTransaction().commit();
		}
		catch(NoResultException e)
		{
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;
	}

	/**
	 * The method allows the sharing of a TD for Leasing estimate between two users identified by
	 * their email addresses.
	 * @param sharedWithEmail the email of the receiver
	 * @param buyingShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @param timestamp the sharing date
	 * @return the sharedCocomo (which will be null if the insertion failed)
	 */
	public SharedBuying shareTDinBuying(String sharedWithEmail, Long buyingShared, String sharedFromEmail, Timestamp timestamp) {
		SharedBuying sharedTDinBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			TDinBuying tdInBuying = entityManager.find(TDinBuying.class, buyingShared);
			sharedTDinBuying = new SharedBuying(sharedFrom, sharedWith, tdInBuying, timestamp); 
			entityManager.getTransaction().begin();
			entityManager.persist(sharedTDinBuying);
			entityManager.getTransaction().commit();
		}
		catch(Exception e) {
			sharedTDinBuying = null;
		}
		finally {
			entityManager.close();
		}
		return sharedTDinBuying;	
	}

	/**
	 * The method checks if a TD for Leasing estimate has already been shared with the specified user.
	 * @param sharedWithEmail the email of the receiver
	 * @param buyingShared the ID of the estimate
	 * @param sharedFromEmail the email of the user who shared the estimate
	 * @return true of the estimate was not shared with the designated receiver, otherwise false
	 */
	public boolean checkBuyingShared(String sharedWithEmail, Long buyingShared, String sharedFromEmail) {
		SharedBuying sharedTDinBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			TDinBuying tdInBuying = entityManager.find(TDinBuying.class, buyingShared);
			sharedTDinBuying = (SharedBuying) entityManager.createQuery("SELECT sharedBuying from "
					+ "SharedBuying as sharedBuying "
					+ "where sharedBuying.fromUserId =?1 AND sharedBuying.toUserId = ?2 AND "
					+ "sharedBuying.buyingId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, tdInBuying)
					.getSingleResult();
		}
		catch(NoResultException e)
		{
			return true;
		}
		finally {
			entityManager.close();
		}
		if(sharedTDinBuying != null)
			return false;
		else return true;
	}

	/**
	 * The method retrieves a list of users the TD for Leasing estimate was shared with.
	 * @param buyingId the ID of the estimate
	 * @return the sharedImplementing list (which will be null if none found)
	 */
	public List<SharedBuying> getSharedUsersByBuying(Long buyingId) {
		List<SharedBuying> sharedList = new ArrayList<SharedBuying>();
		TDinBuying tdInBuying = getTDinBuyingById(buyingId);
		try {
			entityManager = EMF.get().createEntityManager();
			TypedQuery<SharedBuying> query = entityManager.createQuery("SELECT sharedBuying from "
					+ "SharedBuying as sharedBuying "
					+ " where sharedBuying.buyingId = ?1", SharedBuying.class)
					.setParameter(1, tdInBuying);
			sharedList = query.getResultList();
		} catch(NoResultException e)
		{
			sharedList = null;
		}
		finally {
			entityManager.close(); 
		}
		return sharedList;
	}

	/**
	 * The method removes a receiver from a TD for Leasing estimate.
	 * @param sharedWithEmail the email of the receiver
	 * @param buyingShared the ID of the estimate
	 * @param sharedFromEmail the email of the sender
	 * @return true if delete successful, otherwise false
	 */
	public boolean deleteSharedBuyingUser(String sharedWithEmail, Long buyingShared, String sharedFromEmail) {
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin sharedWith = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedWithEmail).getSingleResult();
			UserLogin sharedFrom = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, sharedFromEmail).getSingleResult();
			TDinBuying tdInBuying = entityManager.find(TDinBuying.class, buyingShared);
			SharedBuying sharedBuying = (SharedBuying) entityManager.createQuery("SELECT sharedBuying"
					+ " from SharedBuying as sharedBuying"
					+ " where sharedBuying.fromUserId =?1 AND sharedBuying.toUserId = ?2 "
					+ "AND sharedBuying.buyingId = ?3")
					.setParameter(1, sharedFrom).setParameter(2, sharedWith).setParameter(3, tdInBuying)
					.getSingleResult();
			entityManager.getTransaction().begin();
			entityManager.remove(sharedBuying);
			entityManager.getTransaction().commit();
		}
		catch(NoResultException e)
		{
			return false;
		}
		finally {
			entityManager.close();
		}
		return true;
	}

	/**
	 * The method retrieves a TD for Leasing estimate by the email of the user who created it and
	 * by the name of the estimate.
	 * @param email the email of the user
	 * @param name the name of the TD for Leasing estimate
	 * @return the estimate (which will be null if not found)
	 */
	public TDinBuying getTDinBuyingByUserEmailAndTDName(String email, String name) {
		TDinBuying tdInBuying = null;
		try {
			entityManager = EMF.get().createEntityManager();
			UserLogin user = (UserLogin) entityManager.createQuery("SELECT userlogin from UserLogin as userlogin where "
					+ "userlogin.email = ?1").setParameter(1, email).getSingleResult();
			tdInBuying = (TDinBuying) entityManager.createQuery("SELECT tdInBuying from TDinBuying as tdInBuying "
					+ "where tdInBuying.userId = ?1 AND tdInBuying.name = ?2")
					.setParameter(1, user).setParameter(2, name).getSingleResult();
		}
		catch(NoResultException e)
		{
			tdInBuying = null;
		}
		finally {
			entityManager.close();
		}
		return tdInBuying;
	}
}
