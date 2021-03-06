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

import java.util.Map;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

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
     * Returns the CourseOutline of the current context.
     * 
     * @param callback the callback to return the CourseOutline POJO
     *            corresponding to the current context.
     */
    public void getSerializedCourseOutline(String siteId,
	    AsyncCallback<COSerialized> callback);

    /**
     * Saves the CourseOutline specified. The ID is returned. This is useful if
     * this instance has never been saved before (i.e.: its ID is -1). In this
     * case, it is the responsibility of the client application to keep track of
     * this new ID, notably to save it again at a later time.
     * 
     * @param co the serialized course outline
     * @param callback the callback to return the CourseOutline ID
     */
    public void updateSerializedCourseOutline(COSerialized co,
	    AsyncCallback<Boolean> callback);

    /**
     * Publishes the CourseOutline whose ID is specified. It must have been
     * saved previously. Throws an exception if any error occurs, returns
     * otherwise.
     *
     * @param nonce
     */
    public void publishCourseOutline(
            String nonce, AsyncCallback<Vector<Map<String, String>>> callback);

    /**
     * Returns the Published CourseOutline for the group specified in parameter.
     * It must have been published previously.
     * 
     * @param accessType
     * @param callback
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
     * @param resourceId
     * @param permission
     */
    public void applyPermissions(String resourceId, String permission,
	    AsyncCallback<Void> callback);

    /**
     * Returns the default {@link COConfigSerialized} for current context.
     * 
     * @param callback the callback to return {@link COConfigSerialized}
     */
    public void getSerializedConfig(String siteId,
	    AsyncCallback<COConfigSerialized> callback);

    public void getExistingEntities(String siteId,
	    AsyncCallback<SakaiEntities> callback);

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
    public void checkSitesRelation(String resourceURI,
	    AsyncCallback<Void> callback);

    /**
     * Make an xsl transformation of the specified xml for the specified group
     * 
     * @param xml
     * @param group
     * @param callback will return the result of the transformation
     */
    public void transformXmlForGroup(String xml, String group,
	    AsyncCallback<String> callback);

    /**
     * Method used to release lock that the current user have on the current
     * site course outline
     */
    public void releaseLock(AsyncCallback<Void> callback);

    /**
     * Method used to create a pdf for the edition version of the CO
     * 
     * @param printEditionVersionCallback
     */
    public void createPrintableEditionVersion(
	    AsyncCallback<Void> printEditionVersionCallback);

    /**
     * Method used to add an announcement upon publishing a course outline.
     * 
     * @param siteId
     * @param subject
     * @param body
     * @param callback
     */
    public void notifyOnPublish(String siteId, String subject, String body,
	    AsyncCallback<Void> callback);

    /**
     * Send an event to the server
     * 
     * @param eventType
     * @param resource
     * @param voidCallback
     */
    public void sendEvent(String eventType, String resource,
	    AsyncCallback<Void> voidCallback);
}
