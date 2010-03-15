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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerRPCController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.FireOsylManagerEvents;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.imageBundle.ManagerImageBundleInterface;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;
import org.sakaiquebec.opensyllabus.shared.model.COSite;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerController implements FireOsylManagerEvents {

    private static OsylManagerController _instance = null;

    private Messages messages = GWT.create(Messages.class);

    public static final String WORK_FOLDER_NAME = "work";

    private List<OsylManagerEventHandler> managerEventHandlersList =
	    new ArrayList<OsylManagerEventHandler>();

    private Map<String, String> osylSitesMap;

    private Map<String, String> coursesMap;

    private ManagerImageBundleInterface imageBundle =
	    (ManagerImageBundleInterface) GWT
		    .create(ManagerImageBundleInterface.class);
    
    private List<COSite> selectSites = new ArrayList<COSite>();

    /**
     * @return instance of this class
     */
    public static OsylManagerController getInstance() {
	if (_instance == null)
	    _instance = new OsylManagerController();
	return _instance;
    }

    /**
     * Private constructor use to implements singleton
     */
    private OsylManagerController() {
    }

    /**
     * Checks if the application is running in hosted mode or with the backend.
     * 
     * @return true if in hosted mode, false if not.
     */
    public boolean isInHostedMode() {
	String url = GWT.getHostPageBaseURL();

	if (url.startsWith("http://localhost:8888/")
		&& (url
			.indexOf("org.sakaiquebec.opensyllabus.manager.OsylManagerEntryPoint") != -1))
	    return true;
	else
	    return false;
    }

    /**
     * To get Internationalized messages
     * 
     * @return bundle of messages
     */
    public Messages getMessages() {
	return messages;
    }

    public void setCoursesMap(Map<String, String> coursesMap) {
	this.coursesMap = coursesMap;
    }

    public ManagerImageBundleInterface getImageBundle() {
	return imageBundle;
    }

    public void setImageBundle(ManagerImageBundleInterface imageBundle) {
	this.imageBundle = imageBundle;
    }
    
    public void setSelectSites(List<COSite> selectSiteIDs) {
	this.selectSites = selectSiteIDs;
	OsylManagerEvent event = new OsylManagerEvent(null, OsylManagerEvent.SITES_SELECTION_EVENT);
	notifyManagerEventHandler(event);
    }

    public List<COSite> getSelectSites() {
	return selectSites;
    }

    // SERVER CALLS
    /**
     * Create site with the given name
     * 
     * @param name
     */
    public void createSite(String name, String configRef, String lang) {
	OsylManagerRPCController.getInstance().createSite(this, name,
		configRef, lang);
    }

    public void getOsylConfigs(AsyncCallback<Map<String, String>> callback) {
	OsylManagerRPCController.getInstance().getOsylConfigs(callback);
    }

    /**
     * read and create a CO (with attach documents) given the url of an zip file
     * on the server
     * 
     * @param url
     */
    public void importData(String url,String siteId) {
	OsylManagerRPCController.getInstance().importData(this, url, siteId);
    }

    public void getOsylPackage(String siteId, AsyncCallback<String> cb) {
	OsylManagerRPCController.getInstance().getOsylPackage(this, siteId,cb);
    }

    /**
     * Callback for read (zip or xml) functions
     */
    public void readCB() {
	notifyManagerEventHandler(new OsylManagerEvent(null, OsylManagerEvent.SITE_IMPORT_EVENT));
    }

    /**
     * CallBack for creation of a site
     * 
     * @param id the id of the new site
     */
    public void siteCreatedCB(String id) {
	if (id == null) {
	    Window.alert(messages.siteNotCreated());
	} else {
	    notifyManagerEventHandler(new OsylManagerEvent(id, OsylManagerEvent.SITE_CREATION_EVENT));
	}
    }

    //
    /**
     * {@inheritDoc}
     */
    public void addEventHandler(OsylManagerEventHandler handler) {
	managerEventHandlersList.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEventHandler(OsylManagerEventHandler handler) {
	managerEventHandlersList.remove(handler);
    }

    public void notifyManagerEventHandler(OsylManagerEvent event) {
	for (Iterator<OsylManagerEventHandler> iter =
		managerEventHandlersList.iterator(); iter.hasNext();) {
	    OsylManagerEventHandler osylManagerEventHandler =
		    (OsylManagerEventHandler) iter.next();
	    osylManagerEventHandler.onOsylManagerEvent(event);
	}
    }

    public void getParent(String siteId, AsyncCallback<String> callback) {
	OsylManagerRPCController.getInstance().getParent(siteId, callback);
    }

    public void getOsylSites(List<String> siteIds,
	    AsyncCallback<Map<String, String>> callback) {
	OsylManagerRPCController.getInstance().getOsylSites(siteIds, callback);
    }

    public void associate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().associate(siteId, parentId,
		callback);
    }

    public void dissociate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().dissociate(siteId, parentId,
		callback);
    }

    public void associateToCM(String courseSectionId, String siteId,
	    AsyncCallback<Boolean> callback) {
	OsylManagerRPCController.getInstance().associateToCM(courseSectionId,
		siteId, callback);
    }

    public Map<String, String> getCMCourses() {
	return coursesMap;
    }

    public void getCoAndSiteInfo(String siteId, AsyncCallback<COSite> callback){
	 OsylManagerRPCController.getInstance().getCoAndSiteInfo(siteId, callback);
    }

    public void getCoAndSiteInfo(AsyncCallback<List<COSite>> callback){
	OsylManagerRPCController.getInstance().getCoAndSiteInfo(callback);
    }

    public void getCMCourses(AsyncCallback<Map<String, String>> callback) {
	OsylManagerRPCController.getInstance().getCMCourses(callback);
    }
}
