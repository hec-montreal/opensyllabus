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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.portal.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiquebec.opensyllabus.portal.api.OsylPortalService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylPortalServiceImpl implements OsylPortalService {

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(OsylPortalServiceImpl.class);
    
    private CourseManagementService courseManagementService;
    
    public void setCourseManagementService(CourseManagementService courseManagementService) {
	this.courseManagementService = courseManagementService;
    }

    /**
     * Init method called right after Spring injection.
     */
    public void init() {

    }

    @Override
    public Map<String, String> getCoursesForAcadCareer(String acadCareer) {
	return null;
    }

    @Override
    public Map<String, String> getCoursesForResponsible(String responsible) {
	return null;
    }

    @Override
    public List<String> getAllResponsibles() {
	return courseManagementService.getSectionCategories();
    }

}
