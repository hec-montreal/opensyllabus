/*******************************************************************************
 * $Id: $
 * *****************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gwt.mosaic.core.client.DOM;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler.UploadFileEvent;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pop-up window to create or edit a citation. The citation is saved/updated in
 * the resources of Sakai.
 * 
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina
 *         Bauer-Oppinger</a>
 * @version $Id: $
 */
public class OsylCitationForm extends WindowPanel implements
	OsylViewControllable {

    private static final int FORM_WIDTH = 450;
    // original height of panel according to citation type "book"
    private static final int FORM_HEIGHT = 280;

    /**
     * Information about citation which is updated
     */
    private OsylCitationItem citation;

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

    // UI components
    private VerticalPanel mainPanel;

    private OsylController osylController;

    private ImageAndTextButton cancelButton;

    private ImageAndTextButton saveButton;

    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    private List<UploadFileEventHandler> citationEvtHandlerList =
	    new ArrayList<UploadFileEventHandler>();

    private TextBox hiddenListName;
    private TextBox hiddenDisplayName;

    private ListBox citationType;

    // panels for input fields
    private VerticalPanel citationPanel;
    private TextArea citationField;

    private HorizontalPanel titlePanel;
    private TextBox titleField;

    private HorizontalPanel authorPanel;
    private TextBox authorField;
    private Label authorLabel; // this changes for certain types

    private HorizontalPanel isnPanel;
    private Label isnLabel; // this changes for certain types

    private HorizontalPanel yearPanel;

    private HorizontalPanel journalPanel;

    private HorizontalPanel datePanel;

    private HorizontalPanel volIssuePanel;

    private HorizontalPanel pagePanel;

    private HorizontalPanel doiPanel;

    // keys of the different citation types
    private List<String> citationTypeKeys;

    /**
     * Constructor to create a new citation.
     * 
     * @param osylController
     * @param currentDirectory
     */
    public OsylCitationForm(OsylController osylController,
	    String currentDirectory) {
	this(osylController, currentDirectory, null);
    }

    /**
     * Constructor to create or edit a new citation.
     * 
     * @param osylController
     * @param currentDirectory
     * @param citation to modify
     */
    public OsylCitationForm(OsylController osylController,
	    String currentDirectory, OsylCitationItem citationItem) {
	if (citationItem != null)
	    System.out.println("resource id : " + citationItem.getResourceId());
	// set some properties for WindowPanel
	setResizable(true); // but is not maximizable
	setAnimationEnabled(true);
	setCaptionAction(null);

	citation = citationItem;
	setController(osylController);
	uiMessages = osylController.getUiMessages();
	initCitationTypes();

	mainPanel = new VerticalPanel();
	mainPanel.setWidth(FORM_WIDTH + "px");

	// Create a FormPanel and point it at a service.
	final FormPanel form = new FormPanel();
	form.setWidget(mainPanel);

	if (!getController().isInHostedMode()) {
	    String url = GWT.getModuleBaseURL();
	    // We use the URL from the beginning to the first slash after
	    // the protocol part (hence the 8 skipped chars: we suppose it
	    // can be either http:// or https://)
	    String cleanUrl = url.substring(0, url.indexOf("/", 8));
	    String action =
		    cleanUrl + "/sdata/ci/group/" + getSiteId() + "/"
			    + currentDirectory + "/";
	    if (citation != null) {
		// update and not creation of citation
		action = cleanUrl + "/sdata/ci" + citation.getResourceId();
		// create hidden field to define put(update) method
		mainPanel.add(createHiddenField("method", "put"));
		// create hidden field to define citation id
		mainPanel.add(createHiddenField("cid", citation.getId()));
	    }
	    form.setAction(action);
	}
	form.setMethod(FormPanel.METHOD_POST);

	final Label mainTitle;
	if (citation == null) {
	    mainTitle =
		    new Label(uiMessages
			    .getMessage("CitationForm.createCitation"));
	} else {
	    mainTitle =
		    new Label(uiMessages
			    .getMessage("CitationForm.editCitation"));
	}
	mainTitle.setStylePrimaryName("Osyl-CitationForm-title");
	mainPanel.add(mainTitle);

	// Create hidden field to define the citation list name.
	hiddenListName =
		createHiddenField("listname", citation == null ? "" : citation
			.getResourceId());
	mainPanel.add(hiddenListName);

	// Create hidden field to define the display name of the citation.
	mainPanel.add(createHiddenField("cipkeys", "sakai:displayname"));
	hiddenDisplayName =
		createHiddenField("cipvalues", (citation == null || citation
			.getTitle() == null) ? "" : citation.getTitle());
	mainPanel.add(hiddenDisplayName);

	// Create a listbox for the citation type.
	citationType = createListBox();
	citationType.setName("cipvalues");
	initTypeListBox();
	citationType.addChangeListener(new ChangeListener() {
	    public void onChange(Widget sender) {
		updateForm(getCurrentCitationType());
	    }
	});
	citationType.setStylePrimaryName("Osyl-CitationForm-typeField");
	citationType.setWidth((FORM_WIDTH / 3) + "px");
	// put listbox in horizontal panel to center it
	HorizontalPanel typePanel = new HorizontalPanel();
	typePanel.setWidth(FORM_WIDTH + "px");
	typePanel.add(citationType);
	typePanel.setCellHorizontalAlignment(citationType,
		HasHorizontalAlignment.ALIGN_CENTER);
	// set the type key for saving citation in sakai resources
	mainPanel.add(createHiddenField("cipkeys", "sakai:mediatype"));
	mainPanel.add(typePanel);

	// Create a textarea for the citation type others
	citationPanel = new VerticalPanel();
	citationPanel.setWidth("99%");
	citationPanel.add(createHiddenField("cipkeys", CitationSchema.TITLE));
	citationPanel.add(createNewLabel(osylController
		.getCoMessage("ResProxCitationView_citationLabel")
		+ " *:"));
	citationField =
		createTextArea((citation == null || citation.getTitle() == null) ? ""
			: citation.getTitle());
	citationPanel.add(citationField);
	mainPanel.add(citationPanel);

	// Create a textbox for the title.
	titleField =
		createTextBox((citation == null || citation.getTitle() == null || CitationSchema.TYPE_UNKNOWN
			.equals(citation.getProperty(CitationSchema.TYPE))) ? ""
			: citation.getTitle());
	titlePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_titleLabel")
			+ " *:", titleField, CitationSchema.TITLE);

	mainPanel.add(titlePanel);

	// Create a textbox for the author.
	authorField =
		createTextBox((citation == null || citation.getCreator() == null) ? ""
			: citation.getCreator());
	authorLabel =
		createNewLabel(osylController
			.getCoMessage("ResProxCitationView_authorLabel")
			+ " *:");
	authorPanel =
		createLabelTextboxElement(authorLabel, authorField,
			CitationSchema.CREATOR);
	mainPanel.add(authorPanel);

	// Create a textbox for the year.
	yearPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_yearLabel")
			+ ":", createTextBox((citation == null || citation
			.getProperty(CitationSchema.YEAR) == null) ? ""
			: citation.getProperty(CitationSchema.YEAR)),
			CitationSchema.YEAR);
	mainPanel.add(yearPanel);

	// Create a textbox for the journal.
	journalPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_journalLabel")
			+ ":", createTextBox((citation == null || citation
			.getProperty(CitationSchema.SOURCE_TITLE) == null) ? ""
			: citation.getProperty(CitationSchema.SOURCE_TITLE)),
			CitationSchema.SOURCE_TITLE);
	mainPanel.add(journalPanel);

	// Create a textbox for the date.
	datePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_dateLabel")
			+ ":", createTextBox((citation == null || citation
			.getProperty(CitationSchema.DATE) == null) ? ""
			: citation.getProperty(CitationSchema.DATE)),
			CitationSchema.DATE);
	mainPanel.add(datePanel);

	// Create two textboxes for the volume and issue.
	volIssuePanel =
		createDoubleLabelTextboxElement(
			osylController
				.getCoMessage("ResProxCitationView_volumeLabel")
				+ ":",
			createTextBox((citation == null || citation
				.getProperty(CitationSchema.VOLUME) == null) ? ""
				: citation.getProperty(CitationSchema.VOLUME)),
			CitationSchema.VOLUME,
			osylController
				.getCoMessage("ResProxCitationView_issueLabel")
				+ ":",
			createTextBox((citation == null || citation
				.getProperty(CitationSchema.ISSUE) == null) ? ""
				: citation.getProperty(CitationSchema.ISSUE)),
			CitationSchema.ISSUE);
	mainPanel.add(volIssuePanel);

	// Create a textbox for the pages.
	pagePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_pagesLabel")
			+ ":", createTextBox((citation == null || citation
			.getProperty(CitationSchema.PAGES) == null) ? ""
			: citation.getProperty(CitationSchema.PAGES)),
			CitationSchema.PAGES);
	// Create two textboxes for the start and end page.
	// pagePanel = createDoubleLabelTextboxElement(osylController.
	// getCoMessage("ResProxCitationView_startpageLabel") + ":",
	// createTextBox(), "startPage",
	// osylController.
	// getCoMessage("ResProxCitationView_endpageLabel") + ":",
	// createTextBox(), "endPage");
	mainPanel.add(pagePanel);

	// Create a textbox for ISSN or ISBN.
	isnLabel =
		createNewLabel(osylController
			.getCoMessage("ResProxCitationView_isbnLabel")
			+ ":");
	isnPanel =
		createLabelTextboxElement(isnLabel,
			createTextBox((citation == null || citation
				.getProperty(CitationSchema.ISN) == null) ? ""
				: citation.getProperty(CitationSchema.ISN)),
			CitationSchema.ISN);
	mainPanel.add(isnPanel);

	// Create a textbox for DOI.
	doiPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_doiLabel")
			+ ":", createTextBox((citation == null || citation
			.getProperty(CitationSchema.DOI) == null) ? ""
			: citation.getProperty(CitationSchema.DOI)),
			CitationSchema.DOI);
	mainPanel.add(doiPanel);

	// Add a 'save' button.
	AbstractImagePrototype imgSaveButton = osylImageBundle.save();
	saveButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgSaveButton, uiMessages.getMessage("save"));
	saveButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		form.submit();
	    }
	});
	saveButton.setStylePrimaryName("Osyl-FileUpload-genericButton");

	// Add a 'Cancel' button.
	AbstractImagePrototype imgCancelButton =
		osylImageBundle.action_cancel();
	cancelButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgCancelButton, uiMessages.getMessage("cancel"));
	cancelButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		cancel();
	    }
	});
	cancelButton.setStylePrimaryName("Osyl-FileUpload-genericButton");

	// Create a panel for centering both buttons
	HorizontalPanel buttonPanel = new HorizontalPanel();
	buttonPanel.setWidth(FORM_WIDTH + "px");
	buttonPanel.add(saveButton);
	buttonPanel.add(cancelButton);
	buttonPanel.setCellHorizontalAlignment(saveButton,
		HasHorizontalAlignment.ALIGN_RIGHT);
	buttonPanel.setCellHorizontalAlignment(cancelButton,
		HasHorizontalAlignment.ALIGN_LEFT);
	mainPanel.add(buttonPanel);

	// Add an event handler to the form.
	form.addFormHandler(new FormHandler() {
	    public void onSubmit(FormSubmitEvent event) {
		// This event is fired just before the form is submitted. We can
		// take this opportunity to perform validation.
		String title =
			getCurrentCitationType().equals(
				CitationSchema.TYPE_UNKNOWN) ? citationField
				.getText() : titleField.getText();
		String author = authorField.getText();
		// Title is always required, author is required when type is
		// book
		if (title.length() == 0
			|| (getCurrentCitationType().equals(
				CitationSchema.TYPE_BOOK) && author.length() == 0)) {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(
				    false,
				    true,
				    uiMessages
					    .getMessage("CitationForm.fillRequiredFields"));
		    // get index of citation form to set z-index of alert window
		    int index = new Integer(DOM.getStyleAttribute(
		    		OsylCitationForm.this.getElement(), "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    event.setCancelled(true);
		} else {
		    prepareFormToSubmit();
		    cancelButton.setEnabled(false);
		    saveButton.setEnabled(false);
		}
	    }

	    /*
	     * When the form submission is successfully completed, this event is
	     * fired. SDATA returns an event of type JSON.
	     */
	    public void onSubmitComplete(FormSubmitCompleteEvent event) {
		if (getState(event.getResults())) {
		    hide();
		    OsylUnobtrusiveAlert alert =
			    new OsylUnobtrusiveAlert(uiMessages
				    .getMessage("fileUpload.resSaved"));
		    OsylEditorEntryPoint.showWidgetOnTop(alert);

		    // parse the handler list
		    for (Iterator<UploadFileEventHandler> it =
			    citationEvtHandlerList.iterator(); it.hasNext();) {
			it.next()
				.onUploadFile(
					new UploadFileEvent(getPath(event
						.getResults())));
		    }

		} else {
		    Window.alert(uiMessages
			    .getMessage("fileUpload.resNotSaved"));
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(
				    false,
				    true,
				    "Alert - Upload Error",
				    uiMessages
					    .getMessage("fileUpload.resNotSaved"));
		    // get index of citation form to set z-index of alert window
		    int index = new Integer(DOM.getStyleAttribute(
		    		OsylCitationForm.this.getElement(), "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    cancelButton.setEnabled(true);
		    saveButton.setEnabled(true);
		    return;
		}
	    } // onSubmitComplete
	}); // new FormHandler (inner class)

	setWidget(form);
	updateForm(getCurrentCitationType());
	setStylePrimaryName("Osyl-CitationForm-form");
    } // Constructor

    /**
     * Parse the JSON String returned after file upload
     * 
     * @param jsonString
     * @return a boolean of the success state
     */
    public boolean getState(String jsonString) {
	return (jsonString.contains("sakai:added\":\"true"));
    }

    /**
     * Gets the resource name
     * 
     * @param jsonString
     * @return The resource name
     */
    public String getPath(String jsonString) {
	String s = jsonString.substring(jsonString.indexOf("\"path\":\"") + 8);
	s = s.substring(0, s.indexOf("\""));
	return s;
    }

    /**
     * Called at cancel of form
     */
    private void cancel() {
	hide();
    }

    /**
     * @return the osylController
     */
    public OsylController getController() {
	return osylController;
    }

    /**
     * @param the osylController to set
     */
    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }

    /**
     * @param the handler to add to the event handler list
     */
    public void addEventHandler(UploadFileEventHandler handler) {
	this.citationEvtHandlerList.add(handler);
    }

    /**
     * @param the handler to remove from the event handler list
     */
    public void removeEventHandler(UploadFileEventHandler handler) {
	this.citationEvtHandlerList.remove(handler);
    }

    /**
     * @return the current type of the citation
     */
    private String getCurrentCitationType() {
	return citationType.getValue(citationType.getSelectedIndex());
    }

    /**
     * Creates a label with given text and sets
     * CSS style
     * 
     * @param text of label
     * @return the label
     */
    private Label createNewLabel(String text) {
	Label label = new Label(text);
	label.setStyleName("Osyl-ResProxView-Label");
	return label;
    }

    /**
     * Creates a textbox with given text as value
     * and sets CSS style and width
     * 
     * @param text to set as value
     * @return the textbox
     */
    private TextBox createTextBox(String text) {
	TextBox tb = new TextBox();
	tb.setStylePrimaryName("Osyl-UnitView-TextArea");
	tb.setWidth("99%");
	tb.setName("cipvalues");
	tb.setText(text);
	return tb;
    }

    /**
     * Creates a textarea with given text as value 
     * and sets CSS style, width, and height
     * 
     * @param text to set as value
     * @return the textarea
     */
    private TextArea createTextArea(String text) {
	TextArea ta = new TextArea();
	ta.setStylePrimaryName("Osyl-UnitView-TextArea");
	ta.setWidth("99%");
	ta.setHeight("110px");
	ta.setName("cipvalues");
	ta.setText(text);
	return ta;
    }

    /**
     * Creates a invisible textbox with given name as name
     * of textbox and given value as its value
     * 
     * @param name to set as name of textbox
     * @param value to set as value of textbox
     * @return the textbox
     */
    private TextBox createHiddenField(String name, String value) {
	TextBox tb = new TextBox();
	tb.setName(name);
	tb.setText(value);
	tb.setVisible(false);
	return tb;
    }

    /**
     * Creates an empty lixtbox and sets CSS style 
     * and width
     * 
     * @param text to set as value
     * @return the textarea
     */
    private ListBox createListBox() {
	ListBox lb = new ListBox();
	lb.setStylePrimaryName("Osyl-UnitView-TextArea");
	lb.setWidth("99%");
	return lb;
    }

    /**
     * Creates a horizontal panel with a label and a textbox, 
     * also creates a hidden textbox to define a key which is 
     * used by submit of the form
     * 
     * @param text to set for the label
     * @param textbox to include in panel
     * @param key which is the value of the hidden field
     * @return the horizontal panel with the elements
     */
    private HorizontalPanel createLabelTextboxElement(String text, TextBox tb,
	    String key) {
	Label lab = createNewLabel(text);
	return createLabelTextboxElement(lab, tb, key);
    }

    /**
     * Creates a horizontal panel with a label and a textbox, 
     * also creates a hidden textbox to define a key which is 
     * used by submit of the form
     * 
     * @param label to include in panel
     * @param textbox to include in panel
     * @param key which is the value of the hidden field
     * @return the horizontal panel with the elements
     */
    private HorizontalPanel createLabelTextboxElement(Label label, TextBox tb,
	    String key) {
	HorizontalPanel hp = new HorizontalPanel();
	hp.add(createHiddenField("cipkeys", key));
	hp.add(label);
	hp.add(tb);
	hp.setWidth("99%");
	hp.setCellWidth(label, "15%");
	hp.setCellWidth(tb, "84%");
	hp.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	return hp;
    }

    /**
     * Creates a horizontal panel with two labels and two textboxes
     * in the order label-textbox-label-textbox, also creates two 
     * hidden textboxes to define keys which are used by submit of 
     * the form
     * 
     * @param text to set for the first label
     * @param textbox matched to first label to include in panel
     * @param key which is the value of the hidden field for the first 
     * 			textbox
     * @param text to set for the second label
     * @param textbox matched to second label to include in panel
     * @param key which is the value of the hidden field for the second 
     * 			textbox
     * @return the horizontal panel with the elements
     */
    private HorizontalPanel createDoubleLabelTextboxElement(String label1,
	    TextBox tb1, String key1, String label2, TextBox tb2, String key2) {
	HorizontalPanel hp = new HorizontalPanel();
	Label lab1 = createNewLabel(label1);
	Label lab2 = createNewLabel(label2);
	hp.add(createHiddenField("cipkeys", key1));
	hp.add(lab1);
	hp.add(tb1);
	hp.add(createHiddenField("cipkeys", key2));
	hp.add(lab2);
	hp.add(tb2);
	hp.setWidth("98%");
	hp.setCellWidth(lab1, "15%");
	hp.setCellWidth(tb1, "34%");
	hp.setCellWidth(lab2, "15%");
	hp.setCellWidth(tb2, "34%");
	hp.setCellHorizontalAlignment(lab2, HasHorizontalAlignment.ALIGN_RIGHT);
	tb2.setStylePrimaryName("Osyl-CitationForm-textBoxRight");
	hp.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	return hp;
    }

    /**
     * Creates a list with the types of citations
     */
    private void initCitationTypes() {
	citationTypeKeys = new ArrayList<String>();
	citationTypeKeys.add(CitationSchema.TYPE_BOOK);
	citationTypeKeys.add(CitationSchema.TYPE_ARTICLE);
	citationTypeKeys.add(CitationSchema.TYPE_UNKNOWN);
    }

    /**
     * Initializes listbox for citation types
     */
    private void initTypeListBox() {
	for (int i = 0; i < citationTypeKeys.size(); i++) {
	    String key = citationTypeKeys.get(i);
	    String value =
		    osylController
			    .getCoMessage(CitationSchema.CITATION_TYPE_PREFIX
				    + key);
	    if (value != null && !value.startsWith("Missing")) {
		citationType.addItem(value, key);
		if (citation != null
			&& key
				.equals(citation
					.getProperty(CitationSchema.TYPE))) {
		    citationType.setSelectedIndex(i);
		}
	    }
	}
    }

    /**
     * Returns the site id, shows an alert window when
     * error occurs
     * 
     * @return the site id
     */
    private String getSiteId() {
	String id = osylController.getSiteId();
	if (id == null) {
	    final OsylAlertDialog alertBox =
		    new OsylAlertDialog(false, true, uiMessages
			    .getMessage("fileUpload.unableToGetSiteID"));
	    // get index of citation form to set z-index of alert window
	    int index = new Integer(DOM.getStyleAttribute(
	    		this.getElement(), "zIndex"));
	    alertBox.setZIndex(index + 1);
	    alertBox.center();
	    alertBox.show();
	}
	return id;
    }

    /**
     * Updates form according to the new type of 
     * citation, shows or hides fields for each type
     * and fits the height of the form
     * 
     * @param the new type of citation
     */
    private void updateForm(String newType) {
	if (newType.equals(CitationSchema.TYPE_BOOK)) {
	    authorLabel.setText(osylController
		    .getCoMessage("ResProxCitationView_authorLabel")
		    + " *:");
	    isnLabel.setText(osylController
		    .getCoMessage("ResProxCitationView_isbnLabel")
		    + ":");
	    titlePanel.setVisible(true);
	    authorPanel.setVisible(true);
	    yearPanel.setVisible(true);
	    isnPanel.setVisible(true);
	    citationPanel.setVisible(false);
	    journalPanel.setVisible(false);
	    datePanel.setVisible(false);
	    volIssuePanel.setVisible(false);
	    pagePanel.setVisible(false);
	    doiPanel.setVisible(false);
	    setContentSize(getContentWidth(), FORM_HEIGHT);
	} else if (newType.equals(CitationSchema.TYPE_ARTICLE)) {
	    authorLabel.setText(osylController
		    .getCoMessage("ResProxCitationView_authorLabel")
		    + ":");
	    isnLabel.setText(osylController
		    .getCoMessage("ResProxCitationView_issnLabel")
		    + ":");
	    titlePanel.setVisible(true);
	    authorPanel.setVisible(true);
	    isnPanel.setVisible(true);
	    journalPanel.setVisible(true);
	    datePanel.setVisible(true);
	    volIssuePanel.setVisible(true);
	    pagePanel.setVisible(true);
	    doiPanel.setVisible(true);
	    yearPanel.setVisible(false);
	    citationPanel.setVisible(false);
	    int addHeight = journalPanel.getOffsetHeight() * 5;
	    setContentSize(getContentWidth(), FORM_HEIGHT + addHeight);
	} else {
	    citationPanel.setVisible(true);
	    titlePanel.setVisible(false);
	    authorPanel.setVisible(false);
	    yearPanel.setVisible(false);
	    isnPanel.setVisible(false);
	    journalPanel.setVisible(false);
	    datePanel.setVisible(false);
	    volIssuePanel.setVisible(false);
	    pagePanel.setVisible(false);
	    doiPanel.setVisible(false);
	    setContentSize(getContentWidth(), FORM_HEIGHT + 30);
	}
	layout();
    }

    /**
     * Called before submit of form, updates values of hidden
     * fields and removes invisible fields which belongs to 
     * another type of citation
     */
    private void prepareFormToSubmit() {
	if (citation == null) {
	    String listname =
		    getCurrentCitationType()
			    .equals(CitationSchema.TYPE_UNKNOWN) ? citationField
			    .getText()
			    : titleField.getText();
	    hiddenListName.setText(listname.substring(0, Math.min(40, listname
		    .length())));
	}
	hiddenDisplayName.setText(getCurrentCitationType().equals(
		CitationSchema.TYPE_UNKNOWN) ? citationField.getText()
		: titleField.getText());
	// remove invisible fields, so that they are not
	// submitted with the form
	if (getCurrentCitationType().equals(CitationSchema.TYPE_BOOK)) {
	    citationPanel.removeFromParent();
	    journalPanel.removeFromParent();
	    datePanel.removeFromParent();
	    volIssuePanel.removeFromParent();
	    pagePanel.removeFromParent();
	    doiPanel.removeFromParent();
	} else if (getCurrentCitationType().equals(CitationSchema.TYPE_ARTICLE)) {
	    citationPanel.removeFromParent();
	    yearPanel.removeFromParent();
	} else {
	    titlePanel.removeFromParent();
	    authorPanel.removeFromParent();
	    yearPanel.removeFromParent();
	    isnPanel.removeFromParent();
	    journalPanel.removeFromParent();
	    datePanel.removeFromParent();
	    volIssuePanel.removeFromParent();
	    pagePanel.removeFromParent();
	    doiPanel.removeFromParent();
	}
    }

    /**
     * Centers window with OsylEditorEntryPoint
     */
    @Override
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }
}
