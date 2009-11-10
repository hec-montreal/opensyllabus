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
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationListItem extends OsylAbstractBrowserItem implements
	Serializable {

    private static final long serialVersionUID = 1816353682345687468L;

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

    private List<OsylAbstractBrowserItem> citations;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getResourceId() {
	return resourceId;
    }

    public void setResourceId(String resourceId) {
	this.resourceId = resourceId;
    }

    public String getResourceName() {
	return resourceName;
    }

    public void setResourceName(String resourceName) {
	this.resourceName = resourceName;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

    public List<OsylAbstractBrowserItem> getCitations() {
	return citations;
    }

    public void setCitations(List<OsylAbstractBrowserItem> citations) {
	this.citations = citations;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof OsylCitationListItem) {
	    OsylCitationListItem ofi = (OsylCitationListItem) obj;
	    if (getFilePath().equals(ofi.getFilePath()))
		return true;
	    else
		return false;
	} else {
	    return false;
	}
    }

    @Override
    public String getItemTag() {
	return "UtilityRemoteFileBrowser_citationListTag";
    }

    @Override
    public boolean isFolder() {
	return false;
    }

}
