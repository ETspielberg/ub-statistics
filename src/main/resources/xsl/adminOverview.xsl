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
	<xsl:param name="username" />

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:template match="/adminOverview">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->
				<meta name="description" content="" />
				<meta name="author" content="" />
				<link rel="icon" href="img/favicon.ico">
				</link>

				<title>FachRef-Assistent :: Admin</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/dropzone.js"></script>


				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="navbar" />

				<!-- Main jumbotron for a primary marketing message or call to action -->
				<div class="jumbotron">
					<div class="container">
						<h1>FachRef-Assistent Administration</h1>
						<p>Kontrolle über die Einstellungen </p>
					</div>
				</div>
				<div class="container">
					<div class="col-md-6">
						<h2>Nutzer verwalten</h2>
						<p>Nutzern Rollen vergeben, löschen und Passwort setzen.</p>
						<p>
							<a class="btn btn-danger btn" href="{$WebApplicationBaseURL}fachref/admin/userManagement"
								role="button">Go! &#187;</a>
						</p>
					</div>
					<div class="col-md-6">
						<h2>Standortgruppen</h2>
						<p>Gruppen von Standorten definieren.</p>
						<p>
							<a class="btn btn-danger btn" href="{$WebApplicationBaseURL}fachref/admin/Collections.xed"
								role="button">Go! &#187;</a>
						</p>
					</div>
					<div class="col-md-6">
						<h2>Standortgruppenindex</h2>
						<p>Standortgruppen in den Index schreiben.</p>
						<p>
							<a class="btn btn-danger btn" href="{$WebApplicationBaseURL}fachref/admin/buildCollectionIndex"
								role="button">Go! &#187;</a>
						</p>
					</div>
					<div class="col-md-6">
						<h2>Notationenindex</h2>
						<p>Index aus Notationen erstellen.</p>
						<p>
							<a class="btn btn-danger btn" href="{$WebApplicationBaseURL}fachref/admin/buildNotationIndex"
								role="button">Go! &#187;</a>
						</p>
					</div>
				</div>



				<!-- Bootstrap core JavaScript ================================================== -->
				<!-- Placed at the end of the document so the pages load faster -->
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
