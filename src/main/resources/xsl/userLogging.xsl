<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	xmlns:i18n="xalan://org.mycore.services.i18n.MCRTranslation"
	xmlns:mabxml="http://www.ddb.de/professionell/mabxml/mabxml-1.xsd"
	exclude-result-prefixes="xsl xalan i18n mabxml">

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
					
				<title>UB-Services :: Login</title>

				<script src="{$WebApplicationBaseURL}js/ie-emulation-modes-warning.js"></script>
				<script src="{$WebApplicationBaseURL}js/ie10-viewport-bug-workaround.js"></script>

				<link href="{$WebApplicationBaseURL}css/bootstrap.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/ie10-viewport-bug-workaround.css" rel="stylesheet" />
				<link href="{$WebApplicationBaseURL}css/jumbotron.css" rel="stylesheet" />
				
				<link rel="icon" href="{$WebApplicationBaseURL}img/favicon.ico"/>
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
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand" href="#">UB-Services ::
								Login</a>
						</div>
						<div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav navbar-right">
            <li><a href="index.html">Start</a></li>
          </ul>
        </div><!--/.navbar-collapse -->
					</div>
				</nav>
				<div class="container">
    <div class="col-md-6 col-md-offset-3">
    <h1>Zum FachRef-Portal</h1>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">Anmeldung</h3>
                    </div>
                    
                    <xsl:if test="userLogging/message">
                    <xsl:apply-templates select="userLogging/message">
                    </xsl:apply-templates>
                    </xsl:if>
                    
                    <div class="panel-body">
                        <form name="loginform" action="userLogging" method="POST" accept-charset="UTF-8" role="form">
                        <input type="hidden" name="type" value="login"/>
                            <fieldset>
                                <div class="form-group">
                                    <input class="form-control" placeholder="Kennung" name="email" type="email" autofocus="true"></input>
                                </div>
                                <div class="form-group">
                                    <input class="form-control" placeholder="Passwort" name="password" type="password" value=""></input>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input name="rememberMe" type="checkbox" value="true"> Remember Me</input>
                                    </label>
                                </div>
                                <input class="btn btn-lg btn-success btn-block" type="submit" value="Login"></input>
                            </fieldset>
                        </form>
                        <a href="userRegistration.html">Neuen Nutzer anlegen</a>
                        </div>
            </div>
        </div>
    </div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
   
    <script src="js/ie10-viewport-bug-workaround.js"></script>
			</body>
		</html>
		</xsl:template>
		
		<xsl:template match="message">
		<div class="alert alert-danger">
			<xsl:value-of select="i18n:translate(.)" />
		</div>
		</xsl:template>

</xsl:stylesheet>
