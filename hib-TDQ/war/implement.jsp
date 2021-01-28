<!-- The template was taken from:
     n33 (n.d.), “Freebies”, http://n33.co/freebies [accessed 20 Jul 2013]
     
     The content of this jsp file was written by Georgios Skourletopoulos.
     
     Created 1 August 2013. Last modified 18 August 2013. -->


<!-- This jsp file displays the corresponding web page when the NewImplementingServlet and ImplementingServlet is called. -->


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
		<div class="title">Cost Estimation for Implementing</div>
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
										<h3>Total cost estimation for Implementing Software as a
											Service in Cloud</h3>
										<div name="response">
											<h3 class="warning">${response}</h3>
											<br>
										</div>
										<form method="get" action="" data-validate="parsley">
											<div>
												<div class="row half">
													<div class="6u">
														<h3>
															<label for="costName">Cost estimation for
																Implementing name</label>
														</h3>
														<input type="text" class="text" name="costName"
															id="contact-name" value="${costNameValue}"
															data-type="alphanum" data-required="true"
															data-maxlength="45" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Weighted priority rating for the Development
															process (in %)</h3>
														<input type="text" class="text" name="devWeight"
															id="contact-name" value="${devWeightValue}"
															data-type="digits" data-required="true" data-min="0"
															data-max="100" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Weighted priority rating for the Configuration
															process (in %)</h3>
														<input type="text" class="text" name="configWeight"
															id="contact-name" value="${configWeightValue}"
															data-type="digits" data-required="true" data-min="0"
															data-max="100" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Weighted priority rating for the Deployment
															process (in %)</h3>
														<input type="text" class="text" name="deployWeight"
															id="contact-name" value="${deplopyWeightValue}"
															data-type="digits" data-required="true" data-min="0"
															data-max="100" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Weighted priority rating for the Licences process
															(in %)</h3>
														<input type="text" class="text" name="licenseWeight"
															id="contact-name" value="${licenseWeightValue}"
															data-type="digits" data-required="true" data-min="0"
															data-max="100" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Weighted priority rating for the Infrastructure
															process (in %)</h3>
														<input type="text" class="text" name="infraWeight"
															id="contact-name" value="${infraWeightValue}"
															data-type="digits" data-required="true" data-min="0"
															data-max="100" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Most likely effort applied (in man-months)</h3>
														<input type="text" class="text" name="effort"
															id="contact-name" value="${effortValue}" data-min="0"
															data-type="number" data-required="true" />
													</div>
												</div>
												<div class="row half">
													<div class="6u">
														<h3>Average monthly salary per employee</h3>
													</div>
												</div>
												<div class="row">
													<div class="6u">
														<input type="text" class="text" name="monthlySalary"
															id="contact-name" value="${monthlySalaryValue}"
															data-min="0" data-type="number" data-required="true" />
													</div>
													<div class="6u">
														<input type="submit" class="button button-style1"
															value="Calculate" id="calcButton"
															onClick="setDispatchAction('calculate');setDispatchActionImplement('calculate')" />
													</div>
												</div>
												<div name="evaluations" id="evaluations"
													style="display:${evaluationsVisibility};">
													<br>
													<div class="row">
														<div class="8u">
															<table class="style1">
																<tr>
																	<th>Cost</th>
																	<th>Optimistic</th>
																	<th>Most likely</th>
																	<th>Pessimistic</th>
																</tr>
																<tr>
																	<th>Development</th>
																	<c:forEach items="${developmentCostValue}" var="cost">
																		<td><fmt:formatNumber type="number"
																				maxFractionDigits="2" value="${cost}" /></td>
																	</c:forEach>
																</tr>
																<tr>
																	<th>Configuration</th>
																	<c:forEach items="${configurationCostValue}" var="cost">
																		<td><fmt:formatNumber type="number"
																				maxFractionDigits="2" value="${cost}" /></td>
																	</c:forEach>
																</tr>
																<tr>
																	<th>Deployment</th>
																	<c:forEach items="${deploymentCostValue}" var="cost">
																		<td><fmt:formatNumber type="number"
																				maxFractionDigits="2" value="${cost}" /></td>
																	</c:forEach>
																</tr>
																<tr>
																	<th>Licenses</th>
																	<c:forEach items="${licensesCostValue}" var="cost">
																		<td><fmt:formatNumber type="number"
																				maxFractionDigits="2" value="${cost}" /></td>
																	</c:forEach>
																</tr>
																<tr>
																	<th>Infrastructure</th>
																	<c:forEach items="${infrastructureCostValue}"
																		var="cost">
																		<td><fmt:formatNumber type="number"
																				maxFractionDigits="2" value="${cost}" /></td>
																	</c:forEach>
																</tr>
																<tr>
																	<th><h3 class="warning">Total</h3></th>
																	<c:forEach items="${totalCostValue}" var="cost">
																		<td><h3 class="warning">
																				<fmt:formatNumber type="number"
																					maxFractionDigits="2" value="${cost}" />
																			</h3></td>
																	</c:forEach>
																</tr>
															</table>
														</div>
													</div>
													<div class="row half">
														<div class="6u">
															<h3>Confidence for estimation</h3>
															<select name="confidence"> ${confEst}
															</select>
														</div>
													</div>
													<div class="row half">
														<div class="6u">
															<h3>Product flexibility</h3>
															<select name="productFlexibility">
																${productFlexibilityList}
															</select>
														</div>
													</div>
													<div class="row half">
														<div class="6u">
															<h3>Market flexibility</h3>
															<select name="marketFlexibility">
																${marketFlexibilityList}
															</select>
														</div>
													</div>
													<div class="row half">
														<div class="6u">
															<h3>Risk of entering into a Technical Debt in the
																future</h3>
															<select name="riskIntoTD"> ${riskIntoTDList}
															</select>
														</div>
													</div>
													<div class="row half">
														<div class="6u">
															<h3>Real options valuation (if Technical Debt tends
																to incur in the future)</h3>
															<select name="realOptions"> ${realOptionsList}
															</select>
														</div>
													</div>
													<div class="row">
														<div class="6u">
															<h3>Justification</h3>
															<textarea rows="5" class="text" name="justification"
																id="contact-name" placeholder="For the Project Manager"
																data-maxlength="450"><c:out
																	value="${justificationValue}" /></textarea>
														</div>
													</div>
													<div class="row">
														<div class="12u">
															<ul class="actions">
																<input type="hidden" name="dispatchAction"
																	id="dispatchAction" />
																<input type="hidden" name="dispatchActionImplement"
																	id="dispatchActionImplement" />
																<li><input type="${submitDisabled}"
																	class="button button-style1" value="Submit"
																	name="submitButton"
																	onClick="setDispatchAction('submit')" /></li>
																<li><input type="${saveDisabled}"
																	class="button button-style1" value="Save changes"
																	name="saveButton"
																	onclick="setDispatchActionImplement('save')" /></li>
																<li><input type="${submitSharedDisabled}"
																	class="button button-style1" value="Submit"
																	name="submtitSharedButton"
																	onclick="setDispatchActionImplement('submitShared')" />
																</li>
																<li><input type="reset"
																	class="button button-style2" value="Reset" /></li>
															</ul>
														</div>
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