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
 * NewCocomoServlet is a class that deals with showing the form and creating a COCOMO quantification.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@SuppressWarnings("serial")
public class NewCocomoServlet extends HttpServlet {

	DBService service = new DBService();    //all the actions (interaction) performed in the database

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
			System.out.println("COCOMO Get");
			String dispatch = null;
			dispatch = req.getParameter("dispatchAction");
			if(dispatch == null)
				dispatch = "new";
			System.out.println("dispatch: " + dispatch);
			switch(dispatch) {
			case "new":
				newCocomo(req,resp);
				break;
			case "calculate":
				calculateCocomo(req,resp);
				break;
			case "submit": 
				if(req.getAttribute("response") == null)
					submitCocomo(req,resp);
				else {
					newCocomo(req,resp);
					req.removeAttribute("response");
				}
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform COCOMO
	 * calculations.
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
	 * This method deals with submitting a new COCOMO estimate in order to be added into the
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

		/*
		 * Checks if the name already exists in the database.
		 */
		if(service.checkCocomoForSubmission(user.getEmail(), req.getParameter("cocomoName")) != null) {
			req.setAttribute("response", "COCOMO estimation name already exists!");
			error = true;
			System.out.println("error true");
		}
		Cocomo cocomo = null;
		UserLogin usr = service.getUserByEmail(user.getEmail());	

		/*
		 * If there is not a previous estimation with the same name, then it is inserted in the database.
		 */
		if(!error) {
			cocomo = new Cocomo(usr, req.getParameter("cocomoName"),
					req.getParameter("softDevMode"), new BigDecimal(req.getParameter("kloc")), 
					req.getParameter("confidence"), req.getParameter("justification"));
			if(service.persistCocomo(cocomo) != null) {
				resp.sendRedirect("/main");
			}
			else {
				req.setAttribute("reponse", "Submission unsuccessful!");
				rd = req.getRequestDispatcher("/cocomoNew");
				rd.forward(req, resp);
			}
		}
		else {
			rd = req.getRequestDispatcher("/cocomoNew");
			rd.forward(req, resp);
		} 
	}

	/**
	 * This method deals with redirecting in the COCOMO form.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void newCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("cocomo.jsp");
		req.setAttribute("softDevModeList", buildSoftDevMode(null));
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("display", "none");
		rd.forward(req, resp);
	}

	/**
	 * This method calculates COCOMO according to the provided elements (software development mode
	 * and KLOC) and the corresponding mathematical formula.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("cocomo.jsp");
		String mode = req.getParameter("softDevMode");
		req.setAttribute("display", "block");
		req.setAttribute("softDevModeList", buildSoftDevMode(mode));
		req.setAttribute("cocomoNameValue", req.getParameter("cocomoName"));
		req.setAttribute("klocValue", req.getParameter("kloc")); 
		req.setAttribute("saveDisabled", "hidden");
		req.setAttribute("submitDisabled", "submit");
		req.setAttribute("submitSharedDisabled", "hidden");
		req.setAttribute("confEst", buildConfidence(null));
		Double [] cocomo;
		DecimalFormat df = new DecimalFormat("#.00");

		/*
		 * Calculating COCOMO in order to be viewed with the corresponding results and the
		 * evaluation option to be selected.
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
	 * This method creates the Software development mode frame with the corresponding options to
	 * be selected.
	 * @param softDevMode the priority of the options
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
