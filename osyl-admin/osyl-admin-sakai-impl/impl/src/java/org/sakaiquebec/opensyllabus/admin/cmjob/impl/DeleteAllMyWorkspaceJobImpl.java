package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.DeleteAllMyWorkspaceJob;

/**
 * Deletes all My Workspace sites.
 * 
 * @author Remi Saias
 */
public class DeleteAllMyWorkspaceJobImpl extends OsylAbstractQuartzJobImpl
	implements DeleteAllMyWorkspaceJob {

    /**
     * Our logger
     */
    protected static Log log = LogFactory
	    .getLog(DeleteAllMyWorkspaceJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	long start = System.currentTimeMillis();
	log.info("DeleteAllMyWorkspaces: starting");

	loginToSakai();

	// check all course site realms
	List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, null, null,
			null, SiteService.SortType.NONE, null);

	log.info("filtering " + allSites.size() + " sites");

	Site site = null;
	int count = 0;
	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    if (!site.getId().startsWith("~") || "~admin".equals(site.getId())) {
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
	log.info("completed in " + (System.currentTimeMillis() - start) + " ms");

	logoutFromSakai();
    } // execute

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	super.loginToSakai("DeleteAllMyWorkspaceJob");
    }
}
