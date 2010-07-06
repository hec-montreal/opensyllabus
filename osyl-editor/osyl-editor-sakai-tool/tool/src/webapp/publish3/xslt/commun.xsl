<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template name="head">
	&lt;head&gt;
		&lt;meta http-equiv="Content-Type" content="text/html; charset=Windows-1252" /&gt;
		&lt;link name='css' rel="stylesheet" TYPE='text/css' href='css/cmsie.css' title='default'/&gt;
		&lt;script language='JavaScript1.2' src='js/commun.js'&gt;&lt;/script&gt;

		&lt;title&gt;
			<xsl:call-template name="codeTiret"/>
			<xsl:text> - </xsl:text>
			<xsl:value-of select="/planCours/cours/libelle"/>
		&lt;/title&gt;

	&lt;/head&gt;
</xsl:template>

<xsl:template name="headerPage">

	&lt;div id='zoom'&gt;
		&lt;a href="javascript:zoomPlus()"&gt;&lt;img src='img/zoomPlus.gif' border='0'&gt;&lt;/a&gt;
		&lt;a href="javascript:toggleMenu()"&gt;&lt;img id='imgMenu' src='img/menuOff.gif' border='0'&gt;&lt;/a&gt;
		&lt;a href="javascript:zoomMoins()"&gt;&lt;img src='img/zoomMoins.gif' border='0'&gt;&lt;/a&gt;
	&lt;/div&gt;
	&lt;div id='headerPage'&gt;
		&lt;table width="100%" border="0" cellspacing="0" cellpadding="0"&gt;
			&lt;tr valign="top"&gt;
				&lt;td width="89%"&gt;
					&lt;table border="0" cellspacing="0" cellpadding="0"&gt;
						&lt;tr valign="top"&gt;
							&lt;td&gt;
								&lt;a href="http://www.hec.ca"&gt;
									&lt;img src="img/Logo1.gif" border="0" alt="HEC Montr&amp;eacute;al"&gt;
								&lt;/a&gt;
							&lt;/td&gt;
							&lt;td&gt;
								&lt;a href="http://zonecours.hec.ca"&gt;
									&lt;img src="img/Logo2.gif" width="343" height="55" border="0" alt="Zone Cours"&gt;
								&lt;/a&gt;
							&lt;/td&gt;
							&lt;td valign="top"&gt;
								&lt;a href="af1Aide.jsp?" target="blank"&gt;&lt;img src="img/aide.gif" width="36" height="44" border="0"/&gt;&lt;/a&gt;
							&lt;/td&gt;
						&lt;/tr&gt;
					&lt;/table&gt;
				&lt;/td&gt;
			&lt;/tr&gt;
		&lt;/table&gt;
	&lt;/div&gt;


</xsl:template>

<!-- pour legende version yvette    -->
<!--  
<xsl:template name="headerPage">

	&lt;div id='zoom'&gt;   &lt;/div&gt;

	&lt;div id='headerPage'&gt;
		&lt;table width="100%" border="0" cellspacing="0" cellpadding="0"&gt;
			&lt;tr valign="top"&gt;
				&lt;td width="89%"&gt;
					&lt;table border="0" cellspacing="0" cellpadding="0"&gt;
						&lt;tr valign="top"&gt;
							&lt;td&gt;
								&lt;a href="http://www.hec.ca"&gt;
									&lt;img src="img/Logo1.gif" border="0" alt="HEC Montr&amp;eacute;al"&gt;
								&lt;/a&gt;
							&lt;/td&gt;
							&lt;td&gt;
								&lt;a href="http://zonecours.hec.ca"&gt;
									&lt;img src="img/Logo2.gif" width="343" height="55" border="0" alt="Zone Cours"&gt;
								&lt;/a&gt;
							&lt;/td&gt;
							
							
		
							
							&lt;td valign="top"&gt;

							&lt;/td&gt;
						&lt;/tr&gt;
					&lt;/table&gt;
				&lt;/td&gt;
								
				&lt;td&gt; 
				
				
				&lt;table bordercolor=#009900 border="0" width="157"&gt; &lt;td&gt; &lt;/td&gt;
					&lt;tr&gt; 
						&lt;td &gt;  
				&lt;p align="right"&gt;&lt;font face="Calibri"&gt;
					&lt;a href="#" onmouseover="pop('img/iconeObl.GIF','img/iconeComp.GIF','img/iconeBiblio.GIF','img/iconeCoop.GIF', 'Obligatoire','Complémentaire','Disponible à la Bibliothèque','Disponible à la COOP HEC','#ffffff')"; onmouseout="kill()"&gt;
					&lt;font face="calibri" size="2" color="#000088"&gt;L&amp;eacute;gende des ressources&lt;/font&gt;&lt;/a&gt;&lt;/font&gt;&lt;/td&gt;
					&lt;/tr&gt;
					&lt;/table&gt;
				&lt;/td&gt;
				&lt;td &gt; width="88" &lt;table bordercolor="#009900" border="0"&gt; &lt;td &gt;  
					&lt;p align="right"&gt; 
					&lt;a href="javascript:zoomPlus()"&gt;&lt;img src='img/zoomPlus.gif' border='0'&gt;&lt;/a&gt;
					&lt;a href="javascript:toggleMenu()"&gt;&lt;img id='imgMenu' src='img/menuOff.gif' border='0'&gt;&lt;/a&gt;
					&lt;a href="javascript:zoomMoins()"&gt;&lt;img src='img/zoomMoins.gif' border='0'&gt;&lt;/a&gt;
										
					&lt;/td&gt;&lt;/table&gt;&lt;/td&gt;						
			&lt;/tr&gt;
		&lt;/table&gt;
	&lt;/div&gt;

</xsl:template>
-->

<xsl:template name="separateur">
&lt;div id='margehr'&gt;
	&lt;hr align="right" width=100%/&gt;
	&lt;/div&gt;
</xsl:template>
<xsl:template name="sep">
	&lt;hr align="right" width=100%/&gt;
</xsl:template>

<xsl:template name="footerPage">


	&lt;div id='footer'&gt;
		&lt;div class='dateMaj'&gt;
			<xsl:value-of select="$dateMajLbl"/><xsl:value-of select="substring(/planCours/@dateMaj,1,16)"/>
		&lt;/div&gt;
		
		&lt;div class='teximus'&gt;
			&lt;a href='http://www.teximus.com'&gt;&amp;nbsp;&lt;/a&gt;
		&lt;/div&gt;
		
		&lt;div class='service'&gt;
			<xsl:value-of select="/planCours/cours/service"/>
		&lt;/div&gt;
		
		&lt;div class='copyright'&gt;
			<xsl:if test="not(planCours/@type='annuaire')">
					<xsl:value-of select="/planCours/professeur/prenom"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="/planCours/professeur/nom"/>
					<xsl:text> </xsl:text>
			</xsl:if>
			© HEC Montr&amp;eacute;al, <xsl:value-of select="substring(/planCours/@dateMaj,1,4)"/>. Tous droits réservés.
		&lt;/div&gt;

	&lt;/div&gt;
</xsl:template>


<xsl:template name="footerPage2">
	&lt;div id='footer2'&gt;
		&lt;a href='http://www.teximus.com'&gt;&lt;img src='img/LogoTeximus.gif' class='teximusNew'&gt;&lt;/a&gt;
	
		&lt;div class='dateMaj'&gt;
			<xsl:value-of select="$dateMajLbl"/><xsl:value-of select="substring(/planCours/@dateMaj,1,16)"/>
		&lt;/div&gt;
		
		&lt;div class='service'&gt;
			<xsl:value-of select="/planCours/cours/service"/>
		&lt;/div&gt;
		
		&lt;div class='copyright'&gt;
			<xsl:if test="not(planCours/@type='annuaire')">
					<xsl:value-of select="/planCours/professeur/prenom"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="/planCours/professeur/nom"/>
					<xsl:text> </xsl:text>
			</xsl:if>
			© HEC Montr&amp;eacute;al, <xsl:value-of select="substring(/planCours/@dateMaj,1,4)"/>. Tous droits réservés.
		&lt;/div&gt;

	&lt;/div&gt;
</xsl:template>


<xsl:template name="scriptToggleExercice">
	&lt;script&gt;
		function toggleSujetfr(i){
			if (document.getElementById('SectionSujet'+i).style.display == "block") {
				document.getElementById('SectionSujet'+i).style.display = "none";
				document.getElementById('imgSujet'+i).src = "img/fr/sujetClose.jpg";
				}
			else {
				document.getElementById('SectionSujet'+i).style.display = "block";
				document.getElementById('imgSujet'+i).src = "img/fr/sujetOpen.jpg";
				}
			return true;
		}
		function toggleSujeten(i){
			if (document.getElementById('SectionSujet'+i).style.display == "block") {
				document.getElementById('SectionSujet'+i).style.display = "none";
				document.getElementById('imgSujet'+i).src = "img/en/sujetClose.jpg";
				}
			else {
				document.getElementById('SectionSujet'+i).style.display = "block";
				document.getElementById('imgSujet'+i).src = "img/en/sujetOpen.jpg";
				}
			return true;
		}
		function toggleSujetes(i){
			if (document.getElementById('SectionSujet'+i).style.display == "block") {
				document.getElementById('SectionSujet'+i).style.display = "none";
				document.getElementById('imgSujet'+i).src = "img/es/sujetClose.jpg";
				}
			else {
				document.getElementById('SectionSujet'+i).style.display = "block";
				document.getElementById('imgSujet'+i).src = "img/es/sujetOpen.jpg";
				}
			return true;
		}
		function toggleSolutionfr(i){
			if (document.getElementById('SectionSolution'+i).style.display == "block") {
				document.getElementById('SectionSolution'+i).style.display = "none";
				document.getElementById('imgSolution'+i).src = "img/fr/solutionClose.jpg";
				}
			else {
				document.getElementById('SectionSolution'+i).style.display = "block";
				document.getElementById('imgSolution'+i).src = "img/fr/solutionOpen.jpg";
				}
			return true;
		}
		function toggleSolutionen(i){
			if (document.getElementById('SectionSolution'+i).style.display == "block") {
				document.getElementById('SectionSolution'+i).style.display = "none";
				document.getElementById('imgSolution'+i).src = "img/en/solutionClose.jpg";
				}
			else {
				document.getElementById('SectionSolution'+i).style.display = "block";
				document.getElementById('imgSolution'+i).src = "img/en/solutionOpen.jpg";
				}
			return true;
		}
		function toggleSolutiones(i){
			if (document.getElementById('SectionSolution'+i).style.display == "block") {
				document.getElementById('SectionSolution'+i).style.display = "none";
				document.getElementById('imgSolution'+i).src = "img/es/solutionClose.jpg";
				}
			else {
				document.getElementById('SectionSolution'+i).style.display = "block";
				document.getElementById('imgSolution'+i).src = "img/es/solutionOpen.jpg";
				}
			return true;
		}
	&lt;/script&gt;
</xsl:template>


<xsl:template name="scriptZoom">
	&lt;script&gt;
		function zoomPlus() {
		  if (!document.styleSheets) return;
		  var regles=new Array();
		  if (document.styleSheets[0].cssRules) regles=document.styleSheets[0].cssRules
		  else if (document.styleSheets[0].rules) regles = document.styleSheets[0].rules
		  else return
		  sizeS = regles[0].style.fontSize;
		  sizeN = parseInt(sizeS.substring(0,sizeS.length-1))+10;
		  size = sizeN+"%"
		  regles[0].style.fontSize = size;

		}
		function zoomMoins() {
		  if (!document.styleSheets) return;
		  var regles=new Array();
		  if (document.styleSheets[0].cssRules) regles=document.styleSheets[0].cssRules
		  else if (document.styleSheets[0].rules) regles = document.styleSheets[0].rules
		  else return
		  sizeS = regles[0].style.fontSize;
		  sizeN = parseInt(sizeS.substring(0,sizeS.length-1))-10;
		  size = sizeN+"%"
		  regles[0].style.fontSize = size;

		}
	&lt;/script&gt;
</xsl:template>






<xsl:template name="menu">
	<xsl:param name="lang">fr</xsl:param>
	<xsl:param name="code">1</xsl:param>
	<xsl:param name="page">1</xsl:param>
	<xsl:param name="numSeance">0</xsl:param>
	<xsl:param name="typeSeance">Seance</xsl:param>

		&lt;div id='menu'&gt;
			&lt;div class='itemMenu'&gt;

			&lt;div class='titreMenu'&gt;Menu&lt;/div&gt;
				<xsl:if test="$page=11">&lt;div class="menuon"&gt;<xsl:value-of select="$a11menuLbl"/>&lt;/div&gt;</xsl:if>
				<xsl:if test="not($page=11)">&lt;a class="menuoff2" href="af1Presentation.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a11menuLbl"/>&lt;/a&gt;</xsl:if>
			&lt;/div&gt;

			<xsl:if test="not(planCours/@type='annuaire')">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=12">&lt;div class="menuon"&gt;<xsl:value-of select="$a12menuLbl"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=12)">&lt;a class="menuoff2" href="af1Coordonnee.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a12menuLbl"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>

			<xsl:if test="not(planCours/@type='annuaire') or (planCours/@type='annuaire' and planCours/materiel)">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=13">&lt;div class="menuon"&gt;<xsl:value-of select="$a13menuLbl"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=13)">&lt;a class="menuoff2" href="af1Materiel.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a13menuLbl"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>

			<xsl:if test="not(planCours/@type='annuaire')">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=14">&lt;div class="menuon"&gt;<xsl:value-of select="$a14menuLbl"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=14)">&lt;a class="menuoff2" href="af1Evaluation.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a14menuLbl"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>
			<xsl:if test="planCours/@type='annuaire' and planCours/evaluations">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=14">&lt;div class="menuon"&gt;<xsl:value-of select="$a14menuLbla"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=14)">&lt;a class="menuoff2" href="af1Evaluation.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a14menuLbla"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>

			<xsl:if test="not(planCours/@type='annuaire') or (planCours/@type='annuaire' and planCours/seances)">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=15">&lt;div class="menuon"&gt;<xsl:value-of select="$a15menuLbl"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=15)">&lt;a class="menuoff2" href="af1SeanceListe.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a15menuLbl"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>
			
			<!-- afficher FAQ : Awa 2008-02-07
				 afficher FAQ au besoin : Awa 2008-03-25 -->
	
			<xsl:if test="planCours/faq/*">
				<xsl:if test="not(planCours/@type='annuaire') or (planCours/@type='annuaire' and planCours/faq)">
					&lt;div class='itemMenu'&gt;
						<xsl:if test="$page=22">&lt;div class="menuon"&gt;<xsl:value-of select="$a22menuLbl"/>&lt;/div&gt;</xsl:if>
						<xsl:if test="not($page=22)">&lt;a class="menuoff2" href="af1FAQ.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a22menuLbl"/>&lt;/a&gt;</xsl:if>
					&lt;/div&gt;
				</xsl:if>
			</xsl:if>	
			

			&lt;div class='menuImprimables'&gt;

			<xsl:if test="not(planCours/@type='annuaire')">
				&lt;div class='itemMenu'&gt;
					<xsl:if test="$page=16">&lt;div class="menuon"&gt;<xsl:value-of select="$a16menuLbl"/>&lt;/div&gt;</xsl:if>
					<xsl:if test="not($page=16)">&lt;a class="menuoff2" href="af1CodexListe.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>"&gt;<xsl:value-of select="$a16menuLbl"/>&lt;/a&gt;</xsl:if>
				&lt;/div&gt;
			</xsl:if>
			<xsl:if test="planCours/@type='annuaire'">
				&lt;div class='itemMenu'&gt;
					&lt;a href='af1CodexImp.jsp?instId=<xsl:value-of select="planCours/@koId"/>'&gt;<xsl:value-of select="$planCoursImprimable"/>&lt;/a&gt;
				&lt;/div&gt;
			</xsl:if>

			&lt;/div&gt;&lt;!-- div menuImprimables --&gt;
			<xsl:if test="planCours/@type='annuaire'">
				&lt;br&gt;
				&lt;br&gt;
				&lt;br&gt;
			</xsl:if>

			<!-- Van, besoin verifier le niveau de securite ici avant d"afficher les nouvelles -->
			<xsl:if test="not(planCours/@type='annuaire')">
				<xsl:call-template name="nouvellesCours"/>
				<xsl:if test="not($numSeance=0)">
						<xsl:call-template name="nouvellesSeance">
							<xsl:with-param name="numSeance" select="$numSeance"/>
							<xsl:with-param name="typeSeance" select="$typeSeance"/>
						</xsl:call-template>
				</xsl:if>

				&lt;div class='archiveMenu'&gt;
					&lt;a class="menuoff2" href='af1ArchiveVaria.jsp?instId=<xsl:value-of select="$code"/>&amp;lang=<xsl:value-of select="$lang"/>' target='_blank' &gt;<xsl:value-of select="$archiveMenuLbl"/>&lt;/a&gt;
				&lt;/div&gt;
			</xsl:if>
			
			<!-- ========= vote ===== -->
			<xsl:if test="planCours/@vote">
				&lt;input type='submit' value='voter' />
				&lt;a href = "voter.jsp">&lt;img src="img/votez.jpg"> &lt;/img>&lt;/a> 
			</xsl:if>
			<!-- ==================== -->


		&lt;/div&gt;&lt;!-- menu --&gt;

</xsl:template>

<xsl:template name="nouvellesCours">
<!-- Mise a jour: 15-avril-2008 Message plagiat et politique d'usage des calculatrices
				sont toujours affiches dans le menu a droite -->
				&lt;div <xsl:value-of select="$plagiatCalculatriceLabel"/>&lt;/div&gt;
	<xsl:for-each select="/planCours/cours/nouvelles[texte/@inPce='true']">
		<xsl:if test="position()=1">
			&lt;div class='cours-nouvelles'&gt;
			&lt;div class='titreMenu'&gt;<xsl:value-of select="$DiversCoursmenuLbl"/>&lt;/div&gt;
		</xsl:if>
		<xsl:for-each select="texte[@inPce='true']">
			<!-- <xsl:sort select="@date" order="descending"/> -->
			<xsl:call-template name="nouvelle"/>
		</xsl:for-each>
		<xsl:if test="position()=last()">
			&lt;/div&gt;&lt;!-- cours-nouvelles --&gt;
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="nouvellesSeance">
	<xsl:param name="numSeance">1</xsl:param>
	<xsl:param name="typeSeance">Seance</xsl:param>

	<xsl:for-each select="/planCours/seances/seance[@type=$typeSeance][@no=$numSeance]/ressources/rubriqueNouvelle/ressource[global/texte/@inPce='true']">
		<xsl:if test="position()=1">
			&lt;div class='seance-nouvelles'&gt;
			&lt;div class='hrMenu'&gt;&lt;/div&gt;
			&lt;div class='titreMenu'&gt;<xsl:value-of select="$DiversSeancemenuLbl"/>&lt;/div&gt;
		</xsl:if>
		<xsl:for-each select="global/texte[@inPce='true']">
			<!-- <xsl:sort select="@date" order="descending"/> -->
			<xsl:call-template name="nouvelle"/>
		</xsl:for-each>
		<xsl:if test="position()=last()">
			&lt;/div&gt;&lt;!-- seance-nouvelles --&gt;
		</xsl:if>
	</xsl:for-each>
</xsl:template>

<xsl:template name="nouvelle">
			&lt;div class='textenouvelles'&gt;
				<xsl:value-of select="."/>
			&lt;/div&gt;
			&lt;div class='datenouvelles'&gt;
				<xsl:text>(</xsl:text>
				<xsl:value-of select="substring(@date,1,16)"/>
				<xsl:text>)</xsl:text>
			&lt;/div&gt;
</xsl:template>


<xsl:template name="cours">
	&lt;div id='cours'&gt;
		&lt;div id='codeCours'&gt;<xsl:call-template name="codeTiret"/><xsl:text> - </xsl:text>&lt;/div&gt;
		&lt;div id='libelleCours'&gt;<xsl:value-of select="/planCours/cours/libelle"/>&lt;/div&gt;
	&lt;/div&gt;&lt;!-- cours --&gt;
	<xsl:if test="/planCours/cours/session">
		&lt;div id='session-section'&gt;
			&lt;div id='sessionCours'&gt;<xsl:value-of select="/planCours/cours/session/libelle"/>&lt;/div&gt;
			&lt;div id='separateur-session-section'&gt;<xsl:text>:</xsl:text>&lt;/div&gt;
			&lt;div id='section'&gt;<xsl:value-of select="/planCours/cours/section/no"/>&lt;/div&gt;
		&lt;/div&gt;&lt;!-- session-section --&gt;
	</xsl:if>
</xsl:template>


<xsl:template name="professeur">
	<xsl:if test="not(planCours/@type='annuaire')">
		&lt;div id='profs'&gt;
			<xsl:value-of select="/planCours/@enseignant"/>
		&lt;/div&gt;&lt;!-- profs --&gt;
	</xsl:if>
</xsl:template>

<xsl:template match="coordonnee">
	&lt;div class='rubrique'&gt;
		&lt;div class="logoRubrique"&gt;&lt;/div&gt;
		&lt;div class='titreRubrique'&gt;
			<xsl:value-of select="role"/><xsl:text> : </xsl:text><xsl:value-of select="prenom"/><xsl:text> </xsl:text><xsl:value-of select="nom"/>
		&lt;/div&gt;&lt;!-- titreRubrique --&gt;
		&lt;div class='contenuRubrique'&gt;
			&lt;div class='bureau'&gt;<xsl:apply-templates select="bureau"/>&lt;/div&gt;&lt;!-- bureau --&gt;
			&lt;div class='courriel'&gt;<xsl:apply-templates select="courriel"/>&lt;/div&gt;&lt;!-- courriel --&gt;
			&lt;div class='telephone'&gt;<xsl:apply-templates select="telephone"/>&lt;/div&gt;&lt;!-- telephone --&gt;
			&lt;div class='disponibilite'&gt;<xsl:apply-templates select="disponibilite"/>&lt;/div&gt;&lt;!-- disponibilite --&gt;
			&lt;div class='commentaires'&gt;<xsl:apply-templates select="commentaire"/>&lt;/div&gt;&lt;!-- commentaires --&gt;
		&lt;/div&gt;&lt;!-- contenuRubrique --&gt;
	&lt;/div&gt;&lt;!-- div rubrique --&gt;
</xsl:template>

<xsl:template match="bureau">
		&lt;div class='libelleBureau'&gt;
			<xsl:value-of select="$bureauLbl"/>
		&lt;/div&gt;&lt;!-- libelleBureau --&gt;
		&lt;div class='contenuBureau'&gt;
			<xsl:value-of select="."/>
		&lt;/div&gt;&lt;!-- contenuBureau --&gt;
</xsl:template>

<xsl:template match="courriel">
		&lt;div class='libelleCourriel'&gt;
			<xsl:value-of select="$courrielLbl"/>
		&lt;/div&gt;&lt;!-- libelleCourriel --&gt;
		&lt;div class='contenuCourriel'&gt;
			<xsl:value-of select="."/>
		&lt;/div&gt;&lt;!-- contenuCourriel --&gt;
</xsl:template>

<xsl:template match="telephone">
		&lt;div class='libelleTelephone'&gt;
			<xsl:value-of select="$telLbl"/>
		&lt;/div&gt;&lt;!-- libelleTelephone --&gt;
		&lt;div class='contenuTelephone'&gt;
			<xsl:value-of select="."/>
		&lt;/div&gt;&lt;!-- contenuTelephone --&gt;
</xsl:template>

<xsl:template match="disponibilite">
		&lt;div class='libelleDispo'&gt;
			<xsl:value-of select="$dispoLbl"/>
		&lt;/div&gt;&lt;!-- libelleDispo --&gt;
		&lt;div class='contenuDispo'&gt;
			<xsl:value-of select="."/>
		&lt;/div&gt;&lt;!-- contenuDispo --&gt;
</xsl:template>

<xsl:template match="commentaire">
		&lt;div class='contenuCommentaire'&gt;
			<xsl:value-of select="."/>
		&lt;/div&gt;&lt;!-- contenuCommentaire --&gt;
</xsl:template>
<!-- =================================================================================== -->
<xsl:template name="plagiat">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			&lt;a name="#jumpPlagiat"><xsl:value-of select="$plagiatLbl"/>&lt;/a>
		&lt;/div>&lt;!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			&lt;div class='texte'>
				<xsl:if test="/planCours[@programme='BAA']">
					<xsl:value-of select="$plagiatBAA"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='MSC']">
					<xsl:value-of select="$plagiatMSC"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='MSCP']">
					<xsl:value-of select="$plagiatMSC"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='MBA']">
					<xsl:value-of select="$plagiatMBA"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='PHD']">
					<xsl:value-of select="$plagiatPHD"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='CERT']">
					<xsl:value-of select="$plagiatCERT"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='DES']">
					<xsl:value-of select="$plagiatDES"/>&lt;/a>
				</xsl:if>
				<xsl:if test="/planCours[@programme='APRE']">
					<xsl:value-of select="$plagiatAPRE"/>&lt;/a>
				</xsl:if>
			&lt;/div>&lt;!-- texte -->
		&lt;/div>&lt;!-- contenuRubrique -->
	&lt;/div>&lt;!-- div rubrique -->

</xsl:template>

	<!-- === Mise a jour: 15-avril-2008 par Van:  Message d'usage des calculatrices
		sont toujours affiches tout de suite apres le message Plagiat a gauche === -->
<xsl:template name="usageCalculatrice">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique"> &lt;/div>	<!-- logoRubrique -->

		&lt;div class='titreRubrique'>
			&lt;a name="#jumpCalculatrice">&lt;/a>
		&lt;/div> <!-- titreRubrique -->
		
		&lt;div class='contenuRubrique'>
			&lt;div class='texte'>
				<xsl:value-of select="$usageCalculatriceLabel"/>
			&lt;/div> <!-- texte -->
		&lt;/div>			
	&lt;/div> 
</xsl:template>


<!-- ============  -->
<xsl:template name="codeTiret">
	<xsl:choose>
		<xsl:when test="contains(/planCours/cours/no,'A')">
			<xsl:value-of select="/planCours/cours/no"/>
		</xsl:when>
		<xsl:when test="contains(/planCours/cours/no,'E')">
			<xsl:value-of select="/planCours/cours/no"/>
		</xsl:when>
		<xsl:when test="contains(/planCours/cours/no,'I')">
			<xsl:value-of select="/planCours/cours/no"/>
		</xsl:when>
		<xsl:when test="contains(/planCours/cours/no,'O')">
			<xsl:value-of select="/planCours/cours/no"/>
		</xsl:when>
		<xsl:when test="contains(/planCours/cours/no,'U')">
			<xsl:value-of select="/planCours/cours/no"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="substring(/planCours/cours/no,0,string-length(/planCours/cours/no)-4)"/>
			<xsl:text>-</xsl:text>
			<xsl:value-of select="substring(/planCours/cours/no,string-length(/planCours/cours/no)-4,3)"/>
			<xsl:text>-</xsl:text>
			<xsl:value-of select="substring(/planCours/cours/no,string-length(/planCours/cours/no)-1,2)"/> 
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>


</xsl:stylesheet>