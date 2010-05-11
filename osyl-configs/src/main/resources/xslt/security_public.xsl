<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<xsl:output cdata-section-elements="text comment description availability label identifier"/>
<!--  =========================================================================== -->

	<xsl:template match="/">
		<xsl:apply-templates select="/*[@access='public' and not(./visible='false')]|*[not(@access) and not(./visible='false')]" />
	</xsl:template>
	
	<xsl:template match="@*|node()[@access='public' and not(./visible='false')]">
	  <xsl:copy>
		<xsl:copy-of select="@*"/> 
	    <xsl:apply-templates select="@*|*[@access='public' and not(./visible='false')]|text()|*[not(@access) and not(./visible='false')]"/>
	  </xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|text()|*[not(@access) and not(./visible='false')]">
	  <xsl:copy-of select="."/>
	</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->

</xsl:stylesheet>