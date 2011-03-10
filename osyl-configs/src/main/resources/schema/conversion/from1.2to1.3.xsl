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
	
	<!-- change rubrics-->
	<xsl:template match="asmStructure[@xsi:type='PedagogicalStruct']//semanticTag[@type='HEC']">
		<semanticTag type='HEC'>
			<xsl:choose>
				<xsl:when test=".='learningstrat'">objectives</xsl:when>
				<xsl:when test=".='case'">actResBefore</xsl:when>
				<xsl:when test=".='readinglist'">actResBefore</xsl:when>
				<xsl:when test=".='ressinclass'">actResDuring</xsl:when>
				<xsl:when test=".='exercises'">actResAfter</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</semanticTag>
	</xsl:template>
	
	
</xsl:stylesheet>
