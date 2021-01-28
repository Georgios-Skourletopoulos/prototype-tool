package com.example.hibtdq;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.TDinBuying;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * NewBuyingServlet is a class that deals with showing the form and creating a Technical Debt (TD) for
 * Leasing a Cloud Software as a Service (SaaS) quantification.
 * @author Georgios Skourletopoulos
 * @version 5 August 2013
 */
@SuppressWarnings("serial")
public class NewBuyingServlet extends HttpServlet {

	DBService service = new DBService();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Leasing a Cloud Software as a Service task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		System.out.println("new buying");
		if(!checkJob(req, resp)) {
			String dispatch = null;
			dispatch = req.getParameter("dispatchAction");
			if(dispatch == null)
				dispatch = "new";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "new":
				newBuying(req,resp);
				break;
			case "calculate":
				if(req.getAttribute("response") == null)
					calculateBuying(req,resp);
				else {
					newBuying(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "submit": 
				if(req.getAttribute("response") == null)
					submitBuying(req,resp);
				else { 
					newBuying(req,resp);
					req.removeAttribute("response");
				}
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform TD for
	 * Leasing calculations.
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
	 * This method deals with submitting a new TD estimate in order to be added into the
	 * created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitBuying (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("/main");
		boolean error = false;

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkTDinBuyingForSubmission(user.getEmail(), req.getParameter("tdInBuyingName")) != null) {
			req.setAttribute("response", "Technical Debt estimation for Leasing name already exists!");
			error = true;
			System.out.println("error true");
		}
		TDinBuying tdInBuying = null;
		UserLogin usr = service.getUserByEmail(user.getEmail());	

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			tdInBuying = new TDinBuying(usr, req.getParameter("tdInBuyingName"),
					Integer.parseInt(req.getParameter("yearsOfROI")), 
					new BigInteger(req.getParameter("maxCapacity")),
					new BigInteger(req.getParameter("currentUsers")),
					new BigDecimal(req.getParameter("raiseInDemand")),
					new BigDecimal(req.getParameter("pricePerMonth")),
					new BigDecimal(req.getParameter("raiseInPricePerMonth")),
					new BigDecimal(req.getParameter("monthlyCostInCloud")),
					new BigDecimal(req.getParameter("raiseInMonthlyCostInCloud")),
					req.getParameter("confidence"),
					req.getParameter("scalabilityMarketFlexibility"), 
					req.getParameter("qualityOfService"),
					req.getParameter("riskIntoTD"), 
					req.getParameter("realOptions"),
					req.getParameter("justification"));
			if(service.persistTDinBuying(tdInBuying) != null) {
				resp.sendRedirect("/main");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/buyNew");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/buyNew");
			rd.forward(req, resp);
		} 
	}

	/**
	 * This method deals with redirecting in the TD for Leasing a Cloud Software as a Service form.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void newBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("buy.jsp");
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("evaluationsVisibility", "none");
		rd.forward(req, resp);
	}

	/**
	 * This method calculates the TD for Leasing according to the provided elements and the
	 * corresponding mathematical formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("new buying calculate");
		RequestDispatcher rd = req.getRequestDispatcher("buy.jsp");
		req.setAttribute("tdInBuyingNameValue", req.getParameter("tdInBuyingName"));
		req.setAttribute("yearsOfROIValue", req.getParameter("yearsOfROI"));
		req.setAttribute("maxCapacityValue", req.getParameter("maxCapacity"));
		req.setAttribute("currentUsersValue", req.getParameter("currentUsers"));
		req.setAttribute("raiseInDemandValue", req.getParameter("raiseInDemand"));
		req.setAttribute("pricePerMonthValue", req.getParameter("pricePerMonth"));
		req.setAttribute("raiseInPricePerMonthValue", req.getParameter("raiseInPricePerMonth"));
		req.setAttribute("monthlyCostInCloudValue", req.getParameter("monthlyCostInCloud"));
		req.setAttribute("raiseInMonthlyCostInCloudValue", req.getParameter("raiseInMonthlyCostInCloud"));

		List<Double> td = new ArrayList<Double>(); 

		/*
		 * Calculating the Technical Debt in order to be viewed with the corresponding results
		 * and the evaluation option to be selected.
		 */
		for(int i = 0; i < Integer.parseInt(req.getParameter("yearsOfROI")); i++) {
			td.add(12 * ( new Double(req.getParameter("maxCapacity")) - 
					Math.pow( new Double(1) + new Double(req.getParameter("raiseInDemand")) / 100, i) * 
					new Double(req.getParameter("currentUsers"))) * ( Math.pow(new Double(1) + 
							new Double(req.getParameter("raiseInPricePerMonth")) / (100 * 
									new Double (req.getParameter("yearsOfROI"))), i) * 
									new Double(req.getParameter("pricePerMonth")) - 
									Math.pow(new Double(1) + new Double(req.getParameter("raiseInMonthlyCostInCloud"))
									/ (100 * new Double (req.getParameter("yearsOfROI"))), i)
									* new Double(req.getParameter("monthlyCostInCloud"))));
		}
		req.setAttribute("TD", td);
		req.setAttribute("evaluationsVisibility", "block");
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("confEst", buildConfidence(null));
		req.setAttribute("scalabilityMarketFlexibility", buildFlexibility(null));
		req.setAttribute("qualityOfService", buildConfidence(null));
		req.setAttribute("riskIntoTD", buildConfidence(null));
		req.setAttribute("realOptions", buildRealOptionsValuation(null));
		rd.forward(req, resp);
	}

	/**
	 * This method creates the Service's scalability / Market flexibility frame with the corresponding
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
