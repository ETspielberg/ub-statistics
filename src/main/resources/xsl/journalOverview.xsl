<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:param name="WebApplicationBaseURL" />
	<xsl:param name="ServletsBaseURL" />
	<xsl:param name="RequestURL" />
	<xsl:param name="CurrentLang" />
	<xsl:param name="DefaultLang" />
	
	<xsl:include href="navbar.xsl" />

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
				<xsl:apply-templates select="journalOverview/navbar" />
				<xsl:apply-templates select="journalOverview" />
			</body>
		</html>
</xsl:template>		

<xsl:template match="journalOverview">
	 <div class="jumbotron">
	 <div class="container">
        <h1>e-Journal Overview</h1> <br />
        <p> Nutzung und Lizenzen</p>
     </div>
     </div>
     <div class="container">
        <div class="col-md-4">
          <h2>Pakete</h2>
          <p> Eine Übersicht der veschiedenen Pakete </p>
          <p><a class="btn btn-success" href="journals/packageManagement" role="button">Zu den Paketen &#187;</a></p>
        </div>
        <div class="col-md-4">
          <h2>Nutzung</h2>
          <p> Nutzungsstatistiken von Paketen und e-Journals </p>
          <p><a class="btn btn-success" href="journals/journalUsage" role="button">Zur Nutzung &#187;</a></p>
       </div>
      </div>
</xsl:template>
		

</xsl:stylesheet>