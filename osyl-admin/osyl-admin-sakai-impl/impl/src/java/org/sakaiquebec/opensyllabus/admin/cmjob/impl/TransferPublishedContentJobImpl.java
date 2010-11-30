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
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.exception.IdInvalidException;
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
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.TransferPublishedContentJob;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class TransferPublishedContentJobImpl implements
	TransferPublishedContentJob {

    private List<Site> allSites;

    private List<COSerialized> cos;

    /**
     * Our logger
     */
    private static Log log =
	    LogFactory.getLog(TransferPublishedContentJobImpl.class);

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

    /**
     * The site service used to create new sites: Spring injection
     */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code> needed to create a new site in Sakai.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    /** The osyl content service to be injected by Spring */
    private OsylContentService osylContentService;

    /**
     * Sets the {@link OsylContentService}.
     * 
     * @param osylContentService
     */
    public void setOsylContentService(OsylContentService osylContentService) {
	this.osylContentService = osylContentService;
    }

    private OsylSiteService osylSiteService;

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	loginToSakai();

	log.info("TransferPublishedContentJobImpl: starting");

	allSites =
		siteService.getSites(SiteService.SelectionType.ANY, "course",
			null, null, SiteService.SortType.NONE, null);

	Site site = null;
	String siteTitle = null;
	String contentDid = null;
	String contentPid = null;
	String contentWid = null;
	String contentSid = null;

	log.info("TransferPublishedContentJobImpl: sites to correct:" + allSites.size());

	for (int i = 0; i < allSites.size(); i++) {

	    site = allSites.get(i);
	    siteTitle = site.getTitle();
	    contentSid = contentHostingService.getSiteCollection(site.getId());
	    // create site folder and OpenSyllabus folder in attachments
	     osylContentService.initSiteAttachments(siteTitle);

	    // move publish content
	    contentDid =
		    ATTACHMENT_DIRECTORY_PREFIX + siteTitle + "/"
			    + ATTACHMENT_DIRECTORY_SUFFIX;

	    contentPid = contentSid + PUBLISH_DIRECTORY;
	    copyContent(contentPid, contentDid);

	    // delete publish
	    try {
		contentHostingService.removeCollection(contentPid);
	    } catch (IdUnusedException e) {
		e.printStackTrace();
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
		e.printStackTrace();
	    } catch (TypeException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (InUseException e) {
		e.printStackTrace();
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    }
	    // remove Resources tool to students

	    log.info("The site " + siteTitle + " has been upgraded");
	}

	// change uri in course outline xml

	cos = osylSiteService.getAllCO();
	COModeledServer model;
	Map<String, String> resources;
	Map<String, String> newResourcesUri;
	Set<String> keys;
	String uri, newUri;
	String siteId;

	log.info("TransferPublishedContentJobImpl: Course Outlines to correct:" + cos.size());

	for (int j = 0; j < cos.size(); j++) {
	    COSerialized co = cos.get(j);
	    if (co.getContent() != null) {
		model = new COModeledServer(co);
		model.XML2Model();
		resources = model.getAllDocuments();
		newResourcesUri = new HashMap<String, String>();
		keys = resources.keySet();
		siteId = co.getSiteId();
		contentSid = contentHostingService.getSiteCollection(siteId);
		for (String key : keys) {
		    uri = resources.get(key);
		    if (uri.startsWith(contentSid + PUBLISH_DIRECTORY)) {
			newUri =
				uri.replaceFirst(
					contentSid + PUBLISH_DIRECTORY,
					ATTACHMENT_DIRECTORY_PREFIX + siteId
						+ ATTACHMENT_DIRECTORY_SUFFIX);
			newResourcesUri.put(uri.substring(uri.lastIndexOf("/") + 1), newUri);
		    }

		    if (uri.startsWith(contentSid + WORK_DIRECTORY)) {
			newUri =
				uri.replaceFirst(contentSid + WORK_DIRECTORY,
					contentSid);
			newResourcesUri.put(uri.substring(uri.lastIndexOf("/") + 1), newUri);

		    }

		}

		model.resetXML(newResourcesUri);
		model.model2XML();
		co.setContent(model.getSerializedContent());
		try {
		    osylSiteService.updateSerializedCourseOutline(co);
		} catch (Exception e) {
		    e.printStackTrace();
		}

		log.info("The references of the course outline "
			+ co.getSiteId() + " has been updated.");
	    }
	}

	logoutFromSakai();

    }

    /**
     * Logs in the sakai environment
     */
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

    private void copyContent(String contentOid, String contentDid) {
	List<ContentResource> resources =
		contentHostingService.getAllResources(contentOid);

	String oldResourceId = null;
	String newResourceId = null;
	String collectionId = null;
	int nbSubFolders = 0;

	for (ContentResource resource : resources) {
	    oldResourceId = resource.getId();

	    if (oldResourceId.indexOf("/", contentOid.length() + 1) >= 0) {
		// Copy folder
		newResourceId = contentDid + "/";
		StringTokenizer tokens =
			new StringTokenizer(oldResourceId.substring(contentOid
				.length()), "/");
		ContentCollectionEdit collection = null;
		while (tokens.hasMoreTokens()) {
		    collectionId = tokens.nextToken();
		    if (tokens.hasMoreTokens()) {
			newResourceId = newResourceId + collectionId + "/";
			try {

			    collection =
				    contentHostingService
					    .addCollection(newResourceId);
			    ResourcePropertiesEdit fileProperties =
				    collection.getPropertiesEdit();
			    fileProperties.addProperty(
				    ResourceProperties.PROP_DISPLAY_NAME,
				    collectionId);
			    contentHostingService.commitCollection(collection);
			} catch (IdUsedException e) {
			    // The collection already exists, we do nothing
			} catch (IdInvalidException e) {
			    e.printStackTrace();
			} catch (PermissionException e) {
			    e.printStackTrace();
			} catch (InconsistentException e) {
			    e.printStackTrace();
			}
			log.debug("le dossier " + newResourceId);
		    } else {
			newResourceId = newResourceId + collectionId;
			try {

			    // Can not check if the resource already exists
			    contentHostingService.copyIntoFolder(oldResourceId,
				    contentDid);
			} catch (PermissionException e) {
			    e.printStackTrace();
			} catch (IdUnusedException e) {
			    e.printStackTrace();
			} catch (TypeException e) {
			    e.printStackTrace();
			} catch (InUseException e) {
			    // la ressource existe deja
			} catch (OverQuotaException e) {
			    e.printStackTrace();
			} catch (IdUsedException e) {
			    e.printStackTrace();
			} catch (ServerOverloadException e) {
			    e.printStackTrace();
			} catch (InconsistentException e) {
			    e.printStackTrace();
			} catch (IdLengthException e) {
			    e.printStackTrace();
			} catch (IdUniquenessException e) {
			    e.printStackTrace();
			}

			System.out.println("la ressource " + newResourceId);
		    }
		}

	    } else {
		newResourceId = oldResourceId.replace(contentOid, contentDid);
		try {

		    // Can not check if the resource already exists
		    contentHostingService.copyIntoFolder(oldResourceId,
			    contentDid);
		} catch (PermissionException e) {
		    e.printStackTrace();
		} catch (IdUnusedException e) {
		    e.printStackTrace();
		} catch (TypeException e) {
		    e.printStackTrace();
		} catch (InUseException e) {
		    // la ressource existe deja
		} catch (OverQuotaException e) {
		    e.printStackTrace();
		} catch (IdUsedException e) {
		    e.printStackTrace();
		} catch (ServerOverloadException e) {
		    e.printStackTrace();
		} catch (InconsistentException e) {
		    e.printStackTrace();
		} catch (IdLengthException e) {
		    e.printStackTrace();
		} catch (IdUniquenessException e) {
		    e.printStackTrace();
		}
		log.debug("la ressource " + newResourceId);
	    }
	}
    }

    /**
     * Logs out of the sakai environment
     */
    protected void logoutFromSakai() {
	// post the logout event
	EventTrackingService.post(EventTrackingService.newEvent(
		UsageSessionService.EVENT_LOGOUT, null, true));
    }

}
