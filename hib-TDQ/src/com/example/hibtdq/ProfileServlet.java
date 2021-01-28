package com.example.hibtdq;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.model.JobTitle;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ProfileServlet is a class that deals with editing a user's profile.
 * @author Georgios Skourletopoulos
 * @version 1 August 2013
 */
@SuppressWarnings("serial")
public class ProfileServlet extends HttpServlet  {

	DBService service = new DBService();    //all the actions (interaction) performed in the database

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the profile task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Profile get");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd = req.getRequestDispatcher("profile.jsp");
		fillFields(user,req);
		boolean through = false;

		/*
		 * Check actions and possible errors before saving changes.
		 */
		if(req.getParameter("dispatchAction") != null && req.getParameter("dispatchAction").equals("save")) {
			if(req.getAttribute("success") == null || !req.getAttribute("success").toString().equals("0")) {
				through = true;
				saveRedirect(req,resp); 
			}
		}
		if(through == false) {
			rd.forward(req, resp);
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
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		String company = req.getParameter("companyName");
		String location = req.getParameter("location");
		String title = req.getParameter("title");
		String job = req.getParameter("jobPosition");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		UserLogin usr = service.updateUser(user.getEmail(), title, firstName, lastName, 
				job, company, location);
		RequestDispatcher rd = req.getRequestDispatcher("/profile");

		/*
		 * If the user is updated, it redirects to the main page, otherwise it provides the
		 * corresponding flags.
		 */
		if(usr != null) {
			resp.sendRedirect("/");
		}
		else {
			req.setAttribute("response", "Update unsuccessful!");
			req.setAttribute("success", "0");
			rd.forward(req, resp);
		}
	}

	/**
	 * This method creates the Title frame with the corresponding options to be selected.
	 * @param title the options provided
	 * @return titleSelect the option selected as a String
	 */
	private String buildTitleSelect(String title) {
		String titleSelect = "";
		String mr = "<option>Mr</option>";
		String miss = "<option>Miss</option>";
		String mrs="<option>Mrs</option>";
		String ms = "<option>Ms</option>";
		String dr = "<option>Dr</option>";
		String prof = "<option>Professor</option>";
		if(title == null)
			mr = "<option selected=\"selected\">Mr</option>";
		else {
			switch(title) {
			case "Mr": mr = "<option selected=\"selected\">Mr</option>";
			break;
			case "Miss": miss = "<option selected=\"selected\">Miss</option>";
			break;
			case "Mrs": mrs="<option selected=\"selected\">Mrs</option>";
			break;
			case "Ms": ms="<option selected=\"selected\">Ms</option>";
			break;
			case "Dr": dr="<option selected=\"selected\">Dr</option>";
			break;
			case "Professor": prof="<option selected=\"selected\">Professor</option>";
			break;
			default: mr="<option selected=\"selected\">Mr</option>";
			break;
			}
		}
		titleSelect = mr + miss + mrs + ms + dr + prof;
		return titleSelect;
	}

	/**
	 * This method creates the Job position frame with the corresponding options to be selected.
	 * @param jobId the options provided (each job position has a specific ID)
	 * @return jobSelect the option selected as a String
	 */
	private String buildJobSelect (JobTitle jobId) {
		String jobSelect = "";
		String pm = "<option>Project Manager</option>";
		String tl = "<option>Team Leader</option>";
		String dev = "<option>Developer</option>";
		String arch = "<option>Architect</option>";
		if(jobId == null) pm = "<option selected=\"selected\">Project Manager</option>";
		else {
			switch(jobId.getId().toString()) {
			case "1": pm = "<option selected=\"selected\">Project Manager</option>";
			break;
			case "2": tl = "<option selected=\"selected\">Team Leader</option>";
			break;
			case "3": dev = "<option selected=\"selected\">Developer</option>";
			break;
			case "4": arch = "<option selected=\"selected\">Architect</option>";
			break;
			default: pm = "<option selected=\"selected\">Project Manager</option>";
			break;
			}
		}
		jobSelect = pm + tl + dev + arch;
		return jobSelect;
	}

	/**
	 * This method deals with showing the details that were previously saved in the user's profile.
	 * @param user the corresponding user
	 * @param req is the request object
	 */
	private void fillFields(User user, HttpServletRequest req) {
		UserLogin usr = service.getUserByEmail(user.getEmail());

		try {
			if(usr.getFirstName() != null)
				if(usr.getFirstName().length() != 0)
					req.setAttribute("firstNameValue", usr.getFirstName());
				else req.setAttribute("firstNamePlaceHolder", "First Name");
		}
		catch (NullPointerException e) {
			req.setAttribute("firstNamePlaceHolder", "First Name");
		}

		try {
			if(usr.getLastName() != null)
				if(usr.getLastName().length() != 0)
					req.setAttribute("lastNameValue", usr.getLastName());
				else req.setAttribute("lastNamePlaceHolder", "Last Name");
		}
		catch (NullPointerException e) {
			req.setAttribute("lastNamePlaceHolder", "Last Name");
		}

		try {
			if(usr.getCompanyName() != null)
				if(usr.getCompanyName().length() != 0)
					req.setAttribute("companyValue", usr.getCompanyName());
				else req.setAttribute("companyPlaceHolder", "Company");
		}
		catch (NullPointerException e) {
			req.setAttribute("companyPlaceHolder", "Company");
		}

		try {
			if(usr.getLocation() != null)
				if(usr.getLocation().length() != 0)
					req.setAttribute("locationValue", usr.getLocation());
				else req.setAttribute("locationPlaceHolder", "Location");
		}
		catch (NullPointerException e) {
			req.setAttribute("locationPlaceHolder", "Location");
		}
		req.setAttribute("titleSelect", buildTitleSelect(usr.getTitle()));
		req.setAttribute("jobSelect", buildJobSelect(usr.getJobId()));
	}
}
