<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ======================= Matériel pédagogique ============================== -->
<!--  =========================================================================== -->
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
			&lt;input type='hidden' name='codeCours' value='<xsl:value-of select="planCours/@koId"/>'/&gt;
		</xsl:if>
		<!-- ============== -->
		&lt;div id='mainPage'>
			<!-- ========================================= -->

			<xsl:call-template name="headerPage"/>

			<!-- ============================== -->
			<xsl:call-template name="menu">
					<xsl:with-param name="lang" select="planCours/@lang"/>
					<xsl:with-param name="code" select="planCours/@koId"/>
					<xsl:with-param name="page" select="13"/>
					<xsl:with-param name="numSeance" select="0"/>
			</xsl:call-template>
			<xsl:call-template name="cours"/>
			<xsl:call-template name="professeur"/>

			&lt;div class='titrePage'>
				<xsl:value-of select="$a13Lbl"/>
			&lt;/div><!-- titrePage -->

			&lt;div id='hyperliensRubriques'>
				<xsl:call-template name="jumpRubriques">
						<xsl:with-param name="chemin" select="/planCours/materiel/ressources"/>
				</xsl:call-template>
			&lt;/div>&lt;!-- div hyperliensRubriques -->

			<xsl:call-template name="separateur"/>

			&lt;div id='contenu13'>

				<xsl:if test="/planCours/materiel//ressource[local/important]">
					&lt;div class='rubriqueImportant'>
					&lt;div class='important'>
					&lt;div class="logoImportant">&lt;/div><!-- logoImportant -->
					&lt;div class="titreImportant">Important&lt;/div><!-- titreImportant -->
						<xsl:apply-templates select="/planCours/materiel//ressource[local/important]" />
					&lt;/div>
					&lt;/div>
				</xsl:if>

				<xsl:call-template name="rubriques">
					<xsl:with-param name="chemin" select="/planCours/materiel/ressources"/>
				</xsl:call-template>

			&lt;/div><!-- contenu13 -->

			<!-- ============================== -->
			<xsl:call-template name="footerPage"/>
			<!-- ============================== -->
		&lt;/div> <!-- mainPage -->
	<!-- ========= vote ===== -->
	<xsl:if test="/planCours[@vote='oui']">
		&lt;/form&gt;
	</xsl:if>
	<!-- ==================== -->
	&lt;/body>
	&lt;/html>

</xsl:template>





<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>