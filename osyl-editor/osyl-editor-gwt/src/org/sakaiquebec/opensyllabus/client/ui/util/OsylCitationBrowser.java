/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationBrowser extends OsylAbstractBrowserComposite {

    public OsylCitationBrowser() {
	super();
    }

    public OsylCitationBrowser(String newDirPath, String newFilter) {
	super(newDirPath, newFilter);
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylCitationBrowser(String newResDirName, String newResDirPath,
	    String newFilter, String fileItemNameToSelect) {
	super(newResDirName, newResDirPath, newFilter, fileItemNameToSelect);
    }

    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addCitationButton.tooltip"));
	pb.addClickListener(new FileAddButtonClickListener());
	return pb;
    }

    private final class FileAddButtonClickListener implements ClickListener {

	public void onClick(Widget sender) {
	    openEditor(null);
	}
    }

    private void openEditor(OsylCitationItem citation) {
	OsylCitationForm osylCitationForm =
		new OsylCitationForm(getController(), getCurrentDirectory()
			.getDirectoryPath(), citation);
	osylCitationForm.addEventHandler(OsylCitationBrowser.this);
	osylCitationForm.showModal();
    }

    @Override
    protected String getCurrentSelectionLabel() {
	return getController().getUiMessage("Browser.selected_citation");
    }

    @Override
    protected OsylAbstractBrowserItem getOsylAbstractBrowserItem(
	    JSONObject jObject) {
	OsylCitationItem csi = null;
	JSONString path = (JSONString) jObject.get("path");
	String listname = null;
	if (path != null) {
	    listname =
		    path.stringValue().substring(
			    path.stringValue().lastIndexOf("/") + 1);
	}
	// JSONString citationListId = (JSONString) jObject.get("id");

	JSONObject citations = (JSONObject) jObject.get("citations");
	Set<String> keys = citations.keySet();
	for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
	    String citationId = (String) iter.next();
	    JSONObject jsCitation = (JSONObject) citations.get(citationId);
	    JSONObject properties = (JSONObject) jsCitation.get("properties");
	    JSONString title = (JSONString) properties.get("title");
	    JSONString isn = (JSONString) properties.get("isnIdentifier");
	    JSONString volume = (JSONString) properties.get("volume");
	    JSONString issue = (JSONString) properties.get("issue");
	    JSONString pages = (JSONString) properties.get("pages");
	    JSONString publisher = (JSONString) properties.get("publisher");
	    JSONString year = (JSONString) properties.get("year");
	    JSONString date = (JSONString) properties.get("date");
	    JSONString doi = (JSONString) properties.get("doi");
	    JSONString sourceTitle = (JSONString) properties.get("sourceTitle");
	    JSONString url = (JSONString) properties.get("url");
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

	    csi = new OsylCitationItem();
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
	}
	return csi;
    }

    @Override
    protected void onFileDoubleClicking() {
	OsylCitationItem citation =
		(OsylCitationItem) getSelectedAbstractBrowserItem();
	openEditor(citation);
    }
}
