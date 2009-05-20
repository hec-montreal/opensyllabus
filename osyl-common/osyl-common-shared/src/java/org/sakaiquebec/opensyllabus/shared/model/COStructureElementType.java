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
    public static final String EVALUATIONS = "evaluations";
    public static final String TOPICS = "topics";
    
    /**
     * The list of types.
     */
    private static final String[] types = { LECTURES, EVALUATIONS, TOPICS };

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
