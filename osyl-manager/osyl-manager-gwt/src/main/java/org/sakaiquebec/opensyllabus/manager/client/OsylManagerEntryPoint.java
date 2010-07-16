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

package org.sakaiquebec.opensyllabus.manager.client;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.manager.client.ui.view.OsylManagerMainAdvancedView;
import org.sakaiquebec.opensyllabus.manager.client.ui.view.OsylManagerMainView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OsylManagerEntryPoint implements EntryPoint {

    private OsylManagerController controller =
	    OsylManagerController.getInstance();

    private RootPanel rootPanel = RootPanel.get();
    private OsylManagerAbstractView view;

    /**
     * {@inheritDoc}
     */
    public void onModuleLoad() {
	initView();
    }

    private void initView() {
	view = new OsylManagerMainAdvancedView(controller);
	rootPanel.add(view);
	rootPanel.setHeight("600px");
    }
    
    /**
     * Centers the specified {@link PopupPanel} on the current view. This method
     * takes into account the current position in OSYLEditor, as well as the
     * Sakai navigation header to ensure the centering of the specified pop-up.
     * Warning: the widget must already be visible otherwise its dimensions may
     * not be available! You should ensure that widget.show() is called before.
     * 
     * @param widget the pop-up to center
     */
    public static void centerObject(PopupPanel widget) {
	int width = widget.getOffsetWidth();
	int height = widget.getOffsetHeight();
	width = Math.max(0, (Window.getClientWidth() - width) / 2);
	height = Math.max(0, (Window.getClientHeight() - height) / 2);
	widget.setPopupPosition(width, height);
    }

}
