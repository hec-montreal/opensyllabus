package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
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

/**
 * This job takes charge of adding a new "Home" page to sites that don't have
 * it yet. This page contains two tools: Announcement and MOTD. 
 * 
 * @author Rémi Saïas
 *
 */
public class AddHomePageToolImpl implements Job {

    private final static String SITE_TYPE = "course";

    private List<Site> allSites;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(AddHomePageToolImpl.class);

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

    private final static String[] TOOLS_BEFORE = {
	"sakai.opensyllabus.tool",
	"sakai.assignment.grades", "sakai.resources", "sakai.siteinfo" };

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();

	allSites =
		siteService.getSites(SiteService.SelectionType.ANY, SITE_TYPE,
			null, null, SiteService.SortType.NONE, null);

	Site site = null;

	try {
	    int updated = 0;
	    for (int i = 0; i < allSites.size(); i++) {

		// TODO: should we filter sites for winter 2011

		site = allSites.get(i);
		log.debug("AddHomePageToolImpl: processing site [" + site.getTitle() + "]");
		if (site != null) {
		    try {
			// TODO: we should get the language of the site and
			//       customize the content accordingly
			if (addTools(site)) {
			    updated++;
			}

			// Move page OpenSyllabus in 3rd position
			SitePage osylPage = getOsylPage(site);
			if (osylPage != null) {
			    osylPage.setPosition(2);
			} else {
			    log.debug("osylPage null");
			}

		    } catch (Exception e) {
			log.equals("AddHomePageToolImpl.execute: "  + e);
			e.printStackTrace();
		    }
		}
	    }
	} catch (Exception e){
	    log.error("Job AddHomePageToolImpl failed: " + e);
	    e.printStackTrace();
	} finally {
	    logoutFromSakai();
	}
    }

    /**
     * Iterates through all pages from the specified site and return the list
     * of tools.
     * 
     * @param site
     * @return
     */
    private List<ToolConfiguration> getSiteTools(Site site) {
	List<SitePage> pages = new Vector<SitePage>(site.getPages());
	List<ToolConfiguration> tools = new ArrayList<ToolConfiguration>();

	for (Iterator<SitePage> p = pages.iterator(); p.hasNext();) {
	    SitePage page = (SitePage) p.next();
	    tools.addAll(page.getTools());
	}

	return tools;
    }

    /**
     * Iterates through all pages from the specified site and return the one
     * containing the OpenSyllabus tool.
     * 
     * @param site
     * @return
     */
    private SitePage getOsylPage(Site site) {
	List<SitePage> pages = new Vector<SitePage>(site.getPages());
	Iterator<ToolConfiguration> tools;
	ToolConfiguration cfg;
	Tool tool;
	SitePage page = null;
	
	for (Iterator<SitePage> p = pages.iterator(); p.hasNext();) {
	    page = (SitePage) p.next();
	    for (tools = page.getTools().iterator(); tools.hasNext();) {
		cfg = (ToolConfiguration) tools.next();
		tool = cfg.getTool();
		if ("sakai.opensyllabus.tool".equals(tool.getId())) {
		    return page;
		}
	    }
	}

	return null;
    }

    /**
     * Adds the tools as needed, if possible.
     * 
     * @param site
     */
    private boolean addTools(Site site) {
	int currentToolCount = getSiteTools(site).size();
	log.debug("addTools: site [" + site.getTitle() + "] tools:"
		+ currentToolCount);

	if (currentToolCount != TOOLS_BEFORE.length) {
	    // Oops unexpected situation (might also be a sharable site):
	    log.warn("addTools: site [" + site.getTitle() +
	    	"]: Unable to add tools (unexpected number of tools)");
	    return false;
	}
	
	// Add Home page and its 2 tools
	SitePage homePage = site.addPage();
	homePage.setupPageCategory(SitePage.HOME_TOOL_ID);
	homePage.setTitle("Accueil");
	homePage.setPosition(0);
	saveSite(site);
	
	// 1st tool
	ToolConfiguration synAnncCfg = addTool(homePage,
		"sakai.synoptic.announcement");
	synAnncCfg.setLayoutHints("0,0");
	Properties props = synAnncCfg.getPlacementConfig();
	props.put("days","31");
	synAnncCfg.save();

	// 2nd tool
	ToolConfiguration iframeCfg = addTool(homePage, "sakai.iframe");
	iframeCfg.setLayoutHints("1,0");
	iframeCfg.setTitle("Règlements de HEC Montréal");
	Properties iframeProps = iframeCfg.getPlacementConfig();
	iframeProps.put("height","400px");
	iframeProps.put("source","/library/content/reglements_H2011.html");
	iframeProps.put("reset.button","true");
	// instructors won't be able to change this iFrame unless they get
	// site.upd permission
	iframeCfg.save();
	
	// Add Announcements page
	SitePage anncPage = site.addPage();
	anncPage.setTitle("Annonces");
	anncPage.setPosition(1);
	saveSite(site);
	ToolConfiguration anncCfg = addTool(anncPage, "sakai.announcements");
	// Configure Announcements tool
	Properties anncProps = anncCfg.getPlacementConfig();
	anncProps.put("functions.require","annc.new");
	anncCfg.save();
	saveSite(site);

	return true;
    }


    private ToolConfiguration addTool(SitePage page, 
	    String toolId1) {
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	Tool tool1 = ToolManager.getTool(toolId1);
	ToolConfiguration tool1Conf = page.addTool(tool1);
	
	return tool1Conf;
    }
    
    private void saveSite(Site site) {
	log.debug("saveSite: " + site.getTitle());
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

}
