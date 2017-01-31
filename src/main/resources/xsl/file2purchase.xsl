<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsl xalan ">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		xalan:indent-amount="2" />

	<xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>
	
	<xsl:template match="temporalEvolution" />
	<xsl:template match="numbers" />
	<xsl:template match="daystimesX" />
	<xsl:template match="meanRelativeLoan" />
	<xsl:template match="maxRelativeLoan" />
	<xsl:template match="maxLoansAbs" />
	<xsl:template match="proposedDeletion" />	
	<xsl:template match="finalDeletion" />		
</xsl:stylesheet>