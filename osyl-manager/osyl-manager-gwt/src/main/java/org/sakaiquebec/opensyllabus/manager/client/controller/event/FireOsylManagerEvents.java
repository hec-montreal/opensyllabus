/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.controller.event;

/**
 * Interface used by OSYL Manager classes to add/remove handlers for events of
 * type <code>OsylManagerEvent</code> like SITES_SELECTION_EVENT,
 * SITE_CREATION_EVENT, SITE_IMPORT_EVENT and SITE_INFO_CHANGE.   
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface FireOsylManagerEvents {

    /**
     * Adds an handler to the list of the handlers to notify when there is a
     * <code>OsylManagerEvent</code>.
     * 
     * @param handler the handler to add
     */
    public void addEventHandler(OsylManagerEventHandler handler);

    /**
     * Removes an handler to the list of the handlers to notify when there is a
     * <code>OsylManagerEvent</code>
     * 
     * @param handler the handler to remove
     */
    public void removeEventHandler(OsylManagerEventHandler handler);

}
