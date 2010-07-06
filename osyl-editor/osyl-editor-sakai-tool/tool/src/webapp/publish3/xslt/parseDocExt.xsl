<?xml version="1.0" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
<xsl:output cdata-section-elements="description texte"/>
<!--  =========================================================================== -->
<xsl:template match="/">
	<ressources>
		<xsl:apply-templates select="//ressource[@type='Document']|//ressource[@type='TX_Document']|//ressource[@type='TX_URL']" />
	</ressources>
</xsl:template>

<xsl:template match="ressource">
	<xsl:copy>
		<xsl:for-each select="./@*">
			<xsl:call-template name="identity"/>
		</xsl:for-each>
		<xsl:for-each select="global">
			<xsl:call-template name="identity"/>
		</xsl:for-each>
	</xsl:copy>
</xsl:template>

<!-- =============== transformation identique ================== -->
	<xsl:template name="identity">
		<xsl:copy>
			<xsl:for-each select="./@*|./node()|./*|./text()">
				<xsl:call-template name="identity"/>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>

<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>