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

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:variable name="collections">
		<xsl:value-of select="/deletionList/stockControlProperties/collections/." />
	</xsl:variable>
	<xsl:variable name="systemCode">
		<xsl:value-of select="/deletionList/stockControlProperties/systemCode/." />
	</xsl:variable>
	<xsl:variable name="collections">
		<xsl:value-of select="/deletionList/stockControlProperties/collections/." />
	</xsl:variable>
	<xsl:variable name="minimumYears">
		<xsl:value-of select="/deletionList/stockControlProperties/minimumYears/." />
	</xsl:variable>
	<xsl:variable name="yearsToAverage">
		<xsl:value-of select="/deletionList/stockControlProperties/yearsToAverage/." />
	</xsl:variable>
	<xsl:variable name="stockControl">
		<xsl:value-of select="/deletionList/stockControlProperties/stockControl/." />
	</xsl:variable>
	<xsl:variable name="threshold">
		<xsl:value-of select="/deletionList/stockControlProperties/threshold/." />
	</xsl:variable>

	<xsl:variable name="page.title">
		<xsl:text>Ausleihanalyse für </xsl:text>
		<xsl:value-of select="/document/eventAnalysis/stockControlProperties/stockControl/." />
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

				<title>FachRef-Assistent :: csv-Upload</title>

				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/dropzone.js"></script>
				
					
				<link href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="csvupload/navbar" />
				<xsl:apply-templates select="csvupload" />
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
				<script src="../js/jquery-dropzone.js"></script>
    		</body>
		</html>
	</xsl:template>
	
	<xsl:template match="csvupload">
		<xsl:choose>
		<xsl:when test="count(analysis) &gt; 0">
		<xsl:call-template name="list" />
		</xsl:when>
		<xsl:otherwise>
		<div class="col-md-8 col-md-offset-2 main">
		<p>Es wurden keine Titel zur Aussonderung gefunden.</p>
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
		
			<form action="DeletionList.xed" method="get" target="popup">
				<input type="hidden" name="stockControl" value="{$stockControl}" />
				<table id="sortableTable" class="table table-striped">
					<thead>
						<tr>
							<th>Signatur </th>
							<th>Standort </th>
							<th>Protokoll </th>
							<th>Mittlere Ausleihe </th>
							<th>Trend /Jahr </th>
							<th>Bestand </th>
							<th>Maximale Ausleihe </th>
							<th>Vorschlag </th>
							<th>Kommentar</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="analysis"/>
					</tbody>
				</table>
				<input type="submit" class="btn btn-danger" value="zur Aussonderung" />
			</form>
		</div>
		</div>
	</xsl:template>
	
	<xsl:template match="analysis">
			<tr>
				<xsl:variable name="shelfmark">
					<xsl:value-of select="@shelfmark" />
				</xsl:variable>
				<xsl:variable name="propDel">
					<xsl:value-of select="proposedDeletion/." />
				</xsl:variable>
				<td  data-toggle="tooltip">
				<xsl:attribute name="title">
				<xsl:value-of select="mab/." />
				</xsl:attribute>
					<xsl:value-of select="$shelfmark" />
				</td>
				<td>
					<xsl:value-of select="$collections"></xsl:value-of>
				</td>
				<td>
					<a
						href="../../protokoll?shelfmark={$shelfmark}&amp;collections={$collections}&amp;exact="
						target="popup">Link</a>
				</td>
				<td>
					<xsl:apply-templates select="meanRelativeLoan" />
				</td>
				<td>
					<xsl:value-of select='format-number(@trend, "#%")' />
				</td>
				<td>
					<xsl:apply-templates select="lastStock" />
				</td>
				<td>
					<xsl:apply-templates select="maxLoansAbs" />
				</td>
				<td>
					<xsl:value-of select="$propDel" />
				</td>
				<td>
					<xsl:apply-templates select="comment" />
				</td>
			</tr>
	</xsl:template>


	<xsl:template match="meanRelativeLoan">
		<xsl:value-of select='format-number(., "#%")' />
	</xsl:template>


	<xsl:template match="maxRelativeLoan">
		<xsl:value-of select='format-number(., "#%")' />
	</xsl:template>


	<xsl:template match="lastStock">
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template match="comment">
		<xsl:value-of select="." />
	</xsl:template>




</xsl:stylesheet>