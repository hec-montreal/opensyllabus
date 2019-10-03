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

package org.sakaiquebec.opensyllabus.manager.client.controller;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtServiceAsync;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerRPCController {

    private static OsylManagerRPCController rpcController = null;

    private static String QUALIFIED_NAME = "OsylManagerEntryPoint/";

    final OsylManagerGwtServiceAsync serviceProxy;

    public static OsylManagerRPCController getInstance() {
	if (rpcController == null)
	    rpcController = new OsylManagerRPCController();
	return rpcController;
    }

    private OsylManagerRPCController() {
	serviceProxy =
		(OsylManagerGwtServiceAsync) GWT
			.create(OsylManagerGwtService.class);
	ServiceDefTarget pointService = (ServiceDefTarget) serviceProxy;
	String url = GWT.getModuleBaseURL();
	final String cleanUrl =
		url.substring(0, url.length() - QUALIFIED_NAME.length());
	pointService.setServiceEntryPoint(cleanUrl + "OsylManagerGwtService");
    }

    /**
     * Create site with given siteID, config and language.
     * 
     * @param osylManagerController
     * @param title
     * @param configRef
     * @param lang
     */
    public void createSite(final OsylManagerController osylManagerController,
	    String title, String configRef, String lang, String templateId) {
	final OsylManagerController caller = osylManagerController;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String serverResponse) {
		caller.siteCreatedCB(serverResponse);
	    }

	    public void onFailure(Throwable error) {
		if (error instanceof OsylPermissionException) {
		    Window.alert(osylManagerController.getMessages()
			    .siteNotCreated()
			    + osylManagerController.getMessages()
				    .permission_exception());
		}
		Window.alert(osylManagerController.getMessages()
			.siteNotCreated());
	    }
	};
	serviceProxy.createSite(title, configRef, lang, templateId, callback);
    }

    public void getOsylConfigs(AsyncCallback<Map<String, String>> callback) {

	serviceProxy.getOsylConfigs(callback);
    }

    /**
     * Read the zip file and create CO (and ressource)
     * 
     * @param osylManagerController
     * @param url
     * @param siteId
     */
    public void importData(OsylManagerController osylManagerController,
	    String url, String siteId, AsyncCallback<Void> callback) {
	serviceProxy.importData(url, siteId, callback);
    }

    public void getOsylPackage(OsylManagerController osylManagerController,
	    String siteId, AsyncCallback<String> callback) {
	serviceProxy.getOsylPackage(siteId, callback);
    }

    public void getParent(String siteId, AsyncCallback<String> callback) {
	serviceProxy.getParent(siteId, callback);
    }

    public void getOsylSites(List<String> siteIds, String searchTerm,
	    AsyncCallback<Map<String, String>> callback) {
	serviceProxy.getOsylSites(siteIds, searchTerm, callback);
    }

    public void associate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	serviceProxy.associate(siteId, parentId, callback);
    }

    public void dissociate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	serviceProxy.dissociate(siteId, parentId, callback);
    }

    public void associateToCM(String courseSectionId, String siteId,
	    AsyncCallback<Void> callback) {
	serviceProxy.associateToCM(courseSectionId, siteId, callback);
    }

    public void dissociateFromCM(String siteId, AsyncCallback<Void> callback) {
	serviceProxy.dissociateFromCM(siteId, callback);
    }

    public void getCMCourses(String startsWith,
	    AsyncCallback<List<CMCourse>> callback) {
	serviceProxy.getCMCourses(startsWith, callback);
    }

    public void getCoAndSiteInfo(String siteId, String searchTerm,
	    String academicSession, AsyncCallback<COSite> callback) {
	serviceProxy.getCoAndSiteInfo(siteId, searchTerm, academicSession,
		callback);
    }

    public void getAllCoAndSiteInfo(String searchTerm, String academicSession,
	    AsyncCallback<java.util.List<COSite>> callback) {
	serviceProxy.getAllCoAndSiteInfo(searchTerm, academicSession, callback);
    }

    public void getAllCoAndSiteInfo(String searchTerm, String academicSession, boolean withFrozenSites, 
		AsyncCallback<java.util.List<COSite>> callback) {
	serviceProxy.getAllCoAndSiteInfo(searchTerm, academicSession, withFrozenSites, callback);
    }
    
    public void publish(String siteId,
	    AsyncCallback<Vector<Map<String, String>>> callback) {
        String nonce = generateNonce(siteId);
	serviceProxy.publish(siteId, nonce, callback);
    }

    private String generateNonce(String siteId) {
        return System.currentTimeMillis() + siteId;
    }


    public void getAcademicSessions(
	    AsyncCallback<List<CMAcademicSession>> callback) {
	serviceProxy.getAcademicSessions(callback);
    }

    public void deleteSite(String siteId, AsyncCallback<Void> deleteAsynCallBack) {
	serviceProxy.deleteSite(siteId, deleteAsynCallBack);
    }

    public void copySite(String siteFrom, String siteTo,
	    AsyncCallback<String> copyAsyncCallback) {
	serviceProxy.copySite(siteFrom, siteTo, copyAsyncCallback);
    }

    public void unpublish(String siteId,
	    AsyncCallback<Void> unpublishAsyncCallback) {
	serviceProxy.unpublish(siteId, unpublishAsyncCallback);
    }

    public void getPermissions(AsyncCallback<Map<String,Boolean>> permissionCallback) {
	serviceProxy.getPermissions(permissionCallback);
    }
    
    public void isSuperUser(AsyncCallback<Boolean> superUserCallback){
	serviceProxy.isSuperUser(superUserCallback);
    }
}
