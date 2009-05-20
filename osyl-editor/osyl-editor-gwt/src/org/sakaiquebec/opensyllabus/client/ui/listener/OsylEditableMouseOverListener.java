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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class implementing some mouse events to change css style on mouse over
 * resource proxies and labels displayed on the page.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class OsylEditableMouseOverListener implements MouseListener {

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

    /** {@inheritDoc} */
    public void onMouseDown(Widget sender, int x, int y) {
    }

    /** {@inheritDoc} */
    public void onMouseEnter(Widget sender) {
	if (!view.getEditor().isInEditionMode()
		&& !OsylController.getInstance().isReadOnly()) {
	    view.addStyleDependentName("Hover");
	    view.getButtonPanel().setVisible(true);
	    int left =
		    (view.getOffsetWidth() - view.getButtonPanel()
			    .getOffsetWidth()) / 2;
	    int top =
		    (view.getOffsetHeight() - view.getButtonPanel()
			    .getOffsetHeight()) / 2;
	    view.getMainPanel().setWidgetPosition(view.getButtonPanel(), left,
		    top);

	    view.getUpAndDownPanel().setVisible(true);
	    int leftUDP =
		    (view.getOffsetWidth() - view.getUpAndDownPanel()
			    .getOffsetWidth()-3);
	    int topUDP = view.getOffsetHeight() - view.getOffsetHeight()+3;
	    view.getMainPanel().setWidgetPosition(view.getUpAndDownPanel(),
		    leftUDP, topUDP);

	} else {
	    view.removeStyleDependentName("Hover");
	}
    }

    /** {@inheritDoc} */
    public void onMouseLeave(Widget sender) {
	view.removeStyleDependentName("Hover");
	view.getButtonPanel().setVisible(false);
	view.getUpAndDownPanel().setVisible(false);
    }

    /** {@inheritDoc} */
    public void onMouseMove(Widget sender, int x, int y) {
    }

    /** {@inheritDoc} */
    public void onMouseUp(Widget sender, int x, int y) {
    }
}
