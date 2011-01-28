package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.FunctionsSynchronizationJob;

public class FunctionsSynchronisationJobImpl implements
	FunctionsSynchronizationJob {

    private List<Site> allSites;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(FunctionsSynchronisationJobImpl.class);

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

    /**
     * Administration ConfigurationService injection
     */
    private ConfigurationService adminConfigService;

    /**
     * @param adminConfigService
     */
    public void setAdminConfigService(ConfigurationService adminConfigService) {
	this.adminConfigService = adminConfigService;
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

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("FunctionsSynchronisationJobImpl: starting");

	String roleToRemove = adminConfigService.getRoleToRemove();

	String functionsRole = adminConfigService.getFunctionsRole();

	// Check role in site template realm
	try {
	    AuthzGroup realm = authzGroupService.getAuthzGroup(TEMPLATE_ID);
	    if (functionsRole != null) {
		if (!isRoleInRealm(realm)) {
		    addRole(realm);
		    authzGroupService.save(realm);
		} else {

		    Role role =
			    realm
				    .getRole(adminConfigService
					    .getFunctionsRole());
		    addPermissions(role);
		    removePermissions(role);
		}
	    }
	    // We remove the role
	    if (roleToRemove != null)
		removeRole(realm, roleToRemove);
	    authzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}

	// Check role in group template realm
	try {
	    AuthzGroup realm =
		authzGroupService.getAuthzGroup(GROUP_TEMPLATE_ID);
	    if (functionsRole != null) {
		if (!isRoleInRealm(realm)) {
		    addRole(realm);
		    authzGroupService.save(realm);
		} else {

		    Role role =
			    realm
				    .getRole(adminConfigService
					    .getFunctionsRole());
		    addPermissions(role);
		    removePermissions(role);
		}
	    }
	    // We remove the role
	    if (roleToRemove != null)
		removeRole(realm, roleToRemove);
	    authzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}

	// check all course site realms
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
		    authzGroupService.getAuthzGroup(REALM_PREFIX
				+ site.getId());
	    } catch (GroupNotDefinedException e) {
		log.error(e.getMessage());
	    }

	    if (siteRealm != null) {

		try {
		    if (functionsRole != null) {
			// We check if the role with the required
			// permissions exists in the site
			roleExists = isRoleInRealm(siteRealm);
			if (!roleExists) {
			    addRole(siteRealm);
			    authzGroupService.save(siteRealm);
			} else {

			    Role role =
				    siteRealm.getRole(adminConfigService
					    .getFunctionsRole());
			    // We add the new permissions
			    addPermissions(role);

			    // We remove the specified users
			    removePermissions(role);
			}
		    }

		    // We remove the role
		    if (roleToRemove != null)
			removeRole(siteRealm, roleToRemove);
		    authzGroupService.save(siteRealm);

		} catch (GroupNotDefinedException e) {
		    log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
		    log.error(e.getMessage());
		}
	    }

	    // look if we have a group for the current site
	    Collection groups = site.getGroups();
	    if (groups != null && groups.size() > 0) {
		Iterator<Group> groupIter = groups.iterator();
		while (groupIter.hasNext()) {
		    Group group = groupIter.next();
		    if (group != null) {
			String groupId = group.getId();
			AuthzGroup groupRealm = null;
			try {
			    groupRealm =
				authzGroupService
					    .getAuthzGroup(REALM_PREFIX
						    + site.getId()
						    + GROUP_REALM_PREFIX
						    + groupId);

			    if (functionsRole != null) {
				// We check if the role with the required
				// permissions exists in the group
				roleExists = isRoleInRealm(groupRealm);
				if (!roleExists) {
				    addRole(groupRealm);
				    authzGroupService.save(groupRealm);
				} else {

				    Role role =
					    groupRealm
						    .getRole(adminConfigService
							    .getFunctionsRole());
				    // We add the new permissions
				    addPermissions(role);

				    // We remove the specified users
				    removePermissions(role);
				}
			    }

			    // We remove the role
			    if (roleToRemove != null)
				removeRole(groupRealm, roleToRemove);
			    authzGroupService.save(groupRealm);

			} catch (GroupNotDefinedException e) {
			    log.error(e.getMessage());
			} catch (AuthzPermissionException e) {
			    log.error(e.getMessage());
			}
		    }
		}
	    }
	}
	log.info("FunctionsSynchronisationJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	logoutFromSakai();
    }

    /**
     * Checks if the permissions are associated to the role.
     * 
     * @param siteRealm
     */
    private void addRole(AuthzGroup realm) {
	try {
	    String functionsRole = adminConfigService.getFunctionsRole();
	    if (functionsRole != null) {
		Role role = realm.getRole(functionsRole);

		if (role == null)
		    role = realm.addRole(functionsRole);

		List<String> functionsAdded =
			adminConfigService.getAllowedFunctions();
		List<String> functionsRemoved =
			adminConfigService.getDisallowedFunctions();

		for (Object function : functionsAdded) {
		    if (!role.isAllowed((String) function))
			role.allowFunction((String) function);
		}

		for (Object function : functionsRemoved) {
		    if (role.isAllowed((String) function))
			role.disallowFunction((String) function);
		}

		authzGroupService.save(realm);
	    }
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	} catch (RoleAlreadyDefinedException e) {
	    log.error(e.getMessage());
	}

    }

    private boolean isRoleInRealm(AuthzGroup realm) {
	Role role = realm.getRole(adminConfigService.getFunctionsRole());
	if (role == null)
	    return false;
	else
	    return true;
    }

    private void removePermissions(Role role) {
	List<String> functionsRemoved =
		adminConfigService.getDisallowedFunctions();
	if (functionsRemoved.size() > 0) {
	    for (String permission : functionsRemoved) {
		if (role.isAllowed(permission)) {
		    role.disallowFunction(permission);
		}
	    }
	}
    }

    private void addPermissions(Role role) {
	List<String> functionsAdded = adminConfigService.getAllowedFunctions();
	if (functionsAdded.size() > 0) {
	    for (String perm : functionsAdded) {

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
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1", "CMSync");

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    private void removeRole(AuthzGroup realm, String role) {
	try {
	    Set<String> users = realm.getUsersHasRole(role);

	    for (String userId : users) {
		realm.removeMember(userId);
	    }
	    realm.removeRole(role);

	    authzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}

    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }

}
