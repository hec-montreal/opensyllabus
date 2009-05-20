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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.admin.cm.impl;

import java.sql.Time;
import java.util.Date;
import java.util.Set;
import java.util.List;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * This class represents an implementation of the course management administration in the context
 * of OpenSyllabus. Only used methods are implemented.
 * 
 * TODO: implement necessary methods
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylCourseManagementAdministration implements CourseManagementAdministration{

    /** {@inheritDoc} */
    public void addCanonicalCourseToCourseSet(String courseSetEid,
	    String canonicalCourseEid) throws IdNotFoundException {
    }

    /** {@inheritDoc} */
    public void addCourseOfferingToCourseSet(String courseSetEid,
	    String courseOfferingEid) {
    }

    /** {@inheritDoc} */
    public Membership addOrUpdateCourseOfferingMembership(String userId,
	    String role, String courseOfferingEid, String status) {
	return null;
    }

    /** {@inheritDoc} */
    public Membership addOrUpdateCourseSetMembership(String userId,
	    String role, String courseSetEid, String status)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Enrollment addOrUpdateEnrollment(String userId,
	    String enrollmentSetEid, String enrollmentStatus, String credits,
	    String gradingScheme) {
	return null;
    }

    /** {@inheritDoc} */
    public Membership addOrUpdateSectionMembership(String userId, String role,
	    String sectionEid, String status) {
	return null;
    }

    /** {@inheritDoc} */
    public SectionCategory addSectionCategory(String categoryCode,
	    String categoryDescription) {
	return null;
    }

    /** {@inheritDoc} */
    public AcademicSession createAcademicSession(String eid, String title,
	    String description, Date startDate, Date endDate)
	    throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public CanonicalCourse createCanonicalCourse(String eid, String title,
	    String description) throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public CourseOffering createCourseOffering(String eid, String title,
	    String description, String status, String academicSessionEid,
	    String canonicalCourseEid, Date startDate, Date endDate)
	    throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public CourseSet createCourseSet(String eid, String title,
	    String description, String category, String parentCourseSetEid)
	    throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public EnrollmentSet createEnrollmentSet(String eid, String title,
	    String description, String category,
	    String defaultEnrollmentCredits, String courseOfferingEid,
	    Set officialInstructors) throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public Section createSection(String eid, String title, String description,
	    String category, String parentSectionEid, String courseOfferingEid,
	    String enrollmentSetEid) throws IdExistsException {
	return null;
    }

    /** {@inheritDoc} */
    public Meeting newSectionMeeting(String sectionEid, String location,
	    Time startTime, Time finishTime, String notes) {
	return null;
    }

    /** {@inheritDoc} */
    public void removeAcademicSession(String eid) {
    }

    /** {@inheritDoc} */
    public void removeCanonicalCourse(String eid) {
    }

    /** {@inheritDoc} */
    public boolean removeCanonicalCourseFromCourseSet(String courseSetEid,
	    String canonicalCourseEid) {
	return false;
    }

    /** {@inheritDoc} */
    public void removeCourseOffering(String eid) {
    }

    /** {@inheritDoc} */
    public boolean removeCourseOfferingFromCourseSet(String courseSetEid,
	    String courseOfferingEid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean removeCourseOfferingMembership(String userId,
	    String courseOfferingEid) {
	return false;
    }

    /** {@inheritDoc} */
    public void removeCourseSet(String eid) {
    }

    /** {@inheritDoc} */
    public boolean removeCourseSetMembership(String userId, String courseSetEid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean removeEnrollment(String userId, String enrollmentSetEid) {
	return false;
    }

    /** {@inheritDoc} */
    public void removeEnrollmentSet(String eid) {
    }

    /** {@inheritDoc} */
    public boolean removeEquivalency(CanonicalCourse canonicalCourse) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean removeEquivalency(CourseOffering courseOffering) {
	return false;
    }

    /** {@inheritDoc} */
    public void removeSection(String eid) {
    }

    /** {@inheritDoc} */
    public boolean removeSectionMembership(String userId, String sectionEid) {
	return false;
    }

    /** {@inheritDoc} */
    public void setEquivalentCanonicalCourses(Set canonicalCourses) {
    }

    /** {@inheritDoc} */
    public void setEquivalentCourseOfferings(Set courseOfferings) {
    }

    /** {@inheritDoc} */
    public void updateAcademicSession(AcademicSession academicSession) {
    }

    /** {@inheritDoc} */
    public void updateCanonicalCourse(CanonicalCourse canonicalCourse) {
    }

    /** {@inheritDoc} */
    public void updateCourseOffering(CourseOffering courseOffering) {
    }

    /** {@inheritDoc} */
    public void updateCourseSet(CourseSet courseSet) {
    }

    /** {@inheritDoc} */
    public void updateEnrollmentSet(EnrollmentSet enrollmentSet) {
    }

    /** {@inheritDoc} */
    public void updateSection(Section section) {
    }
    
    /** {@inheritDoc} */
    public void setCurrentAcademicSessions(List<String> currentAcademicSessions){
    }
}

