package com.example.hibtdq;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * HibTDQServlet is the class that constitutes the entry point of the web (cloud-based) application
 * and deals with actions such as the Google login service, adding a new user to the database,
 * updating the login time of the existing users, determining which profile working area they are
 * allowed to access and warning for not being allowed to access any of the areas.
 * @author Georgios Skourletopoulos
 * @version 1 August 2013
 */
@SuppressWarnings("serial")
public class HibTDQServlet extends HttpServlet {

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to the login and job position specification.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		System.out.println("HIBTDQ get");
		DBService service = new DBService ();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		/*
		 * If the user is logged in, it checks if he has a cookie.
		 */
		if (user != null) {
			Cookie[] cookies = req.getCookies();
			boolean cookieFound = false;
			String cookieName = "TDQCookie" + user.getEmail();
			for(int i = 0; i < cookies.length; i++) {
				if(cookies[i].getName().equals(cookieName))
					cookieFound = true;
			}

			/*
			 * If the cookie is not found, it means that it is anew login session and the login time
			 * is updated in the database.
			 */
			if(!cookieFound) {
				System.out.println("cookie not found");
				boolean login = service.userLogin(user, new Timestamp(new java.util.Date().getTime()));

				/*
				 * If the update of the database failed, the user is logged out and redirected to
				 * the login page.
				 */
				if(!login) {
					resp.getWriter().println("<html><head><META http-equiv=\"refresh\" "
							+ "content=\"5;URL=" + userService.createLogoutURL(req.getRequestURI()) + 
							"\"></head><body>There was a problem with your "
							+ "login. Please try again. You are being redirected to the login page in 5 seconds"
							+ "</body></html>");

					System.out.println("error user");
				}

				/*
				 * Set a cookie that will be deleted when the user closes the browser.
				 */
				Cookie cookie = new Cookie("TDQCookie" + user.getEmail(), user.getEmail());
				cookie.setMaxAge(-1);
				resp.addCookie(cookie);
			}
			System.out.println("cookie found");
			RequestDispatcher rd = req.getRequestDispatcher("jobselect.jsp");
			UserLogin usr = service.getUserByEmail(user.getEmail());

			/*
			 * If the user has not specified a job position, he is asked to specify his job position
			 * in order to have access to the application.
			 */
			if(usr.getJobId() == null)
			{
				req.setAttribute("disableDev", "disabled");
				req.setAttribute("disablePM", "disabled");
				req.setAttribute("profileStatement", "If you have not specified your job position yet, "
						+ "please press <input type=\"submit\" class=\"button"
						+ " button-style3 button-small\" value=\"here\" /> in order to have access to the "
						+ "application.");
			}
			else if(usr.getJobId().getId() == Long.parseLong("1"))
			{
				req.setAttribute("disableDev", "disabled");
				req.setAttribute("disablePM", "");
			}
			else 
			{
				req.setAttribute("disableDev", "");
				req.setAttribute("disablePM", "disabled");
			}

			HttpSession session = req.getSession();
			session.setAttribute("userDetails", user);
			session.setAttribute("logoutUrl", userService.createLoginURL(req.getRequestURI()));
			rd.forward(req, resp);
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
	}
}
