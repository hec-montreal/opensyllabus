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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    protected OsylAbstractBrowserComposite browser;

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
	while (!validateFolderName(newFolderName)) {
	    Window.alert("'"
		    + newFolderName
		    + getView().getUiMessage(
			    "DocumentEditor.AddFolderPromt.InvalidName"));
	    newFolderName =
		    Window.prompt(getView().getUiMessage(
			    "DocumentEditor.AddFolderPromt.Promt"),
			    newFolderName);
	}
	
	
	final OsylAbstractBrowserComposite fileBrowser = browser;
	
	AsyncCallback<Void> asyncCallback = new AsyncCallback<Void>() {
		public void onFailure(Throwable caught) {
			removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
			final OsylAlertDialog alertBox = new OsylAlertDialog(false, true, OsylController
					.getInstance().getUiMessage("Global.error"), OsylController.getInstance()
					.getUiMessage("fileUpload.unableReadRemoteDir") + caught.getMessage() );
			alertBox.center();
			alertBox.show();
		}

		public void onSuccess(Void result) {
			// Call to RemoFileBrowser in order to refresh its content
			browser.getRemoteDirectoryListing(browser.getCurrentDirectory().getDirectoryPath());
		}
	};

	// create the new Folder
	OsylRemoteServiceLocator.getDirectoryRemoteService().createNewRemoteDirectory(
			newFolderName, fileBrowser.getCurrentDirectory().getDirectoryPath(), asyncCallback);
	
    }

    public void onItemSelectionEvent(RFBItemSelectionEvent event) {
	refreshBrowsingComponents();
    }

    public void onItemListingAcquired(ItemListingAcquiredEvent event) {
	refreshBrowsingComponents();
    }

    // ADDED METHODS
    public boolean validateFolderName(String newFolderName) {
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
		browser.getSelectedAbstractBrowserItem();
	if (item == null || item instanceof OsylDirectory) {
	    return null;
	} else {
	    return item.getFilePath();
	}
    }
    
    public String getLastModifiedDateString(){
	OsylAbstractBrowserItem item =
		browser.getSelectedAbstractBrowserItem();
	if (item == null || item instanceof OsylDirectory) {
	    return null;
	} else {
	    return item.getLastModifTime();
	}
    }

    // ABSTRACT METHOD
    /**
     * initialize the browser
     */
    protected abstract void initBrowser();


    /**
     * Refresh browser (and the metadata information coming from the browser
     */
    protected abstract void refreshBrowsingComponents();
    
}
