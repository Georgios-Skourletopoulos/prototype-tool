<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the CocomoServlet is called. -->


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
							<li><a href="/main">Home</a></li>
							<li><a href="/profile">Edit profile</a></li>
							<li><a href="/graph">Create Graph</a></li>
							<li><span>Create new estimates</span>
								<ul>
									<li><a href="/cocomoNew">Cocomo estimates</a></li>
									<li><span>Technical Debt Estimates</span>
										<ul>
											<li><a href="/implementNew">Cost estimates for
													implementing</a></li>
											<li><a href="/buyNew">Technical debt quantifications
													for leasing</a></li>
										</ul></li>
								</ul></li>
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
		<div class="title">COCOMO Estimation</div>
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
										<h3>Software development cost, effort and schedule
											estimation with Basic COCOMO &reg; 81 model</h3>
										<div name="response">
											<h3 class="warning">${response}</h3>
											<br>
										</div>

										<form method="get" action="/cocomo" data-validate="parsley">
											<h3>COCOMO estimation details</h3>
											<br>
											<table class="style1">
												<tr>
													<th>COCOMO estimation name</th>
													<td><input type="text" class="text" name="cocomoName"
														id="contact-name" value="${cocomoShared.name}"
														data-type="alphanum" data-required="true"
														data-maxlength="45" /></td>
												</tr>
												<tr>
													<th>Software development mode</th>
													<td>${cocomoShared.developmentMode}</td>
												</tr>
												<tr>
													<th>Product size in KLOC</th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${cocomoShared.productSize}" /></td>
												</tr>
												<tr>
													<th><h3 class="warning">Optimistic effort applied</h3></th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${optEffort}" /></td>
												</tr>
												<tr>
													<th><h3 class="warning">Most likely effort
															applied</h3></th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${effort}" /></td>
												</tr>
												<tr>
													<th><h3 class="warning">Pessimistic effort
															applied</h3></th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${pesEffort}" /></td>
												</tr>
												<tr>
													<th>Development time</th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${development}" /></td>
												</tr>
												<tr>
													<th>People required</th>
													<td><fmt:formatNumber type="number"
															maxFractionDigits="2" value="${people}" /></td>
												</tr>
												<tr>
													<th>Confidence for estimation</th>
													<td>${cocomoShared.confidence}</td>
												</tr>
												<tr>
													<th colspan="2">Justification</th>
												</tr>
												<tr>
													<td colspan="2"><c:out
															value="${cocomoShared.justification}" /></td>
												</tr>
											</table>
											<div class="row">
												<div class="12u">
													<ul class="actions">
														<input type="hidden" name="dispatchActionCocomo"
															id="dispatchActionCocomo" />
														</li>
														<input type="${submitSharedDisabled}"
															class="button button-style1" value="Submit"
															name="submtitSharedButton"
															onclick="setDispatchActionCocomo('submitShared')" />
														<li><input type="reset" class="button button-style2"
															value="Reset" /></li>
													</ul>
												</div>
											</div>
										</form>
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