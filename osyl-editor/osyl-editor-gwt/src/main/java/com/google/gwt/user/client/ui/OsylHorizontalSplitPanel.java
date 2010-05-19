/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package com.google.gwt.user.client.ui;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.listener.SplitterEventHandler;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylHorizontalSplitPanel extends Composite {

    private SplitterEventHandler handler = null;
    private HorizontalSplitPanel horizontalSplitPanel = null;
    private Element splitElement = null;
    private boolean leftElementVisible = true;
    private int leftElementLastPosition;
    private Element collapseElement = null;
    private Anchor collapseAnchor = null;

    private boolean collapseMouseDown = false;

    public Element getSplitElement() {
	return splitElement;
    }

    public OsylHorizontalSplitPanel(SplitterEventHandler handler) {
	this.handler = handler;

	horizontalSplitPanel = new HorizontalSplitPanel();

	splitElement = horizontalSplitPanel.getSplitElement();

	createSplitter();

	initWidget(horizontalSplitPanel);
    }

    /**
     * Attach mouse listener
     */
    @Override
    protected void onLoad() {
	super.onLoad();

	getSplitPanel().addHandler(new MouseMoveHandler() {
	    public void onMouseMove(MouseMoveEvent event) {
		handler.onMouseMove(event);
	    }

	}, MouseMoveEvent.getType());
    }

    public HorizontalSplitPanel getSplitPanel() {
	return horizontalSplitPanel;
    }

    public int getSplitterPosition() {
	try {
	    String widthString =
		    DOM.getStyleAttribute(horizontalSplitPanel.getElement(0),
			    "width");
	    return Integer.parseInt(widthString.substring(0, widthString
		    .indexOf("px")));
	} catch (NumberFormatException e) {
	    Window.alert(e.toString());
	    return -1;
	}
    }

    protected void createSplitter() {
	collapseAnchor = new Anchor();
	collapseAnchor.setStylePrimaryName("Osyl-collapseButton");
	collapseAnchor.setTitle(OsylController.getInstance().getUiMessage(
		"OsylTreeView.collapse"));
	collapseElement = collapseAnchor.getElement();
	Element e =
		DOM.getChild(DOM.getChild(DOM.getChild(DOM.getChild(
			splitElement, 0), 0), 0), 0);
	e.setInnerHTML("");
	e.appendChild(collapseElement);
    }

    public void onBrowserEvent(Event event) {
	switch (DOM.eventGetType(event)) {

	case Event.ONMOUSEDOWN: {
	    Element target = DOM.eventGetTarget(event);
	    if (DOM.isOrHasChild(collapseElement, target)) {
		collapseMouseDown = true;
		collapseAnchor.addStyleName("down");
		DOM.eventPreventDefault(event);
	    }
	    if (DOM.isOrHasChild(splitElement, target)) {
	    	splitElement.getFirstChildElement().setClassName("hsplitter hsplitter-down");
	    }
	    break;
	}

	case Event.ONMOUSEUP: {
	    if (collapseMouseDown) {
		if (leftElementVisible) {
		    leftElementLastPosition = getSplitterPosition();
		    horizontalSplitPanel.setSplitPosition("0px");
		    collapseAnchor.addStyleName("collapse");
		    collapseAnchor.setTitle(OsylController.getInstance()
			    .getUiMessage("OsylTreeView.uncollapse"));
		} else {
		    horizontalSplitPanel
			    .setSplitPosition(leftElementLastPosition + "px");
		    collapseAnchor.removeStyleName("collapse");
		    collapseAnchor.setTitle(OsylController.getInstance()
			    .getUiMessage("OsylTreeView.collapse"));
		}
		leftElementVisible = !leftElementVisible;
		collapseMouseDown = false;
		collapseAnchor.removeStyleName("down");
		handler.onMouseMove(null);
		DOM.eventPreventDefault(event);
	    }
	    splitElement.getFirstChildElement().setClassName("hsplitter");
	    break;
	}
	}
	super.onBrowserEvent(event);
    }

}
