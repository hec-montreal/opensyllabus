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
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.email.api.ContentType;
import org.sakaiproject.email.api.EmailAddress;
import org.sakaiproject.email.api.EmailAddress.RecipientType;
import org.sakaiproject.email.api.EmailMessage;
import org.sakaiproject.site.api.Site;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;

import ca.hec.commons.utils.FormatUtils;

import org.sakaiproject.component.cover.ServerConfigurationService;

import static org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob.WINTER;


/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OfficialSitesJobImpl extends OsylAbstractQuartzJobImpl implements
	OfficialSitesJob {

    private Set<CourseSet> allCourseSets = null;

    private Set<CourseOffering> courseOffs = null;

    private Set<Section> sections = null;

    private static Log log = LogFactory.getLog(OfficialSitesJobImpl.class);

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
	List<String> piloteE2017 = adminConfigService.getPiloteE2017();
	List<String> piloteA2017 = adminConfigService.getPiloteA2017();

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
			    cmService.findCourseOfferingsByAcadCareerAndAcademicSession(program,
				    session.getEid());
		    if (courseOfferings != null && courseOfferings.size() > 0)
			courseOffs.addAll(courseOfferings);
		    courseOfferings = new HashSet<CourseOffering>();
		}

	    }

	}
	// Associated to the category
	else if (servEns != null && servEns.size() > 0) {
	    Set<Section> allSections = new HashSet<Section>();
	    String coffId = null;
	    CourseOffering coff = null;
	    for (String serviceEnseignement : servEns) {
		allSections =
			cmService.findSectionsByCategory(serviceEnseignement);
		if (allSections != null) {
		    for (AcademicSession session : allSessions) {

			for (Section section : allSections) {
			    coffId = section.getCourseOfferingEid();
			    coff = cmService.getCourseOffering(coffId);
			    if (coffId != null
				    && coff.getAcademicSession().getEid()
					    .equals(session.getEid()))
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

	log.info(courseOffs.size() + " courses will be treated.");
	int compteur = 0;

	if (courseOffs != null) {
	    for (CourseOffering courseOff : courseOffs) {

	    	//Continue if course is to be in E2017 pilote or A2017 pilote
			if (adminConfigService.inE2017Pilote(courseOff.getEid(), piloteE2017) ||
					adminConfigService.inA2017Pilote(courseOff.getEid(), piloteA2017))
				continue;


		// Retrieve the sections to be created
		sections = cmService.getSections(courseOff.getEid());

		compteur += sections.size();
		// Create sharable site if necessary
		String sharableName = null;
		boolean createSharable = false;
		if (hasSharable(sections)) {
		    boolean sharableExist = false;
		    try {
			sharableExist =
				osylSiteService
					.siteExists(getSharableSiteName(courseOff));
		    } catch (Exception e) {
		    }
		    if (!sharableExist) {
			sharableName = createShareable(courseOff);
			compteur++;
			createSharable = true;
		    }
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
				boolean siteExist = false;
				try {
				    siteExist =
					    osylSiteService
						    .siteExists(getSiteName(section));
				} catch (Exception e) {
				}
				if (!siteExist) {
				    createSite(section);
				    String siteName = getSiteName(section);
				    if (sharableName != null) {
					associate(siteName, sharableName);
					log.info("Associating " + sharableName
						+ " to " + siteName);
				    }
				    if (firstSection == null) {
					firstSection = siteName;
				    } else {
					if (siteName.compareTo(firstSection) < 0)
					    firstSection = siteName;
				    }
				} else {
				    if (createSharable) {
					// send mail
					try {
					    List<EmailAddress> toRecipients =
						    new ArrayList<EmailAddress>();
					    for (String eid : section
						    .getEnrollmentSet()
						    .getOfficialInstructors()) {
						toRecipients
							.add(new EmailAddress(
								userDirectoryService
									.getUserByEid(
										eid)
									.getEmail()));
					    }
					    EmailMessage message =
						    new EmailMessage();
					    message.setSubject("Création du partageable "
						    + sharableName);
					    message.setContentType(ContentType.TEXT_HTML);
					    message.setBody("Bonjour,<br>suite à l'ajout d'une section au cours "
						    + sharableName
						    + " un partageable a été crée et le cours "
						    + getSiteName(section)
						    + " automatiquement rattaché à celui-ci.<br>Si vous ne souhaitez pas utiliser le contenu du partageable, vous pouvez vous détacher de celui-ci à l'aide du gestionnaire de plan de cours");
					    message.setFrom(getZoneCours2EMail());
					    List<EmailAddress> ccRecipients =
						    new ArrayList<EmailAddress>();
					    ccRecipients.add(new EmailAddress(
						    getZoneCours2EMail()));
					    message.setRecipients(
						    RecipientType.CC,
						    ccRecipients);
					    message.setRecipients(
						    RecipientType.TO,
						    toRecipients);
					    emailService.send(message);
					} catch (Exception e) {
					    log.error("Could not send email to shareable owners:"
						    + e);
					    e.printStackTrace();
					}

				    }

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

    private boolean createSite(Section section) {
	String siteName = getSiteName(section);
	String lang;
	boolean justCreated = false;
	if (section.getLang() == null)
	    lang = TEMPORARY_LANG;
	else
	    lang = section.getLang();
	// 2 try/catch distinct to make the difference between
	// a creation problem and an association problem
	try {
	    if (!osylSiteService.siteExists(siteName)) {
		osylSiteService.createSite(siteName, OSYL_CO_CONFIG, lang);
		justCreated = true;
	    }

	} catch (Exception e) {
	    log.error("Could not create site " + siteName, e);
	}
	try {
	    if (justCreated) {
		osylManagerService.associateToCM(section.getEid(), siteName);
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
	    if (!isDfSection(sectionId)
		    && !sectionId.endsWith(OsylCMJob.SHARABLE_SECTION))
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
	boolean justCreated = false;

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
		justCreated = true;
	    } else {
		log.info("The site " + siteName + " already exist.");
	    }

	    if (justCreated)
		try {
		    osylManagerService.associateToCM(sharableSection.getEid(),
			    siteName);
		} catch (Exception e) {
		    log.error("Could not associate site " + siteName
			    + " with CM", e);
		}
	} catch (Exception e) {
	    log.error(e.getMessage());
	    return null;
	}
	return siteName;
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
	
	String courseId = FormatUtils.formatCourseId(canCourseId);
	String sessionTitle = getSessionName(session);
	String periode = null;
	String groupe = null;

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

	String courseId = FormatUtils.formatCourseId(canCourseId);
	String sessionTitle = getSessionName(session);
	String periode = null;

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
	super.loginToSakai("OfficialSitesJob");
    }

    private String getZoneCours2EMail(){
	return ServerConfigurationService.getString("mail.zc2");
    }

}
