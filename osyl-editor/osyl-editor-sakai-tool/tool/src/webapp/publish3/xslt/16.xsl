<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ====================== Documents imprimables ============================== -->
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
					<xsl:with-param name="page" select="16"/>
					<xsl:with-param name="numSeance" select="0"/>
			</xsl:call-template>
			<xsl:call-template name="cours"/>
			<xsl:call-template name="professeur"/>

			&lt;div class='titrePage'>
				<xsl:value-of select="$a16Lbl"/>
			&lt;/div><!-- titrePage -->

			<xsl:call-template name="separateur"/>

			&lt;div id='contenu16'>

				<xsl:call-template name="planCours"/>
				<xsl:call-template name="Exercices"/>
				<xsl:call-template name="References"/>

			&lt;/div><!-- contenu16 -->
			<!-- ============================== -->
			<xsl:call-template name="footerPage"/>
			<!-- ============================== -->
		&lt;/div> <!-- mainPage -->
	&lt;/body>
	&lt;/html>

</xsl:template>

<xsl:template name="planCours">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
				&lt;a href='af1CodexImp.jsp?instId=<xsl:value-of select="planCours/@koId"/>&amp;lang=<xsl:value-of select="planCours/@lang"/>'><xsl:value-of select="$planCoursImprimable"/>&lt;/a>
		&lt;/div>&lt;!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			&lt;div class='detail'>
				<xsl:value-of select="$planCoursImprimableSsTitre"/>
			&lt;/div>&lt;!-- detail -->
		&lt;/div>&lt;!-- contenuRubrique -->
	&lt;/div>&lt;!-- div rubrique -->
</xsl:template>

<xsl:template name="Exercices">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			&lt;a href='af1CodexEx.jsp?instId=<xsl:value-of select="planCours/@koId"/>&amp;lang=<xsl:value-of select="planCours/@lang"/>'><xsl:value-of select="$exercicesImprimable"/>&lt;/a>
		&lt;/div><!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			&lt;div class='detail'>
				<xsl:value-of select="$exercicesImprimableSsTitre"/>
			&lt;/div>&lt;!-- detail -->
		&lt;/div><!-- contenuRubrique -->
	&lt;/div><!-- div rubrique -->
</xsl:template>

<xsl:template name="References">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			&lt;a href='af1CodexRef.jsp?instId=<xsl:value-of select="planCours/@koId"/>&amp;lang=<xsl:value-of select="planCours/@lang"/>'><xsl:value-of select="$referencesImprimable"/>&lt;/a>
		&lt;/div><!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			&lt;div class='detail'>
				<xsl:value-of select="$referencesImprimableSsTitre"/>
			&lt;/div>&lt;!-- detail -->
		&lt;/div><!-- contenuRubrique -->
	&lt;/div><!-- div rubrique -->
</xsl:template>

<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>