<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page
	import="com.google.appengine.api.users.User,com.example.model.Project"%>

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
							<li><a href="/">Home</a></li>
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
															<h3>${jobPos}</h3>
														</li>
														<li>
															<h1>${company}</h1>
														</li>
													</ul>
												</div>
											</div>
										</div>
										<div name="response">
											<h3 class="warning">${response}</h3>
											<br>
										</div>
										<div class="actions">
											<h3>Projects created</h3>
											<br>
											<form method="get" action="/main">
												<div name="projects">${projectsList}</div>
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