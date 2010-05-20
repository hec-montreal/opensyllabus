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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

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
	ViewContextSelectionEventHandler, FiresClosePushButtonEvents,
	UpdateCOStructureElementEventHandler {

    private OsylTextToolbar osylToolbar;

    private List<SavePushButtonEventHandler> saveEventHandlerList;
    private List<PublishPushButtonEventHandler> publishEventHandlerList;
    private List<ClosePushButtonEventHandler> closeEventHandlerList;

    public static boolean TRACE = false;

    public OsylToolbarView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
	osylToolbar = new OsylTextToolbar(getController());
	initWidget(getOsylToolbar());
    }

    public COElementAbstract getModel() {
	return (COElementAbstract) super.getModel();
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
	// Home Button
	getOsylToolbar().getViewAllPushButton().setCommand(new Command() {
	    public void execute() {
		// set to the starting viewContext
		getController().getViewContext().setContextModel(getController().getMainView().getModel());
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
    }

    /**
     * refresh this whole view
     */
    public void refreshView() {
	if (getController().isInPreview()) {
	    getOsylToolbar().getHomePushButton().setVisible(false);
	    getOsylToolbar().getViewSeparator().setVisible(true);
	    getOsylToolbar().getViewAllPushButton().setVisible(true);
	    getOsylToolbar().getSavePushButton().setVisible(false);
	    getOsylToolbar().getPublishPushButton().setVisible(false);
	    getOsylToolbar().getEditionSeparator().setVisible(false);
	    getOsylToolbar().getPrintPushButton().setVisible(false);
	    getOsylToolbar().getAddMenuItem().setVisible(false);
	    getOsylToolbar().getViewMenuItem().setVisible(false);
	    getOsylToolbar().getClosePushButton().setVisible(true);
	    getOsylToolbar().getPreviewSeparator().setVisible(false);
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
		getOsylToolbar().getPreviewSeparator().setVisible(false);
		getOsylToolbar().getHomePushButton().setVisible(true);
		getOsylToolbar().getViewSeparator().setVisible(true);
	    getOsylToolbar().getViewAllPushButton().setVisible(true);
		getOsylToolbar().getSavePushButton().setVisible(true);
		getOsylToolbar().getPublishPushButton().setVisible(true);
		getOsylToolbar().getAddMenuItem().setVisible(false);
		getOsylToolbar().getPrintPushButton().setVisible(true);
		getOsylToolbar().getViewMenuItem().setVisible(true);
		getOsylToolbar().getEditionSeparator().setVisible(true);
		getOsylToolbar().getAddMenuBar().clearItems();

		// 3 big ViewContext cases
		if (getModel().isCourseOutlineContent()) {
		    getOsylToolbar().getAddMenuBar().setVisible(false);
		} else if (getModel().isCOUnit()) {
		    // Enables or Disables buttons in this ViewContext
			if (getModel().getChildrens().size() > 0) {
				getOsylToolbar().getAddMenuItem().setVisible(true);
			    getOsylToolbar().getAddMenuBar().setVisible(true);
			}
		    // Clear menu list, and set it according to the viewcontext
		    try {

			createAllowedCOUnitAddAction((COUnit) getModel());

		    } catch (RuntimeException e) {
			e.printStackTrace();
			Window.alert("Error while parsing rules: " + e);
		    }
		} else if (getModel().isCOUnitStructure()) {
		    // Enables or Disables buttons in this ViewContext
		    
		    if (getModel().getChildrens().size() > 0) {
		    	getOsylToolbar().getAddMenuItem().setVisible(true);
		    	getOsylToolbar().getAddMenuBar().setVisible(true);
		    }
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
		    castedModel.addEventHandler(this);
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
			    if (subModel instanceof COStructureElement)
				addAddUIMenuItem(subModel.getType(),
					new AddMenuCommand(castedModel,
						subModel));
			    else
				addAddCOMenuItem(subModel.getType(),
					new AddMenuCommand(castedModel,
						subModel));

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
		COContentResourceProxy cocrp = new COContentResourceProxy();
		cocrp.setType(type);
		addAddCOMenuItem(subModel.getType(), new AddMenuCommand(model,
			cocrp, subModel));
	    }
	}
    }

    private void createAllowedCOUnitAddAction(COUnit model) {
	List<COModelInterface> subModels =
		getController().getOsylConfig().getOsylConfigRuler()
			.getAllowedSubModels(model);
	Iterator<COModelInterface> iter = subModels.iterator();
	while (iter.hasNext()) {
	    COModelInterface subModel = (COModelInterface) iter.next();
	    addAddCOMenuItem(subModel.getType(), new AddUnitStructureCommand(
		    model, subModel.getType()));
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

    private void addAddCOMenuItem(String itemType, Command cmd) {
	String html =
		"<div id=\"add" + itemType + "\">" + getCoMessage(itemType)
			+ "</div>";
	getOsylToolbar().getAddMenuBar().addItem(html, true, cmd);
    }

    private void addAddUIMenuItem(String itemType, Command cmd) {
	String html =
		"<div id=\"add" + itemType + "\">" + getUiMessage(itemType)
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

    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	refreshView();
    }

}
