<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:include href="mabxml-isbd.xsl" />
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

	<xsl:variable name="docNumbers">
		<xsl:for-each select="/document/documentsAnalyzed/documentAnalyzed">
			<xsl:value-of select="@key" />
			<xsl:if test="position() &lt; last()">
				<xsl:text>&amp;docNumber= </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="callNo">
		<xsl:for-each select="/document/documentsAnalyzed/documentAnalyzed">
			<xsl:value-of select="." />
			<xsl:if test="position() &lt; last()">
				<xsl:text>, </xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>
	
	<xsl:variable name="propDel">
		<xsl:value-of select="/document/eventAnalysis/analysis/@proposedDeletion" />
	</xsl:variable>
	
	<xsl:variable name="yearsToAverage">
		<xsl:value-of select="/document/eventAnalysis/stockControlProperties/yearsToAverage" />
	</xsl:variable>
		
	<xsl:variable name="page.title">
		<xsl:text>Ausleihanalyse für </xsl:text>
		<xsl:value-of select="/document/eventAnalysis/stockControlProperties/stockControl" />
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

				<title>Systemstellen-Analyse</title>

				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/dataTables.bootstrap.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/d3js/${version.d3}/d3.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/queue.min.js"></script>
					
				<link href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css" rel="stylesheet" />
				<link rel="stylesheet" href="{$WebApplicationBaseURL}css/d3plot.css" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="document/navbar" />
				<xsl:apply-templates select="*" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>


	<xsl:template match="document">
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="data" />
		<xsl:call-template name="form" />
		</div>
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="plot" />
		</div> 
		<!-- 
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="deletion" />
		</div> -->
	</xsl:template>
	
	<xsl:template name="form">
				<form action="document" method="get">
					<label for="yearsToAverage"
						style="display: inline-block; width: 240px; text-align: right">
						Zeitraum in Jahren: 
					</label>
					<input type="text" size="3" value="{$yearsToAverage}"
						name="yearsToAverage" id="yearsToAverage" />
					<xsl:for-each select="/document/documentsAnalyzed/documentAnalyzed">
					  <input type="hidden" name="docNumber" value="{@key}" />
					</xsl:for-each>
					  
					<input type="hidden" name="collections" value="" />
					<input type="hidden" name="materials" value="" />
					<input type="hidden" name="exact" value="" />
					<input type="submit" value="Erneut berechnen" />
					
					
				</form>
				<div>
				Der angegebene Zeitraum wird zur Berechnung der durchschnittlichen und maximalen Ausleihe herangezogen. Ausleihen in Handapparate werden nicht als Ausleihe gezählt,
				sondern reduzieren den ausleihbaren Bestand.
				</div>
	</xsl:template>

	<xsl:template name="data">
			<xsl:apply-templates select="eventAnalysis" />
	</xsl:template>
	
		<xsl:template match="eventAnalysis">
		<xsl:choose>
			<xsl:when test="analysis">
			<div class="table-responsive">
				<table class="table table-striped" id="dataTable">
					<h3>Ausleih-Analyse</h3>
					<thead>
						<tr>
							<th>Mittlere Ausleihe:</th>
							<th>Maximale Ausleihe:</th>
							<th>Maximale Ausleihe absolut:</th>
							<th>Aktueller Bestand:</th>
							<th>Vorschlag zur Aussonderung:</th>
							<th>Signaturen:</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="analysis">
						</xsl:apply-templates>
					</tbody>
				</table>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<p>Keine Exemplare für diesen Titel gefunden.</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="plot">
	<div>
	<h3> Ausleihe nach Gruppen</h3>
	
			<xsl:apply-templates select="documentsAnalyzed" />
			
			<div class="well" id="relativeLoanDescription" style="display:none">
			Die Abbildung zeigt die relative Ausleihe der verschiedenen Benutzergruppen, bezogen auf den jeweils aktuellen Bestand. 
			Die obere horizontale Linie gibt die maximale relative Ausleihe im gewählten Zeitraum an, die untere die durchschnittliche relative Ausleihe.
			Berücksichtigt werdend die Bestände der Suchanfrage im Ausleihprotokoll. 
			Der aktuelle Bestand wird um Handapparatsausleihen korrigiert.
			</div>
			<div class="well" id="absLoanDescription" style="display:none">
			Die Abbildung zeigt die absoluten Ausleihzahlen und Bestände der einzelnen Auflagen additiv übereinander dargestellt. 
			Die horizontale Linie gibt die maximale absolute Ausleihe im gewählten Zeitraum an.
			</div>
			</div>
	</xsl:template>

	<xsl:template name="deletion">
			<form action="fachref/sendDeletionEmail" method="get" target="popup">
			<xsl:text> Aus dem </xsl:text>
				<select name="collections">
					<option value="D35"> Bestand BA</option>
					<option value="E13 E23"> Bestand GWGSW</option>
					<option value="D45"> Bestand MC</option>
					<option value="E33 E43"> Bestand MNT</option>
					<option value="D05"> Bestand LK</option>
				</select>
				<input type="hidden" name="shelfs" value="{$callNo}" />
				<xsl:text> insgesamt </xsl:text>
				<input id="propDel" type="text" name="propDel" value="{$propDel}"
					size="3" />
				<xsl:text> Exemplar(e) aussondern? </xsl:text>
				<input type="submit" value="Ok" />
			</form>
	</xsl:template>




	<xsl:template match="analysis">
		<tr>
			<td>
				<xsl:value-of select='format-number(meanRelativeLoan/., "#%")' />

			</td>
			<td>
				<xsl:value-of select='format-number(maxRelativeLoan/., "#%")' />
			</td>
			<td>
				<xsl:value-of select="maxLoansAbs/." />
				<xsl:text> Exemplar(e) </xsl:text>
			</td>
			<td>
				<xsl:value-of select="lastStock/." />
				<xsl:text> Exemplar(e) </xsl:text>
			</td>
			<td>
				<xsl:value-of select="proposedDeletion/."/>
				<xsl:text> Exemplar(e) </xsl:text>
			</td>
			<td>
				<xsl:value-of select="$callNo" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="documentsAnalyzed">
	<div id="parent" class="container">
		<script type="text/javascript" charset="utf-8">
			//prepare the parameters for the chart-area
			var margin = {top: 20,
			right: 20, bottom: 100, left: 50},
			width = 800 - margin.left -
			margin.right,
			height = 400 - margin.top -
			margin.bottom;

			//initialize time format
			var parseDate = d3.time.format("%Y-%m-%d").parse;
			var	formatPercent =	d3.format(".1%");
			var formatNumber = d3.format(".0f");

			//prepare the scales
			var x = d3.time.scale().range([0, width]);
			var y =	d3.scale.linear().range([height, 0]);


			//initialize the chart-area
			var	svg = d3.select("#parent")
			.append("svg")
			.attr("width", width +
			margin.left + margin.right)
			.attr("height", height + margin.top +
			margin.bottom)
			.append("g")
			.attr("transform",
			"translate(" +
			margin.left + "," + margin.top + ")");

			//set start Years to average
			<xsl:for-each select="//eventAnalysis/analysis">
				yearsToAverage = <xsl:value-of select="$yearsToAverage" />;
				lastStock = <xsl:value-of select="lastStock/." />;
				maxLoansAbs = <xsl:value-of select="maxLoansAbs/." />;
				meanValue =	<xsl:value-of select="meanRelativeLoan/." />;
				maxValue = <xsl:value-of select="maxRelativeLoan/." />;
				deletionProposal = <xsl:value-of select="proposedDeletion/." />;
			</xsl:for-each>


			//if (deletionProposal &#60; 0) {deletionProposal = 0;}
			updateYearsToAverage(yearsToAverage);

			//load data
			var filenames = [];
			var docNumbers = [];
			<xsl:for-each select="documentAnalyzed">
				filenames.push(
				<xsl:text>'timeline?docNumber=</xsl:text>
				<xsl:value-of select="@key" />
				<xsl:text>'</xsl:text>
				);
			</xsl:for-each>
			
			var e = document.getElementById('parent');


			if (filenames.length == 0) {
			console.log("missing document selection");
			alert("Kein Dokument ausgewählt!");
			}
			else if	(filenames.length == 1) {
			d3.csv(filenames[0], function(error, data) {
			if (error) throw error;	analyze_single(data);})
			}
			else {
			currentURL = window.location.href ;
			listDocumentNumbersString =	currentURL.substring(currentURL.indexOf("&amp;")+1);
			getTimelineAllFilename = "series?type=&amp;"+listDocumentNumbersString;
			d3.json(getTimelineAllFilename, function(error, data) {
			if (error) throw error; analyze_multiple(data);})
			}


			/*
			################################################################
			#
			Analyze single Editions with respect to user group #
			################################################################
			*/

			function analyze_single(data) {

			var color = d3.scale.category10();
			
			
  			document.getElementById('relativeLoanDescription').style.display = 'block';



			//prepare the axis
			var xAxis =	d3.svg.axis().scale(x).orient("bottom");
			var yAxis =	d3.svg.axis().scale(y).orient("left").tickFormat(d3.format(".0%"));;

			//prepare the individual lines
			var area = d3.svg.area()
			.x(function(d) { return x(d.Day); })
			.y0(function(d) { return y(d.y0); })
			.y1(function(d) { return y(d.y0 + d.y); })
			.interpolate("step-before");

			//prepare the stacking
			var stack =	d3.layout.stack()
			.values(function(d) { return d.values; });

			//filter data and assign colors
			color.domain(d3.keys(data[0]).filter(function(key) { return key !==	"Day" &amp;&amp; key !== "Stock" &amp;&amp; key !== "Happ"; }));

			//initialize all columns
			data.forEach(function(d) {
			d.Day =	parseDate(d.Day);
			d.Intern = +d.Intern / (+d.Stock-d.Happ);
			d.Extern = +d.Extern / (+d.Stock-d.Happ);
			d.Student = +d.Student /(+d.Stock-d.Happ);
			d.Else = +d.Else / (+d.Stock-d.Happ);
			});

			//declare the x- and y-axis span
			x.domain(d3.extent(data, function(d) { return d.Day; }));

			//build the stacked traces
			var borrowers = stack(color.domain().map(function(name) {
			return {name: name, values:
			data.map(function(d) {return {Day: d.Day, y: d[name]};})};
			}));

			//append data
			var borrower = svg.selectAll(".borrower")
			.data(borrowers)
			.enter().append("g")
			.attr("class", "borrower");

			//append the chart
			borrower.append("path")
			.attr("class", "area")
			.attr("d", function(d) { return area(d.values); })
			.style("fill",
			function(d) { return color(d.name); });


			//prepare the axis
			svg.append("g")
			.attr("class", "x axis")
			.attr("transform",
			"translate(0," + height + ")")
			.call(xAxis);

			svg.append("g")
			.attr("class", "y axis")
			.call(yAxis);

			//add the legend
			svg.append("text")
			.attr("x", 0)
			.attr("y", height + margin.top +40)
			.attr("class","legend")
			.style("fill", function(d) { return	color("Intern"); })
			.text("Interne Ausleihen");

			svg.append("text")
			.attr("x", 0)
			.attr("y", height + margin.top +60)
			.attr("class","legend")
			.style("fill", function(d) { return	color("Extern"); })
			.text("Externe Ausleihen");

			svg.append("text")
			.attr("x", 200)
			.attr("y", height + margin.top +40)
			.attr("class","legend")
			.style("fill", function(d) { return	color("Student"); })
			.text("Studentische Ausleihen");

			svg.append("text")
			.attr("x", 200)
			.attr("y", height + margin.top +60)
			.attr("class","legend")
			.style("fill", function(d) { return	color("Else"); })
			.text("Andere Ausleihen");

			//update the analysis
			d3.select("#yearsToAverageNew").on("input", function() {updateYearsToAverage(+this.value);});

			//display the analysis
			svg.append("svg:line")
			.attr("x1", 0)
			.attr("x2", width)
			.attr("y1", y(+meanValue))
			.attr("y2", y(+meanValue))
			.attr("class","analysis_line")
			.style("stroke","black")
			.attr("id","meanLine");
			svg.append("svg:line")
			.attr("x1", 0)
			.attr("x2", width)
			.attr("y1", y(+maxValue))
			.attr("class","analysis_line")
			.attr("y2", y(+maxValue))
			.style("stroke","black")
			.attr("id", "maxLine");
			}


			/*
			################################################################
			#
			Analyze multiple Editions with respect to editions #
			################################################################
			*/

			function analyze_multiple(data) {
			
						
  			document.getElementById('absLoanDescription').style.display = 'block';

			//prepare the axis
			var xAxis =	d3.svg.axis().scale(x).orient("bottom");
			var yAxis =	d3.svg.axis().scale(y).orient("left");
			
			//accessor to the x- and y-values
			var areaFunction = d3.svg.area()
			.x(function(d) {return x(d.time); })
			.y0(function(d) { return y(d.y0);})
			.y1(function(d) { return y(d.y0 + d.y); })
			.interpolate("step-after");

			var stack = d3.layout.stack().values(function(d) { return d.values; });

			var colorStock = d3.scale.category20b();
			colorStock.domain(d3.keys(data));

			var colorLoans = d3.scale.category20();
			colorLoans.domain(d3.keys(data));

			var loansEditions = stack(colorStock.domain().map(function(id) {
			return {name: data[id].docNumber,
			values:	data[id].loans.map(function(d) {return {time: +d[0], y: +d[1]};})
			};
			}));

			var stockEditions = stack(colorLoans.domain().map(function(id) {
			return {
			name: data[id].docNumber,
			values: data[id].stock.map(function(d) { return {time: +d[0], y: +d[1]};}) };
			}));

			//declare the x- and y-axis span
			x.domain([d3.min(stockEditions,	function(d) {return d3.min(d.values, function (d) { return d.time;});}),d3.max(stockEditions, function(d) { return d3.max(d.values,	function (d) { return d.time; }); })]);
			y.domain([0,d3.max(stockEditions, function(d) { return d3.max(d.values, function (d) { return d.y + d.y0; }); })]);

			//append data
			var stockEdition = svg.selectAll(".edition")
			.data(stockEditions)
			.enter().append("g")
			.attr("class","stock")
			.append ("path")
			.attr("class", "area")
			.attr("d",function(d) { return areaFunction(d.values); })
			.style("fill", function(d) { return colorStock(d.name); })

			var loansEdition = svg.selectAll(".edition")
			.data(loansEditions)
			.enter().append("g")
			.attr("class","loans")
			.append ("path")
			.attr("class", "area")
			.attr("d",function(d) { return areaFunction(d.values); })
			.style("fill", function(d) { return	colorLoans (d.name); })

			//prepare the axis
			svg.append("g")
			.attr("class", "x axis")
			.attr("transform","translate(0," + height +")")
			.call(xAxis);

			svg.append("g")
			.attr("class", "y axis")
			.call(yAxis);
			
			svg.append("svg:line")
			.attr("x1", 0)
			.attr("x2", width)
			.attr("y1", y(+maxLoansAbs))
			.attr("class","analysis_line")
			.attr("y2", y(+maxLoansAbs))
			.style("stroke","black")
			.attr("id", "maxLineAbs");
			}


			function updateYearsToAverage(yearsToAverageNew) {
			//adjust the text on the range slider
			d3.select("#yearsToAverageValue").text(yearsToAverageNew);
			d3.select("#yearsToAverageNew").property("value", yearsToAverageNew);
			return yearsToAverage = +yearsToAverageNew;
			// parse the query string into an object
			var q = queryString.parse(location.search);
			// set the `row` property
			q.yearsToAverage = yearsToAverageNew;
			// convert the	object to a query string
			// and overwrite the existing query string
			location.search = queryString.stringify(q);	}
			
			jQuery(document).ready(function() {
 				jQuery('#export').click(function(e) {
     				var csv = jQuery('#dataTable').table2CSV({delivery:'value'});
      				window.location.href = 'data:text/csv;charset=UTF-8,' + encodeURIComponent(csv);
    			});
			})
		</script>
		</div>
	</xsl:template>


</xsl:stylesheet>
