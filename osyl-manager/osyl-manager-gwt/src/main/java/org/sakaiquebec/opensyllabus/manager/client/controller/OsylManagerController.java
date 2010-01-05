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

import org.sakaiquebec.opensyllabus.manager.client.controller.event.FireOsylManagerEvents;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;

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

    private String siteId;

    public static final String WORK_FOLDER_NAME = "work";

    public static final int STATE_CREATION_FORM = 0;

    public static final int STATE_UPLOAD_FORM = 1;

    public static final int STATE_FILE_DOWNLOAD = 2;

    public static final int STATE_FINISH = 99;

    private int state = OsylManagerController.STATE_CREATION_FORM;

    private List<OsylManagerEventHandler> siteCreationHandlersList =
	    new ArrayList<OsylManagerEventHandler>();

    private Map<String, String> osylSitesMap;

    private Map<String, String> coursesMap;

    private String osylPackageUrl;

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
	OsylManagerRPCController.getInstance().getOsylSitesMap(this);
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

    /**
     * Set the siteID
     * 
     * @param siteId
     */
    public void setSiteId(String siteId) {
	this.siteId = siteId;
    }

    public int getState() {
	return state;
    }

    public void setState(int state) {
	this.state = state;
    }

    public String getOsylPackageUrl() {
	return osylPackageUrl;
    }

    public void setOsylPackageUrl(String osylPackageUrl) {
	this.osylPackageUrl = osylPackageUrl;
    }

    /**
     * @return the id of the site
     */
    public String getSiteId() {
	return siteId;
    }

    public Map<String, String> getOsylSitesMap() {
	return osylSitesMap;
    }

    public void setOsylSitesMap(Map<String, String> osylSitesMap) {
	this.osylSitesMap = osylSitesMap;
    }

    public void setCoursesMap(Map<String, String> coursesMap) {
    	this.coursesMap = coursesMap ;
        }

    //SERVER CALLS
    /**
     * Create site with the given name
     * 
     * @param name
     */
    public void createSite(String name, String configRef, String lang) {
	OsylManagerRPCController.getInstance().createSite(this, name, configRef, lang);
    }
    
    
    public void getOsylConfigs(AsyncCallback<Map<String, String>> callback){
	OsylManagerRPCController.getInstance().getOsylConfigs(callback);
    }

    /**
     * read and create a CO given the url of an xml file on the server
     * 
     * @param url
     */
    public void readXML(String url) {
	OsylManagerRPCController.getInstance().readXML(this, url, siteId);
    }

    /**
     * read and create a CO (with attach documents) given the url of an zip file
     * on the server
     * 
     * @param url
     */
    public void readZip(String url) {
	OsylManagerRPCController.getInstance().readZip(this, url, siteId);
    }

    public void getOsylPackage(String siteId) {
	OsylManagerRPCController.getInstance().getOsylPackage(this, siteId);
    }

    /**
     * Callback for read (zip or xml) functions
     */
    public void readCB() {
	state = OsylManagerController.STATE_FINISH;
	notifySiteCreationEventHandler();
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
	    setSiteId(id);
	    state = OsylManagerController.STATE_UPLOAD_FORM;
	    notifySiteCreationEventHandler();
	}
    }

    public void getOsylPackageCB(String url) {
	if (url == null) {
	    Window.alert(getMessages().unableToExportCO());
	} else {
	    this.setOsylPackageUrl(url);
	    state = OsylManagerController.STATE_FILE_DOWNLOAD;
	    notifySiteCreationEventHandler();
	}
    }

    //
    /**
     * {@inheritDoc}
     */
    public void addEventHandler(OsylManagerEventHandler handler) {
	siteCreationHandlersList.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEventHandler(OsylManagerEventHandler handler) {
	siteCreationHandlersList.remove(handler);
    }

    private void notifySiteCreationEventHandler() {
	for (Iterator<OsylManagerEventHandler> iter =
		siteCreationHandlersList.iterator(); iter.hasNext();) {
	    OsylManagerEventHandler siteCreationEventHandler =
		    (OsylManagerEventHandler) iter.next();
	    siteCreationEventHandler.onOsylManagerEvent();
	}
    }
    
    public void getParent(String siteId,AsyncCallback<String> callback){
	OsylManagerRPCController.getInstance().getParent(siteId,callback);
    }
    
    public void getOsylSites(String siteId,AsyncCallback<Map<String,String>> callback){
	OsylManagerRPCController.getInstance().getOsylSites(siteId,callback);
    }
    
    public void associate(String siteId, String parentId, AsyncCallback<Void> callback){
	OsylManagerRPCController.getInstance().associate(siteId, parentId, callback);
    }
    
    public void dissociate(String siteId, String parentId, AsyncCallback<Void> callback){
	OsylManagerRPCController.getInstance().dissociate(siteId, parentId, callback);
    }

    public void associateToCM (String courseSectionId, String siteId, AsyncCallback<Boolean> callback){
    OsylManagerRPCController.getInstance().associateToCM(courseSectionId, siteId, callback);	
    }
    
    public Map<String, String> getCMCourses(){
    	return coursesMap;
    }
    
    public void getCMCourses(AsyncCallback<Map<String,String>> callback){
    	OsylManagerRPCController.getInstance().getCMCourses(callback);
    }
}
