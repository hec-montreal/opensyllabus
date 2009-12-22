package org.sakaiquebec.opensyllabus.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentContentEdit;
import org.sakaiproject.assignment.api.AssignmentEdit;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.citation.api.Citation;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.citation.api.Schema;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.ResourceType;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiquebec.opensyllabus.api.OsylService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

public class OsylServiceImpl implements OsylService {

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

    // private CORelationDao CORelationDao;
    //
    // /**
    // * Sets the {@link CORelationDao}.
    // *
    // * @param coRelationDAO
    // */
    // public void setCORelationDao(CORelationDao coRelationDao) {
    // this.CORelationDao = coRelationDao;
    // }

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
	    String instructions, Date openDate, Date closeDate, Date dueDate, int percentage) {
	String siteId = "";
	String toolId = "";
	AssignmentEdit edit = null;
	AssignmentContentEdit contentEdit = null;

	Calendar cal = Calendar.getInstance();
	cal.setTime(openDate);
	int openYear=cal.get(Calendar.YEAR);
	int openMonth=cal.get(Calendar.MONTH)+1;
	int openDay=cal.get(Calendar.DAY_OF_MONTH);
//	int openHour=cal.get(Calendar.HOUR_OF_DAY);
//	int openMinute=cal.get(Calendar.MINUTE);
	int openHour=0;
	int openMinute=0;
	
	cal.setTime(closeDate);
	cal.add(Calendar.DATE, 1);
	int closeYear=cal.get(Calendar.YEAR);
	int closeMonth=cal.get(Calendar.MONTH)+1;
	int closeDay=cal.get(Calendar.DAY_OF_MONTH);
//	int closeHour=cal.get(Calendar.HOUR_OF_DAY);
//	int closeMinute=cal.get(Calendar.MINUTE);
	int closeHour=0;
	int closeMinute=-5;
	
	if(dueDate!=null){
	    cal.setTime(dueDate);
	    cal.add(Calendar.DATE, 1);
	    dueDate = cal.getTime();
	}
	int dueYear=cal.get(Calendar.YEAR);
	int dueMonth=cal.get(Calendar.MONTH)+1;
	int dueDay=cal.get(Calendar.DAY_OF_MONTH);
//	int dueHour=cal.get(Calendar.HOUR_OF_DAY);
//	int dueMinute=cal.get(Calendar.MINUTE);
	int dueHour=0;
	int dueMinute=-5;
	
	try {
	    siteId = osylSiteService.getCurrentSiteId();

	} catch (Exception e) {
	    log.error("Unable to retrieve current siteid", e);
	}

	try {
	    // The client doesn't know the id. It must be a new item
	    if (assignmentId==null || assignmentId.equals("")) {
		edit = assignmentService.addAssignment(siteId);
		contentEdit = assignmentService.addAssignmentContent(siteId);
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
			TimeService.newTimeLocal(dueYear, dueMonth,
				dueDay, dueHour, dueMinute, 0, 0);
		edit.setDueTime(dueTime);
	    }
	    edit.setDraft(false);
	    contentEdit.setTitle(title);
	    if (null != instructions) {
		contentEdit.setInstructions(instructions);
	    }
	    contentEdit
		    .setTypeOfSubmission(Assignment.TEXT_AND_ATTACHMENT_ASSIGNMENT_SUBMISSION);
	    contentEdit.setTypeOfGrade(Assignment.SCORE_GRADE_TYPE);

	    if (-1 != percentage) {
		// When we inject a value of 30 for example, Sakai accepts it
		// as a 3.0. That is why we multiply by 10, to be sure that we
		// have a valid value
		contentEdit.setMaxGradePoint(percentage * 10);
	    }
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

    /**
     * Delete a citation from the course outline citation list
     */
    public void removeCitation(String citationListId) {
	CitationCollection citationCollection = null;
	try {
	    citationCollection = citationService.getCollection(citationListId);
	    citationService.removeCollection(citationCollection);
	} catch (IdUnusedException e) {
	    log.error("Remove citation list - Id unused exception", e);
	}

    }

    /**
     * Add or updates a citation in the course outline citation list Here are
     * the available properties for a citation. These may not map directly with
     * OpenSyllabus, but they can be used id necessary creator title sourceTitle
     * year date volume issue pages startPage endPage abstract note
     * isnIdentifier subject Language locIdentifier dateRetrieved openURL doi
     * rights
     */
    public String createOrUpdateCitation(String citationListId,
	    String citation, String author, String type, String isbnIssn,
	    String link) {
	ContentResourceEdit resource = null;
	String resourceDir =
		getResourceReference() + OsylSiteService.WORK_DIRECTORY + "/";
	CitationCollection citationCollection = null;
	Citation newCitation = null;
	String siteId = null;

	// get temporary list of citation
	try {
	    if (citationListId == null || "".equalsIgnoreCase(citationListId))
		citationCollection = citationService.addCollection();
	    else
		citationCollection =
			citationService.getCollection(citationListId);
	} catch (IdUnusedException e) {
	    log.warn("Create or update citation list - Id unused exception", e);
	    return "failed";
	}

	try {
	    // temporary list of citation includes only one citation
	    if (citationCollection.getCitations().size() == 0) {
		newCitation = citationService.addCitation(type);
		citationCollection.add(newCitation);
	    } else {
		newCitation =
			(Citation) citationCollection.getCitations().get(0);
		updateCitationType(newCitation, type);
	    }
	} catch (NullPointerException e) {
	    log.warn("Create or update citation list - Id unused exception: ",
		    e);
	} catch (Exception e) {
	    log.warn("Create or update citation list - Exception while getting"
		    + " or adding resource: ", e);
	}
	// TODO: work around pour fournir un titre et et un nom à la citation
	String citationTitle;
	if (citation.length() > 30)
	    citationTitle = citation.substring(0, 30);
	else
	    citationTitle = citation;

	createOrUpdateCitationProperty(Schema.TITLE, newCitation, citation);
	createOrUpdateCitationProperty(Schema.ISN, newCitation, isbnIssn);
	createOrUpdateCitationProperty(Schema.CREATOR, newCitation, author);

	newCitation.setDisplayName(citationTitle);
	newCitation.setAdded(true);

	try {
	    String resourceId = resourceDir + citationCollection.getId();
	    // temporarily allow the user to read and write resources
	    siteId = osylSiteService.getCurrentSiteId();
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
					citationCollection.getId(),
					null,
					ContentHostingService.MAXIMUM_ATTEMPTS_FOR_UNIQUENESS);
	    } catch (Exception e2) {
		log.error(
			"Create or update citation list - Exception while adding"
				+ " resource: ", e2);
		return "failed";
	    }
	} catch (Exception e1) {
	    log.error(
		    "Create or update citation list - Exception while getting"
			    + " resource: ", e1);
	    return "failed";
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
	    citationService.save(citationCollection);

	    resource.setContent(citationCollection.getId().getBytes());
	    contentHostingService.commitResource(resource);

	} catch (RuntimeException e) {
	    log.error("Remove citation list - Runtime service exception", e);
	} catch (Exception e) {
	    log.error("Error while saving citation list to resources: ", e);
	}

	return citationCollection.getId();
    }

    private void createOrUpdateCitationProperty(String property,
	    Citation citation, String value) {
	List<String> values = new Vector<String>();
	values.add(value);

	if (citation.getCitationProperty(property) != "")
	    citation.updateCitationProperty(property, values);
	else
	    citation.setCitationProperty(property, value);
    }

    private void updateCitationType(Citation citation, String type) {
	Schema schema = citationService.getSchema(type);
	if (schema == null) {
	    schema = citationService.getSchema(CitationService.UNKNOWN_TYPE);
	}
	citation.setSchema(schema);
    }

    /**
     * Adds a new resource to the current context. Path must have a final slash.
     * 
     * @return String a string of the new unique url.
     */
    public String addRessource(String name, byte[] content, String contentType,
	    String siteId, String role) throws Exception {
	try {
	    // Generation of a valid resourceOutput directory
	    String resourceOutputDir =
		    contentHostingService.getSiteCollection(siteId)
			    + OsylSiteService.WORK_DIRECTORY + "/";

	    // Extraction of name and extension
	    String[] temp;
	    temp = name.split(OsylSiteService.FILE_DELIMITER);

	    // Add the resource and its content
	    ContentResourceEdit newResource =
		    contentHostingService.addResource(resourceOutputDir,
			    temp[0], temp[1], 1);
	    newResource.setContent(content);
	    newResource.setContentType(contentType);
	    contentHostingService.commitResource(newResource);

	    String resourceName = resourceOutputDir + name;
	    return resourceName;
	} catch (ServerOverloadException e) {
	    log.error("Add resource - Server overload exception", e);
	    // We wrap the exception in a java.lang.Exception.
	    // This way our "client" doesn't have to know about
	    // this Sakai exception.
	    throw new Exception(e);
	} catch (OverQuotaException e) {
	    log.error("Add resource - Over quota exception", e);
	    // see previous comment
	    throw new Exception(e);
	} catch (Exception e) {
	    log.error("Unable to add a new resource", e);
	    throw e;
	}
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
	    osylSecurityService.applyDirectoryPermissions(directoryId);

	    // we add the default citationList

	    String citationListName = "Références bibliographiques du cours";// TODO
	    // I18N
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
	    contentHostingService.commitResource(cre);
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

    /**
     * Creates a temporary citation that will be the citation created. With this
     * we won't need another callback from the server to retrieve the citationid
     * to be saved in the XML. If the user cancels his creation process, we
     * remove this citation list
     * 
     * @return citationId : the id of the new citation
     */
    private Citation createEmptyCitation() {
	return citationService.getTemporaryCitation();
    }

    /** {@inheritDoc} */
    public String createTemporaryCitationList() {
	CitationCollection citationList = citationService.addCollection();

	citationList.add(createEmptyCitation());

	return citationList.getId();

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
	// try {
	// String currentSiteId = osylSiteService.getCurrentSiteId();
	/*
	 * Site resourceSite; String parent = CORelationDao
	 * .getParentOfCourseOutline(currentSiteId); if
	 * (resourceURI.indexOf(parent, 0) != -1) {
	 * System.err.println(contentHostingService
	 * .allowGetResource(resourceURI) + " le parent est " + parent); //
	 * temporarily allow the user to read and write from assignments //
	 * (asn.revise permission) if
	 * (osylSecurityService.isAllowedToEdit(parent)) {
	 * SecurityService.pushAdvisor(new SecurityAdvisor() { public
	 * SecurityAdvice isAllowed(String userId, String function, String
	 * reference) { return SecurityAdvice.ALLOWED; } }); }
	 */
	// clear the permission

	// if (osylSecurityService.isAllowedToEdit(siteId)) {
	// SecurityService.clearAdvisors(); }

	/*
	 * } } catch (Exception e) { e.printStackTrace(); }
	 */
	return false;
    }

}
