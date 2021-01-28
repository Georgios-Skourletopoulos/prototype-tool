<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the GraphServlet is called. -->


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
<script src="js/parsley.js"></script>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
      google.load('visualization', '1', {packages: ['corechart', 'table']});
    </script>
<script type="text/javascript">

function drawVisualization() {
  // Create and populate the data table.
  var data = google.visualization.arrayToDataTable([${table}]);

  // Create and draw the visualization.
  new google.visualization.LineChart(document.getElementById('visualization')).
      draw(data, {
		  		  title: 'Technical Debt quantification comparison',
				  titlePosition: 'out',
		  		  curveType: "none",
                  width: 800, height: 600}
          );
		  
	 visualization = new google.visualization.Table(document.getElementById('table'));
      visualization.draw(data, null);
}
google.setOnLoadCallback(drawVisualization);

</script>
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
		<div class="title">Technical Debt for Leasing comparison</div>
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
										<h3>Technical Debt estimates for Leasing a Cloud Software
											as a Service</h3>
										<br>
										<h3 class="warning">${response}</h3>

										<form method="get" action="/graph" data-validate="parsley">
											<input type="hidden" id="dispatchAction"
												name="dispatchAction">
											<div class="row half">
												<div class="6u">
													<c:forEach items="${estimations}" var="estimation">
														<input type="checkbox" name="tdInBuying"
															value="${estimation.name}" data-mincheck="2" />
                          ${estimation.name}</br>
													</c:forEach>
												</div>
											</div>
											<div class="row half">
												<div class="6u">
													<input type="submit" class="button button-style1"
														value="Create Graph" name="graphButton"
														onclick="setDispatchAction('create')" ${disableButton} />
												</div>
											</div>
										</form>
										<div style="display:${display}">
											<div class="row half">
												<div class="10u">
													<div id="visualization"
														style="width: 800px; height: 600px;"></div>
												</div>
											</div>
											<div class="row">
												<div class="8u">
													<div id="table"></div>
												</div>
											</div>
											<div class="row">
												<div class="12u">
													<table class="style1">
														<tr>
															<th>Estimation name</th>
															<th>Max capacity</th>
															<th>Variation in<br> demand
															</th>
															<th>Monthly<br> subscription
															</th>
															<th>Variation in<br> subscription
															</th>
															<th>Monthly cost<br> in Cloud
															</th>
															<th>Variation in<br> Cloud cost
															</th>
															<th>Service<br> scalability
															</th>
															<th>QoS</th>
														</tr>
														<c:forEach items="${selectedEstimates}" var="estimation">
															<tr>
																<td>${estimation.name}</td>
																<td>${estimation.maxCapacity}</td>
																<td><fmt:formatNumber type="number"
																		maxFractionDigits="2"
																		value="${estimation.demandRaise}" /></td>
																<td><fmt:formatNumber type="number"
																		maxFractionDigits="2"
																		value="${estimation.subscriptionPrice}" /></td>
																<td><fmt:formatNumber type="number"
																		maxFractionDigits="2"
																		value="${estimation.raiseSubscriptionPrice}" /></td>
																<td><fmt:formatNumber type="number"
																		maxFractionDigits="2" value="${estimation.cloudCost}" /></td>
																<td><fmt:formatNumber type="number"
																		maxFractionDigits="2"
																		value="${estimation.raiseCloudCost} " /></td>
																<td>${estimation.serviceScalability}</td>
																<td>${estimation.qoS}</td>
															</tr>
														</c:forEach>
													</table>
												</div>
											</div>
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