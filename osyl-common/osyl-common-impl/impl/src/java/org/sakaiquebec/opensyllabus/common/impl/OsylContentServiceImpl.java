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
package org.sakaiquebec.opensyllabus.common.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.entity.api.ResourceProperties;
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
import org.sakaiproject.util.Validator;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylContentServiceImpl implements OsylContentService {

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

    private static final Log log =
	    LogFactory.getLog(OsylContentServiceImpl.class);

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylContent service");

    }

    /** {@inheritDoc} */
    public ContentCollection addAttachmentCollection(String parentCollection,
	    String collectionId) throws IdUsedException, IdInvalidException,
	    PermissionException, InconsistentException {
	return null;
    }

    public ContentResource addAttachmentResource(String collectionId,
	    String resourceId) throws PermissionException,
	    IdUniquenessException, IdLengthException, IdInvalidException,
	    InconsistentException, OverQuotaException, ServerOverloadException {
	return null;
    }

    public void removeAttachmentCollection(String collectionId)
	    throws IdUnusedException, TypeException, PermissionException,
	    InUseException, ServerOverloadException {
    }

    public void removeAttachmentResource(String resourceId)
	    throws PermissionException, IdUnusedException, TypeException,
	    InUseException {
    }

    /** {@inheritDoc} */
    public void initSiteAttachments(String siteName) {

	// Make sure the site name can be used as a collection id
	Validator.checkResourceId(siteName);
	String osylToolName = OPENSYLLABUS_ATTACHEMENT_PREFIX;

	// We create the site collection
	String collectionId =
		ContentHostingService.ATTACHMENTS_COLLECTION + siteName;
	ContentCollectionEdit collection = null;
	if (contentHostingService.isCollection(collectionId))
	    try {
		collection =
			(ContentCollectionEdit) contentHostingService
				.getCollection(collectionId);
	    } catch (IdUnusedException e) {
		log.info("The folder " + collectionId + " does not exist.");
	    } catch (PermissionException e) {
		log.info("You are not allowed access.");
	    } catch (TypeException e) {
		log.info("The id " + collectionId
			+ " does not refer to a folder.");
	    }
	else {

	    try {
		collection = contentHostingService.addCollection(collectionId);
		collection.getPropertiesEdit().addProperty(
			ResourceProperties.PROP_DISPLAY_NAME, siteName);
		contentHostingService.commitCollection(collection);
	    } catch (IdUsedException e) {
		log.info("The collection " + collectionId + " already exists.");
	    } catch (IdInvalidException e) {
		log.info("You are refering to an invalid id ");
	    } catch (PermissionException e) {
		log.info("You are not allowed access.");
	    } catch (InconsistentException e) {
		log.info("Inconsistent Error.");
	    }
	}

	try {
	    // We create the OpenSyllabus collection
	    collectionId = collectionId + "/" + osylToolName;
	    if (!contentHostingService.isCollection(collectionId)) {
		collection = contentHostingService.addCollection(collectionId);
		collection.getPropertiesEdit().addProperty(
			ResourceProperties.PROP_DISPLAY_NAME, osylToolName);
		contentHostingService.commitCollection(collection);
	    }

	} catch (IdUsedException e) {
	    log.info("The collection " + collectionId + " already exists.");
	} catch (IdInvalidException e) {
	    log.info("You are refering to an invalid id ");
	} catch (PermissionException e) {
	    log.info("You are not allowed access.");
	} catch (InconsistentException e) {
	    log.info("Inconsistent Error.");
	}
    }

}
