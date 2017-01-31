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

	<!-- ======== HTML Seitenlayout ======== -->


	<xsl:variable name="page.title">
		<xsl:text>Bestandspflege - Management von</xsl:text>
		<xsl:value-of select="/profileOverview/@username" />
	</xsl:variable>
	
	<xsl:variable name="user">
		<xsl:value-of select="/profileOverview/@user" />
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

				<title>FachRef-Assistent :: Profile</title>

				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script src="{$WebApplicationBaseURL}js/dropzone.js"></script>


				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dropzone.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
				<xsl:apply-templates select="profileOverview/navbar" />
				<div class="jumbotron">
				<div class="container">
				<h1>Profile</h1>
				<p>Individuelle Bestandspflege für jeden Fachbereich</p>
				<span class="links">
				<a class="btn btn-primary" href="profile/StockControl_Form.xed" role="button">Neues Profil erstellen</a>
				</span>
				<span class="links">
				<a class="btn btn-success" href="profile/blacklist" target="popup">Zur Blacklist</a>
				</span>
				</div>
				</div>
				<div class="container">
				<xsl:apply-templates select="profileOverview" />
				</div>
				<div class="container">
				<h2><a href="profile/StockControl_Form.xed?id=default" >Standardprofil </a></h2>
				<p>Das <a href="profile/StockControl_Form.xed?id=default" >Standardprofil </a> enthält Startwerte für weitere Profile und wird verwendet, wenn aus dem Ausleihprotokoll heraus Analysen gestartet werden.</p>
				</div>
				<div class="container">
				<h2><a href="profile/StockControl_Form.xed?id=csv">csv-Profil </a></h2>
				<p>Das <a href="profile/StockControl_Form.xed?id=csv">csv-Profil </a> wird verwendet, wenn aus aus hochgeladenen Listen heraus Analysen erstellt werden.</p>
				</div>
				<div class="container">
				<h3 class="sub-header">hochgeladene csv-Dateien</h3>
					<xsl:apply-templates select="profileOverview/csvFiles" />
				<div  class="col-md-4 main">
					<form action="csvUpload" method="post" class="dropzone" enctype="multipart/form-data" />
				</div>
				</div>
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="profileOverview">
		
			<xsl:if test="count(referent/stockControlProperties) &gt; 0">
				<xsl:apply-templates select="referent" />
			</xsl:if>
			<xsl:if test="count(substitute/stockControlProperties) &gt; 0">
				<xsl:apply-templates select="substitute" />
			</xsl:if>

	</xsl:template>


	<xsl:template match="referent">
		<h2 class="sub-header">Profile für das Fachreferat</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>
							Bereich
						</th>
						<th>
							Standort
						</th>
						<th>
							Materialien
						</th>
						<th>
							Gruppiert?
						</th>
						<th>
						  Listen
						</th>
						<th>
						Ausführen/bearbeiten/löschen
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="stockControlProperties" />
				</tbody>
			</table>
		</div>
	</xsl:template>

	<xsl:template match="substitute">
	
		<h2 class="sub-header">Weitere Profile</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th>
							Bereich
						</th>
						<th>
							Standort
						</th>
						<th>
							Materialien
						</th>
						<th>
							Gruppiert?
						</th>
						<th>
						  Listen
						</th>
						<th>
						Ausführen/bearbeiten/löschen
						</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="stockControlProperties" />
				</tbody>
			</table>
		</div>
	</xsl:template>


	<xsl:template match="stockControlProperties">
		<xsl:variable name="stockControl">
			<xsl:value-of select="stockControl/." />
		</xsl:variable>
		<xsl:variable name="subjectID">
			<xsl:value-of select="subjectID" />
		</xsl:variable>
		<tr>
			<th>
				<xsl:value-of select="i18n:translate(concat('scpManagement.subjectID.',subjectID/.))" />
					<xsl:text> </xsl:text>
					<xsl:value-of select="systemCode/." />
			</th>
			<th>
				<xsl:value-of select="collections/." />
			</th>
			<th>
				<xsl:value-of
					select="i18n:translate(concat('scpManagement.materials.',materials/.))" />
			</th>
			<th>
				<xsl:choose>
					<xsl:when test="groupedAnalysis = 'true'">
						<xsl:text>X</xsl:text>
					</xsl:when>
					<xsl:otherwise>
					<xsl:text>-</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</th>
			<th>
			<span class="links">
				<a class="btn btn-sm btn-success" href="profile/deletionAssistant?stockControl={$stockControl}"
					role="button" target="popup">Aussonderung</a>
			</span>
			<span class="links">
				<a class="btn btn-sm btn-success" href="profile/purchaseAssistant?stockControl={$stockControl}"
					role="button" target="popup">Erwerbung</a>
			</span>
			</th>
			<th>
			<span class="links">
				<a class="btn btn-sm btn-primary" href="profile/eventAnalyzer?stockControl={$stockControl}"
					role="button" target="popup"><span class="glyphicon glyphicon-star-empty" aria-hidden="true"></span></a>
			</span>
			<span class="links">
				<a class="btn btn-sm btn-warning" href="profile/StockControl_Form.xed?id={$stockControl}"
					role="button"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
			</span>
			<span class="links">
				<a class="btn btn-sm btn-danger" href="profile/scpDelete?stockControl={$stockControl}"
					role="button"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
			</span>
			</th>
		</tr>
	</xsl:template>
	
	<xsl:template match="csvFiles" >
	<div  class="col-md-8 main">
		<div class="table-responsive">
			<table class="table table-striped">
				<tbody>
					<xsl:apply-templates select="file" />
				</tbody>
			</table>
		</div>
		</div>
	</xsl:template>
	
	<xsl:template match="file" >
	<xsl:variable name="filename">
			<xsl:value-of select="." />
		</xsl:variable>
	<tr>
		<th>
			<xsl:value-of select="." />
		</th>
		<th>
			<a class="btn btn-sm btn-success" href="{$WebApplicationBaseURL}fachref/profile/csvAnalyzer?file={$filename}" role="button" target="popup">zur Liste</a>
		</th>
		<th>
		<a class="btn btn-sm btn-danger" href="{$WebApplicationBaseURL}fachref/profile/csvDelete?file={$filename}"
					role="button"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a>
		</th>
	</tr>
	
	</xsl:template>


</xsl:stylesheet>