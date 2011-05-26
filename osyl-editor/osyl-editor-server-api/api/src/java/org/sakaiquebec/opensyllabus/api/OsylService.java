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

package org.sakaiquebec.opensyllabus.api;

import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

/**
 * This interface intents to replace the resource service interface. It lists
 * all functions and methods related to Osyl Editor and the resources or tools
 * used in it. It acts as a hub to other sakai tools, such as assignment and
 * citations. It works together with the SDATA servlet.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @author <a href="mailto:mame-awa.diopy@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylService {

    /**
     * Create work and publish directories automatically if they don't exists.
     * Also applies default permission level
     */
    public void initService() throws Exception;

    /**
     * Name of the chs folder in which the xslt files used to transform the xml
     * content of the course outline
     */
    public static final String XSLT_DIRECTORY = "xslt";

    /** Path to use to generate a valid URL of the citation list folder. */
    public static final String REFERENCES_PATH = "/citation/content/group/";

    /** Name of the citation list folder in this course outline. */
    public static final String REFERENCES_DIRECTORY = "references";

    /** Name of the citation list folder in this course outline. */
    public static final String PROP_CITATION_COLLECTION_ID =
	    "citation_collection_id";

    public static final String CACHE_ENABLED_CONFIG_KEY =
	    "opensyllabus.cache.enabled";

    /**
     * retourne le xsl au client pour permettre une prévisualisation du plan de
     * cours avec le xml
     * 
     * @param group
     * @return
     */
    public String getXslForGroup(String group, String webappdir);

    /**
     * Links a collection of citations to a site
     * 
     * @param collection
     * @param siteId
     * @return the saved collection
     */
    public CitationCollection linkCitationsToSite(
	    CitationCollection collection, String siteId, String citationTitle);

    /**
     * @return a ResourcesLicencingInfo object which contains informations about
     *         Licencing on resources
     * @uml.property name="resourceLicenceInfo"
     * @uml.associationEnd
     */
    public ResourcesLicencingInfo getResourceLicenceInfo();

    /**
     * Checks if the current site has a relation (child - parent) with the site
     * containing the resource. If it is the case, we allow the site to access
     * to the resource
     * 
     * @param resourceURI
     * @return
     */
    public boolean checkSitesRelation(String resourceURI);

    public SakaiEntities getExistingEntities(String siteId);

    /**
     * Returns true if cache is enabled in the sakai.properties file. This is an
     * experimental optimization.
     * 
     * @return whether cache is enabled
     */
    public boolean isCacheEnabled();

}