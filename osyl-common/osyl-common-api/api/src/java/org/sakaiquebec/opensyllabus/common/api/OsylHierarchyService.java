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
package org.sakaiquebec.opensyllabus.common.api;

import java.util.List;

import org.sakaiquebec.opensyllabus.common.dao.CORelation;

/**
 * The OsylHierarchyService manages access to the sites that have a hierarchical 
 * relation. It verifies that the users of these sites has clear access to the 
 * required resources.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylHierarchyService {
 
    /**
     * Id of the CHILD role
     */
    public static final String CHILD_ROLE = "Child";
    
    /**
     * Permission added to the CHILD role
     */
    public static final String CONTENT_READ_PERMISSION = "content.read";
    
    /**
     * Prefix of the site realm id.
     */
    public static final String REALM_ID_PREFIX = "/site/";
    
    /**
     * This method is used to add the user of the child site to all its ancestors
     * sites with the CHILD role. If the user already exists in the parent site, 
     * he will not be added.
     * 
     * @param siteId
     */
    public void addOrUpdateUsers(String siteId);
    
    /**
     * This method will remove all the users from the parent site that come
     * from the child site and have the CHILD role.
     * 
     * @param parentSiteId
     * @param childSiteId
     */
    public void removeUsers(String parentSiteId, String childSiteId);
    
    /**
     * This method checks if the given site has the CHILD role.
     * 
     * @param siteId
     * @return
     */
    public boolean hasChildRole(String siteId);
    
    /**
     * This method adds the CHILD role in the site if does not already exist. 
     * 
     * @param siteId
     */
    public void addChildRole(String siteId);

    
	/**
	 * This method is used to add the user of the child site to the parent site 
     * with the CHILD role. If the user already exists in the parent site, he
     * will not be added.
     * 
	 * @param parentSiteId
	 * @param childSiteId
	 */
	public void addUsersWithChildRole (String parentSiteId, String childSiteId);
	
	
	/**
	 * @param parentSiteId
	 */
	public void removeUsersWithChildRole(List<CORelation> ancestors);
}

