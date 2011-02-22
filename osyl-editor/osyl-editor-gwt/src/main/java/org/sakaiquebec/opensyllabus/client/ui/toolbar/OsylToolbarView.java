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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.EditPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresClosePushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresEditPushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresPublishPushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresSavePushButtonEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler.ClosePushButtonEvent;
import org.sakaiquebec.opensyllabus.client.controller.event.EditPushButtonEventHandler.EditPushButtonEvent;
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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.datepicker.client.DatePicker;

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
	FiresEditPushButtonEvents, UpdateCOStructureElementEventHandler {

    private OsylTextToolbar osylToolbar;

    private List<SavePushButtonEventHandler> saveEventHandlerList;
    private List<PublishPushButtonEventHandler> publishEventHandlerList;
    private List<ClosePushButtonEventHandler> closeEventHandlerList;
    private List<EditPushButtonEventHandler> editEventHandlerList;

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

    /**
     * refresh this whole view
     */
    public void refreshView() {
	if (getController().isInPreview()) {
	    /* View type menu buttons */
	    getOsylToolbar().getHomePushButton().setVisible(true);
	    getOsylToolbar().getViewSeparator().setVisible(true);
	    getOsylToolbar().getDisplayButton().setVisible(true);
	    getOsylToolbar().getDeleteDateButton().setVisible(getController().getSelectedDate()!=null);

	    /* Preview mode specific menu buttons */
	    getOsylToolbar().getClosePushButton().setVisible(true);

	    /* Edition type menu buttons */
	    getOsylToolbar().getSavePushButton().setVisible(false);
	    getOsylToolbar().getPreviewSeparator().setVisible(false);
	    getOsylToolbar().getViewMenuItem().setVisible(false);
	    getOsylToolbar().getPublishSeparator().setVisible(false);
	    getOsylToolbar().getPublishPushButton().setVisible(false);

	    /* Print menu button */
	    getOsylToolbar().getPrintSeparator().setVisible(true);
	    getOsylToolbar().getPrintPushButton().setVisible(true);

	    /* Add menu button */
	    getOsylToolbar().getAddMenuItem().setVisible(false);

	    /* Edit menu button */
	    getOsylToolbar().getEditSeparator().setVisible(false);
	    getOsylToolbar().getEditPushButton().setVisible(false);

	    setClosePushButtonCommand();
	    setHomePushButtonCommand();
	    setViewAllPushButtonCommand();
	    setSelectDateButtonCommand();
	    setDeleteDateButtonCommand();
	} else if (getController().isReadOnly()) {
	    /* View type menu buttons */
	    getOsylToolbar().getHomePushButton().setVisible(true);
	    getOsylToolbar().getViewSeparator().setVisible(true);
	    getOsylToolbar().getDisplayButton().setVisible(true);
	    getOsylToolbar().getDeleteDateButton().setVisible(getController().getSelectedDate()!=null);

	    /* Preview mode specific menu buttons */
	    getOsylToolbar().getClosePushButton().setVisible(false);

	    /* Edition type menu buttons */
	    getOsylToolbar().getSavePushButton().setVisible(false);
	    getOsylToolbar().getPreviewSeparator().setVisible(false);
	    getOsylToolbar().getViewMenuItem().setVisible(false);
	    getOsylToolbar().getPublishSeparator().setVisible(false);
	    getOsylToolbar().getPublishPushButton().setVisible(false);

	    /* Print menu button */
	    getOsylToolbar().getPrintSeparator().setVisible(false);
	    getOsylToolbar().getPrintPushButton().setVisible(true);

	    /* Edit menu button */
	    getOsylToolbar().getEditSeparator().setVisible(false);
	    getOsylToolbar().getEditPushButton().setVisible(false);

	    /* Add menu button */
	    getOsylToolbar().getAddMenuItem().setVisible(false);

	    setHomePushButtonCommand();
	    setViewAllPushButtonCommand();
	    setSelectDateButtonCommand();
	    setDeleteDateButtonCommand();
	} else {
	    if (getModel() != null) {
		setSavePushButtonCommand();
		setHomePushButtonCommand();
		setViewAllPushButtonCommand();
		setPublishPushButtonCommand();
		setSelectDateButtonCommand();
		setDeleteDateButtonCommand();

		/* View type menu buttons */
		getOsylToolbar().getHomePushButton().setVisible(true);
		getOsylToolbar().getViewSeparator().setVisible(true);
		getOsylToolbar().getDisplayButton().setVisible(true);
		getOsylToolbar().getDeleteDateButton().setVisible(getController().getSelectedDate()!=null);

		/* Preview mode specific menu buttons */
		getOsylToolbar().getClosePushButton().setVisible(false);

		/* Edition type menu buttons */
		getOsylToolbar().getSavePushButton().setVisible(true);
		getOsylToolbar().getPreviewSeparator().setVisible(true);
		getOsylToolbar().getViewMenuItem().setVisible(true);
		getOsylToolbar().getPublishSeparator().setVisible(true);
		getOsylToolbar().getPublishPushButton().setVisible(true);

		/* Print menu button */
		getOsylToolbar().getPrintSeparator().setVisible(true);
		getOsylToolbar().getPrintPushButton().setVisible(true);

		/* Add menu button */
		getOsylToolbar().getAddMenuItem().setVisible(false);
		getOsylToolbar().getAddMenuBar().clearItems();
		if (getController().getOsylConfig().getSettings()
			.isModelTitleEditable(getModel())
			&& getModel().isEditable()) {
		    setEditPushButtonCommand();
		    getOsylToolbar().getEditSeparator().setVisible(true);
		    getOsylToolbar().getEditPushButton().setVisible(true);
		} else {
		    getOsylToolbar().getEditSeparator().setVisible(false);
		    getOsylToolbar().getEditPushButton().setVisible(false);
		}
		// 3 big ViewContext cases

		if (getModel().isCourseOutlineContent()) {
		    getOsylToolbar().getEditSeparator().setVisible(false);
		    getOsylToolbar().getAddMenuItem().setVisible(false);
		} else if (getModel().isCOUnit()) {
		    // Enables or Disables buttons in this ViewContext
		    getOsylToolbar().getAddMenuItem().setVisible(true);
		    // Clear menu list, and set it according to the viewcontext
		    try {

			createAllowedCOUnitAddAction((COUnit) getModel());

		    } catch (RuntimeException e) {
			e.printStackTrace();
			Window.alert("Error while parsing rules: " + e);
		    }
		} else if (getModel().isCOUnitStructure()) {
		    // Enables or Disables buttons in this ViewContext
		    getOsylToolbar().getAddMenuItem().setVisible(true);
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
			boolean toolbarVisible = false;
			while (iter.hasNext()) {
			    COModelInterface subModel =
				    (COModelInterface) iter.next();
			    if (subModel instanceof COStructureElement) {
				List<COElementAbstract> childrens =
					castedModel.getChildrens();
				boolean hasUneditableChild = false;
				for (COElementAbstract c : childrens)
				    if (!c.isEditable())
					hasUneditableChild = true;
				if (!hasUneditableChild) {
				    toolbarVisible = true;
				    addAddCOStructureMenuItem(subModel
					    .getType(), new AddMenuCommand(
					    castedModel, subModel));
				}
			    } else {
				toolbarVisible = true;
				addAddCOMenuItem(subModel.getType(),
					new AddMenuCommand(castedModel,
						subModel));
			    }
			    getOsylToolbar().getAddMenuItem().setVisible(
				    toolbarVisible);

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
	// Enables or Disables buttons in this ViewContext
	/*
	 * if (iter.hasNext() == false) {
	 * getOsylToolbar().getAddMenuItem().setVisible(false); }
	 */
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
	/*
	 * if (iter.hasNext() == false) {
	 * getOsylToolbar().getAddMenuItem().setVisible(false); }
	 */
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

    public OsylTextToolbar getOsylToolbar() {
	return osylToolbar;
    }

    private void addAddCOMenuItem(String itemType, Command cmd) {
	String html =
		"<div id=\"add" + itemType + "\">" + getCoMessage(itemType)
			+ "</div>";
	getOsylToolbar().getAddMenuBar().addItem(html, true, cmd);
    }

    private void addAddCOStructureMenuItem(String itemType, Command cmd) {
	String html =
		"<div id=\"add" + itemType + "\">"
			+ getCoMessage(itemType + ".toolbar.title") + "</div>";
	getOsylToolbar().getAddMenuBar().addItem(html, true, cmd);
    }

    private void setSavePushButtonCommand() {
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
    }

    private void setHomePushButtonCommand() {
	// Home Button
	getOsylToolbar().getHomePushButton().setCommand(new Command() {
	    public void execute() {
		// set to the starting viewContext
		COModelInterface homeModel =
			getController().getMainView().findStartingViewContext();
		getController().getViewContext().setContextModel(homeModel);
	    }
	});
    }

    private void setSelectDateButtonCommand() {
	getOsylToolbar().getSelectDateButton().setCommand(new Command() {
	    public void execute() {

		final WindowPanel datePickerWind =
			new WindowPanel(
				getUiMessage("toolbar.button.selectDate.datePicker.title"),
				false);
		// set to the starting viewContext
		final DateTimeFormat dtf =
			getController().getSettings().getDateFormat();
		DatePicker dp = new DatePicker();
		dp.setVisible(true);
		dp.addValueChangeHandler(new ValueChangeHandler<Date>() {
		    @SuppressWarnings("deprecation")
		    public void onValueChange(ValueChangeEvent<Date> event) {
			datePickerWind.hide();
			Date d = event.getValue();
			d.setHours(0);
			d.setMinutes(0);
			d.setSeconds(0);
			getController().setSelectedDate(d);
			getOsylToolbar()
				.getSelectDateButton()
				.setHTML(
					AbstractImagePrototype.create(
						getOsylImageBundle()
							.calendar_view_month())
						.getHTML()
						+ getUiMessage("toolbar.button.selectDate")
						+ dtf.format(d));
			getOsylToolbar().getSelectDateButton().addStyleName(
				"Osyl-newElement");
			getOsylToolbar().getDeleteDateButton().setVisible(true);
		    }
		});
		datePickerWind.add(dp);
		datePickerWind.showModal();
	    }
	});
    }

    private void setDeleteDateButtonCommand() {
	getOsylToolbar().getDeleteDateButton().setCommand(new Command() {

	    public void execute() {
		getController().setSelectedDate(null);
		getOsylToolbar().getSelectDateButton().setHTML(
			AbstractImagePrototype.create(
				getOsylImageBundle().calendar_view_month())
				.getHTML()
				+ getUiMessage("toolbar.button.selectDate"));
		getOsylToolbar().getSelectDateButton().removeStyleName(
			"Osyl-newElement");
		getOsylToolbar().getDeleteDateButton().setVisible(false);
	    }

	});

    }

    private void setViewAllPushButtonCommand() {
	// View All Button
	getOsylToolbar().getViewAllPushButton().setCommand(new Command() {
	    public void execute() {
		// set to the starting viewContext
		getController().getViewContext().setContextModel(
			getController().getMainView().getModel());
	    }
	});
    }

    private void setPublishPushButtonCommand() {
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

    private void setClosePushButtonCommand() {
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

    }

    private void setEditPushButtonCommand() {
	// Publish Button
	getOsylToolbar().getEditPushButton().setCommand(new Command() {
	    public void execute() {
		EditPushButtonEvent event = new EditPushButtonEvent("");
		Iterator<EditPushButtonEventHandler> iter =
			getEditEventHandlerList().iterator();
		while (iter.hasNext()) {
		    EditPushButtonEventHandler handler =
			    (EditPushButtonEventHandler) iter.next();
		    handler.onEditPushButton(event);
		}
	    }
	});
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

    public void addEventHandler(EditPushButtonEventHandler handler) {
	editEventHandlerList = new ArrayList<EditPushButtonEventHandler>();
	editEventHandlerList.add(handler);
    }

    public void removeEventHandler(EditPushButtonEventHandler handler) {
	if (editEventHandlerList != null) {
	    editEventHandlerList.remove(handler);
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

    public List<EditPushButtonEventHandler> getEditEventHandlerList() {
	return editEventHandlerList;
    }

    public void setEditEventHandlerList(
	    List<EditPushButtonEventHandler> editEventHandlerList) {
	this.editEventHandlerList = editEventHandlerList;
    }

    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	refreshView();
    }

}
