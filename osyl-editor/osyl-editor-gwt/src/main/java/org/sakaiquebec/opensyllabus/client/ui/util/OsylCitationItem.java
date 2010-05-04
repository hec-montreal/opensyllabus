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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COProperty;
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
     * Map of citation properties
     */
    private COProperties properties = new COProperties();

    /**
     * empty constructor
     */
    public OsylCitationItem() {
    }

    public OsylCitationItem(String citationId, String citationListPath) {
	super.setFilePath(citationListPath);
	setProperty(CitationSchema.CITATIONID, citationId);
    }

    /**
     * @return the Id (citationListId)
     */
    public String getId() {
	return id;
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
    public COProperties getProperties() {
	return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(COProperties properties) {
	this.properties = properties;
    }

    /* ----------------- help methods below --------------------- */

    /**
     * Help method to access to a specific property
     */
    public String getProperty(String key) {
	return properties.getProperty(key);
    }

    public String getProperty(String key, String type) {
	return properties.getProperty(key, type);
    }
    
    public COProperty getCOProperty(String key, String type) {
	return properties.getCOProperty(key, type);
    }

    /**
     * Remove the specified property
     */
    public void removeProperty(String key) {
	properties.removeProperty(key);
    }

    /**
     * Remove the property with the specified type
     * 
     * @param key
     * @param type
     */
    public void removeProperty(String key, String type) {
	properties.removeProperty(key, type);
    }

    /**
     * Help method to set specific property
     * 
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
	properties.addProperty(key, value);
    }

    public void setProperty(String key, String type, String value) {
	properties.addProperty(key, type, value);
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
	String type = getProperty(CitationSchema.TYPE);
	OsylController controller = OsylController.getInstance();
	if (!type.equals(CitationSchema.TYPE_UNKNOWN)) {
	    String format = controller.getSettings().getCitationFormat(type);
	    if (format == null) {
		OsylAlertDialog oad =
			new OsylAlertDialog(controller
				.getUiMessage("Global.error"), controller
				.getUiMessages().getMessage(
					"Citation.format.unknownFormat", type));
		oad.center();
		oad.show();
	    } else {
		infos = CitationFormatter.format(this, format);
	    }
	} else {
	    infos =
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
	if (obj instanceof OsylCitationItem) {
	    OsylCitationItem oci = (OsylCitationItem) obj;
	    if (getPropertyValue(CitationSchema.CITATIONID).equals(
		    oci.getPropertyValue(CitationSchema.CITATIONID)))
		return true;
	    else
		return false;
	} else {
	    return false;
	}
    }
}
