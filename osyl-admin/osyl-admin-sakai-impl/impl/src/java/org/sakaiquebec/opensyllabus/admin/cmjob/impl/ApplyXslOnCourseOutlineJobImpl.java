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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.ApplyXslOnCourseOutlineJob;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ApplyXslOnCourseOutlineJobImpl extends OsylAbstractQuartzJobImpl
	implements ApplyXslOnCourseOutlineJob {

    private static Log log = LogFactory
	    .getLog(ApplyXslOnCourseOutlineJobImpl.class);

    public void execute(JobExecutionContext jec) throws JobExecutionException {
	log.info("Start apply xsl on Course Outlines");
	long start = System.currentTimeMillis();
	loginToSakai();
	List<COSerialized> cos = osylSiteService.getAllCO();
	String xsl = adminConfigService.getCourseOutlineXsl();
	for (COSerialized co : cos) {
	    log.debug("Start applying xsl on co with co_id:" + co.getCoId());
	    try {
		String xml = co.getContent();

		if (xml != null) {
		    xml = XmlHelper.applyXsl(xml, xsl);
		    co.setContent(xml);
		    resourceDao.createOrUpdateCourseOutline(co);
		}

	    } catch (Exception e) {
		log.error("Could not apply xsl on co with co_id:"
			+ co.getCoId());
		e.printStackTrace();
	    }
	    log.debug("Finished applying xsl on co with co_id:" + co.getCoId());
	}
	logoutFromSakai();
	log.info("Finished applying xsl on Course Outlines in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    protected void loginToSakai() {
	super.loginToSakai("ApplyXslOnCourseOutlineJob");
    }
}
