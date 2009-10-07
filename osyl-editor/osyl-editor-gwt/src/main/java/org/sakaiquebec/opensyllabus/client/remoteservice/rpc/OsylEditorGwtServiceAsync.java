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

package org.sakaiquebec.opensyllabus.client.remoteservice.rpc;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * OsylEditorGwtService defines the RPC (Remote Procedure Call) interface
 * between the GWT client application and the Sakai server.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface OsylEditorGwtServiceAsync {

	/**
	 * Initialize the tool.
	 */
	public void initTool(AsyncCallback<Void> callback);

	/**
	 * Returns the CourseOutline whose ID is specified.
	 * 
	 * @param String ID
	 * @param callback the callback to return the CourseOutline POJO
	 *            corresponding to the specified ID
	 * @throws Exception
	 */
	public void getSerializedCourseOutline(String id,
			AsyncCallback<COSerialized> callback);

	/**
	 * Returns the CourseOutline of the current context.
	 * 
	 * @param callback the callback to return the CourseOutline POJO
	 *            corresponding to the current context.
	 */
	public void getSerializedCourseOutline(AsyncCallback<COSerialized> callback);

	/**
	 * Saves the CourseOutline specified. The ID is returned. This is useful if
	 * this instance has never been saved before (i.e.: its ID is -1). In this
	 * case, it is the responsibility of the client application to keep track of
	 * this new ID, notably to save it again at a later time.
	 * 
	 * @param COSerialized POJO
	 * @param callback the callback to return the CourseOutline ID
	 */
	public void updateSerializedCourseOutline(COSerialized co,
			AsyncCallback<String> callback);

	/**
	 * Publishes the CourseOutline whose ID is specified. It must have been
	 * saved previously. Throws an exception if any error occurs, returns
	 * otherwise.
	 * 
	 * @param String id
	 */
	public void publishCourseOutline(AsyncCallback<Void> callback);

	/**
	 * Returns the Published CourseOutline for the group specified in parameter.
	 * It must have been published previously.
	 * 
	 * @param String groupName
	 */
	public void getSerializedPublishedCourseOutlineForAccessType(
			String accessType, AsyncCallback<COSerialized> callback);

	/**
	 * Check if the CO of the current context as been published
	 * 
	 * @param callback the callback to return true if the CO has been published
	 *            at least one time;
	 */
	public void hasBeenPublished(AsyncCallback<Boolean> callback);

	/**
	 * Returns the user role for current user.
	 * 
	 * @param callback the callback to return String userRole
	 */
	public void getCurrentUserRole(AsyncCallback<String> callback);

	/**
	 * Applies the specified permission for the specified resource. If something
	 * prevents the call to complete successfully an exception is thrown. TODO:
	 * check if the description is OK, I'm not sure I understand this one well.
	 * 
	 * @param String resourceId
	 * @param String permission
	 */
	public void applyPermissions(String resourceId, String permission,
			AsyncCallback<Void> callback);

	/**
	 * Returns the default {@link COConfigSerialized} for current context.
	 * 
	 * @param callback the callback to return {@link COConfigSerialized}
	 */
	public void getSerializedConfig(AsyncCallback<COConfigSerialized> callback);

	/**
	 * Creates or updates an assignment for this context.
	 * 
	 * @param callback the callback to return the assignment ID
	 */
	public void createOrUpdateAssignment(String assignmentId, String title,
			String instructions, int openYear, int openMonth, int openDay,
			int openHour, int openMinute, int closeYear, int closeMonth,
			int closeDay, int closeHour, int closeMinute, int percentage,
			AsyncCallback<String> callback);

	/**
	 * Creates or updates an assignment for this context.
	 * 
	 * @param callback the callback to return the assignment ID
	 */
	public void createOrUpdateAssignment(String assignmentId, String title,
			AsyncCallback<String> callback);

	/**
	 * Delete an existing assignment.
	 */
	public void removeAssignment(String assignmentId,
			AsyncCallback<Void> callback);

	/**
	 * Delete a citation from the course outline citation list
	 */
	public void removeCitation(String citationId, AsyncCallback<Void> callback);

	/**
	 * Add or updates a citation in the course outline citation list
	 */
	public void createOrUpdateCitation(String citationListId, String citation,
			String author, String type, String isbnIssn, String link,
			AsyncCallback<String> callback);

	/**
	 * @param callback
	 * @return
	 */
	public void createTemporaryCitationList(AsyncCallback<String> callback);

	/**
	 * Pings the server to keep user session alive as long as its client
	 * interface is running.
	 */
	public void ping(AsyncCallback<Void> callback);

	/**
	 * Get the xsl associated with the particular group
	 * 
	 * @param group
	 * @param callback
	 */
	public void getXslForGroup(String group, AsyncCallback<String> callback);

	/**
	 * @return a ResourcesLicencingInfo object which contains informations about
	 *         Licencing on resources
	 */
	public void getResourceLicenceInfo(
			AsyncCallback<ResourcesLicencingInfo> callback);
	
    /**
	 * Checks if the current site has a relation (child - parent) with the site
	 * containing the resource. If it is the case, we allow the site to access
	 * to the resource
	 * 
	 * @param resourceURI
	 * @return
	 */
	public void checkSitesRelation(String resourceURI, AsyncCallback<Void> callback);

}
