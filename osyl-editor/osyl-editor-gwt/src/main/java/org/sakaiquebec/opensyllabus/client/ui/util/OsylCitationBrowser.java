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
package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.List;

import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationBrowser extends OsylAbstractBrowserComposite {

    private boolean firstTimeRefreshing = true;

    private OsylCitationListItem currentCitationListItem = null;
    
    private List<String> typesResourceList;
    
    private String resourceProxyId;

    // CONSTRUUCTORS
    public OsylCitationBrowser() {
	super();
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylCitationBrowser(String newResDirName, String citationId,
	    String citationListPath) {
	this(newResDirName, citationId, citationListPath,(String)null);
    }
    
    public OsylCitationBrowser(String newResDirName, String citationId,
	    String citationListPath, String resourceId) {
	super(newResDirName, new OsylCitationItem(citationId, citationListPath));
	this.resourceProxyId=resourceId;
    }

    // INHERITED METHODS
    @Override
	public void initView() {
    	super.initView();
    	//Disable the folder add button
    	getFolderAddButton().setEnabled(false);
    	getFolderAddButton().setVisible(false);
    }
    
    @Override
    protected void onUpButtonClick() {
	currentCitationListItem = null;
    }

    public void refreshCitationsInList() {
	firstTimeRefreshing = true;
	if (getSelectedAbstractBrowserItem() != null)
	    setItemToSelect(getSelectedAbstractBrowserItem());
	else {
	    OsylCitationItem dumbCitationItem = new OsylCitationItem();
	    dumbCitationItem.setFilePath(currentCitationListItem.getFilePath());
	    setItemToSelect(dumbCitationItem);
	}
	String citationListDirectory =
		getCurrentDirectory().getDirectoryPath().substring(
			0,
			getCurrentDirectory().getDirectoryPath().lastIndexOf(
				"/"));
	getCurrentDirectoryTextBox().setText(extractRessourceUri(citationListDirectory));
	getCurrentDirectory().setDirectoryPath(citationListDirectory);
	getRemoteDirectoryListing(citationListDirectory);
    }

    @Override
    public void onUploadFile(UploadFileEvent event) {
	if (isInCitationList()) {
	    refreshCitationsInList();
	} else {
	    super.onUploadFile(event);
	}
    }

    @Override
    protected PushButton createEditButton() {
    	PushButton pb = new PushButton();
    	pb.setVisible(false);
    	pb.setEnabled(false);
    	return pb;
    }

    @Override
    protected PushButton createAddPushButton() {
    	PushButton pb = new PushButton();
    	pb.setVisible(false);
    	pb.setEnabled(false);
    	return pb;
    }

    @Override
    public void refreshBrowser() {
	if (firstTimeRefreshing) {
	    firstTimeRefreshing = false;
	    firstTimeRefreshBrowser();
	} else {
	    super.refreshBrowser();
	}
    }

    @Override
    protected String getCurrentSelectionLabel() {
	return getController().getUiMessage("Browser.selected_citation");
    }

    @Override
    protected void onFileDoubleClicking() {
	if (getSelectedAbstractBrowserItem() instanceof OsylCitationListItem) {
	    OsylCitationListItem osylCitationListItem =
		    (OsylCitationListItem) getSelectedAbstractBrowserItem();
	    try {
		refreshBrowserWithCitationListContent(osylCitationListItem);
	    } finally {
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
	    }
	}
    }

    @Override
    public void getRemoteDirectoryListing(String directoryPath) {
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-WaitingState");

	if (TRACE) {
	    Window.alert("DIR = " + directoryPath);
	}

	OsylRemoteServiceLocator.getCitationRemoteService()
		.getRemoteDirectoryContent(directoryPath,
			getRemoteDirListingRespHandler());
    }

    // ADDED METHODS

    public boolean validateListName(String newListName) {
	return ((!(newListName.length() < 1))
		&& (!(newListName.length() > 255)) && (validateListNameJSregExp(newListName)));
    }

    native boolean validateListNameJSregExp(String newListName)/*-{
							       // ...implemented with JavaScript
							       var regExp = /^[^\\\/\?\*\"\'\>\<\:\|]*$/;
							       return regExp.test(newListName);
							       }-*/;

    public void firstTimeRefreshBrowser() {
	boolean fileItemFound = false;
	OsylAbstractBrowserItem itemToSelect = getItemToSelect();
	OsylCitationListItem osylCitationListItem = null;
	if (itemToSelect != null) {

	    for (int i = 0; i < getCurrentDirectory().getFilesList().size(); i++) {
		OsylAbstractBrowserItem fileItem =
			getCurrentDirectory().getFilesList().get(i);

		if (fileItem.getFilePath().equals(itemToSelect.getFilePath())) {
		    fileItemFound = true;
		    osylCitationListItem = (OsylCitationListItem) fileItem;
		}
	    }
	}
	if (!fileItemFound) {
	    setSelectedAbstractBrowserItem(null);
	    getFileListing().setSelectedIndex(-1);
	} else {
	    refreshBrowserWithCitationListContent(osylCitationListItem);
	}
    }

    private void refreshBrowserWithCitationListContent(
	    OsylCitationListItem osylCitationListItem) {
	currentCitationListItem = osylCitationListItem;
	setCitationListAsDirectory(currentCitationListItem);
	refreshFileListing(osylCitationListItem.getCitations());
    }

    private void setCitationListAsDirectory(
	    OsylCitationListItem osylCitationListItem) {
	getCurrentDirectory().setDirectoryPath(
		getCurrentDirectory().getDirectoryPath() + "/"
			+ osylCitationListItem.getResourceName());
	getCurrentDirectoryTextBox().setText(
		getCurrentDirectoryTextBox().getText() + "/"
			+ osylCitationListItem.getResourceName());
	getCurrentDirectory().setFilesList(
		currentCitationListItem.getCitations());
    }

    protected boolean isInCitationList() {
	if (currentCitationListItem == null)
	    return false;
	else
	    return true;
    }

    public void setTypesResourceList(List<String> typesResourceList) {
	this.typesResourceList = typesResourceList;
    }
    
    public List<String> getTypesResourceList() {
        return typesResourceList;
    }    
}
