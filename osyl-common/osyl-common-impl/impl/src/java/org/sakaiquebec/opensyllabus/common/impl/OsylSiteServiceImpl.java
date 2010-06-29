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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.presence.api.PresenceService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylHierarchyService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.helper.SchemaHelper;
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

    /** The hierarchy service to be injected by Spring */
    private OsylHierarchyService osylHierarchyService;

    private EventTrackingService eventTrackingService;

    /** The site service to be injected by Spring */
    private SiteService siteService;

    private CORelationDao coRelationDao;

    /** The resouceDao to be injected by Spring */
    private ResourceDao resourceDao;

    /** The configDao to be injected by Spring */
    private COConfigDao configDao;

    private CitationService citationService;

    private SessionManager sessionManager;

    private UserDirectoryService userDirectoryService;

    /**
     * Sets the <code>CitationService</code>.
     * 
     * @param citationService
     * @uml.property name="citationService"
     */
    public void setCitationService(CitationService citationService) {
	this.citationService = citationService;
    }

     /**
     *Course management service integration.
     */
    private CourseManagementService cmService;

    /**
     * @param cmService
     */
    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

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
     * @param service The EntityManager.
     */
    public void setEntityManager(EntityManager entityManager) {
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

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
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

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    public void setUserDirectoryService(
	    UserDirectoryService userDirectoryService) {
	this.userDirectoryService = userDirectoryService;
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylSite service");

	eventTrackingService.addObserver(new Observer() {

	    public void update(Observable o, Object arg) {
		Event e = (Event) arg;
		if (e.getEvent().equals(UsageSessionService.EVENT_LOGOUT)) {
		    resourceDao.clearLocksForSession(sessionManager
			    .getCurrentSession().getId());
		} else if (e.getEvent().equals(PresenceService.EVENT_ABSENCE)) {
		    // we leave the sakai site, so we can unlock the CO
		    String res = e.getResource();
		    String siteId =
			    res.substring(res.lastIndexOf("/") + 1, res
				    .lastIndexOf("-"));
		    try {
			COSerialized cos =
				resourceDao
					.getSerializedCourseOutlineBySiteId(siteId);
			if (cos.getLockedBy().equals(
				sessionManager.getCurrentSession().getId())) {
			    cos.setLockedBy(null);
			    resourceDao.createOrUpdateCourseOutline(cos);
			}
		    } catch (Exception ex) {
		    }

		}
	    }
	});

	// We register the entity manager
	// entityManager.registerEntityProducer(this, REFERENCE_ROOT);

    }

    /** Destroy method to be called by Spring */
    public void destroy() {
	log.info("DESTROY from OsylSite service");
    }

    public COModeledServer getFusionnedPrePublishedHierarchy(String siteId) {
	COModeledServer coModeled = null;
	COSerialized co = null;
	String parentId = null;
	try {
	    co =
		    resourceDao
			    .getPrePublishSerializedCourseOutlineBySiteId(siteId);
	    parentId = coRelationDao.getParentOfCourseOutline(siteId);
	} catch (Exception e) {
	}
	try {
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
		if (osylSecurityService.getCurrentUserRole() == null) {
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
					OsylSecurityService.SECURITY_ROLE_COURSE_HELPDESK)
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
    public boolean siteExists(String siteTitle) {
	try {
	    return siteService.siteExists(siteTitle);
	} catch (Exception e) {
	    log.error(e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * {@inheritDoc}
     */
    public String createSite(String siteTitle, String configRef, String lang)
	    throws Exception {
	Site site = null;
	if (!siteService.siteExists(siteTitle)) {
	    site = siteService.addSite(siteTitle, SITE_TYPE);
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(false);

	    // we add the tools
	    addTool(site, "sakai.opensyllabus.tool");
	    addTool(site, "sakai.assignment.grades");
	    addTool(site, "sakai.resources");
	    addTool(site, "sakai.siteinfo");

	    // we add the directories
	    String directoryId;
	    addCollection(WORK_DIRECTORY, site);
	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + WORK_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    // HIDE COLLECTION
	    ContentCollectionEdit cce =
		    contentHostingService.editCollection(directoryId);
	    cce.setHidden();
	    contentHostingService.commitCollection(cce);

	    // we add the default citationList
	    // TODO I18N
	    String citationListName = "Références bibliographiques du cours";

	    CitationCollection citationList = citationService.addCollection();

	    ContentResourceEdit cre =
		    contentHostingService.addResource(directoryId,
			    citationListName, null, 1);

	    cre.setResourceType(CitationService.CITATION_LIST_ID);
	    cre.setContentType(ResourceType.MIME_TYPE_HTML);

	    ResourcePropertiesEdit props = cre.getPropertiesEdit();
	    props
		    .addProperty(
			    ContentHostingService.PROP_ALTERNATE_REFERENCE,
			    org.sakaiproject.citation.api.CitationService.REFERENCE_ROOT);
	    props.addProperty(ResourceProperties.PROP_CONTENT_TYPE,
		    ResourceType.MIME_TYPE_HTML);
	    props.addProperty(ResourceProperties.PROP_DISPLAY_NAME,
		    citationListName);

	    cre.setContent(citationList.getId().getBytes());
	    contentHostingService.commitResource(cre,
		    NotificationService.NOTI_NONE);

	    addCollection(PUBLISH_DIRECTORY, site);
	    directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + PUBLISH_DIRECTORY + "/";
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    siteService.save(site);
	    COConfigSerialized coConfig = null;
	    COSerialized co = null;

	    try {
		coConfig = configDao.getConfigByRef(configRef);
		co =
			new COSerialized(idManager.createUuid(), lang,
				"shared", "", site.getId(), "sectionId",
				coConfig, null, "shortDescription",
				"description", "title", false, null, null);
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
    public String createSharableSite(String siteTitle, String configRef,
	    String lang) throws Exception {
	Site site = null;
	if (!siteService.siteExists(siteTitle)) {
	    site = siteService.addSite(siteTitle, SITE_TYPE);
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
				"description", "title", false, null, null);
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
     * Add a collection (similar to a sub-directory) under the resource tool.
     * 
     * @param dir name of collection
     * @param parent where to create it (null means top-level)
     * @return boolean whether the collection was added or not
     * @throws Exception
     */
    private void addCollection(String dir, Site site)  {
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
	    log.warn("Unable to add a collection", e);
	    
	}
    }

    /**
     * Tells if a collection is already created in sakai.
     * 
     * @param a String of the collection id.
     * @return boolean whether the collection exists
     */
    private boolean collectionExist(String id) {
	try {
	    contentHostingService.getCollection(id);
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
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
	    if (this.isCOLinkedToCourseManagement(siteId)) {
		String cmTitle = getCourseManagementTitle(siteId);
		String cmCourseNo = getCourseManagementCourseNo(siteId);
		if (cmTitle != null && cmCourseNo != null) {
		    co.setTitle(cmCourseNo + " - " + cmTitle);
		}
	    }
	    co.setShortDescription(site.getShortDescription());
	    co.setDescription(site.getDescription());
	} catch (IdUnusedException e) {
	    log.error("Get site info - Id unused exception", e);
	    // We wrap the exception in a java.lang.Exception. This way our
	    // "client" doesn't have to know about IdUnusedException.
	    throw new Exception(e);
	} catch (IdNotFoundException e) {
	    log.warn("Get site info - Id Not Found exception", e);
	    log
		    .warn("Get site info - Fail to retreive course management title");
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
    public boolean hasCourseOutline(String siteId) {
	return resourceDao.hasCourseOutiline(siteId);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized COSerialized getSerializedCourseOutline(String webappDir) {
	COSerialized thisCo = null;
	COConfigSerialized coConfig = null;
	String siteId = "";

	try {

	    siteId = getCurrentSiteId();
	    thisCo = getSerializedCourseOutlineBySiteId(siteId);

	    if (thisCo == null) {
		coConfig =
			osylConfigService.getConfigByRef(osylConfigService
				.getDefaultConfig(), webappDir);
		thisCo =
			new COSerialized(idManager.createUuid(),
				osylConfigService.getCurrentLocale(), "shared",
				"", siteId, "sectionId", coConfig,
				getXmlStringFromFile(coConfig,
					osylConfigService.getCurrentLocale(),
					webappDir), "shortDescription",
				"description", "title", false, null, null);
		// reinitilaisation des uuids
		SchemaHelper sh = new SchemaHelper(webappDir);
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetXML(null);
		coModeled.setSchemaVersion(sh.getSchemaVersion());
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else if (thisCo.getContent() == null) {
		coConfig = thisCo.getOsylConfig();
		// at the first call we got only the config id and ref. We need
		// to fill the rules so the next call is used to get it.
		coConfig =
			osylConfigService.getConfigByRef(coConfig
				.getConfigRef(), webappDir);
		thisCo.setOsylConfig(coConfig);
		thisCo.setContent(getXmlStringFromFile(coConfig, thisCo
			.getLang(), webappDir));
		String title = getSite(siteId).getTitle();
		String identifier = "";
		if (this.isCOLinkedToCourseManagement(getCurrentSiteId())) {
		    String cmTitle =
			    getCourseManagementTitle(getCurrentSiteId());
		    String cmIdentifier =
			    getCourseManagementCourseNo(getCurrentSiteId());
		    if (cmTitle != null) {
			title = cmTitle;
		    }
		    if (cmIdentifier != null) {
			identifier = cmIdentifier;
		    }
		}
		// reinitilaisation des uuids et ajout titre et identifier
		SchemaHelper sh = new SchemaHelper(webappDir);
		COModeledServer coModeled = new COModeledServer(thisCo);
		coModeled.XML2Model();
		coModeled.resetXML(null);
		coModeled.setCOContentTitle(title);
		coModeled.setCOContentIdentifier(identifier);
		coModeled.setSchemaVersion(sh.getSchemaVersion());
		coModeled.model2XML();
		thisCo.setContent(coModeled.getSerializedContent());

		resourceDao.createOrUpdateCourseOutline(thisCo);
	    } else {
		coConfig = thisCo.getOsylConfig();
	    }
	    String lockedBy = thisCo.getLockedBy();
	    boolean lockFree = false;
	    if (lockedBy == null || lockedBy.equals("")) {
		lockFree = true;
	    } else {
		long d =
			System.currentTimeMillis()
				- (sessionManager.getSession(lockedBy) != null ? sessionManager
					.getSession(lockedBy)
					.getLastAccessedTime()
					: 0);
		if (d > (15 * 60 * 1000))// we invalidate lock after 15 mins of
		    // inactivity
		    lockFree = true;
	    }
	    if (lockFree) {
		thisCo.setLockedBy(sessionManager.getCurrentSession().getId());
		resourceDao.setLockedBy(thisCo.getCoId(), sessionManager
			.getCurrentSession().getId());
	    } else if (!lockedBy.equals(sessionManager.getCurrentSession()
		    .getId())) {
		try {
		    // CO is already in edition
		    thisCo.setEditable(false);
		    // we set the name of the proprietary of the co to diplay to
		    // users
		    User u =
			    userDirectoryService.getUser(sessionManager
				    .getSession(thisCo.getLockedBy())
				    .getUserId());
		    thisCo
			    .setLockedBy(u.getFirstName() + " "
				    + u.getLastName());
		} catch (Exception ex) {
		}
	    }
	    thisCo =
		    osylConfigService.fillCo(webappDir
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + coConfig.getConfigRef(), thisCo);
	    getSiteInfo(thisCo, thisCo.getSiteId());

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
    public COSerialized importDataInCO(String xmlData, String siteId,
	    Map<String, String> filenameChangesMap, String webapp)
	    throws Exception {
	COSerialized co = null;

	SchemaHelper schemaHelper = new SchemaHelper(webapp);
	xmlData = schemaHelper.verifyAndConvert(xmlData);
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
	    throw e;
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
    
    /** {@inheritDoc} */
    public List<String> getChildren (String siteId) throws Exception{
	List<String> children = new ArrayList<String>();
	List<CORelation> courseOutlines = coRelationDao.getCourseOutlineChildren(siteId);
	
	for (CORelation co: courseOutlines){
	    children.add(co.getChild());
	}
	return children;
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

			// We update the users
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
		    // We remove the users
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

    @SuppressWarnings("unchecked")
    public Collection getEntityAuthzGroups(Reference ref, String userId) {
	Collection rv = new Vector();

	try {
	    if ("osyl".equals(ref.getSubType())) {
		rv.add(ref.getReference());
		ref.addSiteContextAuthzGroup(rv);
	    }
	} catch (Exception e) {
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
    @SuppressWarnings("unchecked")
    protected String[] split(String source, String splitter) {
	// hold the results as we find them
	Vector rv = new Vector();
	int last = 0;
	int next = 0;
	do {
	    // find next splitter in source
	    next = source.indexOf(splitter, last);
	    if (next != -1) {
		// isolate from last thru before next
		rv.add(source.substring(last, next));
		last = next + splitter.length();
	    }
	} while (next != -1);
	if (last < source.length()) {
	    rv.add(source.substring(last, source.length()));
	}

	// convert to array
	return (String[]) rv.toArray(new String[rv.size()]);

    } // split

    public boolean parseEntityReference(String reference, Reference ref) {
	if (reference.startsWith(REFERENCE_ROOT)) {
	    // Looks like /osyl/siteid/osylId
	    String[] parts = split(reference, Entity.SEPARATOR);

	    String subType = null;
	    String context = null;
	    String id = null;
	    String container = null;

	    if (parts.length > 2) {
		// the site/context
		context = parts[2];

		// the id
		if (parts.length > 3) {
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

    public void transferCopyEntities(String fromContext, String toContext,
	    List ids) {
	// TODO Auto-generated method stub

    }

    public void transferCopyEntities(String fromContext, String toContext,
	    List ids, boolean cleanup) {
	// TODO Auto-generated method stub

    }

    public COSerialized updateCOContentTitle(String siteId, String webappDir)
	    throws Exception {
	COSerialized co = getSerializedCourseOutlineBySiteId(siteId);
	COConfigSerialized coConfig = co.getOsylConfig();
	// at the first call we got only the config id and ref. We need to fill
	// the rules so the next call is used to get it.
	coConfig =
		osylConfigService.getConfigByRef(coConfig.getConfigRef(),
			webappDir);
	co.setOsylConfig(coConfig);
	updateCOContentTitle(siteId, co);

	return co;
    }

    public void updateCOContentTitle(String siteId, COSerialized co)
	    throws Exception {
	Site site = getSite(siteId);
	String title = site.getTitle();
	String identifier = "";
	try {
	    if (this.isCOLinkedToCourseManagement(siteId)) {
		String cmTitle = getCourseManagementTitle(siteId);
		String cmIdentifier = getCourseManagementCourseNo(siteId);
		if (cmTitle != null) {
		    title = cmTitle;
		}
		if (cmIdentifier != null) {
		    identifier = cmIdentifier;
		}
	    }
	    COModeledServer coModeled = new COModeledServer(co);
	    coModeled.XML2Model();
	    coModeled.setCOContentTitle(title);
	    coModeled.setCOContentIdentifier(identifier);
	    coModeled.model2XML();
	    co.setContent(coModeled.getSerializedContent());
	    resourceDao.createOrUpdateCourseOutline(co);
	} catch (IdUnusedException e) {
	    log.warn("updateCOContentTitle - Id unused exception", e);
	    log
		    .warn("updateCOContentTitle - Failed to retreive course management title");
	    // We wrap the exception in a java.lang.Exception. This way our
	    // "client" doesn't have to know about IdUnusedException.
	    throw new Exception(e);
	}

    }

    private boolean isCOLinkedToCourseManagement(String siteId)
	    throws Exception {
	return (getSite(siteId).getProviderGroupId() != null && getSite(siteId)
		.getProviderGroupId().length() > 0);
    }

    private String getCourseManagementCourseNo(String siteId) throws Exception {
	String courseNo = null;

	String Eid = getSite(siteId).getProviderGroupId();
	if (cmService.isSectionDefined(Eid)) {
	    Section section = cmService.getSection(Eid);
	    courseNo = getSiteName(section);
	}

	return courseNo;

    }

    private String getCourseManagementTitle(String siteId) throws Exception {
	String courseManageMentTitle = null;

	String Eid = getSite(siteId).getProviderGroupId();
	if (cmService.isSectionDefined(Eid)) {
	    Section section = cmService.getSection(Eid);
	    courseManageMentTitle = section.getTitle();
	}

	return courseManageMentTitle;
    }

    private String getSiteName(Section section) {
	StringBuilder siteName = new StringBuilder();
	String sectionId = section.getEid();
	String courseOffId = section.getCourseOfferingEid();
	CourseOffering courseOff = cmService.getCourseOffering(courseOffId);
	String canCourseId = (courseOff.getCanonicalCourseEid()).trim();
	AcademicSession session = courseOff.getAcademicSession();
	String sessionId = session.getEid();

	String courseId = null;
	String courseIdFront = null;
	String courseIdMiddle = null;
	String courseIdBack = null;

	String sessionTitle = null;
	String periode = null;
	String groupe = null;

	if (canCourseId.length() == 7) {
	    courseIdFront = canCourseId.substring(0, 2);
	    courseIdMiddle = canCourseId.substring(2, 5);
	    courseIdBack = canCourseId.substring(5, 7);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else if (canCourseId.length() == 6) {
	    courseIdFront = canCourseId.substring(0, 1);
	    courseIdMiddle = canCourseId.substring(1, 4);
	    courseIdBack = canCourseId.substring(4, 6);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else {
	    courseId = canCourseId;
	}

	if (canCourseId.matches(".*[^0-9].*")) {
	    courseId = canCourseId;
	}
	sessionTitle = getSessionName(session);

	if (sessionId.matches(".*[pP].*")) {
	    periode = sessionId.substring(sessionId.length() - 2);
	}

	groupe = sectionId.substring(courseOffId.length());

	siteName.append(courseId);
	if (groupe != null && groupe.length() > 0) {
	    siteName.append("_");
	    siteName.append(groupe);
	}
	if (sessionTitle != null && sessionTitle.length() > 0) {
	    siteName.append("-");
	    siteName.append(sessionTitle);
	}
	if (periode != null && periode.length() > 0) {
	    siteName.append(":");
	    siteName.append(periode);
	}

	return siteName.toString();
    }

    private String getSessionName(AcademicSession session) {
	String sessionName = null;
	String sessionId = session.getEid();
	Date startDate = session.getStartDate();
	String year = startDate.toString().substring(0, 4);

	if ((sessionId.charAt(3)) == '1')
	    sessionName = WINTER + year;
	if ((sessionId.charAt(3)) == '2')
	    sessionName = SUMMER + year;
	if ((sessionId.charAt(3)) == '3')
	    sessionName = FALL + year;

	return sessionName;
    }



    public void releaseLock() {
	String siteId;
	try {
	    siteId = getCurrentSiteId();
	    COSerialized thisCo =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    if (thisCo.getLockedBy().equals(
		    sessionManager.getCurrentSession().getId())) {
		resourceDao.clearLocksForCoId(thisCo.getCoId());
	    }
	} catch (Exception e) {
	}

    }

    public String getOsylConfigIdForSiteId(String siteId) {
	String configId = null;
	try {
	    COSerialized thisCo =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    configId = thisCo.getOsylConfig().getConfigId();
	} catch (Exception e) {
	}
	return configId;
    }

    public void convertAndSave(String webapp, COSerialized co) throws Exception {
	SchemaHelper schemaHelper = new SchemaHelper(webapp);
	String xmlData = schemaHelper.verifyAndConvert(co.getContent());
	if (xmlData == null || xmlData.trim().equals("")) {
	    log.warn("CO with co_id:"+co.getCoId()+" is null or void. Nothing to convert");
	} else {
	    co.setContent(xmlData);
	    resourceDao.createOrUpdateCourseOutline(co);
	}

    }

    public List<COSerialized> getAllCO() {
	return resourceDao.getCourseOutlines();
    }
}