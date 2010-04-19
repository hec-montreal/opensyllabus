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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.List;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylFileItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylFileBrowser extends OsylAbstractBrowserComposite {

    private List<String> rightsList;

    private OsylFileUpload osylFileUpload;

    public OsylFileBrowser() {
	super();
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylFileBrowser(String newResDirName, String pathToSelect) {
	super(newResDirName, new OsylFileItem(pathToSelect));
    }

    @Override
    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addFileButton.tooltip"));
	pb.addClickHandler(new FileAddButtonClickHandler());
	return pb;
    }

    private final class FileAddButtonClickHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
	    osylFileUpload =
		    new OsylFileUpload(getController().getUiMessages().getMessage("fileUpload.addResource"), getController(), getCurrentDirectory()
			    .getDirectoryPath(), rightsList);
	    osylFileUpload.addEventHandler(OsylFileBrowser.this);
	    osylFileUpload.showModal();
	}
    }

    @Override
    protected String getCurrentSelectionLabel() {
	return getController().getUiMessage("Browser.selected_file");
    }

    @Override
    protected void onFileDoubleClicking() {
	// Nothing to do
    }

    @Override
    public void getRemoteDirectoryListing(String directoryPath) {
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-WaitingState");
	if (TRACE) {
	    Window.alert("DIR = " + directoryPath);
	}

	OsylRemoteServiceLocator.getDirectoryRemoteService()
		.getRemoteDirectoryContent(directoryPath,
			getRemoteDirListingRespHandler());

    }

    @Override
    protected PushButton createEditButton() {
	return null;
    }

    public void onUploadFile(UploadFileEvent event) {
	setItemToSelect(new OsylFileItem(event.getSource().toString()));
	((OsylFileItem) getItemToSelect()).setFileName(event.getSource()
		.toString().substring(
			event.getSource().toString().lastIndexOf("/"),
			event.getSource().toString().length()));

	OsylRemoteServiceLocator
		.getDirectoryRemoteService()
		.updateRemoteFileInfo(
			((OsylFileItem) getItemToSelect()).getFilePath(),
			"",
			osylFileUpload
			.getRight(),
			this.fileUpdateRequestHandler);
    }

    public void setRightsList(List<String> rightsList) {
	this.rightsList = rightsList;
    }

    /**
     * Inner anonymous class that represent the response callback when updating
     * file properties via sdata.
     */
    private final AsyncCallback<Void> fileUpdateRequestHandler =
	    new AsyncCallback<Void>() {
		public void onFailure(Throwable caught) {
		    OsylUnobtrusiveAlert failure =
			    new OsylUnobtrusiveAlert(
				    OsylController.getInstance().getUiMessage(
					    "Global.error")
					    + ": "
					    + OsylController
						    .getInstance()
						    .getUiMessage(
							    "DocumentEditor.document.PropUpdateError"));
		    OsylEditorEntryPoint.showWidgetOnTop(failure);
		}

		public void onSuccess(Void result) {
		    OsylFileBrowser.super.onUploadFile(null);
		}
	    };

}
