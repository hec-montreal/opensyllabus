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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.AddCMTitlePropertyJob;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class AddCMTitlePropertyJobImpl implements AddCMTitlePropertyJob {

	private List<Site> allSites;

	private final static String[] DEFAULT_TOOLS = { "sakai.opensyllabus.tool",
			"sakai.assignment.grades", "sakai.resources", "sakai.siteinfo" };

	private List<ToolConfiguration> siteTools = null;

	/**
	 * Our logger
	 */
	private static Log log = LogFactory.getLog(AddCMTitlePropertyJobImpl.class);

	/**
	 * The cms to be injected by Spring
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

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		loginToSakai();

		long start = System.currentTimeMillis();
		log.info("AddCMTitlePropertyJobImpl: starting");

		allSites = siteService.getSites(SiteService.SelectionType.ANY,
				"course", null, null, SiteService.SortType.NONE, null);

		Site site = null;

		log.info("AddCMTitlePropertyJobImpl: sites to correct:"
				+ allSites.size());

		for (int i = 0; i < allSites.size(); i++) {

			site = allSites.get(i);

			if (site.getProviderGroupId() != null) {
				Section section = cmService.getSection(site
						.getProviderGroupId());
				ResourcePropertiesEdit rpr = site.getPropertiesEdit();
				rpr.addProperty("title", section.getTitle());
				try {
					siteTools = getSiteTools(site);
					validateTools(site);
					siteService.save(site);
				} catch (IdUnusedException e) {
					log.info("The site " + site.getTitle() + " does not exist.");
				} catch (PermissionException e) {
					log.info("You are not allowed to update the site "
							+ site.getTitle());
				}

				log.info("The site " + site.getTitle() + " has been upgraded");
			}

		}

		log.info("AddCMTitlePropertyJobImpl: completed in " 
			+ (System.currentTimeMillis() - start) + " ms");
		logoutFromSakai();

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

	private List<ToolConfiguration> getSiteTools(Site site) {
		List pages = new Vector(site.getPages());
		List tools = new ArrayList();

		for (Iterator p = pages.iterator(); p.hasNext();) {
			SitePage page = (SitePage) p.next();
			tools.addAll(page.getTools());
		}

		return tools;
	}

	private void validateTools(Site site) {
		List<ToolConfiguration> newSiteTools = getSiteTools(site);

		if ((siteTools.size() != newSiteTools.size())
				|| (newSiteTools.size() == 0)) {
			String[] toolsToAdd;
			if (siteTools.size() == 0)
				toolsToAdd = DEFAULT_TOOLS;
			else {
				toolsToAdd = new String[siteTools.size()];
				int i = 0;
				for (ToolConfiguration tool : siteTools) {
					toolsToAdd[i++] = tool.getToolId();
				}
			}

			for (int i = 0; i < toolsToAdd.length; i++) {
				if (site.getTool(toolsToAdd[i]) == null)
					addTool(site, toolsToAdd[i]);
			}
			try {
				siteService.save(site);
			} catch (IdUnusedException e) {
				log.info(e.getMessage());
			} catch (PermissionException e) {
				log.info(e.getMessage());
			}
		}
	}

	public void addTool(Site site, String toolId) {
		SitePage page = site.addPage();
		Tool tool = ToolManager.getTool(toolId);
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

}
