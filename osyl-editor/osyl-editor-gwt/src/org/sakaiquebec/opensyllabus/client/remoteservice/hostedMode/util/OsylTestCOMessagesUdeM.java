/**********************************************************************************
 * $Id: OsylTestCOMessages.java 1866 2009-02-02 14:52:22Z laurent.danet@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
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
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
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
	    messages.put("Citation.author", "Non spécifié");
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
		    "Référence bibliographique");
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
		    "Périodique");
	    messages.put("ResProxCitationView_linkLabel",
		    "Lien");
	    messages.put("ResProxCitationView_numberLabel",
		    "Numéro");
	    messages.put("ResProxCitationView_startpageLabel",
		    "Page début");
	    messages.put("ResProxCitationView_titleLabel", "Titre");
	    messages.put("ResProxCitationView_typeLabel", "Type");
	    messages.put("ResProxCitationView_volumeLabel", "Volume");
	    messages.put("ResProxCitationView_yearLabel", "Année");    
	    
	    messages.put("ResProxContactInfoView_AdjunctProfessor",
		    "Professeur associé");
	    messages.put("ResProxContactInfoView_AffiliatedProfessor",
		    "Professeur affilié");
	    messages.put("ResProxContactInfoView_AssistantProfessor",
		    "Professeur adjoint");
	    messages.put("ResProxContactInfoView_AssociateProfessor",
		    "Professeur agrégé");
	    messages.put("ResProxContactInfoView_AvailabilityLabel",
		    "Disponibilité:");
	    messages.put("availability","");
	    messages.put("ResProxContactInfoView_CommentsLabel", "Commentaires:");
	    messages.put("comments","");
	    messages.put("ResProxContactInfoView_EMailLabel", "Courriel:");
	    messages.put("email","");
	    messages.put("ResProxContactInfoView_FirstNameLabel", "Prénom:");
	    messages.put("firstname","");
	    messages.put("ResProxContactInfoView_FirstNameMandatory",
		    "La coordonnée prénom ne peut pas être vide");
	    messages.put("ResProxContactInfoView_Full-timeFacultyLecturer",
		    "Chargé de formation");
	    messages.put("ResProxContactInfoView_Full-timeLecturer",
		    "Attaché d'enseignement");
	    messages.put("ResProxContactInfoView_Full-timeLecturer_UdeM",
		    "Auxiliaire dâ€™enseignement");
	    messages.put("ResProxContactInfoView_GuestProfessor",
		    "Professeur invité");
	    messages.put("ResProxContactInfoView_HonoraryProfessor",
		    "Professeur honoraire");
	    messages.put("ResProxContactInfoView_LastNameLabel", "Nom:");
	    messages.put("lastname","");
	    messages.put("ResProxContactInfoView_LastNameMandatory",
		    "La coordonnée nom de famille ne peut pas êªtre vide");
	    messages.put("ResProxContactInfoView_OfficeLabel", "Bureau:");
	    messages.put("office","");
	    messages.put("ResProxContactInfoView_Part-timeFacultyLecturer",
		    "Chargé de formation");
	    messages.put("ResProxContactInfoView_PhoneLabel", "Téléphone:");
	    messages.put("phone","");
	    messages.put("ResProxContactInfoView_PleaseChoose",
		    "<Veuillez choisir un rôle>");
	    messages.put("ResProxContactInfoView_Professor", "Professeur titulaire");
	    messages.put("ResProxContactInfoView_Professor_UdeM", "Professeur(e)");
	    messages.put("ResProxContactInfoView_RoleLabel", "Rôle:");
	    messages.put("role","");
	    messages.put("ResProxContactInfoView_RoleMandatory",
		    "La coordonnée rôle ne peut pas être vide");
	    messages.put("ResProxContactInfoView_Secretary", "Secrétaire");
	    messages.put("secretarie","Secrétaire");
	    messages.put("ResProxContactInfoView_Student", "étudiant");
	    messages.put("ResProxContactInfoView_TitleLabel", "Titre:");
	    messages.put("coursecoordinator", "Responsable du cours");
	    
	    messages.put("ResProxEvaluationView_DescriptionLabel", "Description");
	    messages.put("ResProxEvaluationView_DateLabel", "Date");
	    messages.put("ResProxEvaluationView_RatingLabel", "Pourcentage");
	    messages.put("ResProxEvaluationView_TitleLabel", "Titre");

	    messages.put("assesmentdescription", "Description des évaluations");
	    messages.put("assigndescr", "Description du devoir ou de l'examen");
	    messages.put("assignexam", "évaluation");
	    messages.put("assignexams", "évaluations prévues pour le cours");
	    messages.put("assignexamShort", "évaluations");
	    
	    messages.put("assignment", "Outil de remise électronique Assignment");
	    messages.put("assignments", "évaluations");
	    
	    messages.put("attribute", "Attribut");
	    
	    messages.put("cancelEdit", "Annuler");
	    messages.put("casestudies", "études de cas");
	    
	    messages.put("citation", "Référence bibliographique");
	    messages.put("Citation.author", "Unspecified");
	    messages.put("Citation.type.unknown", "Unknown");
	    messages.put("Citation.type.book", "Book");
	    messages.put("Citation.type.chapter", "Book Section");
	    messages.put("Citation.type.article", "Article");
	    messages.put("Citation.type.report", "Report");
	    
	    messages.put("complbibres","Ressources bibliographiques complémentaires");
	    messages.put("contactinfo","Coordonnées de l'ensemble du personnel affecté à ce cours");
	    messages.put("contactinfoShort", "Coordonnées des intervenants");
	    messages.put("contactinforesource", "Coordonnée");
	    messages.put("coordinators", "Coordonateur");
	    
	    messages.put("courseident", "Identificateur du cours");
	    messages.put("courseidentShort", "Identificateur du cours");
	    
	    messages.put("coursetitle", "Titre du cours");
	    messages.put("coursetitleShort", "Titre");
	    messages.put("courseabrev", "Sigle du cours");
	    messages.put("coursecredits", "Nombre de crédits");
	    
	    messages.put("coursedescription", "Description du cours");
	    messages.put("coursedescriptionShort", "Description du cours");
	    messages.put("courseofficialdescription", "Descripteur officiel du cours (annuaire)");
	    messages.put("courseofficialdescriptionShort", "Descripteur officiel");
	    messages.put("courseplace", "Lieu où se donne le cours");
	    
	    messages.put("courseimage", "Image");
	    messages.put("CourseInfo", "Informations générales sur le cours");
	    messages.put("CourseInfoShort", "Informations sur le cours");
	    messages.put("CourseInCurriculum", "Place du cours dans le programme");
	    messages.put("courseoutline", "Hiver 2008 - SOL 2104");

	    messages.put("coursesession", "Trimestre");
	    
	    messages.put("courseintro", "Présentation / Introduction du cours");
	    messages.put("courseintroShort", "Introduction");

	    messages.put("coursescheduleplace", "Horaire et lieux du cours");
	    messages.put("coursescheduleplaceShort", "Horaire et lieux");
	    messages.put("coursebeginend", "Début et fin du cours");
	    messages.put("courseregularschedule", "Horaires réguliers");
	    messages.put("coursespecialevents", "évènements spéciaux");
	    
	    messages.put("CourseObjectives","Objectifs du cours");
	    messages.put("CourseObjectivesShort","Objectifs");
	    messages.put("generalobjectives", "Objectifs généraux");
	    messages.put("specificobjectives", "Objectifs spécifiques");
	    
	    messages.put("CourseSkills","éléments de compétences visés par le cours");
	    messages.put("CourseSkillsShort","Compétences");
	    messages.put("CourseSkillElement", "éléments de compétence");
	    messages.put("CourseSkillsLearningSet", "Apprentissages visés");
	   
	    messages.put("dayseminar", "Séminaire d'un jour");
	    messages.put("description", "Description");

	    messages.put("document", "Document");
	    messages.put("ehomework", "Travail avec remise électronique");
	    messages.put("element", "élement");
	    
	    messages.put("exams", "Examens");
	    messages.put("exam", "Examen");
	    messages.put("exercises", "Exercises");

	    messages.put("facprogident", "Identification de la faculté et du programme");
	    messages.put("facprogidentShort", "Faculté et programme");

	    messages.put("homework", "Travail sans remise électronique");
	   	    
	    messages.put("instructions", "Consignes");

	    messages.put("internaldocument", "Document interne");
	    	    
	    messages.put("labsession", "Laboratoire");
	    	    
	    messages.put("learningstrat", "Stratégie pédagogique");
	    
	    messages.put("evalpreparation", "Préparation à l'évaluation");
	    messages.put("evalcriteria", "Critères d'évaluation");
	    messages.put("evaldetails", "Détails sur l'évaluation");
	    messages.put("evalsubproc", "Procédures de remise et pénalités");
	    messages.put("SpecializedResource", "Ressource spécialisée");
	    	    	    	    
	    messages.put("evaluations","Modalités d'évaluation sommative");
	    messages.put("evaluationsShort","évaluation");
	    messages.put("EvaluationsList","Liste des évaluations");
	    messages.put("EvaluationsListShort","évaluations");
	    	    
	    messages.put("EvaluationStruc", "évaluation");

	    messages.put("evaluation", "évaluation");
	    messages.put("ObjectivesMeasured","Objectifs évaluées");
	    messages.put("SkillsMeasured","Compétences évaluées");
	    messages.put("EvaluationInstrumentsList", "Instruments d'évaluation");
	    messages.put("EvaluationInstrument", "Instrument d'évaluation");
	    
	    messages.put("Evaluation.Location.home","à la maison");
	    messages.put("Evaluation.Location.inclass","En classe");
	    messages.put("Evaluation.Mode.ind","Individuel");
	    messages.put("Evaluation.Mode.team","En équipe");
//	    messages.put("Evaluation.Scope.Fac","Facultatif");
//	    messages.put("Evaluation.Scope.Obl","Obligatoire");
	    messages.put("Evaluation.Scope.Fac","Formatif");
	    messages.put("Evaluation.Scope.Obl","Sommatif");
	    messages.put("Evaluation.Subtype.elect","électronique");
	    messages.put("Evaluation.Subtype.oral","Oral");
	    messages.put("Evaluation.Subtype.paper","Papier");
	    messages.put("Evaluation.Type.case_study", "étude de cas");
	    messages.put("Evaluation.Type.elaboratedwrittenanswer", "Questions à développement élaboré");
	    messages.put("Evaluation.Type.multiplechoice", "Questions à choix de réponses");
	    messages.put("Evaluation.Type.intra_exam", "Examen de mi-session");
	    messages.put("Evaluation.Type.final_exam", "Examen final");
	    messages.put("Evaluation.Type.homework", "Devoir");
	    messages.put("Evaluation.Type.other", "Autre");
	    messages.put("Evaluation.Type.participation", "Participation");
	    messages.put("Evaluation.Type.practice_assignement", "Travaux pratiques");
	    messages.put("Evaluation.Type.quiz", "Quiz");
	    messages.put("Evaluation.Type.session_work", "Travail de session");
	    messages.put("Evaluation.Type.shortwrittenanswer", "Questions à développement court");
	    
	    messages.put("plagiat", "Plagiat");
	    messages.put("plagiarism", "Notes sur le plagiat");
	    messages.put("plagiarismShort", "Plagiat");

	    // COContentUnitType
	    messages.put("lecture", "Séance");
	    messages.put("LectureContextSkills","Rencontre");
	    
	    messages.put("lecturers", "Enseignant(s)");
	    // COStructureElementType
	    messages.put("lectures", "Présentation des activités dâ€™enseignement par séance");
	    messages.put("lecturesShort", "Liste des séances");

	    messages.put("LecturesListContextSkills", "Présentation des activités dâ€™enseignement par rencontre");
	    messages.put("LecturesListContextSkillsShort", "Liste des rencontres");

	    messages.put("mis", "Divers");
	    messages.put("misresources", "Ressources générales");
	    messages.put("name", "Nom");
	    messages.put("news", "Nouvelles");
	    messages.put("objectives", "Objectifs");
	    messages.put("observation", "Observation");	    
	    
	    messages.put("oralpresentation", "Présentation orale");
	    messages.put("otherevaluations", "Autre évaluation");

	    messages.put("participation", "Participation en classe");
	    	    	    
	    messages.put("presentation", "Présentation générale du cours");
	    messages.put("presentationShort", "Présentation du cours");
	    
	    messages.put("coursetopicspresentation","Présentation des thèmes"); 
	    messages.put("courseactivitiespresentation","Présentation des activités");
	    messages.put("coursemodulespresentation","Présentation des modules");
	    messages.put("coursecontentspresentation","Présentation du contenu"); 
	    messages.put("tableofcontents", "Table des matières");
	    messages.put("problematic", "Problématique");
	    
	    messages.put("PedagogicalApproachStruc", "Approche pédagogique générale");
	    messages.put("PedagogicalApproachStrucShort", "Approche pédagogique");
	    messages.put("PedagogicalApproach", "Approche pédagogique");
	    messages.put("PedagogicalApproachShort", "Approche pédagogique");
	    messages.put("PedagogicalApproacSpecificStruc", "Approche pédagogique spécifique");
	    messages.put("PedagogicalApproachSpecific","Approche pédagogique spécifique");
	    messages.put("pedagogicaltheory", "Orientation pédagogique");
	    messages.put("teachingstrategy", "Modalités d'enseignement");
	    messages.put("learningstrategy", "Modalités d'apprentissage");
	    messages.put("modusoperandi", "Modalités / règles de fonctionnement");

	    messages.put("learningmat","Ressources pédagogiques utilisées durant tout le cours");
	    messages.put("learningmatShort", "Ressources pédagogiques");
	    messages.put("requiredbooks","Livre(s) obligatoire(s)");
	    messages.put("recommendedbooks","Livre(s) recommandé(s)");
	    messages.put("codex","Codex / recueil de textes");	
	    messages.put("hardwaresoftware","Matériels et logiciels informatiques");
            // Old exams homework (with or without solutions)
	    messages.put("oldexams", "Anciens examens et travaux");
	    
	    messages.put("qas", "Questions - Réponses");
	    messages.put("quiz", "Quiz");
	    messages.put("readinglist", "Liste de lectures");
	    messages.put("ressinclass", "Ressources utilisées");
	    messages.put("restrictionpattern", "Règle de restriction");
	    messages.put("secretaries", "Secretaire(s)");
	    messages.put("session", "Trimestre");
	    	    
	    messages.put("teachingassistants", "Stagiaire(s) d'enseignement");
	    messages.put("teachnote", "Note de l'enseignant");
	    messages.put("text", "Texte");
	    messages.put("theme", "Thème");
	    messages.put("tools", "Outils");
	    messages.put("topics", "Sujets");
	    // OsylConfigRuler
	    messages.put("undefined", "Non défini");
	    messages.put("validateEdit", "Enregistrer");
	    messages.put("weeklesson", "Leçon hebdomadaire");
	    // Default values
	    messages.put("UndefinedCitation", "Citation pas encore sélectionnée");
	    messages.put("UndefinedDocument", "Document pas encore sélectionné");
	    messages.put("InsertYourTextHere", "...Tapez votre texte ici...");
	    messages.put("url", "Hyperlien");
	    
	    messages.put("LectureDate", "Date");
	    messages.put("LecturePlace", "Lieu");
	    messages.put("LectureObjectives", "Objectifs de la séance");
	    messages.put("LectureRequiredMaterial", "Matériel requis");
	    
	    messages.put("LectureStruc", "Séance");
	    messages.put("LectureStruc1", "Séance 01");
	    messages.put("LectureStruc2", "Séance 02");
	    messages.put("LectureStruc3", "Séance 03");
	    messages.put("LectureStruc4", "Séance 04");
	    messages.put("LectureStruc5", "Séance 05");
	    messages.put("LectureStruc6", "Séance 06");
	    messages.put("LectureStruc7", "Séance 07");
	    messages.put("LectureStruc8", "Séance 08");
	    messages.put("LectureStruc9", "Séance 09");
	    messages.put("LectureStruc10", "Séance 10");
	    messages.put("LectureStruc11", "Séance 11");
	    messages.put("LectureStruc12", "Séance 12");	    
	    messages.put("LectureTeachingActivityList", "Activités d'enseignement");
	    messages.put("LectureTeachingActivity", "Activité d'enseignement");
	    messages.put("LectureLearningActivityList", "Activités d'apprentissage");
	    messages.put("LectureLearningActivity", "Activité d'apprentissage");

	    messages.put("Theme","Thème");
	    messages.put("ThemeDate", "Date de présentation du thème");
	    messages.put("ThemePlace", "Lieu de présentation du thème");
	    messages.put("ThemeGeneralObjectives", "Objectifs généraux");
	    messages.put("ThemeSpecificObjectives", "Objectifs spécifiques");
	    messages.put("ThemeRequiredMaterial", "Matériel requis");

	    messages.put("ThemesList", "Présentation des activités dâ€™enseignement par thème");
	    messages.put("ThemesListShort", "Liste des thèmes");
	    messages.put("ThemeStruc", "Thème");
	    messages.put("ThemeStruc1", "Thème 1");
	    messages.put("ThemeStruc2", "Thème 2");
	    messages.put("ThemeStruc3", "Thème 3");
	    messages.put("ThemeStruc4", "Thème 4");
	    messages.put("ThemeStruc5", "Thème 5");
	    messages.put("ThemeStruc6", "Thème 6");
	    messages.put("ThemeStruc7", "Thème 7");
	    messages.put("ThemeStruc8", "Thème 8");
	    messages.put("ThemeStruc9", "Thème 9");
	    messages.put("ThemeStruc10", "Thème 10");
	    messages.put("ThemeStruc11", "Thème 11");
	    messages.put("ThemeStruc12", "Thème 12");	    
	    messages.put("ThemeTeachingActivityList", "Activités d'enseignement");
	    messages.put("ThemeTeachingActivity", "Activité d'enseignement");
	    messages.put("ThemeLearningActivityList", "Activités d'apprentissage");
	    messages.put("ThemeLearningActivity", "Activité d'apprentissage");

	    messages.put("Bibliography", "Liste des références bibliographiques");
	    messages.put("BibliographyShort", "Bibliographie");
	    
	    messages.put("bibliography", "Référence bibliographique");
	    messages.put("bibliographicReference","Insérer votre référence bibliographique ici ...");

	    messages.put("ThematicBibliography", "Bibliothèque disciplinaire du cours / bibliothèque de la discipline");
	    messages.put("ThematicBibliographyShort", "Bibliothèque disciplinaire");
	    
	    messages.put("LearningSupportServices","Services de soutien à l'apprentissage");
	    messages.put("LearningSupportServicesShort","Services de soutien");
	    messages.put("LearningSupportSAE","Service aux étudiants (SAE)");
	    messages.put("LearningSupportCCE","Centre de communication écrite (CCE)");
	    messages.put("LearningSupportOther","Autre service de soutien");
	    
	}
	return messages;
    }
}
