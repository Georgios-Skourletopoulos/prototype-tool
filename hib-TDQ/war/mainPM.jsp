<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the MainPMServlet is called. -->


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
		<div class="title">Summary page</div>
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
										<div>
											<div class="row half">
												<div class="6u">
													<ul>
														<li>
															<h1><%=((User) session.getAttribute("userDetails"))
					.getNickname()%></h1>
														</li>
														<li>
															<h1>${jobPos}</h1>
														</li>
														<li>
															<h1>${company}</h1>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<div class="actions">
											<form method="get" action="/showProject">
												<h3>Projects created</h3>
												<br>
												<div name="response">${noProjs}</div>
												<input type="hidden" id="dispatchAction"
													name="dispatchAction" /> <input type="hidden" id="setId"
													name="setId" />
												<c:forEach items="${projectsList}" var="projects">
													<div class="row">
														<div class="8u">
															${projects.projectName} <input type="submit"
																value="Show project details"
																class="button button-custom button-summary"
																onClick="setDispatchAction('showProj');setid('${projects.id}')" />
														</div>
													</div>
												</c:forEach>
										</div>
										<h3>Projects shared</h3>
										<br>
										<div name="response">${noSharedProjs}</div>
										<c:forEach items="${sharedList}" var="sharedProjects">
											<div class="row">
												<div class="8u">
													${sharedProjects.projectName} <input type="submit"
														value="Show project details"
														class="button button-custom button-summary"
														onClick="setDispatchAction('showProj');setid('${sharedProjects.id}')" />
												</div>
											</div>
										</c:forEach>
									</div>
									<br>
									<h3>Projects received</h3>
									<br>
									<div name="response">${noProjReceived}</div>
									<c:forEach items="${receivedProjectsList}"
										var="projectsReceived">
										<div class="row">
											<div class="3u">
												${projectsReceived.projectId.projectName}</div>
											<div class="3u" style="font-style: italic">From
												${projectsReceived.fromUserId.email}</div>
											<div class="4u">
												<input type="submit" value="View project details"
													class="button button-custom button-summary"
													onClick="setDispatchAction('showProjReceived');setid('${projectsReceived.projectId.id}')" />
											</div>
										</div>
									</c:forEach>
									</form>
									<form method="get" action="/reports">
										<input type="hidden" id="dispatchActionEstimate"
											name="dispatchActionEstimate" /> <input type="hidden"
											id="idEstimate" name="idEstimate" /> <br> <br>
										<h3>COCOMO estimates received</h3>
										<br>
										<div name="response">${noCocomoReceived}</div>
										<c:forEach items="${cocomoReceived}" var="cocomo">
											<div class="row">
												<div class="3u">${cocomo.cocomoId.name}</div>
												<div class="3u" style="font-style: italic">From
													${cocomo.fromUserId.email}</div>
												<div class="4u">
													<input type="submit" value="Delete estimation"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('delReceivedCocomo');setIdEstimate('${cocomo.cocomoId.id}')" />
													<input type="submit" value="View estimation details"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('showSharedCocomoPM');setIdEstimate('${cocomo.cocomoId.id}')" />
												</div>
											</div>
										</c:forEach>
										<br> <br>
										<h3>Cost estimates for Implementing Software as a Service
											in Cloud received</h3>
										<br>
										<div name="response">${noImplementReceived}</div>
										<c:forEach items="${implementReceived}" var="implement">
											<div class="row">
												<div class="3u">${implement.implementingId.name}</div>
												<div class="3u" style="font-style: italic">From
													${implement.fromUserId.email}</div>
												<div class="4u">
													<input type="submit" value="Delete estimation"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('delReceivedImplement');setIdEstimate('${implement.implementingId.id}')" />
													<input type="submit" value="View estimation details"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('showSharedImplementPM');setIdEstimate('${implement.implementingId.id}')" />
												</div>
											</div>
										</c:forEach>
										<br> <br>
										<h3>Technical Debt estimates for Leasing a Cloud Software
											as a Service received</h3>
										<br>
										<div name="response">${noBuyingReceived}</div>
										<c:forEach items="${buyingReceived}" var="buying">
											<div class="row">
												<div class="3u">${buying.buyingId.name}</div>
												<div class="3u" style="font-style: italic">From
													${buying.fromUserId.email}</div>
												<div class="4u">
													<input type="submit" value="Delete estimation"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('delReceivedBuy');setIdEstimate('${buying.buyingId.id}')" />
													<input type="submit" value="View estimation details"
														class="button button-custom button-summary"
														onClick="setDispatchActionEstimate('showSharedBuyPM');setIdEstimate('${buying.buyingId.id}')" />
												</div>
											</div>
										</c:forEach>
									</form>

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