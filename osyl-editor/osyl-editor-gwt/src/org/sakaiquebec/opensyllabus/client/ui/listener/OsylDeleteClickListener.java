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
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractResProxView;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class to manage click on the edit button
 */
public class OsylDeleteClickListener implements ClickListener {

    private OsylAbstractResProxView osylResProxView;

    /**
     * Constructor.
     * 
     * @param osylResProxView
     */
    public OsylDeleteClickListener(OsylAbstractResProxView osylResProxView) {
	this.osylResProxView = osylResProxView;
    }

    /**
     * @see ClickListener#onClick(Widget)
     */
    public void onClick(Widget sender) {
	try {
	    // Here, we create a dialog box to confirm delete action.
	    OsylOkCancelDialog osylOkCancelDialog =
		    new OsylOkCancelDialog(
			    osylResProxView
				    .getUiMessage("OsylOkCancelDialog_Delete_Title"),
			    osylResProxView
				    .getUiMessage("OsylOkCancelDialog_Delete_Content"));

	    osylOkCancelDialog.addOkButtonCLickListener(new ClickListener() {
		public void onClick(Widget sender) {
		    try {
			osylResProxView.updateModelOnDelete();
		    } catch (Exception e) {
			com.google.gwt.user.client.Window
				.alert("Unable to delete object. Error=" + e);
			e.printStackTrace();
		    }
		}
	    });
	    osylOkCancelDialog
		    .addCancelButtonClickListener(new ClickListener() {

			public void onClick(Widget sender) {
			    osylResProxView.getMainPanel()
				    .removeStyleDependentName("Hover");
			    osylResProxView.getButtonPanel().setVisible(false);
			    osylResProxView.getUpAndDownPanel().setVisible(false);
			}
		    });
	    osylOkCancelDialog.show();
	    osylOkCancelDialog.centerAndFocus();
	} catch (Exception e) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog("Error: Unable to process delete!");
	    alertBox.center();
	    alertBox.show();
	}
    }
}
