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

import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.SessionCompatibilityException;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import java.io.File;
import java.io.InputStream;
import java.util.List;
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
     * Name of the chs folder in which temporary files will be stored
     */
    public static final String TEMP_DIRECTORY = "temp";

    // Special tool id for Home page
    public static final String SITE_INFORMATION_TOOL = "sakai.iframe.site";

    /**
     * Name of the course outline xml file
     */
    public static final String CO_XML_FILENAME = "syllabus.xml";

    /**
     * Tag used in the RIS file to save the citation id
     */
    public static final String CITATION_TAG = "CITATION_ID  - ";

    /**
     * Create a Course outline using the xml reference
     * 
     * @param xmlReference
     * @param siteId
     */
    public void readXML(String xmlReference, String siteId, String webapp)
	    throws Exception, OsylPermissionException;

    /**
     * Create a Course outline using the zip reference
     * 
     * @param zipReference
     * @param siteId
     */
    public void readZip(String zipReference, String siteId, String webapp)
	    throws Exception, OsylPermissionException;

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
	    String contentType, String siteId, String resourceOutputDir)
	    throws Exception;

    /**
     * Construct a package which contains course Outline and files
     * 
     * @return url of the package file
     */
    public String getOsylPackage(String siteId, String webappDir)
	    throws OsylPermissionException;

    public Map<String, String> getOsylSites(List<String> siteIds,
	    String searchTerm);

    public String getParent(String siteId) throws Exception;

    public void associate(String siteId, String parentId) throws Exception,
	    CompatibilityException, FusionException, OsylPermissionException,
	    SessionCompatibilityException;

    public void dissociate(String siteId, String parentId) throws Exception,
	    OsylPermissionException;

    public void associateToCM(String courseSectionId, String siteId)
	    throws Exception, OsylPermissionException;

    public void associateToCM(String courseSectionId, String siteId,
	    String webappDir) throws Exception, OsylPermissionException, SessionCompatibilityException;

    public void dissociateFromCM(String siteId) throws Exception,
	    OsylPermissionException;

    public void dissociateFromCM(String siteId, String webappDir)
	    throws Exception, OsylPermissionException;

    /**
     * This method retrieves all the course sections registered in the course
     * management and that startsWith the given text or numbers.
     * 
     * @param startsWith
     * @return
     */
    public List<CMCourse> getCMCourses(String startsWith);

    /**
     * Retrieve all the informations of the specified site and the course
     * outline it contains. The information is saved in a POJO.
     * 
     * @param siteId
     * @return
     */
    public COSite getCoAndSiteInfo(String siteId, String searchTerm,
	    String academicSession);

    /**
     * Retrieve the informations of all the sites the current user has access
     * to.
     * 
     * @return
     */
    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
	    String academicSession);

    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
    	    String academicSession, boolean withFrozenSites);

    public List<CMAcademicSession> getAcademicSessions();

    public String copySite(String siteFrom, String siteTo) throws Exception,
	    OsylPermissionException;

    public void deleteSite(String siteId) throws OsylPermissionException,
	    Exception;

    public String createSite(String siteTitle, String configRef, String lang, String templateId)
	    throws Exception, OsylPermissionException;

    public Map<String, Boolean> getPermissions();
    
    public Boolean isSuperUser();

}
