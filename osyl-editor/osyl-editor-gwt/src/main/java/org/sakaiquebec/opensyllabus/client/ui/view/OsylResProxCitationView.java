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

import com.google.gwt.user.client.Window;

/**
 * Class providing display and edition capabilities for citations resources.
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
	String text = getModel().getLabel();
	if (getEditor().isInEditionMode()) {
	    return text;
	} else {
	    return generateHTMLLink(getLinkURI(), text);
	}
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
	// TODO setProperty(COPropertiesType.LINK, uri);

	String uri = getEditor().getResourceURI();

	getModel().addProperty(COPropertiesType.TEXT,
		getEditor().getDescription());

	String title =
		getEditor().getSelectedCitationProperty(CitationSchema.TITLE);
	getModel().setLabel(title.substring(0, Math.min(title.length(), 30)));
	if (uri != null) {
	    setProperty(COPropertiesType.URI, uri);
	}
	setModelPropertyWithEditorProperty(CitationSchema.CITATIONID);
	setModelPropertyWithEditorProperty(CitationSchema.TYPE);
	setModelPropertyWithEditorProperty(CitationSchema.LONGTEXT);
	setModelPropertyWithEditorProperty(CitationSchema.SHORTTEXT);
	setModelPropertyWithEditorProperty(CitationSchema.NUMBER);
	setModelPropertyWithEditorProperty(CitationSchema.DATE);
	setModelPropertyWithEditorProperty(CitationSchema.TITLE);
	setModelPropertyWithEditorProperty(CitationSchema.EDITOR);
	setModelPropertyWithEditorProperty(CitationSchema.CREATOR);
	setModelPropertyWithEditorProperty(CitationSchema.VOLUME);
	setModelPropertyWithEditorProperty(CitationSchema.ISSUE);
	setModelPropertyWithEditorProperty(CitationSchema.PAGES);
	setModelPropertyWithEditorProperty(CitationSchema.PUBLISHER);
	setModelPropertyWithEditorProperty(CitationSchema.YEAR);
	setModelPropertyWithEditorProperty(CitationSchema.ISN);
	setModelPropertyWithEditorProperty(CitationSchema.DOI);
	setModelPropertyWithEditorProperty(CitationSchema.SOURCE_TITLE);
	setModelPropertyWithEditorProperty(CitationSchema.URL);
    }

    /**
     * {@inheritDoc}
     */
    protected void updateMetaInfo() {
	super.updateMetaInfo();
	setAvailableInBookstore(getEditor().isAvailableInBookstore());
	setAvailableInLibrary(getEditor().isAvailableInLibrary());
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDocName() {
	String docName=super.getDocName();
	if (docName == null)
	    docName = getCoMessage("UndefinedCitation");
	return docName;
    }

    // Link
    public String getTextFromModelLink() {
	String link =
		getModel().getResource().getProperty(COPropertiesType.LINK);
	if (null == link || "".equals(link)) {
	    return "";
	}

	if (getEditor().isInEditionMode()) {
	    return link;
	} else {
	    return generateHTMLLink(link, link);
	}
    }

    /**
     * 
     * @return true if available in library
     */
    public boolean isAvailableInLibrary() {
	return "true".equals(getModel().getProperty(
		COPropertiesType.AVAILABILITY_LIB));
    }

    public void setAvailableInLibrary(boolean b) {
	String booleanValue = "" + (b);
	getModel().addProperty(COPropertiesType.AVAILABILITY_LIB, booleanValue);
    }

    /**
     * 
     * @return true if available in bookstore
     */
    public boolean isAvailableInBookstore() {
	return "true".equals(getModel().getProperty(
		COPropertiesType.AVAILABILITY_COOP));
    }

    public void setAvailableInBookstore(boolean b) {
	String booleanValue = "" + (b);
	getModel()
		.addProperty(COPropertiesType.AVAILABILITY_COOP, booleanValue);
    }

    private void setModelPropertyWithEditorProperty(String property) {
	String value = getEditor().getSelectedCitationProperty(property);
	if (value!=null && !value.equals("undefined") && value != "")
	    setProperty(property, value);

    }

    public String getCitationId(){
	return getProperty(CitationSchema.CITATIONID);
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
     * @return A link or a simple string if link could not be created
     */
    public String getCitationPreviewAsLink() {
	
	String link = "";
	String url = getProperty(CitationSchema.URL);
	if (url != null)
	    link = generateHTMLLink(url , getCitationPreview());
	else 
	    link = getCitationPreview();
	return link;
	
    }
    
    /**
     * Generate a link, if possible, with citation informations
     * @return A link or a simple string if link could not be created
     */
    public String getCitationsInfosAsLink(OsylCitationItem citationItem) {
	String url = citationItem.getUrl();
	
	if ( url == null)
	    return citationItem.getCitationsInfos();
	else 
	  return  generateHTMLLink(url, citationItem.getCitationsInfos()); 
	
    }

    /**
     * 
     * @return 
     */
    public String getCitationPreview() {
	String infos = "";
	String type = getProperty(CitationSchema.TYPE);
	if (!isDocumentDefined()) {
	    return getCoMessage("UndefinedCitation");
	} else {
	    // 3 cases: Book, Article, Other
	    if (type.equals(CitationSchema.TYPE_BOOK)) {
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
