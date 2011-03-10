<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Presentation</xsl:variable>
<xsl:variable name='a12Lbl'>Staff</xsl:variable>
<xsl:variable name='a13Lbl'>Mat�riel p�dagogique</xsl:variable>
<xsl:variable name='a14Lbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a15Lbl'>Liste des s�ances</xsl:variable>
<xsl:variable name='a16Lbl'>Documents imprimables</xsl:variable>
<xsl:variable name='a18Lbl'>Liste des exercices</xsl:variable>
<xsl:variable name='a19Lbl'>Liste des r�f�rences bibliographiques</xsl:variable>

<!-- ========================= -->
<!-- ======= Menus =========== -->
<!-- ========================= -->

<xsl:variable name='a11menuLbl'>Pr�sentation</xsl:variable>
<xsl:variable name='a12menuLbl'>Coordonn�es</xsl:variable>
<xsl:variable name='a13menuLbl'>Mat�riel p�dagogique</xsl:variable>
<xsl:variable name='a14menuLbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a15menuLbl'>S�ances</xsl:variable>
<xsl:variable name='a16menuLbl'>Documents imprimables</xsl:variable>

<xsl:variable name='DiversCoursmenuLbl'>Nouvelles - Cours</xsl:variable>
<xsl:variable name='DiversSeancemenuLbl'>Nouvelles - S�ance</xsl:variable>

<!-- ========================= -->
<!-- = documents imprimables = -->
<!-- ========================= -->

<xsl:variable name='planCoursImprimable'>Plan de cours imprimable</xsl:variable>
<xsl:variable name='planCoursImprimableSsTitre'>Coordonn�es | Pr�sentation | �valuations | Mat�riel p�dagogique | S�ances</xsl:variable>

<xsl:variable name='exercicesImprimable'>Liste des exercices</xsl:variable>
<xsl:variable name='exercicesImprimableSsTitre'>�nonc�s | Solutions</xsl:variable>

<xsl:variable name='referencesImprimable'>Liste des lectures</xsl:variable>
<xsl:variable name='referencesImprimableSsTitre'>R�f�rences bibliographiques | Lectures</xsl:variable>

<!-- ========================= -->
<!-- ======= Libell�s ======== -->
<!-- ========================= -->

<xsl:variable name='seanceLbl'>S�ance</xsl:variable>
<xsl:variable name='themeLbl'>Th�me</xsl:variable>
<xsl:variable name='seancesLbl'>S�ances</xsl:variable>
<xsl:variable name='enoncesLbl'>�nonc�s</xsl:variable>
<xsl:variable name='solutionsLbl'>Solutions</xsl:variable>

<!-- ========================= -->
<!-- ======= Rubriques ======= -->
<!-- ========================= -->

<xsl:variable name='biblioTag'>rubriqueBibliographie</xsl:variable>
<xsl:variable name='biblioLbl'>Bibliographie</xsl:variable>

<xsl:variable name='descriptionTag'>rubriqueDescription</xsl:variable>
<xsl:variable name='descriptionLbl'>Description</xsl:variable>

<xsl:variable name='objectifsTag'>rubriqueObjectifs</xsl:variable>
<xsl:variable name='objectifsLbl'>Objectifs</xsl:variable>

<xsl:variable name='approcheTag'>rubriqueApprochePedagogique</xsl:variable>
<xsl:variable name='approcheLbl'>Approche p�dagogique</xsl:variable>

<xsl:variable name='supportTag'>rubriqueSupportsCours</xsl:variable>
<xsl:variable name='supportLbl'>Supports de cours</xsl:variable>

<xsl:variable name='lecturesTag'>rubriqueLectures</xsl:variable>
<xsl:variable name='lecturesLbl'>Lectures</xsl:variable>

<xsl:variable name='exercicesTag'>rubriqueExercice</xsl:variable>
<xsl:variable name='exercicesLbl'>Exercices</xsl:variable>

<xsl:variable name='ressourcesTag'>rubriqueRessourcesGenerales</xsl:variable>
<xsl:variable name='ressourcesLbl'>Ressources</xsl:variable>

<xsl:variable name='devoirsTag'>rubriqueDevoirs</xsl:variable>
<xsl:variable name='devoirsLbl'>Devoirs</xsl:variable>

<xsl:variable name='outilsTag'>rubriqueOutils</xsl:variable>
<xsl:variable name='outilsLbl'>Outils</xsl:variable>

<xsl:variable name='anciensexamsTag'>rubriqueAnciensExamens</xsl:variable>
<xsl:variable name='anciensexamsLbl'>Anciens Examens</xsl:variable>

<xsl:variable name='casTag'>rubriqueCas</xsl:variable>
<xsl:variable name='casLbl'>Cas</xsl:variable>

<xsl:variable name='notesFormTag'>rubriqueFormateur</xsl:variable>
<xsl:variable name='notesFormLbl'>Notes de l'enseignant</xsl:variable>

<xsl:variable name='diversTag'>rubriqueDivers</xsl:variable>
<xsl:variable name='diversLbl'>Nouvelles</xsl:variable>

<xsl:variable name='actResBeforeTag'>rubriqueActivitesRessourcesAvantSeance</xsl:variable>
<xsl:variable name='actResBeforeLbl'>Activities/Resources before session</xsl:variable>

<xsl:variable name='actResDuringTag'>rubriqueActivitesRessourcesPendantSeance</xsl:variable>
<xsl:variable name='actResDuringLbl'>Activities/Resources during session</xsl:variable>

<xsl:variable name='actResAfterTag'>rubriqueActivitesRessourcesApresSeance</xsl:variable>
<xsl:variable name='actResAfterLbl'>Activities/Resources after session</xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'>Bureau : </xsl:variable>
<xsl:variable name='courrielLbl'>Courriel : </xsl:variable>
<xsl:variable name='telLbl'>T�l�phone : </xsl:variable>
<xsl:variable name='dispoLbl'>Disponibilit�s : </xsl:variable>
<xsl:variable name='evalDateLbl'>Date : </xsl:variable>
<xsl:variable name='isbnLbl'>ISBN : </xsl:variable>



</xsl:stylesheet>