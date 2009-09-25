package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserPermissionException;
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
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericMatriculeNomMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericProfCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMapEntry;

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
public class OsylCMJobImpl implements OsylCMJob {

	/**
	 * Directory used to store institutional data (students, teachers, courses,
	 * sessions ...
	 */
	private static final String EXTRACTS_PATH = "webapps" + File.separator
			+ "osyl-admin-sakai-tool" + File.separator + "extracts";

	/**
	 * Map used to store information about users : name, id
	 */
	private MatriculeNomMap matNomMap = null;

	/**
	 * Map used to store information about the courses that a teacher gives:
	 * teacher id, course id, course session, course periode
	 */
	private ProfCoursMap profCoursMap = null;

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
	 * Map used to store information about sessions: session id, starting date,
	 * ending date
	 */
	private DetailSessionsMap detailSessionMap = null;

	/**
	 * Map used to store information about the exam dates
	 */
	private GenericExamensMapFactory examenMap = null;

	/**
	 * Type of user considered as a general assistant in the course site
	 */
	private static final String GENERAL_ASSISTANT = "TA";

	/**
	 * Type of user considered as a teacher assistant in the course site
	 */
	private static final String TEACHER_ASSISTANT = "TA";

	/**
	 * Type of user considered as a student in the course site
	 */
	private static final String STUDENT_USER = "Student";

	/**
	 * Type of user considered as a teacher in the course site
	 */
	private static final String TEACHER_USER = "Teacher";

	/**
	 * Our logger
	 */
	private static Log log = LogFactory.getLog(OsylCMJobImpl.class);

	/**
     *
     */
	private CourseManagementAdministration cmAdmin;

	/**
	 * @param cmAdmin
	 */
	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}

	/**
     *
     */
	private CourseManagementService cmService;

	/**
	 * @param cmService
	 */
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}

	/**
	 * The user service injected by the Spring
	 */
	private UserDirectoryService userDirService;

	/**
	 * Sets the <code>UserDirectoryService</code> needed to create the site in
	 * the init() method.
	 * 
	 * @param userDirService
	 */
	public void setUserDirService(UserDirectoryService userDirService) {
		this.userDirService = userDirService;
	}

	/** {@inheritDoc} */
	public void assignTeachers() {
		ProfCoursMapEntry profCoursEntry = null;
		String courseOfferingId = "";
		String courseSectionId = "";
		String matricule = "";
		Iterator cours;
		DetailCoursMapEntry detailsCours;

		Iterator<ProfCoursMapEntry> profCours = profCoursMap.values()
				.iterator();

		while (profCours.hasNext()) {
			profCoursEntry = (ProfCoursMapEntry) profCours.next();
			matricule = profCoursEntry.getMatricule();
			cours = profCoursEntry.getCours();
			while (cours.hasNext()) {
				detailsCours = (DetailCoursMapEntry) cours.next();
				courseOfferingId = getCourseOfferingId(detailsCours);
				courseSectionId = getCourseSectionId(detailsCours);
				// TODO: the role should be a final static constant
				// TODO: the status should be decided and be a final static
				// constant
				// INFO: We add a membership to a course section
				cmAdmin.addOrUpdateCourseOfferingMembership(matricule, "Instructor",
						courseOfferingId, "active");
				cmAdmin.addOrUpdateSectionMembership(matricule, "Instructor", courseSectionId, "active");
				//We add the teacher as official instructor
				EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(getEnrollmentSetId(detailsCours));
				Set instructors = new HashSet();
				instructors.add(matricule);
				enrollmentSet.setOfficialInstructors(instructors);
				cmAdmin.updateEnrollmentSet(enrollmentSet);
				
			}
		}
	}

	/** {@inheritDoc} */
	public void loadCourses() {
		DetailCoursMapEntry coursEntry = null;
		String canonicalCourseId = "", courseOfferingId = "", courseSectionId = "", enrollmentSetId = "";
		String description = "";
		String title = "";
		AcademicSession session = null;
		String status = "active";
		String section = "";
		Set<CanonicalCourse> cc = new HashSet<CanonicalCourse>();
		Set<CourseOffering> courseOfferingSet = new HashSet<CourseOffering>();

		Iterator<DetailCoursMapEntry> cours = detailCoursMap.values()
				.iterator();

		while (cours.hasNext()) {
			coursEntry = (DetailCoursMapEntry) cours.next();
			canonicalCourseId = coursEntry.getUniqueKey();
			title = coursEntry.getTitre();
			description = coursEntry.getTitreAlt();

			// create the canonical course
			if (!cmService.isCanonicalCourseDefined(canonicalCourseId)) {
				cc.add(cmAdmin.createCanonicalCourse(canonicalCourseId, title,
						description));
				cmAdmin.addCanonicalCourseToCourseSet("11", canonicalCourseId);

			} else {
				// we update
				CanonicalCourse canCourse = cmService
						.getCanonicalCourse(canonicalCourseId);
				canCourse.setDescription(description);
				canCourse.setTitle(title);
			}

			// create course offering
			session = cmService.getAcademicSession(coursEntry.getSession());
			title = title + "   " + session.getDescription();
			if (session != null) {
				// TODO: This should be a final static constant
				status = "course";
				courseOfferingId = getCourseOfferingId(coursEntry);
				if (!cmService.isCourseOfferingDefined(courseOfferingId)) {
					courseOfferingSet.add(cmAdmin.createCourseOffering(
							courseOfferingId, title, description, status,
							session.getEid(), canonicalCourseId, session
									.getStartDate(), session.getEndDate()));
					cmAdmin
							.addCourseOfferingToCourseSet("11",
									courseOfferingId);
					cmAdmin.addOrUpdateCourseOfferingMembership("admin",
							"CourseAdmin", courseOfferingId, "active");
				} else {
					// We update
					CourseOffering courseOff = cmService
							.getCourseOffering(courseOfferingId);
					courseOff.setTitle(title);
					courseOff.setDescription(description);
					courseOff.setEndDate(session.getEndDate());
					courseOff.setStartDate(session.getStartDate());
					courseOff.setStatus(status);
					courseOff.setAcademicSession(session);
					courseOfferingSet.add(courseOff);
					cmAdmin.updateCourseOffering(courseOff);
					cmAdmin
					.addCourseOfferingToCourseSet("11",
							courseOfferingId);
					cmAdmin.addOrUpdateCourseOfferingMembership("admin",
							"CourseAdmin", courseOfferingId, "active");
				}

				// We create the enrollmentSets
				// We create enrollmentSets that we will assign to the
				// course section
				// TODO: the extracts don't give the course credit
				// TODO: find a better way to retrieve the list of teachers
				enrollmentSetId = getEnrollmentSetId(coursEntry);
				if (!cmService.isEnrollmentSetDefined(enrollmentSetId)) {
					cmAdmin.createEnrollmentSet(enrollmentSetId, title,
							description, "GTI-Info", "3", courseOfferingId,
							null);
				} else {
					// We update
					EnrollmentSet enrollmentSet = cmService
							.getEnrollmentSet(enrollmentSetId);
					enrollmentSet.setCategory("GTI-Info");
					enrollmentSet.setDefaultEnrollmentCredits("3");
					enrollmentSet.setDescription(description);
					enrollmentSet.setTitle(title);
					cmAdmin.updateEnrollmentSet(enrollmentSet);
				}

				// create course section
				section = coursEntry.getSection();
				courseSectionId = courseOfferingId + section;
				title = title + "  " + section;
				if (!cmService.isSectionDefined(courseSectionId)) {

					// TODO: Find what to match the category with
					// TODO: check if we really need to have an enrollmentSet
					// from here
					cmAdmin
							.createSection(courseSectionId, title, description,
									"GTI-Info", null, courseOfferingId,
									enrollmentSetId);
				} else {
					// We update
					Section courseSection = cmService
							.getSection(courseSectionId);
					courseSection.setCategory("GTI-Info");
					courseSection.setDescription(description);
					courseSection.setTitle(title);
					courseSection.setEnrollmentSet(cmService
							.getEnrollmentSet(enrollmentSetId));
					cmAdmin.updateSection(courseSection);
				}
			}

		}
		cmAdmin.setEquivalentCanonicalCourses(cc);
		cmAdmin.setEquivalentCourseOfferings(courseOfferingSet);
	}

	/** {@inheritDoc} */
	// TODO: the existing methods of the course management service and
	// administration does not allow to do everything we want to do: these 2
	// classes doesn't permit to mark a session as current, create a web service
	// that will talk to the hibernate to mark a session as current.
	// Please note that Sakai 2.6 already has that method
	public void loadSessions() {
		DetailSessionsMapEntry sessionEntry = null;
		String eid = "", title = "", description = "";
		Date startDate = null, endDate = null;
		Iterator<DetailSessionsMapEntry> sessions = detailSessionMap.values()
				.iterator();

		while (sessions.hasNext()) {
			sessionEntry = (DetailSessionsMapEntry) sessions.next();
			eid = sessionEntry.getNumero();
			title = sessionEntry.getShortForm();
			description = sessionEntry.getLongForm();
			try {
				startDate = DateFormat.getDateInstance().parse(
						sessionEntry.getStartDate());
				endDate = DateFormat.getDateInstance().parse(
						sessionEntry.getEndDate());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (!cmService.isAcademicSessionDefined(eid)) {
				cmAdmin.createAcademicSession(eid, title, description,
						startDate, endDate);
				// ///////////////////////////////////////////////////////////////
				// this method is not available for sakai 2.5 //
				// you can activate a session manually by changing to 1 //
				// the value of is_current in the cm_academic_session_t //
				// TODO: activate this method when running again on sakai 2.6 //
				// cmAdmin.setCurrentAcademicSessions(Arrays //
				// .asList(new String[] { eid })); //
				// ////////////////////////////////////////////////////////////////
			} else {
				// We update
				AcademicSession aSession = cmService.getAcademicSession(eid);
				aSession.setDescription(description);
				aSession.setEndDate(endDate);
				aSession.setStartDate(startDate);
				aSession.setTitle(title);
				cmAdmin.updateAcademicSession(aSession);

			}
		}
	}

	/** {@inheritDoc} */
	public void loadUsers() {
		Iterator<MatriculeNomMapEntry> values = matNomMap.values().iterator();
		MatriculeNomMapEntry entry;
		Collection userExists = null;
		// User informations
		String eid, firstName, lastName, email, pw, type;

		while (values.hasNext()) {
			entry = (MatriculeNomMapEntry) values.next();
			try {
				email = entry.getEmailAddress();
				// We check if user already in table
				userExists = userDirService.findUsersByEmail(email);

				if (userExists.size() == 0) {
					// We create a new user
					eid = entry.getMatricule();
					firstName = entry.getFirstName();
					lastName = entry.getLastName();
					type = getType(entry.getStatus());
					pw = eid;
					userDirService.addUser(null, eid, firstName, lastName,
							email, pw, type, null);
				}
			} catch (UserIdInvalidException e) {
				log.error("Create users - user invalid id", e);
			} catch (UserAlreadyDefinedException e) {
				log.warn("Create users - user already defined");
			} catch (UserPermissionException e) {
				log.error("Create users - permission exception", e);
			}

		}
	}

	/**
	 * This method maps the status of a user to a valid type in Sakai
	 * 
	 * @param status
	 * @return
	 */
	private String getType(String status) {
		if (status.equalsIgnoreCase("Y"))
			return "registered";
		return null;
	}

	private String getEnrollmentSetId(DetailCoursMapEntry cours) {
		String enrollmentSetId = null;

		if (cours != null) {
			enrollmentSetId = cours.getSection() + cours.getUniqueKey()
					+ cours.getSession() + cours.getSection();

		}

		return enrollmentSetId;
	}

	/**
	 * This method whether a course is linked to an enrollmentSet
	 * 
	 * @param cours
	 * @return hasEnrollmentSet
	 */
	private boolean courseHasEnrollmentSet(DetailCoursMapEntry cours) {
		boolean hasErollmentSet = false;
		String enrollmentSetId = "";
		if (cours != null) {
			enrollmentSetId = getEnrollmentSetId(cours);
			if (enrollmentSetId != null) {
				return cmService.isEnrollmentSetDefined(enrollmentSetId);
			}
		}

		return hasErollmentSet;
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

	/**
	 * Retreives the folder where is kept the extract file. Later on we will
	 * have a fixed values representing the place where are kept the extract
	 * files.
	 * 
	 * @param CLASSPATHTOEXTRACTS
	 */
	private String getPathToExtracts() {

		String directory = System.getProperty("catalina.home");
		return directory + File.separator + EXTRACTS_PATH;

	}

	/**
	 * {@inheritDoc} This method is for development purpose, in production we
	 * should use quartz
	 */
	public void load() {
		loginToSakai();

		String directory = getPathToExtracts();
		try {
			detailSessionMap = GenericDetailSessionsMapFactory
					.getInstance(directory);
			detailSessionMap = GenericDetailSessionsMapFactory
					.buildMap(directory);
			// We load sessions
			loadSessions();

			matNomMap = GenericMatriculeNomMapFactory.getInstance(directory);
			matNomMap = GenericMatriculeNomMapFactory.buildMap(directory);
			// We load user
			loadUsers();

			// We add a category
			loadCategory();
			// We add a courseSet
			loadCourseSets();
			detailCoursMap = GenericDetailCoursMapFactory
					.getInstance(directory);
			detailCoursMap = GenericDetailCoursMapFactory.buildMap(directory);
			// We load courses
			loadCourses();

			profCoursMap = GenericProfCoursMapFactory.getInstance(directory);
			profCoursMap = GenericProfCoursMapFactory.buildMap(directory,
					detailCoursMap, detailSessionMap);
			// We assign teachers
			loadMembership();

			etudCoursMap = GenericEtudiantCoursMapFactory
					.getInstance(directory);
			etudCoursMap = GenericEtudiantCoursMapFactory.buildMap(directory,
					detailCoursMap, detailSessionMap);
			// We assign students to their classes
			loadEnrollments();

		} catch (IOException e) {
			e.printStackTrace();
		}

		logoutFromSakai();
	}

	/** {@inheritDoc} */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		loginToSakai();

		String directory = getPathToExtracts();
		try {
			detailSessionMap = GenericDetailSessionsMapFactory
					.getInstance(directory);
			detailSessionMap = GenericDetailSessionsMapFactory
					.buildMap(directory);
			loadSessions();

			matNomMap = GenericMatriculeNomMapFactory.getInstance(directory);
			matNomMap = GenericMatriculeNomMapFactory.buildMap(directory);
			loadUsers();

			detailCoursMap = GenericDetailCoursMapFactory
					.getInstance(directory);
			detailCoursMap = GenericDetailCoursMapFactory.buildMap(directory);
			loadCourses();

			profCoursMap = GenericProfCoursMapFactory.getInstance(directory);
			profCoursMap = GenericProfCoursMapFactory.buildMap(directory,
					detailCoursMap, detailSessionMap);

			etudCoursMap = GenericEtudiantCoursMapFactory
					.getInstance(directory);
			etudCoursMap = GenericEtudiantCoursMapFactory.buildMap(directory,
					detailCoursMap, detailSessionMap);

		} catch (IOException e) {
			e.printStackTrace();
		}

		logoutFromSakai();
	}

	/** {@inheritDoc} */
	public void loadEnrollments() {
		EtudiantCoursMapEntry etudiantCoursEntry = null;
		String userId = null;
		String enrollmentSetId = null;
		DetailCoursMapEntry cours;
		Iterator<DetailCoursMapEntry> coursMap;
		Iterator<EtudiantCoursMapEntry> etudiantCoursMap = etudCoursMap
				.getEtudiants();

		while (etudiantCoursMap.hasNext()) {
			etudiantCoursEntry = etudiantCoursMap.next();
			coursMap = etudiantCoursEntry.getCours();
			userId = etudiantCoursEntry.getMatricule();
			while (coursMap.hasNext()) {
				cours = coursMap.next();
				// TODO: with this value we should make sure the enrollmentSet
				// exists
				enrollmentSetId = getEnrollmentSetId(cours);
				// TODO: do something when enrollmentSetId is null
				// TODO: we need information about the credits, gradingScheme
				// and user status in the the course enrollment
				//TODO: map user status to components.xml in provider
				// for now we have enrolled and wait
				// TODO: the official teacher is assigned at the same time as
				// the enrollmentset,
				// move this command
				try {
					EnrollmentSet enrollmentSet = cmService
							.getEnrollmentSet(enrollmentSetId);
				} catch (IdNotFoundException e) {
					System.err
							.println("Il n'y a pas d'enrollment associé pour le "
									+ enrollmentSetId);
				}

				if (courseHasEnrollmentSet(cours))
					if (!cmService.isEnrolled(userId, enrollmentSetId)) {
						cmAdmin.addOrUpdateEnrollment(userId, enrollmentSetId,
								"enrolled ", "3", "Letter");
					}
			}

		}
	}

	private String getCourseOfferingId(DetailCoursMapEntry cours) {
		String courseOfferingId = null;
		if (cours != null) {
			courseOfferingId = cours.getUniqueKey() + cours.getSession();
		}
		return courseOfferingId;
	}

	private String getCourseSectionId(DetailCoursMapEntry course) {
		String courseSectionId = null;

		if (course != null) {
			String coursId = course.getUniqueKey();
			String session = course.getSession();
			String section = course.getSection();

			courseSectionId = coursId + session + section;
		}
		return courseSectionId;
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadMembership() {
		assignTeachers();
	}

	/** {@inheritDoc} */
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
		if (cmService.getSectionCategoryDescription("111") == null)
			cmAdmin.addSectionCategory("111", "GTI-Info");
	}

	// For HEC Montreal, this is the institution HEC Montreal
	private void loadCourseSets() {
		if (!cmService.isCourseSetDefined("11"))
			cmAdmin.createCourseSet("11", "HEC Montréal", "Montréal - Québec",
					"GTI-Info", null);
		cmAdmin.addOrUpdateCourseSetMembership("admin", "DeptAdmin", "11",
				"active");
	}
}
