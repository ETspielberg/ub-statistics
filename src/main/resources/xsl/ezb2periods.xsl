<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output encoding="UTF-8" method="html" media-type="text/html"
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html401/loose.dtd" indent="yes" />
		
	<xsl:template match='/OpenURLResponseXML'>
 	    <ezb>
 	    <xsl:apply-templates select="Full" />
 	    </ezb>
 	  </xsl:template>

<xsl:template match='Full'>
 	    <electronic>
 	    <xsl:apply-templates select="ElectronicData" />
 	    </electronic>
 	    <print>
 	    <xsl:apply-templates select="PrintData" />
 	    </print>
 	  </xsl:template>

<xsl:template match='ElectronicData'>
 	    <sources>
 	    <xsl:apply-templates select="ResultList/Result" />
 	    </sources>
 	  </xsl:template>
 	  
 	  <xsl:template match='Result'>
 	  <source>
 	    <state>
 	    <xsl:value-of select="@state" />
 	    </state>
 	    <xsl:if test="Additionals/Additional[@type='nali']" >
 	    <nali />
 	    </xsl:if>
 	    <xsl:if test="Additionals/Additional[@type='intervall']" >
 	    <period>
 	    <xsl:value-of select="Additionals/Additional[@type='intervall']" />
 	    </period>
 	    </xsl:if>
 	    <xsl:if test="Signature" >
 	    <signature>
 	    <xsl:value-of select="Signature/." />
 	    </signature>
 	    </xsl:if>
 	    <xsl:if test="Period" >
 	    <period>
 	    <xsl:value-of select="Period/." />
 	    </period>
 	    </xsl:if>
 	    </source>
 	  </xsl:template>

<xsl:template match='PrintData'>
 	    <sources>
 	    <xsl:apply-templates select="ResultList/Result" />
 	    </sources>
 	  </xsl:template>
</xsl:stylesheet>