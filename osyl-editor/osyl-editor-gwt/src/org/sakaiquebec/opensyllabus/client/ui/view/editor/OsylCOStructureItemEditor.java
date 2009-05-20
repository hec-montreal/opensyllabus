/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureItemEditor extends OsylLabelEditor {

	private boolean isNotDeletable = false;
	
    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     */
    public OsylCOStructureItemEditor(OsylAbstractView parent) {
	super(parent);

    }

    public void enterView() {
	super.enterView();
	if (!isNotDeletable) {
		getView().getButtonPanel().add(createButtonDelete());
	}
    } // enterView

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    protected ImageAndTextButton createButtonDelete() {
	AbstractImagePrototype imgDeleteButton = getOsylImageBundle().delete();
	String title = getView().getUiMessage("delete");
	ClickListener listener =
		new MyDeletePushButtonListener((COContentUnit) getView()
			.getModel());
	return createButton(imgDeleteButton, title, listener);
    }

    /**
     * Class to manage click on the delete button
     */
    public class MyDeletePushButtonListener implements ClickListener {

	// Model variables (we use either one or the other). We could also use
	// a generic variable and cast it when needed...
	private COContentUnit coContentUnit;

	public MyDeletePushButtonListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	/**
	 * @see ClickListener#onClick(Widget)
	 */
	public void onClick(Widget sender) {
	    try {
		// Here, we create a dialog box to confirm delete
		OsylOkCancelDialog osylOkCancelDialog =
			new OsylOkCancelDialog(getView().getUiMessage(
				"OsylOkCancelDialog_Delete_Title"), getView()
				.getUiMessage(
					"OsylOkCancelDialog_Delete_Content"));
		osylOkCancelDialog
			.addOkButtonCLickListener(new MyOkCancelDialogListener(
				this.coContentUnit));
		osylOkCancelDialog
			.addCancelButtonClickListener(getCancelButtonClickListener());
		osylOkCancelDialog.show();
		osylOkCancelDialog.centerAndFocus();
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
    }

    // The click listener that perform delete action if confirm button pushed
    public class MyOkCancelDialogListener implements ClickListener {

	private COContentUnit coContentUnit;

	public MyOkCancelDialogListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	public void onClick(Widget sender) {
	    try {
		String title = coContentUnit.getLabel();
		coContentUnit.remove();
		OsylUnobtrusiveAlert info =
			new OsylUnobtrusiveAlert(getView().getUiMessages()
				.getMessage("RemovedContentUnit", title));
		OsylEditorEntryPoint.showWidgetOnTop(info);
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
    }
    
    public void setIsNotDeletable(boolean isNotDeletable) {
    	this.isNotDeletable = isNotDeletable;
    }

}
