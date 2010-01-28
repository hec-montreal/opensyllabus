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

package org.sakaiquebec.opensyllabus.shared.events;

import java.util.EventObject;

/**
 * UpdateCOContentResourceProxyEventHandler interface
 *
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public interface UpdateCOContentResourceProxyEventHandler {

    /**
     * Represents the <code>UpdateCOContentResourceEvent</code> in the
     * application.
     * 
     * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
     * @version $Id: $
     */
    public class UpdateCOContentResourceProxyEvent extends EventObject {

	/**
	 * The default Event type when not specified
	 */
	public static final int DEFAULT_EVENT_TYPE = 0;

	/**
	 * The Rubric update event type when a rubric update is done
	 */
	public static final int RUBRIC_UPDATE_EVENT_TYPE = 1;
	
	/**
	 * Delete event of a COContentResourceProxy
	 */
	public static final int DELETE_EVENT_TYPE = 2;
	
	/**
	 * Delete event of a COContentResourceProxy
	 */
	public static final int MOVE_IN_RUBRIC_EVENT_TYPE = 3;

	/**
	 * The Rubric update event type when a rubric update is done
	 */
	public static final int RUBRIC_LABEL_UPDATE_EVENT_TYPE = 4;
	
	/** The unique id for serialization */
	private static final long serialVersionUID = 55L;
	private int eventType = DEFAULT_EVENT_TYPE;

	/** Constructor */
	public UpdateCOContentResourceProxyEvent(Object source) {
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
	    case RUBRIC_UPDATE_EVENT_TYPE:
		this.eventType = RUBRIC_UPDATE_EVENT_TYPE;
		break;
	    case DELETE_EVENT_TYPE:
		this.eventType = DELETE_EVENT_TYPE;
		break;
	    case MOVE_IN_RUBRIC_EVENT_TYPE:
		this.eventType = MOVE_IN_RUBRIC_EVENT_TYPE;
		break;
	    case RUBRIC_LABEL_UPDATE_EVENT_TYPE:
			this.eventType = RUBRIC_LABEL_UPDATE_EVENT_TYPE;
			break;
	    default:
		this.eventType = DEFAULT_EVENT_TYPE;
		break;
	    }
	}

	/**
	 * @return true if current eventType is a RUBRIC UPDATE
	 */
	public boolean isRubricUpdateEvent() {
	    return getEventType() == RUBRIC_UPDATE_EVENT_TYPE;
	}
	
	/**
	 * @return true if current eventType is a RUBRIC LABEL UPDATE
	 */
	public boolean isRubricLabelUpdateEvent() {
	    return getEventType() == RUBRIC_LABEL_UPDATE_EVENT_TYPE;
	}
	
	/**
	 * @return true if current eventType is a COContentREsourcePRoxy delete
	 */
	public boolean isDeleteEvent() {
	    return getEventType() == DELETE_EVENT_TYPE;
	}
	
	/**
	 * @return true if current eventType is a COContentREsourcePRoxy move
	 */
	public boolean isMoveInRubricEvent() {
	    return getEventType() == MOVE_IN_RUBRIC_EVENT_TYPE;
	}
    }

    /** Called when the model is updated */
    void onUpdateModel(UpdateCOContentResourceProxyEvent event);
}
