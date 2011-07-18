/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;

/**
 * This service offers basic security services to the servlet, including roles,
 * permissions, restrictions and user preferences and session.
 * 
 * @author Claude.coulombe
 * @author Tom Landry
 */
public class OsylSecurityServiceImpl implements OsylSecurityService {

    /** Our logger */
    private static Log log = LogFactory.getLog(OsylSecurityServiceImpl.class);

    /** the authorisation service to be injected by Spring */
    private AuthzGroupService authzService;

    /** the site service to be injected by Spring */
    private SiteService siteService;

    /** the site service to be injected by Spring */
    private SecurityService securityService;

    /** the chs to be injected by Spring */
    private ContentHostingService contentHostingService;

    /** the session manager to be injected by Spring */
    private SessionManager sessionManager;

    /** the tool manager to be injected by Spring */
    private ToolManager toolManager;

    /**
     * Sets the <code>AuthzGroupService</code>.
     * 
     * @param authzService
     */
    public void setAuthzGroupService(AuthzGroupService authzService) {
	this.authzService = authzService;
    }

    /**
     * Sets the <code>SiteService</code>.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    /**
     * Sets the <code>SecurityyService</code>.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

    /**
     * Sets the <code>ContentHostingService</code>.
     * 
     * @param contentHostingService
     */
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    /**
     * Sets the <code>SessionManager</code>.
     * 
     * @param sessionManager
     */
    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    /**
     * Sets the <code>ToolManager</code>.
     * 
     * @param toolManager
     */
    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    /** Init method to be called by Spring */
    public void init() {
	log.info("INIT from SecurityService");
    }

    /** Destroy method to be called by Spring */
    public void destroy() {
	log.info("DESTROY from SecurityService");
    }

    /** {@inheritDoc} */
    public String filterContent(String inputXml, String role) {
	// TODO: not yet implemented
	return "yes";
    }

    /** {@inheritDoc} */
    public String getCurrentUserRole() {
	String role = null;
	ToolSession toolSession = sessionManager.getCurrentToolSession();

	if (securityService.isSuperUser())
	    role = OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN;
	else
	    role =
		    authzService.getUserRole(toolSession.getUserId(),
			    getSiteRealmID());

	return role;
    }

    /** {@inheritDoc} */
    public void setCurrentSessionActive() {
	Session session = sessionManager.getCurrentSession();
	session.setActive();
    }

    /** {@inheritDoc} */
    public void applyPermissions(String resourceId, String permission)
	    throws Exception {
	Site site =
		siteService.getSite(toolManager.getCurrentPlacement()
			.getContext());
	applyPermissions(site.getId(), resourceId, permission);

    }

    /** {@inheritDoc} */
    public void applyPermissions(String siteId, String resourceId,
	    String permission) throws Exception {
	try {
	    ResourceProperties properties =
		    contentHostingService.getProperties(resourceId);
	    boolean isCollection = false;

	    try {
		isCollection =
			properties
				.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
	    } catch (Exception e) {
		// assume isCollection is false if property is not set
	    }

	    if (isCollection) {
		ContentCollectionEdit edit =
			contentHostingService.editCollection(resourceId);
		if (!edit.getId().equals(
			contentHostingService.getSiteCollection(siteId))) {
		    if (SecurityInterface.ACCESS_PUBLIC.equals(permission)) {
			edit.setPublicAccess();
		    }
		} else {
		    log.warn("no public access of work directory allowed: "
			    + edit.getId());
		}

		contentHostingService.commitCollection(edit);
	    }

	    else {
		ContentResourceEdit edit =
			contentHostingService.editResource(resourceId);
		if (!resourceId.contains(contentHostingService
			.getSiteCollection(siteId))) {
		    if (SecurityInterface.ACCESS_PUBLIC.equals(permission)) {
			edit.setPublicAccess();
		    }
		} else {
		    // resource is in work directory, no public is allowed
		    log.warn("no public access in work directory allowed: "
			    + resourceId);
		}

		contentHostingService.commitResource(edit,
			NotificationService.NOTI_NONE);
	    }
	} catch (Exception e) {
	    log.error("Unable to apply permissions", e);
	    // We wrap the exception (could be IdUnusedException,
	    // OverQuotaException, etc.) in a java.lang.Exception.
	    // This way our "client" doesn't have to know about
	    // these exceptions.
	    throw new Exception(e);
	}
    }

    /** {@inheritDoc} */
    public void applyDirectoryPermissions(String directoryId) {
	// TODO: set permissions for directory
	// differ between work and publish directory
	if (directoryId.contains(OsylSiteService.WORK_DIRECTORY)) {

	} else {
	    // default folder permissions

	}
    }

    /**
     * Gets the actual site realm Id.
     * 
     * @return a String id of the group of the realm
     */
    private String getSiteRealmID() {
	return ("/site/" + toolManager.getCurrentPlacement().getContext());
    }

    public String getCurrentUserId() {
	return sessionManager.getCurrentSessionUserId();
    }

    public boolean isActionAllowedForCurrentUser(String permission) {
	String userSiteId =
		siteService.getUserSiteId(sessionManager
			.getCurrentSessionUserId());
	try {
	    Site s = siteService.getSite(userSiteId);
	    if (securityService.unlock(permission, s.getReference()))
		return true;
	    return false;
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean isActionAllowedInSite(String siteRef, String permission) {
	if (securityService.unlock(permission, siteRef)) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public boolean isCurrentUserASuperUser() {
	return securityService.isSuperUser();
    }
}
