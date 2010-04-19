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

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylDirectoryRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylFileItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author mathieu colombet
 * This is the embedded implementation of OsylDirectoryRemoteServiceAsync used in hosted mode only.
 * It simulate the server behavior.
 */
public class OsylDirectoryRemoteServiceHostedModeImpl implements OsylDirectoryRemoteServiceAsync {

	final protected List<OsylAbstractBrowserItem> hostedModelist=new ArrayList<OsylAbstractBrowserItem>();

	/**
	 * Constructor
	 */
	public OsylDirectoryRemoteServiceHostedModeImpl() {
		super();
		initFakeFolderTree();
	}
	
	/**
	 * Initialize a fake folder tree to simulate server behavior
	 */
	protected void initFakeFolderTree(){
		List<OsylAbstractBrowserItem> listUserDir = new ArrayList<OsylAbstractBrowserItem>();
		this.hostedModelist.add(new OsylDirectory("work", "work", "2009/04/03", listUserDir));

		listUserDir.add(new OsylDirectory("HostedModeSubDirectory1",
				"work/HostedModeSubDirectory1", "2009/04/03",
				new ArrayList<OsylAbstractBrowserItem>()));
		listUserDir.add(new OsylDirectory("HostedModeSubDirectory2",
				"work/HostedModeSubDirectory2", "2009/04/03",
				new ArrayList<OsylAbstractBrowserItem>()));
		listUserDir.add(new OsylFileItem("File1", "work/File1", false, "2009/04/03", "text/html",
				"File1 description", "File1 coyright"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void getRemoteDirectoryContent(String relativePathFolder,
			final AsyncCallback<List<OsylAbstractBrowserItem>> callback) {
		try {
			List<OsylAbstractBrowserItem> list = findDirectoryByRelativePath(relativePathFolder);
			callback.onSuccess(list);

		} catch (IllegalArgumentException e) {
			callback.onFailure(e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void updateRemoteFileInfo(String fileName, 
			String description, String copyright, final AsyncCallback<Void> callback) {

		try {
			OsylFileItem file = (OsylFileItem) findItembyByName(false, fileName);
			file.setDescription(description);
			file.setCopyrightChoice(copyright);
			callback.onSuccess(null);
		} catch (Exception e) {
			callback.onFailure(e);
		}

		callback.onSuccess(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createNewRemoteDirectory(String newFolderName, String relativePathFolder,
			final AsyncCallback<Void> callback) {

		try {
			List<OsylAbstractBrowserItem> list = findDirectoryByRelativePath(relativePathFolder);
			list.add(new OsylDirectory(newFolderName, relativePathFolder + "/" + newFolderName,
					new Date().toString(), new ArrayList<OsylAbstractBrowserItem>()));
			callback.onSuccess(null);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	/**
	 * return the content of the directory
	 * @param relativePathFolder
	 * @return List<OsylAbstractBrowserItem>
	 * @throws IllegalArgumentException
	 */
	protected List<OsylAbstractBrowserItem> findDirectoryByRelativePath(String relativePathFolder)
	throws IllegalArgumentException {

		List<OsylAbstractBrowserItem> list = this.hostedModelist;

		if (relativePathFolder == null || !(relativePathFolder.length() > 0)) {
			return list;
		}

		// can't use a StringTokenizer with gwt :-(
		String[] dir = relativePathFolder.split("/");
		for (int i = 0; i < dir.length; i++) {
			OsylAbstractBrowserItem item = findItembyByName(true, dir[i]);
			if (item != null) {
				list = ((OsylDirectory) item).getFilesList();
			}else{
				throw new IllegalArgumentException("unreachable directory");
			}
		}

		return list;
	}

	/**
	 * @param directory
	 * @param name
	 * @param currentDirectory
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected OsylAbstractBrowserItem findItembyByName(boolean directory, String path) throws IllegalArgumentException {
		if (path == null || !(path.length() > 0)) {
			return null;
		}
		for (OsylAbstractBrowserItem item : this.hostedModelist) {
			if (item.getFilePath().equals(path)) {
				// match by name
				if (directory == item.isFolder()) {
					// match by type
					return item;
				}

			}
		}

		throw new IllegalArgumentException("unreachable directory");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUploadFileUrl(String relativePathFolder) {
		return "";
	}
}
