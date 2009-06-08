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

import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtServiceAsync;

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

    private static String QUALIFIED_NAME =
	    "org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint/";

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
    public void createSite(OsylManagerController osylManagerController,
	    String title) {
	final OsylManagerController caller = osylManagerController;
	// We first create a call-back for this method call
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String serverResponse) {
		caller.siteCreatedCB(serverResponse);
	    }

	    public void onFailure(Throwable error) {
		Window.alert(caller.getMessages().rpcFailure());
	    }
	};
	serviceProxy.createSite(title, callback);
    }

    /**
     * Read the xml file and create CO
     * 
     * @param osylManagerController
     * @param url
     * @param siteId
     */
    public void readXML(OsylManagerController osylManagerController,
	    String url, String siteId) {
	final OsylManagerController caller = osylManagerController;
	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	    public void onSuccess(Void serverResponse) {
		caller.readCB();
	    }

	    public void onFailure(Throwable error) {
		Window.alert(caller.getMessages().rpcFailure());
	    }
	};
	serviceProxy.readXML(url, siteId, callback);
    }

    /**
     * Read the zip file and create CO (and ressource)
     * 
     * @param osylManagerController
     * @param url
     * @param siteId
     */
    public void readZip(OsylManagerController osylManagerController,
	    String url, String siteId) {
	final OsylManagerController caller = osylManagerController;
	// We first create a call-back for this method call
	AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	    public void onSuccess(Void serverResponse) {
		caller.readCB();
	    }

	    public void onFailure(Throwable error) {
		Window.alert(caller.getMessages().rpcFailure());
	    }
	};
	serviceProxy.readZip(url, siteId, callback);
    }

    /**
     * Get Map<id,title> o site with osyl tool;
     * 
     * @param osylManagerController
     */
    public void getOsylSitesMap(OsylManagerController osylManagerController) {
	final OsylManagerController caller = osylManagerController;
	AsyncCallback<Map<String, String>> callback =
		new AsyncCallback<Map<String, String>>() {
		    public void onSuccess(Map<String, String> serverResponse) {
			caller.setOsylSitesMap(serverResponse);
		    }

		    public void onFailure(Throwable error) {
			Window.alert(caller.getMessages().rpcFailure());
		    }
		};
	serviceProxy.getOsylSitesMap(callback);
    }

    public void getOsylPackage(OsylManagerController osylManagerController,
	    String siteId) {
	final OsylManagerController caller = osylManagerController;
	AsyncCallback<String> callback = new AsyncCallback<String>() {
	    public void onSuccess(String fileUrl) {
		caller.getOsylPackageCB(fileUrl);
	    }

	    public void onFailure(Throwable error) {
		Window.alert(caller.getMessages().rpcFailure());
	    }
	};
	serviceProxy.getOsylPackage(siteId, callback);
    }

    public void getParent(String siteId, AsyncCallback<String> callback) {
	serviceProxy.getParent(siteId, callback);
    }

    public void getOsylSites(String siteId,
	    AsyncCallback<Map<String, String>> callback) {
	serviceProxy.getOsylSites(siteId, callback);
    }

    public void associate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	serviceProxy.associate(siteId, parentId, callback);
    }

    public void dissociate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	serviceProxy.dissociate(siteId, parentId, callback);
    }

}
