package com.example.hibtdq;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.Project;
import com.example.model.Scenario;
import com.example.model.TDinBuying;
import com.example.usecases.CalculateCocomo;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ReportsServlet is a class that deals with building the attributes for the reports the users will
 * view. This communication is between Project Managers - Team Leaders/Developers/Architects and
 * vice-versa.
 * @author Georgios Skourletopoulos
 * @version 9 August 2013
 */
@SuppressWarnings("serial")
public class ReportsServlet extends HttpServlet {

	DBService service = new DBService();

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the reporting task (received projects and estimates).
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String dispatch = null;
		dispatch = req.getParameter("dispatchActionEstimate");

		RequestDispatcher rd;
		switch(dispatch) {
		case "showProjReceivedDev":
			showSharedProject(req,resp);
			break;
		case "delProjReceived":
			delReceivedProject(req,resp);
			break;
		case "showSharedBuyPM": 
			showSharedTDinBuying(req,resp);
			rd = req.getRequestDispatcher("buyViewPM.jsp");
			rd.forward(req, resp);
			break;
		case "delReceivedBuy":
			if(req.getAttribute("response") == null)
				deleteSharedTDinBuying(req,resp, "/mainPM");
			break;
		case "showSharedCocomoPM": 
			showSharedCocomo(req,resp);
			rd = req.getRequestDispatcher("cocomoViewPM.jsp");
			rd.forward(req, resp);
			break;
		case "delReceivedCocomo":
			if(req.getAttribute("response") == null)
				deleteSharedCocomo(req,resp, "/mainPM");
			break;
		case "showSharedImplementPM": 
			showSharedCostForImplementing(req,resp);
			rd = req.getRequestDispatcher("implementViewPM.jsp");
			rd.forward(req, resp);
			break;
		case "delReceivedImplement":
			if(req.getAttribute("response") == null)
				deleteSharedCostForImplementing(req,resp, "/mainPM");
			break;
		default: break;
		}
	}

	/**
	 * This method deals with viewing a received project with its scenarios included from
	 * another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showSharedProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id = Long.parseLong(req.getParameter("idEstimate"));
		Project project = service.getProjectById(id);    //each project holds an ID in the database and is send according to that ID to each user
		req.setAttribute("project", project);
		List<Scenario> sharedScenarios;
		RequestDispatcher rd = req.getRequestDispatcher("projDetailsDev.jsp");
		sharedScenarios = service.getScenarioByProject(project);
		req.setAttribute("scenarioList", sharedScenarios);
		rd.forward(req, resp);
	}

	/**
	 * This method deals with deleting a received project (the scenarios included on that project
	 * are deleted simultaneously).
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void delReceivedProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Project project = service.getProjectById(Long.parseLong(req.getParameter("idEstimate")));
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

	/**
	 * This method deals with viewing a received TD for Leasing estimate from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	private void showSharedTDinBuying(HttpServletRequest req, HttpServletResponse resp) {
		Long id;
		TDinBuying tdInBuying = null;
		id = Long.parseLong(req.getParameter("idEstimate"));
		tdInBuying = service.getTDinBuyingById(id);
		List<Double> td = new ArrayList<Double>(); 

		/*
		 * Calculating the Technical Debt in order to be included in the report with results.
		 */
		for(int i = 0; i < tdInBuying.getRoi(); i++) {
			td.add(12 * ( new Double(tdInBuying.getMaxCapacity().toString()) - 
					Math.pow( new Double(1) + new Double(tdInBuying.getDemandRaise().toPlainString()) / 100, i) * 
					new Double(tdInBuying.getCurrentUsers().toString())) * ( Math.pow(new Double(1) + 
							new Double(tdInBuying.getRaiseSubscriptionPrice().toPlainString()) / (100 * 
									new Double (tdInBuying.getRoi())), i) * 
									new Double(tdInBuying.getSubscriptionPrice().toPlainString()) - 
									Math.pow(new Double(1) + new Double(tdInBuying.getRaiseCloudCost().toPlainString())
									/ (100 * new Double (tdInBuying.getRoi())), i)
									* new Double(tdInBuying.getCloudCost().toPlainString())));
		}
		req.setAttribute("TD", td);
		req.setAttribute("TDinBuyingShared", tdInBuying);
		req.setAttribute("submitSharedDisabled", "submit");
	}

	/**
	 * This method deals with deleting a received TD for Leasing estimate.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @param target is the servlet in which it is going to redirect to
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedTDinBuying(HttpServletRequest req, HttpServletResponse resp, String target) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		TDinBuying tdInBuying = service.getTDinBuyingById(Long
				.parseLong(req.getParameter("idEstimate")));
		if(!service.deleteSharedBuyingUser(user.getEmail(),
				tdInBuying.getId(), tdInBuying.getUserId().getEmail())) {
			req.setAttribute("response", tdInBuying.getName() + " could not be unshared!");
			RequestDispatcher rd = req.getRequestDispatcher(target);
			rd.forward(req, resp);
		}
		else {
			resp.sendRedirect(target);
		}
	}

	/**
	 * This method deals with viewing a received COCOMO estimate from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	private void showSharedCocomo(HttpServletRequest req, HttpServletResponse resp) {
		Long id;
		Cocomo cocomo = null;
		id = Long.parseLong(req.getParameter("idEstimate"));
		cocomo = service.getCocomoById(id);

		Double [] cocomoCalc;    //numerical results stored in an array of Doubles
		DecimalFormat df = new DecimalFormat("#.00");

		/*
		 * Calculating COCOMO in order to be included in the report with results according to the
		 * software project.
		 */
		switch(cocomo.getDevelopmentMode()) {
		case "Organic":
			cocomoCalc = CalculateCocomo.organic(cocomo.getProductSize());
			req.setAttribute("optEffort", df.format(0.9 *cocomoCalc[0]));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", df.format(1.1 * cocomoCalc[0]));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		case "Semi-detached":
			cocomoCalc = CalculateCocomo.semidetached(cocomo.getProductSize());
			req.setAttribute("optEffort", df.format(0.9 *cocomoCalc[0]));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", df.format(1.1 * cocomoCalc[0]));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		case "Embedded":
			cocomoCalc = CalculateCocomo.embedded(cocomo.getProductSize());
			req.setAttribute("optEffort", df.format(0.9 *cocomoCalc[0]));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", df.format(1.1 * cocomoCalc[0]));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		default:
			break;
		}
		req.setAttribute("cocomoShared", cocomo);
		req.setAttribute("submitSharedDisabled", "submit");
	}

	/**
	 * This method deals with deleting a received COCOMO estimate.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @param target is the servlet in which it is going to redirect to
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedCocomo(HttpServletRequest req, HttpServletResponse resp, String target) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Cocomo cocomo = service.getCocomoById(Long.parseLong(req.getParameter("idEstimate")));

		/*
		 * If the estimate is not deleted, it cannot be unshared, otherwise it is unshared and
		 * redirects to the page specified by the target parameter.
		 */
		if(!service.deleteSharedCocomoUser(user.getEmail(), cocomo.getId(), cocomo.getUserId().getEmail())) {
			req.setAttribute("response", cocomo.getName() + " could not be unshared!");
			RequestDispatcher rd = req.getRequestDispatcher(target);
			rd.forward(req, resp);
		}
		else {
			resp.sendRedirect(target);
		}
	}

	/**
	 * This method deals with viewing a received cost estimate for Implementing from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 */
	private void showSharedCostForImplementing(HttpServletRequest req, HttpServletResponse resp) {
		Long id;
		CostForImplementing costForImplementing = null;
		id = Long.parseLong(req.getParameter("idEstimate"));
		costForImplementing = service.getCostForImplementingById(id);

		/*
		 * Calculating the cost for each process and the total one in order to be included in the
		 * report with results.
		 */
		Double devCost = new Double(costForImplementing.getWeightDevelopment()) / 100 * 
				new Double(costForImplementing.getEffortApplied().toPlainString())
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("developmentCost", fillVector(devCost));
		Double devConfig = new Double(costForImplementing.getWeightConfiguration()) / 100 * 
				new Double(costForImplementing.getEffortApplied().toPlainString())
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("configurationCost", fillVector(devConfig));
		Double devDeploy = new Double(costForImplementing.getWeightDeployment()) / 100 * 
				new Double(costForImplementing.getEffortApplied().toPlainString())
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("deploymentCost", fillVector(devDeploy));
		Double devLicence = new Double(costForImplementing.getWeightLicences()) / 100 * 
				new Double(costForImplementing.getEffortApplied().toPlainString())
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("licensesCost", fillVector(devLicence));
		Double devInfra = new Double(costForImplementing.getWeightInfrastructure()) / 100 * 
				new Double(costForImplementing.getEffortApplied().toPlainString())
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("infrastructureCost", fillVector(devInfra));
		Double devTotal = new Double(costForImplementing.getEffortApplied().toPlainString()) 
		* new Double(costForImplementing.getAvgMonthlySalary().toPlainString());
		req.setAttribute("totalCost", fillVector(devTotal));
		req.setAttribute("costForImplementingShared", costForImplementing);
		req.setAttribute("submitSharedDisabled", "submit");
	}

	/**
	 * This method deals with calculating the optimistic, most likely and pessimistic cost for each
	 * process and for the total simultaneously.
	 * @param cost is the most likely cost
	 * @return vector is the array of Doubles containing the three calculated costs
	 */
	private Double[] fillVector (Double cost) {
		Double [] vector = new Double[3];
		vector[0] = 0.9 * cost;
		vector[1] = cost;
		vector[2] = 1.1 * cost;
		return vector;
	}

	/**
	 * This method deals with deleting a received cost for Implementing estimate.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @param target is the servlet in which it is going to redirect to
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedCostForImplementing(HttpServletRequest req, HttpServletResponse resp, String target) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		CostForImplementing costForImplementing = service.getCostForImplementingById(Long
				.parseLong(req.getParameter("idEstimate")));

		/*
		 * If the estimate is not deleted, it cannot be unshared, otherwise it is unshared and
		 * redirects to the page specified by the target parameter.
		 */
		if(!service.deleteSharedImplementingUser(user.getEmail(),
				costForImplementing.getId(), costForImplementing.getUserId().getEmail())) {
			req.setAttribute("response", costForImplementing.getName() + " could not be unshared!");
			RequestDispatcher rd = req.getRequestDispatcher(target);
			rd.forward(req, resp);
		}
		else {
			resp.sendRedirect(target);
		}
	}
}
