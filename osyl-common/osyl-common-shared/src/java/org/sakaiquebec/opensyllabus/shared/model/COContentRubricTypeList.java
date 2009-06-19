/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

/**
 * This class contains the list of all the possible rubrics in a course outline.
 * TODO: when GWT 1.5 release will be used, replace this class with enum types
 * 
 * @author <a href="mailto:sacha.Lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentRubricTypeList {

    public static final String UNDEFINED = "undefined";
    public static final String HOMEWORKS = "homeworks";
    public static final String SESSION = "session";
    public static final String QUIZ = "quiz";
    public static final String QAS = "qas";
    public static final String TOOLS = "tools";
    public static final String READINGLIST = "readinglist";
    
    public static final String EVALUATIONS = "evaluations";
    public static final String EVALUATION = "evaluation";

    public static final String NEWS = "news";
    public static final String LEARNINGSTRAT = "learningstrat";
    public static final String INSTRUCTIONS = "instructions";
    public static final String TEACHNOTE = "teachnote";
    public static final String EXERCISES = "exercises";
    public static final String EXAMS = "exams";
    public static final String ASSIGNMENTS = "assignments";
    public static final String CASESTUDIES = "casestudies";
    public static final String RESSINCLASS = "ressinclass";
    public static final String MISRESOURCES = "misresources";
    public static final String BIBLIOGRAPHY = "bibliography";
    public static final String PASTEXAMS = "pastexams";
    public static final String OBJECTIVES = "objectives";
    public static final String DESCRIPTION = "description";
    public static final String PLAGIAT = "plagiat";
    public static final String CONTACTINFO = "contactinfo";
    public static final String COORDINATORS = "coordinators";
    public static final String LECTURERS = "lecturers";
    public static final String TEACHINGASSISTANTS = "teachingassistants";
    public static final String SECRETARIES = "secretaries";
    
    public static final String COURSECOORDINATOR = "coursecoordinator";

    public static final String OBJECTIVES_MEASURED = "ObjectivesMeasured";
    public static final String SKILLS_MEASURED = "SkillsMeasured";
    public static final String EVALUATION_INSTRUMENT = "EvaluationInstrument";
    
    public static final String EVALCRITERIA = "evalcriteria";
    public static final String EVALPREPARATION = "evalpreparation";
    public static final String EVALDETAILS = "evaldetails";
    public static final String EVALSUBPROC = "evalsubproc";
    public static final String PLAGIARISM = "plagiarism";
    public static final String COMPLBIBRES = "complbibres";
    public static final String SPECIALIZED_RESOURCE = "SpecializedResource";
        
    public static final String COURSEABREV = "courseabrev";
    public static final String COURSETITLE = "coursetitle";
    public static final String COURSECREDITS = "coursecredits";
    
    public static final String MISCELLANEOUS = "mis";

    public static final String COURSESESSION = "coursesession";
    
    public static final String COURSEBEGINEND = "coursebeginend";
    public static final String COURSEREGULARSCHEDULE = "courseregularschedule";
    public static final String COURSESPECIALEVENTS = "coursespecialevents";

    public static final String COURSEPLACE = "courseplace";
    public static final String COURSEIMAGE = "courseimage";
    
    public static final String COURSETOPICSPRESENTATION = "coursetopicspresentation";
    public static final String COURSEACTIVITIESPRESENTATION = "courseactivitiespresentation";
    public static final String COURSEMODULESPRESENTATION = "coursemodulespresentation";
    public static final String COURSECONTENTSPRESENTATION = "coursecontentspresentation";
    public static final String TABLEOFCONTENTS = "tableofcontents";
    public static final String PROBLEMATIC = "problematic";
    
    public static final String PEDAGOGICALTHEORY = "pedagogicaltheory";
    public static final String GENERALOBJECTIVES = "generalobjectives";
    public static final String SPECIFICOBJECTIVES = "specificobjectives";
    public static final String TEACHINGSTRATEGY = "teachingstrategy";
    public static final String LEARNINGSTRATEGY = "learningstrategy";
    public static final String MODUSOPERANDI = "modusoperandi";

    public static final String REQUIREDBOOKS = "requiredbooks";
    public static final String RECOMMENDEDBOOKS = "recommendedbooks";
    public static final String CODEX = "codex";
    public static final String HARDWARESOFTWARE = "hardwaresoftware";
    // Old examens and homeworks (with or without solutions)
    public static final String OLDEXAMS = "oldexams";
    
    public static final String LECTURE_DATE = "LectureDate";
    public static final String LECTURE_PLACE = "LecturePlace";
    public static final String LECTURE_OBJECTIVES = "LectureObjectives";
    public static final String LECTURE_REQUIRED_MATERIAL = "LectureRequiredMaterial";    
    public static final String LECTURE_TEACHING_ACTIVITY = "LectureTeachingActivity";    
    public static final String LECTURE_LEARNING_ACTIVITY = "LectureLearningActivity";    
    
    
    public static final String THEME_DATE = "ThemeDate";
    public static final String THEME_PLACE = "ThemePlace";
    public static final String THEME_GENERAL_OBJECTIVES = "ThemeGeneralObjectives";
    public static final String THEME_SPECIFIC_OBJECTIVES = "ThemeSpecificObjectives";
    public static final String THEME_REQUIRED_MATERIAL = "ThemeRequiredMaterial";    
    public static final String THEME_TEACHING_ACTIVITY = "ThemeTeachingActivity";    
    public static final String THEME_LEARNING_ACTIVITY = "ThemeLearningActivity";    

    public static final String COURSE_SKILL_ELEMENT = "CourseSkillElement";
    public static final String COURSE_SKILLS_LEARNING_SET = "CourseSkillsLearningSet";
    
    public static final String LEARNING_SUPPORT_SAE = "LearningSupportSAE";
    public static final String LEARNING_SUPPORT_CCE = "LearningSupportCCE";
    public static final String LEARNING_SUPPORT_OTHER = "LearningSupportOther";

   /**
     * The list of types.
     */
    private static final String[] rubricTypeList =
	    { 
		  UNDEFINED
		, INSTRUCTIONS
		, OBJECTIVES
		, BIBLIOGRAPHY
		, MISRESOURCES
		, RESSINCLASS
		, LEARNINGSTRAT
		, CASESTUDIES
		, ASSIGNMENTS
		, NEWS
		, EVALUATIONS
		, EXAMS
		, EXERCISES
		, READINGLIST
		, TEACHNOTE
		, TOOLS
		, QAS
		, QUIZ
		, SESSION
		, HOMEWORKS
		, CASESTUDIES
		, PLAGIAT
		, CONTACTINFO
		, COURSECOORDINATOR
		, COORDINATORS
		, LECTURERS
		, TEACHINGASSISTANTS
		, SECRETARIES
		, EVALCRITERIA
		, EVALPREPARATION
		, EVALDETAILS
		, EVALSUBPROC
		, MISCELLANEOUS
		, PLAGIARISM
		, COMPLBIBRES
		, SPECIALIZED_RESOURCE
		
		, COURSEABREV
		, COURSETITLE
		, COURSECREDITS
		
		, COURSESESSION
 		, COURSEBEGINEND
 		, COURSEREGULARSCHEDULE
 		, COURSESPECIALEVENTS
 		, COURSEPLACE
 		, COURSEIMAGE
 		
 		, COURSETOPICSPRESENTATION
 		, COURSEACTIVITIESPRESENTATION
 		, COURSEMODULESPRESENTATION
 		, COURSEMODULESPRESENTATION
 		, COURSECONTENTSPRESENTATION
 		, TABLEOFCONTENTS
 		, PROBLEMATIC
 				
 		, PEDAGOGICALTHEORY
 		, GENERALOBJECTIVES
 		, SPECIFICOBJECTIVES
 		, TEACHINGSTRATEGY
 		, LEARNINGSTRATEGY
 		, MODUSOPERANDI
 		
 		, REQUIREDBOOKS
 		, RECOMMENDEDBOOKS
 		, CODEX
 		, HARDWARESOFTWARE
 		, OLDEXAMS
 		
 		, LECTURE_DATE
 		, LECTURE_PLACE
 		, LECTURE_OBJECTIVES
 		, LECTURE_REQUIRED_MATERIAL
 		, LECTURE_TEACHING_ACTIVITY
 		, LECTURE_LEARNING_ACTIVITY
 		

 		, THEME_DATE
 		, THEME_PLACE
 		, THEME_GENERAL_OBJECTIVES
 		, THEME_SPECIFIC_OBJECTIVES
 		, THEME_REQUIRED_MATERIAL
 		, THEME_TEACHING_ACTIVITY
 		, THEME_LEARNING_ACTIVITY
 		
 		, COURSE_SKILL_ELEMENT
 		, COURSE_SKILLS_LEARNING_SET
 			
		, EVALUATION
		, OBJECTIVES_MEASURED
		, SKILLS_MEASURED
 		, EVALUATION_INSTRUMENT
 		
 		, LEARNING_SUPPORT_SAE
 		, LEARNING_SUPPORT_CCE
 		, LEARNING_SUPPORT_OTHER

		, DESCRIPTION
};

    /**
     * Returns the list of rubrics.
     * 
     * @return
     */
    public static final String[] getRubricTypeList() {
	return rubricTypeList;
    }
}
