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
import java.util.HashMap;
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

    public final static String PROGRAMS = "programs";

    public final static String SERVENS = "servEns";

    public final static String PILOTE_E2017= "pilote-e2017";

    public final static String PILOTE_A2017 = "pilote-a2017";

    public final static String PILOTE_H2018 = "pilote-h2018";

    // For updating all courses sites to frozen sites
    public final static String FROZENPERMISSIONS = "permissions";

    public final static String SESSIONID = "sessionId";

    public final static String ROLESET = "role";

    public final static String ROLEID = "id";

    public final static String PERMISSIONS = "permissions";

    // For adding or removing users with a specific role or removing a role in
    // all the course sites
    public final static String ROLE = "role";

    public final static String DESCRIPTION = "description";

    public final static String ADDEDUSERS = "addedUsers";

    public final static String REMOVEDUSERS = "removedUsers";
    
    public final static String COURSEMANAGEMENT = "coursemanagement";
    
    public final static String REPLACEDUSERS = "replacedusers";

    public final static String FUNCTIONS = "functions";

    public final static String REMOVE_FUNCTIONS = "removefunctions";

    // For adding a role with

    public final static String FUNCTIONS_ROLE = "role";

    public final static String ALLOWED_FUNCTIONS = "allowed_functions";

    public final static String DISALLOWED_FUNCTIONS = "disallowed_functions";

    public final static String REMOVED_ROLE = "removedRole";

    public final static String ADMINSITENAME = "opensyllabusAdmin";

    public final static String ADMIN_CONTENT_FOLDER =
	    "/content/group/opensyllabusAdmin/";

    /**
     * Folder that contains all the configuration files
     */
    public final static String CONFIG_FOLDER = ADMIN_CONTENT_FOLDER + "config/";

    /**
     * This folder contains all the configuration files that modify
     * (add-remove-update) a role.
     */
    public final static String ROLE_FOLDER = CONFIG_FOLDER + "role/";

    public final static String OFFSITESCONFIGFILE = "offSitesConfig.xml";

    public final static String FUNCTIONSSCONFIGFILE = "functionsConfig.xml";

    // File containing the xml configuration of sites to freeze
    public final static String FROZENSITESCONFIG = "frozenSitesConfig.xml";
    public final static String UNFROZENSITESCONFIG = "unFrozenConfig.xml";

    public final static String XSL_FILENAME = "courseOutline.xsl";
    
    public final static String PRINT_VERSION_CONFIG = "printVersionConfig.xml";

    public final static String INCLUDING_FROZEN_SITES =
	    "includingFrozenSites";

    public final static String INCLUDING_DIR_SITES =
	    "includingDirSites";

    public final static String UPDATE_GROUP = "updateGroup";

    public final static String CM_EXCEPTIONS_FOLDER = CONFIG_FOLDER + "cm_exceptions/";
    
    public final static String CM_EXCEPTIONS_USERS = "users";
    
    public final static String CM_EXCEPTIONS_COURSES = "courses";
    
    public final static String CM_EXCEPTIONS_CATEGORY = "category";
    
    public final static String CM_EXCEPTIONS_ROLE = "role";
    
    public final static String CM_EXCEPTIONS_PROGRAM= "program";
    
    
    /*
     * Parameters used for evalsys
     * */
    public final static String EVALSYS_TERMS_FILE = "evalsysTerm.properties";
    
    
    /*
     * Parameters used for overriding site users with cm users
     * */
    public final static String OVERRIDESIREUSERS_CONFIG_FILE_NAME = "overrideSiteUsers.properties";
    
    
    // Parameters used in the official sites synchronisation job
    public Date getEndDate();

    public Date getStartDate();

    public List<String> getCourses();

    public List<String> getServEns();

    public List<String> getPrograms();

   public String getFunctionsRole();

    public String getDescription();

    public String getRoleToRemove();

    public List<String> getAllowedFunctions();

    public List<String> getDisallowedFunctions();

    public String getCourseOutlineXsl();

    public String getSessionId();

    public List<String> getFrozenPermissions();

    public HashMap<String, List<String>> getFrozenFunctionsToAllow();

    public void getFrozenSessionIdConfig();

    public void getConfigToFreeze();
    
    public boolean isIncludingFrozenSites();
    
    public boolean isIncludingDirSites();
    
    public Map<String, String> getPrintVersionJobParams();
    
    public Map<String,Map<String,String>> getCmExceptions();

    public List<String> getPiloteE2017();

    public boolean inE2017Pilote (String courseId, List<String> piloteE2017);

    public List<String> getPiloteA2017();

    public boolean inA2017Pilote (String courseId, List<String> piloteA2017);

    public List<String> getPiloteH2018();

    public boolean inH2018Pilote (String courseId, List<String> piloteH2018);

    List<RoleSynchronizationPOJO> getRoles();
}
