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

package org.sakaiquebec.opensyllabus.portal.api;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

/**
 * OsylAdminService is a administrative service used to define properties and
 * configurations related to the use of OpenSyllabus. From here we can access
 * most administrative services that are to be defined. For now we only have the
 * function allowing to create a number of users.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylPortalService {
    
    public final static String SUMMER = "E";

    public final static String WINTER = "H";

    public final static String FALL = "A";

    public List<CODirectorySite> getCoursesForAcadCareer(String acadCareer);

    public List<CODirectorySite> getCoursesForResponsible(String responsible);
    
    public List<CODirectorySite> getCoursesForFields(String courseNumber,
	    String courseTitle, String instructor, String program,
	    String responsible, String trimester);
    
    public String getDescription(String siteId);
    
    public CODirectorySite getCODirectorySite(String courseNumber);

}
