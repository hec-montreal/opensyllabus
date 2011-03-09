<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	exclude-result-prefixes="xs fn">
	<xsl:output cdata-section-elements="text comment description availability label identifier" />
	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" omit-xml-declaration="no" />
		
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<!-- remove news element -->
	<xsl:template match="asmStructure[@xsi:type='NewsStruct']">
	</xsl:template>
	<xsl:template match="asmContext[.//asmResource[@xsi:type='News']]">
	</xsl:template>
	
</xsl:stylesheet>
