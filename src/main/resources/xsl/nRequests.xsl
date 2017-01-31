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

	<xsl:variable name="readerControl">
		<xsl:value-of select="/nRequests/reader/readerControl/." />
	</xsl:variable>
	<xsl:variable name="name">
		<xsl:value-of select="/nRequests/reader/name/." />
	</xsl:variable>
	<xsl:variable name="notationRange">
		<xsl:value-of select="/nRequests/reader/notationRange/." />
	</xsl:variable>
	<xsl:variable name="subjectID">
		<xsl:value-of select="/nRequests/reader/subjectID/." />
	</xsl:variable>
	<xsl:variable name="perform">
		<xsl:value-of select="/nRequests/reader/perform/." />
	</xsl:variable>
	<xsl:variable name="thresholdQuotient">
		<xsl:value-of select="/nRequests/reader/thresholdQuotient/." />
	</xsl:variable>
	<xsl:variable name="thresholdDuration">
		<xsl:value-of select="/nRequests/reader/thresholdDuration/." />
	</xsl:variable>
	
	<xsl:variable name="page.title">
		<xsl:text>Hitliste </xsl:text>
		<xsl:value-of select="$name" />
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

				<title>FachRef-Assistent :: Vormerk-Hitliste</title>

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
				<xsl:apply-templates select="nRequests/navbar" />
				<div class="jumbotron">
				<div class="container">
				<h1>Hitliste</h1>
				<p>Die Hitliste der am meisten vorgemerkten Titel</p>
				</div>
				</div>
				<div class="container">
				<xsl:apply-templates select="nRequests" />
				</div>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
				<script>
				$('[data-toggle="tooltip"]').tooltip(); 
				</script>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="nRequests">
		<xsl:choose>
		<xsl:when test="count(nRequest) &gt; 0">
		<xsl:call-template name="list" />
		</xsl:when>
		<xsl:otherwise>
		<p>Es wurden keine Titel zur mit Vormerkungen gefunden. Geben Sie entweder eine neue Systemstelle ein oder wählen sie ein anderes Profil.</p>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="list">
	<div class="container-fluid">
		<div class="col-md-12">
	      <div class="alert alert-info" role="alert">
	        <center>Um bibliographische Informationen zu den einzelnen Einträgen zu erhalten, bitte die Maus über die Signatur bewegen.</center> 
	      </div>
				<table id="sortableTable" class="table table-striped">
					<thead>
						<tr>
							<th>Signatur </th>
							<th>Protokoll </th>
							<th>Anzahl Vormerkungen</th>
							<th>Anzahl Ausleihen</th>
							<th>Anzahl Exemplare </th>
							<th>Anzahl ausl. Exemplare </th>
							<th>Quotient </th>
							<th>Dauer </th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="nRequest"/>
					</tbody>
				</table>
		</div>
		</div>
	</xsl:template>
	
	<xsl:template match="nRequest">
			<tr>
				<xsl:variable name="shelfmark">
					<xsl:value-of select="@callNo" />
				</xsl:variable>
				<xsl:variable name="docNumber">
					<xsl:value-of select="@docNumber" />
				</xsl:variable>
				<td  data-toggle="tooltip">
				<xsl:attribute name="title">
				<xsl:value-of select="mab/." />
				</xsl:attribute>
					<xsl:value-of select="$shelfmark" />
				</td>
				<td>
					<a
						href="../.:/protokoll?shelfmark={$shelfmark}&amp;collections=&amp;exact="
						target="popup">Link</a>
				</td>
				<td>
					<xsl:apply-templates select="NRequests/." />
				</td>
				<td>
					<xsl:apply-templates select="NLoans/." />
				</td>
				<td>
					<xsl:value-of select="NItems/." />
				</td>
				<td>
					<xsl:apply-templates select="NLendable/." />
				</td>
				<td>
					<xsl:apply-templates select="ratio/." />
				</td>
				<td>
					<xsl:value-of select="duration/." />
				</td>
			</tr>
	</xsl:template>





</xsl:stylesheet>