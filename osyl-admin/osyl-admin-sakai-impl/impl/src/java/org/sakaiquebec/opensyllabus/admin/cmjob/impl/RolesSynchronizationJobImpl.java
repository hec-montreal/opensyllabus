package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.*;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.RolesSynchronizationJob;

import java.util.*;
import java.util.Map.Entry;

public class RolesSynchronizationJobImpl extends OsylAbstractQuartzJobImpl
        implements RolesSynchronizationJob {

    private static Log log = LogFactory
            .getLog(RolesSynchronizationJobImpl.class);

    private List<Site> allSites;

    private List<ConfigRole> rolesToConfig = null;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        loginToSakai();

        long start = System.currentTimeMillis();
        log.info("Starting");

        // Retrieve information from the xml file
        init();

        Site site = null;
        AuthzGroup siteRealm = null;

        if (rolesToConfig != null) {

            allSites = siteService.getSites(SiteService.SelectionType.ANY,
                    COURSE_SITE, null, null,
                    SiteService.SortType.NONE, null);

            //Do templates first
            // Check role in template realm
            try {
                //hec-template
                AuthzGroup templateRealm =
                        authzGroupService.getAuthzGroup(TEMPLATE_ID);
                addOrUpdateRoles(templateRealm);
                //!group.template.course
                templateRealm = authzGroupService.getAuthzGroup(GROUP_TEMPLATE_ID);
                addOrUpdateRoles(templateRealm);

            } catch (GroupNotDefinedException e) {
                e.printStackTrace();
            }


            for (int i = 0; i < allSites.size(); i++) {
                site = allSites.get(i);
                if (site.getTools("sakai.tenjin").size() > 0) {
                    try {
                        siteRealm =
                                authzGroupService
                                        .getAuthzGroup(REALM_PREFIX
                                                + site.getId());
                        addOrUpdateRoles(site);
                        log.info("Le site à traiter est " + site.getId());
                    } catch (GroupNotDefinedException e) {
                        log.error(e.getMessage());
                    }

                }

                log.info("completed in " + (System.currentTimeMillis() - start)
                        + " ms");
            }
        }

        logoutFromSakai();

    }


    private void addOrUpdateRoles(Site site) {

        AuthzGroup siteRealm = null;
        Collection<Group> siteGroups = null;

        for (ConfigRole configRole : rolesToConfig) {

            // remove users in course management
            if (configRole.getCourseManagement()) {
                removeUsersInCM(configRole.getRemovedUsers(), configRole.getAddedUsers());
            }

            if (configRole.isUpdateGroup()) {
                siteGroups = site.getGroups();
                for (Group group : siteGroups) {
                    processRealm(group, configRole);

                    try {
                        siteService.saveGroupMembership(site);
                        log.info("Le groupe " + group.getId() + " a été traité");
                    } catch (IdUnusedException e) {
                        e.printStackTrace();
                    } catch (PermissionException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    siteRealm =
                            authzGroupService
                                    .getAuthzGroup(REALM_PREFIX
                                            + site.getId());
                    processRealm(siteRealm, configRole);

                    authzGroupService.save(siteRealm);
                } catch (GroupNotDefinedException e) {
                    e.printStackTrace();
                } catch (AuthzPermissionException e) {
                    e.printStackTrace();

                }
            }

        }


    }

    private void addOrUpdateRoles(AuthzGroup siteRealm) {


        for (ConfigRole configRole : rolesToConfig) {
            // remove users in course management
            if (configRole.getCourseManagement()) {
                removeUsersInCM(configRole.getRemovedUsers(), configRole.getAddedUsers());
            }
            processRealm(siteRealm, configRole);

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
                addFunctions(siteRealm, configRole.getConfigRole(),
                        configRole.getFunctions());
                if (configRole.getRemoveFunctions() != null && configRole.getRemoveFunctions().size() > 0)
                    removeFunctions(siteRealm, configRole.getConfigRole(), configRole.getRemoveFunctions());
            }
            // We add the new users
            addUsers(siteRealm, configRole.getConfigRole(),
                    configRole.getAddedUsers());


            // We remove the specified users and replace them with new users
            removeOrRemoveUsers(siteRealm, configRole.getConfigRole(),
                    configRole.getRemovedUsers(),
                    configRole.getReplacedUsers(),
                    configRole.getCourseManagement());

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
        List<String> removeFunctions;
        List<String> replacedUsers;
        boolean updateGroup;
        boolean includingFrozenSites;
        boolean includingDirSites;
        boolean courseManagement;
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
            removeFunctions =
                    (List<String>) values.get(ConfigurationService.REMOVE_FUNCTIONS);
            description = (String) values.get(ConfigurationService.DESCRIPTION);
            replacedUsers =
                    (List<String>) values
                            .get(ConfigurationService.REPLACEDUSERS);
            courseManagement =
                    ((Boolean) values.get(ConfigurationService.COURSEMANAGEMENT))
                            .booleanValue();
            includingFrozenSites = ((Boolean) values
                    .get(ConfigurationService.INCLUDING_FROZEN_SITES))
                    .booleanValue();
            includingDirSites = ((Boolean) values
                    .get(ConfigurationService.INCLUDING_DIR_SITES))
                    .booleanValue();

            updateGroup = ((Boolean) values
                    .get(ConfigurationService.UPDATE_GROUP))
                    .booleanValue();
            configRole = new ConfigRole();
            configRole.setConfigRole(role);
            configRole.setDescription(description);
            configRole.setAddedUsers(addedUsers);
            configRole.setRemovedUsers(removedUsers);
            configRole.setFunctions(functions);
            configRole.setIncludingFrozenSites(includingFrozenSites);
            configRole.setIncludingDirSites(includingDirSites);
            configRole.setReplacedUsers(replacedUsers);
            configRole.setCourseManagement(courseManagement);
            configRole.setUpdateGroup(updateGroup);
            configRole.setRemoveFunctions(removeFunctions);


            rolesToConfig.add(configRole);
        }
    }

    /**
     * Checks if the permissions are associated to the role.
     *
     * @param realm
     */
    private void addFunctions(AuthzGroup realm, String configRole,
                              List<String> functions) {
        Role role = realm.getRole(configRole);

        for (Object function : functions) {
            if (!role.isAllowed((String) function))
                role.allowFunction((String) function);
        }
    }

    /**
     * remove permissions of they are associated to the role.
     *
     * @param realm
     */
    private void removeFunctions(AuthzGroup realm, String configRole,
                                 List<String> functions) {
        Role role = realm.getRole(configRole);

        for (Object function : functions) {
            if (role.isAllowed((String) function))
                role.disallowFunction((String) function);
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

        } catch (RoleAlreadyDefinedException e) {
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
    private void removeOrRemoveUsers(AuthzGroup realm, String configRole,
                                     List<String> removedUsers, List<String> replacedUsers,
                                     boolean courseManagement) {

        String providerId = realm.getProviderGroupId();

        if (removedUsers.size() > 0) {
            String userId = null;
            for (String user : removedUsers) {
                try {
                    userId = userDirectoryService.getUserId(user);
                    //Remove user in the site
                    if (realm.hasRole(userId, configRole)) {
                        realm.removeMember(userId);
                    }

                } catch (UserNotDefinedException e) {
                    log.error("The user " + user
                            + " is not available in the system");
                }
            }
        }
    }

    /*
     * Used only on secretaries.
     */
    private void removeUsersInCM(List<String> removedUsers, List<String> replacedUsers) {
        Map<String, String> courseOffMembers = null;
        Map<String, String> sectionMembers = null;
        CourseOffering courseOff = null;
        Section section = null;
        Set<String> ids = null;
        boolean removed = false;

        for (String matricule : removedUsers) {
            //Remove users from course offerings
            courseOffMembers = cmService.findCourseOfferingRoles(matricule);
            ids = courseOffMembers.keySet();
            for (String id : ids) {
                removed = cmAdmin.removeCourseOfferingMembership(matricule, id);
                if (removed)
                    log.info("The user " + matricule + " has been removed from the course offering " + id);
            }

            //Remove users from sections
            sectionMembers = cmService.findSectionRoles(matricule);
            ids = sectionMembers.keySet();
            for (String id : ids) {
                removed = cmAdmin.removeSectionMembership(matricule, id);
                if (removed)
                    log.info("The user " + matricule + " has been removed from the section " + id);
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

                } catch (UserNotDefinedException e) {
                    log.error("The user " + user
                            + " is not available in the system");
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

        private List<String> removeFunctions;

        private boolean courseManagement;

        private List<String> replacedUsers;

        public List<String> getRemoveFunctions() {
            return removeFunctions;
        }

        public void setRemoveFunctions(List<String> removeFunctions) {
            this.removeFunctions = removeFunctions;
        }

        private boolean includingFrozenSites;

        private boolean includingDirSites;

        public boolean isUpdateGroup() {
            return updateGroup;
        }

        public void setUpdateGroup(boolean updateGroup) {
            this.updateGroup = updateGroup;
        }

        private boolean updateGroup;

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

        public boolean getCourseManagement() {
            return courseManagement;
        }

        public void setCourseManagement(boolean courseManagement) {
            this.courseManagement = courseManagement;
        }

        public List<String> getReplacedUsers() {
            return replacedUsers;
        }

        public void setReplacedUsers(List<String> replacedUsers) {
            this.replacedUsers = replacedUsers;
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
