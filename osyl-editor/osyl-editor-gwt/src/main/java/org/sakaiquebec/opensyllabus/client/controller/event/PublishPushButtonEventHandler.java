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

import java.util.EventObject;

/**
 * Handler for when the publish button is pushed
 * 
 * @version $Id: $
 */
public interface PublishPushButtonEventHandler {

    /**
     * Event triggered when the publish Button is pushed
     * 
     * @version $Id: $
     */
    public class PublishPushButtonEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**constructor.
	 * 
	 * 
	 * @param source
	 */
	public PublishPushButtonEvent(Object source) {
	    super(source);
	}
    }

    /**
     * This method is called when the handler is notified a push on the publish
     * button
     * 
     * @param event
     */
    void onPublishPushButton(PublishPushButtonEvent event);
}
