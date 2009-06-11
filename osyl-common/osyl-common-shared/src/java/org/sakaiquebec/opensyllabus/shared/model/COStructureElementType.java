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
 * This class contains the list of all the possible structure element in a course outline.
 * TODO: when GWT 1.5 release will be used, replace this class with enum types
 *
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @version $Id: COStructureElementType.java 525 2008-05-22 04:30:56Z sacha.lepretre@crim.ca $
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COStructureElementType {

    public static final String LECTURES = "lectures";

    public static final String LECTURES_LIST_CONTEXT_SKILLS = "LecturesListContextSkills";

    public static final String EVALUATIONS = "evaluations";
    public static final String TOPICS = "topics";
    public static final String COURSEINFO = "CourseInfo";
    public static final String COURSEDESCRIPTION = "coursedescription";
    public static final String PEDAGOGICAL_APPROACH_STRUC = "PedagogicalApproachStruc";   
    public static final String PEDAGOGICAL_APPROACH_SPECIFIC_STRUC = "PedagogicalApproachSpecificStruc";
    
    public static final String EVALUATION_STRUC = "EvaluationStruc";
    public static final String EVALUATIONS_LIST = "EvaluationsList";
    public static final String EVALUATION_INSTRUMENTS_LIST = "EvaluationInstrumentsList";
    
    public static final String LECTURE_STRUC = "LectureStruc";
       
    public static final String LECTURE_TEACHING_ACTIVITY_LIST = "LectureTeachingActivityList";    
    public static final String LECTURE_LEARNING_ACTIVITY_LIST = "LectureLearningActivityList";
  
    public static final String THEMES_LIST = "ThemesList";

    public static final String THEME_STRUC = "ThemeStruc";

    public static final String THEME_TEACHING_ACTIVITY_LIST = "ThemeTeachingActivityList";    
    public static final String THEME_LEARNING_ACTIVITY_LIST = "ThemeLearningActivityList";

    /**
     * The list of types.
     */
    private static final String[] types = {   LECTURES 
	 
	                                    , LECTURES_LIST_CONTEXT_SKILLS

	                                    , EVALUATIONS 
	                                    , TOPICS 
	                                    , COURSEINFO
	                                    , COURSEDESCRIPTION
	                                    , PEDAGOGICAL_APPROACH_STRUC
	                                    , PEDAGOGICAL_APPROACH_SPECIFIC_STRUC

	                                    , EVALUATIONS_LIST
	                                    , EVALUATION_STRUC
	                                    , EVALUATION_INSTRUMENTS_LIST

	                                    , LECTURE_STRUC
	                                    , LECTURE_TEACHING_ACTIVITY_LIST
	                                    , LECTURE_LEARNING_ACTIVITY_LIST
	                                    
	                                    , THEMES_LIST
	                                    , THEME_STRUC
	                                    , THEME_TEACHING_ACTIVITY_LIST
	                                    , THEME_LEARNING_ACTIVITY_LIST
    };

    /**
     * @return string array of types
     */
    public static final String[] getTypes() {
	return types;
    }
    
    /**
     * Gets the list of all possible <code>COStructureElement</code> types.
     * 
     * @return a list of all possible types.
     */
    public static final List<String> getTypesList(){
	return Arrays.asList(types);
    }
}
