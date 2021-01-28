package com.example.hibtdq;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AuthenticationServlet is a class that redirects to the authentication page.
 * @author Georgios Skourletopoulos
 * @version 9 August 2013
 */
@SuppressWarnings("serial")
public class AuthenticationServlet extends HttpServlet {

	/**
	 * The method that is called on the HTTP GET request when the servlet is called.
	 * @param req is the request
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher("authentication.jsp");
		rd.forward(req, resp);
	}
}
