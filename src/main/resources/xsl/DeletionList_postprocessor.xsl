<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output encoding="UTF-8" method="xml" media-type="text/xml"
		doctype-public="deletionList"/>

		
	<xsl:template match="/deletionList">
	<deletionList>
		<xsl:copy-of select="stockControlProperties/." />
	<deletions>
		<xsl:copy-of select="analysis" />
	</deletions>
	</deletionList>
	</xsl:template>

</xsl:stylesheet>