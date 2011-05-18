package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

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
import org.sakaiquebec.opensyllabus.admin.cmjob.api.UnFreezeSitesJob;

public class UnFreezeSitesJobImpl extends OsylAbstractQuartzJobImpl implements
	UnFreezeSitesJob {

    private String sessionIdConfig = null;

    private String permissionsConfig = null;

    private List<String> functionsToPut;

    private List<Site> allSites;

    protected static Log log = LogFactory.getLog(UnFreezeSitesJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	long start = System.currentTimeMillis();
	log.info("UnFreezeSitesAfterSessionJobImpl: Starting...");

	loginToSakai();

	// -------------------------------------------------------------------
	// Retrieve session and period for frozen sites to replace permissions
	// to original permissions for each role
	// -------------------------------------------------------------------
	adminConfigService.getFrozenSessionIdConfig();
	setSessionIdConfig(adminConfigService.getSessionId());

	log.info("UnFreezeSitesAfterSessionJobImpl: session from xml:"
		+ getSessionIdConfig());

	if (getSessionIdConfig() != null) {
	    // -------------------------------------------------------------------
	    // Retrieve list of all site to evaluate
	    // -------------------------------------------------------------------

	    allSites =
		    siteService.getSites(SiteService.SelectionType.ANY,
			    "course", null, null, SiteService.SortType.NONE,
			    null);

	    log.info("UnFreezeSitesAfterSessionJobImpl: sites to correct:"
		    + allSites.size());

	    Site site = null;
	    AuthzGroup tmplRealm = null;
	    AuthzGroup siteRealm = null;
	    CourseOffering courseOffering = null;
	    long cont = 0;
	    for (int i = 0; i < allSites.size(); i++) {
		site = allSites.get(i);
		// If the site is frozen
		if (getFrozenValue(site)) {
		    Section section =
			    cmService.getSection(site.getProviderGroupId());
		    String courseOfferingEid = section.getCourseOfferingEid();
		    courseOffering =
			    cmService.getCourseOffering(courseOfferingEid);
		    if (courseOffering != null) {
			String sessionId =
				courseOffering.getAcademicSession().getEid();
			// If the site has a session and period from
			// configuration file
			if (getSessionIdConfig().equals(sessionId)) {
			    try {
				tmplRealm =
					authzGroupService
						.getAuthzGroup(TEMPLATE_ID);
				siteRealm =
					authzGroupService
						.getAuthzGroup(REALM_PREFIX
							+ site.getId());
			    } catch (GroupNotDefinedException e) {
				log.error(e.getMessage());
			    }
			    if (tmplRealm != null && siteRealm != null) {
				log.info("***The site :" + site.getTitle()
					+ " is valid to process it ***");
				replacePermission(tmplRealm, siteRealm);
				setFrozenStatus(site, NOT);
				cont++;
			    } else {
				log.info("*** siteRealm or tmplRealm are null***");
			    }
			}
		    }
		}
	    }
	    log.info("Sites unfrozen : " + cont + "");
	}// session and period
	else {
	    log.info("The file unFrozenSitesConfig.xml does not exist in OpenSyllabus Admin Ressources/config.");
	}

	// -------------------------------------------------------------------
	log.info("UnFreezeSitesAfterSessionJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	logoutFromSakai();
    }

    /**
     * It replaces original permissions to permissions to read and view
     * 
     * @param siteRealm
     */
    private void replacePermission(AuthzGroup tmplt, AuthzGroup realm) {
	try {
	    Set<Role> rolesSite = realm.getRoles();
	    Set<Role> rolesTmpl = tmplt.getRoles();

	    if (rolesSite != null && rolesTmpl != null) {
		// It just to test
		log.info(" Roles site num: " + rolesSite.size());
		log.info(" Roles template num: " + rolesTmpl.size());
		for (Role roleSite : rolesSite) {
		    Role roleToUpdate = realm.getRole(roleSite.getId());
		    for (Role roleTmpl : rolesTmpl) {
			Role roleFromTmpl = realm.getRole(roleTmpl.getId());
			if (roleToUpdate.getId().equalsIgnoreCase(
				roleFromTmpl.getId())) {
			    Set<String> lastPermissions =
				    roleToUpdate.getAllowedFunctions();
			    roleToUpdate.disallowFunctions(lastPermissions);
			    Set<String> oldPermissions =
				    roleTmpl.getAllowedFunctions();
			    roleToUpdate.allowFunctions(oldPermissions);
			    authzGroupService.save(realm);
			    log.info("roleToUpdate:...getId(): '"
				    + roleToUpdate.getId()
				    + "' the permission was applied ***");
			}
		    }
		}
	    } else {
		log.info("Roles are null.");
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
	    log.info("*** The setFrozenStatus works.");
	    try {
		siteService.save(site);
	    } catch (IdUnusedException e) {
		log.info("The site " + site.getTitle() + " does not exist.");
	    } catch (PermissionException e) {
		log.info("You are not allowed to update the site "
			+ site.getTitle());
	    }
	    log.info("The site " + site.getTitle()
		    + " has been upgraded with !isfrozen");
	}
    }

    /**
     * Logs in the sakai environment
     */
    private void loginToSakai() {
	super.loginToSakai("UnFreezeSitesSync");
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

    public List<String> getFunctionsToPut() {
	return functionsToPut;
    }

    public void setFunctionsToPut(List<String> functionsToPut) {
	this.functionsToPut = functionsToPut;
    }
}