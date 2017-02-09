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

				<title>FachRef-Assistent :: e-Journals :: Zeitschriftenpakete</title>

				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/dropzone.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
			
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="journalTitleManagement/navbar" />
				<div class="jumbotron">
				<div class="container">
				<h1>Paketinhalt</h1>
				<p>Im Paket <xsl:value-of select="@anchor" /> enthaltene Zeitschriften (lt. EZB)</p>
				</div>
				</div>
				<div class="container">
				<xsl:apply-templates select="journalTitleManagement" />
				</div>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="journalTitleManagement">
		<xsl:choose>
		<xsl:when test="journalTitle">
		<div class="table-responsive">
			<table id="sortableTable" class="table table-striped">
				<thead>
					<tr>
						<th>
						Name
						</th>
						<th>
							Fach
						</th>
						<th>
						  Typ
						</th>
						<th>
						  Aktionen
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="journalTitle" />
				</tbody>
			</table>
		</div>
		</xsl:when>
		<xsl:otherwise>
		Es wurden keine Zeitschriften in dem Paket gefunden. Bitte ein gültiges Paket angeben.
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="journalTitle">
		<xsl:variable name="issn">
			<xsl:value-of select="issn/." />
		</xsl:variable>
		<tr>
			<th>
				<xsl:value-of select="name/." />
			</th>
			<th>
				<xsl:value-of select="subject/." />
			</th>
			<th>
				<xsl:value-of select="type/." />
			</th>
			<th>
			<a class="btn btn-sm btn-success" href="{$WebApplicationBaseURL}fachref/journals/journalUsage?issn={$issn}"
					role="button" target="popup">Zur Nutzung</a>
			</th>
		</tr>
	</xsl:template>
	
</xsl:stylesheet>