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
 * course outline.
 * 
 * TODO: when GWT 1.5 release will be used, replace this class with enum types
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: COContentUnitType.java 525 2008-05-22 04:30:56Z sacha.lepretre@crim.ca $
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentUnitType {

    // These values must be identical to those found in file rules.xml and the
    // XML representation of Course Outlines.
    public static final String LECTURE = "lecture";
    public static final String PRESENTATION = "presentation";
    public static final String CONTACTINFO = "contactinfo";
    public static final String LEARNINGMAT = "learningmat";
    public static final String EVALUATION = "evaluation";
    public static final String LAB_SESSION = "labsession";
    public static final String WEEK_LESSON = "weeklesson";
    public static final String DAY_SEMINAR = "dayseminar";
    public static final String THEME = "theme";

    /**
     * The list of types.
     */
    private static final String[] types =
	    { LECTURE, PRESENTATION, CONTACTINFO, LEARNINGMAT, EVALUATION,
		    LAB_SESSION, WEEK_LESSON, DAY_SEMINAR, THEME };

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
