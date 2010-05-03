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

import org.sakaiquebec.opensyllabus.client.ui.listener.SplitterEventHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylHorizontalSplitPanel extends Composite {

    private SplitterEventHandler handler = null;
    private HorizontalSplitPanel horizontalSplitPanel = null;
    private Element splitElement = null;

    public Element getSplitElement() {
	return splitElement;
    }

    public OsylHorizontalSplitPanel(SplitterEventHandler handler) {
	this.handler = handler;

	horizontalSplitPanel = new HorizontalSplitPanel();

	initWidget(horizontalSplitPanel);

	splitElement = horizontalSplitPanel.getSplitElement();
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

}
