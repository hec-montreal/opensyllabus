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
package org.sakaiquebec.opensyllabus.common.api;

import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentResource;
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
import org.sakaiproject.tool.cover.ToolManager;

/**
 * Here we will manage (create, remove) all the published resources. The
 * resources ill be saved as attachments in the Administration Workspace
 * resources.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylContentService {

    public final static String PUBLISH_DIRECTORY_PREFIX = "attachment";
    
    public static final String WORK_DIRECTORY_PREFIX = "group";

    public final static String PUBLISH_DIRECTORY_SUFFIX =
	    ToolManager.getTool("sakai.opensyllabus.tool").getTitle();

    public final static String USE_ATTACHMENTS =
	    ServerConfigurationService
		    .getString("opensyllabus.publish.in.attachment");

    /**
     * Creates the folder where all the published resources associated to
     * OpenSyllabus will be saved.
     * 
     * @param siteName the name that will be used as collection name
     */
    public void initSiteAttachments(String siteName);

    /**
     * Creates a collection with the given collection id locked for update.
     * 
     * @param parentCollection the collection where this collection will be
     *            saved
     * @param collectionId the Id of the collection
     * @exception IdUsedException if the id is already in use.
     * @exception IdInvalidException if the id is invalid.
     * @exception PermissionException if the user does not have permission to
     *                add a collection, or add a member to a collection.
     * @exception InconsistentException if the containing collection does not
     *                exist.
     * @return a new ContentCollection object.
     */
    public ContentCollection addAttachmentCollection(String parentCollection,
	    String collectionId) throws IdUsedException, IdInvalidException,
	    PermissionException, InconsistentException;

    /**
     * Create a new resource with the given resource id, locked for update.
     * 
     * @param collectionId the id of the collection where the resource will be
     *            saved
     * @param resourceId The id of the new resource.
     * @exception PermissionException if the user does not have permission to
     *                add a resource to the containing collection.
     * @exception IdUsedException if the resource id is already in use.
     * @exception IdInvalidException if the resource id is invalid.
     * @exception InconsistentException if the containing collection does not
     *                exist.
     * @return a new ContentResource object.
     */
    public ContentResource addAttachmentResource(String collectionId,
	    String resourceId) throws PermissionException,
	    IdUniquenessException, IdLengthException, IdInvalidException,
	    InconsistentException, OverQuotaException, ServerOverloadException;

    /**
     * Remove a collection and all members of the collection, internal or
     * deeper.
     * 
     * @param collectionId The id of the collection.
     * @exception IdUnusedException if the id does not exist.
     * @exception TypeException if the resource exists but is not a collection.
     * @exception PermissionException if the user does not have permissions to
     *                remove this collection, read through any containing
     * @exception InUseException if the collection or a contained member is
     *                locked by someone else. collections, or remove any members
     *                of the collection.
     * @exception ServerOverloadException if the server is configured to write
     *                the resource body to the filesystem and an attempt to
     *                access the resource body of any collection member fails.
     */
    public void removeAttachmentCollection(String collectionId)
	    throws IdUnusedException, TypeException, PermissionException,
	    InUseException, ServerOverloadException;

    /**
     * Remove a resource. For non-collection resources only.
     * 
     * @param resourceId The resource id.
     * @exception PermissionException if the user does not have permissions to
     *                read a containing collection, or to remove this resource.
     * @exception IdUnusedException if the resource id is not found.
     * @exception TypeException if the resource is a collection.
     * @exception InUseException if the resource is locked by someone else.
     */
    public void removeAttachmentResource(String resourceId)
	    throws PermissionException, IdUnusedException, TypeException,
	    InUseException;

}
