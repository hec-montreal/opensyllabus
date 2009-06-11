/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 * Contains the possible types of a <code>COContent</code>
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COContentType {
    public static final String BYLECTURE = "bylecture";
    public static final String BY_OBJECTIVES_LECTURES = "ByObjectivesLectures";
    public static final String BY_OBJECTIVES_THEMES = "ByObjectivesThemes";
    public static final String BY_OBJECTIVES_ACTIVITIES = "ByObjectivesActivities";
    public static final String BY_SKILLS_LECTURES = "BySkillsLectures";
    public static final String BY_SKILLS_ACTIVITIES = "BySkillsActivities";
    public static final String BY_SKILLS_COMPONENTS = "BySkillsComponents";
   
    //TODO to be completed

    /**
     * The list of types.
     */
    private static final String[] types =
	    {   
	        BYLECTURE
	      , BY_OBJECTIVES_LECTURES
	      , BY_OBJECTIVES_THEMES
	      , BY_OBJECTIVES_ACTIVITIES
	      , BY_SKILLS_LECTURES
	      , BY_SKILLS_ACTIVITIES
	      , BY_SKILLS_COMPONENTS
	      
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

