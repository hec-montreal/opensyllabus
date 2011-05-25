/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylAbstractQuartzJob;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;

/**
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public abstract class OsylAbstractQuartzJobImpl implements
	OsylAbstractQuartzJob {
    
    private static Log log = LogFactory
    .getLog(OsylAbstractQuartzJobImpl.class);
    
    // Fields and methods for spring injection
    protected ConfigurationService adminConfigService;
    protected AuthzGroupService authzGroupService;
    protected CourseManagementAdministration cmAdmin;
    protected CourseManagementService cmService;
    protected ContentHostingService contentHostingService;
    protected CORelationDao coRelationDao;
    protected EmailService emailService;
    protected EventTrackingService eventTrackingService;
    protected OsylContentService osylContentService;
    protected OsylDirectoryService osylDirectoryService;
    protected OsylManagerService osylManagerService;
    protected OsylPublishService osylPublishService;
    protected OsylSiteService osylSiteService;
    protected ResourceDao resourceDao;
    protected ServerConfigurationService serverConfigService;
    protected SessionManager sessionManager;
    protected SiteService siteService;
    protected ToolManager toolManager;
    protected UsageSessionService usageSessionService;
    protected UserDirectoryService userDirectoryService;

    public void setAdminConfigService(ConfigurationService adminConfigService) {
	this.adminConfigService = adminConfigService;
    }

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    public void setCmAdmin(CourseManagementAdministration cmAdmin) {
	this.cmAdmin = cmAdmin;
    }

    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    public void setCoRelationDao(CORelationDao coRelationDao) {
	this.coRelationDao = coRelationDao;
    }

    public void setEmailService(EmailService emailService) {
	this.emailService = emailService;
    }

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }
    
    public void setOsylContentService(OsylContentService osylContentService) {
	this.osylContentService = osylContentService;
    }

    public void setOsylDirectoryService(
	    OsylDirectoryService osylDirectoryService) {
	this.osylDirectoryService = osylDirectoryService;
    }

    public void setOsylManagerService(OsylManagerService osylManagerService) {
	this.osylManagerService = osylManagerService;
    }

    public void setOsylPublishService(OsylPublishService osylPublishService) {
	this.osylPublishService = osylPublishService;
    }

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    public void setResourceDao(ResourceDao resourceDao) {
	this.resourceDao = resourceDao;
    }

    /**
     * @param serverConfigService the new value of serverConfigService.
     */
    public void setServerConfigService(
    	ServerConfigurationService serverConfigService) {
        this.serverConfigService = serverConfigService;
    }

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    public void setToolManager(ToolManager toolManager) {
	this.toolManager = toolManager;
    }

    public void setUsageSessionService(UsageSessionService usageSessionService) {
	this.usageSessionService = usageSessionService;
    }

    public void setUserDirectoryService(
	    UserDirectoryService userDirectoryService) {
	this.userDirectoryService = userDirectoryService;
    }
    // ENDOF spring injection

    /**
     * 
     */
    public abstract void execute(JobExecutionContext arg0) throws JobExecutionException;

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai(String sessionName) {
	Session sakaiSession = sessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	usageSessionService.startSession("admin", "127.0.0.1",
		sessionName);

	// update the user's externally provided realm definitions
	authzGroupService.refreshUser("admin");

	// post the login event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	eventTrackingService.post(eventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
	usageSessionService.logout();
    }
    


    protected boolean getFrozenValue(Site site) {
	ResourcePropertiesEdit rp = site.getPropertiesEdit();
	boolean coIsFrozen = false;
	if (rp.getProperty(PROP_SITE_ISFROZEN) != null) {
	    if (rp.getProperty(PROP_SITE_ISFROZEN).equals("true")) {
		coIsFrozen = true;
		log.info("Site frozen: " + site.getTitle());
	    }
	}
	return coIsFrozen;
    }
}