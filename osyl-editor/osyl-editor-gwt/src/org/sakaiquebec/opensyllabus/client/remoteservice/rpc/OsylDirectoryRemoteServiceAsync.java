package org.sakaiquebec.opensyllabus.client.remoteservice.rpc;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;



import com.google.gwt.user.client.rpc.AsyncCallback;

public interface OsylDirectoryRemoteServiceAsync {

	/**
	 * @param relativePathFolder
	 *            The relative folder path
	 * @param callback
	 *            the List<OsylAbstractBrowserItem> callback
	 */
	public void getRemoteDirectoryContent(String relativePathFolder,
			final AsyncCallback<List<OsylAbstractBrowserItem>> callback);

	/**
	 * Update remote directory information
	 * 
	 * @param fileName
	 *            The file name
	 * @param relativePathFolder
	 *            The relative folder path
	 * @param description
	 *            description to update
	 * @param copyright
	 *            copyright to update
	 * @param callback
	 */
	public void updateRemoteFileInfo(String fileName, String relativePathFolder,
			String description, String copyright, final AsyncCallback<Void> callback);

	/**
	 * Create a new remote directory using SData
	 * 
	 * @param newFolderName
	 *            The folder name to create
	 * @param relativePathFolder
	 *            The current relative folder path
	 * @param callback
	 *            the Void callback
	 */
	public void createNewRemoteDirectory(String newFolderName, String relativePathFolder,
			final AsyncCallback<Void> callback);


}
