<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">
	
	<xsl:include href="navbar.xsl" />

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


	<xsl:variable name="page.title">
		<xsl:text>Bestandspflege - Management von</xsl:text>
		<xsl:value-of select="/stockControlManagement/@username" />
	</xsl:variable>
	
	<xsl:variable name="username">
		<xsl:value-of select="/start/@username" />
	</xsl:variable>
	
	<xsl:variable name="email">
		<xsl:value-of select="/start/@email" />
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

				<title>FachRef-Assistent :: Start</title>

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
				<xsl:apply-templates select="/start/navbar" />
 
    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <h1>Willkommen beim FachRef-Assistenten, <br />
        <xsl:value-of select="start/@loggedInAs" /></h1>
        <p>Bestandskontrolle und -steuerung bequem von einem Punkt</p>
        <p><a class="btn btn-primary btn-lg" href="{$WebApplicationBaseURL}forms/User_Form.xed" role="button">Meine Einstellungen &#187;</a></p>
      </div>
    </div>
    <div class="container">
      <div class="row">
        <div class="col-md-3">
          <h2>Protokoll</h2>
          <p> Das Protokoll gibt eine Übersicht über die Nutzung und den Bestand sowohl von einzelnen Auflagen oder über mehrere Auflagen hinweg. Außerdem können auf über das Protokoll nutzungsbasierte Aussonderungsvorschläge erstellt werden.  </p>
          <p><a class="btn btn-success" href="../protokoll" role="button">Zum Ausleihprotokoll &#187;</a></p>
        </div>
        <div class="col-md-3">
          <h2>Profile</h2>
          <p> Im Bestandspflegemodul können Profile zur Bestandspflege erstellt und verwaltet werden. Auf der Basis dieser Parametersätzen werden Listen von nutzungsbasierte Aussonderungs- und Anschaffungsvorschlägen erstellt. </p>
          <p><a class="btn btn-success" href="profile" role="button">Zu den Profilen &#187;</a></p>
       </div>
       <div class="col-md-3">
          <h2>Hitlisten</h2>
          <p> Das experimentelle Bestands-Tool erlaubt einen Vergleich über verschiedenen Ebenen der Aufstellungssystematik hinweg. Hier können für einzelne Bereiche die Nutzung, der Bestand und deren Zusammensetzung visualisiert werden. </p>
          <p><a class="btn btn-success" href="hitlists" role="button">Zu den Hitlisten &#187;</a></p>
        </div>
       <div class="col-md-3">
          <h2>e-Journals</h2>
          <p> Das experimentelle Bestands-Tool erlaubt einen Vergleich über verschiedenen Ebenen der Aufstellungssystematik hinweg. Hier können für einzelne Bereiche die Nutzung, der Bestand und deren Zusammensetzung visualisiert werden. </p>
          <p><a class="btn btn-success" href="journals" role="button">Zu den e-Journals &#187;</a></p>
        </div>
        
        <div class="col-md-3">
          <h2>Bestand</h2>
          <p> Das experimentelle Bestands-Tool erlaubt einen Vergleich über verschiedenen Ebenen der Aufstellungssystematik hinweg. Hier können für einzelne Bereiche die Nutzung, der Bestand und deren Zusammensetzung visualisiert werden. </p>
          <p><a class="btn btn-success" href="stock" role="button">Zur Bestandsanalyse &#187;</a></p>
        </div>
      </div>
      <hr />
    </div>


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
    <script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
