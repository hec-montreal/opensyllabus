<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ============================ Archive des nouvelles ======================== -->
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

	<xsl:call-template name="cours"/>
	<xsl:call-template name="professeur"/>

	&lt;div class='titrePage'>
		<xsl:value-of select="$a21Lbl"/>
	&lt;/div><!-- titrePage -->

	<!-- ============================== -->

			<xsl:call-template name="separateur"/>

	&lt;div id='contenu21'>

		<xsl:call-template name="nouvellesCoursArchive"/>
		<xsl:for-each select="/planCours/seances/seance">
			<xsl:if test=".//rubriqueNouvelle">
				<xsl:call-template name="nouvellesSeancesArchive"/>
			</xsl:if>
		</xsl:for-each>

	&lt;/div><!-- contenu21 -->

	<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> <!-- mainPage -->
	&lt;/body>
	&lt;/html>

</xsl:template>

<xsl:template name="nouvellesCoursArchive">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			<xsl:value-of select="$diversCoursLbl"/>
		&lt;/div><!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
		<xsl:for-each select="/planCours/cours/nouvelles/texte[@inArchive='true']">
			<xsl:sort order="descending" select="@date"/>
			<xsl:call-template name="nouvelle"/>
		</xsl:for-each>
		&lt;/div><!-- contenuRubrique -->
	&lt;/div><!-- div rubrique -->
</xsl:template>

<xsl:template name="nouvelleArchive">
	&lt;div class='nouvelle'&gt;
		&lt;div class="logoRessource">&lt;/div><!-- logoRessource -->
		&lt;div class='texteNouvelles'&gt;
			<xsl:value-of select="."/>
			&lt;span class='datenouvelles'&gt;
				<xsl:text>(</xsl:text>
				<xsl:value-of select="substring(@date,1,16)"/>
				<xsl:text>)</xsl:text>
			&lt;/span&gt;
		&lt;/div&gt;
	&lt;/div&gt;
</xsl:template>

<xsl:template name="nouvellesSeancesArchive">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			<xsl:value-of select="$diversSeanceLbl"/><xsl:text> </xsl:text><xsl:value-of select="position()"/>
		&lt;/div><!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
		<xsl:for-each select="ressources/rubriqueNouvelle/ressource/global/texte[@inArchive='true']">
			<xsl:sort order="descending" select="substring(@date,1,16)"/>
			<xsl:call-template name="nouvelle"/>
		</xsl:for-each>
		&lt;/div><!-- contenuRubrique -->
	&lt;/div><!-- div rubrique -->
</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>