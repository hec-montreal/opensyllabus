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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
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
    private boolean isMouseDownOnSplitter = false;
    private boolean isResizable = true;
    private int minSplitPosition = 130;
    private int maxSplitPosition = 400;
    private int initialSplitPercentPosition = 50;
    
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
    
    

    public int getSplitPosition() {
    	String widthString =
		    DOM.getStyleAttribute(horizontalSplitPanel.getElement(0),
			    "width");
	    return Integer.parseInt(widthString.substring(0, widthString
		    .indexOf("px")));
    }
    
    public void setSplitPosition(int pos) {
    	horizontalSplitPanel.setSplitPosition(pos +"px");
    }
    
    public int getComputedSplitPosition() {
    	return Math.max(
    	    Math.min(getSplitPosition(),
    	    	getMaxSplitPosition()),
    	    		getMinSplitPosition());
    }
    
    public Boolean isResizable() {
    	return getSplitPosition() <= getMaxSplitPosition() &&
    	getSplitPosition() >= getMinSplitPosition();
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
	Element target = DOM.eventGetTarget(event);
	
	boolean isCollapseElementTarget =
		DOM.isOrHasChild(collapseElement, target);
	boolean isSplitElementTarget =
		DOM.isOrHasChild(splitElement, target);
	switch (DOM.eventGetType(event)) {
	case Event.ONMOUSEMOVE: {
			if (isMouseDownOnSplitter) {
	    		setCursor("col-resize");
	    	}
	    break;
	}
	case Event.ONMOUSEDOWN: {
	    if (isCollapseElementTarget) {
		collapseMouseDown = true;
		collapseAnchor.addStyleName("down");
		DOM.eventPreventDefault(event);
	    }
	    if (isSplitElementTarget && leftElementVisible) {
	    	splitElement.getFirstChildElement().setClassName(
			"hsplitter hsplitter-down");
	    	if (!isCollapseElementTarget) {
	    		setCursor("col-resize");
	    		isMouseDownOnSplitter = true;
	    	}else{
	    		setCursor("");
	    	}
	    }
	    break;
	}

	case Event.ONMOUSEUP: {
	    if (collapseMouseDown) {
		if (leftElementVisible) {
		    leftElementLastPosition = getComputedSplitPosition();
		    setSplitPosition(0);
		    collapseAnchor.addStyleName("collapse");
		    collapseAnchor.setTitle(OsylController.getInstance()
			    .getUiMessage("OsylTreeView.uncollapse"));
		    splitElement.getFirstChildElement().setClassName(
			"hsplitter hsplitter-disable");
		} else {
			setSplitPosition(leftElementLastPosition);
		    collapseAnchor.removeStyleName("collapse");
		    collapseAnchor.setTitle(OsylController.getInstance()
			    .getUiMessage("OsylTreeView.collapse"));
		    splitElement.getFirstChildElement().setClassName(
			"hsplitter");
		}
		OsylController.getInstance().getMainView().resize();
		leftElementVisible = !leftElementVisible;
		collapseMouseDown = false;
		collapseAnchor.removeStyleName("down");
		handler.onMouseMove(null);
		
		DOM.eventPreventDefault(event);
	    }
	    setCursor("");
	    isMouseDownOnSplitter = false;
	    break;
		}
	}
	if(leftElementVisible && !collapseMouseDown) {
		super.onBrowserEvent(event);
	}
	
	if (DOM.eventGetType(event) == Event.ONMOUSEMOVE &&
			!isResizable() && leftElementVisible) {
		setSplitPosition(getComputedSplitPosition());
	}
	
	}
	
    private static native void setCursor(String cursor) /*-{
    	var o = $wnd.document.body;
		if (o.style.cursor != cursor) o.style.cursor = cursor;
	}-*/;
    
    public int getInitialSplitPosition() {
    	int maxToolWidth = Math.round(Window.getClientWidth() / 100
        		* initialSplitPercentPosition) - (splitElement.getOffsetWidth() / 2);
    	int maxItemWidth = 
    		OsylController.getInstance().getMainView().
    		getOsylTreeView().getMaxTreeWidth();
		return Math.max(
				Math.min(maxItemWidth, maxToolWidth), 
				minSplitPosition);
	}
    
	public int getMaxSplitPosition() {
		int max = this.getOffsetWidth() - 
		splitElement.getOffsetWidth() - maxSplitPosition;
		return max;
	}
	public void setMaxSplitPosition(int maxSplitPosition) {
		this.maxSplitPosition = maxSplitPosition;
	}
	
	public int getMinSplitPosition() {
		return minSplitPosition;
	}
	public void setMinSplitPosition(int minSplitPosition) {
		this.minSplitPosition = minSplitPosition;
	}
	
	public Boolean isResizing() {
		return isResizable && horizontalSplitPanel.isResizing();
	}
}
