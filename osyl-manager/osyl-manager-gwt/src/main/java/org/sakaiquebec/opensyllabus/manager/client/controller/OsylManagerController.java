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
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is the base class of OsylManager.  The controller manages objects
 * needed by the interface classes and it has access to the server side to get
 * the data needed.
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerController implements FireOsylManagerEvents {

    /**
     * Constant used for the work folder name.
     */
    public static final String WORK_FOLDER_NAME = "work";

    private static OsylManagerController _instance = null;

    private Messages messages = GWT.create(Messages.class);

    private List<OsylManagerEventHandler> managerEventHandlersList =
	    new ArrayList<OsylManagerEventHandler>();

    private Map<String, String> coursesMap;

    private ManagerImageBundleInterface imageBundle =
	    (ManagerImageBundleInterface) GWT
		    .create(ManagerImageBundleInterface.class);

    private List<COSite> selectSites = new ArrayList<COSite>();

    /**
     * Private constructor use to implements singleton
     */
    private OsylManagerController() {
    }

    /**
     * Returns an existing instance of <code>OsylManagerController</code> or
     * instantiates a new one.
     * 
     * @return instance of this class
     */
    public static OsylManagerController getInstance() {
	if (_instance == null)
	    _instance = new OsylManagerController();
	return _instance;
    }

    /**
     * Checks if the application is running in hosted mode or with the backend.
     * 
     * @return true if in hosted mode, false if not.
     */
    public boolean isInHostedMode() {
	String url = GWT.getHostPageBaseURL();
	if (url.startsWith("http://127.0.0.1:8888/"))
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
     * Gives access to the images bundle needed by the user interface objects.
     * @return the imageBundle
     */
    public ManagerImageBundleInterface getImageBundle() {
	return imageBundle;
    }

    /**
     * Sets the images bundle to use.
     * @param imageBundle the imageBundle to be used.
     */
    public void setImageBundle(ManagerImageBundleInterface imageBundle) {
	this.imageBundle = imageBundle;
    }

    /**
     * Returns the list of selected sites in the sites table of OsylManager.
     * @return a list of selected sites
     */
    public List<COSite> getSelectSites() {
	return selectSites;
    }

    /**
     * Sets the new list of selected sites and throw a site selection event.
     * @param selectSiteIDs a list of the selected sites.
     */
    public void setSelectSites(List<COSite> selectSiteIDs) {
	this.selectSites = selectSiteIDs;
	OsylManagerEvent event =
		new OsylManagerEvent(null,
			OsylManagerEvent.SITES_SELECTION_EVENT);
	notifyManagerEventHandler(event);
    }
    
    /**
     * Returns a map of all the Course Management courses
     * @return a courses map
     */
    public Map<String, String> getCMCourses() {
	return coursesMap;
    }

    /**
     * Sets the courses map needed by the user interfaces objects.
     * @param coursesMap a courses map
     */
    public void setCoursesMap(Map<String, String> coursesMap) {
	this.coursesMap = coursesMap;
    }

    /**
     * Adds a site to the selected sites list.
     * @param cosite the site to add.
     */
    public void addSelectedSite(COSite cosite) {
	selectSites.add(cosite);
    }


    // SERVER CALLS
    /**
     * Create site with the given name
     * 
     * @param name the name of the site
     */
    public void createSite(String name, String configRef, String lang) {
	OsylManagerRPCController.getInstance().createSite(this, name,
		configRef, lang);
    }

    /**
     * Retrieves the map of all the osyl configs available
     * @param callback the server callback when the operation is completed
     */
    public void getOsylConfigs(AsyncCallback<Map<String, String>> callback) {
	OsylManagerRPCController.getInstance().getOsylConfigs(callback);
    }

    /**
     * read and create a CO (with attach documents) given the url of an zip file
     * on the server
     * 
     * @param url the url of the zip file
     * @param siteId the site id in which to import the zip file
     */
    public void importData(String url, String siteId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().importData(
		this, url, siteId, callback);
    }

    /**
     * Retrieves the zip file
     * @param siteId
     * @param cb
     */
    public void getOsylPackage(String siteId, AsyncCallback<String> cb) {
	OsylManagerRPCController.getInstance().getOsylPackage(this, siteId, cb);
    }

    /**
     * Callback for read (zip or xml) functions
     */
    public void readCB() {
	notifyManagerEventHandler(new OsylManagerEvent(null,
		OsylManagerEvent.SITE_IMPORT_EVENT));
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
	    notifyManagerEventHandler(new OsylManagerEvent(id,
		    OsylManagerEvent.SITE_CREATION_EVENT));
	}
    }

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

    /**
     * Notifies all the handlers listening to osyl Manager events.
     * @param event the event being thrown
     */
    public void notifyManagerEventHandler(OsylManagerEvent event) {
	for (Iterator<OsylManagerEventHandler> iter =
		managerEventHandlersList.iterator(); iter.hasNext();) {
	    OsylManagerEventHandler osylManagerEventHandler =
		    (OsylManagerEventHandler) iter.next();
	    osylManagerEventHandler.onOsylManagerEvent(event);
	}
    }

    /**
     * Retrieves the parent site of the site with site id.
     * @param siteId the id of the site to find its parent
     * @param callback the callback to contain the server response
     */
    public void getParent(String siteId, AsyncCallback<String> callback) {
	OsylManagerRPCController.getInstance().getParent(siteId, callback);
    }

    /**
     * Retrieves all the sites corresponding of the site ids in the list
     * @param siteIds the list of site ids
     * @param callback the callback to contain server response
     */
    public void getOsylSites(List<String> siteIds,
	    AsyncCallback<Map<String, String>> callback) {
	OsylManagerRPCController.getInstance().getOsylSites(siteIds, callback);
    }

    /**
     * Associates a site to a parent site.
     * @param siteId the site id to associate to a prent site
     * @param parentId the id of the parent site
     * @param callback container of the server response
     */
    public void associate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().associate(siteId, parentId,
		callback);
    }

    /**
     * Dissociates a site from its parent site.
     * @param siteId the id of the site to dissociate from its parent
     * @param parentId the id of the parent site
     * @param callback container of the server response
     */
    public void dissociate(String siteId, String parentId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().dissociate(siteId, parentId,
		callback);
    }

    /**
     * Associates a site to a course management section.
     * @param courseSectionId id of the course management section
     * @param siteId id of the site to associate
     * @param callback container of the server response
     */
    public void associateToCM(String courseSectionId, String siteId,
	    AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().associateToCM(courseSectionId,
		siteId, callback);
    }

    /**
     * Dissociates a site form a course management section.
     * @param siteId id of the site to dissociates
     * @param callback container of the server response
     */
    public void dissociateFromCM(String siteId, AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().dissociateFromCM(siteId,
		callback);
    }

    /**
     * Publishes the course outline of the course site.
     * @param siteId id of the site to publish the course outline
     * @param callback container of the server response
     */
    public void publish(String siteId, AsyncCallback<Void> callback) {
	OsylManagerRPCController.getInstance().publish(siteId, callback);
    }

    /**
     * Retrieves the site and all the information about it.
     * @param siteId id of the site to retrieve
     * @param searchTerm the search term to verify if the corresponding to the
     * id matches it
     * @param callback container of the server response
     */
    public void getCoAndSiteInfo(String siteId, String searchTerm,
	    AsyncCallback<COSite> callback) {
	OsylManagerRPCController.getInstance().getCoAndSiteInfo(siteId,
		searchTerm, callback);
    }

    /**
     * Retrieves all the sites and their information.
     * @param searchTerm the search term entered by the user
     * @param callback container of the server response
     */
    public void getAllCoAndSiteInfo(String searchTerm,
	    AsyncCallback<List<COSite>> callback) {
	OsylManagerRPCController.getInstance().getAllCoAndSiteInfo(searchTerm,
		callback);
    }

    /**
     * Retrieves all the course management courses
     * @param callback container of the server response
     */
    public void getCMCourses(AsyncCallback<List<CMCourse>> callback) {
	OsylManagerRPCController.getInstance().getCMCourses(callback);
    }
}
