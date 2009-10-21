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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
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
import org.sakaiquebec.opensyllabus.common.api.OsylRealmService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * Implementation of the <code>OsylSiteService</code>
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylSiteServiceImpl implements OsylSiteService {

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

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
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
    }

    /** Destroy method to be called by Spring */
    public void destroy() {
	log.info("DESTROY from OsylSite service");
    }

    private COModeledServer getFusionnedPublishedHierarchy(String siteId) {
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
			getFusionnedPublishedHierarchy(parentId);
		if (parentModel != null && coModeled != null) {
		    coModeled.XML2Model();
		    parentModel.XML2Model();
		    coModeled.fusion(parentModel);
		}

	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	}

	return coModeled;
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId) {
	try {
	    COSerialized co =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    if (co != null) {
		getSiteInfo(co, siteId);
		COModeledServer coModelChild = new COModeledServer(co);
		// récupération des parents
		String parentId = null;
		try {
		    parentId = coRelationDao.getParentOfCourseOutline(siteId);
		} catch (Exception e) {
		}
		if (parentId != null) {

		    // fusion
		    COModeledServer coModelParent =
			    getFusionnedPublishedHierarchy(parentId);

		    if (coModelParent != null) {
			coModelChild.XML2Model();
			coModelChild.fusion(coModelParent);
			coModelChild.model2XML();
			co.setContent(coModelChild.getSerializedContent());
		    }

		}
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
    public String createSite(String siteTitle, String configId)
	    throws Exception {
	Site site = null;
	if (!siteService.siteExists(siteTitle)) {
	    site = siteService.addSite(siteTitle, "osylEditor");
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(true);

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
		coConfig = configDao.getConfig(configId);
		co =
			new COSerialized(idManager.createUuid(),
				osylConfigService.getCurrentLocale(), "shared",
				"", site.getId(), "sectionId", coConfig, null,
				"shortDescription", "description", "title",
				false);
		resourceDao.createOrUpdateCourseOutline(co);

	    } catch (Exception e) {
		log.error("createSite", e);
	    }

	} else {
	    site = siteService.getSite(siteTitle);
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
				getXmlStringFromFile(coConfig, webappDir),
				"shortDescription", "description", "title",
				false);
		// reinitilaisation des uuids
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetUuid();
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else if (thisCo.getContent() == null) {
		coConfig = thisCo.getOsylConfig();
		thisCo.setContent(getXmlStringFromFile(coConfig, webappDir));
		// reinitilaisation des uuids
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetUuid();
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else {
		configSiteProperty = getSiteConfigProperty(thisCo.getSiteId());
		if (configSiteProperty == null)
		    coConfig = thisCo.getOsylConfig();
		else
		    coConfig =
			    configDao.getConfigByRef("osylcoconfigs"
				    + File.separator
				    + configSiteProperty.toString());
	    }

	    thisCo =
		    osylConfigService.fillCo(webappDir
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
    public COSerialized importDataInCO(String xmlData, String siteId) {
	COSerialized co = null;

	try {
	    co = getSerializedCourseOutlineBySiteId(siteId);
	    if (co == null) {

	    } else {
		co.setContent(xmlData);
	    }

	    resourceDao.createOrUpdateCourseOutline(co);

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
	    String webappDir) {
	StringBuilder fileData = new StringBuilder(1000);
	try {

	    BufferedReader reader =
		    getXmlTemplateFileReader(coConfig, webappDir);

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
	    COConfigSerialized coConfig, String webappDir) {
	File coXmlFile = null;
	String coXmlFilePath = null;
	BufferedReader reader = null;
	try {
	    coXmlFilePath =
		    webappDir + File.separator + coConfig.getConfigRef()
			    + File.separator + CO_CONTENT_TEMPLATE + "_"
			    + osylConfigService.getCurrentLocale()
			    + OsylSiteService.XML_FILE_EXTENSION;
	    coXmlFile = new File(coXmlFilePath);
	    reader =
		    new BufferedReader(new InputStreamReader(
			    new FileInputStream(coXmlFile), "UTF-8"));
	} catch (FileNotFoundException e) {
	    try {
		coXmlFilePath =
			webappDir
				+ File.separator
				+ osylConfigService.getCurrentConfig(webappDir)
					.getConfigRef() + File.separator
				+ CO_CONTENT_TEMPLATE
				+ OsylSiteService.XML_FILE_EXTENSION;
		coXmlFile = new File(coXmlFilePath);
		reader =
			new BufferedReader(new InputStreamReader(
				new FileInputStream(coXmlFile), "UTF-8"));
	    } catch (Exception e1) {
		log.error(e.getLocalizedMessage(), e1);
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
			    getFusionnedPublishedHierarchy(parentId);

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
		    } else {
			throw new Exception();// TODO
		    }
		}
	    } else {
		throw new Exception();// TODO
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
			    getFusionnedPublishedHierarchy(parentId);

		    if (coModelParent != null) {
			coModelChild.XML2Model();
			coModelChild.dissociate(coModelParent);
			coModelChild.model2XML();
			co.setContent(coModelChild.getSerializedContent());
			resourceDao.createOrUpdateCourseOutline(co);
		    }
		    coRelationDao.removeRelation(siteId, parentId);
		}
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	    throw e;
	}
    }
}
