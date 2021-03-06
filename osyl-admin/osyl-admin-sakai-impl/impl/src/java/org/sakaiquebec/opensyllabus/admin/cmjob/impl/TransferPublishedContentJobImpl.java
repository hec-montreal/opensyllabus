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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.exception.IdLengthException;
import org.sakaiproject.exception.IdUniquenessException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.TransferPublishedContentJob;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class TransferPublishedContentJobImpl extends OsylAbstractQuartzJobImpl
	implements TransferPublishedContentJob {

    /**
     * Our logger
     */
    private static Log log = LogFactory
	    .getLog(TransferPublishedContentJobImpl.class);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();

	long start = System.currentTimeMillis();
	log.info("TransferPublishedContentJobImpl: starting");

	final List<Site> allSites =
		siteService.getSites(SiteService.SelectionType.ANY, "course",
			null, null, SiteService.SortType.NONE, null);

	Thread t1 = new Thread() {
	    public void run() {
		loginToSakai();
		processSites(allSites, 0, 700);
		logoutFromSakai();
	    }
	};
	Thread t2 = new Thread() {
	    public void run() {
		loginToSakai();
		processSites(allSites, 700, 1400);
		logoutFromSakai();
	    }
	};
	Thread t3 = new Thread() {
	    public void run() {
		loginToSakai();
		processSites(allSites, 1400, 2100);
		logoutFromSakai();
	    }
	};
	Thread t4 = new Thread() {
	    public void run() {
		loginToSakai();
		processSites(allSites, 2100, 2800);
		logoutFromSakai();
	    }
	};
	t1.start();
	t2.start();
	t3.start();
	t4.start();

	// change uri in course outline xml
	List<COSerialized> cos = osylSiteService.getAllCO();
	correctAllXMLs(cos);

	log.info("TransferPublishedContentJobImpl: completed in "
		+ (System.currentTimeMillis() - start) + " ms");
	logoutFromSakai();

    } // execute

    private void processSites(List<Site> allSites, int begin, int end) {
	Site site = null;
	String siteTitle = null;
	String contentDid = null;
	String contentPid = null;
	String contentWid = null;
	String contentSid = null;

	int siteCount = allSites.size();
	if (end > siteCount) {
	    end = siteCount;
	}
	log.info("TransferPublishedContentJobImpl: sites to correct:" + begin
		+ " to " + end + " / " + siteCount);

	for (int i = begin; i < end; i++) {

	    site = allSites.get(i);
	    siteTitle = site.getTitle();
	    contentSid = contentHostingService.getSiteCollection(site.getId());
	    // create site folder and OpenSyllabus folder in attachments
	    osylContentService.initSiteAttachments(site);

	    // move publish content
	    contentDid =
		    ATTACHMENT_DIRECTORY_PREFIX + siteTitle + "/"
			    + OPENSYLLABUS_ATTACHEMENT_PREFIX + "/";

	    contentPid = contentSid + PUBLISH_DIRECTORY;
	    copyContent(contentPid, contentDid);

	    // delete publish
	    try {
		contentHostingService.removeCollection(contentPid);
		contentHostingService.removeCollection(contentDid
			+ PUBLISH_DIRECTORY);
	    } catch (IdUnusedException e) {
		log.info("Folder " + contentPid + " already deleted");
	    } catch (TypeException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (InUseException e) {
		e.printStackTrace();
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    }
	    // move work content
	    contentWid = contentSid + WORK_DIRECTORY;
	    copyContent(contentWid, contentSid);

	    // delete work
	    try {
		contentHostingService.removeCollection(contentWid);
	    } catch (IdUnusedException e) {
		log.info("Folder " + contentWid + " already deleted");
	    } catch (TypeException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (InUseException e) {
		e.printStackTrace();
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    }

	    // Hide resource
	    ContentCollectionEdit cce;
	    try {
		cce = contentHostingService.editCollection(contentSid);
		cce.setHidden();
		contentHostingService.commitCollection(cce);
	    } catch (IdUnusedException e) {
		e.printStackTrace();
	    } catch (TypeException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (InUseException e) {
		e.printStackTrace();
	    }

	    log.info("The site " + siteTitle + " has been upgraded [" + i + "/"
		    + siteCount + "]");
	}
    }

    private void correctAllXMLs(List<COSerialized> cos) {
	String contentSid = null;

	COModeledServer model;
	Map<String, String> resources;
	Map<String, String> newResourcesUri;
	Set<String> keys;
	String uri, newUri;
	String siteId;
	List<CORelation> ancestors = null;
	CORelation relation = null;
	int nb;
	int coCount = cos.size();
	log.info("TransferPublishedContentJobImpl: Course Outlines to correct:"
		+ coCount);

	for (int j = 0; j < coCount; j++) {
	    COSerialized co = cos.get(j);
	    if (co.getContent() != null) {
		model = new COModeledServer(co);
		model.XML2Model();
		resources = model.getAllDocuments();
		newResourcesUri = new HashMap<String, String>();
		keys = resources.keySet();
		siteId = co.getSiteId();
		contentSid = contentHostingService.getSiteCollection(siteId);

		// We get the course outline ancestors in case there are
		// referenced in the current course outline
		ancestors = coRelationDao.getCourseOutlineAncestors(siteId);
		nb = 0;
		do {
		    for (String key : keys) {
			uri = resources.get(key);
			if (uri.startsWith(contentSid + PUBLISH_DIRECTORY)) {
			    newUri =
				    uri.replaceFirst(
					    contentSid + PUBLISH_DIRECTORY,
					    ATTACHMENT_DIRECTORY_PREFIX
						    + siteId
						    + "/"
						    + OPENSYLLABUS_ATTACHEMENT_PREFIX
						    + "/");
			    newResourcesUri.put(uri, newUri);
			}

			if (uri.startsWith(contentSid + WORK_DIRECTORY)) {
			    newUri =
				    uri.replaceFirst(contentSid
					    + WORK_DIRECTORY, contentSid);
			    newResourcesUri.put(uri, newUri);

			}

		    }

		    if (ancestors != null && nb < ancestors.size()) {
			relation = ancestors.get(nb);
			siteId = relation.getParent();
			contentSid =
				contentHostingService.getSiteCollection(siteId);
			nb++;
		    }
		} while (nb < ancestors.size());

		model.changeResourceRef(model.getModeledContent(),
			newResourcesUri);
		model.model2XML();
		co.setContent(model.getSerializedContent());
		try {
		    osylSiteService.updateSerializedCourseOutline(co);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		log.info("The references of the course outline "
			+ co.getSiteId() + " has been updated [" + j + "/"
			+ coCount + "]");
	    }
	}

    }

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	super.loginToSakai("TransferPublishedContentJob");
    }

    private void copyContent(String contentOid, String contentDid) {
	String newResourceId = null;

	log.info("Copy content from: " + contentOid + " to: " + contentDid);

	List<ContentEntity> entities =
		contentHostingService.getAllEntities(contentOid);

	for (ContentEntity entity : entities) {
	    newResourceId = (entity.getId()).replace(contentOid, contentDid);
	    try {
		log.info("Adding " + newResourceId);
		if (!newResourceId.equals(contentOid))
		    if (!newResourceId.equals(entity.getId()))

			if (entity.isCollection())
			    // FIXME: This creates duplicated structures (JIRA
			    // SAKAI-2368)
			    contentHostingService.copyIntoFolder(
				    entity.getId(), newResourceId);
			// FIXME: This creates duplicated structures (JIRA
			// SAKAI-2368)
			else
			    contentHostingService.copy(entity.getId(),
				    newResourceId);
	    } catch (PermissionException e) {
		log.info("You are not allowed to move this resource");
	    } catch (IdUnusedException e) {
		log.info("The id " + newResourceId + " is unused.");
	    } catch (TypeException e) {
		log.info("The resource or collection type is not correct");
	    } catch (InUseException e) {
		log.info("The id " + newResourceId + " is already in use");
	    } catch (OverQuotaException e) {
		log.info("This is an OverQuota exception");
	    } catch (IdUsedException e) {
		log.info("The id " + newResourceId + " is already exists");
	    } catch (ServerOverloadException e) {
		log.info("This a server overload exception.");
	    } catch (InconsistentException e) {
		log.info("This a server inconsistency exception.");
	    } catch (IdLengthException e) {
		log.info("This a server length exception.");
	    } catch (IdUniquenessException e) {
		log.info("This a server uniqueness exception.");
	    }

	}
    }
}
