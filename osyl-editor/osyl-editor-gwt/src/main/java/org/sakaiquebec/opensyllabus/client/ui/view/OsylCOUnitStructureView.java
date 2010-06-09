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
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitStructureView extends OsylViewableComposite implements
	UpdateCOUnitStructureEventHandler {

    private VerticalPanel mainPanel;
    private boolean showLabel;

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylCOUnitStructureView(COModelInterface model,
	    OsylController osylController) {
	this(model, osylController, true);
    }

    public OsylCOUnitStructureView(COModelInterface model,
	    OsylController osylController, boolean showLabel) {
	super(model, osylController);
	this.showLabel = showLabel;
	initView();
    }

    private void initView() {
	refreshView();
	initWidget(getMainPanel());
    }

    private void refreshView() {
	mainPanel = new VerticalPanel();
	getMainPanel().setStylePrimaryName("Osyl-UnitView-UnitPanel");
	if (showLabel) {
	    OsylLabelView lbv =
		    new OsylLabelView((COUnitStructure) getModel(),
			    getController(), true, OsylStyleLevelChooser
				    .getLevelStyle(getModel()));
	    getController().getMainView().getOsylToolbarView().addEventHandler(lbv);
	    getMainPanel().add(lbv);
	}
	List<COElementAbstract> childrens = null;
	childrens = ((COUnitStructure) getModel()).getChildrens();
	displayChildrens(childrens);
    }

    public void displayChildrens(List<COElementAbstract> childrens) {
	if (childrens == null) {
	    return;
	} else {
	    boolean hasMultipleCoUnitContent =
		    hasMultipleChildrensOfTypeCOUnitContent(childrens);
	    boolean hasMultipleCOUnitStructure =
		    hasMultipleChildrensOfTypeCOUnitStructure(childrens);
	    Iterator<COElementAbstract> iter = childrens.iterator();
	    while (iter.hasNext()) {
		COElementAbstract absElement = iter.next();
		if (absElement.isCOUnitStructure()) {
		    COUnitStructure itemModel = (COUnitStructure) absElement;
		    addListItemView(itemModel, hasMultipleCOUnitStructure);
		} else if (absElement.isCOUnitContent()) {
		    COUnitContent itemModel = (COUnitContent) absElement;
		    addListItemView(itemModel, hasMultipleCoUnitContent);
		} else {
		    Window
			    .alert("OsylCOUnitView  : unknown child type - is nor COUnitStructure either COUnitContent = "
				    + absElement.getType());
		    return;
		}
	    }
	}
    }

    private boolean hasMultipleChildrensOfTypeCOUnitContent(
	    List<COElementAbstract> childrens) {
	boolean hasMultiple = false;
	boolean foundOne = false;
	Iterator<COElementAbstract> iter = childrens.iterator();
	while (iter.hasNext()) {
	    COElementAbstract coea = iter.next();
	    if (coea.isCOUnitContent()) {
		if (!foundOne) {
		    foundOne = true;
		} else {
		    hasMultiple = true;
		    break;
		}
	    }
	}
	return hasMultiple;
    }

    private boolean hasMultipleChildrensOfTypeCOUnitStructure(
	    List<COElementAbstract> childrens) {
	boolean hasMultiple = false;
	boolean foundOne = false;
	Iterator<COElementAbstract> iter = childrens.iterator();
	while (iter.hasNext()) {
	    COElementAbstract coea = iter.next();
	    if (coea.isCOUnitStructure()) {
		if (!foundOne) {
		    foundOne = true;
		} else {
		    hasMultiple = true;
		    break;
		}
	    }
	}
	return hasMultiple;
    }

    protected void addListItemView(COUnitContent itemModel, boolean showLabel) {
	OsylCOContentUnitView coUnitContentView =
		new OsylCOContentUnitView(itemModel, getController());
	getMainPanel().add(coUnitContentView);
    }

    protected void addListItemView(COUnitStructure itemModel, boolean showLabel) {
	OsylCOUnitStructureView coUnitStructureView =
		new OsylCOUnitStructureView(itemModel, getController(),
			showLabel);
	getMainPanel().add(coUnitStructureView);
    }

    public COUnitStructure getModel() {
	return (COUnitStructure) super.getModel();
    }

    public void onUpdateModel(UpdateCOUnitStructureEvent event) {
	refreshView();
    }

}
