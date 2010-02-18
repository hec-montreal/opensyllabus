package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylHierarchicalCourseOutlineJob;

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
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylHierarchicalCourseOutlineJobImpl implements OsylHierarchicalCourseOutlineJob {

	/**
	 * The user service injected by the Spring
	 */
	private UserDirectoryService userDirService;

	/**
	 * Sets the <code>UserDirectoryService</code> needed to create the site in
	 * the init() method.
	 * 
	 * @param userDirService
	 */
	public void setUserDirService(UserDirectoryService userDirService) {
		this.userDirService = userDirService;
	}

	public void adduser() {
	}

	public String getRealmId() {
	    return null;
	}

}
