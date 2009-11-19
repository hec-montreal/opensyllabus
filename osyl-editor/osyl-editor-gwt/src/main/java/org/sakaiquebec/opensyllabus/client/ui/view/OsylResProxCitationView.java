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
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCitationEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;

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

	setModelPropertyWithEditorProperty(CitationSchema.DATE, getEditor()
		.getSelectedCitationProperty(CitationSchema.DATE));

	setModelPropertyWithEditorProperty(CitationSchema.TITLE, getEditor()
		.getSelectedCitationProperty(CitationSchema.TITLE));

	setModelPropertyWithEditorProperty(CitationSchema.EDITOR, getEditor()
		.getSelectedCitationProperty(CitationSchema.EDITOR));

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

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_LIBRARY, getEditor()
			.getSelectedCitationProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_LIBRARY));

	setModelPropertyWithEditorProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_URL, getEditor()
			.getSelectedCitationProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URL));
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
	if (value != null && !value.equals("undefined") && value != "")
	    setProperty(property, type, value);
    }

    private void setModelPropertyWithEditorProperty(String property,
	    String value) {
	if (value != null && !value.equals("undefined") && value != "")
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

    /**
     * Generate a link, if possible, with citation informations
     * 
     * @return A link or a simple string if link could not be created
     */
    public String getCitationPreviewAsLink() {

	String link = "";
	String url =
		getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URL);
	if (url == null || url.equalsIgnoreCase("undefined") || url.equals(""))
	    url =
		    getProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_LIBRARY);

	if (url == null || !url.equalsIgnoreCase("undefined") || url.equals(""))
	    link = generateHTMLLink(url, getCitationPreview());
	else
	    link = getCitationPreview();
	return link;

    }

    /**
     * Tells us whether the registered link points to the library catalog or
     * another source
     * 
     * @param citationItem
     * @return
     */
    public boolean isCitationLinkLibrary(OsylCitationItem citationItem) {

	String identifier =
		citationItem.getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_LIBRARY);

	if (identifier != null && !"undefined".equalsIgnoreCase(identifier))
	    return true;
	return false;
    }

    /**
     * Generate a link, if possible, with citation informations
     * 
     * @return A link or a simple string if link could not be created
     */
    public String getCitationsInfosAsLink(OsylCitationItem citationItem) {
	String url = citationItem.getUrl();

	if (url == null)
	    return citationItem.getCitationsInfos();
	else
	    return generateHTMLLink(url, citationItem.getCitationsInfos());

    }

    /**
     * @return
     */
    public String getCitationPreview() {
	String infos = "";
	String type = getProperty(CitationSchema.TYPE);
	if (!isDocumentDefined()) {
	    return getCoMessage("UndefinedCitation");
	} else {
	    // 3 cases: Book, Article, Other
	    if (type.equals(CitationSchema.TYPE_BOOK)
		    || type.equals(CitationSchema.TYPE_REPORT)) {
		// <auteurs>, <titre>, <édition>,<pages>, <éditeur>, <année>,
		// <ISBN>
		infos +=
			getPropertyValue(CitationSchema.CREATOR).equals("") ? ""
				: (getPropertyValue(CitationSchema.CREATOR) + ". ");
		infos +=
			getPropertyValue(CitationSchema.TITLE).equals("") ? ""
				: (getPropertyValue(CitationSchema.TITLE) + ", ");
		infos +=
			getPropertyValue(CitationSchema.PUBLISHER).equals("") ? ""
				: (getPropertyValue(CitationSchema.PUBLISHER) + ", ");
		infos +=
			getPropertyValue(CitationSchema.PAGES).equals("") ? ""
				: (getPropertyValue(CitationSchema.PAGES) + ", ");
		infos +=
			getPropertyValue(CitationSchema.EDITOR).equals("") ? ""
				: (getPropertyValue(CitationSchema.EDITOR) + ", ");
		infos +=
			getPropertyValue(CitationSchema.YEAR).equals("") ? ""
				: (getPropertyValue(CitationSchema.YEAR) + ", ");
		infos +=
			getPropertyValue(CitationSchema.ISN).equals("") ? ""
				: getPropertyValue(CitationSchema.ISN);
		infos += ".";

	    } else if (type.equals(CitationSchema.TYPE_ARTICLE)) {
		// <auteurs>, <titre>, <périodique>, <date>, <volume>,
		// <numéro>, <pages>, <ISSN>, <DOI>
		infos +=
			getPropertyValue(CitationSchema.CREATOR).equals("") ? ""
				: (getPropertyValue(CitationSchema.CREATOR) + ". ");
		infos +=
			getPropertyValue(CitationSchema.TITLE).equals("") ? ""
				: getPropertyValue(CitationSchema.TITLE);
		infos +=
			getPropertyValue(CitationSchema.SOURCE_TITLE)
				.equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.SOURCE_TITLE));
		infos +=
			getPropertyValue(CitationSchema.DATE).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.DATE));
		infos +=
			getPropertyValue(CitationSchema.VOLUME).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.VOLUME));
		infos +=
			getPropertyValue(CitationSchema.ISSUE).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.ISSUE));
		infos +=
			getPropertyValue(CitationSchema.PAGES).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.PAGES));
		infos +=
			getPropertyValue(CitationSchema.ISN).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.ISN));
		infos +=
			getPropertyValue(CitationSchema.DOI).equals("") ? ""
				: (", " + getPropertyValue(CitationSchema.DOI));
		infos += ".";
	    } else {
		infos +=
			getPropertyValue(CitationSchema.TITLE).equals("") ? ""
				: (getPropertyValue(CitationSchema.TITLE));
	    }
	}
	return infos;
    }
}
