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
package org.sakaiquebec.opensyllabus.client.ui.base;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylUnitStructureTreeItemView extends OsylTreeItemBaseView
	implements UpdateCOUnitStructureEventHandler {

    public OsylUnitStructureTreeItemView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
    }

    protected String createTreeItemText() {
	String treeItemText = "";
	COUnitStructure itemModel = (COUnitStructure) getModel();
	treeItemText = itemModel.getLabel();
	return treeItemText;
    }

    public void setModel(COModelInterface model) {
	super.setModel(model);
	COUnitStructure itemModel = (COUnitStructure) getModel();
	itemModel.addEventHandler(this);
    }

    public void onUpdateModel(UpdateCOUnitStructureEvent event) {
	refreshView();
    }

}
