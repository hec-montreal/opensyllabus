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

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreatePrintVersionJob;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreatePrintVersionJobImpl implements CreatePrintVersionJob {

    private static Log log = LogFactory.getLog(CreatePrintVersionJobImpl.class);
    
    private OsylPublishService osylPublishService;
    
    public void setOsylPublishService(OsylPublishService osylPublishService) {
        this.osylPublishService = osylPublishService;
    }
    
    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }
    
    private ContentHostingService contentHostingService;
    
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }
    

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	log.info("Start converting Course Outlines");
	long start = System.currentTimeMillis();
	loginToSakai();

	final List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, "course",
			null, null, SiteService.SortType.NONE, null);
	
	String configPath = 
		ServerConfigurationService.getString(OSYL_CONFIG_PATH_KEY,
			null);
	if(configPath==null)
	    configPath = System.getProperty("catalina.home")+File.separator+"webapps"+File.separator+"osyl-admin-sakai-tool";//Ugly but don't know cleaner method.
	
	for(Site site : allSites){
	    	String osylToolName =
		    ToolManager.getTool("sakai.opensyllabus.tool")
			    .getTitle();
	    	String directory =
		    ContentHostingService.ATTACHMENTS_COLLECTION
			    + site.getTitle() + "/" + osylToolName
			    + "/";
	    	try {
		    contentHostingService.getResource(directory + "osylPrintVersion.pdf");
		    contentHostingService.removeResource(directory + "osylPrintVersion.pdf");
		} catch (IdUnusedException idue) {
		    // pdf does not exist, nothing to do
		} catch (Exception e) {
		    log.error("Unable to delete " + directory + "osylPrintVersion.pdf", e);
		}
	    
	    try {
		osylPublishService.createPublishPrintVersion(site.getId(), configPath+File.separator);
	    } catch (PdfGenerationException e) {
		log.error("Could not create pdf for site '"+site.getId()+"'", e);
	    }
	}
	logoutFromSakai();
	log.info("Finished converting Course Outlines in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    protected void loginToSakai() {
	Session sakaiSession = SessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	UsageSessionService.startSession("admin", "127.0.0.1", "CMSync");

	// update the user's externally provided realm definitions
	AuthzGroupService.refreshUser("admin");

	// post the login event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }
    
    protected void logoutFromSakai() {
	// post the logout event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }

}
