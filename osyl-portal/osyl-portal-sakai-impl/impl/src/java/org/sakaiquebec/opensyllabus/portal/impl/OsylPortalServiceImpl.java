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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.portal.api.OsylPortalService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
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
   
    private List<CODirectorySite> courseListByFields;

    private List<String> currentSessions;
    
    private String webappDir = System.getProperty("catalina.home")
	    + File.separator + "webapps" + File.separator
	    + "osyl-portal-sakai-tool" + File.separator;// Ugly but don't know
							// how;
    private Timer timer;

    // SPRING INJECTIONS
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
    
    private OsylSecurityService osylSecurityService;
    
    public void setOsylSecurityService(OsylSecurityService osylSecurityService) {
	this.osylSecurityService = osylSecurityService;
    }

    // END SPRING INJECTIONS

    /**
     * Init method called right after Spring injection.
     */
    public void init() {
	timer = new Timer();
	TimerTask timerTask = new TimerTask() {

	    @Override
	    public void run() {
		//buildCoursesMaps();
	    }
	};
	timer.schedule(timerTask, 0, 86400000);
    }

    public void destroy() {
	log.info("DESTROY from OsylPortalService");
	timer.cancel();
	timer = null;
    }

    private void buildCoursesMaps() {
	log.info("Start building course maps");
	long start = System.currentTimeMillis();

	currentSessions = new ArrayList<String>();
	for (AcademicSession academicSession : courseManagementService
		.getCurrentAcademicSessions()) {
	    currentSessions.add(academicSession.getEid());
	}

	buildCoursesMapsFromCM();

	log.info("Finished building course maps in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    private void buildCoursesMapsFromCM() {
	Map<String, List<CODirectorySite>> temp_courseListByAcadCareerMap =
		new HashMap<String, List<CODirectorySite>>();

	Map<String, List<CODirectorySite>> temp_courseListByResponsibleMap =
		new HashMap<String, List<CODirectorySite>>();

	for (CourseSet courseSet : courseManagementService.getCourseSets()) {
	    for (CanonicalCourse canonicalCourse : courseManagementService
		    .getCanonicalCourses(courseSet.getEid())) {
		CODirectorySite coDirectorySite =
			createCODirectorySite(canonicalCourse);
		String acadCareer = coDirectorySite.getProgram();
		String responsible = coDirectorySite.getResponsible();

		if (acadCareer != null) {
		    List<CODirectorySite> list =
			    temp_courseListByAcadCareerMap.get(acadCareer);
		    if (list == null) {
			list = new ArrayList<CODirectorySite>();
		    }
		    list.add(coDirectorySite);
		    temp_courseListByAcadCareerMap.put(acadCareer, list);
		}
		if (responsible != null) {
		    List<CODirectorySite> list =
			    temp_courseListByResponsibleMap.get(responsible);
		    if (list == null) {
			list = new ArrayList<CODirectorySite>();
		    }
		    list.add(coDirectorySite);
		    temp_courseListByResponsibleMap.put(responsible, list);
		}
	    }
	}
	courseListByAcadCareerMap = temp_courseListByAcadCareerMap;	
	courseListByResponsibleMap = temp_courseListByResponsibleMap;
    }

    private void buildFilteredCoursesFromCM(String courseNumber,
	    String courseTitle, String instructor, String program,
	    String responsible, String trimester) {

	List<CODirectorySite> courseList = new ArrayList<CODirectorySite>();

	for (CourseSet courseSet : courseManagementService.getCourseSets()) {
	    for (CanonicalCourse canonicalCourse : courseManagementService
		    .getCanonicalCourses(courseSet.getEid())) {
		if (canonicalCourse.getEid() != null) {
		    CODirectorySite coSite =
			    createCODirectorySite(canonicalCourse);
		    if (coSite != null) {
			boolean accepted = false;
			if ((!isNull(program)
				&& !isNull(responsible)
				&& (program.equals("ALL") || isFoundField(
					program, coSite.getProgram())) && isFoundField(
				responsible, coSite.getResponsible()))) {
			    // 0 0 0 0
			    if (isNull(courseNumber) && isNull(courseTitle)
				    && isNull(instructor) && isNull(trimester)) {
				accepted = true;
				// 0 0 0 1
			    } else if (isNull(courseNumber)
				    && isNull(courseTitle)
				    && isNull(instructor) && !isNull(trimester)) {
				if (isFoundField(trimester, coSite
					.getArchivedSections(), coSite
					.getCurrentSections())) {
				    accepted = true;
				}
				// 0 0 1 0
			    } else if (isNull(courseNumber)
				    && isNull(courseTitle)
				    && !isNull(instructor) && isNull(trimester)) {
				if (isFoundField(instructor, coSite
					.getInstructor())) {
				    accepted = true;
				}
				// 0 0 1 1
			    } else if (isNull(courseNumber)
				    && isNull(courseTitle)
				    && !isNull(instructor)
				    && !isNull(trimester)) {
				if (isFoundField(instructor, coSite
					.getInstructor())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 0 1 0 0
			    } else if (isNull(courseNumber)
				    && !isNull(courseTitle)
				    && isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseTitle, coSite
					.getCourseName())) {
				    accepted = true;
				}
				// 0 1 0 1
			    } else if (isNull(courseNumber)
				    && !isNull(courseTitle)
				    && isNull(instructor) && !isNull(trimester)) {
				if (isFoundField(courseTitle, coSite
					.getCourseName())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 0 1 1 0
			    } else if (isNull(courseNumber)
				    && !isNull(courseTitle)
				    && !isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseTitle, coSite
					.getCourseName())
					&& isFoundField(instructor, coSite
						.getInstructor())) {
				    accepted = true;
				}
				// 0 1 1 1
			    } else if (isNull(courseNumber)
				    && !isNull(courseTitle)
				    && !isNull(instructor)
				    && !isNull(trimester)) {
				if (isFoundField(courseTitle, coSite
					.getCourseName())
					&& isFoundField(instructor, coSite
						.getInstructor())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 1 0 0 0
			    } else if (!isNull(courseNumber)
				    && isNull(courseTitle)
				    && isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())) {
				    accepted = true;
				}

				// 1 0 0 1
			    } else if (!isNull(courseNumber)
				    && isNull(courseTitle)
				    && isNull(instructor) && !isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 1 0 1 0
			    } else if (!isNull(courseNumber)
				    && isNull(courseTitle)
				    && !isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(instructor, coSite
						.getInstructor())) {
				    accepted = true;
				}
				// 1 0 1 1
			    } else if (!isNull(courseNumber)
				    && isNull(courseTitle)
				    && !isNull(instructor)
				    && !isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(instructor, coSite
						.getInstructor())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 1 1 0 0
			    } else if (!isNull(courseNumber)
				    && !isNull(courseTitle)
				    && isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(courseTitle, coSite
						.getCourseName())) {
				    accepted = true;
				}
				// 1 1 0 1
			    } else if (!isNull(courseNumber)
				    && !isNull(courseTitle)
				    && isNull(instructor) && !isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(courseTitle, coSite
						.getCourseName())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
				// 1 1 1 0
			    } else if (!isNull(courseNumber)
				    && !isNull(courseTitle)
				    && !isNull(instructor) && isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(courseTitle, coSite
						.getCourseName())
					&& isFoundField(instructor, coSite
						.getInstructor())) {
				    accepted = true;
				}
				// 1 1 1 1
			    } else if (!isNull(courseNumber)
				    && !isNull(courseTitle)
				    && !isNull(instructor)
				    && !isNull(trimester)) {
				if (isFoundField(courseNumber, coSite
					.getCourseNumber())
					&& isFoundField(courseTitle, coSite
						.getCourseName())
					&& isFoundField(instructor, coSite
						.getInstructor())
					&& isFoundField(trimester, coSite
						.getArchivedSections(), coSite
						.getCurrentSections())) {
				    accepted = true;
				}
			    }
			}
			if (accepted) {
			    List<String> allSessions = buildAllSections(coSite);
			    coSite.setAllSessions(allSessions);
			    courseList.add(coSite);
			}
		    }// coDirectorySite is null
		}
	    }// for
	}// for
	if (!courseList.isEmpty()) {
	    courseList.get(0).setSessionNamesList(
		    getRightSessions(courseList, trimester));
	}
	courseListByFields = courseList;
    }

    private List<String> buildAllSections(CODirectorySite coSite) {
	List<String> allSections = new ArrayList<String>();

	if (coSite.getArchivedSections() != null) {
	    List<String> archivedSections =
		    new ArrayList<String>(coSite.getArchivedSections().keySet());
	    for (String section1 : archivedSections) {
		if (!isNull(section1)) {
		    allSections.add(section1);
		}
	    }
	}

	if (coSite.getCurrentSections() != null) {
	    List<String> currentSections =
		    new ArrayList<String>(coSite.getCurrentSections().keySet());
	    for (String section2 : currentSections) {
		if (!isNull(section2)) {
		    allSections.add(section2);
		}
	    }
	}
	return allSections;
    }
    
    private List<String> getRightSessions(List<CODirectorySite> courses,
	    String trimester) {
	List<String> rigthSessions = new ArrayList<String>();
	List<String> sessions = new ArrayList<String>();

	if (courses != null) {
	    if (!isNull(trimester)) {
		for (final CODirectorySite coSite : courses) {
		    for (String s : coSite.getAllSessions()) {
			if (isSessionInSite(trimester, s)
				&& !rigthSessions.contains(trimester)) {
			    rigthSessions.add(trimester);
			    return rigthSessions;			    
			}
		    }
		}
	    } else {
		sessions = getAcademicNamesSessions();
		for (String session : sessions) {
		    for (final CODirectorySite coSite : courses) {
			for (String s : coSite.getAllSessions()) {
			    if (isSessionInSite(session, s)
				    && !rigthSessions.contains(session)) {
				rigthSessions.add(session);
			    }
			}
		    }
		}
	    }
	}
	return rigthSessions;
    }

    private boolean isSessionInSite(String element, String sessions) {
	if (!isNull(element) && !isNull(sessions)) {
	    if (sessions.matches("(?i)." + element + "*")
		    || sessions.toLowerCase().indexOf(element.toLowerCase()) >= 0
		    || element.equals(sessions)) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}

    }

    private boolean isFoundField(String field, Map<String, String> achivedSections, Map<String, String> currentSections) {
	if (!isNull(field)) {
	    if (isFieldInCollection(field,achivedSections) || isFieldInCollection(field, currentSections)) {
		return true;
	    } else {
		return false;
	    }
	}
	return true;
    }  
    
    private boolean isFieldInCollection (String field, Map<String, String> map) {
	for (Iterator<Entry<String, String>> sortedSIterator =
	    map.entrySet().iterator(); sortedSIterator.hasNext();) {
	    Entry<String, String> entry =  sortedSIterator.next();
	    if (entry.getKey().matches("(?i)." + field+"*") || entry.getKey().toLowerCase().indexOf(field.toLowerCase()) > 0){
		return true;
	    } 
	}
	return false;
    }
   
    private boolean isFoundField(String field, String attribut) {
	if (!isNull(field) && !isNull(attribut)) {
	    if (attribut.toLowerCase().contains(field.toLowerCase())) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    private boolean isNull(String field) {
	if (field!=null && !field.equals("")) {
	    return false;
	} else {
	    return true;
	}
    }
    
    public CODirectorySite getCODirectorySite(String siteId) {
	CODirectorySite coDirectorySite =
		createCODirectorySite(courseManagementService
			.getCanonicalCourse(siteId.replace("-", "")));
	coDirectorySite.setDescription(getDescription(siteId));
	return coDirectorySite;
    }

    private CODirectorySite createCODirectorySite(
	    CanonicalCourse canonicalCourse) {
	CODirectorySite coDirectorySite = new CODirectorySite();
	coDirectorySite.setCourseNumber(getDirectorySiteName(canonicalCourse
		.getEid()));
	coDirectorySite.setCourseName(canonicalCourse.getTitle());
	for (CourseOffering courseOffering : courseManagementService
		.getCourseOfferingsInCanonicalCourse(canonicalCourse.getEid())) {

	    if (coDirectorySite.getProgram() == null) {
		coDirectorySite.setProgram(courseOffering.getAcademicCareer());
		coDirectorySite.setCredits(courseOffering.getCredits());
		coDirectorySite.setRequirements(courseOffering
			.getRequirements());
		coDirectorySite.setProgram(courseOffering.getAcademicCareer());
	    }
	    if (currentSessions != null
		    && currentSessions.contains(courseOffering
			    .getAcademicSession().getEid())) {
		fillCurrentSectionsForCODirectorySite(coDirectorySite,
			courseOffering);
	    } else {
		fillArchivedSectionsForCODirectorySite(coDirectorySite,
			courseOffering);
	    }
	}
	return coDirectorySite;
    }

    private void fillCurrentSectionsForCODirectorySite(
	    CODirectorySite coDirectorySite, CourseOffering courseOffering) {
	Set<Section> sections =
		courseManagementService.getSections(courseOffering.getEid());
	for (Section section : sections) {
	    if (!section.getEid().endsWith("00")) {
		Set<String> instructors =
			section.getEnrollmentSet().getOfficialInstructors();
		String userName = "";
		if (!instructors.isEmpty()) {
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
		}
		coDirectorySite.putCurrentSection(getSectionName(section), userName);
		String trimesters = coDirectorySite.getSearchedTrimesters();
		if (trimesters != null) {
		    if (trimesters.indexOf(searchedTrimesters(section)) > 0){
			trimesters = trimesters + "," + searchedTrimesters(section);    
		    }
		} else {
		    trimesters = searchedTrimesters(section);
		}
		coDirectorySite.setSearchedTrimesters(trimesters);
	    }
	    
	    if (section.getCategory() != null
		    && !section.getCategory().equals("")) {
		coDirectorySite.setResponsible(section.getCategory());
	    }
	}
    }
    
    private void fillArchivedSectionsForCODirectorySite(
	    CODirectorySite coDirectorySite, CourseOffering courseOffering) {
	Set<Section> sections =
		courseManagementService.getSections(courseOffering.getEid());
	for (Section section : sections) {
	    if (!section.getEid().endsWith("00")) {
		Set<String> instructors =
			section.getEnrollmentSet().getOfficialInstructors();
		String userName = "";
		if (!instructors.isEmpty()) {
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
		}
		coDirectorySite.putArchivedSection(getSectionName(section), userName);
		String trimesters = coDirectorySite.getSearchedTrimesters();
		if (trimesters != null) {
		    if (trimesters.indexOf(searchedTrimesters(section)) > 0){
			trimesters = trimesters + "," + searchedTrimesters(section);    
		    }
		} else {
		    trimesters = searchedTrimesters(section);
		}
		coDirectorySite.setSearchedTrimesters(trimesters);
	    }
	    if (section.getCategory() != null
		    && !section.getCategory().equals("")) {
		coDirectorySite.setResponsible(section.getCategory());
	    }
	}
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
    
    
    private String searchedTrimesters(Section section) {
	String sessionTitle = null;
	String courseOffId = section.getCourseOfferingEid();
	CourseOffering courseOff =
		courseManagementService.getCourseOffering(courseOffId);
	AcademicSession session = courseOff.getAcademicSession();
	sessionTitle = getSessionName(session);
	return sessionTitle;
    }

    private String getSectionName(Section section) {
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


    public List<CODirectorySite> getCoursesForAcadCareer(String acadCareer) {
	return courseListByAcadCareerMap.get(acadCareer);
    }

    public List<CODirectorySite> getCoursesForResponsible(String responsible) {
	return courseListByResponsibleMap.get(responsible);
    }

    public List<CODirectorySite> getCoursesForFields(String courseNumber,
	    String courseTitle, String instructor, String program,
	    String responsible, String trimester) {

	buildFilteredCoursesFromCM(courseNumber, courseTitle, instructor, program, responsible, trimester);
	
	return  courseListByFields;
    }
    
	    
    public String getDescription(String siteId) {
	String description = "";
	try {
	    COSerialized coSerialized =
		    osylPublishService
			    .getSerializedPublishedCourseOutlineForAccessType(
				    siteId, SecurityInterface.ACCESS_PUBLIC,
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
	return description;
    }

    public List<String> getAcademicNamesSessions() {
	List<AcademicSession> acadSessionsList =
		courseManagementService.getAcademicSessions();
	List<String> sessionsList = new ArrayList<String>();
	for (AcademicSession acadSession : acadSessionsList) {
	    CMAcademicSession cmAcadSession = new CMAcademicSession();
	    cmAcadSession.setId(acadSession.getEid());
	    cmAcadSession.setTitle(acadSession.getTitle());
	    cmAcadSession.setDescription(acadSession.getDescription());
	    cmAcadSession.setStartDate(acadSession.getStartDate());
	    cmAcadSession.setEndDate(acadSession.getEndDate());
	    sessionsList.add(cmAcadSession.getSessionName());
	}
	Collections.sort(sessionsList);
	return sessionsList;
    }     

}
