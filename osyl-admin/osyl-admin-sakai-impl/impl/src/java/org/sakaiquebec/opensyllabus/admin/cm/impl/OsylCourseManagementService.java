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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * This class represents a temporary implementation of the course management. It allows us to create 
 * sites that are section aware which will allow us to communicate other sakai tools
 * For now only used method are implemented.
 * 
 * TODO: implement necessary methods
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylCourseManagementService implements CourseManagementService{

    /** {@inheritDoc} */
    public Map<String, String> findCourseOfferingRoles(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseOffering> findCourseOfferings(String courseSetEid,
	    String academicSessionEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Map<String, String> findCourseSetRoles(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public List<CourseSet> findCourseSets(String category) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<EnrollmentSet> findCurrentlyEnrolledEnrollmentSets(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<EnrollmentSet> findCurrentlyInstructingEnrollmentSets(
	    String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Section> findEnrolledSections(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Enrollment findEnrollment(String userEid, String enrollmentSetEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Section> findInstructingSections(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Section> findInstructingSections(String userEid,
	    String academicSessionEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Map<String, String> findSectionRoles(String userEid) {
	return null;
    }

    /** {@inheritDoc} */
    public AcademicSession getAcademicSession(String eid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public List<AcademicSession> getAcademicSessions() {
	return null;
    }

    /** {@inheritDoc} */
    public CanonicalCourse getCanonicalCourse(String canonicalCourseEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CanonicalCourse> getCanonicalCourses(String courseSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseSet> getChildCourseSets(String parentCourseSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Section> getChildSections(String parentSectionEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public CourseOffering getCourseOffering(String courseOfferingEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Membership> getCourseOfferingMemberships(String courseOfferingEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseOffering> getCourseOfferingsInCanonicalCourse(
	    String canonicalCourseEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseOffering> getCourseOfferingsInCourseSet(String courseSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public CourseSet getCourseSet(String courseSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Membership> getCourseSetMemberships(String courseSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseSet> getCourseSets() {
	return null;
    }

    /** {@inheritDoc} */
    public List<AcademicSession> getCurrentAcademicSessions() {
	return null;
    }

    /** {@inheritDoc} */
    public EnrollmentSet getEnrollmentSet(String enrollmentSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<EnrollmentSet> getEnrollmentSets(String courseOfferingEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Map<String, String> getEnrollmentStatusDescriptions(Locale locale) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Enrollment> getEnrollments(String enrollmentSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CanonicalCourse> getEquivalentCanonicalCourses(
	    String canonicalCourseEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<CourseOffering> getEquivalentCourseOfferings(
	    String courseOfferingEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Map<String, String> getGradingSchemeDescriptions(Locale locale) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<String> getInstructorsOfRecordIds(String enrollmentSetEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Map<String, String> getMembershipStatusDescriptions(Locale locale) {
	return null;
    }

    /** {@inheritDoc} */
    public Section getSection(String sectionEid) throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public List<String> getSectionCategories() {
	return null;
    }

    /** {@inheritDoc} */
    public String getSectionCategoryDescription(String categoryCode) {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Membership> getSectionMemberships(String sectionEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public Set<Section> getSections(String courseOfferingEid)
	    throws IdNotFoundException {
	return null;
    }

    /** {@inheritDoc} */
    public boolean isAcademicSessionDefined(String eid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isCanonicalCourseDefined(String eid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isCourseOfferingDefined(String eid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isCourseSetDefined(String eid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isEmpty(String courseSetEid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isEnrolled(String userEid, Set<String> enrollmentSetEids) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isEnrolled(String userEid, String enrollmentSetEid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isEnrollmentSetDefined(String eid) {
	return false;
    }

    /** {@inheritDoc} */
    public boolean isSectionDefined(String eid) {
	return false;
    }
}

