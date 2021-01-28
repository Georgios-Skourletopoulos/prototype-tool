package com.example.hibtdq;

import java.io.IOException;
import java.math.BigInteger;
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
 * ScenariosServlet is a class that deals with creating, showing, submitting, editing and deleting
 * a scenario attached in a specific project.
 * @author Georgios Skourletopoulos
 * @version 1 August 2013
 */
@SuppressWarnings("serial")
public class ScenariosServlet extends HttpServlet {

	DBService service = new DBService ();

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Scenario task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			System.out.println("Scenarios get");
			HttpSession session = req.getSession();
			Project project = (Project) session.getAttribute("projectId");
			System.out.println(project.getProjectName());
			String dispatch = null;
			dispatch = req.getParameter("dispatchActionScenario");
			if(dispatch == null)
				dispatch = "view";
			else if (dispatch.trim() == "")
				dispatch = "view";
			RequestDispatcher rd;
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "view":
				rd = req.getRequestDispatcher("scenarios.jsp");
				List<Scenario> scenariosList = service.getScenarioByProject(project);

				/*
				 * If there are no scenarios in the list, it is displayed the corresponding message.
				 */
				if(scenariosList.size() == 0)
					req.setAttribute("noScens", "<h3 class=\"warning\">No scenarios available.</h3>");
				req.setAttribute("scenariosList", scenariosList);
				rd.forward(req, resp);
				break;
			case "new":
				req.setAttribute("submitHidden", "submit");
				req.setAttribute("saveHidden", "hidden");
				newScenario(req,resp);
				break;
			case "submit": 
				if(req.getAttribute("response") == null)
					submitScenario(req,resp);
				else {
					req.setAttribute("submitHidden", "submit");
					req.setAttribute("saveHidden", "hidden");
					newScenario(req,resp); 	
					req.removeAttribute("response");
				}
				break;
			case "delete":
				if(req.getAttribute("response") == null) 
					deleteScenario(req,resp);
				else {
					showScenario(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "save":
				if(req.getAttribute("response") == null) {
					saveRedirect(req,resp); 
				}
				else {
					req.setAttribute("submitHidden", "hidden");
					req.setAttribute("saveHidden", "submit");
					showScenario(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "showDetails":
				req.setAttribute("submitHidden", "hidden");
				req.setAttribute("saveHidden", "submit");
				showScenario(req,resp);
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform
	 * operations in the Scenario area.
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
	 * This method deals with showing the details of an already created scenario.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showScenario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("show details");
		HttpSession session = req.getSession();
		BigInteger id;
		if(req.getParameter("setId") != null) {
			id = new BigInteger(req.getParameter("setId")); 
			session.setAttribute("scenarioId", service.getScenarioById(id)); }
		else 
			id = ((Scenario)session.getAttribute("scenarioId")).getId();
		System.out.println("showScenario() ID " + id);
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		Scenario scenario = service.getScenarioById(id);
		RequestDispatcher rd = req.getRequestDispatcher("scenario.jsp");
		fillDetails(scenario,req);
		rd.forward(req, resp);
	}

	/**
	 * This method deals with redirecting in the Add Scenario form.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void newScenario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("scenario.jsp");
		req.setAttribute("scenarioType", buildScenarioType(null));
		req.setAttribute("requirementType", buildRequirementType(null));
		req.setAttribute("priority", buildPriority(null));
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		rd.forward(req, resp);
	}

	/**
	 * This method deals with submitting a new scenario in order to be added into the list with the attached scenarios.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitScenario (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		HttpSession session = req.getSession();
		Long projectId = ((Project) session.getAttribute("projectId")).getId();
		Project project = service.getProjectById(projectId);
		List<Scenario> scenariosList = service.getScenarioByProject(project);
		Iterator<Scenario> iter = scenariosList.iterator();
		RequestDispatcher rd = req.getRequestDispatcher("/scenarios");
		boolean error = false;

		/*
		 * Checks if the name already exists in the database.
		 */
		while(iter.hasNext()) 
			if((iter.next().getScenarioName().equals(req.getParameter("scenarioName")))) {
				req.setAttribute("response", "Scenario name already exists for that project!");
				error = true;
				System.out.println("error true");
			}
		Scenario scenario = null;

		/*
		 * If there is not a previous scenario with the same name, then it is inserted in the database.
		 */
		if(!error) {
			scenario = new Scenario(project, req.getParameter("scenarioName"), req.getParameter("scenarioType"), 
					req.getParameter("requirementType"), req.getParameter("priority"));
			if(service.persistScenario(scenario) != null) {
				resp.sendRedirect("/scenarios");
			}
			else {
				req.setAttribute("response", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/scenarios");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/scenarios");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with deleting a created scenario.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void deleteScenario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BigInteger id = new BigInteger(req.getParameter("setId"));
		String scenarioName = service.getScenarioById(id).getScenarioName();
		if(!service.deleteScenario(id)) {
			req.setAttribute("response", scenarioName + " could not be deleted!");
			RequestDispatcher rd = req.getRequestDispatcher("/scenarios");
			rd.forward(req, resp);
			HttpSession session = req.getSession();
			if (session.getAttribute("scenarioId") != null) {
				session.removeAttribute("scenarioId");
			}
		}
		else {
			resp.sendRedirect("/scenarios");
		}
	}

	/**
	 * This method deals with saving the actions performed and the elements provided.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void saveRedirect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("save redirect");
		HttpSession session = req.getSession();
		Scenario scenario = (Scenario) session.getAttribute("scenarioId");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean error = false;
		RequestDispatcher rd = req.getRequestDispatcher("/scenarios");
		if(!scenario.getScenarioName().equals(req.getParameter("scenarioName")))
		{
			List<Scenario> scenariosList = service.getScenariosByEmail(user.getEmail());
			Iterator<Scenario> iter = scenariosList.iterator();
			while(iter.hasNext()) 
				if((iter.next().getScenarioName().equals(req.getParameter("scenarioName")))) {
					req.setAttribute("response", "Scenario name already exists!");
					error = true;
					System.out.println("error true");
				}
		}
		if(!error) {
			System.out.println("same scenario name");
			scenario = service.updateScenario(scenario.getId(),req.getParameter("scenarioName"),
					req.getParameter("scenarioType"),
					req.getParameter("requirementType"),
					req.getParameter("priority"));

			if(scenario != null) {
				System.out.println("saved");
				resp.sendRedirect("/scenarios");
			}
			else {
				System.out.println("error update");
				req.setAttribute("response", "Update unsuccessful!");
				rd.forward(req, resp);
			}
		}
		else {
			rd.forward(req, resp);
		}
	}

	/**
	 * This method creates the Scenario type frame with the corresponding options to be selected.
	 * @param scenarioType the options provided
	 * @return line the option selected as a String
	 */
	private String buildScenarioType(String scenarioType) {
		String line = "";
		String impl = "<option>Implementing</option>";
		String buy = "<option>Leasing</option>";
		if(scenarioType == null) impl = "<option selected=\"selected\">Implementing</option>";
		else { 
			switch (scenarioType) {
			case "Implementing": impl = "<option selected=\"selected\">Implementing</option>";
			break;
			case "Leasing": buy = "<option selected=\"selected\">Leasing</option>";
			break;
			default: impl = "<option selected=\"selected\">Implementing</option>";
			break;
			}
		}
		line = impl + buy;
		return line;
	}

	/**
	 * This method creates the Requirement type frame with the corresponding options to be selected.
	 * @param reqType the options provided
	 * @return line the option selected as a String
	 */
	private String buildRequirementType(String reqType) {
		String line = "";
		String eff = "<option>Efficiency</option>";
		String use = "<option>Usability</option>";
		String dep = "<option>Dependability</option>";
		String sec = "<option>Security</option>";
		String env = "<option>Environmental</option>";
		String op = "<option>Operational</option>";
		String dev = "<option>Development</option>";
		String reg = "<option>Regulatory</option>";
		String leg = "<option>Legislative</option>";
		String et = "<option>Ethical</option>";
		if(reqType == null) eff = "<option selected=\"selected\">Efficiency</option>";
		else {
			switch(reqType) {
			case "Efficiency": eff = "<option selected=\"selected\">Efficiency</option>";
			break;
			case "Usability": use = "<option selected=\"selected\">Usability</option>";
			break;
			case "Dependability": dep = "<option selected=\"selected\">Dependability</option>";
			break;
			case "Security": sec = "<option selected=\"selected\">Security</option>";
			break;
			case "Environmental": env = "<option selected=\"selected\">Environmental</option>";
			break;
			case "Operational": op = "<option selected=\"selected\">Operational</option>";
			break;
			case "Development": dev = "<option selected=\"selected\">Development</option>";
			break;
			case "Regulatory": reg = "<option selected=\"selected\">Regulatory</option>";
			break;
			case "Legislative": leg = "<option selected=\"selected\">Legislative</option>";
			break;
			case "Ethical": et = "<option selected=\"selected\">Ethical</option>";
			break;
			default:  eff = "<option selected=\"selected\">Efficiency</option>";
			break;
			}
		}

		line = eff + use + dep + sec + env + op + dev + reg + leg + et;
		return line;
	}

	/**
	 * This method creates the Priority frame with the corresponding options to be selected.
	 * @param priority the options provided
	 * @return line the option selected as a String
	 */
	private String buildPriority (String priority) {
		String line = "";
		String high = "<option>High</option>";
		String med = "<option>Moderate</option>";
		String low = "<option>Low</option>";

		if(priority == null) high = "<option selected=\"selected\">High</option>";
		else {
			switch(priority) {
			case "High": high = "<option selected=\"selected\">High</option>";
			break;
			case "Low": low = "<option selected=\"selected\">Low</option>";
			break;
			case "Moderate": med = "<option selected=\"selected\">Moderate</option>";
			break;
			default: high = "<option selected=\"selected\">High</option>";
			break;
			}

		}
		line = high + med + low;
		return line;
	}

	/**
	 * This method deals with showing the details that were previously saved for a specific scenario.
	 * @param scenario the corresponding scenario
	 * @param req is the request object
	 */
	private void fillDetails(Scenario scenario, HttpServletRequest req) {
		req.setAttribute("scenarioNameValue", scenario.getScenarioName());
		req.setAttribute("scenarioType", buildScenarioType(scenario.getScenarioType()));
		req.setAttribute("requirementType", buildRequirementType(scenario.getRequirementType()));
		req.setAttribute("priority", buildPriority(scenario.getPriority()));
	}
}
