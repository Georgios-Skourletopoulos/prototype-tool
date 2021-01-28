package com.example.hibtdq;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;


/**
 * ErrorServlet is a class that redirects to the error page.
 * @author Georgios Skourletopoulos
 * @version 15 August 2013
 */
@SuppressWarnings("serial")
public class ErrorServlet extends HttpServlet {

	/**
	 * This method deals with sending data to the web client notifying the type of error that
	 * occured.
	 * @param req is the request object
	 * @param resp the respond for that request
	 * @throws ServletException when there is an error inside the servlet
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("error servlet get");
		Throwable throwable = (Throwable)
				req.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer)
				req.getAttribute("javax.servlet.error.status_code");
		String statusCodeMessage = (String)
				req.getAttribute("javax.servlet.error.message");
		String servletName = (String)
				req.getAttribute("javax.servlet.error.servlet_name");

		/*
		 * If the servlet name is not recognised, it is named as unknown.
		 */
		if (servletName == null){
			servletName = "Unknown";
		}
		String requestUri = (String)
				req.getAttribute("javax.servlet.error.request_uri");

		/*
		 * If the location is not determined, it is named unknown.
		 */
		if (requestUri == null){
			requestUri = "Unknown";
		}

		String part1 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
				+ " \"http://www.w3.org/TR/html4/loose.dtd\"> " +
				"<html> <head> <title>Technical Debt in Cloud</title> "
				+ "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />"
				+ "<meta name=\"description\" content=\"\" /> "
				+ "<meta name=\"keywords\" content=\"\" />"
				+ "<link href=\"http://fonts.googleapis.com/"
				+ "css?family=Source+Sans+Pro:400,400italic,700,900\" rel=\"stylesheet\" />"
				+ "<script src=\"js/jquery.min.js\"></script>"
				+ "<script src=\"js/jquery.dropotron.js\"></script>"
				+ "<script src=\"js/config.js\"></script>"
				+ "<script src=\"js/skel.min.js\"></script>"
				+ "<script src=\"js/skel-panels.min.js\"></script>"
				+ "<script src=\"js/myFunctions.js\"></script>"
				+ "<noscript>"
				+ "<link rel=\"stylesheet\" href=\"css/skel-noscript.css\" />"
				+ "<link rel=\"stylesheet\" href=\"css/style.css\" />"
				+ "<link rel=\"stylesheet\" href=\"css/style-desktop.css\" />"
				+ "</noscript>"
				+ "<META http-equiv=\"refresh\" content=\"15;URL=/\">"
				+ "</head><body class=\"no-sidebar\">"
				+ "<!-- Header Wrapper -->"
				+ "<div id=\"header-wrapper\" class=\"wrapper\">"
				+ "  <div class=\"container\">"
				+ "    <div class=\"row\">"
				+ "      <div class=\"12u\">"
				+ "        <!-- Header -->"
				+ "        <div id=\"header\">"
				+ "          <!-- Logo -->"
				+ "          <div id=\"logo\">"
				+ "            <h1><a href=\"#\">Technical Debt in Cloud</a></h1>"
				+ "          </div>"
				+ "          <!-- /Logo -->"
				+ "          <!-- Nav -->"
				+ "          <nav id=\"nav\" style=\"text-align:right\">"
				+ "            <ul>"
				+ "              <li><a href=\"/\">Home</a></li>"
				+ "          </ul>"
				+ "        </nav>"
				+ "        <!-- /Nav -->"
				+ "      </div>"
				+ "      <!-- /Header -->"
				+ "    </div>"
				+ "  </div>"
				+ "</div>"
				+ "</div>"
				+ "<!-- /Header Wrapper -->"
				+ "<!-- Main Wrapper -->"
				+ "<div class=\"wrapper wrapper-style1\">"
				+ "<div class=\"title\">Error/Exception Information</div>"
				+ "<div class=\"container\">"
				+ "  <div class=\"row\">"
				+ "    <div class=\"12u\">"
				+ "      <!-- Main -->"
				+ "      <div id=\"main\">"
				+ "        <div>"
				+ "          <div class=\"row\">"
				+ "            <div class=\"12u skel-cell-mainContent\">"
				+ "              <!-- Content -->"
				+ "              <div id=\"content\"> <div class=\"actions actions-centered\">"
				+ "                <p><b>You have encountered an error.</b></div>"
				+ "                <p>We apologise for the inconvenience.</p>"
				+ "                <p><b>Error information:</b></p>";

		String message = "";

		/*
		 * If the error cannot be determined, it displays the corresponding message.
		 * If it is a status error, it displays the corresponding message.
		 * Otherwise, it displays the full information of the caught exception.
		 */
		if (throwable == null && statusCode == null){
			message = "Error information is missing";
		} else if (statusCode != null) {
			message = "Error status code : " + statusCode + "<br><br>"
					+ "Message: " + statusCodeMessage;
		} else {
			message = "Servlet Name : " + servletName + 
					"</br></br>"
					+ "Exception Type : " + throwable.getClass( ).getName( ) + 
					"</br></br>" 
					+ "The request URI: " + requestUri + 
					"<br><br>"
					+ "The exception message: " + throwable.getMessage( );
		}

		String part2 = "                  <p>" + message + "</p>"
				+ "                  <p>You will be redirected to the main page in 15 seconds...</p>"
				+ "                </div>"
				+ "              </div>"
				+ "              <!-- /Content -->"
				+ "            </div>"
				+ "          </div>"
				+ "        </div>"
				+ "      </div>"
				+ "      <!-- /Main -->"
				+ "    </div>"
				+ "  </div>"
				+ "</div>"
				+ "</div>"
				+ "<!-- /Main Wrapper -->"
				+ "<!-- Footer Wrapper -->"
				+ "<div id=\"footer-wrapper\" class=\"wrapper\">"
				+ "  <div class=\"title\"></div>"
				+ "  <div class=\"container\">"
				+ "    <div class=\"row\">"
				+ "      <div class=\"12u\">"
				+ "        <!-- Copyright -->"
				+ "        <div id=\"copyright\"> <span> Copyright &copy;"
				+ " 2013 by Georgios Skourletopoulos. All rights reserved. </span> </div>"
				+ "        <!-- /Copyright -->"
				+ "      </div>"
				+ "    </div>"
				+ "  </div>"
				+ "</div>"
				+ "<!-- /Footer Wrapper -->"
				+ "</body></html>";

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println(part1 + part2);
	}
}
