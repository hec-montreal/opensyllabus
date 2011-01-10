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
	
	<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']/location">
		<location>
			<xsl:choose>
				<xsl:when test=".='À la maison' or .='At Home' or .='A la casa'">home</xsl:when>
				<xsl:when test=".='En classe' or .='In Class' or .='En la classe'">inClass</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</location>
	</xsl:template>
	
	<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']/submition_type">
		<submition_type>
			<xsl:choose>
				<xsl:when test=".='Électronique' or .='Electronic' or .='Electronica'">elect</xsl:when>
				<xsl:when test=".='Oral'">oral</xsl:when>
				<xsl:when test=".='Papier' or .='Paper' or .='Papel'">paper</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</submition_type>
	</xsl:template>
	
	<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']/mode">
		<mode>
			<xsl:choose>
				<xsl:when test=".='Individuel' or .='Individual'">individual</xsl:when>
				<xsl:when test=".='En équipe' or .='Team'">team</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>		
		</mode>
	</xsl:template>
	
	<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']/assessmentType">
		<assessmentType>
			<xsl:choose>
				<xsl:when test=".='Examen intra' or .='Midterm Exam' or .='Examen parcial'">intra_exam</xsl:when>
				<xsl:when test=".='Examen final' or .='Final Exam'">final_exam</xsl:when>
				<xsl:when test=".='Travail' or .='Work' or .='Trabajo'">session_work</xsl:when>
				<xsl:when test=".='Participation' or .='Participación'">participation</xsl:when>
				<xsl:when test=".='Test/Quiz'">quiz</xsl:when>
				<xsl:when test=".='Autre' or .='Other'">other</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</assessmentType>
	</xsl:template>
	
</xsl:stylesheet>
