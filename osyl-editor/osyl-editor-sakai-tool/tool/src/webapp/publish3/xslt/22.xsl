<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ====================== FAQ ================================= -->
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
				<xsl:with-param name="page" select="22"/>
				<xsl:with-param name="numSeance" select="0"/>
		</xsl:call-template>
		<xsl:call-template name="cours"/>
		<xsl:call-template name="professeur"/>

		&lt;div class='titrePage'>
		<xsl:if test="not(planCours/@type='annuaire')">
			<xsl:value-of select="$a22Lbl"/>
		</xsl:if>
		<xsl:if test="planCours/@type='annuaire'">
			<xsl:value-of select="$a22Lbla"/>
		</xsl:if>
		&lt;/div>&lt;!-- titrePage -->

		<!--  Ajouter pour hyperliens : Awa 2008-02-07 -->
		&lt;div id='hyperliensRubriques'>
			<xsl:call-template name="jumpRubriques">
					<xsl:with-param name="chemin" select="/planCours/faq"/>
			</xsl:call-template>
			<xsl:call-template name="sep"/>
		&lt;/div>&lt;!-- div hyperliensRubriques -->
			
			
		<!-- Ajouter pour les rubriques : Awa 20078-02-20-->
		&lt;div id='contenu22'>
			<xsl:call-template name="rubriques">
						<xsl:with-param name="chemin" select="/planCours/faq"/>
			</xsl:call-template>
		&lt;/div>&lt;!-- contenu22 -->	
	
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