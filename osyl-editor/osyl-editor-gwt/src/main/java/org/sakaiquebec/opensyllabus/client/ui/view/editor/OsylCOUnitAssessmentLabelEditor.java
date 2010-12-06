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
package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.Date;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOUnitAssessmentLabelView;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitAssessmentLabelEditor extends OsylCOUnitLabelEditor {

    private TextBox weightTextBox;
    private ListBox localisationListBox;
    private ListBox modeListBox;
    // private TextBox livrableTextBox;
    // private ListBox scopeListBox;
    // private DateBox startDateBox;
    private DateBox dateDateBox;
    private ListBox subTypeListBox;
    private ListBox typeListBox;

    private DateTimeFormat dateTimeFormat;

    public OsylCOUnitAssessmentLabelEditor(OsylAbstractView parent,
	    boolean isDeletable) {
	super(parent, isDeletable);
	dateTimeFormat = getController().getSettings().getDateFormat();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public boolean prepareForSave() {
	boolean ok = true;
	String messages = "";

	// required fields validations
	if (getText().trim().equals("")) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Assessment.name"))
			    + "\n";
	    ok = false;
	}
	String weight = weightTextBox.getText();
	if (weight.trim().equals("")) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Assessment.rating"))
			    + "\n";
	    ok = false;
	} else {
	    try {
		NumberFormat nf = NumberFormat.getDecimalFormat();
		double w = nf.parse(weight);
		if (w < 0 || w > 100)
		    throw new Exception();
	    } catch (Exception e) {
		messages +=
			getUiMessage("Assessment.field.weight.format") + "\n";
		ok = false;
	    }
	}

	String endDateString = dateDateBox.getTextBox().getText();
	if (!endDateString.trim().equals("")) {
	    try {
		dateTimeFormat.parseStrict(endDateString);
	    } catch (IllegalArgumentException e) {
		messages +=
			getView().getUiMessages().getMessage(
				"Global.field.date.format",
				getUiMessage("Assessment.date"),
				dateTimeFormat.getPattern())
				+ "\n";
		ok = false;
	    }
	}

	if (typeListBox.getSelectedIndex() == 0) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Assessment.type"))
			    + "\n";
	    ok = false;
	}

	if (localisationListBox.getSelectedIndex() == 0) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Assessment.location"))
			    + "\n";
	    ok = false;
	}
	if (modeListBox.getSelectedIndex() == 0) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Assessment.mode"))
			    + "\n";
	    ok = false;
	}
	if (!ok) {
	    OsylAlertDialog osylAlertDialog =
		    new OsylAlertDialog(getView().getUiMessage("Global.error"),
			    messages);
	    osylAlertDialog.center();
	    osylAlertDialog.show();
	}
	return ok;
    }

    public void enterView() {

	super.enterView();

	if (getView().getTextFromModel() != null
		&& getView().getTextFromModel().length() > 0) {
	    String rating =
		    (getView().getWeight() != null && !getView().getWeight()
			    .equals("")) ? " (" + getView().getWeight() + "%)"
			    : "";

	    String date =
		    (getView().getDateEnd() != null) ? ("  " + dateTimeFormat
			    .format(getView().getDateEnd())) : "";

	    setText(getView().getTextFromModel() + rating + date);
	}
	// If we are in read-only mode, we return now to not add buttons and
	// listeners enabling edition or deletion:

	if (!isDeletable()) {
	    getMainPanel().add(getAdditionalInfos());
	}
	if (!isReadOnly())
	    getMainPanel().add(getMetaInfoLabel());
	refreshButtonPanel();

    } // enterView

    @Override
    public Widget[] getOptionWidgets() {

	VerticalPanel vp = new VerticalPanel();
	vp.setStylePrimaryName("Osyl-EditorPopup-OptionGroup-Row");
	vp.setSpacing(7);

	VerticalPanel ponderationPanel = new VerticalPanel();
	ponderationPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	HTML l1 =
		new HTML(getUiMessage("Assessment.rating")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	weightTextBox = new TextBox();
	weightTextBox.setText(getView().getWeight());
	weightTextBox.setWidth("40px");
	weightTextBox.setTitle(getUiMessage("Assessment.rating.tooltip"));
	ponderationPanel.add(l1);
	HorizontalPanel weightPanel = new HorizontalPanel();
	weightPanel.add(weightTextBox);
	weightPanel.add(new Label("%"));
	ponderationPanel.add(weightPanel);

	VerticalPanel localisationPanel = new VerticalPanel();
	localisationPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	HTML l2 =
		new HTML(getUiMessage("Assessment.location")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	localisationListBox = new ListBox();
	localisationListBox.setName("Assessment.location");
	localisationListBox
		.setTitle(getUiMessage("Assessment.location.tooltip"));
	localisationListBox.addItem("");
	localisationListBox.addItem(getView().getCoMessage(
		"Assessment.Location.inclass"));
	localisationListBox.addItem(getView().getCoMessage(
		"Assessment.Location.home"));
	selectItemListBox(localisationListBox, getView().getLocation());
	localisationPanel.add(l2);
	localisationPanel.add(localisationListBox);

	VerticalPanel modePanel = new VerticalPanel();
	modePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	HTML l3 =
		new HTML(getUiMessage("Assessment.mode")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	modeListBox = new ListBox();
	modeListBox.setName("Assessment.mode");
	modeListBox.setTitle(getUiMessage("Assessment.mode.tooltip"));
	modeListBox.addItem("");
	modeListBox.addItem(getView().getCoMessage("Assessment.Mode.ind"));
	modeListBox.addItem(getView().getCoMessage("Assessment.Mode.team"));
	selectItemListBox(modeListBox, getView().getMode());
	modePanel.add(l3);
	modePanel.add(modeListBox);

	// VerticalPanel livrablePanel = new VerticalPanel();
	// livrablePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	// Label l4 = new Label(getUiMessage("Assessment.deliverable"));
	// livrableTextBox = new TextBox();
	// livrableTextBox
	// .setTitle(getUiMessage("Assessment.deliverable.tooltip"));
	// livrableTextBox.setText(getView().getResult());
	// livrablePanel.add(l4);
	// livrablePanel.add(livrableTextBox);
	//
	// VerticalPanel scopePanel = new VerticalPanel();
	// scopePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	// Label l5 = new Label(getUiMessage("Assessment.scope"));
	// scopeListBox = new ListBox();
	// scopeListBox.setName("Assessment.scope");
	// scopeListBox.setTitle(getUiMessage("Assessment.scope.tooltip"));
	// scopeListBox.addItem(getView().getCoMessage("Assessment.Scope.Obl"));
	// scopeListBox.addItem(getView().getCoMessage("Assessment.Scope.Fac"));
	// selectItemListBox(scopeListBox, getView().getScope());
	// scopePanel.add(l5);
	// scopePanel.add(scopeListBox);

	VerticalPanel endDatePanel = new VerticalPanel();
	endDatePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	HTML l7 = new HTML(getUiMessage("Assessment.date"));
	DateBox.DefaultFormat dateBoxFormat =
		new DateBox.DefaultFormat(dateTimeFormat);

	dateDateBox = new DateBox();
	dateDateBox.setFormat(dateBoxFormat);
	dateDateBox.setTitle(getUiMessage("Assessment.date.tooltip"));
	dateDateBox.setValue(getView().getDateEnd());
	endDatePanel.add(l7);
	endDatePanel.add(dateDateBox);

	VerticalPanel subTypePanel = new VerticalPanel();
	subTypePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l8 = new Label(getView().getCoMessage("Assessment.Subtype"));
	subTypeListBox = new ListBox();
	subTypeListBox.setName("Assessment.subtype");
	subTypeListBox.setTitle(getUiMessage("Assessment.subtype.tooltip"));
	subTypeListBox.addItem("");
	subTypeListBox.addItem(getView().getCoMessage(
		"Assessment.Subtype.paper"));
	subTypeListBox.addItem(getView().getCoMessage(
		"Assessment.Subtype.elect"));
	subTypeListBox.addItem(getView()
		.getCoMessage("Assessment.Subtype.oral"));
	selectItemListBox(subTypeListBox, getView().getSubmitionType());
	subTypePanel.add(l8);
	subTypePanel.add(subTypeListBox);

	VerticalPanel typePanel = new VerticalPanel();
	typePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	HTML l9 =
		new HTML(getUiMessage("Assessment.type")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	typeListBox = new ListBox();
	typeListBox.setName("Assessment.type");
	typeListBox.setTitle(getUiMessage("Assessment.type.tooltip"));
	typeListBox.addItem("");
	List<String> evalTypeList =
		getController().getOsylConfig().getEvalTypeList();
	if (evalTypeList != null) {
	    for (String evalTypeKey : evalTypeList) {
		typeListBox.addItem(getView().getCoMessage(evalTypeKey));
	    }
	}

	boolean editExamDate = false;
	if (getController().getOsylConfig().getSettings().containsKey(
		"assessement.exam.date.editable")
		&& getController().getOsylConfig().getSettings()
			.getSettingsProperty("assessement.exam.date.editable")
			.equals("true")) {
	    editExamDate = true;
	}

	if (!editExamDate) {
	    typeListBox.addChangeHandler(new ChangeHandler() {

		public void onChange(ChangeEvent event) {
		    int selectedIndex = typeListBox.getSelectedIndex();
		    setText(typeListBox.getItemText(selectedIndex));
		    typeListBox.setSelectedIndex(selectedIndex);
		    if (getText()
			    .equals(
				    getView().getCoMessage(
					    "Assessment.Type.intra_exam"))
			    || getText().equals(
				    getView().getCoMessage(
					    "Assessment.Type.final_exam"))) {
			dateDateBox.setValue(null);
			dateDateBox.setEnabled(false);
		    } else {
			dateDateBox.setEnabled(true);
		    }
		}

	    });
	}
	selectItemListBox(typeListBox, getView().getAssessmentType());
	if (!editExamDate) {
	    if (getView().getAssessmentType() != null
		    && (getView().getAssessmentType()
			    .equals(
				    getView().getCoMessage(
					    "Assessment.Type.intra_exam")) || getView()
			    .getAssessmentType().equals(
				    getView().getCoMessage(
					    "Assessment.Type.final_exam")))) {
		dateDateBox.setValue(null);
		dateDateBox.setEnabled(false);
	    }
	}
	typePanel.add(l9);
	typePanel.add(typeListBox);

	HorizontalPanel ligneSuper = new HorizontalPanel();
	HorizontalPanel ligne1 = new HorizontalPanel();
	HorizontalPanel ligne2 = new HorizontalPanel();

	if (super.getOptionWidgets() != null) {
	    for (Widget w : super.getOptionWidgets()) {
		ligneSuper.add(w);
	    }
	    vp.add(ligneSuper);
	}

	ligne1.add(typePanel);
	ligne1.add(ponderationPanel);
	ligne1.add(localisationPanel);
	// ligne1.add(livrablePanel);

	ligne2.add(modePanel);
	ligne2.add(endDatePanel);
	ligne2.add(subTypePanel);
	// ligne2.add(scopePanel);

	vp.add(ligne1);
	vp.add(ligne2);

	return new Widget[] { vp };
    }

    protected OsylCOUnitAssessmentLabelView getView() {
	return (OsylCOUnitAssessmentLabelView) super.getView();
    }

    public void closeEditor() {
	dateDateBox.hideDatePicker();
	super.closeEditor();
    }

    @Override
    protected Widget getMetaInfoLabel() {
	VerticalPanel metaInfosPanel = new VerticalPanel();

	String assessementType = getView().getAssessmentType();

	assessementType = assessementType != null ? assessementType : "";

	String assessementTypeLabel =
		getUiMessage("Assessment.type") + ": " + assessementType;

	String metaInfoLabelStr1 = assessementTypeLabel;

	Label label1 = new Label(metaInfoLabelStr1);
	label1.setStylePrimaryName("Osyl-UnitView-MetaInfo");
	metaInfosPanel.add(label1);
	return metaInfosPanel;
    }

    protected Label getNameLabel() {
	return new HTML(getUiMessage("Assessment.name")
		+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
    }

    protected String getNameTooltip() {
	return getUiMessage("Assessment.name.tooltip");
    }

    @Override
    public Widget getInformationWidget() {
	return new HTML(OsylAbstractEditor.MANDATORY_FIELD_INDICATOR
		+ getUiMessage("Global.fields.mandatory"));
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */
    public String getWeight() {
	return weightTextBox.getText();
    }

    public String getLocation() {
	return localisationListBox.getItemText(localisationListBox
		.getSelectedIndex());
    }

    public String getMode() {
	return modeListBox.getItemText(modeListBox.getSelectedIndex());
    }

    // public String getResult() {
    // return livrableTextBox.getText();
    // }

    // public String getScope() {
    // return scopeListBox.getItemText(scopeListBox.getSelectedIndex());
    // }

    public Date getDate() {
	return dateDateBox.getValue();
    }

    public String getSubmitionType() {
	return subTypeListBox.getItemText(subTypeListBox.getSelectedIndex());
    }

    public String getType() {
	return typeListBox.getItemText(typeListBox.getSelectedIndex());
    }

    private void selectItemListBox(ListBox lb, String text) {
	int selectedIndex = 0;
	for (int i = 0; i < lb.getItemCount(); i++) {
	    if (lb.getItemText(i).equals(text)) {
		selectedIndex = i;
		break;
	    }
	}
	lb.setSelectedIndex(selectedIndex);
    }

    private Widget getAdditionalInfos() {
	VerticalPanel metaInfosPanel = new VerticalPanel();

	String location = getView().getLocation();
	String workMode = getView().getMode();
	String submissionMode = getView().getSubmitionType();

	workMode =
		(workMode != null && !workMode.trim().equals("")) ? workMode
			+ " / " : "";
	location = location != null ? location : "";

	submissionMode = submissionMode != null ? submissionMode : "";

	String submissionModeLabel =
		!submissionMode.equals("") ? getView().getCoMessage(
			"Assessment.Subtype")
			+ " : " + submissionMode : "";

	Label label1 = new Label(workMode + location);
	Label label2 = new Label(submissionModeLabel);

	label1.setStylePrimaryName("Osyl-AssessmentView-AdditionalInfos");
	label2.setStylePrimaryName("Osyl-AssessmentView-AdditionalInfos");
	metaInfosPanel.add(label1);
	metaInfosPanel.add(label2);
	return metaInfosPanel;
    }

}
