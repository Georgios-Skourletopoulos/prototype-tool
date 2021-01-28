package com.example.hibtdq;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.model.Project;
import com.example.model.SharedProject;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ShareProjectServlet is a class that deals with sharing and deleting a user from any project.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@SuppressWarnings("serial")
public class ShareProjectServlet extends HttpServlet {

	DBService service = new DBService();

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to a project task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			String dispatch;
			if(req.getParameter("dispatchActionShare") == null)
				dispatch = "view";
			else
				dispatch = req.getParameter("dispatchActionShare");
			switch(dispatch) {
			case "view":
				fillDetails(req,resp);
				break;
			case "addShare":
				if(req.getAttribute("response") == null)
					shareProject(req,resp);
				else fillDetails(req,resp); 			
				break;
			case "delShare":
				if(req.getAttribute("response") == null)
					deleteSharedUser(req,resp);
				else fillDetails(req,resp); 			
				break;
			default: break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform
	 * operations in the Project area.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @return true if the user is not a Project Manager redirecting him to the authentication page, otherwise false if he is a Project Manager
	 * @throws IOException
	 */
	private boolean checkJob(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		UserLogin usr = service.getUserByEmail(user.getEmail());
		HttpSession session = req.getSession();
		if(!usr.getJobId().getName().trim().equals("Project Manager")) {
			session.setAttribute("user", usr);
			resp.sendRedirect("/authentication");
			return true;
		}
		return false;
	}

	/**
	 * This method deals with showing the details that were previously saved for a specific project.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void fillDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("share.jsp");
		HttpSession session = req.getSession();
		Project project = (Project)session.getAttribute("projectId");
		List<SharedProject> sharedWithList = service.getSharedUsersByProject(project.getId());
		if(sharedWithList.size() == 0) {
			req.setAttribute("delDisplay", "none");
		}
		else req.setAttribute("delDisplay", "block");
		req.setAttribute("shareList", sharedWithList);
		rd.forward(req, resp);
	}

	/**
	 * The method that deals with sharing a project with another user.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void shareProject(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sharedWithEmail = req.getParameter("sharedWith");

		Timestamp timestamp = new Timestamp(new java.util.Date().getTime());

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		RequestDispatcher rd;

		/*
		 * Check if the user shares with himself.
		 */
		if(sharedWithEmail.trim().equals(user.getEmail().trim())) {
			req.setAttribute("response", "You cannot share with yourself!");
			rd = req.getRequestDispatcher("/share");
			rd.forward(req, resp);
		}
		else {
			UserLogin userShared = service.getUserByEmail(sharedWithEmail);
			SharedProject shared = null;

			/*
			 * Check if the receiver exists.
			 */
			if(userShared == null) {
				req.setAttribute("response", "The user you are trying to share with does not exist!");
				rd = req.getRequestDispatcher("/share");
				rd.forward(req, resp);
			}
			else {
				System.out.println("found user");
				HttpSession session = req.getSession();
				boolean shareCheck = service.checkShared(sharedWithEmail, 
						((Project)session.getAttribute("projectId")).getId(), user.getEmail());
				System.out.println(shareCheck);

				/*
				 * Check if the user has shared the same project again with the same receiver.
				 */
				if (!shareCheck) {
					System.out.println("already shared");
					req.setAttribute("response", "You have already shared this project with " + sharedWithEmail + "!");
					rd = req.getRequestDispatcher("/share");
					rd.forward(req, resp);
				}
				else {
					shared = service.shareProject(sharedWithEmail, 
							((Project)session.getAttribute("projectId")).getId(), user.getEmail(), timestamp);

					if(shared != null) { 
						System.out.println("shared DB");
						resp.sendRedirect("/share");
					}
					else {
						System.out.println("share not added DB");
						req.setAttribute("response", "Sharing failed!");
						rd = req.getRequestDispatcher("/share");
						rd.forward(req, resp);
					}
				}
			}
		}
	}

	/**
	 * The method that deals with deleting a receiver from a shared project.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] users = req.getParameterValues("email");
		HttpSession session = req.getSession();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		RequestDispatcher rd;
		boolean success = true;

		/*
		 * Check if a user was selected.
		 */
		if(users == null) {
			req.setAttribute("response", "No user selected!");
			rd = req.getRequestDispatcher("/share");
			rd.forward(req, resp);
		}
		else {
			for(String userShared : users) {
				if(!service.deleteSharedUser(userShared, ((Project)session.getAttribute("projectId")).getId(), user.getEmail()))
					success = false;
			}
			if(success == false) {
				req.setAttribute("response", "Deletion failed!");
				rd = req.getRequestDispatcher("/share");
				rd.forward(req, resp);
			} else {
				resp.sendRedirect("/share");
			}
		}
	}
}
