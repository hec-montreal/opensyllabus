/**
 * *****************************************************************************
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

package org.sakaiquebec.opensyllabus.client.ui.base;


import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * A wrapper around a {@link PushButton} that uses different images for
 * enabled/disabled state.
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public class OsylPushButton extends Composite {

    private PushButton pushButton;
    private Image imgEnabled;
    private Image imgDisabled;
    private Image imgHover;

    /**
     * Constructor providing enabled and disabled images. The default button
     * state is disabled.
     * 
     * @param imageEnabled
     * @param imageDisabled
     */
    public OsylPushButton(Image imageEnabled, Image imageDisabled, Image imageHover) {
	HorizontalPanel mainPanel = new HorizontalPanel();
	mainPanel.setPixelSize(imageEnabled.getWidth(), imageEnabled
		.getHeight());
	pushButton = new PushButton();
	this.setImgEnabled(imageEnabled);
	this.setImgDisabled(imageDisabled);
	this.setImgHover(imageHover);
	this.getPushButton().getUpHoveringFace().setImage(getImgHover());

	this.getPushButton().setEnabled(false);
	this.getPushButton().setStylePrimaryName("Osyl-PushButton");
	mainPanel.add(pushButton);
	mainPanel.setVisible(true);
	initWidget(mainPanel);
    }

    public void setEnabledButton() {
	this.getPushButton().getUpFace().setImage(this.getImgEnabled());
	this.getPushButton().setEnabled(true);
    }

    public void setDisabledButton() {
	this.getPushButton().getUpFace().setImage(this.getImgDisabled());
	this.getPushButton().setEnabled(false);
    }

    public PushButton getPushButton() {
	return this.pushButton;
    }

    public void setPushButton(PushButton button) {
	this.pushButton = button;
    }

    public Image getImgEnabled() {
	return imgEnabled;
    }

    public void setImgEnabled(Image imgEnabled) {
	this.imgEnabled = imgEnabled;
    }

    public Image getImgDisabled() {
	return imgDisabled;
    }

    public void setImgDisabled(Image imgDisabled) {
	this.imgDisabled = imgDisabled;
    }

    /**
     * @return the imgHover
     */
    public Image getImgHover() {
	return imgHover;
    }

    /**
     * @param imgHover the imgHover to set
     */
    public void setImgHover(Image imgHover) {
	this.imgHover = imgHover;
    }

    public void setEnabled(boolean enabled) {
	getPushButton().setEnabled(enabled);
    }

    /**
     * Adds a <code>ClickListener</code>.
     * 
     * @param clickListener
     */
    public void addClickHandler(ClickHandler clickHandler) {
	getPushButton().addClickHandler(clickHandler);
    }
}
