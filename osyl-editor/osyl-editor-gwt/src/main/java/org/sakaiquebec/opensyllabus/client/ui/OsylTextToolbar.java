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

package org.sakaiquebec.opensyllabus.client.ui;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A toolBar displayed in the upper part of the editor. the OsylToolbar
 * constructor takes care of instantiating the buttons. OsylToolbar only
 * provides setters and getters to access its buttons. Behavior of these buttons
 * is implemented in {@link OsylToolbarView}.
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylTextToolbar extends Composite {
    
    private OsylController osylController;

    /**
     * User interface message bundle
     */
    final OsylConfigMessages uiMessages;

    private MenuBar menuBar;
    
    private DecoratorPanel enclosingPanel;

    private MenuItem homePushButton;

    private MenuItem savePushButton;

    private MenuBar addMenuButton;

    private MenuBar viewMenuButton;

    private MenuItem attendeeViewMenuItem;

    private MenuItem publicViewMenuItem;

    private MenuItem publishPushButton;

    private MenuItem printPushButton;

    private MenuItem closePreviewPushButton;
    
    // Image Bundle
    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT.create(OsylImageBundleInterface.class);

    public OsylTextToolbar(OsylController osylController) {
	// int messages
	setOsylController(osylController);
	uiMessages = osylController.getUiMessages();

	menuBar = new MenuBar();
	
	enclosingPanel = new DecoratorPanel();
	enclosingPanel.setWidget(menuBar);
	enclosingPanel.setStylePrimaryName("Osyl-MenuBar");

	if (getOsylController().isInPreview()) {
	    closePreviewPushButton =
		    createMenuItem("ButtonCloseToolBar", 
			    getOsylImageBundle().cross(),
			    "ButtonCloseToolBarTooltip");
	    menuBar.addItem(closePreviewPushButton);
	} else {
	    homePushButton =
		    createMenuItem("ButtonHomeToolBar",
			    getOsylImageBundle().home(),
			    "ButtonHomeToolBarTooltip");

	    savePushButton =
		    createMenuItem("ButtonSaveToolBar",
			    getOsylImageBundle().save(),
			    "ButtonSaveToolBarTooltip");

	    addMenuButton = new MenuBar(true);
	    addMenuButton.setTitle(uiMessages
		    .getMessage("ButtonAddToolBarTooltip"));
	    addMenuButton.setAutoOpen(true);

	    viewMenuButton = new MenuBar(true);
	    viewMenuButton.setTitle(uiMessages
		    .getMessage("ButtonViewToolBarTooltip"));
	    viewMenuButton.setAutoOpen(true);

	    publishPushButton =
		    createMenuItem("ButtonPublishToolBar",
			    getOsylImageBundle().publish(),
			    "ButtonPublishToolBarTooltip");

	    printPushButton =
		    createMenuItem("ButtonPrintToolBar",
			    getOsylImageBundle().printer(),
			    "ButtonPrintToolBarTooltip");

	    menuBar.addItem(homePushButton);
	    menuBar.addItem(savePushButton);
	    // MenuBar Item with icon - nice trick...
	    menuBar.addItem( getOsylImageBundle().plus().getHTML() + "&nbsp;" + 
		    	uiMessages.getMessage("ButtonAddToolBar"),
		    	true,
		    	addMenuButton);
	    menuBar.addItem(getOsylImageBundle().preview().getHTML() + "&nbsp;" + 
		    uiMessages.getMessage("ButtonViewToolBar") ,
		    true,
		    viewMenuButton);
	    menuBar.addItem(publishPushButton);
	    menuBar.addItem(printPushButton);
	}
//	initWidget(menuBar);
	initWidget(enclosingPanel);

    }

    public MenuItem createMenuItem(String messageKey, 
	    AbstractImagePrototype menuImage, 
	    String toolTipKey) {
	Command nullCommand = null;
        MenuItem menuItem =
		new MenuItem(menuImage.getHTML() + 
			"&nbsp;"+ uiMessages.getMessage(messageKey), 
			true, 
			nullCommand);
        menuItem.setTitle(uiMessages.getMessage(toolTipKey));
	return menuItem;
    }

    public MenuItem createMenuItem(String messageKey, String toolTipKey) {
	Command nullCommand = null;
	MenuItem menuItem =
		new MenuItem(uiMessages.getMessage(messageKey), nullCommand);
	menuItem.setTitle(uiMessages.getMessage(toolTipKey));
	return menuItem;
    }

    public OsylController getOsylController() {
	return osylController;
    }

    public void setOsylController(OsylController osylController) {
	this.osylController = osylController;
    }

    public OsylImageBundleInterface getOsylImageBundle() {
	return osylImageBundle;
    }

    public MenuBar getMenuBar() {
	return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
	this.menuBar = menuBar;
    }

    public MenuItem getHomePushButton() {
	return homePushButton;
    }

    public void setHomePushButton(MenuItem homePushButton) {
	this.homePushButton = homePushButton;
    }

    public MenuItem getSavePushButton() {
	return savePushButton;
    }

    public void setSavePushButton(MenuItem savePushButton) {
	this.savePushButton = savePushButton;
    }

    public MenuBar getAddMenuButton() {
	return addMenuButton;
    }

    public void setAddMenuButton(MenuBar addMenuButton) {
	this.addMenuButton = addMenuButton;
    }

    public MenuBar getViewMenuButton() {
	return viewMenuButton;
    }

    public void setViewMenuButton(MenuBar viewMenuButton) {
	this.viewMenuButton = viewMenuButton;
    }

    public MenuItem getPublishPushButton() {
	return publishPushButton;
    }

    public void setPublishPushButton(MenuItem publishPushButton) {
	this.publishPushButton = publishPushButton;
    }

    public MenuItem getPrintPushButton() {
	return printPushButton;
    }

    public void setPrintPushButton(MenuItem printPushButton) {
	this.printPushButton = printPushButton;
    }

    public MenuItem getClosePreviewPushButton() {
	return closePreviewPushButton;
    }

    public void setClosePreviewPushButton(MenuItem closePreviewPushButton) {
	this.closePreviewPushButton = closePreviewPushButton;
    }

    public MenuItem getAttendeeViewMenuItem() {
	return attendeeViewMenuItem;
    }

    public void setAttendeeViewMenuItem(MenuItem attendeeViewMenuItem) {
	this.attendeeViewMenuItem = attendeeViewMenuItem;
    }

    public MenuItem getPublicViewMenuItem() {
	return publicViewMenuItem;
    }

    public void setPublicViewMenuItem(MenuItem publicViewMenuItem) {
	this.publicViewMenuItem = publicViewMenuItem;
    }
}
