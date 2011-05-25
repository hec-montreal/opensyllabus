package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.AddHomePageToolJob;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * This job takes charge of adding a new "Home" page to sites that don't have it
 * yet. This page contains two tools: Announcement and MOTD.
 * 
 * @author Rémi Saïas
 */
public class AddHomePageToolJobImpl extends OsylAbstractQuartzJobImpl implements
	AddHomePageToolJob {

    private List<Site> allSites;

    private String xsl;

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(AddHomePageToolJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	log.info("Start adding home pages to sites");
	long start = System.currentTimeMillis();
	loginToSakai();

	String configPath =
		ServerConfigurationService
			.getString(OSYL_CONFIG_PATH_KEY, null);
	if (configPath == null)
	    configPath =
		    System.getProperty("catalina.home") + File.separator
			    + "webapps" + File.separator
			    + "osyl-admin-sakai-tool";// Ugly but don't know
	// cleaner method.
	String xslPath =
		configPath + File.separator + OsylSiteService.XSLT_DIRECTORY
			+ File.separator + "news.xslt";
	xsl = FileHelper.getFileContent(xslPath);

	allSites =
		siteService.getSites(SiteService.SelectionType.ANY, SITE_TYPE,
			null, null, SiteService.SortType.NONE, null);

	Site site;

	try {
	    int updated = 0;
	    for (int i = 0; i < allSites.size(); i++) {

		site = allSites.get(i);

		// change news and regulations to news
		if (site.getTitle().indexOf(".H2011") != -1) {
		    changeNewsAndRegulationsToNewsForSite(site.getId());
		}

		// we only process specific sites for winter 2011
		if (site.getTitle().indexOf(".H2011.") == -1) {
		    continue;
		}
		log.debug("processing site [" + site.getTitle() + "]");
		String locale = getSiteLocale(site);

		if (site != null) {
		    try {
			if (addTools(site, locale)) {
			    updated++;
			}

			// Move page OpenSyllabus in 3rd position
			SitePage osylPage = getOsylPage(site);
			if (osylPage != null) {
			    osylPage.setPosition(2);
			} else {
			    log.debug("osylPage null");
			}
			log.info("upgraded site [" + site.getTitle() + "]");

		    } catch (Exception e) {
			log.error("execute: site.getId()" + e);
			e.printStackTrace();
		    }
		}

	    }
	    log.info("Finished adding home pages in "
		    + (System.currentTimeMillis() - start) + " ms");
	} catch (Exception e) {
	    log.error("Job AddHomePageToolImpl failed: " + e);
	    e.printStackTrace();
	} finally {
	    logoutFromSakai();
	}
    }

    private String getSiteLocale(Site site) {
	COSerialized co;
	String locale;
	try {
	    co = resourceDao.getSerializedCourseOutlineBySiteId(site.getId());
	    locale = co.getLang();
	} catch (Exception e) {
	    locale = DEFAULT_LOCALE;
	    log.warn("getSiteLocale: " + e + " site [" + site.getId()
		    + "]: defaulting to " + locale);
	    e.printStackTrace();
	}
	return locale;
    }

    /**
     * Iterates through all pages from the specified site and return the list of
     * tools.
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
     * @param locale
     */
    private boolean addTools(Site site, String locale) {
	int currentToolCount = getSiteTools(site).size();

	// ####################################################################################
	// TODO: peut-on vérifier si c'est un spécifique?
	// ####################################################################################

	if (currentToolCount != TOOLS_BEFORE.length) {
	    // Oops unexpected situation (might also be a sharable site):
	    log.warn("addTools: site [" + site.getTitle()
		    + "]: Unable to add tools (unexpected number of tools: "
		    + currentToolCount + ")");
	    return false;
	}

	// Add Home page and its 2 tools
	SitePage homePage = site.addPage();
	homePage.setupPageCategory(SitePage.HOME_TOOL_ID);
	homePage.setTitle("Accueil");
	homePage.setPosition(0);
	saveSite(site);

	// 1st tool
	ToolConfiguration synAnncCfg =
		addTool(homePage, "sakai.synoptic.announcement");
	synAnncCfg.setLayoutHints("0,0");
	Properties props = synAnncCfg.getPlacementConfig();
	props.put("days", "31");
	synAnncCfg.save();

	// 2nd tool
	String toolTitle;
	if (Locale.CANADA_FRENCH.toString().equals(locale)) {
	    toolTitle = OsylSiteService.HEC_MONTREAL_RULES_TITLE_FR_CA;
	} else {
	    toolTitle = OsylSiteService.HEC_MONTREAL_RULES_TITLE_EN;
	}
	ToolConfiguration iframeCfg =
		addTool(homePage, "sakai.iframe", toolTitle);
	iframeCfg.setLayoutHints("1,0");

	Properties iframeProps = iframeCfg.getPlacementConfig();
	iframeProps.put("height", "400px");
	// instructors won't be able to change this iFrame unless they get
	// site.upd permission
	iframeProps.put("source",
		OsylSiteService.HEC_MONTREAL_RULES_FILE_BASE_NAME + locale
			+ OsylSiteService.HEC_MONTREAL_RULES_FILE_EXTENSION);
	iframeProps.put("reset.button", "true");
	iframeCfg.save();

	// Add Announcements page
	SitePage anncPage = site.addPage();
	anncPage.setTitle("Annonces");
	anncPage.setPosition(1);
	addTool(anncPage, "sakai.announcements");
	saveSite(site);

	return true;
    }

    private ToolConfiguration addTool(SitePage page, String toolId) {
	return addTool(page, toolId, null);
    }

    private ToolConfiguration addTool(SitePage page, String toolId,
	    String specifiedTitle) {
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	Tool tool = toolManager.getTool(toolId);
	ToolConfiguration toolConf = page.addTool(tool);
	if (specifiedTitle != null) {
	    toolConf.setTitle(specifiedTitle);
	} else {
	    toolConf.setTitle(tool.getTitle());
	}

	return toolConf;
    }

    private void saveSite(Site site) {
	log.trace("saveSite: " + site.getTitle());
	try {
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    log.error("Add tool - Unused id exception", e);
	} catch (PermissionException e) {
	    log.error("Add tool - Permission exception", e);
	}
    }

    private void changeNewsAndRegulationsToNewsForSite(String siteId) {

	List<COSerialized> list = resourceDao.getCourseOutlinesFoSite(siteId);
	for (COSerialized cos : list) {
	    String xml = cos.getContent();
	    try {
		xml = XmlHelper.applyXsl(xml, xsl);
		cos.setContent(xml);
		resourceDao.createOrUpdateCourseOutline(cos);
	    } catch (Exception e) {
		log.error("could not update co to change news and"
			+ " regulation to news [" + siteId + "]", e);
	    }
	}
    }

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	super.loginToSakai("AddHomePageToolJob");
    }
}
