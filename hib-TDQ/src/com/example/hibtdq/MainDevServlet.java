package com.example.hibtdq;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.SharedBuying;
import com.example.model.SharedCocomo;
import com.example.model.SharedImplementing;
import com.example.model.SharedProject;
import com.example.model.TDinBuying;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * MainDevServlet is the servlet that deals with managing the Team Leader/Developer/Architect main
 * page.
 * @author Georgios Skourletopoulos
 * @version 2 August 2013
 */
@SuppressWarnings("serial")
public class MainDevServlet extends HttpServlet {

	DBService service = new DBService();

	/**
	 * This method deals with sending data from the web client according to a specific action
	 * relating to the Team Leader/Developer/Architect profile working area.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req,resp)) {
			HttpSession session = req.getSession();
			session.removeAttribute("userJob");
			session.removeAttribute("targetJobPosition");
			System.out.println("Main get");
			fillUserDetails(req);
			RequestDispatcher rd;
			fillProjectsCreated(req);
			rd = req.getRequestDispatcher("mainDev.jsp");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform
	 * any kind of estimations.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @return true if the user is a Project Manager redirecting him to the authentication page, otherwise false if he is a Team Leader/Developer/Architect
	 * @throws IOException
	 */
	private boolean checkJob(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserLogin usr = service.getUserByEmail(user.getEmail());
		HttpSession session = req.getSession();
		if(usr.getJobId().getName().trim().equals("Project Manager")) {
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
		}
		return false;
	}

	/**
	 * This method sets the profile details shown in the working area (job position, company).
	 * @param req is the request object
	 */
	private void fillUserDetails(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserLogin usr = service.getUserByEmail(user.getEmail());
		req.setAttribute("jobPos", usr.getJobId().getName());
		req.setAttribute("company", usr.getCompanyName());
	}

	/**
	 * This method sets the attributes for the estimate (created, shared, received) and project (received)
	 * lists connected to the user.
	 * @param req is the request object
	 */
	private void fillProjectsCreated(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		List<Cocomo> cocomoCreated;
		cocomoCreated = service.getCocomoCreatedByEmail(user.getEmail());

		/*
		 * If the estimate and project lists are empty, the corresponding messages are displayed.
		 */
		if(cocomoCreated.size() == 0)
			req.setAttribute("noCocomoCreated", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("cocomoCreated", cocomoCreated);
		List<Cocomo> cocomoShared = service.getCocomoSharedByEmail(user.getEmail());

		if(cocomoShared.size() == 0)
			req.setAttribute("noCocomoShared", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("cocomoShared", cocomoShared);
		List<SharedCocomo> receivedCocomoList;
		receivedCocomoList = service.getReceivedSharedCocomoByUser(user.getEmail());
		System.out.println(receivedCocomoList.size());

		if(receivedCocomoList.size() == 0)
			req.setAttribute("noCocomoReceived", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("cocomoReceived", receivedCocomoList);
		List<CostForImplementing> implementCreated;
		implementCreated = service.getCostForImplementingCreatedByEmail(user.getEmail());

		if(implementCreated.size() == 0)
			req.setAttribute("noImplementCreated", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("implementCreated", implementCreated);
		List<CostForImplementing> implementShared = service.getCostForImplementingSharedByEmail(user.getEmail());

		if(implementShared.size() == 0)
			req.setAttribute("noImplementShared", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("implementShared", implementShared);
		List<SharedImplementing> receivedImplement;
		receivedImplement = service.getReceivedSharedCostForImplementingByUser(user.getEmail());

		if(receivedImplement.size() == 0)
			req.setAttribute("noImplementReceived", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("implementReceived", receivedImplement);
		List<TDinBuying> buyingCreated;
		buyingCreated = service.getTDinBuyingCreatedByEmail(user.getEmail());

		if(buyingCreated.size() == 0)
			req.setAttribute("noBuyingCreated", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("buyingCreated", buyingCreated);
		List<TDinBuying> buyingShared = service.getTDinBuyingSharedByEmail(user.getEmail());

		if(buyingShared.size() == 0)
			req.setAttribute("noBuyingShared", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("buyingShared", buyingShared);
		List<SharedBuying> receivedBuying;
		receivedBuying = service.getReceivedSharedBuyingByUser(user.getEmail());

		if(receivedBuying.size() == 0)
			req.setAttribute("noBuyingReceived", "<h3 class=\"warning\">No estimates available.</h3>");
		req.setAttribute("buyingReceived", receivedBuying);
		List<SharedProject> receivedProjectList;
		receivedProjectList = service.getReceivedSharedProjectsByUser(user.getEmail());
		System.out.println(receivedProjectList.size());

		if(receivedProjectList.size() == 0)
			req.setAttribute("noProjReceived", "<h3 class=\"warning\">No projects available.</h3>");
		req.setAttribute("receivedProjectsList", receivedProjectList);
	}
}
