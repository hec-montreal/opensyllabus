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

package org.sakaiquebec.opensyllabus.client.remoteservice.json;

import java.util.Date;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.callback.OsylFileRemoteDirectoryContentCallBackAdaptator;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.callback.OsylVoidCallBackAdaptator;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylDirectoryRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylDirectoryRemoteServiceJsonImpl implements
	OsylDirectoryRemoteServiceAsync {

    protected static final boolean TRACE = false;
    protected String remoteUri;
    protected String serverId;

    public OsylDirectoryRemoteServiceJsonImpl() {
	super();
	serverId = GWT.getModuleBaseURL().split("\\s*/portal/tool/\\s*")[0];
	initRemoteUri();
    }

    protected void initRemoteUri() {
	this.remoteUri = serverId + "/sdata/c";
	this.remoteUri =
		OsylAbstractBrowserComposite.uriSlashCorrection(this.remoteUri);
    }

    protected String getRessourceUri(String relativePathFolder) {
	// Keep the parent path directory in order to refresh its content
	String ressourceUri = this.remoteUri;
	if (relativePathFolder != null) {
	    ressourceUri += relativePathFolder + "/";
	}
	return ressourceUri;
    }

    protected RequestCallback getRemoteDirectoryContentCallBackAdaptator(
	    final AsyncCallback<List<OsylAbstractBrowserItem>> callback) {
	return new OsylFileRemoteDirectoryContentCallBackAdaptator(callback);
    }

    /**
     * {@inheritDoc}
     */
    public void getRemoteDirectoryContent(String relativePathFolder,
	    final AsyncCallback<List<OsylAbstractBrowserItem>> callback) {

	RequestCallback requestCallback =
		getRemoteDirectoryContentCallBackAdaptator(callback);

	// Keep the parent path directory in order to refresh its content
	String resourceDirectoryPath = getRessourceUri(relativePathFolder);
	resourceDirectoryPath += "?nocache=" + new Date().getTime();

	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.GET, URL
			.encode(resourceDirectoryPath));
	requestBuilder.setCallback(requestCallback);
	try {
	    if (TRACE) {
		Window.alert("Call the server - getRemoteDirectoryListing : "
			+ resourceDirectoryPath);
	    }
	    requestBuilder.send();
	    if (TRACE) {
		Window.alert("Returned from the server ");
	    }
	} catch (RequestException e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, OsylController
			    .getInstance().getUiMessage("Global.error")
			    + " " + e.getMessage(), OsylController
			    .getInstance().getUiMessage(
				    "fileUpload.unableReadRemoteDir"));
	    alertBox.center();
	    alertBox.show();
	}
    }

    /**
     * {@inheritDoc}
     */
    public void updateRemoteFileInfo(String filePath, String description, String copyright,
	    final AsyncCallback<Void> callback) {

	String requestParams =
		"f=" + URL.encode("pu") + "&fp="
			+ URL.encode("CHEF:description") + "&fv="
			+ URL.encode(description) + "&fp="
			+ URL.encode("CHEF:copyrightchoice") + "&fv="
			+ URL.encode(copyright);
	String resourceDirectoryPath = remoteUri + filePath;

	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.POST, URL
			.encode(resourceDirectoryPath));
	requestBuilder.setHeader("Content-type",
		"application/x-www-form-urlencoded");

	RequestCallback requestCallback =
		new OsylVoidCallBackAdaptator(callback);

	try {
	    requestBuilder.sendRequest(requestParams, requestCallback);
	} catch (RequestException e) {
	    // TODO : Manage exception
	    e.printStackTrace();
	}
    }

    /**
     * {@inheritDoc}
     */
    public void createNewRemoteDirectory(String newFolderName,
	    String relativePathFolder, final AsyncCallback<Void> callback) {

	RequestCallback requestCallback =
		new OsylVoidCallBackAdaptator(callback);

	String resourceDirectoryPath =
		getRessourceUri(relativePathFolder) + newFolderName;
	resourceDirectoryPath =
		OsylAbstractBrowserComposite
			.uriSlashCorrection(resourceDirectoryPath);

	StringBuffer postData = new StringBuffer();
	postData.append(URL.encode("f")).append("=").append(URL.encode("cf"));

	RequestBuilder requestBuilder =
		new RequestBuilder(RequestBuilder.POST, URL
			.encode(resourceDirectoryPath));
	requestBuilder.setCallback(requestCallback);
	requestBuilder.setHeader("Content-type",
		"application/x-www-form-urlencoded");
	try {
	    if (TRACE) {
		Window.alert("Call the server - createNewRemoteDirectory "
			+ resourceDirectoryPath);
	    }
	    requestBuilder.sendRequest(postData.toString(), requestCallback);
	    if (TRACE) {
		Window.alert("Returned from the server ");
	    }
	} catch (RequestException e) {
	    Window.alert("createNewRemoteDirectory" + e.getMessage());
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, OsylController
			    .getInstance().getUiMessage("Global.error"),
			    OsylController.getInstance().getUiMessage(
				    "fileUpload.unableReadRemoteDir")
				    + " error during folder creation");
	    alertBox.center();
	    alertBox.show();
	}
    }

    /**
     * @param relativePathFolder The current relative folder path
     * @return the url to submit the upload file form
     */
    public String getUploadFileUrl(String relativePathFolder) {
	return URL.encode(getRessourceUri(relativePathFolder));
    }
}
