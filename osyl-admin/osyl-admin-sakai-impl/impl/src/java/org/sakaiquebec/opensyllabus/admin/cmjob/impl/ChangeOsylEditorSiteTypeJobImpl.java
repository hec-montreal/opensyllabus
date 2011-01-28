package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;

public class ChangeOsylEditorSiteTypeJobImpl implements Job {

    private List<Site> allSites;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(ChangeOsylEditorSiteTypeJobImpl.class);

    // ***************** SPRING INJECTION ************************//
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

    private AuthzGroupService authzGroupService;

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    private EventTrackingService eventTrackingService;

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }

    private UsageSessionService usageSessionService;

    public void setUsageSessionService(UsageSessionService usageSessionService) {
	this.usageSessionService = usageSessionService;
    }

    private SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    private ToolManager toolManager;

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    // ***************** END SPRING INJECTION ************************//

    private final static String NEW_SITE_TYPE = "course";

    private final static String SITE_TYPE = "osylEditor";

    private final static String[] DEFAULT_TOOLS =
	    { "sakai.opensyllabus.tool", "sakai.assignment.grades",
		    "sakai.resources", "sakai.siteinfo" };

    private List<ToolConfiguration> siteTools = null;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();

	allSites =
		siteService.getSites(SiteService.SelectionType.ANY, SITE_TYPE,
			null, null, SiteService.SortType.NONE, null);

	Site site = null;

	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    if (site != null) {
		try {
		    siteTools = getSiteTools(site);
		    site.setType(NEW_SITE_TYPE);

		    siteService.save(site);
		    validateTools(site);
		} catch (IdUnusedException e) {
		    log.error(e.getMessage());
		} catch (PermissionException e) {
		    log.error(e.getMessage());
		}
	    }
	}
	logoutFromSakai();
    }

    @SuppressWarnings("unchecked")
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

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1", "CMSync");

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }

}
