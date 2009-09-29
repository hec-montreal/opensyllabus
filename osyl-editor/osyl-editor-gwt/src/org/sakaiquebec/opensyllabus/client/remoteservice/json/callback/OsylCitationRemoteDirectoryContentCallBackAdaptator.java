package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mathieu Colombet implementation of
 *         OsylAbstractRemoteDirectoryContentCallBackAdaptator used to convert
 *         json returned string in a "Citation" OsylDirectory object
 */
public class OsylCitationRemoteDirectoryContentCallBackAdaptator extends
	OsylAbstractRemoteDirectoryContentCallBackAdaptator {

    public OsylCitationRemoteDirectoryContentCallBackAdaptator(
	    AsyncCallback<List<OsylAbstractBrowserItem>> asyncCallback) {
	super(asyncCallback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OsylAbstractBrowserItem getOsylAbstractBrowserItem(
	    JSONObject jObject) {
	List<OsylAbstractBrowserItem> citationsList =
		new ArrayList<OsylAbstractBrowserItem>();
	JSONString path = (JSONString) jObject.get("path");
	String listname = null;
	if (path != null) {
	    listname =
		    path.stringValue().substring(
			    path.stringValue().lastIndexOf("/") + 1);
	}
	JSONString citationListName =
		(JSONString) jObject.get("sakai:displayname");

	JSONObject citations = (JSONObject) jObject.get("citations");
	Set<String> keys = citations.keySet();
	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
	    String citationId = (String) iter.next();
	    JSONObject jsCitation = (JSONObject) citations.get(citationId);
	    JSONObject properties = (JSONObject) jsCitation.get("properties");
	    JSONString title =
		    (JSONString) properties.get(CitationSchema.TITLE);
	    JSONString isn = (JSONString) properties.get(CitationSchema.ISN);
	    JSONString volume =
		    (JSONString) properties.get(CitationSchema.VOLUME);
	    JSONString issue =
		    (JSONString) properties.get(CitationSchema.ISSUE);
	    JSONString pages =
		    (JSONString) properties.get(CitationSchema.PAGES);
	    JSONString publisher =
		    (JSONString) properties.get(CitationSchema.PUBLISHER);
	    JSONString year = (JSONString) properties.get(CitationSchema.YEAR);
	    JSONString date = (JSONString) properties.get(CitationSchema.DATE);
	    JSONString doi = (JSONString) properties.get(CitationSchema.DOI);
	    JSONString sourceTitle =
		    (JSONString) properties.get(CitationSchema.SOURCE_TITLE);
	    JSONString url = (JSONString) properties.get(CitationSchema.URL);
	    JSONString mediaType =
		    (JSONString) properties.get("sakai:mediatype");

	    List<String> creatorsList = new ArrayList<String>();
	    JSONValue creatorValue = properties.get("creator");
	    String creatorsString = "";
	    if (creatorValue != null) {
		JSONArray creatorsArrays = (JSONArray) creatorValue;
		for (int i = 0; i < creatorsArrays.size(); i++) {
		    String creatorString =
			    ((JSONString) creatorsArrays.get(i)).stringValue();
		    creatorsList.add(creatorString);
		    creatorsString += creatorString + ", ";
		}
		if (!creatorsString.equals(""))
		    creatorsString =
			    creatorsString.substring(0,
				    creatorsString.length() - 2);
	    }

	    List<String> editorsList = new ArrayList<String>();
	    JSONValue editorValue = properties.get("editor");
	    String editorsString = "";
	    if (editorValue != null) {
		if (editorValue.isArray() == null) {
		    editorsString = ((JSONString) editorValue).toString();
		} else {
		    JSONArray editorsArrays = (JSONArray) editorValue;
		    for (int i = 0; i < editorsArrays.size(); i++) {
			String editorString =
				((JSONString) editorsArrays.get(i))
					.stringValue();
			editorsList.add(editorString);
			editorsString += editorString + ", ";
		    }
		    if (!editorsString.equals(""))
			editorsString =
				editorsString.substring(0, editorsString
					.length() - 2);
		}
	    }

	    OsylCitationItem csi = new OsylCitationItem();
	    csi.setId(citationId);
	    csi.setProperty(CitationSchema.CITATIONID, citationId);
	    csi.setProperty(CitationSchema.TYPE, mediaType.stringValue());

	    csi.setProperty(CitationSchema.TITLE, title == null ? "" : title
		    .stringValue());
	    csi.setProperty(CitationSchema.ISN, isn == null ? "" : isn
		    .stringValue());
	    csi.setProperty(CitationSchema.CREATOR, creatorsString == null ? ""
		    : creatorsString);// TODO
	    csi.setProperty(CitationSchema.EDITOR, editorsString == null ? ""
		    : editorsString);// TODO
	    csi.setProperty(CitationSchema.VOLUME, volume == null ? "" : volume
		    .stringValue());
	    csi.setProperty(CitationSchema.ISSUE, issue == null ? "" : issue
		    .stringValue());
	    csi.setProperty(CitationSchema.PAGES, pages == null ? "" : pages
		    .stringValue());
	    csi.setProperty(CitationSchema.PUBLISHER, publisher == null ? ""
		    : publisher.stringValue());
	    csi.setProperty(CitationSchema.YEAR, year == null ? "" : year
		    .stringValue());
	    csi.setProperty(CitationSchema.DATE, date == null ? "" : date
		    .stringValue());
	    csi.setProperty(CitationSchema.DOI, doi == null ? "" : doi
		    .stringValue());
	    csi.setProperty(CitationSchema.URL, url == null ? "" : url
		    .stringValue());
	    csi.setProperty(CitationSchema.SOURCE_TITLE,
		    sourceTitle == null ? "" : sourceTitle.stringValue());
	    csi.setFileName(title == null ? "" : title.stringValue());
	    csi.setFilePath(path == null ? "" : path.stringValue());
	    csi.setResourceId(path == null ? "" : path.stringValue());
	    csi.setResourceName(listname == null ? "" : listname);
	    citationsList.add(csi);
	}
	OsylCitationListItem osylCitationListItem = new OsylCitationListItem();

	osylCitationListItem.setFileName(citationListName == null ? ""
		: citationListName.stringValue());
	osylCitationListItem
		.setFilePath(path == null ? "" : path.stringValue());
	osylCitationListItem.setResourceId(path == null ? "" : path
		.stringValue());
	osylCitationListItem.setResourceName(listname == null ? "" : listname);
	osylCitationListItem.setCitations(citationsList);

	return osylCitationListItem;

    }

}
