<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ==================== Liste des références ================================= -->
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
		<xsl:value-of select="$a19Lbl"/>
	&lt;/div>&lt;!-- titrePage -->
	<!-- ============================== -->

	&lt;div id='contenu19'>

	<xsl:apply-templates select="/planCours/materiel/ressources/rubriqueBibliographie" />


	<xsl:if test="/planCours/seances//rubriqueLectures/ressource[@type='Entree']">
		&lt;div class='rubrique'>
			&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
			&lt;div class='titreRubrique'>
				<xsl:value-of select="$lecturesLbl"/>&lt;/a>
			&lt;/div>&lt;!-- titreRubrique -->
			&lt;div class='contenuRubrique'>
			<xsl:for-each select="/planCours/seances/seance" >
				<xsl:if test="/planCours/seances/seance[@type='Seance']">
				<xsl:if test="ressources/rubriqueLectures/ressource[@type='Entree']">
					&lt;div class='rubrique'>
						&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
						&lt;div class='titreRubrique'>
							Seance <xsl:value-of select="position()"/>
						&lt;/div><!-- titreRubrique -->
						&lt;div class='contenuRubrique'>
							<xsl:apply-templates select="ressources/rubriqueLectures/ressource[@type='Entree']"/>
						&lt;/div><!-- contenuRubrique -->
					&lt;/div><!-- div rubrique -->
				</xsl:if>
				</xsl:if>
			</xsl:for-each>
			&lt;/div>&lt;!-- contenuRubrique -->
		&lt;/div>&lt;!-- div rubrique -->

	</xsl:if>



	&lt;/div>&lt;!-- contenu19 -->

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