package org.sakaiquebec.opensyllabus.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContentEdit;
import org.sakaiproject.assignment.api.AssignmentEdit;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entitybroker.EntityBroker;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.api.OsylService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

public class OsylServiceImpl implements OsylService {

    /*
     * FIXME: les listes de sites, de providers et d'entites devraient etre dans
     * un POJO. J'avais des problèmes de serialization c'est pour cela qu'ils
     * son des listes.
     */

    /** the site service to be injected by Spring */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code>.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    /**
     * The chs to be injected by Spring
     * 
     * @uml.property name="contentHostingService"
     * @uml.associationEnd
     */
    private ContentHostingService contentHostingService;

    /**
     * Sets the <code>ContentHostingService</code>.
     * 
     * @param contentHostingService
     * @uml.property name="contentHostingService"
     */
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    protected static final String ASSIGNMENT_TOOL_ID =
	    "sakai.assignment.grades";

    /** the session manager to be injected by Spring */
    private SessionManager sessionManager;

    private EntityBroker entityBroker;

    public void setEntityBroker(EntityBroker entityBroker) {
	this.entityBroker = entityBroker;
    }

    /**
     * Sets the <code>SessionManager</code>.
     * 
     * @param sessionManager
     */
    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    /**
     * The assignment service to be injected by Spring
     * 
     * @uml.property name="assignmentService"
     * @uml.associationEnd
     */
    private AssignmentService assignmentService;

    /**
     * Sets the <code>AssignmentService</code>.
     * 
     * @param assignmentService
     * @uml.property name="assignmentService"
     */
    public void setAssignmentService(AssignmentService assignmentService) {
	this.assignmentService = assignmentService;
    }

    /**
     * The citation service to be injected by Spring
     * 
     * @uml.property name="citationService"
     * @uml.associationEnd multiplicity="(0 -1)"
     *                     elementType="org.sakaiproject.citation.api.Citation"
     */
    private CitationService citationService;

    /**
     * Sets the <code>CitationService</code>.
     * 
     * @param citationService
     * @uml.property name="citationService"
     */
    public void setCitationService(CitationService citationService) {
	this.citationService = citationService;
    }

    private static Log log = LogFactory.getLog(OsylServiceImpl.class);

    /**
     * @uml.property name="osylSiteService"
     * @uml.associationEnd
     */
    private OsylSiteService osylSiteService;

    /**
     * @param osylSiteService
     * @uml.property name="osylSiteService"
     */
    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.warn("INIT from Osyl service");

    }

    private CORelationDao coRelationDao;

    /**
     * Sets the {@link CORelationDao}.
     * 
     * @param coRelationDAO
     */
    public void setCORelationDao(CORelationDao coRelationDao) {
	this.coRelationDao = coRelationDao;
    }

    /**
     * The security service to be injected by Spring
     * 
     * @uml.property name="osylSecurityService"
     * @uml.associationEnd
     */
    private OsylSecurityService osylSecurityService;

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /** {@inheritDoc} */
    public String createOrUpdateAssignment(String assignmentId, String title,
	    String instructions, Date openDate, Date closeDate, Date dueDate) {
	String siteId = "";
	String toolId = "";
	AssignmentEdit edit = null;
	AssignmentContentEdit contentEdit = null;

	Calendar cal = Calendar.getInstance();
	cal.setTime(openDate);
	int openYear = cal.get(Calendar.YEAR);
	int openMonth = cal.get(Calendar.MONTH) + 1;
	int openDay = cal.get(Calendar.DAY_OF_MONTH);
	// int openHour=cal.get(Calendar.HOUR_OF_DAY);
	// int openMinute=cal.get(Calendar.MINUTE);
	int openHour = 0;
	int openMinute = 0;

	cal.setTime(closeDate);
	cal.add(Calendar.DATE, 1);
	int closeYear = cal.get(Calendar.YEAR);
	int closeMonth = cal.get(Calendar.MONTH) + 1;
	int closeDay = cal.get(Calendar.DAY_OF_MONTH);
	// int closeHour=cal.get(Calendar.HOUR_OF_DAY);
	// int closeMinute=cal.get(Calendar.MINUTE);
	int closeHour = 0;
	int closeMinute = -5;

	if (dueDate != null) {
	    cal.setTime(dueDate);
	    cal.add(Calendar.DATE, 1);
	    dueDate = cal.getTime();
	}
	int dueYear = cal.get(Calendar.YEAR);
	int dueMonth = cal.get(Calendar.MONTH) + 1;
	int dueDay = cal.get(Calendar.DAY_OF_MONTH);
	// int dueHour=cal.get(Calendar.HOUR_OF_DAY);
	// int dueMinute=cal.get(Calendar.MINUTE);
	int dueHour = 0;
	int dueMinute = -5;

	try {
	    siteId = osylSiteService.getCurrentSiteId();

	} catch (Exception e) {
	    log.error("Unable to retrieve current siteid", e);
	}

	try {
	    // The client doesn't know the id. It must be a new item
	    if (assignmentId == null || assignmentId.equals("")) {
		edit = assignmentService.addAssignment(siteId);
		contentEdit = assignmentService.addAssignmentContent(siteId);
		contentEdit.setTypeOfGrade(Assignment.SCORE_GRADE_TYPE);
		contentEdit.setMaxGradePoint(1000);
		// Ajouter le user dans le user dans le groupe de l'assignment
	    } else {
		// temporarily allow the user to read and write from assignments
		// (asn.revise permission)
		if (osylSecurityService.isAllowedToEdit(siteId)) {
		    SecurityService.pushAdvisor(new SecurityAdvisor() {
			public SecurityAdvice isAllowed(String userId,
				String function, String reference) {
			    return SecurityAdvice.ALLOWED;
			}
		    });

		}
		edit = assignmentService.editAssignment(assignmentId);
		contentEdit =
			assignmentService.editAssignmentContent(edit
				.getContent().getId());

		// clear the permission
		if (osylSecurityService.isAllowedToEdit(siteId)) {
		    SecurityService.clearAdvisors();
		}
	    }
	} catch (Exception e) {
	    log.error("Unable to create an assignment", e);
	}

	try {
	    edit.setTitle(title);
	    edit.setContext(siteId);
	    if (openYear != -1) {
		Time openTime =
			TimeService.newTimeLocal(openYear, openMonth, openDay,
				openHour, openMinute, 0, 0);
		edit.setOpenTime(openTime);
	    }
	    if (closeYear != -1) {
		Time closeTime =
			TimeService.newTimeLocal(closeYear, closeMonth,
				closeDay, closeHour, closeMinute, 0, 0);
		edit.setCloseTime(closeTime);
		edit.setDropDeadTime(closeTime);
	    }
	    if (dueYear != -1) {
		Time dueTime =
			TimeService.newTimeLocal(dueYear, dueMonth, dueDay,
				dueHour, dueMinute, 0, 0);
		edit.setDueTime(dueTime);
	    }
	    edit.setDraft(false);
	    contentEdit.setTitle(title);
	    if (null != instructions) {
		contentEdit.setInstructions(instructions);
	    }
	    contentEdit
		    .setTypeOfSubmission(Assignment.TEXT_AND_ATTACHMENT_ASSIGNMENT_SUBMISSION);

	    edit.setContent(contentEdit);

	    assignmentService.commitEdit(edit);
	    assignmentService.commitEdit(contentEdit);
	} catch (RuntimeException e) {
	    log.error("Unable to save the assignment", e);
	}

	Site site;
	ToolConfiguration tool;

	try {
	    site = osylSiteService.getSite(siteId);
	    tool = site.getToolForCommonId(ASSIGNMENT_TOOL_ID);

	    // We verify if the site contains the assignment tool
	    if (tool == null) {
		osylSiteService.addTool(site, ASSIGNMENT_TOOL_ID);
		tool = site.getToolForCommonId(ASSIGNMENT_TOOL_ID);
	    }

	    toolId = tool.getId();
	} catch (IdUnusedException e1) {
	    log.error("Unused id exception", e1);
	}

	// if assignment creation is a success, look for the tool context before
	// returning an url

	String assignmentUrl =
		"/portal/tool/" + toolId + "?assignmentReference="
			+ edit.getReference()
			+ "&panel=Main&sakai_action=doView_submission";
	log.info("Create or update assignment URL " + assignmentUrl);
	return assignmentUrl;
    }

    /** {@inheritDoc} */
    public void removeAssignment(String assignmentId) {
	log.info("Removing Assignment id=" + assignmentId);
	try {
	    AssignmentEdit toRemove =
		    assignmentService.editAssignment(assignmentId);
	    assignmentService.removeAssignment(toRemove);
	    assignmentService.cancelEdit(toRemove);
	} catch (IdUnusedException e) {
	    log.error("Remove assignment - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Remove assignment - Permission exception", e);
	} catch (InUseException e) {
	    log.error("Remove assignment - In use exception", e);
	} catch (Exception e) {
	    log.error("Unable to remove assignment", e);
	}
    }

    /** {@inheritDoc} */
    public CitationCollection linkCitationsToSite(
	    CitationCollection collection, String siteId, String citationTitle) {

	ContentResourceEdit resource = null;
	String resourceDir =
		getResourceReference(siteId) + OsylSiteService.WORK_DIRECTORY
			+ "/";
	try {
	    String resourceId = resourceDir + citationTitle;
	    // temporarily allow the user to read and write resources
	    if (osylSecurityService.isAllowedToEdit(siteId)) {
		SecurityService.pushAdvisor(new SecurityAdvisor() {
		    public SecurityAdvice isAllowed(String userId,
			    String function, String reference) {
			return SecurityAdvice.ALLOWED;
		    }
		});
	    }
	    // check if resource is existing - throws IdUnusedException
	    contentHostingService.checkResource(resourceId);
	    // get resource to update citation
	    resource = contentHostingService.editResource(resourceId);

	} catch (IdUnusedException e) {
	    // resource does not exist, create new resource
	    try {
		resource =
			contentHostingService
				.addResource(
					resourceDir,
					citationTitle,
					null,
					ContentHostingService.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);
	    } catch (Exception e2) {
		log.error(
			"Create or update citation list - Exception while adding"
				+ " resource: ", e2);
	    }
	} catch (Exception e1) {
	    log.error(
		    "Create or update citation list - Exception while getting"
			    + " resource: ", e1);
	} finally {
	    // clear the permission
	    if (osylSecurityService.isAllowedToEdit(siteId)) {
		SecurityService.clearAdvisors();
	    }
	}

	resource.setResourceType(CitationService.CITATION_LIST_ID);
	resource.setContentType(ResourceType.MIME_TYPE_HTML);

	ResourcePropertiesEdit props = resource.getPropertiesEdit();
	// set the alternative_reference to point to reference_root for
	// CitationService
	props.addProperty(ContentHostingService.PROP_ALTERNATE_REFERENCE,
		org.sakaiproject.citation.api.CitationService.REFERENCE_ROOT);
	props.addProperty(ResourceProperties.PROP_CONTENT_TYPE,
		ResourceType.MIME_TYPE_HTML);
	props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, citationTitle);

	try {
	    citationService.save(collection);

	    resource.setContent(collection.getId().getBytes());
	    contentHostingService.commitResource(resource,
		    NotificationService.NOTI_NONE);

	} catch (RuntimeException e) {
	    log.error("Remove citation list - Runtime service exception", e);
	} catch (Exception e) {
	    log.error("Error while saving citation list to resources: ", e);
	}

	return collection;
    }

    /**
     * {@inheritDoc}
     */
    public String getXslForGroup(String group, String webappdir) {
	String xslFileName =
		OsylSiteService.XSL_PREFIX + group
			+ OsylSiteService.XSL_FILE_EXTENSION;
	File coXslFile =
		new File(webappdir + File.separator
			+ OsylSiteService.XSLT_DIRECTORY + File.separator,
			xslFileName);
	StringBuffer fileData = new StringBuffer();
	try {
	    BufferedReader reader =
		    new BufferedReader(new FileReader(coXslFile));
	    String str;
	    while ((str = reader.readLine()) != null) {
		fileData.append(str);
	    }
	    reader.close();

	} catch (Exception e) {
	    log.error("Error while reading file: " + xslFileName);
	}
	return fileData.toString();

    }

    /**
     * Create work and publish directories automatically if they don't exists.
     * Also applies default permission level
     */
    public void initService() throws Exception {
	String directoryId = "";

	if (addCollection(WORK_DIRECTORY, null)) {
	    directoryId =
		    (osylSiteService.getCurrentSiteReference() + WORK_DIRECTORY + "/")
			    .substring(8);

	    // HIDE COLLECTION
	    ContentCollectionEdit cce =
		    contentHostingService.editCollection(directoryId);
	    cce.setHidden();
	    contentHostingService.commitCollection(cce);

	    osylSecurityService.applyDirectoryPermissions(directoryId);

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
	    // Permission application
	    // osylSecurityService.applyPermissions(newId,SecurityInterface.ACCESS_PUBLIC);
	}

	if (addCollection(PUBLISH_DIRECTORY, null)) {
	    directoryId =
		    (osylSiteService.getCurrentSiteReference()
			    + PUBLISH_DIRECTORY + "/").substring(8);
	    osylSecurityService.applyDirectoryPermissions(directoryId);
	}
    }

    /**
     * Get a valid resource reference base URL to be used in later calls. Will
     * get deprecated very soon!
     * 
     * @return a String of the base URL
     */
    public String getResourceReference() {
	String refString = osylSiteService.getCurrentSiteReference();
	refString = refString.substring(OsylSiteService.CONTENT.length());
	return refString;
    }

    private String getResourceReference(String siteId) {

	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString = contentHostingService.getReference(val2);

	refString = refString.substring(OsylSiteService.CONTENT.length());
	return refString;
    }

    /**
     * Add a collection (similar to a sub-directory) under the resource tool.
     * 
     * @param dir name of collection
     * @param parent where to create it (null means top-level)
     * @return boolean whether the collection was added or not
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    private boolean addCollection(String dir, String parent) throws Exception {
	String id = null;
	if (parent != null) {
	    id = parent + "/" + dir;

	} else {
	    id = osylSiteService.getCurrentSiteReference() + dir;
	    id = id.substring(8);
	}
	try {
	    if (!collectionExist(id + "/")) {
		ResourcePropertiesEdit fileProperties =
			contentHostingService.newResourceProperties();
		fileProperties.addProperty(
			ResourceProperties.PROP_DISPLAY_NAME, dir);
		contentHostingService.addCollection(id, fileProperties);
		return true;
	    } else {
		// Nothing to do!
		return false;
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
    private boolean collectionExist(String id) {
	try {
	    contentHostingService.getCollection(id);

	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    /** {@inheritDoc} */
    public ResourcesLicencingInfo getResourceLicenceInfo() {
	ResourcesLicencingInfo resourcesLicencingInfo =
		new ResourcesLicencingInfo();
	List<String> copyrightTypeList = new ArrayList<String>();
	if (ServerConfigurationService.getStrings("copyrighttype") != null) {
	    String[] copyrighttypeTab =
		    ServerConfigurationService.getStrings("copyrighttype");
	    for (String copyright : copyrighttypeTab) {
		copyrightTypeList.add(copyright);
	    }
	}
	String defaultCopyright =
		ServerConfigurationService.getString("default.copyright");
	resourcesLicencingInfo.setCopyrightTypeList(copyrightTypeList);
	resourcesLicencingInfo.setDefaultCopyright(defaultCopyright);
	return resourcesLicencingInfo;
    }

    public boolean checkSitesRelation(String resourceURI) {
	try {
	    String currentSiteId = osylSiteService.getCurrentSiteId();
	    String parent =
		    coRelationDao.getParentOfCourseOutline(currentSiteId);
	    if (resourceURI.indexOf(parent, 0) != -1) {
		// temporarily allow the user to read and write from assignments
		// (asn.revise permission)

		// if (osylSecurityService.isAllowedToEdit(parent)) {
		SecurityService.pushAdvisor(new SecurityAdvisor() {
		    public SecurityAdvice isAllowed(String userId,
			    String function, String reference) {
			return SecurityAdvice.ALLOWED;
		    }
		});

		// }

		// clear the permission

		// if (osylSecurityService.isAllowedToEdit(siteId)) {
		// SecurityService.clearAdvisors(); }

	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
    }

    public Map<String, String> getMySites() {
	Map<String, String> mySites = new HashMap<String, String>();
	// Retrieve sites user has access to
	List<Site> sites =
		siteService
			.getSites(
				org.sakaiproject.site.api.SiteService.SelectionType.ACCESS,
				null,
				null,
				null,
				org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC,
				null);

	Collections.sort(sites);
	// ++++++++++++++ Retrieve providers
	for (Site site : sites) {
	    mySites.put(site.getId(), site.getTitle());
	}
	System.out.println("taille " + mySites.size());
	return mySites;
    }

    public Map<String, String> getAllowedProviders() {
	Map<String, String> providersMap = new HashMap<String, String>();

	// By default we only hide Forum messages and topics, should probably be
	// a new capability interface
	String[] hiddenProviders = { "forum_message", "forum_topic" };
	// End Retrieve sites user has access to
	Set<String> providers = entityBroker.getRegisteredPrefixes();

	// ------------------------------------------ Retrieve allowed providers
	// for each site
	for (String provider : providers) {
	    // Check if this provider is hidden or not
	    boolean skip = false;
	    if (ServerConfigurationService
		    .getString("entity-browser.hiddenProviders") != null
		    && !"".equals(ServerConfigurationService
			    .getString("entity-browser.hiddenProviders")))
		hiddenProviders =
			ServerConfigurationService.getString(
				"entity-browser.hiddenProviders").split(",");

	    for (int i = 0; i < hiddenProviders.length; i++) {
		if (provider.equals(hiddenProviders[i]))
		    skip = true;
	    }

	    // If the provider is not hidden and is among the ones the user has
	    // access to we retrieve the entity reference
	    if (!skip) {
		providersMap.put(provider, provider);
		System.out.println("les pr " + provider);
	    }
	}
	return providersMap;

    }

    public Map<String, String> getExistingEntities(String siteId) {
	Map<String, String> entitiesMap = new HashMap<String, String>();
	String currentUserId = sessionManager.getCurrentSessionUserId();
	List<String> entities, theSiteEntities;
	String title;
	Map<String, String> siteEntities = null;

	// By default we only hide Forum messages and topics, should probably be
	// a new capability interface
	String[] hiddenProviders = { "forum_message", "forum_topic" };
	List<String> allowedProviders = new ArrayList<String>();
	// End Retrieve sites user has access to
	Set<String> providers = entityBroker.getRegisteredPrefixes();

	// ------------------------------------------ Retrieve allowed providers
	// for each site
	for (String provider : providers) {
	    // Check if this provider is hidden or not
	    boolean skip = false;
	    if (ServerConfigurationService
		    .getString("entity-browser.hiddenProviders") != null
		    && !"".equals(ServerConfigurationService
			    .getString("entity-browser.hiddenProviders")))
		hiddenProviders =
			ServerConfigurationService.getString(
				"entity-browser.hiddenProviders").split(",");

	    for (int i = 0; i < hiddenProviders.length; i++) {
		if (provider.equals(hiddenProviders[i]))
		    skip = true;
	    }

	    // If the provider is not hidden and is among the ones the user has
	    // access to we retrieve the entity reference
	    if (!skip) {
		allowedProviders.add(provider);
		System.out.println("les pr " + provider);
	    }
	}
	// ------------------------------------------ End Retrieve allowed
	// providers for each site

	// ++++++++++++++ Retrieve providers

	Site site;
	try {
	    site = siteService.getSite(siteId);
	    if (siteEntities == null)
		siteEntities = new HashMap<String, String>();
	    siteEntities.put(siteId, site.getTitle());
	    theSiteEntities = new ArrayList<String>();

	    for (String provider : allowedProviders) {
		entities =
			entityBroker.findEntityRefs(new String[] { provider },
				new String[] { "context", "userId" },
				new String[] { siteId, currentUserId }, true);
		if (entities != null) {
		    theSiteEntities.addAll(entities);

		    for (String ent : entities) {
			title = entityBroker.getPropertyValue(ent, "title");
			entitiesMap.put(ent, title);
			System.out.println("l'entite est " + title);
		    }
		}
	    }
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	}

	return entitiesMap;

    }

}
