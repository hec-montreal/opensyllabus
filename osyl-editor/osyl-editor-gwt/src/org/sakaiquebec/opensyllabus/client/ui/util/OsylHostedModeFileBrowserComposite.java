/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public class OsylHostedModeFileBrowserComposite extends OsylFileBrowser {

    /**
     * Constructors for OsylRemoteFileBrowser with default directory and no
     * filter
     */

    public OsylHostedModeFileBrowserComposite() {
	super("UserDir", "");
	initView();
    }

    /**
     * Constructor.
     * 
     * @param newDirPath
     * @param newFilter
     */
    public OsylHostedModeFileBrowserComposite(String newDirPath,
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
	ArrayList<OsylAbstractBrowserItem> listOfUserDir1Item =
		new ArrayList<OsylAbstractBrowserItem>();
	ArrayList<OsylAbstractBrowserItem> listOfUserDir2Item =
		new ArrayList<OsylAbstractBrowserItem>();
	ArrayList<OsylAbstractBrowserItem> listOfUserDir3Item =
		new ArrayList<OsylAbstractBrowserItem>();
	ArrayList<OsylAbstractBrowserItem> listOfUserDir4Item =
		new ArrayList<OsylAbstractBrowserItem>();
	OsylDirectory dir1 =
		new OsylDirectory("Dir1", "UserDir/Dir1", "",
			listOfUserDir1Item);
	listOfUserDirItem.add(dir1);
	OsylFileItem file1 =
		new OsylFileItem("file1", "UserDir/file1", false, (new Date())
			.toString(), "ppt", "description file1",
			"license file1");
	listOfUserDirItem.add(file1);
	OsylFileItem file2 =
		new OsylFileItem("file2", "UserDir/file2", false, (new Date())
			.toString(), "ppt", "description file2",
			"license file2");
	listOfUserDirItem.add(file2);
	OsylDirectory dir2 =
		new OsylDirectory("Dir2", "UserDir/Dir2", "",
			listOfUserDir2Item);
	listOfUserDirItem.add(dir2);
	OsylFileItem file3 =
		new OsylFileItem("file3", "UserDir/file3", false, (new Date())
			.toString(), "ppt", "description file3",
			"license file2");
	listOfUserDirItem.add(file3);
	OsylFileItem file4 =
		new OsylFileItem("file4", "UserDir/file4", false, (new Date())
			.toString(), "pdf", "description file4",
			"license file4");
	listOfUserDirItem.add(file4);
	OsylFileItem file5 =
		new OsylFileItem("file5", "UserDir/file5", false, (new Date())
			.toString(), "ppt", "description file5",
			"license file5");
	listOfUserDirItem.add(file5);
	OsylFileItem file6 =
		new OsylFileItem("file6", "UserDir/file6", false, (new Date())
			.toString(), "ppt", "description file6",
			"license file6");
	listOfUserDirItem.add(file6);
	OsylFileItem file7 =
		new OsylFileItem("file7", "UserDir/file7", false, (new Date())
			.toString(), "pdf", "description file7",
			"license file7");
	listOfUserDirItem.add(file7);
	OsylDirectory dir4 =
		new OsylDirectory("Dir4", "UserDir/Dir4", "",
			listOfUserDir4Item);
	listOfUserDirItem.add(dir4);
	OsylFileItem file8 =
		new OsylFileItem("file8", "UserDir/file8", false, (new Date())
			.toString(), "ppt", "description file8",
			"license file8");
	listOfUserDirItem.add(file8);
	OsylFileItem file9 =
		new OsylFileItem("file9", "UserDir/file9", false, (new Date())
			.toString(), "ppt", "description file9",
			"license file9");
	listOfUserDirItem.add(file9);
	OsylFileItem file10 =
		new OsylFileItem("file10", "UserDir/Dir1/file10", false,
			(new Date()).toString(), "pdf", "description file10",
			"license file10");
	listOfUserDir1Item.add(file10);
	OsylFileItem file11 =
		new OsylFileItem("file11", "UserDir/Dir2/file11", false,
			(new Date()).toString(), "pdf", "description file11",
			"license file11");
	listOfUserDir2Item.add(file11);
	OsylFileItem file12 =
		new OsylFileItem("file12", "UserDir/Dir2/Dir3/file12", false,
			(new Date()).toString(), "pdt", "description file12",
			"license file12");
	listOfUserDir3Item.add(file12);
	OsylDirectory dir3 =
		new OsylDirectory("Dir3", "UserDir/Dir2/Dir3", "",
			listOfUserDir3Item);
	listOfUserDir2Item.add(dir3);
	OsylDirectory userDir =
		new OsylDirectory("UserDir", "UserDir", "", listOfUserDirItem);
	if (currentPath.equals("UserDir")) {
	    returnDirectory = userDir;
	} else if (currentPath.equals("UserDir/Dir1")) {
	    returnDirectory = dir1;
	} else if (currentPath.equals("UserDir/Dir1/file10")) {
	    returnDirectory = dir1;
	} else if (currentPath.startsWith("UserDir/file")) {
	    returnDirectory = userDir;
	} else if (currentPath.equals("UserDir/Dir4")) {
	    returnDirectory = dir4;
	} else if (currentPath.equals("UserDir/Dir2")) {
	    returnDirectory = dir2;
	} else if (currentPath.equals("UserDir/Dir2/file11")) {
	    returnDirectory = dir2;
	} else if (currentPath.equals("UserDir/Dir2/Dir3")) {
	    returnDirectory = dir3;
	} else if (currentPath.equals("UserDir/Dir2/Dir3/file12")) {
	    returnDirectory = dir3;
	}
	return returnDirectory;
    }
}
