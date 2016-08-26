/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.common.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiquebec.opensyllabus.common.api.OsylDirectoryService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.helper.SchemaHelper;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylDirectoryServiceImpl implements OsylDirectoryService{

    /** The configDao to be injected by Spring */
    private COConfigDao configDao;

    /**
     * Sets the {@link COConfigDao}.
     *
     * @param configDao
     */
    public void setConfigDao(COConfigDao configDao) {
	this.configDao = configDao;
    }

    /** The resouceDao to be injected by Spring */
    private ResourceDao resourceDao;

    /**
     * Sets the {@link ResourceDao}.
     *
     * @param resourceDao
     */
    public void setResourceDao(ResourceDao resourceDao) {
	this.resourceDao = resourceDao;
    }

    private IdManager idManager;

    public void setIdManager(IdManager idManager) {
	this.idManager = idManager;
    }

    private SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    /**
     * Course management service integration.
     */
    private CourseManagementService cmService;

    /**
     * @param cmService
     */
    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    /** The site service to be injected by Spring */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code>.
     *
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }


    private SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

    private ToolManager toolManager;

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    private static final Log log = LogFactory.getLog( OsylDirectoryServiceImpl.class);


    public boolean createSite(String siteTitle, CanonicalCourse canCourse) throws Exception{

	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] creates site [" + siteTitle + "]");
	long start = System.currentTimeMillis();
	Site site = null;
	siteTitle = siteTitle.replaceAll(" ", "");
	if (!siteService.siteExists(siteTitle)) {

	    SecurityAdvisor advisor = new SecurityAdvisor() {
		public SecurityAdvice isAllowed(String userId, String function,
			String reference) {
		    return SecurityAdvice.ALLOWED;
		}
	    };

	    try {
		securityService.pushAdvisor(advisor);

		site = siteService.addSite(siteTitle, SITE_TYPE);
		    addTool(site, "sakai.opensyllabus.tool");
		    addTool(site, "sakai.siteinfo");

		site.setTitle(siteTitle);
		site.setPublished(true);
		site.setJoinable(false);

		siteService.save(site);

		//add least one course outline for the current session
		List <AcademicSession> sessions = cmService.getCurrentAcademicSessions();
		String courseOffId = null;
		for (AcademicSession session : sessions) {
		    courseOffId = canCourse.getEid() + session.getEid();
		    if (cmService.isCourseOfferingDefined(courseOffId)) {
			createCourseOutline(
				cmService.getCourseOffering(courseOffId),
				siteTitle);
		    }
		}
	    } catch (IdInvalidException e) {
		e.printStackTrace();
	    } catch (IdUsedException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } finally {
		securityService.popAdvisor();
	    }	} else {
		    log.error("Could not create directory site because site with title='"
			    + siteTitle + "' already exists");
		    throw new Exception(
			    "Could not create directory site because site with title='"
				    + siteTitle + "' already exists");
		}
		log.info("Directory site [" + siteTitle + "] created in "
			+ (System.currentTimeMillis() - start) + " ms ");

		    return false;
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylDirectory service");
    }

    private ToolConfiguration addTool(Site site,  String toolId) {

	Tool tool = toolManager.getTool(toolId);
	SitePage page = site.addPage();
	page.setTitle(toolManager.getTool(toolId).getTitle());
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	ToolConfiguration toolConf = page.addTool(tool);

	    toolConf.setTitle(tool.getTitle());

	toolConf.setLayoutHints("0,0");
	try {
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    log.error("Add tool - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Add tool - Permission exception", e);
	}

	return toolConf;
    }

    public boolean createCourseOutline(CourseOffering courseOff, String siteId) {

	COConfigSerialized coConfig = null;
	COSerialized co = null;
	String lang = "en"; // Sakai community only (TODO: use static var)
	lang = courseOff.getLang(); // HEC ONLY SAKAI-2723

	String configPath =
		ServerConfigurationService.getString(
			"opensyllabus.configs.path", null);
	if (configPath == null)
	    configPath =
		    System.getProperty("catalina.home")
			    + File.separator + "webapps"
			    + File.separator + "osyl-editor-sakai-tool";

	SchemaHelper schemaHelper = new SchemaHelper(configPath);
	String version = schemaHelper.getSchemaVersion();

	try {
	    coConfig = configDao.getConfigByRef(CONFIG_REF);
	    co =
		    new COSerialized(idManager.createUuid(), lang,
			    "shared", "", siteId, "sectionId",
			    coConfig, null, "shortDescription",
			    "description", "title", false, null, null,
			    version);
	    resourceDao.createOrUpdateCourseOutline(co);
	} catch (Exception e) {
	    log.error("createCourse outline", e);
	}

	return false;
    }


}

