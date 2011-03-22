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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.util.StringUtil;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;
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

    private List<String> existingCO = null;

    private List<String> newCO = null;

    private Set<Section> sections = null;

    // ***************** SPRING INJECTION ************************//
    private OsylManagerService osylManagerService;

    public void setOsylManagerService(OsylManagerService osylManagerService) {
	this.osylManagerService = osylManagerService;
    }

    /**
     * Course management service integration.
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

    private AuthzGroupService authzGroupService;

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    private EventTrackingService eventTrackingService;

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }

    private UsageSessionService usageSessionService;

    public void setUsageSessionService(UsageSessionService usageSessionService) {
	this.usageSessionService = usageSessionService;
    }

    private SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    private EmailService emailService;

    public void setEmailService(EmailService emailService) {
	this.emailService = emailService;
    }

    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    private UserDirectoryService userDirectoryService;

    public void setUserDirectoryService(
	    UserDirectoryService userDirectoryService) {
	this.userDirectoryService = userDirectoryService;
    }

    // ***************** END SPRING INJECTION ************************//

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();
	courseOffs = new HashSet<CourseOffering>();
	Date startDateInterval = adminConfigService.getStartDate();
	Date endDateInterval = adminConfigService.getEndDate();
	// If we have a list of courses, we will create only those one. If we
	// don't we create
	// any course defined in the given sessions
	List<String> courses = adminConfigService.getCourses();
	List<String> programs = adminConfigService.getPrograms();
	List<String> servEns = adminConfigService.getServEns();

	// If we have an interval of time, we take the courses defined in that
	// period of time
	// If we don't have any interval, we take the courses defined in the
	// active sessions.
	List<AcademicSession> allSessions =
		getSessions(startDateInterval, endDateInterval);

	// Associated to canonical courses
	if (courses != null && courses.size() > 0) {
	    String coId = null;
	    for (String courseId : courses) {
		for (AcademicSession session : allSessions) {
		    coId = courseId + session.getEid();
		    if (cmService.isCourseOfferingDefined(coId))
			courseOffs.add(cmService.getCourseOffering(coId));
		}

	    }
	}
	// Associated to Acad_Career
	else if (programs != null && programs.size() > 0) {
	    Set<CourseOffering> courseOfferings = null;
	    for (String program : programs) {
		for (AcademicSession session : allSessions) {
		    courseOfferings =
			    cmService.findCourseOfferingsByAcadCareer(program,
				    session.getEid());
		    if (courseOfferings != null)
			courseOffs.addAll(courseOfferings);
		}

	    }

	}
	// Associated to the category
	else if (servEns != null && servEns.size() > 0) {
	    Set<Section> allSections = new HashSet<Section>();
	    String coffId = null;
	    for (String serviceEnseignement : servEns) {
		for (AcademicSession session : allSessions) {
		    allSections =
			    cmService.findSectionsByCategory(
				    serviceEnseignement, session.getEid());
		    if (allSections != null) {
			for (Section section : allSections) {
			    coffId = section.getCourseOfferingEid();
			    if (coffId != null)
				courseOffs.add(cmService
					.getCourseOffering(coffId));
			}

		    }
		}
	    }
	} else {
	    // Retrieve all the course sets
	    allCourseSets = cmService.getCourseSets();
	    Set<CourseOffering> fcourseOff = null;
	    for (CourseSet courseSet : allCourseSets) {

		for (AcademicSession session : allSessions) {
		    fcourseOff =
			    cmService.findCourseOfferings(courseSet.getEid(),
				    session.getEid());
		    courseOffs.addAll(fcourseOff);
		}

	    }
	}

	int compteur = 0;

	if (courseOffs != null) {
	    for (CourseOffering courseOff : courseOffs) {

		// Retrieve the sections to be created
		sections = cmService.getSections(courseOff.getEid());

		compteur += sections.size();
		// Create sharable site if necessary
		String shareableName = null;
		if (hasSharable(sections)) {
		    shareableName = createShareable(courseOff);
		    compteur++;
		}
		if (sections != null) {
		    String sectionId = null;
		    String sharableSectionId =
			    courseOff.getEid() + OsylCMJob.SHARABLE_SECTION;
		    List<String> dfSections = new ArrayList<String>();
		    String firstSection = null;
		    // create 'normal section'
		    for (Section section : sections) {
			sectionId = section.getEid();

			// We do not create sites associated to DF
			if (!isDfSection(sectionId)) {

			    // We do not create site associated to section 00
			    if (!sectionId.equalsIgnoreCase(sharableSectionId)) {
				createSite(section);

				String siteName = getSiteName(section);
				if (shareableName != null) {
				    associate(siteName, shareableName);
				    log.info("Associating " + shareableName
					    + " to " + siteName);
				}
				if (firstSection == null) {
				    firstSection = siteName;
				} else {
				    if (siteName.compareTo(firstSection) < 0)
					firstSection = siteName;
				}
			    }
			} else {
			    // we create site DF only if there is
			    // students
			    Set<Enrollment> enrollments =
				    cmService.getEnrollments(section
					    .getEnrollmentSet().getEid());
			    if (enrollments.size() > 0) {
				createSite(section);
				dfSections.add(getSiteName(section));
			    }
			}
		    }

		    // Removed because secretary will do it
		    // associate df with 1st 'normal' course
		    // for (String dfSite : dfSections) {
		    // associate(dfSite, firstSection);
		    // }
		}
	    }

	    log.info(compteur
		    + " sites has been processed (not necessarily created)");

	}
	logoutFromSakai();
    } // execute

    private void osyl(String child, String parent) {
	try {
	    osylSiteService.associate(child, parent);
	} catch (Exception e) {
	    log.error("Could not associate site " + child + " with site "
		    + parent, e);
	}
    }

    private boolean createSite(Section section) {
	String siteName = getSiteName(section);
	String lang;
	if (section.getLang() == null)
	    lang = TEMPORARY_LANG;
	else
	    lang = section.getLang();
	// 2 try/catch distinct to make the difference between
	// a creation problem and an association problem
	try {
	    if (!osylSiteService.siteExists(siteName)) {
		osylSiteService.createSite(siteName, OSYL_CO_CONFIG, lang);
	    }

	} catch (Exception e) {
	    log.error("Could not create site " + siteName, e);
	}
	try {
	    if (!osylSiteService.siteExists(siteName)) {
		osylManagerService.associateToCM(section.getEid(), siteName);
		Site site = siteService.getSite(siteName);
		siteService.save(site);
		log.info("The site " + siteName
			+ " has been created and associated to the section "
			+ section.getEid() + " in the Course Management");
	    }
	    return true;
	} catch (Exception e) {
	    log.error("Could not associate site " + siteName + " with CM", e);
	}
	log.debug("The site " + siteName + " has not been created");
	return false;
    }

    private boolean hasSharable(Set<Section> sections) {
	int nbSections = 0;
	String sectionId = null;

	for (Section section : sections) {
	    sectionId = section.getEid();
	    if (!isDfSection(sectionId))
		nbSections++;
	}

	if (nbSections > 1)
	    return true;
	else
	    return false;
    }

    
    
    private boolean isDfSection(String sectionId) {
	return sectionId.matches(".*[Dd][Ff][1-9]");
    }

    private void associate(String child, String parent) {
	try {
	    osylSiteService.associate(child, parent);
	} catch (Exception e) {
	    log.error("Could not associate site " + child + " with site "
		    + parent, e);
	}
    }

    private String createShareable(CourseOffering course) {
	String siteName = getSharableSiteName(course);
	String sharableSectionId = course.getEid() + OsylCMJob.SHARABLE_SECTION;
	Section sharableSection = null;
	String lang = null;

	log.info("Creating sharable site for " + siteName);

	if (!cmService.isSectionDefined(sharableSectionId)) {
	    log.info("There is no special section (" + sharableSectionId
		    + ") for this sharable site.");
	    lang = TEMPORARY_LANG;
	} else {
	    sharableSection = cmService.getSection(sharableSectionId);
	    lang = sharableSection.getLang();
	}

	try {
	    if (!osylSiteService.siteExists(siteName)) {
		osylSiteService.createSharableSite(siteName, OSYL_CO_CONFIG,
			lang);
	    } else {
		log.info("The site " + siteName + " already exist.");
	    }

	    Site sharable = siteService.getSite(siteName);

	    if (sharableSection != null)
		try {

		    osylManagerService.associateToCM(sharableSection.getEid(),
			    siteName);
		} catch (Exception e) {
		    log.error("Could not associate site " + siteName
			    + " with CM", e);
		}

	    siteService.save(sharable);

	    // After we are sure the sharable course outline has been created,
	    // We check if we have to send a message telling there is already a
	    // course section that might need to be transferred

	    // FIXME: SAKAI-1550
	    existingCourseOutlineSections(course);
	    if (existingCO != null) {

		Vector<User> receivers = new Vector<User>();
		// Add the users that will receive the mail
		receivers.add(userDirectoryService.getUserByEid("admin"));

		String message = getNotificationMessages(existingCO.toString());
		if (receivers.size() > 0)
		    emailService.sendToUsers(
			    receivers,
			    getHeaders(null, "test d'envoi",
				    receivers.toString()), message);
	    }

	} catch (Exception e) {
	    log.error(e.getMessage());
	    return null;
	}
	return siteName;
    }

    protected List<String> getHeaders(String receiverEmail, String subject,
	    String from) {
	List<String> rv = new Vector<String>();

	rv.add("MIME-Version: 1.0");
	rv.add("Content-Type: multipart/alternative; boundary=\"======sakai-multi-part-boundary======\"");
	// set the subject
	rv.add(subject);

	// from
	rv.add(from);

	// to
	if (StringUtil.trimToNull(receiverEmail) != null) {
	    rv.add("To: " + receiverEmail);
	}

	return rv;
    }

    // FIXME: SAKAI:1550
    private void existingCourseOutlineSections(CourseOffering courseOff) {

	Set<Section> sections = cmService.getSections(courseOff.getEid());

	existingCO = null;
	newCO = null;

	String siteSectionId;

	for (Section section : sections) {
	    siteSectionId = getSiteName(section);
	    if (siteService.siteExists(siteSectionId)) {
		if (existingCO == null) {
		    existingCO = new ArrayList<String>();
		}
		existingCO.add(siteSectionId);
	    } else {
		if (newCO == null) {
		    newCO = new ArrayList<String>();
		}
		newCO.add(siteSectionId);
	    }
	}
    }

    // FIXME: SAKAI:1550
    private String getNotificationMessages(String existingSections) {
	StringBuilder message = new StringBuilder();
	String plainTextContent = " le contenu en texte";
	String htmlContent = " le contenu en html";
	String subject = "sujet du message " + existingSections;

	message.append("This message is for MIME-compliant mail readers.");
	message.append("\n\n--======sakai-multi-part-boundary======\n");
	message.append("Content-Type: text/plain\n\n");
	message.append(plainTextContent);
	message.append("\n\n--======sakai-multi-part-boundary======\n");
	message.append("Content-Type: text/html\n\n");
	message.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n");
	message.append("    \"http://www.w3.org/TR/html4/loose.dtd\">\n");
	message.append("<html>\n");
	message.append("  <head><title>");
	message.append(subject);
	message.append("</title></head>\n");
	message.append("  <body>\n");
	message.append(htmlContent);
	message.append("\n  </body>\n</html>\n");
	message.append("\n\n--======sakai-multi-part-boundary======--\n\n");

	return message.toString();
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
	    if (canCourseId.endsWith("A") || canCourseId.endsWith("E")) {
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
	    if (canCourseId.endsWith("A") || canCourseId.endsWith("E")) {
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

	if (periode == null)
	    siteName = courseId + "." + sessionTitle;
	else
	    siteName = courseId + "." + sessionTitle + "." + periode;

	return siteName;
    }

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1",
		"OfficialSitesSync");

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
	usageSessionService.logout();
    }

}
