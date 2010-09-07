/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.ui.dialog;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class OsylUnobstrusiveAlert : As should be obvious by its name, this Class is
 * implementing a unobtrusive alert dialog that pops-up and close by itself after
 * a fixed duration or display time.--------------------------------------------
 * IMPORTANT : The look of the alert is totally dependent of a CSS declaration
 * from the OsylCore.css file: .Osyl-UnobtrusiveAlert { background-color: white;
 * border: thin solid #C3D9FF; padding: 5px; text-decoration: blink; }
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */

public class OsylUnobtrusiveAlert extends DialogBox {

    final static int DEFAULT_DURATION = 3000; // in milliseconds
    private int displayDuration = DEFAULT_DURATION;
    private boolean DEFAULT_ANIM_BEHAVIOR = true;
    final static int DEF_CENTER_POS = -1;
    private int leftPosition = DEF_CENTER_POS;
    private int topPosition = DEF_CENTER_POS;
    private Label contentLabel;

    /**
     * OsylUnobstrusiveDialog 1 parameter Constructor
     * 
     * @param messageToDisplay : the message to be displayed by the alert
     *                Default duration is fixed to 3 seconds DEFAULT_DURATION
     *                Default position is centered (i.e. DEF_CENTER_POS)
     */
    public OsylUnobtrusiveAlert(String messageToDisplay) {
	this(messageToDisplay, DEFAULT_DURATION, DEF_CENTER_POS, DEF_CENTER_POS);
    }

    /**
     * OsylUnobstrusiveDialog 2 parameters Constructor
     * 
     * @param messageToDisplay : the message to be displayed by the alert
     * @param duration : the duration of the alert display --------------
     *                Default position is centered (i.e. DEF_CENTER_POS)
     */
    public OsylUnobtrusiveAlert(String messageToDisplay, int duration) {
	this(messageToDisplay, duration, DEF_CENTER_POS, DEF_CENTER_POS);
    }

    /**
     * OsylUnobstrusiveDialog Constructor
     * 
     * @param messageToDisplay : the message to be displayed by the alert
     * @param duration : the duration of the alert display
     * @param newTopPosition : the position of the top side of the alert
     * @param newLeftPosition : the position of the left side of the alert
     */
    public OsylUnobtrusiveAlert(String dialogContent, int duration,
	    int newTopPosition, int newLeftPosition) {
	// (autohide is false and modal is false
	super(false, false);
	final OsylUnobtrusiveAlert reference = this;
	setDisplayDuration(duration);
	setTopPosition(newTopPosition);
	setLeftPosition(newLeftPosition);
	this.setStyleName("Osyl-UnobtrusiveAlert");
	// Set the animation behaviour
	this.setAnimationEnabled(DEFAULT_ANIM_BEHAVIOR);
	// Set the dialog box's caption.
	VerticalPanel dialogVPanel = new VerticalPanel();
	dialogVPanel.setWidth("100%");
	dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
	// Set the Content
	contentLabel = new Label(dialogContent);
	dialogVPanel.add(contentLabel);
	// Set the position of the alert box
	if (this.getLeftPosition() <= DEF_CENTER_POS) {
	    this.center();
	    this.show();
	} else {
	    this.setPopupPosition(getLeftPosition(), getTopPosition());
	    this.show();
	}
	// Set the Timer which controls the duration of display
	Timer timer = new Timer() {
	    public void run() {
		reference.hide();
	    }
	};
	timer.schedule(getDisplayDuration());
	// Set the contents of the Widget
	// DialogBox is a SimplePanel, so you have to set its widget property to
	// whatever you want its contents to be.
	this.setWidget(dialogVPanel);
    }

    /**
     * @return the timeToDisplay value in millisecond
     */
    public int getDisplayDuration() {
	return this.displayDuration;
    }

    /**
     * @param newDuration the new value of displayDuration in millisecond
     */
    public void setDisplayDuration(int newDuration) {
	this.displayDuration = newDuration;
    }

    /**
     * @return the leftPosition value.
     */
    public int getLeftPosition() {
	return this.leftPosition;
    }

    /**
     * @param leftPosition the new value of leftPosition.
     */
    public void setLeftPosition(int leftPosition) {
	this.leftPosition = leftPosition;
    }

    /**
     * @return the topPosition value of the
     */
    public int getTopPosition() {
	return this.topPosition;
    }

    /**
     * @param topPosition the new value of topPosition.
     */
    public void setTopPosition(int topPosition) {
	this.topPosition = topPosition;
    }

    public void setMsg(String text) {
	contentLabel.setText(text);
    }
}
