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

import java.util.List;

/**
 * This interface provides all the CRUD methods for the OSYL tool to manipulate
 * relationship between course outlines
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface CORelationDao {

    /**
     * Adds a new Parent to the courseOutline
     * 
     * @param coId
     * @param idParent
     */
    public void createRelation(String child, String parent) throws Exception;

    /**
     * Removes the parent to the courseOutline
     * 
     * @param coId
     * @param idParent
     */
    public boolean removeRelation(String coId, String idParent) throws Exception;

    /**
     * Get parent (unique) of the course outline
     * 
     * @param coId
     * @return
     */
    public String getParentOfCourseOutline(String coId) throws Exception;

    /**
     * All the children of the courseOutline
     * 
     * @param coId
     * @return
     */
    public List<CORelation> getCourseOutlineChildren(String coId) throws Exception;

    
    /**
     * Returns the content of the osyl_co_relation table representing all the
     * linked course outlines.
     * 
     * @return
     */
    public List<CORelation> getAllLinkedCourseOutlines ();
    
    /**
     * Method used to retrieve all the ancestors of a course outline in the 
     * hierarchy.
     * 
     * @param coId
     * @return
     */
    public List<CORelation> getCourseOutlineAncestors(String coId);
    
    /**
     * Method used to retrieve all the descendants of a course outline in the 
     * hierarchy.
     * 
     * @param coId
     * @return
     */
    public List<CORelation> getCORelationDescendants (String coId);
    
    /**
     * Tells whether or not two course outlines are related.
     * 
     * @param parentSiteId
     * @param childSiteId
     * @return
     */
    public boolean areCourseOutlinesRelated (String parentSiteId, String childSiteId);
    
  
}
