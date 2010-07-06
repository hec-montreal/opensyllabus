<?xml version="1.0"  encoding="ISO-8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Introduction</xsl:variable>
<xsl:variable name='a12Lbl'>Contact information</xsl:variable>
<xsl:variable name='a13Lbl'>Learning material</xsl:variable>
<xsl:variable name='a14Lbl'>Assignments and examinations</xsl:variable>
<xsl:variable name='a14Lbla'>Evaluation</xsl:variable>
<xsl:variable name='a15Lbl'>List of sessions</xsl:variable>
<xsl:variable name='a15LblTP'>List of assignments</xsl:variable>
<xsl:variable name='a15LblTheme'>List of themes</xsl:variable>
<xsl:variable name='a16Lbl'>Printable documents</xsl:variable>
<xsl:variable name='a18Lbl'>List of exercises</xsl:variable>
<xsl:variable name='a19Lbl'>Bibliography</xsl:variable>
<xsl:variable name='a21Lbl'>News archives</xsl:variable>
<xsl:variable name='a22Lbl'>FAQ</xsl:variable>
<xsl:variable name='a22Lbla'>Foire aux questions</xsl:variable>

<!-- ========================= -->
<!-- ======= Menus =========== -->
<!-- ========================= -->

<xsl:variable name='a11menuLbl'>Introduction</xsl:variable>
<xsl:variable name='a12menuLbl'> Contact information</xsl:variable>
<xsl:variable name='a13menuLbl'> Learning material</xsl:variable>
<xsl:variable name='a14menuLbl'> Assignments and examinations</xsl:variable>
<xsl:variable name='a14menuLbla'>Evaluation</xsl:variable>
<xsl:variable name='a15menuLbl'>Sessions</xsl:variable>
<xsl:variable name='a22menuLbl'>FAQ</xsl:variable>
<xsl:variable name='a16menuLbl'> Printable documents</xsl:variable>

<xsl:variable name='DiversCoursmenuLbl'>News - Course</xsl:variable>
<!-- Mise a jour: 15-avril-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite -->
<xsl:variable name='plagiatCalculatriceLabel_version15Avril'>
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Plagiarism &lt;/b&gt;: Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt; article 12 &lt;/a&gt;&lt;/b&gt; of the &lt;i&gt;Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.
		&lt;/div &gt;
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Calculator &lt;/b&gt;: Please consult the  &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank"&gt; calculator usage policy &lt;/a&gt;during exams when applicable.
	&lt;/div &gt;
		&lt;br&gt;
</xsl:variable>

<!-- Mise a jour: 9-juillet-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite, sous forme hyperlien -->
<xsl:variable name='plagiatCalculatriceLabel'>
		&lt;br&gt;
		Regulations
		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank" &gt; Plagiarism and fraud &lt;/a&gt;  

		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/en/plagiarism/exams/calculatrice.html" target="_blank" &gt; Calculators &lt;/a&gt;  
		&lt;br&gt;
		
		<xsl:call-template name="separateur"/>
</xsl:variable>

<!-- Mise a jour: 15-avril-2008 apr Van: Message d'usage des calculatrices
	sont toujours affiches tout de suite apres le message Plagiat a gauche -->
<xsl:variable name='usageCalculatriceLabel'>
	&lt;b&gt; Calculator &lt;/b&gt;
	&lt;br&gt; Please consult the  &lt;a href="http://www.hec.ca/en/plagiarism/exams/calculatrice.html" target="_blank"&gt; calculator usage policy &lt;/a&gt;during exams when applicable.
	&lt;br&gt;
</xsl:variable> 

<xsl:variable name='DiversSeancemenuLbl'>News - Session</xsl:variable>

<xsl:variable name='archiveMenuLbl'>News archives</xsl:variable>

<!-- ========================= -->
<!-- = documents imprimables = -->
<!-- ========================= -->

<xsl:variable name='planCoursImprimable'>Printable course outline</xsl:variable>
<xsl:variable name='planCoursImprimableSsTitre'>Contact information | Introduction | Evaluation | Learning material | Sessions</xsl:variable>

<xsl:variable name='exercicesImprimable'>List of exercises</xsl:variable>
<xsl:variable name='exercicesImprimableSsTitre'>Questions | Solutions</xsl:variable>

<xsl:variable name='referencesImprimable'>List of references</xsl:variable>
<xsl:variable name='referencesImprimableSsTitre'>Bibliography | Reading list</xsl:variable>

<!-- ========================= -->
<!-- ======= Libellés ======== -->
<!-- ========================= -->

<xsl:variable name='seanceLbl'>Session</xsl:variable>
<xsl:variable name='tpLbl'>Assignment</xsl:variable>
<xsl:variable name='themeLbl'>Theme</xsl:variable>
<xsl:variable name='themeTitreLbl'>Title</xsl:variable>
<xsl:variable name='seancesLbl'>Sessions</xsl:variable>
<xsl:variable name='tpsLbl'>Assignments </xsl:variable>
<xsl:variable name='enoncesLbl'>Questions</xsl:variable>
<xsl:variable name='solutionsLbl'>Solutions</xsl:variable>

<!-- ========================= -->
<!-- ======= Rubriques ======= -->
<!-- ========================= -->

<xsl:variable name='biblioTag'>rubriqueBibliographie</xsl:variable>
<xsl:variable name='biblioLbl'>Bibliography</xsl:variable>

<xsl:variable name='descriptionTag'>rubriqueDescription</xsl:variable>
<xsl:variable name='descriptionLbl'>Description</xsl:variable>

<xsl:variable name='objectifsTag'>rubriqueObjectifs</xsl:variable>
<xsl:variable name='objectifsLbl'>Objectives</xsl:variable>

<xsl:variable name='evaluationTag'>evaluations</xsl:variable>
<xsl:variable name='evaluationLbl'>Evaluation</xsl:variable>

<xsl:variable name='approcheTag'>rubriqueApprochePedagogique</xsl:variable>
<xsl:variable name='approcheLbl'>Academic approach</xsl:variable>

<xsl:variable name='supportTag'>rubriqueSupportsCours</xsl:variable>
<xsl:variable name='supportLbl'>Resources used in class</xsl:variable>

<xsl:variable name='lecturesTag'>rubriqueLectures</xsl:variable>
<xsl:variable name='lecturesLbl'>Reading list</xsl:variable>

<xsl:variable name='exercicesTag'>rubriqueExercice</xsl:variable>
<xsl:variable name='exercicesLbl'>Exercises</xsl:variable>

<xsl:variable name='ressourcesTag'>rubriqueRessourcesGenerales</xsl:variable>
<xsl:variable name='ressourcesLbl'>Miscellaneous resources</xsl:variable>

<xsl:variable name='ressourcesComplementairesTag'>rubriqueRessourcesComplementaires</xsl:variable>
<xsl:variable name='ressourcesComplementairesLbl'>Complementary resources</xsl:variable>

<xsl:variable name='devoirsTag'>rubriqueDevoirs</xsl:variable>
<xsl:variable name='devoirsLbl'>Assignments</xsl:variable>

<xsl:variable name='outilsTag'>rubriqueOutils</xsl:variable>
<xsl:variable name='outilsLbl'>Tools</xsl:variable>

<xsl:variable name='anciensexamsTag'>rubriqueAnciensExamens</xsl:variable>
<xsl:variable name='anciensexamsLbl'>Past examinations</xsl:variable>

<xsl:variable name='casTag'>rubriqueCas</xsl:variable>
<xsl:variable name='casLbl'>Case studies</xsl:variable>

<xsl:variable name='notesFormTag'>rubriqueNotesFormateur</xsl:variable>
<xsl:variable name='notesFormLbl'>Teacher's note</xsl:variable>

<xsl:variable name='diversTag'>rubriqueDivers</xsl:variable>
<xsl:variable name='diversLbl'>Miscellaneous</xsl:variable>

<xsl:variable name='nouvelleTag'>rubriqueNouvelle</xsl:variable>

<xsl:variable name='diversCoursLbl'>News - course</xsl:variable>
<xsl:variable name='diversSeanceLbl'>News - this class </xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'>Office: </xsl:variable>
<xsl:variable name='courrielLbl'>Email: </xsl:variable>
<xsl:variable name='telLbl'>Telephone : </xsl:variable>
<xsl:variable name='dispoLbl'>Availability: </xsl:variable>
<xsl:variable name='evalDateLbl'>Date: </xsl:variable>
<xsl:variable name='isbnLbl'>ISBN: </xsl:variable>

<!-- ========================= -->
<!-- ======= Autres ========== -->
<!-- ========================= -->

<xsl:variable name='sujetLbl'>Subject</xsl:variable>
<xsl:variable name='solutionLbl'>Solution</xsl:variable>
<xsl:variable name='dateMajLbl'>Last updated: </xsl:variable>
<xsl:variable name='plagiatLbl'>Plagiarism</xsl:variable>
<xsl:variable name='calculatriceLbl'>Calculator</xsl:variable>

<!-- Afichage incorrect: rajouter le guillemet manquant - Awa 2008-03-12 -->
<xsl:variable name='plagiatBAA'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt; article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatPHD'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatMSC'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatMBA'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatCERT'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatDES'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>
<xsl:variable name='plagiatAPRE'>Please consult &lt;b>&lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank"&gt;article 12 &lt;/a>&lt;/b> of the &lt;i>Règlement régissant l'activité étudiante à HEC Montréal &lt;/i> (regulation governing student activities at HEC Montréal), to find out the acts and gestures that are considered plagiarism or another academic violation (12.1), along with the applicable procedure (12.2) and sanctions, which range up to suspension and even expulsion from HEC (12.3).  Violations are analyzed based on the facts and circumstances, and sanctions are applied accordingly.</xsl:variable>

<!-- ========================= -->
<!-- ======= Annuaire ======== -->
<!-- ========================= -->

<xsl:variable name='responsable'>Coordinator: </xsl:variable>
<xsl:variable name='programme'>Program: </xsl:variable>
<xsl:variable name='credit'>Credit(s): </xsl:variable>
<xsl:variable name='exigence'>Requirements: </xsl:variable>
<xsl:variable name='details'>Click here for more details</xsl:variable>
<xsl:variable name='sessionCourante'>Course outline for this session</xsl:variable>
<xsl:variable name='sessionArchive'>Click here for all course outlines (for this course only)</xsl:variable>
<xsl:variable name='horaire'>Schedule: </xsl:variable>
<xsl:variable name='horaireLibelle'>Schedule search tool</xsl:variable>
<xsl:variable name='horaireLibelle2'>via HEC en ligne</xsl:variable>

<!-- ===== Libellés =========== -->
<xsl:variable name='programmeAPRE'>Année prépa.</xsl:variable>
<xsl:variable name='programmeBAA'>B.B.A.</xsl:variable>
<xsl:variable name='programmeCERT'>Certificates</xsl:variable>
<xsl:variable name='programmeDES'>Graduate Diploma</xsl:variable>
<xsl:variable name='programmeMBA'>MBA</xsl:variable>
<xsl:variable name='programmeMSC'>M.Sc.</xsl:variable>
<xsl:variable name='programmePHD'>Ph.D.</xsl:variable>


<xsl:variable name='serviceBAA'>B.B.A. dept.</xsl:variable>
<xsl:variable name='serviceCERT'>Certificate dept.</xsl:variable>
<xsl:variable name='serviceDES'>Grad. Dipl. dept.</xsl:variable>
<xsl:variable name='serviceMBA'>MBA dept.</xsl:variable>
<xsl:variable name='serviceMSC'>M.Sc. and Ph.D. dept.</xsl:variable>
<xsl:variable name='servicePHD'>M.Sc. and Ph.D. dept.</xsl:variable>

<xsl:variable name='serviceBUR.REGIST'>Office of the Registrar</xsl:variable>
<xsl:variable name='serviceCETAI'>Department of International Business</xsl:variable>
<xsl:variable name='serviceQUAL.COMM.'>Quality of Communication Centre</xsl:variable>

<xsl:variable name='serviceFINANCE'>Department of Finance</xsl:variable>
<xsl:variable name='serviceGOP'>Department of Logistics and Operation Management</xsl:variable>
<xsl:variable name='serviceGOL'>Department of Logistics and Operation Management</xsl:variable>
<xsl:variable name='serviceGRH'>Department of Human Resources Management</xsl:variable>
<xsl:variable name='serviceIEA'>Institute of Applied Economics</xsl:variable>
<xsl:variable name='serviceMARKETING'>Department of Marketing</xsl:variable>
<xsl:variable name='serviceMNGT'>Department of Management</xsl:variable>
<xsl:variable name='serviceMQG'>Department of Management Sciences</xsl:variable>
<xsl:variable name='serviceSC.COMPT.'>Department of Accounting Studies</xsl:variable>
<xsl:variable name='serviceTI'>Department of Information Technologies</xsl:variable>
<xsl:variable name='serviceINTERNAT'>Department of International Business</xsl:variable>

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
<xsl:variable name='serviceUrlPHD'>http://www.hec.ca/programmes/phd</xsl:variable>

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
