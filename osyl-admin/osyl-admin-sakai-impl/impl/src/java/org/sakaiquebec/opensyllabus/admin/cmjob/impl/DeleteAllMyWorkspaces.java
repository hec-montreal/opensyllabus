package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

/**
 * Deletes all My Workspace sites.
 * 
 * @author Remi Saias
 *
 */
public class DeleteAllMyWorkspaces implements Job {

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(DeleteAllMyWorkspaces.class);

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

    // ***************** END SPRING INJECTION ************************//

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	long start = System.currentTimeMillis();
	log.info("DeleteAllMyWorkspaces: starting");
	
        loginToSakai();

	// check all course site realms
	List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, null,
			null, null, SiteService.SortType.NONE, null);

	log.info("filtering " + allSites.size() + " sites");

	Site site = null;
	int count = 0;
	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    if (! site.getId().startsWith("~")
		    || "~admin".equals(site.getId())) {
		log.trace("skipping " + site.getId());
		continue;
	    }
	    try {
		log.debug("remove " + site.getId());
		count++;
		siteService.removeSite(site);
	    } catch (Exception e) {
		log.error(e.getMessage());
	    }
	}

	log.info("deleted " + count + " workspace sites"); 
	log.info("completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	
	logoutFromSakai();
    } // execute

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1",
		"WorkspaceRemover");

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
	usageSessionService.logout();
    }

}
