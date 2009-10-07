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

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to manage click on the edit button.
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OsylEditClickListener implements ClickListener {

    private OsylAbstractView view;

    /**
     * Constructor for adding a click listener on an element of the specified
     * {@link OsylAbstractView}. 
     * 
     * @param view
     */
    public OsylEditClickListener(OsylAbstractView view) {
	if (view.getController().isReadOnly()) {
	    // This may not be good encapsulation but that exception will help
	    // not forgetting the handling of ro mode in editors...
	    throw new IllegalStateException("OsylEditClickListener should" +
	    		" not be instantiated in read-only mode");
	}
	this.view = view;
    }

    /**
     * @see ClickListener#onClick(Widget)
     */
    public void onClick(Widget sender) {
	try {
	    view.getController().getViewContext().setChild(view);

	    OsylAbstractEditor editor = view.getEditor();

	    if (editor.isInEditionMode()) {
		return;
	    } else {
		view.enterEdit();
	    }

	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		new OsylAlertDialog(false, true, "OsylEditClickListener 256,"
			+ " Unable to enter edit mode: " + e);
	    e.printStackTrace();
	    alertBox.center();
	    alertBox.show();
	}
    } // onClick
}
