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
import java.util.Map;

import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtService;
import org.sakaiquebec.opensyllabus.portal.client.rpc.OsylPortalGwtServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class PortalRpcController {

    private static PortalRpcController _instance;

    private final OsylPortalGwtServiceAsync serviceProxy;

    private static String QUALIFIED_NAME = "OsylPortalEntryPoint/";

    public static PortalRpcController getInstance() {
	if (_instance == null)
	    _instance = new PortalRpcController();
	return _instance;
    }

    private PortalRpcController() {
	serviceProxy =
		(OsylPortalGwtServiceAsync) GWT
			.create(OsylPortalGwtService.class);
	ServiceDefTarget pointService = (ServiceDefTarget) serviceProxy;
	String url = GWT.getModuleBaseURL();
	final String cleanUrl =
		url.substring(0, url.length() - QUALIFIED_NAME.length());
	pointService.setServiceEntryPoint(cleanUrl + "OsylPortalGwtService");
    }

    public void getCoursesForAcadCareer(String acadCareer, AsyncCallback<Map<String, String>> callback) {
	serviceProxy.getCoursesForAcadCareer(acadCareer,callback);
    }

    public void getCoursesForResponsible(String responsible,
	    AsyncCallback<Map<String, String>> callback) {
	serviceProxy.getCoursesForResponsible(responsible,callback);
    }

    public void getAllResponsibles(AsyncCallback<List<String>> callback) {
	serviceProxy.getAllResponsibles(callback);
    }

}
