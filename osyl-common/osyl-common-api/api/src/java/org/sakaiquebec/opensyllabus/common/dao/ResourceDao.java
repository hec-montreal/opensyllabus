/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.common.dao;

import java.util.Date;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * This interface provides all CRUD methods for the OSYL tool persisted data.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface ResourceDao {

    /**
     * @return All the courseOutlines
     */
    public List<COSerialized> getCourseOutlines();

    /**
     * @param siteId
     * @return The site id of the courseOutline
     * @throws Exception if null parameter or non existing course outline
     */
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId)
	    throws Exception;

    /**
     * @param siteId
     * @return The site id of the courseOutline
     * @throws Exception if null parameter or non existing course outline
     */
    public COSerialized getPrePublishSerializedCourseOutlineBySiteId(
	    String siteId) throws Exception;

    /**
     * @param siteId
     * @param access the level to access (e.g.: public, attendee, etc.)
     * @return The site id of the courseOutline
     * @throws Exception if null parameter or non existing course outline
     */
    public COSerialized getPublishedSerializedCourseOutlineBySiteIdAndAccess(
	    String siteId, String access) throws Exception;

    /**
     * Returns the course outline with the given id
     * 
     * @param idCO
     * @return The COSerialized of the given id
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutline(String idCO)
	    throws Exception;

    /**
     * @param courseoutline
     * @return Creates a new courseOutline
     * @throws Exception
     */
    public void createOrUpdateCourseOutline(COSerialized courseoutline)
	    throws Exception;

    /**
     * Removes the courseOutline
     * 
     * @param idCo
     * @return
     * @throws Exception if parameter is null
     */
    public boolean removeCourseOutline(String idCo) throws Exception;

    /**
     * Check if the Co for the siteId has been published
     * 
     * @param siteId
     * @return true if the CO has been published
     */
    public boolean hasBeenPublished(String siteId) throws Exception;

    /**
     * Check if a Co for edition exists for the siteId
     * 
     * @param siteId
     * @return true if a CO in edition exists
     */
    public boolean hasCourseOutiline(String siteId);

    /**
     * release lock made with session identified by sessionId
     * 
     * @param sessionId
     */
    public void clearLocksForSession(String sessionId);

    public void clearLocksForCoId(String coId);

    public void setLockedBy(String coId, String sessionId);

    public void setPublicationDate(String coId, Date pubDate);

    public Date getPublicationDate(String siteId) throws Exception;

    public Date getModifiedDate(String siteId) throws Exception;

    /**
     * Remove all CO for given siteId
     * 
     * @param siteId
     */
    public void removeCoForSiteId(String siteId);

    /**
     * @param siteId
     * @return
     */
    public List<COSerialized> getCourseOutlinesFoSite(String siteId);
    
    public void removePublishVersionsForSiteId(String siteId);

    //for proof of concept
    public List<String> getPublishCoSiteIds();
    
    
}
