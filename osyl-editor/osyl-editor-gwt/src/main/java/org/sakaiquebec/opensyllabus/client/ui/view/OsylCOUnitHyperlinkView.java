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
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PushButton;

/**
 * This view is used to display a COContentUnit in a list view, for instance a
 * lecture in a list of Lecture.
 * 
 * @version $Id: $
 */
public class OsylCOUnitHyperlinkView extends OsylViewableComposite implements
	UpdateCOUnitEventHandler {

    private FlexTable mainPanel;
    private PushButton coUnitHyperlink;
    private OsylCOUnitLabelView coUnitLabel;

    // View Constructor
    public OsylCOUnitHyperlinkView(COUnit model, OsylController controller) {
	this(model, controller, new OsylCOUnitLabelView(model, controller,
		true, OsylStyleLevelChooser.getLevelStyle(model)));
    }

    public OsylCOUnitHyperlinkView(COUnit model, OsylController controller,
	    OsylCOUnitLabelView lv) {
	super(model, controller);
	setCoUnitLabel(lv);
	initView();
    }

    protected void initView() {
	setMainPanel(new FlexTable());
	getMainPanel().setStylePrimaryName("Osyl-ListItemView-Table");
	COUnit coUnit = (COUnit) getModel();
	coUnit.addEventHandler(this);
	String p = coUnit.getProperty(COPropertiesType.PREFIX);
	if (p == null)
	    p = coUnit.getPosition();
	addCoUnitLink(p);

	addCoUnitLabel();

	initWidget(getMainPanel());
    }

    private void addCoUnitLink(String position) {
	position = position + (!position.trim().equals("") ? "-" : "");
	setCoUnitHyperlink(new PushButton());
	getCoUnitHyperlink().setText(position);
	getCoUnitHyperlink().setStylePrimaryName("Osyl-ListItemView-labelNoPushButton");
	getCoUnitHyperlink().addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		getController().getViewContext().setContextModel(
			(COUnit) getModel());
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

    private void addCoUnitLabel() {
	getMainPanel().setWidget(0, 1, getCoUnitLabel());
	getMainPanel().getFlexCellFormatter().setStylePrimaryName(0, 1,
		"Osyl-ListItemView-Hyperlink");
	getMainPanel().getFlexCellFormatter().setAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT,
		HasVerticalAlignment.ALIGN_MIDDLE);
    }

    public PushButton getCoUnitHyperlink() {
	return coUnitHyperlink;
    }

    public void setCoUnitHyperlink(PushButton CoUnitHyperlink) {
	this.coUnitHyperlink = CoUnitHyperlink;
    }

    public FlexTable getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(FlexTable mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylCOUnitLabelView getCoUnitLabel() {
	return coUnitLabel;
    }

    public void setCoUnitLabel(OsylCOUnitLabelView coUnitLabel) {
	this.coUnitLabel = coUnitLabel;
    }

    public void onUpdateModel(UpdateCOUnitEvent event) {
	COUnit counit = (COUnit) getModel();
	String p = counit.getProperty(COPropertiesType.PREFIX);
	if (p == null)
	    p = counit.getPosition();
	addCoUnitLink(p);
    }

}
