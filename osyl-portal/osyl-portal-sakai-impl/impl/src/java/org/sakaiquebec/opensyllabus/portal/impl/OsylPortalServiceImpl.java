/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.portal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.portal.api.OsylPortalService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylPortalServiceImpl implements OsylPortalService {

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(OsylPortalServiceImpl.class);

    private Map<String, List<CODirectorySite>> courseListByAcadCareerMap;

    private Map<String, List<CODirectorySite>> courseListByResponsibleMap;

    private List<AcademicSession> currentSessions;

    private String webappDir = System.getProperty("catalina.home")
	    + File.separator + "webapps" + File.separator
	    + "osyl-portal-sakai-tool" + File.separator;// Ugly but don't know
							// how;

    private Timer timer;
    
    //SPRING INJECTONS
    private CourseManagementService courseManagementService;

    public void setCourseManagementService(
	    CourseManagementService courseManagementService) {
	this.courseManagementService = courseManagementService;
    }

    private OsylPublishService osylPublishService;

    public void setOsylPublishService(OsylPublishService osylPublishService) {
	this.osylPublishService = osylPublishService;
    }

    private UserDirectoryService userDirectoryService;

    public void setUserDirectoryService(
	    UserDirectoryService userDirectoryService) {
	this.userDirectoryService = userDirectoryService;
    }
    //END SPRING INJECTIONS

    /**
     * Init method called right after Spring injection.
     */
    public void init() {
	timer = new Timer();
	TimerTask timerTask = new TimerTask() {

	    @Override
	    public void run() {
		buildCoursesMaps();
	    }
	};
	timer.schedule(timerTask, 50000, 86400000);
    }

    public void destroy() {
	log.info("DESTROY from OsylPortalService");
	timer.cancel();
	timer = null;
    }

    private void buildCoursesMaps() {
	log.info("Start building course maps");
	long start = System.currentTimeMillis();

	currentSessions = courseManagementService.getCurrentAcademicSessions();

	// ACAD_CARRER
	Map<String, List<CODirectorySite>> temp_courseListByAcadCareerMap =
		new HashMap<String, List<CODirectorySite>>();
	for (String acad_career : ACAD_CARREERS) {
	    temp_courseListByAcadCareerMap.put(acad_career,
		    buildCoursesListForAcadCareer(acad_career, webappDir));
	}
	courseListByAcadCareerMap = temp_courseListByAcadCareerMap;

	// RESPONSIBLE
	Map<String, List<CODirectorySite>> temp_courseListByResponsibleMap =
		new HashMap<String, List<CODirectorySite>>();
	for (String responsible : courseManagementService
		.getSectionCategories()) {
	    temp_courseListByResponsibleMap.put(responsible,
		    buildCoursesListForResponsible(responsible, webappDir));
	}
	courseListByResponsibleMap = temp_courseListByResponsibleMap;

	log.info("Finished building course maps in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    private List<CODirectorySite> buildCoursesListForAcadCareer(
	    String acadCareer, String webappDir) {
	List<CODirectorySite> coursesList = new ArrayList<CODirectorySite>();
	List<String> coursesName = new ArrayList<String>();
	Set<CourseOffering> courseOfferings =
		courseManagementService
			.findCourseOfferingsByAcadCareer(acadCareer);

	for (CourseOffering courseOffering : courseOfferings) {
	    if (!coursesName.contains(courseOffering.getCanonicalCourseEid())) {
		CODirectorySite coSite =
			fillCODirectorySiteWithCourseOffering(courseOffering,
				webappDir);
		coursesList.add(coSite);
		coursesName.add(courseOffering.getCanonicalCourseEid());
	    }
	}
	return coursesList;
    }

    private List<CODirectorySite> buildCoursesListForResponsible(
	    String responsible, String webappDir) {
	List<String> coursesName = new ArrayList<String>();
	List<CODirectorySite> coursesList = new ArrayList<CODirectorySite>();
	Set<Section> sections =
		courseManagementService.findSectionsByCategory(responsible);
	for (Section section : sections) {
	    CourseOffering courseOffering =
		    courseManagementService.getCourseOffering(section
			    .getCourseOfferingEid());
	    if (!coursesName.contains(courseOffering.getCanonicalCourseEid())) {
		CODirectorySite coSite =
			fillCODirectorySiteWithCourseOffering(courseOffering,
				webappDir);
		coursesList.add(coSite);
		coursesName.add(courseOffering.getCanonicalCourseEid());
	    }
	}
	return coursesList;
    }

    private CODirectorySite fillCODirectorySiteWithCourseOffering(
	    CourseOffering courseOffering, String webappDir) {
	CODirectorySite coDirectorySite = new CODirectorySite();
	coDirectorySite.setCourseNumber(getDirectorySiteName(courseOffering
		.getCanonicalCourseEid()));
	coDirectorySite.setCourseName(courseOffering.getTitle());
	coDirectorySite.setProgram(courseOffering.getAcademicCareer());
	coDirectorySite.setCredits("3");// TODO
	coDirectorySite
		.setRequirements("Vous devez avoir suivi les cours suivants pour vous inscrire<br><b>COURS-101</b><br><b>COURS-102</b>");// TODO

	Set<Section> sections =
		courseManagementService.getSections(courseOffering.getEid());
	for (Section section : sections) {
	    if (!section.getEid().endsWith("00")) {
		Set<String> instructors =
			section.getEnrollmentSet().getOfficialInstructors();
		if (!instructors.isEmpty()) {
		    String userName = "";
		    try {
			User user =
				userDirectoryService
					.getUserByEid((String) instructors
						.toArray()[0]);
			userName =
				user.getFirstName().substring(0, 1)
					.toUpperCase()
					+ user.getFirstName().substring(1)
					+ " "
					+ user.getLastName().substring(0, 1)
						.toUpperCase()
					+ user.getLastName().substring(1);
		    } catch (Exception e) {
		    }
		    coDirectorySite.putSection(getSiteName(section), userName);
		}
	    }
	    if (section.getCategory() != null
		    && !section.getCategory().equals("")) {
		coDirectorySite.setResponsible(section.getCategory());
	    }
	}

	// if (!currentSessions.contains(courseOffering.getAcademicSession()))
	// coDirectorySite.setSections(new HashMap<String, String>());

	coDirectorySite.setProgram(courseOffering.getAcademicCareer());

	String description = "";
	try {
	    COSerialized coSerialized =
		    osylPublishService
			    .getSerializedPublishedCourseOutlineForAccessType(
				    coDirectorySite.getCourseNumber(),
				    SecurityInterface.ACCESS_PUBLIC, webappDir);
	    Node d = XmlHelper.parseXml(coSerialized.getContent());
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr;
	    expr =
		    xpath.compile("//asmContext[semanticTag[@type='HEC']='description']/asmResource/text");

	    NodeList nodes =
		    (NodeList) expr.evaluate(d, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);
		description += node.getTextContent();
	    }
	} catch (Exception e) {
	}
	coDirectorySite.setDescription(description);

	return coDirectorySite;
    }

    private String getDirectorySiteName(String canCourseId) {
	String courseId = null;
	String courseIdFront = null;
	String courseIdMiddle = null;
	String courseIdBack = null;

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
	    if (canCourseId.endsWith("A") || canCourseId.endsWith("E")
		    || canCourseId.endsWith("R")) {
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
	return courseId;
    }

    private String getSiteName(Section section) {
	String siteName = null;
	String sectionId = section.getEid();
	String courseOffId = section.getCourseOfferingEid();
	CourseOffering courseOff =
		courseManagementService.getCourseOffering(courseOffId);
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
	    if (canCourseId.endsWith("A") || canCourseId.endsWith("E")
		    || canCourseId.endsWith("R")) {
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

	if (periode == null)
	    siteName = courseId + "." + sessionTitle + "." + groupe;
	else
	    siteName =
		    courseId + "." + sessionTitle + "." + periode + "."
			    + groupe;

	return siteName;
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

    @Override
    public List<CODirectorySite> getCoursesForAcadCareer(String acadCareer) {
	for (CODirectorySite coDirectorySite : courseListByAcadCareerMap
		.get(acadCareer)) {
	    String description = "";
	    try {
		COSerialized coSerialized =
			osylPublishService
				.getSerializedPublishedCourseOutlineForAccessType(
					coDirectorySite.getCourseNumber(),
					SecurityInterface.ACCESS_PUBLIC,
					webappDir);
		Node d = XmlHelper.parseXml(coSerialized.getContent());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr;
		expr =
			xpath.compile("//asmContext[semanticTag[@type='HEC']='description']/asmResource/text");

		NodeList nodes =
			(NodeList) expr.evaluate(d, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
		    Node node = nodes.item(i);
		    description += node.getTextContent();
		}
	    } catch (Exception e) {
	    }
	    coDirectorySite.setDescription(description);
	}
	return courseListByAcadCareerMap.get(acadCareer);
    }

    public List<CODirectorySite> getCoursesForResponsible(String responsible) {
	for (CODirectorySite coDirectorySite : courseListByResponsibleMap
		.get(responsible)) {
	    String description = "";
	    try {
		COSerialized coSerialized =
			osylPublishService
				.getSerializedPublishedCourseOutlineForAccessType(
					coDirectorySite.getCourseNumber(),
					SecurityInterface.ACCESS_PUBLIC,
					webappDir);
		Node d = XmlHelper.parseXml(coSerialized.getContent());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr;
		expr =
			xpath.compile("//asmContext[semanticTag[@type='HEC']='description']/asmResource/text");

		NodeList nodes =
			(NodeList) expr.evaluate(d, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
		    Node node = nodes.item(i);
		    description += node.getTextContent();
		}
	    } catch (Exception e) {
	    }
	    coDirectorySite.setDescription(description);
	}
	return courseListByResponsibleMap.get(responsible);
    }

}
