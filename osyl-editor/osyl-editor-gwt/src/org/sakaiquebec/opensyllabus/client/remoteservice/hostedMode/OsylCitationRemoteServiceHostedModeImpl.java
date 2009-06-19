package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylCitationRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OsylCitationRemoteServiceHostedModeImpl extends
		OsylDirectoryRemoteServiceHostedModeImpl implements OsylCitationRemoteServiceAsync {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initFakeFolderTree(){
		// init a fake folder tree to simulate server behavior
		List<OsylAbstractBrowserItem> listUserDir = new ArrayList<OsylAbstractBrowserItem>();
		this.hostedModelist.add(new OsylDirectory("work", "work", "2009/04/03", listUserDir));

		listUserDir.add(new OsylDirectory("HostedModeSubDirectory1",
				"work/HostedModeSubDirectory1", "2009/04/03",
				new ArrayList<OsylAbstractBrowserItem>()));
		listUserDir.add(new OsylDirectory("HostedModeSubDirectory2",
				"work/HostedModeSubDirectory2", "2009/04/03",
				new ArrayList<OsylAbstractBrowserItem>()));
		
		OsylCitationItem citation = new OsylCitationItem();

		citation.setFileName("ma citation");
		citation.setFilePath("work");
		citation.setResourceId("work");
		citation.setResourceName("ressourceName");
		citation.setProperty(CitationSchema.CITATIONID, "dummyId");
		citation.setProperty(CitationSchema.TYPE, "Book");
		citation.setProperty(CitationSchema.TITLE, "ma citation");
		citation.setProperty(CitationSchema.ISN, "dummyISN");	
		citation.setProperty(CitationSchema.CREATOR, "Mathieu");
		citation.setProperty(CitationSchema.EDITOR, "Editeur");
		citation.setProperty(CitationSchema.VOLUME, "volume");
		citation.setProperty(CitationSchema.ISSUE, "issue");
		citation.setProperty(CitationSchema.PAGES, "pages");
		citation.setProperty(CitationSchema.PUBLISHER, "publisher");
		citation.setProperty(CitationSchema.YEAR, "2009");
		citation.setProperty(CitationSchema.DATE, "01/01/2009");
		citation.setProperty(CitationSchema.DOI, "doi");
		citation.setProperty(CitationSchema.URL, "www.google.com");
		citation.setProperty(CitationSchema.SOURCE_TITLE,"sourceTitle");
		
		listUserDir.add(citation);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void createOrUpdateCitation(String p_relativePathFolder,
			OsylCitationItem p_citation, AsyncCallback<Void> callback) {
		List<OsylAbstractBrowserItem> list = findDirectoryByRelativePath(p_relativePathFolder);
		if(list!=null){
			if(p_citation.getId()!=null){
				// update an existing one
				removeCitationById(p_citation.getId(), list);
			}else{
				//create an unique id
				String id = String.valueOf(new Date().getTime());
				p_citation.setId(id);
				p_citation.setProperty(CitationSchema.CITATIONID, id);
				p_citation.setFilePath(p_relativePathFolder);
			}
			//add the new reference
			list.add(p_citation);
		}
		callback.onSuccess(null);
	}
	
	/**
	 * remove a citation from a folder
	 * @param p_id
	 * @param p_currentDirectory
	 * @throws IllegalArgumentException
	 */
	protected void removeCitationById(String p_id,
			List<OsylAbstractBrowserItem> p_currentDirectory) throws IllegalArgumentException {
		if (p_id == null || !(p_id.length() > 0)) {
			return;
		}
		Iterator<OsylAbstractBrowserItem> it = p_currentDirectory.iterator();
		while (it.hasNext()) {
			OsylAbstractBrowserItem item = it.next();
			if(item instanceof OsylCitationItem){
				OsylCitationItem citation = (OsylCitationItem) item;
				if (p_id.equals(citation.getId())) {
					it.remove();
					return;
				}
			}
		}
		throw new IllegalArgumentException("unreachable directory");
	}

}
