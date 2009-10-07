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

package org.sakaiquebec.opensyllabus.manager.api;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * This is the service that provides the necessary methods to manage a course
 * outline (change course outline language ...). It also gives access to some
 * administrative services depending on the user role (create site ...).
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylManagerService {

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

    /**
     * Name of the chs folder in which temporary files will be stored
     */
    public static final String TEMP_DIRECTORY = "temp";

    /**
     * Name of the course outline xml file
     */
    public static final String CO_XML_FILENAME = "syllabus.xml";

    /**
     * Create a Course outline using the xml reference
     * 
     * @param xmlReference
     * @param siteId
     */
    public void readXML(String xmlReference, String siteId);

    /**
     * Create a Course outline using the zip reference
     * 
     * @param zipReference
     * @param siteId
     */
    public void readZip(String zipReference, String siteId);

    /**
     * This method return a list of the files contained in the same zip file as
     * the xml Data. getXmlDataFromZip should be called before this method
     * otherwise you will have a null pointer exception.
     * 
     * @return files, empty if there no file other than the xml file, key is the
     *         file, value is the real file Name to use
     */
    public Map<File, String> getImportedFiles();

    /**
     * A method that allows us to add a new resource in site resource tool
     * 
     * @param name
     * @param content
     * @param contentType
     * @param siteId
     * @return the resource name, throw an exception if the resource wasn't
     *         created
     * @throws Exception
     */
    // TODO: Quand la refactorisation sera terminee, il faut penser a supprimer
    // cette
    // methode et passer par le service correspondant
    public String addRessource(String name, InputStream content,
	    String contentType, String siteId) throws Exception;

    /**
     * Get Map<id,name> of sites with OsylTool
     * 
     * @return
     */
    public Map<String, String> getOsylSitesMap();

    /**
     * Construct a package which contains course Outline and files
     * 
     * @return url of the package file
     */
    public String getOsylPackage(String siteId);

    public Map<String, String> getOsylSites(String siteId);

    public String getParent(String siteId) throws Exception;

    public void associate(String siteId, String parentId) throws Exception;

    public void dissociate(String siteId, String parentId) throws Exception;

    public Boolean associateToCM(String courseSectionId, String siteId);

    /**
     * This method retrieves all the course sections registered in the course
     * management
     * 
     * @return list of the course sections
     */
    public Map<String, String> getCMCourses();

    /**
     * This method retrieves all sites that contains an OpenSyllabus tool and a
     * published course outline
     * 
     * @return list of the site that contain a opensyllabus tool and that has a
     *         published course outline
     */
    public Map<String, String> getPublishedOsylSites();
}