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

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:variable name="anchor">
		<xsl:value-of select="/journalCollectionMetrics/package/anchor/." />
	</xsl:variable>
	
	<xsl:variable name="year">
		<xsl:value-of select="/journalCollectionMetrics/@year" />
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

				<title>FachRef-Assistent :: e-Journals :: Zeitschriftenpakete</title>

				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/highcharts.src.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/modules/exporting.src.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
			
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="journalCollectionMetrics/navbar"/>
				<div class="jumbotron">
				<div class="container">
				<h1>Paketmetriken</h1>
				<p>Nutzungsanalyse von Zeitschriftenpaketen</p>
				</div>
				</div>
				<div class="container">
				<xsl:apply-templates select="journalCollectionMetrics" />
				</div>
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
				<xsl:apply-templates select="journalCollectionMetrics/json" />
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match ="journalCollectionMetrics">
		<xsl:apply-templates select="package" />
		<xsl:apply-templates select="subjects" />
	</xsl:template>

	<xsl:template match="package">
		<p>Im Paket <xsl:value-of select="anchor/." /> entfielen im Jahr <xsl:value-of select="year/." /> folgende Nutzungen auf die Fächer. Auf die Gesamtkosten des Pakets von <xsl:value-of select="price" /> Euro ergeben sich die in der letzten Spalte aufgeführten nutzungsbasierten Kosten</p>
		<p>Neben der Nutzungsverteilung ist in der unteren Abbildung zudem die Verteilung der Nutzung auf die einzelnen Fächer mit angegeben.</p>
	</xsl:template>


	<xsl:template match="subjects">
		<xsl:if test="count(collectionUsagePerSubject) &gt; 0">
		<div class="col-md-8 col-md-offset-2 main">
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>
						Fach
						</th>
						<th>
						  Zugriffe
						</th>
						<th>
						  Kostenanteil
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="collectionUsagePerSubject" />
				</tbody>
			</table>
		</div>
		</div>
		<div class="col-md-12 main">
		<div class="col-md-6 main ">
		<div id="fractionUsagePerSubject" class="highchart" />
		</div>
		<div class="col-md-6 main ">
		<div id="fractionJournals" style="min-width: 310px; max-width: 100%;  margin: 0 auto" />
		</div>
		</div>
		</xsl:if>
		
	</xsl:template>


	<xsl:template match="collectionUsagePerSubject">
		<xsl:variable name="collection">
			<xsl:value-of select="collection/." />
		</xsl:variable>
		<tr>
			<th>
				<xsl:value-of select="subject/." />
			</th>
			<th>
				<xsl:value-of select="usagePerSubject/." />
			</th>
			<th>
				<xsl:value-of select="pricePerSubject/." />
			</th>
		</tr>
	</xsl:template>
	
	<xsl:template match="json">
	<script>
	jQuery(function () {
	jQuery('#fractionUsagePerSubject').highcharts({
        		chart: {
                	plotBackgroundColor: null,
                	plotBorderWidth: null,
                	plotShadow: false,
                	type: 'pie'
            	},
            	title: {
                	text: 'Nutzungsanteile der Fächer'
            	},
            	tooltip: {
                	pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            	},
            	plotOptions: {
                	pie: {
                    	allowPointSelect: true,
                    	cursor: 'pointer',
                    	dataLabels: {
                        	enabled: false
                    	},
                    	showInLegend: true
                	}
            	},
        		series: <xsl:value-of select="fractionUsage/." />
    		});
    		jQuery('#fractionJournals').highcharts({
        		chart: {
                	plotBackgroundColor: null,
                	plotBorderWidth: null,
                	plotShadow: false,
                	type: 'pie'
            	},
            	title: {
                	text: 'Nutzungsanteile der einzelnen Zeitschriften'
            	},
            	tooltip: {
                	pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
            	},
            	plotOptions: {
                	pie: {
                    	allowPointSelect: true,
                    	cursor: 'pointer',
                    	dataLabels: {
                        	enabled: false
                    	},
                    	showInLegend: true
                	}
            	},
        		series: <xsl:value-of select="fractionJournals/." />
    		});
		});
</script>
	</xsl:template>
	
</xsl:stylesheet>