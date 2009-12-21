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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

/**
 * Class implementing some mouse events to change css style on mouse over
 * resource proxies and labels displayed on the page.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class OsylEditableMouseOverListener implements MouseOutHandler,
	MouseOverHandler {

    /**
     * We need the abstract view to get control on the view elements like the
     * main panel and the button panel.
     */
    private OsylAbstractView view;

    /**
     * Constructor.
     * 
     * @param view The abstract view needed to get control over its elements
     */
    public OsylEditableMouseOverListener(OsylAbstractView view) {
	this.view = view;
    }

    public void onMouseOut(MouseOutEvent event) {
	view.removeStyleDependentName("Hover");
	view.getButtonPanel().setVisible(false);
	view.getUpAndDownPanel().setVisible(false);
    }

    public void onMouseOver(MouseOverEvent event) {
	if (!view.getEditor().isInEditionMode()
		&& !OsylController.getInstance().isReadOnly()) {
	    view.addStyleDependentName("Hover");
	    view.getButtonPanel().setVisible(true);
	    // left position of buttons is according to viewport if
	    // width of main panel is larger than this
	    int widthReference =
		    OsylEditorEntryPoint.getViewportWidth()
			    - view.getMainPanel().getAbsoluteLeft() < view
			    .getOffsetWidth() ? OsylEditorEntryPoint
			    .getViewportWidth()
			    - view.getMainPanel().getAbsoluteLeft() : view
			    .getOffsetWidth();
	    int left =
		    (widthReference - view.getButtonPanel().getOffsetWidth()) / 2;
	    int top =
		    (view.getOffsetHeight() - view.getButtonPanel()
			    .getOffsetHeight()) / 2;
	    view.getMainPanel().setWidgetPosition(view.getButtonPanel(), left,
		    top);

	    view.getUpAndDownPanel().setVisible(true);
	    int leftUDP =
		    (widthReference - view.getUpAndDownPanel().getOffsetWidth() - 3);
	    int topUDP = view.getOffsetHeight() - view.getOffsetHeight() + 3;
	    view.getMainPanel().setWidgetPosition(view.getUpAndDownPanel(),
		    leftUDP, topUDP);

	} else {
	    view.removeStyleDependentName("Hover");
	}
    }
}
