<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the ScenariosServlet is called. -->


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.google.appengine.api.users.User"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
	Escape Velocity 2.0 by HTML5 UP
	html5up.net | @n33co
	Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
-->
<html>
<head>
<title>Technical Debt in Cloud</title>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
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
<script src="js/parsley.js"></script>
<noscript>
	<link rel="stylesheet" href="css/skel-noscript.css" />
	<link rel="stylesheet" href="css/style.css" />
	<link rel="stylesheet" href="css/style-desktop.css" />
</noscript>
<!--[if lte IE 8]><script src="js/html5shiv.js"></script><link rel="stylesheet" href="css/ie8.css" /><![endif]-->
</head>
<body class="no-sidebar">

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
							<li><a href="/project">Create new project</a></li>
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
		<div class="title">Scenario Details</div>
		<div class="container">
			<div class="row">
				<div class="12u">

					<!-- Main -->
					<div id="main">
						<div>
							<div class="row">
								<div class="12u skel-cell-mainContent">

									<!-- Content -->
									<div id="content">
										<div name="response">
											<h3 class="warning">${response}</h3>
											<br>
										</div>
										<form method="get" action="/scenarios" data-validate="parsley">
											<div>
												<div class="row half">
													<div class="6u">
														<h3>Scenario name</h3>
														<input type="text" class="text" name="scenarioName"
															id="contact-name" value="${scenarioNameValue}"
															data-type="alphanum" data-required="true"
															data-maxlength="45" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Scenario type</h3>
														<select name="scenarioType"> ${scenarioType}
														</select>
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Requirement type</h3>
														<select name="requirementType">
															${requirementType}
														</select>
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Priority</h3>
														<select name="priority"> ${priority}
														</select>
													</div>
												</div>
												<div class="row">
													<div class="12u">
														<ul class="actions">
															<input type="hidden" name="dispatchActionScenario"
																id="dispatchActionScenario" />
															<li><input class="button button-style1"
																value="Submit" name="submitButton"
																type="${submitHidden}"
																onClick="setDispatchActionScenario('submit')" /></li>
															<li><input class="button button-style1"
																value="Save changes" name="saveButton"
																type="${saveHidden}"
																onclick="setDispatchActionScenario('save')" /></li>
															<li><input type="reset" class="button button-style2"
																value="Reset" /></li>
														</ul>
													</div>
												</div>
											</div>
										</form>
									</div>
								</div>
								<!-- /Content -->

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