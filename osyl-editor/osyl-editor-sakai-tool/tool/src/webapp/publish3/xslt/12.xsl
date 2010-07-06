<?xml version="1.0"  encoding="ISO8859-1" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ============================ Coordonnées ================================== -->
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
			<xsl:with-param name="page" select="12"/>
			<xsl:with-param name="numSeance" select="0"/>
	</xsl:call-template>

	<xsl:call-template name="cours"/>
	<xsl:call-template name="professeur"/>

	&lt;div class='titrePage'>
		<xsl:value-of select="$a12Lbl"/>
	&lt;/div><!-- titrePage -->

	<!-- ============================== -->

			<xsl:call-template name="separateur"/>

	&lt;div id='contenu12'>

		<xsl:apply-templates select="/planCours/coordonnees/*" />

	&lt;/div><!-- contenu12 -->

	<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> <!-- mainPage -->
	&lt;/body>
	&lt;/html>

</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>