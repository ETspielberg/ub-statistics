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


	<xsl:variable name="page.title">
		<xsl:text>Bestandspflege - Management von</xsl:text>
		<xsl:value-of select="/nRequestsManagement/@username" />
	</xsl:variable>
	
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

				<title>FachRef-Assistent :: e-Journals :: SUSHI Accounts</title>

				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/dropzone.js"></script>


				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="publisherManagement/navbar" />
				<div class="jumbotron">
				<div class="container">
				<h1 >SUSHI Accounts</h1>
				<p>Eingerichtete SUSHI-Provider.</p>
				<p><a class="btn btn btn-primary" href="{$WebApplicationBaseURL}fachref/eMedia/PublisherDefine.html" role="button">Neues Profil erstellen</a>
				</p>
				</div>
				</div>
				<div class="container">
				<xsl:apply-templates select="publisherManagement" />
				</div>
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="publisherManagement">
		<xsl:if test="count(publisher) &gt; 0">
		
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>
							Name
						</th>
						<th>
							SUSHI-URL
						</th>
						<th>
						  RequestorID
						</th>
						<th>
						  Customer Reference ID
						</th>
						<th>
						  Aktionen
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="publisher" />
				</tbody>
			</table>
		</div>
		</xsl:if>
		<!-- 
		<xsl:if test="count(alerts/alert) &gt; 0">
			<xsl:apply-templates select="alerts" />
		</xsl:if> -->
	</xsl:template>


	<xsl:template match="publisher">
		<xsl:variable name="name">
			<xsl:value-of select="name/." />
		</xsl:variable>
		<tr>
			<th>
				<xsl:value-of select="name/." />
			</th>
			<th>
				<xsl:value-of select="sushiURL/." />
			</th>
			<th>
				<xsl:value-of select="sushiRequestorID/." />
			</th>
			<th>
				<xsl:value-of select="sushiCustomerReferenceID/." />
			</th>
			<th>
				<a class="btn btn-sm btn-success" href="{$WebApplicationBaseURL}fachref/eMedia/publisherDelete?name={$name}"
					role="button">Löschen</a>
			</th>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>