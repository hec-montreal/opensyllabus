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
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureAssessmentItemLabelView;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
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
public class OsylCOStructureAssessmentItemEditor extends
	OsylCOStructureItemEditor {

    private static final DateTimeFormat dateTimeFormat =
	    DateTimeFormat.getFormat("yyyy-MM-dd");
    private static final DateBox.Format dateFormat =
	    new DateBox.DefaultFormat(dateTimeFormat);

    private TextBox weightTextBox;
    private ListBox localisationListBox;
    private ListBox modeListBox;
    // private TextBox livrableTextBox;
    // private ListBox scopeListBox;
    private DateBox startDateBox;
    private DateBox endDateBox;
    private ListBox subTypeListBox;
    private ListBox typeListBox;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     */
    public OsylCOStructureAssessmentItemEditor(OsylAbstractView parent) {
	this(parent, false);

    }

    public OsylCOStructureAssessmentItemEditor(OsylAbstractView parent,
	    boolean isDeletable) {
	super(parent, isDeletable);
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public boolean prepareForSave() {
	boolean ok = true;
	String messages = "";
	boolean errordate = false;
	// ISO format yyyy-mm-dd
	String isoRegex =
		"^(\\d{4})\\D?(0[1-9]|1[0-2])\\D?([12]\\d|0[1-9]|3[01])$";

	// required fields validations
	String weight = weightTextBox.getText();
	if (weight.trim().equals("")) {
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Evaluation.rating"))
			    + "\n";
	    ok = false;
	} else {
	    if (!weight.matches("^[0-9][0-9]")) {
		messages +=
			getUiMessage("Evaluation.field.weight.format") + "\n";
		ok = false;
	    }
	}

	String endDateString = endDateBox.getTextBox().getText();
	// if (endDateString.trim().equals("")) {
	// messages +=
	// getView().getUiMessage("Global.field.required",
	// getUiMessage("Evaluation.EndDate"))
	// + "\n\n";
	// ok = false;
	// } else {
	// if (!endDateString.matches(isoRegex)) {
	if (!endDateString.trim().equals("")
		&& !endDateString.matches(isoRegex)) {
	    messages +=
		    getView().getUiMessage("Global.field.date.unISO",
			    getUiMessage("Evaluation.EndDate"))
			    + "\n";
	    ok = false;
	    errordate = true;
	}
	// }

	if (!isDeletable()) {
	    if (typeListBox.getSelectedIndex() == 0) {
		messages +=
			getView().getUiMessage("Global.field.required",
				getUiMessage("Evaluation.type"))
				+ "\n";
		ok = false;
	    }
	    if (localisationListBox.getSelectedIndex() == 0) {
		messages +=
			getView().getUiMessage("Global.field.required",
				getUiMessage("Evaluation.location"))
				+ "\n";
		ok = false;
	    }
	    if (modeListBox.getSelectedIndex() == 0) {
		messages +=
			getView().getUiMessage("Global.field.required",
				getUiMessage("Evaluation.mode"))
				+ "\n";
		ok = false;
	    }

	    // date validation

	    String startDateString = startDateBox.getTextBox().getText();
	    if (!startDateString.trim().equals("")
		    && !startDateString.matches(isoRegex)) {
		messages +=
			getView().getUiMessage("Global.field.date.unISO",
				getUiMessage("Evaluation.StartDate"))
				+ "\n";
		ok = false;
		errordate = true;
	    }

	    if (!errordate && startDateString.compareTo(endDateString) > 0) {
		messages += getUiMessage("Evaluation.field.date.order") + "\n";
		ok = false;
	    }

	    if (!errordate && !endDateString.trim().equals("")) {
		String verifyAssignement = verifyAssignementTool();
		if (!verifyAssignement.equals("")) {
		    ok = false;
		    messages += verifyAssignement;
		}
	    }
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

    private String verifyAssignementTool() {
	return verifyAssignementTool(getView().getModel());
    }

    private String verifyAssignementTool(COElementAbstract model) {
	String message = "";
	if (model.isCOContentResourceProxy()) {
	    COContentResourceProxy contentResourceProxy =
		    (COContentResourceProxy) model;
	    if (contentResourceProxy.getResource().getType().equals(
		    COContentResourceType.ASSIGNMENT)) {
		COContentResource resource =
			(COContentResource) contentResourceProxy.getResource();
		Date assignementStartDate =
			OsylDateUtils.getDateFromXMLDate(resource
				.getProperty(COPropertiesType.DATE_START));
		Date assignementEndDate =
			OsylDateUtils.getDateFromXMLDate(resource
				.getProperty(COPropertiesType.DATE_END));
		if (endDateBox.getValue().before(assignementStartDate)) {
		    message +=
			    getView()
				    .getUiMessages()
				    .getMessage(
					    "Assignement.field.date.order.dateAfterDate",
					    getUiMessage("Evaluation.EndDate"),
					    getUiMessage("Assignement.date_start"))
				    + "\n";
		}
		if (assignementEndDate.before(endDateBox.getValue())) {
		    message +=
			    getView()
				    .getUiMessages()
				    .getMessage(
					    "Assignement.field.date.order.dateAfterDate",
					    getUiMessage("Assignement.date_end"),
					    getUiMessage("Evaluation.EndDate"))
				    + "\n";
		}

	    }
	} else {
	    for (Iterator<COElementAbstract> childsIterator =
		    model.getChildrens().iterator(); childsIterator.hasNext();) {
		message += verifyAssignementTool(childsIterator.next());
	    }
	}
	return message;
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
		(getView().getWeight() != null && !getView().getWeight()
			.equals("")) ? " (" + getView().getWeight() + "%)"
			: "";

	String date =
		(getView().getDateEnd() != null) ? (" - "+dateTimeFormat
			.format(getView().getDateEnd())) : "";

	setText(getView().getTextFromModel() + rating + date);
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
	weightTextBox.setText(getView().getWeight());
	weightTextBox.setWidth("40px");
	weightTextBox.setTitle(getUiMessage("Evaluation.rating.tooltip"));
	ponderationPanel.add(l1);
	HorizontalPanel weightPanel = new HorizontalPanel();
	weightPanel.add(weightTextBox);
	weightPanel.add(new Label("%"));
	ponderationPanel.add(weightPanel);

	VerticalPanel localisationPanel = new VerticalPanel();
	localisationPanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l2 = new Label(getUiMessage("Evaluation.location"));
	localisationListBox = new ListBox();
	localisationListBox.setName("Evaluation.location");
	localisationListBox
		.setTitle(getUiMessage("Evaluation.location.tooltip"));
	localisationListBox.addItem("");
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
	modeListBox.setName("Evaluation.mode");
	modeListBox.setTitle(getUiMessage("Evaluation.mode.tooltip"));
	modeListBox.addItem("");
	modeListBox.addItem(getView().getCoMessage("Evaluation.Mode.ind"));
	modeListBox.addItem(getView().getCoMessage("Evaluation.Mode.team"));
	selectItemListBox(modeListBox, getView().getMode());
	modePanel.add(l3);
	modePanel.add(modeListBox);

	// VerticalPanel livrablePanel = new VerticalPanel();
	// livrablePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	// Label l4 = new Label(getUiMessage("Evaluation.deliverable"));
	// livrableTextBox = new TextBox();
	// livrableTextBox
	// .setTitle(getUiMessage("Evaluation.deliverable.tooltip"));
	// livrableTextBox.setText(getView().getResult());
	// livrablePanel.add(l4);
	// livrablePanel.add(livrableTextBox);
	//
	// VerticalPanel scopePanel = new VerticalPanel();
	// scopePanel.setStylePrimaryName("Osyl-EditorPopup-LastOptionGroup");
	// Label l5 = new Label(getUiMessage("Evaluation.scope"));
	// scopeListBox = new ListBox();
	// scopeListBox.setName("Evaluation.scope");
	// scopeListBox.setTitle(getUiMessage("Evaluation.scope.tooltip"));
	// scopeListBox.addItem(getView().getCoMessage("Evaluation.Scope.Obl"));
	// scopeListBox.addItem(getView().getCoMessage("Evaluation.Scope.Fac"));
	// selectItemListBox(scopeListBox, getView().getScope());
	// scopePanel.add(l5);
	// scopePanel.add(scopeListBox);

	VerticalPanel startDatePanel = new VerticalPanel();
	startDatePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l6 = new Label(getUiMessage("Evaluation.StartDate"));
	startDateBox = new DateBox();
	startDateBox.setFormat(dateFormat);
	startDateBox.setTitle(getUiMessage("Evaluation.StartDate.tooltip"));
	startDateBox.setValue(getView().getDateStart());
	startDatePanel.add(l6);
	startDatePanel.add(startDateBox);

	VerticalPanel endDatePanel = new VerticalPanel();
	endDatePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l7 = new Label(getUiMessage("Evaluation.EndDate"));
	endDateBox = new DateBox();
	endDateBox.setFormat(dateFormat);
	endDateBox.setTitle(getUiMessage("Evaluation.EndDate.tooltip"));
	endDateBox.setValue(getView().getDateEnd());
	endDatePanel.add(l7);
	endDatePanel.add(endDateBox);

	VerticalPanel subTypePanel = new VerticalPanel();
	subTypePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l8 = new Label(getUiMessage("Evaluation.subtype"));
	subTypeListBox = new ListBox();
	subTypeListBox.setName("Evaluation.subtype");
	subTypeListBox.setTitle(getUiMessage("Evaluation.subtype.tooltip"));
	subTypeListBox.addItem("");
	subTypeListBox.addItem(getView().getCoMessage(
		"Evaluation.Subtype.paper"));
	subTypeListBox.addItem(getView().getCoMessage(
		"Evaluation.Subtype.elect"));
	subTypeListBox.addItem(getView()
		.getCoMessage("Evaluation.Subtype.oral"));
	selectItemListBox(subTypeListBox, getView().getSubmitionType());
	subTypePanel.add(l8);
	subTypePanel.add(subTypeListBox);

	VerticalPanel typePanel = new VerticalPanel();
	typePanel.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	Label l9 = new Label(getUiMessage("Evaluation.type"));
	typeListBox = new ListBox();
	typeListBox.setName("Evaluation.type");
	typeListBox.setTitle(getUiMessage("Evaluation.type.tooltip"));
	typeListBox.addItem("");
	List<String> evalTypeList =
		getView().getController().getOsylConfig().getEvalTypeList();
	if (evalTypeList != null) {
	    for (String evalTypeKey : evalTypeList) {
		typeListBox.addItem(getView().getCoMessage(evalTypeKey));
	    }
	}
	typeListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		setText(typeListBox.getItemText(typeListBox.getSelectedIndex()));
		typeListBox.setSelectedIndex(typeListBox.getSelectedIndex());
	    }

	});
	selectItemListBox(typeListBox, getView().getAssessmentType());
	typePanel.add(l9);
	typePanel.add(typeListBox);

	if (isDeletable()) {
	    ligne1.add(ponderationPanel);
	    ligne1.add(endDatePanel);
	    vp.add(ligne1);
	} else {
	    ligne1.add(typePanel);
	    ligne1.add(ponderationPanel);
	    ligne1.add(localisationPanel);
	    ligne1.add(modePanel);
	    // ligne1.add(livrablePanel);

	    ligne2.add(startDatePanel);
	    ligne2.add(endDatePanel);
	    ligne2.add(subTypePanel);
	    // ligne2.add(scopePanel);
	    
	    vp.add(ligne1);
	    vp.add(ligne2);
	}

	return new Widget[] { vp };
    }

    protected OsylCOStructureAssessmentItemLabelView getView() {
	return (OsylCOStructureAssessmentItemLabelView) super.getView();
    }

    public void closeEditor() {
	startDateBox.hideDatePicker();
	endDateBox.hideDatePicker();
	super.closeEditor();
    }

    @Override
    protected Widget getMetaInfoLabel() {
	VerticalPanel metaInfosPanel = new VerticalPanel();

	String assessementType = getView().getAssessmentType();
	String weight = getView().getWeight();
	String location = getView().getLocation();
	String workMode = getView().getMode();
	// String deliverable = getView().getResult();
	String dateStart =
		getView().getDateStart() == null ? "" : dateTimeFormat
			.format(getView().getDateStart());
	String dateEnd =
		getView().getDateEnd() == null ? "" : dateTimeFormat
			.format(getView().getDateEnd());
	String submissionMode = getView().getSubmitionType();
	// String scope = getView().getScope();

	assessementType = assessementType != null ? assessementType : "";
	weight = weight != null ? weight : "";
	location = location != null ? location : "";
	workMode = workMode != null ? workMode : "";
	// deliverable = deliverable != null ? deliverable : "";
	dateStart = dateStart != null ? dateStart : "";
	dateEnd = dateEnd != null ? dateEnd : "";
	submissionMode = submissionMode != null ? submissionMode : "";
	// scope = scope != null ? scope : "";

	String assessementTypeLabel =
		getUiMessage("Evaluation.type") + ": " + assessementType
			+ " | ";
	String weightLabel =
		getUiMessage("Evaluation.rating") + ": " + weight + "% | ";
	String locationLabel =
		getUiMessage("Evaluation.location") + ": " + location + " | ";
	String workModeLabel =
		getUiMessage("Evaluation.mode") + ": " + workMode;
	// String deliverableLabel =
	// !deliverable.equals("") ? getUiMessage("Evaluation.deliverable")
	// + ": " + deliverable + " | "
	// : "";
	String startDateLabel =
		!dateStart.equals("") ? getUiMessage("Evaluation.StartDate")
			+ ": " + dateStart + " | " : "";
	String endDateLabel =
		!dateEnd.equals("") ? getUiMessage("Evaluation.EndDate") + ": "
			+ dateEnd + " | " : "";
	String submissionModeLabel =
		!submissionMode.equals("") ? getUiMessage("Evaluation.subtype")
			+ ": " + submissionMode : "";
	// String scopeLabel = getUiMessage("Evaluation.scope") + ": " + scope;

	String metaInfoLabelStr1 =
		assessementTypeLabel + weightLabel + locationLabel
			+ workModeLabel;
	// String metaInfoLabelStr2 =
	// deliverableLabel + startDateLabel + endDateLabel
	// + submissionModeLabel + scopeLabel;
	String metaInfoLabelStr2 =
		startDateLabel + endDateLabel + submissionModeLabel;

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
	oep.setStylePrimaryName("Osyl-ContactInfo-Panel");
	oep.setWidth("100%");
	return oep;
    }

    protected Widget getReadOnlyMetaInfoLabel() {

	String assessementType = getView().getAssessmentType();
	String location = getView().getLocation();
	String workMode = getView().getMode();
	// String deliverable = getView().getResult();
	String dateStart =
		getView().getDateStart() == null ? "" : dateTimeFormat
			.format(getView().getDateStart());
	String dateEnd =
		getView().getDateEnd() == null ? "" : dateTimeFormat
			.format(getView().getDateEnd());
	String submissionMode = getView().getSubmitionType();
	// String scope = getView().getScope();

	assessementType = assessementType != null ? assessementType : "";
	location = location != null ? location : "";
	workMode = workMode != null ? workMode : "";
	// deliverable = deliverable != null ? deliverable : "";
	dateStart = dateStart != null ? dateStart : "";
	dateEnd = dateEnd != null ? dateEnd : "";
	submissionMode = submissionMode != null ? submissionMode : "";
	// scope = scope != null ? scope : "";

	HTML evaluationTypeHTML = createNewViewer();
	HTML localisationHTML = createNewViewer();
	HTML workModeHTML = createNewViewer();
	// HTML deliverableHTML = createNewViewer();
	HTML startDateHTML = createNewViewer();
	HTML endDateHTML = createNewViewer();
	HTML submissionModeHTML = createNewViewer();
	// HTML scopeHTML = createNewViewer();

	// panels used to display information
	VerticalPanel viewerPanelEvaluationType;
	VerticalPanel viewerPanelLocalisation;
	VerticalPanel viewerPanelWorkMode;
	// VerticalPanel viewerPanelDeliverable;
	VerticalPanel viewerPanelStartDate;
	VerticalPanel viewerPanelEndDate;
	VerticalPanel viewerPanelSubmissionMode;
	// VerticalPanel viewerPanelScope;

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

	// if (!deliverable.equals("")) {
	// fieldNumber++;
	// flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
	// addNewLabel(getUiMessage("Evaluation.deliverable")));
	// // Value(editor)
	// viewerPanelDeliverable = addNewEditorPanel();
	// viewerPanelDeliverable.add(deliverableHTML);
	// fieldNumber++;
	// flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
	// viewerPanelDeliverable);
	// }

	if (!dateStart.equals("")) {
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
	if (!dateEnd.equals("")) {
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    addNewLabel(getUiMessage("Evaluation.EndDate")));
	    // Value(editor)
	    viewerPanelEndDate = addNewEditorPanel();
	    viewerPanelEndDate.add(endDateHTML);
	    fieldNumber++;
	    flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
		    viewerPanelEndDate);
	}

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
	// viewerPanelScope = addNewEditorPanel();
	// viewerPanelScope.add(scopeHTML);
	// fieldNumber++;
	// flexTable.setWidget(fieldNumber / 4, fieldNumber % 4,
	// viewerPanelScope);

	evaluationTypeHTML.setHTML(assessementType);
	localisationHTML.setHTML(location);
	workModeHTML.setHTML(workMode);
	// deliverableHTML.setHTML(deliverable);
	startDateHTML.setHTML(dateStart);
	endDateHTML.setHTML(dateEnd);
	submissionModeHTML.setHTML(submissionMode);
	// scopeHTML.setHTML(scope);

	return flexTable;
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

    public Date getOpenDate() {
	return startDateBox.getValue();
    }

    public Date getCloseDate() {
	return endDateBox.getValue();
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

    protected Label getNameLabel() {
	return new Label(getUiMessage("Evaluation.name"));
    }

    protected String getNameTooltip() {
	return getUiMessage("Evaluation.name.tooltip");
    }
}
