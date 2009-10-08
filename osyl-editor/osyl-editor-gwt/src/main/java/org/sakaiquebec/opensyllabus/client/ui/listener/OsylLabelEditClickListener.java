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

package org.sakaiquebec.opensyllabus.client.ui.listener;

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * This class represents the click listeners of a Osyl label being clicked.
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylLabelEditClickListener implements ClickHandler {
    private OsylAbstractView view;

    /**
     * Constructor.
     * 
     * @param view
     */
    public OsylLabelEditClickListener(OsylAbstractView view) {
	this.view = view;
    }

    public void onClick(ClickEvent event) {
	try {

	    view.getController().getViewContext().setView(view);

	    OsylAbstractEditor editor = view.getEditor();

	    if (editor.isInEditionMode()) {
		return;
	    } else {
		view.enterEdit();
	    }

	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true,
			    "OsylLabelEditClickListener 256,"
				    + " Unable to enter edit mode: " + e);
	    e.printStackTrace();
	    alertBox.center();
	    alertBox.show();
	}
    } // onClick
}
