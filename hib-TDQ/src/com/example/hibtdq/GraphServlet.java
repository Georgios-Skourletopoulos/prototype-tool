package com.example.hibtdq;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * GraphServlet is a class that deals with selecting the Technical Debt (TD) for Leasing a Cloud
 * Software as a Service (SaaS) estimates in order to be compared and generate the appropriate graphs and
 * tables.
 * @author Georgios Skourletopoulos
 * @version 12 August 2013
 */
@SuppressWarnings("serial")
public class GraphServlet extends HttpServlet {

	DBService service = new DBService();    //all the actions (interaction) performed in the database

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
			String dispatch = req.getParameter("dispatchAction");
			if(dispatch == null)
				dispatch = "new";
			System.out.println(dispatch);
			switch(dispatch) { 
			case "new":
				fillDetails(req,resp);
				break;
			case "create":
				calculateTD(req,resp);
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
	 * This method deals with displaying the list with the created or shared TD for Leasing
	 * estimates in order to be selected and compared.
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void fillDetails(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		List<TDinBuying> buying = service.getTDinBuyingCreatedByEmail(user.getEmail());
		List<TDinBuying> buyingShared = service.getTDinBuyingSharedByEmail(user.getEmail());
		buying.addAll(buyingShared);

		/*
		 * If there are no estimates to be compared, display the corresponding message.
		 */
		if(buying.size() < 1) {
			req.setAttribute("response", "There are no estimates to be displayed!");
			req.setAttribute("disableButton", "disabled");
		}
		else {
			req.setAttribute("estimations", buying);
		}
		req.setAttribute("display", "none");
		RequestDispatcher rd = req.getRequestDispatcher("graph.jsp");
		rd.forward(req, resp);
	}

	/**
	 * This method bring all the selected TD quantifications together as a HashMap and 
	 * @param req is the request object
	 * @param resp is the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	private void calculateTD(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		List<TDinBuying> buying = service.getTDinBuyingCreatedByEmail(user.getEmail());    //retrieve the estimates that the user created
		List<TDinBuying> buyingShared = service.getTDinBuyingSharedByEmail(user.getEmail());    //retrieve the estimates that the user shared
		buying.addAll(buyingShared);
		req.setAttribute("estimations", buying);
		System.out.println("graph td");
		String [] tdInBuyingNames = req.getParameterValues("tdInBuying");    //names of estimates selected by the user for the comparison
		TDinBuying aux = null;
		Map<TDinBuying, Double[]> map = new HashMap<TDinBuying, Double[]>();    //results will be stored in a HashMap
		int maxROI = 0;
		for(int i=0; i < tdInBuyingNames.length; i++) {
			aux = service.getTDinBuyingByUserEmailAndTDName(user.getEmail(), tdInBuyingNames[i]);

			/*
			 * Determining the maximum years of ROI form the set of the selected estimates.
			 */
			if(aux.getRoi() > maxROI)
				maxROI = aux.getRoi();
			map.put(aux, doCalculation(aux));
		}

		req.setAttribute("selectedEstimates", map.keySet());

		/**
		 * Part of code taken and adapted accordingly by Google (2012), “Google Charts”,
		 * https://developers.google.com/chart/ [accessed 21 Aug 2013]
		 * Constructing the String representing the array that will be used on the client side in
		 * order to create the graph and the table.
		 */
		String array = "['Year',"; 
		for(TDinBuying estimate : map.keySet()) {
			array += "'" + estimate.getName() + "',";
		}
		array = array.substring(0, array.length()-1);
		array += "],";

		DecimalFormat df = new DecimalFormat("#");

		for (int i=0; i<maxROI; i++) {
			array += "['" + (i+1) + "',";
			for (TDinBuying estimate : map.keySet()) {
				if(estimate.getRoi() >= i+1)
					array += "" + df.format(map.get(estimate)[i]) +",";
				else array += "null,";
			}
			array = array.substring(0, array.length()-1);
			array += "],";
		}
		array = array.substring(0, array.length()-1);
		System.out.println(array);
		req.setAttribute("table", array);    //set attribute that represent the data that the graph and table will be generated on
		RequestDispatcher rd = req.getRequestDispatcher("graph.jsp");
		rd.forward(req, resp);
	}

	/**
	 * This method calulates the TD for Leasing according to the formula provided for a given
	 * estimation.
	 * @param tdInBuying is the estimation that is asked to be calculated
	 * @return td is the result
	 */
	private Double[] doCalculation(TDinBuying tdInBuying) {
		Double [] td = new Double[tdInBuying.getRoi()]; 
		for(int i = 0; i < tdInBuying.getRoi(); i++) {
			td[i] = (12 * ( new Double(tdInBuying.getMaxCapacity().toString()) - 
					Math.pow( new Double(1) + new Double(tdInBuying.getDemandRaise().toPlainString()) / 100, i) * 
					new Double(tdInBuying.getCurrentUsers().toString())) * ( Math.pow(new Double(1) + 
							new Double(tdInBuying.getRaiseSubscriptionPrice().toPlainString()) / (100 * 
									new Double (tdInBuying.getRoi())), i) * 
									new Double(tdInBuying.getSubscriptionPrice().toPlainString()) - 
									Math.pow(new Double(1) + new Double(tdInBuying.getRaiseCloudCost().toPlainString())
									/ (100 * new Double (tdInBuying.getRoi())), i)
									* new Double(tdInBuying.getCloudCost().toPlainString())));
		}
		return td;
	}
}
