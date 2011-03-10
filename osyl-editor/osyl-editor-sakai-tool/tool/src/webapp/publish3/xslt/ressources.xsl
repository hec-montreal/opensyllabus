<?xml version="1.0"  encoding="ISO-8859-1" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="text()"/>


<!-- ============================== -->
<!-- =========== jump Rubriques === -->
<!-- ============================== -->

<!-- ========================================= -->
<xsl:template name="jumpRubriques">
	<xsl:param name="chemin">/planCours/presentation/ressources</xsl:param>

	<!-- =========1======== -->
	<xsl:for-each select="$chemin/rubriqueDescription[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpDescription"><xsl:value-of select="$descriptionLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>

	<!-- =========2======== -->
	<xsl:for-each select="$chemin/rubriqueObjectifs[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpObjectifs"><xsl:value-of select="$objectifsLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>

	<!-- =========3======== -->
	<xsl:for-each select="$chemin/rubriqueLectures[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpLectures"><xsl:value-of select="$lecturesLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- ============NEW RUBRICS ================== -->
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesAvantSeance[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpActResBefore"><xsl:value-of select="$actResBeforeLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesPendantSeance[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpActResDuring"><xsl:value-of select="$actResDuringLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesApresSeance[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpActResAfter"><xsl:value-of select="$actResAfterLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========4======== -->
	<xsl:for-each select="$chemin/rubriqueRessourcesGenerales[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpRessources"><xsl:value-of select="$ressourcesLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========4======== -->
	<xsl:for-each select="$chemin/rubriqueRessourcesComplementaires[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpRessources"><xsl:value-of select="$ressourcesComplementairesLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>

	<!-- =========5======== -->
	<xsl:for-each select="$chemin/rubriqueSupportsCours[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpSupport"><xsl:value-of select="$supportLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========6======== -->
	<xsl:for-each select="$chemin/rubriqueCas[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpCas"><xsl:value-of select="$casLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========7======== -->
	<xsl:for-each select="$chemin/rubriqueBibliographie[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpBiblio"><xsl:value-of select="$biblioLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>

	<!-- =========8======== -->
	<xsl:for-each select="$chemin/rubriqueApprochePedagogique[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpApproche"><xsl:value-of select="$approcheLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>

	<!-- =========9======== -->
	<xsl:for-each select="$chemin/rubriqueOutils[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpOutils"><xsl:value-of select="$outilsLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========10======== -->
	<xsl:for-each select="$chemin/rubriqueAnciensExamens[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpAnciensExamens"><xsl:value-of select="$anciensexamsLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========11======== -->
	<xsl:for-each select="$chemin/rubriqueNotesFormateur[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpNotesFormateur"><xsl:value-of select="$notesFormLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========12======== -->
	<xsl:for-each select="$chemin/rubriqueExercice[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpExercice"><xsl:value-of select="$exercicesLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========13======== -->
	<xsl:for-each select="$chemin/rubriqueDevoirs[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpDevoirs"><xsl:value-of select="$devoirsLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>
	
	<!-- =========14======== -->
	<xsl:for-each select="$chemin/rubriqueDivers[*]">
		<xsl:if test="position()=1">
			<xsl:text> | </xsl:text>
			&lt;a href="#jumpDivers"><xsl:value-of select="$diversLbl"/>&lt;/a>
		</xsl:if>
	</xsl:for-each>		

	<!--  Ajouter pour les liens hypertextes FAQ : Awa 2008-02-07 -->
	<!-- =========14======== -->
	<xsl:for-each select="$chemin/questrep[*]">
		&lt;div class="logoFAQ">&lt;/div>
		<xsl:choose>	
			<xsl:when test="local/description">
				&lt;a href='#<xsl:value-of select="local/description"/>'><xsl:value-of select="local/description"/>&lt;/a>
			</xsl:when>
			<xsl:otherwise>
				&lt;a href='#<xsl:value-of select="global/question"/>'><xsl:value-of select="global/question"/>&lt;/a>
			</xsl:otherwise>			
		</xsl:choose>	
		
		&lt;br/>
	</xsl:for-each>		

</xsl:template>

<!-- ============================== -->
<!-- =========== Rubriques ======== -->
<!-- ============================== -->


<xsl:template name="rubriques">
	<xsl:param name="chemin">/planCours/presentation/ressources</xsl:param>


	<!-- Ajouté par Awa pour les rubriques de FAQ -->
	<xsl:for-each select="$chemin/questrep[*]">
		&lt;div class='ressource'>
			&lt;div class="logoFAQ">&lt;/div>&lt;!-- logoFAQ -->
		
			<xsl:choose>	
				<xsl:when test="local/description">
					&lt;div class='titreRubrique' id='<xsl:value-of select="local/description"/>'>
					<xsl:value-of select="local/description"/>
					&lt;/div><!-- titreRubrique -->
					&lt;div class='contenuRessource'>
					&lt;div class='contenuQuestion'>
						&lt;b><xsl:text>Q: </xsl:text>&lt;/b><xsl:value-of select="global/question" />
					&lt;/div><!-- contenuQuestion -->
				</xsl:when>
				<xsl:otherwise>
					&lt;div class='titreRubrique' id='<xsl:value-of select="global/question"/>'>
					&lt;div class='contenuQuestion'>
					<xsl:text>Q: </xsl:text><xsl:value-of select="global/question" />
					&lt;/div><!-- contenuQuestion -->
					&lt;/div><!-- titreRubrique -->
					&lt;div class='contenuRessource'>
				</xsl:otherwise>			
			</xsl:choose>	
			&lt;div class='contenuReponse'> 
				&lt;b><xsl:text>R: </xsl:text>&lt;/b><xsl:value-of select="global/reponse" />
			&lt;/div>&lt;!-- contenuReponse-->
			&lt;/div>&lt;!-- contenuRessource -->
			&lt;/div>&lt;!-- div rubrique -->
			&lt;br/>&lt;!-- saut de ligne --> 
	</xsl:for-each>
		<!-- Fin ajout Awa -->

	<xsl:for-each select="$chemin/rubriqueDescription[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpDescription"><xsl:value-of select="$descriptionLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueObjectifs[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpObjectifs"><xsl:value-of select="$objectifsLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueLectures[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpLecture"><xsl:value-of select="$lecturesLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesAvantSeance[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpActResBefore"><xsl:value-of select="$actResBeforeLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesPendantSeance[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpActResDuring"><xsl:value-of select="$actResDuringLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueActivitesRessourcesApresSeance[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpActResAfter"><xsl:value-of select="$actResAfterLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	
	
	<xsl:for-each select="$chemin/rubriqueRessourcesGenerales[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpRessources"><xsl:value-of select="$ressourcesLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueRessourcesComplementaires[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpRessources"><xsl:value-of select="$ressourcesComplementairesLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueSupportsCours[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpSupport"><xsl:value-of select="$supportLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueCas[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpCas"><xsl:value-of select="$casLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueBibliographie[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpBiblio"><xsl:value-of select="$biblioLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueApprochePedagogique[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpApproche"><xsl:value-of select="$approcheLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueOutils[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpOutils"><xsl:value-of select="$outilsLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueAnciensExamens[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpAnciensExamens"><xsl:value-of select="$anciensexamsLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueNotesFormateur[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpNotesFormateur"><xsl:value-of select="$notesFormLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
	<xsl:for-each select="$chemin/rubriqueExercice[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpExercice"><xsl:value-of select="$exercicesLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueDevoirs[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpDevoirs"><xsl:value-of select="$devoirsLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueDivers[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpDivers"><xsl:value-of select="$diversLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

</xsl:template>



<!-- ============================== -->
<!-- =========== Rubriques ======== -->
<!-- ============================== -->


<xsl:template name="rubriquesAnnuaire">
	<xsl:param name="chemin">/planCours/presentation/ressources</xsl:param>

	<xsl:for-each select="$chemin/rubriqueDescription[*]">
		<xsl:if test="position()=1">
			&lt;div class=bulletCarreVert&gt;&lt;div class='ssTitrePageBullet'&gt;
				&lt;a name="#jumpDescription"><xsl:value-of select="$descriptionLbl"/>&lt;/a>
			&lt;/div&gt;&lt;/div&gt;&lt;!-- sstitrePage -->
			&lt;div id='encadreTableAnnuaireBlanc'&gt;
			&lt;table border='0' cellspacing='0' class='tableauNormal2'&gt;
		</xsl:if>

		&lt;tr&gt;
			&lt;td width="100%" valign="top"&gt;
				<xsl:apply-templates select='ressource'/>
			&lt;/td&gt;
		&lt;/tr&gt;

		<xsl:if test="position()=last()">
				&lt;/table&gt;
			&lt;/div>&lt;!-- div encadreTable -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueObjectifs[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpObjectifs"><xsl:value-of select="$objectifsLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>

	<xsl:for-each select="$chemin/rubriqueApprochePedagogique[*]">
		<xsl:if test="position()=1">
			&lt;div class='rubrique'>
				&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
				&lt;div class='titreRubrique'>
						&lt;a name="#jumpApproche"><xsl:value-of select="$approcheLbl"/>&lt;/a>
				&lt;/div>&lt;!-- titreRubrique -->
				&lt;div class='contenuRubrique'>
		</xsl:if>
					<xsl:apply-templates select='ressource'/>
		<xsl:if test="position()=last()">
				&lt;/div>&lt;!-- contenuRubrique -->
			&lt;/div>&lt;!-- div rubrique -->
		</xsl:if>
	</xsl:for-each>
	
</xsl:template>

<!-- ============================== -->
<!-- =========== Attributs ========= -->
<!-- ============================== -->

<xsl:template match="texte">
	&lt;div class='texte'><xsl:value-of select="."/>&lt;/div> &lt;!-- texte -->
</xsl:template>

<xsl:template match="local/description">
	&lt;div class='commentaire-local'>
		<xsl:value-of select="."/>
	&lt;/div>&lt;!-- commentaire-local -->
</xsl:template>

<xsl:template match="description">
	&lt;div class='commentaire'>
		<xsl:value-of select="."/>
	&lt;/div>&lt;!-- commentaire -->
</xsl:template>

<xsl:template match="isbn">
	&lt;span class='isbn'>
		<xsl:value-of select="$isbnLbl"/><xsl:value-of select="."/>
	&lt;/span><!-- isbn -->
</xsl:template>

<!-- ========================================= -->
<!-- ================ boutonsVote =================== -->
<!-- ========================================= -->
<xsl:template name="boutonsVote">
	<xsl:param name="koid">koid</xsl:param>
	<xsl:param name="docid">docid</xsl:param>

	<xsl:if test="/planCours[@vote='oui']">
	
		&lt;span class='input'>	
			&lt;img src="img/_.jpg"/>
			&lt;input type='hidden' name='ress_<xsl:value-of select="$koid"/>' value='<xsl:value-of select="$docid"/>'/>
			&lt;input type='radio' name='vote_<xsl:value-of select="$koid"/>' value='0' checked style='display:none'/>
			&lt;input type='radio' name='vote_<xsl:value-of select="$koid"/>' value='1'/>
			&lt;input type='radio' name='vote_<xsl:value-of select="$koid"/>' value='2'/>
			&lt;input type='radio' name='vote_<xsl:value-of select="$koid"/>' value='3'/>
			&lt;input type='radio' name='vote_<xsl:value-of select="$koid"/>' value='4'/>
			&lt;img src="img/+.jpg"/>
		&lt;/span><!-- input -->
	</xsl:if>

</xsl:template>

<!-- ========================================= -->
<!-- =========== RessTexte =================== -->
<!-- ========================================= -->

<xsl:template match="ressource[@type='ressTexte']">

</xsl:template>

<!-- ========================================= -->
<!-- =========== Entrée ====================== -->
<!-- ========================================= -->

<xsl:template match="ressource[@type='Entree']">
&lt;div class='ressource2007'>
	&lt;table border ='0' > 
		&lt;tr>
		&lt;td width = "40px"> &lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/coop,'vrai')">
					&lt;img src='img/iconeCoop.gif' alt='Disponible à la COOP HEC'/>
				</xsl:if>
				<xsl:if test="not(contains(local/coop,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/biblio,'vrai')">
					&lt;img src='img/iconeBiblio.gif' alt='Disponible à la	bibliothèque' />
				</xsl:if>
				<xsl:if test="not(contains(local/biblio,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="20px">
				<xsl:if test="contains(local/complementaire,'vrai')">
					&lt;img src='img/iconeComp.gif' alt='Complémentaire' />
				</xsl:if>
				<xsl:if test="contains(local/obligatoire,'vrai')  ">
					&lt;img src='img/iconeObl.gif' alt='Obligatoire' />
				</xsl:if>
				<xsl:if	test="not(contains(local/obligatoire,'vrai')) and not(contains(local/complementaire,'vrai'))  ">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>
			
			&lt;td width = "20px" align = 'center'> 
					&lt;img src='img/point.gif' />
			&lt;/td> 
			&lt;td width = "700px" align = "left"> 
			
					&lt;span class='titreRessource'>
						<xsl:value-of select="global/reference"/>
						<xsl:call-template name="boutonsVote">
							<xsl:with-param name="koid" select="global/code"/>
							<xsl:with-param name="docid" select="@koid"/>
						</xsl:call-template>
					&lt;/span><!-- titreRessource -->
			&lt;/td> 
		&lt;/tr>

		&lt;tr> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td>
				&lt;div class='contenuRessource'>
			
					<xsl:apply-templates select="global/isbn" />
					<xsl:apply-templates select="global/description" />
					<xsl:apply-templates select="local/description" />
					<xsl:apply-templates select="global/documents" />
					
				&lt;/div><!-- contenuRessource -->
		&lt;/td>
		&lt;/tr>
	&lt;/table>
	&lt;/div><!-- div ressource -->
</xsl:template>

<!-- ========================================= -->
<!-- =========== Document =================== -->
<!-- ========================================= -->

<xsl:template match="ressource[@type='Document']">
	&lt;div class='ressource2007'>
	&lt;table border ='0' > 
		&lt;tr>
		&lt;td width = "40px"> &lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/coop,'vrai')">
					&lt;img src='img/iconeCoop.gif' alt='Disponible à la COOP HEC'/>
				</xsl:if>
				<xsl:if test="not(contains(local/coop,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/biblio,'vrai')">
					&lt;img src='img/iconeBiblio.gif' alt='Disponible à la	bibliothèque' />
				</xsl:if>
				<xsl:if test="not(contains(local/biblio,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="20px">
				<xsl:if test="contains(local/complementaire,'vrai')">
					&lt;img src='img/iconeComp.gif' alt='Complémentaire' />
				</xsl:if>
				<xsl:if test="contains(local/obligatoire,'vrai')  ">
					&lt;img src='img/iconeObl.gif' alt='Obligatoire' />
				</xsl:if>
				<xsl:if	test="not(contains(local/obligatoire,'vrai')) and not(contains(local/complementaire,'vrai'))  ">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>
			
			&lt;td width = "20px" align = 'center'> 
					&lt;img src='img/point.gif' />
			&lt;/td> 
			&lt;td width = "700px" align = "left"> 
			
				&lt;span class='titreRessource'>
					<xsl:if test="contains(global/url,'http://') or contains(global/url,'ftp://')">
						&lt;a href='<xsl:value-of select="global/url"/>' target='_blank'>
					</xsl:if>
					<xsl:if test="not(contains(global/url,'http://')) and not(contains(global/url,'ftp://'))">
						&lt;a href='documents/<xsl:value-of select="@koId"/>.<xsl:value-of select="substring-after(substring-after(global/url,'/'),'/')"/>' target='_blank'>
					</xsl:if>
						<xsl:value-of select="local/libelle"/>&lt;/a>
						
						<xsl:call-template name="boutonsVote">
							<xsl:with-param name="koid" select="global/code"/>
							<xsl:with-param name="docid" select="@koid"/>
						</xsl:call-template>
						
				&lt;/span>&lt;!-- titreRessource -->
				<!-- 
				<xsl:call-template name="boutonsVote">
						<xsl:with-param name="koid" select="@koId"/>
				</xsl:call-template>
 				-->
			&lt;/td> 
		&lt;/tr>

		&lt;tr> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td>
				&lt;div class='contenuRessource'>
					<xsl:apply-templates select="local/description" />
					<xsl:apply-templates select="global/description" />
				&lt;/div><!-- contenuRessource -->
			&lt;/td>
			&lt;/tr>
		&lt;/table>
	&lt;/div><!-- div ressource -->
</xsl:template>
<!-- ========================================= -->
<!-- =========== TXDocument et TXURL =================== -->
<!-- ========================================= -->



<xsl:template match="ressource[@type='TX_Document']|ressource[@type='TX_URL']">
	&lt;div class='ressource2007'>
	&lt;table border ='0' > 
		&lt;tr>
			&lt;td width = "40px"> &lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/coop,'vrai')">
					&lt;img src='img/iconeCoop.gif' alt='Disponible à la COOP HEC'/>
				</xsl:if>
				<xsl:if test="not(contains(local/coop,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>  
			&lt;td width="49px">
				<xsl:if test="contains(local/biblio,'vrai')">
					&lt;img src='img/iconeBiblio.gif' alt='Disponible à la	bibliothèque' />
				</xsl:if>
				<xsl:if test="not(contains(local/biblio,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="20px">
				<xsl:if test="contains(local/complementaire,'vrai')">
					&lt;img src='img/iconeComp.gif' alt='Complémentaire' />
				</xsl:if>
				<xsl:if test="contains(local/obligatoire,'vrai')  ">
					&lt;img src='img/iconeObl.gif' alt='Obligatoire' />
				</xsl:if>
				<xsl:if	test="not(contains(local/obligatoire,'vrai')) and not(contains(local/complementaire,'vrai'))  ">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>
			
			&lt;td width = "20px" align = 'center'> 
					&lt;img src='img/point.gif' />
			&lt;/td> 
			&lt;td  align = "left"> 
			
					&lt;span class='titreRessource'>
						<xsl:if	test="contains(global/url,'http://') or contains(global/url,'ftp://')">
							&lt;a href='<xsl:value-of select="global/url" />' target='_blank'>
						</xsl:if>
						<xsl:if	test="not(contains(global/url,'http://')) and not(contains(global/url,'ftp://'))">
							&lt;a href='documents/<xsl:value-of select="@koId" />.<xsl:value-of select="substring-after(global/url,'/')" />' target='_blank'>
						</xsl:if>
						<xsl:value-of select="local/libelle" />
							&lt;/a>

					&lt;/span><!-- titreRessource -->
					<xsl:call-template name="boutonsVote">
							<xsl:with-param name="koid" select="global/code"/>
							<xsl:with-param name="docid" select="@koid"/>
						</xsl:call-template>
<!-- 
					<xsl:call-template name="boutonsVote">
						<xsl:with-param name="koid" select="@koId" />
					</xsl:call-template>
 -->
			&lt;/td> 
		&lt;/tr>

		&lt;tr> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td>
				&lt;div class='contenuRessource'>
					<xsl:apply-templates select="local/description" />
					<xsl:apply-templates select="global/description" />
				&lt;/div><!-- contenuRessource -->
			&lt;/td>
		&lt;/tr>
	&lt;/table>
	&lt;/div><!-- div ressource -->
</xsl:template>


<!-- ========================================= -->
<!-- =========== DocumentInterne ============= -->
<!-- ========================================= -->

<xsl:template match="ressource[@type='DocInterne']">
	&lt;div class='ressource2007'>
	&lt;table border ='0' > 
		&lt;tr>
		&lt;td width = "40px"> &lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/coop,'vrai')">
					&lt;img src='img/iconeCoop.gif' alt='Disponible à la COOP HEC'/>
				</xsl:if>
				<xsl:if test="not(contains(local/coop,'vrai'))">
					&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/biblio,'vrai')">
					&lt;img src='img/iconeBiblio.gif' alt='Disponible à la	bibliothèque' />
				</xsl:if>
				<xsl:if test="not(contains(local/biblio,'vrai'))">
						&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="20px">
				<xsl:if test="contains(local/complementaire,'vrai')">
					&lt;img src='img/iconeComp.gif' alt='Complémentaire' />
				</xsl:if>
				<xsl:if test="contains(local/obligatoire,'vrai')  ">
					&lt;img src='img/iconeObl.gif' alt='Obligatoire' />
				</xsl:if>
				<xsl:if	test="not(contains(local/obligatoire,'vrai')) and not(contains(local/complementaire,'vrai'))  ">
						&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>
			
			&lt;td width = "20px" align = 'center'> 
					&lt;img src='img/point.gif' />
			&lt;/td> 
			&lt;td width = "700px" align = "left"> 
				&lt;span class='titreRessource'>
					<xsl:for-each select="local/libelle">
						&lt;a href="javascript:winDocInt=window.open('af1DocumentInt.jsp?instId=<xsl:value-of select="../../@koId"/>&amp;lang=<xsl:value-of select="/planCours/@lang"/>','winDocInt', 'toolbar=no,menubar=no,scrollbars=yes,resizable=yes,screenX=450,screenY=450,width=800,height=420');winDocInt.focus();void(0);"><xsl:value-of select="."/>&lt;/a>
				</xsl:for-each>
				&lt;/span><!-- titreRessource -->
			&lt;/td> 
		&lt;/tr>

		&lt;tr> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td> &lt;/td> 
			&lt;td>
				&lt;div class='contenuRessource'>
					<xsl:apply-templates select="local/description" />
					<xsl:apply-templates select="global/documents" />
				&lt;/div><!-- contenuRessource -->
			&lt;/td>
			&lt;/tr>
		&lt;/table>
	&lt;/div><!-- div ressource -->
</xsl:template>


<!-- ========================================= -->
<!-- =========== Exercice =================== -->
<!-- ========================================= -->

<xsl:template match="ressource[@type='Exercice']">
	&lt;div class='ressource2007'>
	&lt;table border ='0' > 
		&lt;tr>
		&lt;td width = "40px"> &lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/coop,'vrai')">
					&lt;img src='img/iconeCoop.gif' alt='Disponible à la COOP HEC'/>
				</xsl:if>
				<xsl:if test="not(contains(local/coop,'vrai'))">
						&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="49px">
				<xsl:if test="contains(local/biblio,'vrai')">
					&lt;img src='img/iconeBiblio.gif' alt='Disponible à la	bibliothèque' />
				</xsl:if>
				<xsl:if test="not(contains(local/biblio,'vrai'))">
						&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td> 
			&lt;td width="20px">
				<xsl:if test="contains(local/complementaire,'vrai')">
					&lt;img src='img/iconeComp.gif' alt='Complémentaire' />
				</xsl:if>
				<xsl:if test="contains(local/obligatoire,'vrai')  ">
					&lt;img src='img/iconeObl.gif' alt='Obligatoire' />
				</xsl:if>
				<xsl:if	test="not(contains(local/obligatoire,'vrai')) and not(contains(local/complementaire,'vrai'))  ">
						&lt;p>&lt;/p> 
				</xsl:if>
			&lt;/td>
			
			&lt;td width = "20px" align = 'center'> 
					&lt;img src='img/point.gif' />
			&lt;/td> 
				
			&lt;td width = "700px" align = "left"> 
				&lt;span class='titreRessource'>
							<xsl:value-of select="local/libelle"/>
					<xsl:if test="global/enonces/ressource">
						&lt;img id='imgSujet<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>' src='img/<xsl:value-of select="/planCours/@lang"/>/sujetClose.jpg' onClick="toggleSujet<xsl:value-of select="/planCours/@lang"/>('<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>')"/>
					</xsl:if>
					<xsl:if test="global/solutions/ressource">
						&lt;img id='imgSolution<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>' src='img/<xsl:value-of select="/planCours/@lang"/>/solutionClose.jpg' onClick="toggleSolution<xsl:value-of select="/planCours/@lang"/>('<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>')"/>
					</xsl:if>
				&lt;/span><!-- titreRessource -->
			&lt;/td> 
		&lt;/tr>

		&lt;tr> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>&lt;/td> 
			&lt;td>
				&lt;div class='contenuRessource'>
					<xsl:apply-templates select="local/description" />
					<xsl:apply-templates select="global/description" />
					<xsl:if test="global/enonces/ressource">
						&lt;div class='enonce' id='SectionSujet<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>' style='display:none'>
							&lt;div class='titre'>
								<xsl:value-of select="$sujetLbl"/>
							&lt;/div>&lt;!-- titre -->
							<xsl:for-each select="global/enonces/ressource">
								<xsl:value-of select="global/texte" />
							</xsl:for-each>
						&lt;/div>
					</xsl:if>
					<xsl:if test="global/solutions/ressource">
						&lt;div class='solution' id='SectionSolution<xsl:number count="ressource[@type='Exercice']" level="any" format="1"/>' style='display:none'>
							&lt;div class='titre'>
								<xsl:value-of select="$solutionLbl"/>
							&lt;/div>&lt;!-- titre -->
							<xsl:for-each select="global/solutions/ressource">
								<xsl:value-of select="global/texte" />
							</xsl:for-each>
						&lt;/div>
					</xsl:if>
					<xsl:apply-templates select="global/documents" />
				&lt;/div><!-- contenuRessource -->
			&lt;/td>
			&lt;/tr>
		&lt;/table>
	&lt;/div><!-- div ressource -->
</xsl:template>


<!-- ========================================= -->
<!-- =========== Évaluation ================== -->
<!-- ========================================= -->

<xsl:template match="evaluation">
	&lt;div class='ressource'>
		&lt;div class="logoRessource">&lt;/div><!-- logoRessource -->
		&lt;div class='titreRessource'>
			<xsl:value-of select="global/libelle"/><xsl:text> (</xsl:text><xsl:value-of select="global/valeur"/><xsl:text>%)</xsl:text>
		&lt;/div><!-- titreRessource -->
		&lt;div class='contenuRessource'>
			<xsl:apply-templates select="local/*" />
			<xsl:apply-templates select="global/*" />
		&lt;/div><!-- contenuRessource -->
	&lt;/div><!-- div ressource -->
</xsl:template>

<xsl:template match="evaluation/global/date">
	&lt;div class='date'>
		<xsl:value-of select="$evalDateLbl"/><xsl:value-of select="."/>
	&lt;/div><!-- evaluationDate -->
</xsl:template>


<!-- ========================================= -->
<!-- =========== question-Reponse ================== -->
<!-- ========================================= -->

<xsl:template match="questrep">
	&lt;div class='ressource'>
		&lt;div class="logoRessource">&lt;/div><!-- logoRessource -->
		&lt;div class='titreRessource'>
			<xsl:value-of select="local/description"/>
		&lt;/div><!-- titreRessource -->
		&lt;div class='contenuRessource'>
		&lt;div class='contenuQuestion'>
			<xsl:text>Q: </xsl:text><xsl:value-of select="global/question" />
		&lt;/div><!-- contenuQuestion -->
		&lt;div class='contenuReponse'> 
			<xsl:text>R: </xsl:text><xsl:value-of select="global/reponse" />
			&lt;/div><!-- contenuReponse-->
		&lt;/div><!-- contenuRessource -->
	&lt;/div><!-- div ressource -->
</xsl:template>



<!-- ========================================= -->
<!-- =========== Documents attachés ========== -->
<!-- ========================================= -->

<xsl:template match="documents">
			<xsl:apply-templates select="*" />
</xsl:template>


</xsl:stylesheet>
