package com.example.hibtdq;

import java.io.IOException;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.Project;
import com.example.model.Scenario;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ProjectServlet is a class that deals with the Project operations, namely with creating, showing,
 * editing, deleting and submitting a received project. This communication is between Project Managers.
 * @author Georgios Skourletopoulos
 * @version 2 August 2013
 */
@SuppressWarnings("serial")
public class ProjectServlet extends HttpServlet {

	DBService service = new DBService ();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Project task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		System.out.println("Project get");
		if(!checkJob(req,resp)) {
			String dispatch = null;
			dispatch = req.getParameter("dispatchActionProject");
			if(dispatch == null)
				dispatch = "new";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "new":
				newProject(req,resp);
				break;
			case "edit":
				req.setAttribute("saveDisabled", "submit");
				req.setAttribute("submitDisabled", "hidden");
				req.setAttribute("action", "submit");
				showProject(req,resp);
				break;
			case "submit": 
				if(req.getAttribute("response") == null)
					submitProject(req,resp);
				else {
					newProject(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "showShared": 
				req.setAttribute("saveDisabled", "hidden");
				req.setAttribute("submitDisabled", "submit");
				req.setAttribute("action", "submitShared");
				showProject(req,resp); 			
				break;
			case "submitShared" :
				if(req.getAttribute("response") == null)
					submitSharedProject(req,resp);
				else {	
					req.setAttribute("saveDisabled", "hidden");
					req.setAttribute("submitDisabled", "submit");
					req.setAttribute("action", "submitShared");
					showProject(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "save":
				if(req.getAttribute("response") == null)
					saveRedirect(req,resp); 
				else {
					req.setAttribute("saveDisabled", "submit");
					req.setAttribute("submitDisabled", "hidden");
					req.setAttribute("action", "submit");
					showProject(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "delete":
				if(req.getAttribute("response") == null)
					deleteProject(req,resp);
				else {
					showProject(req,resp);
					req.removeAttribute("response");
				}
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform
	 * operations in the Project area.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @return true if the user is not a Project Manager redirecting him to the authentication page, otherwise false if he is a Project Manager
	 * @throws IOException
	 */
	private boolean checkJob(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserLogin usr = service.getUserByEmail(user.getEmail());
		HttpSession session = req.getSession();
		if(!usr.getJobId().getName().trim().equals("Project Manager")) {
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
			return true;
		}
		return false;
	}

	/**
	 * This method deals with redirecting in the Create Project form.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void newProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("project.jsp");
		req.setAttribute("projectCateg", buildProjCateg(null));
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("action", "submit");
		req.setAttribute("submitDisabled", "submit");
		rd.forward(req, resp);
	}

	/**
	 * This method deals with showing the details of an already created project.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Long id = ((Project)session.getAttribute("projectId")).getId();
		System.out.println("showProject() ID " + id);
		Project project = service.getProjectById(id);
		RequestDispatcher rd = req.getRequestDispatcher("project.jsp");
		fillDetails(project,req);
		rd.forward(req, resp);
	}

	/**
	 * This method deals with submitting a new project in order to be added into the created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void submitProject (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		List<Project> projectList = service.getProjectsByUserEmail(user.getEmail());
		Iterator<Project> iter = projectList.iterator();
		RequestDispatcher rd = req.getRequestDispatcher("/project");
		boolean error = false;

		/*
		 * Checks if the name already exists in the database.
		 */
		while(iter.hasNext()) 
			if((iter.next().getProjectName().equals(req.getParameter("projectName")))) {
				req.setAttribute("response", "Project name already exists!");
				error = true;
				System.out.println("error true");
			}
		Project project = null;
		UserLogin usr = service.getUserByEmail(user.getEmail());	

		Date startDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectStart").substring(0, 4)) - 1900,
				Integer.parseInt(req.getParameter("projectStart").substring(5, 7)) - 1,
				Integer.parseInt(req.getParameter("projectStart").substring(8, 10))).getTime());
		Date endDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectEnd").substring(0, 4)) - 1900, 
				Integer.parseInt(req.getParameter("projectEnd").substring(5, 7)) - 1, 
				Integer.parseInt(req.getParameter("projectEnd").substring(8, 10))).getTime());

		/*
		 * Checks if the completion date of a project is greater than the start date.
		 */
		if(endDate.compareTo(startDate) <= 0) {
			req.setAttribute("response", "The completion date of a project should be greater than"
					+ " the start date!");
			error = true;
		}

		/*
		 * If there is not a previous project with the same name, then it is inserted in the database.
		 */
		if(!error) {
			project = new Project(usr, req.getParameter("projectName"),
					req.getParameter("projectCategory"), req.getParameter("projectGoals"),
					startDate, endDate);
			if(service.persistProject(project) != null) {
				resp.sendRedirect("/mainPM");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/project");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/project");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with submitting a received project in order to be added into the
	 * created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void submitSharedProject (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		System.out.println("Submit shared");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		List<Project> projectList = service.getProjectsByUserEmail(user.getEmail());
		Iterator<Project> iter = projectList.iterator();
		RequestDispatcher rd = req.getRequestDispatcher("/project");
		boolean error = false;

		/*
		 * Checks if the name already exists in the database.
		 */
		while(iter.hasNext()) 
			if((iter.next().getProjectName().equals(req.getParameter("projectName")))) {
				req.setAttribute("response", "Project name already exists!");
				error = true;
				System.out.println("error true");
			}
		Project project = null;
		UserLogin usr = service.getUserByEmail(user.getEmail());	

		Date startDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectStart").substring(0, 4)) - 1900,
				Integer.parseInt(req.getParameter("projectStart").substring(5, 7)) - 1,
				Integer.parseInt(req.getParameter("projectStart").substring(8, 10))).getTime());
		Date endDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectEnd").substring(0, 4)) - 1900, 
				Integer.parseInt(req.getParameter("projectEnd").substring(5, 7)) - 1, 
				Integer.parseInt(req.getParameter("projectEnd").substring(8, 10))).getTime());

		/*
		 * Checks if the completion date of a project is greater than the start date.
		 */
		if(endDate.compareTo(startDate) <= 0) {
			req.setAttribute("response", "The completion date of a project should be greater than"
					+ " the start date!");
			error = true;
		}

		/*
		 * If there is not a previous project with the same name, then it is inserted in the database.
		 */
		if(!error) {
			project = new Project(usr, req.getParameter("projectName"),
					req.getParameter("projectCategory"), req.getParameter("projectGoals"),
					startDate, endDate);
			Project persisted = service.persistProject(project); 
			if(persisted != null) {

				List<Scenario> scenariosAttached = service.getScenarioByProject((Project)session.getAttribute("projectId"));

				/*
				 * When the user submits the received project, all the scenarios attached on that
				 * project are submitted as well.
				 */
				for (Scenario scenario : scenariosAttached) {
					service.persistScenario(new Scenario(persisted, scenario.getScenarioName(), 
							scenario.getScenarioType(), scenario.getRequirementType(), scenario.getPriority()));
				}
				resp.sendRedirect("/mainPM");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/project");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/project");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with deleting a created or received project.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void deleteProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Project project = ((Project)session.getAttribute("projectId"));
		Long id = project.getId();
		String projectName = project.getProjectName();		
		UserLogin projectCreator = project.getUserId();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if(projectCreator.getEmail().equals(user.getEmail())) {
			if(!service.deleteProject(id)) {
				req.setAttribute("response", projectName + " could not be deleted!");
				RequestDispatcher rd = req.getRequestDispatcher("/project");    //if the deletion is unsuccessful redirects to the project page with the corresponding message
				rd.forward(req, resp);
			}
			else {
				resp.sendRedirect("/mainPM");    //if the deletion is successful redirects to the main page
			}
		}
		else {

			/*
			 * If the received project is not deleted, it cannot be unshared, otherwise it is unshared and
			 * redirects to the main page.
			 */
			if(!service.deleteSharedUser(user.getEmail(), project.getId(), projectCreator.getEmail())) {
				req.setAttribute("response", projectName + " could not be unshared!");
				RequestDispatcher rd = req.getRequestDispatcher("/project");    //if the deletion is unsuccessful redirects to the project page with the corresponding message
				rd.forward(req, resp);
			}
			else {
				resp.sendRedirect("/mainPM");
			}
		}
	}

	/**
	 * This method creates the Project category frame with the corresponding options to be selected.
	 * @param projCategory the options provided
	 * @return projCateg the option selected as a String
	 */
	private String buildProjCateg(String projCategory) {
		String projCateg = "";
		String sd = "<option>Software Development</option>";
		String crypto = "<option>Cryptography</option>";
		String dm = "<option>Data Mining</option>";
		String stats = "<option>Statistics</option>";
		String hci = "<option>Human-Computer Interaction</option>";
		String cs = "<option>Computer Security</option>";
		String os = "<option>Operating Systems</option>";
		String se = "<option>Software Engineering</option>";
		String ca = "<option>Computer Architecture</option>";
		String dsa = "<option>Data Structures and Algorithms</option>";
		if(projCategory == null) sd = "<option selected=\"selected\">Software Development</option>";
		else { 
			switch (projCategory) {
			case "Software Development": sd = "<option selected=\"selected\">Software Development</option>";
			break;
			case "Cryptography": crypto = "<option selected=\"selected\">Cryptography</option>";
			break;
			case "Data Mining": dm = "<option selected=\"selected\">Data Mining</option>";
			break;
			case "Statistics": stats = "<option selected=\"selected\">Statistics</option>";
			break;
			case "Human-Computer Interaction": hci = "<option selected=\"selected\">Human-Computer Interaction</option>";
			break;
			case "Computer Security": cs = "<option selected=\"selected\">Computer Security</option>";
			break;
			case "Operating Systems": os = "<option selected=\"selected\">Operating Systems</option>";
			break;
			case "Software Engineering": se = "<option selected=\"selected\">Software Engineering</option>";
			break;
			case "Computer Architecture": ca = "<option selected=\"selected\">Computer Architecture</option>";
			break;
			case "Data Structures and Algorithms": dsa = "<option selected=\"selected\">Data Structures and Algorithms</option>";
			break;
			default: sd = "<option selected=\"selected\">Software Development</option>";
			break;
			}
		}
		projCateg = sd + crypto + dm + stats + hci + cs + os + se + ca + dsa;
		return projCateg;
	}

	/**
	 * This method deals with showing the details that were previously saved for a specific project.
	 * @param project the corresponding project
	 * @param req is the request object
	 */
	private void fillDetails(Project project, HttpServletRequest req) {
		req.setAttribute("projectNameValue", project.getProjectName());
		req.setAttribute("projectGoalsValue", project.getProjectGoals());
		req.setAttribute("projectStartValue", project.getProjectStart());
		req.setAttribute("projectEndValue", project.getProjectEnd());
		req.setAttribute("projectCateg", buildProjCateg(project.getProjectCategory()));
	}

	/**
	 * This method deals with saving the actions performed and the elements provided.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void saveRedirect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("save redirect");
		HttpSession session = req.getSession();
		Long id = ((Project)session.getAttribute("projectId")).getId();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Project project = service.getProjectById(id);
		boolean error = false;
		RequestDispatcher rd = req.getRequestDispatcher("/project");
		if(!project.getProjectName().equals(req.getParameter("projectName")))
		{
			List<Project> projectList = service.getProjectsByUserEmail(user.getEmail());
			Iterator<Project> iter = projectList.iterator();

			/*
			 * Checks if the name already exists in the database.
			 */
			while(iter.hasNext()) 
				if((iter.next().getProjectName().equals(req.getParameter("projectName")))) {
					req.setAttribute("response", "Project name already exists!");
					error = true;
					System.out.println("project name exists");
				}
		}

		Date startDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectStart").substring(0, 4)) - 1900,
				Integer.parseInt(req.getParameter("projectStart").substring(5, 7)) - 1,
				Integer.parseInt(req.getParameter("projectStart").substring(8, 10))).getTime());
		Date endDate = new Date(new java.util.Date(Integer.parseInt(req.getParameter("projectEnd").substring(0, 4)) - 1900, 
				Integer.parseInt(req.getParameter("projectEnd").substring(5, 7)) - 1, 
				Integer.parseInt(req.getParameter("projectEnd").substring(8, 10))).getTime());

		/*
		 * Checks if the completion date of a project is greater than the start date.
		 */
		if(endDate.compareTo(startDate) <= 0) {
			req.setAttribute("response", "The completion date of a project should be greater than"
					+ " the start date!");
			error = true;
		}

		/*
		 * If there is not a previous project with the same name, then it is updated in the database.
		 */
		if(!error) {
			project = service.updateProject(id,req.getParameter("projectName"),req.getParameter("projectCategory"),
					req.getParameter("projectGoals"), startDate, endDate);

			if(project != null) {
				resp.sendRedirect("/mainPM");
			}
			else {
				req.setAttribute("response", "Update unsuccessful!");
				rd.forward(req, resp);
			}
		}
		else {
			rd.forward(req, resp);
		}
	}
}
