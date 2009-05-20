/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.shared.api;

/**
 * This interface provides security and context-related methods used by the DAO
 * and services.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface SecurityInterface {

    /**
     * On site access security value.
     */
    public static final String SECURITY_ACCESS_ONSITE = "onsite";

    /**
     * Public access security value.
     */
    public static final String SECURITY_ACCESS_PUBLIC = "public";
    
    /**
     * Attendee access security value.
     */
    public static final String SECURITY_ACCESS_ATTENDEE = "attendee";

    /**
     * Project access role security value.
     */
    public static final String SECURITY_ROLE_PROJECT_ACCESS = "access";

    /**
     * Course access role security value.
     */
    public static final String SECURITY_ROLE_COURSE_ACCESS = "Student";

    /**
     * Project maintain role security value.
     */
    public static final String SECURITY_ROLE_PROJECT_MAINTAIN = "maintain";

    /**
     * Course maintain role security value.
     */
    public static final String SECURITY_ROLE_COURSE_MAINTAIN = "Instructor";

    /**
     * Returns the actual site id of this context.
     * 
     * @return a String of the current site id.
     */
    public String getCurrentSiteId() throws Exception;

    /**
     * Get the locale of the actual session.
     * 
     * @return a String representation of the locale.
     */
    public String getCurrentLocale() throws Exception;

    /**
     * Returns the user role for current user.
     * 
     * @return String userRole
     */
    public String getCurrentUserRole();

    /**
     * Returns the group for current user in the current site. The highest
     * privileges will be returned.
     * 
     * @return String the user group
     */
//    public String getCurrentUserGroup();

    /**
     * Applies public access for the specified resource. 
     * If something prevents the call to complete successfully an
     * exception is thrown.
     * 
     * @param String resourceId
     * @param String permission
     */
    public void applyPermissions(String resourceId, String permission) throws Exception;

    /**
     * Applies public access for the specified resource and 
     * the specified site. If something prevents the call to
     * complete successfully an exception is thrown.
     * 
     * @param String resourceId
     * @param String siteId
     * @param String permission
     */
    public void applyPermissions(String siteId, String resourceId, String permission) throws Exception;

    public void applyDirectoryPermissions(String directoryId) throws Exception;
    
    /**
     * Returns the value of the configuration property linked to the site
     * representing the course outline and used to choose the skin and message
     * bundle
     * 
     * @param
     * @return String value of the configuration
     */
    public String getSiteConfigProperty(String siteId);
}
