<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:include href="navbar.xsl" />
	<xsl:include href="mabxml-isbd.xsl" />

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

				<title>FachRef-Assistent :: Bestandsanalyse</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js"
					language="javascript"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}js/dataTables.bootstrap.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/d3js/${version.d3}/d3.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/queue.min.js"></script>

				<link
					href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css"
					rel="stylesheet" />
				<link rel="stylesheet" href="{$WebApplicationBaseURL}css/d3plot.css" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="stockAnalysis/navbar" />
				<div class="jumbotron">
					<div class="container">
						<h1>Bestandsinfo</h1>
						<p>Zeitliche Entwicklung und Preisverteilung</p>
					</div>
				</div>
				<xsl:apply-templates select="stockAnalysis" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
			</body>
		</html>
	</xsl:template>


	<xsl:template match="stockAnalysis">
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
			<xsl:call-template name="histogramm" />
		</div>
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
			<xsl:call-template name="plotEvolution" />
		</div>
	</xsl:template>

	<xsl:template name="plotEvolution">
		<script>
			var margin = {top: 20, right: 20, bottom: 30, left: 50},
			width = 960 - margin.left - margin.right,
			height = 500 - margin.top - margin.bottom;

			var formatCount = d3.format(",.0f");

			var x = d3.time.scale().range([0, width]);

			var y = d3.scale.linear().range([height, 0]);

			var color = d3.scale.category20();

			var xAxis = d3.svg.axis()
			.scale(x)
			.orient("bottom");

			var yAxis = d3.svg.axis()
			.scale(y)
			.orient("left")
			.tickFormat(formatCount);

			var area = d3.svg.area()
			.x(function(d) { return x(d.date); })
			.y0(function(d) { return +y(d.y0); })
			.y1(function(d) { return +y(d.y0 + d.y); })
			.interpolate("step-after");

			var line = d3.svg.line()
			.x(function(d) { return d.date; })
			.y(function(d) { return +y(d.y); })


			var stack = d3.layout.stack()
			.values(function(d) { return d.values; });

			var svg = d3.select("body").append("svg")
			.attr("width", width + margin.left + margin.right)
			.attr("height", height + margin.top + margin.bottom)
			.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			var timeAndCount =<xsl:value-of select="csvData/timeAndCount/." />;

			var data = d3.csv.parse(timeAndCount);

			color.domain(d3.keys(data[0]).filter(function(key) { return key !==	"time"; }));

			data.forEach(function(d) {
			d.date = (new Date(+d.time));
			});

			var browsers = stack(color.domain().map(function(name) {
			return {
			name: name,
			values: data.map(function(d) {
			return {date: d.date, y: +d[name]};
			})
			};
			}));

			console.log(browsers[0])

			x.domain(d3.extent(data, function(d) { return d.date; }));
			y.domain([0,d3.max(browsers, function(d) { return d3.max(d.values, function (d) { return d.y + d.y0; }); })]);


			var browser = svg.selectAll(".browser")
			.data(browsers)
			.enter().append("g")
			.attr("class", "browser");

			browser.append("path")
			.attr("class", "area")
			.attr("d", function(d) { return area(d.values); })
			.style("fill", function(d) { return color(d.name); });

			browser.append("text")
			.datum(function(d) { return {name: d.name, value: d.values[d.values.length - 1]}; })
			.attr("transform", function(d) { return "translate(" + x(d.value.date) + "," + y(d.value.y0 + d.value.y / 2) + ")"; })
			.attr("x", -80)
			.attr("dy", ".35em")
			.text(function(d) { return d.name; });

			svg.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + height + ")")
			.call(xAxis);

			svg.append("g")
			.attr("class", "y axis")
			.call(yAxis);


		</script>
	</xsl:template>

	<xsl:template name="histogramm">

	</xsl:template>
</xsl:stylesheet>