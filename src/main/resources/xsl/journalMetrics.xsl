<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes"
		xalan:indent-amount="2" />

	<xsl:include href="navbar.xsl" />

	<!-- ============ Parameter von MyCoRe LayoutService ============ -->

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />

	<xsl:variable name="years">
		<xsl:value-of select="journalMetrics/@years" />
	</xsl:variable>

	<xsl:variable name="issn">
		<xsl:value-of select="journalMetrics/@issn" />
	</xsl:variable>

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

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/highcharts.src.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/modules/exporting.src.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="journalMetrics/navbar" />
				<div class="jumbotron">
					<div class="container">
						<h1>Zeitschriftenmetriken</h1>
						<xsl:apply-templates select="journalMetrics/journal" />

					</div>
				</div>
				<div class="container">
					<xsl:call-template name="form" />
					<xsl:apply-templates select="journalMetrics" />
				</div>
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>

	<xsl:template name="form">
		<form action="journalMetrics" method="get">
			<table>
				<tr>
					<td align="right">
						<label for="years">Jahre zurück:  </label>
					</td>
					<td>
						<input type="hidden" name="issn" value="{$issn}" />
						<input id="years" type="text" name="years" value="{$years}"
							size="20" autofocus="true" />
						<input type="submit" class="btn btn-sm btn-success" value="Absenden" />
					</td>
				</tr>
			</table>
		</form>
	</xsl:template>

	<xsl:template match="journalMetrics">
		<xsl:apply-templates select="journalTitles" />
	</xsl:template>


	<xsl:template match="journal">
		<xsl:variable name="zdbID">
			<xsl:value-of select="zdbID/." />
		</xsl:variable>
		<p>
			Daten zur Zeitschrift
			<xsl:value-of select="actualName/." />
		</p>
		<span class="links">
			<a class="btn btn-success">
				<xsl:attribute name="href">
					<xsl:value-of select="link/." />
				</xsl:attribute>
				zur Zeitschrift
			</a>
		</span>
		<span class="links">
			<a class="btn btn-success">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('http://dispatch.opac.d-nb.de/DB=1.1/CMD?ACT=SRCHA&amp;IKT=8509&amp;SRT=LST_ty&amp;TRM=all+',zdbID)" />
				</xsl:attribute>
				zur ZDB
			</a>

		</span>
		<span class="links">
			<a class="btn btn-success">
				<xsl:attribute name="href">
					<xsl:value-of select="concat('https://rzblx1.uni-regensburg.de/ezeit/detail.phtml?bibid=UGHE&amp;colors=7&amp;lang=de&amp;jour_id=', ezbID)" />
				</xsl:attribute>
				zur EZB
			</a>
		</span>
	</xsl:template>

	<xsl:template match="journalTitles">
		<xsl:choose>
			<xsl:when test="journalTitlesPerYear">
				<div class="table-responsive">
					<table class="table table-striped">
						<thead>
							<tr>
								<th>
									Jahr
								</th>
								<th>
									Preis laut Aleph
								</th>
								<th>
									Preis laut Nutzung
								</th>
								<th>
									SNIP aus
									<a href="www.scopus.com">Scopus</a>
								</th>
								<th>
									Nutzung
								</th>
								<th>
									Preis pro Nutzung
								</th>
								<th>
									Preis pro SNIP
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="journalTitlesPerYear" />
						</tbody>
					</table>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p>Keine Einträge gefunden.</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="journalTitlesPerYear">
		<tr>
			<th>
				<xsl:value-of select="year/." />
			</th>
			<th>
				<xsl:value-of select="price/." />
			</th>
			<th>
				<xsl:value-of select="priceCalculated/." />
			</th>
			<th>
				<xsl:value-of select="snip/." />
			</th>
			<th>
				<xsl:value-of select="totalUsage/." />
			</th>
			<th>
				<xsl:value-of select="format-number(price/. div totalUsage/.,'##0.00')" />
			</th>
			<th>
				<xsl:value-of select="format-number(price/. div snip/.,'##0.00')" />
			</th>
		</tr>
	</xsl:template>

</xsl:stylesheet>