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

import java.util.ArrayList;

import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class OsylHostedModeCitationBrowserComposite extends OsylCitationBrowser {
    /**
     * Constructors for OsylRemoteFileBrowser with default directory and no
     * filter
     */

    public OsylHostedModeCitationBrowserComposite() {
	super("UserDir", "");
	initView();
    }

    /**
     * Constructor.
     * 
     * @param newDirPath
     * @param newFilter
     */
    public OsylHostedModeCitationBrowserComposite(String newDirPath,
	    String newFilter) {
	super(newDirPath, newFilter);
	String uri = GWT.getModuleBaseURL();
	if (TRACE) {
	    System.out.println("*** uri: " + uri);
	    Window.alert("*** uri: " + uri);
	}
	setCurrentDirectory(getHostedModeDirectoryService(getInitialDirPath()));
	// Uncomment the line below to test JSON parsing
	// remoteDirectory =
	// getRemoteDirectoryContent(JSONTestString.getTestString());
	setBaseDirectoryPath("");
	setInitialDirPath(getCurrentDirectory().getDirectoryPath());
	setFileExtensionFilter(newFilter);
	initView();
    }

    @Override
    public void getRemoteDirectoryListing(String newDirPath) {
	setCurrentDirectory(getHostedModeDirectoryService(newDirPath));
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-WaitingState");
	getFileListing().clear();
	try {
	    int nbrElements = getCurrentDirectory().getFilesList().size();
	    for (int i = 0; i < nbrElements; i++) {
		getFileListing().addItem(
			formatListingLine(getCurrentDirectory().getFilesList()
				.get(i)));
	    }
	} catch (Exception e) {
	    // TODO: handle exception
	}
	getFileListing().removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
    }

    private OsylDirectory getHostedModeDirectoryService(String currentPath) {
	OsylDirectory returnDirectory = null;
	ArrayList<OsylAbstractBrowserItem> listOfUserDirItem =
		new ArrayList<OsylAbstractBrowserItem>();
	OsylCitationItem csi = new OsylCitationItem();
	csi.setFileName("essai");
	csi.setId("id");
	csi.setProperty(CitationSchema.TYPE,CitationSchema.TYPE_BOOK);
	csi.setProperty(CitationSchema.TITLE, "citation title");
	csi.setProperty(CitationSchema.ISN, "1234-5678");
	csi.setProperty(CitationSchema.CREATOR, "Auteur");
	csi.setProperty(CitationSchema.VOLUME, "Volume");
	csi.setProperty(CitationSchema.ISSUE, "Issue");
	csi.setProperty(CitationSchema.PAGES, "123");
	csi.setProperty(CitationSchema.PUBLISHER, "Publisher");
	csi.setProperty(CitationSchema.YEAR, "2009");
	csi.setProperty(CitationSchema.SOURCE_TITLE, "Source title");
	csi.setFileName("CitationName");
	csi.setFilePath("fake/filePath");
	listOfUserDirItem.add(csi);

	OsylDirectory userDir =
		new OsylDirectory("UserDir", "UserDir", "", listOfUserDirItem);
	if (currentPath.equals("UserDir")) {
	    returnDirectory = userDir;
	}
	return returnDirectory;
    }
}
