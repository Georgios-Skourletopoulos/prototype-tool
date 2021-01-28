package com.example.JUnits;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.hibtdq.BuyingServlet;
import com.example.hibtdq.CocomoServlet;
import com.example.hibtdq.GraphServlet;
import com.example.hibtdq.ImplementingServlet;
import com.example.hibtdq.NewBuyingServlet;
import com.example.hibtdq.ShareEstimationServlet;
import com.example.model.Cocomo;
import com.example.usecases.DBService;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;


/**
 * Mockito is an API for mocking objects, with the purpose of simplifying the testing process
 * (jar file found in the lib folder).
 */
import static org.mockito.Mockito.*;

/**
 * ServletsDevTest is a class for testing the functionality of the NewBuying, Cocomo, Implementing,
 * Buying, Graph and ShareEstimation Servlets.
 * @author Georgios Skourletopoulos, by adapting code from:
 * Google Project Hosting (n.d.), "Mockito", https://code.google.com/p/mockito/ [accessed 18 Aug 2013]
 * GitHub (2013), "GoogleCloudPlatform/appengine-guestbook-java", https://github.com/GoogleCloudPlatform/appengine-guestbook-java/blob/master/src/test/java/com/google/appengine/demos/guestbook/GuestbookServletTest.java [accessed 18 Aug 2013]
 * Soliloguy (2013), "A dive into Mockito – Part 2", http://blog.karthiksankar.com/mockito-2/ [accessed 18 Aug 2013]
 * @version 18 August 2013
 */
public class ServletsDevTest {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalUserServiceTestConfig())
	.setEnvIsLoggedIn(true)
	.setEnvAuthDomain("example.com")
	.setEnvEmail("testUser2@example.com");

	@Before
	public void setupHelper() {
		helper.setUp();
	}

	@After
	public void tearDownHelper() {
		helper.tearDown();
	}

	@Test
	public void NewBuyingServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);

		HttpSession session = mock(HttpSession.class);
		when(request.getSession()).thenReturn(session);

		when(request.getParameter("dispatchAction")).thenReturn("new");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("buy.jsp")).thenReturn(rd);

		new NewBuyingServlet().doGet(request, response);

		verify(request).getParameter("dispatchAction");
		verify(rd).forward(request, response);
	}

	@Test
	public void CocomoServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession()).thenReturn(session);
		when(request.getParameter("dispatchActionCocomo")).thenReturn("showCocomo");
		when(request.getParameter("idCocomo")).thenReturn("1");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("cocomo.jsp")).thenReturn(rd);

		new CocomoServlet().doGet(request, response);

		verify(rd).forward(request, response);
	}

	@Test
	public void ImplementingServletTest() throws ServletException, IOException {

		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession()).thenReturn(session);
		when(request.getParameter("dispatchActionImplement")).thenReturn("shareImplement");
		when(request.getParameter("idImplement")).thenReturn("1");

		new ImplementingServlet().doGet(request, response);

		verify(session).setAttribute(eq("estimation"), any());
		verify(session).setAttribute("type", "costForImplementing");
	}

	@Test
	public void BuyingServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession()).thenReturn(session);
		when(request.getParameter("dispatchActionBuying")).thenReturn("showBuy");
		when(request.getParameter("idBuy")).thenReturn("1");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("buy.jsp")).thenReturn(rd);

		new BuyingServlet().doGet(request, response);

		verify(rd).forward(request, response);
	}

	@Test
	public void GraphServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		when(request.getSession()).thenReturn(session);

		when(request.getParameter("dispatchAction")).thenReturn("new");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("graph.jsp")).thenReturn(rd);

		new GraphServlet().doGet(request, response);

		verify(rd).forward(request, response);
	}

	@Test
	public void ShareEstimationServletTest() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);
		session.setAttribute("type", "cocomo");
		when(request.getSession()).thenReturn(session);

		when(session.getAttribute("type")).thenReturn("cocomo");		
		when(request.getParameter("dispatchActionShare")).thenReturn("view");
		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("shareEstimation.jsp")).thenReturn(rd);

		DBService db = new DBService();
		Cocomo cocomo = db.getCocomoById(new Long(1));

		when(session.getAttribute("estimation")).thenReturn(cocomo);

		new ShareEstimationServlet().doGet(request, response);

		verify(session).getAttribute("type");
		verify(request, times(2)).getParameter("dispatchActionShare");
		verify(rd).forward(request, response);
	}
}
