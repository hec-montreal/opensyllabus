package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.FreezeSitesAfterSessionJob;

public class FreezeSitesAfterSessionJobImpl extends OsylAbstractQuartzJobImpl
	implements FreezeSitesAfterSessionJob {

    private String sessionIdConfig = null;

    private String permissionsConfig = null;

    private HashMap<String, List<String>> functionsToPut;

    private HashMap<String, List<String>> functionsToAllow;

    private List<Site> allSites;

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

    private void xreplacePermission(AuthzGroup realm,
	    HashMap<String, List<String>> functions) {
	try {
	    Set<Role> roles = realm.getRoles();
	    if (roles != null) {
		log.info(" Roles num: " + roles.size());
		for (Role role : roles) {
		    Role roleToUpdate = realm.getRole(role.getId());
		    Set<String> st = functions.keySet();
		    Iterator<String> iterator = st.iterator();
		    while (iterator.hasNext()) {
				String keyRole = iterator.next();
				log.info("FreezeSitesAfterSessionJobImpl: Role "
					+ keyRole + " has "
					+ functions.get(keyRole).size()
					+ "functions **");
				if (roleToUpdate.getId().equalsIgnoreCase(keyRole)) {
				    List<String> newPermissions =
					    functions.get(keyRole);
				    Set<String> oldPermissions =
					    roleToUpdate.getAllowedFunctions();
				    //roleToUpdate.disallowFunctions(oldPermissions);
				    for (Object oldFunction : oldPermissions) {
						if (roleToUpdate.isAllowed((String) oldFunction))
							roleToUpdate.disallowFunction((String) oldFunction);
					}				    
				    //roleToUpdate.allowFunctions(newPermissions);
				    for (Object newFunction : newPermissions) {
						if (!roleToUpdate.isAllowed((String) newFunction))
							roleToUpdate.allowFunction((String) newFunction);
					}				    
				    //realm.getRole(roleToUpdate.getId());
				    authzGroupService.save(realm);
				    log.info("roleToUpdate:...getId(): '"
					    + roleToUpdate.getId()
					    + "' the permission was applied ***");
				}
		    }
		}
	    } else {
		log.info("Roles is null.");
	    }
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
    }

    private void replacePermission(AuthzGroup realm,
    	    HashMap<String, List<String>> functions) {
	try {
		
		AuthzGroup realmEdit = authzGroupService.getAuthzGroup(realm.getId());
		authzGroupService.save(realmEdit);

	    Set<Role> roles = realmEdit.getRoles();
	    if (roles != null) {
		log.info(" Roles num: " + roles.size());
	    Set<String> st = functions.keySet();
	    Iterator<String> iterator = st.iterator();
	    while (iterator.hasNext()) {
			String keyRole = iterator.next();
			for (Role role : roles) {
			    Role roleToUpdate = realmEdit.getRole(role.getId());
				if (roleToUpdate.getId().equalsIgnoreCase(keyRole)) {
				    List<String> newPermissions =
					    functions.get(keyRole);
				    Set<String> oldPermissions =
					    roleToUpdate.getAllowedFunctions();
				    for (Object oldFunction : oldPermissions) {
				    	String content = oldFunction.toString().substring(0,7);
				    	if (!content.equalsIgnoreCase("content")) {
							if (roleToUpdate.isAllowed((String) oldFunction))
								roleToUpdate.disallowFunction((String) oldFunction);
				    	}
					}				    
				    for (Object newFunction : newPermissions) {
						if (!roleToUpdate.isAllowed((String) newFunction))
							roleToUpdate.allowFunction((String) newFunction);
					}				    
				    authzGroupService.save(realmEdit);
				}
		    }
		}
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
	    ResourcePropertiesEdit rpr = site.getPropertiesEdit();
	    rpr.addProperty("isfrozen", value);
	    try {
		siteService.save(site);
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
}