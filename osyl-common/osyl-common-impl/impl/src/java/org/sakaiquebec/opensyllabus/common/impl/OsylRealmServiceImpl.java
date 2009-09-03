package org.sakaiquebec.opensyllabus.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.common.api.OsylRealmService;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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

/**
 * Implementation of the <code>OsylRealmService</code>
 * 
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina Bauer-Oppinger</a>
 * @version $Id: $
 */
public class OsylRealmServiceImpl implements OsylRealmService {

	private static final Log log = LogFactory.getLog(OsylRealmServiceImpl.class);

	private FunctionManager functionManager;
		
	private AuthzGroupService authService;
	
	private SessionManager sessionManager;
	
	/**
    * Name of realm from which new realm will be copied
    */
	private String parentRealm;
	/**
    * Name of new realm
    */
	private String newRealmName;
	/**
    * Set of all the roles to register in order to add them to the site
    */
	private Set<String> rolesToRegister;
    /**
     * Set of all the functions to register in order to allow them in roles that
     * need them.
     */
    private Set<String> functionsToRegister;
    /**
     * HashMap of the functions to allow in a particular role. The key is the
     * role and the value is a list of the functions to allow.
     */
    private HashMap<String, List<String>> functionsToAllow;
    /**
     * Boolean for recreating realm
     */
	private boolean recreate = false;
    /**
     * Boolean for audo ddl
     */
	private boolean autoDdl = true;

	public OsylRealmServiceImpl() {
	    super();
	    this.rolesToRegister = new HashSet<String>();
	    this.functionsToRegister = new HashSet<String>();
	    this.functionsToAllow = new HashMap<String, List<String>>();
	}
	
	/**
	 * Init method called at initialization of the bean.
	 */
	public void init() {
		log.info("INIT from Osyl realm service");
		// register new functions for permissions
		for (String function: this.functionsToRegister) {
		    functionManager.registerFunction(function);
		}
		
		if (autoDdl) {
			Session sakaiSession = sessionManager.getCurrentSession();
			String userId = sakaiSession.getUserId();
			try {
				sakaiSession.setUserId("admin");
				sakaiSession.setUserEid("admin");
            try {
            	AuthzGroup group = authService.getAuthzGroup(newRealmName);
            	if (group != null) {
            		if (recreate){
            		    authService.removeAuthzGroup(group);
            		}
            		else {            		   
            			return;
            		}
            	}
            } catch (GroupNotDefinedException e) {
               // no worries... must not be created yet.
            } catch (AuthzPermissionException e) {
               log.error("Failed to recreate realm.", e);
               return;
            }
   
            try {
            	AuthzGroup newRealm;
            	AuthzGroup parent = null;
            	if (parentRealm != null) {
            		parent = authService.getAuthzGroup(parentRealm);
            	}
            	if (parent == null) {
	            	newRealm = authService.addAuthzGroup(newRealmName);
	            }
	            else {
	            	// copy an existing AuthzGroup 
	            	newRealm = authService.addAuthzGroup(newRealmName, parent, null);
	            }
	            addRoles(newRealm);
	            addPermissions(newRealm);
	            authService.save(newRealm);            	
            	
            } catch (GroupNotDefinedException e) {
               throw new RuntimeException(e);
            } catch (AuthzPermissionException e) {
               throw new RuntimeException(e);
            } catch (GroupAlreadyDefinedException e) {
               throw new RuntimeException(e);
            } catch (GroupIdInvalidException e) {
               throw new RuntimeException(e);
            } catch (RoleAlreadyDefinedException e) {
                 throw new RuntimeException(e);
         }
         } finally {
            sakaiSession.setUserId(userId);
            sakaiSession.setUserEid(userId);
         }
      }
   }

	public void destroy() {
		log.info("DESTROY from Osyl realm service");
	}
	
	public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    public void setAuthzGroupService(AuthzGroupService authService) {
        this.authService = authService;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
	 * Sets the name of the realm from which the new
	 * realm should be copied
	 * 
	 * @param parentRealm
	 */
   public void setParentRealm(String parentRealm) {
      this.parentRealm = parentRealm;
   }

	/**
	 * Sets the name of the new realm 
	 * 
	 * @param newRealmName
	 */
   public void setNewRealmName(String newRealmName) {
      this.newRealmName = newRealmName;
   }

	/**
	 * Sets boolean if realm should be recreated
	 * 
	 * @param recreated
	 */
   public void setRecreate(boolean recreate) {
      this.recreate = recreate;
   }

	/**
	 * Sets auto ddl
	 * 
	 * @param autoDdl
	 */
   public void setAutoDdl(boolean autoDdl) {
      this.autoDdl = autoDdl;
   }
   
	/**
	 * Sets the set with names of roles to register
	 * 
	 * @param rolesToRegister
	 */
   public void setRolesToRegister(Set<String> rolesToRegister) {
       this.rolesToRegister.clear();
	   this.rolesToRegister.addAll(rolesToRegister);
   }   

	/**
	 * Sets the set with the functions to register
	 * 
	 * @param functionsToRegister
	 */
   public void setFunctionsToRegister(Set<String> functionsToRegister) {
       this.functionsToRegister.clear();
	   this.functionsToRegister.addAll(functionsToRegister);
   }
   
	/**
	 * Sets the HashMap with the name of the role and
	 * a list of allowed functions
	 * 
	 * @param functionsToAllow
	 */
   public void setFunctionsToAllow(HashMap<String, List<String>> functionsToAllow) {
       this.functionsToAllow.clear();
	   this.functionsToAllow.putAll(functionsToAllow);
   }
   
   /** {@inheritDoc} */
   public Set<String> getPermissionFunctions() {
	   return this.functionsToRegister;
   }  
   
   /** {@inheritDoc} */
   public String getSiteType() {
		int i = newRealmName.indexOf("template");
		if (i == -1)
			return newRealmName;
		else
			return newRealmName.substring(i+8);
   }
   
   /** {@inheritDoc} */
   @SuppressWarnings("unchecked")
   public Set<String> getRoles() {
	   Set<String> roles = new LinkedHashSet<String>();
	   try {
		   AuthzGroup group = authService.getAuthzGroup(newRealmName);
		   Set<Role> groupRoles = group.getRoles();
			   
		   for (Iterator<Role> iRoles = groupRoles.iterator(); 
				iRoles.hasNext();) {
				   roles.add(iRoles.next().getId());
		   }		
		} catch (GroupNotDefinedException e) {
			log.error("Error while getting roles from realm : " + e.getMessage());
		}
		return roles;  
   }
   
   /** {@inheritDoc} */
   @SuppressWarnings("unchecked")
   public void updateFunction(String function, Set<String> allowedRoles) {
	   try {
		AuthzGroup group = authService.getAuthzGroup(newRealmName);
		Set<Role> groupRoles = group.getRoles();
		for (Iterator<Role> iRoles = groupRoles.iterator(); 
			iRoles.hasNext();) {
			Role role = iRoles.next();
		    if (allowedRoles.contains(role.getId())) {
		    	role.allowFunction(function);
		    }
		    else {
		    	role.disallowFunction(function);
		    }
		}
		authService.save(group);
	   } catch (GroupNotDefinedException e) {
			log.error("Error while updating functions: Realm not found " 
					+ e.getMessage());
	   } catch (AuthzPermissionException e) {
		   log.error("No permission to update functions of realm.");
	   }	   
   }
   
   /** {@inheritDoc} */
   @SuppressWarnings("unchecked")
   public HashMap<String, List<String>> getFunctionsWithAllowedRoles() {
	   HashMap<String, List<String>> res = new HashMap<String, List<String>>();
	   try {
		   AuthzGroup group = authService.getAuthzGroup(newRealmName);
		   Set<Role> groupRoles = group.getRoles();
		   
		   for (Iterator<String> iFunctions = getPermissionFunctions().
				   iterator(); iFunctions.hasNext();) {
			    String function = iFunctions.next();
			    List<String> roles = new ArrayList<String>();
			    for (Iterator<Role> iRoles = groupRoles.iterator(); 
					iRoles.hasNext();) {
					Role role = iRoles.next();
				    if (role.isAllowed(function)) {
				    	roles.add(role.getId());
				    }
				}
			    res.put(function, roles);
		   }
	   } catch (GroupNotDefinedException e) {
			log.error("Error while getting functions with allowed roles: " +
					e.getMessage());
	   }
	   return res;
   }
   
   /**
    * Adds roles to the given realm
    * 
    * @param newRealm
	*            realm where functions should be allowed
	* @exception RoleAlreadyDefinedException
	*/
   private void addRoles(AuthzGroup newRealm) throws RoleAlreadyDefinedException {
		for (Iterator<String> iRolesToRegister =
			this.rolesToRegister.iterator(); iRolesToRegister
			.hasNext();) {
			String roleToRegister = iRolesToRegister.next();
			if (newRealm.getRole(roleToRegister) == null){
			    log.warn("new role registered: " + roleToRegister);
			    newRealm.addRole(roleToRegister);
			}
		}
	}

   /**
    * Adds permissions to the roles of the realm
    * 
    * @param newRealm
	*            realm where functions should be allowed
	*/
   private void addPermissions(AuthzGroup newRealm) {
	   for (Iterator<Entry<String, List<String>>> iFunctionsToAllow =
			   	getAllowedFunctions().entrySet().iterator(); iFunctionsToAllow
				.hasNext();) {
		    Entry<String, List<String>> entry =
			    iFunctionsToAllow.next();
		    Role role = newRealm.getRole(entry.getKey());
		    if (role != null) {
		    	role.allowFunctions(entry.getValue());
		    }
		}
	}
   
   /**
    * Load permissions from sakai.properties. If they are not set,
    * it will then use the spring value from components.xml
    * Updates functionsToAllow
    *      
	* @return HashMap map with modified permissions
	*/
   private HashMap<String, List<String>> getAllowedFunctions() {
	   if (functionsToAllow == null) {
		   functionsToAllow = new HashMap<String, List<String>>();;
	   }
	   for (Iterator<String> iFunctionsToRegister =
		   this.functionsToRegister.iterator(); iFunctionsToRegister
		   	.hasNext();) {
			String function=iFunctionsToRegister.next();
			String rolesString = org.sakaiproject.component.cover.ServerConfigurationService
		       .getString(function);
			
			if (rolesString != null) {
				String[] roles = rolesString.split(",");
				
				// remove old entries of this function
				for (Iterator<Entry<String, List<String>>> iFunctions =
					functionsToAllow.entrySet().iterator(); iFunctions
					.hasNext();) {
				    Entry<String, List<String>> entry =
					    iFunctions.next();
				    entry.getValue().remove(function);
				}
				
				// add function with role
				for (int i=0;i<roles.length;i++) {
					if (functionsToAllow.containsKey(roles[i].trim())) {
						functionsToAllow.get(roles[i].trim()).add(function);
					}
					else if (!roles[i].trim().equals("")){
						List<String> l = new ArrayList<String>();
						l.add(function);
						functionsToAllow.put(roles[i], l);
					}
				}
			}
	   }
	   return functionsToAllow;
   }
   
}