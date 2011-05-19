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
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.RolesSynchronizationJob;

public class RolesSynchronizationJobImpl extends OsylAbstractQuartzJobImpl
	implements RolesSynchronizationJob {

    protected static Log log = LogFactory.getLog(RolesSynchronizationJobImpl.class);
    
    private List<Site> allSites;

    private List<ConfigRole> rolesToConfig = null;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("Starting");

	// Retrieve information from the xml file
	init();

	if (rolesToConfig != null) {

	    for (ConfigRole configRole : rolesToConfig) {
		String role = configRole.getConfigRole();
		log.info("role: " + role);
		String description = configRole.getDescription();
		log.info("description: " + description);
		List<String> addedUsers = configRole.getAddedUsers();
		log.info("addedUsers: " + addedUsers);
		List<String> removedUsers = configRole.getRemovedUsers();
		log.info("removedUsers: " + removedUsers);
		List<String> functions = configRole.getFunctions();
		log.info("functions: " + functions);
		boolean includingFrozenSites =
			configRole.isIncludingFrozenSites();
		log.info("isIncludingFrozenSites: " + includingFrozenSites);
		boolean includingDirSites = configRole.isIncludingDirSites();
		log.info("isIncludingDirSites: " + includingDirSites);

		// Check role in template realm
		try {
		    AuthzGroup realm =
			    authzGroupService.getAuthzGroup(TEMPLATE_ID);
		    processRealm(realm, configRole);
		} catch (GroupNotDefinedException e) {
		    e.printStackTrace();
		}

		allSites =
			siteService.getSites(SiteService.SelectionType.ANY,
				COURSE_SITE, null, null,
				SiteService.SortType.NONE, null);

		if (configRole.isIncludingDirSites()) {
		    allSites.addAll(siteService.getSites(
			    SiteService.SelectionType.ANY, DIRECTORY_SITE,
			    null, null, SiteService.SortType.NONE, null));
		}
		Site site = null;
		AuthzGroup siteRealm = null;

		for (int i = 0; i < allSites.size(); i++) {
		    site = allSites.get(i);

		    if (configRole.isIncludingFrozenSites()
			    || (!configRole.isIncludingFrozenSites() && !getFrozenValue(site))) {
			try {
			    siteRealm =
				    authzGroupService
					    .getAuthzGroup(REALM_PREFIX
						    + site.getId());
			} catch (GroupNotDefinedException e) {
			    log.error(e.getMessage());
			}
			processRealm(siteRealm, configRole);
		    }
		}
		log.info("completed in " + (System.currentTimeMillis() - start)
			+ " ms");
		logoutFromSakai();
	    }
	}
    }

    private void processRealm(AuthzGroup siteRealm, ConfigRole configRole) {
	boolean roleExists = false;

	if (siteRealm != null) {

	    // We check if the role with the required
	    // permissions
	    // exists in the site
	    roleExists = isRoleInRealm(siteRealm, configRole.getConfigRole());
	    if (!roleExists) {
		addRole(siteRealm, configRole.getConfigRole(),
			configRole.getFunctions(), configRole.getDescription());
	    } else {
		checkRole(siteRealm, configRole.getConfigRole(),
			configRole.getFunctions());
	    }
	    // We add the new users
	    addUsers(siteRealm, configRole.getConfigRole(),
		    configRole.getAddedUsers());

	    // We remove the specified users
	    removeUsers(siteRealm, configRole.getConfigRole(),
		    configRole.getRemovedUsers());

	}
    }

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
	boolean includingFrozenSites;
	boolean includingDirSites;
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
	    includingFrozenSites =
		    ((Boolean) values
			    .get(ConfigurationService.INCLUDING_FROZEN_SITES))
			    .booleanValue();
	    includingDirSites =
		    ((Boolean) values
			    .get(ConfigurationService.INCLUDING_DIR_SITES))
			    .booleanValue();

	    configRole = new ConfigRole();
	    configRole.setConfigRole(role);
	    configRole.setDescription(description);
	    configRole.setAddedUsers(addedUsers);
	    configRole.setRemovedUsers(removedUsers);
	    configRole.setFunctions(functions);
	    configRole.setIncludingFrozenSites(includingFrozenSites);
	    configRole.setIncludingDirSites(includingDirSites);

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
		    userId = userDirectoryService.getUserId(user);
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
		    userId = userDirectoryService.getUserId(user);

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
	super.loginToSakai("RolesSynchronizationJob");
    }

    class ConfigRole {
	private String configRole;

	private String description;

	private String removedRole;

	private List<String> addedUsers;

	private List<String> removedUsers;

	private List<String> functions;

	private boolean includingFrozenSites;

	private boolean includingDirSites;

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

	/**
	 * @return the includingFrozenSites value.
	 */
	public boolean isIncludingFrozenSites() {
	    return includingFrozenSites;
	}

	/**
	 * @param includingFrozenSites the new value of includingFrozenSites.
	 */
	public void setIncludingFrozenSites(boolean includingFrozenSites) {
	    this.includingFrozenSites = includingFrozenSites;
	}

	/**
	 * @return the includingDirSites value.
	 */
	public boolean isIncludingDirSites() {
	    return includingDirSites;
	}

	/**
	 * @param includingDirSites the new value of includingDirSites.
	 */
	public void setIncludingDirSites(boolean includingDirSites) {
	    this.includingDirSites = includingDirSites;
	}
    }
}