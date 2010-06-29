package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.id.cover.IdManager;
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
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericSecretairesMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericServiceEnseignementMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.SecretairesMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.SecretairesMapEntry;
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
public class OsylCMJobImplUdeM implements OsylCMJob {

	/**
	 * Directory used to store institutional data (students, teachers, courses,
	 * sessions ...
	 */
	@Deprecated
	private static final String EXTRACTS_PATH = 
		"webapps" + File.separator	+ "osyl-admin-sakai-tool" 
			+ File.separator + "extracts";

	/**
	 * Map used to store information about users : name, id
	 */
	// private MatriculeNomMap matNomMap = null;
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
     * Map used to store information about the secretaries.
     */
    private SecretairesMap secretairesMap = null;
	
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
	private static Log log = LogFactory.getLog(OsylCMJobImplUdeM.class);

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

	/* load the instructors and assigns them to their courses */
	private void assignTeachers() {
		ProfCoursMapEntry profCoursEntry = null;
		String courseOfferingId = "";
		String courseSectionId = "";
		String matricule = "";
		Iterator<DetailCoursMapEntry> cours;
		DetailCoursMapEntry detailsCours;

		Iterator<ProfCoursMapEntry> profCours = 
			profCoursMap.values().iterator();

		while (profCours.hasNext()) {
			profCoursEntry = (ProfCoursMapEntry) profCours.next();
			matricule = profCoursEntry.getEmplId();
			cours = profCoursEntry.getCours();
			while (cours.hasNext()) {
				detailsCours = (DetailCoursMapEntry) cours.next();
				courseOfferingId = getCourseOfferingId(detailsCours);
				courseSectionId = getCourseSectionId(detailsCours);		

				if (cmService.isSectionDefined(courseSectionId))
					cmAdmin.addOrUpdateSectionMembership(matricule,
							"I", courseSectionId, "active");
			}
		}
	}

	// TODO: add a method to load the services d'enseignement that will be used
	// as course set
	// canonical course id = course catalog_nbr [+ version]
	// course offering id = course catalog_nbr [+ version] + session code +
	// periode
	// section id = course catalog_nbr [+ version] + session code + section +
	// periode

	/** {@inheritDoc} */
	public void loadCourses() {

		DetailCoursMapEntry coursEntry = null;
		String canonicalCourseId = "", courseOfferingId = "", courseSectionId = "", courseSetId = "";
		String description = "";
		String title = "";
		AcademicSession session = null;
		String status = "active";
		String section = "";
		String category;
		Set<CanonicalCourse> cc = new HashSet<CanonicalCourse>();
		Set<CourseOffering> courseOfferingSet = new HashSet<CourseOffering>();

		Iterator<DetailCoursMapEntry> cours = 
			detailCoursMap.values().iterator();

		while (cours.hasNext()) {
			coursEntry = (DetailCoursMapEntry) cours.next();
			canonicalCourseId = getCanonicalCourseId(coursEntry);
			title = coursEntry.getCourseTitleLong();
			description = coursEntry.getCourseTitleLong();
			courseSetId = coursEntry.getAcadOrg();
			String lang = coursEntry.getLangue();
			// create the canonical course
			if (!cmService.isCanonicalCourseDefined(canonicalCourseId)) {
				cc.add(cmAdmin.createCanonicalCourse(canonicalCourseId, title,
						description));
				cmAdmin.setEquivalentCanonicalCourses(cc);

			} else {
				// we update
				CanonicalCourse canCourse = 
					cmService.getCanonicalCourse(canonicalCourseId);
				canCourse.setDescription(description);
				canCourse.setTitle(title);
			}

			if (cmService.isCourseSetDefined(courseSetId)) {
				cmAdmin.removeCanonicalCourseFromCourseSet(courseSetId,
						canonicalCourseId);
				cmAdmin.addCanonicalCourseToCourseSet(courseSetId,
						canonicalCourseId);
			}
			// create course offering
			session = cmService.getAcademicSession(coursEntry.getStrmId());
			title = title + " " + session.getDescription();
			CourseOffering courseOff;
			if (cmService.isAcademicSessionDefined(coursEntry.getStrmId())) {
				// TODO: This should be a final static constant
				status = "course";
				courseOfferingId = getCourseOfferingId(coursEntry);
				title = getCourseOfferingTitle(coursEntry);
				if (!cmService.isCourseOfferingDefined(courseOfferingId)) {
					courseOff = cmAdmin.createCourseOffering(courseOfferingId,
							title, description, status, session.getEid(),
							canonicalCourseId, session.getStartDate(), session
									.getEndDate(), lang);
					courseOfferingSet.add(courseOff);
				} else {
					// We update
					courseOff = cmService.getCourseOffering(courseOfferingId);
					courseOff.setTitle(title);
					courseOff.setDescription(description);
					courseOff.setEndDate(session.getEndDate());
					courseOff.setStartDate(session.getStartDate());
					courseOff.setStatus(status);
					courseOff.setAcademicSession(session);
					courseOfferingSet.add(courseOff);
					cmAdmin.updateCourseOffering(courseOff);
				}

				if (cmService.isCourseSetDefined(courseSetId)) {
					cmAdmin.removeCourseOfferingFromCourseSet(courseSetId, courseOfferingId);
					cmAdmin.addCourseOfferingToCourseSet(courseSetId,courseOfferingId);
				}
				// // We create the enrollmentSets
				// // We create enrollmentSets that we will assign to the
				// // course section
				// // TODO: the extracts don't give the course credit
				// // TODO: find a better way to retrieve the list of teachers
				// // enrollmentSetId = getEnrollmentSetId(coursEntry);
				// // if (!cmService.isEnrollmentSetDefined(enrollmentSetId)) {
				// // cmAdmin.createEnrollmentSet(enrollmentSetId, title,
				// // description, "111", "3", courseOfferingId,
				// // null);
				// // } else {
				// // // We update
				// // EnrollmentSet enrollmentSet = cmService
				// // .getEnrollmentSet(enrollmentSetId);
				// // enrollmentSet.setCategory("111");
				// // enrollmentSet.setDefaultEnrollmentCredits("3");
				// // enrollmentSet.setDescription(description);
				// // enrollmentSet.setTitle(title);
				// // cmAdmin.updateEnrollmentSet(enrollmentSet);
				// // }
				//				
				// create course section
				section = coursEntry.getClassSection();
				category = coursEntry.getAcadOrg();
				courseSectionId = getCourseSectionId(coursEntry);
				title = getCourseSectionTitle(coursEntry);

				if (!cmService.isSectionDefined(courseSectionId)) {

					// TODO: check if we really need to have an enrollmentSet
					// from here
					cmAdmin.createSection(courseSectionId, title, description,
							category, null, courseOfferingId, null, lang);
				} else {
					// We update
					Section courseSection = 
						cmService.getSection(courseSectionId);
					courseSection.setCategory(category);
					courseSection.setDescription(description);
					courseSection.setTitle(title);
					cmAdmin.updateSection(courseSection);
				}
			}

		}

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

		Date now = new Date(System.currentTimeMillis());
		Iterator<DetailSessionsMapEntry> sessions = 
			detailSessionMap.values().iterator();

		while (sessions.hasNext()) {
			sessionEntry = (DetailSessionsMapEntry) sessions.next();
			eid = sessionEntry.getUniqueKey();
			title = sessionEntry.getDescAnglais();
			description = sessionEntry.getDescAnglais();
			try {
				startDate = DateFormat.getDateInstance().parse(
						sessionEntry.getBeginDate());
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
				if ((now.compareTo(startDate)) >= 0
						&& endDate.compareTo(endDate) <= 0)
					cmAdmin.setCurrentAcademicSessions(Arrays
							.asList(new String[] { eid })); //
				// ////////////////////////////////////////////////////////////////
			} else {
				// We update
				AcademicSession aSession = cmService.getAcademicSession(eid);
				aSession.setDescription(description);
				aSession.setEndDate(endDate);
				aSession.setStartDate(startDate);
				aSession.setTitle(title);
				cmAdmin.updateAcademicSession(aSession);
				if ((now.compareTo(startDate)) >= 0
						&& endDate.compareTo(endDate) <= 0)
					cmAdmin.setCurrentAcademicSessions(Arrays
							.asList(new String[] { eid })); //

			}
		}
	}

	/** {@inheritDoc} */
	public void loadUsers() {
		// Iterator<MatriculeNomMapEntry> values =
		// matNomMap.values().iterator();
		// MatriculeNomMapEntry entry;
		// Collection userExists = null;
		// // User informations
		// String eid, firstName, lastName, email, pw, type;
		//
		// while (values.hasNext()) {
		// entry = (MatriculeNomMapEntry) values.next();
		// try {
		// email = entry.getEmailAddress();
		// // We check if user already in table
		// userExists = userDirService.findUsersByEmail(email);
		//
		// if (userExists.size() == 0) {
		// // We create a new user
		// eid = entry.getMatricule();
		// firstName = entry.getFirstName();
		// lastName = entry.getLastName();
		// type = getType(entry.getStatus());
		// pw = eid;
		// userDirService.addUser(null, eid, firstName, lastName,
		// email, pw, type, null);
		// }
		// } catch (UserIdInvalidException e) {
		// log.error("Create users - user invalid id", e);
		// } catch (UserAlreadyDefinedException e) {
		// log.warn("Create users - user already defined");
		// } catch (UserPermissionException e) {
		// log.error("Create users - permission exception", e);
		// }
		//
		// }
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

	// Has same ID as course offering
	private String getCourseOfferingEnrollmentSetId(DetailCoursMapEntry course) {
		String enrollmentSetId = null;

		if (course != null) {
			String coursId = course.getCatalogNbr();
			String session = course.getStrm();
			String periode = course.getSessionCode();

			enrollmentSetId = coursId + session + periode;
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
			enrollmentSetId = getCourseSectionEnrollmentSetId(cours);
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

	/** {@inheritDoc} */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info(" Strart synchronizing PeopleSoft data extracts to the course management");

		loginToSakai();

		String directory = 
			ServerConfigurationService.getString(
					"coursemanagement.extract.files.path", 
					getPathToExtracts());

		if (directory == null || "".equalsIgnoreCase(directory)) {
			log
					.warn(
							this,
							new IllegalStateException(
									"The property 'coursemanagement.extract.files.path' is not defined in the sakai.properties file"));
			return;
		}
		try {
			detailSessionMap = 
				GenericDetailSessionsMapFactory.getInstance(directory);
			detailSessionMap = 
				GenericDetailSessionsMapFactory.buildMap(directory);
			// We load sessions
			loadSessions();

			seMap = GenericServiceEnseignementMapFactory.getInstance(directory);
			seMap = GenericServiceEnseignementMapFactory.buildMap(directory);

			// We add a category
			loadCategory();

			// We add a courseSet
			loadCourseSets();

			detailCoursMap = 
				GenericDetailCoursMapFactory.getInstance(directory);
			detailCoursMap = GenericDetailCoursMapFactory.buildMap(directory);
			// We load courses
			loadCourses();

			profCoursMap = GenericProfCoursMapFactory.getInstance(directory);
			profCoursMap = 
				GenericProfCoursMapFactory.buildMap(directory,
					detailCoursMap, detailSessionMap);
			
		    // secretairesMap =
			//    GenericSecretairesMapFactory.getInstance(directory);
		    // secretairesMap = GenericSecretairesMapFactory.buildMap(directory);
		    
			// We assign teachers and the secretaries
			 loadMembership();

			 etudCoursMap = 
				 GenericEtudiantCoursMapFactory.getInstance(directory);
			 etudCoursMap = 
				 GenericEtudiantCoursMapFactory.buildMap(directory,
						 detailCoursMap, detailSessionMap);
			 // We assign students to their classes
			 syncEnrollmentSets();

		} catch (IOException e) {
			e.printStackTrace();
		}

		logoutFromSakai();
		log.info(" End synchronizing PeopleSoft data extracts to the course management");
		
	}

	/** {@inheritDoc} */
	public void syncEnrollmentSets() {
		EtudiantCoursMapEntry etudiantCoursEntry = null;
		String userId = null;
		String enrollmentSetId = null;
		String coursSectionId;
		DetailCoursMapEntry cours = null;
		Iterator<DetailCoursMapEntry> coursMap = null;
		Section coursSection = null;
		EnrollmentSet enrollmentSet = null;

		Iterator<EtudiantCoursMapEntry> etudiantCoursMap = 
			etudCoursMap.getEtudiants();

		while (etudiantCoursMap.hasNext()) {
			etudiantCoursEntry = etudiantCoursMap.next();
			coursMap = etudiantCoursEntry.getCours();
			userId = etudiantCoursEntry.getMatricule();
			while (coursMap.hasNext()) {
				cours = coursMap.next();
				// TODO: do something when enrollmentSetId is null
				// TODO: we need information about the credits, gradingScheme
				// and user status in the the course enrollment
				// TODO: map user status to components.xml in provider
				// for now we have enrolled and wait
				// TODO: the official teacher is assigned at the same time as
				// the enrollmentset,
				// move this command
				coursSection = null;
				coursSectionId = null;
				
				enrollmentSet = null;
				enrollmentSetId = null;
				try {
					coursSection = 
						cmService.getSection(getCourseSectionId(cours));
					if (coursSection != null)
						coursSectionId = coursSection.getEid();

					enrollmentSet = coursSection.getEnrollmentSet();
					if (enrollmentSet != null)
						enrollmentSetId = enrollmentSet.getEid();
					
				} catch (IdNotFoundException e) {
					System.err
							.println("Il n'y a pas d'enrollment associé pour le "
									+ enrollmentSetId);
				}

				if (coursSection != null)
					cmAdmin.addOrUpdateSectionMembership(userId,
							"S", coursSectionId, "active");
			}

		}
	}

	private String getCourseOfferingId(DetailCoursMapEntry cours) {
		String courseOfferingId = null;
		if (cours != null) {
			courseOfferingId = cours.getCatalogNbr() + cours.getStrm()
			+ cours.getSessionCode();

		}
		return courseOfferingId;
	}
	
	private String getCourseOfferingTitle(DetailCoursMapEntry cours) {
		String courseOfferingId = null;
		if (cours != null) {
			courseOfferingId = cours.getCatalogNbr() + " " + cours.getStrmId();
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

	private String getCourseSectionTitle(DetailCoursMapEntry course) {
		String courseSectionId = null;

		if (course != null) {
			String coursId = course.getCatalogNbr();
			String session = course.getStrmId();
			String periode = course.getSessionCode();
			String section = course.getClassSection();

			if (section.trim().length() == 0)
				courseSectionId = coursId + " " + session;
			else
				courseSectionId = coursId + "-" + section + " " + session;
		}
		return courseSectionId;
	}

	
	private String getCourseSetId(DetailCoursMapEntry course) {
		String csId = null;

		if (course != null) {
			String acadOrg = course.getAcadOrg();

			csId = acadOrg;
		}
		return csId;
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
	 * {@inheritDoc}
	 */
	public void loadMembership() {
		assignTeachers();
		// assignSecretaries();
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
	 * {@inheritDoc}
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
	/*
     * Loads the secretaries and assigns them to the CourseSet in their associated
     * SE.
     */
    private void assignSecretaries() {

    	SecretairesMapEntry entry = null;
    	String deptId = null;
    	Iterator<SecretairesMapEntry> secretaries =
    		secretairesMap.values().iterator();

    	ServiceEnseignementMapEntry seEntry = null;
    	CourseSet courseS = null;
    	Iterator<CourseOffering> courseOffs = null;
    	Iterator<Section> sections = null;
    	CourseOffering courseOff = null;
    	Section section = null;

    	while (secretaries.hasNext()) {
    		entry = secretaries.next();
    		deptId = entry.getDeptId();
    		seEntry = seMap.getByDeptId(deptId);

    		if (seEntry != null) {
    			courseS = cmService.getCourseSet(seEntry.getAcadOrg());

    			if (courseS != null) {
    				courseOffs =
    					cmService.getCourseOfferingsInCourseSet(
    							courseS.getEid()).iterator();

    				while (courseOffs.hasNext()) {

    					courseOff = courseOffs.next();
    					sections =
    						cmService.getSections(courseOff.getEid()).iterator();

    					while (sections.hasNext()) {
    						section = sections.next();

    						cmAdmin.addOrUpdateSectionMembership(entry
    								.getEmplId(), "I", section.getEid(),
    								"active");
    					}
    				}
    			}
    		}
    	}
    }
}
