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
				<meta name="description" content="" />
				<meta name="author" content="" />
				<link rel="icon" href="img/favicon.ico">
				</link>

				<title>FachRef-Assistent :: Blacklist</title>

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
				<xsl:apply-templates select="ignoredManagement/navbar" />
				<xsl:apply-templates select="ignoredManagement" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="ignoredManagement">
		<div class="jumbotron">
			<div class="container">
				<h1>Blackliste</h1>
				<p>Diese Titel sind von der weiteren Analyse und Aussonderung
					ausgeschlossen:</p>
			</div>
		</div>
		<xsl:choose>
			<xsl:when test="count(/ignoredManagement/ignored) &gt; 0">

				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>
									Art
								</th>
								<th>
									Systemkennung / Grundsignatur
								</th>
								<th>
									Signatur
								</th>
								<th>
									Kommentar
								</th>
								<th>
									Datum Aufnahme
								</th>
								<th>
									Datum Ende
								</th>
								<th>
									Löschen
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="ignored" />
						</tbody>
					</table>
				</div>
			</xsl:when>
			<xsl:otherwise>
				Es wurden keine Einträge auf der Blacklist gefunden.
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="ignored">
		<xsl:variable name="identifier">
			<xsl:value-of select="identifier/." />
		</xsl:variable>

		<tr>
			<th>
				<xsl:value-of select="i18n:translate(type/.)" />
			</th>
			<th>
				<xsl:value-of select="identifier/." />
			</th>
			<th>
				<xsl:value-of select="shelfmark/." />
			</th>
			<th>
				<xsl:value-of select="comment/." />
			</th>
			<th>
				<xsl:value-of select="timestamp/." />
			</th>
			<th>
				<xsl:value-of select="expire/." />
			</th>
			<th>
				<span class="links">
					<a class="btn btn-sm btn-danger"
						href="{$WebApplicationBaseURL}fachref/profile/ignoredDelete?identifier={$identifier}"
						role="button">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
					</a>
				</span>
			</th>
		</tr>

	</xsl:template>
</xsl:stylesheet>