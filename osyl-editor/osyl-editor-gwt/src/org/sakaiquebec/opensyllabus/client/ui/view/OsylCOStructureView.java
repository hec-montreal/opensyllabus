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
import org.sakaiquebec.opensyllabus.shared.model.COContentType;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

    private Label titleLabel;

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
	COStructureElement coStructElt = (COStructureElement) this.getModel();
	getMainPanel().clear();
	titleLabel = new Label(getCoMessage(coStructElt.getType()));
	titleLabel.setStylePrimaryName("Osyl-UnitView-Title");
	getMainPanel().add(titleLabel);
	// displaying all sub views
	List<COElementAbstract> children = null;
	children = ((COStructureElement) getModel()).getChildren();
	displayChildren(children);	
    }
    
    public void displayChildren(List<COElementAbstract> children) {
	if ( children == null ) {
	    if (TRACE) Window.alert("OsylCOStructureView 82 - displayChildren() : No more children ");
	    return;
	}
	else {
	    if (TRACE) Window.alert("OsylCOStructureView 86 - displayChildren() : There are children! ");
	    Iterator<COElementAbstract> iter = children.iterator();
	    while (iter.hasNext()) {
           // this can be a Lecture leaf
           //  COContentUnit itemModel = (COContentUnit) iter.next();
		COElementAbstract absElement = iter.next();
		if ( absElement.isCOStructureElement() ) {
		    if (TRACE) Window.alert("OsylCOStructureView 93 - displayChildren() : COStructureElement = " + absElement.getType());
		    COStructureElement newCOStructEl = (COStructureElement) absElement;
	            addListItemView( newCOStructEl);	  
	            children = newCOStructEl.getChildren();
//	            if ( children.size() == 1 ) {
		    	displayChildren(children);
//	            }
		}
		else if (absElement.isCOContentUnit()) {
		    if (TRACE) Window.alert("OsylCOStructureView 100 - displayChildren() : COContentUnit = " + absElement.getType());
		    COContentUnit itemModel = (COContentUnit) absElement;
		    addListItemView(itemModel);
		}
		else {
		    if (TRACE) Window.alert("OsylCOStructureView 100 - displayChildren() : nor COStructureElement either COContentUnit = " + absElement.getType());
		    return;
		}
	    }
	}
    }

    protected void addListItemView(COContentUnit itemModel) {
	OsylCOStructureItemView listItemView =
		new OsylCOStructureItemView(itemModel, getController());
	getMainPanel().add(listItemView);
    }

    protected void addListItemView(COStructureElement itemModel) {
	Label COStructTitleLabel = new Label(getCoMessage(itemModel.getType()));
	COStructTitleLabel.setStylePrimaryName("Osyl-UnitView-Title");
	getMainPanel().add(COStructTitleLabel);
//	addCoUnitLink(getCoMessage(itemModel.getType()),itemModel);
    }
    
    private void addCoUnitLink(String type, final COStructureElement itemModel) {
	Hyperlink coUnitHyperlink = new Hyperlink(type,null);
	coUnitHyperlink.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		getController().getViewContext().setContextModel(itemModel);
	    }
	});
//	getMainPanel().setWidget(0, 0, coUnitHyperlink);
//	getMainPanel().getFlexCellFormatter().setWordWrap(0, 0, false);
//	getMainPanel().getFlexCellFormatter().setStylePrimaryName(0, 0,
//		"Osyl-ListItemView-labelNo");
//	getMainPanel().getFlexCellFormatter().setAlignment(0, 0,
//		HasHorizontalAlignment.ALIGN_LEFT,
//		HasVerticalAlignment.ALIGN_MIDDLE);

	getMainPanel().add(coUnitHyperlink);
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
