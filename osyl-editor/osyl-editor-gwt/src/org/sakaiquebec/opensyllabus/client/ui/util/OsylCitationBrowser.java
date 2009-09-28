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

import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationBrowser extends OsylAbstractBrowserComposite {

    private boolean firstTimeRefreshing = true;

    private OsylCitationListItem currentCitationListItem = null;

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
	super(newResDirName, null);
	setCitationIdToSelect(citationId, citationListPath);
    }

    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addCitationButton.tooltip"));
	pb.addClickListener(new FileAddButtonClickListener());
	return pb;
    }

    public void setCitationIdToSelect(String id, String citationListPath) {
	OsylCitationItem ofi = new OsylCitationItem();
	ofi.setProperty(CitationSchema.CITATIONID, id);
	ofi.setFilePath(citationListPath);
	setItemToSelect(ofi);
    }

    private final class FileAddButtonClickListener implements ClickListener {

	public void onClick(Widget sender) {
	    openEditor(null);
	}
    }

    private void openEditor(OsylCitationItem citation) {
	OsylCitationForm osylCitationForm =
		new OsylCitationForm(getController(), getCurrentDirectory()
			.getDirectoryPath(), citation);
	osylCitationForm.addEventHandler(OsylCitationBrowser.this);
	osylCitationForm.showModal();
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

    public void refreshBrowser() {
	if (firstTimeRefreshing) {
	    firstTimeRefreshing = false;
	    firstTimeRefreshBrowser();
	} else {
	    super.refreshBrowser();
	}
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
	setFolderAddButtonEnabled(false);
	setAddFileButtonEnabled(false);// TODO delete this when add could add a
	// citation into an existing list
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

    protected void onUpButtonClick() {
	setFolderAddButtonEnabled(true);
	setAddFileButtonEnabled(true);
	currentCitationListItem = null;
    }

    public void onUploadFile(UploadFileEvent event) {
	firstTimeRefreshing = true;
	setItemToSelect(getSelectedAbstractBrowserItem());
	String citationListDirectory =
		getCurrentDirectory().getDirectoryPath().substring(
			0,
			getCurrentDirectory().getDirectoryPath().lastIndexOf(
				"/"));
	getCurrentDirectory().setDirectoryPath(citationListDirectory);
	getRemoteDirectoryListing(citationListDirectory);
    }

}
