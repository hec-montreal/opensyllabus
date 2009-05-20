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

package org.sakaiquebec.opensyllabus.shared.events;

import java.util.EventObject;

/**
 * Handler for when there is an update on a resource of a CO
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public interface UpdateCOContentResourceEventHandler {
    
    /**
     * Represents the <code>UpdateCOContentResourceEvent</code> in the
     * application.
     * 
     * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
     * @version $Id: $
     */
    public class UpdateCOContentResourceEvent extends EventObject {

	/** The unique id for serialization */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor.
	 * 
	 * @param source
	 */
	public UpdateCOContentResourceEvent(Object source) {
	    super(source);
	}
    }
    
    /** Called when the model is updated */
    void onUpdateModel(UpdateCOContentResourceEvent event);
}

