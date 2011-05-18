package org.sakaiquebec.opensyllabus.admin.cmjob.api;


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
 *****************************************************************************/

/**
 * This job takes away several permissions on all sites corresponding to session 
 * period identified in a XML configuration file.
 * Permissions to be taken away: osyl.edit, osyl.publish, asn, etc. (to validate)
 * an objective being to allow permissions to read and copy 
 * 
 * @author <a href="mailto:alejandro.hernandez-pena@hec.ca">Alejandro Hernandez</a>
 * @version $Id: $
 */
public interface UnFreezeSitesJob extends OsylAbstractQuartzJob {

	// These are the functions that will allowed to these sites. If we encounter
	// any other function the job will try to remove it.
		
    public final static String ADMINSITENAME = "opensyllabusAdmin";

	//Folder that contains all the configuration files
	public final static String ADMIN_CONTENT_FOLDER = "/content/group/opensyllabusAdmin/";
    public final static String CONFIGFORLDER = ADMIN_CONTENT_FOLDER + "config/";  
    
    //The site has a field "isfrozen"
    public final static String PROP_SITE_ISFROZEN = "isfrozen";
    
    //Do you want to freeze this site?
    public final static String YES = "true";
    public final static String NOT = "false";
    
    public final static String REALM_PREFIX = "/site/";
    
    public final static String TEMPLATE_ID = "!site.template.course";

}
