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
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.FunctionsSynchronizationJob;

public class FunctionsSynchronizationJobImpl extends OsylAbstractQuartzJobImpl
	implements FunctionsSynchronizationJob {

    /**
     * Our logger
     */
    private static Log log = LogFactory
	    .getLog(FunctionsSynchronizationJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("starting");

	String roleToRemove = adminConfigService.getRoleToRemove();
	String functionsRole = adminConfigService.getFunctionsRole();
	String roleDescription = adminConfigService.getDescription();
	List<String> allowedFunctions =
		adminConfigService.getAllowedFunctions();
	List<String> disallowedFunctions =
		adminConfigService.getDisallowedFunctions();

	log.info("data provided by adminConfigService:");
	log.info("roleToRemove:        " + roleToRemove);
	log.info("functionsRole:       " + functionsRole);
	log.info("roleDEscription:       " + roleDescription);
	log.info("allowedFunctions:    " + allowedFunctions);
	log.info("disallowedFunctions: " + disallowedFunctions);

	// Check role in site template realm
	try {
	    AuthzGroup realm = authzGroupService.getAuthzGroup(TEMPLATE_ID);
	    if (functionsRole != null) {
		if (!isRoleInRealm(realm, functionsRole)) {
		    addRole(realm, functionsRole, roleDescription,
			    allowedFunctions, disallowedFunctions);
		    authzGroupService.save(realm);
		} else {

		    Role role = realm.getRole(functionsRole);
		    role.setDescription(roleDescription);
		    addPermissions(role, allowedFunctions);
		    removePermissions(role, disallowedFunctions);
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
		if (!isRoleInRealm(realm, functionsRole)) {
		    addRole(realm, functionsRole, roleDescription,
			    allowedFunctions, disallowedFunctions);
		    authzGroupService.save(realm);
		} else {

		    Role role = realm.getRole(functionsRole);
		    role.setDescription(roleDescription);
		    addPermissions(role, allowedFunctions);
		    removePermissions(role, disallowedFunctions);
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
	final List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY,
			COURSE_SITE, null, null, SiteService.SortType.NONE,
			null);

	if (adminConfigService.isIncludingDirSites()) {
	    allSites.addAll(siteService
		    .getSites(SiteService.SelectionType.ANY, DIRECTORY_SITE,
			    null, null, SiteService.SortType.NONE, null));
	}
	log.info("processing " + allSites.size() + " sites");

	int THREAD_COUNT = 8;
	int SHARE_COUNT = THREAD_COUNT * 8;

	if (allSites.size() < SHARE_COUNT) {
	    processSites(allSites, functionsRole, roleDescription,
		    allowedFunctions, disallowedFunctions, roleToRemove);
	} else {
	    final int share =
		    new Double(Math.floor(allSites.size() / (SHARE_COUNT)))
			    .intValue();

	    logoutFromSakai();

	    MyThread[] threads = new MyThread[THREAD_COUNT];
	    int shareStart = 0;
	    int shareEnd = 0;

	    for (int i = 0; i < THREAD_COUNT; i++) {
		shareStart = i * share;
		shareEnd = Math.min(i * share + share, allSites.size());
		threads[i] =
			new MyThread(i, allSites.subList(shareStart, shareEnd),
				functionsRole, roleDescription,
				allowedFunctions, disallowedFunctions,
				roleToRemove);
		log.debug("allocated sites " + shareStart + " to " + shareEnd
			+ " to thread #" + i);
		threads[i].start();
	    }
	    shareStart = THREAD_COUNT * share;

	    // Check that every thread has completed.
	    boolean allCompleted = true;
	    while (true) {
		allCompleted = true;
		for (int i = 0; i < THREAD_COUNT; i++) {
		    if (threads[i].completed()) {
			// found one free thread
			if (shareEnd != allSites.size()) {
			    // not all sites processed
			    allCompleted = false;
			    shareEnd =
				    Math.min(shareStart + share,
					    allSites.size());
			    threads[i] =
				    new MyThread(i, allSites.subList(
					    shareStart, shareEnd),
					    functionsRole, roleDescription,
					    allowedFunctions,
					    disallowedFunctions, roleToRemove);
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
		+ ((System.currentTimeMillis() - start) / 1000) + " seconds");
	if (functionsRole != null) {
	    log.info("roleModified:       " + functionsRole);
	}
	if (roleToRemove != null) {
	    log.info("roleRemoved:        " + roleToRemove);
	}
    } // execute

    class MyThread extends Thread {
	List<Site> sites;
	String functionsRole;
	String roleDescription;
	String roleToRemove;
	List<String> allowedFunctions;
	List<String> disallowedFunctions;
	boolean completed = false;
	int threadNo;

	MyThread(int threadNo, List<Site> sites, String functionsRole,
		String roleDescription, List<String> allowedFunctions,
		List<String> disallowedFunctions, String roleToRemove) {
	    this.threadNo = threadNo;
	    setSiteList(sites);
	    setFunctionsRole(functionsRole);
	    setRoleDescription(roleDescription);
	    setRoleToRemove(roleToRemove);
	    setAllowedFunctions(allowedFunctions);
	    setDisallowedFunctions(disallowedFunctions);
	}

	private void setSiteList(List<Site> sites) {
	    this.sites = sites;
	}

	public void setFunctionsRole(String functionsRole) {
	    this.functionsRole = functionsRole;
	}

	public void setRoleDescription(String roleDescription) {
	    this.roleDescription = roleDescription;
	}

	public void setRoleToRemove(String roleToRemove) {
	    this.roleToRemove = roleToRemove;
	}

	public void setAllowedFunctions(List<String> allowedFunctions) {
	    this.allowedFunctions = allowedFunctions;
	}

	public void setDisallowedFunctions(List<String> disallowedFunctions) {
	    this.disallowedFunctions = disallowedFunctions;
	}

	public void run() {
	    completed = false;
	    log.debug("Thread #" + threadNo + " (" + getName() + ") Starting");
	    loginToSakai();
	    try {
		processSites(sites, functionsRole, roleDescription,
			allowedFunctions, disallowedFunctions, roleToRemove);
	    } catch (Exception e) {
		log.error("Thread #" + threadNo + " (" + getName()
			+ ") : Unable to process all sites without error: " + e);
		e.printStackTrace();
	    }
	    logoutFromSakai();
	    log.debug("Thread #" + threadNo + " Finished");
	    completed = true;
	}

	public boolean completed() {
	    return completed;
	}
    } // class MyThread

    private void processSites(List<Site> sites, String functionsRole,
	    String roleDescription, List<String> allowedFunctions,
	    List<String> disallowedFunctions, String roleToRemove) {
	boolean roleExists = false;
	Site site = null;
	AuthzGroup siteRealm = null;

	for (int i = 0; i < sites.size(); i++) {
	    site = sites.get(i);

	    if (adminConfigService.isIncludingFrozenSites()
		    || (!adminConfigService.isIncludingFrozenSites() && !getFrozenValue(site))) {

		if (!getFrozenValue(site)) {

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
				roleExists =
					isRoleInRealm(siteRealm, functionsRole);
				if (!roleExists) {
				    addRole(siteRealm, functionsRole,
					    roleDescription, allowedFunctions,
					    disallowedFunctions);
				    authzGroupService.save(siteRealm);
				} else {

				    Role role =
					    siteRealm.getRole(functionsRole);
				    role.setDescription(roleDescription);
				    // We add the new permissions
				    addPermissions(role, allowedFunctions);

				    // We remove the specified users
				    removePermissions(role, disallowedFunctions);
				}
			    }

			    // We remove the role
			    if (roleToRemove != null)
				removeRole(siteRealm, roleToRemove);
			    try {
				authzGroupService.save(siteRealm);
			    } catch (Exception e) {
				log.error("Unable to save changes for site "
					+ site.getId() + " : " + e);
				e.printStackTrace();

			    }

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

				    if (functionsRole != null) {
					// We check if the role with the
					// required
					// permissions exists in the group
					roleExists =
						isRoleInRealm(groupRealm,
							functionsRole);
					if (!roleExists) {
					    addRole(groupRealm, functionsRole,
						    roleDescription,
						    allowedFunctions,
						    disallowedFunctions);
					    authzGroupService.save(groupRealm);
					} else {

					    Role role =
						    groupRealm
							    .getRole(functionsRole);
					    role.setDescription(roleDescription);
					    // We add the new permissions
					    addPermissions(role,
						    allowedFunctions);

					    // We remove the specified users
					    removePermissions(role,
						    disallowedFunctions);
					}
				    }

				    // We remove the role
				    if (roleToRemove != null)
					removeRole(groupRealm, roleToRemove);
				    try {
					authzGroupService.save(groupRealm);
				    } catch (Exception e) {
					log.error("Unable to save changes for group "
						+ groupId + " : " + e);
					e.printStackTrace();
				    }

				} catch (GroupNotDefinedException e) {
				    log.error(e.getMessage());
				} catch (AuthzPermissionException e) {
				    log.error(e.getMessage());
				}
			    }
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
    private void addRole(AuthzGroup realm, String functionsRole,
	    String roleDescription, List<String> allowedFunctions,
	    List<String> disallowedFunctions) {
	try {
	    if (functionsRole != null) {
		Role role = realm.getRole(functionsRole);

		if (role == null)
		    role = realm.addRole(functionsRole);

		role.setDescription(roleDescription);

		for (Object function : allowedFunctions) {
		    if (!role.isAllowed((String) function))
			role.allowFunction((String) function);
		}

		for (Object function : disallowedFunctions) {
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

    private boolean isRoleInRealm(AuthzGroup realm, String functionsRole) {
	Role role = realm.getRole(functionsRole);
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
	super.loginToSakai("FunctionsSynchronizationJob");
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
}
