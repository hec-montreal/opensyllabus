package org.sakaiquebec.opensyllabus.common.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

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
 *****************************************************************************/

/**
 * This interface contains all the variables and functions needed by all the
 * tools of OpenSyllabus (OsylEditor, OsylManager, OsylAdmin). It allows to
 * retrieve informations from a course outline and the site related to it.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylSiteService extends EntityProducer {

    // Arguments used by the entity broker
    public static final String REFERENCE_ROOT = Entity.SEPARATOR + "osyl";
    public static final String APPLICATION_ID = "sakai:osyl";

    public static final String XSLT_DIRECTORY = "xslt";

    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String XSL_FILE_EXTENSION = ".xsl";
    public static final String HTML_FILE_EXTENSION = ".html";

    public static final String XSL_PREFIX = "security_";

    public final static String SITE_TYPE = "course";

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

    /** A simple string used to format Urls. */
    public static final String CONTENT = "/content";

    public static final String FILE_DELIMITER = "\\.";

    /**
     * The default type of site we are creating
     */
    public static final String DEFAULT_SITE_TYPE = "course";

    /**
     * use to generate course management title
     */
    public final static String SUMMER = "E";

    public final static String WINTER = "H";

    public final static String FALL = "A";

    /**
     * Title of iFrame presenting rules and regulations (French Canadian).
     */
    public static final String HEC_MONTREAL_RULES_TITLE_FR_CA =
	    "Règlements de HEC Montréal";

    /**
     * Title of iFrame presenting rules and regulations (English).
     */
    public static final String HEC_MONTREAL_RULES_TITLE_EN =
	    "HEC Montréal Regulations";
    
    /**
     * Title of iFrame presenting rules and regulations (French Canadian).
     */
    public static final String HEC_WELCOME_FR_CA =
	    "Bienvenue dans ce cours!";
    /**
     * Title of iFrame presenting rules and regulations (English).
     */
    public static final String HEC_WELCOME_EN =
	    "Welcome in this course";
    
    /**
     * Base name of files containing a summary and links of rules and
     * regulations.
     */
    public static final String HEC_MONTREAL_RULES_FILE_BASE_NAME =
	    "/library/content/HEC_Montreal_rules_";

    public static final String HEC_MONTREAL_RULES_FILE_EXTENSION =
	    HTML_FILE_EXTENSION;

    /**
     * Get a valid resource reference base site URL to be used in later calls.
     * Will get deprecated very soon!
     * 
     * @return a String of the base URL
     */
    public String getCurrentSiteReference();

    /**
     * Returns whether a site with the specified title exists.
     * 
     * @param siteTitle
     * @return true or false
     * @throws Exception
     */
    public boolean siteExists(String siteTitle) throws Exception;

    /**
     * From the information given by the user, we create a new Site that will
     * contain a course outline that will be associated to a course of the
     * course management or will be a work site.
     * 
     * @param siteId
     * @param siteTitle
     * @param siteSession
     * @param siteGroup
     * @param siteProf
     * @return the id of the site
     * @throws Exception
     */
    public String createSite(String siteTitle, String configRef, String lang)
	    throws Exception;

    /**
     * From the information given by the user, we create a new Site that will
     * contain a sharable course outline.
     * 
     * @param siteId
     * @param siteTitle
     * @param siteSession
     * @param siteGroup
     * @param siteProf
     * @return the id of the site
     * @throws Exception
     */
    public String createSharableSite(String siteTitle, String configRef,
	    String lang) throws Exception;

    /**
     * Returns the actual site id of this context.
     * 
     * @return a String of the current site id.
     */
    public String getCurrentSiteId() throws Exception;

    /**
     * This method adds site info to the COSerialized.
     * 
     * @param co the COSerialized
     * @param siteId The current site id.
     * @throws Exception
     */
    public void getSiteInfo(COSerialized co, String siteId) throws Exception;

    /**
     * Check if the Co of the current context has been published
     * 
     * @return true if published
     */
    public boolean hasBeenPublished();

    /**
     * Check if a Co in edition exists for the give site
     * 
     * @return true if course line exists
     */
    public boolean hasCourseOutline(String siteId);

    /**
     * Check if the Co with the siteId has been published
     * 
     * @return true if published
     */
    public boolean hasBeenPublished(String siteId);

    /**
     * Returns the CourseOutline whose ID is specified.
     * 
     * @param String the course outline ID
     * @return the CourseOutline POJO corresponding to the specified ID
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutline(String coId, String webappDir)
	    throws Exception;

    /**
     * Returns the CourseOutline of the current context.
     * 
     * @return the CourseOutline POJO corresponding to the current context.
     * @throws FusionException
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutlineForEditor(String siteId,
	    String webappDir) throws Exception;

    /**
     * Returns the CourseOutline of the current context (version unfusionned).
     * 
     * @return the CourseOutline POJO corresponding to the current context.
     */
    public COSerialized getUnfusionnedSerializedCourseOutlineBySiteId(String id);

    /**
     * Saves the CourseOutline specified. The ID is returned. This is useful if
     * this instance has never been saved before (i.e.: its ID is -1). In this
     * case, it is the responsibility of the client application to keep track of
     * this new ID, notably to save it again at a later time.
     * 
     * @param COSerialized POJO
     * @return the CourseOutline ID
     * @throws Exception
     */
    public boolean updateSerializedCourseOutline(COSerialized co)
	    throws Exception;

    /**
     * We create a new course outline from a xmlContent
     * 
     * @param xmlData
     * @return
     */
    public COSerialized importDataInCO(String xmlData, String siteId,
	    Map<String, String> filenameChangesMap, String webapp)
	    throws Exception;

    public Site getSite(String siteId) throws IdUnusedException;

    public String getParent(String siteId) throws Exception;

    public List<String> getChildren(String siteId) throws Exception;

    public void associate(String siteId, String parentId) throws Exception,
	    CompatibilityException, FusionException;

    public void dissociate(String siteId, String parentId) throws Exception;

    public COModeledServer getFusionnedPrePublishedHierarchy(String siteId)
	    throws Exception, FusionException;

    public COSerialized updateCOCourseInformations(String siteId,
	    String webappDir) throws Exception;

    /**
     * Method used to release lock that the current user have on the current
     * site course outline
     */
    public void releaseLock();

    /**
     * Get the id of the config used by site identified by siteId
     * 
     * @param siteId
     * @return
     */
    public String getOsylConfigIdForSiteId(String siteId);

    /**
     * @return list of all Course outlines of the system
     */
    public List<COSerialized> getAllCO();

    /**
     * Convert the co to the most up to date version of osyl
     * 
     * @param co
     */
    public void convertAndSave(String webapp, COSerialized co) throws Exception;

    /**
     * Retrieves the site name that represents the association between the site
     * and the course management.
     * 
     * @param sectionId
     * @return
     */
    public String getSiteName(String sectionId);

    /**
     * Retrive date of last publication
     * 
     * @param siteId
     * @return
     */
    public Date getCoLastPublicationDate(String siteId);

    /**
     * Get date of last modification
     * 
     * @param siteId
     * @return
     */
    public Date getCoLastModifiedDate(String siteId);

    /**
     * Get co for export
     * 
     * @param siteId
     * @param webappDir
     * @return
     * @throws Exception
     */
    public COSerialized getCourseOutlineForExport(String siteId,
	    String webappDir) throws Exception;

    /**
     * delete site (all xml related to this site)
     * 
     * @param siteId
     * @throws Exception
     */
    public void deleteSite(String siteId) throws Exception;

    /**
     * @param co
     * @param webappDir
     * @throws Exception
     */
    public void setCoContentWithTemplate(COSerialized co, String webappDir)
	    throws Exception;

    /**
     * Add an annoucment in site
     * 
     * @param siteId
     * @param subject
     * @param body
     */
    public void addAnnounce(String siteId, String subject, String body);

}
