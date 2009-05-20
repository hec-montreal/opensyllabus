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

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.Date;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylJSONRemoteDirectory {

    private static final boolean TRACE = false;

    /**
     * Get the content of the remote directory
     * 
     * @param path The path to look
     * @param callback The method which will receive the response
     */
    public static void getRemoteDirectoryContent(String path,
	    RequestCallback callback) {
	Date date = new Date();
	path += "?nocache=" + date.getTime();
	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.GET, path);
	requestBuilder.setCallback(callback);
	try {
	    if ( TRACE) Window.alert("Call the server - getRemoteDirectoryListing");
	    requestBuilder.send();
	    if ( TRACE) Window.alert("Returned from the server ");
	} catch (RequestException e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, OsylController
			    .getInstance().getUiMessage("Global.error"),
			    OsylController.getInstance().getUiMessage(
				    "fileUpload.unableReadRemoteDir"));
	    alertBox.center();
	    alertBox.show();
	}
    }
    
     /**
     * Updates file properties via sdata
     * @param path the path to the file being updated
     * @param params file properties
     * @param callback Response after the update.
     */
    public static void updateRemoteFileInfo(String path, String params, RequestCallback callback){
	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.POST, path);
	requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
	
	try {
	    requestBuilder.sendRequest(params, callback);
	} catch (RequestException e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, OsylController
			    .getInstance().getUiMessage("Global.error"),
			    OsylController.getInstance().getUiMessage(
				    "DocumentEditor.document.PropUpdateError"));
	    alertBox.center();
	    alertBox.show();
	}
    }
    
    /**
     * Create a new remote directory using SData
     * 
     * @param path The path of the new directory including the name of the new directory
     * @param callback The method which will receive the response
     * @param postData The data to be posted 
     */
    public static void createNewRemoteDirectory(String path,
	    RequestCallback callback, StringBuffer postData) {
	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.POST, path);
	requestBuilder.setCallback(callback);
	requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
	try {
	    if ( TRACE) Window.alert("Call the server - createNewRemoteDirectory " + path);
	    //requestBuilder.send();
	    requestBuilder.sendRequest(postData.toString(), callback);
	    if ( TRACE) Window.alert("Returned from the server ");
	} catch (RequestException e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, OsylController
			    .getInstance().getUiMessage("Global.error"),
			    OsylController.getInstance().getUiMessage(
				    "fileUpload.unableReadRemoteDir") + " error during folder creation");
	    alertBox.center();
	    alertBox.show();
	}
    }
}
