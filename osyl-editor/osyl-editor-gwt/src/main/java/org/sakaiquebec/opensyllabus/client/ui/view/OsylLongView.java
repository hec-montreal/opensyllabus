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
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

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
     * 
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
	getMainPanel().setStylePrimaryName("Osyl-UnitView-MainPanel");
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
	    if (null != getController().getCOSerialized().getTitle()
		    && !"".equals(getController().getCOSerialized().getTitle())) {
		titleLabel =
			new Label(getController().getCOSerialized().getTitle());
		// + " N1");
	    } else {
		titleLabel = new Label(getCoMessage("courseoutline"));
		// + " N1");
	    }

	} catch (Exception e) {
	    titleLabel = new Label(getCoMessage("courseoutline"));
	}
	titleLabel.setStylePrimaryName("Osyl-UnitView-Title");
	titleLabel.addStyleName("Osyl-Title1");
	getMainPanel().add(titleLabel);
	// displaying all sub views
	List<COElementAbstract> children = content.getChildrens();
	displayChildren(children);
    }

    public void displayChildren(List<COElementAbstract> children) {
	if (children == null) {
	    return;
	} else {
	    Iterator<COElementAbstract> iter = children.iterator();
	    while (iter.hasNext()) {
		// this can be a Lecture leaf
		COElementAbstract absElement = iter.next();
		if (absElement.isCOStructureElement()) {
		    COStructureElement newCOStructEl =
			    (COStructureElement) absElement;

		    children = newCOStructEl.getChildrens();
		    List<COModelInterface> subModels =
			getController().getOsylConfig().getOsylConfigRuler()
				.getAllowedSubModels(newCOStructEl);
		    if (subModels!=null || children.size()>1){
			//addListItemView(newCOStructEl);
		    	displayCOStructureElement(newCOStructEl);
		    }
		    displayChildren(children);
		} else if (absElement.isCOUnit()) {
		    COUnit itemModel = (COUnit) absElement;
		    // addListItemView(itemModel);
		    displayCOUnit(itemModel);
		} else {
		    return;
		}
	    }
	}
    }

    private void addListItemView(COUnit itemModel) {
	OsylCOStructureItemView listItemView =
		new OsylCOStructureItemView(itemModel, getController());
	getMainPanel().add(listItemView);
    }

    private void addListItemView(COStructureElement itemModel) {
	Label COStructTitleLabel = new Label(getCoMessage(itemModel.getType()));
	COStructTitleLabel.setStylePrimaryName("Osyl-UnitView-Title");
	COStructTitleLabel.addStyleName(OsylStyleLevelChooser
		.getLevelStyle(itemModel));
	getMainPanel().add(COStructTitleLabel);
    }

    /**
     * Display a contentUnitElement
     * 
     * @param contentUnit
     */
    private void displayCOUnit(COUnit contentUnit) {
	OsylCOUnitView view = new OsylCOUnitView(contentUnit, getController());
	getMainPanel().add(view);
    }

    /**
     * Display an element of type structureElement
     * 
     * @param structureElement
     */
    private void displayCOStructureElement(COStructureElement structureElement) {
	OsylCOStructureView view = new OsylCOStructureView(structureElement, getController(), true);	
	getMainPanel().add(view);
    }

    // public int getLevel(COStructureElement itemModel) {
    // if (itemModel.isCourseOutlineContent()) {
    // return 1;
    // }
    // if ( itemModel.getParent().isCourseOutlineContent()) {
    // return 2;
    // }
    // if ( itemModel.getParent().isCOStructureElement() ) {
    // COStructureElement parent = (COStructureElement) itemModel.getParent();
    // if (parent.getParent().isCourseOutlineContent()){
    // return 3;
    // }
    // }
    // if ( itemModel.getParent().isCOStructureElement() ) {
    // COStructureElement parent1 = (COStructureElement) itemModel.getParent();
    // if (parent1.getParent().isCOStructureElement()) {
    // COStructureElement parent2 = (COStructureElement) parent1.getParent();
    // if ( parent2.getParent().isCourseOutlineContent()){
    // return 4;
    // }
    // }
    // }
    // return 5;
    // }
}
