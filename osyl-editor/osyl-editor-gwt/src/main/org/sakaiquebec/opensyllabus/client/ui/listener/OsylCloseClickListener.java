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

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Listener for handling a close button click while in editing a resource. It
 * can be used for the cancel-changes button as well as the save-changes
 * button. 
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylCloseClickListener implements ClickListener {

    // Either we want to save or not on close.
    private boolean save;
    
    // The OsylAbstractView we are listening to.
    private OsylAbstractView view;

    /**
     * Constructor specifying the {@link OsylAbstractView} this listener is working
     * for and whether it should save changes (if the validate button is
     * clicked) or discard them (cancel button clicked).
     * 
     * @param view
     * @param save
     */
    public OsylCloseClickListener(OsylAbstractView view, boolean save) {
	this.view = view;
	this.save = save;
    }

    /** {@inheritDoc} */
    public void onClick(Widget sender) {
    view.closeAndSaveEdit(save);
    // Note: getView().leaveEdit(); is now called in the 
    // WindowCloseListener of the editor pop-up
    }
}
