<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the ShowProjectServlet is called. -->


<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.sql.*" errorPage=""%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.google.appengine.api.users.User"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Technical Debt in Cloud</title>
<meta name="description" content="" />
<meta name="keywords" content="" />
<link
	href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:400,400italic,700,900"
	rel="stylesheet" />
<script src="js/jquery.min.js"></script>
<script src="js/jquery.dropotron.js"></script>
<script src="js/config.js"></script>
<script src="js/skel.min.js"></script>
<script src="js/skel-panels.min.js"></script>
<script src="js/myFunctions.js"></script>
<noscript>
	<link rel="stylesheet" href="css/skel-noscript.css" />
	<link rel="stylesheet" href="css/style.css" />
	<link rel="stylesheet" href="css/style-desktop.css" />
</noscript>
<!--[if lte IE 8]><script src="js/html5shiv.js"></script><link rel="stylesheet" href="css/ie8.css" /><![endif]-->
</head>
<body class="right-sidebar">

	<!-- Header Wrapper -->
	<div id="header-wrapper" class="wrapper">
		<div class="container">
			<div class="row">
				<div class="12u">

					<!-- Header -->
					<div id="header">

						<!-- Logo -->
						<div id="logo">
							<h1>
								<a href="#">Technical Debt in Cloud</a>
							</h1>
						</div>
						<!-- /Logo -->

						<!-- Nav -->
						<nav id="nav" style="text-align:right">
						<ul>
							<li><a href="/mainPM">Home</a></li>
							<li><a href="/profile">Edit profile</a></li>
							<li><a href="/project">Create new project</a> <!-- <form action="/project" method="get"><input type="hidden" name="dispatchAction" id="dispatchAction"><input type="submit" value="Create new project" onClick="setDispatchAction('new')"/></form> --></li>
							<li><span><%=((User) session.getAttribute("userDetails"))
					.getNickname()%></span>
								<ul>
									<li><a href="<%=session.getAttribute("logoutUrl")%>">Logout</a></li>
								</ul></li>
						</ul>
						</nav>
						<!-- /Nav -->

					</div>
					<!-- /Header -->

				</div>
			</div>
		</div>
	</div>
	<!-- /Header Wrapper -->

	<!-- Main Wrapper -->
	<div class="wrapper wrapper-style3">
		<div class="title">${project.projectName}</div>
		<div class="container">
			<div class="row">
				<div class="12u">

					<!-- Main -->
					<div id="main">
						<div>
							<div class="row">
								<div class="8u skel-cell-mainContent">

									<!-- Content -->
									<div id="content">
										<article class="is is-post">
										<table class="style1">
											<tr>
												<th>Project name</th>
												<td>${project.projectName}</td>
											</tr>
											<tr>
												<th>Project category</th>
												<td>${project.projectCategory}</td>
											</tr>
											<tr>
												<th colspan="2">Project goals</th>
											</tr>
											<tr>
												<td colspan="2"><c:out value="${project.projectGoals}" /></td>
											</tr>
											<tr>
												<th>Project start date</th>
												<td>${project.projectStart}</td>
											</tr>
											<tr>
												<th>Project completion date</th>
												<td>${project.projectEnd}</td>
											</tr>
										</table>
										</article>
										<article class="is is-post"> <c:forEach
											items="${scenarioList}" var="scenarios">
											<table class="style1">
												<tr>
													<th>Scenario name</th>
													<td>${scenarios.scenarioName}</td>
												</tr>
												<tr>
													<th>Scenario type</th>
													<td>${scenarios.scenarioType}</td>
												</tr>
												<tr>
													<th>Requirement type</th>
													<td>${scenarios.requirementType}</td>
												</tr>
												<tr>
													<th>Priority</th>
													<td>${scenarios.priority}</td>
												</tr>
											</table>
										</c:forEach> </article>
									</div>
									<!-- /Content -->

								</div>
								<div class="4u">

									<!-- Sidebar -->
									<div id="sidebar" style="display:${sidebar}">
										<section class="is"> <header>
										<h2>Actions</h2>
										</header>
										<ul class="style2">
											<li>
												<form method="get" action="/project">
													<input type="hidden" name="dispatchActionProject"
														id="dispatchActionProject" />
													<div style="display:${visibility}">
														<div class="row">
															<div class="4u">
																<input type="submit" value="Edit project"
																	class="button button-style3 button-small"
																	onclick="setDispatchActionProject('edit')" />
															</div>
														</div>
													</div>
													<br />
													<div class="row">
														<div class="4u">
															<input type="submit" value="Delete project"
																class="button button-style3 button-small"
																onclick="setDispatchActionProject('delete')" />
														</div>
													</div>
													<br />
													<div style="display:${submitButton}">
														<div class="row">
															<div class="4u">
																<input type="submit" value="Submit project"
																	class="button button-style3 button-small"
																	onclick="setDispatchActionProject('showShared')" />
															</div>
														</div>
													</div>
												</form>
											</li>
											<li>
												<form method="get" action="/scenarios">
													<input type="hidden" name="dispatchActionScenario"
														id="dispatchActionScenario" />
													<div style="display:${visibility}">
														<div class="row">
															<div class="4u">
																<input type="submit" value="Add scenario"
																	class="button button-style3 button-small"
																	onclick="setDispatchActionScenario('new')" />
															</div>
														</div>
														<div class="row">
															<div class="4u">
																<input type="submit" value="View scenarios"
																	class="button button-style3 button-small"
																	onclick="setDispatchActionScenario('${view}')" />
															</div>
														</div>
													</div>
												</form>
											</li>
											<li>
												<form method="get" action="/share">
													<div style="display:${visibility}">
														<input type="hidden" name="dispatchAction"
															id="dispatchAction" />
														<div class="row">
															<div class="4u">
																<input type="submit" value="Share"
																	class="button button-style3 button-small" />
															</div>
														</div>
													</div>
												</form>
											</li>
										</ul>
										</section>
									</div>
									<!-- /Sidebar -->

								</div>
							</div>
						</div>
					</div>
					<!-- /Main -->

				</div>
			</div>
		</div>
	</div>
	<!-- /Main Wrapper -->

	<!-- Footer Wrapper -->
	<div id="footer-wrapper" class="wrapper">
		<div class="title"></div>
		<div class="container">
			<div class="row">
				<div class="12u">

					<!-- Copyright -->
					<div id="copyright">
						<span> Copyright &copy; 2013 by Georgios Skourletopoulos.
							All rights reserved. </span>
					</div>
					<!-- /Copyright -->

				</div>
			</div>
		</div>
	</div>
	<!-- /Footer Wrapper -->
</body>
</html>