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
					
				<title>FachRef-Assistent :: e-Journals</title>

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
				<xsl:apply-templates select="eMedia/navbar" />
				<xsl:apply-templates select="eMedia" />
			</body>
		</html>
</xsl:template>		

<xsl:template match="eMedia">
	 <div class="jumbotron">
	 <div class="container">
        <h1>e-Journals</h1> <br />
        <p> Nutzung, Lizenzen, Pakete und Metriken</p>
     </div>
     </div>
     <div class="container">
     <div class="row">
     	<div class="col-md-4">
          <h2>Jahre</h2>
          <p>Erweitert den intialen Upload auf den verfügbaren Zeitbereich</p>
          <p><a class="btn btn-success" href="eMedia/yearExtender" role="button">Jahre ausdehnen &#187;</a></p>
        </div>
        <div class="col-md-4">
          <h2>Preise</h2>
          <p>Fügt die Preise für Pakete und Zeitschriften hinzu</p>
          <p><a class="btn btn-success" href="eMedia/priceExtender" role="button">Preise hinzufügen &#187;</a></p>
        </div>
        <div class="col-md-4">
          <h2>SNIP</h2>
          <p>Fügt den Source Normalized Impact per Paper (SNIP) hinzu</p>
          <p><a class="btn btn-success" href="eMedia/snipExtender" role="button">SNIP hinzufügen &#187;</a></p>
       </div>
       </div>
       <div class="row">
        <div class="col-md-4">
          <h2>SUSHI</h2>
          <p> Management von SUSHI-Accounts </p>
          <p><a class="btn btn-success" href="eMedia/publisherManagement" role="button">Zu den SUSHI-Accounts &#187;</a></p>
        </div>
        <div class="col-md-4">
          <h2>Verteilung</h2>
          <p> Berechnet die Verteilung von Nutzungen auf die Fächer </p>
          <p><a class="btn btn-success" href="eMedia/subjectDistributor" role="button">Verteilung berechnen &#187;</a></p>
        </div>
        </div>
        <div class="row">
        <div  class="col-md-12 main">
        <h2>EZB-Datei-Upload</h2>
			<form action="eMedia/ezbUpload" method="post" class="dropzone" enctype="multipart/form-data" />
		</div>
        </div>
        </div>
</xsl:template>
		

</xsl:stylesheet>