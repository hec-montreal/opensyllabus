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

import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtServiceAsync;
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
     * Create site given name and siteID
     * 
     * @param osylManagerController
     * @param title
     * @param siteId
     */
    public void createSite(final OsylManagerController osylManagerController,
	    String title, String configRef, String lang) {
	final OsylManagerController caller = osylManagerController;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String serverResponse) {
		caller.siteCreatedCB(serverResponse);
	    }

	    public void onFailure(Throwable error) {
		Window.alert(osylManagerController.getMessages()
			.siteNotCreated());
	    }
	};
	serviceProxy.createSite(title, configRef, lang,  callback);
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

    public void getOsylSites(List<String> siteIds,
	    AsyncCallback<Map<String, String>> callback) {
	serviceProxy.getOsylSites(siteIds, callback);
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
    
    public void dissociateFromCM(String siteId, AsyncCallback<Void> callback){
	serviceProxy.dissociateFromCM(siteId, callback);
    }

    public void getCMCourses(AsyncCallback<List<CMCourse>> callback) {
	serviceProxy.getCMCourses(callback);
    }
    
    public void getCoAndSiteInfo(String siteId, String searchTerm, AsyncCallback<COSite> callback){
	serviceProxy.getCoAndSiteInfo(siteId, searchTerm, callback);
    }

    public void getAllCoAndSiteInfo(String searchTerm, AsyncCallback< java.util.List<COSite>> callback){
	serviceProxy.getAllCoAndSiteInfo(searchTerm, callback);
    }

    public void publish(String siteId, AsyncCallback<Void> callback) {
	serviceProxy.publish(siteId,callback);
    }

}
