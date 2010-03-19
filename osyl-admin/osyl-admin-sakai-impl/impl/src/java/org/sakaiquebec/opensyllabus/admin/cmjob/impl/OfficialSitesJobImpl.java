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

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OfficialSitesJob;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OfficialSitesJobImpl implements OfficialSitesJob {

    private static Log log = LogFactory.getLog(OfficialSitesJobImpl.class);

    // TODO: Configure Value
    private String session = "20931";

    private Set<CourseSet> allCourseSets = null;

    private CourseSet aCourseSet = null;

    private Set<CourseOffering> courseOffs = null;

    private Set<Section> sections = null;
    /**
     *Course management service integration.
     */
    private CourseManagementService cmService;

    /**
     * @param cmService
     */
    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    /**
     * The site service used to create new sites: Spring injection
     */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code> needed to create a new site in Sakai.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    private ToolManager toolManager;

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	AcademicSession academicSession = cmService.getAcademicSession(session);
	String siteName = null;
	// Retrieve all the course sets
	allCourseSets = cmService.getCourseSets();

	System.out.println("on est dedans");
	for (CourseSet courseSet : allCourseSets) {

	    // Retrieve the course offerings
	    courseOffs =
		    cmService.getCourseOfferingsInCourseSet(courseSet.getEid());

	    if (courseOffs != null) {
		int i= 0;
		for (CourseOffering courseOff : courseOffs) {

		    // Check if we have the good session
		    if ((courseOff.getAcademicSession().getEid())
			    .equals(academicSession.getEid())) {

			// Retrieve the sections to be created
			sections = cmService.getSections(courseOff.getEid());

			if (sections != null) {
			    for (Section section : sections) {
				if (i < 3){
				    createSite(getSiteName(section), section);
				    i++;
				}
			    }
			}

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
	    courseIdBack = canCourseId.substring(5, 7);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else if (canCourseId.length() == 6) {
	    courseIdFront = canCourseId.substring(0, 1);
	    courseIdMiddle = canCourseId.substring(1, 4);
	    courseIdBack = canCourseId.substring(4, 6);
	    courseId =
		    courseIdFront + "-" + courseIdMiddle + "-" + courseIdBack;
	} else {
	    courseId = canCourseId;
	}

	if (canCourseId.matches("[^0-9]")) {
	    System.out.println("contient des lettres " + sectionId);
	    courseId = canCourseId;
	}
	sessionTitle = session.getTitle();

	if (sessionId.matches(".[p|P].")) {
	    periode = sessionId.substring(sessionId.length() - 2);
	}

	groupe = sectionId.substring(courseOffId.length());

	if (periode == null)
	    siteName = courseId + "_" + groupe + "_" + sessionTitle;
	else
	    siteName =
		    courseId + "_" + groupe + "_" + sessionTitle + "_"
			    + periode;

	return siteName;
    }

    private void createSite(String siteName, Section section) {

	if (siteService.siteExists(siteName))
	    System.out.println("Le site existe déjà");
	else {
	    try {
		enableSecurityAdvisor();
		Session s = SessionManager.getCurrentSession();
		s.setUserId(UserDirectoryService.ADMIN_ID);

		Site osylSite = siteService.addSite(siteName, "osylEditor");
		osylSite.setTitle(siteName);
		osylSite.setPublished(true);
		osylSite.setJoinable(false);

		// we add the tools
		addTool(osylSite, "sakai.opensyllabus.tool");
		addTool(osylSite, "sakai.assignment.grades");
		addTool(osylSite, "sakai.resources");
		addTool(osylSite, "sakai.siteinfo");

		osylSite.setProviderGroupId(section.getEid());
		
		//TODO: Add properties from the course management to the site
		siteService.save(osylSite);

	    } catch (IdInvalidException e) {
		e.printStackTrace();
	    } catch (IdUsedException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (IdUnusedException e) {
		e.printStackTrace();
	    }

	}

    }

    public void addTool(Site site, String toolId) {
	SitePage page = site.addPage();
	Tool tool = toolManager.getTool(toolId);
	page.setTitle(tool.getTitle());
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	ToolConfiguration toolConf = page.addTool();
	toolConf.setTool(toolId, tool);
	toolConf.setTitle(tool.getTitle());
	toolConf.setLayoutHints("0,0");

	try {
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    log.error("Add tool - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Add tool - Permission exception", e);
	}

    }

    private void enableSecurityAdvisor() {
	// put in a security advisor so we can create citationAdmin site without
	// need
	// of further permissions
	SecurityService.pushAdvisor(new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
		return SecurityAdvice.ALLOWED;
	    }
	});
    }
}
