/**********************************************************************************
 * $Id: UpdateCOContentUnitEventHandler.java 507 2008-05-21 18:09:48Z sacha.lepretre@crim.ca $
 **********************************************************************************
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.shared.events;

import java.util.EventObject;

/**
 * UpdateCOContentUnitEventHandler interface
 *
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @version $Id: UpdateCOContentUnitEventHandler.java 507 2008-05-21 18:09:48Z sacha.lepretre@crim.ca $
 */
public interface UpdateCOContentUnitEventHandler {
    
    /**
     * Represents the <code>UpdateCOContentUnitEvent</code> in the application.
     * 
     * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
     * @version $Id: $
     */
    public class UpdateCOContentUnitEvent extends EventObject {

	/**
	 * The default Event type when not specified
	 */
	public static final int DEFAULT_EVENT_TYPE = 0;

	/**
	 * COContentResourceProxy ADD event
	 */
	public static final int ADD_RESSOURCE_PROXY_EVENT_TYPE = 1;

	private int eventType = DEFAULT_EVENT_TYPE;

	/** The unique id for serialization */
	private static final long serialVersionUID = 55L;

	/** Constructor */
	public UpdateCOContentUnitEvent(Object source) {
	    super(source);
	}

	/**
	 * @return the eventType value.
	 */
	public int getEventType() {
	    return eventType;
	}

	/**
	 * Sets the eventType. DEFAULT_EVENT_TYPE and RUBRIC_UPDATE_EVENT_TYPE are only allowed.
	 * @param eventType the new value of eventType.
	 */
	public void setEventType(int eventType) {
	    switch (eventType) {
	    case DEFAULT_EVENT_TYPE:
		this.eventType = DEFAULT_EVENT_TYPE;
		break;
	    case ADD_RESSOURCE_PROXY_EVENT_TYPE:
		this.eventType = ADD_RESSOURCE_PROXY_EVENT_TYPE;
		break;

	    default:
		this.eventType = DEFAULT_EVENT_TYPE;
		break;
	    }
	}

	/**
	 * @return true if current eventType is an ADD_RESSOURCE_PROXY_EVENT_TYPE
	 */
	public boolean isAddRessProxEvent() {
	    return getEventType() == ADD_RESSOURCE_PROXY_EVENT_TYPE;
	}

    }

    /** Called when the model is updated */
    void onUpdateModel(UpdateCOContentUnitEvent event);
}
