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
import org.sakaiquebec.opensyllabus.client.helper.FormHelper;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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

    private Hidden hiddenListName;
    private Hidden hiddenDisplayName;

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

    final private HorizontalPanel yearPanel;

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
	    final String currentDirectory, final OsylCitationItem citationItem) {
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
		FormHelper.createHiddenField("listname", citation == null ? ""
			: citation.getResourceId());
	mainPanel.add(hiddenListName);

	// Create hidden field to define the display name of the citation.
	mainPanel.add(FormHelper.createHiddenField("cipkeys",
		"sakai:displayname"));
	hiddenDisplayName =
		FormHelper.createHiddenField("cipvalues",
			(citation == null || citation.getTitle() == null) ? ""
				: citation.getTitle());
	mainPanel.add(hiddenDisplayName);

	// Create a listbox for the citation type.
	citationType = FormHelper.createListBox("Osyl-UnitView-TextArea");
	citationType.setName("cipvalues");
	initTypeListBox();
	citationType.addChangeHandler(new ChangeHandler() {
	    public void onChange(ChangeEvent event) {
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
	mainPanel.add(FormHelper
		.createHiddenField("cipkeys", "sakai:mediatype"));
	mainPanel.add(typePanel);

	// Create a textarea for the citation type others
	citationPanel = new VerticalPanel();
	citationPanel.setWidth("99%");
	citationPanel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.TITLE));
	citationPanel.add(createNewLabel(osylController
		.getCoMessage("ResProxCitationView_citationLabel")
		+ " *:"));
	citationField =
		FormHelper.createTextArea((citation == null || citation
			.getTitle() == null) ? "" : citation.getTitle(),
			"Osyl-UnitView-TextArea");
	citationPanel.add(citationField);
	mainPanel.add(citationPanel);

	// Create a textbox for the title.
	titleField =
		FormHelper
			.createTextBox(
				(citation == null
					|| citation.getTitle() == null || CitationSchema.TYPE_UNKNOWN
					.equals(citation
						.getProperty(CitationSchema.TYPE))) ? ""
					: citation.getTitle(),
				"Osyl-UnitView-TextArea");
	titlePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_titleLabel")
			+ " *:", titleField, CitationSchema.TITLE);

	mainPanel.add(titlePanel);

	// Create a textbox for the author.
	authorField =
		FormHelper.createTextBox((citation == null || citation
			.getCreator() == null) ? "" : citation.getCreator(),
			"Osyl-UnitView-TextArea");
	authorLabel =
		createNewLabel(osylController
			.getCoMessage("ResProxCitationView_authorLabel")
			+ " *:");
	authorPanel =
		createLabelTextboxElement(authorLabel, authorField,
			CitationSchema.CREATOR);
	mainPanel.add(authorPanel);

	// Create a textbox for the year.
	final TextBox yearTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.YEAR) == null) ? ""
			: citation.getProperty(CitationSchema.YEAR),
			"Osyl-UnitView-TextArea");
	yearPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_yearLabel")
			+ ":", yearTextBox, CitationSchema.YEAR);
	mainPanel.add(yearPanel);

	// Create a textbox for the journal.
	final TextBox sourceTitle =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.SOURCE_TITLE) == null) ? ""
			: citation.getProperty(CitationSchema.SOURCE_TITLE),
			"Osyl-UnitView-TextArea");
	journalPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_journalLabel")
			+ ":", sourceTitle, CitationSchema.SOURCE_TITLE);
	mainPanel.add(journalPanel);

	// Create a textbox for the date.
	final TextBox dateTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.DATE) == null) ? ""
			: citation.getProperty(CitationSchema.DATE),
			"Osyl-UnitView-TextArea");
	datePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_dateLabel")
			+ ":", dateTextBox, CitationSchema.DATE);
	mainPanel.add(datePanel);

	// Create two textboxes for the volume and issue.
	final TextBox volumeTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.VOLUME) == null) ? ""
			: citation.getProperty(CitationSchema.VOLUME),
			"Osyl-UnitView-TextArea");
	final TextBox issueTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.ISSUE) == null) ? ""
			: citation.getProperty(CitationSchema.ISSUE),
			"Osyl-UnitView-TextArea");
	volIssuePanel =
		createDoubleLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_volumeLabel")
			+ ":", volumeTextBox, CitationSchema.VOLUME,
			osylController
				.getCoMessage("ResProxCitationView_issueLabel")
				+ ":", issueTextBox, CitationSchema.ISSUE);
	mainPanel.add(volIssuePanel);

	// Create a textbox for the pages.
	final TextBox pagesTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.PAGES) == null) ? ""
			: citation.getProperty(CitationSchema.PAGES),
			"Osyl-UnitView-TextArea");
	pagePanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_pagesLabel")
			+ ":", pagesTextBox, CitationSchema.PAGES);

	mainPanel.add(pagePanel);

	// Create a textbox for ISSN or ISBN.
	isnLabel =
		createNewLabel(osylController
			.getCoMessage("ResProxCitationView_isbnLabel")
			+ ":");
	final TextBox isnTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.ISN) == null) ? ""
			: citation.getProperty(CitationSchema.ISN),
			"Osyl-UnitView-TextArea");
	isnPanel =
		createLabelTextboxElement(isnLabel, isnTextBox,
			CitationSchema.ISN);
	mainPanel.add(isnPanel);

	// Create a textbox for DOI.
	final TextBox doiTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.DOI) == null) ? ""
			: citation.getProperty(CitationSchema.DOI),
			"Osyl-UnitView-TextArea");
	doiPanel =
		createLabelTextboxElement(osylController
			.getCoMessage("ResProxCitationView_doiLabel")
			+ ":", doiTextBox, CitationSchema.DOI);
	mainPanel.add(doiPanel);

	// Add a 'save' button.
	AbstractImagePrototype imgSaveButton = osylImageBundle.save();
	saveButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgSaveButton, uiMessages.getMessage("save"));

	saveButton.addClickHandler(new ClickHandler() {
	    
	    public void onClick(ClickEvent event) {

		final String title =
			getCurrentCitationType().equals(
				CitationSchema.TYPE_UNKNOWN) ? citationField
				.getText() : titleField.getText();

		// Data validation
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
		    int index =
			    new Integer(DOM.getStyleAttribute(
				    OsylCitationForm.this.getElement(),
				    "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    return;
		} else {
		    hiddenDisplayName.setValue(title);
		    cancelButton.setEnabled(false);
		    saveButton.setEnabled(false);
		}

		// create the data pojo
		OsylCitationItem citation;
		if (citationItem != null) {
		    citation = citationItem;
		} else {
		    citation = new OsylCitationItem();
		}

		citation.setProperty(CitationSchema.TYPE, citationType
			.getValue(citationType.getSelectedIndex()));
		citation.setProperty(CitationSchema.TITLE, title);
		citation.setProperty(CitationSchema.ISN, isnTextBox.getText());
		citation.setProperty(CitationSchema.CREATOR, authorField
			.getText());
		// citation.setProperty(CitationSchema.EDITOR, );
		citation.setProperty(CitationSchema.VOLUME, volumeTextBox
			.getText());
		citation.setProperty(CitationSchema.ISSUE, issueTextBox
			.getText());
		citation.setProperty(CitationSchema.PAGES, pagesTextBox
			.getText());
		// citation.setProperty(CitationSchema.PUBLISHER, );
		citation
			.setProperty(CitationSchema.YEAR, yearTextBox.getText());
		citation
			.setProperty(CitationSchema.DATE, dateTextBox.getText());
		citation.setProperty(CitationSchema.DOI, doiTextBox.getText());
		// citation.setProperty(CitationSchema.URL, );
		citation.setProperty(CitationSchema.SOURCE_TITLE, sourceTitle
			.getText());
		// citation.setResourceName(listname == null ? "" : listname);

		citation.setFileName(titleField.getText());

		OsylRemoteServiceLocator.getCitationRemoteService()
			.createOrUpdateCitation(currentDirectory, citation,
				new AsyncCallback<String>() {
				    public void onFailure(Throwable caught) {
					Window
						.alert(uiMessages
							.getMessage("fileUpload.resNotSaved"));
					final OsylAlertDialog alertBox =
						new OsylAlertDialog(
							false,
							true,
							"Alert - Citation Error",
							uiMessages
								.getMessage("fileUpload.resNotSaved"));
					// get index of citation form to set
					// z-index of alert window
					int index =
						new Integer(
							DOM
								.getStyleAttribute(
									OsylCitationForm.this
										.getElement(),
									"zIndex"));
					alertBox.setZIndex(index + 1);
					alertBox.center();
					alertBox.show();
					cancelButton.setEnabled(true);
					saveButton.setEnabled(true);
				    }

				    public void onSuccess(String result) {
					hide();
					OsylUnobtrusiveAlert alert =
						new OsylUnobtrusiveAlert(
							uiMessages
								.getMessage("fileUpload.resSaved"));
					OsylEditorEntryPoint
						.showWidgetOnTop(alert);
					// parse the handler list
					for (Iterator<UploadFileEventHandler> it =
						citationEvtHandlerList
							.iterator(); it
						.hasNext();) {
					    it
						    .next()
						    .onUploadFile(
							    new UploadFileEventHandler.UploadFileEvent(
								    result));
					}
				    }
				});
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
	cancelButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
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

	ScrollPanel sp = new ScrollPanel(form);
	setWidget(sp);
	updateForm(getCurrentCitationType());
	setStylePrimaryName("Osyl-CitationForm-form");
    } // Constructor

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
     * Creates a label with given text and sets CSS style
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
     * Creates a horizontal panel with a label and a textbox, also creates a
     * hidden textbox to define a key which is used by submit of the form
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
     * Creates a horizontal panel with a label and a textbox, also creates a
     * hidden textbox to define a key which is used by submit of the form
     * 
     * @param label to include in panel
     * @param textbox to include in panel
     * @param key which is the value of the hidden field
     * @return the horizontal panel with the elements
     */
    private HorizontalPanel createLabelTextboxElement(Label label, TextBox tb,
	    String key) {
	HorizontalPanel hp = new HorizontalPanel();
	hp.add(FormHelper.createHiddenField("cipkeys", key));
	hp.add(label);
	hp.add(tb);
	hp.setWidth("99%");
	hp.setCellWidth(label, "15%");
	hp.setCellWidth(tb, "84%");
	hp.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	return hp;
    }

    /**
     * Creates a horizontal panel with two labels and two textboxes in the order
     * label-textbox-label-textbox, also creates two hidden textboxes to define
     * keys which are used by submit of the form
     * 
     * @param text to set for the first label
     * @param textbox matched to first label to include in panel
     * @param key which is the value of the hidden field for the first textbox
     * @param text to set for the second label
     * @param textbox matched to second label to include in panel
     * @param key which is the value of the hidden field for the second textbox
     * @return the horizontal panel with the elements
     */
    private HorizontalPanel createDoubleLabelTextboxElement(String label1,
	    TextBox tb1, String key1, String label2, TextBox tb2, String key2) {
	HorizontalPanel hp = new HorizontalPanel();
	Label lab1 = createNewLabel(label1);
	Label lab2 = createNewLabel(label2);
	hp.add(FormHelper.createHiddenField("cipkeys", key1));
	hp.add(lab1);
	hp.add(tb1);
	hp.add(FormHelper.createHiddenField("cipkeys", key2));
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
     * Updates form according to the new type of citation, shows or hides fields
     * for each type.
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
	}
	layout();
    }

    /**
     * Centers window with OsylEditorEntryPoint
     */
    @Override
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }
}
