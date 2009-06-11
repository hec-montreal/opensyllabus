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

package org.sakaiquebec.opensyllabus.client.hostedMode;

import java.util.HashMap;
import java.util.Map;

/**
 * CO Messages map for disconnected mode.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylTestCOMessages.java 1750 2008-12-01 22:03:01Z
 *          mathieu.cantin@hec.ca $
 */
public class OsylTestCOMessages {

    private static Map<String, String> messages;

    /**
     * @return the messages map
     */
    public static Map<String, String> getMap() {
	if (messages == null) {
	    messages = new HashMap<String, String>();
	    messages.put("ResProxAssignmentView_DescriptionLabel",
		    "Assignment Instructions");
	    messages.put("ResProxAssignmentView_SakaiAssignmentLinkLabel",
		    "Sakai Assignment Link");

	    messages.put("ResProxContactInfoView_AdjunctProfessor",
		    "Adjunct Professor");
	    messages.put("ResProxContactInfoView_AffiliatedProfessor",
		    "Affiliated Professor");
	    messages.put("ResProxContactInfoView_AssistantProfessor",
		    "Assistant Professor");
	    messages.put("ResProxContactInfoView_AssociateProfessor",
		    "Associate Professor");
	    messages.put("ResProxContactInfoView_AvailabilityLabel",
		    "Availability:");
	    messages.put("ResProxContactInfoView_CommentsLabel", "Comments:");
	    messages.put("ResProxContactInfoView_EMailLabel", "E-mail:");
	    messages
		    .put("ResProxContactInfoView_FirstNameLabel", "First Name:");
	    messages.put("ResProxContactInfoView_FirstNameMandatory",
		    "The First Name field is required.");
	    messages.put("ResProxContactInfoView_Full-timeFacultyLecturer",
		    "Full-time Lecturer");
	    messages.put("ResProxContactInfoView_Full-timeLecturer",
		    "Full-time Faculty Lecturer");
	    messages.put("ResProxContactInfoView_GuestProfessor",
		    "Guest Professor");
	    messages.put("ResProxContactInfoView_HonoraryProfessor",
		    "Honorary Professor");
	    messages.put("ResProxContactInfoView_LastNameLabel", "Last Name:");
	    messages.put("ResProxContactInfoView_LastNameMandatory",
		    "The Last Name field is required.");
	    messages.put("ResProxContactInfoView_OfficeLabel", "Office:");
	    messages.put("ResProxContactInfoView_Part-timeFacultyLecturer",
		    "Part-time Lecturer");
	    messages.put("ResProxContactInfoView_PhoneLabel", "Phone:");
	    messages.put("ResProxContactInfoView_PleaseChoose",
		    "Please Choose a Title");
	    messages.put("ResProxContactInfoView_Professor", "Professor");
	    messages.put("ResProxContactInfoView_RoleLabel", "Role:");
	    messages.put("ResProxContactInfoView_RoleMandatory",
		    "The Role field is required.");
	    messages.put("ResProxContactInfoView_Secretary", "Secretary");
	    messages.put("ResProxContactInfoView_Student", "Student");
	    messages.put("ResProxContactInfoView_TitleLabel", "Title:");
	    messages.put("ResProxContactInfoView_AvailabilityLabel",
		    "Availability:");
	    messages.put("ResProxEvaluationView_RatingLabel", "Rating");
	    messages.put("ResProxEvaluationView_TitleLabel", "Title");
	    messages.put("ResProxEvaluationView_DescriptionLabel", "Description");
		   messages.put("ResProxEvaluationView_DateLabel", "Date");
	    messages.put("assigndescr", "Assignment Description...");
	    messages.put("assignexam", "Assignment and Exam");
	    messages.put("assignexamShort", "Assignments and Exams");
	    messages.put("evaluations",
		    "Assessments Modes");
	    messages.put("evaluationsShort",
	    "Assessments");
	    messages.put("evaluation", "Assessment");
	    messages.put("assignexamsShort", "Assignments and Exams");
	    messages.put("assignment", "Electronic Submission Tool Assignment");
	    messages.put("assignments", "Assessments");
	    messages.put("attribute", "Attribute");
	    messages.put("bibliography", "Bibliography");
	    messages.put("cancelEdit", "Cancel");
	    messages.put("casestudies", "Case Studies");

	    messages.put("citation", "Citation");
	    messages.put("ResProxCitationView_typeLabel", "Type");
	    messages.put("ResProxCitationView_citationLabel", "Citation");
	    messages.put("ResProxCitationView_titleLabel", "Title");
	    messages.put("ResProxCitationView_authorLabel", "Author");
	    messages.put("ResProxCitationView_isbnIssnLabel", "ISBN/ISSN");
	    messages.put("ResProxCitationView_isbnLabel", "ISBN");
	    messages.put("ResProxCitationView_issnLabel", "ISSN");
	    messages.put("ResProxCitationView_linkLabel", "Link");
	    messages.put("ResProxCitationView_yearLabel", "Year");
	    messages.put("ResProxCitationView_dateLabel", "Date");
	    messages.put("ResProxCitationView_journalLabel", "Journal");
	    messages.put("ResProxCitationView_volumeLabel", "Volume");
	    messages.put("ResProxCitationView_issueLabel", "Issue");
	    messages.put("ResProxCitationView_pagesLabel", "Pages");
	    messages.put("ResProxCitationView_startpageLabel", "Start Page");
	    messages.put("ResProxCitationView_endpageLabel", "End Page");
	    messages.put("ResProxCitationView_doiLabel", "DOI");
	    messages.put("bibliographicReference", "Insert your Citation Here ...");
	    messages.put("Citation.author", "Unspecified");
	    messages.put("Citation.type.unknown", "Others");
	    messages.put("Citation.type.book", "Book");
	    messages.put("Citation.type.article", "Article");
	    
	    messages.put("complbibres",
		    "Complementary Bibliographical Resources");
	    messages
		    .put("contactinfo",
			    "Contact Information for all Staff Assigned to this Course");
	    messages.put("contactinfoShort", "Contact Information");
	    messages.put("contactinforesource", "Contact Information");
	    messages.put("coordinators", "Coordinator");
	    // CourseOutline
	    messages.put("courseoutline", "Course Outline");
	    messages.put("dayseminar", "Day Seminar");
	    messages.put("description", "Description");
	    // COContentResourceProxyType
	    messages.put("document", "Document");
	    messages.put("url", "Hyperlink");
	    messages.put("element", "Element");
	    messages.put("evaluationShort", "Assessment");
	    messages.put("evaluations", "Assessment Modes");
	    messages.put("exams", "Exams");
	    messages.put("exam", "Exam");
	    messages.put("oralpresentation", "Oral Presentation");
	    messages.put("participation", "Participation");
	    messages.put("otherevaluations", "Other Assessments");
	    messages.put("exercises", "Exercises");
	    messages.put("ehomework", "Homework with Electronic Submission");
	    messages.put("homework", "Homework without Electronic Submission");
	    // COContentRubricTypeList
	    messages.put("internaldocument", "Internal Document");
	    messages.put("labsession", "Lab Session");
	    messages.put("learningmat",
		    "Learning Material used throughout the Course");
	    messages.put("learningmatShort", "Learning Material");
	    messages.put("learningstrat", "Learning Strategy");
	    messages.put("instructions", "Instructions");
	    messages.put("evalpreparation", "Preparation to Assessment");
	    messages.put("evalcriteria", " Assessment Criteria");
	    messages.put("evaldetails", " Assessment Details");
	    messages.put("evalsubproc", "Submission Procedures and Penalities");
	    messages.put("plagiarism", "Notes on Plagiarism");
	    
	    messages.put("Evaluation.Location.home","At Home");
	    messages.put("Evaluation.Location.inclass","In Class");
	    messages.put("Evaluation.Mode.ind","Individual");
	    messages.put("Evaluation.Mode.team","Team");
	    messages.put("Evaluation.Scope.Fac","Facultative");
	    messages.put("Evaluation.Scope.Obl","Compulsory");
	    messages.put("Evaluation.Subtype.elect","Electronic");
	    messages.put("Evaluation.Subtype.oral","Oral");
	    messages.put("Evaluation.Subtype.paper","Paper");
	    messages.put("Evaluation.Type.case_study", "Case Study");
	    messages.put("Evaluation.Type.practice_assignement", "Practice Assignment");
	    messages.put("Evaluation.Type.session_work", "Session Work");
	    messages.put("Evaluation.Type.intra_exam", "Intra Exam");
	    messages.put("Evaluation.Type.final_exam", "Final Exam");
	    messages.put("Evaluation.Type.quiz", "Quiz");
	    messages.put("Evaluation.Type.homework", "Homework");
	    messages.put("Evaluation.Type.participation", "Participation");
	    messages.put("Evaluation.Type.other", "Other");
	    
	    // COContentUnitType
	    messages.put("lecture", "Lecture");
	    messages.put("lecturers", "Lecturer(s)");
	    // COStructureElementType
	    messages.put("lectures", "Lectures");
	    messages.put("mis", "Miscellaneous");
	    messages.put("misresources", "Miscellaneous Resources");
	    messages.put("name", "Name");
	    messages.put("news", "News");
	    messages.put("objectives", "Objectives");
	    messages.put("pastexams", "Past Exams");
	    messages.put("plagiat", "Plagiarism");
	    messages.put("presentation", "General Introduction to the Course");
	    messages.put("presentationShort", "Course Introduction");
	    messages.put("qas", "Questions - Answers");
	    messages.put("quiz", "Quiz");
	    messages.put("readinglist", "Reading List");
	    messages.put("ressinclass", "Resources used in Class");
	    messages.put("restrictionpattern", "Restriction Pattern");
	    messages.put("secretaries", "Secretary(ies)");
	    messages.put("session", "Session");
	    messages.put("teachingassistants", "Teaching Assistant(s)");
	    messages.put("teachnote", "Teacher's Note");
	    messages.put("text", "Text");
	    messages.put("theme", "Theme");
	    messages.put("tools", "Tools");
	    messages.put("topics", "Topics");
	    // OsylConfigRuler
	    messages.put("undefined", "Undefined");
	    messages.put("validateEdit", "Save");
	    messages.put("weeklesson", "Week Lesson");
	    // Default values
	    messages.put("InsertYourTextHere", "Insert your Text Here...");
	    messages.put("SendWork", "Click here to submit your work");
	    messages.put("UndefinedDocument", "Document not yet Selected");
	    messages.put("UndefinedCitation", "Citation not yet Selected");
	}
	return messages;
    }
}
