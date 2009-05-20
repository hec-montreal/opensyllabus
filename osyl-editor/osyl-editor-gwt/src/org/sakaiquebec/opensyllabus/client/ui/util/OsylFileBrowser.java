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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylFileBrowser extends OsylAbstractBrowserComposite {

    public OsylFileBrowser() {
	super();
    }

    public OsylFileBrowser(String newDirPath, String newFilter) {
	super(newDirPath, newFilter);
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylFileBrowser(String newResDirName, String newResDirPath,
	    String newFilter, String fileItemNameToSelect) {
	super(newResDirName, newResDirPath, newFilter, fileItemNameToSelect);
    }

    @Override
    protected PushButton createAddPushButton() {
	PushButton pb =
		createTopButton(getOsylImageBundle().document_add(),
			getController().getUiMessage(
				"Browser.addFileButton.tooltip"));
	pb.addClickListener(new FileAddButtonClickListener());
	return pb;
    }

    private final class FileAddButtonClickListener implements ClickListener {

	public void onClick(Widget sender) {
	    OsylFileUpload osylFileUpload =
		    new OsylFileUpload(getController(), getCurrentDirectory()
			    .getDirectoryPath());
	    osylFileUpload.addEventHandler(OsylFileBrowser.this);
	    osylFileUpload.showModal();
	}
    }

    @Override
    protected String getCurrentSelectionLabel() {
	return getController().getUiMessage("Browser.selected_file");
    }

    @Override
    protected OsylAbstractBrowserItem getOsylAbstractBrowserItem(
	    JSONObject jObject) {
	JSONString referenceRoot =
		(JSONString) ((JSONObject) jObject.get("properties"))
			.get("sakai:reference-root");
	if (referenceRoot != null
		&& referenceRoot.stringValue().equals("/citation")) {
	    return null;
	} else {

	    JSONString path = (JSONString) jObject.get("path");
	    JSONString name =
		    (JSONString) ((JSONObject) jObject.get("properties"))
			    .get("DAV:displayname");
	    String mimeTypeString = "";
	    String lastmodifiedString = "";
	    String descriptionString = "";
	    String copyrightchoiceString = "";

	    if (jObject.get("mimeType") != null) {
		JSONString mimeType = (JSONString) jObject.get("mimeType");
		mimeTypeString = mimeType.stringValue();
	    }
	    if (((JSONObject) jObject.get("properties"))
		    .get("DAV:getlastmodified") != null) {
		JSONString lastmodified =
			(JSONString) ((JSONObject) jObject.get("properties"))
				.get("DAV:getlastmodified");
		lastmodifiedString = lastmodified.stringValue();
	    }
	    if (((JSONObject) jObject.get("properties"))
		    .get("CHEF:description") != null) {
		JSONString description =
			(JSONString) ((JSONObject) jObject.get("properties"))
				.get("CHEF:description");
		descriptionString = description.stringValue();
	    }
	    if (((JSONObject) jObject.get("properties"))
		    .get("CHEF:copyrightchoice") != null) {
		JSONString copyrightchoice =
			(JSONString) ((JSONObject) jObject.get("properties"))
				.get("CHEF:copyrightchoice");
		copyrightchoiceString = copyrightchoice.stringValue();
	    }
	    return new OsylFileItem(name.stringValue(), path.stringValue(),
		    false, lastmodifiedString, mimeTypeString,
		    descriptionString, copyrightchoiceString);
	}
    }

    @Override
    protected void onFileDoubleClicking() {
	//Nothing to do
    }

}
