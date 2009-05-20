<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<!--  =========================================================================== -->

	<xsl:template match="/">
		<xsl:apply-templates select="/*[@scrty='public' or @scrty='onsite' or @scrty='attendee']|*[not(@scrty)]" />
	</xsl:template>
	
	<xsl:template match="@*|node()[@scrty='public' or @scrty='onsite' or @scrty='attendee']">
	  <xsl:copy>
	  	<xsl:copy-of select="@*"/> 
	    <xsl:apply-templates select="@*|*[@scrty='public' or @scrty='onsite' or @scrty='attendee']|text()|*[not(@scrty)]"/>
	  </xsl:copy>
	</xsl:template>

	<xsl:template match="@*|text()|*[not(@scrty)]">
	  <xsl:copy-of select="."/>
	</xsl:template>
<!-- ========================================================== -->
<!-- ========================================================== -->

</xsl:stylesheet>