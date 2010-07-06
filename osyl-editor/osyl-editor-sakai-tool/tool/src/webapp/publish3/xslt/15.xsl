<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ========================= Liste des séances =============================== -->
<!--  =========================================================================== -->
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>

<xsl:template match="/">

	&lt;html>
		<xsl:call-template name="head"/>

	&lt;body>

		&lt;div id='mainPage'>
			<!-- =============================== -->

			<xsl:call-template name="headerPage"/>

			<!-- ============================== -->
			<xsl:call-template name="menu">
					<xsl:with-param name="lang" select="planCours/@lang"/>
					<xsl:with-param name="code" select="planCours/@koId"/>
					<xsl:with-param name="page" select="15"/>
					<xsl:with-param name="numSeance" select="0"/>
			</xsl:call-template>
			<xsl:call-template name="cours"/>
			<xsl:call-template name="professeur"/>

			<xsl:if test="/planCours/seances/seance[@type='Seance']">
				&lt;div class='titrePage'>
					<xsl:value-of select="$a15Lbl"/>
				&lt;/div><!-- titrePage -->

				<xsl:call-template name="seances">
						<xsl:with-param name="typeValue">Seance</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<xsl:if test="/planCours/seances/seance[@type='TP']">
				&lt;div class='titrePage'>
					<xsl:value-of select="$a15LblTP"/>
				&lt;/div><!-- titrePage -->
				<xsl:call-template name="seances">
						<xsl:with-param name="typeValue">TP</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<xsl:if test="/planCours/seances/seance[@type='Theme']">
				&lt;div class='titrePage'>
					<xsl:value-of select="$a15LblTheme"/>
				&lt;/div><!-- titrePage -->
				<xsl:call-template name="seances">
						<xsl:with-param name="typeValue">Theme</xsl:with-param>
				</xsl:call-template>
			</xsl:if>

			<xsl:call-template name="separateur"/>

			&lt;div id='contenu15'>
				<xsl:call-template name="lignesSeances">
					<xsl:with-param name="typeValue">Seance</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="lignesSeances">
					<xsl:with-param name="typeValue">Theme</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="lignesSeances">
					<xsl:with-param name="typeValue">TP</xsl:with-param>
				</xsl:call-template>
			&lt;/div> &lt;!-- contenu15 -->

			<!-- ============================== -->
			<xsl:call-template name="footerPage"/>
			<!-- ============================== -->
		&lt;/div> <!-- mainPageSeance -->
	&lt;/body>
	&lt;/html>

</xsl:template>


<!-- ============================== -->
<!-- =========== Seances =========== -->
<!-- ============================== -->
<xsl:template name="seances">
	<xsl:param name="typeValue">seance</xsl:param>

	&lt;div id='seancesDansListeSeances'>
		<xsl:for-each select="/planCours/seances/seance[@coursType='specifique' or @coursType='ajout' or @coursType='annuaire']">
			<xsl:sort select="@no" order="ascending" data-type="number"/>
			<xsl:if test="@type=$typeValue">
				<xsl:call-template name="jumpSeance" />
			</xsl:if>
		</xsl:for-each>
	&lt;/div>
</xsl:template>

<xsl:template name="jumpSeance">
	<xsl:param name="code">123</xsl:param>
	&lt;span class='jumpSeance'>&lt;a href='af1SeancePage.jsp?instId=<xsl:value-of select="@koId"/>&amp;lang=<xsl:value-of select="/planCours/@lang"/>'><xsl:value-of select="no"/>&lt;/a><xsl:text> | </xsl:text>&lt;/span>
</xsl:template>


<!-- ============================== -->
<!-- =========== lignesSeance ====== -->
<!-- ============================== -->
<xsl:template name="lignesSeances">
	<xsl:param name="typeValue">seance</xsl:param>

	<xsl:if test="/planCours/seances/seance[@type=$typeValue]">

		&lt;div id='listeSeances'>

			&lt;div class='headerListeSeance'>
				&lt;span class='noSeanceListe'>
					<xsl:if test="$typeValue='Seance'">
						<xsl:value-of select="$seanceLbl"/>
					</xsl:if>
					<xsl:if test="$typeValue='TP'">
						<xsl:value-of select="$tpLbl"/>
					</xsl:if>
					<xsl:if test="$typeValue='Theme'">
						<xsl:value-of select="$themeLbl"/>
					</xsl:if>
				&lt;/span>&lt;!-- noSeanceListe -->

				&lt;span class='theme'>
					<xsl:value-of select="$themeTitreLbl"/>
				&lt;/span>&lt;!-- theme -->
			&lt;/div>&lt;!-- headerListeSeance -->

			<xsl:for-each select="/planCours/seances/seance[@coursType='specifique' or @coursType='ajout' or @coursType='annuaire']">
				<xsl:sort select="@no" order="ascending" data-type="number"/>
				<xsl:if test="@type=$typeValue">
					<xsl:call-template name="ligneSeance" />
				</xsl:if>
			</xsl:for-each>

		&lt;/div> &lt;!-- listeSeances -->

	</xsl:if>

</xsl:template>

<xsl:template name="ligneSeance">

	&lt;div class='ligneSeance'>
		&lt;span class='noSeanceListe'>

		&lt;span class='jumpSeance'>
			&lt;a href='af1SeancePage.jsp?instId=<xsl:value-of select="@koId"/>&amp;lang=<xsl:value-of select="/planCours/@lang"/>'><xsl:value-of select="no"/>&lt;/a>
		&lt;/span>

		&lt;/span>&lt;!-- noSeanceListe -->

		&lt;span class='theme'>
			&lt;a href='af1SeancePage.jsp?instId=<xsl:value-of select="@koId"/>&amp;lang=<xsl:value-of select="/planCours/@lang"/>'><xsl:value-of select="libelle" />&lt;/a>
		&lt;/span>&lt;!-- theme -->
	&lt;/div><!-- ligneSeance -->

</xsl:template>



<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>