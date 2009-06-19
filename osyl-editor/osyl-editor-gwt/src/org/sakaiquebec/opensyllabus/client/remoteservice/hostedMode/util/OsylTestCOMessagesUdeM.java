/**********************************************************************************
 * $Id: OsylTestCOMessages.java 1866 2009-02-02 14:52:22Z laurent.danet@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util;

import java.util.HashMap;
import java.util.Map;

/**
 * CO Messages map for disconnected mode.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepr�tre</a>
 * @version $Id: OsylTestCOMessages.java 1750 2008-12-01 22:03:01Z
 *          mathieu.cantin@hec.ca $
 */
public class OsylTestCOMessagesUdeM {

    private static Map<String, String> messages;

    /**
     * @return the messages map
     */
    public static Map<String, String> getMap() {
	
	if (messages == null) {
	    messages = new HashMap<String, String>();
	    messages.put("Citation.author", "Non sp�cifi�");
	    messages.put("Citation.type.article", "Article");
	    messages.put("Citation.type.book", "Livre");
	    messages.put("Citation.type.chapter", "Section / chapitre de livre");
	    messages.put("Citation.type.report", "Rapport");
	    messages.put("Citation.type.unknown", "Inconnu");
	    
	    messages.put("ResProxAssignmentView_DescriptionLabel",
		    "Directives");
	    messages.put("ResProxAssignmentView_SakaiAssignmentLinkLabel",
		    "Lien vers l'outil Sakai");
	    messages.put("ResProxCitationView_authorLabel",
		    "Auteur");
	    messages.put("ResProxCitationView_citationLabel",
		    "R�f�rence bibliographique");
	    messages.put("ResProxCitationView_dateLabel",
		    "Date");
	    messages.put("ResProxCitationView_doiLabel",
		    "DOI");
	    messages.put("ResProxCitationView_endpageLabel",
		    "Page fin");
	    messages.put("ResProxCitationView_isbnIssnLabel",
		    "ISBN/ISSN");
	    messages.put("ResProxCitationView_isbnLabel",
		    "ISBN");
	    messages.put("ResProxCitationView_issnLabel",
		    "ISSN");
	    messages.put("ResProxCitationView_journalLabel",
		    "P�riodique");
	    messages.put("ResProxCitationView_linkLabel",
		    "Lien");
	    messages.put("ResProxCitationView_numberLabel",
		    "Num�ro");
	    messages.put("ResProxCitationView_startpageLabel",
		    "Page d�but");
	    messages.put("ResProxCitationView_titleLabel", "Titre");
	    messages.put("ResProxCitationView_typeLabel", "Type");
	    messages.put("ResProxCitationView_volumeLabel", "Volume");
	    messages.put("ResProxCitationView_yearLabel", "Ann�e");    
	    
	    messages.put("ResProxContactInfoView_AdjunctProfessor",
		    "Professeur associ�");
	    messages.put("ResProxContactInfoView_AffiliatedProfessor",
		    "Professeur affili�");
	    messages.put("ResProxContactInfoView_AssistantProfessor",
		    "Professeur adjoint");
	    messages.put("ResProxContactInfoView_AssociateProfessor",
		    "Professeur agr�g�");
	    messages.put("ResProxContactInfoView_AvailabilityLabel",
		    "Disponibilit�:");
	    messages.put("availability","");
	    messages.put("ResProxContactInfoView_CommentsLabel", "Commentaires:");
	    messages.put("comments","");
	    messages.put("ResProxContactInfoView_EMailLabel", "Courriel:");
	    messages.put("email","");
	    messages.put("ResProxContactInfoView_FirstNameLabel", "Pr�nom:");
	    messages.put("firstname","");
	    messages.put("ResProxContactInfoView_FirstNameMandatory",
		    "La coordonn�e pr�nom ne peut pas �tre vide");
	    messages.put("ResProxContactInfoView_Full-timeFacultyLecturer",
		    "Charg� de formation");
	    messages.put("ResProxContactInfoView_Full-timeLecturer",
		    "Attach� d'enseignement");
	    messages.put("ResProxContactInfoView_Full-timeLecturer_UdeM",
		    "Auxiliaire d’enseignement");
	    messages.put("ResProxContactInfoView_GuestProfessor",
		    "Professeur invit�");
	    messages.put("ResProxContactInfoView_HonoraryProfessor",
		    "Professeur honoraire");
	    messages.put("ResProxContactInfoView_LastNameLabel", "Nom:");
	    messages.put("lastname","");
	    messages.put("ResProxContactInfoView_LastNameMandatory",
		    "La coordonn�e nom de famille ne peut pas �tre vide");
	    messages.put("ResProxContactInfoView_OfficeLabel", "Bureau:");
	    messages.put("office","");
	    messages.put("ResProxContactInfoView_Part-timeFacultyLecturer",
		    "Charg� de formation");
	    messages.put("ResProxContactInfoView_PhoneLabel", "T�l�phone:");
	    messages.put("phone","");
	    messages.put("ResProxContactInfoView_PleaseChoose",
		    "<Veuillez choisir un r�le>");
	    messages.put("ResProxContactInfoView_Professor", "Professeur titulaire");
	    messages.put("ResProxContactInfoView_Professor_UdeM", "Professeur(e)");
	    messages.put("ResProxContactInfoView_RoleLabel", "R�le:");
	    messages.put("role","");
	    messages.put("ResProxContactInfoView_RoleMandatory",
		    "La coordonn�e r�le ne peut pas �tre vide");
	    messages.put("ResProxContactInfoView_Secretary", "Secr�taire");
	    messages.put("secretarie","Secr�taire");
	    messages.put("ResProxContactInfoView_Student", "�tudiant");
	    messages.put("ResProxContactInfoView_TitleLabel", "Titre:");
	    messages.put("coursecoordinator", "Responsable du cours");
	    
	    messages.put("ResProxEvaluationView_DescriptionLabel", "Description");
	    messages.put("ResProxEvaluationView_DateLabel", "Date");
	    messages.put("ResProxEvaluationView_RatingLabel", "Pourcentage");
	    messages.put("ResProxEvaluationView_TitleLabel", "Titre");

	    messages.put("assesmentdescription", "Description des �valuations");
	    messages.put("assigndescr", "Description du devoir ou de l'examen");
	    messages.put("assignexam", "�valuation");
	    messages.put("assignexams", "�valuations pr�vues pour le cours");
	    messages.put("assignexamShort", "�valuations");
	    
	    messages.put("assignment", "Outil de remise �lectronique Assignment");
	    messages.put("assignments", "�valuations");
	    
	    messages.put("attribute", "Attribut");
	    
	    messages.put("cancelEdit", "Annuler");
	    messages.put("casestudies", "�tudes de cas");
	    
	    messages.put("citation", "R�f�rence bibliographique");
	    messages.put("Citation.author", "Unspecified");
	    messages.put("Citation.type.unknown", "Unknown");
	    messages.put("Citation.type.book", "Book");
	    messages.put("Citation.type.chapter", "Book Section");
	    messages.put("Citation.type.article", "Article");
	    messages.put("Citation.type.report", "Report");
	    
	    messages.put("complbibres","Ressources bibliographiques compl�mentaires");
	    messages.put("contactinfo","Coordonn�es de l'ensemble du personnel affect� � ce cours");
	    messages.put("contactinfoShort", "Coordonn�es des intervenants");
	    messages.put("contactinforesource", "Coordonn�e");
	    messages.put("coordinators", "Coordonateur");
	    
	    messages.put("courseident", "Identificateur du cours");
	    messages.put("courseidentShort", "Identificateur du cours");
	    
	    messages.put("coursetitle", "Titre du cours");
	    messages.put("coursetitleShort", "Titre");
	    messages.put("courseabrev", "Sigle du cours");
	    messages.put("coursecredits", "Nombre de cr�dits");
	    
	    messages.put("coursedescription", "Description du cours");
	    messages.put("coursedescriptionShort", "Description du cours");
	    messages.put("courseofficialdescription", "Descripteur officiel du cours (annuaire)");
	    messages.put("courseofficialdescriptionShort", "Descripteur officiel");
	    messages.put("courseplace", "Lieu o� se donne le cours");
	    
	    messages.put("courseimage", "Image");
	    messages.put("CourseInfo", "Informations g�n�rales sur le cours");
	    messages.put("CourseInfoShort", "Informations sur le cours");
	    messages.put("CourseInCurriculum", "Place du cours dans le programme");
	    messages.put("courseoutline", "Hiver 2008 - SOL 2104");

	    messages.put("coursesession", "Trimestre");
	    
	    messages.put("courseintro", "Pr�sentation / Introduction du cours");
	    messages.put("courseintroShort", "Introduction");

	    messages.put("coursescheduleplace", "Horaire et lieux du cours");
	    messages.put("coursescheduleplaceShort", "Horaire et lieux");
	    messages.put("coursebeginend", "D�but et fin du cours");
	    messages.put("courseregularschedule", "Horaires r�guliers");
	    messages.put("coursespecialevents", "�v�nements sp�ciaux");
	    
	    messages.put("CourseObjectives","Objectifs du cours");
	    messages.put("CourseObjectivesShort","Objectifs");
	    messages.put("generalobjectives", "Objectifs g�n�raux");
	    messages.put("specificobjectives", "Objectifs sp�cifiques");
	    
	    messages.put("CourseSkills","�l�ments de comp�tences vis�s par le cours");
	    messages.put("CourseSkillsShort","Comp�tences");
	    messages.put("CourseSkillElement", "�l�ments de comp�tence");
	    messages.put("CourseSkillsLearningSet", "Apprentissages vis�s");
	   
	    messages.put("dayseminar", "S�minaire d'un jour");
	    messages.put("description", "Description");

	    messages.put("document", "Document");
	    messages.put("ehomework", "Travail avec remise �lectronique");
	    messages.put("element", "�lement");
	    
	    messages.put("exams", "Examens");
	    messages.put("exam", "Examen");
	    messages.put("exercises", "Exercises");

	    messages.put("facprogident", "Identification de la facult� et du programme");
	    messages.put("facprogidentShort", "Facult� et programme");

	    messages.put("homework", "Travail sans remise �lectronique");
	   	    
	    messages.put("instructions", "Consignes");

	    messages.put("internaldocument", "Document interne");
	    	    
	    messages.put("labsession", "Laboratoire");
	    	    
	    messages.put("learningstrat", "Strat�gie p�dagogique");
	    
	    messages.put("evalpreparation", "Pr�paration � l'�valuation");
	    messages.put("evalcriteria", "Crit�res d'�valuation");
	    messages.put("evaldetails", "D�tails sur l'�valuation");
	    messages.put("evalsubproc", "Proc�dures de remise et p�nalit�s");
	    messages.put("SpecializedResource", "Ressource sp�cialis�e");
	    	    	    	    
	    messages.put("evaluations","Modalit�s d'�valuation sommative");
	    messages.put("evaluationsShort","�valuation");
	    messages.put("EvaluationsList","Liste des �valuations");
	    messages.put("EvaluationsListShort","�valuations");
	    	    
	    messages.put("EvaluationStruc", "�valuation");

	    messages.put("evaluation", "�valuation");
	    messages.put("ObjectivesMeasured","Objectifs �valu�es");
	    messages.put("SkillsMeasured","Comp�tences �valu�es");
	    messages.put("EvaluationInstrumentsList", "Instruments d'�valuation");
	    messages.put("EvaluationInstrument", "Instrument d'�valuation");
	    
	    messages.put("Evaluation.Location.home","� la maison");
	    messages.put("Evaluation.Location.inclass","En classe");
	    messages.put("Evaluation.Mode.ind","Individuel");
	    messages.put("Evaluation.Mode.team","En �quipe");
//	    messages.put("Evaluation.Scope.Fac","Facultatif");
//	    messages.put("Evaluation.Scope.Obl","Obligatoire");
	    messages.put("Evaluation.Scope.Fac","Formatif");
	    messages.put("Evaluation.Scope.Obl","Sommatif");
	    messages.put("Evaluation.Subtype.elect","�lectronique");
	    messages.put("Evaluation.Subtype.oral","Oral");
	    messages.put("Evaluation.Subtype.paper","Papier");
	    messages.put("Evaluation.Type.case_study", "�tude de cas");
	    messages.put("Evaluation.Type.elaboratedwrittenanswer", "Questions � d�veloppement �labor�");
	    messages.put("Evaluation.Type.multiplechoice", "Questions � choix de r�ponses");
	    messages.put("Evaluation.Type.intra_exam", "Examen de mi-session");
	    messages.put("Evaluation.Type.final_exam", "Examen final");
	    messages.put("Evaluation.Type.homework", "Devoir");
	    messages.put("Evaluation.Type.other", "Autre");
	    messages.put("Evaluation.Type.participation", "Participation");
	    messages.put("Evaluation.Type.practice_assignement", "Travaux pratiques");
	    messages.put("Evaluation.Type.quiz", "Quiz");
	    messages.put("Evaluation.Type.session_work", "Travail de session");
	    messages.put("Evaluation.Type.shortwrittenanswer", "Questions � d�veloppement court");
	    
	    messages.put("plagiat", "Plagiat");
	    messages.put("plagiarism", "Notes sur le plagiat");
	    messages.put("plagiarismShort", "Plagiat");

	    // COContentUnitType
	    messages.put("lecture", "S�ance");
	    messages.put("LectureContextSkills","Rencontre");
	    
	    messages.put("lecturers", "Enseignant(s)");
	    // COStructureElementType
	    messages.put("lectures", "Pr�sentation des activit�s d’enseignement par s�ance");
	    messages.put("lecturesShort", "Liste des s�ances");

	    messages.put("LecturesListContextSkills", "Pr�sentation des activit�s d’enseignement par rencontre");
	    messages.put("LecturesListContextSkillsShort", "Liste des rencontres");

	    messages.put("mis", "Divers");
	    messages.put("misresources", "Ressources g�n�rales");
	    messages.put("name", "Nom");
	    messages.put("news", "Nouvelles");
	    messages.put("objectives", "Objectifs");
	    messages.put("observation", "Observation");	    
	    
	    messages.put("oralpresentation", "Pr�sentation orale");
	    messages.put("otherevaluations", "Autre �valuation");

	    messages.put("participation", "Participation en classe");
	    	    	    
	    messages.put("presentation", "Pr�sentation g�n�rale du cours");
	    messages.put("presentationShort", "Pr�sentation du cours");
	    
	    messages.put("coursetopicspresentation","Pr�sentation des th�mes"); 
	    messages.put("courseactivitiespresentation","Pr�sentation des activit�s");
	    messages.put("coursemodulespresentation","Pr�sentation des modules");
	    messages.put("coursecontentspresentation","Pr�sentation du contenu"); 
	    messages.put("tableofcontents", "Table des mati�res");
	    messages.put("problematic", "Probl�matique");
	    
	    messages.put("PedagogicalApproachStruc", "Approche p�dagogique g�n�rale");
	    messages.put("PedagogicalApproachStrucShort", "Approche p�dagogique");
	    messages.put("PedagogicalApproach", "Approche p�dagogique");
	    messages.put("PedagogicalApproachShort", "Approche p�dagogique");
	    messages.put("PedagogicalApproacSpecificStruc", "Approche p�dagogique sp�cifique");
	    messages.put("PedagogicalApproachSpecific","Approche p�dagogique sp�cifique");
	    messages.put("pedagogicaltheory", "Orientation p�dagogique");
	    messages.put("teachingstrategy", "Modalit�s d'enseignement");
	    messages.put("learningstrategy", "Modalit�s d'apprentissage");
	    messages.put("modusoperandi", "Modalit�s / r�gles de fonctionnement");

	    messages.put("learningmat","Ressources p�dagogiques utilis�es durant tout le cours");
	    messages.put("learningmatShort", "Ressources p�dagogiques");
	    messages.put("requiredbooks","Livre(s) obligatoire(s)");
	    messages.put("recommendedbooks","Livre(s) recommand�(s)");
	    messages.put("codex","Codex / recueil de textes");	
	    messages.put("hardwaresoftware","Mat�riels et logiciels informatiques");
            // Old exams homework (with or without solutions)
	    messages.put("oldexams", "Anciens examens et travaux");
	    
	    messages.put("qas", "Questions - R�ponses");
	    messages.put("quiz", "Quiz");
	    messages.put("readinglist", "Liste de lectures");
	    messages.put("ressinclass", "Ressources utilis�es");
	    messages.put("restrictionpattern", "R�gle de restriction");
	    messages.put("secretaries", "Secretaire(s)");
	    messages.put("session", "Trimestre");
	    	    
	    messages.put("teachingassistants", "Stagiaire(s) d'enseignement");
	    messages.put("teachnote", "Note de l'enseignant");
	    messages.put("text", "Texte");
	    messages.put("theme", "Th�me");
	    messages.put("tools", "Outils");
	    messages.put("topics", "Sujets");
	    // OsylConfigRuler
	    messages.put("undefined", "Non d�fini");
	    messages.put("validateEdit", "Enregistrer");
	    messages.put("weeklesson", "Le�on hebdomadaire");
	    // Default values
	    messages.put("UndefinedCitation", "Citation pas encore s�lectionn�e");
	    messages.put("UndefinedDocument", "Document pas encore s�lectionn�");
	    messages.put("InsertYourTextHere", "...Tapez votre texte ici...");
	    messages.put("url", "Hyperlien");
	    
	    messages.put("LectureDate", "Date");
	    messages.put("LecturePlace", "Lieu");
	    messages.put("LectureObjectives", "Objectifs de la s�ance");
	    messages.put("LectureRequiredMaterial", "Mat�riel requis");
	    
	    messages.put("LectureStruc", "S�ance");
	    messages.put("LectureStruc1", "S�ance 01");
	    messages.put("LectureStruc2", "S�ance 02");
	    messages.put("LectureStruc3", "S�ance 03");
	    messages.put("LectureStruc4", "S�ance 04");
	    messages.put("LectureStruc5", "S�ance 05");
	    messages.put("LectureStruc6", "S�ance 06");
	    messages.put("LectureStruc7", "S�ance 07");
	    messages.put("LectureStruc8", "S�ance 08");
	    messages.put("LectureStruc9", "S�ance 09");
	    messages.put("LectureStruc10", "S�ance 10");
	    messages.put("LectureStruc11", "S�ance 11");
	    messages.put("LectureStruc12", "S�ance 12");	    
	    messages.put("LectureTeachingActivityList", "Activit�s d'enseignement");
	    messages.put("LectureTeachingActivity", "Activit� d'enseignement");
	    messages.put("LectureLearningActivityList", "Activit�s d'apprentissage");
	    messages.put("LectureLearningActivity", "Activit� d'apprentissage");

	    messages.put("Theme","Th�me");
	    messages.put("ThemeDate", "Date de pr�sentation du th�me");
	    messages.put("ThemePlace", "Lieu de pr�sentation du th�me");
	    messages.put("ThemeGeneralObjectives", "Objectifs g�n�raux");
	    messages.put("ThemeSpecificObjectives", "Objectifs sp�cifiques");
	    messages.put("ThemeRequiredMaterial", "Mat�riel requis");

	    messages.put("ThemesList", "Pr�sentation des activit�s d’enseignement par th�me");
	    messages.put("ThemesListShort", "Liste des th�mes");
	    messages.put("ThemeStruc", "Th�me");
	    messages.put("ThemeStruc1", "Th�me 1");
	    messages.put("ThemeStruc2", "Th�me 2");
	    messages.put("ThemeStruc3", "Th�me 3");
	    messages.put("ThemeStruc4", "Th�me 4");
	    messages.put("ThemeStruc5", "Th�me 5");
	    messages.put("ThemeStruc6", "Th�me 6");
	    messages.put("ThemeStruc7", "Th�me 7");
	    messages.put("ThemeStruc8", "Th�me 8");
	    messages.put("ThemeStruc9", "Th�me 9");
	    messages.put("ThemeStruc10", "Th�me 10");
	    messages.put("ThemeStruc11", "Th�me 11");
	    messages.put("ThemeStruc12", "Th�me 12");	    
	    messages.put("ThemeTeachingActivityList", "Activit�s d'enseignement");
	    messages.put("ThemeTeachingActivity", "Activit� d'enseignement");
	    messages.put("ThemeLearningActivityList", "Activit�s d'apprentissage");
	    messages.put("ThemeLearningActivity", "Activit� d'apprentissage");

	    messages.put("Bibliography", "Liste des r�f�rences bibliographiques");
	    messages.put("BibliographyShort", "Bibliographie");
	    
	    messages.put("bibliography", "R�f�rence bibliographique");
	    messages.put("bibliographicReference","Ins�rer votre r�f�rence bibliographique ici ...");

	    messages.put("ThematicBibliography", "Biblioth�que disciplinaire du cours / biblioth�que de la discipline");
	    messages.put("ThematicBibliographyShort", "Biblioth�que disciplinaire");
	    
	    messages.put("LearningSupportServices","Services de soutien � l'apprentissage");
	    messages.put("LearningSupportServicesShort","Services de soutien");
	    messages.put("LearningSupportSAE","Service aux �tudiants (SAE)");
	    messages.put("LearningSupportCCE","Centre de communication �crite (CCE)");
	    messages.put("LearningSupportOther","Autre service de soutien");
	    
	}
	return messages;
    }
}
