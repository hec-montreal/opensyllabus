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
package org.sakaiquebec.opensyllabus.admin.conversionJob.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiquebec.opensyllabus.admin.conversionJob.api.OsylConversionJob;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylConversionJobImpl  implements OsylConversionJob{

   
    
    private static Log log = LogFactory.getLog(OsylConversionJobImpl.class);
    
    private OsylSiteService osylSiteService;
    
    
    public void execute(JobExecutionContext jec) throws JobExecutionException {
	log.info("Start converting Course Outlines");
	loginToSakai();
	List<COSerialized> cos = osylSiteService.getAllCO();
	String configPath = 
		ServerConfigurationService.getString(OSYL_CONFIG_PATH_KEY,
			null);
	if(configPath==null)
	    configPath = System.getProperty("catalina.home")+File.separator+"webapps"+File.separator+"osyl-admin-sakai-tool";//Ugly but don't know cleaner method.
	for(COSerialized co : cos){
	    log.debug("Start conversion of co with co_id:"+co.getCoId());
	    try {
		osylSiteService.convertAndSave(configPath,co);
	    } catch (Exception e) {
		log.error("Could not convert co with co_id:"+co.getCoId());
		e.printStackTrace();
	    }
	    log.debug("Finish conversion of co with co_id:"+co.getCoId());
	}
	logoutFromSakai();
	log.info("Finish converting Course Outlines");
    }

    protected void loginToSakai() {
	Session sakaiSession = SessionManager.getCurrentSession();
	sakaiSession.setUserId("admin");
	sakaiSession.setUserEid("admin");

	// establish the user's session
	UsageSessionService.startSession("admin", "127.0.0.1", "COConv");

	// update the user's externally provided realm definitions
	AuthzGroupService.refreshUser("admin");

	// post the login event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGIN, null, true));
    }
    
    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }
    
    
    

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }


}

