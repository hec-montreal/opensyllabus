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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationItem extends OsylAbstractBrowserItem implements
	Serializable {

    private static final long serialVersionUID = 1816353602345687468L;

    /**
     * the id (citationListId)
     */
    private String id;

    /**
     * the resource full path id
     */
    private String resourceId;

    /**
     * Name of the resource (citationList name)
     */
    private String resourceName;

    /**
     * Link to the Library
     */
    private String url;

    /**
     * Map of citation properties
     */
    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * empty constructor
     */
    public OsylCitationItem() {
    }

    /**
     * @return the Id (citationListId)
     */
    public String getId() {
	return id;
    }

    /**
     * @return the url to our library
     */
    public String getUrl() {
	return getProperty(CitationSchema.URL);
    }

    /**
     * @param url to our library
     */
    private void setUrl(String url) {
	this.url = url;
    }

    /**
     * @param Id the Id to set (citationListId)
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
	return resourceId;
    }

    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(String resourceId) {
	this.resourceId = resourceId;
    }

    /**
     * @return the resourceName (citationList name)
     */
    public String getResourceName() {
	return resourceName;
    }

    /**
     * @param resourceName the resourceName to set (citationList name)
     */
    public void setResourceName(String resourceName) {
	this.resourceName = resourceName;
    }

    /**
     * @return the properties of the citation
     */
    public Map<String, String> getProperties() {
	return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
	this.properties = properties;
    }

    /* ----------------- help methods below --------------------- */

    /**
     * Help method to access to a specific property
     */
    public String getProperty(String key) {
	return properties.get(key);
    }

    /**
     * Help method to set specific property
     * 
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
	properties.put(key, value);
    }

    /**
     * Help method
     * 
     * @return the title
     */
    public String getTitle() {
	return getProperty(CitationSchema.TITLE);
    }

    /**
     * Help method
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
	setProperty(CitationSchema.TITLE, title);
    }

    /**
     * Help method
     * 
     * @return the isn
     */
    public String getIsn() {
	return getProperty(CitationSchema.ISN);
    }

    /**
     * Help method
     * 
     * @param isn the isn to set
     */
    public void setIsn(String isn) {
	setProperty(CitationSchema.ISN, isn);
    }

    /**
     * Help method
     * 
     * @return the creator
     */
    public String getCreator() {
	return getProperty(CitationSchema.CREATOR);
    }

    /**
     * Help method
     * 
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
	setProperty(CitationSchema.CREATOR, creator);
    }

    /**
     * Help method
     * 
     * @return the parent dir of this ressource
     */
    public String getResourceDir() {
	String resourceDir = null;
	if (getResourceId() != null && getId() != null) {
	    int pathLength = getResourceId().length();
	    resourceDir =
		    getResourceId().substring(pathLength - getId().length());
	}
	return resourceDir;
    }

    @Override
    public boolean isFolder() {
	return false;
    }

    private String getPropertyValue(String property) {
	return getProperty(property) == null ? "" : getProperty(property);
    }

    public String getCitationsInfos() {
	String infos = "";
	// 3 cases: Book, Article, Other
	String type = getProperty(CitationSchema.TYPE);
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
			    : (getPropertyValue(CitationSchema.TITLE));
	    infos +=
		    getPropertyValue(CitationSchema.SOURCE_TITLE).equals("") ? ""
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
	return infos;
    }

    @Override
    public String getItemTag() {
	return "UtilityRemoteFileBrowser_citationTag";
    }

    @Override
    public boolean equals(Object obj) {
	OsylCitationItem oci = (OsylCitationItem) obj;
	if (getPropertyValue(CitationSchema.CITATIONID).equals(
		oci.getPropertyValue(CitationSchema.CITATIONID)))
	    return true;
	else
	    return false;
    }

}
