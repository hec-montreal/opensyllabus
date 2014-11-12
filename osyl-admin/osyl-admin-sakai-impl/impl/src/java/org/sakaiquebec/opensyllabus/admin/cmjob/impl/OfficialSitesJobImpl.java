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
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.email.api.ContentType;
import org.sakaiproject.email.api.EmailAddress;
import org.sakaiproject.email.api.EmailAddress.RecipientType;
import org.sakaiproject.email.api.EmailMessage;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylCMJob;
import org.sakaiproject.component.cover.ServerConfigurationService;

import ca.hec.commons.utils.FormatUtils;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>, <a href="mailto:curtis.van-osch@hec.ca">Curtis van Osch</a>
 * @version $Id: $
 */
public class OfficialSitesJobImpl extends OsylAbstractQuartzJobImpl
	implements OfficialSitesJob {

	private static Log log = LogFactory.getLog(OfficialSitesJobImpl.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		log.info("start OfficialSitesJob");

		loginToSakai();

		// If we have a list of courses, we will create only those one. If we
		// don't we create any course defined in the given sessions
		List<String> courses = adminConfigService.getCourses();
		List<String> programs = adminConfigService.getPrograms();
		List<String> departments = adminConfigService.getServEns();
		List<String> sessions = adminConfigService.getSessions();
		Date startDate = adminConfigService.getStartDate();
		Date endDate = adminConfigService.getEndDate();

		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();

		// If user specified some sessions, retrieve them
		// If we have an interval of time, retrieve the sessions that fall within
		// else, retrieve all active sessions
		List<AcademicSession> academicSessions;
		if (sessions != null && sessions.size() > 0) {
			academicSessions = getSessions(sessions);
		} else if (startDate != null && endDate != null) {
			academicSessions = getSessions(startDate, endDate);
		} else {
			academicSessions = new ArrayList<AcademicSession>();
			academicSessions.addAll(cmService.getCurrentAcademicSessions());
		}

		log.info("Retrieve course offerings");
		if (courses != null && courses.size() > 0) {
			courseOfferings = getSpecifiedCourseOfferings(courses, academicSessions);
		}
		else if (programs != null && programs.size() > 0) {
			courseOfferings = getCourseOfferingsForPrograms(programs, academicSessions);
		}
		else if (departments != null && departments.size() > 0) {
			courseOfferings = getCourseOfferingsForDepartments(departments, academicSessions);
		}
		else {
			courseOfferings = getCourseOfferingsForSessions(academicSessions);
		}

		log.info("found " + courseOfferings.size() + " course offerings");

		for (CourseOffering co : courseOfferings) {
			String siteId = getSiteName(co);
			Set<Section> sections = cmService.getSections(co.getEid());

			if (siteService.siteExists(siteId)) {
				log.info(siteId + " already exists");
			} else if (sections == null || sections.size() < 1) {
				log.info(siteId + " has no sections");
			} else {
				log.info(siteId + " doesn't exists, create it and associate to CM CourseOffering");

				try {
					Site site = siteService.addSite(siteId, "course");
					site.setTitle(co.getTitle());
					site.setPublished(true);
					site.setJoinable(false);

					ResourcePropertiesEdit rp = site.getPropertiesEdit();
					rp.addProperty("term", co.getAcademicSession().getTitle());

					String providerGroupId = "";
					for (Section section : sections) {
						if (providerGroupId.length() > 0)
							providerGroupId += "+";
						providerGroupId += section.getEid();
					}

					if (providerGroupId.length() > 0)
						site.setProviderGroupId(providerGroupId);

					//add site info
					SitePage page = site.addPage();
					page.setTitle(toolManager.getTool("sakai.siteinfo").getTitle());
					page.setLayout(SitePage.LAYOUT_SINGLE_COL);
					page.addTool("sakai.siteinfo");

					siteService.save(site);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			}
		}
		logoutFromSakai();

		log.info("end OfficialSitesJob");
	}

	private Set<CourseOffering> getSpecifiedCourseOfferings(List<String> courses, List<AcademicSession> sessions)
	{
		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();
		for (String courseId : courses)
		{
			for (AcademicSession session : sessions)
			{
				String coId = courseId + session.getEid();
				if (cmService.isCourseOfferingDefined(coId))
					courseOfferings.add(cmService.getCourseOffering(coId));
			}
		}
		return courseOfferings;
	}

	private Set<CourseOffering> getCourseOfferingsForPrograms(List<String> programs, List<AcademicSession> sessions)
	{
		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();

		for (String program : programs)
		{
			for (AcademicSession session : sessions)
			{
				courseOfferings.addAll(
						cmService.findCourseOfferingsByAcadCareerAndAcademicSession(
								program,
								session.getEid()));
			}
		}
		return courseOfferings;
	}

	private Set<CourseOffering> getCourseOfferingsForDepartments(List<String> departments, List<AcademicSession> sessions)
	{
		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();

		Set<Section> allSections = new HashSet<Section>();
		String coffId = null;

		for (String department : departments) {
			allSections =
					cmService.findSectionsByCategory(department);
			if (allSections != null) {
				for (AcademicSession session : sessions) {

					for (Section section : allSections) {
						coffId = section.getCourseOfferingEid();
						CourseOffering coff = cmService.getCourseOffering(coffId);
						if (coffId != null
								&& coff.getAcademicSession().getEid()
								.equals(session.getEid()))
							courseOfferings.add(cmService
									.getCourseOffering(coffId));
					}
				}
			}
		}

		return courseOfferings;
	}

	private Set<CourseOffering> getCourseOfferingsForSessions(List<AcademicSession> sessions)
	{
		Set<CourseOffering> courseOfferings = new HashSet<CourseOffering>();

		// Retrieve all the course sets
		Set<CourseSet> allCourseSets = cmService.getCourseSets();

		for (CourseSet courseSet : allCourseSets) {

			for (AcademicSession session : sessions) {
				Set<CourseOffering> foundCourseOfferings =
						cmService.findCourseOfferings(courseSet.getEid(),
								session.getEid());
				courseOfferings.addAll(foundCourseOfferings);
			}
		}

		return courseOfferings;
	}

	private List<AcademicSession> getSessions(List<String> sessions) {
		List<AcademicSession> allAcademicSessions = cmService.getAcademicSessions();
		List<AcademicSession> academicSessions = new ArrayList<AcademicSession>();

		for (AcademicSession session : allAcademicSessions) {
			if (sessions.contains(session.getTitle())) {
				academicSessions.add(session);
			}
		}

		return academicSessions;
	}

	private List<AcademicSession> getSessions(Date startDate, Date endDate) {
		List<AcademicSession> sessions = new ArrayList<AcademicSession>();

		List<AcademicSession> allSessions = cmService.getAcademicSessions();
		Date sessionStartDate, sessionEndDate;
		for (AcademicSession session : allSessions) {
			sessionStartDate = session.getStartDate();
			sessionEndDate = session.getEndDate();
			if (startDate.compareTo(sessionStartDate) <= 0
					&& endDate.compareTo(sessionEndDate) >= 0)
				sessions.add(session);
		}

		return sessions;
	}

	private String getSiteName(CourseOffering offering) {
		AcademicSession session = offering.getAcademicSession();
		String sessionId = session.getEid();

		String sessionTitle = null;
		String period = null;

		String courseId = null;

		courseId = FormatUtils.formatCourseId(offering.getCanonicalCourseEid());
		sessionTitle = FormatUtils.getSessionName(sessionId);

		if (sessionId.matches(".*[pP].*")) {
			period = sessionId.substring(sessionId.length() - 2);
		}

		if (period == null)
			return courseId + "." + sessionTitle;
		else
			return courseId + "." + sessionTitle + "." + period;
	}

	/**
	 * Logs in the sakai environment
	 */
	protected void loginToSakai() {
		super.loginToSakai("OfficialSitesJob");
	}
}
