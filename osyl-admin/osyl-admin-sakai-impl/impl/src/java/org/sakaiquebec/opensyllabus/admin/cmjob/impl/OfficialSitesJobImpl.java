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
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OfficialSitesJobImpl implements OfficialSitesJob {

    private static Log log = LogFactory.getLog(OfficialSitesJobImpl.class);

    private final static String TEMPORARY_LANG = "fr_CA";

    private final static String OSYL_CO_CONFIG = "default";

    private Set<CourseSet> allCourseSets = null;

    private CourseSet aCourseSet = null;

    private Set<CourseOffering> courseOffs = null;

    private Set<CanonicalCourse> canonicalCourses =
	    new HashSet<CanonicalCourse>();;

    private Set<Section> sections = null;
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

    /**
     * The site service used to create new sites: Spring injection
     */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code> needed to create a new site in Sakai.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    private ToolManager toolManager;

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
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
		int i = 0;
		for (CourseOffering courseOff : courseOffs) {

		    // Check if we have the good session
		    for (AcademicSession academicSession : allSessions) {
			if ((courseOff.getAcademicSession().getEid())
				.equals(academicSession.getEid())) {

			    // Retrieve the sections to be created
			    sections =
				    cmService.getSections(courseOff.getEid());

			    if (sections != null) {
				for (Section section : sections) {
				    siteName = getSiteName(section);
				    try {
					System.out.println("le site cree est "
						+ osylSiteService.createSite(
							siteName,
							OSYL_CO_CONFIG,
							TEMPORARY_LANG));
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
	boolean create = false;

	return create;
    }

    private void createShareable(CourseOffering course) {

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
	sessionTitle = session.getTitle();

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

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	Session sakaiSession = SessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	UsageSessionService.startSession("admin", "127.0.0.1", "CMSync");

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
