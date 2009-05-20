/**
 * *****************************************************************************
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

package org.sakaiquebec.opensyllabus.client.controller.event;

import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import java.util.EventObject;

/**
 * Handler for when a view context is selected.
 */
public interface ViewContextSelectionEventHandler {

    /**
     * ViewContextSelectionEvent is an internal class instanciated when a view
     * selection event is created.
     */
    public class ViewContextSelectionEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. The parameter object is the model which should be
	 * displayed.
	 * 
	 * @param COModelInterface
	 */
	public ViewContextSelectionEvent(COModelInterface source) {
	    super(source);
	}

	/**
	 * Returns the {@link COModelInterface} to display.
	 * 
	 * @return COModelInterface
	 */
	public Object getSource() {
	    return super.getSource();
	}
    }

    /**
     * This method is called when the handler is notified a change of view
     * context (e.g.: the user clicked on a lecture to display it).
     * 
     * @param event
     */
    void onViewContextSelection(ViewContextSelectionEvent event);
}
