<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:translator="xalan://unidue.ub.i18n.Translator" 
  exclude-result-prefixes="xsl xalan translator">
  
  <xsl:include href="navbar.xsl" />

  <xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" xalan:indent-amount="2" />

  <xsl:param name="baseURL" />
  <xsl:param name="requestURL" />
  <xsl:param name="lang" select="'de'"/>

  <xsl:template match="/bibliothek">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="lageplan.css" />
        <title>
          <xsl:value-of select="translator:translate('title',$lang)" />
          <xsl:text>: </xsl:text>
          <xsl:value-of select="concat(@standort,' ',@signatur)" />
        </title>
      </head>
      <body>
        <xsl:call-template name="openlayers.common" />
        <div class="container">
          <xsl:call-template name="exemplar" />
          <img src="https://www.uni-due.de/imperia/md/images/ub/ub_logo_205.png" class="logo" />
          <xsl:apply-templates select="fachbibliothek[descendant::regal[@mark='true']]" />
		  <xsl:call-template name="qrcode" />
        </div>
      </body>
    </html>
  </xsl:template>
  
  <!-- ==================== Exemplar, das gesucht war ==================== -->

  <xsl:template name="exemplar">
    <div class="ex">
      <div class="signatur">
		<xsl:value-of select="translator:translate('reqshelfmark',$lang)" /> <xsl:text>: </xsl:text>
        <span class="blau">
          <xsl:value-of select="concat(@standort,' ',@signatur)" />
        </span>    
      </div>
    </div>
  </xsl:template>

  <!-- ==================== Standortinformationen, Pläne usw. für eine Fachbibliothek ==================== -->

  <xsl:template match="fachbibliothek">
	<div class="ort">
	  <xsl:apply-templates select="." mode="standort" />
	  <xsl:apply-templates select="descendant::raum[descendant::regal[@mark='true']]" />
	</div>
	<xsl:apply-templates select="." mode="info" />
	<xsl:apply-templates select="." mode="map" />
  </xsl:template>
  
  <!-- ==================== Info Fachbibliothek ausgeben ==================== -->

  <xsl:template match="fachbibliothek" mode="standort">
    <div>
	  <xsl:value-of select="translator:translate('locallibrary',$lang)" /> <xsl:text>: </xsl:text>
      <strong>
        <span class="blau">
          <xsl:apply-templates select="." mode="bezeichnung" />
        </span>
      </strong>
    </div>
  </xsl:template>
  
  <!-- ==================== Raum innerhalb einer Fachbibliothek ausgeben ==================== -->

  <xsl:template match="raum">
    <xsl:apply-templates select="." mode="standort" />
    <xsl:apply-templates select="." mode="plan" />
  </xsl:template>

  <!-- ==================== Raum, Bereice und Regalnummern ausgeben ==================== -->

  <xsl:template match="raum" mode="standort">
    <div>
     <xsl:value-of select="translator:translate('floor',$lang)" /> <xsl:text>: </xsl:text>
      <span class="blau">
        <xsl:apply-templates select="." mode="bezeichnung" />
      </span>
    </div>
    <div>
      <xsl:variable name="regal" select="descendant::regal[@mark='true']" />
      <xsl:choose>
        <xsl:when test="count($regal/ancestor::bereich) &gt; 1">
		<xsl:value-of select="translator:translate('areas',$lang)" /> <xsl:text>: </xsl:text>
        </xsl:when>
        <xsl:otherwise>
		<xsl:value-of select="translator:translate('area',$lang)" /> <xsl:text>: </xsl:text>
        </xsl:otherwise> 
      </xsl:choose>
      <xsl:text> </xsl:text>
      <xsl:for-each select="$regal/ancestor::bereich">
        <xsl:apply-templates select="." mode="bezeichnung" />
        <xsl:text>, </xsl:text>
        <xsl:if test="position() != last()">
         <xsl:value-of select="translator:translate('and',$lang)" /> <xsl:text> </xsl:text>
        </xsl:if>
      </xsl:for-each>
      <xsl:choose>
        <xsl:when test="count($regal) &gt; 1">
		  <xsl:value-of select="translator:translate('shelves',$lang)" /> <xsl:text> </xsl:text>
        </xsl:when>
        <xsl:otherwise>
		  <xsl:value-of select="translator:translate('shelf',$lang)" /> <xsl:text> </xsl:text>
        </xsl:otherwise>
      </xsl:choose>
	  <strong><xsl:value-of select="translator:translate('shelfnumber',$lang)" /></strong>
      <xsl:for-each select="$regal">
        <strong><xsl:value-of select="substring-after(@id,'XMLID_')" /></strong>	<xsl:text> </xsl:text>
        <xsl:if test="position() != last()">
          <xsl:text> + </xsl:text>
        </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="translator:translate('highlighted',$lang)" /><xsl:text>: </xsl:text>
    </div>
  </xsl:template>
  
  <!-- ==================== Regalplan für Raum mit allen markierten Regalen anzeigen ==================== -->

  <xsl:template match="raum" mode="plan">
    <xsl:variable name="link">
      <xsl:value-of select="concat($baseURL,'/regalplan/',@plan,'?lang=',$lang,'&amp;')" />
      <xsl:for-each select="descendant::regal[@mark='true']">
        <xsl:value-of select="concat('regalID=',@id)" />
        <xsl:if test="position() != last()">
          <xsl:text>&amp;</xsl:text>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <div class="plan" id="plan">
      <object type="image/svg+xml" data="{$link}" />
    </div>
    <br />
    <div class="klein">	
	<xsl:value-of select="translator:translate('scaleplan',$lang)" />
    </div>  
  </xsl:template>
  

  
  <!-- ==================== Öffnungszeiten der Fachbibliothek ==================== -->

  <xsl:template match="fachbibliothek" mode="info">
    <div class="fb1">
      <xsl:apply-templates select="." mode="bezeichnung" />
    </div>
    <div class="fbadr">
      <xsl:copy-of select="info/node()" />
    </div>
  </xsl:template>

  <!-- ==================== JavaScript für OpenLayers Kartendarstellung ==================== -->

  <xsl:template name="openlayers.common">
    <script src="http://maps.google.com/maps/api/js?v=3&amp;sensor=false" />
    <script src="http://dev.openlayers.org/OpenLayers.js" />
    <script>
      var projectionWGS1984 = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
      var projectionSphericalMercator = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
    </script>
  </xsl:template>

  <!-- ==================== Lage der Fachbibliothek auf der Karte ==================== -->
  
  <xsl:template match="fachbibliothek" mode="map">
    <span class="mapbezeichnung">
	<xsl:value-of select="translator:translate('locationof',$lang)" /> <xsl:text> </xsl:text>
      <xsl:apply-templates select="." mode="bezeichnung" />
      <xsl:text>:</xsl:text> 
    </span>
    <input class="button" type="button" id="resize{generate-id(.)}" value="vergrößern" />
	<div class="map">
      <div  id="map{generate-id(.)}" class="lageplan" style="display: block" />
	</div>
    <script>
      var map<xsl:value-of select="generate-id(.)" /> = new OpenLayers.Map('map<xsl:value-of select="generate-id(.)" />',
      {
        projection : projectionSphericalMercator,
        displayProjection : projectionWGS1984,
        maxExtent : new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34),
        numZoomLevels : 20,
        maxResolution : 156543,
        units : 'meters'
      });

      map<xsl:value-of select="generate-id(.)" />.addLayer( new OpenLayers.Layer.OSM() );
      map<xsl:value-of select="generate-id(.)" />.addLayer( new OpenLayers.Layer.Google( "Google Maps Straßen", { numZoomLevels : 20 } ) );
      map<xsl:value-of select="generate-id(.)" />.addLayer( new OpenLayers.Layer.Google( "Google Maps Satellit", { type : google.maps.MapTypeId.SATELLITE, numZoomLevels : 20 } ) );
    
      map<xsl:value-of select="generate-id(.)" />.addControl(new OpenLayers.Control.LayerSwitcher());
      map<xsl:value-of select="generate-id(.)" />.addControl(new OpenLayers.Control.PanZoomBar());
      
      var position<xsl:value-of select="generate-id(.)" /> = new OpenLayers.LonLat(<xsl:value-of select="@laengengrad" />,<xsl:value-of select="@breitengrad" />).transform( projectionWGS1984, projectionSphericalMercator);

      var markers<xsl:value-of select="generate-id(.)" /> = new OpenLayers.Layer.Markers( '<xsl:apply-templates select="." mode="bezeichnung" />' );
      map<xsl:value-of select="generate-id(.)" />.addLayer(markers<xsl:value-of select="generate-id(.)" />);
      markers<xsl:value-of select="generate-id(.)" />.addMarker(new OpenLayers.Marker(position<xsl:value-of select="generate-id(.)" />));
      
      map<xsl:value-of select="generate-id(.)" />.setCenter(position<xsl:value-of select="generate-id(.)" />, 16 );
    
      document.getElementById('resize<xsl:value-of select="generate-id(.)" />').onclick = function() {
        var divMap = document.getElementById('map<xsl:value-of select="generate-id(.)" />');
        divMap.style.width = '80%';
        divMap.style.height = '60%';
        divMap.scrollIntoView();
        map<xsl:value-of select="generate-id(.)" />.updateSize();
      }
    </script>

  </xsl:template>
  
  <!-- ==================== Bezeichnung ausgeben, möglichst sprachabhängig  ==================== -->
  
  <xsl:template match="*" mode="bezeichnung">
    <xsl:choose>
      <xsl:when test="@i18n">
        <xsl:value-of select="translator:translate(@i18n,$lang)" />
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@bezeichnung" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- ==================== QR-Code ==================== -->

  <xsl:template name="qrcode" >
    <div class="qrcode"><img alt="QR-Code" src="w/chart?cht=qr&amp;chs=350x350&amp;chld=L&amp;choe=UTF-8&amp;chl={$requestURL}" />
    <span class="klein1">
	<xsl:value-of select="translator:translate('permalink',$lang)" />
    </span>
  </div>
  </xsl:template>

</xsl:stylesheet>
