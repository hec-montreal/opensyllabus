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

import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * OsylEditorGwtService defines the RPC (Remote Procedure Call) interface
 * between the GWT client application and the Sakai server.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface OsylEditorGwtService extends RemoteService {

    /**
     * Initialize the tool.
     */
    public void initTool() throws Exception;

    /**
     * Returns the CourseOutline of the current context.
     * 
     * @return the CourseOutline POJO corresponding to the current context.
     */
    public COSerialized getSerializedCourseOutline(String siteId)
	    throws Exception;

    /**
     * Saves the CourseOutline specified. The ID is returned. This is useful if
     * this instance has never been saved before (i.e.: its ID is -1). In this
     * case, it is the responsibility of the client application to keep track of
     * this new ID, notably to save it again at a later time.
     * 
     * @param co the serialized course outline
     * @return the CourseOutline ID
     * @throws Exception
     */
    public boolean updateSerializedCourseOutline(COSerialized co)
	    throws Exception;

    /**
     * Publishes the CourseOutline whose ID is specified. It must have been
     * saved previously. Throws an exception if any error occurs, returns
     * otherwise.
     * 
     * @param nonce single use identifier to avoid cloned publish request
     */
    public Vector<Map<String, String>> publishCourseOutline(String nonce) throws Exception,
	    FusionException;

    /**
     * Returns the Published CourseOutline for the access type specified in
     * parameter. It must have been published previously.
     * 
     * @param accessType
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType) throws Exception;

    /**
     * Check if the CO of the current context as been published
     * 
     * @return true if the CO has been published at least one time;
     */
    public boolean hasBeenPublished() throws Exception;

    /**
     * Returns the user role for current user.
     * 
     * @return String userRole
     */
    public String getCurrentUserRole();

    /**
     * Applies permission for the specified resource. If something prevents the
     * call to complete successfully an exception is thrown.
     * 
     * @param resourceId
     * @param permission
     */
    public void applyPermissions(String resourceId, String permission);

    /**
     * Returns the default {@link COConfigSerialized} for current context.
     * 
     * @return {@link COConfigSerialized}
     */
    public COConfigSerialized getSerializedConfig(String siteId)
	    throws Exception;

    public SakaiEntities getExistingEntities(String siteId);

    /**
     * Pings the server to keep user session alive as long as its client
     * interface is running.
     */
    public void ping();

    /**
     * Get the xsl associated with the particular group
     * 
     * @param group
     */
    public String getXslForGroup(String group);

    /**
     * @return a ResourcesLicencingInfo object which contains informations about
     *         Licencing on resources
     */
    public ResourcesLicencingInfo getResourceLicenceInfo();

    /**
     * Checks if the current site has a relation (child - parent) with the site
     * containing the resource. If it is the case, we allow the site to access
     * to the resource
     * 
     * @param resourceURI
     * @return
     */
    public boolean checkSitesRelation(String resourceURI);

    /**
     * Make an xsl transformation of the specified xml for the specified group
     * 
     * @param xml
     * @param group
     * @return the result of the transformation
     */
    public String transformXmlForGroup(String xml, String group)
	    throws Exception;

    /**
     * Method used to release lock that the current user have on the current
     * site course outline
     */
    public void releaseLock();

    /**
     * Method used to create a pdf for the edition version of the CO
     *
     */
    public void createPrintableEditionVersion() throws Exception;

    /**
     * Method used to add an announcement upon publishing a course outline.
     * 
     * @param siteId
     * @param subject
     * @param body
     */
    public void notifyOnPublish(String siteId, String subject, String body)
	    throws Exception;

    /**
     * Send an event to the server
     * 
     * @param eventType
     * @param resource
     */
    public void sendEvent(String eventType, String resource);

}
