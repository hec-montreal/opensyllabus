/**
 * ****************************************************************************
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.controller.event;

/**
 * Interface which permits to manage handlers of PublishPushButtonEvent
 * 
 * @version $Id: $
 */
public interface FiresPublishPushButtonEvents {

    /**
     * Add an handler to the list of the handlers to notify when there is a
     * PublishPushButtonEvent
     * 
     * @param handler
     */
    public void addEventHandler(PublishPushButtonEventHandler handler);

    /**
     * Remove an handler to the list of the handlers to notify when there is a
     * PublishPushButtonEvent
     * 
     * @param handler
     */
    public void removeEventHandler(PublishPushButtonEventHandler handler);

}
