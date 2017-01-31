<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:include href="navbar.xsl" />

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />


	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes"
		xalan:indent-amount="2" />

	<xsl:template match="/">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->

				<title>FachRef-Assistent :: e-Journals :: Nutzung</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/highcharts.src.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/modules/exporting.src.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/themes/grid.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link rel="stylesheet"
					href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="usageAnalysis/navbar" />
				<div class="jumbotron">
					<div class="container">
						<h1>Nutzung</h1>
						<p>Die Nutzung von Zeitschriften oder Paketen</p>
						<xsl:call-template name="form" />
					</div>
				</div>
				<div class="container">

					<xsl:apply-templates select="usageAnalysis" />
				</div>
				<script src="{$WebApplicationBaseURL}js/highcharts_usage.js" />
				<xsl:apply-templates select="usageAnalysis/json" />
			</body>
		</html>
	</xsl:template>

	<xsl:template name="form">
		<form action="journalUsage" method="get">
			<table class="table table-bordered">
				<tr>
					<td align="right">
						<label for="issn">ISSN:  </label>
					</td>
					<td>
						<input id="issn" type="text" name="issn" value="{@issn}"
							size="20" autofocus="true" />
						<input type="submit" class="btn btn-sm btn-success" value="Absenden" />
					</td>
				</tr>
			</table>
		</form>
	</xsl:template>


	<xsl:template match="usageAnalysis">
		<xsl:variable name="issn">
			<xsl:value-of select="issn/." />
		</xsl:variable>
		<div class="col-md-10 col-md-offset-2 main">
			<div id="usageAnalysis" data-issn="{$issn}" class="highchart"
				style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
			<div id="coverage" data-issn="{$issn}" class="highchartCoverage"
				style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
		</div>
	</xsl:template>

	<xsl:template match="json">
		<script>
			jQuery(document).ready(
			function () {
			Highcharts.Chart('highchartCoverage', {

			chart: {
			type: 'columnrange',
			inverted: true
			},

			title: {
			text: 'Zeitliche Abdeckung'
			},

			xAxis: {
			categories:
			<xsl:value-of select="categories/." />
			},

			legend: {
			enabled: false
			},

			plotOptions: {
			columnrange: {
			dataLabels: {
			enabled: true,
			formatter: function () {
			return this.y;
			}
			}
			}
			},

			series: [{
			name: 'Abdeckung',
			data:
			<xsl:value-of select="data/." />
			}]

			});

			});


		</script>

	</xsl:template>



</xsl:stylesheet>