package org.sakaiquebec.opensyllabus.common.api;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 * OsylRealmService defines all functions for managing
 * created realms for Opensyllabus which can be used as
 * a template for sites. It also covers functions to managing
 * the roles and permissions of the realm.
 * 
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina Bauer-Oppinger</a>
 * @version $Id: $
 */
public interface OsylRealmService {
	
    /**
     * Returns the name of the site type of this realm
     * 
     * @return a String with the site type
     */
	public String getSiteType();
	
    /**
     * Returns the special defined functions of this realm
     * 
     * @return a Set containing the new functions of this realm
     */
	public Set<String> getPermissionFunctions();
	
    /**
     * Returns all defined roles of this realm
     * 
     * @return a Set containing the IDs of the roles
     */
	public Set<String> getRoles();
	
    /**
     * Returns the functions with the lists of allowed roles
     * 
     * @return a HashMap containing the function as key and the
     * 		list with the allowed roles as value
     */
	public HashMap<String, List<String>> getFunctionsWithAllowedRoles();
	
    /**
     * Gives all roles in the given list the permission for this
     * function. Roles not included in the list lose the
     * permission.
     * 
     * @param function the name of the function to update
     * @param allowedRoles names of the roles which have permission 
     * 		for this function
     */
	public void updateFunction(String function, Set<String> allowedRoles);
	
}
