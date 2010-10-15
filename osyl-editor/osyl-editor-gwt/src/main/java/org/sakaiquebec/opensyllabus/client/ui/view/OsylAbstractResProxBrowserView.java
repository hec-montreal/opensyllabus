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
package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractBrowserEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylAbstractResProxBrowserView extends
	OsylAbstractResProxView {

    protected OsylAbstractResProxBrowserView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
    }

    public boolean isDocumentDefined() {
	return getRawURI() != null && !getRawURI().equals("");
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

    /**
     * Returns the text value of current model.
     */
    public String getTextFromSdataModel() {
	String text = getModel().getLabel();
	if (getEditor().isInEditionMode()) {
	    return text;
	} else {
	    return generateHTMLLink(getLinkSdataURI(), text);
	}
    }

    /**
     * Returns the URI of current resource
     */
    public String getLinkURI() {
	// We get the URI from the model
	String uri = getRawURI();

	if (uri.matches("^(https?|ftp|mailto)://.+")) {
	    // If it's an external link we return it as is
	    return uri;
	} else {
	    return "/access/content" + uri;
	}
    } // getLinkURI

    /**
     * Returns the URI of current resource
     */
    public String getLinkSdataURI() {
	// We get the URI from the model
	String uri = getRawURI();

	if (uri.matches("^(https?|ftp|mailto)://.+")) {
	    // If it's an external link we return it as is
	    return uri;
	} else {
	    return "/sdata/c" + uri + "?child=" + getController().getSiteId();
	}
    } // getLinkURI

    /**
     * Returns the URI of current resource
     */
    public String getRawURI() {
	return getModel().getResource().getProperty(
		COPropertiesType.IDENTIFIER, COPropertiesType.IDENTIFIER_TYPE_URI);
    } // getRawURI

    /**
     * Returns the description text of current resource.
     */
    public String getCommentFromModel() {
	return getModel().getProperty(COPropertiesType.COMMENT);
    }

    /**
     * Returns the document name (without any path information)
     * 
     * @return document name
     */
    public String getDocName() {
	String docName;
	if (isDocumentDefined()) {
	    docName = extractResourceName(getRawURI());
	} else {
	    docName = null;
	}
	return docName;
    }

    public String getDocPath() {
	String docPath;
	if (isDocumentDefined()) {
	    docPath = extractResourcePath(getRawURI());
	} else {
	    docPath = null;
	}
	return docPath;
    }

    public OsylAbstractBrowserEditor getEditor() {
	return (OsylAbstractBrowserEditor) super.getEditor();

    }

    @Override
    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.MODIFIED,
		getEditor().getLastModifiedDateString());
    }

}
