/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COUnitType {
    
    public final static String OVERVIEW_UNIT = "OverviewUnit";
    public final static String SCHEDULEPLACE_UNIT ="SchedulePlaceUnit";
    public final static String STAFF_UNIT = "StaffUnit";
    public final static String INTRODUCTION_UNIT = "IntroductionUnit";
    public final static String OBJECTIVES_UNIT = "ObjectivesUnit";
    public final static String SKILLS_UNIT = "SkillsUnit";
    public static final String ASSESSMENT_HEADER_UNIT = "AssessmentHeaderUnit";
    public static final String ASSESSMENT_INTRO_UNIT = "AssessmentIntroUnit";
    public final static String ASSESSMENT_UNIT = "AssessmentUnit";
    public final static String PLAGIARISM_UNIT = "PlagiarismUnit";
    public final static String PEDAGOGICAL_APPROACH_UNIT = "PedagogicalApproachUnit";
    public final static String LEARNING_MATERIAL_UNIT = "LearningMaterialUnit";
    public final static String PEDAGOGICAL_UNIT = "PedagogicalUnit";
    public final static String SKILLS_COMPONENTS_UNIT = "SkillsComponentsUnit";
    public final static String SKILLS_LECTURES_UNIT = "SkillsLecturesUnit";
    public final static String LECTURES_ACTIVITIES_UNIT = "LecturesActivitiesUnit";
    public final static String BIBLIOGRAPHY_UNIT = "BibliographyUnit";
    public final static String THEMATIC_BIBLIOGRAPHY_UNIT = "ThematicBibliographyUnit";
    public final static String LEARNING_SUPPORT_SERVICES_UNIT = "LearningSupportServicesUnit";
    public final static String NEWS_UNIT = "NewsUnit";
    public static final String INFORMATION_OF_LESSON_UNIT = "InformationOfLessonUnit";
    public static final String TEACHER_UNIT = "TeacherUnit";
    public static final String COURSE_INTRODUCTION_UNIT = "CourseIntroductionUnit";
    public static final String PURPOSEFUL_STUDIES_UNIT = "PurposefulStudiesUnit";
    public static final String INFORMATION_UNIT = "InformationUnit";
    public static final String MODALITY_OF_VALUATION_UNIT = "ModalityOfValuationUnit";
    public static final String NEW_PLAGIARISM_UNIT = "NewPlagiarismUnit";
    public static final String PEDAGOGIC_APPROACHES_UNIT = "PedagogicApproachesUnit";
    public static final String PEDAGOGIC_RESOURCES_UNIT = "PedagogicResourcesUnit";  
    public static final String PERIODE_UNIT = "PeriodeUnit";  
    public static final String NEW_BIBLIOGRAPHY_UNIT= "NewBibliographyUnit";
    public static final String DISCIPLINARY_LIBRARY_UNIT = "DisciplinaryLibraryUnit";
    public static final String SERVICES_OF_SUPPORT_TO_STUDY_UNIT = "ServicesOfSupportToStudyUnit";
    
    //public final static String NEWS = "news";
    // public final static String RECITATION= "recitation";
    // public final static String MODULE= "module";
//    public final static String LABORATORY = "laboratory";
//    public final static String FAQ = "faq";

    /**
     * The list of types.
     */
    private static final String[] types = {

	OVERVIEW_UNIT, 
	SCHEDULEPLACE_UNIT, 
	STAFF_UNIT, 
	INTRODUCTION_UNIT,
	OBJECTIVES_UNIT,
	SKILLS_UNIT,
	ASSESSMENT_HEADER_UNIT,
	ASSESSMENT_INTRO_UNIT,
	ASSESSMENT_UNIT, 
	PLAGIARISM_UNIT,
	PEDAGOGICAL_APPROACH_UNIT,
	LEARNING_MATERIAL_UNIT, 
	PEDAGOGICAL_UNIT,
	SKILLS_COMPONENTS_UNIT,
	SKILLS_LECTURES_UNIT,
	LECTURES_ACTIVITIES_UNIT,
	BIBLIOGRAPHY_UNIT,
	THEMATIC_BIBLIOGRAPHY_UNIT,
	LEARNING_SUPPORT_SERVICES_UNIT,
	NEWS_UNIT,
	INFORMATION_OF_LESSON_UNIT,
	TEACHER_UNIT,
	COURSE_INTRODUCTION_UNIT,
	PURPOSEFUL_STUDIES_UNIT,
	INFORMATION_UNIT,
	MODALITY_OF_VALUATION_UNIT,
	NEW_PLAGIARISM_UNIT,
	PEDAGOGIC_APPROACHES_UNIT,
	PEDAGOGIC_RESOURCES_UNIT,
	PERIODE_UNIT,
	NEW_BIBLIOGRAPHY_UNIT,
	DISCIPLINARY_LIBRARY_UNIT,
	SERVICES_OF_SUPPORT_TO_STUDY_UNIT,

    };

    /**
     * @return string array of types
     */
    public static final String[] getTypes() {
	return types;
    }

    /**
     * Gets the list of all possible <code>COUnit</code> types.
     * 
     * @return a list of all possible types.
     */
    public static final List<String> getTypesList() {
	return Arrays.asList(types);
    }
}
