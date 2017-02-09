<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

	<xsl:include href="mabxml-isbd-reduced.xsl" />
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

	<xsl:variable name="who">
		<xsl:value-of select="/ignored/@loggedInAs" />
	</xsl:variable>
	<xsl:variable name="stockControl">
		<xsl:value-of select="/ignored/@stockControl" />
	</xsl:variable>
	<xsl:variable name="identifier">
		<xsl:value-of select="/ignored/@identifier" />
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

				<title>FachRef-Assistent :: Aussonderung</title>

				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery/${version.jquery}/jquery.min.js"></script>
				<script type="text/javascript"> jQuery.noConflict(); </script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.min.js"></script>
				<script type="text/javascript"
					src="{$WebApplicationBaseURL}webjars/datatables/${version.datatables}/js/jquery.dataTables.min.js"
					language="javascript"></script>
				<!-- <script type="text/javascript" src="{$WebApplicationBaseURL}js/dataTables.bootstrap.min.js"></script> -->
				<script type="text/javascript" src="{$WebApplicationBaseURL}js/DT-bootstrap.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>


				<link
					href="{$WebApplicationBaseURL}webjars/jquery-ui/${version.jquery}/jquery-ui.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css"
					rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dashboard.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/dataTables.bootstrap.min.css"
					rel="stylesheet" />

				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico" />
			</head>
			<body>
				<xsl:apply-templates select="Ignored/navbar" />
				<xsl:apply-templates select="Ignored" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="Ignored">
		<div class="container">
			<div class="col-md-10 col-md-offset-1">
				<h1>Werk von zukünftigen Analysen ausschließen</h1>
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">Begründung</h3>
					</div>
					<div class="panel-body">
						<form id="ignoreForm" name="ignore" action="ignore" method="POST"
							accept-charset="UTF-8" role="form">
							<input type="hidden" name="who" value="{@who}" />
							<input type="hidden" name="identifier" value="{@identifier}" />
							<input type="hidden" name="stockControl" value="{@stockControl}" />
							<fieldset>
								<div class="form-group">
									<label class="col-md-4 control-label" for="Zeitraum">Zeitraum</label>
									<div class="col-md-8">
										<input id="expire" name="expire" placeholder="x Jahre"
											class="form-control input-md" type="text" />
										<span class="help-block">Zeitraum in Jahren, in denen dieses
											Werk nicht berücksichtigt wird.</span>
									</div>
								</div>

								<!-- Textarea -->
								<div class="form-group">
									<label class="col-md-4 control-label" for="comment">Kommentar</label>
									<div class="col-md-8">
										<textarea class="form-control" id="comment" name="comment">Grund: </textarea>
									</div>
								</div>

								<div class="form-group">
									<label class="col-md-4 control-label" for="checkboxes">Für immer
										ignorieren?</label>
									<div class="col-md-8">
										<input type="checkbox" name="infiniteExpire" value="true"/>
									</div>
								</div>

								<!-- Button (Double) -->
								<div class="form-group">
									<label class="col-md-4 control-label" for="submit"></label>
									<div class="col-md-8">
										<input type="submit" class="btn btn-success"
											value="Absenden" />
										<a class="btn btn-danger" href="deletionList?stockControl={@stockControl}"
											role="button">Abbrechen</a>
									</div>
								</div>
							</fieldset>
						</form>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>