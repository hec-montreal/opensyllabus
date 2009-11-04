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
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COUnitStructureType {
 // These values must be identical to those found in file rules.xml and the
    // XML representation of Course Outlines.
    public static final String OVERVIEW_UNITSTRUCTURE = "OverviewUnitStruct";
    public static final String SCHEDULEPLACE_UNITSTRUCTURE = "SchedulePlaceUnitStruct";
    public static final String STAFF_UNITSTRUCTURE = "StaffUnitStruct";
    public static final String INTRODUCTION_UNITSTRUCTURE = "IntroductionUnitStruct";
    public static final String OBJECTIVES_UNITSTRUCTURE = "ObjectivesUnitStruct";
    public static final String SKILLS_UNITSTRUCTURE = "SkillsUnitStruct";
    public static final String ASSESSMENT_HEADER_UNITSTRUCTURE = "AssessmentHeaderUnitStruct";
    public static final String ASSESSMENT_INTRO_UNITSTRUCTURE = "AssessmentIntroUnitStruct";
    public static final String ASSESSMENT_UNITSTRUCTURE = "AssessmentUnitStruct";
    public static final String PLAGIARISM_UNITSTRUCTURE = "PlagiarismUnitStruct";
    public static final String PEDAGOGICAL_APPROACH_UNITSTRUCTURE = "PedagogicalApproachUnitStruct";
    public static final String LEARNING_MATERIAL_UNITSTRUCTURE = "LearningMaterialUnitStruct";
    public static final String PEDAGOGICAL_UNITSTRUCTURE = "PedagogicalUnitStruct";
    public static final String SKILLS_COMPONENTS_UNITSTRUCTURE = "SkillsComponentsUnitStruct";
    public static final String SKILLS_LECTURES_UNITSTRUCTURE = "SkillsLecturesUnitStruct";
    public static final String LECTURES_ACTIVITIES_UNITSTRUCTURE = "LecturesActivitiesUnitStruct";
    public static final String BIBLIOGRAPHY_UNITSTRUCTURE = "BibliographyUnitStruct";
    public static final String THEMATIC_BIBLIOGRAPHY_UNITSTRUCTURE = "ThematicBibliographyUnitStruct";
    public static final String LEARNING_SUPPORT_SERVICES_UNITSTRUCTURE = "LearningSupportServicesUnitStruct";
    public final static String NEWS_UNITSTRUCTURE = "NewsUnitStruct";
    
    /**
     * The list of types.
     */
    private static final String[] types = {

    OVERVIEW_UNITSTRUCTURE, 
    SCHEDULEPLACE_UNITSTRUCTURE, 
    STAFF_UNITSTRUCTURE, 
    INTRODUCTION_UNITSTRUCTURE,
    OBJECTIVES_UNITSTRUCTURE, 
    SKILLS_UNITSTRUCTURE,
    ASSESSMENT_HEADER_UNITSTRUCTURE,
    ASSESSMENT_INTRO_UNITSTRUCTURE,
    ASSESSMENT_UNITSTRUCTURE,
    PLAGIARISM_UNITSTRUCTURE,
    PEDAGOGICAL_APPROACH_UNITSTRUCTURE,
    LEARNING_MATERIAL_UNITSTRUCTURE,
    PEDAGOGICAL_UNITSTRUCTURE,
    SKILLS_COMPONENTS_UNITSTRUCTURE,
    SKILLS_LECTURES_UNITSTRUCTURE,
    LECTURES_ACTIVITIES_UNITSTRUCTURE,
    BIBLIOGRAPHY_UNITSTRUCTURE,
    THEMATIC_BIBLIOGRAPHY_UNITSTRUCTURE,
    LEARNING_SUPPORT_SERVICES_UNITSTRUCTURE,
    NEWS_UNITSTRUCTURE
    
    };

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

