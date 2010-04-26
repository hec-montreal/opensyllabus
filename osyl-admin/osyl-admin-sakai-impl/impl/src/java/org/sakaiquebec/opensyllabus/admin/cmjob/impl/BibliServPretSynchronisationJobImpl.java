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
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.BibliServPretSynchronizationJob;

public class BibliServPretSynchronisationJobImpl implements
BibliServPretSynchronizationJob {

    /**
     * The user service injected by the Spring
     */
    private UserDirectoryService userDirService;

    /**
     * Sets the <code>UserDirectoryService</code> needed to create the site in
     * the init() method.
     * 
     * @param userDirService
     */
    public void setUserDirService(UserDirectoryService userDirService) {
	this.userDirService = userDirService;
    }

    private List<Site> allSites;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(BibliServPretSynchronisationJobImpl.class);

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
     * List of the users that will added to the sites of type osyleditor with
     * role helpdesk
     */
    private String[] usersIdAdded = { "11091096" };

    /**
     * List of the users with the role Helpdesk that will be removed from the
     * sites of type osylEditor.
     */
    private String[] usersIdRemoved = { "11060533" , "11127242" };

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();
	// Check role in template realm
	try {
	    AuthzGroup realm = AuthzGroupService.getAuthzGroup(TEMPLATE_ID);
	    if (!isRoleInRealm(realm))
		addRole(realm);
	    else
		checkRole(realm);
	    addUsers(realm);
	    removeUsers(realm);

	} catch (GroupNotDefinedException e) {
	    e.printStackTrace();
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

		// We check if the role helpdesk with the required permissions
		// exists in the site
		roleExists = isRoleInRealm(siteRealm);
		if (!roleExists) 
		    addRole(siteRealm);
		else
		    checkRole(siteRealm);
		// We add the new users
		addUsers(siteRealm);

		// We remove the specified users
		removeUsers(siteRealm);

	    }
	}
	logoutFromSakai();
    }

    
    /**
     * Checks if the permissions are associated to the role.
     * @param siteRealm
     */
    private void checkRole (AuthzGroup realm){
	try {
	    Role role = realm.getRole(BIBLISERVPRET_ROLE);
	    
	    for(Object function: FUNCTIONS_TO_ALLOW){
		if(!role.isAllowed((String)function))
		    role.allowFunction((String)function);
	    }
	    AuthzGroupService.save(realm);

	}catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
    }

    private void addRole(AuthzGroup realm) {
	try {
	    Role role = realm.addRole(BIBLISERVPRET_ROLE);
	    role.allowFunctions(FUNCTIONS_TO_ALLOW);

	    AuthzGroupService.save(realm);

	} catch (RoleAlreadyDefinedException e) {
	    log.error(e.getMessage());
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
    }

    private boolean isRoleInRealm(AuthzGroup realm) {
	Role role = realm.getRole(BIBLISERVPRET_ROLE);
	if (role == null)
	    return false;
	else
	    return true;
    }

    private void removeUsers(AuthzGroup realm) {
	if (usersIdRemoved.length > 0) {
	    String userId = null;
	    for (String user : usersIdRemoved) {
		try {
		    userId = userDirService.getUserId(user);
		    if (realm.hasRole(userId, BIBLISERVPRET_ROLE)) {
			realm.removeMember(userId);
		    }
		    AuthzGroupService.save(realm);

		} catch (UserNotDefinedException e) {
		    log.error("The user " + user
			    + " is not available in the system");
		} catch (GroupNotDefinedException e) {
		    log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
		    log.error(e.getMessage());
		}
	    }
	}
    }

    private void addUsers(AuthzGroup realm) {
	if (usersIdAdded.length > 0) {
	    String userId = null;
	    for (String user : usersIdAdded) {

		try {
		    userId = userDirService.getUserId(user);

		    if (realm.getMember(userId) == null) {
			realm.addMember(userId, BIBLISERVPRET_ROLE, true, false);
		    }

		    AuthzGroupService.save(realm);

		} catch (UserNotDefinedException e) {
		    log.error("The user " + user
			    + " is not available in the system");
		} catch (GroupNotDefinedException e) {
		    log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
		    log.error(e.getMessage());
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
