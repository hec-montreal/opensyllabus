package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Tool;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.FreezeSitesAfterSessionJob;

public class FreezeSitesAfterSessionJobImpl extends OsylAbstractQuartzJobImpl
	implements FreezeSitesAfterSessionJob {

    private String sessionIdConfig = null;

    private String permissionsConfig = null;

    private HashMap<String, List<String>> functionsToPut;

    private HashMap<String, List<String>> functionsToAllow;

    private List<Site> allSites;
    
    private List<ToolConfiguration> siteTools = null;

	private static Log log = LogFactory
	    .getLog(FreezeSitesAfterSessionJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	long start = System.currentTimeMillis();
	log.info("FreezeSitesAfterSessionJobImpl: Starting...");

	loginToSakai();

	// -------------------------------------------------------------------
	// Retrieve a HashMap with sessionId and a list of permissions by role
	// to replace original permissions from configuration file
	// -------------------------------------------------------------------
	adminConfigService.getConfigToFreeze();
	setSessionIdConfig(adminConfigService.getSessionId());
	functionsToPut = adminConfigService.getFrozenFunctionsToAllow();
	setFunctionsToAllow(functionsToPut);

	log.info("FreezeSitesAfterSessionJobImpl: sessionId from xml:"
		+ getSessionIdConfig());

	if (getSessionIdConfig() != null) {
	    // -------------------------------------------------------------------
	    // Retrieve list of all sites to evaluate
	    // -------------------------------------------------------------------
	    allSites =
		    siteService.getSites(SiteService.SelectionType.ANY,
			    "course", null, null, SiteService.SortType.NONE,
			    null);

	    log.info("FreezeSitesAfterSessionJobImpl: sites to correct:"
		    + allSites.size());
	    long cont = 0;
	    Site site = null;
	    AuthzGroup siteRealm = null;
	    CourseOffering courseOffering = null;
	    // For each site add isfrozen as a property
	    for (int i = 0; i < allSites.size(); i++) {
		site = allSites.get(i);
		if (site.getProviderGroupId() != null
			&& site.getProviderGroupId().length() > 0) {
		    Section section =
			    cmService.getSection(site.getProviderGroupId());
		    String courseOfferingEid = section.getCourseOfferingEid();
		    courseOffering =
			    cmService.getCourseOffering(courseOfferingEid);
		    if (courseOffering != null) {
			String sessionId =
				courseOffering.getAcademicSession().getEid();
			if (getSessionIdConfig().equals(sessionId)) {
			    try {
				siteRealm =
					authzGroupService
						.getAuthzGroup(REALM_PREFIX
							+ site.getId());
			    } catch (GroupNotDefinedException e) {
				log.error(e.getMessage());
			    }
			    if (siteRealm != null) {
				log.info("*** The site :" + site.getTitle()
					+ " will be frozen ***");
				replacePermission(siteRealm, functionsToPut);
				setFrozenStatus(site, YES);
				cont++;
			    } else {
				log.info("*** siteRealm is null***");
			    }
			}
		    }
		}
	    }
	    log.info("Sites frozen : " + cont + "");
	}// session and period
	else {
	    log.info("The file frozenSitesConfig.xml does not exist in OpenSyllabus Admin Ressources/config.");
	}

	// -------------------------------------------------------------------
	log.info("FreezeSitesAfterSessionJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	logoutFromSakai();

    }

    private void replacePermission(AuthzGroup realm,
    	    HashMap<String, List<String>> functions) {
	try {
		
	    Set<Role> roles = realm.getRoles();
	    if (roles != null) {
			log.info(" Roles num: " + roles.size());
		    Set<String> st = functions.keySet();
		    Iterator<String> iterator = st.iterator();
		    while (iterator.hasNext()) {
				String keyRole = iterator.next();
				for (Role roleToUpdate : roles) {
					if (keyRole.equalsIgnoreCase(roleToUpdate.getId())) {
					    Set<String> oldPermissions = roleToUpdate.getAllowedFunctions();						
					    for (String oldFunction : oldPermissions) {
				    		String contentFtn = oldFunction.toString().substring(0,7);
							if (roleToUpdate.isAllowed((String) oldFunction)) {
									if (!contentFtn.equalsIgnoreCase("content"))
									roleToUpdate.disallowFunction((String) oldFunction);
					    	}
						}
					    List<String> newPermissions = functions.get(keyRole);
					    for (String newFunction : newPermissions) {
							if (!roleToUpdate.isAllowed((String) newFunction))
								roleToUpdate.allowFunction((String) newFunction);
						}
					}
			    }
			}
		    authzGroupService.save(realm);
	    } else {
	    	log.info("Roles is null.");
	    }
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
   }
    
    private void setFrozenStatus(Site site, String value) {
	// Replace property isfrozen to true
	// if sakai_realm.provider_id not null
	if (site.getProviderGroupId() != null) {
	    siteTools = getSiteTools(site);
	    ResourcePropertiesEdit rpr = site.getPropertiesEdit();
	    rpr.addProperty("isfrozen", value);
	    try {
	    	siteService.save(site);
	    	checkTools(site);
	    } catch (IdUnusedException e) {
	    	log.info("The site " + site.getTitle() + " does not exist.");
	    } catch (PermissionException e) {
	    	log.info("You are not allowed to update the site "
			+ site.getTitle());
	    }
	    	log.info("The site " + site.getTitle()
		    + " has been upgraded with isfrozen");
	}
    }
    
    
    private void checkTools(Site site) {    
	try {
	    //siteTools = getSiteTools(site);
	    validateTools(site);
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    log.info("The site " + site.getTitle() + " does not exist.");
	} catch (PermissionException e) {
	    log.info("You are not allowed to update the site "
		    + site.getTitle());
	}
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
    private void loginToSakai() {
	super.loginToSakai("FreezeSitesSync");
    }

    public String getSessionIdConfig() {
	return sessionIdConfig;
    }

    private void setSessionIdConfig(String sessionIdConfig) {
	this.sessionIdConfig = sessionIdConfig;
    }

    public String getPermissionsConfig() {
	return permissionsConfig;
    }

    public void setPermissionsConfig(String permissionsConfig) {
	this.permissionsConfig = permissionsConfig;
    }

    public HashMap<String, List<String>> getFunctionsToPut() {
	return functionsToPut;
    }

    public void setFunctionsToPut(HashMap<String, List<String>> functionsToPut) {
	this.functionsToPut = functionsToPut;
    }

    public HashMap<String, List<String>> getFunctionsToAllow() {
	return functionsToAllow;
    }

    public void setFunctionsToAllow(
	    HashMap<String, List<String>> functionsToAllow) {
	this.functionsToAllow = functionsToAllow;
    }
    
    public List<ToolConfiguration> getSiteTools() {
		return siteTools;
	}

	public void setSiteTools(List<ToolConfiguration> siteTools) {
		this.siteTools = siteTools;
	}
}