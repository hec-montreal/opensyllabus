<?xml version="1.0"  encoding="ISO-8859-1" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Pr�sentation</xsl:variable>
<xsl:variable name='a12Lbl'>Coordonn�es</xsl:variable>
<xsl:variable name='a13Lbl'>Mat�riel p�dagogique</xsl:variable>
<xsl:variable name='a14Lbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a14Lbla'>�valuation</xsl:variable>
<xsl:variable name='a15Lbl'>Liste des s�ances</xsl:variable>
<xsl:variable name='a15LblTP'>Liste des travaux pratiques</xsl:variable>
<xsl:variable name='a15LblTheme'>Liste des th�mes</xsl:variable>
<xsl:variable name='a16Lbl'>Documents imprimables</xsl:variable>
<xsl:variable name='a18Lbl'>Liste des exercices</xsl:variable>
<xsl:variable name='a19Lbl'>Liste des r�f�rences bibliographiques</xsl:variable>
<xsl:variable name='a21Lbl'>Archives des nouvelles</xsl:variable>
<xsl:variable name='a22Lbl'>FAQ</xsl:variable>
<xsl:variable name='a22Lbla'>Foire aux questions</xsl:variable>

<!-- ========================= -->
<!-- ======= Menus =========== -->
<!-- ========================= -->

<xsl:variable name='a11menuLbl'>Pr�sentation</xsl:variable>
<xsl:variable name='a12menuLbl'>Coordonn�es</xsl:variable>
<xsl:variable name='a13menuLbl'>Mat�riel p�dagogique</xsl:variable>
<xsl:variable name='a14menuLbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a14menuLbla'>�valuation</xsl:variable>
<xsl:variable name='a15menuLbl'>S�ances</xsl:variable>
<xsl:variable name='a22menuLbl'>FAQ</xsl:variable>
<xsl:variable name='a16menuLbl'>Documents imprimables</xsl:variable>

<xsl:variable name='DiversCoursmenuLbl'>Nouvelles - Cours</xsl:variable>

<!-- Mise a jour: 15-avril-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite -->
<xsl:variable name='plagiatCalculatriceLabel_version15Avril2008'>
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Plagiat &lt;/b&gt; : Les �tudiants sont pri�s de consulter &lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt;l'article 12 &lt;/a&gt; du R�glement r�gissant l'activit� �tudiante � HEC Montr�al intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3). Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence. 
		&lt;/div &gt;
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Calculatrice &lt;/b&gt;: Les �tudiants sont pri�s de prendre connaissance de la &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank"&gt; politique d'utilisation de calculatrices &lt;/a&gt;lors d'examens lorsque celles-ci sont autoris�es.
		&lt;/div &gt;
		&lt;br&gt;
</xsl:variable>

<!-- Mise a jour: 9-juillet-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite, sous forme hyperlien -->
<xsl:variable name='plagiatCalculatriceLabel'>
		&lt;br&gt;
		R�glements
		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank" &gt; Plagiat et fraude &lt;/a&gt;  

		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank" &gt; Calculatrices &lt;/a&gt;  
		&lt;br&gt;
		
		<xsl:call-template name="separateur"/>
		
</xsl:variable>


<!-- Mise a jour: 15-avril-2008 apr Van: Message d'usage des calculatrices
	sont toujours affiches tout de suite apres le message Plagiat a gauche -->
<xsl:variable name='usageCalculatriceLabel'>
	&lt;b&gt; Calculatrice &lt;/b&gt;
	&lt;br&gt; Les �tudiants sont pri�s de prendre connaissance de la &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank"&gt; politique d'utilisation de calculatrices &lt;/a&gt;lors d'examens lorsque celles-ci sont autoris�es.
	&lt;br&gt;
</xsl:variable> 

<xsl:variable name='DiversSeancemenuLbl'>Nouvelles - S�ance</xsl:variable>

<xsl:variable name='archiveMenuLbl'>Archives des nouvelles</xsl:variable>

<!-- ========================= -->
<!-- = documents imprimables = -->
<!-- ========================= -->

<xsl:variable name='planCoursImprimable'>Plan de cours imprimable</xsl:variable>
<xsl:variable name='planCoursImprimableSsTitre'>Coordonn�es | Pr�sentation | �valuation | Mat�riel p�dagogique | S�ances</xsl:variable>

<xsl:variable name='exercicesImprimable'>Liste des exercices</xsl:variable>
<xsl:variable name='exercicesImprimableSsTitre'>�nonc�s | Solutions</xsl:variable>

<xsl:variable name='referencesImprimable'>Liste des lectures</xsl:variable>
<xsl:variable name='referencesImprimableSsTitre'>R�f�rences bibliographiques | Lectures</xsl:variable>

<!-- ========================= -->
<!-- ======= Libell�s ======== -->
<!-- ========================= -->

<xsl:variable name='seanceLbl'>S�ance</xsl:variable>
<xsl:variable name='tpLbl'>TP</xsl:variable>
<xsl:variable name='themeLbl'>Th�me</xsl:variable>
<xsl:variable name='themeTitreLbl'>Titre</xsl:variable>
<xsl:variable name='seancesLbl'>S�ances</xsl:variable>
<xsl:variable name='tpsLbl'>Travaux pratiques</xsl:variable>
<xsl:variable name='enoncesLbl'>�nonc�s</xsl:variable>
<xsl:variable name='solutionsLbl'>Solutions</xsl:variable>

<!-- ========================= -->
<!-- ======= Rubriques ======= -->
<!-- ========================= -->

<xsl:variable name='biblioTag'>rubriqueBibliographie</xsl:variable>
<xsl:variable name='biblioLbl'>R�f�rences bibliographiques</xsl:variable>

<xsl:variable name='descriptionTag'>rubriqueDescription</xsl:variable>
<xsl:variable name='descriptionLbl'>Description</xsl:variable>

<xsl:variable name='objectifsTag'>rubriqueObjectifs</xsl:variable>
<xsl:variable name='objectifsLbl'>Objectifs</xsl:variable>

<xsl:variable name='evaluationTag'>evaluations</xsl:variable>
<xsl:variable name='evaluationLbl'>�valuation</xsl:variable>

<xsl:variable name='approcheTag'>rubriqueApprochePedagogique</xsl:variable>
<xsl:variable name='approcheLbl'>Approche p�dagogique</xsl:variable>

<xsl:variable name='supportTag'>rubriqueSupportsCours</xsl:variable>
<xsl:variable name='supportLbl'>Ressources utilis�es pendant la s�ance</xsl:variable>

<xsl:variable name='lecturesTag'>rubriqueLectures</xsl:variable>
<xsl:variable name='lecturesLbl'>Lectures</xsl:variable>

<xsl:variable name='exercicesTag'>rubriqueExercice</xsl:variable>
<xsl:variable name='exercicesLbl'>Exercices</xsl:variable>

<xsl:variable name='ressourcesTag'>rubriqueRessourcesGenerales</xsl:variable>
<xsl:variable name='ressourcesLbl'>Ressources g�n�rales</xsl:variable>

<xsl:variable name='ressourcesComplementairesTag'>rubriqueRessourcesComplementaires</xsl:variable>
<xsl:variable name='ressourcesComplementairesLbl'>Ressources Compl�mentaires</xsl:variable>

<xsl:variable name='devoirsTag'>rubriqueDevoirs</xsl:variable>
<xsl:variable name='devoirsLbl'>Devoirs</xsl:variable>

<xsl:variable name='outilsTag'>rubriqueOutils</xsl:variable>
<xsl:variable name='outilsLbl'>Outils</xsl:variable>

<xsl:variable name='anciensexamsTag'>rubriqueAnciensExamens</xsl:variable>
<xsl:variable name='anciensexamsLbl'>Anciens examens</xsl:variable>

<xsl:variable name='casTag'>rubriqueCas</xsl:variable>
<xsl:variable name='casLbl'>Cas</xsl:variable>

<xsl:variable name='notesFormTag'>rubriqueNotesFormateur</xsl:variable>
<xsl:variable name='notesFormLbl'>Notes de l'enseignant</xsl:variable>

<xsl:variable name='diversTag'>rubriqueDivers</xsl:variable>
<xsl:variable name='diversLbl'>Divers</xsl:variable>

<xsl:variable name='nouvelleTag'>rubriqueNouvelle</xsl:variable>

<xsl:variable name='diversCoursLbl'>Nouvelles du cours</xsl:variable>
<xsl:variable name='diversSeanceLbl'>Nouvelles de la s�ance</xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'>Bureau : </xsl:variable>
<xsl:variable name='courrielLbl'>Courriel : </xsl:variable>
<xsl:variable name='telLbl'>T�l�phone : </xsl:variable>
<xsl:variable name='dispoLbl'>Disponibilit� : </xsl:variable>
<xsl:variable name='evalDateLbl'>Date : </xsl:variable>
<xsl:variable name='isbnLbl'>ISBN : </xsl:variable>

<!-- ========================= -->
<!-- ======= Autres ========== -->
<!-- ========================= -->

<xsl:variable name='sujetLbl'>Sujet</xsl:variable>
<xsl:variable name='solutionLbl'>Solution</xsl:variable>
<xsl:variable name='dateMajLbl'>Derni�re mise � jour: </xsl:variable>
<xsl:variable name='plagiatLbl'>Plagiat</xsl:variable>
<xsl:variable name='calculatriceLbl'>Calculatrice</xsl:variable>

<xsl:variable name='plagiatBAA'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatPHD'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatMSC'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatMBA'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatCERT'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatDES'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>
<xsl:variable name='plagiatAPRE'>Les �tudiants sont pri�s de consulter l'&lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html"> article 12 &lt;/a>&lt;/b> du &lt;i> R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i>intitul� Plagiat et fraude, de prendre connaissance des actes et des gestes qui sont consid�r�s comme �tant du plagiat ou une autre infraction de nature p�dagogique (12.1), de la proc�dure (12.2) et des sanctions, qui peuvent aller jusqu'� la suspension et m�me l'expulsion de l'�cole (12.3).  Toute infraction sera analys�e en fonction des faits et des circonstances, et une sanction sera appliqu�e en cons�quence.</xsl:variable>

<xsl:variable name='plagiatLien'>Les �tudiants sont pri�s de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et de noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au comit� des programmes, qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer...</xsl:variable>


<!-- ========================= -->
<!-- ======= Annuaire ======== -->
<!-- ========================= -->

<xsl:variable name='responsable'>Responsable: </xsl:variable>
<xsl:variable name='programme'>Programme d'�tudes: </xsl:variable>
<xsl:variable name='credit'>Cr�dit(s): </xsl:variable>
<xsl:variable name='exigence'>Exigences: </xsl:variable>
<xsl:variable name='details'>Pour plus de d�tails, cliquez ici.</xsl:variable>
<xsl:variable name='sessionCourante'>Plans de cours du trimestre courant</xsl:variable>
<xsl:variable name='sessionArchive'>Pour la liste de tous les plans de cours, cliquez ici.</xsl:variable>
<xsl:variable name='horaire'>Horaire: </xsl:variable>
<xsl:variable name='horaireLibelle'>Outil de recherche d'horaire</xsl:variable>
<xsl:variable name='horaireLibelle2'>via HEC en ligne</xsl:variable>

<!-- ===== Libell�s =========== -->
<xsl:variable name='programmeAPRE'>Ann�e pr�pa.</xsl:variable>
<xsl:variable name='programmeBAA'>B.A.A.</xsl:variable>
<xsl:variable name='programmeCERT'>Certificats</xsl:variable>
<xsl:variable name='programmeDES'>D.E.S.S.</xsl:variable>
<xsl:variable name='programmeMBA'>MBA</xsl:variable>
<xsl:variable name='programmeMSC'>M. Sc.</xsl:variable>
<xsl:variable name='programmePHD'>Ph. D.</xsl:variable>


<xsl:variable name='serviceBAA'>Dir. B.A.A.</xsl:variable>
<xsl:variable name='serviceCERT'>Dir. Certificats</xsl:variable>
<xsl:variable name='serviceDES'>Dir. D.E.S.</xsl:variable>
<xsl:variable name='serviceMBA'>Dir. MBA</xsl:variable>
<xsl:variable name='serviceMSC'>Dir. M. Sc. et Ph. D.</xsl:variable>
<xsl:variable name='servicePHD'>Dir. M. Sc. et Ph. D.</xsl:variable>

<xsl:variable name='serviceBUR.REGIST'>Bureau du registraire</xsl:variable>
<xsl:variable name='serviceCETAI'>Affaires internationales</xsl:variable>
<xsl:variable name='serviceQUAL.COMM.'>Qualit� de la communication</xsl:variable>

<xsl:variable name='serviceFINANCE'>Finance</xsl:variable>
<xsl:variable name='serviceGOP'>GOL</xsl:variable>
<xsl:variable name='serviceGOL'>GOL</xsl:variable>
<xsl:variable name='serviceGRH'>GRH</xsl:variable>
<xsl:variable name='serviceIEA'>IEA</xsl:variable>
<xsl:variable name='serviceMARKETING'>Marketing</xsl:variable>
<xsl:variable name='serviceMNGT'>Management</xsl:variable>
<xsl:variable name='serviceMQG'>MQG</xsl:variable>
<xsl:variable name='serviceSC.COMPT.'>Sciences comptables</xsl:variable>
<xsl:variable name='serviceTI'>TI</xsl:variable>
<xsl:variable name='serviceINTERNAT'>Affaires internationales</xsl:variable>

<!-- ===== URLs =========== -->
<xsl:variable name='programmeUrlAPRE'>http://www.hec.ca/programmes/baa/prepa</xsl:variable>
<xsl:variable name='programmeUrlBAA'>http://www.hec.ca/programmes/baa</xsl:variable>
<xsl:variable name='programmeUrlCERT'>http://www.hec.ca/programmes/certificats</xsl:variable>
<xsl:variable name='programmeUrlDES'>http://www.hec.ca/programmes/des</xsl:variable>
<xsl:variable name='programmeUrlMBA'>http://www.hec.ca/programmes/mba</xsl:variable>
<xsl:variable name='programmeUrlMSC'>http://www.hec.ca/programmes/msc</xsl:variable>
<xsl:variable name='programmeUrlPHD'>http://www.hec.ca/programmes/phd</xsl:variable>


<xsl:variable name='serviceUrlBAA'>http://www.hec.ca/programmes/baa</xsl:variable>
<xsl:variable name='serviceUrlCERT'>http://www.hec.ca/programmes/certificats</xsl:variable>
<xsl:variable name='serviceUrlDES'>http://www.hec.ca/programmes/des</xsl:variable>
<xsl:variable name='serviceUrlMBA'>http://www.hec.ca/programmes/mba</xsl:variable>
<xsl:variable name='serviceUrlMSC'>http://www.hec.ca/programmes/msc</xsl:variable>
<xsl:variable name='serviceUrlPHD'>http://www.hec.ca/programmes</xsl:variable>

<xsl:variable name='serviceUrlBUR.REGIST'>http://www.hec.ca/registraire</xsl:variable>
<xsl:variable name='serviceUrlCETAI'>http://cetai.hec.ca</xsl:variable>
<xsl:variable name='serviceUrlQUAL.COMM.'>http://www.hec.ca/qualitecomm</xsl:variable>

<xsl:variable name='serviceUrlFINANCE'>http://www.hec.ca/finance</xsl:variable>
<xsl:variable name='serviceUrlGOP'>http://www.hec.ca/gop</xsl:variable>
<xsl:variable name='serviceUrlGOL'>http://www.hec.ca/gop</xsl:variable>
<xsl:variable name='serviceUrlGRH'>http://www.hec.ca/grh</xsl:variable>
<xsl:variable name='serviceUrlIEA'>http://www.hec.ca/iea</xsl:variable>
<xsl:variable name='serviceUrlMARKETING'>http://www.hec.ca/marketing</xsl:variable>
<xsl:variable name='serviceUrlMNGT'>http://www.hec.ca/management</xsl:variable>
<xsl:variable name='serviceUrlMQG'>http://www.hec.ca/mqg</xsl:variable>
<xsl:variable name='serviceUrlSC.COMPT.'>http://www.hec.ca/sco</xsl:variable>
<xsl:variable name='serviceUrlTI'>http://www.hec.ca/ti</xsl:variable>
<xsl:variable name='serviceUrlINTERNAT'>http://www.hec.ca/affint/</xsl:variable>


</xsl:stylesheet>
