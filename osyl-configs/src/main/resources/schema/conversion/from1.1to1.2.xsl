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
	<xsl:template match="asmResource[@xsi:type='Assignment']">
		<asmResource xsi:type="Entity">
			<xsl:attribute name="access"><xsl:value-of select="@access" /></xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
			<modified>
				<xsl:value-of select="./modified" />
			</modified>
			<xsl:variable name="original_uri" select="./identifier[@type='uri']" />
			<identifier type="uri">
				<xsl:text>/assignment/</xsl:text>
				<xsl:call-template name="getAssignmentId">
					<xsl:with-param name="uri"
						select="substring-before($original_uri,'&amp;')" />
				</xsl:call-template>
			</identifier>
			<provider>assignment</provider>
		</asmResource>
	</xsl:template>


	<xsl:template name="getAssignmentId">
		<xsl:param name="uri" />
		<xsl:choose>
			<xsl:when test="contains($uri,'/')">
				<xsl:call-template name="getAssignmentId">
					<xsl:with-param name="uri" select="substring-after($uri,'/')" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$uri" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
