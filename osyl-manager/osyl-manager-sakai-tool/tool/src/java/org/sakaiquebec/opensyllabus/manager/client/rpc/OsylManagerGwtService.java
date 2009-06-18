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

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface OsylManagerGwtService extends RemoteService {

    /**
     * Creates a site with siteTitle title.
     * 
     * @param siteTitle
     * @return
     */
    public String createSite(String siteTitle, String configId);

    public Map<String, String> getOsylConfigs();

    /**
     * Reads the XML and creates a course outline.
     * 
     * @param xmlReference
     * @param siteId
     */
    public void readXML(String xmlReference, String siteId);

    /**
     * Reads the archive file.
     * 
     * @param xmlReference
     * @param siteId
     */
    public void readZip(String xmlReference, String siteId);

    public Map<String, String> getOsylSitesMap();

    public String getOsylPackage(String siteId);

    public Map<String, String> getOsylSites(String siteId);

    public String getParent(String siteId);

    public void associate(String siteId, String parentId) throws Exception;

    public void dissociate(String siteId, String parentId) throws Exception;
}
