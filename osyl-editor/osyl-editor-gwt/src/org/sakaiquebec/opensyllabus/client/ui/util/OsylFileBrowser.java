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

import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylFileBrowser extends OsylAbstractBrowserComposite {

    public OsylFileBrowser() {
	super();
    }


    /**
     * Constructor.
     * @param model
     * @param newController
     */
    public OsylFileBrowser(String newResDirName, String fileItemNameToSelect) {
	super(newResDirName, fileItemNameToSelect);
    }

    @Override
    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addFileButton.tooltip"));
	pb.addClickListener(new FileAddButtonClickListener());
	return pb;
    }

    private final class FileAddButtonClickListener implements ClickListener {

	public void onClick(Widget sender) {
	    OsylFileUpload osylFileUpload =
		    new OsylFileUpload(getController(), getCurrentDirectory()
			    .getDirectoryPath());
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
	//Nothing to do
    }


	@Override
	public void getRemoteDirectoryListing(String directoryPath) {
		getFileListing().addStyleName("Osyl-RemoteFileBrowser-WaitingState");
		if (TRACE){
			Window.alert("DIR = " + directoryPath);
		}
	    
	
		OsylRemoteServiceLocator.getDirectoryRemoteService().getRemoteDirectoryContent(directoryPath, 
				getRemoteDirListingRespHandler());
	    
		
	}

}
