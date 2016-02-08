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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CMOverrideSiteUsers;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class CMOverrideSiteUsersImpl extends OsylAbstractQuartzJobImpl
	implements CMOverrideSiteUsers {

    private PropertyResourceBundle bundle = null;

    private static Log log = LogFactory.getLog(CMOverrideSiteUsersImpl.class);

    private Set<String> changedEntries = null;

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    protected ContentHostingService contentHostingService = null;

    public void setContentHostingService(ContentHostingService service) {
	contentHostingService = service;
    }

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

	loginToSakai("CMOverrideSiteUsers");

	changedEntries = new HashSet<String>();

	List<Site> sites = null;

	List<String> terms = getTerms();
	Map<String, String> criteria = null;
	String providerId = null;
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
		providerId = site.getProviderGroupId();
		if (cmService.isSectionDefined(providerId)|| cmService.isCourseOfferingDefined(providerId)) {
		    authzGroupIds = authzGroupService.getAuthzGroupIds(providerId);

		    for (String authzGroupId : authzGroupIds) {
			try {
			    azGroup = authzGroupService.getAuthzGroup(authzGroupId);
			    if (!providerId.endsWith("00")) {
				es = cmService.getEnrollmentSet(providerId);
				officialInstructors = es.getOfficialInstructors();
				
				for (String instructorEid : officialInstructors) {

					userRole = authzGroupService.getUserRole(userDirectoryService.getUserId(instructorEid),	authzGroupId);

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
					    changedEntries.add(providerId + userRole +courseOffMember.getUserId());
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

	log.info(changedEntries.toString() + " entries has been changed");
	log.info("The terms " + terms.toString() + " have been treated.");
	logoutFromSakai();
    }

    
    /**
     * Look for the session closest to the end of the current session.
     * Logically it is always the next, done like this because the next
     * session ( or period) can start the next day or next week.
     * @return a list of session titles
     */
    public List<String> getTerms() {
	List<String> terms = new ArrayList<String>();

	//From the logic implemented in OsylCMJob the list always contains one element
	List<AcademicSession> currentSessions = cmService.getCurrentAcademicSessions();
	AcademicSession currentSession = currentSessions.get(0);
	terms.add(currentSession.getTitle());

	long interval = -1;
	List<AcademicSession> allSessions = cmService.getAcademicSessions();

	AcademicSession nextAS = allSessions.get(0);

	for (AcademicSession as: allSessions){
	    interval = Math.abs(as.getStartDate().getTime() - currentSession.getEndDate().getTime());
	    if ( (interval< Math.abs((nextAS.getStartDate().getTime()-currentSession.getEndDate().getTime()))))
		nextAS = as;
	}
	
	if (nextAS != null)
	    terms.add(nextAS.getTitle());
	log.info(terms);
	return terms;
    }
    
  

}
