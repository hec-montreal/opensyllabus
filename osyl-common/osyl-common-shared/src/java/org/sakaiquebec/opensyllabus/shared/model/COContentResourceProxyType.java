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
    public static final String INFORMATION="InformationContext";
    public static final String BIBLIO="BiblioContext";
    public static final String REFERENCE="ReferenceContext";
    public static final String PEOPLE="PeopleContext";

    /**
     * The list of types.
     */
    private static final String[] types = { 
	INFORMATION,BIBLIO,REFERENCE,PEOPLE
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
