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

package org.sakaiquebec.opensyllabus.common.api;

/**
 * OsylSecurityService defines all calls related to security and context. It
 * should offer a minimal interface to the actual session. If several entry
 * points to session attributes are required, a refactoring may be considered.
 * The servlet or the osylService could then directly use the SessionManager.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface OsylSecurityService {

    /**
     * Project access role value.
     */
    public static final String SECURITY_ROLE_PROJECT_ACCESS = "access";

    /**
     * Project maintain role value.
     */
    public static final String SECURITY_ROLE_PROJECT_MAINTAIN = "maintain";

    /**
     * Course student role value.
     */
    public static final String SECURITY_ROLE_COURSE_STUDENT = "Student";

    /**
     * HelpDesk user role value.
     */
    public static final String SECURITY_ROLE_COURSE_HELPDESK = "helpdesk";

    /**
     * Course instructor role value.
     */
    public static final String SECURITY_ROLE_COURSE_INSTRUCTOR = "Instructor";

    /**
     * Course Coordonator role value
     */
    public static final String SECURITY_ROLE_COURSE_COORDONATOR = "Coordonator";

    /**
     * Course Secretary role value
     */
    public static final String SECURITY_ROLE_COURSE_SECRETARY = "Secretary";

    /**
     * Course general assistant role value.
     */
    public static final String SECURITY_ROLE_COURSE_GENERAL_ASSISTANT =
	    "General Assistant";

    /**
     * Course teaching assistant role value.
     */
    public static final String SECURITY_ROLE_COURSE_TEACHING_ASSISTANT =
	    "Teaching Assistant";

    /**
     * Takes an XML file and strips tags based on a user role
     * 
     * @param inputXml the XML content to strip
     * @param role the role of the user requesting the XML file
     * @return a String containing the parsed XML
     */
    public String filterContent(String inputXml, String role);

    /**
     * Returns true if the current user has the permission to use Opensyllabus
     * editor
     * 
     * @param siteId TODO
     */
    public boolean isAllowedToEdit(String siteId);

    /**
     * Returns the user role for current user.
     * 
     * @return String userRole
     */
    public String getCurrentUserRole();

    /**
     * Sets the current Sakai session active as of now.
     */
    public void setCurrentSessionActive();

    /**
     * Returns the group for current user in the current site. The highest
     * privileges will be returned.
     * 
     * @return String the user group
     */
    // public String getCurrentUserGroup();
    /**
     * Applies public access for the specified resource. If something prevents
     * the call to complete successfully an exception is thrown.
     * 
     * @param String resourceId
     * @param String permission
     */
    public void applyPermissions(String resourceId, String permission)
	    throws Exception;

    /**
     * Applies public access for the specified resource and the specified site.
     * If something prevents the call to complete successfully an exception is
     * thrown.
     * 
     * @param String resourceId
     * @param String siteId
     * @param String permission
     */
    public void applyPermissions(String siteId, String resourceId,
	    String permission) throws Exception;

    public void applyDirectoryPermissions(String directoryId) throws Exception;

    public String getCurrentUserId();

}
