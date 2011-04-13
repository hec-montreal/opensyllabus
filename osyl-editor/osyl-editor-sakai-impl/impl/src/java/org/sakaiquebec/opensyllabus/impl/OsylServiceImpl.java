package org.sakaiquebec.opensyllabus.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.FunctionManager;
//import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
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
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.api.OsylService;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

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

    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

    protected static final String ASSIGNMENT_TOOL_ID =
	    "sakai.assignment.grades";

    // Whether the experimental cache is enabled or not
    private static boolean CACHE_ENABLED = false;

    // Whether we have read if the cache configuration
    private static boolean CACHE_CFG_INITIALIZED = false;

    /** the session manager to be injected by Spring */
    private SessionManager sessionManager;

    private EntityBroker entityBroker;

    public void setEntityBroker(EntityBroker entityBroker) {
	this.entityBroker = entityBroker;
    }

    /** The osyl content service to be injected by Spring */
    private OsylContentService osylContentService;

    /**
     * Sets the {@link OsylContentService}.
     * 
     * @param osylContentService
     */
    public void setOsylContentService(OsylContentService osylContentService) {
	this.osylContentService = osylContentService;
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
	log.info("INIT from Osyl service");
	/******************************************************************************/

	for (Iterator<String> i = functionsToRegister.iterator(); i.hasNext();) {
	    String function = i.next();
	    getFunctionManager().registerFunction(function);
	}
    }

    private FunctionManager functionManager;

    public FunctionManager getFunctionManager() {
	return functionManager;
    }

    public void setFunctionManager(FunctionManager functionManager) {
	this.functionManager = functionManager;
    }

    private List<String> functionsToRegister;

    // public List getFunctionsToRegister() {
    // return functionsToRegister;
    // }

    public void setFunctionsToRegister(List<String> functionsToRegister) {
	this.functionsToRegister = functionsToRegister;
    }

    /**************************************************************************/

    private CORelationDao coRelationDao;

    /**
     * Sets the {@link CORelationDao}.
     * 
     * @param coRelationDAO
     */
    public void setCORelationDao(CORelationDao coRelationDao) {
	this.coRelationDao = coRelationDao;
    }

    private COConfigDao coConfigDao;

    /**
     * Sets the {@link COConfigDao}.
     * 
     * @param coConfigDAO
     */
    public void setCOConfigDao(COConfigDao coConfigDao) {
	this.coConfigDao = coConfigDao;
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
    public void setOsylSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /** {@inheritDoc} */
    public CitationCollection linkCitationsToSite(
	    CitationCollection collection, String siteId, String citationTitle) {

	ContentResourceEdit resource = null;
	String resourceDir = getResourceReference(siteId);
    log.info("*** linkCitationsToSite SecurityAdvisor advisor = new SecurityAdvisor() { OsylServiceImpl *** ");	    

	/*
	SecurityAdvisor advisor = new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
		return SecurityAdvice.ALLOWED;
	    }
	};
	*/
	try {
	    String resourceId = resourceDir + citationTitle;
	    // temporarily allow the user to read and write resources
	    if (osylSecurityService.isActionAllowedInSite(osylSiteService
		    .getSiteReference(siteId),
		    OsylSecurityService.OSYL_FUNCTION_EDIT)) {
		//securityService.pushAdvisor(advisor);
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
	    if (osylSecurityService.isActionAllowedInSite(osylSiteService
		    .getSiteReference(siteId),
		    OsylSecurityService.OSYL_FUNCTION_EDIT)) {
		//securityService.popAdvisor();
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

	if (ServerConfigurationService.getString(
		"opensyllabus.publish.in.attachment").equals("true")) {
	    // directoryId =
	    // OsylContentService.PUBLISH_DIRECTORY_PREFIX
	    // + osylSiteService.getCurrentSiteId()
	    // + OsylContentService.USE_ATTACHMENTS;
	    // if (!collectionExist(directoryId)) {
	    // osylContentService.initSiteAttachments(osylSiteService
	    // .getCurrentSiteId());
	    // }
	    //
	    // directoryId = osylSiteService.getCurrentSiteId();

	    // TODO : SAKAI-2160 - add citations list ?

	    // Create attachment folder if necessary
	    directoryId =
		    ContentHostingService.ATTACHMENTS_COLLECTION
			    + osylSiteService.getCurrentSiteId()
			    + "/"
			    + OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
			    + "/";
	    if (!collectionExist(directoryId))
		osylContentService.initSiteAttachments(osylSiteService
			.getCurrentSiteId());

	} else {
	    if (addCollection(WORK_DIRECTORY, null)) {
		directoryId =
			(osylSiteService.getCurrentSiteReference()
				+ WORK_DIRECTORY + "/").substring(8);

		// HIDE COLLECTION
		ContentCollectionEdit cce =
			contentHostingService.editCollection(directoryId);
		cce.setHidden();
		contentHostingService.commitCollection(cce);

		osylSecurityService.applyDirectoryPermissions(directoryId);

		// we add the default citationList
		// TODO I18N
		String citationListName =
			"Références bibliographiques du cours";

		CitationCollection citationList =
			citationService.addCollection();

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
		

	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
    }

    public SakaiEntities getExistingEntities(String siteId) {
	SakaiEntities sakaiEntities = new SakaiEntities();
	String currentUserId = sessionManager.getCurrentSessionUserId();
	List<String> entities;
	String title;
	Map<String, String> siteEntities = new HashMap<String, String>();
	Map<String, String> siteProviders = new HashMap<String, String>();

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
		siteProviders.put(provider, provider);
	    }
	}
	// ------------------------------------------ End Retrieve allowed
	// providers for each site

	// ++++++++++++++ Retrieve providers
	try {
	    siteService.getSite(siteId);

	    for (String provider : allowedProviders) {
		entities =
			entityBroker.findEntityRefs(new String[] { provider },
				new String[] { "context", "userId" },
				new String[] { siteId, currentUserId }, true);
		if (entities != null && !entities.isEmpty()) {

		    for (String ent : entities) {
			if (!"assignment".equals(provider)
				|| !Boolean.parseBoolean(entityBroker
					.getPropertyValue(ent, "draft"))) {
			    title = entityBroker.getPropertyValue(ent, "title");
			    siteEntities.put(ent, title);
			}
		    }
		}
	    }
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	}

	sakaiEntities.setEntities(siteEntities);
	sakaiEntities.setProviders(siteProviders);
	return sakaiEntities;

    }

    private void initCache() {
	String cfg =
		ServerConfigurationService.getString(CACHE_ENABLED_CONFIG_KEY);
	if (cfg != null && cfg.equalsIgnoreCase("true")) {
	    CACHE_ENABLED = true;
	} else {
	    CACHE_ENABLED = false;
	}
	// We have to inject this config to the DAO
	coConfigDao.setCacheEnabled(CACHE_ENABLED);
	CACHE_CFG_INITIALIZED = true;
    }

    /** {@inheritDoc} */
    public boolean isCacheEnabled() {
	if (!CACHE_CFG_INITIALIZED) {
	    initCache();
	}
	return CACHE_ENABLED;
    }

}
