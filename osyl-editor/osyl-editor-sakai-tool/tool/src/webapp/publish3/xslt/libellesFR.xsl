<?xml version="1.0"  encoding="ISO8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Pr�sentation</xsl:variable>
<xsl:variable name='a12Lbl'>Coordonn�es</xsl:variable>
<xsl:variable name='a13Lbl'>Mat�riel p�dagogique</xsl:variable>
<xsl:variable name='a14Lbl'>Travaux et examens</xsl:variable>
<xsl:variable name='a15Lbl'>Liste des s�ances</xsl:variable>
<xsl:variable name='a15LblTP'>Liste des Travaux pratiques</xsl:variable>
<xsl:variable name='a16Lbl'>Documents imprimables</xsl:variable>
<xsl:variable name='a18Lbl'>Liste des exercices</xsl:variable>
<xsl:variable name='a19Lbl'>Liste des r�f�rences bibliographiques</xsl:variable>
<xsl:variable name='a21Lbl'>Archive des nouvelles</xsl:variable>

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

<xsl:variable name='archiveMenuLbl'>Archive des nouvelles</xsl:variable>

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
<xsl:variable name='tpLbl'>TP</xsl:variable>
<xsl:variable name='themeLbl'>Th�me</xsl:variable>
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
<xsl:variable name='diversSeanceLbl'>Nouvelles de la s�ance</xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'>Bureau : </xsl:variable>
<xsl:variable name='courrielLbl'>Courriel : </xsl:variable>
<xsl:variable name='telLbl'>T�l�phone : </xsl:variable>
<xsl:variable name='dispoLbl'>Disponibilit�s : </xsl:variable>
<xsl:variable name='evalDateLbl'>Date : </xsl:variable>
<xsl:variable name='isbnLbl'>ISBN : </xsl:variable>

<!-- ========================= -->
<!-- ======= Autres ========== -->
<!-- ========================= -->

<xsl:variable name='sujetLbl'>Sujet</xsl:variable>
<xsl:variable name='solutionLbl'>Solution</xsl:variable>
<xsl:variable name='dateMajLbl'>Derni�re mise � jour: </xsl:variable>
<xsl:variable name='plagiatLbl'>Plagiat</xsl:variable>

<xsl:variable name='plagiatBAA'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/baa/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/baa/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>
<xsl:variable name='plagiatPHD'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/phd/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/phd/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>
<xsl:variable name='plagiatMSC'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/msc/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/msc/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>
<xsl:variable name='plagiatMBA'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/mba/2000/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/mba/2000/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>
<xsl:variable name='plagiatCERT'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/certificats/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/certificats/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>
<xsl:variable name='plagiatDES'>L'�tudiant est pri� de noter de lire l'article 12 des r�glements des programmes de HEC Montr�al sur le plagiat et d'y noter les formes multiples que prend le plagiat (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.1&lt;/a>) et les sanctions qui peuvent aller jusqu'� l'exclusion de l'�cole (&lt;a href="http://www.hec.ca/registraire/reglement/dess/12-plagiat-fraude.html">12.2&lt;/a>). Toute infraction sera signal�e au Comit� des programmes qui analysera la situation et les circonstances et d�cidera de la sanction � appliquer.</xsl:variable>

</xsl:stylesheet>