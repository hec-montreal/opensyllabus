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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * This view is used to display a COContentUnit in a list view, for instance a
 * lecture in a list of Lecture.
 * 
 * @version $Id: $
 */
public class OsylCOStructureItemView extends OsylViewableComposite {

    private FlexTable mainPanel;
    private Hyperlink coUnitHyperlink;
    private OsylCOStructureItemLabelView coUnitLabel;
    

    // View Constructor
    public OsylCOStructureItemView(COContentUnit model,
	    OsylController controller) {
	super(model, controller);
	setCoUnitLabel(new OsylCOStructureItemLabelView(model,controller,true));
	initView();
    }

    protected void initView(){
	setMainPanel(new FlexTable());
	getMainPanel().setWidth("92%");
	getMainPanel().setStylePrimaryName("Osyl-ListItemView-Table");
	COContentUnit coContentUnit = (COContentUnit) getModel();
	COStructureElement coStructElt =
		(COStructureElement) coContentUnit.getParent();
	addCoUnitLink(getCoMessage(coContentUnit.getType()), coStructElt
		.getChildPosition(coContentUnit));
	
	addCoUnitLabel();
	
	initWidget(getMainPanel());
    }
    
    private void addCoUnitLink(String type, String position) {
	setCoUnitHyperlink(new Hyperlink(type + " " + position + " - ",null));
	getCoUnitHyperlink().addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		getController().getViewContext().setContextModel(
			(COContentUnit) getModel());
	    }
	});
	getMainPanel().setWidget(0, 0, getCoUnitHyperlink());
	getMainPanel().getFlexCellFormatter().setWordWrap(0, 0, false);
	getMainPanel().getFlexCellFormatter().setStylePrimaryName(0, 0,
		"Osyl-ListItemView-labelNo");
	getMainPanel().getFlexCellFormatter().setAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_LEFT,
		HasVerticalAlignment.ALIGN_MIDDLE);
    }
    
   private void addCoUnitLabel(){
       getMainPanel().setWidget(0, 1, getCoUnitLabel());
	getMainPanel().getFlexCellFormatter().setStylePrimaryName(0, 1,
		"Osyl-ListItemView-Hyperlink");
	getMainPanel().getFlexCellFormatter().setAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT,
		HasVerticalAlignment.ALIGN_MIDDLE);
   }
    
    public Hyperlink getCoUnitHyperlink() {
        return coUnitHyperlink;
    }

    public void setCoUnitHyperlink(Hyperlink CoUnitHyperlink) {
        this.coUnitHyperlink = CoUnitHyperlink;
    }
    
    public FlexTable getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(FlexTable mainPanel) {
        this.mainPanel = mainPanel;
    }
    
    public OsylCOStructureItemLabelView getCoUnitLabel() {
        return coUnitLabel;
    }

    public void setCoUnitLabel(OsylCOStructureItemLabelView coUnitLabel) {
        this.coUnitLabel = coUnitLabel;
    }

    
}
