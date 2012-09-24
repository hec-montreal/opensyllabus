package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.DeleteAllMyWorkspaceJob;
import org.sakaiproject.component.cover.ServerConfigurationService;


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
    private static Log log = LogFactory
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
	StringBuffer sb = new StringBuffer();
	int count = 0;

	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    if (!site.getId().startsWith("~") || "~admin".equals(site.getId())) {
		log.trace("skipping " + site.getId());
		continue;
	    }
	    try {
		if (!site.getTools("sakai.opensyllabus.manager.tool").isEmpty()) {
		    User user =
			    userDirectoryService.getUser(site.getId()
				    .substring(1));
		    if ("student".equals(user.getType())
			    || "guest".equals(user.getType())) {
			sb.append(user.getFirstName()+" "+user.getLastName()+"("+user.getEid()+") id:"+user.getId() + "\n");
		    }
		}
		log.debug("remove " + site.getId());
		count++;
		siteService.removeSite(site);
	    } catch (Exception e) {
		log.error(e.getMessage());
	    }
	}
	// send GDSC users
	emailService.send(getZoneCours2EMail(), getZoneCours2EMail(),
		"Liste des utilisateurs du GDSC", sb.toString(), null, null,
		null);

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
    
    private String getZoneCours2EMail(){
	return ServerConfigurationService.getString("mail.zc2");
    }

}
