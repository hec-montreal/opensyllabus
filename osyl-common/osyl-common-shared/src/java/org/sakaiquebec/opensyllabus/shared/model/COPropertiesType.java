/*******************************************************************************
 * $Id: COPropertiesType.java 1750 2008-12-01 22:03:01Z mathieu.cantin@hec.ca $
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

/**
 * This class contains the list of all the possible properties in a course
 * outline. TODO: when GWT 1.5 release will be used, replace this class with
 * enum types
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: COContentUnitType.java 525 2008-05-22 04:30:56Z
 *          sacha.lepretre@crim.ca $
 */
public class COPropertiesType {

    // General
    public static final String TEXT = "text";
    public static final String URI = "uri";
    public static final String VISIBILITY = "visible";
    public static final String IMPORTANCE = "importance";
    public static final String REQUIREMENT_LEVEL = "level";

    public final static String REQ_LEVEL_MANDATORY = "mandatory";
    public final static String REQ_LEVEL_RECOMMENDED = "recommended";
    public final static String REQ_LEVEL_COMPLEMENTARY = "complementary";
    public final static String REQ_LEVEL_UNDEFINED = "undefined";
    // Specific to citations
    public static final String AVAILABILITY_COOP = "availability_coop";
    public static final String AVAILABILITY_LIB = "availability_lib";
    public static final String CITATION = "citation";
    public static final String LINK = "link";
    public static final String TYPE = "type";
    public static final String ISBNISSN = "isbnissn";
    public static final String CITATIONLISTID = "citation_list_id";

    // Specific to assignments/evaluations
    public static final String RATING = "rating";
    public static final String OPENDATE = "opendate";
    public static final String CLOSEDATE = "closedate";
    public static final String DATE = "date";
    public static final String LOCATION = "location";
    public static final String MODE = "mode";
    public static final String RESULT = "result";
    public static final String SCOPE = "scope";
    public static final String SUBMITION_TYPE ="submition_type";
    public static final String EVALUATION_TYPE= "evaluation_type";

    // Specific to contact-info (coordonnées)
    public static final String ROLE = "role";
    public static final String LASTNAME = "lastname";
    public static final String FIRSTNAME = "firstname";
    public static final String OFFICE = "office";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String AVAILABILITY = "availability";
    public static final String COMMENTS = "comments";
    
    public static final String MODIFIED = "modified";

    /**
     * The list of types.
     */
    private static final String[] types =
	    {     
	      TEXT
		, URI
		, VISIBILITY
		, IMPORTANCE
		, REQUIREMENT_LEVEL
		, AVAILABILITY_COOP
		, AVAILABILITY_LIB
		, CITATION
		, LINK
		, ISBNISSN
		, TYPE
		, CITATIONLISTID
		, RATING
		, OPENDATE
		, CLOSEDATE
		, DATE
		, ROLE
		, LASTNAME
		, FIRSTNAME
		, OFFICE
		, PHONE
		, EMAIL
		, AVAILABILITY
		, COMMENTS
		    
	    };

    /**
     * @return string array of types
     */
    public static final String[] getTypes() {
	return types;
    }
}
