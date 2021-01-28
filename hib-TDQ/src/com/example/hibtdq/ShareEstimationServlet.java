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

import com.example.model.Cocomo;
import com.example.model.CostForImplementing;
import com.example.model.SharedBuying;
import com.example.model.SharedCocomo;
import com.example.model.SharedImplementing;
import com.example.model.TDinBuying;
import com.example.model.UserLogin;
import com.example.usecases.DBService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ShareEstimationServlet is a class that deals with sharing and deleting a user from any kind of estimation.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@SuppressWarnings("serial")
public class ShareEstimationServlet extends HttpServlet {

	DBService service = new DBService();

	/**
	 * This method deals with retrieving and sending data from the web client according to a
	 * specific action relating to a specific estimation task.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(!checkJob(req, resp)) {
			String dispatch;
			HttpSession session = req.getSession();
			String estimationType = session.getAttribute("type").toString().trim();
			System.out.println(estimationType);
			switch(estimationType) {
			case "cocomo":
				if(req.getParameter("dispatchActionShare") == null)
					dispatch = "view";
				else
					dispatch = req.getParameter("dispatchActionShare");
				System.out.println("share dispatch: " + dispatch);
				switch(dispatch) {
				case "view":
					fillDetailsCocomo(req,resp);
					break;
				case "addShare":
					if(req.getAttribute("response") == null)
						shareCocomo(req,resp);
					else { 
						fillDetailsCocomo(req,resp);
						req.removeAttribute("response");
					}
					break;
				case "delShare":
					if(req.getAttribute("response") == null)
						deleteSharedUserCocomo(req,resp);
					else {
						fillDetailsCocomo(req,resp);
						req.removeAttribute("response");
					}
					break;
				default:
					break;
				}
				break;
			case "costForImplementing":
				if(req.getParameter("dispatchActionShare") == null)
					dispatch = "view";
				else
					dispatch = req.getParameter("dispatchActionShare");
				System.out.println("share dispatch: " + dispatch);
				switch(dispatch) {
				case "view":
					fillDetailsCostForImplementing(req,resp);
					break;
				case "addShare":
					if(req.getAttribute("response") == null)
						shareCostForImplementing(req,resp);
					else {
						fillDetailsCostForImplementing(req,resp);
						req.removeAttribute("response");
					}
					break;
				case "delShare":
					if(req.getAttribute("response") == null)
						deleteSharedUserCostForImplementing(req,resp);
					else {
						fillDetailsCostForImplementing(req,resp);
						req.removeAttribute("response");
					}
					break;
				default: break;
				}
				break;
			case "tdInBuying":
				if(req.getParameter("dispatchActionShare") == null)
					dispatch = "view";
				else
					dispatch = req.getParameter("dispatchActionShare");
				System.out.println("share dispatch: " + dispatch);
				switch(dispatch) {
				case "view":
					fillDetailsTDinBuying(req,resp);
					break;
				case "addShare":
					if(req.getAttribute("response") == null)
						shareTDinBuying(req,resp);
					else {
						fillDetailsTDinBuying(req,resp);
						req.removeAttribute("response");
					}
					break;
				case "delShare":
					if(req.getAttribute("response") == null)
						deleteSharedUserTDinBuying(req,resp);
					else {
						fillDetailsTDinBuying(req,resp);
						req.removeAttribute("response");
					}
					break;
				default:
					break;
				}
				break;
			default: 
				break;
			}
		}
	}

	/**
	 * This method checks the correctness of the job position in order the user to perform calculations.
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
	 * This method deals with showing the details that were previously saved for a specific COCOMO estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void fillDetailsCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("shareEstimation.jsp");
		HttpSession session = req.getSession();
		Cocomo cocomo = (Cocomo)session.getAttribute("estimation");

		List<SharedCocomo> sharedWithList = service.getSharedUsersByCocomo(cocomo.getId());
		if(sharedWithList.size() == 0) {
			req.setAttribute("delDisplay", "none");
		}
		else req.setAttribute("delDisplay", "block");
		req.setAttribute("shareList", sharedWithList);
		rd.forward(req, resp);
	}

	/**
	 * The method that deals with sharing a COCOMO estimation with another user.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void shareCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			UserLogin userShared = service.getUserByEmail(sharedWithEmail);
			SharedCocomo shared = null;

			/*
			 * Check if the receiver exists.
			 */
			if(userShared == null) {
				req.setAttribute("response", "The user you are trying to share with does not exist!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			}
			else {
				System.out.println("found user");
				HttpSession session = req.getSession();
				boolean shareCheck = service.checkSharedCocomo(sharedWithEmail, 
						((Cocomo)session.getAttribute("estimation")).getId(), user.getEmail());
				System.out.println(shareCheck);

				/*
				 * Check if the user has shared the same COCOMO estimation again with the same receiver.
				 */
				if (!shareCheck) {
					System.out.println("already shared");
					req.setAttribute("response", "You have already shared this estimation with " 
							+ sharedWithEmail + "!");
					rd = req.getRequestDispatcher("/shareEstimate");
					rd.forward(req, resp);
				}
				else {
					shared = service.shareCocomo(sharedWithEmail, 
							((Cocomo)session.getAttribute("estimation")).getId(), user.getEmail(), timestamp);

					if(shared != null) { 
						System.out.println("shared DB");
						resp.sendRedirect("/shareEstimate");
					}
					else {
						System.out.println("share not added DB");
						req.setAttribute("response", "Sharing failed!");
						rd = req.getRequestDispatcher("/shareEstimate");
						rd.forward(req, resp);
					}
				}
			}
		}
	}

	/**
	 * The method that deals with deleting a receiver from a shared COCOMO estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedUserCocomo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			for(String userShared : users) {
				if(!service.deleteSharedCocomoUser(userShared, ((Cocomo)session.
						getAttribute("estimation")).getId(), user.getEmail()))
					success = false;
			}
			if(success == false) {
				req.setAttribute("response", "Deletion failed!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			} else {
				resp.sendRedirect("/shareEstimate");
			}
		}
	}

	/**
	 * This method deals with showing the details that were previously saved for a specific cost
	 * for Implementing estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void fillDetailsCostForImplementing(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("shareEstimation.jsp");
		HttpSession session = req.getSession();
		CostForImplementing costForImplementing = (CostForImplementing)session.getAttribute("estimation");

		List<SharedImplementing> sharedWithList = service.getSharedUsersByImplementing(costForImplementing.getId());
		if(sharedWithList.size() == 0) {
			req.setAttribute("delDisplay", "none");
		}
		else req.setAttribute("delDisplay", "block");
		req.setAttribute("shareList", sharedWithList);
		rd.forward(req, resp);
	}

	/**
	 * The method that deals with sharing a cost for Implementing estimation with another user.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void shareCostForImplementing(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			UserLogin userShared = service.getUserByEmail(sharedWithEmail);
			SharedImplementing shared = null;

			/*
			 * Check if the receiver exists.
			 */
			if(userShared == null) {
				req.setAttribute("response", "The user you are trying to share with does not exist!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			}
			else {
				System.out.println("found user");
				HttpSession session = req.getSession();
				boolean shareCheck = service.checkSharedImplementing(sharedWithEmail, 
						((CostForImplementing)session.getAttribute("estimation")).getId(), user.getEmail());
				System.out.println(shareCheck);

				/*
				 * Check if the user has shared the same cost for Implementing estimation again with the same receiver.
				 */
				if (!shareCheck) {
					System.out.println("already shared");
					req.setAttribute("response", "You have already shared this estimation with " 
							+ sharedWithEmail + "!");
					rd = req.getRequestDispatcher("/shareEstimate");
					rd.forward(req, resp);
				}
				else {
					shared = service.shareImplementing(sharedWithEmail, 
							((CostForImplementing)session.getAttribute("estimation")).getId(), user.getEmail(), 
							timestamp);

					if(shared != null) { 
						System.out.println("shared DB");
						resp.sendRedirect("/shareEstimate");
					}
					else {
						System.out.println("share not added DB");
						req.setAttribute("response", "Sharing failed!");
						rd = req.getRequestDispatcher("/shareEstimate");
						rd.forward(req, resp);
					}
				}
			}
		}
	}

	/**
	 * The method that deals with deleting a receiver from a shared cost for Implementing estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedUserCostForImplementing(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			for(String userShared : users) {
				if(!service.deleteSharedImplementingUser(userShared, ((CostForImplementing)session.
						getAttribute("estimation")).getId(), user.getEmail()))
					success = false;
			}

			if(success == false) {
				req.setAttribute("response", "Deletion failed!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			} else {
				resp.sendRedirect("/shareEstimate");
			}
		}
	}

	/**
	 * This method deals with showing the details that were previously saved for a specific Technical
	 * Debt (TD) for Leasing estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void fillDetailsTDinBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("shareEstimation.jsp");
		HttpSession session = req.getSession();
		TDinBuying tdInBuying = (TDinBuying)session.getAttribute("estimation");

		List<SharedBuying> sharedWithList = service.getSharedUsersByBuying(tdInBuying.getId());
		if(sharedWithList.size() == 0) {
			req.setAttribute("delDisplay", "none");
		}
		else req.setAttribute("delDisplay", "block");
		req.setAttribute("shareList", sharedWithList);
		rd.forward(req, resp);
	}

	/**
	 * The method that deals with sharing a TD for Leasing estimation with another user.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void shareTDinBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			UserLogin userShared = service.getUserByEmail(sharedWithEmail);
			SharedBuying shared = null;

			/*
			 * Check if the receiver exists.
			 */
			if(userShared == null) { 
				req.setAttribute("response", "The user you are trying to share with does not exist!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			}
			else {
				System.out.println("found user");
				HttpSession session = req.getSession();
				boolean shareCheck = service.checkBuyingShared(sharedWithEmail, 
						((TDinBuying)session.getAttribute("estimation")).getId(), user.getEmail());
				System.out.println(shareCheck);

				/*
				 * Check if the user has shared the same TD for Leasing estimation again with the same receiver.
				 */
				if (!shareCheck) {
					System.out.println("already shared");
					req.setAttribute("response", "You have already shared this estimation with " 
							+ sharedWithEmail + "!");
					rd = req.getRequestDispatcher("/shareEstimate");
					rd.forward(req, resp);
				}
				else {
					shared = service.shareTDinBuying(sharedWithEmail, 
							((TDinBuying)session.getAttribute("estimation")).getId(), user.getEmail(), timestamp);

					if(shared != null) { 
						System.out.println("shared DB");
						resp.sendRedirect("/shareEstimate");
					}
					else {
						System.out.println("share not added DB");
						req.setAttribute("response", "Sharing failed!");
						rd = req.getRequestDispatcher("/shareEstimate");
						rd.forward(req, resp);
					}
				}
			}
		}
	}

	/**
	 * The method that deals with deleting a receiver from a shared TD for Leasing estimation.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void deleteSharedUserTDinBuying(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			rd = req.getRequestDispatcher("/shareEstimate");
			rd.forward(req, resp);
		}
		else {
			for(String userShared : users) {
				if(!service.deleteSharedBuyingUser(userShared, ((TDinBuying)session.
						getAttribute("estimation")).getId(), user.getEmail()))
					success = false;
			}

			if(success == false) {
				req.setAttribute("response", "Deletion failed!");
				rd = req.getRequestDispatcher("/shareEstimate");
				rd.forward(req, resp);
			} else {
				resp.sendRedirect("/shareEstimate");
			}
		}
	}
}
