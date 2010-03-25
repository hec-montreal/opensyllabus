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
package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OfficialSitesJobImpl implements OfficialSitesJob {

    private static Log log = LogFactory.getLog(OfficialSitesJobImpl.class);

    private Set<CourseSet> allCourseSets = null;


    private Set<CourseOffering> courseOffs = null;

    private Set<CanonicalCourse> canonicalCourses =
	    new HashSet<CanonicalCourse>();;

    private Set<Section> sections = null;

    private OsylManagerService osylManagerService;

    public void setOsylManagerService(OsylManagerService osylManagerService) {
	this.osylManagerService = osylManagerService;
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
     * Integration of the OsylSiteService
     */
    private OsylSiteService osylSiteService;

    /**
     * @param osylSiteService
     */
    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    /**
     * Administration ConfigurationService injection
     */
    private ConfigurationService adminConfigService;

    /**
     * @param adminConfigService
     */
    public void setAdminConfigService(ConfigurationService adminConfigService) {
	this.adminConfigService = adminConfigService;
    }

 

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();
	Date startDateInterval = adminConfigService.getIntervalStartDate();
	Date endDateInterval = adminConfigService.getIntervalEndDate();

	// If we have a list of courses, we will create only those one. If we
	// don't we create
	// any course defined in the given sessions
	List<String> courses = adminConfigService.getCourses();

	// If we have an interval of time, we take the courses defined in that
	// period of time
	// If we don't have any interval, we take the courses defined in the
	// active sessions.
	List<AcademicSession> allSessions =
		getSessions(startDateInterval, endDateInterval);

	String siteName = null;

	if (courses != null && courses.size() > 0) {
	    for (String courseId : courses) {
		if (cmService.isCanonicalCourseDefined(courseId))
		    canonicalCourses
			    .add(cmService.getCanonicalCourse(courseId));
	    }
	} else {
	    // Retrieve all the course sets
	    allCourseSets = cmService.getCourseSets();

	    for (CourseSet courseSet : allCourseSets) {

		// Retrieve the canonical courses
		canonicalCourses =
			cmService.getCanonicalCourses(courseSet.getEid());
	    }
	}

	String canCourseId = null;

	for (CanonicalCourse canCourse : canonicalCourses) {
	    canCourseId = canCourse.getEid();

	    // Check if it is in the list of courses to be created
	    // If the list is not empty and the course is not inside, we
	    // don't create it
	    if (courses != null)
		if (!courses.contains(canCourseId.trim()))
		    continue;

	    // Retrieve the course offerings
	    courseOffs =
		    cmService.getCourseOfferingsInCanonicalCourse(canCourseId);

	    if (courseOffs != null) {
		for (CourseOffering courseOff : courseOffs) {

		    // Check if we have the good session
		    for (AcademicSession academicSession : allSessions) {
			if ((courseOff.getAcademicSession().getEid())
				.equals(academicSession.getEid())) {

			    // Retrieve the sections to be created
			    sections =
				    cmService.getSections(courseOff.getEid());

			    // Create sharable site if necessary
			    if (hasSharable(sections))
				createShareable(courseOff);

			    // Create site for section
			    if (sections != null) {
				for (Section section : sections) {
				    siteName = getSiteName(section);
				    try {
					System.out.println("le site cree est "
						+ osylSiteService.createSite(
							siteName,
							OSYL_CO_CONFIG,
							section.getLang()));
					osylManagerService.associateToCM(
						section.getEid(), siteName);
				    } catch (Exception e) {
					log.debug(e.getMessage());
				    }

				}
			    }

			}
		    }
		}
	    }
	}

	logoutFromSakai();
    }

    private boolean hasSharable(Set<Section> sections) {
	int nbSections = 0;
	String sectionId = null;

	for (Section section : sections) {
	    sectionId = section.getEid();
	    if (!sectionId.endsWith(DIFFERED_GROUP))
		nbSections++;
	}

	if (nbSections > 1)
	    return true;
	else
	    return false;
    }

    private void createShareable(CourseOffering course) {
	String siteName = getSharableSiteName(course);
	Set<Membership> sectionMembers = new HashSet<Membership>();
	
	//Retrieve the members of the course sections associated to
	//this course offering
	Set<Section> sections = cmService.getSections(course.getEid());
	Set<Membership> members = null;
	
	for (Section section: sections){
	    members = cmService.getSectionMemberships(section.getEid());
	    sectionMembers.addAll(members);
	}
	
	try {
	    osylSiteService.createSharableSite(siteName, OSYL_CO_CONFIG,
		    TEMPORARY_LANG);
	    //TODO: add users to sites
	    Site sharable = osylSiteService.getSite(siteName);
	    
	    for (Membership member: sectionMembers){
		sharable.addMember(member.getUserId(), "Instructor", true, false);
	    }
	    
	    SiteService.save(sharable);
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
    }

    private List<AcademicSession> getSessions(Date startDate, Date endDate) {
	List<AcademicSession> sessions = new ArrayList<AcademicSession>();

	if (startDate == null || endDate == null)
	    sessions.addAll(cmService.getCurrentAcademicSessions());
	else {
	    // AcademicSession session = null;
	    List<AcademicSession> allSessions = cmService.getAcademicSessions();
	    Date sessionStartDate, sessionEndDate;
	    for (AcademicSession session : allSessions) {
		sessionStartDate = session.getStartDate();
		sessionEndDate = session.getEndDate();
		if (startDate.compareTo(sessionStartDate) <= 0
			&& endDate.compareTo(sessionEndDate) >= 0)
		    sessions.add(session);
	    }
	}

	return sessions;
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

    private String getSiteName(Section section) {
	String siteName = null;
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

	if (periode == null)
	    siteName = courseId + "_" + groupe + "_" + sessionTitle;
	else
	    siteName =
		    courseId + "_" + groupe + "_" + sessionTitle + "_"
			    + periode;

	return siteName;
    }

    private String getSharableSiteName(CourseOffering courseOff) {
	String siteName = null;
	String canCourseId = (courseOff.getCanonicalCourseEid()).trim();
	AcademicSession session = courseOff.getAcademicSession();
	String sessionId = session.getEid();

	String courseId = null;
	String courseIdFront = null;
	String courseIdMiddle = null;
	String courseIdBack = null;

	String sessionTitle = null;
	String periode = null;

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

	if (periode == null)
	    siteName = courseId + "_" + sessionTitle;
	else
	    siteName = courseId + "_" + sessionTitle + "_" + periode;

	return siteName;
    }

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	Session sakaiSession = SessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	UsageSessionService.startSession("admin", "127.0.0.1",
		"OfficialSitesSync");

	// update the user's externally provided realm definitions
	AuthzGroupService.refreshUser("admin");

	// post the login event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }

}
