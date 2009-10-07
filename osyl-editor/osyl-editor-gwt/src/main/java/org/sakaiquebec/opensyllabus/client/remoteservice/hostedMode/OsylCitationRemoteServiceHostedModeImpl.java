package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylCitationRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;
import org.sakaiquebec.opensyllabus.shared.util.UUID;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OsylCitationRemoteServiceHostedModeImpl extends
	OsylDirectoryRemoteServiceHostedModeImpl implements
	OsylCitationRemoteServiceAsync {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initFakeFolderTree() {
	// init a fake folder tree to simulate server behavior
	List<OsylAbstractBrowserItem> listUserDir =
		new ArrayList<OsylAbstractBrowserItem>();
	this.hostedModelist.add(new OsylDirectory("work", "work", "2009/04/03",
		listUserDir));

	listUserDir.add(new OsylDirectory("HostedModeSubDirectory1",
		"work/HostedModeSubDirectory1", "2009/04/03",
		new ArrayList<OsylAbstractBrowserItem>()));
	listUserDir.add(new OsylDirectory("HostedModeSubDirectory2",
		"work/HostedModeSubDirectory2", "2009/04/03",
		new ArrayList<OsylAbstractBrowserItem>()));

	OsylCitationListItem citationsList = new OsylCitationListItem();
	citationsList.setFileName("Citation list name");
	citationsList.setFilePath("work/HostedModeCitationListPath");

	List<OsylAbstractBrowserItem> citations =
		new ArrayList<OsylAbstractBrowserItem>();

	for (int i = 0; i < 2; i++) {
	    OsylCitationItem citation = new OsylCitationItem();

	    citation.setFileName("ma citation"+i);
	    citation.setFilePath("work/HostedModeCitationListPath");
	    citation.setResourceId("dummyId"+i);
	    citation.setResourceName("ressourceName");
	    citation.setProperty(CitationSchema.CITATIONID, "dummyId"+i);
	    citation.setProperty(CitationSchema.TYPE, "Book"+i);
	    citation.setProperty(CitationSchema.TITLE, "ma citation"+i);
	    citation.setProperty(CitationSchema.ISN, "dummyISN"+i);
	    citation.setProperty(CitationSchema.CREATOR, "Mathieu"+i);
	    citation.setProperty(CitationSchema.EDITOR, "Editeur"+i);
	    citation.setProperty(CitationSchema.VOLUME, "volume"+i);
	    citation.setProperty(CitationSchema.ISSUE, "issue"+i);
	    citation.setProperty(CitationSchema.PAGES, "pages"+i);
	    citation.setProperty(CitationSchema.PUBLISHER, "publisher"+i);
	    citation.setProperty(CitationSchema.YEAR, "2009"+i);
	    citation.setProperty(CitationSchema.DATE, "01/01/2009");
	    citation.setProperty(CitationSchema.DOI, "doi"+i);
	    citation.setProperty(CitationSchema.URL, "www.google.com");
	    citation.setProperty(CitationSchema.SOURCE_TITLE, "sourceTitle"+i);
	    citations.add(citation);
	}
	citationsList.setCitations(citations);
	listUserDir.add(citationsList);
    }

    /**
     * {@inheritDoc}
     */
    public void createOrUpdateCitation(String p_relativePathFolder,
	    OsylCitationItem p_citation, AsyncCallback<String> callback) {
	List<OsylAbstractBrowserItem> list =
		findDirectoryByRelativePath(p_relativePathFolder);
	String id = p_citation.getId();
	if (list != null) {
	    if (id != null) {
		// update an existing one
		removeCitationById(id, list);
	    } else {
		// create an unique id
		id = UUID.uuid();
		p_citation.setId(id);
		p_citation.setProperty(CitationSchema.CITATIONID, id);
		p_citation.setFilePath(p_relativePathFolder);
	    }
	    // add the new reference
	    list.add(p_citation);
	}
	callback.onSuccess(id);
    }

    /**
     * remove a citation from a folder
     * 
     * @param p_id
     * @param p_currentDirectory
     * @throws IllegalArgumentException
     */
    protected void removeCitationById(String p_id,
	    List<OsylAbstractBrowserItem> p_currentDirectory)
	    throws IllegalArgumentException {
	if (p_id == null || !(p_id.length() > 0)) {
	    return;
	}
	Iterator<OsylAbstractBrowserItem> it = p_currentDirectory.iterator();
	while (it.hasNext()) {
	    OsylAbstractBrowserItem item = it.next();
	    if (item instanceof OsylCitationItem) {
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
