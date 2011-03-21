package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.RolesSynchronizationJob;

public class RolesSynchronisationJobImpl implements RolesSynchronizationJob {

    private static Log log =
	    LogFactory.getLog(RolesSynchronisationJobImpl.class);

    private List<Site> allSites;

    private List<ConfigRole> rolesToConfig = null;

    // ***************** SPRING INJECTION ************************//
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

    /**
     * Our logger
     */

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

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("Starting");

	// Retrieve information from the xml file
	init();
	String role;
	String description;
	List<String> addedUsers;
	List<String> removedUsers;
	List<String> functions;

	if (rolesToConfig != null)
	    for (ConfigRole configRole : rolesToConfig) {
		role = configRole.getConfigRole();
		log.info("role: " + role);
		description = configRole.getDescription();
		log.info("description: " + description);
		addedUsers = configRole.getAddedUsers();
		log.info("addedUsers: " + addedUsers);
		removedUsers = configRole.getRemovedUsers();
		log.info("removedUsers: " + removedUsers);
		functions = configRole.getFunctions();
		log.info("functions: " + functions);

		// Check role in template realm
		try {
		    AuthzGroup realm =
			    authzGroupService.getAuthzGroup(TEMPLATE_ID);
		    if (!isRoleInRealm(realm, role))
			addRole(realm, role, functions, description);
		    else
			checkRole(realm, role, functions);
		    addUsers(realm, role, addedUsers);
		    removeUsers(realm, role, removedUsers);

		} catch (GroupNotDefinedException e) {
		    e.printStackTrace();
		}

		allSites =
			siteService.getSites(SiteService.SelectionType.ANY,
				SITE_TYPE, null, null,
				SiteService.SortType.NONE, null);

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

			// We check if the role helpdesk with the required
			// permissions
			// exists in the site
			roleExists = isRoleInRealm(siteRealm, role);
			if (!roleExists)
			    addRole(siteRealm, role, functions, description);
			else
			    checkRole(siteRealm, role, functions);

			// We add the new users
			addUsers(siteRealm, role, addedUsers);

			// We remove the specified users
			removeUsers(siteRealm, role, removedUsers);

		    }
		}
		log.info("completed in "
			+ (System.currentTimeMillis() - start) + " ms");
		logoutFromSakai();
	    }
    }

    @SuppressWarnings("unchecked")
    private void init() {
	if (rolesToConfig == null) {
	    rolesToConfig = new ArrayList<ConfigRole>();
	}

	Map<String, Map<String, Object>> roles =
		adminConfigService.getUdatedRoles();

	String role;
	String description;
	List<String> removedUsers;
	List<String> addedUsers;
	List<String> functions;
	ConfigRole configRole;
	Map<String, Object> values;

	for (Entry<String, Map<String, Object>> entry : roles.entrySet()) {
	    role = entry.getKey();
	    values = entry.getValue();
	    removedUsers =
		    (List<String>) values
			    .get(ConfigurationService.REMOVEDUSERS);
	    addedUsers =
		    (List<String>) values.get(ConfigurationService.ADDEDUSERS);
	    functions =
		    (List<String>) values.get(ConfigurationService.FUNCTIONS);
	    description = (String) values.get(ConfigurationService.DESCRIPTION);

	    configRole = new ConfigRole();
	    configRole.setConfigRole(role);
	    configRole.setDescription(description);
	    configRole.setAddedUsers(addedUsers);
	    configRole.setRemovedUsers(removedUsers);
	    configRole.setFunctions(functions);

	    rolesToConfig.add(configRole);
	}
    }

    /**
     * Checks if the permissions are associated to the role.
     * 
     * @param siteRealm
     */
    private void checkRole(AuthzGroup realm, String configRole,
	    List<String> functions) {
	try {
	    Role role = realm.getRole(configRole);

	    for (Object function : functions) {
		if (!role.isAllowed((String) function))
		    role.allowFunction((String) function);
	    }
	    authzGroupService.save(realm);

	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
    }

    /**
     * Adds the role in the realm
     * 
     * @param realm
     */
    private void addRole(AuthzGroup realm, String configRole,
	    List<String> functions, String description) {
	try {
	    Role role = realm.addRole(configRole);
	    role.allowFunctions(functions);
	    role.setDescription(description);

	    authzGroupService.save(realm);

	} catch (RoleAlreadyDefinedException e) {
	    log.error(e.getMessage());
	} catch (GroupNotDefinedException e) {
	    log.error(e.getMessage());
	} catch (AuthzPermissionException e) {
	    log.error(e.getMessage());
	}
    }

    /**
     * Checks if the role is in the realm.
     * 
     * @param realm
     * @return
     */
    private boolean isRoleInRealm(AuthzGroup realm, String configRole) {
	Role role = realm.getRole(configRole);
	if (role == null)
	    return false;
	else
	    return true;
    }

    /**
     * Remove the users with the role helpdesk
     * 
     * @param realm
     */
    private void removeUsers(AuthzGroup realm, String configRole,
	    List<String> removedUsers) {
	if (removedUsers.size() > 0) {
	    String userId = null;
	    for (String user : removedUsers) {
		try {
		    userId = userDirService.getUserId(user);
		    if (realm.hasRole(userId, configRole)) {
			realm.removeMember(userId);
		    }
		    authzGroupService.save(realm);

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
     * Add the users with the role helpdesk.
     * 
     * @param realm
     */
    private void addUsers(AuthzGroup realm, String configRole,
	    List<String> addedUsers) {
	if (addedUsers.size() > 0) {
	    String userId = null;
	    for (String user : addedUsers) {

		try {
		    userId = userDirService.getUserId(user);

		    if (realm.getMember(userId) == null) {
			realm.addMember(userId, configRole, true, false);
		    }

		    authzGroupService.save(realm);

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
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1", "RoleSync");

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
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

    class ConfigRole {
	private String configRole;

	private String description;

	private String removedRole;

	private List<String> addedUsers;

	private List<String> removedUsers;

	private List<String> functions;

	public String getConfigRole() {
	    return configRole;
	}

	public void setConfigRole(String configRole) {
	    this.configRole = configRole;
	}

	public String getRemovedRole() {
	    return removedRole;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public void setRemovedRole(String removedRole) {
	    this.removedRole = removedRole;
	}

	public List<String> getAddedUsers() {
	    return addedUsers;
	}

	public void setAddedUsers(List<String> addedUsers) {
	    this.addedUsers = addedUsers;
	}

	public List<String> getRemovedUsers() {
	    return removedUsers;
	}

	public void setRemovedUsers(List<String> removedUsers) {
	    this.removedUsers = removedUsers;
	}

	public List<String> getFunctions() {
	    return functions;
	}

	public void setFunctions(List<String> functions) {
	    this.functions = functions;
	}

    }
}
