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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreatePrintVersionJob;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreatePrintVersionJobImpl extends OsylAbstractQuartzJobImpl
	implements CreatePrintVersionJob {

    private static Log log = LogFactory.getLog(CreatePrintVersionJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	log.info("Start converting Course Outlines");
	long start = System.currentTimeMillis();
	loginToSakai();

	final List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY,
			COURSE_SITE, null, null, SiteService.SortType.NONE,
			null);

	// Getting configuration (and defaulting to safe value if needed)
	Map<String, String> configMap = adminConfigService.getPrintVersionJobParams();
	boolean includeDirSites;
	try {
	    includeDirSites = TRUE.equals(configMap.get(
			ConfigurationService.INCLUDING_DIR_SITES));
	} catch (Exception e) {
	    log.warn("Unable to get configuration, using default: directory" +
	    		" sites are not included");
	    includeDirSites = false;
	}
	if (includeDirSites) {
	    allSites.addAll(siteService
		    .getSites(SiteService.SelectionType.ANY, DIRECTORY_SITE,
			    null, null, SiteService.SortType.NONE, null));
	}

	String configPath =
		serverConfigService.getString(OSYL_CONFIG_PATH_KEY, null);
	if (configPath == null)
	    configPath =
		    System.getProperty("catalina.home") + File.separator
			    + "webapps" + File.separator
			    + "osyl-admin-sakai-tool";// Ugly but don't know
						      // cleaner method.

	boolean includeFrozenSites;
	try {
	    includeFrozenSites = TRUE.equals(configMap.get(
			ConfigurationService.INCLUDING_FROZEN_SITES));
	} catch (Exception e) {
	    log.warn("Unable to get configuration, using default: frozen" +
	    		" sites are not included");
	    includeFrozenSites = false;
	}
	
	for (Site site : allSites) {

	    if (includeFrozenSites
		    || (!includeFrozenSites && !getFrozenValue(site))) {

		String directory =
			ContentHostingService.ATTACHMENTS_COLLECTION
				+ site.getTitle()
				+ "/"
				+ OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
				+ "/";
		try {
		    contentHostingService.getResource(directory
			    + "osylPrintVersion.pdf");
		    contentHostingService.removeResource(directory
			    + "osylPrintVersion.pdf");
		} catch (IdUnusedException idue) {
		    // pdf does not exist, nothing to do
		} catch (Exception e) {
		    log.error("Unable to delete " + directory
			    + "osylPrintVersion.pdf", e);
		}

		try {
		    osylPublishService.createPublishPrintVersion(site.getId(),
			    configPath + File.separator);
		} catch (PdfGenerationException e) {
		    log.error("Could not create pdf for site '" + site.getId()
			    + "'", e);
		}
	    }
	}
	logoutFromSakai();
	log.info("Finished converting Course Outlines in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    protected void loginToSakai() {
	super.loginToSakai("CreatePrintVersionJob");
    }
}
