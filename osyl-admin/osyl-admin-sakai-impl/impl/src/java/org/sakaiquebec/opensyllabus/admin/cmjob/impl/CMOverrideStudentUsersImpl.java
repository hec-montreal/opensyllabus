/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2015 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.admin.cmjob.impl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CMOverrideStudentUsers;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */

public class CMOverrideStudentUsersImpl extends OsylAbstractQuartzJobImpl
		implements CMOverrideStudentUsers {

    private static Log log = LogFactory.getLog(CMOverrideStudentUsersImpl.class);

    private Set<String> changedEntries = null;
    
    private static String ROLE_GUEST = "Guest"; 
    
    private static boolean isRunning = false;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

//    	if (isRunning) {
//			log.warn("Stopping job since it's already running");
//			return;
//		}
//		isRunning = true;

		loginToSakai("CMOverrideStudentUsers");

		changedEntries = new HashSet<String>();

		List<Site> sites = null;

		List<String> terms = getActiveTerms();
		Map<String, String> criteria = null;
		String providerId = null;
		EnrollmentSet es = null;
		Section cmSection = null;
		boolean isProvided = false;
		Set<String> guestUsers = null;
		Set<String> authzGroupIds = null;
		String userMatricule = null;
		AuthzGroup azGroup = null;
		

		for (int i = 0; i < terms.size(); i++) {

		    criteria = new HashMap<String, String>();
		    criteria.put("term", terms.get(i));

		    sites =
			    siteService.getSites(SelectionType.ANY, "course", null, criteria, SiteService.SortType.NONE, null);
		    
		    for (Site site : sites) {
		    	guestUsers = site.getUsersHasRole(ROLE_GUEST);
		    	providerId = site.getProviderGroupId();
		    	if (cmService.isSectionDefined(providerId) && guestUsers != null && guestUsers.size() >0){
		    		
			    	for (String userId: guestUsers){
			    		try {
							userMatricule = userDirectoryService.getUserEid(userId);
							cmSection = cmService.getSection(providerId);
							es = cmSection.getEnrollmentSet();
							if (es == null)
								isProvided = false;
							else
								isProvided = cmService.isEnrolled(userMatricule, es.getEid());
				    		
				    		if (isProvided){
				    			authzGroupIds = authzGroupService.getAuthzGroupIds(providerId);
				    			for (String authzGroupId : authzGroupIds) {
				    				 try {
										azGroup = authzGroupService.getAuthzGroup(authzGroupId);
										azGroup.removeMember(userId);
										changedEntries.add(userMatricule);
										authzGroupService.save(azGroup);
									} catch (GroupNotDefinedException e) {
											e.printStackTrace();
									} catch (AuthzPermissionException e) {
											e.printStackTrace();
									}
				    			}
				    		}
							
						} catch (UserNotDefinedException e) {
							e.printStackTrace();
						}
			    		
			    	}
		    	}

		    }

		}

		log.info(changedEntries.toString() + " entries has been changed");
		log.info("The terms " + terms.toString() + " have been treated.");
		logoutFromSakai();

	}

}
