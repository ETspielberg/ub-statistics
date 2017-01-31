<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">
	
	<xsl:include href="mabxml-isbd-reduced.xsl" />
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
	<xsl:param name="unidue.ub.statistics.yearsToAverage" />
	<xsl:param name="unidue.ub.statistics.threshold" />

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:variable name="collections">
		<xsl:value-of select="/purchaseList/stockControlProperties/collections/." />
	</xsl:variable>
	<xsl:variable name="systemCode">
		<xsl:value-of select="/purchaseList/stockControlProperties/systemCode/." />
	</xsl:variable>
	<xsl:variable name="currentYear">
		<xsl:value-of select="/purchaseList/@currentYear" />
	</xsl:variable>
	<xsl:variable name="collections">
		<xsl:value-of select="/purchaseList/stockControlProperties/collections/." />
	</xsl:variable>
	<xsl:variable name="yearsOfRequests">
		<xsl:value-of select="/purchaseList/stockControlProperties/yearsOfRequests/." />
	</xsl:variable>
	<xsl:variable name="minimumDaysOfRequest">
		<xsl:value-of select="/purchaseList/stockControlProperties/minimumDaysOfRequest/." />
	</xsl:variable>
	<xsl:variable name="stockControl">
		<xsl:value-of select="/purchaseList/stockControlProperties/stockControl/." />
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

				<title>FachRef-Assistent :: Erwerbung</title>

				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<!--  <script type="text/javascript" src="{$WebApplicationBaseURL}js/dataTables.bootstrap.min.js"></script> -->
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
				
					
				<link href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="purchaseList/navbar" />
				<xsl:apply-templates select="purchaseList" />
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
				
			</body>
		</html>
	</xsl:template>

	<xsl:template match="purchaseList">
		<xsl:choose>
			<xsl:when test="count(analysis) &gt; 1">
				<xsl:call-template name="list" />
			</xsl:when>
		<xsl:otherwise>
		<div class="col-md-8 col-md-offset-2 main">
		<p>Es wurden keine Titel zur Beschaffung gefunden. Geben Sie entweder eine neue Systemstelle ein oder wählen sie ein anderes Profil.</p>
		</div>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
		
	<xsl:template name="list">
		<div class="container-fluid">
		<div class="col-md-12">
	      <div class="alert alert-info" role="alert">
	        <center>Um bibliographische Informationen zu den einzelnen Einträgen zu erhalten, bitte die Maus über die Signatur bewegen.</center> 
	      </div>
		<form action="fachref/profile/sendPurchaseEmail" method="get" target="popup">
			<input type="hidden" name="stockControl" value="{$stockControl}" />
			<table id="sortableTable" class="table table-striped">
				<thead>
					<tr>
						<th>Signatur </th>
						<th>ISBN</th>
						<th>Ausleihprotokoll </th>
						<th>Bestand</th>
						<th>Maximale Anzahl Vormerkungen </th>
						<th>Durchschnittliche Vormerkdauer</th>
						<th>Vorschlag </th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="analysis"/>
			    </tbody>
			</table>
			<input type="submit" class="btn btn-success" value="Bestellen" />
		</form>
		</div>
		</div>
	</xsl:template>

	
	<xsl:template match="analysis">
		<xsl:variable name="shelfmark">
			<xsl:value-of select="@shelfmark" />
		</xsl:variable>
		<xsl:variable name="mab">
			<xsl:value-of select="mab/." />
		</xsl:variable>
			<tr>
				<td  data-toggle="tooltip">
				<xsl:attribute name="title">
				<xsl:value-of select="mab/." />
				</xsl:attribute>
					<xsl:value-of select="$shelfmark" />
				</td>
				<td>
				<a
						href="https://www.amazon.de/s/ref=nb_sb_noss?field-keywords={$mab}"
						target="popup">Suche bei Amazon</a>

				</td>
				
				<td>
					<a
						href="../../ausleihprotokoll?shelfmark={$shelfmark}&amp;collections={$collections}&amp;exact="
						target="popup" data-tooltip="{$shelfmark}">Link</a>
				</td>
				
				<td>
					<xsl:value-of select="lastStock/." />
				</td>
				<td>
					<xsl:value-of select="numberRequest/." />
				</td>
				<td>
					<xsl:value-of select='format-number(totalDaysRequest/. div numberRequests/.,"#0.0")' />
				</td>
				<td>
					<xsl:value-of select="proposedPurchase/." />
				</td>
			</tr>
	</xsl:template>



</xsl:stylesheet>