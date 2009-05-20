/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains the list of all the possible ressource proxies in a course outline.
 * TODO: when GWT 1.5 release will be used, replace this class with enum types
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: COContentUnitType.java 525 2008-05-22 04:30:56Z sacha.lepretre@crim.ca $
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentResourceProxyType {

    // These values must be identical to those found in file rules.xml and the
    // XML representation of Course Outlines.
    public static final String DOCUMENT = "document";
    public static final String HYPERLINK = "url";
    public static final String INTERNALDOCUMENT = "internaldocument";
    public static final String TEXT = "text";
    public static final String CITATION = "citation";
    public static final String CONTACTINFO = "contactinforesource";
    //at this point, there are different kind of assignments
    public static final String ASSIGNMENT = "assignment";
    //public static final String EVALUATION = "evaluation";
    public static final String EHOMEWORK= "ehomework";
    public static final String HOMEWORK= "homework";
    public static final String  EXAM = "exam";
    public static final String ORALPRESENTATION= "oralpresentation";
    public static final String PARTICIPATION= "participation";
    public static final String QUIZ= "quiz";
    public static final String OTHEREVALUATIONS = "otherevaluations";

    
    /**
     * The list of types.
     */
    private static final String[] types = { 
	DOCUMENT, 
	HYPERLINK, 
	INTERNALDOCUMENT, 
	TEXT, 
	CITATION,
	CONTACTINFO,
	ASSIGNMENT,
	//EVALUATION,
	HOMEWORK,
	EHOMEWORK,
	EXAM,
	ORALPRESENTATION,
	PARTICIPATION,
	QUIZ,
	OTHEREVALUATIONS
	
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
    public static final List<String> getTypesList(){
	return Arrays.asList(types);
    }
    
}
