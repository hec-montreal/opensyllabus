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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.announcement.api.AnnouncementChannel;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementMessageEdit;
import org.sakaiproject.announcement.api.AnnouncementMessageHeaderEdit;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentEdit;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.component.cover.ServerConfigurationService;
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
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.event.api.UsageSessionService;
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
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.ModelHelper;
import org.sakaiquebec.opensyllabus.common.helper.SchemaHelper;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.VersionCompatibilityException;
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

    private static final Log log = LogFactory.getLog(OsylSiteServiceImpl.class);

    private static final String ORIGINAL_ANNOUNCEMENT_MESSAGE_REF =
	    "originalAnnouncementMessageRef";

    private ToolManager toolManager;

    private IdManager idManager;

    /** The config service to be injected by Spring */
    private OsylConfigService osylConfigService;

    /** The security service to be injected by Spring */
    private OsylSecurityService osylSecurityService;

    /** The osyl content service to be injected by Spring */
    private OsylContentService osylContentService;

    /** The chs to be injected by Spring */
    private ContentHostingService contentHostingService;

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

    private AssignmentService assignmentService;

    private SecurityService securityService;

    private AnnouncementService announcementService;

    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

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
     * Course management service integration.
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
    public void setOsylSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /**
     * Sets the {@link OsylContentService}.
     * 
     * @param osylContentService
     */
    public void setOsylContentService(OsylContentService osylContentService) {
	this.osylContentService = osylContentService;
    }

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
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

    public void setAssignmentService(AssignmentService assignmentService) {
	this.assignmentService = assignmentService;
    }

    public void setAnnouncementService(AnnouncementService announcementService) {
	this.announcementService = announcementService;
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylSite service");

	eventTrackingService.addObserver(new Observer() {

	    // private class for register AnnoucementEvent (resource, priority)
	    // to applied to sub-site if needed
	    class AnnouncementEvent {

		private String resource;

		private int priority;

		public AnnouncementEvent(String resource, int priority) {
		    super();
		    this.resource = resource;
		    this.priority = priority;
		}

		public String getResource() {
		    return resource;
		}

		public int getPriority() {
		    return priority;
		}
	    }

	    // need this because annoucement are copied in draft mode and we
	    // want want
	    // to know what to 'commit' after
	    private TreeMap<String, AnnouncementEvent> expectedAnnouncementAdd =
		    new TreeMap<String, AnnouncementEvent>();

	    private TreeMap<String, String> expectedAnnouncementModification =
		    new TreeMap<String, String>();

	    public void update(Observable o, Object arg) {
		Event e = (Event) arg;
		if (e.getEvent().equals(UsageSessionService.EVENT_LOGIN)) {
		    // ##### LOGIN ####//
		    DateFormat df =
			    new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
		    log.info("user ["
			    + sessionManager.getCurrentSession().getUserEid()
			    + "] login " + df.format(new Date()));
		} else if (e.getEvent()
			.equals(UsageSessionService.EVENT_LOGOUT)) {
		    // ##### LOGOUT ####//
		    // we unlocks all CO locks by current user
		    resourceDao.clearLocksForSession(sessionManager
			    .getCurrentSession().getId());
		} else if (e.getEvent().equals(PresenceService.EVENT_ABSENCE)) {
		    // ##### LEFT SITE ####//
		    // we can unlock the CO
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
		} else if (e.getEvent().equals(SiteService.SECURE_REMOVE_SITE)) {
		    // ##### DELETE SITE ####//
		    String siteid =
			    e.getResource().substring("/site/".length());
		    if (!siteid.startsWith("~")) {
			if (ServerConfigurationService.getBoolean(
				"osyl.site_deletion.co.delete", false)) {
			    // we have to delete COs associated to this site too
			    // breaks relation with other co

			    try {
				String parent = null;
				try {
				    parent =
					    coRelationDao
						    .getParentOfCourseOutline(siteid);
				} catch (Exception e2) {
				}
				if (parent != null)
				    dissociate(siteid, parent);
				List<CORelation> childrens =
					coRelationDao
						.getCourseOutlineChildren(siteid);
				if (childrens != null)
				    for (CORelation corelation : childrens) {
					String child = corelation.getChild();
					dissociate(child, siteid);
				    }

				// delete co for siteid
				resourceDao.removeCoForSiteId(siteid);

			    } catch (Exception e1) {
				log
					.error(
						"Could not delete Co after site removing",
						e1);
			    }
			}

			if (ServerConfigurationService.getBoolean(
				"osyl.site_deletion.resource.delete", false)) {
			    // we have to delete resources
			    String collectionId = "/group/" + siteid + "/";
			    try {
				contentHostingService
					.removeCollection(collectionId);
			    } catch (Exception e1) {
				log
					.error(
						"Could not delete resources after site removing",
						e1);
			    }
			}

			if (ServerConfigurationService.getBoolean(
				"osyl.site_deletion.assignement.delete", false)) {
			    // we have to delete asssignement
			    Iterator assignementsIter;
			    for (assignementsIter =
				    assignmentService
					    .getAssignmentsForContext(siteid); assignementsIter
				    .hasNext();) {
				Assignment assignment =
					(Assignment) assignementsIter.next();
				AssignmentEdit toRemove;
				try {
				    toRemove =
					    assignmentService
						    .editAssignment(assignment
							    .getId());
				    assignmentService
					    .removeAssignment(toRemove);
				    assignmentService.cancelEdit(toRemove);
				} catch (Exception e1) {
				    log
					    .error(
						    "Could not delete assignement after site removing",
						    e1);
				}
			    }
			    String attachementCollectionId =
				    "/attachment/" + siteid + "/";
			    try {
				contentHostingService
					.removeCollection(attachementCollectionId);
			    } catch (Exception e1) {
				log
					.error(
						"Could not delete assignement attachement after site removing",
						e1);
			    }
			}
		    }
		} else if (e.getEvent().equals(
			AnnouncementService.SECURE_ANNC_ADD)) {
		    // ##### NEW ANNOUNCE ####//
		    if (ServerConfigurationService.getBoolean(
			    "osyl.annc_new.descendant.add", false)) {
			String messageId = null;
			String siteId =
				e.getResource().substring(
					"/announcement/msg/".length());
			messageId = siteId.substring(siteId.indexOf("/") + 1);
			messageId =
				messageId.substring(messageId.indexOf("/") + 1);
			siteId = siteId.substring(0, siteId.indexOf("/"));

			AnnouncementMessage msg =
				getAnnouncementMessage(e.getResource());
			String subjectString =
				msg.getAnnouncementHeader().getSubject();
			if (expectedAnnouncementAdd.keySet().contains(siteId)) {
			    AnnouncementMessage originalMessage =
				    getAnnouncementMessage(expectedAnnouncementAdd
					    .get(siteId).getResource());
			    if (originalMessage.getAnnouncementHeader()
				    .getSubject().equals(subjectString)) {
				expectedAnnouncementModification.put(siteId, e
					.getResource());
				AnnouncementEvent t =
					expectedAnnouncementAdd.get(siteId);
				setAnnoucementMessageDraftValue(siteId,
					messageId, t.getResource(),
					originalMessage.getAnnouncementHeader()
						.getDraft(), t.getPriority());
				expectedAnnouncementAdd.remove(siteId);
			    }
			} else {
			    for (CORelation cor : coRelationDao
				    .getCORelationDescendants(siteId)) {
				expectedAnnouncementAdd.put(cor.getChild(),
					new AnnouncementEvent(e.getResource(),
						e.getPriority()));
			    }
			    copyAnnouncmentToDescendants(siteId, messageId);
			}
		    }
		} else if (e.getEvent().equals(
			AnnouncementService.SECURE_ANNC_UPDATE_ANY)
			|| e.getEvent().equals(
				AnnouncementService.SECURE_ANNC_UPDATE_OWN)) {
		    // ##### UPDATE ANNOUNCE ####//
		    if (ServerConfigurationService.getBoolean(
			    "osyl.annc_new.descendant.add", false)) {
			String messageId = null;
			String siteId =
				e.getResource().substring(
					"/announcement/msg/".length());
			messageId = siteId.substring(siteId.indexOf("/") + 1);
			messageId =
				messageId.substring(messageId.indexOf("/") + 1);
			siteId = siteId.substring(0, siteId.indexOf("/"));
			if (!expectedAnnouncementModification.keySet()
				.contains(siteId)) {
			    for (CORelation cor : coRelationDao
				    .getCORelationDescendants(siteId)) {
				deleteSiteAnnouncementForOriginalAnnouncementMessageRef(
					cor.getChild(), e.getResource());
				expectedAnnouncementAdd.put(cor.getChild(),
					new AnnouncementEvent(e.getResource(),
						e.getPriority()));
			    }
			    copyAnnouncmentToDescendants(siteId, messageId);
			} else {
			    expectedAnnouncementModification.remove(siteId);
			}
		    }

		} else if (e.getEvent().equals(
			AnnouncementService.SECURE_ANNC_REMOVE_ANY)
			|| e.getEvent().equals(
				AnnouncementService.SECURE_ANNC_REMOVE_OWN)) {
		    // ##### REMOVE ANNOUNCE ####//
		    if (ServerConfigurationService.getBoolean(
			    "osyl.annc_new.descendant.add", false)) {
			String siteId =
				e.getResource().substring(
					"/announcement/msg/".length());
			siteId = siteId.substring(0, siteId.indexOf("/"));
			for (CORelation cor : coRelationDao
				.getCORelationDescendants(siteId)) {
			    deleteSiteAnnouncementForOriginalAnnouncementMessageRef(
				    cor.getChild(), e.getResource());
			}
		    }
		}
	    }
	});

	// We register the entity manager
	entityManager.registerEntityProducer(this, REFERENCE_ROOT);

    }

    /** Destroy method to be called by Spring */
    public void destroy() {
	log.info("DESTROY from OsylSite service");
    }

    public COModeledServer getFusionnedPrePublishedHierarchy(String siteId)
	    throws Exception, FusionException {
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
	    if (coModeled != null) {
		if (parentId == null) {
		    coModeled.XML2Model();
		} else {
		    COModeledServer parentModel =
			    getFusionnedPrePublishedHierarchy(parentId);
		    coModeled.XML2Model();
		    if (parentModel != null) {
			coModeled.fusion(parentModel);
		    }
		}
	    }
	} catch (Exception e) {
	    log.error(e.getLocalizedMessage(), e);
	    throw e;
	}
	return coModeled;
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

    public COSerialized getCourseOutlineForExport(String siteId,
	    String webappDir) throws Exception {
	COSerialized co = getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	// create coContent if null
	if (co.getContent() == null) {
	    setCoContentWithTemplate(co, webappDir);
	    resourceDao.createOrUpdateCourseOutline(co);
	}
	COConfigSerialized coConfig = co.getOsylConfig();
	// at the first call we got only the config id and ref. We need to fill
	// the rules so the next call is used to get it.
	coConfig =
		osylConfigService.getConfigByRefAndVersion(coConfig
			.getConfigRef(), co.getConfigVersion(), webappDir);
	co.setOsylConfig(coConfig);
	updateCOCourseInformations(siteId, co);
	return co;
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
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] creates site [" + siteTitle + "]");
	long start = System.currentTimeMillis();
	Site site = null;

	if (!siteService.siteExists(siteTitle)) {
	    enableSecurityAdvisor();
	    String template =
		    ServerConfigurationService.getString(
			    "opensyllabus.course.template.prefix", null);
	    if (template != null) {
		site =
			siteService.addSite(siteTitle, siteService
				.getSite(template + lang));
		site.getPropertiesEdit().addProperty("template", "false");
	    } else {
		site = siteService.addSite(siteTitle, SITE_TYPE);
		// we add the tools
		addHomePage(site, lang);
		addTool(site, "sakai.announcements");
		addTool(site, "sakai.opensyllabus.tool");
		addTool(site, "sakai.resources");
		addTool(site, "sakai.siteinfo");
	    }
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(false);

	    enableSecurityAdvisor();
	    siteService.save(site);

	    // we add the directories
	    String directoryId;

	    osylContentService.initSiteAttachments(site.getTitle());
	    directoryId = contentHostingService.getSiteCollection(site.getId());
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

	    COConfigSerialized coConfig = null;
	    COSerialized co = null;

	    String configPath =
		    ServerConfigurationService.getString(
			    "opensyllabus.configs.path", null);
	    if (configPath == null)
		configPath =
			System.getProperty("catalina.home") + File.separator
				+ "webapps" + File.separator
				+ "osyl-editor-sakai-tool";// TODO SAKAI-860
	    SchemaHelper schemaHelper = new SchemaHelper(configPath);
	    String version = schemaHelper.getSchemaVersion();

	    try {
		coConfig = configDao.getConfigByRef(configRef);
		co =
			new COSerialized(idManager.createUuid(), lang,
				"shared", "", site.getId(), "sectionId",
				coConfig, null, "shortDescription",
				"description", "title", false, null, null,
				version);
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

	if (osylSecurityService.getCurrentUserRole().equals(
		OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)
		|| osylSecurityService.getCurrentUserRole().equals(
			OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN)) {
	    securityService.clearAdvisors();
	}
	log.info("Site [" + siteTitle + "] created in "
		+ (System.currentTimeMillis() - start) + " ms ");
	return site.getId();
    }

    /**
     * {@inheritDoc}
     */
    public String createSharableSite(String siteTitle, String configRef,
	    String lang) throws Exception {
	Site site = null;
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] creates site [" + siteTitle + "]");
	if (!siteService.siteExists(siteTitle)) {
	    String template =
		    ServerConfigurationService.getString(
			    "opensyllabus.course.template.prefix", null);
	    if (template != null) {
		site =
			siteService.addSite(siteTitle, siteService
				.getSite(template + lang));
		site.getPropertiesEdit().addProperty("template", "false");
	    } else {
		site = siteService.addSite(siteTitle, SITE_TYPE);
		// we add the tools
		addTool(site, "sakai.announcements");
		addTool(site, "sakai.opensyllabus.tool");
		addTool(site, "sakai.resources");
		addTool(site, "sakai.siteinfo");
	    }
	    site.setTitle(siteTitle);
	    site.setPublished(true);
	    site.setJoinable(false);

	    enableSecurityAdvisor();
	    siteService.save(site);

	    // we add the directories
	    // SAKAI-2160
	    String directoryId;

	    osylContentService.initSiteAttachments(site.getTitle());
	    directoryId = contentHostingService.getSiteCollection(site.getId());
	    ContentCollectionEdit cce =
		    contentHostingService.editCollection(directoryId);
	    cce.setHidden();
	    contentHostingService.commitCollection(cce);

	    COConfigSerialized coConfig = null;
	    COSerialized co = null;

	    String configPath =
		    ServerConfigurationService.getString(
			    "opensyllabus.configs.path", null);
	    if (configPath == null)
		configPath =
			System.getProperty("catalina.home") + File.separator
				+ "webapps" + File.separator
				+ "osyl-editor-sakai-tool";// TODO SAKAI-860
	    SchemaHelper schemaHelper = new SchemaHelper(configPath);
	    String version = schemaHelper.getSchemaVersion();

	    try {
		coConfig = configDao.getConfigByRef(configRef);
		co =
			new COSerialized(idManager.createUuid(), lang,
				"shared", "", site.getId(), "sectionId",
				coConfig, null, "shortDescription",
				"description", "title", false, null, null,
				version);
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

	if (osylSecurityService.getCurrentUserRole().equals(
		OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)
		|| osylSecurityService.getCurrentUserRole().equals(
			OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN)) {
	    securityService.clearAdvisors();
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
    private void addCollection(String dir, Site site) {
	ContentCollectionEdit collection = null;
	String id = null;

	id = getSiteReference(site) + dir;
	id = id.substring(8);
	try {
	    if (!collectionExists(id)) {
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
    private boolean collectionExists(String id) {
	try {
	    contentHostingService.getCollection(id);
	} catch (Exception e) {
	    log.debug("collectionExists [" + id + "]:" + e);
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
    public String getSiteReference(String siteId) {
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
    public COSerialized getSerializedCourseOutlineForEditor(String siteId,
	    String webappDir) throws Exception {
	long start = System.currentTimeMillis();
	COSerialized thisCo = null;
	try {
	    if (!osylSecurityService.isActionAllowedInSite(
		    getSiteReference(siteId),
		    OsylSecurityService.OSYL_FUNCTION_EDIT)) {
		if (osylSecurityService.getCurrentUserRole() == null) {
		    thisCo =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_PUBLIC);
		    return thisCo;
		} else if (osylSecurityService.isActionAllowedInSite(
			getSiteReference(siteId),
			OsylSecurityService.OSYL_FUNCTION_VIEW_STUDENT)) {
		    thisCo =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_ATTENDEE);
		} else if (osylSecurityService.isActionAllowedInSite(
			getSiteReference(siteId),
			OsylSecurityService.OSYL_FUNCTION_VIEW_COMMUNITY)) {
		    thisCo =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_COMMUNITY);
		} else {
		    thisCo =
			    resourceDao
				    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
					    siteId,
					    SecurityInterface.ACCESS_PUBLIC);
		}
	    } else {
		thisCo = getSerializedCourseOutlineAndLockIt(siteId, webappDir);
	    }
	    thisCo = osylConfigService.fillCo(webappDir, thisCo);
	    getSiteInfo(thisCo, thisCo.getSiteId());
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by siteId", e);
	} finally {
	    log.debug("getSerializedCourseOutlineForEditor  " + siteId
		    + elapsed(start));
	}
	return thisCo;
    } // getSerializedCourseOutlineForEditor

    public synchronized COSerialized getSerializedCourseOutlineAndLockIt(
	    String siteId, String webappDir) throws Exception {

	COSerialized thisCo = getSerializedCourseOutline(siteId, webappDir);
	String lockedBy = thisCo.getLockedBy();
	boolean lockFree = false;
	if (lockedBy == null || lockedBy.equals("")) {
	    lockFree = true;
	} else {
	    long d =
		    System.currentTimeMillis()
			    - (sessionManager.getSession(lockedBy) != null ? sessionManager
				    .getSession(lockedBy).getLastAccessedTime()
				    : 0);
	    if (d > (15 * 60 * 1000))// we invalidate lock after 15 mins of
		// inactivity
		lockFree = true;
	}
	if (lockFree) {
	    thisCo.setLockedBy(sessionManager.getCurrentSession().getId());
	    resourceDao.setLockedBy(thisCo.getCoId(), sessionManager
		    .getCurrentSession().getId());
	} else if (!lockedBy.equals(sessionManager.getCurrentSession().getId())) {
	    try {
		// CO is already in edition
		thisCo.setEditable(false);
		// we set the name of the proprietary of the co to diplay to
		// users
		User u =
			userDirectoryService.getUser(sessionManager.getSession(
				thisCo.getLockedBy()).getUserId());
		thisCo.setLockedBy(u.getFirstName() + " " + u.getLastName());
	    } catch (Exception ex) {
		log.error("Unable to retrieve name of CO locker", ex);
	    }
	}
	return thisCo;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutline(String siteId,
	    String webappDir) throws Exception {
	try {
	    COSerialized co =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    COConfigSerialized coConfig = null;
	    if (co == null) {
		coConfig =
			osylConfigService.getConfigByRefAndVersion(
				osylConfigService.getDefaultConfig(), null,
				webappDir);
		co =
			new COSerialized(idManager.createUuid(),
				osylConfigService.getCurrentLocale(), "shared",
				"", siteId, "sectionId", coConfig, null,
				"shortDescription", "description", "title",
				false, null, null, coConfig.getVersion());
		setCoContentWithTemplate(co, webappDir);
		resourceDao.createOrUpdateCourseOutline(co);
	    } else if (co.getContent() == null) {
		setCoContentWithTemplate(co, webappDir);
		resourceDao.createOrUpdateCourseOutline(co);
	    }
	    COModeledServer coModelChild = new COModeledServer(co);
	    // Fetch the parent
	    String parentId = null;
	    try {
		parentId = coRelationDao.getParentOfCourseOutline(siteId);
	    } catch (Exception e) {
	    }
	    if (parentId != null) {

		// fusion
		COModeledServer coModelParent = null;

		try {
		    coModelParent = getFusionnedPrePublishedHierarchy(parentId);
		} catch (FusionException e) {
		    co.setIncompatibleHierarchy(true);
		} catch (CompatibilityException e) {
		    co.setIncompatibleHierarchy(true);
		}

		if (coModelParent != null) {

		    try {
			coModelChild.XML2Model();
			coModelChild.fusion(coModelParent);
			coModelChild.model2XML();
			co.setContent(coModelChild.getSerializedContent());
		    } catch (FusionException e) {
			co.setIncompatibleWithHisParent(true);
		    } catch (CompatibilityException e) {
			co.setIncompatibleWithHisParent(true);
		    }
		}
	    }
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
    public boolean updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] saves CO [" + co.getTitle() + "]");

	boolean reload = (co.getCoId() == null);
	try {
	    backupCo(co);
	} catch (IOException e) {
	    log.error("Unable to backup course outline", e);
	    throw e;
	}

	try {
	    String parentId = null;
	    try {
		parentId =
			coRelationDao.getParentOfCourseOutline(co.getSiteId());
	    } catch (Exception e) {
	    }
	    if (parentId != null) {
		COModeledServer coModelChild = new COModeledServer(co);
		coModelChild.XML2Model();
		if (!coModelChild.isXmlAssociated()) {
		    try {
			COModeledServer coModelParent =
				getFusionnedPrePublishedHierarchy(parentId);

			if (coModelParent != null) {
			    coModelChild.associate(coModelParent);
			    coModelChild.model2XML();
			    co.setContent(coModelChild.getSerializedContent());
			    reload = true;
			}
		    } catch (Exception e) {
		    }
		}
	    }
	    resourceDao.createOrUpdateCourseOutline(co);
	} catch (Exception e) {
	    log.error("Unable to update course outline", e);
	    throw e;
	}
	return reload;
    }

    /**
     * Writes the XML into a temp file. This is a temporary measure. TODO
     * SAKAI-1932: find a better way to backup course outlines, possibly
     * computing differences and limiting how long and how many copies are kept.
     * 
     * @param co
     * @throws IOException
     */
    private void backupCo(COSerialized co) throws IOException {
	File backup =
		File.createTempFile("osyl-co-" + co.getSiteId() + "_", ".xml");
	FileHelper.writeFileContent(backup, co.getContent());
    }

    private ToolConfiguration addTool(Site site, String toolId) {
	SitePage page = site.addPage();
	page.setTitle(toolManager.getTool(toolId).getTitle());
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);

	return addTool(site, page, toolId);
    }

    private ToolConfiguration addTool(Site site, SitePage page, String toolId) {
	return addTool(site, page, toolId, null);
    }

    private ToolConfiguration addTool(Site site, SitePage page, String toolId,
	    String specifiedTitle) {

	Tool tool = toolManager.getTool(toolId);
	ToolConfiguration toolConf = page.addTool(tool);
	if (specifiedTitle != null) {
	    toolConf.setTitle(specifiedTitle);
	} else {
	    toolConf.setTitle(tool.getTitle());
	}
	toolConf.setLayoutHints("0,0");

	try {
	    enableSecurityAdvisor();
	    siteService.save(site);
	    if (osylSecurityService.getCurrentUserRole().equals(
		    OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)
		    || osylSecurityService.getCurrentUserRole().equals(
			    OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN)) {
		securityService.clearAdvisors();
	    }

	} catch (IdUnusedException e) {
	    log.error("Add tool - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Add tool - Permission exception", e);
	}

	return toolConf;
    }

    private void addHomePage(Site site, String locale) {
	// Add Home page and its 2 tools
	SitePage homePage = site.addPage();
	homePage.setupPageCategory(SitePage.HOME_TOOL_ID);
	homePage.setPosition(0);
	homePage.setLayout(SitePage.LAYOUT_DOUBLE_COL);
	homePage.getPropertiesEdit().addProperty(SitePage.IS_HOME_PAGE,
		Boolean.TRUE.toString());

	// 1st tool
	String toolTitle;
	if (Locale.CANADA_FRENCH.toString().equals(locale)) {
	    toolTitle = HEC_WELCOME_FR_CA;
	} else {
	    toolTitle = HEC_WELCOME_EN;
	}
	ToolConfiguration iframeCfg =
		addTool(site, homePage, "sakai.iframe.site", toolTitle);
	iframeCfg.setLayoutHints("0,0");
	Properties iframeProps = iframeCfg.getPlacementConfig();
	iframeProps.put("height", "600px");
	iframeCfg.save();
	site.setInfoUrl("/library/image/image_daip.jpg");

	// 2nd tool
	ToolConfiguration synAnncCfg =
		addTool(site, homePage, "sakai.synoptic.announcement");
	synAnncCfg.setLayoutHints("0,1");
	Properties props = synAnncCfg.getPlacementConfig();
	props.put("days", "31");
	synAnncCfg.save();

	// 3nd tool
	if (Locale.CANADA_FRENCH.toString().equals(locale)) {
	    toolTitle = HEC_MONTREAL_RULES_TITLE_FR_CA;
	} else {
	    toolTitle = HEC_MONTREAL_RULES_TITLE_EN;
	}
	iframeCfg =
		addTool(site, homePage, "sakai.rutgers.linktool", toolTitle);
	iframeCfg.setLayoutHints("1,1");

	iframeProps = iframeCfg.getPlacementConfig();
	iframeProps.put("height", "400px");
	// instructors won't be able to change this iFrame unless they get
	// site.upd permission
	iframeProps.put("url", HEC_MONTREAL_RULES_FILE_BASE_NAME + locale
		+ HEC_MONTREAL_RULES_FILE_EXTENSION);
	iframeCfg.save();

    }

    /** {@inheritDoc} */
    public COSerialized importDataInCO(String xmlData, String siteId,
	    Map<String, String> filenameChangesMap, String webapp)
	    throws Exception {
	COSerialized co = null;

	SchemaHelper schemaHelper = new SchemaHelper(webapp);
	try {
	    co = getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	    if (co != null) {
		co.setContent(xmlData);
		COModeledServer coModeledServer = new COModeledServer(co);
		coModeledServer.XML2Model();
		coModeledServer.resetXML(filenameChangesMap);
		coModeledServer.model2XML();
		co.setContent(coModeledServer.getSerializedContent());
		co = schemaHelper.verifyAndConvert(co);
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
    public List<String> getChildren(String siteId) throws Exception {
	List<String> children = new ArrayList<String>();
	List<CORelation> courseOutlines =
		coRelationDao.getCourseOutlineChildren(siteId);

	for (CORelation co : courseOutlines) {
	    children.add(co.getChild());
	}
	return children;
    }

    public void associate(String siteId, String parentId) throws Exception,
	    CompatibilityException, FusionException, OsylPermissionException,
	    VersionCompatibilityException {
	if (!osylSecurityService
		.isActionAllowedForCurrentUser(OsylSecurityService.OSYL_MANAGER_FUNCTION_ASSOCIATE)) {
	    throw new OsylPermissionException(sessionManager
		    .getCurrentSession().getUserEid(),
		    OsylSecurityService.OSYL_MANAGER_FUNCTION_ASSOCIATE);
	} else {
	    String parent = null;
	    try {
		parent = coRelationDao.getParentOfCourseOutline(siteId);
	    } catch (Exception e) {
	    }
	    if (parent != null && parent.equals(parentId))
		return;
	    log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		    + "] associates [" + siteId + "] to parent [" + parentId
		    + "]");
	    COSerialized co;
	    COSerialized cop;
	    try {
		co = resourceDao.getSerializedCourseOutlineBySiteId(siteId);
		cop = resourceDao.getSerializedCourseOutlineBySiteId(parentId);
		String versionSite = co.getConfigVersion();
		String versionParent = cop.getConfigVersion();
		if (co != null) {
		    if (versionSite.equalsIgnoreCase(versionParent)) {
			getSiteInfo(co, siteId);
			if (parentId != null) {
			    COModeledServer coModelParent =
				    getFusionnedPrePublishedHierarchy(parentId);
			    if (coModelParent != null
				    && co.getContent() != null) {
				ModelHelper.createAssociationInXML(co,
					coModelParent);
				resourceDao.createOrUpdateCourseOutline(co);
			    }
			}
			coRelationDao.createRelation(siteId, parentId);
		    } else {
			throw new VersionCompatibilityException(
				"Versions cours outline are incompatible");
		    }
		} else {
		    throw new Exception("Child course outline is null");
		}
	    } catch (Exception e) {
		log.error(e.getLocalizedMessage(), e);
		throw e;
	    }
	}
    }

    public void dissociate(String siteId, String parentId) throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] dissociates [" + siteId + "] from parent [" + parentId
		+ "]");
	COSerialized co;
	try {
	    co = resourceDao.getSerializedCourseOutlineBySiteId(siteId);

	    if (co != null) {
		try {
		    getSiteInfo(co, siteId);
		} catch (Exception e) {
		}
		if (parentId != null) {
		    if (co.getContent() != null) {
			COModeledServer coModelChild = new COModeledServer(co);
			coModelChild.XML2Model();
			coModelChild.dissociate();
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
	String[] toolIds = { "sakai.opensyllabus.tool" };
	return toolIds;
    }

    public void transferCopyEntities(String fromContext, String toContext,
	    List ids) {
	transferCopyEntities(fromContext, toContext, ids, false);
    }

    public void transferCopyEntities(String fromContext, String toContext,
	    List ids, boolean cleanup) {
	COSerialized sourceCo =
		getUnfusionnedSerializedCourseOutlineBySiteId(fromContext);
	COSerialized destinationCo =
		getUnfusionnedSerializedCourseOutlineBySiteId(toContext);
	if (destinationCo == null) {
	    destinationCo = sourceCo;
	    destinationCo.setCoId(null);
	} else {
	    destinationCo.setContent(sourceCo.getContent());
	}
	COModeledServer coModeledServer = new COModeledServer(destinationCo);
	coModeledServer.XML2Model();
	coModeledServer.resetXML(null);
	coModeledServer.model2XML();
	destinationCo.setContent(coModeledServer.getSerializedContent());

	// convert
	String configPath =
		ServerConfigurationService.getString(
			"opensyllabus.configs.path", null);
	if (configPath == null)
	    configPath =
		    System.getProperty("catalina.home") + File.separator
			    + "webapps" + File.separator
			    + "osyl-editor-sakai-tool";// TODO SAKAI-860
	try {
	    convertAndSave(configPath, destinationCo);
	} catch (Exception e) {
	    log.error("transferCopyEntities:createOrUpdateCourseOutline", e);
	}
    }

    public COSerialized updateCOCourseInformations(String siteId,
	    String webappDir) throws Exception {
	COSerialized co = getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	COConfigSerialized coConfig = co.getOsylConfig();
	// at the first call we got only the config id and ref. We need to fill
	// the rules so the next call is used to get it.
	coConfig =
		osylConfigService.getConfigByRefAndVersion(coConfig
			.getConfigRef(), co.getConfigVersion(), webappDir);
	co.setOsylConfig(coConfig);
	updateCOCourseInformations(siteId, co);

	return co;
    }

    public void updateCOCourseInformations(String siteId, COSerialized co)
	    throws Exception {
	try {
	    if (co.getContent() != null) {
		COModeledServer coModeled = new COModeledServer(co);
		coModeled.XML2Model();
		updateCOCourseInformations(siteId, coModeled);
		coModeled.model2XML();
		co.setContent(coModeled.getSerializedContent());
		resourceDao.createOrUpdateCourseOutline(co);
	    }
	} catch (IdUnusedException e) {
	    log.warn("updateCOContentTitle - Id unused exception", e);
	    log
		    .warn("updateCOContentTitle - Failed to retreive course management title");
	    // We wrap the exception in a java.lang.Exception. This way our
	    // "client" doesn't have to know about IdUnusedException.
	    throw new Exception(e);
	}
    }

    private void updateCOCourseInformations(String siteId,
	    COModeledServer coModeled) throws Exception {
	Site site = getSite(siteId);
	String title = site.getTitle();
	String identifier = "";
	String program = "";
	try {
	    if (this.isCOLinkedToCourseManagement(siteId)) {
		String cmTitle = getCourseManagementTitle(siteId);
		String cmIdentifier = getCourseManagementCourseNo(siteId);
		String cmProgram = getCourseManagementProgram(siteId);
		if (cmTitle != null) {
		    title = cmTitle;
		}
		if (cmIdentifier != null) {
		    identifier = cmIdentifier;
		}
		if (cmProgram != null) {
		    program = cmProgram;
		}
	    }
	    coModeled.setCOContentTitle(title);
	    coModeled.setCOContentCourseId(identifier);
	    coModeled.setCOContentIdentifier(identifier);
	    coModeled.setCOProgram(program);
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

    private String getCourseManagementProgram(String siteId) throws Exception {
	String program = "";

	String Eid = getSite(siteId).getProviderGroupId();
	if (cmService.isSectionDefined(Eid)) {
	    Section section = cmService.getSection(Eid);
	    program = getProgram(section);
	}
	return program;
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

    public String getSiteName(String sectionId) {
	return getSiteName(cmService.getSection(sectionId));
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
	    courseIdBack = canCourseId.substring(5);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else if (canCourseId.length() == 6) {
	    courseIdFront = canCourseId.substring(0, 1);
	    courseIdMiddle = canCourseId.substring(1, 4);
	    courseIdBack = canCourseId.substring(4);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else {
	    courseId = canCourseId;
	}

	if (canCourseId.matches(".*[^0-9].*")) {
	    if (canCourseId.endsWith("A") || canCourseId.endsWith("E")) {
		if (canCourseId.length() == 8) {
		    courseIdFront = canCourseId.substring(0, 2);
		    courseIdMiddle = canCourseId.substring(2, 5);
		    courseIdBack = canCourseId.substring(5);
		    courseId =
			    courseIdFront + "-" + courseIdMiddle + "-"
				    + courseIdBack;

		}
		if (canCourseId.length() == 7) {
		    courseIdFront = canCourseId.substring(0, 1);
		    courseIdMiddle = canCourseId.substring(1, 4);
		    courseIdBack = canCourseId.substring(4);
		    courseId =
			    courseIdFront + "-" + courseIdMiddle + "-"
				    + courseIdBack;

		}
	    } else
		courseId = canCourseId;
	}
	sessionTitle = getSessionName(session);

	if (sessionId.matches(".*[pP].*")) {
	    periode = sessionId.substring(sessionId.length() - 2);
	}

	groupe = sectionId.substring(courseOffId.length());

	siteName.append(courseId);
	if (sessionTitle != null && sessionTitle.length() > 0) {
	    siteName.append(".");
	    siteName.append(sessionTitle);
	}
	if (periode != null && periode.length() > 0) {
	    siteName.append(".");
	    siteName.append(periode);
	}
	if (groupe != null && groupe.length() > 0) {
	    siteName.append(".");
	    siteName.append(groupe);
	}
	return siteName.toString();
    }

    private String getProgram(Section section) {
	String courseOffId = section.getCourseOfferingEid();
	CourseOffering courseOff = cmService.getCourseOffering(courseOffId);
	return courseOff.getAcademicCareer();
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

    public COConfigSerialized getOsylConfigForSiteId(String siteId,
	    String webappDir) {
	COConfigSerialized config = null;
	try {
	    COSerialized thisCo =
		    resourceDao.getSerializedCourseOutlineBySiteId(siteId);
	    String cfgId = thisCo.getOsylConfig().getConfigId();
	    if (cfgId == null) {
		String configPath =
			ServerConfigurationService.getString(
				"opensyllabus.configs.path", null);
		if (configPath == null)
		    configPath =
			    System.getProperty("catalina.home")
				    + File.separator + "webapps"
				    + File.separator + "osyl-editor-sakai-tool";// TODO
		// SAKAI-860
		SchemaHelper schemaHelper = new SchemaHelper(configPath);
		String version = schemaHelper.getSchemaVersion();
		config =
			osylConfigService.getConfigByRefAndVersion(
				osylConfigService.getDefaultConfig(), version,
				webappDir);
	    } else {
		config =
			osylConfigService.getConfig(cfgId, thisCo
				.getConfigVersion(), webappDir);
	    }
	} catch (Exception e) {
	    log.debug("getOsylConfigIdForSiteId: " + e);
	}
	return config;
    }

    public void convertAndSave(String webapp, COSerialized co) throws Exception {
	SchemaHelper schemaHelper = new SchemaHelper(webapp);
	co = schemaHelper.verifyAndConvert(co);
	if (co.getContent() == null || co.getContent().trim().equals("")) {
	    log.warn("CO with co_id:" + co.getCoId()
		    + " is null or void. Nothing to convert");
	} else {
	    resourceDao.createOrUpdateCourseOutline(co);
	}
    }

    public List<COSerialized> getAllCO() {
	return resourceDao.getCourseOutlines();
    }

    // only to improve readability while profiling
    private static String elapsed(long start) {
	return ": elapsed : " + (System.currentTimeMillis() - start) + " ms ";
    }

    public Date getCoLastModifiedDate(String siteId) {
	try {
	    return resourceDao.getModifiedDate(siteId);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public Date getCoLastPublicationDate(String siteId) {
	try {
	    return resourceDao.getPublicationDate(siteId);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public void deleteSite(String siteId) {
	long start = System.currentTimeMillis();
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] deletes site [" + siteId + "]");

	Site site;
	try {
	    enableSecurityAdvisor();
	    site = getSite(siteId);
	    siteService.removeSite(site);
	    securityService.clearAdvisors();
	} catch (IdUnusedException e) {
	    log.info("User " + sessionManager.getCurrentSession().getUserEid()
		    + " can not delete the site " + siteId
		    + " because this site does not exist.");
	} catch (PermissionException e) {
	    log.info("User " + sessionManager.getCurrentSession().getUserEid()
		    + " has no right to delete site " + siteId);
	}

	log.info("Site [" + siteId + "] deleted in "
		+ (System.currentTimeMillis() - start) + " ms ");
    }

    public void setCoContentWithTemplate(COSerialized co, String webappDir)
	    throws Exception {
	COConfigSerialized coConfig = co.getOsylConfig();
	coConfig =
		osylConfigService.getConfigByRefAndVersion(coConfig
			.getConfigRef(), coConfig.getVersion(), webappDir);
	co.setContent(osylConfigService.getXml(coConfig, co.getLang(),
		webappDir));

	SchemaHelper sh = new SchemaHelper(webappDir);
	COModeledServer coModeled = new COModeledServer(co);
	coModeled.XML2Model();
	// reinitialisation de id
	coModeled.resetXML(null);
	coModeled.setSchemaVersion(sh.getSchemaVersion());
	// associate with parent if exist
	String parentId = null;
	try {
	    parentId = coRelationDao.getParentOfCourseOutline(co.getSiteId());
	    if (parentId != null && !parentId.equals("")) {
		COModeledServer coModelParent =
			getFusionnedPrePublishedHierarchy(parentId);
		if (coModelParent != null)
		    coModeled.associate(coModelParent);
	    }
	} catch (Exception e) {
	}
	coModeled.model2XML();
	co.setContent(coModeled.getSerializedContent());
    }

    protected void enableSecurityAdvisor() {
	if (osylSecurityService.getCurrentUserRole().equals(
		OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)
		|| osylSecurityService.getCurrentUserRole().equals(
			OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN)) {
	    securityService.pushAdvisor(new SecurityAdvisor() {
		public SecurityAdvice isAllowed(String userId, String function,
			String reference) {
		    return SecurityAdvice.ALLOWED;
		}
	    });
	}
    }

    public void addAnnounce(String siteId, String subject, String body) {
	// We add the message to the current site
	if (siteExists(siteId)) {
	    try {
		addAnnouncement(siteId, subject, body);
		log.info("An announcement has been made in the site " + siteId);
	    } catch (Exception e) {
		log.error("Could not add an annoucement in site " + siteId, e);
	    }
	}
    }

    private void addAnnouncement(String siteId, String subject, String body)
	    throws Exception {

	AnnouncementChannel channel = getAnnouncementChannel(siteId);

	if (channel != null) {
	    AnnouncementMessageEdit message = null;
	    message = channel.addAnnouncementMessage();

	    if (message != null) {
		AnnouncementMessageHeaderEdit header =
			message.getAnnouncementHeaderEdit();
		header.setSubject(subject);
		message.setBody(body);

		header.clearGroupAccess();

		channel.commitMessage(message);

	    }
	} else {
	    throw new Exception("No annoucement channel available");
	}
    }

    private void copyAnnouncmentToDescendants(String siteId, String ref) {
	List<CORelation> cos = null;
	String originalSiteId = siteId;
	try {
	    cos = coRelationDao.getCORelationDescendants(siteId);
	} catch (Exception e) {
	}

	// We add the message to the children site
	for (CORelation relation : cos) {
	    siteId = relation.getChild();
	    if (siteExists(siteId)) {
		try {
		    copyAnnoucement(originalSiteId, siteId, ref);
		    log
			    .info("An announcement has been made in the site "
				    + siteId + " concerning the site "
				    + originalSiteId);
		} catch (Exception e) {
		    log.error("Could not add an annoucement in site " + siteId
			    + ". The annoucement come from site "
			    + originalSiteId, e);
		}
	    }
	}
    }

    private void copyAnnoucement(String fromSite, String toSite, String ref) {
	List<String> list = new ArrayList<String>();
	list.add(ref);
	SecurityAdvisor advisor = new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String arg0, String arg1,
		    String arg2) {
		return SecurityAdvice.ALLOWED;
	    }
	};
	try {
	    securityService.pushAdvisor(advisor);
	    ((EntityTransferrer) announcementService).transferCopyEntities(
		    fromSite, toSite, list);
	} finally {
	    securityService.popAdvisor();
	}

    }

    private AnnouncementChannel getAnnouncementChannel(String siteId) {
	AnnouncementChannel channel = null;
	String channelId =
		ServerConfigurationService.getString("channel", null);
	if (channelId == null) {
	    channelId =
		    announcementService.channelReference(siteId,
			    SiteService.MAIN_CONTAINER);
	    try {
		channel = announcementService.getAnnouncementChannel(channelId);
	    } catch (IdUnusedException e) {
		log
			.warn(this
				+ "getAnnouncement:No announcement channel found");
		channel = null;
	    } catch (PermissionException e) {
		log
			.warn(this
				+ "getAnnouncement:Current user not authorized to deleted annc associated "
				+ "with assignment. " + e.getMessage());
		channel = null;
	    }
	}
	return channel;
    }

    private AnnouncementMessage getAnnouncementMessage(String ref) {
	String messageId = null;
	String siteId = ref.substring("/announcement/msg/".length());
	messageId = siteId.substring(siteId.indexOf("/") + 1);
	messageId = messageId.substring(messageId.indexOf("/") + 1);
	siteId = siteId.substring(0, siteId.indexOf("/"));

	AnnouncementChannel channel = getAnnouncementChannel(siteId);
	AnnouncementMessage msg = null;
	try {
	    msg = channel.getAnnouncementMessage(messageId);
	} catch (Exception e) {
	    log.error("Could not retrieve announcement " + ref, e);
	}
	return msg;
    }

    private void setAnnoucementMessageDraftValue(String siteId,
	    String messageId, String originalRef, boolean draft, int priority) {
	AnnouncementMessageEdit msg = null;
	try {
	    AnnouncementChannel channel = getAnnouncementChannel(siteId);
	    msg = channel.editAnnouncementMessage(messageId);
	    msg.getHeaderEdit().setDraft(draft);
	    msg.getPropertiesEdit().addProperty(
		    ORIGINAL_ANNOUNCEMENT_MESSAGE_REF, originalRef);
	    channel
		    .commitMessage(msg, priority,
			    "org.sakaiproject.announcement.impl.SiteEmailNotificationAnnc");
	} catch (Exception ee) {
	    log.error("Could not modify announcement " + messageId, ee);
	}
    }

    private void deleteSiteAnnouncementForOriginalAnnouncementMessageRef(
	    String siteId, String ref) {
	SecurityAdvisor advisor = new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String arg0, String arg1,
		    String arg2) {
		return SecurityAdvice.ALLOWED;
	    }
	};
	try {
	    securityService.pushAdvisor(advisor);
	    AnnouncementChannel channel = getAnnouncementChannel(siteId);
	    List messageList = channel.getMessages(null, true);

	    for (Iterator iter = messageList.iterator(); iter.hasNext();) {
		AnnouncementMessage msg = (AnnouncementMessage) iter.next();
		if (ref.equals(msg.getProperties().get(
			ORIGINAL_ANNOUNCEMENT_MESSAGE_REF)))

		    channel.removeMessage(msg.getId());
	    }
	} catch (Exception e) {
	} finally {
	    securityService.popAdvisor();
	}
    }
}