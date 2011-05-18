package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.ChangeOsylEditorSiteTypeJob;

public class ChangeOsylEditorSiteTypeJobImpl extends OsylAbstractQuartzJobImpl
	implements ChangeOsylEditorSiteTypeJob {

    private List<Site> allSites;

    /**
     * Our logger
     */
    protected static Log log = LogFactory
	    .getLog(ChangeOsylEditorSiteTypeJobImpl.class);

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

    private List<ToolConfiguration> getSiteTools(Site site) {
	List<SitePage> pages = new Vector<SitePage>(site.getPages());
	List<ToolConfiguration> tools = new ArrayList<ToolConfiguration>();

	for (Iterator<SitePage> p = pages.iterator(); p.hasNext();) {
	    SitePage page = p.next();
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
	super.loginToSakai("ChangeOsylEditorSiteTypeJob");
    }
}
