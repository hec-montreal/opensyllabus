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

package org.sakaiquebec.opensyllabus.manager.client.ui.api;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerAbstractView extends Composite {

    
    private OsylManagerController controller;
    
    /**
     * Constructor.
     * 
     * @param controller
     */
    public OsylManagerAbstractView(OsylManagerController controller) {
	super();
	this.controller = controller;
    }

    /**
     * Create a horizontal panel which contains the label and the widget
     * associated
     * 
     * @param label
     * @param widget
     * @return
     */
    @Deprecated
    static public FlexTable createFormElement(Label label, Widget widget) {
	FlexTable flexTable = new FlexTable();
	flexTable.setWidget(0, 0, label);
	flexTable.getFlexCellFormatter().setAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_LEFT,
		HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.getFlexCellFormatter().setStylePrimaryName(0, 0,
		"OsylManager-form-label");
	flexTable.setWidget(0, 1, widget);
	flexTable.getFlexCellFormatter().setAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT,
		HasVerticalAlignment.ALIGN_MIDDLE);
	flexTable.getFlexCellFormatter().setStylePrimaryName(0, 1,
		"OsylManager-form-element");
	flexTable.setWidth("100%");
	return flexTable;
    }

    public OsylManagerController getController() {
        return controller;
    }

    public void setController(OsylManagerController controller) {
        this.controller = controller;
    }
}

