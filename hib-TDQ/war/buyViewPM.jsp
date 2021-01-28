<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the ReportsServlet is called. -->


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
		<div class="title">Technical Debt Estimation for Leasing</div>
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
										<h3>Technical Debt estimation for Leasing a Cloud
											Software as a Service Details</h3>
										<br>
										<table class="style1">
											<tr>
												<th>Technical Debt estimation for Leasing name</th>
												<td>${TDinBuyingShared.name}</td>
											</tr>
											<tr>
												<th>Years of Return on Investment (ROI)</th>
												<td>${TDinBuyingShared.roi}</td>
											</tr>
											<tr>
												<th>Maximum capacity of service (in users)</th>
												<td>${TDinBuyingShared.maxCapacity}</td>
											</tr>
											<tr>
												<th>Current users of service</th>
												<td>${TDinBuyingShared.currentUsers}</td>
											</tr>
											<tr>
												<th>Average variation in demand per year (in %)</th>
												<td><fmt:formatNumber type="number"
														maxFractionDigits="2"
														value="${TDinBuyingShared.demandRaise}" /></td>
											</tr>
											<tr>
												<th>Price per monthly subscription (in monetary units)</th>
												<td><fmt:formatNumber type="number"
														maxFractionDigits="2"
														value="${TDinBuyingShared.subscriptionPrice}" /></td>
											</tr>
											<tr>
												<th>Average variation in monthly subscription price for
													the declared years of ROI (in %)</th>
												<td><fmt:formatNumber type="number"
														maxFractionDigits="2"
														value="${TDinBuyingShared.raiseSubscriptionPrice}" /></td>
											</tr>
											<tr>
												<th>Estimated monthly cost for servicing a user in
													Cloud (in monetary units)</th>
												<td><fmt:formatNumber type="number"
														maxFractionDigits="2"
														value="${TDinBuyingShared.cloudCost}" /></td>
											</tr>
											<tr>
												<th>Average variation in the monthly cost for servicing
													a user in Cloud for the declared years of ROI (in %)</th>
												<td><fmt:formatNumber type="number"
														maxFractionDigits="2"
														value="${TDinBuyingShared.raiseCloudCost}" /></td>
											</tr>
											<c:forEach items="${TD}" var="year" varStatus="status">
												<tr>
													<td colspan="2"><h3 class="warning">
															Year
															<c:out value="${status.count}" />
															- Technical Debt quantification (in monetary units):
															<fmt:formatNumber type="number" maxFractionDigits="2"
																value="${year}" />
														</h3></td>
												</tr>
											</c:forEach>
											<tr>
												<th>Confidence for estimation</th>
												<td>${TDinBuyingShared.confidence}</td>
											</tr>
											<tr>
												<th>Service's scalability / Market flexibility</th>
												<td>${TDinBuyingShared.serviceScalability}</td>
											</tr>
											<tr>
												<th>Quality of Service (QoS) according to
													non-functional requirements (i.e. availability,
													performance, etc.)</th>
												<td>${TDinBuyingShared.qoS}</td>
											</tr>
											<tr>
												<th>Risk of entering into a Technical Debt in the
													future</th>
												<td>${TDinBuyingShared.riskOfFutureTD}</td>
											</tr>
											<tr>
												<th>Real options valuation (if Technical Debt tends to
													incur in the future)</th>
												<td>${TDinBuyingShared.realOptionsValuation}</td>
											</tr>
											<tr>
												<th colspan="2">Justification</th>
											</tr>
											<tr>
												<td colspan="2"><c:out
														value="${TDinBuyingShared.justification}" /></td>
											</tr>
										</table>
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