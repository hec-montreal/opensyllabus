/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylLinkEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * Class providing display and edition capabilities for Hyperlink resources.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxLinkView extends OsylAbstractResProxView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxLinkView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylLinkEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected void updateModel() {
	updateMetaInfo();
	String formerLink =
		getModel().getResource().getProperty(
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	if (formerLink != null
		&& !formerLink.trim().equals("")
		&& !(((OsylLinkEditor) getEditor()).getLink())
			.equals(formerLink)) {
	    if (OsylEditorEntryPoint.getInstance().getResourceContextTypeMap()
		    .get(formerLink) != null){
		OsylEditorEntryPoint.getInstance().getResourceContextTypeMap()
			.get(formerLink).remove(getModel().getId());
	    }
	    // DocumentContextVisibilityMap
	    if (OsylEditorEntryPoint.getInstance()
		    .getResourceContextVisibilityMap().get(formerLink) != null){
		OsylEditorEntryPoint.getInstance()
			.getResourceContextVisibilityMap().get(formerLink)
			.remove(getModel().getId());
	    }
	}

	getModel().setLabel(getEditor().getText());
	getModel().addProperty(COPropertiesType.COMMENT,
		((OsylLinkEditor) getEditor()).getDescription());
	// Type of link resource
	getModel().getResource().addProperty(
		COPropertiesType.ASM_RESOURCE_TYPE,
		((OsylLinkEditor) getEditor()).getTypeLinkSelected());

	getModel().getResource().addProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_URI,
		((OsylLinkEditor) getEditor()).getLink());

	// update resContextVisibilitymap
	Map<String, String> cv =
		OsylEditorEntryPoint.getInstance()
			.getResourceContextVisibilityMap()
			.get(((OsylLinkEditor) getEditor()).getLink());
	if (cv == null) {
	    cv = new HashMap<String, String>();
	}
	cv.put(getModel().getId(),
		getModel().getProperty(COPropertiesType.VISIBILITY));
	OsylEditorEntryPoint.getInstance().getResourceContextVisibilityMap()
		.put(((OsylLinkEditor) getEditor()).getLink(), cv);

	// update resTypeContextVisibilitymap
	Map<String, String> cr =
		OsylEditorEntryPoint.getInstance().getResourceContextTypeMap()
			.get(((OsylLinkEditor) getEditor()).getLink());
	if (cr == null) {
	    cr = new HashMap<String, String>();
	}
	cr.put(getModel().getId(),
		getModel().getResource().getProperty(
			COPropertiesType.ASM_RESOURCE_TYPE));
	OsylEditorEntryPoint.getInstance().getResourceContextTypeMap()
		.put(((OsylLinkEditor) getEditor()).getLink(), cr);
    }

    /**
     * ===================== ADDED METHODS =====================
     */

    /**
     * Returns the text value of current model.
     */
    public String getTextFromModel() {
	String text = getModel().getLabel();
	if (getEditor().isInEditionMode()) {
	    return text;
	} else {
	    return generateHTMLLink(getLinkURI(), text);
	}
    }

    /**
     * Returns the description text of current resource.
     */
    public String getCommentFromModel() {
	return getModel().getProperty(COPropertiesType.COMMENT);
    }

    /**
     * Returns the type of resource text of current resource.
     */
    public String getResourceTypeFromModel() {
	return getModel().getResource().getProperty(
		COPropertiesType.ASM_RESOURCE_TYPE);
    }

    /**
     * Returns the URI of current resource
     */
    public String getLinkURI() {
	// We get the URI from the model
	String uri = getRawURI();

	if (uri.matches("^((https?|ftp)://.+)|(mailto:[^/]+)|(mailto://.+)$")) {
	    // If it's an external link we return it as is
	    return uri;
	} else {
	    // Otherwise we have to prepend Sakai stuff in the URI
	    OsylController controller = getController();
	    String url = GWT.getModuleBaseURL();
	    String serverId = url.split("\\s*/portal/site/\\s*")[0];
	    String siteId = controller.getSiteId();
	    String docFolder = controller.getDocFolderName();

	    return serverId + "/access/content/group/" + siteId + "/"
		    + (docFolder.equals("") ? "" : docFolder + "/") + uri;
	}
    } // getLinkURI

    /**
     * Returns the URI of current resource
     */
    public String getRawURI() {
	return getModel().getResource().getProperty(
		COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_URI);
    }

    @Override
    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.MODIFIED,
		OsylDateUtils.getCurrentDateAsXmlString());
    }
}
