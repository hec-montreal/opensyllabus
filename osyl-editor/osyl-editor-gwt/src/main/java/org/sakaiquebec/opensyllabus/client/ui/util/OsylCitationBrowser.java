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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationBrowser extends OsylAbstractBrowserComposite {

    private boolean firstTimeRefreshing = true;

    private OsylCitationListItem currentCitationListItem = null;
    
    private List<String> typesResourceList;

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
	super(newResDirName, new OsylCitationItem(citationId, citationListPath));
    }

    // INHERITED METHODS
    @Override
    protected void onUpButtonClick() {
	getFolderAddButton().setEnabled(true);
	getAddFileButton().setTitle(
		getController().getUiMessage(
			"Browser.addCitationListButton.tooltip"));
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
	PushButton pb =
		createTopButton(getOsylImageBundle().document_edit(),
			getController().getUiMessage(
				"Browser.editButton.tooltip"));
	pb.addClickHandler(new EditButtonClickHandler());
	return pb;
    }

    @Override
    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addCitationListButton.tooltip"));
	pb.addClickHandler(new AddButtonClickHandler());
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

	} else {
	    OsylCitationItem citation =
		    (OsylCitationItem) getSelectedAbstractBrowserItem();
	    openEditor(citation);
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

    private void createOrUpdateCitationList(
	    OsylCitationListItem osylCitationListItem) {
	String citationListName = "";
	if (osylCitationListItem != null) {
	    citationListName = osylCitationListItem.getFileName();
	} else {
	    citationListName =
		    getController()
			    .getUiMessage(
				    "CitationEditor.AddCitationListPromt.InitCitationListName");
	    osylCitationListItem = new OsylCitationListItem();
	}

	citationListName =
		Window.prompt(getController().getUiMessage(
			"CitationEditor.AddCitationListPromt.Promt"),
			citationListName);
	while (!validateListName(citationListName)) {
	    Window.alert("'"
		    + citationListName
		    + getController().getUiMessage(
			    "CitationEditor.AddCitationListPromt.InvalidName"));
	    citationListName =
		    Window.prompt(getController().getUiMessage(
			    "CitationEditor.AddCitationListPromt.Promt"),
			    citationListName);
	}

	AsyncCallback<Void> asyncCallback = new AsyncCallback<Void>() {
	    public void onFailure(Throwable caught) {
		removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
		final OsylAlertDialog alertBox =
			new OsylAlertDialog(false, true, OsylController
				.getInstance().getUiMessage("Global.error"),
				OsylController.getInstance().getUiMessage(
					"fileUpload.unableReadRemoteDir")
					+ caught.getMessage());
		alertBox.center();
		alertBox.show();
	    }

	    public void onSuccess(Void result) {
		// Call to RemoFileBrowser in order to refresh its
		// content
		getRemoteDirectoryListing(getCurrentDirectory()
			.getDirectoryPath());
	    }
	};

	// create or update citation.
	osylCitationListItem.setFileName(citationListName);
	OsylRemoteServiceLocator.getCitationRemoteService()
		.createOrUpdateCitationList(
			getCurrentDirectory().getDirectoryPath(),
			osylCitationListItem, asyncCallback);
    }

    public boolean validateListName(String newListName) {
	return ((!(newListName.length() < 1))
		&& (!(newListName.length() > 255)) && (validateListNameJSregExp(newListName)));
    }

    native boolean validateListNameJSregExp(String newListName)/*-{
							       // ...implemented with JavaScript
							       var regExp = /^[^\\\/\?\*\"\'\>\<\:\|]*$/;
							       return regExp.test(newListName);
							       }-*/;

    private final class AddButtonClickHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
	    if (!isInCitationList()) {
		createOrUpdateCitationList(null);
	    } else {
		OsylCitationItem oci = new OsylCitationItem();
		oci.setFilePath(currentCitationListItem.getFilePath());
		oci.setResourceType(null);
		oci.setId(null);
		openEditor(oci);
	    }
	}
    }

    private final class EditButtonClickHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
	    if (!isInCitationList()) {
		if (getSelectedAbstractBrowserItem() instanceof OsylCitationListItem) {
		    createOrUpdateCitationList((OsylCitationListItem) getSelectedAbstractBrowserItem());
		} else {
		    // TODO add folder edit action
		}
	    } else {
		OsylCitationItem citation =
			(OsylCitationItem) getSelectedAbstractBrowserItem();
		openEditor(citation);
	    }
	}
    }

    private void openEditor(OsylCitationItem citation) {
	OsylCitationForm osylCitationForm =
		new OsylCitationForm(getController(), getCurrentDirectory()
			.getDirectoryPath(), citation);
	osylCitationForm.addEventHandler(OsylCitationBrowser.this);
	osylCitationForm.showModal();
    }

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
	getFolderAddButton().setEnabled(false);
	getAddFileButton().setTitle(
		getController().getUiMessage(
			"Browser.addCitationButton.tooltip"));
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
