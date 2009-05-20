/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylJSONRemoteDirectory;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class defining common methods for Resource Proxy editors which need
 * browser.
 * 
 * @author <a href="mailto:Laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylAbstractBrowserEditor extends
	OsylAbstractResProxEditor implements RFBItemSelectionEventHandler,
	RFBAddFolderEventHandler, ItemListingAcquiredEventHandler {

    // The panel containing the document browser
    protected VerticalPanel browserPanel;

    private RequestCallback folderCreationRespHandler = new RequestCallback() {

	private static final int STATUS_CODE_SUCCESS = 200;

	/**
	 * {@inheritDoc}
	 */
	public void onError(Request request, Throwable exception) {
	    removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, getView().getController()
			    .getUiMessage("Global.error"), getView()
			    .getController().getUiMessage(
				    "fileUpload.unableReadRemoteDir"));
	    alertBox.show();
	}

	/** {@inheritDoc} */
	public void onResponseReceived(Request request, Response response) {
	    if (response.getStatusCode() == STATUS_CODE_SUCCESS) {
		String parentResourceDirectoryPath =
			getResourcesPath()+getBrowser().getBrowsedSiteId()+"/"
				+ getBrowser().getCurrentDirectory()
					.getDirectoryPath();
		// Call to RemoFileBrowser in order to refresh its content
		OsylJSONRemoteDirectory.getRemoteDirectoryContent(
			parentResourceDirectoryPath, getBrowser()
				.getRemoteDirListingRespHandler());
	    } else {
		final OsylAlertDialog alertBox =
			new OsylAlertDialog(false, true, getView()
				.getController().getUiMessage("Global.error"),
				getView().getController().getUiMessage(
					"fileUpload.unableReadRemoteDir"));
		alertBox.show();
	    }
	}
    };

    public OsylAbstractBrowserEditor(OsylAbstractView view) {
	super(view);
    }

    public Widget getBrowserWidget() {
	initBrowser();
	return browserPanel;
    }

    // INTERFACE IMPLEMENTATION
    public void onClickAddFolderButton(RFBAddFolderEvent event) {

	String newFolderName =
		getView().getUiMessage(
			"DocumentEditor.AddFolderPromt.InitFolderName");
	newFolderName =
		Window.prompt(getView().getUiMessage(
			"DocumentEditor.AddFolderPromt.Promt"), newFolderName);
	while (!validFolderName(newFolderName)) {
	    Window.alert("'"
		    + newFolderName
		    + getView().getUiMessage(
			    "DocumentEditor.AddFolderPromt.InvalidName"));
	    newFolderName =
		    Window.prompt(getView().getUiMessage(
			    "DocumentEditor.AddFolderPromt.Promt"),
			    newFolderName);
	}
	String resourcesPath = getFolderCreationPath();
	String initResourceDirectoryName =
		getBrowser().getCurrentDirectory().getDirectoryPath();
	resourcesPath += initResourceDirectoryName + "/" + newFolderName;
	resourcesPath =
		OsylAbstractBrowserComposite.uriSlashCorrection(resourcesPath);
	StringBuffer postData = new StringBuffer();
	postData.append(URL.encode("f")).append("=").append(URL.encode("cf"));
	// SData call in order to create the new Folder
	OsylJSONRemoteDirectory.createNewRemoteDirectory(resourcesPath,
		folderCreationRespHandler, postData);
    }

    public void onItemSelectionEvent(RFBItemSelectionEvent event) {
	refreshBrowsingComponents();
    }

    public void onItemListingAcquired(ItemListingAcquiredEvent event) {
	refreshBrowsingComponents();
    }

    // ADDED METHODS
    public boolean validFolderName(String newFolderName) {
	return ((!(newFolderName.length() < 1))
		&& (!(newFolderName.length() > 255)) && (validateFolderNameJSregExp(newFolderName)));
    }

    native boolean validateFolderNameJSregExp(String newFolderName)/*-{
    	// ...implemented with JavaScript
   	var regExp = /^[^\\\/\?\*\"\'\>\<\:\|]*$/;
   	return regExp.test(newFolderName);
    }-*/;

    public String getResourceURI() {
	OsylAbstractBrowserItem item =
		getBrowser().getSelectedAbstractBrowserItem();
	if (item == null) {
	    return null;
	} else {
	    return item.getFilePath();
	}
    }

    protected String getResourceDirectoryName() {
	if (getView().getController().isInHostedMode()) {
	    return "UserDir";
	} else {
	    return getView().getController().getDocFolderName();
	}
    }

    /**
     * Return path used to create new folder
     **/
    private String getFolderCreationPath() {
	String uri = GWT.getModuleBaseURL();
	String serverId = uri.split("\\s*/portal/tool/\\s*")[0];
	String resourcesPath = serverId + "/sdata/c/group/" + getBrowser().getBrowsedSiteId() + "/";
	resourcesPath =
		OsylAbstractBrowserComposite.uriSlashCorrection(resourcesPath);
	return resourcesPath;
    }

    // ABSTRACT METHOD
    /**
     * Return the browser used in the editor
     */
    protected abstract OsylAbstractBrowserComposite getBrowser();

    /**
     * initialize the browser
     */
    protected abstract void initBrowser();

    /**
     * Get the path used to obtain information from sdata
     * 
     * @return
     */
    protected abstract String getResourcesPath();

    /**
     * Refresh browser (and the metadata information coming from the browser
     */
    protected abstract void refreshBrowsingComponents();

}
