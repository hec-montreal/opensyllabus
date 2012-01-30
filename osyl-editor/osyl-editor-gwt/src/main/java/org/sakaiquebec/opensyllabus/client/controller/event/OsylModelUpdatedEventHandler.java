/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.controller.event;

import java.util.EventObject;


/**
 * Event handler to catch model change
 *
 * @author <a href="mailto:philippe.rancourt@hec.ca">Philippe Rancourt</a>
 * @version $Id: $
 */
public interface OsylModelUpdatedEventHandler {
    
    /**
     * Event triggered when the Osyl model has been updated.
     * 
     * @version $Id: $
     */
    public class OsylModelUpdatedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param source
	 */
	public OsylModelUpdatedEvent(Object source) {
	    super(source);
	}
    }

    /**
     * This method is called when the handler is notified that the model
     * has been updated
     * 
     * @param event
     */
    void onModelUpdated(OsylModelUpdatedEvent event);

    /**
     * This method is called when the handler is notified that the model
     * has been successfully saved
     * 
     * @param event
     */
    void onModelSaved(OsylModelUpdatedEvent event);

}

