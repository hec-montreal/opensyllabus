/**********************************************************************************
 * $Id: OsylUnitTreeItemView.java 1360 2008-10-01 18:39:09Z remi.saias@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.base;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;

/**
 * The Unit Tree Item View usable for the Osyl tree view
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepr�tre</a>
 * @version $Id: UnitTreeItemView.java 594 2008-05-26 16:10:05Z
 *          laurent.danet@hec.ca $
 */
public class OsylUnitTreeItemView extends OsylTreeItemBaseView implements
	UpdateCOUnitEventHandler {

    /**
     * Constructor.
     * 
     * @param model
     * @param osylController
     */
    public OsylUnitTreeItemView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
    }

    protected String createTreeItemText() {
	String treeItemText = "";
	String rate = "";
	COUnit itemModel = (COUnit) getModel();

	boolean showNumbering = getSettings().isTreeViewNumbering(itemModel);

	COElementAbstract eltAbs = itemModel.getParent();
	if (eltAbs.isCOStructureElement()) {
	    if (showNumbering) {
		String p = itemModel.getProperty(COPropertiesType.PREFIX);
		if (p == null)
		    p = itemModel.getPosition();
		p = p + (!p.trim().equals("") ? " : " : "");
		treeItemText = p + itemModel.getLabel();
	    } else {
		treeItemText = itemModel.getLabel();
	    }

	    if (getSettings().isTreeViewAssessmentShowRate(itemModel)
		    && COUnitType.ASSESSMENT_UNIT.equalsIgnoreCase(itemModel
			    .getType())) {
		if (itemModel.getProperty(COPropertiesType.WEIGHT) != null
			&& itemModel.getProperty(COPropertiesType.WEIGHT)
				.length() > 0) {
		    rate =
			    " ("
				    + itemModel
					    .getProperty(COPropertiesType.WEIGHT)
				    + "%)";
		    treeItemText += rate;
		}
	    }
	} else {
	    // A content unit with no parent structure element
	    treeItemText = getShortCoMessage(itemModel.getType());
	}
	return treeItemText;
    }

    public void setModel(COModelInterface model) {
	super.setModel(model);
	COUnit itemModel = (COUnit) getModel();
	itemModel.addEventHandler(this);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOUnitEvent event) {
	refreshView();
    }

}
