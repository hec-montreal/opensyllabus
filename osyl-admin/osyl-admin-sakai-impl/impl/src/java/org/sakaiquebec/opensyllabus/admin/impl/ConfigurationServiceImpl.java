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

    private List<String> allowedFunctions = null;

    private List<String> disallowedFunctions = null;

    private String removedRole = null;

    private String functionsRole = null;

    private List<String> courses = null;
    
    private String courseOutlineXsl = null;

    private Map<String, Map<String, Object>> updatedRoles = null;
    
    private Map <String, String> configFiles = null;

    String description;
    private List<String> functions;
    private List<String> addedUsers;
    private List<String> removedUsers;

    protected ContentHostingService contentHostingService = null;

    public void setContentHostingService(ContentHostingService service) {
	contentHostingService = service;
    }
    
    private EventTrackingService eventTrackingService;
    
    public void setEventTrackingService(EventTrackingService eventTrackingService) {
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

	configFiles = new HashMap <String, String>();
	updateConfig(CONFIGFORLDER + OFFSITESCONFIGFILE);
	updateConfig(ROLEFOLDER);
	updateConfig(ADMIN_CONTENT_FOLDER + XSL_FILENAME);
	updateConfig(CONFIGFORLDER + FUNCTIONSSCONFIGFILE);

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

    private void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public Date getEndDate() {
	return getDate(endDate);
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
		if (referenceString.contains(ROLEFOLDER)) {
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
		
		if(referenceString.contains(XSL_FILENAME)){
		    log.info("Updating XSL in resource"
			    + referenceString);
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
		// We allow access to the file
		securityService.pushAdvisor(new SecurityAdvisor() {
		    public SecurityAdvice isAllowed(String userId,
			    String function, String reference) {
			if (function.equals("content.read"))
			    return SecurityAdvice.ALLOWED;
			return SecurityAdvice.NOT_ALLOWED;
		    }
		});
		if (fileName.contains(ROLEFOLDER)) {
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
			    retrieveConfigs(ress.getReference(), ress
				    .streamContent());
		    }
		} else if(fileName.contains(XSL_FILENAME)){
		    resource =
			    contentHostingService
				    .getResource(reference.getId());
		    try {
			setCourseOutlineXsl(new String(resource.getContent(),"UTF-8"));
		    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		    }
		} else{
		    resource =
			    contentHostingService
				    .getResource(reference.getId());
		    if (resource != null)
			retrieveConfigs(fileName, resource.streamContent());
		}
		// We remove access to the resource
		securityService.clearAdvisors();
	    } catch (PermissionException e) {
		log.info("You are not allowed to access this resource");
	    } catch (IdUnusedException e) {
		//The file has been removed - remove config in list
		if (configFiles != null && fileName.contains(ROLEFOLDER)) {
		    String role = configFiles.get(fileName);
		    configFiles.remove(fileName);
		    updatedRoles.remove(role);
		}
		if ( fileName.contains(OFFSITESCONFIGFILE)) {
		    setCourses(null);
		    setStartDate(null);
		    setEndDate(null);

		}
		if ( fileName.contains(FUNCTIONSSCONFIGFILE)) {
		    setFunctionsRole(null);
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
	    log.warn("There is no " + fileName + " file ");
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

		setCourses(courses);
		setStartDate(startDate);
		setEndDate(endDate);
		
		
	    }

	    if (configurationXml.contains(ROLEFOLDER)) {

		Map<String, Object> values = new HashMap<String, Object>();

		String role = retrieveParameter(document, ROLE);
		String description = retrieveParameter(document,DESCRIPTION);
		String addedUsers = retrieveParameter(document, ADDEDUSERS);
		String removedUsers = retrieveParameter(document, REMOVEDUSERS);
		String functions = retrieveParameter(document, FUNCTIONS);

		setDescription(description);
		setAddedUsers(addedUsers);
		setRemovedUsers(removedUsers);
		setFunctions(functions);

		values.put(ADDEDUSERS, this.addedUsers);
		values.put(REMOVEDUSERS, this.removedUsers);
		values.put(FUNCTIONS, this.functions);
		values.put(DESCRIPTION, this.description);

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
		String removedRole = retrieveParameter(document, REMOVED_ROLE);
		String allowedFunctions =
			retrieveParameter(document, ALLOWED_FUNCTIONS);
		String disallowedFunctions =
			retrieveParameter(document, DISALLOWED_FUNCTIONS);

		setFunctionsRole(fuctionsRole);
		setRemovedRole(removedRole);
		setAllowedFunctions(allowedFunctions);
		setDisallowedFunctions(disallowedFunctions);
	    }
	}

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

    public String getRoleToRemove() {
	return removedRole;
    }

    public void setCourseOutlineXsl(String courseOutlineXsl) {
	this.courseOutlineXsl = courseOutlineXsl;
    }

    public String getCourseOutlineXsl() {
	return courseOutlineXsl;
    }

}
