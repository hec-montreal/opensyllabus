/*******************************************************************************
 * *****************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This view is used to display a list of COContentUnit, for instance a list of
 * Lecture.
 * 
 * @version $Id: $
 */
public class OsylCOStructureView extends OsylViewableComposite implements
	UpdateCOStructureElementEventHandler {

    // View variables
    private VerticalPanel mainPanel;

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylCOStructureView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	initView();
    }

    protected void initView() {
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-UnitView-UnitPanel");
	getMainPanel().setWidth("98%");
	refreshView();
	initWidget(getMainPanel());
    }

    public void refreshView() {
	getMainPanel().clear();
	// displaying all sub views
	List<COElementAbstract> children = ((COStructureElement) getModel()).getChildren();
	Iterator<COElementAbstract> iter = children.iterator();
	while (iter.hasNext()) {
	    // this can be a Lecture leaf
	    COContentUnit itemModel = (COContentUnit) iter.next();
	    addListItemView(itemModel);
	}
    }

    protected void addListItemView(COContentUnit itemModel) {
	OsylCOStructureItemView listItemView =
		new OsylCOStructureItemView(itemModel, getController());
	getMainPanel().add(listItemView);
    }

    public COStructureElement getModel() {
	return (COStructureElement) super.getModel();
    }

    public void setModel(COModelInterface model) {
	super.setModel(model);
	getModel().addEventHandler(this);
    }

    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	refreshView();
    }

}
