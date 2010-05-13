/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.admin.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class is used to retrieved informations saved in the config.xml file.
 * The properties in the config.xml file are used for administration purposes
 * such as determine the courses that will be created automatically.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface ConfigurationService {

    // Tags used in the xml files
    // For the official sites xml configuration file
    public final static String COURSES = "courses";

    public final static String STARTDATE = "startDate";

    public final static String ENDDATE = "endDate";

    // For adding or removing users with a specific role or removing a role in
    // all the course sites
    public final static String ROLE = "role";

    public final static String ADDEDUSERS = "addedUsers";

    public final static String REMOVEDUSERS = "removedUsers";

    public final static String FUNCTIONS = "FUNCTIONS";
    
    
    //For adding a role with
    
    public final static String FUNCTIONS_ROLE = "role";
    
    public final static String ALLOWED_FUNCTIONS = "allowed_functions";

    public final static String DISALLOWED_FUNCTIONS = "disallowed_functions";

    public final static String REMOVED_ROLE = "removedRole";

    public final static String ADMINSITENAME = "opensyllabusAdmin";

    /**
     * Folder that contains all the configuration files
     */
    public final static String CONFIGFORLDER =
	    "/content/group/opensyllabusAdmin/config/";

    /**
     * This folder contains all the configuration files that modify
     * (add-remove-update) a role.
     */
    public final static String ROLEFOLDER =
	    "/content/group/opensyllabusAdmin/config/role/";

    public final static String OFFSITESCONFIGFILE = "offSitesConfig.xml";

    public final static String FUNCTIONSSCONFIGFILE = "functionsConfig.xml";

    // Parameters used in the official sites synchronisation job
    public Date getEndDate();

    public Date getStartDate();

    public List<String> getCourses();

    public Map<String, Map<String, Object>> getUdatedRoles();
    
    public String getFunctionsRole();
    
    public String getRoleToRemove();
    
    public List<String> getAllowedFunctions();
    
    public List<String> getDisallowedFunctions();

}
