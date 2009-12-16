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
import java.util.TreeSet;

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxContactInfoView;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Editor to be used within by {@link OsylResProxContactInfoView}. It is
 * different from other editors as it shows 8 different views and their
 * respective editor. Each contact-info shows these views:
 * <ul>
 * <li>First Name</li>
 * <li>Last Name</li>
 * <li>Role</li>
 * <li>eMail</li>
 * <li>Phone</li>
 * <li>Office</li>
 * <li>Availability</li>
 * <li>Comments</li>
 * </ul>
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class OsylContactInfoEditor extends OsylAbstractResProxEditor {

    /**
     * ====================== CONSTRUCTORS ======================
     */
    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylContactInfoEditor(OsylAbstractView parent) {
	super(parent);
	this.setHasRequirement(false);
	this.setHasImportant(false);
	this.setHasHide(false);
	initMainPanel();
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewer();
	initWidget(mainPanel);
    }

    // Our main panel which will display the description panel as well as the
    // link panel
    private VerticalPanel mainPanel;

    /**
     * ===================== SETTERS + GETTERS =====================
     * =================== FOR VIEWERS + EDITORS ===================
     */

    // Edition widgets ------------------------------------------------

    private VerticalPanel editorContainer;

    public VerticalPanel getEditorContainer() {
	if (editorContainer == null) {
	    editorContainer = new VerticalPanel();
	    // TODO : to be controlled by CSS
	    // editorContainer.setWidth("500px");
	    editorContainer.setStyleName("Osyl-ContactInfo-Editor");
	}
	return editorContainer;
    }

    // remember height of rich text elements for maximizing popup
    private int originalEditorRichTextHeight;

    public void setOriginalEditorRichTextHeight(int originalEditorRichTextHeight) {
	this.originalEditorRichTextHeight = originalEditorRichTextHeight;
    }

    public int getOriginalEditorRichTextHeight() {
	return originalEditorRichTextHeight;
    }

    private TextBox editorFirstName;

    public TextBox getEditorFirstName() {
	if (editorFirstName == null) {
	    editorFirstName = new TextBox();
	    editorFirstName.setStylePrimaryName("Osyl-ContactInfo-TextBox");
	    editorFirstName.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_FirstNameLabel"));
	}
	return editorFirstName;
    }

    private HTML firstNameLabel;

    public HTML getFirstNameLabel() {
	if (firstNameLabel == null) {
	    firstNameLabel =
		    new HTML(getView().getCoMessage(
			    "ResProxContactInfoView_FirstNameLabel")
			    + OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	    firstNameLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    firstNameLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return firstNameLabel;
    }

    private TextBox editorLastName;

    public TextBox getEditorLastName() {
	if (editorLastName == null) {
	    editorLastName = new TextBox();
	    editorLastName.setStylePrimaryName("Osyl-ContactInfo-TextBox");
	    editorLastName.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_LastNameLabel"));
	}
	return editorLastName;
    }

    private HTML lastNameLabel;

    public HTML getLastNameLabel() {
	if (lastNameLabel == null) {
	    lastNameLabel =
		    new HTML(getView().getCoMessage(
			    "ResProxContactInfoView_LastNameLabel")
			    + OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	    lastNameLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    lastNameLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return lastNameLabel;
    }

    private ListBox editorRole;

    public ListBox getEditorRole() {
	if (editorRole == null) {
	    editorRole = createRoleListBox();
	    editorRole.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_TitleLabel"));
	}
	return editorRole;
    }

    private HTML roleLabel;

    public HTML getRoleLabel() {
	if (roleLabel == null) {
	    roleLabel =
		    new HTML(getView().getCoMessage(
			    "ResProxContactInfoView_TitleLabel"));
	    roleLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    roleLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return roleLabel;
    }

    private ListBox createRoleListBox() {
	ListBox lb = new ListBox();
	lb.setName("listBoxContactInfoRole");
	lb.setStylePrimaryName("Osyl-ContactInfo-ListBox");
	List<String> rolesList =
		getView().getController().getOsylConfig().getRolesList();
	TreeSet<String> sortedRoleSet =
		new TreeSet<String>(LocalizedStringComparator.getInstance());
	for (String roleKey : rolesList) {
	    sortedRoleSet.add(getView().getCoMessage(roleKey));
	}
	if (sortedRoleSet.size() > 0) {
	    lb.addItem("", "");
	}
	for (String role : sortedRoleSet) {
	    lb.addItem(role);
	}
	return lb;
    }

    private TextBox editorEMail;

    public TextBox getEditorEMail() {
	if (editorEMail == null) {
	    editorEMail = new TextBox();
	    editorEMail.setStylePrimaryName("Osyl-ContactInfo-TextBox");
	    editorEMail.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_EMailLabel"));
	}
	return editorEMail;
    }

    private Label eMailLabel;

    public Label getEMailLabel() {
	if (eMailLabel == null) {
	    eMailLabel =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_EMailLabel"));
	    eMailLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    eMailLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return eMailLabel;
    }

    private TextBox editorPhone;

    public TextBox getEditorPhone() {
	if (editorPhone == null) {
	    editorPhone = new TextBox();
	    editorPhone.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_PhoneLabel"));
	    editorPhone.setStylePrimaryName("Osyl-ContactInfo-TextBox");
	}
	return editorPhone;
    }

    private Label phoneLabel;

    public Label getPhoneLabel() {
	if (phoneLabel == null) {
	    phoneLabel =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_PhoneLabel"));
	    phoneLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    phoneLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return phoneLabel;
    }

    private TextBox editorOffice;

    public TextBox getEditorOffice() {
	if (editorOffice == null) {
	    editorOffice = new TextBox();
	    editorOffice.setStylePrimaryName("Osyl-ContactInfo-TextBox");
	    editorOffice.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_OfficeLabel"));
	}
	return editorOffice;
    }

    private Label officeLabel;

    public Label getOfficeLabel() {
	if (officeLabel == null) {
	    officeLabel =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_OfficeLabel"));
	    officeLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    officeLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return officeLabel;
    }

    private Label officeLabelViewer;

    public Label getOfficeLabelViewer() {
	if (officeLabelViewer == null) {
	    officeLabelViewer =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_OfficeLabel"));
	    officeLabelViewer.addStyleName("Osyl-ContactInfo-OfficeLabel");
	}
	return officeLabelViewer;
    }

    private RichTextArea editorAvailability;

    public RichTextArea getEditorAvailability() {
	if (editorAvailability == null) {
	    editorAvailability = new RichTextArea();
	    editorAvailability.setHeight("7em");
	    editorAvailability.setStylePrimaryName("Osyl-UnitView-TextArea");
	    editorAvailability.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_AvailabilityLabel"));
	}
	return editorAvailability;
    }

    private Label availabilityLabel;

    public Label getAvailabilityLabel() {
	if (availabilityLabel == null) {
	    availabilityLabel =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_AvailabilityLabel"));
	    availabilityLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    availabilityLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return availabilityLabel;
    }

    private Label availabilityLabelViewer;

    public Label getAvailabilityLabelViewer() {
	if (availabilityLabelViewer == null) {
	    availabilityLabelViewer =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_AvailabilityLabel"));
	    availabilityLabelViewer
		    .addStyleName("Osyl-ContactInfo-AvailabilityLabel");
	}
	return availabilityLabelViewer;
    }

    private RichTextArea editorComments;

    public RichTextArea getEditorComments() {
	if (editorComments == null) {
	    editorComments = new RichTextArea();
	    editorComments.setHeight("7em");
	    editorComments.setStylePrimaryName("Osyl-UnitView-TextArea");
	    editorComments.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_CommentsLabel"));
	}
	return editorComments;
    }

    private Label commentsLabel;

    public Label getCommentsLabel() {
	if (commentsLabel == null) {
	    commentsLabel =
		    new Label(getView().getCoMessage(
			    "ResProxContactInfoView_CommentsLabel"));
	    commentsLabel.setStylePrimaryName("Osyl-ResProxView-Label");
	    commentsLabel.addStyleName("Osyl-ContactInfo-Label");
	}
	return commentsLabel;
    }

    // Viewer widgets ------------------------------------------------

    private VerticalPanel viewerContainer;

    public VerticalPanel getViewerContainer() {
	if (viewerContainer == null) {
	    viewerContainer = new VerticalPanel();
	    // TODO : to be controlled by CSS
	    // viewerContainer.setWidth("500px");
	    viewerContainer.setStyleName("Osyl-ContactInfo-Viewer");
	}
	return viewerContainer;
    }

    private HTML viewerFirstName;

    public HTML getViewerFirstName() {
	if (viewerFirstName == null) {
	    viewerFirstName = new HTML();
	    viewerFirstName.setStylePrimaryName("Osyl-ContactInfo-FirstName");
	    viewerFirstName.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_FirstNameLabel"));
	}
	return viewerFirstName;
    }

    private HTML viewerLastName;

    public HTML getViewerLastName() {
	if (viewerLastName == null) {
	    viewerLastName = new HTML();
	    viewerLastName.setStylePrimaryName("Osyl-ContactInfo-LastName");
	    viewerLastName.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_LastNameLabel"));
	}
	return viewerLastName;
    }

    private HTML viewerRole;

    public HTML getViewerRole() {
	if (viewerRole == null) {
	    viewerRole = new HTML();
	    viewerRole.setStylePrimaryName("Osyl-ContactInfo-Role");
	    viewerRole.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_TitleLabel"));
	}
	return viewerRole;
    }

    private HTML viewerEMail;

    public HTML getViewerEMail() {
	if (viewerEMail == null) {
	    viewerEMail = new HTML();
	    viewerEMail.setStylePrimaryName("Osyl-ContactInfo-EMail");
	    viewerEMail.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_EMailLabel"));
	}
	return viewerEMail;
    }

    private HTML viewerPhone;

    public HTML getViewerPhone() {
	if (viewerPhone == null) {
	    viewerPhone = new HTML();
	    viewerPhone.setStylePrimaryName("Osyl-ContactInfo-Phone");
	    viewerPhone.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_PhoneLabel"));
	}
	return viewerPhone;
    }

    private HTML viewerOffice;

    public HTML getViewerOffice() {
	if (viewerOffice == null) {
	    viewerOffice = new HTML();
	    viewerOffice.setStylePrimaryName("Osyl-ContactInfo-Office");
	    viewerOffice.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_OfficeLabel"));
	}
	return viewerOffice;
    }

    private HTML viewerAvailability;

    public HTML getViewerAvailability() {
	if (viewerAvailability == null) {
	    viewerAvailability = new HTML();
	    viewerAvailability
		    .setStylePrimaryName("Osyl-ContactInfo-Availability");
	    viewerAvailability.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_AvailabilityLabel"));
	}
	return viewerAvailability;
    }

    private HTML viewerComments;

    public HTML getViewerComments() {
	if (viewerComments == null) {
	    viewerComments = new HTML();
	    viewerComments.setStylePrimaryName("Osyl-ContactInfo-Comments");
	    viewerComments.setTitle(getView().getCoMessage(
		    "ResProxContactInfoView_CommentsLabel"));
	}
	return viewerComments;
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

	// Panels used to edit each information

	SimplePanel firstNameLabelPanel = new SimplePanel();
	firstNameLabelPanel.add(getFirstNameLabel());
	SimplePanel editorPanelFirstName = new SimplePanel();
	editorPanelFirstName.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelFirstName.add(getEditorFirstName());
	SimplePanel lastNameLabelPanel = new SimplePanel();
	lastNameLabelPanel.add(getLastNameLabel());
	SimplePanel editorPanelLastName = new SimplePanel();
	editorPanelLastName.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelLastName.add(getEditorLastName());
	HorizontalPanel nameHzContainer = new HorizontalPanel();
	nameHzContainer.add(firstNameLabelPanel);
	nameHzContainer.add(editorPanelFirstName);
	nameHzContainer.add(lastNameLabelPanel);
	nameHzContainer.add(editorPanelLastName);
	getEditorContainer().add(nameHzContainer);

	// Osyl-ContactInfoView-
	SimplePanel roleLabelPanel = new SimplePanel();
	roleLabelPanel.add(getRoleLabel());
	SimplePanel editorPanelRole = new SimplePanel();
	editorPanelRole.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelRole.add(getEditorRole());
	HorizontalPanel roleHzContainer = new HorizontalPanel();
	roleHzContainer.add(roleLabelPanel);
	roleHzContainer.add(editorPanelRole);
	getEditorContainer().add(roleHzContainer);

	SimplePanel emailLabelPanel = new SimplePanel();
	emailLabelPanel.add(getEMailLabel());
	SimplePanel editorPanelEMail = new SimplePanel();
	editorPanelEMail.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelEMail.add(getEditorEMail());
	HorizontalPanel emailHzContainer = new HorizontalPanel();
	emailHzContainer.add(emailLabelPanel);
	emailHzContainer.add(editorPanelEMail);
	getEditorContainer().add(emailHzContainer);

	SimplePanel phoneLabelPanel = new SimplePanel();
	phoneLabelPanel.add(getPhoneLabel());
	SimplePanel editorPanelPhone = new SimplePanel();
	editorPanelPhone.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelPhone.add(getEditorPhone());
	HorizontalPanel phoneHzContainer = new HorizontalPanel();
	phoneHzContainer.add(phoneLabelPanel);
	phoneHzContainer.add(editorPanelPhone);
	getEditorContainer().add(phoneHzContainer);

	SimplePanel officeLabelPanel = new SimplePanel();
	officeLabelPanel.add(getOfficeLabel());
	SimplePanel editorPanelOffice = new SimplePanel();
	editorPanelOffice.add(getEditorOffice());
	editorPanelOffice.setStylePrimaryName("Osyl-ContactInfo-Panel");
	HorizontalPanel officeHzContainer = new HorizontalPanel();
	officeHzContainer.add(officeLabelPanel);
	officeHzContainer.add(editorPanelOffice);
	getEditorContainer().add(officeHzContainer);

	SimplePanel availabilityLabelPanel = new SimplePanel();
	availabilityLabelPanel.add(getAvailabilityLabel());
	getEditorContainer().add(availabilityLabelPanel);
	SimplePanel editorPanelAvailability = new SimplePanel();
	editorPanelAvailability.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelAvailability.add(getEditorAvailability());
	getEditorContainer().add(editorPanelAvailability);

	SimplePanel commentsLabelPanel = new SimplePanel();
	commentsLabelPanel.add(getCommentsLabel());
	getEditorContainer().add(commentsLabelPanel);
	SimplePanel editorPanelComments = new SimplePanel();
	editorPanelComments.setStylePrimaryName("Osyl-ContactInfo-Panel");
	editorPanelComments.add(getEditorComments());
	getEditorContainer().add(editorPanelComments);

    } // initEditor

    /**
     * Creates the main layout which consists of: Label+TextBox for LastName,
     * FirstName, Office, Phone and E-Mail, and Label+TextArea for Availability
     * and Comments and Label+ComboBox for the Title.
     */
    private void initViewer() {
	getViewerContainer().clear();
	// Panels used to display information
	if (nullOrEmpty(getView().getLastName())
		&& nullOrEmpty(getView().getFirstName())) {
	    HTML message =
		    new HTML(getView().getUiMessage(
			    "ContactInfoEditor.contact.undefined"));
	    getViewerContainer().add(message);

	} else {
	    SimplePanel viewerPanelFirstName = new SimplePanel();
	    viewerPanelFirstName.add(getViewerFirstName());
	    SimplePanel viewerPanelLastName = new SimplePanel();
	    viewerPanelLastName.add(getViewerLastName());
	    HorizontalPanel viewerNameHzContainer = new HorizontalPanel();
	    viewerNameHzContainer.add(viewerPanelFirstName);
	    viewerNameHzContainer.add(viewerPanelLastName);
	    getViewerContainer().add(viewerNameHzContainer);

	    SimplePanel viewerPanelRole = new SimplePanel();
	    viewerPanelRole.add(getViewerRole());
	    getViewerContainer().add(viewerPanelRole);

	    SimplePanel viewerPanelEMail = new SimplePanel();
	    viewerPanelEMail.add(getViewerEMail());
	    if (!nullOrEmpty(getView().getEMail())) {
		getViewerContainer().add(viewerPanelEMail);
	    }

	    SimplePanel viewerPanelPhone = new SimplePanel();
	    viewerPanelPhone.add(getViewerPhone());
	    if (!nullOrEmpty(getView().getPhone())) {
		getViewerContainer().add(viewerPanelPhone);
	    }

	    SimplePanel viewerOfficeLabelPanel = new SimplePanel();
	    viewerOfficeLabelPanel.add(getOfficeLabelViewer());
	    SimplePanel viewerPanelOffice = new SimplePanel();
	    viewerPanelOffice.add(getViewerOffice());
	    HorizontalPanel viewerOfficeHzContainer = new HorizontalPanel();
	    viewerOfficeHzContainer.add(viewerOfficeLabelPanel);
	    viewerOfficeHzContainer.add(viewerPanelOffice);
	    if (!nullOrEmpty(getView().getOffice())) {
		getViewerContainer().add(viewerOfficeHzContainer);
	    }

	    SimplePanel viewerAvailabilityLabelPanel = new SimplePanel();
	    viewerAvailabilityLabelPanel.add(getAvailabilityLabelViewer());
	    SimplePanel viewerPanelAvailability = new SimplePanel();
	    viewerPanelAvailability.add(getViewerAvailability());
	    HorizontalPanel viewerAvailabilityHzContainer =
		    new HorizontalPanel();
	    viewerAvailabilityHzContainer.add(viewerAvailabilityLabelPanel);
	    viewerAvailabilityHzContainer.add(viewerPanelAvailability);
	    if (!nullOrEmpty(getView().getAvailability())) {
		getViewerContainer().add(viewerAvailabilityHzContainer);
	    }

	    SimplePanel viewerPanelComments = new SimplePanel();
	    viewerPanelComments.add(getViewerComments());
	    if (!nullOrEmpty(getView().getComments())) {
		getViewerContainer().add(viewerPanelComments);
	    }
	}

    } // initViewer

    private boolean nullOrEmpty(String st) {
	if (st == null || st.trim().equals("")
		|| st.equalsIgnoreCase("<P>&nbsp;</P>")
		|| st.equalsIgnoreCase("<BR>"))
	    return true;
	else
	    return false;
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
	getMainPanel().add(getViewerContainer());
	getViewerRole().setHTML(getView().getRole());
	getViewerLastName().setHTML(getView().getLastName());
	getViewerFirstName().setHTML(getView().getFirstName());
	getViewerOffice().setHTML(getView().getOffice());
	getViewerPhone().setHTML(getView().getPhone());
	getViewerEMail().setHTML(
		"<a href=\"mailto:" + getView().getEMail() + "\">"
			+ getView().getEMail() + "</a>");
	getViewerAvailability().setHTML(getView().getAvailability());
	getViewerComments().setHTML(getView().getComments());

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
	return editorContainer;
    }

    public boolean prepareForSave() {
	String lastName = getTextLastName();
	String firstName = getTextFirstName();
	String messages = "";
	boolean error = false;
	if ("".equals(firstName.trim())) {
	    messages +=
		    getView().getCoMessage(
			    "ResProxContactInfoView_FirstNameMandatory")
			    + "\n";
	    error = true;
	}
	if ("".equals(lastName.trim())) {
	    messages +=
		    getView().getCoMessage(
			    "ResProxContactInfoView_LastNameMandatory")
			    + "\n";
	    error = true;
	}
	if (error) {
	    OsylAlertDialog alert =
		    new OsylAlertDialog(getView().getUiMessage("Global.error"),
			    messages);
	    alert.show();
	}
	return !error;
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
	return editorRole.getValue(editorRole.getSelectedIndex());
    }

    private void setTextRole(String text) {
	if (isInEditionMode()) {
	    int selectedIndex = 0;
	    for (int i = 0; i < editorRole.getItemCount(); i++) {
		if (editorRole.getValue(i).equals(text)) {
		    selectedIndex = i;
		    break;
		}
	    }
	    editorRole.setSelectedIndex(selectedIndex);
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

	// We instruct the editor to use almost all the available space. Do not
	// use
	// 100% as it makes the textarea left-end go beyond the popup limits.
	rta.setWidth("98%");

	// We get the text to edit from the model
	rta.setHTML(txt);

    } // enterEdit

    /**
     * Enters in edition mode for the specified panel using the specified editor
     * and text.
     */
    private void enterEdit(TextBox tb, String txt) {
	// We get the text to edit from the model
	tb.setText(txt);

    } // enterEdit

}
