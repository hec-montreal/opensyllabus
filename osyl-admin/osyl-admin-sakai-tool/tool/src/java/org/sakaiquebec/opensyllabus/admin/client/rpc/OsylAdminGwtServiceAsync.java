/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.admin.client.rpc;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylAdminGwtServiceAsync{

    /**
     * Creates users from data extracts located in specified directory.
     * 
     * @param fileDirectory
     * @param callback
     */
    public void createUsers(String fileDirectory, AsyncCallback<Void> callback);

    /**
     * Returns all defined roles of the realm template
     * 
     * @return a Set containing the IDs of the roles
     */
    public void getTemplateRoles(AsyncCallback<Set<String>> callback);
    
    /**
     * Returns the functions with the lists of allowed roles 
     * of the realm template
     * 
     * @return a HashMap containing the function as key and the
     * 		list with the allowed roles as value
     */
    public void getTemplateFunctionsWithAllowedRoles(AsyncCallback<HashMap<String, List<String>>> callback);
    
    /**
     * Gives all roles in the given list the permission for this
     * function. Roles not included in the list lose the
     * permission.
     * 
     * @param function the name of the function to update
     * @param allowedRoles names of the roles which have permission 
     * 		for this function
     */
    public void updateTemplateFunction(String function, Set<String> allowedRoles, AsyncCallback<Void> callback);
    
    /**
     * Gives all roles in the given list the permission for the
     * functions which are the key values of the HashMap. 
     * Roles not included in the list lose the permission.
     * 
     * @param functions the updated functions keys and the allowed
     * 		roles as values
     */
    public void updateTemplateFunctions(HashMap<String, Set<String>> functions, AsyncCallback<Void> callback);
}

