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
package org.sakaiquebec.opensyllabus.common.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylEventService;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylDirectoryPublishServiceImpl extends OsylPublishServiceImpl {

    private static Log log = LogFactory
	    .getLog(OsylDirectoryPublishServiceImpl.class);

    // @Override
    // public COSerialized getSerializedPublishedCourseOutlineForAccessType(
    // String siteId, String accessType, String webappdir) {
    // return null;
    // }

    @Override
    public Vector<Map<String, String>> publish(String webappDir, String siteId, String nonce)
	    throws Exception, FusionException {
	if (!osylSecurityService.isActionAllowedInSite(
		osylSiteService.getSiteReference(siteId),
		SecurityInterface.OSYL_FUNCTION_PUBLISH)) {
	    throw new OsylPermissionException(sessionManager
		    .getCurrentSession().getUserEid(),
		    SecurityInterface.OSYL_FUNCTION_PUBLISH);
	} else {
            if (isAlreadyPublished(nonce)) {
                log.error("Publish request for site [" + siteId
                        + "] was already made using the same id!");
                return null;
            } else {
                setAlreadyPublished(nonce);
            }

	    long start = System.currentTimeMillis();
	    log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		    + "] publish site [" + siteId + "]");
	    Vector<Map<String, String>> publicationResults =
		    new Vector<Map<String, String>>();

	    SecurityAdvisor advisor = new SecurityAdvisor() {
		public SecurityAdvice isAllowed(String userId, String function,
			String reference) {
		    return SecurityAdvice.ALLOWED;
		}
	    };

	    COSerialized co =
		    osylSiteService
			    .getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	    try {
		securityService.pushAdvisor(advisor);

		if (co.getContent() == null) {
		    osylSiteService.setCoContentWithTemplate(co, webappDir);
		    resourceDao.createOrUpdateCourseOutline(co);
		}

		String content =
			transformXmlForGroup(co.getContent(),
				SecurityInterface.ACCESS_PUBLIC, webappDir);
		co.setContent(content);

		COSerialized publishedCO = null;
		try {
		    publishedCO =
			    getSerializedPublishedCourseOutlineForAccessType(
				    co.getSiteId(),
				    SecurityInterface.ACCESS_PUBLIC, webappDir);
		} catch (Exception e) {
		}

		// Create a course outline with security public
		if (publishedCO == null) {
		    publishedCO = new COSerialized(co);
		    publishedCO.setCoId(idManager.createUuid());
		    publishedCO.setAccess(SecurityInterface.ACCESS_PUBLIC);
		    publishedCO.setPublished(true);
		    resourceDao.createOrUpdateCourseOutline(publishedCO);
		} else {
		    publishedCO.setContent(co.getContent());
		    resourceDao.createOrUpdateCourseOutline(publishedCO);
		}
		// publishedSiteIds.add(siteId);
		// change publication date
		TreeMap<String, String> publicationProperties =
			new TreeMap<String, String>();
		COSerialized coSerialized =
			osylSiteService
				.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
		COModeledServer coModeledServer =
			new COModeledServer(coSerialized);
		coModeledServer.XML2Model(false);
		COContent coContent = coModeledServer.getModeledContent();
		coContent
			.addProperty(
				COPropertiesType.PREVIOUS_PUBLISHED,
				coContent
					.getProperty(COPropertiesType.PUBLISHED) != null ? coContent
					.getProperty(COPropertiesType.PUBLISHED)
					: "");
		coContent.addProperty(COPropertiesType.PUBLISHED,
			OsylDateUtils.getCurrentDateAsXmlString());
		coModeledServer.model2XML();
		coSerialized.setContent(coModeledServer.getSerializedContent());
		resourceDao.createOrUpdateCourseOutline(coSerialized);
		publicationProperties
			.put(COPropertiesType.PREVIOUS_PUBLISHED,
				coContent
					.getProperty(COPropertiesType.PREVIOUS_PUBLISHED));
		publicationProperties.put(COPropertiesType.PUBLISHED,
			coContent.getProperty(COPropertiesType.PUBLISHED));
		publicationResults.add(publicationProperties);

		Map<String, String> pdfGenerationResults =
			new HashMap<String, String>();
		pdfGenerationResults.put(siteId, "publish.pdfGeneration.ok");
		publicationResults.add(pdfGenerationResults);

	    } finally {
		securityService.popAdvisor();
	    }

	    eventTrackingService.post(eventTrackingService.newEvent(
		    OsylEventService.EVENT_OPENSYLLABUS_PUBLICATION,
		    osylSiteService.getSiteReference(siteId), false));

	    log.info("Finished publishing course outline for site ["
		    + (co.getTitle() == null ? siteId : co.getTitle())
		    + "] in " + (System.currentTimeMillis() - start) + " ms");
	    return publicationResults;
	}
    }

    // @Override
    // public String transformXmlForGroup(String xml, String group,
    // String webappDir) throws Exception {
    // return null;
    // }

    @Override
    public void createEditionPrintVersion(COSerialized cos, String webappdir)
	    throws Exception {
	updateCourseInformations(cos, webappdir);
	String transformXml =
		transformXmlForGroup(cos.getContent(),
			SecurityInterface.ACCESS_PUBLIC, webappdir);
	cos.setContent(transformXml);
	File f = createPrintVersion(cos, webappdir);
	String siteId = cos.getSiteId();
	if (f != null) {
	    String workDirectory =
		    contentHostingService.getSiteCollection(siteId);
	    createPdfInResource(siteId, workDirectory, f, "");
	}
    }

    @Override
    public void createPublishPrintVersion(String siteId, String webappdir)
	    throws PdfGenerationException {
	try {

	    COSerialized coSerializedPublic =
		    getSerializedPublishedCourseOutlineForAccessType(siteId,
			    SecurityInterface.ACCESS_PUBLIC, webappdir);
	    File fPublic = createPrintVersion(coSerializedPublic, webappdir);

	    if (fPublic != null) {
		String publishDirectory = "";
		publishDirectory =
			ContentHostingService.ATTACHMENTS_COLLECTION
				+ siteId
				+ "/"
				+ OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
				+ "/";

		createPdfInResource(siteId, publishDirectory, fPublic,
			SecurityInterface.ACCESS_PUBLIC);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new PdfGenerationException(e);
	}
    }

    @Override
    public void unpublish(String siteId, String webappDir) throws Exception,
	    PermissionException {
	resourceDao.removePublishVersionsForSiteId(siteId);
    }

}
