<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<!--  =========================================================================== -->
<xsl:template match="/">
	<xsl:if test="//*[@securite='2']">
		<niv2/>
	</xsl:if>
</xsl:template>

<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>