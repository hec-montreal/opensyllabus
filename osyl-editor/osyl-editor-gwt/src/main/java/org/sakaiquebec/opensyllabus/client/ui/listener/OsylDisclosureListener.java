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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.listener;

import org.sakaiquebec.opensyllabus.client.ui.base.OsylWindowPanel;

import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;

/**
 * Listener for handling a opening or closing a disclosure panel in 
 * an editor popup with type WindowPanel.
 * 
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina Bauer-Oppinger</a>
 */
public class OsylDisclosureListener implements DisclosureHandler {

    // Popup which includes DisclosurePanel
    private OsylWindowPanel popup;

    /**
     * Constructor specifying the {@link WindowPanel} which includes the 
     * disclosure panel.
     *  
     * @param popup
     */
    public OsylDisclosureListener(OsylWindowPanel popup) {
	this.popup = popup;
    }

    /** {@inheritDoc} */
	public void onClose(DisclosureEvent event) {
		DisclosurePanel panel = (DisclosurePanel)event.getSource();
		popup.updateLayout(popup.getContentWidth(), 
				popup.getContentHeight() - panel.getContent().getOffsetHeight());
	}

	/** {@inheritDoc} */
	public void onOpen(DisclosureEvent event) {
		DisclosurePanel panel = (DisclosurePanel)event.getSource();
		popup.updateLayout(popup.getContentWidth(), 
				popup.getContentHeight() + panel.getContent().getOffsetHeight());
	}
}
