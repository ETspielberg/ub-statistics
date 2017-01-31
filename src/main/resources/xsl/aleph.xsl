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
					
				<title>UB-Services :: Protokoll</title>

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
				<xsl:apply-templates select="aleph/navbar"/>
				<xsl:apply-templates select="*" />
				<script src="{$WebApplicationBaseURL}js/bootstrap.min.js"/>
			</body>
		</html>
	</xsl:template>

	<xsl:key name="itemByID" match="item" use="concat(../../@key,'#',@id)" />
	<xsl:key name="eventsOfItem" match="event"
		use="concat(../../@key,'#',@item)" />
	<xsl:key name="eventsByDocType" match="event"
		use="concat(../../@key,'.',@type)" />
	<xsl:key name="eventsByDocTypeYear" match="event"
		use="concat(../../@key,'.',@type,'.',@year)" />

	<xsl:variable name="page.title">
		<xsl:text>Ausleihprotokoll</xsl:text>
		<xsl:for-each select="/aleph">
			<xsl:for-each select="@shelfmark">
				<xsl:text> für </xsl:text>
				<xsl:value-of select="." />
			</xsl:for-each>
			<xsl:if test="'true' = @exact">
				<xsl:text> (exakt)</xsl:text>
			</xsl:if>
			<xsl:call-template name="filter" />
		</xsl:for-each>
	</xsl:variable>

	<xsl:template match="aleph">
	<div class="col-sm-2 col-md-1 sidebar">
          <ul class="nav nav-sidebar">
          <li>
          <a href="#form" > Neue Abfrage </a>
          </li>
            <xsl:apply-templates select="document" mode="tab-label">
							<xsl:sort select="@edition" data-type="number" order="descending" />
							<xsl:sort select="@callNo" />
			</xsl:apply-templates>
          </ul>
    </div>
	<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="form" />
		</div>
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="editions" />
		</div>
		<div class="col-sm-9 col-md-10 col-md-offset-1 main">
		<xsl:call-template name="documents" />
		</div>
	</xsl:template>

	<xsl:template name="form">
	<div  id="form"  class="col-md-12 document-title">
		<h2>
		Abfrage
		</h2>
		</div>
		<div  class="col-md-12">
			<form action="protokoll" method="get">
				<table class="table table-bordered">
					<tr>
						<td align="right">
							<label for="shelfmark">Signatur:  </label>
						</td>
						<td>
							<span class="links">
								<input id="shelfmark" type="text" name="shelfmark" value="{@shelfmark}" size="20"  autofocus="true"/>
							</span>
							<input type="submit" class="btn btn-sm btn-success" value="Absenden" />
						</td>
					</tr>
					<tr>
						<td />
						<td>
						<span class="links">
							<input id="exakt" type="checkbox" name="exact" value="true">
							
								<xsl:if test="@exact='true'">
									<xsl:attribute name="checked">checked</xsl:attribute>
								</xsl:if>
							</input>
						</span>
						<label for="exakt"> exakte Übereinstimmung, d.h. keine anderen
								Auflagen oder Bände</label>
						</td>
					</tr>
					<tr>
						<td align="right">
							<label for="collections">Standorte:</label>
						</td>
						<td>
						<span class="links">
							<input id="collections" type="text" name="collections"
								value="{@collections}" size="20" />
						</span>
							<label for="collections"> dreistellig, z.B. E31, E?3, E??, ??3 (mehrere
								durch Leerzeichen trennen)</label>
						</td>
					</tr>
					<tr>
						<td align="right">
							<label for="materials">Materialien:</label>
						</td>
						<td>
						<span class="links">
							<input id="materials" type="text" name="materials" value="{@materials}"
								size="20" />
								</span>
							<label for="materials"> z.B. BOOK, CDROM (mehrere durch Leerzeichen
								trennen)</label>
						</td>
					</tr>
				</table>
			</form>
			</div>
	</xsl:template>

	<xsl:template name="editions">
		<xsl:if test="count(document) &gt; 1">
			<div class="table-responsive">
				<form action="protokoll/analytics" method="get" target="popup">
					<input type="hidden" name="materials" value="{@materials}" />
					<input type="hidden" name="yearsToAverage" value="" />
					<input type="hidden" name="collections" value="{@collections}" />
					<input type="hidden" name="exact" value="{@exact}" />
					<table class="table table-striped">

						<xsl:variable name="tmp" xmlns:date="http://exslt.org/dates-and-times">
							<xsl:value-of select="date:year()" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-1" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-2" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-3" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-4" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-5" />
							<xsl:text> </xsl:text>
							<xsl:value-of select="date:year()-6" />
							<xsl:text> </xsl:text>
						</xsl:variable>
						<xsl:variable name="theLastYears" select="xalan:tokenize($tmp)" />

						<thead>
							<tr>
								<th colspan="4">
									Es wurden
									<xsl:value-of select="count(document)" />
									Titelaufnahmen gefunden:
								</th>
								<th colspan="2">Bestand</th>
								<th colspan="{count($theLastYears)}">Ausleihen</th>
							</tr>
							<tr>
								<th>
									<input type="submit" class="btn btn-sm btn-success" value="?" />
								</th>
								<th>Aufl.</th>
								<th>Jahr</th>
								<th>Signatur</th>
								<th>Ess</th>
								<th>Dui</th>
								<xsl:for-each select="$theLastYears">
									<xsl:sort data-type="number" order="descending" />
									<th>
										<xsl:value-of select="." />
									</th>
								</xsl:for-each>
							</tr>
						</thead>
						<tbody>
							<xsl:for-each select="document">
								<xsl:sort select="@edition" data-type="number" order="descending" />
								<xsl:sort select="@callNo" />
								<tr>
									<td>
										<input type="checkbox" name="docNumber" value="{@key}" />
									</td>
									<td class="number">
										<a href="#tab-{@key}" >
											<xsl:value-of select="@edition" />
											. Aufl.
											<xsl:text />
										</a>
									</td>
									<td>
										<xsl:value-of
											select="mabxml:datensatz/mabxml:feld[@nr='425'][@ind='a']/mabxml:uf[@code='a']" />
									</td>
									<td>
										<xsl:value-of select="@callNo" />
									</td>
									<td class="number stock">
										<xsl:value-of
											select="count(items/item[not(starts-with(@collection,'D'))])" />
									</td>
									<td class="number stock">
										<xsl:value-of
											select="count(items/item[starts-with(@collection,'D')])" />
									</td>
									<xsl:variable name="document" select="." />
									<xsl:for-each select="$theLastYears">
										<xsl:sort data-type="number" order="descending" />
										<xsl:variable name="year">
											<xsl:value-of select="." />
										</xsl:variable>
										<td class="number loans">
											<xsl:variable name="numLoans">
												<xsl:for-each select="$document">
													<xsl:value-of
														select="count(key('eventsByDocTypeYear',concat($document/@key,'.loan.',$year)))" />
												</xsl:for-each>
											</xsl:variable>
											<xsl:if test="$numLoans &gt; 0">
												<xsl:value-of select="$numLoans" />
											</xsl:if>
										</td>
									</xsl:for-each>
								</tr>
							</xsl:for-each>
						</tbody>
					</table>
				</form>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="documents">
		<xsl:choose>
			<xsl:when test="document">
				<div id="tabs">
					<xsl:apply-templates select="document" mode="tab-content">
						<xsl:sort select="@edition" data-type="number" order="descending" />
						<xsl:sort select="@callNo" />
					</xsl:apply-templates>
				</div>
				
				<!-- <xsl:apply-templates select="document"> <xsl:sort select="@edition" 
					data-type="number" order="descending" /> <xsl:sort select="@callNo" /> </xsl:apply-templates> -->
			</xsl:when>
			<xsl:otherwise>
			<div class="col-sm-9 col-md-10 col-md-offset-2 main">
					<p>Es wurden keine Titel gefunden. Bitte geben Sie eine gültige
						Signatur ein.</p>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="filter">
		<xsl:for-each select="/aleph">
			<xsl:if test="@collections or @materials">
				<xsl:text> (gefiltert nach </xsl:text>
				<xsl:for-each select="@collections">
					<xsl:value-of select="." />
				</xsl:for-each>
				<xsl:if test="@collections and @materials">
					,
				</xsl:if>
				<xsl:for-each select="@materials">
					<xsl:value-of select="." />
				</xsl:for-each>
				<xsl:text>)</xsl:text>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="document" mode="tab-label">
		<li>
			<a href="#tab-{@key}" >
				<xsl:value-of select="@edition" />
				. Aufl.
				<xsl:text />
			</a>
		</li>
	</xsl:template>

	<xsl:template match="document" mode="tab-content">
		<div id="tab-{@key}"  class="col-md-12 document-title">
		<h2>
		<xsl:value-of select="@edition" />
				. Auflage
				<xsl:text />
		</h2>
		</div>
		<div  class="col-md-12">
			<xsl:apply-templates select="." mode="chart" />
			<xsl:call-template name="statistics" />
			<div class="well">
			<xsl:apply-templates select="mabxml:datensatz" />
			<xsl:call-template name="links" />
			</div>
			<xsl:call-template name="items" />
			<xsl:call-template name="events" />
		</div>
	</xsl:template>

	<xsl:template match="document">
		<article>
			<xsl:if test="string-length(@callNo) &gt; 0">
				<hgroup>
					<h2>
						<xsl:value-of select="@edition" />
						. Auflage:
						<xsl:value-of select="@callNo" />
					</h2>
				</hgroup>
			</xsl:if>
			<a name="{@key}" />
			<xsl:apply-templates select="mabxml:datensatz" />

			<xsl:call-template name="links" />
			<xsl:call-template name="statistics" />
			<xsl:apply-templates select="." mode="chart" />
			<xsl:call-template name="items" />
			<xsl:call-template name="events" />
		</article>
	</xsl:template>

	<xsl:template name="links">
	  <table class="table">
		<tbody>
		  <tr>
		    <td>
		      <xsl:variable name="id1" select="concat('000000',mabxml:datensatz/@id)" />
			  <xsl:variable name="id2" select="substring($id1,string-length($id1)-8)" />
			  <a class="btn btn-success" href="http://primo.ub.uni-due.de/UDE:UDEALEPH{$id2}" role="button">Titel in Primo anzeigen</a>
		    </td>
		    <td>
		      <a class="btn btn-success" href="protokoll/analytics?yearsToAverage=&amp;docNumber={@key}&amp;collections={@collections}&amp;materials={@materials}&amp;exact={@exact}" role="button">Benutzergruppen-Analyse</a>
		    </td>
		  </tr>
		</tbody>
	  </table>
	</xsl:template>

	<xsl:template name="statistics">
		<xsl:if test="events">
			<table class="table table-striped">
				<thead>
					<tr>
						<th />
						<xsl:for-each select="years/year">
							<th>
								<xsl:value-of select="." />
							</th>
						</xsl:for-each>
						<th>alle</th>
					</tr>
				</thead>
				<tbody>
					<xsl:call-template name="sum">
						<xsl:with-param name="type">loan</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="avg">
						<xsl:with-param name="type">loan</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="sum">
						<xsl:with-param name="type">cald</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="sum">
						<xsl:with-param name="type">request</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="avg">
						<xsl:with-param name="type">request</xsl:with-param>
					</xsl:call-template>
				</tbody>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="minimax">
		<xsl:param name="mode" />
		<xsl:param name="type" />
		<xsl:param name="type2" />

		<xsl:variable name="key" select="@key" />

		<tr>
			<th>
				<xsl:value-of select="$mode" />
				<xsl:text>. </xsl:text>
				<xsl:value-of select="i18n:translate(concat('aleph.eventType.',$type,'.status'))" />
				<xsl:text>:</xsl:text>
			</th>
			<xsl:for-each select="years/year">
				<td>
					<xsl:variable name="selected" select="key('eventsByDocTypeYear',concat($key,'.',$type,'.',.)) | key('eventsByDocTypeYear',concat($key,'.',$type2,'.',.))" />
					<xsl:choose>
						<xsl:when test="$selected">
							<xsl:for-each select="$selected">
								<xsl:sort data-type="number" select="@counter" />
								<xsl:call-template name="outputMinMax">
									<xsl:with-param name="mode" select="$mode" />
									<xsl:with-param name="type" select="$type" />
									<xsl:with-param name="type2" select="$type2" />
								</xsl:call-template>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="position() = 1">
							0
						</xsl:when>
						<xsl:otherwise>
							&#171;
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:for-each>
			<td>
				<xsl:call-template name="totalMinMax">
					<xsl:with-param name="mode" select="$mode" />
					<xsl:with-param name="type" select="$type" />
					<xsl:with-param name="type2" select="$type2" />
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="totalMinMax">
		<xsl:param name="mode" select="'max'" />
		<xsl:param name="type" select="'loan'" />
		<xsl:param name="type2" select="'return'" />

		<xsl:variable name="selected" select="key('eventsByDocType',concat(@key,'.',$type)) | key('eventsByDocType',concat(@key,'.',$type2))" />
		<xsl:choose>
			<xsl:when test="$selected">
				<xsl:for-each select="$selected">
					<xsl:sort data-type="number" select="@counter" />
					<xsl:call-template name="outputMinMax">
						<xsl:with-param name="mode" select="$mode" />
						<xsl:with-param name="type" select="$type" />
						<xsl:with-param name="type2" select="$type2" />
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				0
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="outputMinMax">
		<xsl:param name="mode" />
		<xsl:param name="type" />
		<xsl:param name="type2" />

		<xsl:variable name="value" select="number(@counter)" />

		<xsl:choose>
			<xsl:when test="(position() = 1) and ($mode='min')">
				<xsl:choose>
					<xsl:when test="@end">
						<xsl:value-of select="$value - 1" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="(position() = last()) and ($mode='max')">
				<xsl:choose>
					<xsl:when test="@end">
						<xsl:value-of select="$value" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value + 1" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="avg">
		<xsl:param name="type" />
		<xsl:variable name="key" select="concat(@key,'.',$type)" />
		<tr>
			<th>
				Ø
				<xsl:value-of select="i18n:translate(concat('aleph.eventType.',$type,'.duration'))" />
				(Tage):
			</th>
			<xsl:for-each select="years/year">
				<td>
					<xsl:variable name="selected" select="key('eventsByDocTypeYear',concat($key,'.',.))[@days]" />
					<xsl:choose>
						<xsl:when test="$selected">
							<xsl:value-of select="round(sum($selected/@days) div count($selected))" />
						</xsl:when>
						<xsl:otherwise>
							0
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</xsl:for-each>
			<td>
				<xsl:variable name="selected" select="key('eventsByDocType',$key)[@days]" />
				<xsl:choose>
					<xsl:when test="$selected">
						<xsl:value-of select="round(sum($selected/@days) div count($selected))" />
					</xsl:when>
					<xsl:otherwise>
						0
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="sum">
		<xsl:param name="type" />

		<xsl:variable name="key" select="concat(@key,'.',$type)" />

		<tr>
			<th>
				<xsl:value-of
					select="i18n:translate(concat('aleph.eventType.',$type,'.plural'))" />
				:
			</th>
			<xsl:for-each select="years/year">
				<td>
					<xsl:value-of
						select="count(key('eventsByDocTypeYear',concat($key,'.',.)))" />
				</td>
			</xsl:for-each>
			<td>
				<xsl:value-of select="count(key('eventsByDocType',$key))" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="document[events/event]" mode="chart">
		<!-- highcharts.js will find this and use the docnumber to retrieve chart 
			data -->
		<div data-docnumber="{@key}" class="highchart" />
	</xsl:template>

	<xsl:template name="numItems">
		<xsl:value-of select="count(items/item)" />
		<xsl:text> Exemplar(e)</xsl:text>
		<xsl:call-template name="filter" />
	</xsl:template>

	<xsl:template name="items">
		<xsl:choose>
			<xsl:when test="items">
			<h3>
			Exemplarübersicht
			</h3>
				<table id="sortableTable-items-{@key}" class="table table-striped">
					<thead>
						<tr>
							<xsl:call-template name="itemCommonTH" />
							<th>Medium:</th>
							<th>Ausleihen:</th>
							<th>Status:</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="items/item">
							<xsl:sort select="@callNo" data-type="text" order="ascending" />
							<xsl:sort select="@id" data-type="number" order="ascending" />
						</xsl:apply-templates>
					</tbody>
				</table>
				<!-- <script>jQuery(document).ready(function() {jQuery('#itemTable-{@key}').DataTable();});</script> -->
			</xsl:when>
			<xsl:otherwise>
				<p>Keine Exemplare für diesen Titel gefunden.</p>
			</xsl:otherwise>
		</xsl:choose>
		
		
		
	</xsl:template>

	<xsl:template name="events">
		<xsl:choose>
			<xsl:when test="events">
			<h3>
			<xsl:text>Ereignisprotokoll</xsl:text>
			<xsl:call-template name="filter" />
			</h3>
				<table id="sortableTable-events-{@key}" class="table table-striped">
					<thead>
						<tr>
							<th>Datum von:</th>
							<th>Datum bis:</th>
							<th>Tage:</th>
							<th>Ereignis:</th>
							<th>Benutzer:</th>
							<xsl:call-template name="itemCommonTH" />
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="events/event" />
					</tbody>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<p>Keine Buchungen für diesen Titel gefunden.</p>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="itemCommonTH">
		<th>Ort:</th>
		<th>Signatur:</th>
	</xsl:template>

	<xsl:template match="item">
		<tr>
			<xsl:apply-templates select="." mode="common" />
			<td>
				<xsl:value-of select="@material" />
			</td>
			<td>
				<xsl:value-of
					select="count(key('eventsOfItem',concat(../../@key,'#',@id))[@type='loan'])" />
			</td>
			<td>
				<xsl:if test="@status">
					<xsl:value-of select="i18n:translate(concat('aleph.itemStatus.',@status))" />
				</xsl:if>
				<xsl:if test="@status and @process">
					/
				</xsl:if>
				<xsl:if test="@process">
					<xsl:value-of
						select="i18n:translate(concat('aleph.processStatus.',@process))" />
				</xsl:if>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="item" mode="common">
		<td>
			<xsl:value-of select="@collection" />
		</td>
		<td>
			<xsl:value-of select="@callNo" />
		</td>
	</xsl:template>

	<xsl:template match="event">
		<tr>
			<td>
				<xsl:value-of select="@date" />
			</td>
			<td>
				<xsl:value-of select="@end" />
			</td>
			<td>
				<xsl:value-of select="@days" />
			</td>
			<td>
				<xsl:value-of select="i18n:translate(concat('aleph.eventType.',@type))" />
			</td>
			<td>
				<xsl:variable name="borrowerLabel" select="i18n:translate(concat('aleph.borrowerStatus.',@borrower))" />
				<xsl:choose>
					<xsl:when test="string-length(@borrower) = 0" />
					<xsl:when test="string-length($borrowerLabel) &gt; 3">
						<xsl:value-of select="$borrowerLabel" />
					</xsl:when>
					<xsl:otherwise>
						Sonderausweis
					</xsl:otherwise>
				</xsl:choose>
			</td>
			<xsl:apply-templates select="key('itemByID',concat(../../@key,'#',@item))"
				mode="common" />
		</tr>
	</xsl:template>

</xsl:stylesheet>
