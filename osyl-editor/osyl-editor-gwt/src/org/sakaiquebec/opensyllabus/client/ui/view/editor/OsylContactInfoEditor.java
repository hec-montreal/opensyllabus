/******************************************************************************
 * $Id$
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylUdeMSwitch;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxContactInfoView;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Editor to be used within by {@link OsylResProxContactInfoView}. It is
 * different from other editors as it shows 8 different views and their
 * respective editor. Each contact-info shows these views:
 * <ul>
 * <li>Role</li>
 * <li>Last Name</li>
 * <li>First Name</li>
 * <li>Office</li>
 * <li>Phone</li>
 * <li>eMail</li>
 * <li>Availability</li>
 * <li>Comments</li>
 * </ul>
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class OsylContactInfoEditor extends OsylAbstractResProxEditor {

    // Our main panel which will display the description panel as well as the
    // link panel
    private VerticalPanel mainPanel;

    // Our edition widgets
    private FlexTable editor;
    private ListBox editorRole;
    private TextBox editorLastName;
    private TextBox editorFirstName;
    private TextBox editorOffice;
    private TextBox editorPhone;
    private TextBox editorEMail;
    private RichTextArea editorAvailability;
    private RichTextArea editorComments;

    // Our viewers
    private FlexTable viewer;
    private HTML viewerRole;
    private HTML viewerLastName;
    private HTML viewerFirstName;
    private HTML viewerOffice;
    private HTML viewerPhone;
    private HTML viewerEMail;
    private HTML viewerAvailability;
    private HTML viewerComments;

    // remember height of rich text elements for maximizing popup
    private int originalEditorRichTextHeight;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylContactInfoEditor(OsylAbstractView parent) {
	super(parent,false);
	initMainPanel();
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewer();
	initWidget(mainPanel);
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */

    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
	mainPanel.setWidth("98%");
    }

    private void setMainPanel(VerticalPanel p) {
	this.mainPanel = p;
    }

    private VerticalPanel getMainPanel() {
	return this.mainPanel;
    }

    /**
     * Creates the main layout which consists of: Label+TextBox for LastName,
     * FirstName, Office, Phone and E-Mail, and Label+TextArea for Availability
     * and Comments and Label+ComboBox for the Title.
     */
    private void initEditor() {

	// Panel used to display each information
	VerticalPanel editorPanelRole;
	VerticalPanel editorPanelLastName;
	VerticalPanel editorPanelFirstName;
	VerticalPanel editorPanelOffice;
	VerticalPanel editorPanelPhone;
	VerticalPanel editorPanelEMail;
	VerticalPanel editorPanelAvailability;
	VerticalPanel editorPanelComments;

	// creation of the differents editors
	setEditorRole(createRoleListBox());
	setEditorLastName(createNewTextBox());
	setEditorFirstName(createNewTextBox());
	setEditorOffice(createNewTextBox());
	setEditorPhone(createNewTextBox());
	setEditorEMail(createNewTextBox());
	RichTextArea rta = createNewRichTextArea();
	setEditorAvailability(rta);
	rta = createNewRichTextArea();
	setEditorComments(rta);

	final FlexTable flexTable = new FlexTable();
	flexTable.setWidth("500px");

	// General setting for the flextable
	flexTable.setStylePrimaryName("Osyl-ContactInfo");

	// Column size distribution
	flexTable.getFlexCellFormatter().setWidth(0, 0, "75px");
	flexTable.getFlexCellFormatter().setWidth(0, 1, "175px");
	flexTable.getFlexCellFormatter().setWidth(0, 2, "75px");
	flexTable.getFlexCellFormatter().setWidth(0, 3, "175px");

	// LastName
	// Label
	flexTable.setWidget(0, 0, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_LastNameLabel")));
	// Value(editor)
	editorPanelLastName = addNewEditorPanel();
	editorPanelLastName.add(editorLastName);
	flexTable.setWidget(0, 1, editorPanelLastName);

	// FirstName
	// Label
	flexTable.setWidget(0, 2, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_FirstNameLabel")));
	// Value(editor)
	editorPanelFirstName = addNewEditorPanel();
	editorPanelFirstName.add(editorFirstName);
	flexTable.setWidget(0, 3, editorPanelFirstName);

	// Title
	// Label
	flexTable.setWidget(1, 0, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_TitleLabel")));
	// Value(editor)
	editorPanelRole = addNewEditorPanel();
	editorPanelRole.add(editorRole);
	flexTable.setWidget(1, 1, editorPanelRole);

	// Office
	// Label
	flexTable.setWidget(1, 2, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_OfficeLabel")));
	// Value(editor)
	editorPanelOffice = addNewEditorPanel();
	editorPanelOffice.add(editorOffice);
	flexTable.setWidget(1, 3, editorPanelOffice);

	// Phone
	// Label
	flexTable.setWidget(2, 0, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_PhoneLabel")));
	// Value(editor)
	editorPanelPhone = addNewEditorPanel();
	editorPanelPhone.add(editorPhone);
	flexTable.setWidget(2, 1, editorPanelPhone);

	// EMail
	// Label
	flexTable.setWidget(2, 2, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_EMailLabel")));
	// Value(editor)
	editorPanelEMail = addNewEditorPanel();
	editorPanelEMail.add(editorEMail);
	flexTable.setWidget(2, 3, editorPanelEMail);

	// Availability
	// Label
	flexTable.setWidget(3, 0, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_AvailabilityLabel")));
	// Value(editor)
	editorPanelAvailability = addNewEditorPanel();
	editorPanelAvailability.add(editorAvailability);
	flexTable.setWidget(3, 1, editorPanelAvailability);
	flexTable.getFlexCellFormatter().setColSpan(3, 1, 3);
	editorPanelAvailability.setHeight("75px");
	editorAvailability.setHeight("75px");
	editorAvailability.setWidth("100%");

	// Comments
	// Label
	flexTable.setWidget(4, 0, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_CommentsLabel")));
	// Value(editor)
	editorPanelComments = addNewEditorPanel();
	editorPanelComments.add(editorComments);
	flexTable.setWidget(4, 1, editorPanelComments);
	flexTable.getFlexCellFormatter().setColSpan(4, 1, 3);
	editorPanelComments.setHeight("75px");
	editorComments.setHeight("75px");
	editorComments.setWidth("100%");

	setEditor(flexTable);

    } // initEditor

    /**
     * Creates the main layout which consists of: Label+TextBox for LastName,
     * FirstName, Office, Phone and E-Mail, and Label+TextArea for Availability
     * and Comments and Label+ComboBox for the Title.
     */
    private void initViewer() {

	// panels used to display information
	VerticalPanel viewerPanelRole;
	VerticalPanel viewerPanelLastName;
	VerticalPanel viewerPanelFirstName;
	VerticalPanel viewerPanelOffice;
	VerticalPanel viewerPanelPhone;
	VerticalPanel viewerPanelEMail;
	VerticalPanel viewerPanelAvailability;
	VerticalPanel viewerPanelComments;

	// creations of viewers
	setViewerRole(createNewViewer());
	setViewerLastName(createNewNameViewer());
	setViewerFirstName(createNewNameViewer());
	setViewerOffice(createNewViewer());
	setViewerPhone(createNewViewer());
	setViewerEMail(createNewViewer());
	setViewerAvailability(createNewViewer());
	setViewerComments(createNewViewer());

	final FlexTable flexTable = new FlexTable();

	// General setting for the flextable
	flexTable.setStylePrimaryName("Osyl-ContactInfo");

	// Column size distribution
	int row = 0;
	int column = 0;
	flexTable.getFlexCellFormatter().setWidth(row, column++, "13%");
	flexTable.getFlexCellFormatter().setWidth(row, column++, "37%");
	flexTable.getFlexCellFormatter().setWidth(row, column++, "13%");
	flexTable.getFlexCellFormatter().setWidth(row, column++, "37%");

	column = 0;
	// LastName
	// Label
	flexTable.setWidget(row, column++, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_LastNameLabel")));
	// Value(editor)
	viewerPanelLastName = addNewEditorPanel();
	viewerPanelLastName.add(viewerLastName);
	flexTable.setWidget(row, column++, viewerPanelLastName);

	// FirstName
	// Label
	flexTable.setWidget(row, column++, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_FirstNameLabel")));
	// Value(editor)
	viewerPanelFirstName = addNewEditorPanel();
	viewerPanelFirstName.add(viewerFirstName);
	flexTable.setWidget(row, column++, viewerPanelFirstName);

	// line change
	row++;
	column = 0;

	// Title
	// Label
	flexTable.setWidget(row, column++, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_TitleLabel")));
	// Value(editor)
	viewerPanelRole = addNewEditorPanel();
	viewerPanelRole.add(viewerRole);
	flexTable.setWidget(row, column++, viewerPanelRole);

	// Office
	// Label
	flexTable.setWidget(row, column++, addNewLabel(getView().getCoMessage(
		"ResProxContactInfoView_OfficeLabel")));
	// Value(editor)
	viewerPanelOffice = addNewEditorPanel();
	viewerPanelOffice.add(viewerOffice);
	flexTable.setWidget(row, column++, viewerPanelOffice);

	if (!((nullOrEmpty(getView().getPhone())) && nullOrEmpty(getView()
		.getEMail()))) {
	    // line change
	    row++;
	    column = 0;

	    if (!nullOrEmpty(getView().getPhone())) {
		// Phone
		// Label
		flexTable.setWidget(row, column++, addNewLabel(getView()
			.getCoMessage("ResProxContactInfoView_PhoneLabel")));
		// Value(editor)
		viewerPanelPhone = addNewEditorPanel();
		viewerPanelPhone.add(viewerPhone);
		flexTable.setWidget(row, column++, viewerPanelPhone);
	    }

	    if (!nullOrEmpty(getView().getEMail())) {
		// EMail
		// Label
		flexTable.setWidget(row, column++, addNewLabel(getView()
			.getCoMessage("ResProxContactInfoView_EMailLabel")));
		// Value(editor)
		viewerPanelEMail = addNewEditorPanel();
		viewerPanelEMail.add(viewerEMail);
		flexTable.setWidget(row, column++, viewerPanelEMail);
	    }
	}

	// Availability
	// Label
	if (!nullOrEmpty(getView().getAvailability())) {
	    // line change
	    row++;
	    column = 0;
	    flexTable.setWidget(row, column++, addNewLabel(getView()
		    .getCoMessage("ResProxContactInfoView_AvailabilityLabel")));
	    // Value(editor)
	    viewerPanelAvailability = addNewEditorPanel();
	    viewerPanelAvailability.add(viewerAvailability);
	    flexTable.setWidget(row, column, viewerPanelAvailability);
	    flexTable.getFlexCellFormatter().setColSpan(row, column++, 3);
	    viewerPanelAvailability.setHeight("100%");
	    viewerAvailability.setHeight("100%");
	}

	// Comments
	// Label
	if (!nullOrEmpty(getView().getComments())) {
	    // line change
	    row++;
	    column = 0;
	    flexTable.setWidget(row, column++, addNewLabel(getView()
		    .getCoMessage("ResProxContactInfoView_CommentsLabel")));
	    // Value(editor)
	    viewerPanelComments = addNewEditorPanel();
	    flexTable.setWidget(row, column, viewerPanelComments);
	    viewerPanelComments.add(viewerComments);
	    flexTable.getFlexCellFormatter().setColSpan(row, column++, 3);
	    viewerPanelComments.setHeight("100%");
	    viewerComments.setHeight("100%");
	}

	setViewer(flexTable);
    } // initViewer

    private boolean nullOrEmpty(String st) {
	if (st == null || st.trim().equals("") || st.equalsIgnoreCase("<P>&nbsp;</P>")
		|| st.equalsIgnoreCase("<BR>"))
	    return true;
	else
	    return false;
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

    private ListBox createRoleListBox() {
	ListBox lb = new ListBox();
	lb.setStylePrimaryName("Osyl-ContactInfo-ListBox");

	lb.addItem(getView()
		.getCoMessage("ResProxContactInfoView_PleaseChoose"));
	lb.addItem(getView().getCoMessage("ResProxContactInfoView_Professor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_AssistantProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_AdjunctProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_AffiliatedProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_AssociateProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_GuestProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_HonoraryProfessor"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_Full-timeFacultyLecturer"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_Part-timeFacultyLecturer"));
	lb.addItem(getView().getCoMessage(
		"ResProxContactInfoView_Full-timeLecturer"));
	lb.addItem(getView().getCoMessage("ResProxContactInfoView_Trainee"));
	lb.addItem(getView().getCoMessage("ResProxContactInfoView_Student"));
	lb.addItem(getView().getCoMessage("ResProxContactInfoView_Secretary"));
	// TODO : Should be moved to XML external configuration file
	if ( OsylUdeMSwitch.isUdeM() ) {
		lb.addItem(getView().getCoMessage(
			"ResProxContactInfoView_Professor_UdeM"));
		lb.addItem(getView().getCoMessage(
			"ResProxContactInfoView_Full-timeLecturer_UdeM"));
	}
	return lb;
    }

    private RichTextArea createNewRichTextArea() {
	RichTextArea rta = new RichTextArea();
	rta.setStylePrimaryName("Osyl-UnitView-TextArea");
	return rta;
    }

    private TextBox createNewTextBox() {
	TextBox tb = new TextBox();
	tb.setStylePrimaryName("Osyl-ContactInfoView-TextBox");
	return tb;
    }

    private HTML createNewViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-ResProxView-LabelValue");
	return htmlViewer;
    }

    private HTML createNewNameViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-ResProxView-NameValue");
	return htmlViewer;
    }

    /**
     * ===================== SETTERS + GETTERS =====================
     * =================== FOR VIEWERS + EDITORS ===================
     */
    public FlexTable getEditor() {
	return editor;
    }

    public void setEditor(FlexTable editor) {
	this.editor = editor;
    }

    public FlexTable getViewer() {
	return viewer;
    }

    public void setViewer(FlexTable viewer) {
	this.viewer = viewer;
    }

    private void setEditorRole(ListBox editorRole) {
	this.editorRole = editorRole;
    }

    private void setEditorLastName(TextBox editorLastName) {
	this.editorLastName = editorLastName;
    }

    private void setEditorFirstName(TextBox editorFirstName) {
	this.editorFirstName = editorFirstName;
    }

    private void setEditorOffice(TextBox editorOffice) {
	this.editorOffice = editorOffice;
    }

    private void setEditorPhone(TextBox editorPhone) {
	this.editorPhone = editorPhone;
    }

    private void setEditorEMail(TextBox editorEMail) {
	this.editorEMail = editorEMail;
    }

    private void setEditorAvailability(RichTextArea editorAvailability) {
	this.editorAvailability = editorAvailability;
    }

    private void setEditorComments(RichTextArea editorComments) {
	this.editorComments = editorComments;
    }

    private void setViewerRole(HTML viewerRole) {
	this.viewerRole = viewerRole;
    }

    private void setViewerLastName(HTML viewerLastName) {
	this.viewerLastName = viewerLastName;
    }

    private void setViewerFirstName(HTML viewerFirstName) {
	this.viewerFirstName = viewerFirstName;
    }

    private void setViewerOffice(HTML viewerOffice) {
	this.viewerOffice = viewerOffice;
    }

    private void setViewerPhone(HTML viewerPhone) {
	this.viewerPhone = viewerPhone;
    }

    private void setViewerEMail(HTML viewerEMail) {
	this.viewerEMail = viewerEMail;
    }

    private void setViewerAvailability(HTML viewerAvailability) {
	this.viewerAvailability = viewerAvailability;
    }

    private void setViewerComments(HTML viewerComments) {
	this.viewerComments = viewerComments;
    }

    /**
     * ===================== OVERRIDDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected OsylResProxContactInfoView getView() {
	return (OsylResProxContactInfoView) super.getView();
    }

    /**
     * The contactInfo editor displays two editable fields. For this reason,
     * setText(String) should not be used as it does not explicitly refer to one
     * of these fields. Use {@link OsylContactInfoEditor#setTextDesc(String)} or
     * {@link OsylContactInfoEditor#setTextLink(String)} instead.
     */
    public void setText(String text) {
	throw new IllegalStateException(
		"Do not use setText(String) for contactInfos.");
    }

    /**
     * The contactInfo editor displays two editable fields. For this reason,
     * getText() should not be used as it does not explicitly refer to one of
     * these fields. Use {@link OsylContactInfoEditor#getTextDesc()} or
     * {@link OsylContactInfoEditor#getTextLink()} instead.
     */
    public String getText() {
	throw new IllegalStateException(
		"Do not use getText() for contactInfos.");
    }

    /**
     * Enters edition mode by showing all the editors.
     */
    public void enterEdit() {

	// We keep track that we are now in edition-mode
	setInEditionMode(true);

	enterEditRole();
	enterEdit(editorLastName, getView().getLastName());
	enterEdit(editorFirstName, getView().getFirstName());
	enterEdit(editorOffice, getView().getOffice());
	enterEdit(editorPhone, getView().getPhone());
	enterEdit(editorEMail, getView().getEMail());
	enterEdit(editorAvailability, getView().getAvailability());
	enterEdit(editorComments, getView().getComments());

	// And we give the focus to the editor
	editorLastName.setFocus(true);

	createEditBox();

    } // enterEdit

    /**
     * Enters view mode by showing all the viewers.
     */
    public void enterView() {
	// We keep track that we are now in edition-mode
	setInEditionMode(false);
	getMainPanel().clear();
	initViewer();
	getMainPanel().add(getViewer());
	viewerRole.setHTML(getView().getRole());
	viewerLastName.setHTML(getView().getLastName());
	viewerFirstName.setHTML(getView().getFirstName());
	viewerOffice.setHTML(getView().getOffice());
	viewerPhone.setHTML(getView().getPhone());
	viewerEMail.setHTML("<a href=\"mailto:"+getView().getEMail()+"\">"+getView().getEMail()+"</a>");
	viewerAvailability.setHTML(getView().getAvailability());
	viewerComments.setHTML(getView().getComments());

	if (!isReadOnly()) {
	    addViewerStdButtons();
	    getMainPanel().add(getMetaInfoLabel());
	}
    }

    /**
     * Gives focus to the description editor.
     */
    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editorLastName.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editor;
    }

    public boolean prepareForSave() {
	String role = getTextRole();
	String lastName = getTextLastName();
	String firstName = getTextFirstName();
	if ("".equals(role)) {
	    OsylAlertDialog alert =
		    new OsylAlertDialog(getView().getUiMessage("Global.error"),
			    getView().getCoMessage(
				    "ResProxContactInfoView_RoleMandatory"));
	    alert.show();
	    return false;
	} else if ("".equals(lastName)) {
	    OsylAlertDialog alert =
		    new OsylAlertDialog(getView().getUiMessage("Global.error"),
			    getView().getCoMessage(
				    "ResProxContactInfoView_LastNameMandatory"));
	    alert.show();
	    return false;
	} else if ("".equals(firstName)) {
	    OsylAlertDialog alert =
		    new OsylAlertDialog(
			    getView().getUiMessage("Global.error"),
			    getView()
				    .getCoMessage(
					    "ResProxContactInfoView_FirstNameMandatory"));
	    alert.show();
	    return false;
	} else {
	    return true;
	}
    }

    @Override
    public Widget getConfigurationWidget() {
	return null;
    }

    @Override
    public Widget[] getAdditionalOptionWidgets() {
	return null;
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(editorRole);
	focusWidgetList.add(editorLastName);
	focusWidgetList.add(editorFirstName);
	focusWidgetList.add(editorOffice);
	focusWidgetList.add(editorPhone);
	focusWidgetList.add(editorEMail);
	focusWidgetList.add(editorAvailability);
	focusWidgetList.add(editorComments);
	return focusWidgetList;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    @Override
    public void maximizeEditor() {
	originalEditorRichTextHeight = editorAvailability.getOffsetHeight();
	super.maximizeEditor();
	int rtAdd =
		(getEditorPopup().getOffsetHeight() - getOriginalEditorPopupHeight()) / 2;
	editorAvailability.setHeight((originalEditorRichTextHeight + rtAdd)
		+ "px");
	editorComments.setHeight((originalEditorRichTextHeight + rtAdd) + "px");
    }

    @Override
    public void normalizeEditorWindowState() {
	super.normalizeEditorWindowState();
	editorAvailability.setHeight(originalEditorRichTextHeight + "px");
	editorComments.setHeight(originalEditorRichTextHeight + "px");
    }

    /*
     * ====================== ADDED METHODS ====================== Not in any
     * superclass. Most of these methods exist because we have several fields
     * instead of only one as in the superclass. They are all the equivalent of
     * an OsylAbstractEditor method but for one of the two fields.
     */

    public String getTextRole() {
	// We return the listBox value as this is what must be stored in the
	// model (and not the text value).
	return editorRole.getItemText(editorRole.getSelectedIndex());
    }

    private void setTextRole(String text) {
	if (isInEditionMode()) {
	    int selectedIndex = 0;
	    for (int i = 0; i < editorRole.getItemCount(); i++) {
		if (editorRole.getItemText(i).equals(text)) {
		    selectedIndex = i;
		    break;
		}
	    }
	    editorRole.setSelectedIndex(selectedIndex);
	    editorRole.setWidth("100%");
	} else {
	    if (null == text || text.length() == 0) {
		text =
			getView().getCoMessage(
				"ResProxContactInfoView_PleaseChoose");
	    }
	    viewerRole.setHTML(text);
	}
    }

    public String getTextLastName() {
	if (isInEditionMode()) {
	    return editorLastName.getText();
	} else {
	    return viewerLastName.getHTML();
	}
    }

    public String getTextFirstName() {
	if (isInEditionMode()) {
	    return editorFirstName.getText();
	} else {
	    return viewerFirstName.getHTML();
	}
    }

    public String getTextOffice() {
	if (isInEditionMode()) {
	    return editorOffice.getText();
	} else {
	    return viewerOffice.getHTML();
	}
    }

    public String getTextPhone() {
	if (isInEditionMode()) {
	    return editorPhone.getText();
	} else {
	    return viewerPhone.getHTML();
	}
    }

    public String getTextEMail() {
	if (isInEditionMode()) {
	    return editorEMail.getText();
	} else {
	    return viewerEMail.getHTML();
	}
    }

    public String getTextAvailability() {
	if (isInEditionMode()) {
	    return editorAvailability.getHTML();
	} else {
	    return viewerAvailability.getHTML();
	}
    }

    public String getTextComments() {
	if (isInEditionMode()) {
	    return editorComments.getHTML();
	} else {
	    return viewerComments.getHTML();
	}
    }

    /*
     * ====================== ENTER EDITION METHODS ======================
     */

    /**
     * Enters the role view in edition mode. WARNING: this method is not
     * supposed to be called independently from others enterEdit*(). They are to
     * be called sequentially in enterEdit(). They could be merged in a single
     * method but are kept separate for the sake of readability!
     */
    private void enterEditRole() {

	// We get the text to edit from the model
	setTextRole(getView().getRole());

    } // enterEditRole

    /**
     * Enters in edition mode for the specified panel using the specified editor
     * and text.
     */
    private void enterEdit(RichTextArea rta, String txt) {

	// We instruct the editor to use all the available space
	rta.setWidth("100%");

	// We get the text to edit from the model
	rta.setHTML(txt);

    } // enterEdit

    /**
     * Enters in edition mode for the specified panel using the specified editor
     * and text.
     */
    private void enterEdit(TextBox tb, String txt) {

	// We instruct the editor to use all the available space
	tb.setWidth("100%");
	// We get the text to edit from the model
	// rta.setHTML(txt);
	tb.setText(txt);

    } // enterEdit

}
