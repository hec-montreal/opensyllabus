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
import org.sakaiquebec.opensyllabus.shared.model.COConfig;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.util.BrowserUtil;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

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
	private String subType;

	public AddMenuCommand(final COElementAbstract parentModel, String type) {
	    this.parentModel = parentModel;
	    this.type = type;
	    this.subType = null;
	}

	public AddMenuCommand(final COElementAbstract parentModel, String type,
		String subtype) {
	    this.parentModel = parentModel;
	    this.type = type;
	    this.subType = subtype;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
	    if (parentModel.isCOStructureElement()) {
		COUnit coU =
			COUnit.createDefaultCOUnit(type, getCoMessages(),
				parentModel);
		try {
		    List<COModelInterface> coUnitSubModels =
			    getController().getOsylConfig()
				    .getOsylConfigRuler().getAllowedSubModels(
					    coU);
		    if (!coUnitSubModels.isEmpty()) {
			COUnitStructure coUnitStructure =
				COUnitStructure.createDefaultCOUnitStructure(
					coUnitSubModels.get(0).getType(),
					getCoMessages(), coU);
			List<COModelInterface> coUnitStructureSubModels =
				getController().getOsylConfig()
					.getOsylConfigRuler()
					.getAllowedSubModels(coUnitStructure);
			if (!coUnitStructureSubModels.isEmpty())
			    COUnitContent.createDefaultCOContentUnit(
				    coUnitStructureSubModels.get(0).getType(),
				    getCoMessages(), coUnitStructure);
		    }

		} catch (RuntimeException e) {
		    // TODO: handle exception
		}

	    } else if (parentModel.isCOUnit()) {
		// TODO change this when multiple coUnitContent under one COUnit
		// will be authorized
		COUnitContent coUnitContent =
			(COUnitContent) parentModel.getChildrens().get(0);
		createASMContext(coUnitContent);

	    } else if (parentModel.isCOUnitStructure()) {
		// TODO change this when multiple coUnitContent under one COUnit
		// will be authorized
		COUnitContent coUnitContent =
			(COUnitContent) parentModel.getChildrens().get(0);
		createASMContext(coUnitContent);
	    }
	}

	private void createASMContext(COUnitContent coUnitContent) {
	    String defaultRubric = "";

	    // NEWS HACK
	    if (subType.equals(COContentResourceType.NEWS)) {
		defaultRubric = COContentRubric.RUBRIC_TYPE_NEWS;
	    } else {
		List<COModelInterface> coUnitSubModels =
			getController().getOsylConfig().getOsylConfigRuler()
				.getAllowedSubModels(coUnitContent);

		for (int i = 0; i < coUnitSubModels.size(); i++) {
		    Object subModel = coUnitSubModels.get(i);

		    if (subModel instanceof COContentRubric) {
			COContentRubric coContentRubric =
				(COContentRubric) subModel;
			if (!coContentRubric.getType().equals(
				COContentRubric.RUBRIC_TYPE_NEWS)) {
			    defaultRubric = coContentRubric.getType();
			    break;
			}
		    }
		}
	    }
	    COContentResourceProxy resProxModel =
		    COContentResourceProxy.createDefaultResProxy(type,
			    getCoMessages(), coUnitContent, subType,
			    defaultRubric);
	}

    }

    class AddUnitStructureCommand implements Command {

	private COElementAbstract parentModel;
	private String type;

	public AddUnitStructureCommand(final COElementAbstract parentModel,
		String type) {
	    this.parentModel = parentModel;
	    this.type = type;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
	    COUnitStructure coUnitStructure =
		    COUnitStructure.createDefaultCOUnitStructure(type,
			    getCoMessages(), parentModel);
	    try {
		List<COModelInterface> subModels =
			getController().getOsylConfig().getOsylConfigRuler()
				.getAllowedSubModels(coUnitStructure);
		if (!subModels.isEmpty()) {
		    COUnitContent.createDefaultCOContentUnit(subModels.get(0)
			    .getType(), getCoMessages(), coUnitStructure);
		}
	    } catch (RuntimeException e) {
		e.printStackTrace();
		Window.alert("Error while parsing rules: " + e);
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
	// TODO: cleanup! Do a separate command to remove all this code which
	// should not be in initButtonsCommands
	// Print Button
	// This method works for Chrome and Safari Browsers
	// but it doesn't work well with FireFox
	// With FireFox, it prints using a text format in a DRAFT format
	// Maybe, it's related to the fact that Safari and Chrome are based on
	// the Webkit html rendering technology while Firefox is based on Gecko.
	getOsylToolbar().getPrintPushButton().setCommand(new Command() {
	    public void execute() { // Command Print
		getController().setReadOnly(true);
		if (osylPrintView == null) {
		    getController().getViewContext().setContextModel(
			    entryPoint.getModel());
		    osylPrintView =
			    new OsylLongView(entryPoint.getModel(),
				    getController());
		    osylPrintView
			    .setStylePrimaryName("Osyl-MainPanel-ReadOnly");
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
					"<iframe id=\"__printingFrame\" style=\"width:0;height:0;border:0\"></iframe>"));
		getController().getViewContext().closeAllEditors();
		getController().getMainView().setHorizontalSplitPanelPosition(
			"0px");
		getOsylToolbar().getClosePushButton().setCommand(new Command() {
		    public void execute() {
			getController().setReadOnly(false);
			entryPoint.refreshView();
			entryPoint.setView(previousMainView);
			entryPoint.refreshView();
			getController().getViewContext().setContextModel(
				getController().getMainView()
					.findStartingViewContext());
			((OsylMainView) previousMainView).refreshView();
			getController().getMainView()
				.setHorizontalSplitPanelPosition(
					OsylTreeView.getInitialSplitPosition());
			OsylToolbarView.this.refreshView();
		    }
		});
		getOsylToolbar().getHomePushButton().setVisible(false);
		getOsylToolbar().getSavePushButton().setVisible(false);
		getOsylToolbar().getPublishPushButton().setVisible(false);
		getOsylToolbar().getPrintPushButton().setVisible(false);
		getOsylToolbar().getAddMenuItem().setVisible(false);
		getOsylToolbar().getViewMenuItem().setVisible(false);
		getOsylToolbar().getClosePushButton().setVisible(true);

		final int sp = 100;
		Timer t = new Timer() {
		    public void run() {
			int documentHeight =
				osylPrintView.getOffsetHeight() + sp;
			entryPoint.setToolHeight(documentHeight);
//			if (BrowserUtil.getBrowserType().equals("webkit")) {
//			    printJSNI();
//			} else {
			    draftPrinting();
//			}
		    }
		};
		t.schedule(250);
	    }
	});
    }

    /**
     * Called when the print button is clicked!
     */

    private static native void printJSNI()/*-{
					  window.parent.print();
					  }-*/;

    /*
     * Draft Printing for Browser different from WebKit
     */
    private void draftPrinting() {
	String stylesheetPath =
		getController().getOsylConfig().getStylesheetPath();
	String cssLinks =
		"<link href=\"" + stylesheetPath + COConfig.MAIN_CSS
			+ "\" type=\"text/css\" rel=\"stylesheet\"/>";
	cssLinks +=
		"\n" + "<link href=\"" + stylesheetPath + COConfig.PRINT_CSS
			+ "\" type=\"text/css\" rel=\"stylesheet\"/>";
	cssLinks +=
		"\n" + "<link href=\"" + stylesheetPath + COConfig.READONLY_CSS
			+ "\" type=\"text/css\" rel=\"stylesheet\"/>";
	Print.it(cssLinks, osylPrintView);
    }

    /**
     * refresh this whole view
     */
    public void refreshView() {
	if (getController().isInPreview()) {
	    getOsylToolbar().getHomePushButton().setVisible(false);
	    getOsylToolbar().getSavePushButton().setVisible(false);
	    getOsylToolbar().getPublishPushButton().setVisible(false);
	    getOsylToolbar().getPrintPushButton().setVisible(false);
	    getOsylToolbar().getAddMenuItem().setVisible(false);
	    getOsylToolbar().getViewMenuItem().setVisible(false);
	    getOsylToolbar().getClosePushButton().setVisible(true);
	    getOsylToolbar().getClosePushButton().setCommand(new Command() {
		public void execute() {
		    ClosePushButtonEvent event = new ClosePushButtonEvent("");
		    Iterator<ClosePushButtonEventHandler> iter =
			    getCloseEventHandlerList().iterator();
		    while (iter.hasNext()) {
			ClosePushButtonEventHandler handler =
				(ClosePushButtonEventHandler) iter.next();
			handler.onClosePushButton(event);
		    }
		}
	    });
	} else {
	    if (getModel() != null) {
		initButtonsCommands();
		getOsylToolbar().getClosePushButton().setVisible(false);
		getOsylToolbar().getHomePushButton().setVisible(true);
		getOsylToolbar().getSavePushButton().setVisible(true);
		getOsylToolbar().getPublishPushButton().setVisible(true);
		getOsylToolbar().getAddMenuItem().setVisible(true);
		getOsylToolbar().getPrintPushButton().setVisible(true);
		getOsylToolbar().getViewMenuItem().setVisible(true);
		getOsylToolbar().getAddMenuBar().clearItems();

		// 3 big ViewContext cases
		if (getModel().isCourseOutlineContent()) {
		    getOsylToolbar().getAddMenuBar().setVisible(false);
		} else if (getModel().isCOUnit()) {
		    // Enables or Disables buttons in this ViewContext
		    getOsylToolbar().getAddMenuBar().setVisible(true);
		    // Clear menu list, and set it according to the viewcontext
		    try {

			createAllowedCOUnitAddAction((COUnit) getModel());

		    } catch (RuntimeException e) {
			e.printStackTrace();
			Window.alert("Error while parsing rules: " + e);
		    }
		} else if (getModel().isCOUnitStructure()) {
		    // Enables or Disables buttons in this ViewContext
		    getOsylToolbar().getAddMenuBar().setVisible(true);
		    // Clear menu list, and set it according to the viewcontext
		    try {
			createAllowedCOUnitStructureAddAction((COUnitStructure) getModel());
			// TODO allow creation of nested COUNITSTRUCTURE
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
			if (subModels != null) {
			    Iterator<COModelInterface> iter =
				    subModels.iterator();
			    while (iter.hasNext()) {
				COModelInterface subModel =
					(COModelInterface) iter.next();
				// Special case : No addition is allowable under
				// Header COStructure
				String parentType = castedModel.getType();
				if (parentType.endsWith("Header")) {
				    getOsylToolbar().getAddMenuBar()
					    .setVisible(false);
				    return;
				} else {
				    addAddMenuItem(subModel.getType(),
					    new AddMenuCommand(castedModel,
						    subModel.getType()));
				}
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

    private void createAllowedCOUnitStructureAddAction(COUnitStructure model) {
	List<COModelInterface> subModels =
		getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(model.getChildrens().get(0));
	Iterator<COModelInterface> iter = subModels.iterator();
	while (iter.hasNext()) {
	    COModelInterface subModel = (COModelInterface) iter.next();
	    if (subModel instanceof COContentResource) {
		String type = "";
		if (subModel.getType().equals(COContentResourceType.TEXT)
			|| subModel.getType()
				.equals(COContentResourceType.NEWS)) {
		    type = COContentResourceProxyType.INFORMATION;
		} else if (subModel.getType().equals(
			COContentResourceType.BIBLIO_RESOURCE)) {
		    type = COContentResourceProxyType.BIBLIO;
		} else if (subModel.getType().equals(
			COContentResourceType.PERSON)) {
		    type = COContentResourceProxyType.PEOPLE;
		} else {
		    type = COContentResourceProxyType.REFERENCE;
		}
		addAddMenuItem(subModel.getType(), new AddMenuCommand(model,
			type, subModel.getType()));
	    }
	}
    }

    private void createAllowedCOUnitAddAction(COUnit model) {
	List<COModelInterface> subModels =
		getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(model);
	if (subModels != null) {
	    Iterator<COModelInterface> iter = subModels.iterator();
	    while (iter.hasNext()) {
		COModelInterface subModel = (COModelInterface) iter.next();
		addAddMenuItem(subModel.getType(), new AddUnitStructureCommand(
			model, subModel.getType()));
	    }
	}
	if (getModel().getChildrens().size() == 1) {
	    if ((COUnitStructure) model.getChildrens().get(0) != null) {
		createAllowedCOUnitStructureAddAction((COUnitStructure) model
			.getChildrens().get(0));
	    }
	}
    }

    private OsylTextToolbar getOsylToolbar() {
	return osylToolbar;
    }

    private void addAddMenuItem(String itemType, Command cmd) {
	String html =
		"<div id=\"add" + itemType + "\">" + getCoMessage(itemType)
			+ "</div>";
	getOsylToolbar().getAddMenuBar().addItem(html, true, cmd);
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
