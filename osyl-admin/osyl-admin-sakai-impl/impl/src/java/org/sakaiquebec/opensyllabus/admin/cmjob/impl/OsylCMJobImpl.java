package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
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
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.Constants;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailChargeFormationMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailChargeFormationMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.EtudiantCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.EtudiantCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailChargeFormationMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailSessionsMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericEtudiantCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericExamensMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericProfCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericProgrammeEtudesMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericSecretairesMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericServiceEnseignementMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProfCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProgrammeEtudesMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.ProgrammeEtudesMapEntry;
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

public class OsylCMJobImpl extends OsylAbstractQuartzJobImpl implements
	OsylCMJob {

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

    /**
     * Map used to store information about the charge de formation
     */
    private DetailChargeFormationMap chargeFormationMap = null;

    /**
     * Students currently registered as enrolled in the system
     */
    private Map<String, Enrollment> actualStudents;

    /**
     * Set of students currently registered as enrolled in the system
     */
    private HashSet<EnrollmentSet> actualEnrollmentSets;

    /**
     * Classes sections actually registered in the system
     */
    private Set<Section> actualCoursesSection;

    /**
     * Any person currently registed as member of a site in the system.
     */
    private HashMap<String, Membership> actualCourseMembers;

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(OsylCMJobImpl.class);

    /* load the instructors and assigns them to their courses */
    private void assignTeachers() {
	ProfCoursMapEntry profCoursEntry = null;
	String matricule = "";
	Iterator<DetailCoursMapEntry> cours;
	DetailCoursMapEntry detailsCours;
	Iterator<ProfCoursMapEntry> profCours =
		profCoursMap.values().iterator();
	String courseOfferingId = null;

	while (profCours.hasNext()) {
	    profCoursEntry = (ProfCoursMapEntry) profCours.next();
	    if (profCoursEntry == null) {
		log.warn("Null instructor entry");
		continue;
	    }
	    matricule = profCoursEntry.getEmplId();
	    cours = profCoursEntry.getCours();
	    while (cours.hasNext()) {
		detailsCours = (DetailCoursMapEntry) cours.next();

		EnrollmentSet enrollmentSet = null;
		Set<String> instructors = new HashSet<String>();
		String coordinator = null;

		// On a un enseignant
		// Add instructor to section
		String enrollmentSetId =
			getCourseSectionEnrollmentSetId(detailsCours);

		enrollmentSet = cmService.getEnrollmentSet(enrollmentSetId);
		enrollmentSet.setDefaultEnrollmentCredits(profCoursEntry
			.getUnitMinimum());
		instructors = enrollmentSet.getOfficialInstructors();
		if (instructors == null)
		    instructors = new HashSet<String>();
		if (matricule != null)
		    instructors.add(matricule);
		else {
		    log.warn("The course " + enrollmentSetId
			    + " does not have instructors");
		    // TODO: send mail also
		}
		enrollmentSet.setOfficialInstructors(instructors);
		cmAdmin.updateEnrollmentSet(enrollmentSet);
		log.info("Instructors for " + detailsCours.getUniqueKey()
			+ ": " + instructors.toString());

		if (detailsCours.getCoordonnateur() == null) {
		    // We don't have a coordinator: not expected!
		    log.warn("The course " + enrollmentSetId
			    + " does not have a coordinator");
		    // TODO: send mail also
		} else {
		    coordinator = detailsCours.getCoordonnateur().getEmplId();
		    // Add coordinator to course offering for CERTIFICAT
		    if (detailsCours.isInCertificat()
			    || detailsCours.isQualiteComm()) {

			courseOfferingId = getCourseOfferingId(detailsCours);
			cmAdmin.addOrUpdateCourseOfferingMembership(
				coordinator, COORDONNATEUR_ROLE,
				courseOfferingId, ACTIVE_STATUS);
			actualCourseMembers.remove(coordinator
				+ courseOfferingId);
			log.info("Coordinators for "
				+ detailsCours.getUniqueKey() + ": "
				+ coordinator);
		    } else {
			// Add coordinator to sharable site for other courses
			enrollmentSetId =
				getCourseOfferingId(detailsCours)
					+ SHARABLE_SECTION;
			cmAdmin.addOrUpdateSectionMembership(coordinator,
				COORDONNATEUR_ROLE, enrollmentSetId,
				ACTIVE_STATUS);
			actualCourseMembers.remove(coordinator
				+ enrollmentSetId);
			log.info("Coordinators for "
				+ detailsCours.getUniqueKey() + ": "
				+ coordinator);

		    }
		}
	    }

	}
    }

    /* load the 'charge de formation' and assigns them to their courses */
    private void assignChargeFormation() {
	DetailChargeFormationMapEntry chargeFormationEntry = null;
	String matricule = "";
	Vector<DetailCoursMapEntry> cours;
	Iterator<DetailChargeFormationMapEntry> chargeFormation =
		chargeFormationMap.values().iterator();
	String courseOfferingId = null;

	while (chargeFormation.hasNext()) {
	    chargeFormationEntry =
		    (DetailChargeFormationMapEntry) chargeFormation.next();
	    matricule = chargeFormationEntry.getEmplId();
	    cours = chargeFormationEntry.getCours();
	    for (DetailCoursMapEntry detailsCours : cours) {

		courseOfferingId = getCourseOfferingId(detailsCours);
		cmAdmin.addOrUpdateCourseOfferingMembership(matricule,
			CHARGE_FORMATION_ROLE, courseOfferingId, ACTIVE_STATUS);
		actualCourseMembers.remove(matricule + courseOfferingId);
		log.info("Charge de Formation for "
			+ detailsCours.getUniqueKey() + ": " + matricule);

	    }

	}
    }

    /** {@inheritDoc} */
    public void loadCourses() {

	DetailCoursMapEntry coursEntry = null;
	String canonicalCourseId = "", courseOfferingId = "", courseSectionId =
		"", courseSetId = "";
	String description = "";
	String title = "";
	String lang = "";
	String career = "";
	AcademicSession session = null;
	String category;
	Set<CanonicalCourse> cc = new HashSet<CanonicalCourse>();
	Set<CourseOffering> courseOfferingSet = new HashSet<CourseOffering>();
	CanonicalCourse canCourse = null;

	Iterator<DetailCoursMapEntry> cours =
		detailCoursMap.values().iterator();

	while (cours.hasNext()) {
	    coursEntry = (DetailCoursMapEntry) cours.next();
	    canonicalCourseId = getCanonicalCourseId(coursEntry);
	    title = coursEntry.getCourseTitleLong();
	    description = coursEntry.getCourseTitleLong();
	    courseSetId = coursEntry.getAcadOrg();

	    // create the canonical course
	    if (!cmService.isCanonicalCourseDefined(canonicalCourseId)) {
		canCourse =
			cmAdmin.createCanonicalCourse(canonicalCourseId, title,
				description);
		cc.add(canCourse);
		cmAdmin.setEquivalentCanonicalCourses(cc);

	    } else {
		// we update
		canCourse = cmService.getCanonicalCourse(canonicalCourseId);
		canCourse.setDescription(description);
		canCourse.setTitle(title);
	    }

	    // Check wether there is a directory site
	    hasDirectorySite(canCourse);

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
		ProgrammeEtudesMapEntry programmeEtudesMapEntry =
			programmeEtudesMap.get(coursEntry.getAcadCareer());
		career = programmeEtudesMapEntry.getAcadCareer();
		
		if (!cmService.isCourseOfferingDefined(courseOfferingId)) {
		    courseOff =
			    cmAdmin.createCourseOffering(courseOfferingId,
				    title, description, COURSE_OFF_STATUS,
				    session.getEid(), canonicalCourseId,
				    session.getStartDate(),
				    session.getEndDate(), lang, career);
		    courseOfferingSet.add(courseOff);
		} else {
		    // We update
		    courseOff = cmService.getCourseOffering(courseOfferingId);
		    courseOff.setTitle(title);
		    courseOff.setDescription(description);
		    courseOff.setEndDate(session.getEndDate());
		    courseOff.setStartDate(session.getStartDate());
		    courseOff.setStatus(COURSE_OFF_STATUS);
		    courseOff.setAcademicSession(session);
		    courseOfferingSet.add(courseOff);
		    courseOff.setLang(lang);
		    courseOff.setAcademicCareer(career);
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
				    courseOfferingId, null, lang);

		} else {
		    // We update
		    newSection = cmService.getSection(courseSectionId);
		    newSection.setCategory(category);
		    newSection.setDescription(description);
		    newSection.setTitle(title);
		    newSection.setLang(lang);
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
					    description, courseSetId, CREDITS,
					    courseOfferingId,
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

	}

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

	if (!cmService.isSectionDefined(courseSectionId)) {

	    newSection =
		    cmAdmin.createSection(courseSectionId, title, description,
			    category, null, courseOfferingId, null, lang);

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
	    title = sessionEntry.getDescAnglais();
	    description = sessionEntry.getDescAnglais();
	    try {
		startDate =
			DateFormat.getDateInstance().parse(
				sessionEntry.getBeginDate());
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
	File secretairesFile = new File(directory, SECRETAIRES_FILE);
	File servensFile = new File(directory, SERV_ENS_FILE);
	File progEtudFile = new File(directory, PROG_ETUD_FILE);
	File chargeFormFile = new File(directory, CHARGE_FORMATION);

	if (sessionFile.exists() && coursFile.exists() && etudiantFile.exists()
		&& horairesFile.exists() && profFile.exists()
		&& secretairesFile.exists() && servensFile.exists()
		&& progEtudFile.exists() && chargeFormFile.exists()) {
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
	    emailService.send("admin.zonecours2@hec.ca",
		    "admin.zonecours2@hec.ca",
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
		    GenericProfCoursMapFactory.getInstance(directory
			    + File.separator + PROF_FILE);
	    profCoursMap =
		    GenericProfCoursMapFactory.buildMap(directory
			    + File.separator + PROF_FILE, detailCoursMap,
			    detailSessionMap);

	    secretairesMap =
		    GenericSecretairesMapFactory.getInstance(directory
			    + File.separator + SECRETAIRES_FILE);
	    secretairesMap =
		    GenericSecretairesMapFactory.buildMap(directory
			    + File.separator + SECRETAIRES_FILE);

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

	    chargeFormationMap =
		    GenericDetailChargeFormationMapFactory
			    .getInstance(directory + File.separator
				    + CHARGE_FORMATION);
	    chargeFormationMap =
		    GenericDetailChargeFormationMapFactory
			    .buildMap(directory + File.separator
				    + CHARGE_FORMATION, detailCoursMap);
	    // We first retrieve the current values in the system for the same
	    log.info("Finished reading extracts. Now updating the Course Management");
	    // time period as the extracts

	    retrieveCurrentCMContent();

	    
	    //We load academic careers
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

	    // We assign teachers and the secretaries
	    loadMembership();
	    log.info("Membership updated successfully");

	    // We synchronize students to their classes
	    syncEnrollments();
	    log.info("Enrollments updated successfully");

	} catch (Exception e) {
	    String message =
		    "Synchronization with PeopleSoft failed cause :\n"
			    + e.toString();
	    emailService.send("admin.zonecours2@hec.ca",
		    "admin.zonecours2@hec.ca",
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

	while (sessions.hasNext()) {
	    sessionEntry = sessions.next();
	    sessionId = sessionEntry.getUniqueKey();
	    if (cmService.isAcademicSessionDefined(sessionId)) {
		academicSession = cmService.getAcademicSession(sessionId);
		academicSessions.add(academicSession);
	    }
	}

	// Retrieve the course offerings we will update
	// Retrieve the students (enrollment ) we will update

	String enrollmentSetId = "", courseOfferingId = "";
	Set<CourseSet> courseSets = cmService.getCourseSets();
	String courseSetId = null;
	Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();
	Set<CourseOffering> courseOff = null;

	Set<EnrollmentSet> enrollmentSets = new HashSet<EnrollmentSet>();
	Set<EnrollmentSet> enrollmentS = null;

	Set<Section> sections = null;
	String sectionId = null;
	Set<Membership> memberships = null;

	actualStudents = new HashMap<String, Enrollment>();
	actualEnrollmentSets = new HashSet<EnrollmentSet>();
	actualCourseMembers = new HashMap<String, Membership>();
	actualCoursesSection = new HashSet<Section>();
	Set<Enrollment> enrollments = null;

	FileWriter outFile = null;
	PrintWriter out = null;
	try {
	    outFile = new FileWriter("out.txt");
	    out = new PrintWriter(outFile);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	for (CourseSet courseSet : courseSets) {
	    courseSetId = courseSet.getEid();
	    for (AcademicSession session : academicSessions) {
		sessionId = session.getEid();
		courseOff =
			cmService.findCourseOfferings(courseSetId, sessionId);
		courseOfferings.addAll(courseOff);

		for (CourseOffering course : courseOff) {
		    courseOfferingId = course.getEid();
		    // We retrieve the sections and the memberships in the
		    // sections
		    sections = cmService.getSections(courseOfferingId);
		    for (Section section : sections) {
			sectionId = section.getEid();
			actualCoursesSection.add(section);

			memberships =
				cmService.getSectionMemberships(sectionId);
			for (Membership member : memberships) {
			    actualCourseMembers.put(member.getUserId()
				    + sectionId, member);
			    if (out != null)
				out.println(member.getUserId() + "   "
					+ sectionId);
			}
		    }
		    // We retrieve the enrollments sets and enrollments
		    enrollmentS = cmService.getEnrollmentSets(courseOfferingId);
		    enrollmentSets.addAll(enrollmentS);
		    actualEnrollmentSets.addAll(enrollmentSets);

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
     * This method is used to load teachers, secretaries, interns .... For now
     * it is used just for teachers and secretaries. Each secretary will be
     * automatically added to all the courses of the service she is associated
     * to;
     */
    public void loadMembership() {
	assignChargeFormation();
	assignTeachers();
	syncSecretaries();
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

    int i = 0;

    // Add secretary directly to the course offering
    private void addSecretariesToMembership(List<String> secretaries,
	    List<DetailCoursMapEntry> courses) {
	String courseOfferingId = null;

	for (DetailCoursMapEntry course : courses) {
	    courseOfferingId = getCourseOfferingId(course);

	    log.info("Adding Secretaries for [" + course + "]: "
		    + secretaries.toString());

	    for (String secretary : secretaries) {
		cmAdmin.addOrUpdateCourseOfferingMembership(secretary,
			SECRETARY_ROLE, courseOfferingId, ACTIVE_STATUS);
		actualCourseMembers.remove(secretary + courseOfferingId);

	    }
	}

    }

    /*
     * Loads the secretaries and assigns them to the courses in their associated
     * SE.
     */
    private void syncSecretaries() {
	i = 0;
	SecretairesMapEntry entry = null;
	String deptId = null;
	String acadOrg = null;
	List<String> secretaries = null;
	List<DetailCoursMapEntry> cours = null;
	ServiceEnseignementMapEntry servEnsEntry = null;

	Set<String> keys = servEnsMap.keySet();

	// les cours du certificat
	secretaries = secretairesMap.getSecretairesCertificat();
	cours = detailCoursMap.getCoursByAcadCareer(CERTIFICAT);

	addSecretariesToMembership(secretaries, cours);
	// /////////////////////////////////////////

	// les cours qui ne sont pas du certificat
	for (String key : keys) {
	    servEnsEntry = servEnsMap.get(key);
	    deptId = servEnsEntry.getDeptId();
	    acadOrg = servEnsEntry.getAcadOrg();

	    secretaries =
		    secretairesMap.getSecretairesByAcadOrg(Integer
			    .parseInt(deptId));
	    cours = detailCoursMap.getNonCERTFCoursByAcadOrg(acadOrg);

	    addSecretariesToMembership(secretaries, cours);
	}
	// ////////////////////////////////////////

	Set<String> acmKeys = actualCourseMembers.keySet();
	Membership member = null;
	String sectionId = null;
	String userId = null;
	for (String key : acmKeys) {
	    member = actualCourseMembers.get(key);
	    userId = member.getUserId();
	    sectionId = key.substring(member.getUserId().length());
	    cmAdmin.removeSectionMembership(userId, sectionId);
	    log.info("L'utilisateur " + userId + " n'est plus membre du cours "
		    + sectionId);
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
		exists = osylDirectoryService.createSite(siteTitle, canCourse);

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

}
