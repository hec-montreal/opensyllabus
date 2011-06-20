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
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.OsylMenuBar;

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

    private OsylMenuBar displayMenuBar;

    private MenuItem displayButton;

    private MenuItem viewAllPushButton;

    private MenuItem savePushButton;

    private OsylMenuBar addMenuBar;

    private OsylMenuBar viewMenuBar;

    private OsylMenuBar printMenuBar;

    private MenuItem addMenuItem;

    private MenuItem viewMenuItem;

    private MenuItem publishPushButton;

    private MenuItem printPushButton;

    private MenuItem closePushButton;

    private MenuItem editPushButton;

    private MenuItem selectDateButton;

    private MenuItem deleteDateButton;

    private MenuItemSeparator viewSeparator;

    private MenuItemSeparator previewSeparator;

    private MenuItemSeparator publishSeparator;

    private MenuItemSeparator printSeparator;

    private MenuItemSeparator editSeparator;

    AsyncCallback<Boolean> print_publishedVersion_callback_attendee =
	    new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean serverResponse) {
		    if (serverResponse) {
			OsylTextToolbar.this
				.openDownloadPrintPublishedVersionLink(SecurityInterface.ACCESS_ATTENDEE);
		    } else {
			OsylAlertDialog oad =
				new OsylAlertDialog(
					uiMessages.getMessage("Global.error"),
					uiMessages
						.getMessage("toolbar.button.print.printVersionUnavailable"));
			oad.center();
			oad.show();
		    }

		}

		public void onFailure(Throwable error) {
		    Window.alert("RPC FAILURE - hasBeenPublished : "
			    + error.toString());
		}
	    };

    AsyncCallback<Boolean> print_publishedVersion_callback_community =
	    new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean serverResponse) {
		    if (serverResponse) {
			OsylTextToolbar.this
				.openDownloadPrintPublishedVersionLink(SecurityInterface.ACCESS_COMMUNITY);
		    } else {
			OsylAlertDialog oad =
				new OsylAlertDialog(
					uiMessages.getMessage("Global.error"),
					uiMessages
						.getMessage("toolbar.button.print.printVersionUnavailable"));
			oad.center();
			oad.show();
		    }

		}

		public void onFailure(Throwable error) {
		    Window.alert("RPC FAILURE - hasBeenPublished : "
			    + error.toString());
		}
	    };

    AsyncCallback<Boolean> print_publishedVersion_callback_public =
	    new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean serverResponse) {
		    if (serverResponse) {
			OsylTextToolbar.this
				.openDownloadPrintPublishedVersionLink(SecurityInterface.ACCESS_PUBLIC);
		    } else {
			OsylAlertDialog oad =
				new OsylAlertDialog(
					uiMessages.getMessage("Global.error"),
					uiMessages
						.getMessage("toolbar.button.print.printVersionUnavailable"));
			oad.center();
			oad.show();
		    }

		}

		public void onFailure(Throwable error) {
		    Window.alert("RPC FAILURE - hasBeenPublished : "
			    + error.toString());
		}
	    };

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
		createMenuItem("toolbar.button.close", getOsylImageBundle()
			.cross(), "toolbar.button.close.tooltip");
	rightMenuBar.addItem(closePushButton);

	homePushButton =
		createMenuItem("toolbar.button.home", getOsylImageBundle()
			.home(), "toolbar.button.home.tooltip");

	savePushButton =
		createMenuItem("toolbar.button.save", getOsylImageBundle()
			.save(), "toolbar.button.save.tooltip");

	addMenuBar = new OsylMenuBar(true);
	addMenuBar
		.setTitle(uiMessages.getMessage("toolbar.button.add.tooltip"));
	addMenuBar.setAutoOpen(true);
	addMenuBar.addStyleName("Osyl-MenuBar-vertical");
	addMenuBar.addStyleName("Osyl-MenuBar-Add");

	viewMenuBar = new OsylMenuBar(true);
	viewMenuBar.setTitle(uiMessages
		.getMessage("toolbar.button.view.tooltip"));
	viewMenuBar.setAutoOpen(true);
	viewMenuBar.addStyleName("Osyl-MenuBar-vertical");
	viewMenuBar.addStyleName("Osyl-MenuBar-View");

	publishPushButton =
		createMenuItem("toolbar.button.publish", getOsylImageBundle()
			.publish(), "toolbar.button.publish.tooltip");

	leftMenuBar.addItem(homePushButton);
	leftMenuBar.addSeparator(viewSeparator);
	displayMenuBar = new OsylMenuBar(true);
	displayMenuBar.setTitle(uiMessages
		.getMessage("toolbar.button.display.tooltip"));
	displayMenuBar.setAutoOpen(true);
	displayMenuBar.addStyleName("Osyl-MenuBar-vertical");
	displayMenuBar.addStyleName("Osyl-MenuBar-View");
	addDisplayMenuBarItems();
	displayButton =
		leftMenuBar.addItem(
			AbstractImagePrototype.create(
				getOsylImageBundle().view_all()).getHTML()
				+ uiMessages
					.getMessage("toolbar.button.display"),
			true, displayMenuBar);
	displayButton.addStyleName("Osyl-MenuItem-vertical");
	displayButton.addStyleName("Osyl-MenuItem-View");
	leftMenuBar.addItem(displayButton);

	rightMenuBar.addItem(savePushButton);
	rightMenuBar.addSeparator(previewSeparator);
	editPushButton =
		createMenuItem("toolbar.button.edit", getOsylImageBundle()
			.edit(), "toolbar.button.edit.tooltip");
	sectionMenuBar.addItem(editPushButton);
	sectionMenuBar.addSeparator(editSeparator);
	// MenuBar Item with icon - nice trick...
	addMenuItem =
		sectionMenuBar.addItem(
			AbstractImagePrototype.create(
				getOsylImageBundle().plus()).getHTML()
				+ uiMessages.getMessage("toolbar.button.add"),
			true, addMenuBar);

	addMenuItem.addStyleName("Osyl-MenuItem-vertical");
	addMenuItem.addStyleName("Osyl-MenuItem-Add");

	viewMenuItem =
		rightMenuBar.addItem(
			AbstractImagePrototype.create(
				getOsylImageBundle().preview()).getHTML()
				+ uiMessages.getMessage("toolbar.button.view"),
			true, viewMenuBar);
	viewMenuItem.addStyleName("Osyl-MenuItem-vertical");
	viewMenuItem.addStyleName("Osyl-MenuItem-View");

	if (OsylEditorEntryPoint.isInternetExplorer()) {
	    DOM.setStyleAttribute(viewMenuItem.getElement(),
		    "backgroundPosition", "-49px 0");
	}

	addPreviewMenuBarItems();
	rightMenuBar.addSeparator(publishSeparator);
	rightMenuBar.addItem(publishPushButton);
	rightMenuBar.addSeparator(printSeparator);

	if (osylController.isReadOnly()) {
	    printPushButton =
		    createMenuItem("toolbar.button.print", getOsylImageBundle()
			    .printer(), "toolbar.button.print.tooltip");
	    printPushButton.setCommand(new Command() {
		public void execute() {
		    openDownloadPrintPublishedVersionLink();
		}
	    });
	    rightMenuBar.addItem(printPushButton);
	} else {
	    printMenuBar = new OsylMenuBar(true);
	    printMenuBar.setTitle(uiMessages
		    .getMessage("toolbar.button.print.tooltip"));
	    printMenuBar.setAutoOpen(true);
	    printMenuBar.addStyleName("Osyl-MenuBar-vertical");
	    printMenuBar.addStyleName("Osyl-MenuBar-View");
	    addprintMenuBarItems();
	    printPushButton =
		    rightMenuBar
			    .addItem(
				    AbstractImagePrototype.create(
					    getOsylImageBundle().printer())
					    .getHTML()
					    + uiMessages
						    .getMessage("toolbar.button.print"),
				    true, printMenuBar);
	    printPushButton.addStyleName("Osyl-MenuItem-vertical");
	    printPushButton.addStyleName("Osyl-MenuItem-View");
	}

	initWidget(menuBar);

    }

    private void addDisplayMenuBarItems() {
	Command nullCommand = null;

	viewAllPushButton =
		new MenuItem(uiMessages.getMessage("toolbar.button.viewAll"),
			true, nullCommand);
	viewAllPushButton.setTitle(uiMessages
		.getMessage("toolbar.button.viewAll.tooltip"));

	selectDateButton =
		new MenuItem(
			uiMessages.getMessage("toolbar.button.selectDate"),
			true, nullCommand);
	selectDateButton.setTitle(uiMessages
		.getMessage("toolbar.button.selectDate.tooltip"));

	deleteDateButton =
		new MenuItem(
			uiMessages.getMessage("toolbar.button.deleteDate"),
			true, nullCommand);
	deleteDateButton.setTitle(uiMessages
		.getMessage("toolbar.button.deleteDate.tooltip"));

	getDisplayMenuBar().addItem(viewAllPushButton);
	getDisplayMenuBar().addItem(selectDateButton);
	getDisplayMenuBar().addItem(deleteDateButton);

    }

    private void addprintMenuBarItems() {

	final AsyncCallback<Void> print_editionVersion_callback =
		new AsyncCallback<Void>() {
		    public void onSuccess(Void v) {
			OsylTextToolbar.this
				.openDownloadPrintEditionVersionLink();
		    }

		    public void onFailure(Throwable error) {
			Window.alert("RPC FAILURE - create printable edition version : "
				+ error.toString());
		    }
		};

	MenuItem editionPrintMenuItem =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"toolbar.button.print.edition_version"), new Command() {
		    public void execute() {
			AsyncCallback<Void> callback =
				new AsyncCallback<Void>() {
				    public void onSuccess(Void serverResponse) {
					osylController
						.createPrintableEditionVersion(print_editionVersion_callback);
				    }

				    public void onFailure(Throwable error) {
				    }
				};

			osylController.saveCourseOutline(callback);
		    }
		});

	MenuItem publishedPrintMenuItemAttendee =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"MetaInfo.audience.attendee"), new Command() {
		    public void execute() {
			osylController
				.hasBeenPublished(print_publishedVersion_callback_attendee);
		    }
		});
	MenuItem publishedPrintMenuItemCommunity =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"MetaInfo.audience.community"), new Command() {
		    public void execute() {
			osylController
				.hasBeenPublished(print_publishedVersion_callback_community);
		    }
		});
	MenuItem publishedPrintMenuItemPublic =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"MetaInfo.audience.public"), new Command() {
		    public void execute() {
			osylController
				.hasBeenPublished(print_publishedVersion_callback_public);
		    }
		});
	getPrintMenuBar().addItem(editionPrintMenuItem);
	getPrintMenuBar().addItem(publishedPrintMenuItemAttendee);
	getPrintMenuBar().addItem(publishedPrintMenuItemCommunity);
	getPrintMenuBar().addItem(publishedPrintMenuItemPublic);
    }

    private void addPreviewMenuBarItems() {
	MenuItem attendeeViewMenuItem =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"Preview.attendee_version"), new Command() {
		    public void execute() {
			new OsylPreviewView(SecurityInterface.ACCESS_ATTENDEE,
				getOsylController());
		    }
		});
	MenuItem communityViewMenuItem =
		new MenuItem(getOsylController().getUiMessages().getMessage(
			"Preview.community_version"), new Command() {
		    public void execute() {
			new OsylPreviewView(SecurityInterface.ACCESS_COMMUNITY,
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
	getViewMenuBar().addItem(communityViewMenuItem);
	getViewMenuBar().addItem(publicViewMenuItem);
    }

    public MenuItem createMenuItem(String messageKey, ImageResource menuImage,
	    String toolTipKey) {
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

    public OsylMenuBar getPrintMenuBar() {
	return printMenuBar;
    }

    public void setPrintMenuBar(OsylMenuBar printMenuButton) {
	this.printMenuBar = printMenuButton;
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

    public void setSelectDateButton(MenuItem selectDateButton) {
	this.selectDateButton = selectDateButton;
    }

    public MenuItem getSelectDateButton() {
	return selectDateButton;
    }

    public MenuItem getClosePushButton() {
	return closePushButton;
    }

    public void setClosePushButton(MenuItem closePushButton) {
	this.closePushButton = closePushButton;
    }

    public MenuItem getEditPushButton() {
	return editPushButton;
    }

    public void setEditPushButton(MenuItem editPushButton) {
	this.editPushButton = editPushButton;
    }

    public void setDeleteDateButton(MenuItem deleteDateButton) {
	this.deleteDateButton = deleteDateButton;
    }

    public MenuItem getDeleteDateButton() {
	return deleteDateButton;
    }

    public OsylMenuBar getDisplayMenuBar() {
	return displayMenuBar;
    }

    public void setDisplayMenuBar(OsylMenuBar displayMenuBar) {
	this.displayMenuBar = displayMenuBar;
    }

    public MenuItem getDisplayButton() {
	return displayButton;
    }

    public void setDisplayButton(MenuItem displayButton) {
	this.displayButton = displayButton;
    }

    private void openDownloadPrintPublishedVersionLink() {
	openDownloadPrintPublishedVersionLink(null);
    }

    private void openDownloadPrintPublishedVersionLink(String access) {
	String url = GWT.getModuleBaseURL();
	String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	String siteId = OsylController.getInstance().getSiteId();
	String siteTitle = OsylController.getInstance().getCOSerialized().getTitle();
	String downloadUrl;

	if (access == null) {
	    access = osylController.getPublishedSecurityAccessType();
	}

	if (access == null) {
	    access = osylController.getMainView().getAccess();
	}

	downloadUrl =
		serverId + "/sdata/c/attachment/" + siteId + "/OpenSyllabus/"
			+ siteTitle + ((access == null) ? "" : "_" + access)
			+ ".pdf?child=" + siteId;

	Window.open(downloadUrl, "_blank", "");
    }

    private void openDownloadPrintEditionVersionLink() {
	String url = GWT.getModuleBaseURL();
	String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	String siteId = OsylController.getInstance().getSiteId();
	String siteTitle = OsylController.getInstance().getCOSerialized().getTitle();
	String downloadUrl;
	downloadUrl =
		serverId + "/access/content/group/" + siteId + "/" + siteTitle
			+ ".pdf";
	downloadUrl = validateUrl(downloadUrl);
	Window.open(downloadUrl, "_blank", "");
    }
    
    private String validateUrl(String downloadUrl){
	downloadUrl = downloadUrl.replaceAll("'", "_");
	downloadUrl = downloadUrl.replaceAll("&", "_");
	return downloadUrl;
    }
}
