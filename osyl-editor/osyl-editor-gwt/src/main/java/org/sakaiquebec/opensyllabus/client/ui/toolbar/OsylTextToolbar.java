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

package org.sakaiquebec.opensyllabus.client.ui.toolbar;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.OsylPreviewView;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.OsylMenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;

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
    
    private OsylMenuBar leftMenuBar;
    
    private OsylMenuBar rightMenuBar;
    
    private OsylMenuBar sectionMenuBar;
    
    private FlowPanel menuBar;

    private MenuItem homePushButton;
    
    private MenuItem viewAllPushButton;

    private MenuItem savePushButton;

    private OsylMenuBar addMenuBar;

    private OsylMenuBar viewMenuBar;

    private MenuItem addMenuItem;

    private MenuItem viewMenuItem;

    private MenuItem attendeeViewMenuItem;

    private MenuItem publicViewMenuItem;

    private MenuItem publishPushButton;

    private MenuItem printPushButton;

    private MenuItem closePushButton;
    
    private MenuItem editPushButton;
    
    private MenuItemSeparator viewSeparator;
    
    private MenuItemSeparator previewSeparator;
    
    private MenuItemSeparator publishSeparator;
    
    private MenuItemSeparator printSeparator;
    
    private MenuItemSeparator editSeparator;
   

    // Image Bundle
    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    public OsylTextToolbar(final OsylController osylController) {
	// int messages
	setOsylController(osylController);
	uiMessages = osylController.getUiMessages();

	leftMenuBar = new OsylMenuBar(false);
	rightMenuBar = new OsylMenuBar(false);
	sectionMenuBar = new OsylMenuBar(false);
	viewSeparator = new MenuItemSeparator();
	previewSeparator = new MenuItemSeparator();
	publishSeparator = new MenuItemSeparator();
	printSeparator = new MenuItemSeparator();
	editSeparator = new MenuItemSeparator();
	menuBar = new FlowPanel();
	menuBar.add(leftMenuBar);
	menuBar.add(rightMenuBar);
	menuBar.add(sectionMenuBar);
	menuBar.setStylePrimaryName("Osyl-MenuBar");
	leftMenuBar.addStyleDependentName("Left");
	rightMenuBar.addStyleDependentName("Right");
	sectionMenuBar.addStyleDependentName("Section");
	closePushButton =
		createMenuItem("ButtonCloseToolBar", getOsylImageBundle()
			.cross(), "ButtonCloseToolBarTooltip");
	rightMenuBar.addItem(closePushButton);
	homePushButton =
		createMenuItem("ButtonHomeToolBar",
			getOsylImageBundle().home(), "ButtonHomeToolBarTooltip");

	viewAllPushButton =
		createMenuItem("ButtonViewAllToolBar",
			getOsylImageBundle().view_all(), "ButtonViewAllToolBarTooltip");

	savePushButton =
		createMenuItem("ButtonSaveToolBar",
			getOsylImageBundle().save(), "ButtonSaveToolBarTooltip");

	addMenuBar = new OsylMenuBar(true);
	addMenuBar.setTitle(uiMessages.getMessage("ButtonAddToolBarTooltip"));
	addMenuBar.setAutoOpen(true);
	addMenuBar.addStyleName("Osyl-MenuBar-vertical");
	addMenuBar.addStyleName("Osyl-MenuBar-Add");
	
	viewMenuBar = new OsylMenuBar(true);
	viewMenuBar.setTitle(uiMessages.getMessage("ButtonViewToolBarTooltip"));
	viewMenuBar.setAutoOpen(true);
	viewMenuBar.addStyleName("Osyl-MenuBar-vertical");
	viewMenuBar.addStyleName("Osyl-MenuBar-View");

	publishPushButton =
		createMenuItem("ButtonPublishToolBar", getOsylImageBundle()
			.publish(), "ButtonPublishToolBarTooltip");

	printPushButton =
		createMenuItem("ButtonPrintToolBar", getOsylImageBundle()
			.printer(), "ButtonPrintToolBarTooltip");
	printPushButton.setCommand(new Command() {

	    public void execute() {

		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
		    public void onSuccess(Boolean serverResponse) {
			if (serverResponse) {
			    OsylTextToolbar.this.openDownloadPrintLink();
			} else {
			    OsylAlertDialog oad =
				    new OsylAlertDialog(
					    uiMessages
						    .getMessage("Global.error"),
					    uiMessages
						    .getMessage("ButtonPrintToolBar.printVersionUnavailable"));
			    oad.center();
			    oad.show();
			}

		    }

		    public void onFailure(Throwable error) {
			Window.alert("RPC FAILURE - hasBeenPublished : "
				+ error.toString());
		    }
		};
		if (osylController.isReadOnly())
		    openDownloadPrintLink();
		else
		    osylController.hasBeenPublished(callback);

	    }
	});
	
	leftMenuBar.addItem(homePushButton);
	leftMenuBar.addSeparator(viewSeparator);
	leftMenuBar.addItem(viewAllPushButton);
	rightMenuBar.addItem(savePushButton);
	rightMenuBar.addSeparator(previewSeparator);
    editPushButton =
		createMenuItem("ButtonEditToolBar", getOsylImageBundle()
			.edit(), "ButtonEditToolBarTooltip");
    sectionMenuBar.addItem(editPushButton);
    sectionMenuBar.addSeparator(editSeparator);
	// MenuBar Item with icon - nice trick...
	addMenuItem =
		sectionMenuBar.addItem(AbstractImagePrototype.create(getOsylImageBundle().plus()).getHTML()
			+ uiMessages.getMessage("ButtonAddToolBar"), true,
			addMenuBar);
	
	addMenuItem.addStyleName("Osyl-MenuItem-vertical");
	addMenuItem.addStyleName("Osyl-MenuItem-Add");
	
	viewMenuItem =
		rightMenuBar.addItem(AbstractImagePrototype.create(getOsylImageBundle().preview()).getHTML()
			+ uiMessages.getMessage("ButtonViewToolBar"), true,
			viewMenuBar);
	viewMenuItem.addStyleName("Osyl-MenuItem-vertical");
	viewMenuItem.addStyleName("Osyl-MenuItem-View");
	
	if (OsylEditorEntryPoint.isInternetExplorer()) {
		DOM.setStyleAttribute(viewMenuItem.getElement(), 
			"backgroundPosition", "-49px 0");
	}
	
	addViewMenuBarItems();
	rightMenuBar.addSeparator(publishSeparator);
	rightMenuBar.addItem(publishPushButton);
	rightMenuBar.addSeparator(printSeparator);
	rightMenuBar.addItem(printPushButton);

	initWidget(menuBar);

    }

    private void addViewMenuBarItems() {
	MenuItem attendeeViewMenuItem =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"Preview.attendee_version"), new Command() {
		    public void execute() {
			new OsylPreviewView(SecurityInterface.ACCESS_ATTENDEE,
				getOsylController());
		    }
		});
	MenuItem publicViewMenuItem =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"Preview.public_version"), new Command() {
		    public void execute() {
			new OsylPreviewView(SecurityInterface.ACCESS_PUBLIC,
				getOsylController());
		    }
		});
	getViewMenuBar().addItem(attendeeViewMenuItem);
	getViewMenuBar().addItem(publicViewMenuItem);
    }

    public MenuItem createMenuItem(String messageKey,
	    ImageResource menuImage, String toolTipKey) {
	Command nullCommand = null;
	MenuItem menuItem =
		new MenuItem(AbstractImagePrototype.create(menuImage).getHTML()
			+ uiMessages.getMessage(messageKey), true, nullCommand);
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
    
    public OsylMenuBar getLeftMenuBar() {
    	return leftMenuBar;
    }

    public void setLeftMenuBar(OsylMenuBar leftMenuBar) {
    	this.leftMenuBar = leftMenuBar;
    }

    public OsylMenuBar getRightMenuBar() {
    	return rightMenuBar;
    }
    
    public OsylMenuBar getSectionMenuBar() {
    	return sectionMenuBar;
    }

    public void setSectionMenuBar(OsylMenuBar sectionMenuBar) {
    	this.sectionMenuBar = sectionMenuBar;
    }
    
    public MenuItemSeparator getViewSeparator() {
    	return viewSeparator;
    }

    public void setViewSeparator(MenuItemSeparator viewSeparator) {
    	this.viewSeparator = viewSeparator;
    }
    
    public MenuItemSeparator getPreviewSeparator() {
    	return previewSeparator;
    }

    public void setPreviewSeparator(MenuItemSeparator previewSeparator) {
    	this.previewSeparator = previewSeparator;
    }
    
    public MenuItemSeparator getPublishSeparator() {
        return publishSeparator;
    }

    public void setPublishSeparator(MenuItemSeparator publishSeparator) {
        this.publishSeparator = publishSeparator;
    }
    
    
    public MenuItemSeparator getPrintSeparator() {
    	return printSeparator;
    }

    public void setPrintSeparator(MenuItemSeparator printSeparator) {
    	this.printSeparator = printSeparator;
    }
    
    public MenuItemSeparator getEditSeparator() {
    	return editSeparator;
    }

    public void setEditSeparator(MenuItemSeparator editSeparator) {
    	this.editSeparator = editSeparator;
    }
    
    public MenuItem getHomePushButton() {
	return homePushButton;
    }

    public void setHomePushButton(MenuItem homePushButton) {
	this.homePushButton = homePushButton;
    }
    
    public MenuItem getViewAllPushButton() {
    return viewAllPushButton;
    }
    
    public void setViewAllPushButton(MenuItem viewAllPushButton) {
    this.viewAllPushButton = viewAllPushButton;
    }
    
    public MenuItem getSavePushButton() {
	return savePushButton;
    }

    public void setSavePushButton(MenuItem savePushButton) {
	this.savePushButton = savePushButton;
    }

    public OsylMenuBar getAddMenuBar() {
	return addMenuBar;
    }

    public void setAddMenuBar(OsylMenuBar addMenuButton) {
	this.addMenuBar = addMenuButton;
    }

    public OsylMenuBar getViewMenuBar() {
	return viewMenuBar;
    }

    public void setViewMenuBar(OsylMenuBar viewMenuButton) {
	this.viewMenuBar = viewMenuButton;
    }

    public MenuItem getAddMenuItem() {
	return addMenuItem;
    }

    public void setAddMenuItem(MenuItem addMenuItem) {
	this.addMenuItem = addMenuItem;
    }

    public MenuItem getViewMenuItem() {
	return viewMenuItem;
    }

    public void setViewMenuItem(MenuItem viewMenuItem) {
	this.viewMenuItem = viewMenuItem;
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

    public MenuItem getClosePushButton() {
	return closePushButton;
    }

    public void setClosePushButton(MenuItem closePushButton) {
	this.closePushButton = closePushButton;
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
    
    public void setPublicSectionMenuBar(OsylMenuBar sectionMenuBar) {
    this.sectionMenuBar = sectionMenuBar;
    }
    
    public OsylMenuBar getPublicSectionMenuBar() {
    return sectionMenuBar;
    }
    

    public MenuItem getEditPushButton() {
		return editPushButton;
	}

	public void setEditPushButton(MenuItem editPushButton) {
		this.editPushButton = editPushButton;
	}

	private void openDownloadPrintLink() {
	String url = GWT.getModuleBaseURL();
	String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	String siteId = OsylController.getInstance().getSiteId();
	String downloadUrl =
		serverId + "/access/content/group/" + siteId + "/"
			+ OsylController.PUBLISH_FOLDER_NAME + "/"
			+ OsylController.PRINT_VERSION_FILENAME;

	Window.open(downloadUrl, "_blank", "");
    }
}
