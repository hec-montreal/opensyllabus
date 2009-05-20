package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserPermissionException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailCoursMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.DetailSessionsMapEntry;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.EtudiantCoursMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailCoursMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericDetailSessionsMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericEtudiantCoursMapFactory;
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
    private static final String EXTRACTS_PATH =
	    "webapps" + File.separator + "sakai-opensyllabus-admin-tool" + File.separator + "extracts";

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
    public void addCanonicalCourseToCourseSet(String courseSetEid,
	    String canonicalCourseEid) {
	cmAdmin.addCanonicalCourseToCourseSet(courseSetEid, canonicalCourseEid);
    }

    /** {@inheritDoc} */
    public void addCourseOfferingMembership(String userId, String role,
	    String courseOfferingEid, String status) {
	cmAdmin.addOrUpdateCourseOfferingMembership(userId, role,
		courseOfferingEid, status);
    }

    /** {@inheritDoc} */
    public void addCourseSetMembership(String userId, String role,
	    String courseSetEid, String status) {

    }

    /** {@inheritDoc} */
    public void addEnrollment(String userId, String enrollmentSetEid,
	    String enrollmentStatus, String credits, String gradingScheme) {

    }

    /** {@inheritDoc} */
    public void addSectionCategory(String categoryCode,
	    String categoryDescription) {

    }

    /** {@inheritDoc} */
    public void createCanonicalCourse(String title, String description) {
	cmAdmin.createCanonicalCourse(IdManager.createUuid(), title,
		description);

    }

    /** {@inheritDoc} */
    public void createCanonicalCourse(String eid, String title,
	    String description) {
	cmAdmin.createCanonicalCourse(eid, title, description);

    }

    /** {@inheritDoc} */
    public void createCourseOffering(String title, String description,
	    String status, String academicSessionEid,
	    String canonicalCourseEid, Date startDate, Date endDate) {
	cmAdmin.createCourseOffering(IdManager.createUuid(), title,
		description, status, academicSessionEid, canonicalCourseEid,
		startDate, endDate);
    }

    /** {@inheritDoc} */
    public void createCourseOffering(String eid, String title,
	    String description, String status, String academicSessionEid,
	    String canonicalCourseEid, Date startDate, Date endDate) {
	cmAdmin.createCourseOffering(eid, title, description, status,
		academicSessionEid, canonicalCourseEid, startDate, endDate);

    }

    /** {@inheritDoc} */
    public void createCourseSet(String title, String description,
	    String category, String parentCourseSetEid) {
	cmAdmin.createCourseSet(IdManager.createUuid(), title, description,
		category, parentCourseSetEid);

    }

    /** {@inheritDoc} */
    public void createCourseSet(String eid, String title, String description,
	    String category, String parentCourseSetEid) {
	cmAdmin.createCourseSet(eid, title, description, category,
		parentCourseSetEid);

    }

    /** {@inheritDoc} */
    public void createSection(String title, String description,
	    String category, String parentSectionEid, String courseOfferingEid,
	    String enrollmentSetEid) {

    }

    /** {@inheritDoc} */
    public void createSession(String title, String description, Date startDate,
	    Date endDate) {
	cmAdmin.createAcademicSession(IdManager.createUuid(), title,
		description, startDate, endDate);
    }

    /** {@inheritDoc} */
    public void createSession(String eid, String title, String description,
	    Date startDate, Date endDate) {
	cmAdmin.createAcademicSession(eid, title, description, startDate,
		endDate);
    }

    /** {@inheritDoc} */
    public void dropEnrollment(String userId, String enrollmentSetEid) {
	cmAdmin.removeEnrollment(userId, enrollmentSetEid);
    }

    /** {@inheritDoc} */
    public void assignTeachers() {
	ProfCoursMapEntry profCoursEntry = null;
	String cours = "";
	String matricule = "";
	int nbCours = 0;

	Iterator<ProfCoursMapEntry> profCours =
		profCoursMap.values().iterator();

	while (profCours.hasNext()) {
	    profCoursEntry = (ProfCoursMapEntry) profCours.next();
	    nbCours = profCoursEntry.getCoursCount();
	    matricule = profCoursEntry.getMatricule();
	}
    }

    /** {@inheritDoc} */
    public void loadCourses() {
	DetailCoursMapEntry coursEntry = null;
	String id = "";
	String description = "";
	String title = "";
	AcademicSession session = null;
	String status = "";

	Iterator<DetailCoursMapEntry> cours =
		detailCoursMap.values().iterator();

	while (cours.hasNext()) {
	    coursEntry = (DetailCoursMapEntry) cours.next();
	    id = coursEntry.getUniqueKey();
	    title = coursEntry.getTitre();
	    description = coursEntry.getTitreAlt();

	    // create course set
	    if (!cmService.isCourseSetDefined(id))
		createCourseSet(id, title, description, "Course", null);

	    // create the canonical course
	    if (!cmService.isCanonicalCourseDefined(id)) {
		createCanonicalCourse(id, title, description);
		// link canonical course to course set
		cmAdmin.addCanonicalCourseToCourseSet(id, id);
	    }

	    // create course offering
	    session = cmService.getAcademicSession(coursEntry.getSession());
	    if (session != null) {
		status = "Course";
		if (!cmService.isCourseOfferingDefined(id)) {
		    createCourseOffering(id, title, description, status,
			    session.getEid(), id, session.getStartDate(),
			    session.getEndDate());
		    cmAdmin.addCourseOfferingToCourseSet(id, id);
		    System.out.println("le cours: " + title + " la session "
			    + session.getTitle());
		}
	    }

	}
    }

    /** {@inheritDoc} */
    public void loadSessions() {
	DetailSessionsMapEntry sessionEntry = null;
	String eid = "", title = "", description = "";
	Date startDate = null, endDate = null;
	Iterator<DetailSessionsMapEntry> sessions =
		detailSessionMap.values().iterator();

	while (sessions.hasNext()) {
	    sessionEntry = (DetailSessionsMapEntry) sessions.next();
	    eid = sessionEntry.getNumero();
	    title = sessionEntry.getShortForm();
	    description = sessionEntry.getLongForm();
	    try {
		startDate =
			DateFormat.getDateInstance().parse(
				sessionEntry.getStartDate());
		endDate =
			DateFormat.getDateInstance().parse(
				sessionEntry.getEndDate());
	    } catch (ParseException e) {
		e.printStackTrace();
	    }

	    if (!cmService.isAcademicSessionDefined(eid)) {
		createSession(eid, title, description, startDate, endDate);
		// this method is not available for sakai 2.5
		// you can activate a session manually by changing to 1
		// the value of is_current in the cm_academic_session_t
		// TODO: activate this method when running again on sakai 2.6
		//cmAdmin.setCurrentAcademicSessions(Arrays
		//	.asList(new String[] { eid }));
	    }
	}
    }

    /** {@inheritDoc} */
    public void loadUsers() {
	Iterator<MatriculeNomMapEntry> values = matNomMap.values().iterator();
	MatriculeNomMapEntry entry;
	UserEdit userEdit = null;
	Collection userExists = null;

	while (values.hasNext()) {
	    entry = (MatriculeNomMapEntry) values.next();
	    try {
		// We check if user already in table
		userExists =
			userDirService
				.findUsersByEmail(entry.getEmailAddress());
		if (userExists.size() == 0)
		    userEdit =
			    userDirService.addUser(null, entry.getMatricule());

	    } catch (UserIdInvalidException e) {
		log.error("Create users - user invalid id", e);
	    } catch (UserAlreadyDefinedException e) {
		log.warn("Create users - user already defined");
	    } catch (UserPermissionException e) {
		log.error("Create users - permission exception", e);
	    }

	}
    }

    /** {@inheritDoc} */
    public void removeCanonicalCourseToCourseSet(String courseSetEid,
	    String canonicalCourseEid) {

    }

    /** {@inheritDoc} */
    public void removeCourseOfferingMembership(String userId,
	    String courseOfferingEid) {

    }

    /** {@inheritDoc} */
    public void removeCourseSetMembership(String userId, String courseSetEid) {

    }

    /** {@inheritDoc} */
    public void removeSection(String eid) {

    }

    /** {@inheritDoc} */
    public void removeSession(String eid) {

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

	String directory =
		System.getProperty("catalina.home");
	return directory + File.separator + EXTRACTS_PATH;

    }

    /** {@inheritDoc} */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();
	System.out.println(getPathToExtracts());

	String directory = getPathToExtracts();
	try {
	    detailSessionMap =
		    GenericDetailSessionsMapFactory.getInstance(directory);
	    detailSessionMap =
		    GenericDetailSessionsMapFactory.buildMap(directory);
	    loadSessions();

	    matNomMap = GenericMatriculeNomMapFactory.getInstance(directory);
	    matNomMap = GenericMatriculeNomMapFactory.buildMap(directory);
	    loadUsers();

	    detailCoursMap =
		    GenericDetailCoursMapFactory.getInstance(directory);
	    detailCoursMap = GenericDetailCoursMapFactory.buildMap(directory);
	    loadCourses();

	    profCoursMap = GenericProfCoursMapFactory.getInstance(directory);
	    profCoursMap =
		    GenericProfCoursMapFactory.buildMap(directory,
			    detailCoursMap, detailSessionMap);

	    etudCoursMap =
		    GenericEtudiantCoursMapFactory.getInstance(directory);
	    etudCoursMap =
		    GenericEtudiantCoursMapFactory.buildMap(directory,
			    detailCoursMap, detailSessionMap);

	} catch (IOException e) {
	    e.printStackTrace();
	}

	logoutFromSakai();
    }

    /** {@inheritDoc} */
    public void createEnrollmentSet(String eid, String title,
	    String description, String category,
	    String defaultEnrollmentCredits, String courseOfferingEid,
	    Set officialInstructors) {
	cmAdmin.createEnrollmentSet(eid, title, description, category,
		defaultEnrollmentCredits, courseOfferingEid,
		officialInstructors);

    }

    /** {@inheritDoc} */
    public void createEnrollmentSet(String title, String description,
	    String category, String defaultEnrollmentCredits,
	    String courseOfferingEid, Set officialInstructors) {
	cmAdmin.createEnrollmentSet(IdManager.createUuid(), title, description,
		category, defaultEnrollmentCredits, courseOfferingEid,
		officialInstructors);

    }

}
