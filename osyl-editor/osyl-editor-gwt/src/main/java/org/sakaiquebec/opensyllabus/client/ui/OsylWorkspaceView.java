/*******************************************************************************
 * $Id: OsylWorkspaceView.java 636 2008-05-27 15:41:58Z remi.saias@hec.ca $
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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.EditPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureAssessmentView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOUnitStructureView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOUnitView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylLongView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElementType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * OsylWorkspaceView is the main area in OpenSyllabus editor.<br/>
 * <br/>
 * 
 * OsylWorkspaceView implements {@link ViewContextSelectionEventHandler} to be
 * notified that it has to refresh its view, for instance when the user clicked
 * on a lecture to display it.
 * 
 * @version $Id: $
 */
public class OsylWorkspaceView extends OsylViewableComposite implements
	ViewContextSelectionEventHandler  {

    // View variables
    private VerticalPanel workspacePanel;
    private Widget currentView;

    // View Constructor
    public OsylWorkspaceView(COModelInterface model, OsylController controller) {
	super(model, controller);
	setWorkspacePanel(new VerticalPanel());
	getWorkspacePanel().setSize("100%", "100%");
	getWorkspacePanel().setHorizontalAlignment(
		HasHorizontalAlignment.ALIGN_CENTER);
	initWidget(getWorkspacePanel());
	
    }

    public COElementAbstract getModel() {
	return (COElementAbstract) super.getModel();
    }

    public VerticalPanel getWorkspacePanel() {
	return workspacePanel;
    }

    private void setWorkspacePanel(VerticalPanel workspacePanel) {
	this.workspacePanel = workspacePanel;
    }

    /**
     * {@inheritDoc}
     */
    public void onViewContextSelection(ViewContextSelectionEvent event) {
	COModelInterface eventModel = (COModelInterface) event.getSource();
	if (eventModel != null) {
	    setModel(eventModel);
	    refreshView();
	}
    }

    /**
     * Refreshes the current view according to current model object.
     */
    public void refreshView() {
	boolean viewFirstElement = true;
	if (getModel() != null) {
	    if (getModel().isCourseOutlineContent()) {
		getWorkspacePanel().clear();
		OsylLongView newView =
			new OsylLongView(getModel(), getController());
		currentView = newView;
		getWorkspacePanel().add(currentView);
	    } else if (getModel().isCOUnit()) {
		// Display a COContentUnit
		getWorkspacePanel().clear();
		OsylCOUnitView newView =
			new OsylCOUnitView(getModel(), getController(), viewFirstElement);
		((COUnit)getModel()).addEventHandler(newView);
		currentView = newView;
		getWorkspacePanel().add(currentView);
	    } else if (getModel().isCOStructureElement()) {
		// Display a COStructureElement
		getWorkspacePanel().clear();
		
		// Special case: evaluation
		if (COStructureElementType.ASSESSMENT_STRUCT
			.equals(((COStructureElement) getModel()).getType())) {
		    OsylCOStructureAssessmentView newView =
			    new OsylCOStructureAssessmentView(getModel(),
				    getController(),viewFirstElement);
		    currentView = newView;
		}// end special case
		else{
			OsylCOStructureView newView =
				new OsylCOStructureView(getModel(), getController(),false, viewFirstElement);
			currentView = newView; 
		}
		getWorkspacePanel().add(currentView);
	    } else if(getModel().isCOUnitStructure()){
		getWorkspacePanel().clear();
		OsylCOUnitStructureView newView =
			new OsylCOUnitStructureView(getModel(), getController());
		currentView = newView;
		getWorkspacePanel().add(currentView);
	    }
	} else {
	    Window.alert("owv : modele null");
	}
    }

    public void setBorderWidth(int i) {
	getWorkspacePanel().setBorderWidth(i);
    }

    public void setSize(String width, String height) {
	getWorkspacePanel().setSize(width, height);
    }

    public void add(Composite contentView) {
	getWorkspacePanel().add(contentView);
    }

    public OsylWorkspaceView getWorkspaceView() {
	return this;
    }

}
