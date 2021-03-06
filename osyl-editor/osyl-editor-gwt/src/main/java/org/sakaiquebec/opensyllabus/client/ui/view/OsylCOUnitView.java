/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
package org.sakaiquebec.opensyllabus.client.ui.view;

import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.EditPushButtonEventHandler.EditPushButtonEvent;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitView extends OsylViewableComposite implements
	UpdateCOUnitEventHandler {

    private VerticalPanel mainPanel;
    private boolean viewFirstElement;

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylCOUnitView(COModelInterface model, OsylController osylController) {
	this(model, osylController, false);
    }

    public OsylCOUnitView(COModelInterface model, OsylController osylController, boolean viewFirstElement) {
   	super(model, osylController);
   	setViewFirstElement(viewFirstElement);
   	initView();
    }

    private void initView() {
	setMainPanel(new VerticalPanel());
	refreshView();
	initWidget(getMainPanel());
    }

    private void refreshView() {
	getMainPanel().clear();
	getMainPanel().setStylePrimaryName("Osyl-WorkspaceView-MainPanel");
	// If we are editing a lecture or theme we allow to edit the title
	// otherwise we don't (presentation, contact info, etc.)

	OsylLabelView lbv = null;
	if (COUnitType.ASSESSMENT_UNIT.equals(getModel().getType())) {
	    // do not allow to delete the title and therefore the evaluation
	    // within the COContentUnit (only at COStructure)
	    lbv =
		    new OsylCOUnitAssessmentLabelView((COUnit) getModel(),
			    getController(), false, OsylStyleLevelChooser
				    .getLevelStyle(getModel()),isViewFirstElement());
	} else {
	    lbv =
		    new OsylCOUnitLabelView((COUnit) getModel(),
			    getController(), false, OsylStyleLevelChooser
				    .getLevelStyle(getModel()),true, isViewFirstElement());

	}
	if(isViewFirstElement()){
		getController().getMainView().getOsylToolbarView().addEventHandler(lbv);
	}
	getMainPanel().add(lbv);
	List<COElementAbstract> childrens = null;
	childrens = ((COUnit) getModel()).getChildrens();
	displayChildrens(childrens);
    }

    public void displayChildrens(List<COElementAbstract> childrens) {
	if (childrens == null) {
	    return;
	} else {
	    boolean hasMultipleCoUnitStructure = childrens.size() > 1;
	    Iterator<COElementAbstract> iter = childrens.iterator();
	    while (iter.hasNext()) {
		COElementAbstract absElement = iter.next();
		if (absElement.isCOUnitStructure()) {
		    COUnitStructure itemModel = (COUnitStructure) absElement;
		    addListItemView(itemModel, hasMultipleCoUnitStructure);
		} else {
		    Window
			    .alert("OsylCOUnitView  : unknown child type - is nor COUnitStructure either COUnitContent = "
				    + absElement.getType());
		    return;
		}
	    }
	}
    }

    protected void addListItemView(COUnitStructure itemModel, boolean showLabel) {
	OsylCOUnitStructureView coUnitStructureView =
		new OsylCOUnitStructureView(itemModel, getController(),
			showLabel);
	getMainPanel().add(coUnitStructureView);
    }

    public void onUpdateModel(UpdateCOUnitEvent event) {
	refreshView();
    }

    public COUnit getModel() {
	return (COUnit) super.getModel();
    }

    public boolean isViewFirstElement() {
	return viewFirstElement;
    }

    public void setViewFirstElement(boolean viewFirstElement) {
	this.viewFirstElement = viewFirstElement;
    }


}
