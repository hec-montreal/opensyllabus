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

import java.util.Arrays;
import java.util.List;

/**
 * This class contains the list of all the possible content unit type in a
 * course outline. TODO: when GWT 1.5 release will be used, replace this class
 * with enum types
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: COContentUnitType.java 525 2008-05-22 04:30:56Z
 *          sacha.lepretre@crim.ca $
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COUnitContentType {

    // These values must be identical to those found in file rules.xml and the
    // XML representation of Course Outlines.
    public static final String OVERVIEW_UNITCONTENT = "OverviewUnitContent";
    public static final String COURSE_OFFICIALDESCRIPTION_UNITCONTENT = "CourseOfficialDescriptionUnitContent";
    public static final String SCHEDULEPLACE_UNITCONTENT = "SchedulePlaceUnitContent";
    public static final String STAFF_UNITCONTENT = "StaffUnitContent";
    public static final String INTRODUCTION_UNITCONTENT = "IntroductionUnitContent";
    public static final String OBJECTIVES_UNITCONTENT = "ObjectivesUnitContent";
    public static final String SKILLS_UNITCONTENT = "SkillsUnitContent";
    public static final String ASSESSMENT_HEADER_UNITCONTENT = "AssessmentHeaderUnitContent";
    public static final String ASSESSMENT_INTRO_UNITCONTENT = "AssessmentIntroUnitContent";
    public static final String ASSESSMENT_UNITCONTENT = "AssessmentUnitContent";
    public static final String PLAGIARISM_UNITCONTENT = "PlagiarismUnitContent";
    public static final String PEDAGOGICAL_APPROACH_UNITCONTENT = "PedagogicalApproachUnitContent";
    public static final String LEARNING_MATERIAL_UNITCONTENT = "LearningMaterialUnitContent";
    public static final String PEDAGOGICAL_UNITCONTENT = "PedagogicalUnitContent";
    public static final String SKILLS_COMPONENTS_UNITCONTENT = "SkillsComponentsUnitContent";
    public static final String SKILLS_LECTURES_UNITCONTENT = "SkillsLecturesUnitContent";
    public static final String LECTURES_ACTIVITIES_UNITCONTENT = "LecturesActivitiesUnitContent";
    public static final String BIBLIOGRAPHY_UNITCONTENT = "BibliographyUnitContent";
    public static final String THEMATIC_BIBLIOGRAPHY_UNITCONTENT = "ThematicBibliographyUnitContent";
    public static final String LEARNING_SUPPORT_SERVICES_UNITCONTENT = "LearningSupportServicesUnitContent";
    public final static String NEWS_UNITCONTENT="NewsUnitContent"; 
    public static final String INFORMATION_OF_LESSON_UNITCONTENT = "InformationOfLessonUnitContent";
    public static final String TEACHER_UNITCONTENT = "TeacherUnitContent";
    public static final String COURSE_INTRODUCTION_UNITCONTENT = "CourseIntroductionUnitContent";
    public static final String PURPOSEFUL_STUDIES_UNITCONTENT = "PurposefulStudiesUnitContent";
    public static final String INFORMATION_UNITCONTENT = "InformationUnitContent";
    public static final String MODALITY_OF_VALUATION_UNITCONTENT = "ModalityOfValuationUnitContent";
    public static final String NEW_PLAGIARISM_UNITCONTENT = "NewPlagiarismUnitContent";
    public static final String PEDAGOGIC_APPROACHES_UNITCONTENT = "PedagogicApproachesUnitContent";
    public static final String PEDAGOGIC_RESOURCES_UNITCONTENT = "PedagogicResourcesUnitContent";  
    public static final String PERIODE_UNITCONTENT = "PeriodeUnitContent";  
    public static final String NEW_BIBLIOGRAPHY_UNITCONTENT = "NewBibliographyUnitContent";
    public static final String DISCIPLINARY_LIBRARY_UNITCONTENT = "DisciplinaryLibraryUnitContent";
    public static final String SERVICES_OF_SUPPORT_TO_STUDY_UNITCONTENT = "ServicesOfSupportToStudyUnitContent";

    // public static final String EVALUATION_INSTRUMENT =
    // "EvaluationInstrument";
    // public static final String LAB_SESSION = "labsession";
    // public static final String WEEK_LESSON = "weeklesson";
    // public static final String DAY_SEMINAR = "dayseminar";
    // public static final String THEME = "Theme";
    // public static final String FACPROGIDENT = "facprogident";
    // public static final String COURSEIDENT = "courseident";
    // public static final String ACTIVITY = "Activity";
    // public static final String LECTURE_CONTEXT_SKILLS =
    // "LectureContextSkills";
    // public static final String COURSEOFFICIALDESCRIPTION =
    // "courseofficialdescription";
    // public static final String COURSE_IN_CURRICULUM = "CourseInCurriculum";
    // public static final String COURSEINTRO = "courseintro";
    // public static final String COURSESCHEDULEPLACE = "coursescheduleplace";
    // public static final String COURSE_OBJECTIVES = "CourseObjectives";
    // public static final String COURSE_SKILLS = "CourseSkills";
    // public static final String PEDAGOGICAL_APPROACH = "PedagogicalApproach";
    // public static final String PEDAGOGICAL_APPROACH_SPECIFIC=
    // "PedagogicalApproachSpecific";
    // public static final String ASSESMENTDESCRIPTION = "assesmentdescription";
    // public static final String PLAGIARISM = "plagiarism";
    // public static final String LECTURE_TEACHING_ACTIVITY =
    // "LectureTeachingActivity";
    // public static final String LECTURE_LEARNING_ACTIVITY =
    // "LectureLearningActivity";
    // public static final String THEME_TEACHING_ACTIVITY =
    // "ThemeTeachingActivity";
    // public static final String THEME_LEARNING_ACTIVITY =
    // "ThemeLearningActivity";
    // public static final String BIBLIOGRAPHY = "Bibliography";
    // public static final String THEMATIC_BIBLIOGRAPHY =
    // "ThematicBibliography";
    // public static final String LEARNING_SUPPORT_SERVICES =
    // "LearningSupportServices";

    /**
     * The list of types.
     */
    private static final String[] types = {

	OVERVIEW_UNITCONTENT, 
	COURSE_OFFICIALDESCRIPTION_UNITCONTENT, 
	SCHEDULEPLACE_UNITCONTENT, 
	STAFF_UNITCONTENT, 
	INTRODUCTION_UNITCONTENT,
	OBJECTIVES_UNITCONTENT,
	SKILLS_UNITCONTENT,
	ASSESSMENT_UNITCONTENT,
	ASSESSMENT_INTRO_UNITCONTENT,
	PLAGIARISM_UNITCONTENT,
	PEDAGOGICAL_APPROACH_UNITCONTENT,
	LEARNING_MATERIAL_UNITCONTENT, 
	PEDAGOGICAL_UNITCONTENT, 
	SKILLS_COMPONENTS_UNITCONTENT,
	SKILLS_LECTURES_UNITCONTENT,
	LECTURES_ACTIVITIES_UNITCONTENT,
	THEMATIC_BIBLIOGRAPHY_UNITCONTENT,
	LEARNING_SUPPORT_SERVICES_UNITCONTENT,
	NEWS_UNITCONTENT,
	INFORMATION_OF_LESSON_UNITCONTENT,
	TEACHER_UNITCONTENT,
	COURSE_INTRODUCTION_UNITCONTENT,
	PURPOSEFUL_STUDIES_UNITCONTENT,
	INFORMATION_UNITCONTENT,
	MODALITY_OF_VALUATION_UNITCONTENT,
	NEW_PLAGIARISM_UNITCONTENT,
	PEDAGOGIC_APPROACHES_UNITCONTENT,
	PEDAGOGIC_RESOURCES_UNITCONTENT,
	PERIODE_UNITCONTENT ,  
	NEW_BIBLIOGRAPHY_UNITCONTENT,
	DISCIPLINARY_LIBRARY_UNITCONTENT,
	SERVICES_OF_SUPPORT_TO_STUDY_UNITCONTENT
	
    };

    /*
     * , EVALUATION_INSTRUMENT , LAB_SESSION , WEEK_LESSON , DAY_SEMINAR , THEME
     * , FACPROGIDENT , COURSEIDENT , COURSEOFFICIALDESCRIPTION ,
     * COURSE_IN_CURRICULUM , COURSESCHEDULEPLACE , COURSEINTRO ,
     * COURSE_OBJECTIVES , COURSE_SKILLS , PEDAGOGICAL_APPROACH ,
     * PEDAGOGICAL_APPROACH_SPECIFIC , ASSESMENTDESCRIPTION , PLAGIARISM ,
     * LECTURE_TEACHING_ACTIVITY , LECTURE_LEARNING_ACTIVITY ,
     * THEME_TEACHING_ACTIVITY , THEME_LEARNING_ACTIVITY , BIBLIOGRAPHY ,
     * THEMATIC_BIBLIOGRAPHY , LEARNING_SUPPORT_SERVICES ,
     * LECTURE_CONTEXT_SKILLS , ACTIVITY
     */

    /**
     * @return string array of types
     */
    public static final String[] getTypes() {
	return types;
    }

    /**
     * @return List of types
     */
    public static final List<String> getTypesList() {
	return Arrays.asList(types);
    }
}
