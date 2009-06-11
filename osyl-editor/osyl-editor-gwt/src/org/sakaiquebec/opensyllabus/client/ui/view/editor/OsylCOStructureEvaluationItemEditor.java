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

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylLabelEditClickListener;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureEvaluationItemLabelView;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusWidget;
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
public class OsylCOStructureEvaluationItemEditor extends OsylAbstractEditor {

    // Our main panel which will display the viewer
    private VerticalPanel mainPanel;

    // Our editor
    private VerticalPanel editorPanel;
    private TextBox nameEditor;

    // Our viewer
    private HTML viewer;

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

    private boolean isInCoUnitList;

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
	    boolean isInCoUnitList) {
	super(parent);
	this.isInCoUnitList = isInCoUnitList;
	initMainPanel();
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewer();
	initWidget(getMainPanel());
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */
    private VerticalPanel getMainPanel() {
	return mainPanel;
    }

    private void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
    }

    /**
     * Creates and set the low-level editor (TextBox).
     */
    private void initEditor() {
	editorPanel = new VerticalPanel();
	editorPanel.setWidth("98%");

	Label nameLabel = new Label(getUiMessage("Evaluation.name"));

	editorPanel.add(nameLabel);

	nameEditor = new TextBox();
	nameEditor.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	nameEditor.setWidth("100%");
	nameEditor.setTitle(getUiMessage("Evaluation.name.tooltip"));
	editorPanel.add(nameEditor);
    }

    /**
     * Creates and set the low-level viewer (HTML panel).
     */
    private void initViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-LabelEditor-View");
	setViewer(htmlViewer);
    }

    private void setViewer(HTML html) {
	this.viewer = html;
    }

    private HTML getViewer() {
	return viewer;
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public void setText(String text) {
	if (isInEditionMode()) {
	    nameEditor.setText(text);
	} else {
	    viewer.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return nameEditor.getText();
	} else {
	    return viewer.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    nameEditor.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editorPanel;
    }

    public boolean prepareForSave() {
	return true;
    }

    public void enterEdit() {

	createEditBox();

	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	// We get the text to edit from the model
	setText(getView().getTextFromModel());
	// And put the cursor at the end
	nameEditor.setCursorPos(getText().length());
	// And we give the focus to the editor
	nameEditor.setFocus(true);

    } // enterEdit

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
			.equals("")) ? "\t(" + getView().getRating() + ")" : "";

	setText(getView().getTextFromModel() + rating);
	// If we are in read-only mode, we return now to not add buttons and
	// listeners enabling edition or deletion:
	if (isReadOnly()) {
	    return;
	}

	// We only create an edit button (as delete is not allowed) and add it:
	String title = getView().getUiMessage("edit");
	ClickListener listener = new OsylLabelEditClickListener(getView());
	AbstractImagePrototype imgEditButton = getOsylImageBundle().edit();
	ImageAndTextButton pbEdit =
		createButton(imgEditButton, title, listener);
	getView().getButtonPanel().clear();
	getView().getButtonPanel().add(pbEdit);

	if (!isInCoUnitList) {
	    if (!isReadOnly()) {
		getMainPanel().add(getMetaInfoLabel());
	    }
	} else {
	    getView().getButtonPanel().add(createButtonDelete());
	}

    } // enterView

    @Override
    public Widget getConfigurationWidget() {
	return null;
    }

    @Override
    public Widget getBrowserWidget() {
	return null;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    @Override
    public void maximizeEditor() {
	getEditorPopup().setHeight(getOriginalEditorPopupHeight() + "px");
	OsylEditorEntryPoint.centerObject(getEditorPopup());
    }

    @Override
    public void normalizeEditorWindowState() {
	// do nothing as editor height has not been changed
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(nameEditor);
	return focusWidgetList;
    }

    protected ClickListener getCancelButtonClickListener() {
	return new ClickListener() {

	    public void onClick(Widget sender) {
		getView().getMainPanel().removeStyleDependentName("Hover");
		getView().getButtonPanel().setVisible(false);
	    }
	};
    }

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
	ponderationPanel.add(weightTextBox);

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
		getUiMessage("Evaluation.type") + ": " + assessementType + " | ";
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

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    private void setSelectedTypeIndex(int index) {
	this.selectedTypeIndex = index;
    }

    private int getSelectedTypeIndex() {
	return selectedTypeIndex;
    }

    protected Button createButtonDelete() {
	AbstractImagePrototype imgDeleteButton = getOsylImageBundle().delete();
	String title = getUiMessage("delete");
	ClickListener listener =
		new MyDeletePushButtonListener((COContentUnit) getView()
			.getModel());
	return createButton(imgDeleteButton, title, listener);

    }

    /**
     * Class to manage click on the delete button
     */
    public class MyDeletePushButtonListener implements ClickListener {

	// Model variables (we use either one or the other). We could also use
	// a generic variable and cast it when needed...
	private COContentUnit coContentUnit;

	public MyDeletePushButtonListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	/**
	 * @see ClickListener#onClick(Widget)
	 */
	public void onClick(Widget sender) {
	    try {
		// Here, we create a dialog box to confirm delete
		OsylOkCancelDialog osylOkCancelDialog =
			new OsylOkCancelDialog(
				getView().getUiMessage(
					"OsylOkCancelDialog_Delete_Title"),
				getUiMessage("OsylOkCancelDialog_Delete_Content"));
		osylOkCancelDialog
			.addOkButtonCLickListener(new MyOkCancelDialogListener(
				this.coContentUnit));
		osylOkCancelDialog
			.addCancelButtonClickListener(getCancelButtonClickListener());
		osylOkCancelDialog.show();
		osylOkCancelDialog.centerAndFocus();
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
    }

    // The click listener that perform delete action if confirm button pushed
    public class MyOkCancelDialogListener implements ClickListener {

	private COContentUnit coContentUnit;

	public MyOkCancelDialogListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	public void onClick(Widget sender) {
	    try {
		String title = coContentUnit.getLabel();
		coContentUnit.remove();
		OsylUnobtrusiveAlert info =
			new OsylUnobtrusiveAlert(getView().getUiMessages()
				.getMessage("RemovedContentUnit", title));
		OsylEditorEntryPoint.showWidgetOnTop(info);
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
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
}
