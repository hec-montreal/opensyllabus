/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.rpc;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface OsylManagerGwtServiceAsync {

    /**
     * Crates a site with siteTitle title.
     * 
     * @param siteTitle
     * @param callback
     */
    public void createSite(String siteTitle, String configId, String lang, AsyncCallback<String> callback);

    
    public void getOsylConfigs(AsyncCallback<Map<String, String>> callback);
    
    /**
     * Reads the XML data and creates a course outline form it.
     * 
     * @param xmlReference
     * @param siteId
     * @param callback
     */
    public void readXML(String xmlReference, String siteId,
	    AsyncCallback<Void> callback);

    /**
     * Reads a archive file.
     * 
     * @param xmlReference
     * @param siteId
     * @param callback
     */
    public void readZip(String xmlReference, String siteId,
	    AsyncCallback<Void> callback);

    public void getOsylSitesMap(AsyncCallback<Map<String, String>> callback);

    public void getOsylPackage(String siteId, AsyncCallback<String> callback);

    public void getOsylSites(String siteId,
	    AsyncCallback<Map<String, String>> callback);

    public void getParent(String siteId, AsyncCallback<String> callback);

    public void associate(String siteId, String parentId,
	    AsyncCallback<Void> callback);
    
    public void dissociate(String siteId, String parentId,
	    AsyncCallback<Void> callback);
    
    public void associateToCM (String courseSectionId, String siteId, AsyncCallback<Boolean> callback);
    
    public void getCMCourses( AsyncCallback<Map<String, String>> callback) ;
}
