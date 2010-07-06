<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ===================== Liste des exercices ================================= -->
<!--  =========================================================================== -->
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>
<xsl:include href="ressources.xsl"/>

<xsl:template match="/">

	&lt;html>
		<xsl:call-template name="head"/>

	&lt;body>
		&lt;div id='mainPagePlanImprimable'>
		<!-- ========================================= -->

	<xsl:call-template name="headerPage"/>

	<!-- ============================== -->
	<xsl:call-template name="cours"/>
	<xsl:call-template name="professeur"/>


	&lt;div class='titrePage'>
		<xsl:value-of select="$a18Lbl"/>
	&lt;/div><!-- titrePage -->
	<!-- ============================== -->

	&lt;div id='contenu18'>

	<xsl:if test="//enonces">
		&lt;div class='rubrique'>
			&lt;div class="logoRubrique">&lt;/div>&lt;/!-- logoRubrique -->
			&lt;div class='titreRubrique'>
				<xsl:value-of select="$enoncesLbl"/>
			&lt;/div>&lt;/!-- titreRubrique -->
			&lt;div class='contenuRubrique'>
				<xsl:for-each select="//enonces">
					<xsl:call-template name="enonce-solution"/>
				</xsl:for-each>
			&lt;/div>&lt;/!-- contenuRubrique -->
		&lt;/div>&lt;/!-- div rubrique -->
	</xsl:if>

	<xsl:if test="//solutions">
		&lt;div class='rubrique'>
			&lt;div class="logoRubrique">&lt;/div>&lt;/!-- logoRubrique -->
			&lt;div class='titreRubrique'>
				<xsl:value-of select="$solutionsLbl"/>
			&lt;/div>&lt;/!-- titreRubrique -->
			&lt;div class='contenuRubrique'>
				<xsl:for-each select="//solutions">
					<xsl:call-template name="enonce-solution"/>
				</xsl:for-each>
			&lt;/div>&lt;/!-- contenuRubrique -->
		&lt;/div>&lt;/!-- div rubrique -->
	</xsl:if>

	&lt;/div>&lt;/!-- contenu18 -->

	<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> <!-- mainPageSeance -->
	&lt;/body>
	&lt;/html>

</xsl:template>


<xsl:template name="enonce-solution">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			<xsl:value-of select="../../local/libelle"/>
		&lt;/div><!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			<xsl:apply-templates select="ressource/global/texte"/>
		&lt;/div><!-- contenuRubrique -->
	&lt;/div><!-- div rubrique -->
</xsl:template>




<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>