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
	<xsl:param name="username" />

	<!-- ======== HTML Seitenlayout ======== -->

	<xsl:template match="/help">
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

				<title>FachRef-Assistent :: Hilfe</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/dropzone.js"></script>


				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<nav class="navbar navbar-inverse navbar-fixed-top">
					<div class="container-fluid">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle collapsed"
								data-toggle="collapse" data-target="#navbar" aria-expanded="false"
								aria-controls="navbar">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand" href="{$WebApplicationBaseURL}help/start">Hilfe</a>
						</div>
						<div id="navbar" class="navbar-collapse collapse">
							<ul class="nav navbar-nav navbar-right">
								<li>
									<a href="{$WebApplicationBaseURL}fachref/start">Zurück</a>
								</li>
							</ul>
						</div><!--/.navbar-collapse -->
					</div>
				</nav>

				<!-- Main jumbotron for a primary marketing message or call to action -->
				<xsl:apply-templates select="components" />

				<!-- Bootstrap core JavaScript ================================================== -->
				<!-- Placed at the end of the document so the pages load faster -->
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js" />
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="components">
	<div class="jumbotron">
		<div class="container">
		<h1>
			<xsl:value-of select="title/." />
		</h1>
		<p>
			<xsl:value-of select="description/." />
		</p>
		</div>
	</div>
	<div class="container">
		<xsl:if test="figures">
			<div class="col-md-12">
				<xsl:apply-templates select="figures" />
			</div>
		</xsl:if>
			<xsl:apply-templates select="modules" />
			<xsl:apply-templates select="functions" />
			<xsl:apply-templates select="sections" />
			<hr />
		</div>
	</xsl:template>

	<xsl:template match="modules">
		<div class="row">
			<xsl:apply-templates select="module" />
		</div>
	</xsl:template>

	<xsl:template match="functions">
		<div class="row">
			<xsl:apply-templates select="function" />
		</div>
	</xsl:template>
	
	<xsl:template match="sections">
		<xsl:apply-templates select="section" />
	</xsl:template>

	<xsl:template match="module">
		<xsl:variable name="name">
			<xsl:value-of select="name/." />
		</xsl:variable>
		<div class="col-md-3">
			
			<h2>
				<xsl:value-of select="title" />
			</h2>
			<xsl:if test="figures">
			<xsl:apply-templates select="figures" />
			</xsl:if>
			<p>
				<xsl:value-of select="description" />
			</p>
			<p><a class="btn btn-success" href="{$RequestURL}/{$name}" role="button">Weitere Infos &#187;</a></p>
		</div>
	</xsl:template>
	
	<xsl:template match="function">
		<xsl:variable name="name">
			<xsl:value-of select="name/." />
		</xsl:variable>
		<div class="col-md-3">
			<h2>
				<xsl:value-of select="title" />
			</h2>
			<xsl:if test="figures">
			<xsl:apply-templates select="figures" />
			</xsl:if>
			<p>
				<xsl:value-of select="description" />
			</p>
			<p><a class="btn btn-success" href="{$RequestURL}/{$name}" role="button">Weitere Infos &#187;</a></p>
		</div>
	</xsl:template>
	
	<xsl:template match="section">
		<div class="col-md-12">
			<h2>
				<xsl:value-of select="title" />
			</h2>
			<xsl:if test="figures">
			<xsl:apply-templates select="figures" />
			</xsl:if>
			<p>
				<xsl:copy-of select="description/node()" />
			</p>
		</div>
	</xsl:template>
	
	<xsl:template match="figures">
  		<xsl:apply-templates select="figure"/>
	</xsl:template>
	
	<xsl:template match="figure">
		<xsl:variable name="filename">
			<xsl:value-of select="file/." />
		</xsl:variable>
		<div class="col-md-12 screenshot">
		<img src="{$WebApplicationBaseURL}img/{$filename}" width="800" alt="{$filename}" />
		<div class="caption"><xsl:value-of select="caption/." /></div>
		</div>
	</xsl:template>
	
	<xsl:template match="description">
		<xsl:copy>
 	      <xsl:apply-templates select='@*|node()' />
 	    </xsl:copy>
	</xsl:template>
</xsl:stylesheet>
