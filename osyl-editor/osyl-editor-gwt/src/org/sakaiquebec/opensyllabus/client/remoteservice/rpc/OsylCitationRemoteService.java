package org.sakaiquebec.opensyllabus.client.remoteservice.rpc;

import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;



public interface OsylCitationRemoteService extends OsylDirectoryRemoteService {

	public void createOrUpdateCitation(OsylCitationItem p_citation);

}
