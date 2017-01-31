<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsl xalan ">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		xalan:indent-amount="2" />

	<xsl:template match="/systemCodeAnalysis">
		<documentAnalyses>
			<xsl:apply-templates select="documentAnalysis" />
					<systemCodeData>
			<xsl:copy-of select="stats/." />
		</systemCodeData>
		</documentAnalyses>

	</xsl:template>

	<xsl:template match="documentAnalysis">
		<stats>
		<xsl:attribute name="shelfmark">
			<xsl:value-of select="@shelfmark" />
		</xsl:attribute>
		<xsl:apply-templates select="analysis[@key='scp']" />
		</stats>
	</xsl:template>

	<xsl:template match="analysis[@key='scp']" >
			<actualNumberOfItems>
		<xsl:value-of select="lastStock" />
		</actualNumberOfItems>
		<numberItemsTotal>
		<xsl:value-of select="numbers/itemsTotal" />
		</numberItemsTotal>
		<itemsLBS>
		<xsl:value-of select="numbers/itemsLBS" />
		</itemsLBS>
		<actualItemsLBS>
		<xsl:value-of select="numbers/actualItemsLBS" />
		</actualItemsLBS>
			<xsl:apply-templates select="daystimesX" />
	</xsl:template>	
	
	<xsl:template match="daystimesX">
		<xsl:copy-of select="node()" />
	</xsl:template>	
</xsl:stylesheet>