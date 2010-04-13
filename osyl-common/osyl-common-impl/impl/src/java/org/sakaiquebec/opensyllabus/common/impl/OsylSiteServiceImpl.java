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
package org.sakaiquebec.opensyllabus.common.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylHierarchyService;
import org.sakaiquebec.opensyllabus.common.api.OsylRealmService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of the <code>OsylSiteService</code>
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylSiteServiceImpl implements OsylSiteService, EntityTransferrer {

    private static final String CO_CONTENT_TEMPLATE = "coContentTemplate";

    private static final Log log = LogFactory.getLog(OsylSiteServiceImpl.class);

    private ToolManager toolManager;

    private IdManager idManager;

    /** The config service to be injected by Spring */
    private OsylConfigService osylConfigService;

    /** The security service to be injected by Spring */
    private OsylSecurityService osylSecurityService;

    /** The chs to be injected by Spring */
    private ContentHostingService contentHostingService;

    /** The Osyl realm service to be injected by Spring */
    private OsylRealmService osylRealmService;

    /** The hierarchy service to be injected by Spring */
    private OsylHierarchyService osylHierarchyService;

    /** The site service to be injected by Spring */
    private SiteService siteService;

    private CORelationDao coRelationDao;

    /** The resouceDao to be injected by Spring */
    private ResourceDao resourceDao;

    /** The configDao to be injected by Spring */
    private COConfigDao configDao;

    /**
     * Sets the {@link OsylConfigService}.
     * 
     * @param osylConfigService
     */
    public void setConfigService(OsylConfigService osylConfigService) {
	this.osylConfigService = osylConfigService;
    }

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    public void setIdManager(IdManager idManager) {
	this.idManager = idManager;
    }

	/** Dependency: EntityManager. */
	protected EntityManager entityManager = null;

	/**
	 * Dependency: EntityManager.
	 * 
	 * @param service
	 *        The EntityManager.
	 */
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	
    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setOsylHierarchyService(OsylHierarchyService hierarchyService) {
	this.osylHierarchyService = hierarchyService;
    }

    /**
     * Sets the <code>ContentHostingService</code>.
     * 
     * @param contentHostingService
     */
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    /**
     * Sets the {@link CORelationDao}.
     * 
     * @param configDao
     */
    public void setCoRelationDao(CORelationDao relationDao) {
	this.coRelationDao = relationDao;
    }

    /**
     * Sets the {@link ResourceDao}.
     * 
     * @param resourceDao
     */
    public void setResourceDao(ResourceDao resourceDao) {
	this.resourceDao = resourceDao;
    }

    /**
     * Sets the {@link COConfigDao}.
     * 
     * @param configDao
     */
    public void setConfigDao(COConfigDao configDao) {
	this.configDao = configDao;
    }

    /**
     * Sets the <code>SiteService</code>.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    /**
     * Sets the <code>OsylRealmService</code>.
     * 
     * @param osylRealmService
     */
    public void setOsylRealmService(OsylRealmService osylRealmService) {
	this.osylRealmService = osylRealmService;
    }

    /**
     * @inherited
     */
    public String getSiteConfigProperty(String siteId) {
	Object configSiteProperty = "";

	try {
	    configSiteProperty =
		    siteService.getSite(siteId).getPropertiesEdit().get(
			    "configuration");
	} catch (Exception e) {
	    log.error("Unable to retrieve config site property", e);
	}
	// FIXME: this will never return null since it's initialized to "".
	if (configSiteProperty == null)
	    return null;
	else
	    return configSiteProperty.toString();
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylSite service");
	
	//We register the entity manager
	//entityManager.registerEntityProducer(this, REFERENCE_ROOT);
	
    }

    /** Destroy method to be called by Spring */
    public void destroy() {
	log.info("DESTROY from OsylSite service");
    }

    public COModeledServer getFusionnedPrePublishedHierarchy(String siteId) {
	COModeledServer coModeled = null;
	try {
	    COSerialized co =
		    resourceDao
			    .getPrePublishSerializedCourseOutlineBySiteId(siteId);
	    String parentId = null;
	    try {
		parentId = coRelationDao.getParentOfCourseOutline(siteId);
	    } catch (Exception e) {
	    }
	    coModeled = (co == null) ? null : new COModeledServer(co);
	    if (parentId == null) {
		if (coModeled != null)
		    coModeled.XML2Model();
		return coModeled;
	    } else {
		COModeledServer parentModel =
			getFusionnedPrePublishedHierarchy(parentId);
		if (parentModel != null && coModeled != null) {
		    coModeled.XML2Model();
		    coModeled.fusion(parentModel);
		}

	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	}

	return coModeled;
    }
    
    public String getSerializedCourseOutlineContentBySiteId(String siteId) {
    	String content = null;
    	
    	COSerialized co = getSerializedCourseOutlineBySiteId(siteId);
    	content = co.getContent();
    	
    	return content;
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId) {
	try {
	    COSerialized co;
	    if (osylSecurityService.isAllowedToEdit(siteId)) {
		co = resourceDao.getSerializedCourseOutlineBySiteId(siteId);
		if (co != null) {
		    getSiteInfo(co, siteId);
		    COModeledServer coModelChild = new COModeledServer(co);
		    // récupération des parents
		    String parentId = null;
		    try {
			parentId =
				coRelationDao.getParentOfCourseOutline(siteId);
		    } catch (Exception e) {
		    }
		    if (parentId != null) {

			// fusion
			COModeledServer coModelParent =
				getFusionnedPrePublishedHierarchy(parentId);

			if (coModelParent != null) {
			    coModelChild.XML2Model();
			    coModelChild.fusion(coModelParent);
			    coModelChild.model2XML();
			    co.setContent(coModelChild.getSerializedContent());
			}

		    }
		}
	    } else {
	    	if (osylSecurityService.getCurrentUserRole() == null){
	    		co =
				    resourceDao
					    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
						    siteId,
						    SecurityInterface.ACCESS_PUBLIC);
	    		return co;
		    }
		    
		if (osylSecurityService
			.getCurrentUserRole()
			.equals(
				OsylSecurityService.SECURITY_ROLE_COURSE_GENERAL_ASSISTANT)
			|| osylSecurityService
				.getCurrentUserRole()
				.equals(
					OsylSecurityService.SECURITY_ROLE_COURSE_TEACHING_ASSISTANT)
			|| osylSecurityService
				.getCurrentUserRole()
				.equals(
					OsylSecurityService.SECURITY_ROLE_COURSE_STUDENT)
			|| osylSecurityService
				.getCurrentUserRole()
				.equals(
					OsylSecurityService.SECURITY_ROLE_PROJECT_ACCESS))
		    co =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_ATTENDEE);
		else
		    co =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_PUBLIC);

	    }
	    return co;
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by siteId", e);
	}
	return null;
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getUnfusionnedSerializedCourseOutlineBySiteId(
	    String siteId) {
	try {
	    COSerialized co =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    return co;
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by siteId", e);
	}
	return null;
    }

    /**
     * {@inheritDoc}
     */
    public String createSite(String siteTitle, String configRef, String lang)
	    throws Exception {
	Site site = null;
	if (!siteService.siteExists(siteTitle)) {
	    site = siteService.addSite(siteTitle, "osylEditor");
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(false);

	    // we add the tools
	    addTool(site, "sakai.opensyllabus.tool");
	    addTool(site, "sakai.assignment.grades");
	    addTool(site, "sakai.resources");
	    addTool(site, "sakai.siteinfo");

	    siteService.save(site);

	    // we add the directories
	    String directoryId;
	    addCollection(WORK_DIRECTORY, site);
	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + WORK_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    addCollection(PUBLISH_DIRECTORY, site);

	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + PUBLISH_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    COConfigSerialized coConfig = null;
	    COSerialized co = null;

	    try {
		coConfig = configDao.getConfigByRef(configRef);
		co =
			new COSerialized(idManager.createUuid(), lang,
				"shared", "", site.getId(), "sectionId",
				coConfig, null, "shortDescription",
				"description", "title", false);
		resourceDao.createOrUpdateCourseOutline(co);

	    } catch (Exception e) {
		log.error("createSite", e);
	    }

	} else {
	    log.error("Could not create site because site with title='"
		    + siteTitle + "' already exists");
	    throw new Exception(
		    "Could not create site because site with title='"
			    + siteTitle + "' already exists");
	}
	return site.getId();
    }

    /**
     * {@inheritDoc}
     */
    public String createSharableSite(String siteTitle, String configRef, String lang)
	    throws Exception {
	Site site = null;
	if (!siteService.siteExists(siteTitle)) {
	    site = siteService.addSite(siteTitle, "osylEditor");
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(false);

	    // we add the tools
	    addTool(site, "sakai.opensyllabus.tool");
	    addTool(site, "sakai.resources");
	    addTool(site, "sakai.siteinfo");

	    siteService.save(site);

	    // we add the directories
	    String directoryId;
	    addCollection(WORK_DIRECTORY, site);
	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + WORK_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    addCollection(PUBLISH_DIRECTORY, site);

	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + PUBLISH_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    COConfigSerialized coConfig = null;
	    COSerialized co = null;

	    try {
		coConfig = configDao.getConfigByRef(configRef);
		co =
			new COSerialized(idManager.createUuid(), lang,
				"shared", "", site.getId(), "sectionId",
				coConfig, null, "shortDescription",
				"description", "title", false);
		resourceDao.createOrUpdateCourseOutline(co);

	    } catch (Exception e) {
		log.error("createSite", e);
	    }

	} else {
	    log.error("Could not create site because site with title='"
		    + siteTitle + "' already exists");
	    throw new Exception(
		    "Could not create site because site with title='"
			    + siteTitle + "' already exists");
	}
	return site.getId();
    }

    /**
     * Returns the site type of the realm service if there is one otherwise the
     * default site type
     * 
     * @return String the type of the site
     */
    private String getSiteType() {
	if (osylRealmService == null) {
	    return DEFAULT_SITE_TYPE;
	} else {
	    return osylRealmService.getSiteType();
	}
    }

    /**
     * Add a collection (similar to a sub-directory) under the resource tool.
     * 
     * @param dir name of collection
     * @param parent where to create it (null means top-level)
     * @return boolean whether the collection was added or not
     * @throws Exception
     */
    private void addCollection(String dir, Site site) throws Exception {
	ContentCollectionEdit collection = null;
	String id = null;

	id = getSiteReference(site) + dir;
	id = id.substring(8);
	try {
	    if (!collectionExist(id)) {
		collection = contentHostingService.addCollection(id);
		ResourcePropertiesEdit fileProperties =
			collection.getPropertiesEdit();
		fileProperties.addProperty(
			ResourceProperties.PROP_DISPLAY_NAME, dir);
		contentHostingService.commitCollection(collection);
	    }
	} catch (Exception e) {
	    log.error("Unable to add a collection", e);
	    throw e;
	}
    }

    /**
     * Tells if a collection is already created in sakai.
     * 
     * @param a String of the collection id.
     * @return boolean whether the collection exists
     */
    private boolean collectionExist(String siteId) {
	String collection = contentHostingService.getSiteCollection(siteId);

	if (collection == null)
	    return false;
	return true;
    }

    /**
     * Get a valid resource reference base site URL to be used in later calls.
     * 
     * @return a String of the base URL
     */
    private String getSiteReference(Site site) {
	String siteId = site.getId();
	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString = contentHostingService.getReference(val2);
	return refString;
    }

    /** {@inheritDoc} */
    public String getCurrentSiteReference() {
	String siteId = "";
	try {
	    siteId = getCurrentSiteId();
	} catch (Exception e) {
	    log.error("Unable to retrieve current site id", e);
	}
	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString = contentHostingService.getReference(val2);
	return refString;
    }

    /** {@inheritDoc} */
    public String getCurrentSiteId() throws Exception {

	String siteId = "";

	try {
	    siteId =
		    siteService.getSite(
			    toolManager.getCurrentPlacement().getContext())
			    .getId();
	} catch (IdUnusedException e) {
	    log.error("Get current site id - Id unused exception", e);
	    // We wrap the exception in a java.lang.Exception. This way our
	    // "client" doesn't have to know about IdUnusedException.
	    throw new Exception(e);
	}

	return siteId;
    }

    /** {@inheritDoc} */
    public void getSiteInfo(COSerialized co, String siteId) throws Exception {
	try {
	    Site site = getSite(siteId);
	    co.setTitle(site.getTitle());
	    co.setShortDescription(site.getShortDescription());
	    co.setDescription(site.getDescription());
	} catch (IdUnusedException e) {
	    log.error("Get site info - Id unused exception", e);
	    // We wrap the exception in a java.lang.Exception. This way our
	    // "client" doesn't have to know about IdUnusedException.
	    throw new Exception(e);
	}
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasBeenPublished() {
	try {
	    return hasBeenPublished(getCurrentSiteId());
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	}
	return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasBeenPublished(String siteId) {
	try {
	    return resourceDao.hasBeenPublished(siteId);
	} catch (Exception e) {
	    log.warn("Unable to see if course outline has been published", e);
	    return false;
	}
    }
    
    /** {@inheritDoc} */
    public boolean hasCourseOutline(String siteId){
	return resourceDao.hasCourseOutiline(siteId);
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getSerializedCourseOutline(String webappDir) {
	COSerialized thisCo = null;
	COConfigSerialized coConfig = null;
	String siteId = "";
	Object configSiteProperty = "";

	try {

	    siteId = getCurrentSiteId();
	    thisCo = getSerializedCourseOutlineBySiteId(siteId);

	    if (thisCo == null) {
		coConfig =
			osylConfigService
				.getConfigByRef(
					OsylConfigService.DEFAULT_CONFIG_REF,
					webappDir);
		thisCo =
			new COSerialized(idManager.createUuid(),
				osylConfigService.getCurrentLocale(), "shared",
				"", siteId, "sectionId", coConfig,
				getXmlStringFromFile(coConfig,
					osylConfigService.getCurrentLocale(),
					webappDir), "shortDescription",
				"description", "title", false);
		// reinitilaisation des uuids
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetXML(null);
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else if (thisCo.getContent() == null) {
		coConfig = thisCo.getOsylConfig();
		thisCo.setContent(getXmlStringFromFile(coConfig, thisCo
			.getLang(), webappDir));
		// reinitilaisation des uuids
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetXML(null);
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else {
		configSiteProperty = getSiteConfigProperty(thisCo.getSiteId());
		if (configSiteProperty == null)
		    coConfig = thisCo.getOsylConfig();
		else
		    coConfig =
			    configDao.getConfigByRef(configSiteProperty
				    .toString());
	    }

	    thisCo =
		    osylConfigService.fillCo(webappDir
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + coConfig.getConfigRef(), thisCo);
	    getSiteInfo(thisCo, thisCo.getSiteId());
	    // System.err.println("siteId : "+siteId);

	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	}

	return thisCo;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutline(String id, String webappDir)
	    throws Exception {
	try {
	    COSerialized co = getSerializedCourseOutlineBySiteId(id);
	    getSiteInfo(co, co.getSiteId());
	    return co;
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}

    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public String updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	log.info("Saving course outline [" + co.getTitle() + "]");

	try {
	    return resourceDao.createOrUpdateCourseOutline(co);
	} catch (Exception e) {
	    log.error("Unable to update course outline", e);
	    throw e;
	}
    }

    public String createOrUpdateCO(COSerialized co) {

	try {
	    return resourceDao.createOrUpdateCourseOutline(co);
	} catch (Exception e) {
	    log.error("Unable to create or update course outline", e);
	}
	return "";
    }

    /** {@inheritDoc} */
    public void addTool(Site site, String toolId) {
	SitePage page = site.addPage();
	Tool tool = toolManager.getTool(toolId);
	page.setTitle(tool.getTitle());
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	ToolConfiguration toolConf = page.addTool();
	toolConf.setTool(toolId, tool);
	toolConf.setTitle(tool.getTitle());
	toolConf.setLayoutHints("0,0");

	try {
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    log.error("Add tool - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Add tool - Permission exception", e);
	}

    }

    /** {@inheritDoc} */
    public COSerialized importDataInCO(String xmlData, String siteId, Map<String,String> filenameChangesMap) {
	COSerialized co = null;

	try {
	    co = getSerializedCourseOutlineBySiteId(siteId);
	    if (co != null) {
		co.setContent(xmlData);
		COModeledServer coModeledServer = new COModeledServer(co);
		coModeledServer.XML2Model();
		coModeledServer.resetXML(filenameChangesMap);
		coModeledServer.model2XML();
		co.setContent(coModeledServer.getSerializedContent());
		resourceDao.createOrUpdateCourseOutline(co);
	    }
	} catch (Exception e) {
	    log.error("CreateCOFromData", e);
	}
	return co;
    }

    /** {@inheritDoc} */
    public Site getSite(String siteId) throws IdUnusedException {
	return siteService.getSite(siteId);
    }

    /**
     * Reads the course outline xml template from a file located in the
     * osylcoconfigs directory.
     * 
     * @param webappDir The path to the webapp directory
     * @return
     */
    private String getXmlStringFromFile(COConfigSerialized coConfig,
	    String lang, String webappDir) {
	StringBuilder fileData = new StringBuilder(1000);
	try {

	    BufferedReader reader =
		    getXmlTemplateFileReader(coConfig, lang, webappDir);

	    char[] buf = new char[1024];
	    int numRead = 0;
	    while ((numRead = reader.read(buf)) != -1) {
		fileData.append(buf, 0, numRead);
	    }
	    reader.close();
	} catch (IOException e) {
	    log.error(e.getLocalizedMessage(), e);
	}
	return fileData.toString();
    }

    /**
     * Checks if the file of the co template exists, if not it takes the default
     * template file, and return a buffered reader on the file.
     * 
     * @param webappDir the location of the webapp
     * @return a BufferedReader on the appropriate template file.
     */
    private BufferedReader getXmlTemplateFileReader(
	    COConfigSerialized coConfig, String lang, String webappDir) {
	File coXmlFile = null;
	String coXmlFilePath = null;
	BufferedReader reader = null;
	String templateFileName = "";
	try {
	    templateFileName =
		    CO_CONTENT_TEMPLATE + "_" + lang
			    + OsylSiteService.XML_FILE_EXTENSION;
	    coXmlFilePath =
		    webappDir + OsylConfigService.CONFIG_DIR + File.separator
			    + coConfig.getConfigRef() + File.separator
			    + templateFileName;
	    coXmlFile = new File(coXmlFilePath);
	    reader =
		    new BufferedReader(new InputStreamReader(
			    new FileInputStream(coXmlFile), "UTF-8"));

	    log.info("Course outline created with template '"
		    + templateFileName + "' and config '"
		    + coConfig.getConfigRef() + "'");
	} catch (FileNotFoundException e) {
	    try {
		templateFileName =
			CO_CONTENT_TEMPLATE
				+ OsylSiteService.XML_FILE_EXTENSION;
		;
		coXmlFilePath =
			webappDir + OsylConfigService.CONFIG_DIR
				+ File.separator + coConfig.getConfigRef()
				+ File.separator + templateFileName;
		coXmlFile = new File(coXmlFilePath);
		reader =
			new BufferedReader(new InputStreamReader(
				new FileInputStream(coXmlFile), "UTF-8"));
		log.info("Course outline created with template '"
			+ templateFileName + "' and config '"
			+ coConfig.getConfigRef() + "'");
	    } catch (Exception e1) {
		try {
		    templateFileName =
			    CO_CONTENT_TEMPLATE
				    + OsylSiteService.XML_FILE_EXTENSION;
		    String defaultConfigRef =
			    osylConfigService.getCurrentConfig(webappDir)
				    .getConfigRef();
		    coXmlFilePath =
			    webappDir + OsylConfigService.CONFIG_DIR
				    + File.separator + defaultConfigRef
				    + File.separator + templateFileName;
		    coXmlFile = new File(coXmlFilePath);
		    reader =
			    new BufferedReader(new InputStreamReader(
				    new FileInputStream(coXmlFile), "UTF-8"));
		    log.info("Course outline created with template '"
			    + templateFileName + "' and config '"
			    + defaultConfigRef + "'");
		} catch (Exception e2) {
		    log.error("Could not created course oultine. "
			    + e2.getLocalizedMessage(), e2);
		}
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	}
	return reader;
    }

    /**
     * {@inheritDoc}
     */
    public String getParent(String siteId) throws Exception {
	try {
	    return coRelationDao.getParentOfCourseOutline(siteId);
	} catch (Exception e) {
	    return null;
	}
    }

    public void associate(String siteId, String parentId) throws Exception {
	COSerialized co;
	try {
	    co = resourceDao.getSerializedCourseOutlineBySiteId(siteId);

	    if (co != null) {
		getSiteInfo(co, siteId);

		if (parentId != null) {
		    COModeledServer coModelParent =
			    getFusionnedPrePublishedHierarchy(parentId);

		    if (coModelParent != null) {
			COModeledServer coModelChild = new COModeledServer(co);
			// if(co.getSerializedContent()==null){
			// coModelChild.setModeledContent(coModelParent.getModeledContent());
			// coModelChild.resetUuid();
			// }else{
			coModelChild.XML2Model();
			// }

			coModelChild.associate(coModelParent);
			coModelChild.model2XML();
			co.setContent(coModelChild.getSerializedContent());
			resourceDao.createOrUpdateCourseOutline(co);
			coRelationDao.createRelation(siteId, parentId);
			
			//We update the users
			osylHierarchyService.addOrUpdateUsers(siteId);
		    } else {
			throw new Exception("Parent course outline is null");
		    }
		}
	    } else {
		throw new Exception("Child course outline is null");
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	    throw e;
	}
    }

    public void dissociate(String siteId, String parentId) throws Exception {
	COSerialized co;
	try {
	    co = resourceDao.getSerializedCourseOutlineBySiteId(siteId);

	    if (co != null) {
		getSiteInfo(co, siteId);
		COModeledServer coModelChild = new COModeledServer(co);
		if (parentId != null) {
		    COModeledServer coModelParent =
			    getFusionnedPrePublishedHierarchy(parentId);

		    if (coModelParent != null) {
			coModelChild.XML2Model();
			coModelChild.dissociate(coModelParent);
			coModelChild.model2XML();
			co.setContent(coModelChild.getSerializedContent());
			resourceDao.createOrUpdateCourseOutline(co);
		    }
		    //We remove the users
		    osylHierarchyService.removeUsers(parentId, siteId);

		    coRelationDao.removeRelation(siteId, parentId);
		    
		}
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	    throw e;
	}
    }

	public String archive(String arg0, Document arg1, Stack arg2, String arg3,
			List arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		Collection rv = new Vector();

		try
		{

			if ("osyl".equals(ref.getSubType()))
			{
				rv.add(ref.getReference());
				
				ref.addSiteContextAuthzGroup(rv);
			}
		}
		catch (Exception e) 
		{
			log.error("OsylSiteServiceImpl:getEntityAuthzGroups - " + e);
			e.printStackTrace();
		}

		return rv;
	}

	public String getEntityDescription(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {		
		return "opensyllabus";
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map arg4, Map arg5, Set arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * from StringUtil.java
	 */
	protected String[] split(String source, String splitter)
	{
		// hold the results as we find them
		Vector rv = new Vector();
		int last = 0;
		int next = 0;
		do
		{
			// find next splitter in source
			next = source.indexOf(splitter, last);
			if (next != -1)
			{
				// isolate from last thru before next
				rv.add(source.substring(last, next));
				last = next + splitter.length();
			}
		}
		while (next != -1);
		if (last < source.length())
		{
			rv.add(source.substring(last, source.length()));
		}

		// convert to array
		return (String[]) rv.toArray(new String[rv.size()]);

	} // split

	public boolean parseEntityReference(String reference, Reference ref) {
		if (reference.startsWith(REFERENCE_ROOT))
		{
			//Looks like /osyl/siteid/osylId
			String[] parts = split(reference, Entity.SEPARATOR);

			String subType = null;
			String context = null;
			String id = null;
			String container = null;

			if (parts.length > 2)
			{
				// the site/context
				context = parts[2];

				// the id
				if (parts.length > 3)
				{
					id = parts[3];
				}
			}

			ref.set(APPLICATION_ID, subType, id, container, context);

			return true;
		}
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

	public String[] myToolIds() {
		String[] toolIds = { "sakai.openSyllabus" };
		return toolIds;
	}

	public void transferCopyEntities(String fromContext, String toContext, List ids) {
		// TODO Auto-generated method stub
		
	}

	public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
			 {
		// TODO Auto-generated method stub
		
	}
}
