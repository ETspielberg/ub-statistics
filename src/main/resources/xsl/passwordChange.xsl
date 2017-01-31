<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:include href="navbar.xsl" />
	
	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes"
		xalan:indent-amount="2" />

	<!-- ============ Parameter von MyCoRe LayoutService ============ -->

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:template match="/">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->

				<title>FachRef-Assistent :: Neuer Nutzer</title>
				<script	src="{$WebApplicationBaseURLjs/jquery.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/jumbotron.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<nav class="navbar navbar-inverse navbar-fixed-top">
					<div class="container-fluid">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle collapsed"
								data-toggle="collapse" data-target="#navbar" aria-expanded="false"
								aria-controls="navbar">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand" href="#">FachRef-Assistent :: Neuer Nutzer</a>
						</div>
						<div id="navbar" class="navbar-collapse collapse">
							<ul class="nav navbar-nav navbar-right">

								<li>
									<a href="../start">Zurück</a>
								</li>
							</ul>
						</div><!--/.navbar-collapse -->
					</div>
				</nav>
				<div class="container">
					<div class="col-md-6 col-md-offset-3">
						<xsl:choose>
							<xsl:when test="passwordChange/@success">
								<h1>Passwort ändern</h1>
								<div class="panel panel-default">
									<div class="panel-heading">
										<h3 class="panel-title">Passwort geändert</h3>
									</div>
									<div class="panel-body">
										<div class="alert alert-success">
											Das Passwort wurde erfolgreich geändert.
										</div>
										<a class="btn btn-success" href="../start" role="button">Zur Startseite &#187;</a>
										<xsl:if test="passwordChange/@userAdmin">
										<a class="btn btn-success" href="../userManagement" role="button">Zum Benutzermanagement &#187;</a>
										</xsl:if>
									</div>
								</div>
							</xsl:when>
							<xsl:otherwise>
								<h1>Passwort ändern</h1>
								<div class="panel panel-default">
									<div class="panel-heading">
										<h3 class="panel-title">Neues Passwort eingeben</h3>
									</div>
									<xsl:apply-templates select="passwordChange/message" />
									<div class="panel-body">
										<form name="userRegistration" action="passwordChange"
											method="POST" accept-charset="UTF-8" role="form">
											<fieldset>
												<div class="form-group">
													<input class="form-control" placeholder="Passwort"
														name="newPassword" type="password" autofocus="true"></input>
												</div>
												<div class="form-group">
													<input class="form-control" placeholder="Passwort wiederholen"
														name="newPasswordCheck" type="password" value=""></input>
												</div>
												<input class="btn btn-lg btn-success btn-block" type="submit"
													value="Passwort ändern"></input>
											</fieldset>
										</form>
									</div>
								</div>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</div>
				<script src="{$WebApplicationBaseURLjs/bootstrap.min.js"></script>
				<script src="{$WebApplicationBaseURLjs/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="message">
		<div class="alert alert-danger">
			<xsl:value-of select="i18n:translate(.)" />
		</div>
	</xsl:template>

</xsl:stylesheet>
