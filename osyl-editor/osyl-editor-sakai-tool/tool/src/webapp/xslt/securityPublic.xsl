<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<xsl:output cdata-section-elements="text uri label"/>
<!--  =========================================================================== -->

	<xsl:template match="/">
		<xsl:apply-templates select="/*[@scrty='public']|*[not(@scrty)]" />
	</xsl:template>
	
	<xsl:template match="@*|node()[@scrty='public']">
	  <xsl:copy>
		<xsl:copy-of select="@*"/> 
	    <xsl:apply-templates select="@*|*[@scrty='public']|text()|*[not(@scrty)]"/>
	  </xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|text()|*[not(@scrty)]">
	  <xsl:copy-of select="."/>
	</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->

</xsl:stylesheet>