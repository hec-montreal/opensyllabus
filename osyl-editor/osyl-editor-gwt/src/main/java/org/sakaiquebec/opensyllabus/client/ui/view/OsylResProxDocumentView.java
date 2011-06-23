/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.client.ui.view;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylDocumentEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * Class providing display and edition capabilities for Document resources.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxDocumentView extends OsylAbstractResProxBrowserView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxDocumentView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylDocumentEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public OsylDocumentEditor getEditor() {
	return (OsylDocumentEditor) super.getEditor();
    }

    protected void updateModel() {
	updateMetaInfo();
	getModel().setLabel(getEditor().getText());
	getModel().addProperty(COPropertiesType.COMMENT,
		getEditor().getDescription());
	//Document resource type	
	getModel().getResource().addProperty(COPropertiesType.ASM_RESOURCE_TYPE,
		getEditor().getTypeDocument());

	// FIXME This is a workaround. Should be deleted after we have a way to
	// display the fileBrowser showing the previously selected file.
	String uri = getEditor().getResourceURI();
	if (uri != null) {
	    getModel().getResource().addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI, uri);
	    // update documentContextVisibilitymap
	    Map<String, String> cv =
		    OsylEditorEntryPoint.getInstance()
			    .getDocumentContextVisibilityMap().get(uri);
	    if (cv == null) {
		cv = new HashMap<String, String>();
	    }
	    cv.put(getModel().getId(),
		    getModel().getProperty(COPropertiesType.VISIBILITY));
	    OsylEditorEntryPoint.getInstance()
		    .getDocumentContextVisibilityMap().put(uri, cv);
	}
    }

    public String getDocType() {
	return getModel().getResource().getProperty(
		COPropertiesType.ASM_RESOURCE_TYPE);
    }

    public String getDocName() {
	String docName = super.getDocName();
	if (docName == null)
	    docName = getCoMessage("UndefinedDocument");
	return docName;
    }

    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.LICENSE,
		getEditor().getLicence());
	getModel().getResource().addProperty(COPropertiesType.DESCRIPTION,
		getEditor().getResourceDescription());
	//Document resource type	
	getModel().getResource().addProperty(COPropertiesType.ASM_RESOURCE_TYPE,
		getEditor().getTypeDocument());
    }

    // TODO Hack to remove bad links to resources inserted in the XML. Remove
    // this
    // as soon as the cause is found and corrected.
    public String validateLinkLabel(String link) {

	if (link.contains("><")) {
	    link = link.substring(link.indexOf("><"), link.length() - 5);
	}
	String linkLabel = link;
	StringTokenizer linkTokenizer = new StringTokenizer(link, "><");

	if (linkTokenizer.hasMoreTokens()) {
	    linkTokenizer.nextToken();
	}

	if (linkTokenizer.hasMoreTokens()) {
	    linkLabel = linkTokenizer.nextToken();
	}
	return linkLabel;
    }
}
