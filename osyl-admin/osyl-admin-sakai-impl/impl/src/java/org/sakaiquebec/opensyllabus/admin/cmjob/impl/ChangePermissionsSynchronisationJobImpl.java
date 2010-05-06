package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.ChangePermissionsSynchronizationJob;

public class ChangePermissionsSynchronisationJobImpl implements
	ChangePermissionsSynchronizationJob {

    private List<Site> allSites;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(ChangePermissionsSynchronisationJobImpl.class);

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

    /**
     * List of the permissions that will added to the role in the course sites.
     */
    private String[] permAdded = { "site.viewRoster" };

    /**
     * List of the permissions that will be removed from the role.
     */
    private String[] permRemoved = { "site.add" };

    private String modifiedRole = "Instructor";

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();
	// Check role in template realm
	try {
	    AuthzGroup realm = AuthzGroupService.getAuthzGroup(TEMPLATE_ID);
	    if (!isRoleInRealm(realm)) {
		addRole(realm);
		AuthzGroupService.save(realm);
	    } else {

		Role role = realm.getRole(modifiedRole);
		addPermissions(role);
		removePermissions(role);
		AuthzGroupService.save(realm);
	    }
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}

	allSites =
		siteService.getSites(SiteService.SelectionType.ANY, SITE_TYPE,
			null, null, SiteService.SortType.NONE, null);

	boolean roleExists = false;
	Site site = null;
	AuthzGroup siteRealm = null;

	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);

	    try {
		siteRealm =
			AuthzGroupService.getAuthzGroup(REALM_PREFIX
				+ site.getId());
	    } catch (GroupNotDefinedException e) {
		log.error(e.getMessage());
	    }

	    if (siteRealm != null) {

		try {
		    // We check if the role helpdesk with the required
		    // permissions
		    // exists in the site
		    roleExists = isRoleInRealm(siteRealm);
		    if (!roleExists) {
			addRole(siteRealm);
			AuthzGroupService.save(siteRealm);
		    } else {

			Role role = siteRealm.getRole(modifiedRole);
			// We add the new permissions
			addPermissions(role);

			// We remove the specified users
			removePermissions(role);
			AuthzGroupService.save(siteRealm);
		    }
		} catch (GroupNotDefinedException e) {
		    log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
		    log.error(e.getMessage());
		}
	    }

	}
	logoutFromSakai();
    }

    /**
     * Checks if the permissions are associated to the role.
     * 
     * @param siteRealm
     */
    private void addRole(AuthzGroup realm) {
	try {
	    Role role = realm.getRole(modifiedRole);

	    if (role == null)
		role = realm.addRole(modifiedRole);
	    for (Object function : permAdded) {
		if (!role.isAllowed((String) function))
		    role.allowFunction((String) function);
	    }

	    for (Object function : permRemoved) {
		if (role.isAllowed((String) function))
		    role.disallowFunction((String) function);
	    }

	    AuthzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	} catch (RoleAlreadyDefinedException e) {
	    log.error(e.getMessage());
	}
    }

    private boolean isRoleInRealm(AuthzGroup realm) {
	Role role = realm.getRole(modifiedRole);
	if (role == null)
	    return false;
	else
	    return true;
    }

    private void removePermissions(Role role) {
	if (permRemoved.length > 0) {
	    for (String permission : permRemoved) {
		if (role.isAllowed(permission)) {
		    role.disallowFunction(permission);
		}
	    }
	}
    }

    private void addPermissions(Role role) {
	if (permAdded.length > 0) {
	    for (String perm : permAdded) {

		if (!role.isAllowed(perm)) {
		    role.allowFunction(perm);
		}

	    }
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
