<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="ISO8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<!--  ========================== Plan de cours imprimable ======================= -->
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
	<!-- ============================== -->

	&lt;div id='contenu17'>
		<xsl:apply-templates select="/planCours/presentation" />
		<xsl:apply-templates select="/planCours/coordonnees" />
		<xsl:apply-templates select="/planCours/materiel"/>
		<xsl:apply-templates select="/planCours/evaluations" />
		<xsl:call-template name="plagiat" />
		<xsl:apply-templates select="/planCours/seances" />
	&lt;/div>&lt;!-- contenu17 -->

	<!-- ============================== -->
		<xsl:call-template name="footerPage"/>
		<!-- ============================== -->
		&lt;/div> &lt;!-- mainPagePlanImprimable -->
	&lt;/body>
	&lt;/html>

</xsl:template>


<xsl:template match="presentation">
	<xsl:if test="/planCours/presentation/ressources/*">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a11Lbl"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>

				<xsl:call-template name="rubriques">
						<xsl:with-param name="chemin" select="/planCours/presentation/ressources"/>
				</xsl:call-template>

			&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template match="coordonnees">
	<xsl:if test="/planCours/coordonnees/*">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a12Lbl"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
					<xsl:apply-templates select="/planCours/coordonnees/*" />
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template match="materiel">
	<xsl:if test="/planCours/materiel/ressources/*">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a13Lbl"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>

				<xsl:call-template name="rubriques">
						<xsl:with-param name="chemin" select="/planCours/materiel/ressources"/>
				</xsl:call-template>

				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template match="evaluations">
	<xsl:if test="/planCours/evaluations/evaluation">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a14Lbl"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>

					<xsl:apply-templates select="/planCours/evaluations/evaluation" />

				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>
</xsl:template>


<xsl:template match="seances">
	<xsl:if test="/planCours/seances/seance[@type='Seance'][@coursType='specifique']">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a15Lbl"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
				<xsl:for-each select="/planCours/seances/seance[@type='Seance'][@coursType='specifique']">
					<xsl:variable name="numSeance" select="@no" />
					&lt;div class='rubrique'>
						&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
						&lt;div class='titreRubrique'>
							<xsl:if test="@type='Seance'">
								<xsl:value-of select="$seanceLbl"/><xsl:text> </xsl:text>
							</xsl:if>
							<xsl:if test="@type='TP'">
								<xsl:value-of select="$tpLbl"/><xsl:text> </xsl:text>
							</xsl:if>
							<xsl:if test="@type='Theme'">
								<xsl:value-of select="$themeLbl"/><xsl:text> </xsl:text>
							</xsl:if>
							<xsl:value-of select="no"/>
							<xsl:text> : </xsl:text>
							<xsl:value-of select="libelle"/>
						&lt;/div>&lt;!-- titreRubrique -->
						&lt;div class='contenuRubrique'>
					<xsl:call-template name="rubriques">
						<xsl:with-param name="chemin" select="/planCours/seances/seance[@type='Seance'][@no=$numSeance]/ressources"/>
					</xsl:call-template>
						&lt;/div>&lt;!-- contenuRubrique -->
					&lt;/div>&lt;!-- rubrique -->
				</xsl:for-each>
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours/seances/seance[@type='Theme'][@coursType='specifique']">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a15LblTheme"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
				<xsl:for-each select="/planCours/seances/seance[@type='Theme'][@coursType='specifique']">
					<xsl:variable name="numSeance" select="@no" />
					&lt;div class='rubrique'>
						&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
						&lt;div class='titreRubrique'>
						<xsl:if test="@type='Seance'">
							<xsl:value-of select="$seanceLbl"/><xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="@type='TP'">
							<xsl:value-of select="$tpLbl"/><xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="@type='Theme'">
							<xsl:value-of select="$themeLbl"/><xsl:text> </xsl:text>
						</xsl:if>
							<xsl:value-of select="no"/>
							<xsl:text> : </xsl:text>
							<xsl:value-of select="libelle"/>
						&lt;/div>&lt;!-- titreRubrique -->
						&lt;div class='contenuRubrique'>
							<xsl:call-template name="rubriques">
								<xsl:with-param name="chemin" select="/planCours/seances/seance[@type='Theme'][@no=$numSeance]/ressources"/>
							</xsl:call-template>
						&lt;/div>&lt;!-- contenuRubrique -->
					&lt;/div>&lt;!-- crubrique -->
				</xsl:for-each>
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>
	
	<xsl:if test="/planCours/seances/seance[@type='TP'][@coursType='specifique']">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
				&lt;div class='titreRubrique'>
					<xsl:value-of select="$a15LblTP"/>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
				<xsl:for-each select="/planCours/seances/seance[@type='TP'][@coursType='specifique']">
					<xsl:variable name="numSeance" select="@no" />
					&lt;div class='rubrique'>
						&lt;div class="logoRubrique">&lt;/div>&lt;!-- logoRubrique -->
						&lt;div class='titreRubrique'>
						<xsl:if test="@type='Seance'">
							<xsl:value-of select="$seanceLbl"/><xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="@type='TP'">
							<xsl:value-of select="$tpLbl"/><xsl:text> </xsl:text>
						</xsl:if>
						<xsl:if test="@type='Theme'">
							<xsl:value-of select="$themeLbl"/><xsl:text> </xsl:text>
						</xsl:if>
						<xsl:value-of select="no"/>
						<xsl:text> : </xsl:text>
						<xsl:value-of select="libelle"/>
						&lt;/div>&lt;!-- titreRubrique -->
						&lt;div class='contenuRubrique'>
							<xsl:call-template name="rubriques">
								<xsl:with-param name="chemin" select="/planCours/seances/seance[@type='TP'][@no=$numSeance]/ressources"/>
							</xsl:call-template>
						&lt;/div>&lt;!-- contenuRubrique -->
					&lt;/div>&lt;!-- crubrique -->
				</xsl:for-each>
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:if>


</xsl:template>



<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>