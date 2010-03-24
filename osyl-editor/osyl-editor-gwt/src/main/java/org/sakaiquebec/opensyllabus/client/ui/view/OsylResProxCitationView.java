/*******************************************************************************
 * $Id: $
 *******************************************************************************
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.util.CitationFormatter;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCitationEditor;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;

import com.google.gwt.user.client.Window;

/**
 * Class providing display and edition capabilities for citations resources.
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 */
public class OsylResProxCitationView extends OsylAbstractResProxBrowserView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxCitationView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylCitationEditor(this));
	initView();
    }

    /**
     * {@inheritDoc}
     */
    public String getTextFromModel() {
	throw new IllegalStateException("Do not use getTextFromModel() for "
		+ "BiblioContext.");
    }

    /**
     * {@inheritDoc}
     */
    public OsylCitationEditor getEditor() {
	return (OsylCitationEditor) super.getEditor();
    }

    /**
     * {@inheritDoc}
     */
    protected void updateModel() {
	getModel().getResource().setProperties(new COProperties());
	updateMetaInfo();

	String uri = getEditor().getResourceURI();

	getModel().addProperty(COPropertiesType.COMMENT,
		getEditor().getDescription());

	if (uri != null) {
	    setProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI, uri
			    + "/"
			    + getEditor().getSelectedCitationProperty(
				    CitationSchema.CITATIONID));
	}
	setModelPropertyWithEditorProperty(COPropertiesType.RESOURCE_TYPE,
		getEditor().getSelectedCitationProperty(CitationSchema.TYPE));

	setModelPropertyWithEditorProperty(CitationSchema.LONGTEXT, getEditor()
		.getSelectedCitationProperty(CitationSchema.LONGTEXT));

	setModelPropertyWithEditorProperty(CitationSchema.SHORTTEXT,
		getEditor().getSelectedCitationProperty(
			CitationSchema.SHORTTEXT));
	setModelPropertyWithEditorProperty(CitationSchema.NUMBER, getEditor()
		.getSelectedCitationProperty(CitationSchema.NUMBER));

	setModelPropertyWithEditorProperty(CitationSchema.YEAR, getEditor()
		.getSelectedCitationProperty(CitationSchema.YEAR));

	setModelPropertyWithEditorProperty(CitationSchema.TITLE, getEditor()
		.getSelectedCitationProperty(CitationSchema.TITLE));

	setModelPropertyWithEditorProperty(CitationSchema.PUBLISHER,
		getEditor().getSelectedCitationProperty(
			CitationSchema.PUBLISHER));

	setModelPropertyWithEditorProperty(COPropertiesType.AUTHOR, getEditor()
		.getSelectedCitationProperty(CitationSchema.CREATOR));

	setModelPropertyWithEditorProperty(CitationSchema.VOLUME, getEditor()
		.getSelectedCitationProperty(CitationSchema.VOLUME));

	setModelPropertyWithEditorProperty(CitationSchema.ISSUE, getEditor()
		.getSelectedCitationProperty(CitationSchema.ISSUE));

	setModelPropertyWithEditorProperty(CitationSchema.PAGES, getEditor()
		.getSelectedCitationProperty(CitationSchema.PAGES));

	setModelPropertyWithEditorProperty(CitationSchema.PAGES, getEditor()
		.getSelectedCitationProperty(CitationSchema.PAGES));

	setModelPropertyWithEditorProperty(CitationSchema.PAGES, getEditor()
		.getSelectedCitationProperty(CitationSchema.PAGES));

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_ISN, getEditor()
			.getSelectedCitationProperty(CitationSchema.ISN));

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_DOI, getEditor()
			.getSelectedCitationProperty(CitationSchema.DOI));

	setModelPropertyWithEditorProperty(COPropertiesType.JOURNAL,
		getEditor().getSelectedCitationProperty(
			CitationSchema.SOURCE_TITLE));

	setModelPropertyWithEditorProperty(CitationSchema.PUBLICATION_LOCATION,
		getEditor().getSelectedCitationProperty(
			CitationSchema.PUBLICATION_LOCATION));

	String hasLink =
		getEditor().getSelectedCitationProperty(
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_NOLINK);
	if (hasLink != null && !hasLink.equals("")) {
	    getModel().getResource().removeProperty(
		    COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_LIBRARY);
	} else {
	    setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_LIBRARY, getEditor()
			    .getSelectedCitationProperty(
				    COPropertiesType.IDENTIFIER,
				    COPropertiesType.IDENTIFIER_TYPE_LIBRARY));
	}

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE, getEditor()
			.getSelectedCitationProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE));

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_OTHERLINK, getEditor()
			.getSelectedCitationProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_OTHERLINK));

	// setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
	// COPropertiesType.IDENTIFIER_TYPE_NOLINK, getEditor()
	// .getSelectedCitationProperty(
	// COPropertiesType.IDENTIFIER,
	// COPropertiesType.IDENTIFIER_TYPE_NOLINK));
    }

    /**
     * {@inheritDoc}
     */
    protected void updateMetaInfo() {
	super.updateMetaInfo();
	setAvailableInBookstore(getEditor().isAvailableInBookstore());
    }

    /**
     * {@inheritDoc}
     */
    public String getDocName() {
	String docName = super.getDocName();
	if (docName == null)
	    docName = getCoMessage("UndefinedCitation");
	return docName;
    }

    public String getRawURI() {
	String identifier_uri =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	if (identifier_uri != null) {
	    return identifier_uri.substring(0, identifier_uri.lastIndexOf("/"));
	} else
	    return null;
    }

    /**
     * @return true if available in bookstore
     */
    public boolean isAvailableInBookstore() {
	return "true"
		.equals(getModel().getProperty(COPropertiesType.BOOKSTORE));
    }

    public void setAvailableInBookstore(boolean b) {
	String booleanValue = "" + (b);
	getModel().addProperty(COPropertiesType.BOOKSTORE, booleanValue);
    }

    private void setModelPropertyWithEditorProperty(String property,
	    String type, String value) {
	if (value != null && !value.equals(""))
	    setProperty(property, type, value);
    }

    private void setModelPropertyWithEditorProperty(String property,
	    String value) {
	if (value != null && !value.equals(""))
	    setProperty(property, value);
    }

    public String getCitationId() {
	String identifier_uri =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	if (identifier_uri != null) {
	    return identifier_uri.substring(
		    identifier_uri.lastIndexOf("/") + 1, identifier_uri
			    .length());
	} else
	    return null;
    }

    /**
     * @param property
     * @return the property value
     */
    private String getPropertyValue(String property) {
	return getProperty(property) == null ? "" : getProperty(property);
    }

    public String getCitationLibraryLink() {
	String url =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_LIBRARY);
	if (url == null || url.equals(""))
	    return null;
	else
	    return url;

    }

    public String getCitationBookstoreLink() {
	String url =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE);
	if (url == null || url.equals(""))
	    return null;
	else
	    return url;

    }

    public String getCitationOtherLink() {
	String url =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK);
	if (url == null || url.equals(""))
	    return null;
	else
	    return url;

    }

    /**
     * @return
     */
    public String getCitationPreview() {
	String infos = "";
	if (!isDocumentDefined()) {
	    return getCoMessage("UndefinedCitation");
	} else {
	    String type = getProperty(COPropertiesType.RESOURCE_TYPE);
	    if (!type.equals(CitationSchema.TYPE_UNKNOWN)) {
		String format =
			getController().getSettings().getCitationFormat(type);
		if (format == null) {
		    OsylAlertDialog oad =
			    new OsylAlertDialog(getUiMessage("Global.error"),
				    getUiMessage(
					    "Citation.format.unknownFormat",
					    type));
		    oad.center();
		    oad.show();
		} else {
		    infos =
			    CitationFormatter.format(
				    (COContentResource) getModel()
					    .getResource(), format);
		}
	    } else {
		infos +=
			getPropertyValue(CitationSchema.TITLE).equals("") ? ""
				: (getPropertyValue(CitationSchema.TITLE));
	    }
	}
	return infos;
    }
}
