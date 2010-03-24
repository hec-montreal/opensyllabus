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
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
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

    public final static String COURSES_DELIMITER = ",";

    private Log log = LogFactory.getLog(ConfigurationServiceImpl.class);

    private String startDate = null;

    private String endDate = null;

    private List<String> courses = null;

    protected ContentHostingService contentHostingService = null;

    public void setContentHostingService(ContentHostingService service) {
	contentHostingService = service;
    }

    public void init() {

	log.info("initialize OsylAdmin configuration service");

	EventTrackingService.addObserver(this);
	
	updateConfig(CONFIGREF);

    }

    public void destroy() {
	log.info("destroy OsylAdmin configuration service");
    }

    private String getStartDate() {
	return startDate;
    }

    public Date getIntervalStartDate() {
	return getDate(getStartDate());
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    private String getEndDate() {
	return endDate;
    }

    public Date getIntervalEndDate() {
	return getDate(getEndDate());
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

    public List<String> getCourses() {
	return courses;
    }

    public void setCourses(List<String> courses) {
	this.courses = courses;
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
		if (referenceString.contains(CONFIGFILE)) {

		    log.info("Updating config from " + CONFIGFILE);
		    updateConfig(referenceString);
		}
	    }
	}
    }

    private void updateConfig(String fileReference) {
	Reference reference = EntityManager.newReference(fileReference);

	if (reference != null) {

	    ContentResource resource = null;

	    try {
		//We allow access to the file
		SecurityService.pushAdvisor(new SecurityAdvisor() {
			public SecurityAdvice isAllowed(String userId,
				String function, String reference) {
			    return SecurityAdvice.ALLOWED;
			}
		    });
		
		resource = contentHostingService.getResource(reference.getId());
		if (resource != null)
		    retrieveConfigs(fileReference, resource.streamContent());
		//We remove access to the resource 
		SecurityService.clearAdvisors();
	    } catch (PermissionException e) {
		log.error(e.getMessage());
	    } catch (IdUnusedException e) {
		log.warn("There is no " + fileReference + " file ");
	    } catch (TypeException e) {
		log.error(e.getMessage());
	    } catch (ServerOverloadException e) {
		log.error(e.getMessage());
	    }
	}
    }

    private void retrieveConfigs(String configurationXml, InputStream stream) {
	org.w3c.dom.Document document;

	/*
	 * Parse the XML - if that fails, give up now
	 */
	if ((document = parseXmlFromStream(stream)) == null) {
	    return;
	}

	synchronized (this) {
	    Map<String, String> parameterMap = new HashMap<String, String>();

	    saveParameter(document, parameterMap, "startDate");
	    saveParameter(document, parameterMap, "endDate");
	    saveParameter(document, parameterMap, "courses");

	    setStartDate(parameterMap.get("startDate"));
	    setEndDate(parameterMap.get("endDate"));
	    setCourses(getCourses(parameterMap.get("courses")));

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

    private List<String> getCourses(String courses) {
	List<String> allCourses = new ArrayList<String>();
	String[] coursesTable = courses.split(COURSES_DELIMITER);
	for (int i = 0; i < coursesTable.length; i++) {
	    allCourses.add(coursesTable[i].trim());
	}

	return allCourses;
    }

    /**
     * Lookup and save one dynamic configuration parameter
     * 
     * @param Configuration XML
     * @param parameterMap Parameter name=value pairs
     * @param name Parameter name
     */
    private void saveParameter(org.w3c.dom.Document document,
	    Map<String, String> parameterMap, String name) {
	String value;
	if ((value = getText(document.getDocumentElement(), name)) != null) {
	    parameterMap.put(name, value);
	}
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

}
