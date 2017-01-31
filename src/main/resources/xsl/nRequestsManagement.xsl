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


	<xsl:variable name="user">
		<xsl:value-of select="/nRequestsManagement/@user" />
	</xsl:variable>


	<xsl:template match="/">
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

				<title>FachRef-Assistent :: Hitlisten</title>

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
				<xsl:apply-templates select="nRequestsManagement/navbar" />
				<div class="jumbotron">
					<div class="container">
						<h1>Hitlisten</h1>
						<p>Die am stärksten vorgemerkten Titel je Bereich</p>
						<a class="btn btn-primary" href="{$WebApplicationBaseURL}fachref/hitlists/RequestsAlert_Form.xed"
							role="button">Neues Profil erstellen</a>
					</div>
				</div>
				<xsl:apply-templates select="nRequestsManagement" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="nRequestsManagement">
		<xsl:if test="count(alerts/alert) &gt; 0">
			<div class="container">
				<xsl:apply-templates select="alerts" />
			</div>
			<div class="container">
				<a class="btn btn-primary" href="{$WebApplicationBaseURL}fachref/hitlists/RequestsAlert_Form.xed"
					role="button">Neues Profil erstellen</a>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template match="alerts">
		<h2 class="sub-header">Eingerichtete Alerts</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>
							Name
						</th>
						<th>
							Bereich
						</th>
						<th>
							Aktiv?
						</th>
						<th>
							Link
						</th>
						<th>
							Bearbeiten
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="alert" />
				</tbody>
			</table>
		</div>
	</xsl:template>


	<xsl:template match="alert">
		<xsl:variable name="alertControl">
			<xsl:value-of select="alertControl" />
		</xsl:variable>
		<xsl:variable name="performAlert">
			<xsl:value-of select="performAlert/." />
		</xsl:variable>
		<tr>
			<th>
				<xsl:value-of select="name/." />
			</th>
			<th>
				<xsl:value-of select="notationRange/." />
			</th>
			<th>
				<xsl:choose>
					<xsl:when test="$performAlert = 'true'">
						<xsl:text> X </xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> - </xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<th>
				<a class="btn btn-sm btn-success"
					href="{$WebApplicationBaseURL}fachref/hitlists/nRequests?readerControl={$alertControl}"
					role="button" target="popup">zur Hitliste</a>
			</th>
			<th>
				<span class="links">
					<a class="btn btn-sm btn-warning"
						href="{$WebApplicationBaseURL}fachref/hitlists/RequestsAlert_Form.xed?id={$alertControl}"
						role="button">
						<span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
					</a>
				</span>
				<span class="links">
					<a class="btn btn-sm btn-danger"
						href="{$WebApplicationBaseURL}fachref/hitlists/alertDelete?alertControl={$alertControl}"
						role="button">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
					</a>
				</span>
			</th>
		</tr>
	</xsl:template>



</xsl:stylesheet>