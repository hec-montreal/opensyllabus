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
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
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
        String siteProviderId = null;
        String[] providerIds = null;
        Set<String> authzGroupIds = null;

        try {
            for (int i = 0; i < terms.size(); i++) {

                Map<String, String> criteria = new HashMap<String, String>();
                criteria.put("term", terms.get(i));

                sites = siteService.getSites(SelectionType.ANY, "course", null, criteria, SiteService.SortType.NONE, null);

                for (Site site : sites) {

                    siteProviderId = site.getProviderGroupId();
                    if (siteProviderId == null)
                        continue;

                    providerIds = siteProviderId.split("\\+");
                    for (String providerId : providerIds) {
                        if (cmService.isSectionDefined(providerId) || cmService.isCourseOfferingDefined(providerId)) {
                            authzGroupIds = authzGroupService.getAuthzGroupIds(providerId);

                            for (String authzGroupId : authzGroupIds) {
                                try {
                                    AuthzGroup azGroup = authzGroupService.getAuthzGroup(authzGroupId);

                                    Set<Membership> memberships = cmService.getSectionMemberships(providerId);
                                    Section section = cmService.getSection(providerId);
                                    String courseOffEid = section.getCourseOfferingEid();
                                    memberships.addAll(cmService.getCourseOfferingMemberships(courseOffEid));

                                    for (Membership member : memberships) {
                                        String userId = userDirectoryService.getUserId(member.getUserId());
                                        String azRole = authzGroupService.getUserRole(userId, authzGroupId);

                                        String membershipRole = null;
                                        if (member.getRole().equals("C")) {
                                            membershipRole = "Coordinator";
                                        } else if (member.getRole().equals("CI")) {
                                            membershipRole = "Coordinator-Instructor";
                                        } else if (member.getRole().equals("I")) {
                                            membershipRole = "Instructor";
                                        }

                                        if (null != azRole && null != membershipRole && !membershipRole.equals(azRole)) {
                                            log.info("Remove user " + member.getUserId() + " with role " + azRole + " from authzGroup " + azGroup.getId());
                                            azGroup.removeMember(userId);
                                            authzGroupService.save(azGroup);
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                        }
                    }
                }

            }

            log.info("The terms " + terms.toString() + " have been treated.");
            logoutFromSakai();
        } finally {
            isRunning = false;
        }
    }
}
