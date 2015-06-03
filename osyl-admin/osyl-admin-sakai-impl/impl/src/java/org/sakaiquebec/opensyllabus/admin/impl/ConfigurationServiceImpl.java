/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.admin.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ConfigurationServiceImpl implements ConfigurationService, Observer {

    public final static String LIST_DELIMITER = ",";

    private Log log = LogFactory.getLog(ConfigurationServiceImpl.class);

    private String startDate = null;

    private String endDate = null;

    private List<String> programs = null;

    private List<String> servEns = null;

    private List<String> allowedFunctions = null;

    private List<String> disallowedFunctions = null;

    private String removedRole = null;

    private String functionsRole = null;

    private List<String> courses = null;

    private String courseOutlineXsl = null;

    private Map<String, Map<String, Object>> updatedRoles = null;

    private Map<String, Map<String, String>> cmExceptions = null;

    private Map<String, String> configFiles = null;

    private String sessionId = null;

    private String roleId = null;

    private String permissions = null;

    private List<String> frozenPermissions;

    private HashMap<String, List<String>> frozenFunctionsToAllow;

    private String description;
    private boolean courseManagement;
    
    private boolean includingFrozenSites;

    private boolean includingDirSites;

    private List<String> functions;
    private List<String> addedUsers;
    private List<String> removedUsers;
    private List<String> replacedUsers;

    private Map<String, String> printVersionJobParams = null;

    protected ContentHostingService contentHostingService = null;

    public void setContentHostingService(ContentHostingService service) {
	contentHostingService = service;
    }

    private EventTrackingService eventTrackingService;

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

    public void init() {
	log.info("initialize OsylAdmin configuration service");

	eventTrackingService.addObserver(this);

	configFiles = new HashMap<String, String>();
	updateConfig(CONFIG_FOLDER + OFFSITESCONFIGFILE);
	updateConfig(ROLE_FOLDER);
	updateConfig(ADMIN_CONTENT_FOLDER + XSL_FILENAME);
	updateConfig(CONFIG_FOLDER + FUNCTIONSSCONFIGFILE);
	updateConfig(CONFIG_FOLDER + PRINT_VERSION_CONFIG);
	updateConfig(CM_EXCEPTIONS_FOLDER);
    }

    public String getFunctionsRole() {
	return functionsRole;
    }

    private void setFunctionsRole(String functionsRole) {
	this.functionsRole = functionsRole;
    }

    public List<String> getAllowedFunctions() {
	return allowedFunctions;
    }

    private void setAllowedFunctions(String allowedFunctions) {
	this.allowedFunctions = new ArrayList<String>();
	if (allowedFunctions != null && allowedFunctions.length() > 0) {
	    String[] allowedFunctionsTable =
		    allowedFunctions.split(LIST_DELIMITER);
	    for (int i = 0; i < allowedFunctionsTable.length; i++) {
		this.allowedFunctions.add(allowedFunctionsTable[i].trim());
	    }
	}
    }

    
   
    public List<String> getDisallowedFunctions() {
	return disallowedFunctions;
    }

    private void setDisallowedFunctions(String disallowedFunctions) {
	this.disallowedFunctions = new ArrayList<String>();
	if (disallowedFunctions != null && disallowedFunctions.length() > 0) {
	    String[] disallowedFunctionsTable =
		    disallowedFunctions.split(LIST_DELIMITER);
	    for (int i = 0; i < disallowedFunctionsTable.length; i++) {
		this.disallowedFunctions
			.add(disallowedFunctionsTable[i].trim());
	    }
	}
    }

    public String getRemovedRole() {
	return removedRole;
    }

    private void setRemovedRole(String removedRole) {
	this.removedRole = removedRole;
    }

    public void destroy() {
	log.info("destroy OsylAdmin configuration service");
    }

    public Date getStartDate() {
	return getDate(startDate);
    }

    private void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    private void setPrograms(String programs) {
	this.programs = new ArrayList<String>();
	if (programs != null && programs.length() > 0) {
	    String[] programsTable = programs.split(LIST_DELIMITER);
	    for (int i = 0; i < programsTable.length; i++) {
		this.programs.add(programsTable[i].trim());
	    }
	}
    }

    private void setServEns(String servEns) {
	this.servEns = new ArrayList<String>();
	if (servEns != null && servEns.length() > 0) {
	    String[] servEnsTable = servEns.split(LIST_DELIMITER);
	    for (int i = 0; i < servEnsTable.length; i++) {
		this.servEns.add(servEnsTable[i].trim());
	    }
	}
    }

    private void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public Date getEndDate() {
	return getDate(endDate);
    }

    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
    }

    public String getRoleId() {
	return roleId;
    }

    public void setRoleId(String roleId) {
	this.roleId = roleId;
    }

    public String getPermissions() {
	return permissions;
    }

    public void setPermissions(String permissions) {
	this.frozenPermissions = new ArrayList<String>();
	if (permissions != null && permissions.length() > 0) {
	    String[] permissionsTable = permissions.split(LIST_DELIMITER);
	    for (int i = 0; i < permissionsTable.length; i++) {
		this.frozenPermissions.add(permissionsTable[i].trim());
	    }
	}
    }

    public List<String> getFrozenPermissions() {
	return frozenPermissions;
    }

    public void setFrozenPermissions(List<String> frozenPermissions) {
	this.frozenPermissions = frozenPermissions;
    }

    public HashMap<String, List<String>> getFrozenFunctionsToAllow() {
	return frozenFunctionsToAllow;
    }

    public void setFrozenFunctionsToAllow(
	    HashMap<String, List<String>> frozenFunctionsToAllow) {
	this.frozenFunctionsToAllow = frozenFunctionsToAllow;
    }

    /**
     * Called when an observed object changes.
     * 
     * @param ob
     * @param o
     */
    public void update(Observable ob, Object o) {
	if (o instanceof Event) {

	    Event event = (Event) o;

	    if (event.getModify()) {
		String referenceString = event.getResource();

		// If the offSitesConfig.xml update, we update the values
		if (referenceString.contains(OFFSITESCONFIGFILE)) {
		    log.info("Updating official sites config from "
			    + referenceString);
		    updateConfig(referenceString);
		}

		// If the content of the role folder update, we update the
		// values
		if (referenceString.contains(ROLE_FOLDER)) {
		    log.info("Updating roles config files from "
			    + referenceString);
		    updateConfig(referenceString);
		}

		// If the functions files updated we change the values
		if (referenceString.contains(FUNCTIONSSCONFIGFILE)) {
		    log.info("Updating permissions config files from "
			    + referenceString);
		    updateConfig(referenceString);
		}

		if (referenceString.contains(XSL_FILENAME)) {
		    log.info("Updating XSL in resource" + referenceString);
		    updateConfig(referenceString);
		}

		if (referenceString.contains(PRINT_VERSION_CONFIG)) {
		    log.info("Updating 'createPrintVersion' job config"
			    + referenceString);
		    updateConfig(referenceString);
		}
		if (referenceString.contains(CM_EXCEPTIONS_FOLDER)) {
		    log.info("Updating CM exceptions" + referenceString);
		    updateConfig(referenceString);
		}

	    }
	}
    }

    private void updateConfig(String fileName) {

	Reference reference = entityManager.newReference(fileName);

	if (reference != null) {

	    ContentResource resource = null;

	    try {
		log.info("*** securityService.pushAdvisor(new SecurityAdvisor() ConfigurationServiceImpl *** ");

		// We allow access to the file

		securityService.pushAdvisor(new SecurityAdvisor() {
		    public SecurityAdvice isAllowed(String userId,
			    String function, String reference) {
			if (function.equals("content.read"))
			    return SecurityAdvice.ALLOWED;
			return SecurityAdvice.NOT_ALLOWED;
		    }
		});

		if (fileName.contains(ROLE_FOLDER)) {
		    ContentCollection collection;
		    if (!contentHostingService.isCollection(reference.getId())) {
			resource =
				contentHostingService.getResource(reference
					.getId());

			collection = resource.getContainingCollection();
		    } else {
			collection =
				contentHostingService.getCollection(reference
					.getId());
		    }
		    List<ContentResource> resources =
			    contentHostingService.getAllResources(collection
				    .getId());

		    for (ContentResource ress : resources) {
			if (ress != null)
			    retrieveConfigs(ress.getReference(),
				    ress.streamContent());
		    }
		} else if (fileName.contains(CM_EXCEPTIONS_FOLDER)) {
		    ContentCollection collection;
		    if (!contentHostingService.isCollection(reference.getId())) {
			resource =
				contentHostingService.getResource(reference
					.getId());

			collection = resource.getContainingCollection();
		    } else {
			collection =
				contentHostingService.getCollection(reference
					.getId());
		    }
		    List<ContentResource> resources =
			    contentHostingService.getAllResources(collection
				    .getId());

		    for (ContentResource ress : resources) {
			if (ress != null)
			    retrieveCmExceptions(ress.getReference(),
				    ress.streamContent());
		    }
		} else if (fileName.contains(XSL_FILENAME)) {
		    resource =
			    contentHostingService
				    .getResource(reference.getId());
		    try {
			setCourseOutlineXsl(new String(resource.getContent(),
				"UTF-8"));
		    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		    }
		} else {
		    resource =
			    contentHostingService
				    .getResource(reference.getId());
		    if (resource != null)
			retrieveConfigs(fileName, resource.streamContent());
		}

	    } catch (PermissionException e) {
		log.info("You are not allowed to access this resource");
	    } catch (IdUnusedException e) {
		// The file has been removed - remove config in list
		if (configFiles != null) {
		    if (fileName.contains(ROLE_FOLDER)) {
			String role = configFiles.get(fileName);
			configFiles.remove(fileName);
			if (updatedRoles != null) {
			    updatedRoles.remove(role);
			}
		    }
		}
		if (fileName.contains(OFFSITESCONFIGFILE)) {
		    setCourses(null);
		    setStartDate(null);
		    setEndDate(null);
		    setPrograms(null);
		    setServEns(null);

		}
		if (fileName.contains(FUNCTIONSSCONFIGFILE)) {
		    setFunctionsRole(null);
		    setDescription(null);
		    setRemovedRole(null);
		    setAllowedFunctions(null);
		    setDisallowedFunctions(null);

		}
		


		log.info("There is no " + fileName + " has been removed ");
	    } catch (TypeException e) {
		log.info("The resource requested has the wrong type");
	    } catch (ServerOverloadException e) {
		log.info(e.getMessage());
	    }
	} else {
	    securityService.popAdvisor();
	    log.warn("There is no " + fileName + " file ");
	}
    }

    private void retrieveCmExceptions(String configurationXml,
	    InputStream stream) {
	org.w3c.dom.Document document;

	/*
	 * Parse the XML - if that fails, give up now
	 */
	if ((document = parseXmlFromStream(stream)) == null) {
	    log.warn("retrieveConfigs: XML document is null");
	    return;
	}

	synchronized (this) {

	    String users = retrieveParameter(document, CM_EXCEPTIONS_USERS);
	    String courses = retrieveParameter(document, CM_EXCEPTIONS_COURSES);
	    String category =
		    retrieveParameter(document, CM_EXCEPTIONS_CATEGORY);
	    String program = retrieveParameter(document, CM_EXCEPTIONS_PROGRAM);
	    String role = retrieveParameter(document, CM_EXCEPTIONS_ROLE);

	    if (cmExceptions == null)
		cmExceptions = new HashMap<String, Map<String, String>>();
	    HashMap<String, String> map = new HashMap<String, String>();
	    map.put(CM_EXCEPTIONS_USERS, users);
	    map.put(CM_EXCEPTIONS_COURSES, courses);
	    map.put(CM_EXCEPTIONS_CATEGORY, category);
	    map.put(CM_EXCEPTIONS_PROGRAM, program);
	    map.put(CM_EXCEPTIONS_ROLE, role);

	    cmExceptions.put(configurationXml, map);

	}
    }

    public void getFrozenSessionIdConfig() {
	String fileName = CONFIG_FOLDER + UNFROZENSITESCONFIG;
	Reference reference = entityManager.newReference(fileName);
	if (reference != null) {
	    ContentResource resource = null;
	    try {
		resource = contentHostingService.getResource(reference.getId());
		if (resource != null)
		    retrieveConfigs(fileName, resource.streamContent());
	    } catch (PermissionException e) {
		log.info("You are not allowed to access this resource");
	    } catch (IdUnusedException e) {
		if (fileName.contains(UNFROZENSITESCONFIG)) {
		    setSessionId(null);
		}
		log.info("There is no " + fileName + " has been removed ");
	    } catch (TypeException e) {
		log.info("The resource requested has the wrong type");
	    } catch (ServerOverloadException e) {
		log.info(e.getMessage());
	    }
	}
    }

    public void getConfigToFreeze() {
	String fileName = CONFIG_FOLDER + FROZENSITESCONFIG;
	Reference reference = entityManager.newReference(fileName);
	if (reference != null) {
	    ContentResource resource = null;
	    try {
		resource = contentHostingService.getResource(reference.getId());
		if (resource != null)
		    retrieveConfigs(fileName, resource.streamContent());
	    } catch (PermissionException e) {
		log.info("You are not allowed to access this resource");
	    } catch (IdUnusedException e) {
		if (fileName.contains(FROZENSITESCONFIG)) {
		    setSessionId(null);
		    setFrozenFunctionsToAllow(null);
		}
		log.info("There is no " + fileName + " has been removed ");
	    } catch (TypeException e) {
		log.info("The resource requested has the wrong type");
	    } catch (ServerOverloadException e) {
		log.info(e.getMessage());
	    }
	}
    }

    private void retrieveConfigs(String configurationXml, InputStream stream) {
	org.w3c.dom.Document document;

	/*
	 * Parse the XML - if that fails, give up now
	 */
	if ((document = parseXmlFromStream(stream)) == null) {
	    log.warn("retrieveConfigs: XML document is null");
	    return;
	}

	synchronized (this) {
	    if (configurationXml.contains(OFFSITESCONFIGFILE)) {
		String courses = retrieveParameter(document, COURSES);
		String endDate = retrieveParameter(document, ENDDATE);
		String startDate = retrieveParameter(document, STARTDATE);
		String programs = retrieveParameter(document, PROGRAMS);
		String servEns = retrieveParameter(document, SERVENS);

		setCourses(courses);
		setStartDate(startDate);
		setEndDate(endDate);
		setPrograms(programs);
		setServEns(servEns);
	    }

	    if (configurationXml.contains(ROLE_FOLDER)) {
		Map<String, Object> values = new HashMap<String, Object>();
		String role = retrieveParameter(document, ROLE);
		String description = retrieveParameter(document, DESCRIPTION);
		String courseManagement = retrieveParameter(document, COURSEMANAGEMENT);
		boolean includingFrozenSites =
			Boolean.parseBoolean(retrieveParameter(document,
				INCLUDING_FROZEN_SITES));
		boolean includingDirSites =
			Boolean.parseBoolean(retrieveParameter(document,
				INCLUDING_DIR_SITES));
		String addedUsers = retrieveParameter(document, ADDEDUSERS);
		String removedUsers = retrieveParameter(document, REMOVEDUSERS);
		String replacedUsers = retrieveParameter(document, REPLACEDUSERS);
		String functions = retrieveParameter(document, FUNCTIONS);

		setDescription(description);
		setCourseManagement(courseManagement);
		setIncludingFrozenSites(includingFrozenSites);
		setIncludingDirSites(includingDirSites);
		setAddedUsers(addedUsers);
		setRemovedUsers(removedUsers);
		setReplacedUsers(replacedUsers);
		setFunctions(functions);

		values.put(ADDEDUSERS, this.addedUsers);
		values.put(REMOVEDUSERS, this.removedUsers);
		values.put(FUNCTIONS, this.functions);
		values.put(DESCRIPTION, this.description);
		values.put(REPLACEDUSERS, this.replacedUsers);
		values.put(COURSEMANAGEMENT, this.courseManagement);
		values.put(INCLUDING_DIR_SITES, includingDirSites);
		values.put(INCLUDING_FROZEN_SITES, includingFrozenSites);

		if (role != null) {
		    if (updatedRoles == null)
			updatedRoles =
				new HashMap<String, Map<String, Object>>();

		    if (updatedRoles.containsKey(role))
			updatedRoles.remove(role);
		    updatedRoles.put(role, values);
		    configFiles.put(configurationXml, role);
		}
	    }

	    if (configurationXml.contains(FUNCTIONSSCONFIGFILE)) {
		String fuctionsRole = retrieveParameter(document, ROLE);
		String description = retrieveParameter(document, DESCRIPTION);
		String removedRole = retrieveParameter(document, REMOVED_ROLE);
		boolean includingFrozenSites =
			Boolean.parseBoolean(retrieveParameter(document,
				INCLUDING_FROZEN_SITES));
		boolean includingDirSites =
			Boolean.parseBoolean(retrieveParameter(document,
				INCLUDING_DIR_SITES));
		String allowedFunctions =
			retrieveParameter(document, ALLOWED_FUNCTIONS);
		String disallowedFunctions =
			retrieveParameter(document, DISALLOWED_FUNCTIONS);

		setFunctionsRole(fuctionsRole);
		setDescription(description);
		setRemovedRole(removedRole);
		setIncludingFrozenSites(includingFrozenSites);
		setIncludingDirSites(includingDirSites);
		setAllowedFunctions(allowedFunctions);
		setDisallowedFunctions(disallowedFunctions);
	    }

	    if (configurationXml.contains(FROZENSITESCONFIG)) {
		String sessionId = retrieveParameter(document, SESSIONID);
		setSessionId(sessionId);
		setFrozenFunctionsToAllow(getFrozenPermissionsByRole(document));
	    }

	    if (configurationXml.contains(UNFROZENSITESCONFIG)) {
		String frozenSessionId = retrieveParameter(document, SESSIONID);
		setSessionId(frozenSessionId);
	    }

	    if (configurationXml.contains(PRINT_VERSION_CONFIG)) {
		printVersionJobParams = new HashMap<String, String>();
		printVersionJobParams.put(INCLUDING_DIR_SITES,
			retrieveParameter(document, INCLUDING_DIR_SITES));
		printVersionJobParams.put(INCLUDING_FROZEN_SITES,
			retrieveParameter(document, INCLUDING_FROZEN_SITES));
	    }
	}
    }

    private HashMap<String, List<String>> getFrozenPermissionsByRole(
	    Document document) {
	HashMap<String, List<String>> rolesToFrozen =
		new HashMap<String, List<String>>();
	// get the root element
	Element docEle = document.getDocumentElement();
	// get a nodelist of elements
	NodeList nl = docEle.getElementsByTagName(ROLESET);
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the role element
		Element element = (Element) nl.item(i);
		// get the role object
		String roleId = element.getAttribute(ROLEID);
		String permissions = element.getTextContent();
		setPermissions(permissions);
		// add it to list
		List<String> permissionsAllowed = this.getFrozenPermissions();
		rolesToFrozen.put(roleId, permissionsAllowed);
	    }
	    return rolesToFrozen;
	}
	return rolesToFrozen;
    }

    private Date getDate(String date) {
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	Date convertedDate = null;
	try {
	    convertedDate = dateFormat.parse(date);
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return convertedDate;

    }

    private void setCourses(String courses) {
	List<String> allCourses = new ArrayList<String>();
	if (courses != null && courses.length() > 0) {
	    String[] coursesTable = courses.split(LIST_DELIMITER);
	    for (int i = 0; i < coursesTable.length; i++) {
		allCourses.add(coursesTable[i].trim());
	    }
	}
	this.courses = allCourses;
    }

    public List<String> getCourses() {
	return courses;
    }

    private void setDescription(String description) {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public boolean isCourseManagement() {
        return courseManagement;
    }

    public void setCourseManagement(String courseManagement) {
        this.courseManagement = Boolean.parseBoolean(courseManagement);
    }

    /**
     * @return the includingFrozenSites value.
     */
    public boolean isIncludingFrozenSites() {
	return includingFrozenSites;
    }

    /**
     * @param includingFrozenSites the new value of includingFrozenSites.
     */
    private void setIncludingFrozenSites(boolean includingFrozenSites) {
	this.includingFrozenSites = includingFrozenSites;
    }

    /**
     * @return the includingDirSites value.
     */
    public boolean isIncludingDirSites() {
	return includingDirSites;
    }

    /**
     * @param includingDirSites the new value of includingDirSites.
     */
    private void setIncludingDirSites(boolean includingDirSites) {
	this.includingDirSites = includingDirSites;
    }

    private void setFunctions(String functions) {
	this.functions = new ArrayList<String>();
	if (functions != null && functions.length() > 0) {
	    String[] functionsTable = functions.split(LIST_DELIMITER);
	    for (int i = 0; i < functionsTable.length; i++) {
		this.functions.add(functionsTable[i].trim());
	    }
	}
    }

    private void setAddedUsers(String addedUsers) {
	this.addedUsers = new ArrayList<String>();
	if (addedUsers != null && addedUsers.length() > 0) {
	    String[] addedUsersTable = addedUsers.split(LIST_DELIMITER);
	    for (int i = 0; i < addedUsersTable.length; i++) {
		this.addedUsers.add(addedUsersTable[i].trim());
	    }
	}
    }

    private void setRemovedUsers(String removedUsers) {
	this.removedUsers = new ArrayList<String>();
	if (removedUsers != null && removedUsers.length() > 0) {
	    String[] removedUsersTable = removedUsers.split(LIST_DELIMITER);
	    for (int i = 0; i < removedUsersTable.length; i++) {
		this.removedUsers.add(removedUsersTable[i].trim());
	    }
	}
    }
    
    private void setReplacedUsers(String replacedUsers) {
	this.replacedUsers = new ArrayList<String>();
	if (replacedUsers != null && replacedUsers.length() > 0) {
	    String[] replacedUsersTable = replacedUsers.split(LIST_DELIMITER);
	    for (int i = 0; i < replacedUsersTable.length; i++) {
		this.replacedUsers.add(replacedUsersTable[i].trim());
	    }
	}
    }
    

    /**
     * Lookup and rerieve one dynamic configuration parameter
     * 
     * @param Configuration XML
     * @param name Parameter name
     */
    private String retrieveParameter(org.w3c.dom.Document document, String name) {
	String value = getText(document.getDocumentElement(), name);
	if ((value) != null) {
	    return value;
	} else
	    return null;
    }

    /**
     * Get the text associated with this element
     * 
     * @param root The root node of the text element
     * @return Text (trimmed of leading/trailing whitespace, null if none)
     */
    private String getText(Element root, String elementName) {
	NodeList nodeList;
	Node parent;
	String text;

	nodeList = root.getElementsByTagName(elementName);
	if (nodeList.getLength() == 0) {
	    return null;
	}

	text = null;
	parent = (Element) nodeList.item(0);

	for (Node child = parent.getFirstChild(); child != null; child =
		child.getNextSibling()) {
	    switch (child.getNodeType()) {
	    case Node.TEXT_NODE:
		text = normalizeText(text, child.getNodeValue());
		break;

	    default:
		break;
	    }
	}
	return text == null ? text : text.trim();
    }

    /**
     * "Normalize" XML text node content to create a simple string
     * 
     * @param original Original text
     * @param update Text to add to the original string (a space separates the
     *            two)
     * @return Concatenated contents (trimmed)
     */
    private String normalizeText(String original, String update) {
	StringBuilder result;

	if (original == null) {
	    return (update == null) ? "" : update.trim();
	}

	result = new StringBuilder(original.trim());
	result.append(' ');
	result.append(update.trim());

	return result.toString();
    }

    /**
     * Parse an XML resource
     * 
     * @param filename The filename (or URI) to parse
     * @return DOM Document (null if parse fails)
     */
    private Document parseXmlFromStream(InputStream stream) {
	try {
	    DocumentBuilderFactory factory;

	    factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(false);

	    DocumentBuilder documentBuilder = factory.newDocumentBuilder();

	    if (documentBuilder != null) {
		return documentBuilder.parse(stream);
	    }
	} catch (Exception exception) {
	    log.warn("XML parse on \"" + stream + "\" failed: " + exception);
	}
	return null;
    }

    public Map<String, Map<String, Object>> getUdatedRoles() {
	return updatedRoles;
    }

    public Map<String, Map<String, String>> getCmExceptions() {
	return cmExceptions;
    }

    public String getRoleToRemove() {
	return removedRole;
    }

    public void setCourseOutlineXsl(String courseOutlineXsl) {
	this.courseOutlineXsl = courseOutlineXsl;
    }

    public String getCourseOutlineXsl() {
	return courseOutlineXsl;
    }

    public List<String> getServEns() {
	return servEns;
    }

    public List<String> getPrograms() {
	return programs;
    }

    /**
     * @return the printVersionJobParams value.
     */
    public Map<String, String> getPrintVersionJobParams() {
	return printVersionJobParams;
    }
}
