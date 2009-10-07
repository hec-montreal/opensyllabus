/**
 * 
 */
package org.sakaiquebec.opensyllabus.client.controller.event;

import java.util.EventObject;

/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */

public interface RFBAddFolderEventHandler {

    /**
     * Event triggered when the Validate Button is pushed
     * 
     * @version $Id: $
     */
    public class RFBAddFolderEvent extends EventObject {

	/**
	 * constructor.
	 * 
	 * @param source
	 */
	public RFBAddFolderEvent(Object source) {
	    super(source);
	}

	private static final long serialVersionUID = 1L;

    }

    /*
     * This method is called when the handler is notified a click on the
     * validate button @param event
     */
    void onClickAddFolderButton(RFBAddFolderEvent event);
}
