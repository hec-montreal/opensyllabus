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

    String functionsRole;
    String roleDescription;
    String roleToRemove;
    List<String> allowedFunctions;
    List<String> disallowedFunctions;
    long start;
    
    private String getFunctionsRole() {
        return functionsRole;
    }

    private void setFunctionsRole(String functionsRole) {
        this.functionsRole = functionsRole;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    private String getRoleToRemove() {
        return roleToRemove;
    }

    private void setRoleToRemove(String roleToRemove) {
        this.roleToRemove = roleToRemove;
    }

    private List<String> getAllowedFunctions() {
        return allowedFunctions;
    }

    private void setAllowedFunctions(List<String> allowedFunctions) {
        this.allowedFunctions = allowedFunctions;
    }

    private List<String> getDisallowedFunctions() {
        return disallowedFunctions;
    }

    private void setDisallowedFunctions(List<String> disallowedFunctions) {
        this.disallowedFunctions = disallowedFunctions;
    }

    private long getStart() {
        return start;
    }

    private void setStart(long start) {
        this.start = start;
    }

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	
        loginToSakai();

	setStart(System.currentTimeMillis());
	log.info("starting");

	setRoleToRemove(adminConfigService.getRoleToRemove());
	setFunctionsRole(adminConfigService.getFunctionsRole());
	setRoleDescription(adminConfigService.getDescription());
	setAllowedFunctions(adminConfigService.getAllowedFunctions());
	setDisallowedFunctions(adminConfigService.getDisallowedFunctions());
	
	log.info("data provided by adminConfigService:");
	log.info("roleToRemove:        " + getRoleToRemove());
	log.info("functionsRole:       " + getFunctionsRole());
	log.info("roleDEscription:       " + getRoleDescription());
	log.info("allowedFunctions:    " + getAllowedFunctions());
	log.info("disallowedFunctions: " + getDisallowedFunctions());

	// Check role in site template realm
	try {
	    AuthzGroup realm = authzGroupService.getAuthzGroup(TEMPLATE_ID);
	    if (getFunctionsRole() != null) {
		if (!isRoleInRealm(realm)) {
		    addRole(realm);
		    authzGroupService.save(realm);
		} else {

		    Role role = realm.getRole(getFunctionsRole());
		    role.setDescription(getRoleDescription());
		    addPermissions(role, getAllowedFunctions());
		    removePermissions(role, getDisallowedFunctions());
		}
	    }
	    // We remove the role
	    if (getRoleToRemove() != null)
		removeRole(realm, getRoleToRemove());
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
	    if (getFunctionsRole() != null) {
		if (!isRoleInRealm(realm)) {
		    addRole(realm);
		    authzGroupService.save(realm);
		} else {

		    Role role = realm.getRole(getFunctionsRole());
		    role.setDescription(getRoleDescription());
		    addPermissions(role, getAllowedFunctions());
		    removePermissions(role, getDisallowedFunctions());
		}
	    }
	    // We remove the role
	    if (getRoleToRemove() != null)
		removeRole(realm, getRoleToRemove());
	    authzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}

	// check all course site realms
	final List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, SITE_TYPE,
			null, null, SiteService.SortType.NONE, null);

	log.info("processing " + allSites.size() + " sites");
	
	int THREAD_COUNT = 8;
	int SHARE_COUNT = THREAD_COUNT * 8;
	
	if(allSites.size()<SHARE_COUNT){
	    processSites(allSites);
	} else {
	    final int share = new Double(
		    Math.floor(allSites.size()/(SHARE_COUNT))).intValue();
		
	    logoutFromSakai();

	    MyThread[] threads = new MyThread[THREAD_COUNT];
	    int shareStart = 0;
	    int shareEnd = 0;

	    for (int i = 0; i < THREAD_COUNT; i++) {
		shareStart = i * share;
		shareEnd = Math.min(i * share + share, allSites.size());
		threads[i] = new MyThread(i,
			allSites.subList(shareStart, shareEnd));
		log.debug("allocated sites " + shareStart + " to "
			+ shareEnd + " to thread #" + i);
		threads[i].start();
	    }
	    shareStart = THREAD_COUNT * share;
	    
	    // Check that every thread has completed.
	    boolean allCompleted = true; 
	    while (true) {
		allCompleted = true;
		for (int i = 0; i < THREAD_COUNT; i++) {
		    if(threads[i].completed()) {
			// found one free thread 
			if (shareEnd != allSites.size()) {
			    // not all sites processed
			    allCompleted = false;
			    shareEnd = Math.min(shareStart + share,
				    allSites.size());
			    threads[i] = new MyThread(
				    i, 
				    allSites.subList(
					    shareStart,
					    shareEnd));
			    log.debug("allocated sites " + shareStart + " to "
				    + shareEnd + " to thread #" + i);
			    threads[i].start();
			    shareStart = shareEnd;
			}
		    } else {
			allCompleted = false;
		    }
		}

		if (allCompleted) {
		    break;
		} else {
		    try {
			// Wait a bit
			Thread.sleep(100);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	log.info("Completed after "
		+ ((System.currentTimeMillis() - getStart())/1000)
		+ " seconds");
	if (getFunctionsRole() != null) {
	    log.info("roleModified:       " + getFunctionsRole());
	}
	if (getRoleToRemove() != null) {
	    log.info("roleRemoved:        " + getRoleToRemove());
	}
    } // execute
    
    class MyThread extends Thread {
	List<Site> sites;
	boolean completed = false;
	int threadNo;

	MyThread(int threadNo, List<Site> sites) {
	    this.threadNo = threadNo;
	    setSiteList(sites);
	}
	
	private void setSiteList(List<Site> sites) {
	    this.sites = sites;
	}

	public void run() {
	    completed = false;
	    log.debug("Thread #" + threadNo + " (" + getName() + ") Starting");
	    loginToSakai();
	    processSites(sites);
	    logoutFromSakai();
	    log.debug("Thread #" + threadNo + " Finished");
	    completed = true;
	}

	public boolean completed() {
	    return completed;
	}
    } // class MyThread
	
    private void processSites(List<Site> sites){
	boolean roleExists = false;
	Site site = null;
	AuthzGroup siteRealm = null;
	
	for (int i = 0; i < sites.size(); i++) {
	    site = sites.get(i);
	    try {
		siteRealm =
		    authzGroupService.getAuthzGroup(REALM_PREFIX
				+ site.getId());
	    } catch (GroupNotDefinedException e) {
		log.error(e.getMessage());
	    }

	    if (siteRealm != null) {
		try {
		    if (getFunctionsRole() != null) {
			// We check if the role with the required
			// permissions exists in the site
			roleExists = isRoleInRealm(siteRealm);
			if (!roleExists) {
			    addRole(siteRealm);
			    authzGroupService.save(siteRealm);
			} else {

			    Role role = siteRealm.getRole(getFunctionsRole());
			    role.setDescription(getRoleDescription());
			    // We add the new permissions
			    addPermissions(role, getAllowedFunctions());

			    // We remove the specified users
			    removePermissions(role, getDisallowedFunctions());
			}
		    }

		    // We remove the role
		    if (getRoleToRemove() != null)
			removeRole(siteRealm, getRoleToRemove());
		    authzGroupService.save(siteRealm);

		} catch (GroupNotDefinedException e) {
		    log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
		    log.error(e.getMessage());
		}
	    }

	    // look if we have a group for the current site
	    Collection<Group> groups = site.getGroups();
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

			    if (getFunctionsRole() != null) {
				// We check if the role with the required
				// permissions exists in the group
				roleExists = isRoleInRealm(groupRealm);
				if (!roleExists) {
				    addRole(groupRealm);
				    authzGroupService.save(groupRealm);
				} else {

				    Role role =
					    groupRealm
						    .getRole(getFunctionsRole());
				    role.setDescription(getRoleDescription());
				    // We add the new permissions
				    addPermissions(role, getAllowedFunctions());

				    // We remove the specified users
				    removePermissions(role, getDisallowedFunctions());
				}
			    }

			    // We remove the role
			    if (getRoleToRemove() != null)
				removeRole(groupRealm, getRoleToRemove());
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
    } // processSites

    /**
     * Checks if the permissions are associated to the role.
     * 
     * @param siteRealm
     */
    private void addRole(AuthzGroup realm) {
	try {
	    if (getFunctionsRole() != null) {
		Role role = realm.getRole(getFunctionsRole());

		if (role == null)
		    role = realm.addRole(getFunctionsRole());
		
		role.setDescription(getRoleDescription());

		for (Object function : getAllowedFunctions()) {
		    if (!role.isAllowed((String) function))
			role.allowFunction((String) function);
		}

		for (Object function : getDisallowedFunctions()) {
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
	Role role = realm.getRole(getFunctionsRole());
	if (role == null)
	    return false;
	else
	    return true;
    }

    private void removePermissions(Role role, List<String> functionsRemoved) {
	if (functionsRemoved.size() > 0) {
	    for (String permission : functionsRemoved) {
		if (role.isAllowed(permission)) {
		    role.disallowFunction(permission);
		}
	    }
	}
    }

    private void addPermissions(Role role, List<String> functionsAdded) {
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
	usageSessionService.startSession("admin", "127.0.0.1", "FunctionsSync");

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
	usageSessionService.logout();
    }

}
