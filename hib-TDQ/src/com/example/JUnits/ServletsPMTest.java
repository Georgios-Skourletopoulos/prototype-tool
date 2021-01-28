package com.example.JUnits;

/**
 * Mockito is an API for mocking objects, with the purpose of simplifying the testing process
 * (jar file found in the lib folder).
 */
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.hibtdq.ProjectServlet;
import com.example.hibtdq.ReportsServlet;
import com.example.hibtdq.ScenariosServlet;
import com.example.hibtdq.ShareProjectServlet;
import com.example.model.Project;
import com.example.usecases.DBService;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * ServletsPMTest is a class for testing the functionality of the Project, Scenario, ShareProject
 * and Reports Servlets.
 * @author Georgios Skourletopoulos, by adapting code from:
 * Google Project Hosting (n.d.), "Mockito", https://code.google.com/p/mockito/ [accessed 18 Aug 2013]
 * GitHub (2013), "GoogleCloudPlatform/appengine-guestbook-java", https://github.com/GoogleCloudPlatform/appengine-guestbook-java/blob/master/src/test/java/com/google/appengine/demos/guestbook/GuestbookServletTest.java [accessed 18 Aug 2013]
 * Soliloguy (2013), "A dive into Mockito – Part 2", http://blog.karthiksankar.com/mockito-2/ [accessed 18 Aug 2013]
 * @version 18 August 2013
 */
public class ServletsPMTest {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalUserServiceTestConfig())
	.setEnvIsLoggedIn(true)
	.setEnvAuthDomain("example.com")
	.setEnvEmail("testUser1@example.com");

	@Before
	public void setupHelper() {
		helper.setUp();
	}

	@After
	public void tearDownHelper() {
		helper.tearDown();
	}

	@Test
	public void ProjectServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter("dispatchActionProject")).thenReturn("new");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("project.jsp")).thenReturn(rd);

		new ProjectServlet().doGet(request, response);

		verify(rd).forward(request, response);
	}

	@Test
	public void ScenarioServletTest() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);

		HttpSession session = mock(HttpSession.class);
		DBService db = new DBService();
		Project project = db.getProjectById(new Long(1));
		session.setAttribute("projectId", project);

		when(request.getSession()).thenReturn(session);
		when(request.getParameter("dispatchActionScenario")).thenReturn("new");
		when(session.getAttribute("projectId")).thenReturn(project);

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("scenario.jsp")).thenReturn(rd);

		new ScenariosServlet().doGet(request, response);

		verify(rd).forward(request, response);
		verify(request).getParameter("dispatchActionScenario");
	}

	@Test
	public void ShareProjectTest() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);
		HttpSession session = mock(HttpSession.class);

		DBService db = new DBService();
		Project project = db.getProjectById(new Long(1));
		session.setAttribute("projectId", project);

		when(request.getSession()).thenReturn(session);
		when(request.getParameter("dispatchActionShare")).thenReturn("addShare");
		when(request.getParameter("sharedWith")).thenReturn("testUser2@example.com");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("/share")).thenReturn(rd);


		when(session.getAttribute("projectId")).thenReturn(project);

		new ShareProjectServlet().doGet(request, response);

		verify(rd).forward(request, response);
	}

	@Test
	public void ReportsServletTest() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);       
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter("dispatchActionEstimate")).thenReturn("showProjReceivedDev");
		when(request.getParameter("idEstimate")).thenReturn("1");

		RequestDispatcher rd = mock(RequestDispatcher.class);
		when(request.getRequestDispatcher("projDetailsDev.jsp")).thenReturn(rd);

		new ReportsServlet().doGet(request, response);

		verify(request, times(2)).setAttribute(anyString(), any());
	}
}
