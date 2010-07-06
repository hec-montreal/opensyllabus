<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<!--  =========================================================================== -->
<xsl:template match="/">
	<xsl:apply-templates select="/*[@securite='0' or @securite='1' or @securite='2']" />
</xsl:template>

<xsl:template match="@*|node()[@securite='0' or @securite='1' or @securite='2']|text()">
  <xsl:copy>
    <xsl:apply-templates select="@*|*[@securite='0' or @securite='1' or @securite='2']|text()"/>
  </xsl:copy>
</xsl:template>
<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>