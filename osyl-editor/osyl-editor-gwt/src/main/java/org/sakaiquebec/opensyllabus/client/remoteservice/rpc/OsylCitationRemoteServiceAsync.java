package org.sakaiquebec.opensyllabus.client.remoteservice.rpc;

import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;

import com.google.gwt.user.client.rpc.AsyncCallback;



public interface OsylCitationRemoteServiceAsync extends OsylDirectoryRemoteServiceAsync {
	

	/**
	 * To create or update a citation
	 * @param p_relativePathFolder
	 * @param p_citation
	 * @param callback with path parameter (String)
	 */
	public void createOrUpdateCitation(String p_relativePathFolder, OsylCitationItem p_citation, final AsyncCallback<String> callback);
	
	public void createOrUpdateCitationList(String p_relativePathFolder, OsylCitationListItem l_citation, final AsyncCallback<Void> callback);
	
}
