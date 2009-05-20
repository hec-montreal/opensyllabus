package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.SectionCategory;

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
 * This class contains all methods needed to connect our institutional system to
 * the course management of sakai, we use to provide users, courses, sessions,
 * teachers. This class can be used to implement the methods to provide the job
 * used to populate tables and a service to request or send information to the
 * course management.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylCMJob extends Job {

    /**
     * Method used to create the sessions
     */
    public void loadSessions();

    /**
     * Method used to create users
     */
    public void loadUsers();

    /**
     * Method used to load the courses
     */
    public void loadCourses();

    /**
     * This method allows us to create a new session without having an id
     * 
     * @param title
     * @param description
     * @param startDate
     * @param endDate
     */
    public void createSession(String title, String description, Date startDate,
	    Date endDate);

    /**
     * This method is used when we know the id of the session we are creating
     * 
     * @param eid
     * @param title
     * @param description
     * @param startDate
     * @param endDate
     */
    public void createSession(String eid, String title, String description,
	    Date startDate, Date endDate);

    /**
     * This method allow us to create a canonical course
     * 
     * @param title
     * @param description
     */
    public void createCanonicalCourse(String title, String description);

    /**
     * This method allow us to create a course offering without knowing the id
     * 
     * @param title
     * @param description
     * @param status
     * @param academicSessionEid
     * @param canonicalCourseEid
     * @param startDate
     * @param endDate
     */
    public void createCourseOffering(String title, String description,
	    String status, String academicSessionEid,
	    String canonicalCourseEid, Date startDate, Date endDate);

    /**
     * This method allows us to create a course offering with the given id
     * 
     * @param eid
     * @param title
     * @param description
     * @param status
     * @param academicSessionEid
     * @param canonicalCourseEid
     * @param startDate
     * @param endDate
     */
    public void createCourseOffering(String eid, String title,
	    String description, String status, String academicSessionEid,
	    String canonicalCourseEid, Date startDate, Date endDate);

    /**
     * Creates a canonical course with the given id
     * 
     * @param eid
     * @param title
     * @param description
     */
    public void createCanonicalCourse(String eid, String title,
	    String description);

    /**
     * Creates a course set without knowing the id
     * 
     * @param title
     * @param description
     * @param category
     * @param parentCourseSetEid
     */
    public void createCourseSet(String title, String description,
	    String category, String parentCourseSetEid);

    /**
     * Creates a course set with the given id
     * 
     * @param eid
     * @param title
     * @param description
     * @param category
     * @param parentCourseSetEid
     */
    public void createCourseSet(String eid, String title, String description,
	    String category, String parentCourseSetEid);

    /**
     * Creates a section
     * 
     * @param title
     * @param description
     * @param category
     * @param parentSectionEid
     * @param courseOfferingEid
     * @param enrollmentSetEid
     */
    public void createSection(String title, String description,
	    String category, String parentSectionEid, String courseOfferingEid,
	    String enrollmentSetEid);

    /**
     * Links a canonical course to the course set
     * 
     * @param courseSetEid
     * @param canonicalCourseEid
     */
    public void addCanonicalCourseToCourseSet(String courseSetEid,
	    String canonicalCourseEid);

    /**
     * Adds a new user to a course offering.
     * 
     * @param userId
     * @param role
     * @param courseOfferingEid
     * @param status
     */
    public void addCourseOfferingMembership(String userId, String role,
	    String courseOfferingEid, String status);

    /**
     * Adds a new user to the course set
     * 
     * @param userId
     * @param role
     * @param courseSetEid
     * @param status
     */
    public void addCourseSetMembership(String userId, String role,
	    String courseSetEid, String status);

    /**
     * Enrolls a new user
     * 
     * @param userId
     * @param enrollmentSetEid
     * @param enrollmentStatus
     * @param credits
     * @param gradingScheme
     */
    public void addEnrollment(String userId, String enrollmentSetEid,
	    String enrollmentStatus, String credits, String gradingScheme);

    /**
     * Assign a teacher to a course offering
     */
    public void assignTeachers();

    /**
     * Creates a new enrollmentSet for a course offering and the teachers linked
     * to it
     * 
     * @param eid
     * @param title
     * @param description
     * @param category
     * @param defaultEnrollmentCredits
     * @param courseOfferingEid
     * @param officialInstructors
     */
    public void createEnrollmentSet(String eid, String title,
	    String description, String category,
	    String defaultEnrollmentCredits, String courseOfferingEid,
	    Set officialInstructors);

    /**
     * Creates a new enrollmentSet with the given id for a course offering and
     * the teachers linked to it
     * 
     * @param title
     * @param description
     * @param category
     * @param defaultEnrollmentCredits
     * @param courseOfferingEid
     * @param officialInstructors
     */
    public void createEnrollmentSet(String title, String description,
	    String category, String defaultEnrollmentCredits,
	    String courseOfferingEid, Set officialInstructors);

    /**
     * Removes the link between a user and an enrollment
     * 
     * @param userId
     * @param enrollmentSetEid
     */
    public void dropEnrollment(String userId, String enrollmentSetEid);

    /**
     * Adds a category to a section
     * 
     * @param categoryCode
     * @param categoryDescription
     */
    public void addSectionCategory(String categoryCode,
	    String categoryDescription);

    /**
     * removes the link between a course set and a canonical course
     * 
     * @param courseSetEid
     * @param canonicalCourseEid
     */
    public void removeCanonicalCourseToCourseSet(String courseSetEid,
	    String canonicalCourseEid);

    /**
     * Removes a user membership to a course offering
     * 
     * @param userId
     * @param courseOfferingEid
     */
    public void removeCourseOfferingMembership(String userId,
	    String courseOfferingEid);

    /**
     * Removes a user membership to a course set
     * 
     * @param userId
     * @param courseSetEid
     */
    public void removeCourseSetMembership(String userId, String courseSetEid);

    /**
     * Deletes a section
     * 
     * @param eid
     */
    public void removeSection(String eid);

    /**
     * Deletes a session
     * 
     * @param eid
     */
    public void removeSession(String eid);

}
