package com.example.hibtdq;

import java.io.IOException;
import java.math.BigDecimal;

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
 * NewImplementingServlet is a class that deals with showing the form and creating a Total Cost for
 * Implementing Software as a Service (SaaS) in the Cloud quantification.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@SuppressWarnings("serial")
public class NewImplementingServlet extends HttpServlet {

	DBService service = new DBService();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the total cost for Implementing Software as a Service in the
	 * Cloud task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			System.out.println("New Implement Get");
			String dispatch = null;
			dispatch = req.getParameter("dispatchAction");
			if(dispatch == null)
				dispatch = "new";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "new":
				newImplement(req,resp);
				break;
			case "calculate":
				if(req.getAttribute("response") == null)
					calculateImplement(req,resp);
				else {
					newImplement(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "submit": 
				if(req.getAttribute("response") == null)
					submitImplement(req,resp);
				else {
					newImplement(req,resp);
					req.removeAttribute("response"); 
				}
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform cost for
	 * Implementing calculations.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @return true if the user is a Project Manager redirecting him to the authentication page, otherwise false if he is a Team Leader/Developer/Architect.
	 * @throws IOException
	 */
	private boolean checkJob(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();    //generates the service in order to access the user details according to the Google credentials
		User user = userService.getCurrentUser();    //gets the current login user
		UserLogin usr = service.getUserByEmail(user.getEmail());    //gets the user object from the database
		HttpSession session = req.getSession();    //initialises a session
		if(usr.getJobId().getName().trim().equals("Project Manager")) {
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
			return true;
		}
		return false;
	}

	/**
	 * This method deals with submitting a new cost for Implementing estimate in order to be added
	 * into the created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitImplement (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("/main");
		boolean error = false;

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkCostForImplementingForSubmission(user.getEmail(), req.getParameter("costName")) != null) {
			req.setAttribute("response", "Cost estimation for Implementing name already exists!");
			error = true;
			System.out.println("error true");
		}
		CostForImplementing costForImplementing = null;
		UserLogin usr = service.getUserByEmail(user.getEmail());	

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			costForImplementing = new CostForImplementing(usr, req.getParameter("costName"),
					Integer.parseInt(req.getParameter("devWeight")), 
					Integer.parseInt(req.getParameter("configWeight")),
					Integer.parseInt(req.getParameter("deployWeight")), 
					Integer.parseInt(req.getParameter("licenseWeight")),
					Integer.parseInt(req.getParameter("infraWeight")), 
					new BigDecimal(req.getParameter("effort")),
					new BigDecimal(req.getParameter("monthlySalary")), 
					req.getParameter("confidence"),
					req.getParameter("productFlexibility"), req.getParameter("marketFlexibility"),
					req.getParameter("riskIntoTD"), req.getParameter("realOptions"),
					req.getParameter("justification"));
			if(service.persistImplementing(costForImplementing) != null) {
				resp.sendRedirect("/main");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/implementNew");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/implementNew");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with redirecting in the cost estimation for Implementing Software as a
	 * Service in the Cloud form.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void newImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("implement.jsp");
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("evaluationsVisibility", "none");
		rd.forward(req, resp);
	}

	/**
	 * This method calculates the cost for Implementing according to the provided elements and the
	 * corresponding mathematical formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateImplement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			req.setAttribute("evaluationsVisibility", "none");
			newImplement(req,resp);
		}
		else {
			req.setAttribute("evaluationsVisibility", "block");
			req.setAttribute("saveDisabled", "hidden");
			req.setAttribute("submitDisabled", "submit");
			req.setAttribute("submitSharedDisabled", "hidden");
			req.setAttribute("confEst", buildConfidence(null));
			req.setAttribute("productFlexibilityList", buildFlexibility(null));
			req.setAttribute("marketFlexibilityList", buildFlexibility(null));
			req.setAttribute("riskIntoTDList", buildConfidence(null));
			req.setAttribute("realOptionsList", buildRealOptionsValuation(null));
			fillCosts(req);
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with calculating the cost for each process as long as the total one
	 * after creating a new cost for Implementing estimation according to the provided elements
	 * and the corresponding mathematical formula.
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
	 * This method creates the Confidence for estimation frame with the corresponding options to
	 * be selected.
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
