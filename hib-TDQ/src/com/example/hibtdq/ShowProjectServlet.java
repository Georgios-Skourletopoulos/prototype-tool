package com.example.hibtdq;

import java.io.IOException;
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
 * ShowProjectServlet is a class that deals with the Project operations, namely with showing all
 * types of project and deleting the received ones.
 * @author Georgios Skourletopoulos
 * @version 4 August 2013
 */
@SuppressWarnings("serial")
public class ShowProjectServlet extends HttpServlet {

	DBService service = new DBService();

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Project task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			System.out.println("Show Project get");
			Long id = Long.parseLong(req.getParameter("setId"));
			Project project = service.getProjectById(id);
			HttpSession session = req.getSession();
			session.setAttribute("projectId", project);
			String dispatch = req.getParameter("dispatchAction");
			if(dispatch == null)
				dispatch = "showProj";
			RequestDispatcher rd = req.getRequestDispatcher("projDetails.jsp");
			req.setAttribute("project", project);
			List<Scenario> sharedScenarios;
			switch(dispatch.trim()) {
			case "showProjReceived":
				req.setAttribute("visibility", "none");
				req.setAttribute("submitButton", "block");
				sharedScenarios = service.getScenarioByProject(project);
				req.setAttribute("scenarioList", sharedScenarios);
				rd.forward(req, resp);
				break;
			case "showProj":
				req.setAttribute("visibility", "block");
				req.setAttribute("submitButton", "none");
				rd.forward(req, resp);
				break;
			case "showProjReceivedDev":
				rd = req.getRequestDispatcher("projDetailsDev.jsp");
				sharedScenarios = service.getScenarioByProject(project);
				req.setAttribute("scenarioList", sharedScenarios);
				rd.forward(req, resp);
				break;
			case "delProjReceived":
				delReceivedProject(req,resp);
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
	 * This method deals with deleting a received project.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void delReceivedProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Project project = service.getProjectById(Long.parseLong(req.getParameter("setId")));
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(!service.deleteSharedUser(user.getEmail(), project.getId(), project.getUserId().getEmail())) {
			req.setAttribute("response", project.getProjectName()+ " could not be unshared!");
			RequestDispatcher rd = req.getRequestDispatcher("/main");
			rd.forward(req, resp);
		}
		else {
			resp.sendRedirect("/main");
		}
	}
}	
