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

import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.view.OsylManagerMainAdvancedView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OsylManagerEntryPoint implements EntryPoint {

    /**
     * Controller needed to communicate with the server side.
     */
    private OsylManagerController controller = OsylManagerController
	    .getInstance();

    /**
     * The base panel of Osyl Manager GUI
     */
    private RootPanel rootPanel = RootPanel.get();

    /**
     * The base abstract view of Osyl Manager GUI
     */
    private OsylManagerAbstractView view;

    /**
     * {@inheritDoc}
     */
    public void onModuleLoad() {
	AsyncCallback<Boolean> superUserCallBack = new AsyncCallback<Boolean>() {
	    public void onFailure(Throwable caught) {
	    }

	    public void onSuccess(Boolean result) {
		controller.setSuperUser(result);
		initView();
	    }
	};
	controller.isSuperUser(superUserCallBack);
	
	AsyncCallback<Map<String,Boolean>> permissionsCallBack = new AsyncCallback<Map<String,Boolean>>() {
	    public void onFailure(Throwable caught) {
		OsylOkCancelDialog warning =
				new OsylOkCancelDialog(false, true, controller
					.getMessages().error(), controller
					.getMessages().init_failed(), false, true);
		warning.show();
		warning.centerAndFocus();
	    }

	    public void onSuccess(Map<String, Boolean> result) {
		controller.setPermissions(result);
		initView();
	    }
	};
	controller.getPermissions(permissionsCallBack);
	
    }

    /**
     * Initialization of the base view.
     */
    private void initView() {
	view = new OsylManagerMainAdvancedView(controller);
	rootPanel.add(view);
	rootPanel.addStyleName("OsylManager-rootPanel");

        Timer t = new Timer() {
            public void run() {
                if (rootPanel.getOffsetHeight() < 300) {
                    rootPanel.setHeight("600px");
                }
            }
        };
        t.schedule(500);
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

    /**
     * Shows the specified PopupPanel widget as close as possible to the top of
     * interface (but always below the toolBar). After the specified default
     * delay (3 seconds) it will be hidden.
     * 
     * @param p widget
     */
    public static void showWidgetOnTop(PopupPanel panel) {
	showWidgetOnTop(panel, 3000);
    }

    /**
     * Shows the specified PopupPanel widget as close as possible to the top of
     * interface (but always below the toolBar). After the specified amount of
     * time (in ms) it is hidden.
     * 
     * @param p widget
     * @param time
     */
    public static void showWidgetOnTop(PopupPanel panel, int time) {
	final PopupPanel p = panel;
	final int maxTime = time;
	p.show();
	setTopMostPosition(p);
	final long start = System.currentTimeMillis();
	Timer timer = new Timer() {
	    public void run() {
		setTopMostPosition(p);
		if (System.currentTimeMillis() - start >= maxTime) {
		    p.hide();
		    cancel();
		}
	    }
	};
	timer.scheduleRepeating(30);
    }

    private static void setTopMostPosition(PopupPanel p) {
	setTopMostPosition(p,
		(Window.getClientWidth() - p.getOffsetWidth()) / 2);
    }

    private static void setTopMostPosition(PopupPanel p, int x) {
	p.setPopupPosition(x, getTopMostPosition());
    }

    /**
     * getTopMostPosition returns the top most position. Since Osyl have the
     * title, there is no need to place this value below the tool menu. So we
     * simply put it with the course title.
     */
    private static int getTopMostPosition() {
	return Window.getScrollTop() + 2;
    }

}
