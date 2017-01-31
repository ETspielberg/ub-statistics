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

				<title>FachRef-Assistent :: Bestand</title>

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
				<xsl:apply-templates select="stockUsageAnalysis/navbar" />
				<div class="jumbotron">
					<div class="container">
						<h1>Bestand</h1>
						<p>Übersicht über Zusammensetzung von Bestadn und Ausleihen</p>
					</div>
				</div>
				<div class="container">
					<xsl:apply-templates select="stockUsageAnalysis" />
				</div>
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="stockUsageAnalysis">
		<div class="col-sm-2 col-md-1 sidebar">
			<ul class="nav nav-sidebar">
				<li>
					<b>Bereich: </b>
					<br />
					<xsl:value-of select="topLevel/range" />
				</li>
				<li>
					<b>Name: </b>
					<br />
					<xsl:value-of select="topLevel/bez" />
				</li>
				<li>
					<b>Gesamtanzahl: </b>
					<br />
					<xsl:value-of select="topLevel/actualTotalNumberOfItems/." />
				</li>
				<li>
					<b>Mittlere Ausleihe: </b>
					<br />
					<xsl:choose>
						<xsl:when test='topLevel/dayStockLendableTotal/.=0'>
							0
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select='format-number((topLevel/daysLoanedTotal/. div topLevel/dayStockLendableTotal/.),"#0.0%")' />
						</xsl:otherwise>
					</xsl:choose>
				</li>
				<li>
					<b>Durchschnittl. Exemplarzahl </b>
					<br />
					<xsl:choose>
						<xsl:when test='topLevel/dayStockLendableTotal/.=0'>
							0
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select='format-number((number(topLevel/actualTotalNumberOfItems/.) div number(topLevel/actualTotalNumberOfWorks/.)),"#0.0")' />
						</xsl:otherwise>
					</xsl:choose>
				</li>
			</ul>
		</div>
		<xsl:apply-templates select="json" />
	</xsl:template>

	<xsl:template match="json">
		<div class="col-md-10 col-md-offset-2 main">
			<div class="col-md-6">
				<div id="mittlereAusleihe" style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
			</div>
			<div class="col-md-6">
				<div id="ausleihen" style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
			</div>
			<div class="col-md-6">
				<div id="zusammensetzung" style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
			</div>
			<div class="col-md-6">
				<div id="bestand" style="min-width: 310px; max-width: 100%; ; margin: 0 auto" />
			</div>
		</div>


		<script>
			jQuery(function () {
			jQuery('#bestand').highcharts({
			chart: {
			type: 'column',
			zoomType: 'xy',
			height: 500
			},
			title: {
			text: 'Bestand'
			},
			xAxis: {
			categories:
			<xsl:value-of select="stellen" />
			},
			yAxis: {
			min: 0,
			title: {
			text: 'Anzahl'
			}
			},
			legend: {
			reversed: true
			},
			series: [{
			name: 'Bestand',
			data:
			<xsl:value-of select="numberItemsTotal" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}, {
			name: 'Ausgesondert',
			data:
			<xsl:value-of select="numberItemsDeleted" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}]
			});
			});
			jQuery(function () {
			jQuery('#mittlereAusleihe').highcharts({
			chart: {
			type: 'column',
			zoomType: 'xy',
			height: 500
			},
			title: {
			text: 'Mittlere Ausleihe'
			},
			xAxis: {
			categories:
			<xsl:value-of select="stellen" />
			},
			yAxis: {
			min: 0,
			title: {
			text: 'Mittlere Ausleihe in %'
			}
			},
			legend: {
			reversed: true
			},
			plotOptions: {
			series: {
			stacking: 'normal'
			}
			},
			series: [{
			name: 'Mittlere Ausleihe in %',
			data:
			<xsl:value-of select="meanRelativeLoan" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}]
			});
			});
			jQuery(function () {
			jQuery('#ausleihen').highcharts({
			chart: {
			type: 'column',
			zoomType: 'xy',
			height: 500
			},
			title: {
			text: 'Zusammensetzung der Ausleihen'
			},
			xAxis: {
			categories:
			<xsl:value-of select="stellen" />
			},
			yAxis: {
			min: 0,
			title: {
			text: 'Tage ausgeliehen'
			}
			},
			legend: {
			reversed: true
			},
			plotOptions: {
			series: {
			stacking: 'percent'
			}

			},
			series: [{
			name: 'Studenten',
			data:
			<xsl:value-of select="daysLoanedStudents" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}, {
			name: 'Externe',
			data:
			<xsl:value-of select="daysLoanedExtern" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}, {
			name: 'Interne',
			data:
			<xsl:value-of select="daysLoanedIntern" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			},{
			name: 'Handapparat',
			data:
			<xsl:value-of select="daysLoanedHapp" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			},{
			name: 'Andere',
			data:
			<xsl:value-of select="daysLoanedElse" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}]
			});
			});
			jQuery(function () {
			jQuery('#zusammensetzung').highcharts({
			chart: {
			type: 'column',
			zoomType: 'xy',
			height: 500
			},
			title: {
			text: 'Zusammensetzung des Bestandes'
			},
			xAxis: {
			categories:
			<xsl:value-of select="stellen" />
			},
			yAxis: {
			min: 0,
			title: {
			text: 'Tage im Bestand'
			}
			},
			legend: {
			reversed: true
			},
			plotOptions: {
			series: {
			stacking: 'percent'
			}

			},
			series: [{
			name: 'LBS',
			data:
			<xsl:value-of select="daysStockLBS" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}, {
			name: 'Normalbestand',
			data:
			<xsl:value-of select="daysStockLendableNonLBS" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}, {
			name: 'Präsenz',
			data:
			<xsl:value-of select="daysStockNonLendable" />
			,
			point: {
			events: {
			click: function () {
			location.href = this.link;
			}
			}
			}
			}]
			});
			});



		</script>
	</xsl:template>



</xsl:stylesheet>