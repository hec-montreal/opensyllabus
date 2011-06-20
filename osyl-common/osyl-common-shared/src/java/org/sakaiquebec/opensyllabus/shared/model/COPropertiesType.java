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

import java.util.Arrays;
import java.util.List;

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
    public static final String IDENTIFIER = "identifier";
    public static final String TEXT = "text";
    public static final String IDENTIFIER_TYPE_URI = "uri";
    public static final String VISIBILITY = "visible";
    public static final String IMPORTANCE = "importance";
    public static final String REQUIREMENT_LEVEL = "level";
    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    public static final String COMMENT = "comment";
    public static final String NON_APPLICABLE = "n/a";
    public static final String ASM_RESOURCE_TYPE = "asmResourceType";

    //Requirement Level
    public final static String REQ_LEVEL_MANDATORY = "mandatory";
    public final static String REQ_LEVEL_RECOMMENDED = "recommended";
    public final static String REQ_LEVEL_COMPLEMENTARY = "complementary";
    public final static String REQ_LEVEL_UNDEFINED = "undefined";

    // Specific to assignments/evaluations
    public static final String WEIGHT = "weight";
    public static final String DATE_START = "date-start";
    public static final String DATE_END = "date-end";
    public static final String LOCATION = "location";
    public static final String MODE = "mode";
    public static final String SUBMITION_TYPE = "submition_type";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String MODALITY = "modality";
    public static final List<String> LOCATION_VALUES = Arrays.asList(new String[]{"inClass","home"}); 
    public static final List<String> MODE_VALUES = Arrays.asList(new String[]{"individual","team"}); 
    public static final List<String> SUBMITION_TYPE_VALUES = Arrays.asList(new String[]{"paper","elect"}); 
    public static final List<String> MODALITY_VALUES = Arrays.asList(new String[]{"oral","written"});

    //SPECIFIC to sakai entity
    public static final String PROVIDER = "provider";
    
    // Specific to contact-info
    public static final String PERSON_TITLE = "title";
    public static final String SURNAME = "surname";
    public static final String FIRSTNAME = "firstname";
    public static final String OFFICE_ROOM = "officeroom";
    public static final String TEL = "tel";
    public static final String EMAIL = "email";
    public static final String AVAILABILITY = "availability";

    // AsmObject
    public static final String MODIFIED = "modified";

    //Specific to BiblioResource
    public static final String BOOKSTORE = "bookstore";
    public static final String AUTHOR="author";
    public static final String JOURNAL="journal";
    public static final String RESOURCE_TYPE="resourceType";
    public static final String IDENTIFIER_TYPE_LIBRARY = "library";
    public static final String IDENTIFIER_TYPE_BOOKSTORE = "bookstore";
    public static final String IDENTIFIER_TYPE_OTHERLINK = "other_link";
    public static final String IDENTIFIER_TYPE_OTHERLINK_LABEL = "label";
    public static final String IDENTIFIER_TYPE_NOLINK = "nolink";
    public static final String IDENTIFIER_TYPE_DOI = "doi";
    public static final String IDENTIFIER_TYPE_ISN = "isn";

    // AsmRoot
    public static final String TEMPLATE = "template";
    public static final String TITLE = "title";
    public static final String LANGUAGE = "language";
    public static final String CREATED = "created";
    public static final String LAST_MODIFIED_BY = "lastModifiedBy";
    public static final String PARENT = "parent";
    public static final String CREATOR="creator";

    // CO type
    public static final String PUBLISHED = "published";
    public static final String PREVIOUS_PUBLISHED = "previousPublished";
    public static final String CHILDS = "childs";
    public static final String COURSE_ID = "courseId";
    public static final String DEPARTMENT = "department";
    public static final String PROGRAM="program";

    //Specific to  Reference context
    public static final String DISPLAY_AS = "displayAs";

    //Specific to Document resource
    public static final String LICENSE = "license";
    public static final String RIGHTS = "rights";
    
    //Specific to Type of resource    
    public static final String RESOURCE_TYPE_CITATION = "typeResource";
    public static final String RESOURCE_TYPE_DOCUMENT = "typeResource";    
    public static final String RESOURCE_TYPE_LINK = "typeResource";    
    public static final String TYPE_RESOURCE = "typeResource";    

    //Specific to ASMUnit
    public static final String PREFIX = "prefix";

    public final static String SEMANTIC_TAG = "semanticTag";
    public final static String SEMANTIC_TAG_USERDEFLABEL="userDefLabel";

    public final static List<String> CDATA_NODE_NAMES =
	    Arrays.asList(new String[] { COPropertiesType.DESCRIPTION,
		    COPropertiesType.TEXT, COPropertiesType.COMMENT,
		    COPropertiesType.AVAILABILITY });
    

}
