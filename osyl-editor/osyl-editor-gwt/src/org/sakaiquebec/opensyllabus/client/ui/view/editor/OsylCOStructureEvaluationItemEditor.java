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


import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylUdeMSwitch;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureEvaluationItemLabelView;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureEvaluationItemEditor extends
	OsylCOStructureItemEditor {

    // Our main panel which will display the viewer

    private TextBox weightTextBox;
    private ListBox localisationListBox;
    private ListBox modeListBox;
    private TextBox livrableTextBox;
    private ListBox porteeListBox;
    private TextBox startDateTextBox;
    private TextBox endDateListBox;
    private ListBox typeRemiseListBox;
    private ListBox typeListBox;
    private int selectedTypeIndex;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     */
    public OsylCOStructureEvaluationItemEditor(OsylAbstractView parent) {
	this(parent, false);

    }

    public OsylCOStructureEvaluationItemEditor(OsylAbstractView parent,
	    boolean isDeletable) {
	super(parent, isDeletable);
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public boolean prepareForSave() {
	return true;
    }

    public void enterView() {

	// We remove any previous widget
	getMainPanel().clear();
	// And we put the viewer instead
	getMainPanel().add(getViewer());
	// We keep track that we are now in view-mode
	setInEditionMode(false);
	// We get the text to display from the model
	setText(getView().getTextFromModel());

	String rating =
		(getView().getRating() != null && !getView().getRating()
			.equals("")) ? "\t(" + getView().getRating() + "%)" : "";

	setText(getView().getTextFromModel() + rating);
	// If we are in read-only mode, we return now to not add buttons and
	// listeners enabling edition or deletion:

	if (!isDeletable()) {
	    if (!isReadOnly()) {
		getMainPanel().add(getMetaInfoLabel());
	    } else {
		getMainPanel().add(getReadOnlyMetaInfoLabel());
	    }
	}
	if (!isReadOnly())
	    refreshButtonPanel();

    } // enterView

    @Override
    public Widget[] getOptionWidgets() {

	VerticalPanel vp = new VerticalPanel();
	vp.setStylePrimaryName("Osyl-EditorPopup-OptionGroup-Row");
	vp.setSpacing(7);
	HorizontalPanel ligne1 = new HorizontalPanel();
	HorizontalPanel ligne2 = new HorizontalPanel();

	VerticalPanel ponderationPanel = new VerticalPanel();
	ponderationPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l1 = new Label(getUiMessage("Evaluation.rating"));
	weightTextBox = new TextBox();
	weightTextBox.setText(getView().getRating());
	weightTextBox.setWidth("40px");
	weightTextBox.setTitle(getUiMessage("Evaluation.rating.tooltip"));
	ponderationPanel.add(l1);
	HorizontalPanel ratingPanel = new HorizontalPanel();
	ratingPanel.add(weightTextBox);
	Label percentageLabel= new Label("%");
	ratingPanel.add(percentageLabel);
	ponderationPanel.add(ratingPanel);

	VerticalPanel localisationPanel = new VerticalPanel();
	localisationPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l2 = new Label(getUiMessage("Evaluation.location"));
	localisationListBox = new ListBox();
	localisationListBox
		.setTitle(getUiMessage("Evaluation.location.tooltip"));
	localisationListBox.addItem(getView().getCoMessage(
		"Evaluation.Location.inclass"));
	localisationListBox.addItem(getView().getCoMessage(
		"Evaluation.Location.home"));
	selectItemListBox(localisationListBox, getView().getLocation());
	localisationPanel.add(l2);
	localisationPanel.add(localisationListBox);

	VerticalPanel modePanel = new VerticalPanel();
	modePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l3 = new Label(getUiMessage("Evaluation.mode"));
	modeListBox = new ListBox();
	modeListBox.setTitle(getUiMessage("Evaluation.mode.tooltip"));
	modeListBox.addItem(getView().getCoMessage("Evaluation.Mode.ind"));
	modeListBox.addItem(getView().getCoMessage("Evaluation.Mode.team"));
	selectItemListBox(modeListBox, getView().getMode());
	modePanel.add(l3);
	modePanel.add(modeListBox);

	VerticalPanel livrablePanel = new VerticalPanel();
	livrablePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	Label l4 = new Label(getUiMessage("Evaluation.deliverable"));
	livrableTextBox = new TextBox();
	livrableTextBox
		.setTitle(getUiMessage("Evaluation.deliverable.tooltip"));
	livrableTextBox.setText(getView().getResult());
	livrablePanel.add(l4);
	livrablePanel.add(livrableTextBox);

	VerticalPanel porteePanel = new VerticalPanel();
	porteePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	Label l5 = new Label(getUiMessage("Evaluation.scope"));
	porteeListBox = new ListBox();
	porteeListBox.setTitle(getUiMessage("Evaluation.scope.tooltip"));
	porteeListBox.addItem(getView().getCoMessage("Evaluation.Scope.Obl"));
	porteeListBox.addItem(getView().getCoMessage("Evaluation.Scope.Fac"));
	selectItemListBox(porteeListBox, getView().getScope());
	porteePanel.add(l5);
	porteePanel.add(porteeListBox);

	VerticalPanel startDatePanel = new VerticalPanel();
	startDatePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l6 = new Label(getUiMessage("Evaluation.StartDate"));
	startDateTextBox = new TextBox();
	startDateTextBox.setTitle(getUiMessage("Evaluation.StartDate.tooltip"));
	startDateTextBox.setText(getView().getOpenDate());
	startDatePanel.add(l6);
	startDatePanel.add(startDateTextBox);

	VerticalPanel endDatePanel = new VerticalPanel();
	endDatePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l7 = new Label(getUiMessage("Evaluation.EndDate"));
	endDateListBox = new TextBox();
	endDateListBox.setTitle(getUiMessage("Evaluation.EndDate.tooltip"));
	endDateListBox.setText(getView().getCloseDate());
	endDatePanel.add(l7);
	endDatePanel.add(endDateListBox);

	VerticalPanel typeRemisePanel = new VerticalPanel();
	typeRemisePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l8 = new Label(getUiMessage("Evaluation.subtype"));
	typeRemiseListBox = new ListBox();
	typeRemiseListBox.setTitle(getUiMessage("Evaluation.subtype.tooltip"));
	typeRemiseListBox.addItem("");
	typeRemiseListBox.addItem(getView().getCoMessage(
		"Evaluation.Subtype.paper"));
	typeRemiseListBox.addItem(getView().getCoMessage(
		"Evaluation.Subtype.elect"));
	typeRemiseListBox.addItem(getView().getCoMessage(
		"Evaluation.Subtype.oral"));
	selectItemListBox(typeRemiseListBox, getView().getSubmitionType());
	typeRemisePanel.add(l8);
	typeRemisePanel.add(typeRemiseListBox);

	VerticalPanel typePanel = new VerticalPanel();
	typePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l9 = new Label(getUiMessage("Evaluation.type"));
	typeListBox = new ListBox();
	typeListBox.setTitle(getUiMessage("Evaluation.type.tooltip"));
	typeListBox.addItem(getView()
		.getCoMessage("Evaluation.Type.case_study"));
	typeListBox.addItem(getView().getCoMessage(
		"Evaluation.Type.practice_assignement"));
	typeListBox.addItem(getView().getCoMessage(
		"Evaluation.Type.session_work"));
	typeListBox.addItem(getView()
		.getCoMessage("Evaluation.Type.intra_exam"));
	typeListBox.addItem(getView()
		.getCoMessage("Evaluation.Type.final_exam"));
	typeListBox.addItem(getView().getCoMessage("Evaluation.Type.quiz"));
	typeListBox.addItem(getView().getCoMessage("Evaluation.Type.homework"));
	typeListBox.addItem(getView().getCoMessage(
		"Evaluation.Type.participation"));
	// TODO : Should be moved to XML external configuration file
	if ( OsylUdeMSwitch.isUdeM() ) {
		typeListBox.addItem(getView().getCoMessage(
			"Evaluation.Type.multiplechoice"));
		typeListBox.addItem(getView().getCoMessage(
			"Evaluation.Type.shortwrittenanswer"));
		typeListBox.addItem(getView().getCoMessage(
			"Evaluation.Type.elaboratedwrittenanswer"));
	}
	typeListBox.addItem(getView().getCoMessage("Evaluation.Type.other"));
	typeListBox.addChangeListener(new ChangeListener() {

	    public void onChange(Widget sender) {
		if (typeListBox.getItemText(getSelectedTypeIndex()).equals(
			getText())
			|| getView().getCoMessage("evaluation").equals(
				getText())) {
		    setText(typeListBox.getItemText(typeListBox
			    .getSelectedIndex()));
		}
		setSelectedTypeIndex(typeListBox.getSelectedIndex());
	    }

	});
	selectItemListBox(typeListBox, getView().getType());
	setSelectedTypeIndex(typeListBox.getSelectedIndex());
	typePanel.add(l9);
	typePanel.add(typeListBox);

	ligne1.add(typePanel);
	ligne1.add(ponderationPanel);
	ligne1.add(localisationPanel);
	ligne1.add(modePanel);
	ligne1.add(livrablePanel);

	ligne2.add(startDatePanel);
	ligne2.add(endDatePanel);
	ligne2.add(typeRemisePanel);
	ligne2.add(porteePanel);

	ligne1.setWidth("100%");
	ligne2.setWidth("100%");
	vp.add(ligne1);
	vp.add(ligne2);

	return new Widget[] { vp };
    }

    protected OsylCOStructureEvaluationItemLabelView getView() {
	return (OsylCOStructureEvaluationItemLabelView) super.getView();
    }

    @Override
    protected Widget getMetaInfoLabel() {
	VerticalPanel metaInfosPanel = new VerticalPanel();

	String assessementType = getView().getType();
	String weight = getView().getRating();
	String location = getView().getLocation();
	String workMode = getView().getMode();
	String deliverable = getView().getResult();
	String startDate = getView().getOpenDate();
	String endDate = getView().getCloseDate();
	String submissionMode = getView().getSubmitionType();
	String scope = getView().getScope();

	assessementType = assessementType != null ? assessementType : "";
	weight = weight != null ? weight : "";
	location = location != null ? location : "";
	workMode = workMode != null ? workMode : "";
	deliverable = deliverable != null ? deliverable : "";
	startDate = startDate != null ? startDate : "";
	endDate = endDate != null ? endDate : "";
	submissionMode = submissionMode != null ? submissionMode : "";
	scope = scope != null ? scope : "";

	String assessementTypeLabel =
		getUiMessage("Evaluation.type") + ": " + assessementType
			+ " | ";
	String weightLabel =
		getUiMessage("Evaluation.rating") + ": " + weight + " | ";
	String locationLabel =
		getUiMessage("Evaluation.location") + ": " + location + " | ";
	String workModeLabel =
		getUiMessage("Evaluation.mode") + ": " + workMode;
	String deliverableLabel =
		!deliverable.equals("") ? getUiMessage("Evaluation.deliverable")
			+ ": " + deliverable + " | "
			: "";
	String startDateLabel =
		!startDate.equals("") ? getUiMessage("Evaluation.StartDate")
			+ ": " + startDate + " | " : "";
	String endDateLabel =
		getUiMessage("Evaluation.EndDate") + ": " + endDate + " | ";
	String submissionModeLabel =
		!submissionMode.equals("") ? getUiMessage("Evaluation.subtype")
			+ ": " + submissionMode + " | " : "";
	String scopeLabel = getUiMessage("Evaluation.scope") + ": " + scope;

	String metaInfoLabelStr1 =
		assessementTypeLabel + weightLabel + locationLabel
			+ workModeLabel;
	String metaInfoLabelStr2 =
		deliverableLabel + startDateLabel + endDateLabel
			+ submissionModeLabel + scopeLabel;
	;

	Label label1 = new Label(metaInfoLabelStr1);
	Label label2 = new Label(metaInfoLabelStr2);

	label1.setStylePrimaryName("Osyl-UnitView-MetaInfo");
	label2.setStylePrimaryName("Osyl-UnitView-MetaInfo");
	metaInfosPanel.add(label1);
	metaInfosPanel.add(label2);
	return metaInfosPanel;
    }

    private HTML createNewViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-ResProxView-LabelValue");
	return htmlViewer;
    }

    private Label addNewLabel(String text) {
	Label label = new Label(text);
	label.setStyleName("Osyl-ResProxView-Label");
	return label;

    }

    private VerticalPanel addNewEditorPanel() {
	VerticalPanel oep = new VerticalPanel();
	oep.setStylePrimaryName("Osyl-ContactInfoView-Panel");
	oep.setWidth("100%");
	return oep;
    }

    protected Widget getReadOnlyMetaInfoLabel() {

	String assessementType = getView().getType();
	String location = getView().getLocation();
	String workMode = getView().getMode();
	String deliverable = getView().getResult();
	String startDate = getView().getOpenDate();
	String endDate = getView().getCloseDate();
	String submissionMode = getView().getSubmitionType();
	String scope = getView().getScope();

	assessementType = assessementType != null ? assessementType : "";
	location = location != null ? location : "";
	workMode = workMode != null ? workMode : "";
	deliverable = deliverable != null ? deliverable : "";
	startDate = startDate != null ? startDate : "";
	endDate = endDate != null ? endDate : "";
	submissionMode = submissionMode != null ? submissionMode : "";
	scope = scope != null ? scope : "";

	HTML evaluationTypeHTML = createNewViewer();
	HTML localisationHTML = createNewViewer();
	HTML workModeHTML = createNewViewer();
	HTML deliverableHTML = createNewViewer();
	HTML startDateHTML = createNewViewer();
	HTML endDateHTML = createNewViewer();
	HTML submissionModeHTML = createNewViewer();
	HTML scopeHTML = createNewViewer();

	// panels used to display information
	VerticalPanel viewerPanelEvaluationType;
	VerticalPanel viewerPanelLocalisation;
	VerticalPanel viewerPanelWorkMode;
	VerticalPanel viewerPanelDeliverable;
	VerticalPanel viewerPanelStartDate;
	VerticalPanel viewerPanelEndDate;
	VerticalPanel viewerPanelSubmissionMode;
	VerticalPanel viewerPanelScope;

	final FlexTable flexTable = new FlexTable();

	// General setting for the flextable
	flexTable.setStylePrimaryName("Osyl-ContactInfo");

	// Column size distribution
	int fieldNumber = 0;
	flexTable.getFlexCellFormatter().setWidth(0, 0, "13%");
	flexTable.getFlexCellFormatter().setWidth(0, 1, "37%");
	flexTable.getFlexCellFormatter().setWidth(0, 2, "13%");
	flexTable.getFlexCellFormatter().setWidth(0, 3, "37%");

	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		addNewLabel(getUiMessage("Evaluation.type")));
	// Value(editor)
	viewerPanelEvaluationType = addNewEditorPanel();
	viewerPanelEvaluationType.add(evaluationTypeHTML);
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		viewerPanelEvaluationType);

	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		addNewLabel(getUiMessage("Evaluation.location")));
	// Value(editor)
	viewerPanelLocalisation = addNewEditorPanel();
	viewerPanelLocalisation.add(localisationHTML);
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		viewerPanelLocalisation);

	// line change
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		addNewLabel(getUiMessage("Evaluation.mode")));
	// Value(editor)
	viewerPanelWorkMode = addNewEditorPanel();
	viewerPanelWorkMode.add(workModeHTML);
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		viewerPanelWorkMode);

	if (!deliverable.equals("")) {
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    addNewLabel(getUiMessage("Evaluation.deliverable")));
	    // Value(editor)
	    viewerPanelDeliverable = addNewEditorPanel();
	    viewerPanelDeliverable.add(deliverableHTML);
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    viewerPanelDeliverable);
	}

	if (!startDate.equals("")) {
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    addNewLabel(getUiMessage("Evaluation.StartDate")));
	    // Value(editor)
	    viewerPanelStartDate = addNewEditorPanel();
	    viewerPanelStartDate.add(startDateHTML);
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    viewerPanelStartDate);
	}
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		addNewLabel(getUiMessage("Evaluation.EndDate")));
	// Value(editor)
	viewerPanelEndDate = addNewEditorPanel();
	viewerPanelEndDate.add(endDateHTML);
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		viewerPanelEndDate);

	if (!submissionMode.equals("")) {
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    addNewLabel(getUiMessage("Evaluation.subtype")));
	    // Value(editor)
	    viewerPanelSubmissionMode = addNewEditorPanel();
	    viewerPanelSubmissionMode.add(submissionModeHTML);
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    viewerPanelSubmissionMode);
	}

	// Label
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		addNewLabel(getUiMessage("Evaluation.scope")));
	// Value(editor)
	viewerPanelScope = addNewEditorPanel();
	viewerPanelScope.add(scopeHTML);
	fieldNumber++;
	flexTable.setWidget(fieldNumber / 4, fieldNumber % 4, viewerPanelScope);

	evaluationTypeHTML.setHTML(assessementType);
	localisationHTML.setHTML(location);
	workModeHTML.setHTML(workMode);
	deliverableHTML.setHTML(deliverable);
	startDateHTML.setHTML(startDate);
	endDateHTML.setHTML(endDate);
	submissionModeHTML.setHTML(submissionMode);
	scopeHTML.setHTML(scope);

	return flexTable;
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    private void setSelectedTypeIndex(int index) {
	this.selectedTypeIndex = index;
    }

    private int getSelectedTypeIndex() {
	return selectedTypeIndex;
    }

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

    public String getResult() {
	return livrableTextBox.getText();
    }

    public String getScope() {
	return porteeListBox.getItemText(porteeListBox.getSelectedIndex());
    }

    public String getOpenDate() {
	return startDateTextBox.getText();
    }

    public String getCloseDate() {
	return endDateListBox.getText();
    }

    public String getSubmitionType() {
	return typeRemiseListBox.getItemText(typeRemiseListBox
		.getSelectedIndex());
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

    protected Label getNameLabel() {
	return new Label(getUiMessage("Evaluation.name"));
    }

    protected String getNameTooltip() {
	return getUiMessage("Evaluation.name.tooltip");
    }
}
