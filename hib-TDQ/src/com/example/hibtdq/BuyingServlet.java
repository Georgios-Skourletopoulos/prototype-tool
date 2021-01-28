package com.example.hibtdq;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
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
 * BuyingServlet is a class that deals with the Leasing a Cloud Software as a Service operations,
 * namely with showing, editing, calculating, deleting, sharing and submitting a received Technical
 * Debt estimation. This communication is between Team Leaders/Developers/Architects.
 * @author Georgios Skourletopoulos
 * @version 5 August 2013
 */
@SuppressWarnings("serial")
public class BuyingServlet extends HttpServlet {

	DBService service = new DBService ();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the Leasing a Cloud Software as a Service task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			String dispatch = null;
			dispatch = req.getParameter("dispatchActionBuying");
			if(dispatch == null)
				dispatch = "showBuy";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "calculate":
				calculateBuying(req,resp);
				break;
			case "showBuy":
				showBuying(req,resp);
				break;
			case "showSharedBuy": 
				showSharedTDinBuying(req,resp);
				break;
			case "delSharedBuy":
				if(req.getAttribute("response") == null)
					deleteSharedTDinBuying(req,resp, "/main");
				break;
			case "submitShared":
				if(req.getAttribute("response") == null)
					submitTDinBuying(req,resp); 
				else {
					showSharedTDinBuying(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "shareBuy":
				shareBuying(req,resp); 
				break;
			case "delBuy":
				if(req.getAttribute("response") == null)
					deleteBuying(req,resp);
				break;
			case "save":
				if(req.getAttribute("response") == null)
					saveBuying(req,resp); 
				else {
					showBuying(req,resp);
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
	 * Technical Debt for Leasing calculations.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @return true if the user is a Project Manager redirecting him to the authentication page, otherwise false if he is a Team Leader/Developer/Architect
	 * @throws IOException
	 */
	private boolean checkJob(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();    //generates the service in order to access the user details according to the Google credentials
		User user = userService.getCurrentUser();    //gets the current login user
		UserLogin usr = service.getUserByEmail(user.getEmail());    //gets the user object from the database
		HttpSession session = req.getSession();    //initialises a session
		if(usr.getJobId().getName().trim().equals("Project Manager")) {
			System.out.println("error");
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
			return true;
		}
		return false;
	}

	/**
	 * This method deals with deleting a Technical Debt for Leasing calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void deleteBuying(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		Long id = Long.parseLong(req.getParameter("idBuy"));
		TDinBuying tdInBuying = service.deleteTDInBuying(id);
		if(tdInBuying != null)
			resp.sendRedirect("/main");    //if the deletion is successful redirects to the main page
		else {
			req.setAttribute("response", "The estimation could not be deleted!");
			RequestDispatcher rd = req.getRequestDispatcher("/main");    //if the deletion is unsuccessful redirects to the main page with the corresponding message
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with saving a Technical Debt for Leasing calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void saveBuying(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		HttpSession session = req.getSession();
		TDinBuying originalBuying = (TDinBuying) session.getAttribute("buy");    //retrieves the estimation object stored in the session
		RequestDispatcher rd = req.getRequestDispatcher("buy.jsp");    //given argument for the page that should be loaded

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean check = true;

		/*
		 * Checks if the name of the estimation has changed and if yes it checks if the new one
		 * exists in the database.
		 */
		if(!originalBuying.getName().trim().equals(req.getParameter("tdInBuyingName").trim())) {
			System.out.println("not same name");
			if(service.checkTDinBuyingForSubmission(user.getEmail(), req.getParameter("tdInBuyingName")) != null)
				check = false;
			else check = true;
		}

		/*
		 * If the name was not changed or the new name does not exist in the database, then
		 * proceed to the update.
		 */
		if(check) {
			TDinBuying tdInBuying = service.updateTDInBuying(originalBuying.getId(), 
					req.getParameter("tdInBuyingName"),
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
			if(tdInBuying != null) {
				req.removeAttribute("buy");
				resp.sendRedirect("/main");    //if the update was successful, redirects to the main page
			}
			else {
				req.setAttribute("response", "The estimation could not be updated!");
				rd = req.getRequestDispatcher("/buy");    //if the update was unsuccessful, remain to the page with the corresponding message
				rd.forward(req, resp);
			}
		}
		else {
			req.setAttribute("response", "Technical Debt estimation for Leasing name already exists!");
			rd = req.getRequestDispatcher("/buy");    //if the name exists in the database, remain to the page with the corresponding message
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with showing the details of an already created Technical Debt (TD)
	 * for Leasing calculation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("evaluationsVisibility", "none");
		HttpSession session = req.getSession();
		Long id;
		TDinBuying tdInBuying = null;
		if (req.getParameter("idBuy") != null) {
			id = Long.parseLong(req.getParameter("idBuy"));
			tdInBuying = service.getTDinBuyingById(id);
			session.setAttribute("buy", tdInBuying);
		}
		else
			tdInBuying = (TDinBuying) session.getAttribute("buy");	

		DecimalFormat df = new DecimalFormat("#.##");    //how many decimals should be included in the result
		RequestDispatcher rd = req.getRequestDispatcher("buy.jsp");    //given argument for the page that should be loaded
		req.setAttribute("tdInBuyingNameValue", tdInBuying.getName());    //puts the values of the operation that was performed
		req.setAttribute("yearsOfROIValue", tdInBuying.getRoi());
		req.setAttribute("maxCapacityValue", tdInBuying.getMaxCapacity());
		req.setAttribute("currentUsersValue", tdInBuying.getCurrentUsers());
		req.setAttribute("raiseInDemandValue", df.format(tdInBuying.getDemandRaise()));
		req.setAttribute("pricePerMonthValue", df.format(tdInBuying.getSubscriptionPrice()));
		req.setAttribute("raiseInPricePerMonthValue", df.format(tdInBuying.getRaiseSubscriptionPrice()));
		req.setAttribute("monthlyCostInCloudValue", df.format(tdInBuying.getCloudCost()));
		req.setAttribute("raiseInMonthlyCostInCloudValue", df.format(tdInBuying.getRaiseCloudCost()));
		rd.forward(req, resp);
	}

	/**
	 * This method deals with sharing a TD estimate with other users.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws IOException
	 */
	private void shareBuying(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		Long id = Long.parseLong(req.getParameter("idBuy"));    //each estimation holds an ID in the database and is send according to that ID to each user
		TDinBuying tdInBuying = service.getTDinBuyingById(id);
		session.setAttribute("estimation", tdInBuying);
		session.setAttribute("type", "tdInBuying");
		resp.sendRedirect("/shareEstimate");
	}

	/**
	 * This method deals with viewing a received TD estimate from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException 
	 */
	private void showSharedTDinBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Long id;
		TDinBuying tdInBuying = null;
		if (req.getParameter("idBuy") != null) {
			id = Long.parseLong(req.getParameter("idBuy"));
			tdInBuying = service.getTDinBuyingById(id);
			session.setAttribute("buy", tdInBuying);
		}
		else
			tdInBuying = (TDinBuying) session.getAttribute("buy");	

		List<Double> td = new ArrayList<Double>();    //numerical results stored in an ArrayList of Doubles

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
		req.setAttribute("submitSharedDisabled", "submit");    //setting attribute to activate the submit button
		RequestDispatcher rd = req.getRequestDispatcher("buyView.jsp");
		rd.forward(req, resp);
	}

	/**
	 * This method deals with deleting a received TD estimate.
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
				.parseLong(req.getParameter("idBuy")));

		/*
		 * If the estimate is not deleted, it cannot be unshared, otherwise it is unshared and
		 * redirects to the page specified by the target parameter.
		 */
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
	 * This method deals with submitting a received TD estimate in order to be added into the
	 * created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitTDinBuying (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("/main");
		boolean error = false;

		HttpSession session = req.getSession();

		TDinBuying tdInBuying = (TDinBuying) session.getAttribute("buy");
		UserLogin usr = service.getUserByEmail(user.getEmail());

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkTDinBuyingForSubmission(user.getEmail(), req.getParameter("tdInBuyingName")) != null) {
			req.setAttribute("response", "Technical Debt estimation for Leasing name already exists!");
			error = true;
			System.out.println("error true");
		}

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			tdInBuying = new TDinBuying(usr, req.getParameter("tdInBuyingName"),
					tdInBuying.getRoi(), tdInBuying.getMaxCapacity(), tdInBuying.getCurrentUsers(),
					tdInBuying.getDemandRaise(), tdInBuying.getSubscriptionPrice(), tdInBuying.getRaiseSubscriptionPrice(),
					tdInBuying.getCloudCost(), tdInBuying.getRaiseCloudCost(), tdInBuying.getConfidence(),
					tdInBuying.getServiceScalability(), tdInBuying.getQoS(), tdInBuying.getRiskOfFutureTD(),
					tdInBuying.getRealOptionsValuation(), tdInBuying.getJustification());
			if(service.persistTDinBuying(tdInBuying) != null) {
				session.removeAttribute("buy");
				resp.sendRedirect("/main");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/buy");
				rd.forward(req, resp);
			}
		}
		else 
			rd = req.getRequestDispatcher("/buy");
		rd.forward(req, resp);

	}

	/**
	 * This method deals with viewing the TD after submitting from the received ones according to
	 * the provided elements and the corresponding formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Buying servlet calculate");
		HttpSession session = req.getSession();
		TDinBuying tdInBuying = (TDinBuying) session.getAttribute("buy");
		RequestDispatcher rd = req.getRequestDispatcher("buy.jsp");
		req.setAttribute("tdInBuyingNameValue", req.getParameter("tdInBuyingName"));    //puts the values of the operation that was performed
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
		 * and evaluations performed.
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
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("confEst", buildConfidence(tdInBuying.getConfidence()));
		req.setAttribute("scalabilityMarketFlexibility", buildFlexibility(tdInBuying.getServiceScalability()));
		req.setAttribute("qualityOfService", buildConfidence(tdInBuying.getQoS()));
		req.setAttribute("riskIntoTD", buildConfidence(tdInBuying.getRiskOfFutureTD()));
		req.setAttribute("realOptions", buildRealOptionsValuation(tdInBuying.getRealOptionsValuation()));
		req.setAttribute("justificationValue", tdInBuying.getJustification());
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
