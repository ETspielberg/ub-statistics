<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes" />
		
	<xsl:template match='@*|node()'>
 	    <xsl:param name="ID" />
 	    <!-- default template: just copy -->
 	    <xsl:copy>
 	      <xsl:apply-templates select='@*|node()' />
 	      <xsl:if test="$ID">
 	        <xsl:attribute name="ID">
 	          <xsl:value-of select="$ID" />
 	        </xsl:attribute>
 	      </xsl:if>
 	    </xsl:copy>
 	  </xsl:template>

</xsl:stylesheet>