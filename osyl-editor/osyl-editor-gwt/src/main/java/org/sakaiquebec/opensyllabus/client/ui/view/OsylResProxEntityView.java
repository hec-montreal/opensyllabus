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

import java.util.StringTokenizer;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylEntityEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * Class providing display and edition capabilities for Document resources.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxEntityView extends OsylAbstractResProxBrowserView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxEntityView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylEntityEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public OsylEntityEditor getEditor() {
	return (OsylEntityEditor) super.getEditor();
    }

    protected void updateModel() {
	updateMetaInfo();
	getModel().setLabel(getEditor().getText());
	getModel().addProperty(COPropertiesType.COMMENT,
		getEditor().getDescription());
	String uri = getEditor().getResourceURI();
	if (uri != null) {
	    getModel().getResource().addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI, uri);
	    getModel().getResource().addProperty(COPropertiesType.PROVIDER,
		    getEditor().getEntityProvider());
	}
    }

    public String getDocName() {
	String docName = super.getDocName();
	if (docName == null)
	    docName = getCoMessage("UndefinedEntity");
	return docName;
    }

    @Override
    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.MODIFIED,
		OsylDateUtils.getCurrentDateAsXmlString());
    }

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

    public String getLinkURI() {
	// We get the URI from the model
	String uri = getRawURI();
	return "/direct" + uri;
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
