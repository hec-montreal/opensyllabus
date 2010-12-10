<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="no"/>
	<xsl:output cdata-section-elements="text comment description availability label identifier"/>
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="asmUnit[@xsi:type='NewsUnit']/label">
		<xsl:choose>
			<xsl:when test="//OSYL/CO/language='FR-CA'"><label>Nouvelles</label></xsl:when>
			<xsl:when test="//OSYL/CO/language='ES'"><label>Novedades</label></xsl:when>
			<xsl:otherwise><label>News</label></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="asmUnitStructure[@xsi:type='NewsUnitStruct']/label">
		<xsl:choose>
			<xsl:when test="//OSYL/CO/language='FR-CA'"><label>Nouvelles</label></xsl:when>
			<xsl:when test="//OSYL/CO/language='ES'"><label>Novedades</label></xsl:when>
			<xsl:otherwise><label>News</label></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
