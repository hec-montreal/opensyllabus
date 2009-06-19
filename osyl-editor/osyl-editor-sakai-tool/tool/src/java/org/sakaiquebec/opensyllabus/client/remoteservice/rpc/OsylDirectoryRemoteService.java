package org.sakaiquebec.opensyllabus.client.remoteservice.rpc;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.user.client.rpc.RemoteService;

public interface OsylDirectoryRemoteService extends RemoteService {

	public void createNewRemoteDirectory(String newFolderName, String relativePathFolder);

	public List<OsylAbstractBrowserItem> getRemoteDirectoryContent(String relativePathFolder);

	public void updateRemoteFileInfo(String fileName, String relativePathFolder,
			String description, String copyright);

}
