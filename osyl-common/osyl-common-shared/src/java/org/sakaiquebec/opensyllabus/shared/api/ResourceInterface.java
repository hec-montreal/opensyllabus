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

package org.sakaiquebec.opensyllabus.shared.api;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * This interface provides resource and content-related methods used by the DAO
 * and services. The RPC interface should also implement these, but it would
 * require that ResourceInterface extends RemoteInterface, which is
 * questionable.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface ResourceInterface {

    /*
     * Returns the CourseOutline whose ID is specified. @param String the course
     * outline ID @return the CourseOutline POJO corresponding to the specified
     * ID
     */
    // public COSerialized getSerializedCourseOutline(String coId, String
    // webappDir);
    /**
     * Returns the CourseOutline whose site ID is specified.
     * 
     * @param String the site ID
     * @return the CourseOutline POJO corresponding to the specified siteID
     */
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId);

    /*
     * Returns the CourseOutline of the current context. @return the
     * CourseOutline POJO corresponding to the current context.
     */
    // public COSerialized getSerializedCourseOutline(String webappDir);
    /*
     * Saves the CourseOutline specified. The ID is returned. This is useful if
     * this instance has never been saved before (i.e.: its ID is -1). In this
     * case, it is the responsibility of the client application to keep track of
     * this new ID, notably to save it again at a later time. @param
     * COSerialized POJO @return the CourseOutline ID
     */
    //public String updateSerializedCourseOutline(COSerialized co);

}
