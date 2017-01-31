<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xsl xalan ">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		xalan:indent-amount="2" />

	<xsl:template match="/">
		<xsl:apply-templates select="systemCodeAnalysis" />
	</xsl:template>

	<xsl:template match="systemCodeAnalysis">
		<systemstelle>
			<xsl:apply-templates select="stats" />
			<parameters>
				<xsl:apply-templates select="stockControlProperties" />
			</parameters>
		</systemstelle>
	</xsl:template>

	<xsl:template match="stockControlProperties">
		<xsl:copy-of select="collections" />
		<xsl:copy-of select="materials" />
		<xsl:copy-of select="yearsToAverage" />
	</xsl:template>

	<xsl:template match="stats">
		<stats>
		<numberOfWorks>
		<xsl:value-of select="numberOfWorks" />
		</numberOfWorks>
		<actualNumberOfItems>
		<xsl:value-of select="sum(../documentAnalysis/analysis[@key='scp']/lastStock)" />
		</actualNumberOfItems>
		<numberItemsTotal>
		<xsl:value-of select="sum(../documentAnalysis/analysis[@key='scp']/numbers/itemsTotal)" />
		</numberItemsTotal>
		<itemsLBS>
		<xsl:value-of select="sum(../documentAnalysis/analysis[@key='scp']/numbers/itemsLBS)" />
		</itemsLBS>
		<actualItemsLBS>
		<xsl:value-of select="sum(../documentAnalysis/analysis[@key='scp']/numbers/actualItemsLBS)" />
		</actualItemsLBS>
			<xsl:apply-templates select="analysis/daystimesX" />
		</stats>
	</xsl:template>

	<xsl:template match="daystimesX">
		<xsl:copy-of select="node()" />
	</xsl:template>

</xsl:stylesheet>