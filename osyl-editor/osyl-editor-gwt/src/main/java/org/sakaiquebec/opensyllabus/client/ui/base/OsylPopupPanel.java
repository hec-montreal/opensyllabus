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

package org.sakaiquebec.opensyllabus.client.ui.base;

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * PopUp used when the mouse is over an {@link OsylAbstractView}. It shows the
 * view's button panel. {@link OsylAbstractView#getButtonPanel()}.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:remi.saia@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylPopupPanel extends PopupPanel {

    private HorizontalPanel hPanel;
    private Panel buttonPanel;

    // private Timer timer;

    // private MyListener listener;

    public OsylPopupPanel() {
	super();
    }

    public OsylPopupPanel(boolean autoHide) {
	super(autoHide);
    }

    public OsylPopupPanel(boolean autoHide, boolean modal, Panel buttonPanel) {
	super(autoHide, modal);

	hPanel = new HorizontalPanel();
	hPanel.setStylePrimaryName("MainPanel");
	hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	super.add(hPanel);

	this.buttonPanel = buttonPanel;

	// To be used later (see listener below)
	// timer = new Timer() {
	// public void run() {
	// hide();
	// }
	// };

    }

    private void setWidgets() {

	hPanel.clear();

	// Label dummy1 = new Label();
	// dummy1.setWidth("100%");
	// hPanel.add(dummy1);

	hPanel.add(buttonPanel);
	buttonPanel.setVisible(true);

	// Label dummy2 = new Label();
	// dummy2.setWidth("100%");
	// hPanel.add(dummy2);

    }

    public void show() {
	setWidgets();
	// listener.setEnabled(true);
	super.show();
    }

    /**
     * @see Widget#setElement(Element)
     */
    protected void setElement(Element elem) {
	super.setElement(elem);
	sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK);
    }

    /**
     * @see Widget#onBrowserEvent(Event)
     */
    // public void onBrowserEvent(Event event) {
    // switch (DOM.eventGetType(event)) {
    // case Event.ONCLICK:
    // if(clickListeners != null) {
    // clickListeners.fireClick(this);
    // }
    // break;
    // case Event.ONMOUSEOVER:
    // if (mouseListeners != null) {
    // mouseListeners.fireMouseEnter(this);
    // }
    // break;
    // case Event.ONMOUSEOUT:
    // if(mouseListeners != null) {
    // mouseListeners.fireMouseLeave(this);
    // }
    // break;
    // }
    // }
    // public void addMouseListener(MouseListener listener) {
    // if (mouseListeners == null) {
    // mouseListeners = new MouseListenerCollection();
    // }
    // mouseListeners.add(listener);
    // }
    //
    // public void removeMouseListener(MouseListener listener) {
    // if (mouseListeners != null) {
    // mouseListeners.remove(listener);
    // }
    // }
    //    
    // public void removeAllMouseListeners(){
    // if (mouseListeners != null) {
    // mouseListeners.removeAll(mouseListeners);
    // }
    // }
    // private class MyListener implements MouseListener, ClickListener {
    //
    // private boolean enabled;
    //	
    // public void onMouseDown(Widget sender, int x, int y) {
    // }
    //
    // public void setEnabled(boolean b) {
    // enabled = b;
    // }
    //
    // public boolean isEnabled() {
    // return enabled;
    // }
    //
    // public void onMouseEnter(Widget sender) {
    // // if (! isEnabled()) {
    // // hide();
    // // return;
    // // }
    // // timer.cancel();
    // // timer.schedule(3000);
    // }
    //
    // public void onMouseLeave(Widget sender) {
    // // if (! isEnabled()) {
    // // hide();
    // // return;
    // // }
    // // hide();
    // }
    //
    // public void onMouseMove(Widget sender, int x, int y) {
    // // if (! isEnabled()) {
    // // hide();
    // // return;
    // // }
    // // timer.cancel();
    // // timer.schedule(3000);
    // }
    //
    // public void onMouseUp(Widget sender, int x, int y) {
    // }
    //
    // public void onClick(Widget sender) {
    // // TODO ? l'idéal serait peut-être d'entrer en édition même quand on
    // // clique à côté du bouton Edit...
    //	    
    // // Window.alert("onclick");
    // // hide();
    // }
    //
    // }

    public void hide() {
	// TODO: implanter un fade out!
	super.hide();
    }
}
