/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.portal.client.controller;

import java.util.List;
import java.util.MissingResourceException;

import org.sakaiquebec.opensyllabus.portal.client.OsylPortalEntryPoint;
import org.sakaiquebec.opensyllabus.portal.client.message.OsylPortalMessages;
import org.sakaiquebec.opensyllabus.portal.client.view.AbstractPortalView;
import org.sakaiquebec.opensyllabus.portal.client.view.CoursesPage;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class PortalController {

    private static PortalController _instance;

    private OsylPortalEntryPoint osylPortalEntryPoint;

    private OsylPortalMessages messages = (OsylPortalMessages) GWT
	    .create(OsylPortalMessages.class);

    public static String RESPONSIBLE_PREFIX = "responsible.";

    public static String ACAD_CAREER_PREFIX = "acad_career.";

    public static String ABBREVIATION_SUFFIX = "_abb";

    public static PortalController getInstance() {
	if (_instance == null) {
	    _instance = new PortalController();
	}
	return _instance;
    }

    private PortalController() {
	super();
    }

    public void setEntryPoint(OsylPortalEntryPoint osylPortalEntryPoint2) {
	this.osylPortalEntryPoint = osylPortalEntryPoint2;
    }

    public void setView(AbstractPortalView view) {
	osylPortalEntryPoint.setView(view);
    }

    public String getMessage(String key) {
	try {
	    return messages.getString(key.replace(".", "_"));
	} catch (MissingResourceException e) {
	    return "Missing key: " + key;
	}
    }

    public void createCourseViewForAcadCareer(final String acadCareer) {
	AsyncCallback<List<CODirectorySite>> callback =
		new AsyncCallback<List<CODirectorySite>>() {

		    public void onFailure(Throwable caught) {
		    }

		    public void onSuccess(List<CODirectorySite> result) {
			setView(new CoursesPage(
				ACAD_CAREER_PREFIX + acadCareer, result));

		    }
		};
	getCoursesForAcadCareer(acadCareer, callback);
    }

    public void createCourseViewForResponsible(final String responsible) {
	AsyncCallback<List<CODirectorySite>> callback =
		new AsyncCallback<List<CODirectorySite>>() {

		    public void onFailure(Throwable caught) {
		    }

		    public void onSuccess(List<CODirectorySite> result) {
			setView(new CoursesPage(RESPONSIBLE_PREFIX
				+ responsible, result));

		    }
		};
	getCoursesForResponsible(responsible, callback);
    }

    /***************************** RPC CALLS *********************************/
    public void getCoursesForAcadCareer(String acadCareer,
	    AsyncCallback<List<CODirectorySite>> callback) {
	PortalRpcController.getInstance().getCoursesForAcadCareer(acadCareer,
		callback);
    }

    public void getCoursesForResponsible(String responsible,
	    AsyncCallback<List<CODirectorySite>> callback) {
	PortalRpcController.getInstance().getCoursesForResponsible(responsible,
		callback);
    }

    public void getDescription(String siteId, AsyncCallback<String> callback) {
	PortalRpcController.getInstance().getDescription(siteId, callback);
    }

    public void getCODirectorySite(String siteId,
	    AsyncCallback<CODirectorySite> callback) {
	PortalRpcController.getInstance().getCODirectorySite(siteId, callback);
    }

    public void getAccessForSiteId(String siteId, AsyncCallback<String> callback) {
	PortalRpcController.getInstance().getAccessForSiteId(siteId, callback);
    }
    /************************** END RPC CALLS *******************************/

}
