/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylLongView extends OsylViewableComposite {

    // View variables
    private VerticalPanel mainPanel;

    private Label titleLabel;

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    /**
     * Constructor
     * @param model the model to display
     * @param osylController
     */
    public OsylLongView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
	initView();

    }

    /**
     * To init the view
     */
    private void initView() {
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-UnitView-UnitPanel");
	getMainPanel().setWidth("98%");
	refreshView();
	initWidget(getMainPanel());
    }

    /**
     * Refresh the view
     */
    public void refreshView() {
	COContent content = (COContent) this.getModel();
	getMainPanel().clear();

	try {
	    if(null != getController().getCOSerialized().getTitle()
		    && !"".equals(getController().getCOSerialized().getTitle())){
		titleLabel = new Label(getController().getCOSerialized().getTitle());
	    } else {
		titleLabel = new Label(getCoMessage("courseoutline"));
	    }

	} catch (Exception e) {
	    titleLabel = new Label(getCoMessage("courseoutline"));
	}
	titleLabel.setStylePrimaryName("Osyl-LongView-CourseOutline");
	getMainPanel().add(titleLabel);
	// displaying all sub views
	List<COElementAbstract> children = content.getChildren();
	Iterator<COElementAbstract> iter = children.iterator();
	while (iter.hasNext()) {
	    COElementAbstract childrenModel = (COElementAbstract) iter.next();
	    if(childrenModel.isCOContentUnit()){
		displayCOUnit((COContentUnit)childrenModel);
	    }
	    else if (childrenModel.isCOStructureElement()){
		displayCOStructureElement((COStructureElement)childrenModel);
	    }
	}

    }

    /**
     * Display a contentUnitElement
     * @param contentUnit
     */
    private void displayCOUnit(COContentUnit contentUnit){
	OsylCOContentUnitView view =
	    new OsylCOContentUnitView(contentUnit, getController());
	getMainPanel().add(view);
    }

    /**
     * Display an element of type structureElement
     * @param structureElement
     */
    private void displayCOStructureElement(COStructureElement structureElement){
	List<COElementAbstract> children = structureElement.getChildren();
	Iterator<COElementAbstract> iter = children.iterator();
	while (iter.hasNext()) {
	    COContentUnit contentUnit = (COContentUnit) iter.next();
	    displayCOUnit(contentUnit);
	}
    }

}
