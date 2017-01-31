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

	<xsl:template match="/">
		<html>
			<head>
				<meta charset="utf-8" />
				<meta http-equiv="X-UA-Compatible" content="IE=edge" />
				<meta name="viewport" content="width=device-width, initial-scale=1" />
				<!-- The above 3 meta tags *must* come first in the head; any other head 
					content must come *after* these tags -->
					
				<title>FachRef-Assistent :: Budget-Analyse</title>

				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/highcharts.src.js"></script>
				<script	type="text/javascript" src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/modules/exporting.src.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/highcharts/${version.highcharts}/themes/grid.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/highcharts.js"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js" language="javascript"></script>
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js" ></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link rel="stylesheet" href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/description_box.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
			</head>
			<body>
			<xsl:apply-templates select="budgetCollector/navbar" />
			<div class="jumbotron">
			<div class="container">
			<h1>Titel nach Etat</h1>
			<p>Titel analysieren, die aus einem bestimmten Etat gehören</p>
			</div>
			</div>
			<xsl:apply-templates select="budgetCollector" />
			<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="budgetCollector">
	<div class="container">
		<xsl:call-template name="form" />
		</div>
	</xsl:template>

	<xsl:template name="form">
	<div  id="form"  class="col-md-12 document-title">
		<h2>
		Abfrage
		</h2>
		</div>
		<div  class="col-md-12">
			<form action="budgetCollector" method="get">
				<table class="table table-bordered">
					<tr>
					<td>
						<label for="subject">Fach  </label>
					</td>
					<td>
						<label for="type">Typ  </label>
					</td>
					<td>
						<label for="special">Spezial  </label>
					</td>
					<td>
						<label for="yearStart">Jahr / Startjahr  </label>
					</td>
					<td>
						<label for="yearEnd">Endjahr  </label>
					</td>
					</tr>
					<tr>
					<td>
					<span class="links">
						<select id="subject" name="subject" class="form-control">
      						<option value="0001">Anglistik</option>
      						<option value="0002">Germanistik</option>
      						<option value="0003">Deutsch als Zweitsprache</option>
      						<option value="0004">Romanistik</option>
      						<option value="0005">Turkistik</option>
      						<option value="0006">Kommunikationswissenschaft</option>
      						<option value="0007">Geschichte</option>
      						<option value="0008">Philosophie</option>
      						<option value="0009">Ev. Theologie</option>
      						<option value="0010">Kath. Theologie</option>
      						<option value="0011">Kunst und Design</option>
      						<option value="0012">Geographie</option>
      						<option value="0013">Soziologie</option>
      						<option value="0014">Politik</option>
      						<option value="0016">Soziale Arbeit</option>
      						<option value="0017">Erziehungswissenschaft</option>
      						<option value="0018">Psychologie</option>
      						<option value="0019">Inst. f. Berufs- u. Weiterbildung</option>
      						<option value="0020">Sport u. Bewegungswissenschaften</option>
      						<option value="0021">BWL/VWL/Jura</option>
      						<option value="0022">Informatik</option>
      						<option value="0023">Wirtschaftsinformatik</option>
      						<option value="0024">BWL/VWL/Jura/Ostasien</option>
      						<option value="0025">Mathematik</option>
      						<option value="0026">Physik</option>
      						<option value="0027">Chemie</option>
      						<option value="0028">Biologie</option>
      						<option value="0029">Informatik</option>
      						<option value="0030">Angewandte Kognitionswissenschaften</option>
      						<option value="0031">E-Technik</option>
      						<option value="0032">Maschinenbau</option>
      						<option value="0034">Bauingenieurwesen</option>
      						<option value="0035">Technik</option>
    					</select>
    				</span>
					</td>
					<td>
					<span class="links">
						<select id="type" name="type" class="form-control">
      						<option value="0000">Monographien</option>
      						<option value="0200">Zeitschriften</option>
      						<option value="0300">Fortsetzungen</option>
      						<option value="0500">E-Zsn</option>
      						<option value="0700">Datenbanken</option>
      						<option value="1900">Sonstige Literaturkosten</option>
    					</select>
    				</span>
					</td>
					<td>
					<span class="links">
						<input id="special" type="text" name="special" size="40" />
    				</span>
					</td>
					<td>
					<span class="links">
						<input id="yearStart" type="text" name="yearStart" value="{@yearStart}" size="20" />
    				</span>
					</td>
					<td>
					<span class="links">
						<input id="yearEnd" type="text" name="yearEnd" value="{@yearEnd}" size="20" />
    				</span>
					</td>
					</tr>
				</table>
				<input type="submit" class="btn btn-sm btn-success" value="Absenden" />
			</form>
			</div>
			<xsl:choose>
		<xsl:when test="count(analysis) &gt; 0">
		<xsl:call-template name="list" />
		</xsl:when>
		<xsl:otherwise>
		<div class="col-md-8 col-md-offset-2 main">
		<p>Es wurden keine Titel gefunden.</p>
		</div>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="list">
	<div class="container-fluid">
		<div class="col-md-12">
	      <div class="alert alert-info" role="alert">
	        <center>Um bibliographische Informationen zu den einzelnen Einträgen zu erhalten, bitte die Maus über die Signatur bewegen.</center> 
	      </div>
				<table id="sortableTable" class="table table-striped">
					<thead>
						<tr>
							<th>Signatur </th>
							<th>Protokoll </th>
							<th>Mittlere Ausleihe </th>
							<th>Trend /Jahr </th>
							<th>Bestand </th>
							<th>Maximale Ausleihe </th>
							<th>Vorschlag </th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="analysis"/>
					</tbody>
				</table>
		</div>
		</div>
	</xsl:template>
	
	<xsl:template match="analysis">
			<tr>
				<xsl:variable name="shelfmark">
					<xsl:value-of select="@shelfmark" />
				</xsl:variable>
				<td  data-toggle="tooltip">
				<xsl:attribute name="title">
				<xsl:value-of select="mab/." />
				</xsl:attribute>
					<xsl:value-of select="$shelfmark" />
				</td>
				<td>
					<a
						href="../../protokoll?shelfmark={$shelfmark}&amp;collections=&amp;exact="
						target="popup">Link</a>
				</td>
				<td>
					<xsl:apply-templates select="meanRelativeLoan" />
				</td>
				<td>
					<xsl:value-of select='format-number(@trend, "#%")' />
				</td>
				<td>
					<xsl:apply-templates select="lastStock" />
				</td>
				<td>
					<xsl:apply-templates select="maxLoansAbs" />
				</td>
				<td>
					<xsl:value-of select="proposedDeletion" />
				</td>
			</tr>
	</xsl:template>


	<xsl:template match="meanRelativeLoan">
		<xsl:value-of select='format-number(., "#%")' />
	</xsl:template>


	<xsl:template match="maxRelativeLoan">
		<xsl:value-of select='format-number(., "#%")' />
	</xsl:template>


	<xsl:template match="lastStock">
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template match="comment">
		<xsl:value-of select="." />
	</xsl:template>
	

</xsl:stylesheet>
