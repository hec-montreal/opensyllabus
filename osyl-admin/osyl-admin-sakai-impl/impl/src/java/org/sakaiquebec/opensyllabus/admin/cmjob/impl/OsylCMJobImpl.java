package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicCareer;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CourseEventSynchroJob;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.EtudiantCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.EtudiantCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailSessionsMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericEtudiantCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericExamensMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericProfCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericProgrammeEtudesMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericRequirementsCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericServiceEnseignementMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProgrammeEtudesMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProgrammeEtudesMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.RequirementsCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.RequirementsCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ServiceEnseignementMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ServiceEnseignementMapEntry;

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

/**
 * The class is an implementation of the OsylCMJob as job launched by the quartz
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */

public class OsylCMJobImpl extends OsylAbstractQuartzJobImpl implements
OsylCMJob {

	String webappDir = null;

	/**
	 * Map of instructor/coordinator information (from extract) as a list
	 * associated to each cours (key being catalogNbr + strm + session_code + classSection)
	 */
	private Map<String, ProfCoursMapEntry> profCoursMap = null;

	/**
	 * Map used to store information about the courses taken by a student:
	 * student id, course id, course session, course period
	 */
	private EtudiantCoursMap etudCoursMap = null;

	/**
	 * Map used to store information about course details : course id,
	 * description, language, session, group, period, department
	 */
	private DetailCoursMap detailCoursMap = null;

	/**
	 * Map used to store information about the department which will represent
	 * the course set
	 */
	private ServiceEnseignementMap seMap = null;
	/**
	 * Map used to store information about sessions: session id, starting date,
	 * ending date
	 */
	private DetailSessionsMap detailSessionMap = null;

	/**
	 * Map used to store information about the departments.
	 */
	private ServiceEnseignementMap servEnsMap = null;

	/**
	 * Map used to store information about the programs
	 */
	private ProgrammeEtudesMap programmeEtudesMap = null;

	/**
	 * Map used to store information about the exam dates
	 */
	private GenericExamensMapFactory examenMap = null;

	private RequirementsCoursMap requirementsCoursMap;

	/**
	 * Students currently registered as enrolled in the system
	 */
	private Map<String, Enrollment> actualStudents;

	/**
	 * Classes sections actually registered in the system
	 */
	private Set<Section> actualCoursesSection;

	private Date actualStartDate = null;

	private Date actualEndIntervalDate = null;

	/**
	 * Course event synchro job (injected by spring)
	 */
	private CourseEventSynchroJob courseEventSynchroJob;

	/**
	 * Our logger
	 */
	private static Log log = LogFactory.getLog(OsylCMJobImpl.class);

	public void setCourseEventSynchroJob(
			CourseEventSynchroJob courseEventSynchroJob) {
		this.courseEventSynchroJob = courseEventSynchroJob;
	}

	/* load the instructors and assigns them to their courses */
	private void assignTeachers() {

		// tenir compte des coordonnateurs actuel dans les sites pour savoir lesquels aller supprimer
		Map<String, Set<String>> actualCoordinators = new HashMap<String, Set<String>>();
		Set<String> addedCoordinators = new HashSet<String>();

		for (String enrollmentSetId : profCoursMap.keySet()) {

			ProfCoursMapEntry profCoursEntry = profCoursMap.get(enrollmentSetId);

			try {
				Section section = cmService.getSection(enrollmentSetId);
				CourseOffering offering = cmService.getCourseOffering(section.getCourseOfferingEid());
				EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(enrollmentSetId);

				// add official instructors
				enrollmentSet.setOfficialInstructors(profCoursEntry.getInstructors());
				cmAdmin.updateEnrollmentSet(enrollmentSet);

				log.info("Set instructors for " + enrollmentSetId + ": " + enrollmentSet.getOfficialInstructors());

				// retrieve coordinators for current course offering or section (if we haven't yet)
				Set<String> coordinatorSet = null;
				Set<Membership> memberships = null;
				try {
					if ("CERT".equals(offering.getAcademicCareer()) ||
							"QUAL.COMM.".equals(section.getCategory())) {

						if (!actualCoordinators.containsKey(offering.getEid())) {
							coordinatorSet = new HashSet<String>();
							actualCoordinators.put(offering.getEid(), coordinatorSet);
							memberships = cmService.getCourseOfferingMemberships(offering.getEid());
						} else {
							coordinatorSet = actualCoordinators.get(offering.getEid());
						}
					} else {
						if (!actualCoordinators.containsKey(offering.getEid() + SHARABLE_SECTION)) {
							coordinatorSet =  new HashSet<String>();
							actualCoordinators.put(offering.getEid() + SHARABLE_SECTION, coordinatorSet);
							memberships = cmService.getSectionMemberships(offering.getEid() + SHARABLE_SECTION);
						} else {
							coordinatorSet = actualCoordinators.get(offering.getEid() + SHARABLE_SECTION);
						}
					}
				}
				catch (IdNotFoundException infe) {
					log.error("CourseManagement memberships not found: " + infe.getMessage());
				}

				// add the coordinators to the map
				if (memberships != null) {
					for (Membership m : memberships) {
						if (m.getRole().equals(COORDONNATEUR_ROLE)) {
							coordinatorSet.add(m.getUserId());
						}
					}
				}

				// add the coordinators to the course offering or shareable section membership
				for (String coordinator : profCoursEntry.getCoordinators()) {
					if (("CERT".equals(offering.getAcademicCareer()) || "QUAL.COMM.".equals(section.getCategory()))) {

						// if we haven't yet added this coordinator to the CM, do it now
						if (!addedCoordinators.contains(offering.getEid()+coordinator)) {
							cmAdmin.addOrUpdateCourseOfferingMembership(
									coordinator, COORDONNATEUR_ROLE, offering.getEid(), ACTIVE_STATUS);

							//record that we've added this coordinator so we don't do it again
							addedCoordinators.add(offering.getEid()+coordinator);
							coordinatorSet.remove(coordinator);

							log.info("Coordinator added to CourseOffering " + offering.getEid() + ": " + coordinator);
						}
					}
					else if (!addedCoordinators.contains(offering.getEid()+SHARABLE_SECTION+coordinator)){

						// Add coordinator to sharable site for other courses
						cmAdmin.addOrUpdateSectionMembership(
								coordinator, COORDONNATEUR_ROLE, offering.getEid() + SHARABLE_SECTION, ACTIVE_STATUS);

						//record that we've added this coordinator so we don't do it again
						addedCoordinators.add(offering.getEid()+SHARABLE_SECTION+coordinator);
						coordinatorSet.remove(coordinator);

						log.info("Coordinator added to shareable Section "
								+ offering.getEid() + SHARABLE_SECTION + ": "
								+ coordinator);
					}
				}
			} catch (IdNotFoundException infe) {
				log.error("Id Not Found: " + infe.getMessage());
				continue;
			}
		}

		// Remove coordinators that are still in the actualCoordinators map
		for (Map.Entry<String, Set<String>> entry : actualCoordinators.entrySet()) {
			String courseId = entry.getKey();
			Set<String> coordinatorsToRemove = entry.getValue();

			if (coordinatorsToRemove.size() > 0) {
				if (courseId.endsWith("00")) {
					for(String coordinator : coordinatorsToRemove) {
						cmAdmin.removeSectionMembership(coordinator, courseId);
						log.info("Coordinator removed from shareable Section for "
								+ courseId + ": " + coordinator);
					}
				} else {
					for(String coordinator : coordinatorsToRemove) {
						cmAdmin.removeCourseOfferingMembership(coordinator, courseId);
						log.info("Coordinator removed from CourseOffering for "
								+ courseId + ": " + coordinator);

					}
				}
			}
		}

		// il faut faire un refresh des realms, surtout quand on ajout/supprime un instructor ou coordinator qui a/avait les deux roles (sinon il conserve son
		// ancien role dans les realms)
		// en fait, si on ne le fait pas pour les nouveau instructors, ils ne s'affiche pas tant qu'il n'y a pas de refresh.  L'ajout d'un usager avec un autre role
		// provoque un refresh (voir addParticipantsFromMemberships dans SiteParticipantHelper).  Pour une raison inconnu l'ajout du prof ne le fait pas.
		log.info("Start refreshing realms");
		for (String enrollmentSetId : profCoursMap.keySet()) {
			Set<String> realmIds = AuthzGroupService.getAuthzGroupIds(enrollmentSetId);
			for(String realmId : realmIds) {
				try {
					AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);
					AuthzGroupService.save(realm);
				}
				catch (GroupNotDefinedException e) {
					log.info("Error refreshing AuthzGroup (group does not exist): " + realmId);
				}
				catch (AuthzPermissionException e) {
					log.error("Error refreshing AuthzGroup (user doesn't have permission): " + realmId);
				}
			}
		}
		log.info("Finished refreshing realms");
	}

	/** {@inheritDoc} */
	public void loadCourses() {

		DetailCoursMapEntry coursEntry = null;
		String canonicalCourseId = "", courseOfferingId = "", courseSectionId =
				"", courseSetId = "";
		String description = "";
		String title = "";
		String lang = "";
		String typeEvaluation = "";
		String career = "";
		String credits = "";
		String requirements = "";
		AcademicSession session = null;
		String category;
		Set<CanonicalCourse> cc = new HashSet<CanonicalCourse>();
		Set<CourseOffering> courseOfferingSet = new HashSet<CourseOffering>();
		CanonicalCourse canCourse = null;

		Iterator<DetailCoursMapEntry> cours =
				detailCoursMap.values().iterator();

		while (cours.hasNext()) {
			coursEntry = (DetailCoursMapEntry) cours.next();

			//we do not make any processing for ZC1 courses
			if (!"ZC1".equals(coursEntry.getClassSection())) {
				if (!DetailCoursMapEntry.CLASS_STATUS_CANCELLED.equals(coursEntry
						.getClassStat())) {
					canonicalCourseId = getCanonicalCourseId(coursEntry);
					title = coursEntry.getCourseTitleLong();
					description = coursEntry.getCourseTitleLong();
					courseSetId = coursEntry.getAcadOrg();

					// create the canonical course
					if (!cmService.isCanonicalCourseDefined(canonicalCourseId)) {
						canCourse =
								cmAdmin.createCanonicalCourse(canonicalCourseId,
										title, description);
						cc.add(canCourse);
						cmAdmin.setEquivalentCanonicalCourses(cc);

					} else {
						// we update
						canCourse = cmService.getCanonicalCourse(canonicalCourseId);
						canCourse.setDescription(description);
						canCourse.setTitle(title);
					}

					// Check wether there is a directory site
					//hasDirectorySite(canCourse);

					if (cmService.isCourseSetDefined(courseSetId)) {
						cmAdmin.removeCanonicalCourseFromCourseSet(courseSetId,
								canonicalCourseId);
						cmAdmin.addCanonicalCourseToCourseSet(courseSetId,
								canonicalCourseId);
					}
					// create course offering
					session = cmService.getAcademicSession(coursEntry.getStrmId());
					CourseOffering courseOff;
					if (cmService.isAcademicSessionDefined(coursEntry.getStrmId())) {
						courseOfferingId = getCourseOfferingId(coursEntry);
						lang = coursEntry.getLangue();
						typeEvaluation = coursEntry.getTypeEvaluation();
						credits = coursEntry.getUnitsMinimum();
						ProgrammeEtudesMapEntry programmeEtudesMapEntry =
								programmeEtudesMap.get(coursEntry.getAcadCareer());
						RequirementsCoursMapEntry requirementsCoursMapEntry =
								requirementsCoursMap.get(coursEntry.getCourseId());
						try {
							requirements = requirementsCoursMapEntry.getDescription(lang);
						} catch (NullPointerException e) {
							log.warn("requirementsCoursMapEntry is null for course"
									+ canonicalCourseId);
							requirements = null;
						}
						career = programmeEtudesMapEntry.getAcadCareer();

						if (!cmService.isCourseOfferingDefined(courseOfferingId)) {
							courseOff =
									cmAdmin.createCourseOffering(courseOfferingId,
											title, description, COURSE_OFF_STATUS,
											session.getEid(), canonicalCourseId,
											session.getStartDate(), session
											.getEndDate(), lang, career,
											credits, requirements);
							courseOfferingSet.add(courseOff);
						} else {
							// We update
							courseOff =
									cmService.getCourseOffering(courseOfferingId);
							courseOff.setTitle(title);
							courseOff.setDescription(description);
							courseOff.setEndDate(session.getEndDate());
							courseOff.setStartDate(session.getStartDate());
							courseOff.setStatus(COURSE_OFF_STATUS);
							courseOff.setAcademicSession(session);
							courseOfferingSet.add(courseOff);
							courseOff.setLang(lang);
							courseOff.setAcademicCareer(career);
							courseOff.setCredits(credits);
							courseOff.setRequirements(requirements);
							cmAdmin.updateCourseOffering(courseOff);
						}

						if (cmService.isCourseSetDefined(courseSetId)) {
							cmAdmin.removeCourseOfferingFromCourseSet(courseSetId,
									courseOfferingId);
							cmAdmin.addCourseOfferingToCourseSet(courseSetId,
									courseOfferingId);
						}

						// Add section for sharable site
						createOrUpdateSpecialCourseSection(coursEntry);

						// create course section
						category = coursEntry.getAcadOrg();
						courseSectionId = getCourseSectionId(coursEntry);
						Section newSection = null;
						if (!cmService.isSectionDefined(courseSectionId)) {

							newSection =
									cmAdmin.createSection(courseSectionId, title,
											description, category, null,
											courseOfferingId, null, lang, typeEvaluation);

						} else {
							// We update
							newSection = cmService.getSection(courseSectionId);
							newSection.setCategory(category);
							newSection.setDescription(description);
							newSection.setTitle(title);
							newSection.setLang(lang);
							newSection.setTypeEvaluation(typeEvaluation);
							cmAdmin.updateSection(newSection);
						}

						// INFO: Create the enrollment set as soon as the section
						// exists so the students
						// can see the site even though the teacher is not in the
						// system yet.
						String enrollmentSetId =
								getCourseSectionEnrollmentSetId(coursEntry);
						if (newSection.getEnrollmentSet() == null) {

							// Create the enrollment set
							EnrollmentSet enrollmentSet = null;

							if (!cmService.isEnrollmentSetDefined(enrollmentSetId)) {
								try {
									enrollmentSet =
											cmAdmin.createEnrollmentSet(
													enrollmentSetId, title,
													description, courseSetId,
													CREDITS, courseOfferingId,
													new HashSet<String>());
								} catch (IdExistsException e) {
									e.printStackTrace();
								}
							} else
								enrollmentSet =
								cmService.getEnrollmentSet(enrollmentSetId);
							newSection.setEnrollmentSet(enrollmentSet);
							cmAdmin.updateSection(newSection);
						}

					}

				} else {
					// section de cours annulée
					courseOfferingId = getCourseOfferingId(coursEntry);
					courseSectionId = getCourseSectionId(coursEntry);
					try {
						Section section = cmService.getSection(courseSectionId);
						String siteId = getSiteName(section);
						boolean siteExist = osylSiteService.siteExists(siteId);
						if (siteExist) {
							Site site = siteService.getSite(siteId);
							if (osylSiteService.hasBeenPublished(siteId)) {
								osylPublishService.unpublish(siteId, webappDir);
							}
							if (site.getProviderGroupId() != null
									&& !"".equals(site.getProviderGroupId())) {
								osylManagerService.dissociateFromCM(siteId);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

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
			sessionName = OfficialSitesJobImpl.WINTER + year;
		if ((sessionId.charAt(3)) == '2')
			sessionName = OfficialSitesJobImpl.SUMMER + year;
		if ((sessionId.charAt(3)) == '3')
			sessionName = OfficialSitesJobImpl.FALL + year;

		return sessionName;
	}

	private String getSessionName(String sessionId, Date startDate) {
		String sessionName = null;
		Calendar startCalendarDate = Calendar.getInstance();
		startCalendarDate.setTime(startDate);
		String year = Integer.toString(startCalendarDate.get(Calendar.YEAR));

		if ((sessionId.charAt(3)) == '1')
			sessionName = OfficialSitesJobImpl.WINTER + year;
		if ((sessionId.charAt(3)) == '2')
			sessionName = OfficialSitesJobImpl.SUMMER + year;
		if ((sessionId.charAt(3)) == '3')
			sessionName = OfficialSitesJobImpl.FALL + year;

		return sessionName;
	}

	/*
	 * Creates or updates the special section used for the sharable sites
	 * Sharable sites should not have enrollment set so students and everything
	 * section and group aware should not be associated to it.
	 * @param courseSetId
	 * @param title
	 * @param description
	 * @param category
	 * @param courseOfferingId
	 * @param lang
	 * @return
	 */
	private Section createOrUpdateSpecialCourseSection(
			DetailCoursMapEntry coursEntry) {

		String courseOfferingId = getCourseOfferingId(coursEntry);
		Section newSection = null;
		String courseSectionId = courseOfferingId + SHARABLE_SECTION;
		String title = coursEntry.getCourseTitleLong();
		String description = coursEntry.getCourseTitleLong();
		String category = coursEntry.getAcadOrg();
		String lang = coursEntry.getLangue();
		String typeEvaluation = coursEntry.getTypeEvaluation();

		if (!cmService.isSectionDefined(courseSectionId)) {

			log.info("Info: " + courseSectionId + ", " + title + ", " + description
					+ ", " + category + ", " + courseOfferingId + ", " + lang);
			newSection =
					cmAdmin.createSection(courseSectionId, title, description,
							category, null, courseOfferingId, null, lang, typeEvaluation);

		} else {
			// We update
			newSection = cmService.getSection(courseSectionId);
			newSection.setCategory(category);
			newSection.setDescription(description);
			newSection.setTitle(title);
			newSection.setLang(lang);
			cmAdmin.updateSection(newSection);
		}

		return newSection;
	}

	/**
	 * Method used to create academic careers.
	 */
	public void loadAcademicCareers() {
		ProgrammeEtudesMapEntry acadCareerEntry = null;
		String eid = null;
		String description = null;
		String description_fr_ca = null;
		AcademicCareer acadCareer = null;

		Iterator<ProgrammeEtudesMapEntry> academicCareers =
				programmeEtudesMap.values().iterator();

		while (academicCareers.hasNext()) {
			acadCareerEntry = academicCareers.next();
			eid = acadCareerEntry.getAcadCareer();
			description = acadCareerEntry.getDescEng();
			description_fr_ca = acadCareerEntry.getDescFr();
			if (!cmService.isAcademicCareerDefined(eid)) {
				acadCareer =
						cmAdmin.createAcademicCareer(eid, description,
								description_fr_ca);
			} else {
				acadCareer = cmService.getAcademicCareer(eid);
				acadCareer.setDescription(description);
				acadCareer.setDescription_fr_ca(description_fr_ca);
				cmAdmin.updateAcademicCareer(acadCareer);
			}
		}
	}

	/**
	 * Method used to create the sessions
	 */
	// INFO: the existing methods of the course management service and
	// administration does not allow to do everything we want to do: these 2
	// classes doesn't permit to mark a session as current, create a web service
	// that will talk to the hibernate to mark a session as current.
	// Please note that Sakai 2.6 already has that method
	public void loadSessions() {
		DetailSessionsMapEntry sessionEntry = null;
		String eid = "", title = "", description = "";
		Date startDate = null, endDate = null;
		List<String> currentSessions = null;
		AcademicSession aSession = null;

		Date now = new Date(System.currentTimeMillis());
		Iterator<DetailSessionsMapEntry> sessions =
				detailSessionMap.values().iterator();

		while (sessions.hasNext()) {
			sessionEntry = (DetailSessionsMapEntry) sessions.next();
			eid = sessionEntry.getUniqueKey();
			description = sessionEntry.getDescAnglais();
			try {
				startDate =
						DateFormat.getDateInstance().parse(
								sessionEntry.getBeginDate());
				title = getSessionName(eid, startDate);
				endDate =
						DateFormat.getDateInstance().parse(
								sessionEntry.getEndDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (!cmService.isAcademicSessionDefined(eid)) {
				aSession =
						cmAdmin.createAcademicSession(eid, title, description,
								startDate, endDate);
			} else {
				// We update
				aSession = cmService.getAcademicSession(eid);
				aSession.setDescription(description);
				aSession.setEndDate(endDate);
				aSession.setStartDate(startDate);
				aSession.setTitle(title);
				cmAdmin.updateAcademicSession(aSession);
			}

			if (currentSessions == null) {
				currentSessions = new ArrayList<String>();
			}
			if ((now.compareTo(startDate)) >= 0 && now.compareTo(endDate) <= 0) {
				currentSessions.add(aSession.getEid());
			} else
				currentSessions.remove(aSession.getEid());
			cmAdmin.setCurrentAcademicSessions(currentSessions);

		}
	}

	// Has same ID as course section
	private String getCourseSectionEnrollmentSetId(DetailCoursMapEntry course) {
		String enrollmentSetId = null;

		if (course != null) {
			String coursId = course.getCatalogNbr();
			String session = course.getStrm();
			String periode = course.getSessionCode();
			String section = course.getClassSection();

			enrollmentSetId = coursId + session + periode + section;
		}

		return enrollmentSetId;
	}

	/**
	 * Logs in the sakai environment
	 */
	protected void loginToSakai() {
		super.loginToSakai("OsylCMJob");
	}

	private boolean filesExist(String directory) {

		File sessionFile = new File(directory, SESSION_FILE);
		File coursFile = new File(directory, COURS_FILE);
		File etudiantFile = new File(directory, ETUDIANT_FILE);
		File horairesFile = new File(directory, HORAIRES_FILE);
		File profFile = new File(directory, PROF_FILE);
		File servensFile = new File(directory, SERV_ENS_FILE);
		File progEtudFile = new File(directory, PROG_ETUD_FILE);
		File requirementsFile = new File(directory, REQUIREMENTS);

		if (sessionFile.exists() && coursFile.exists() && etudiantFile.exists()
				&& horairesFile.exists() && profFile.exists()
				&& servensFile.exists() && progEtudFile.exists()
				&& requirementsFile.exists()) {
			return true;
		}

		log.warn("At least one file of the PeopleSoft extract file "
				+ "set is missing in directory " + directory);
		return false;

	} // filesExist

	/** {@inheritDoc} */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		long start = System.currentTimeMillis();

		log.info("Start synchronizing PeopleSoft extracts to the"
				+ " course management");

		loginToSakai();

		webappDir =
				System.getProperty("catalina.home") + File.separator
				+ "webapps" + File.separator + "osyl-admin-sakai-tool";// Ugly
		// but
		// don't
		// know
		// cleaner
		// method.

		String directory =
				ServerConfigurationService.getString(EXTRACTS_PATH_CONFIG_KEY,
						null);

		if (directory == null || "".equalsIgnoreCase(directory)) {
			log.warn(this, new IllegalStateException("The property '"
					+ EXTRACTS_PATH_CONFIG_KEY
					+ "' is not defined in the sakai.properties file"));
			return;
		}

		if (!filesExist(directory)) {
			String message =
					"The synchronization did not take place because"
							+ " one of the extract files is missing";
			emailService.send(getAdminZoneCours2EMail(),
					getAdminZoneCours2EMail(),
					"Synchronization with PeopleSoft failed", message, null,
					null, null);
			return;
		}

		try {

			detailSessionMap =
					GenericDetailSessionsMapFactory.getInstance(directory
							+ File.separator + SESSION_FILE);
			detailSessionMap =
					GenericDetailSessionsMapFactory.buildMap(directory
							+ File.separator + SESSION_FILE);
			servEnsMap =
					GenericServiceEnseignementMapFactory.buildMap(directory
							+ File.separator + SERV_ENS_FILE);

			seMap =
					GenericServiceEnseignementMapFactory.getInstance(directory
							+ File.separator + SERV_ENS_FILE);
			seMap =
					GenericServiceEnseignementMapFactory.buildMap(directory
							+ File.separator + SERV_ENS_FILE);

			detailCoursMap =
					GenericDetailCoursMapFactory.getInstance(directory
							+ File.separator + COURS_FILE);
			detailCoursMap =
					GenericDetailCoursMapFactory.buildMap(directory
							+ File.separator + COURS_FILE);

			profCoursMap =
					GenericProfCoursMapFactory.buildMap(directory
							+ File.separator + PROF_FILE);

			etudCoursMap =
					GenericEtudiantCoursMapFactory.getInstance(directory
							+ File.separator + ETUDIANT_FILE);
			etudCoursMap =
					GenericEtudiantCoursMapFactory.buildMap(directory
							+ File.separator + ETUDIANT_FILE, detailCoursMap,
							detailSessionMap);

			programmeEtudesMap =
					GenericProgrammeEtudesMapFactory.getInstance(directory
							+ File.separator + PROG_ETUD_FILE);
			programmeEtudesMap =
					GenericProgrammeEtudesMapFactory.buildMap(directory
							+ File.separator + PROG_ETUD_FILE);

			requirementsCoursMap =
					GenericRequirementsCoursMapFactory.getInstance(directory
							+ File.separator + REQUIREMENTS);
			requirementsCoursMap =
					GenericRequirementsCoursMapFactory.buildMap(directory
							+ File.separator + REQUIREMENTS);

			// We first retrieve the current values in the system for the same
			log.info("Finished reading extracts. Now updating the Course Management");
			// time period as the extracts

			retrieveCurrentCMContent();

			// We load academic careers
			loadAcademicCareers();
			log.info("Academic Careers updated successfully");

			// We load sessions
			loadSessions();
			log.info("Sessions updated successfully");

			// We add a category
			loadCategory();
			log.info("Categories updated successfully");

			// We add a courseSet
			loadCourseSets();
			log.info("CourseSets updated successfully");

			// We load courses
			loadCourses();
			log.info("Courses updated successfully");

			// We assign teachers
			loadMembership();
			log.info("Membership updated successfully");

			// We synchronize students to their classes
			syncEnrollments();
			log.info("Enrollments updated successfully");

			// course events synch
			courseEventSynchroJob.execute(directory + File.separator
					+ HORAIRES_FILE);

		} catch (Exception e) {
			String message =
					"Synchronization with PeopleSoft failed cause :\n"
							+ e.toString();
			emailService.send(getAdminZoneCours2EMail(),
					getAdminZoneCours2EMail(),
					"Synchronization with PeopleSoft failed", message, null,
					null, null);
			e.printStackTrace();
		}

		logoutFromSakai();
		int minutes = (int) ((System.currentTimeMillis() - start) / 60000);
		log.info("Synchronization completed in " + minutes + " minutes");
	}

	/*
	 * We retrieve the content of the course management for the same period as
	 * the extracts we are synchronizing (period read in the session.dat file)
	 */
	private void retrieveCurrentCMContent() {

		// We retrieve informations for the same sessions as the ones in the
		// extract
		DetailSessionsMapEntry sessionEntry = null;
		String sessionId = "";
		String student = "";
		List<AcademicSession> academicSessions =
				new ArrayList<AcademicSession>();
		AcademicSession academicSession;

		Iterator<DetailSessionsMapEntry> sessions =
				detailSessionMap.values().iterator();

		try{
			while (sessions.hasNext()) {
				sessionEntry = sessions.next();
				sessionId = sessionEntry.getUniqueKey();
				if (actualStartDate == null)
	    		    actualStartDate =
	    			    DateFormat.getDateInstance().parse(
	    				    sessionEntry.getBeginDate());
	    		if (cmService.isAcademicSessionDefined(sessionId)) {
	    		    academicSession = cmService.getAcademicSession(sessionId);
	    		    academicSessions.add(academicSession);
	    		}

	    		if (!sessions.hasNext())
	    		    actualEndIntervalDate =
	    			    DateFormat.getDateInstance().parse(
	    				    sessionEntry.getEndDate());

			}
      	} catch (ParseException e) {
    	    e.printStackTrace();
    	}

		// Retrieve the course offerings we will update
		// Retrieve the students (enrollment ) we will update

		String enrollmentSetId = "", courseOfferingId = "";
		Set<CourseSet> courseSets = cmService.getCourseSets();
		String courseSetId = null;
		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();
		Set<CourseOffering> courseOff = null;

		Set<EnrollmentSet> enrollmentS = null;

		actualStudents = new HashMap<String, Enrollment>();
		actualCoursesSection = new HashSet<Section>();
		Set<Enrollment> enrollments = null;

		for (CourseSet courseSet : courseSets) {
			courseSetId = courseSet.getEid();
			for (AcademicSession session : academicSessions) {
				sessionId = session.getEid();
				courseOff =
						cmService.findCourseOfferings(courseSetId, sessionId);
				courseOfferings.addAll(courseOff);

				for (CourseOffering course : courseOff) {
					courseOfferingId = course.getEid();

					// We retrieve the enrollments sets and enrollments
					enrollmentS = cmService.getEnrollmentSets(courseOfferingId);

					for (EnrollmentSet es : enrollmentS) {
						enrollmentSetId = es.getEid();
						enrollments = cmService.getEnrollments(enrollmentSetId);
						for (Enrollment e : enrollments) {
							student = e.getUserId() + enrollmentSetId;
							// We only consider students enrolled
							// if removed they stay in database with dropped =
							// true
							if (!e.isDropped())
								actualStudents.put(student, e);
						}
					}
				}

			}
		}

	}

	/**
	 * This method is used to assign students to their given course section
	 */
	public void syncEnrollments() {
		EtudiantCoursMapEntry etudiantCoursEntry = null;
		String userId = null;
		String enrollmentSetId = null;
		DetailCoursMapEntry cours = null;
		Iterator<DetailCoursMapEntry> coursMap = null;
		Section coursSection = null;
		EnrollmentSet enrollmentSet = null;
		Enrollment enrollment = null;
		AuthzGroup group = null;

		Iterator<EtudiantCoursMapEntry> etudiantCoursMap =
				etudCoursMap.getEtudiants();

		while (etudiantCoursMap.hasNext()) {
			etudiantCoursEntry = etudiantCoursMap.next();
			coursMap = etudiantCoursEntry.getCours();
			userId = etudiantCoursEntry.getMatricule();
			while (coursMap.hasNext()) {
				cours = coursMap.next();
				// TODO: we need information about the credits, gradingScheme
				// and user status in the the course enrollment
				// TODO: map user status to components.xml in provider
				// for now we have enrolled and wait
				// move this command
				coursSection = null;

				enrollmentSet = null;
				enrollmentSetId = null;
				try {
					coursSection =
							cmService.getSection(getCourseSectionId(cours));

					enrollmentSet = coursSection.getEnrollmentSet();
					if (enrollmentSet != null)
						enrollmentSetId = enrollmentSet.getEid();

				} catch (IdNotFoundException e) {
					log.error("Il n'y a pas d'enrollment associe pour le "
							+ enrollmentSetId);
				}

				if (enrollmentSetId != null)
					// The student is not enrolled
					if (!cmService.isEnrolled(userId, enrollmentSetId)) {
						// new enrollment: we create it
						enrollment =
								cmAdmin.addOrUpdateEnrollment(userId,
										enrollmentSetId, ENROLLMENT_STATUS,
										CREDITS, GRADING_SCHEME);
						// the enrollment was been removed but it is added again
						if (enrollment.isDropped())
							enrollment.setDropped(false);
					} else {
						// the student is already enrolled
						if (actualStudents != null) {
							actualStudents.remove(userId + enrollmentSetId);
						}
					}
			}

		}

		// Remove the users in the actual student
		userId = "";
		enrollmentSetId = "";
		enrollment = null;
		Set<String> keys = actualStudents.keySet();
		Set<String> realms = null;
		for (String key : keys) {
			enrollment = actualStudents.get(key);
			enrollment.setDropped(true);
			userId = enrollment.getUserId();
			enrollmentSetId = key.substring(userId.length());

			if (cmAdmin.removeEnrollment(userId, enrollmentSetId)) {
				realms = authzGroupService.getAuthzGroupIds(enrollmentSetId);

				if (realms != null && realms.size() > 0)
					for (String groupId : realms) {
						try {
							group = authzGroupService.getAuthzGroup(groupId);
							group.removeMember(userDirectoryService
									.getUserId(userId));
							authzGroupService.save(group);
						} catch (GroupNotDefinedException e) {
							log.error(e.getMessage());
						} catch (UserNotDefinedException e) {
							log.error(e.getMessage());
						} catch (AuthzPermissionException e) {
							log.error(e.getMessage());
						}
					}
				log.info("L'utilisateur " + userId + " du cours "
						+ enrollmentSetId + " a ete enleve");

			}
		}
	}

	private String getCourseOfferingId(DetailCoursMapEntry cours) {
		String courseOfferingId = null;
		if (cours != null) {
			courseOfferingId =
					cours.getCatalogNbr() + cours.getStrm()
					+ cours.getSessionCode();
			;
		}
		return courseOfferingId;
	}

	private String getCourseSectionId(DetailCoursMapEntry course) {
		String courseSectionId = null;

		if (course != null) {
			String coursId = course.getCatalogNbr();
			String session = course.getStrm();
			String periode = course.getSessionCode();
			String section = course.getClassSection();

			courseSectionId = coursId + session + periode + section;
		}
		return courseSectionId;
	}

	private String getCanonicalCourseId(DetailCoursMapEntry course) {
		String ccId = null;

		if (course != null) {
			String coursId = course.getCatalogNbr();

			ccId = coursId;
		}
		return ccId;
	}

	/**
	 * This method is used to load teachers, interns .... For now
	 * it is used just for teachers
	 */
	public void loadMembership() {
		assignTeachers();
		syncCmExceptions();
	}

	/**
	 * This method is used to automatically register mid-term and final exams.
	 * It can also be used for any prescheduled meeting
	 */
	public void loadMeetings() {
		// TODO: create class ExamenMap and the method getInstance in the class
		// GenericExamenMapFactory to be able to retrieve and register
		// information
	}

	/**
	 * This method is created to load a bogus category that will be used in the
	 * Worksite Setup
	 */
	// For HEC Montreal this will list the different services
	private void loadCategory() {
		ServiceEnseignementMapEntry seEntry;
		String categoryId = null;
		String categoryDescription = null;
		Iterator<ServiceEnseignementMapEntry> se = seMap.values().iterator();

		while (se.hasNext()) {
			seEntry = se.next();
			categoryId = seEntry.getAcadOrg();
			categoryDescription = seEntry.getDescFormal();
			if (cmService.getSectionCategoryDescription(categoryId) == null)
				cmAdmin.addSectionCategory(categoryId, categoryDescription);
			else {
				// TODO: implement update of a category
			}
		}

	}

	/**
	 * Method used to load course sets
	 */
	public void loadCourseSets() {

		ServiceEnseignementMapEntry seEntry;
		String courseSetId = null;
		String courseSetTitle = null;
		String courseSetDescription = null;
		String courseSetCategory = null;
		CourseSet courseSetParent = null;

		Iterator<ServiceEnseignementMapEntry> se = seMap.values().iterator();

		while (se.hasNext()) {
			seEntry = se.next();
			courseSetId = seEntry.getAcadOrg();
			courseSetTitle = seEntry.getDescFormal();
			courseSetDescription = seEntry.getDescFormal();

			if (!cmService.isCourseSetDefined(courseSetId)) {
				cmAdmin.createCourseSet(courseSetId, courseSetTitle,
						courseSetDescription, null, null);
			} else {
				CourseSet cs = cmService.getCourseSet(courseSetId);
				cs.setEid(courseSetId);
				cs.setTitle(courseSetTitle);
				cs.setDescription(courseSetDescription);
				cs.setCategory(courseSetCategory);
				cs.setParent(courseSetParent);
				cmAdmin.updateCourseSet(cs);
			}

		}
	}

	private void syncCmExceptions() {

		Map<String, Map<String, String>> exceptions =
				adminConfigService.getCmExceptions();
		if (exceptions != null && !exceptions.isEmpty())
			for (Entry<String, Map<String, String>> entry : exceptions
					.entrySet()) {
				log.info("Start processing exceptions from " + entry.getKey());
				Map<String, String> props = entry.getValue();
				String users =
						props.get(ConfigurationService.CM_EXCEPTIONS_USERS);
				String courses =
						props.get(ConfigurationService.CM_EXCEPTIONS_COURSES);
				String category =
						props.get(ConfigurationService.CM_EXCEPTIONS_CATEGORY);
				String program =
						props.get(ConfigurationService.CM_EXCEPTIONS_PROGRAM);
				String role =
						props.get(ConfigurationService.CM_EXCEPTIONS_ROLE);
				List<String> matricules = Arrays.asList(users.split(","));
				if (courses != null && !"".equals(courses)) {
					for (String course : Arrays.asList(courses.split(","))) {
						course = course.replaceAll("-", "");
						for (DetailCoursMapEntry dcme : detailCoursMap
								.getAllGroupeCours(course)) {
							String courseOfferingId = getCourseOfferingId(dcme);
							for (String matricule : matricules) {
								log.info("Adding " + matricule
										+ " to courseOffering "
										+ courseOfferingId + " with " + role
										+ " role");
								cmAdmin.addOrUpdateCourseOfferingMembership(
										matricule, role, courseOfferingId,
										ACTIVE_STATUS);
							}
						}
					}
				} else if (category != null && !"".equals(category)) {
					List<DetailCoursMapEntry> coursesList = null;
					if (program != null && !"".equals(program)) {
						coursesList =
								detailCoursMap.getCoursByAcadOrgAndProg(
										category, program);
					} else {
						coursesList =
								detailCoursMap.getCoursByAcadOrg(category);
					}
					for (DetailCoursMapEntry dcme : coursesList) {
						String courseOfferingId = getCourseOfferingId(dcme);
						for (String matricule : matricules) {
							log.info("Adding " + matricule
									+ " to courseOffering " + courseOfferingId
									+ " with " + role + " role");
							cmAdmin.addOrUpdateCourseOfferingMembership(
									matricule, role, courseOfferingId,
									ACTIVE_STATUS);
						}
					}
				}
				log.info("End processing exceptions from " + entry.getKey());
			}

	}

	/**
	 * Check if we have a directory site associated.
	 *
	 * @param canCourse
	 * @return
	 */
	private boolean hasDirectorySite(CanonicalCourse canCourse) {
		boolean exists = false;
		String canCourseId = canCourse.getEid().trim();
		String siteTitle = getDirectorySiteName(canCourseId);

		if (!siteExists(siteTitle)) {
			try {
				// SAKAI-2856 : uncomment when ready
				// exists = osylDirectoryService.createSite(siteTitle,
				// canCourse);

			} catch (Exception e) {
				log.error("The directory site for " + siteTitle
						+ " has not been created.");
				e.printStackTrace();
			}

			// TODO: put a more visible message like sending mail
			log.info("The directory site for " + siteTitle
					+ " has been created.");
		}
		return exists;
	}

	/**
	 * Get the name of the directory site associated
	 *
	 * @param canCourseId
	 * @return
	 */
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

	/**
	 * Check whether a site with the given title exists.
	 *
	 * @param siteTitle
	 * @return
	 */
	public boolean siteExists(String siteTitle) {
		try {
			return siteService.siteExists(siteTitle);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private String getAdminZoneCours2EMail(){
		return ServerConfigurationService.getString("mail.admin.zc2");
	}


}
