package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

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

	//TODO: get this from settings?
    protected final static List<String> SUPPORTED_CITATION_TYPES =
	    Arrays.asList(
	    		new String[] { 
	    				CitationSchema.TYPE_ARTICLE,
	    				CitationSchema.TYPE_BOOK,
	    				CitationSchema.TYPE_CHAPTER,
	    				CitationSchema.TYPE_ELECTRONIC,
	    				CitationSchema.TYPE_REPORT,
	    				CitationSchema.TYPE_THESIS,
	    				CitationSchema.TYPE_UNKNOWN, 
	    				CitationSchema.TYPE_PROCEED });

    /**
     * Value used to map the citation IDENTIFIER_TYPE_URL to preferredUrl of
     * SDATA
     */
    private static final String PREFERRED_URL_LABEL = "sakai:url_label";
    private static final String PREFERRED_URL = "preferredUrl";
    private static final String BOOKSTORE_URL = "bookstoreUrl";
    private static final String RESOURCE_TYPE = "asmResourceType";
    private static final String NO_URL = "noUrl";
    private static final String URL="url";

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
	JSONString lastModified =
		(JSONString) jObject.get("DAV:getlastmodified");
	JSONObject citations = (JSONObject) jObject.get("citations");
	
	Set<String> keys = citations.keySet();
	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
	    String citationId = (String) iter.next();
	    JSONObject jsCitation = (JSONObject) citations.get(citationId);
	    JSONObject properties = (JSONObject) jsCitation.get("properties");

	    JSONString mediaType =
		    (JSONString) properties.get("sakai:mediatype");
	    if (SUPPORTED_CITATION_TYPES.contains(mediaType.stringValue())) {
		JSONString title =
			(JSONString) properties.get(CitationSchema.TITLE);
		JSONString isn =
			(JSONString) properties.get(CitationSchema.ISN);
		JSONString volume =
				(JSONString) properties.get(CitationSchema.VOLUME);
		JSONString edition =
				(JSONString) properties.get(CitationSchema.EDITION);
		JSONString issue =
			(JSONString) properties.get(CitationSchema.ISSUE);
		JSONString pages =
			(JSONString) properties.get(CitationSchema.PAGES);
		JSONString publisher =
			(JSONString) properties.get(CitationSchema.PUBLISHER);
		JSONString year =
			(JSONString) properties.get(CitationSchema.YEAR);
		JSONString date =
			(JSONString) properties.get(CitationSchema.DATE);
		JSONString doi =
			(JSONString) properties.get(CitationSchema.DOI);
		JSONString publicationLocation =
			(JSONString) properties
				.get(CitationSchema.PUBLICATION_LOCATION);
		JSONString sourceTitle =
			(JSONString) properties
				.get(CitationSchema.SOURCE_TITLE);
		JSONString start_page =
				(JSONString) properties.get(CitationSchema.START_PAGE);
		JSONString end_page =
				(JSONString) properties.get(CitationSchema.END_PAGE);
		JSONString date_retrieved =
				(JSONString) properties.get(CitationSchema.DATE_RETRIEVED);
		JSONString document_type =
				(JSONString) properties.get(CitationSchema.DOCUMENT_TYPE);
		JSONString university =
				(JSONString) properties.get(CitationSchema.UNIVERSITY);

		//Type of resource
		JSONValue resourceTypeValue =
			(JSONValue) properties
				.get(CitationSchema.CITATION_RESOURCE_TYPE);

		JSONString resourceType = null;
		if (resourceTypeValue != null) {
			JSONArray typeArray = resourceTypeValue.isArray();

			if (typeArray != null) {
				// use only the first asmResourceType
				resourceType = (JSONString) typeArray.get(0);
			} else {
				resourceType = (JSONString) resourceTypeValue;
			}
		}


		JSONString identifierTypeUrlLabel =
		    (JSONString) properties.get(PREFERRED_URL_LABEL);
		
		JSONString identifierTypeUrl =
			(JSONString) properties.get(PREFERRED_URL);
		JSONString identifierTypeLibrary =
			(JSONString) properties
				.get(URL);

		JSONString identifierTypeBookstore =
			(JSONString) properties.get(BOOKSTORE_URL);

		JSONString identifierTypeNoUrl =
			(JSONString) properties.get(NO_URL);

		List<String> creatorsList = new ArrayList<String>();
		JSONValue creatorValue = properties.get("creator");
		String creatorsString = "";
		if (creatorValue != null) {
		    JSONArray creatorsArrays = (JSONArray) creatorValue;
		    for (int i = 0; i < creatorsArrays.size(); i++) {
			String creatorString =
				((JSONString) creatorsArrays.get(i))
					.stringValue();
			creatorsList.add(creatorString);
			creatorsString += creatorString + " & ";
		    }
		    if (!creatorsString.equals(""))
			creatorsString =
				creatorsString.substring(0, creatorsString
					.length() - 3);
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

		csi.setProperty(CitationSchema.TITLE, title == null ? ""
			: title.stringValue());
		csi.setProperty(CitationSchema.ISN, isn == null ? "" : isn
			.stringValue());
		csi.setProperty(CitationSchema.CREATOR,
			creatorsString == null ? "" : creatorsString);
		csi.setProperty(CitationSchema.EDITOR,
			editorsString == null ? "" : editorsString);
		csi.setProperty(CitationSchema.EDITION,
			edition == null ? "" : edition.stringValue());
		csi.setProperty(CitationSchema.VOLUME, volume == null ? ""
			: volume.stringValue());
		csi.setProperty(CitationSchema.ISSUE, issue == null ? ""
			: issue.stringValue());
		csi.setProperty(CitationSchema.PAGES, pages == null ? ""
			: pages.stringValue());
		csi.setProperty(CitationSchema.PUBLISHER,
			publisher == null ? "" : publisher.stringValue());
		csi.setProperty(CitationSchema.YEAR, year == null ? "" : year
			.stringValue());
		csi.setProperty(CitationSchema.DATE, date == null ? "" : date
			.stringValue());
		csi.setProperty(CitationSchema.DOI, doi == null ? "" : doi
				.stringValue());
		csi.setProperty(CitationSchema.START_PAGE, start_page == null ? "" : start_page
				.stringValue());
		csi.setProperty(CitationSchema.END_PAGE, end_page == null ? "" : end_page
				.stringValue());
		csi.setProperty(CitationSchema.DATE_RETRIEVED, date_retrieved == null ? "" : date_retrieved
				.stringValue());
		csi.setProperty(CitationSchema.DOCUMENT_TYPE, document_type == null ? "" : document_type
				.stringValue());
		csi.setProperty(CitationSchema.UNIVERSITY, university == null ? "" : university
				.stringValue());
		
		//Type of resource
		csi.setProperty(CitationSchema.CITATION_RESOURCE_TYPE,
				resourceType == null ? "" : resourceType.stringValue());

		csi.setProperty(CitationSchema.PUBLICATION_LOCATION,
			publicationLocation == null ? "" : publicationLocation
				.stringValue());

		csi.setProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK,
			identifierTypeUrl == null ? "" : identifierTypeUrl
				.stringValue());
		
		csi.getCOProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK)
			.addAttribute(
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK_LABEL,
			identifierTypeUrlLabel == null ? "" :
			    identifierTypeUrlLabel.stringValue());

		csi.setProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_NOLINK,
			identifierTypeNoUrl == null ? "" : identifierTypeNoUrl
				.stringValue());

		csi.setProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_LIBRARY,
			identifierTypeLibrary == null ? ""
				: identifierTypeLibrary.stringValue());

		csi.setProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.RESOURCE_TYPE,
			resourceType == null ? ""
					: resourceType.stringValue());

		csi.setProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE,
			identifierTypeBookstore == null ? ""
				: identifierTypeBookstore.stringValue());

		csi.setProperty(CitationSchema.SOURCE_TITLE,
			sourceTitle == null ? "" : sourceTitle.stringValue());
		csi.setFileName(title == null ? "" : title.stringValue());
		csi.setFilePath(path == null ? "" : path.stringValue());
		csi.setResourceId(path == null ? "" : path.stringValue());
		csi.setResourceName(listname == null ? "" : listname);
		csi.setLastModifTime(OsylDateUtils
			.getXmlDateStringFromSakaiDateString(lastModified
				.stringValue()));
		citationsList.add(csi);
	    }
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
