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

import com.example.model.Cocomo;
import com.example.model.UserLogin;
import com.example.usecases.CalculateCocomo;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * CocomoServlet is a class that deals with the COCOMO operations,
 * namely with showing, editing, calculating, deleting, sharing and submitting a received COCOMO
 * estimation. This communication is between Team Leaders/Developers/Architects.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@SuppressWarnings("serial")
public class CocomoServlet extends HttpServlet {

	DBService service = new DBService ();  //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the COCOMO task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			String dispatch = null;
			dispatch = req.getParameter("dispatchActionCocomo");
			if(dispatch == null)
				dispatch = "showCocomo";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "calculate":
				calculateCocomo(req,resp);
				break;
			case "showCocomo":
				showCocomo(req,resp);
				break;
			case "showSharedCocomo": 
				showSharedCocomo(req,resp);				
				break;
			case "submitShared":
				if(req.getAttribute("response") == null)
					submitCocomo(req,resp); 
				else {
					showSharedCocomo(req,resp);
					req.removeAttribute("response");
				}
				break;
			case "shareCocomo":
				shareCocomo(req,resp);
				break;
			case "delCocomo":
				if(req.getAttribute("response") == null)
					deleteCocomo(req,resp);
				break;
			case "delSharedCocomo":
				if(req.getAttribute("response") == null)
					deleteSharedCocomo(req,resp, "/main");
				break;
			case "save":
				if(req.getAttribute("response") == null)
					saveCocomo(req,resp); 
				else {
					showCocomo(req,resp);
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
	 * This method checks the correctness of the job position in order the user to perform COCOMO
	 * calculations.
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
			return true;
		}
		return false;
	}

	/**
	 * This method deals with deleting a COCOMO calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void deleteCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long id = Long.parseLong(req.getParameter("idCocomo"));
		Cocomo cocomo = service.deleteCocomo(id);
		if(cocomo != null)
			resp.sendRedirect("/main");    //if the deletion is successful redirects to the main page
		else {
			req.setAttribute("response", "The estimation could not be deleted!");
			RequestDispatcher rd = req.getRequestDispatcher("/main");    //if the deletion is unsuccessful redirects to the main page with the corresponding message
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with saving a COCOMO calculation.
	 * @param req is the request object
	 * @param resp is the response for that request
	 * @throws IOException
	 * @throws ServletException when there is an error inside the servlet
	 */
	private void saveCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Cocomo originalCocomo = (Cocomo) session.getAttribute("cocomo");
		RequestDispatcher rd = req.getRequestDispatcher("cocomo.jsp");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean check = true;

		/*
		 * Checks if the name of the estimation has changed and if yes it checks if the new one
		 * exists in the database.
		 */
		if(!originalCocomo.getName().trim().equals(req.getParameter("cocomoName").trim())) {
			System.out.println("not same name");
			if(service.checkCocomoForSubmission(user.getEmail(), req.getParameter("cocomoName")) != null)
				check = false;
			else check = true;
		}

		/*
		 * If the name was not changed or the new name does not exist in the database, then
		 * proceed to the update.
		 */
		if(check) {
			System.out.println("check true");
			Cocomo cocomo = service.updateCocomo(originalCocomo.getId(), req.getParameter("cocomoName"), 
					req.getParameter("softDevMode"), new BigDecimal(req.getParameter("kloc")), 
					req.getParameter("confidence"), req.getParameter("justification"));
			if(cocomo != null) {
				req.removeAttribute("cocomo");
				resp.sendRedirect("/main");    //if the update was successful, redirects to the main page
			}
			else {
				req.setAttribute("response", "The estimation could not be updated!");
				rd = req.getRequestDispatcher("/cocomo");    //if the update was unsuccessful, remain to the page with the corresponding message
				rd.forward(req, resp);
			}
		}
		else {
			System.out.println("check false");
			req.setAttribute("response", "COCOMO estimation name already exists!");
			rd = req.getRequestDispatcher("/cocomo");    //if the name exists in the database, remain to the page with the corresponding message
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with showing the details of an already created COCOMO calculation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void showCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("display", "none");
		HttpSession session = req.getSession();
		Long id;
		Cocomo cocomo = null;
		if(req.getParameter("idCocomo") != null) {
			id = Long.parseLong(req.getParameter("idCocomo"));
			cocomo = service.getCocomoById(id);
			session.setAttribute("cocomo", cocomo);
		}
		else {
			cocomo = (Cocomo) session.getAttribute("cocomo");
		}
		DecimalFormat df = new DecimalFormat("#.##");    //how many decimals should be included in the result
		RequestDispatcher rd = req.getRequestDispatcher("cocomo.jsp");    //given argument for the page that should be loaded
		req.setAttribute("cocomoNameValue", cocomo.getName());    //puts the values of the operation that was performed
		req.setAttribute("softDevModeList", buildSoftDevMode(cocomo.getDevelopmentMode()));
		req.setAttribute("klocValue", df.format(cocomo.getProductSize()));
		rd.forward(req, resp);
	}

	/**
	 * This method deals with sharing a COCOMO estimate with other users.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws IOException
	 */
	private void shareCocomo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		Long id = Long.parseLong(req.getParameter("idCocomo"));    //each estimation holds an ID in the database and is send according to that ID to each user
		Cocomo cocomo = service.getCocomoById(id);
		session.setAttribute("estimation", cocomo);
		session.setAttribute("type", "cocomo");
		resp.sendRedirect("/shareEstimate");
	}

	/**
	 * This method deals with viewing a received COCOMO estimate from another user. 
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException 
	 */
	private void showSharedCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Long id;
		Cocomo cocomo = null;
		if(req.getParameter("idCocomo") != null) {
			id = Long.parseLong(req.getParameter("idCocomo"));
			cocomo = service.getCocomoById(id);
			session.setAttribute("cocomo", cocomo);
		}
		else 
			cocomo = (Cocomo) session.getAttribute("cocomo");

		Double [] cocomoCalc;    //numerical results stored in an array of Doubles
		DecimalFormat df = new DecimalFormat("#.##");

		/*
		 * Calculating COCOMO in order to be included in the report with results according to
		 * the software project.
		 */
		switch(cocomo.getDevelopmentMode()) {
		case "Organic":
			cocomoCalc = CalculateCocomo.organic(cocomo.getProductSize());
			req.setAttribute("optEffort", (df.format(0.9 * cocomoCalc[0])));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", (df.format(1.1 * cocomoCalc[0])));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		case "Semi-detached":
			cocomoCalc = CalculateCocomo.semidetached(cocomo.getProductSize());
			req.setAttribute("optEffort", (df.format(0.9 * cocomoCalc[0])));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", (df.format(1.1 * cocomoCalc[0])));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		case "Embedded":
			cocomoCalc = CalculateCocomo.embedded(cocomo.getProductSize());
			req.setAttribute("optEffort", (df.format(0.9 * cocomoCalc[0])));
			req.setAttribute("effort", df.format(cocomoCalc[0]));
			req.setAttribute("pesEffort", (df.format(1.1 * cocomoCalc[0])));
			req.setAttribute("development", df.format(cocomoCalc[1]));
			req.setAttribute("people", df.format(cocomoCalc[2]));
			break;
		default:
			break;
		}
		req.setAttribute("cocomoShared", cocomo);
		req.setAttribute("submitSharedDisabled", "submit");    //setting attribute to activate the submit button
		RequestDispatcher rd = req.getRequestDispatcher("cocomoView.jsp");
		rd.forward(req, resp);
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
		Cocomo cocomo = service.getCocomoById(Long.parseLong(req.getParameter("idCocomo")));

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
	 * This method deals with submitting a received COCOMO estimate in order to be added into the
	 * created ones.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void submitCocomo (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Submit");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("/main");
		boolean error = false;

		HttpSession session = req.getSession();

		Cocomo cocomo = (Cocomo) session.getAttribute("cocomo");
		UserLogin usr = service.getUserByEmail(user.getEmail());

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkCocomoForSubmission(user.getEmail(), req.getParameter("cocomoName")) != null) {
			req.setAttribute("response", "COCOMO estimation name already exists!");
			error = true;
			System.out.println("error true");
		}

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			cocomo = new Cocomo(usr, req.getParameter("cocomoName"),
					cocomo.getDevelopmentMode(), cocomo.getProductSize(), 
					cocomo.getConfidence(), cocomo.getJustification());
			if(service.persistCocomo(cocomo) != null) {
				session.removeAttribute("cocomo");
				resp.sendRedirect("/main");    //if submission is successful, redirects to the main page
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/cocomo");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/cocomo");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method deals with viewing COCOMO after submitting from the received ones according to
	 * the provided elements and the corresponding formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Cocomo cocomoProj = ((Cocomo) session.getAttribute("cocomo"));
		RequestDispatcher rd = req.getRequestDispatcher("cocomo.jsp");
		String mode = req.getParameter("softDevMode");
		req.setAttribute("display", "block");    //puts the values of the operation that was performed
		req.setAttribute("softDevModeList", buildSoftDevMode(mode));
		req.setAttribute("cocomoNameValue", req.getParameter("cocomoName"));
		req.setAttribute("klocValue", req.getParameter("kloc")); 
		req.setAttribute("saveDisabled", "submit");
		req.setAttribute("submitDisabled", "hidden");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("confEst", buildConfidence(cocomoProj.getConfidence()));
		req.setAttribute("justificationValue", cocomoProj.getJustification());
		Double [] cocomo;
		DecimalFormat df = new DecimalFormat("#.00");

		/*
		 * Calculating COCOMO in order to be viewed with the corresponding results
		 * and evaluations performed.
		 */
		switch(mode) {
		case "Organic":
			cocomo = CalculateCocomo.organic(new BigDecimal(req.getParameter("kloc")));
			req.setAttribute("optEffortValue", (df.format(0.9 * cocomo[0])));
			req.setAttribute("effortValue", df.format(cocomo[0]));
			req.setAttribute("pesEffortValue", (df.format(1.1 * cocomo[0])));
			req.setAttribute("developmentValue", df.format(cocomo[1]));
			req.setAttribute("peopleValue", df.format(cocomo[2]));
			break;
		case "Semi-detached":
			cocomo = CalculateCocomo.semidetached(new BigDecimal(req.getParameter("kloc")));
			req.setAttribute("optEffortValue", (df.format(0.9 * cocomo[0])));
			req.setAttribute("effortValue", df.format(cocomo[0]));
			req.setAttribute("pesEffortValue", (df.format(1.1 * cocomo[0])));
			req.setAttribute("developmentValue", df.format(cocomo[1]));
			req.setAttribute("peopleValue", df.format(cocomo[2]));
			break;
		case "Embedded":
			cocomo = CalculateCocomo.embedded(new BigDecimal(req.getParameter("kloc")));
			req.setAttribute("optEffortValue", (df.format(0.9 * cocomo[0])));
			req.setAttribute("effortValue", df.format(cocomo[0]));
			req.setAttribute("pesEffortValue", (df.format(1.1 * cocomo[0])));
			req.setAttribute("developmentValue", df.format(cocomo[1]));
			req.setAttribute("peopleValue", df.format(cocomo[2]));
			break;
		default:
			break;
		}
		rd.forward(req, resp);
	}

	/**
	 * This method creates the Software development mode frame with the corresponding
	 * options to be selected.
	 * @param priority the priority of the options
	 */
	private String buildSoftDevMode (String softDevMode) {
		String devMode = "";
		String organic = "<option>Organic</option>";
		String semiDetached = "<option>Semi-detached</option>";
		String embedded = "<option>Embedded</option>";
		if(softDevMode == null) organic = "<option selected=\"selected\">Organic</option>";
		else { 
			switch (softDevMode) {
			case "Organic": organic = "<option selected=\"selected\">Organic</option>";
			break;
			case "Semi-detached": semiDetached = "<option selected=\"selected\">Semi-detached</option>";
			break;
			case "Embedded": embedded = "<option selected=\"selected\">Embedded</option>";
			break;
			default: organic = "<option selected=\"selected\">Organic</option>";
			break;
			}
		}
		devMode = organic + semiDetached + embedded;
		return devMode;
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
