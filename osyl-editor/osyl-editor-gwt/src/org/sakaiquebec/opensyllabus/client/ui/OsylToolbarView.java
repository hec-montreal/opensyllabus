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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresClosePushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresPublishPushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresSavePushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler.ClosePushButtonEvent;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler.PublishPushButtonEvent;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler.SavePushButtonEvent;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.Print;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylLongView;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * A view to the {@link OsylToolbar} displayed in the upper part of the editor.
 * the OsylToolbarView constructor takes care of instantiating the toolbar
 * itself and creating the appropriate listeners.<br/>
 * <br/>
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylToolbarView.java 521 2008-05-21 22:34:37Z
 *          sacha.lepretre@crim.ca $
 */

public class OsylToolbarView extends OsylViewableComposite implements
	FiresSavePushButtonEvents, FiresPublishPushButtonEvents,
	ViewContextSelectionEventHandler, FiresClosePushButtonEvents {

    private OsylTextToolbar osylToolbar;

    private OsylLongView osylPrintView = null;

    private OsylViewable previousMainView;

    private MenuItem closeMenuItem = null;

    private OsylConfigMessages uiMessages = getController().getUiMessages();

    private List<SavePushButtonEventHandler> saveEventHandlerList;
    private List<PublishPushButtonEventHandler> publishEventHandlerList;
    private List<ClosePushButtonEventHandler> closeEventHandlerList;

    private OsylEditorEntryPoint entryPoint =
	    OsylEditorEntryPoint.getInstance();

    public static boolean TRACE = false;

    public OsylToolbarView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
	osylToolbar = new OsylTextToolbar(getController());
	initWidget(getOsylToolbar());
    }

    public COElementAbstract getModel() {
	return (COElementAbstract) super.getModel();
    }

    class AddMenuCommand implements Command {

	private COElementAbstract parentModel;
	private String type;

	public AddMenuCommand(final COElementAbstract parentModel, String type) {
	    this.parentModel = parentModel;
	    this.type = type;
	}

	public void execute() {
	    if (parentModel.isCOStructureElement()) {
		COContentUnit.createDefaultCOContenUnit(type, getCoMessages(),
			parentModel);
	    } else if (parentModel.isCOContentUnit()) {
		COContentResourceProxy resProxModel =
			COContentResourceProxy.createDefaultResProxy(type,
				getCoMessages(), parentModel);
		if (type
			.equalsIgnoreCase(COContentResourceProxyType.ASSIGNMENT)) {
		    COContentUnit parentEvaluationUnit =
			    (COContentUnit) parentModel;
		    // Call the SAKAI server in order to receive an assignment
		    // id
		    // Callback will be processed by the Controller
		    resProxModel.getProperties().addProperty(
			    COPropertiesType.URI, "emptyAssignmentURI");
		    // IMPORTANT : when the rating (the last parameter) is -1
		    // then it is a default assignment
		    int rating = -1;
		    int openYear = 0;
		    int openMonth = 0;
		    int openDay = 0;
		    int closeYear = 0;
		    int closeMonth = 0;
		    int closeDay = 0;
		    String ratingString =
			    parentEvaluationUnit
				    .getProperty(COPropertiesType.RATING);
		    if (null != ratingString
			    || !"undefined".equals(ratingString)
			    || !"".equals(ratingString)) {
			rating = Integer.parseInt(ratingString.substring(0,ratingString.lastIndexOf("%")));
		    }

		    String openDateString =
			    parentEvaluationUnit
				    .getProperty(COPropertiesType.OPENDATE);
		    if (null != openDateString
			    || !"undefined".equals(openDateString)
			    || !"".equals(openDateString)) {
			openYear = Integer.parseInt(openDateString.substring(0, 4));
			openMonth = Integer.parseInt(openDateString.substring(5, 7));
			openDay = Integer.parseInt(openDateString.substring(8, 10));
			
		    }

		    String closeDateString =
			    parentEvaluationUnit
				    .getProperty(COPropertiesType.CLOSEDATE);
		    if (null != closeDateString
			    || !"undefined".equals(closeDateString)
			    || !"".equals(closeDateString)) {
			closeYear = Integer.parseInt(closeDateString.substring(0, 4));
			closeMonth = Integer.parseInt(closeDateString.substring(5, 7));
			closeDay = Integer.parseInt(closeDateString.substring(8, 10));
		    }

		    getController().createOrUpdateAssignment(resProxModel, "",
			    parentEvaluationUnit.getLabel(), null, openYear,
			    openMonth, openDay, 0, 0, closeYear, closeMonth,
			    closeDay, 0, 0, rating);
		}
	    }
	}
    }

    public void onViewContextSelection(ViewContextSelectionEvent event) {
	COModelInterface eventModel = (COModelInterface) event.getSource();
	if (eventModel != null) {
	    setModel(eventModel);
	    refreshView();
	}
    }

    private void initButtonsCommands() {
	// Save Button
	getOsylToolbar().getSavePushButton().setCommand(new Command() {
	    public void execute() {
		SavePushButtonEvent event = new SavePushButtonEvent("");
		Iterator<SavePushButtonEventHandler> iter =
			getSaveEventHandlerList().iterator();
		while (iter.hasNext()) {
		    SavePushButtonEventHandler handler =
			    (SavePushButtonEventHandler) iter.next();
		    handler.onSavePushButton(event);
		}
	    }
	});
	// Home Button
	getOsylToolbar().getHomePushButton().setCommand(new Command() {
	    public void execute() {
		// set to the starting viewContext
		COModelInterface homeModel =
			getController().getMainView().findStartingViewContext();
		getController().getViewContext().setContextModel(homeModel);
	    }
	});
	// Publish Button
	getOsylToolbar().getPublishPushButton().setCommand(new Command() {
	    public void execute() {
		PublishPushButtonEvent event = new PublishPushButtonEvent("");
		Iterator<PublishPushButtonEventHandler> iter =
			getPublishEventHandlerList().iterator();
		while (iter.hasNext()) {
		    PublishPushButtonEventHandler handler =
			    (PublishPushButtonEventHandler) iter.next();
		    handler.onPublishPushButton(event);
		}
	    }
	});
	// Print Button
	// This method works for Chrome and Safari Browsers
	// but it doesn't work well with FireFox
	// With FireFox, it prints using a text format in a DRAFT format
	// Maybe, it's related to the fact that Safari and Chrome are based on
	// the Webkit html rendering technology while Firefox is based on Gecko.
	getOsylToolbar().getPrintPushButton().setCommand(new Command() {
	    public void execute() { // Command Print
		if (osylPrintView == null) {
		    getController().getViewContext().setContextModel(
			    entryPoint.getModel());
		    osylPrintView =
			    new OsylLongView(entryPoint.getModel(),
				    getController());
		    osylPrintView.setStyleName("Osyl-LongView-CourseOutline");
		}
		previousMainView = entryPoint.getView();
		getController().getMainView().getWorkspaceView()
			.getWorkspacePanel().clear();
		getController().getMainView().getWorkspaceView()
			.getWorkspacePanel().add(osylPrintView);
		// Invisible iFrame that should be added to HTML page
		// in order to print Widgets using Print class from Andre
		// Freller
		getController()
			.getMainView()
			.getWorkspaceView()
			.getWorkspacePanel()
			.add(
				new HTML(
					"<iframe id='__printingFrame' style='width:0;height:0;border:0'></iframe>"));
		getController().setReadOnly(true);
		getController().getViewContext().closeAllEditors();
		getController().getMainView().setHorizontalSplitPanelPosition(
			"0px");
		final MenuItem bHome = getOsylToolbar().getHomePushButton();
		final MenuItem bSave = getOsylToolbar().getSavePushButton();
		final MenuBar bAdd = getOsylToolbar().getAddMenuButton();
		final MenuBar bView = getOsylToolbar().getViewMenuButton();
		final MenuItem bPublish =
			getOsylToolbar().getPublishPushButton();
		final MenuItem bPrint = getOsylToolbar().getPrintPushButton();
		getOsylToolbar().getMenuBar().clearItems();
		if (closeMenuItem == null) {
		    closeMenuItem =
			    new MenuItem(uiMessages
				    .getMessage("ButtonCloseToolBar"),
				    new Command() {
					public void execute() {
					    getController().setReadOnly(false);
					    entryPoint.refreshView();
					    entryPoint
						    .setView(previousMainView);
					    entryPoint.refreshView();
					    getController()
						    .getViewContext()
						    .setContextModel(
							    getController()
								    .getMainView()
								    .findStartingViewContext());
					    ((OsylMainView) previousMainView)
						    .refreshView();
					    getController()
						    .getMainView()
						    .setHorizontalSplitPanelPosition(
							    OsylTreeView
								    .getInitialSplitPosition());
					    getOsylToolbar().getMenuBar()
						    .addItem(bHome);
					    getOsylToolbar().getMenuBar()
						    .addItem(bSave);
					    getOsylToolbar()
						    .getMenuBar()
						    .addItem(
							    uiMessages
								    .getMessage("ButtonAddToolBar"),
							    bAdd);
					    getOsylToolbar()
						    .getMenuBar()
						    .addItem(
							    uiMessages
								    .getMessage("ButtonViewToolBar"),
							    bView);
					    getOsylToolbar().getMenuBar()
						    .addItem(bPublish);
					    getOsylToolbar().getMenuBar()
						    .addItem(bPrint);
					    closeMenuItem.setVisible(false);
					}
				    });
		    getOsylToolbar().getMenuBar().addItem(closeMenuItem);
		} else {
		    closeMenuItem.setVisible(true);
		    getOsylToolbar().getMenuBar().addItem(closeMenuItem);
		}
		final int sp = 100;
		Timer t = new Timer() {
		    public void run() {
			int documentHeight =
				osylPrintView.getOffsetHeight() + sp;
			entryPoint.setToolHeight(documentHeight);
			if (getBrowserType().equals("webkit")) {
			    printJSNI();
			} else {
			    draftPrinting();
			}
		    }
		};
		t.schedule(250);
	    }
	});
    }

    /**
     * Called when the print button is clicked!
     */

    private static native void printJSNI() /*-{  
               window.parent.print();
             }-*/;

    /**
     * The code comes from UserAgent.gwt.xml in gwt-user.jar
     * 
     * @return client or user agent browser name
     */
    private native String getBrowserType() /*-{ 
                var ua = navigator.userAgent.toLowerCase(); 
                if (ua.indexOf("opera") != -1) { 
                    return "opera"; 
                } 
                else if (ua.indexOf("webkit") != -1) { 
                    return "webkit"; 
                } 
                else if ((ua.indexOf("msie 6.0") != -1) || 
                         (ua.indexOf("msie 7.0") != -1)) { 
                    return "ie6"; 
                      } 
                else if (ua.indexOf("gecko") != -1) { 
                    var result = /rv:([0-9]+)\.([0-9]+)/.exec(ua); 
                    if (result && result.length == 3) { 
                      var version = (parseInt(result[1]) * 10) + parseInt(result[2]); 
                      if (version >= 18) 
                        return "gecko1_8"; 
                    } 
                    return "gecko"; 
                } 
                return "unknown"; 
            }-*/;

    /*
     * Draft Printing for Browser different from WebKit
     */
    private void draftPrinting() {
	Print
		.it(
			"<style type=text/css media=paper> "
				+ ".Osyl-UnitView-MainPanel { padding: 10px 5px 0px 50px; } "
				+ ".Osyl-LongView-CourseOutline { margin: 2px 30px 10px 2px; color:#063871; text-align:center; font-size: 20px; font-weight:bold; padding-top:4px; padding-bottom:4px; font-family: sans-serif, Arial, Verdana; } "
				+ ".Osyl-UnitView-Title { margin: 2px 30px 10px 2px; color:#063871; font-size: 16px; font-weight:bold; padding: 4px 0px 4px 1px; font-family: sans-serif, Arial, Verdana; border: 1px solid transparent; } "
				+ ".Osyl-UnitView-Title .Osyl-LabelEditor-View { margin: 2px 30px 10px 2px; color:#063871; font-size: 16px; font-weight:bold; padding:4px; font-family: sans-serif, Arial, Verdana; } "
				+ ".Osyl-UnitView-Title .Osyl-LabelEditor-TextBox { border: 1px solid #aaa; margin-bottom: 12px; color:#063871; font-size: 16px; font-weight:bold; padding-top:4px; padding-bottom:4px; font-family: sans-serif, Arial, Verdana; } "
				+ ".Osyl-UnitView-RubricName { font-family: sans-serif, Arial, Verdana; font-size: 14px; font-weight:bold; } "
				+ ".Osyl-UnitView-RubricImg { display: list-item; list-style-image: url(img/carreVert.gif); margin:3px 4px 4px 4px; } "
				+ ".Osyl-ResProxView-MetaInfo { display: block; margin: 15px 0px 15px 0px; font-family: sans-serif, Arial, Verdana; font-size: 10px; color: #a0a0a0; }"
				+ ".Osyl-ContactInfo { border-bottom: 1px solid #C3D9FF; } "
				+ "</style>", osylPrintView);
    }

    /**
     * refresh this whole view
     */
    public void refreshView() {
	if (getController().isInPreview()) {
	    if (getOsylToolbar().getClosePreviewPushButton() != null) {
		getOsylToolbar().getClosePreviewPushButton().setVisible(true);
		getOsylToolbar().getClosePreviewPushButton().setCommand(
			new Command() {
			    public void execute() {
				ClosePushButtonEvent event =
					new ClosePushButtonEvent("");
				Iterator<ClosePushButtonEventHandler> iter =
					getCloseEventHandlerList().iterator();
				while (iter.hasNext()) {
				    ClosePushButtonEventHandler handler =
					    (ClosePushButtonEventHandler) iter
						    .next();
				    handler.onClosePushButton(event);
				}
			    }

			});
	    }
	} else {
	    if (getModel() != null) {
		initButtonsCommands();
		getOsylToolbar().getHomePushButton().setVisible(true);
		getOsylToolbar().getSavePushButton().setVisible(true);
		getOsylToolbar().getPublishPushButton().setVisible(true);
		getOsylToolbar().getAddMenuButton().setVisible(true);
		getOsylToolbar().getPrintPushButton().setVisible(true);
		getOsylToolbar().getViewMenuButton().setVisible(true);
		getOsylToolbar().getViewMenuButton().clearItems();
		getOsylToolbar().getAddMenuButton().clearItems();
		MenuItem attendeeViewMenuItem =
			new MenuItem(getUiMessages().getMessage(
				"Preview.attendee_version"), new Command() {
			    public void execute() {
				new OsylPreviewView(
					SecurityInterface.SECURITY_ACCESS_ATTENDEE,
					getController());
			    }
			});
		MenuItem publicViewMenuItem =
			new MenuItem(getUiMessages().getMessage(
				"Preview.public_version"), new Command() {
			    public void execute() {
				new OsylPreviewView(
					SecurityInterface.SECURITY_ACCESS_PUBLIC,
					getController());
			    }
			});
		getOsylToolbar().getViewMenuButton().addItem(
			attendeeViewMenuItem);
		getOsylToolbar().getViewMenuButton()
			.addItem(publicViewMenuItem);

		// 3 big ViewContext cases
		if (getModel().isCourseOutlineContent()) {
		    getOsylToolbar().getAddMenuButton().setVisible(false);
		} else if (getModel().isCOContentUnit()) {
		    // Enables or Disables buttons in this ViewContext
		    COContentUnit castedModel = (COContentUnit) getModel();
		    getOsylToolbar().getAddMenuButton().setVisible(true);
		    // Clear menu list, and set it according to the viewcontext
		    try {
			List<COModelInterface> subModels =
				getController().getOsylConfig()
					.getOsylConfigRuler()
					.getAllowedSubModels(getModel());
			Iterator<COModelInterface> iter = subModels.iterator();
			while (iter.hasNext()) {
			    COModelInterface subModel =
				    (COModelInterface) iter.next();
			    if (subModel instanceof COContentResourceProxy) {
				getOsylToolbar().getAddMenuButton().addItem(
					getCoMessage(subModel.getType()),
					new AddMenuCommand(castedModel,
						subModel.getType()));
			    }
			}
		    } catch (RuntimeException e) {
			e.printStackTrace();
			Window.alert("Error while parsing rules: " + e);
		    }
		} else if (getModel().isCOStructureElement()) {
		    COStructureElement castedModel =
			    (COStructureElement) getModel();
		    // Clear menu list, and set it according to the viewcontext
		    try {
			List<COModelInterface> subModels =
				getController().getOsylConfig()
					.getOsylConfigRuler()
					.getAllowedSubModels(getModel());
			Iterator<COModelInterface> iter = subModels.iterator();
			while (iter.hasNext()) {
			    COModelInterface subModel =
				    (COModelInterface) iter.next();
			    // Special case : No addition is allowable under
			    // Header COStructure
			    String parentType = castedModel.getType();
			    if (parentType.endsWith("Header")) {
				getOsylToolbar().getAddMenuButton().setVisible(
					false);
				return;
			    } else {
				getOsylToolbar().getAddMenuButton().addItem(
					getCoMessage(subModel.getType()),
					new AddMenuCommand(castedModel,
						subModel.getType()));
			    }
			}
		    } catch (RuntimeException e) {
			e.printStackTrace();
			Window.alert("Error while parsing rules: " + e);
		    }
		}
	    }
	}
    } // refreshView

    private OsylTextToolbar getOsylToolbar() {
	return osylToolbar;
    }

    public void addEventHandler(PublishPushButtonEventHandler handler) {
	if (publishEventHandlerList == null) {
	    publishEventHandlerList =
		    new ArrayList<PublishPushButtonEventHandler>();
	}
	publishEventHandlerList.add(handler);
    }

    public void removeEventHandler(PublishPushButtonEventHandler handler) {
	if (publishEventHandlerList != null) {
	    publishEventHandlerList.remove(handler);
	}
    }

    public void addEventHandler(SavePushButtonEventHandler handler) {
	if (saveEventHandlerList == null) {
	    saveEventHandlerList = new ArrayList<SavePushButtonEventHandler>();
	}
	saveEventHandlerList.add(handler);
    }

    public void removeEventHandler(SavePushButtonEventHandler handler) {
	if (saveEventHandlerList != null) {
	    saveEventHandlerList.remove(handler);
	}
    }

    public void addEventHandler(ClosePushButtonEventHandler handler) {
	if (closeEventHandlerList == null) {
	    closeEventHandlerList =
		    new ArrayList<ClosePushButtonEventHandler>();
	}
	closeEventHandlerList.add(handler);
    }

    public void removeEventHandler(ClosePushButtonEventHandler handler) {
	if (closeEventHandlerList != null) {
	    closeEventHandlerList.remove(handler);
	}
    }

    public List<SavePushButtonEventHandler> getSaveEventHandlerList() {
	return saveEventHandlerList;
    }

    public void setSaveEventHandlerList(
	    List<SavePushButtonEventHandler> saveEventHandlerList) {
	this.saveEventHandlerList = saveEventHandlerList;
    }

    public List<PublishPushButtonEventHandler> getPublishEventHandlerList() {
	return publishEventHandlerList;
    }

    public void setPublishEventHandlerList(
	    List<PublishPushButtonEventHandler> publishEventHandlerList) {
	this.publishEventHandlerList = publishEventHandlerList;
    }

    public List<ClosePushButtonEventHandler> getCloseEventHandlerList() {
	return closeEventHandlerList;
    }

    public void setCloseEventHandlerList(
	    List<ClosePushButtonEventHandler> closeEventHandlerList) {
	this.closeEventHandlerList = closeEventHandlerList;
    }

}
