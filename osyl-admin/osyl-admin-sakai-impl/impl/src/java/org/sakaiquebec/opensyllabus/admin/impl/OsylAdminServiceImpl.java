/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.admin.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserPermissionException;
import org.sakaiquebec.opensyllabus.admin.api.OsylAdminService;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.GenericMatriculeNomMapFactory;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMap;
import org.sakaiquebec.opensyllabus.admin.impl.extracts.MatriculeNomMapEntry;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylAdminServiceImpl implements OsylAdminService {

    /**
     * Directory used to store institutional data (students, teachers, courses,
     * sessions ...
     */
    protected static final String EXTRACTS_DIRECTORY = "extracts";

    /**
     * Map used to store information about users : name, id
     */
    private MatriculeNomMap matNomMap = null;

    /*
     * Map used to store information about the courses that a teacher gives:
     * teacher id, course id, course session, course periode
     */
    // private ProfCoursMap profCoursMap = null;

    /*
     * Map used to store information about the courses taken by a student:
     * student id, course id, course session, course period
     */
    // private EtudiantCoursMap etudCoursMap = null;

    /**
     * The value of the student status represented in our extracts
     */
    protected static final String USER_STATUS = "E";

    /**
     * Type of user considered as a student in the course site
     */
    protected static final String STUDENT_USER = "Student";

    /**
     * Type of user considered as a teacher in the course site
     */
    protected static final String TEACHER_USER = "Teacher";

    /**
     * Our logger
     */
    private static Log log = LogFactory.getLog(OsylAdminServiceImpl.class);

    /**
     * The name of opensyllabusAdmin site to be created.
     */
    private String osylAdminSiteName;

    /**
     * Sets the name of the site.
     * 
     * @param osylAdminSiteName
     */
    public void setOsylAdminSiteName(String osylAdminSiteName) {
	this.osylAdminSiteName = osylAdminSiteName;
    }

    /**
     * The name of the realm created at the site<s creation.
     */
    private String authzGroupName;

    /**
     * Sets the authz group name.
     * 
     * @param authzGroupName
     */
    public void setAuthzGroupName(String authzGroupName) {
	this.authzGroupName = authzGroupName;
    }

    /**
     * HashMap of all the roles to add to the site with their allowed functions.
     */
    private HashMap<String, List<String>> rolesToAdd;

    /**
     * Sets the map of roles to add to the site and the functions to allow for
     * each role.
     * 
     * @param rolesToAdd
     */
    public void setRolesToAdd(HashMap<String, List<String>> rolesToAdd) {
	this.rolesToAdd = rolesToAdd;
    }

    /**
     * Set of the opensyllabusAdmin specific functions to register.
     */
    private Set<String> functionsToRegister;

    /**
     * Sets the <code>Set</code> of functions to register.
     * 
     * @param functionsToRegister
     */
    public void setFunctionsToRegister(Set<String> functionsToRegister) {
	this.functionsToRegister = functionsToRegister;
    }

    /**
     * Sakai user session manager injected by Spring.
     */
    private SessionManager sessionManager;

    /**
     * Sets the <code>SessionManager</code> needed to create the site in the
     * init() method.
     * 
     * @param sessionManager
     */
    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

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
    
    private FunctionManager functionManager;
    
    public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }
    
    private SecurityService securityService;
    
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /** {@inheritDoc} */
    public void createUsers(String fileDirectory) {
	String directory = fileDirectory + File.separator + EXTRACTS_DIRECTORY;

	try {
	    matNomMap = GenericMatriculeNomMapFactory.getInstance(directory);
	    matNomMap = GenericMatriculeNomMapFactory.buildMap(directory);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	Iterator<MatriculeNomMapEntry> values = matNomMap.values().iterator();
	MatriculeNomMapEntry entry;
	UserEdit userEdit = null;
	Collection userExists = null;

	while (values.hasNext()) {
	    entry = (MatriculeNomMapEntry) values.next();
	    try {
		// We check if user already in table
		userExists =
			userDirService
				.findUsersByEmail(entry.getEmailAddress());
		if (userExists.size() == 0)
		    userEdit =
			    userDirService.addUser(null, entry.getMatricule());

	    } catch (UserIdInvalidException e) {
		log.error("Create users - user invalid id", e);
	    } catch (UserAlreadyDefinedException e) {
		log.error("Create users - user already defined", e);
	    } catch (UserPermissionException e) {
		log.error("Create users - permission exception", e);
	    }

	    if (userEdit != null) {
		userEdit.setEmail(entry.getEmailAddress());
		userEdit.setFirstName(entry.getFirstName());
		userEdit.setLastName(entry.getLastName());
		userEdit.setPassword(entry.getMatricule());
		if (USER_STATUS.equalsIgnoreCase(entry.getStatus()))
		    userEdit.setType(STUDENT_USER);
		else
		    userEdit.setType(TEACHER_USER);

		try {
		    userDirService.commitEdit(userEdit);
		} catch (UserAlreadyDefinedException e) {
		    log.error("Create users - user already defined", e);
		}
	    }
	}

    }

    /**
     * Init method called right after Spring injection.
     */
    public void init() {
	log
		.info("OsylAdminServiceImpl service init() site managerSiteName == \""
			+ this.osylAdminSiteName + "\"");

	if (null == this.osylAdminSiteName) {
	    // can't create
	    log.info("init() managerSiteName is null");
	} else if (siteService.siteExists(this.osylAdminSiteName)) {
	    // no need to create
	    log.info("init() site " + this.osylAdminSiteName
		    + " already exists");
	} else {
	    // need to create
	    try {
		enableSecurityAdvisor();
	    log.info("*** enableSecurityAdvisor() OsylAdminServiceImpl *** ");	    	
		Session s = sessionManager.getCurrentSession();
		s.setUserId(UserDirectoryService.ADMIN_ID);

		Site osylAdminSite =
			siteService.addSite(this.osylAdminSiteName, "project");
		osylAdminSite.setTitle("OpenSyllabus Admin");
		osylAdminSite.setPublished(true);
		osylAdminSite.setJoinable(false);

		AuthzGroup currentGroup =
		    authzGroupService.getAuthzGroup(
				this.authzGroupName);

		for (Iterator<String> iFunctionsToRegister =
			this.functionsToRegister.iterator(); iFunctionsToRegister
			.hasNext();) {
		    functionManager.registerFunction(iFunctionsToRegister
			    .next());
		}

		for (Iterator<Entry<String, List<String>>> iRolesToAdd =
			this.rolesToAdd.entrySet().iterator(); iRolesToAdd
			.hasNext();) {
		    Entry<String, List<String>> entry = iRolesToAdd.next();

		    Role roleToAdd = currentGroup.getRole(entry.getKey());

		    if (roleToAdd == null) {
			roleToAdd = currentGroup.addRole(entry.getKey());
		    }
		    roleToAdd.allowFunctions(entry.getValue());
		}
		authzGroupService.save(currentGroup);

		// add Resources tool
		SitePage page2 = osylAdminSite.addPage();
		page2.setTitle("Resources");
		page2.addTool("sakai.resources");

		// add OsylAdmin tool
		SitePage page = osylAdminSite.addPage();
		page.setTitle(this.osylAdminSiteName);
		page.addTool("sakai.opensyllabus.admin.tool");

		siteService.save(osylAdminSite);

		log.debug("init() site " + this.osylAdminSiteName
			+ " has been created");
	    } catch (IdInvalidException e) {
		log.warn("IdInvalidException ", e);
		e.printStackTrace();
	    } catch (IdUsedException e) {
		// we've already verified that the site doesn't exist but
		// this can occur if site was created by another server
		// in a cluster that is starting up at the same time.
		log.warn("IdUsedException ", e);
		e.printStackTrace();
	    } catch (PermissionException e) {
		log.warn("PermissionException ", e);
		e.printStackTrace();
	    } catch (IdUnusedException e) {
		log.warn("IdUnusedException ", e);
		e.printStackTrace();
	    } catch (GroupNotDefinedException e) {
		e.printStackTrace();
	    } catch (RoleAlreadyDefinedException e) {
		e.printStackTrace();
	    } catch (AuthzPermissionException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Establish a security advisor to allow the "embedded" azg work to occur
     * with no need for additional security permissions.
     */
    protected void enableSecurityAdvisor() {
	// put in a security advisor so we can create citationAdmin site without
	// need
	// of further permissions
	securityService.pushAdvisor(new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
            if ((userId != null) && (userId.equals(UserDirectoryService.ADMIN_ID))) {	    	
            	return SecurityAdvice.ALLOWED;
            }
            return SecurityAdvice.PASS;
	  }
	});
    }
}
