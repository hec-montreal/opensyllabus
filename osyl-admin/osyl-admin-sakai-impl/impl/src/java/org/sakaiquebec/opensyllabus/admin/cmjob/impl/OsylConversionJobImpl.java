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
package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylConversionJob;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylConversionJobImpl extends OsylAbstractQuartzJobImpl implements
	OsylConversionJob {

    private static Log log = LogFactory.getLog(OsylConversionJobImpl.class);

    public void execute(JobExecutionContext jec) throws JobExecutionException {
	log.info("Start converting Course Outlines");
	long start = System.currentTimeMillis();
	loginToSakai();
	List<COSerialized> cos = osylSiteService.getAllCO();
	String configPath =
		ServerConfigurationService
			.getString(OSYL_CONFIG_PATH_KEY, null);
	if (configPath == null)
	    configPath =
		    System.getProperty("catalina.home") + File.separator
			    + "webapps" + File.separator
			    + "osyl-admin-sakai-tool";// TODO SAKAI-860
	// cleaner method.
	for (COSerialized co : cos) {
	    log.debug("Start conversion of co with co_id:" + co.getCoId());
	    try {
		osylSiteService.convertAndSave(configPath, co);
	    } catch (Exception e) {
		log.error("Could not convert co with co_id:" + co.getCoId());
		e.printStackTrace();
	    }
	    log.debug("Finished conversion of co with co_id:" + co.getCoId());
	}
	logoutFromSakai();
	log.info("Finished converting Course Outlines in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    protected void loginToSakai() {
	super.loginToSakai("OsylConversionJob");
    }
}
