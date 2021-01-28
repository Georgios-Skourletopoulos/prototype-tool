package com.example.hibtdq;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.CostForImplementing;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ImplementingServlet is a class that deals with the Implementing Software as a Service (SaaS) in the
 * Cloud operations, namely with showing, editing, calculating, deleting, sharing and submitting
 * a received cost for Implementing estimation. This communication is between Team Leaders/Developers/Architects.
 * @author Georgios Skourletopoulos
 * @version 5 August 2013
 */
@SuppressWarnings("serial")
public class ImplementingServlet extends HttpServlet {

	DBService service = new DBService ();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Implementing Software as a Service in the Cloud task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			String dispatch = null;
			dispatch = req.getParameter("dispatchActionImplement");
			if(dispatch == null)
				dispatch = "showImplement";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "calculate":				
				if(req.getAttribute("response") == null) {
					calculateImplement(req,resp);
					System.out.println("no response");
				}
				else {
					showImplement(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "showImplement":
				showImplement(req,resp);
				break;
			case "showSharedImplement": 
				showSharedCostForImplementing(req,resp);				
				break;
			case "submitShared":
				if(req.getAttribute("response") == null)
					submitCostForImplementing(req,resp); 
				else {
					showSharedCostForImplementing(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "shareImplement":
				shareImplement(req,resp); 
				break;
			case "delImplement":
				if(req.getAttribute("response") == null)
					deleteImplement(req,resp);
				break;
			case "delSharedImplement":
				if(req.getAttribute("response") == null)
					deleteSharedCostForImplementing(req,resp, "/main");
				break;
			case "save":
				if(req.getAttribute("response") == null) {
					saveImplement(req,resp);
				}
				else {
					showImplement(req,resp);
					req.removeAttribute("response");
				}
				break;
			default:
				resp.sendRedirect("/main");
				break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform
	 * cost for Implementing Software as a Service in the Cloud calculations.
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
			System.out.println("error");
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
			return true;
		}
		return false;
	}

	/**
	 * This method deals with deleting a cost for Implementing Software as a Service in the Cloud
	 * calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void deleteImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id = Long.parseLong(req.getParameter("idImplement"));
		CostForImplementing costForImplementing = service.deleteCostForImplementing(id);
		if(costForImplementing != null)
			resp.sendRedirect("/main");
		else {
			req.setAttribute("response", "The estimation could not be deleted!");
			RequestDispatcher rd = req.getRequestDispatcher("/main");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with saving a cost for Implementing Software as a Service in the Cloud
	 * calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void saveImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		CostForImplementing originalImplementing = (CostForImplementing) session.getAttribute("implement");
		RequestDispatcher rd = req.getRequestDispatcher("implement.jsp");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean check = true;

		/*
		 * Checks if the name of the estimation has changed and if yes it checks if the new one
		 * exists in the database.
		 */
		if(!originalImplementing.getName().trim().equals(req.getParameter("costName").trim())) {
			System.out.println("not same name");
			if(service.checkCostForImplementingForSubmission(user.getEmail(), req.getParameter("costName")) != null)
				check = false;
			else check = true;
		}

		/*
		 * If the name was not changed or the new name does not exist in the database, then
		 * proceed to the update.
		 */
		if(check) {
			CostForImplementing costForImplementing = service.updateCostForImplementing(originalImplementing.getId(), 
					req.getParameter("costName"), Integer.parseInt(req.getParameter("devWeight")), 
					Integer.parseInt(req.getParameter("configWeight")), 
					Integer.parseInt(req.getParameter("deployWeight")),
					Integer.parseInt(req.getParameter("licenseWeight")), 
					Integer.parseInt(req.getParameter("infraWeight")),
					new BigDecimal(req.getParameter("effort")), 
					new BigDecimal(req.getParameter("monthlySalary")),
					req.getParameter("confidence"), req.getParameter("productFlexibility"),
					req.getParameter("marketFlexibility"), req.getParameter("riskIntoTD"),
					req.getParameter("realOptions"), req.getParameter("justification"));
			if(costForImplementing != null) {
				req.removeAttribute("implement");
				resp.sendRedirect("/main");
			}
			else {
				req.setAttribute("response", "The estimation could not be updated!");
				rd = req.getRequestDispatcher("/implement");
				rd.forward(req, resp);
			}
		}
		else {
			req.setAttribute("response", "Cost estimation for Implementing name already exists!");
			rd = req.getRequestDispatcher("/implement");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with showing the details of an already created cost for Implementing
	 * Software as a Service calculation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("evaluationsVisibility", "none");
		HttpSession session = req.getSession();
		Long id;
		CostForImplementing costForImplementing = null;

		if (req.getParameter("idImplement") != null) {
			id = Long.parseLong(req.getParameter("idImplement"));
			costForImplementing = service.getCostForImplementingById(id);
			session.setAttribute("implement", costForImplementing);
		}
		else 
			costForImplementing = (CostForImplementing) session.getAttribute("implement");

		DecimalFormat df = new DecimalFormat("#.##");
		RequestDispatcher rd = req.getRequestDispatcher("implement.jsp");
		req.setAttribute("costNameValue", costForImplementing.getName());
		req.setAttribute("devWeightValue", costForImplementing.getWeightDevelopment());
		req.setAttribute("configWeightValue", costForImplementing.getWeightConfiguration());
		req.setAttribute("deplopyWeightValue", costForImplementing.getWeightDeployment());
		req.setAttribute("licenseWeightValue", costForImplementing.getWeightLicences());
		req.setAttribute("infraWeightValue", costForImplementing.getWeightInfrastructure());
		req.setAttribute("effortValue", df.format(costForImplementing.getEffortApplied()));
		req.setAttribute("monthlySalaryValue", df.format(costForImplementing.getAvgMonthlySalary()));
		rd.forward(req, resp);
	}

	/**
	 * This method deals with sharing a cost for Implementing estimate with other users.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws IOException
	 */
	private void shareImplement(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		Long id = Long.parseLong(req.getParameter("idImplement"));
		CostForImplementing costForImplementing = service.getCostForImplementingById(id);
		session.setAttribute("estimation", costForImplementing);
		session.setAttribute("type", "costForImplementing");
		resp.sendRedirect("/shareEstimate");
	}

	/**
	 * This method deals with viewing a received cost estimate for Implementing from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException 
	 */
	private void showSharedCostForImplementing(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id;
		HttpSession session = req.getSession();
		CostForImplementing costForImplementing = null;
		if (req.getParameter("idImplement") != null) {
			id = Long.parseLong(req.getParameter("idImplement"));
			costForImplementing = service.getCostForImplementingById(id);
			session.setAttribute("implement", costForImplementing);
		}
		else 
			costForImplementing = (CostForImplementing) session.getAttribute("implement");

		/*
		 * Calculating the cost in order to be included in the report with results.
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
		RequestDispatcher rd = req.getRequestDispatcher("implementView.jsp");
		rd.forward(req, resp);
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
				.parseLong(req.getParameter("idImplement")));

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

	/**
	 * This method deals with submitting a received cost for Implementing estimate in order to be
	 * added into the created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitCostForImplementing (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("/main");
		boolean error = false;

		HttpSession session = req.getSession();

		CostForImplementing costForImplementing = (CostForImplementing) session.getAttribute("implement");
		UserLogin usr = service.getUserByEmail(user.getEmail());

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkCostForImplementingForSubmission(user.getEmail(), req.getParameter("costName")) != null) {
			req.setAttribute("response", "Cost estimation for Implementing name already exists!");
			error = true;
			System.out.println("error true");
		}

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			costForImplementing = new CostForImplementing(usr, req.getParameter("costName"),
					costForImplementing.getWeightDevelopment(), costForImplementing.getWeightConfiguration(),
					costForImplementing.getWeightDeployment(), costForImplementing.getWeightLicences(),
					costForImplementing.getWeightInfrastructure(), costForImplementing.getEffortApplied(),
					costForImplementing.getAvgMonthlySalary(), costForImplementing.getConfidence(),
					costForImplementing.getProductFlexibility(), costForImplementing.getMarketFlexibility(),
					costForImplementing.getRiskOfFutureTD(), costForImplementing.getRealOptionsValuation(),
					costForImplementing.getJustification());
			if(service.persistImplementing(costForImplementing) != null) {
				session.removeAttribute("implement");
				resp.sendRedirect("/main");
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/implement");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/implement");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with viewing the cost estimate after submitting from the received ones
	 * according to the provided elements and the corresponding formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		CostForImplementing costForImplementing  = ((CostForImplementing) session.getAttribute("implement"));
		RequestDispatcher rd = req.getRequestDispatcher("implement.jsp");
		req.setAttribute("costNameValue", req.getParameter("costName"));
		req.setAttribute("devWeightValue", req.getParameter("devWeight"));
		req.setAttribute("configWeightValue", req.getParameter("configWeight"));
		req.setAttribute("deplopyWeightValue", req.getParameter("deployWeight"));
		req.setAttribute("licenseWeightValue", req.getParameter("licenseWeight"));
		req.setAttribute("infraWeightValue", req.getParameter("infraWeight"));
		req.setAttribute("effortValue", req.getParameter("effort"));
		req.setAttribute("monthlySalaryValue", req.getParameter("monthlySalary"));

		int checkIntegrity = Integer.parseInt(req.getParameter("devWeight")) + 
				Integer.parseInt(req.getParameter("configWeight")) + 
				Integer.parseInt(req.getParameter("deployWeight")) + 
				Integer.parseInt(req.getParameter("licenseWeight")) + 
				Integer.parseInt(req.getParameter("infraWeight"));

		/*
		 * Check if the sum of all weighted priority ratings is 100%.
		 */
		if(checkIntegrity != 100) {
			req.setAttribute("response", "The sum of all weighted priority ratings should be 100%. Please"
					+ " try again!");
			showImplement(req,resp);
		}
		else {
			req.setAttribute("evaluationsVisibility", "block");
			req.setAttribute("saveDisabled", "submit");
			req.setAttribute("submitDisabled", "hidden");
			req.setAttribute("submitSharedDisabled", "hidden");
			req.setAttribute("confEst", buildConfidence(costForImplementing.getConfidence()));
			req.setAttribute("productFlexibilityList", buildFlexibility(costForImplementing.getProductFlexibility()));
			req.setAttribute("marketFlexibilityList", buildFlexibility(costForImplementing.getMarketFlexibility()));
			req.setAttribute("riskIntoTDList", buildConfidence(costForImplementing.getRiskOfFutureTD()));
			req.setAttribute("realOptionsList", buildRealOptionsValuation(costForImplementing.getRealOptionsValuation()));
			req.setAttribute("justificationValue", costForImplementing.getJustification());
			fillCosts(req);
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with calculating the cost for each process as long as the total one
	 * after submitting from the received ones according to the provided elements and the
	 * corresponding mathematical formula.
	 * @param req is the request object
	 */
	private void fillCosts(HttpServletRequest req) {
		Double devCost = new Double(req.getParameter("devWeight")) / 100 * new Double(req.getParameter("effort"))
		* new Double(req.getParameter("monthlySalary"));
		req.setAttribute("developmentCostValue", fillVector(devCost));
		Double devConfig = new Double(req.getParameter("configWeight")) / 100 * new Double(req.getParameter("effort"))
		* new Double(req.getParameter("monthlySalary"));
		req.setAttribute("configurationCostValue", fillVector(devConfig));
		Double devDeploy = new Double(req.getParameter("deployWeight")) / 100 * new Double(req.getParameter("effort"))
		* new Double(req.getParameter("monthlySalary"));
		req.setAttribute("deploymentCostValue", fillVector(devDeploy));
		Double devLicence = new Double(req.getParameter("licenseWeight")) / 100 * new Double(req.getParameter("effort"))
		* new Double(req.getParameter("monthlySalary"));
		req.setAttribute("licensesCostValue", fillVector(devLicence));
		Double devInfra = new Double(req.getParameter("infraWeight")) / 100 * new Double(req.getParameter("effort"))
		* new Double(req.getParameter("monthlySalary"));
		req.setAttribute("infrastructureCostValue", fillVector(devInfra));
		Double devTotal = new Double(req.getParameter("effort")) * new Double(req.getParameter("monthlySalary"));
		req.setAttribute("totalCostValue", fillVector(devTotal));
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
	 * This method creates the Product and Market flexibility frames with the corresponding
	 * options to be selected.
	 * @param priority the priority of the options
	 */
	private String buildFlexibility (String priority) {
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
	 * This method creates the Real options valuation frame with the corresponding options to be
	 * selected.
	 * @param priority the priority of the options
	 */
	private String buildRealOptionsValuation (String priority) {
		String line = "";
		String none = "<option> </option>";
		String sw = "<option>Switch</option>";
		String abandon = "<option>Abandon</option>";
		String defer = "<option>Defer</option>";
		String expand = "<option>Expand</option>";
		String downsize = "<option>Downsize</option>";

		if(priority == null) none = "<option selected=\"selected\"> </option>";
		else {
			switch(priority) {
			case " ": none = "<option selected=\"selected\"> </option>";
			break;
			case "Low": sw = "<option selected=\"selected\">Switch</option>";
			break;
			case "Abandon": abandon = "<option selected=\"selected\">Abandon</option>";
			break;
			case "Defer": defer = "<option selected=\"selected\">Defer</option>";
			break;
			case "Expand": expand = "<option selected=\"selected\">Expand</option>";
			break;
			case "Downsize": downsize = "<option selected=\"selected\">Downsize</option>";
			break;
			default: none = "<option selected=\"selected\"> </option>";
			break;
			}
		}
		line = none + sw + abandon + defer + expand + downsize;
		return line;
	}

	/**
	 * This method creates the Confidence for estimation frame with the corresponding
	 * options to be selected.
	 * @param priority the priority of the options
	 */
	private String buildConfidence (String priority) {
		String line = "";
		String high = "<option>High</option>";
		String med = "<option>Medium</option>";
		String low = "<option>Low</option>";

		if(priority == null) high = "<option selected=\"selected\">High</option>";
		else {
			switch(priority) {
			case "High": high = "<option selected=\"selected\">High</option>";
			break;
			case "Low": low = "<option selected=\"selected\">Low</option>";
			break;
			case "Medium": med = "<option selected=\"selected\">Medium</option>";
			break;
			default: high = "<option selected=\"selected\">High</option>";
			break;
			}
		}
		line = high + med + low;
		return line;
	}
}
