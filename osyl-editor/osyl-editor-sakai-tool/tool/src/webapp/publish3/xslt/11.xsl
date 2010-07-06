<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
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
				<xsl:with-param name="page" select="11"/>
				<xsl:with-param name="numSeance" select="0"/>
		</xsl:call-template>
		<xsl:call-template name="cours"/>
		<xsl:call-template name="professeur"/>
		&lt;div class='titrePage'>
			<xsl:value-of select="$a11Lbl"/>
		&lt;/div><!-- titrePage -->
		<!-- ============================== -->

		&lt;div id='hyperliensRubriques'>
			<xsl:call-template name="jumpRubriques">
					<xsl:with-param name="chemin" select="/planCours/presentation/ressources"/>
			</xsl:call-template>
<!--
			<xsl:call-template name="jumpRubriques">
					<xsl:with-param name="chemin" select="/planCours/evaluations"/>
			</xsl:call-template>
-->
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpPlagiat"><xsl:value-of select="$plagiatLbl"/>&lt;/a>
			
			<!-- MAJ: 16 avril 2008/par Van. Ajouter un jump vers message d'usage d'une calculatrice  -->
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpCalculatrice"><xsl:value-of select="$calculatriceLbl"/>&lt;/a>
			
		&lt;/div>&lt;!-- div hyperliensRubriques -->

		<!-- ============================== -->
			<xsl:call-template name="separateur"/>

		&lt;div id='contenu11'>
			<xsl:call-template name="rubriques">
					<xsl:with-param name="chemin" select="/planCours/presentation/ressources"/>
			</xsl:call-template>
<!--
			<xsl:call-template name="evaluations" />
-->
			<xsl:call-template name="plagiat" />
			
			<!-- MAJ: 16 avril 2008/par Van. Ajouter une message usage d'une calculatrice  -->
			<xsl:call-template name="usageCalculatrice" />
			
		&lt;/div><!-- contenu11 -->

		<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> <!-- mainPageSeance -->
	&lt;/body>
	&lt;/html>

</xsl:template>

<!-- ========================================= -->
<xsl:template name="evaluations">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			&lt;a name="#jumpEvaluation"><xsl:value-of select="$evaluationLbl"/>&lt;/a>
		&lt;/div>&lt;!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			<xsl:for-each select='//evaluation'>
				&lt;div class='ressource'>
					&lt;div class="logoRessource">&lt;/div><!-- logoRessource -->
					&lt;div class='titreRessource'>
						<xsl:value-of select="global/libelle"/><xsl:text> (</xsl:text><xsl:value-of select="global/valeur"/><xsl:text>%)</xsl:text>
					&lt;/div><!-- titreRessource -->
				&lt;/div><!-- div ressource -->
			</xsl:for-each>
		&lt;/div>&lt;!-- contenuRubrique -->
	&lt;/div>&lt;!-- div rubrique -->

</xsl:template>

<!-- ========================================= -->


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>