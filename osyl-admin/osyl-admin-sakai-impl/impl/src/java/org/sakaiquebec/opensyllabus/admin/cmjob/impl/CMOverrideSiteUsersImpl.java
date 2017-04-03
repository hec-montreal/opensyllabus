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
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CMOverrideSiteUsers;

import java.util.*;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class CMOverrideSiteUsersImpl extends OsylAbstractQuartzJobImpl
	implements CMOverrideSiteUsers {

    private static Log log = LogFactory.getLog(CMOverrideSiteUsersImpl.class);

    private static boolean isRunning = false;

    private Set<String> changedEntries = null;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

		if (isRunning) {
			log.warn("Stopping job since it's already running");
			return;
		}
		isRunning = true;

		loginToSakai("CMOverrideSiteUsers");

	changedEntries = new HashSet<String>();

	List<Site> sites = null;

	List<String> terms = getActiveTerms();
	Map<String, String> criteria = null;
	String siteProviderId = null;
	String [] providerIds = null;
	EnrollmentSet es = null;
	Set<String> officialInstructors = null;
	Set<String> authzGroupIds = null;
	String userRole = null;
	Set<Membership> sectionMemberships = null;
	Set<Membership> courseOffMemberships = null;
	AuthzGroup azGroup = null;
	

	for (int i = 0; i < terms.size(); i++) {

	    criteria = new HashMap<String, String>();
	    criteria.put("term", terms.get(i));

	    sites =
		    siteService.getSites(SelectionType.ANY, "course", null, criteria, SiteService.SortType.NONE, null);

	    for (Site site : sites) {

			siteProviderId = site.getProviderGroupId();
			providerIds = siteProviderId.split("\\+");
			for (String providerId: providerIds){
				if (cmService.isSectionDefined(providerId)|| cmService.isCourseOfferingDefined(providerId)) {
					authzGroupIds = authzGroupService.getAuthzGroupIds(providerId);

					for (String authzGroupId : authzGroupIds) {
						try {
							azGroup = authzGroupService.getAuthzGroup(authzGroupId);
							if (!providerId.endsWith("00")) {
								es = cmService.getEnrollmentSet(providerId);
								officialInstructors = es.getOfficialInstructors();

								for (String instructorEid : officialInstructors) {

									userRole = authzGroupService.getUserRole(userDirectoryService.getUserId(instructorEid), authzGroupId);

									if (!"Instructor".equalsIgnoreCase(userRole)) {
										azGroup.removeMember(userDirectoryService.getUserId(instructorEid));
										authzGroupService.save(azGroup);
										changedEntries.add(providerId + userRole + instructorEid);
									}


								}
							} else {
								// Check section membership
								sectionMemberships = cmService.getSectionMemberships(providerId);
								for (Membership sectionMember : sectionMemberships) {
									userRole = authzGroupService.getUserRole(userDirectoryService.getUserId(sectionMember.getUserId()), authzGroupId);
									if (!"Coordinator".equalsIgnoreCase(userRole)) {
										azGroup.removeMember(userDirectoryService.getUserId(sectionMember.getUserId()));
										authzGroupService.save(azGroup);
										changedEntries.add(providerId + userRole + sectionMember.getUserId());
									}
								}
								// Check courseoffering membership
								Section section = cmService.getSection(providerId);
								String courseOffEid = section.getCourseOfferingEid();
								courseOffMemberships = cmService.getCourseOfferingMemberships(courseOffEid);
								for (Membership courseOffMember : courseOffMemberships) {
									userRole = authzGroupService.getUserRole(userDirectoryService.getUserId(courseOffMember.getUserId()), authzGroupId);
									if (!"Coordinator".equalsIgnoreCase(userRole)) {
										azGroup.removeMember(userDirectoryService.getUserId(courseOffMember.getUserId()));
										authzGroupService.save(azGroup);
										changedEntries.add(providerId + userRole + courseOffMember.getUserId());
									}
								}

							}
						} catch (GroupNotDefinedException e1) {
							e1.printStackTrace();
						} catch (UserNotDefinedException e) {
							e.printStackTrace();
						} catch (AuthzPermissionException e) {
							e.printStackTrace();
						}
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
