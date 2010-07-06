<?xml version='1.0' encoding='iso-8859-1' standalone='yes'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<xsl:output cdata-section-elements="description texte"/>
<!--  =========================================================================== -->
<xsl:param name="numSeance" select="4"/>
<xsl:param name="typeSeance" select="'Seance'"/>
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>
<xsl:include href="ressources.xsl"/>

<xsl:template match="/">
	&lt;html>
		<xsl:call-template name="head"/>

		&lt;body>
		
		<!-- legende olivier -->
		&lt;div id='leg'&gt;
	&lt;img src='img/legendeTexte.jpg' onMouseOver="document.getElementById('legende').style.display='block'" onMouseOut="document.getElementById('legende').style.display='none'"&gt;
&lt;/div&gt;
	&lt;div style="display:none;top:100px;left:15;margin-left:0px;" class='fenetre'   id="legende"&gt;
	&lt;iframe name='legende' src="legende/legende.jsp" frameborder=0 width='305' height='130' scrolling='no'&gt;&lt;/iframe&gt;
	&lt;/div&gt;
	
	<!-- fin legende olivier -->
		
		
		<!-- ==== vote ==== -->
		<xsl:if test="/planCours[@vote='oui']">
			&lt;form action='voter.jsp' method='post'&gt;
			&lt;input type='hidden' name='codeCours' value='<xsl:value-of select="/planCours/@koId"/>'/&gt;
			&lt;input type='hidden' name='codePage' value='<xsl:value-of select="/planCours/seances/seance[@no=$numSeance]/@koId"/>'/&gt;
		</xsl:if>
		<!-- ============== -->

			&lt;div id='mainPage'>

			<xsl:call-template name="headerPage"/>

			<!-- ============================== -->
			<xsl:call-template name="menu">
						<xsl:with-param name="lang" select="planCours/@lang"/>
						<xsl:with-param name="code" select="planCours/@koId"/>
						<xsl:with-param name="page" select="10"/>
						<xsl:with-param name="numSeance" select="$numSeance"/>
						<xsl:with-param name="typeSeance" select="$typeSeance"/>
			</xsl:call-template>

			<!-- ============================== -->
			<xsl:call-template name="cours"/>
			<xsl:call-template name="professeur"/>
			<xsl:call-template name="seance">
				<xsl:with-param name="numSeance" select="$numSeance"/>
				<xsl:with-param name="typeSeance" select="$typeSeance"/>
			</xsl:call-template>
			<xsl:call-template name="listeSeances">
				<xsl:with-param name="typeValue" select="$typeSeance"/>
			</xsl:call-template>



			&lt;div id='hyperliensRubriques'>
				<xsl:call-template name="jumpRubriques">
						<xsl:with-param name="chemin" select="/planCours/seances/seance[@no=$numSeance][@type=$typeSeance]/ressources"/>
				</xsl:call-template>

				<xsl:call-template name="sep"/>
			&lt;/div>&lt;!-- div hyperliensRubriques -->

			<!-- ============================== -->
			&lt;div id='contenu10'>

					<xsl:if test="/planCours/seances/seance[@no=$numSeance][@type=$typeSeance]//ressource[local/important]">
						&lt;div class='rubriqueImportant'>
						&lt;div class='important'>
						&lt;div class="logoImportant">&lt;/div><!-- logoImportant -->
						&lt;div class="titreImportant">Important&lt;/div><!-- titreImportant -->
							<xsl:apply-templates select="/planCours/seances/seance[@no=$numSeance][@type=$typeSeance]//ressource[local/important]" />
						&lt;/div>
						&lt;/div>
					</xsl:if>

					<xsl:call-template name="rubriques">
						<xsl:with-param name="chemin" select="/planCours/seances/seance[@no=$numSeance][@type=$typeSeance]/ressources"/>
					</xsl:call-template>

			&lt;/div>&lt;!-- contenu10 -->

			<!-- ============================== -->

			<xsl:call-template name="footerPage"/>

		<!-- ============================== -->
			&lt;/div> &lt;!-- mainPage -->
		<!-- ========= vote ===== -->
		<xsl:if test="/planCours[@vote='oui']">
			&lt;/form&gt;
		</xsl:if>
		<!-- ==================== -->
		&lt;/body>
	&lt;/html>


</xsl:template>


<!-- ============================== -->
<!-- =========== Seance =========== -->
<!-- ============================== -->
<xsl:template name="seance">
	<xsl:param name="numSeance">1</xsl:param>
	<xsl:param name="typeSeance">Seance</xsl:param>
	&lt;div id='noSeance'>
		<xsl:if test="$typeSeance='Seance'">
			<xsl:value-of select="$seanceLbl"/><xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="$typeSeance='TP'">
			<xsl:value-of select="$tpLbl"/><xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="$typeSeance='Theme'">
			<xsl:value-of select="$themeLbl"/><xsl:text> </xsl:text>
		</xsl:if>
		<xsl:value-of select="$numSeance"/>
		<xsl:text> : </xsl:text>
	&lt;/div>
	<!-- ====================== -->
	&lt;div id='titreSeance'>
		<xsl:value-of select="/planCours/seances/seance[@no=$numSeance][@type=$typeSeance][@coursType='specifique' or @coursType='ajout' or @coursType='annuaire']/libelle"/>
	&lt;/div>
</xsl:template>

<!-- ============================== -->
<!-- =========== Seances =========== -->
<!-- ============================== -->
<xsl:template name="listeSeances">
	<xsl:param name="typeValue">TP</xsl:param>

	&lt;div id='seances'>
		<xsl:if test="$typeValue='Seance'">
			<xsl:value-of select="$seancesLbl"/><xsl:text><![CDATA[&nbsp;]]> </xsl:text>
		</xsl:if>
		<xsl:if test="$typeValue='TP'">
			<xsl:value-of select="$tpsLbl"/><xsl:text><![CDATA[&nbsp;]]> </xsl:text>
		</xsl:if>
		<xsl:if test="$typeValue='Theme'">
			<xsl:value-of select="$themeLbl"/><xsl:text><![CDATA[&nbsp;]]> </xsl:text>
		</xsl:if>
	&lt;/div>&lt;!-- seances -->
	&lt;div id='noSeanceLiens'>
		<xsl:text> | </xsl:text>
		<xsl:for-each select="/planCours/seances/seance[@type=$typeValue][@coursType='specifique' or @coursType='ajout' or @coursType='annuaire']">
			<xsl:sort select="@no" order="ascending" data-type="number"/>
			<xsl:call-template name="jumpSeance" />
		</xsl:for-each>
	&lt;/div>&lt;!-- noSeanceLiens -->
</xsl:template>

<xsl:template name="jumpSeance">
	<xsl:param name="code">123</xsl:param>
	&lt;span class='jumpSeance'>&lt;a href='af1SeancePage.jsp?instId=<xsl:value-of select="@koId"/>&amp;lang=<xsl:value-of select="/planCours/@lang"/>'><xsl:value-of select="no"/>&lt;/a><xsl:text> | </xsl:text>&lt;/span>
</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>
