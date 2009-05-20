/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.gwt.mosaic.core.client.DOM;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * WindowPanel for Pop-up windows used in {@link OsylAbstractEditor}.
 * 
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina
 *         Bauer-Oppinger</a>
 */
public class OsylWindowPanel extends WindowPanel {

    // editor which manages this WindowPanel as pop-up
    private OsylAbstractEditor editor;
    // remember with for restoring window
    private int restoredWidth;
    // remember height for restoring window
    private int restoredHeight;

    /**
     * Constructor specifying the {@link OsylAbstractEditor} which manages this
     * window panel as well as the title of the window panel.
     * 
     * @param title
     * @param editor
     */
    public OsylWindowPanel(String title, OsylAbstractEditor editor) {
	super(title);
	this.editor = editor;
	// default caption action (double click on caption of pop-up)
	// is COLLAPSE, we disable collapse of pop-up
	setCaptionAction(null);
    }

    /**
     * Called to resize and update layout of the window panel
     * 
     * @param newContentWidth
     * @param newContentHeight
     */
    public void updateLayout(int newContentWidth, int newContentHeight) {
	int diffHeight = newContentHeight - getContentHeight();
	editor.setOriginalEditorPopupHeight(editor
		.getOriginalEditorPopupHeight()
		+ diffHeight);
	setContentSize(newContentWidth, newContentHeight);
	layout();
    }

    /**
     * Maximizes window, allows move of window while maximized
     * 
     * @param oldState
     */
    @Override
    protected void maximize(WindowState oldState) {
	if (isResizable()) {
	    if (!isActive()) {
		toFront();
	    }
	    final Widget boundaryPanel = RootPanel.get();
	    if (oldState != WindowState.MINIMIZED) {
		restoredWidth = getContentWidth();
		restoredHeight = getContentHeight();
	    }
	    final int[] size = DOM.getClientSize(boundaryPanel.getElement());
	    final int[] size2 = DOM.getBoxSize(getElement());
	    final int[] size3 = DOM.getBoxSize(getLayoutPanel().getElement());
	    setPopupPosition(0, 0);
	    setContentSize(size[0] - (size2[0] - size3[0]), size[1]
		    - (size2[1] - size3[1]));
	    delayedLayout(MIN_DELAY_MILLIS);
	}
    }

    /**
     * Restores window to normal state, sets position of window according to
     * current position of maximized window
     * 
     * @param oldState
     */
    @Override
    protected void restore(WindowState oldState) {
	final Widget boundaryPanel = RootPanel.get();
	final int[] borders = DOM.getBorderSizes(boundaryPanel.getElement());
	if (isResizable() && oldState == WindowState.MAXIMIZED) {
	    int maxLeft =
		    getAbsoluteLeft() - borders[3]
			    - boundaryPanel.getAbsoluteLeft();
	    int maxTop =
		    getAbsoluteTop() - borders[0]
			    - boundaryPanel.getAbsoluteTop();
	    int restoredLeft =
		    maxLeft + (getContentWidth() - restoredWidth) / 2;
	    int restoredTop =
		    maxTop + (getContentHeight() - restoredHeight) / 2;
	    setPopupPosition(restoredLeft, restoredTop);
	    getLayoutPanel().setSize("0px", "0px");
	    setContentSize(restoredWidth, restoredHeight);
	    delayedLayout(MIN_DELAY_MILLIS);
	}
    }

    /**
     * Centers window with OsylEditorEntryPoint
     */
    @Override
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }
}
