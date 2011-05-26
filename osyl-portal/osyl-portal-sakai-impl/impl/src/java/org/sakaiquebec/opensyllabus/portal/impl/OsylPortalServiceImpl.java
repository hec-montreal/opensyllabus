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

package org.sakaiquebec.opensyllabus.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.portal.api.OsylPortalService;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylPortalServiceImpl implements OsylPortalService {

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(OsylPortalServiceImpl.class);

    private CourseManagementService courseManagementService;

    public void setCourseManagementService(
	    CourseManagementService courseManagementService) {
	this.courseManagementService = courseManagementService;
    }

    private OsylSiteService osylSiteService;

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    /**
     * Init method called right after Spring injection.
     */
    public void init() {

    }

    @Override
    public List<COSite> getCoursesForAcadCareer(String acadCareer) {
	List<COSite> coursesList = new ArrayList<COSite>();
	List<String> coursesName = new ArrayList<String>();
	Set<CourseOffering> courseOfferings =
		courseManagementService
			.findCourseOfferingsByAcadCareer(acadCareer);

	for (CourseOffering courseOffering : courseOfferings) {
	    if (!coursesName.contains(courseOffering.getCanonicalCourseEid())) {
		COSite coSite = fillCOSiteWithCourseOffering(courseOffering);
		coursesList.add(coSite);
		coursesName.add(courseOffering.getCanonicalCourseEid());
	    }
	}
	return coursesList;
    }

    @Override
    public List<COSite> getCoursesForResponsible(String responsible) {
	List<String> coursesName = new ArrayList<String>();
	List<COSite> coursesList = new ArrayList<COSite>();
	Set<Section> sections =
		courseManagementService.findSectionsByCategory(responsible);
	for (Section section : sections) {
	    CourseOffering courseOffering =
		    courseManagementService.getCourseOffering(section
			    .getCourseOfferingEid());
	    if (!coursesName.contains(courseOffering.getCanonicalCourseEid())) {
		COSite coSite =
			fillCOSiteWithCourseOffering(courseOffering);
		coursesList.add(coSite);
		coursesName.add(courseOffering.getCanonicalCourseEid());
	    }
	}
	return coursesList;
    }

    private COSite fillCOSiteWithCourseOffering(CourseOffering courseOffering) {
	COSite coSite = new COSite();
	coSite.setCourseNumber(getDirectorySiteName(courseOffering
		.getCanonicalCourseEid()));
	coSite.setCourseName(courseOffering.getTitle());
	Section shareable =
		courseManagementService.getSection(courseOffering.getEid()
			+ "00");
	if (shareable.getEnrollmentSet() != null) {
	    coSite.setCourseCoordinator((String) shareable.getEnrollmentSet()
		    .getOfficialInstructors().toArray()[0]);
	}
	coSite.setAcademicCareer(courseOffering.getAcademicCareer());
	coSite.setSiteDescription(courseOffering.getDescription());
	return coSite;
    }

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

    @Override
    public List<String> getAllResponsibles() {
	return courseManagementService.getSectionCategories();
    }

}
