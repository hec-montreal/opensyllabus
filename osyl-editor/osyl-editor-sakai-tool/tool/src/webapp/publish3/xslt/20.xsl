<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ======================= Document interne ================================== -->
<!--  =========================================================================== -->
<xsl:param name="idRessource" select="3"/>
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>

<xsl:template match="/">

	&lt;html>
	&lt;head>
		&lt;meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		&lt;link name='css' rel="stylesheet" TYPE='text/css' href='css/cmsie.css' title='default'/>

		&lt;title>
			<xsl:text> Document </xsl:text>
		&lt;/title>

		<xsl:call-template name="scriptZoom"/>

	&lt;/head>

	&lt;body>
		&lt;div id='mainPage'>
		<!-- ========================================= -->

		<xsl:call-template name="headerPage"/>

		&lt;div class='titrePage'>
			<xsl:value-of select="//ressource[@koId=$idRessource]/global/titre"/>
		&lt;/div>&lt;!-- titrePage -->

		<!-- ============================== -->
		&lt;div id='contenu20'>
			<xsl:value-of select="//ressource[@koId=$idRessource]/global/texte" />
		&lt;/div>&lt;!-- contenu20 -->
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