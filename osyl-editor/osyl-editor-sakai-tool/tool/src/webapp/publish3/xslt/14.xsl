<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ====================== Travaux et Examens ================================= -->
<!--  =========================================================================== -->
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>
<xsl:include href="ressources.xsl"/>

<xsl:template match="/">

	&lt;html>
		<xsl:call-template name="head"/>

	&lt;body>
		&lt;div id='mainPage'>
		<!-- ========================================= -->

		<xsl:call-template name="headerPage"/>

		<!-- ============================== -->
		<xsl:call-template name="menu">
				<xsl:with-param name="lang" select="planCours/@lang"/>
				<xsl:with-param name="code" select="planCours/@koId"/>
				<xsl:with-param name="page" select="14"/>
				<xsl:with-param name="numSeance" select="0"/>
		</xsl:call-template>
		<xsl:call-template name="cours"/>
		<xsl:call-template name="professeur"/>

		&lt;div class='titrePage'>
		<xsl:if test="not(planCours/@type='annuaire')">
			<xsl:value-of select="$a14Lbl"/>
		</xsl:if>
		<xsl:if test="planCours/@type='annuaire'">
			<xsl:value-of select="$a14Lbla"/>
		</xsl:if>
		&lt;/div>&lt;!-- titrePage -->

		<xsl:call-template name="separateur"/>

		&lt;div id='contenu14'>
			<xsl:apply-templates select="/planCours/evaluations/*" />
		&lt;/div>&lt;!-- contenu14 -->
		<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> &lt;!-- mainPageSeance -->
	&lt;/body>
	&lt;/html>

</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>