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
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This view is used to display a list of COContentUnit, for instance a list of
 * Lecture.
 * 
 * @version $Id: $
 */
public class OsylCOStructureView extends OsylViewableComposite implements
	UpdateCOStructureElementEventHandler {

    public static boolean TRACE = false;

    // View variables
    private VerticalPanel mainPanel;

    private OsylCOStructureLabelView editableTitleLabel;

    private boolean showTitleOnly = false;

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylCOStructureView(COModelInterface model,
	    OsylController osylController) {
	this(model, osylController, false);
    }

    public OsylCOStructureView(COModelInterface model,
	    OsylController osylController, boolean showTitleOnly) {
	super(model, osylController);
	this.showTitleOnly = showTitleOnly;
	initView();
    }

    protected void initView() {
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-WorkspaceView-MainPanel");
	refreshView();
	initWidget(getMainPanel());
    }

    public void refreshView() {
	getMainPanel().clear();
	editableTitleLabel =
		new OsylCOStructureLabelView(getModel(), getController(),
			false, OsylStyleLevelChooser.getLevelStyle(getModel()));
	getMainPanel().add(editableTitleLabel);
	if (getShowTitleOnly() == false) {
	    // displaying all sub views
	    List<COElementAbstract> children = null;
	    children = ((COStructureElement) getModel()).getChildrens();
	    displayChildren(children);
	}
    }

    /**
     * @return true if the view displays only the title
     */
    private boolean getShowTitleOnly() {
	return showTitleOnly;
    }

    public void displayChildren(List<COElementAbstract> children) {
	if (children == null) {
	    if (TRACE)
		Window.alert("OsylCOStructureView 91 : No more children ");
	    return;
	} else {
	    if (TRACE)
		Window.alert("OsylCOStructureView 95 : There are children! ");
	    Iterator<COElementAbstract> iter = children.iterator();
	    while (iter.hasNext()) {
		// this can be a Lecture leaf
		// COContentUnit itemModel = (COContentUnit) iter.next();
		COElementAbstract absElement = iter.next();
		if (absElement.isCOStructureElement()) {
		    if (TRACE)
			Window
				.alert("OsylCOStructureView 103 : COStructureElement = "
					+ absElement.getType());
		    List<COModelInterface> subModels =
			    getController().getOsylConfig()
				    .getOsylConfigRuler().getAllowedSubModels(
					    absElement);
		    COStructureElement newCOStructEl =
			    (COStructureElement) absElement;
		    newCOStructEl.addEventHandler(this);
		    if (!(absElement.getChildrens().size() == 1 && subModels
			    .isEmpty())) {
			addListItemView(newCOStructEl);
		    }
		    children = newCOStructEl.getChildrens();
		    // if ( children.size() == 1 ) {
		    displayChildren(children);
		    // }
		} else if (absElement.isCOUnit()) {
		    if (TRACE)
			Window
				.alert("OsylCOStructureView 114 : COContentUnit = "
					+ absElement.getType());
		    COUnit itemModel = (COUnit) absElement;
		    addListItemView(itemModel);
		} else {
		    if (TRACE)
			Window
				.alert("OsylCOStructureView 120 : nor COStructureElement either COContentUnit = "
					+ absElement.getType());
		    return;
		}
	    }
	}
    }

    protected void addListItemView(COUnit itemModel) {
	OsylCOUnitHyperlinkView listItemView =
		new OsylCOUnitHyperlinkView(itemModel, getController());
	getMainPanel().add(listItemView);
    }

    protected void addListItemView(COStructureElement itemModel) {
	OsylCOStructureLabelView v =
		new OsylCOStructureLabelView(itemModel, getController(), true,
			OsylStyleLevelChooser.getLevelStyle(itemModel));
	getMainPanel().add(v);
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
