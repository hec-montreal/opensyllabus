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

import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

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

    /**
     * Name of the chs folder in which the course outline content will be
     * stored.
     */
    public static final String WORK_DIRECTORY = "work";

    /**
     * Name of the chs folder in which the course outline content will be
     * published.
     */
    public static final String PUBLISH_DIRECTORY = "publish";

    /** Path to use to generate a valid URL of the citation list folder. */
    public static final String REFERENCES_PATH = "/citation/content/group/";

    /** Name of the citation list folder in this course outline. */
    public static final String REFERENCES_DIRECTORY = "references";

    /** Name of the citation list folder in this course outline. */
    public static final String PROP_CITATION_COLLECTION_ID =
	    "citation_collection_id";

    /**
     * retourne le xsl au client pour permettre une prévisualisation du plan de
     * cours avec le xml
     * 
     * @param group
     * @return
     */
    public String getXslForGroup(String group, String webappdir);

    /**
     * Adds a new resource to the current context EDITOR
     * 
     * @return a String the id of the new resource
     */
    public String addRessource(String name, byte[] content, String contentType,
	    String path, String role) throws Exception;

    /**
     * Adds or update an assignment to the site. This method checks
     * whether the Assignment tool is already integrated into the site, if it
     * isn't, it is automatically added.
     */
    public String createOrUpdateAssignment(String assignmentId, String title,
	    String instructions, int openYear, int openMonth, int openDay,
	    int openHour, int openMinute, int closeYear, int closeMonth,
	    int closeDay, int closeHour, int closeMinute, int percentage);

    /**
     * Adds or update an assignment to the site. This method checks
     * whether the Assignment tool is already integrated into the site, if it
     * isn't, it is automatically added.
     */
    public String createOrUpdateAssignment(String assignmentId, String title);

    /**
     * Delete an assignment
     */
    public void removeAssignment(String assignmentId);

    /**
     * Delete a citation from the course outline citation list
     */
    public void removeCitation(String citationId);

    /**
     * Add or updates a citation in the course outline citation list
     */
    public String createOrUpdateCitation(String citationListId, String citation, String author, String type, 
	    String isbnIssn, String link);

    /**
     * Creates a temporary citation list that will contain the citation created.
     * With this we won't need another callback from the server to retrieve the
     * citationlistid to be saved in the XML. If the user cancels his creation
     * process, we remove this citation list
     * 
     * @return citationListId : the id of the new citation list
     */
    public String createTemporaryCitationList();

    /**
     * @return   a ResourcesLicencingInfo object which contains informations about Licencing on resources
     * @uml.property  name="resourceLicenceInfo"
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

}