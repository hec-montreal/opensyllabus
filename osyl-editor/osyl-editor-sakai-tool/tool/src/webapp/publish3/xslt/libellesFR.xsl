<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Présentation</xsl:variable>
<xsl:variable name='a12Lbl'>Coordonnées</xsl:variable>
<xsl:variable name='a13Lbl'>Matériel pédagogique</xsl:variable>
<xsl:variable name='a14Lbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a15Lbl'>Liste des séances</xsl:variable>
<xsl:variable name='a15LblTP'>Liste des Travaux pratiques</xsl:variable>
<xsl:variable name='a16Lbl'>Documents imprimables</xsl:variable>
<xsl:variable name='a18Lbl'>Liste des exercices</xsl:variable>
<xsl:variable name='a19Lbl'>Liste des références bibliographiques</xsl:variable>
<xsl:variable name='a21Lbl'>Archive des nouvelles</xsl:variable>

<!-- ========================= -->
<!-- ======= Menus =========== -->
<!-- ========================= -->

<xsl:variable name='a11menuLbl'>Présentation</xsl:variable>
<xsl:variable name='a12menuLbl'>Coordonnées</xsl:variable>
<xsl:variable name='a13menuLbl'>Matériel pédagogique</xsl:variable>
<xsl:variable name='a14menuLbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a15menuLbl'>Séances</xsl:variable>
<xsl:variable name='a16menuLbl'>Documents imprimables</xsl:variable>

<xsl:variable name='DiversCoursmenuLbl'>Nouvelles - Cours</xsl:variable>
<xsl:variable name='DiversSeancemenuLbl'>Nouvelles - Séance</xsl:variable>

<xsl:variable name='archiveMenuLbl'>Archive des nouvelles</xsl:variable>

<!-- ========================= -->
<!-- = documents imprimables = -->
<!-- ========================= -->

<xsl:variable name='planCoursImprimable'>Plan de cours imprimable</xsl:variable>
<xsl:variable name='planCoursImprimableSsTitre'>Coordonnées | Présentation | Évaluations | Matériel pédagogique | Séances</xsl:variable>

<xsl:variable name='exercicesImprimable'>Liste des exercices</xsl:variable>
<xsl:variable name='exercicesImprimableSsTitre'>Énoncés | Solutions</xsl:variable>

<xsl:variable name='referencesImprimable'>Liste des lectures</xsl:variable>
<xsl:variable name='referencesImprimableSsTitre'>Références bibliographiques | Lectures</xsl:variable>

<!-- ========================= -->
<!-- ======= Libellés ======== -->
<!-- ========================= -->

<xsl:variable name='seanceLbl'>Séance</xsl:variable>
<xsl:variable name='tpLbl'>TP</xsl:variable>
<xsl:variable name='themeLbl'>Thème</xsl:variable>
<xsl:variable name='seancesLbl'>Séances</xsl:variable>
<xsl:variable name='tpsLbl'>Travaux pratiques</xsl:variable>
<xsl:variable name='enoncesLbl'>Énoncés</xsl:variable>
<xsl:variable name='solutionsLbl'>Solutions</xsl:variable>

<!-- ========================= -->
<!-- ======= Rubriques ======= -->
<!-- ========================= -->

<xsl:variable name='biblioTag'>rubriqueBibliographie</xsl:variable>
<xsl:variable name='biblioLbl'>Références bibliographiques</xsl:variable>

<xsl:variable name='descriptionTag'>rubriqueDescription</xsl:variable>
<xsl:variable name='descriptionLbl'>Description</xsl:variable>

<xsl:variable name='objectifsTag'>rubriqueObjectifs</xsl:variable>
<xsl:variable name='objectifsLbl'>Objectifs</xsl:variable>

<xsl:variable name='evaluationTag'>evaluations</xsl:variable>
<xsl:variable name='evaluationLbl'>Évaluation</xsl:variable>

<xsl:variable name='approcheTag'>rubriqueApprochePedagogique</xsl:variable>
<xsl:variable name='approcheLbl'>Approche pédagogique</xsl:variable>

<xsl:variable name='supportTag'>rubriqueSupportsCours</xsl:variable>
<xsl:variable name='supportLbl'>Ressources utilisées pendant la séance</xsl:variable>

<xsl:variable name='lecturesTag'>rubriqueLectures</xsl:variable>
<xsl:variable name='lecturesLbl'>Lectures</xsl:variable>

<xsl:variable name='exercicesTag'>rubriqueExercice</xsl:variable>
<xsl:variable name='exercicesLbl'>Exercices</xsl:variable>

<xsl:variable name='ressourcesTag'>rubriqueRessourcesGenerales</xsl:variable>
<xsl:variable name='ressourcesLbl'>Ressources générales</xsl:variable>

<xsl:variable name='devoirsTag'>rubriqueDevoirs</xsl:variable>
<xsl:variable name='devoirsLbl'>Devoirs</xsl:variable>

<xsl:variable name='outilsTag'>rubriqueOutils</xsl:variable>
<xsl:variable name='outilsLbl'>Outils</xsl:variable>

<xsl:variable name='anciensexamsTag'>rubriqueAnciensExamens</xsl:variable>
<xsl:variable name='anciensexamsLbl'>Anciens Examens</xsl:variable>

<xsl:variable name='casTag'>rubriqueCas</xsl:variable>
<xsl:variable name='casLbl'>Cas</xsl:variable>

<xsl:variable name='notesFormTag'>rubriqueNotesFormateur</xsl:variable>
<xsl:variable name='notesFormLbl'>Notes de l'enseignant</xsl:variable>

<xsl:variable name='diversTag'>rubriqueDivers</xsl:variable>
<xsl:variable name='diversLbl'>Divers</xsl:variable>

<xsl:variable name='nouvelleTag'>rubriqueNouvelle</xsl:variable>

<xsl:variable name='diversCoursLbl'>Nouvelles du cours</xsl:variable>
<xsl:variable name='diversSeanceLbl'>Nouvelles de la séance</xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'>Bureau : </xsl:variable>
<xsl:variable name='courrielLbl'>Courriel : </xsl:variable>
<xsl:variable name='telLbl'>Téléphone : </xsl:variable>
<xsl:variable name='dispoLbl'>Disponibilités : </xsl:variable>
<xsl:variable name='evalDateLbl'>Date : </xsl:variable>
<xsl:variable name='isbnLbl'>ISBN : </xsl:variable>

<!-- ========================= -->
<!-- ======= Autres ========== -->
<!-- ========================= -->

<xsl:variable name='sujetLbl'>Sujet</xsl:variable>
<xsl:variable name='solutionLbl'>Solution</xsl:variable>
<xsl:variable name='dateMajLbl'>Dernière mise à jour: </xsl:variable>
<xsl:variable name='plagiatLbl'>Plagiat</xsl:variable>

<xsl:variable name='plagiatBAA'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/baa/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/baa/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>
<xsl:variable name='plagiatPHD'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/phd/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/phd/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>
<xsl:variable name='plagiatMSC'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/msc/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/msc/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>
<xsl:variable name='plagiatMBA'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/mba/2000/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/mba/2000/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>
<xsl:variable name='plagiatCERT'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/certificats/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/certificats/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>
<xsl:variable name='plagiatDES'>L'étudiant est prié de noter de lire l'article 12 des règlements des programmes de HEC Montréal sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'à l'exclusion de l'École (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signalée au Comité des programmes qui analysera la situation et les circonstances et décidera de la sanction à appliquer.</xsl:variable>

</xsl:stylesheet>