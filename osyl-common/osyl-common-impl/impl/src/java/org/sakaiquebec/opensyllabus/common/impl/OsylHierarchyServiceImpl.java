/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.common.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.common.api.OsylHierarchyService;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylHierarchyServiceImpl implements OsylHierarchyService {

	/** The chs to be injected by Spring */
	private ContentHostingService contentHostingService;

	/**
	 * Sets the <code>ContentHostingService</code>.
	 * 
	 * @param contentHostingService
	 */
	public void setContentHostingService(
			ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}

	/** The site service to be injected by Spring */
	private SiteService siteService;

	/**
	 * Sets the <code>SiteService</code>.
	 * 
	 * @param siteService
	 */
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	private static final Log log = LogFactory
			.getLog(OsylHierarchyServiceImpl.class);

	/** the authorisation service to be injected by Spring */
	private AuthzGroupService authzService;

	/**
	 * Sets the <code>AuthzGroupService</code>.
	 * 
	 * @param authzService
	 */
	public void setAuthzGroupService(AuthzGroupService authzService) {
		this.authzService = authzService;
	}

	/**
	 * Injection of the CORelationDao
	 */
	private CORelationDao coRelationDao;

	/**
	 * Sets the {@link CORelationDao}.
	 * 
	 * @param configDao
	 */
	public void setCoRelationDao(CORelationDao relationDao) {
		this.coRelationDao = relationDao;
	}

	/**
	 * Init method called at initialization of the bean.
	 */
	public void init() {
		log.info("INIT from OsylHierarchy service");

	}

	/**
	 * Retrieve the realm id associated to the site.
	 * 
	 * @param siteId
	 * @return
	 */
	private String getRealmId(String siteId) {
		String realmId = null;

		realmId = REALM_ID_PREFIX + siteId;

		return realmId;
	}

	/** {@inheritDoc} */
	public boolean hasChildRole(String siteId) {
		boolean hasRole = false;

		try {
			AuthzGroup group = authzService.getAuthzGroup(getRealmId(siteId));

			if (group.getRole(CHILD_ROLE) != null)
				hasRole = true;
		} catch (GroupNotDefinedException e) {
			log.error(e.getMessage());
		}

		return hasRole;
	}

	/** {@inheritDoc} */
	public void addChildRole(String siteId) {

		try {
			AuthzGroup group = authzService.getAuthzGroup(getRealmId(siteId));

			if (group.getRole(CHILD_ROLE) == null) {
				Role childRole = group.addRole(CHILD_ROLE);
				childRole.allowFunction(CONTENT_READ_PERMISSION);
				authzService.save(group);
			}

		} catch (GroupNotDefinedException e) {
			log.error(e.getMessage());
		} catch (RoleAlreadyDefinedException e) {
			log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
			log.error(e.getMessage());
		}

	}

	public void addUsersWithChildRole(String parentSiteId, String childSiteId) {
		// We retrieve all the users in the child site
		Set<String> childSiteUsers = null;

		try {
			AuthzGroup childSiteGroup = authzService
					.getAuthzGroup(getRealmId(childSiteId));

			AuthzGroup parentSiteGroup = authzService
					.getAuthzGroup(getRealmId(parentSiteId));

			// We retrieve all the ACTIVE users of the child site
			childSiteUsers = childSiteGroup.getUsers();
			for (Iterator<String> users = childSiteUsers.iterator(); users
					.hasNext();) {
				String user = (String) users.next();

				// If the user is not already of the parent site we add
				// him with the role CHILD
				if (parentSiteGroup.getMember(user) == null)
					parentSiteGroup.addMember(user, CHILD_ROLE, true, false);
			}

			authzService.save(parentSiteGroup);
		} catch (GroupNotDefinedException e) {
			log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
			log.error(e.getMessage());
		}

	}

	/** {@inheritDoc} */
	public void addOrUpdateUsers(String siteId) {

		// We retrieve the ancestors of the course outline
		List<CORelation> ancestors = coRelationDao
				.getCourseOutlineAncestors(siteId);
		String child = null;
		String parent = null;
		CORelation relation = null;

		for (int i = 0; i < ancestors.size(); i++) {
			relation = ancestors.get(i);
			parent = relation.getParent();
			child = relation.getChild();
			System.out.println("parent " + parent + " child " + child);

			// We check if the parent has the child role, if not, we add it
			if (!hasChildRole(parent))
				addChildRole(parent);

			// We add the users
			addUsersWithChildRole(parent, child);
		}
	}

	/** {@inheritDoc} */
	public void removeUsers(String parentSiteId, String childSiteId) {

		// We retrieve the ancestors or parent site where the following actions will be
		// performed
		List<CORelation> ancestors = coRelationDao
				.getCourseOutlineAncestors(childSiteId);
		List<CORelation> children = null;
		CORelation relation = null;
		CORelation childParent = null;
		for (int i = 0; i < ancestors.size(); i++) {
			relation = ancestors.get(i);
			System.out.println(" parent " + relation.getParent() + " child " + relation.getChild());
			// We remove all the users that have CHILD role in the parent site
			removeUsersWithChildRole(relation.getParent());
			
			// We retrieve all the children of the parent site
			try {
				children = coRelationDao.getCourseOutlineChildren(relation.getParent());
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
			System.out.println(" enfants " + children.size());
			
			// For all the chidren of the parent site (except the child to be
			// removed) we add the users to the parent site
			for (int j=0; j<children.size(); j++){
				childParent = children.get(j);
				if (!childParent.getChild().equals(childSiteId)){
					System.out.println(" enfant " + childParent.getChild());
					addUsersWithChildRole(parentSiteId, childSiteId);
				}
				
			}
		}

	}

	public void removeUsersWithChildRole(String parentSiteId) {

		try {

			AuthzGroup parentSiteGroup = authzService
					.getAuthzGroup(getRealmId(parentSiteId));

			// We retrieve all the ACTIVE users of the current site
			Set<String> parentSiteUsers = parentSiteGroup.getUsers();
			for (Iterator<String> users = parentSiteUsers.iterator(); users
					.hasNext();) {
				String user = (String) users.next();
				Member member = parentSiteGroup.getMember(user);
				String memberRoleId = null;

				// If the user is not already of the parent site we add
				// him with the role CHILD
				if (parentSiteGroup.getMember(user) != null) {
					memberRoleId = member.getRole().getId();
					if (CHILD_ROLE.equals(memberRoleId)) {
						parentSiteGroup.removeMember(user);
					}
				}
			}

			authzService.save(parentSiteGroup);
		} catch (GroupNotDefinedException e) {
			log.error(e.getMessage());
		} catch (AuthzPermissionException e) {
			log.error(e.getMessage());
		}

	}

}
