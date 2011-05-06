/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;


/**
 * This class is the service giving access to everything about directory sites.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylDirectoryService {

    
    public static final String SITE_TYPE = "directory";
    
    public static final String CONFIG_REF = "directory";
    
    /**
     * Creates a site of type directory. Make sure the siteName respects HEC
     * name conventions : 3-600-96 or 4-900-10A.
     * 
     * @param siteName
     * @return
     */
    public boolean createSite(String siteTitle, CanonicalCourse canCourse) throws Exception;
    
    public boolean createCourseOutline (CourseOffering courseOff, String siteId);
}

