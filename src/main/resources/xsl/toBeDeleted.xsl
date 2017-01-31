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

				<title>Aussonderungsliste</title>

				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}/js/dataTables.bootstrap.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
					
				<link href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
			<xsl:apply-templates select="toBeDeleted/navbar" />
    <xsl:apply-templates select="toBeDeleted" />
    <script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
    </body>
    </html>
  </xsl:template>

<xsl:template match="toBeDeleted">
<div class="col-sm-9 col-md-10 col-md-offset-1 main">
			<h2 class="sub-header">Finale Aussonderungsliste</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>Signatur </th>
						<th>Standort </th>
					
						<th>Anzahl </th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="deletion" />
				</tbody>
			</table>
			</div>
			</div>
	</xsl:template>
	
	
	<xsl:template match ="deletion">
	<tr>
	<td>
					<xsl:value-of select="shelfmark/." />
				</td>
				<td>
					<xsl:value-of select="collections/."/>
				</td>
				<td>
					<xsl:apply-templates select="number/." />
				</td>
				</tr>
	</xsl:template>
	
</xsl:stylesheet>
