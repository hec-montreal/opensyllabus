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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractResProxEditor;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.util.LinkValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
    private static final String RECOMMENDED_FIELD_INDICATOR =
	    "&nbsp;<sup>+</sup>";

    /**
     * Information about citation which is updated
     */
    private OsylCitationItem citation;

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;
    private OsylConfigMessages coMessages;

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
    private HTML authorLabel; // this changes for certain types

    private HorizontalPanel isnPanel;
    private Label isnLabel; // this changes for certain types

    private HorizontalPanel yearPanel;

    private HorizontalPanel journalPanel;

    private HorizontalPanel proceedingPanel;

    private HorizontalPanel volumePanel;

    private HorizontalPanel volIssuePanel;

    private HorizontalPanel pagePanel;

    private HorizontalPanel doiPanel;

    private HorizontalPanel publisherPanel;

    private HorizontalPanel publicationLocationPanel;

    private HorizontalPanel resourceTypePanel;

    private List<String> typeResourceList;

    private ListBox typeResourceListBox;

    private String typeResource;

    private VerticalPanel urlPanel;
    private TextBox urlTextBox;

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

	// set some properties for WindowPanel
	setResizable(true); // but is not maximizable
	setAnimationEnabled(true);
	setCaptionAction(null);

	citation = citationItem;
	setController(osylController);
	uiMessages = osylController.getUiMessages();
	coMessages = osylController.getCoMessages();
	initCitationTypes();
	this.typeResourceList =
		osylController.getOsylConfig().getResourceTypeList();

	if (citation.getProperty(CitationSchema.CITATION_RESOURCE_TYPE) != null) {
	    setTypeResource(citation
		    .getProperty(CitationSchema.CITATION_RESOURCE_TYPE));
	} else {
	    setTypeResource(osylController.getViewContext().getContextModel()
		    .getProperty(CitationSchema.CITATION_RESOURCE_TYPE));
	}

	mainPanel = new VerticalPanel();
	mainPanel.setWidth(FORM_WIDTH + "px");

	// Create a FormPanel and point it at a service.
	final FormPanel form = new FormPanel();
	form.setWidget(mainPanel);

	final Label mainTitle;
	if (citation == null) {
	    mainTitle =
		    new Label(
			    uiMessages
				    .getMessage("CitationForm.createCitation"));
	} else {
	    mainTitle =
		    new Label(
			    uiMessages.getMessage("CitationForm.editCitation"));
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

	mainPanel.add(new HTML(OsylAbstractEditor.MANDATORY_FIELD_INDICATOR
		+ osylController.getUiMessage("Global.fields.mandatory")));
	// mainPanel.add(new HTML(uiMessages
	// .getMessage("CitationForm.recommendedFields")));
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
	citationPanel.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	citationPanel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.TITLE));
	citationPanel.add(createNewLabel(osylController
		.getUiMessage("ResProxCitationView.citation.label")
		+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":"));
	citationField =
		FormHelper.createTextArea((citation == null || citation
			.getTitle() == null) ? "" : citation.getTitle(),
			"Osyl-CitationForm-textArea");
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
				"Osyl-CitationForm-textBox");
	titlePanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.title.label")
				+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR
				+ ":", titleField, CitationSchema.TITLE);

	mainPanel.add(titlePanel);

	// Create a textbox for the author.
	authorField =
		FormHelper.createTextBox((citation == null || citation
			.getCreator() == null) ? "" : citation.getCreator(),
			"Osyl-CitationForm-textBox");
	authorLabel =
		createNewLabel(osylController
			.getUiMessage("ResProxCitationView.author.label")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":");
	authorPanel =
		createLabelTextboxElement(authorLabel, authorField,
			CitationSchema.CREATOR);
	mainPanel.add(authorPanel);

	// Create a textbox for the year.
	final TextBox yearTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.YEAR) == null) ? ""
			: citation.getProperty(CitationSchema.YEAR),
			"Osyl-CitationForm-textBox");
	yearPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.year.label")
				+ ":", yearTextBox, CitationSchema.YEAR);
	mainPanel.add(yearPanel);

	// Create a textbox for the year.
	final TextBox publisherBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.PUBLISHER) == null) ? ""
			: citation.getProperty(CitationSchema.PUBLISHER),
			"Osyl-CitationForm-textBox");
	publisherPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.publisher.label")
				+ ":", publisherBox, CitationSchema.PUBLISHER);
	mainPanel.add(publisherPanel);

	// Create a textbox for the publisher.
	final TextBox publicationLocationTextBox =
		FormHelper
			.createTextBox(
				(citation == null || citation
					.getProperty(CitationSchema.PUBLICATION_LOCATION) == null) ? ""
					: citation
						.getProperty(CitationSchema.PUBLICATION_LOCATION),
				"Osyl-CitationForm-textBox");
	publicationLocationPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.publicationLocation.label")
				+ ":", publicationLocationTextBox,
			CitationSchema.PUBLICATION_LOCATION);
	mainPanel.add(publicationLocationPanel);

	String typeCitationRes = "";
	if (citation == null
		|| citation.getProperty(CitationSchema.CITATION_RESOURCE_TYPE) == null) {
	    typeCitationRes = "";
	} else {
	    typeCitationRes =
		    citation.getProperty(CitationSchema.CITATION_RESOURCE_TYPE);
	}

	// Create a textbox for the journal.
	final TextBox sourceTitle =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.SOURCE_TITLE) == null) ? ""
			: citation.getProperty(CitationSchema.SOURCE_TITLE),
			"Osyl-CitationForm-textBox");
	journalPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.journal.label")
				+ ":", sourceTitle, CitationSchema.SOURCE_TITLE);
	mainPanel.add(journalPanel);

	final TextBox proceedingSourceTitle =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.SOURCE_TITLE) == null) ? ""
			: citation.getProperty(CitationSchema.SOURCE_TITLE),
			"Osyl-CitationForm-textBox");

	proceedingPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.proceeding.label")
				+ ":", proceedingSourceTitle,
			CitationSchema.SOURCE_TITLE);
	mainPanel.add(proceedingPanel);

	// Create two textboxes for the volume and issue.
	final TextBox volumeTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.VOLUME) == null) ? ""
			: citation.getProperty(CitationSchema.VOLUME),
			"Osyl-CitationForm-doubleTextBox");
	final TextBox issueTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.ISSUE) == null) ? ""
			: citation.getProperty(CitationSchema.ISSUE),
			"Osyl-CitationForm-doubleTextBox");
	volIssuePanel =
		createDoubleLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.volume.label")
				+ ":",
			volumeTextBox,
			CitationSchema.VOLUME,
			osylController
				.getUiMessage("ResProxCitationView.issue.label")
				+ ":", issueTextBox, CitationSchema.ISSUE);
	mainPanel.add(volIssuePanel);

	// Textbox used for the property volume in a proceeding
	// Different from volume textbox because the layout is different
	// If mixed layout in article will not be correct
	final TextBox proceedVolumeTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.VOLUME) == null) ? ""
			: citation.getProperty(CitationSchema.VOLUME),
			"Osyl-CitationForm-textBox");

	volumePanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.volume.label")
				+ ":", proceedVolumeTextBox,
			CitationSchema.VOLUME);
	mainPanel.add(volumePanel);

	// Create a textbox for the pages.
	final TextBox pagesTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.PAGES) == null) ? ""
			: citation.getProperty(CitationSchema.PAGES),
			"Osyl-CitationForm-textBox");
	pagePanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.pages.label")
				+ ":", pagesTextBox, CitationSchema.PAGES);

	mainPanel.add(pagePanel);

	// Create a textbox for ISSN or ISBN.
	isnLabel =
		createNewLabel(osylController
			.getUiMessage("ResProxCitationView.isbn.label") + ":");
	final TextBox isnTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.ISN) == null) ? ""
			: citation.getProperty(CitationSchema.ISN),
			"Osyl-CitationForm-textBox");
	isnPanel =
		createLabelTextboxElement(isnLabel, isnTextBox,
			CitationSchema.ISN);
	mainPanel.add(isnPanel);

	// Create a textbox for DOI.
	final TextBox doiTextBox =
		FormHelper.createTextBox((citation == null || citation
			.getProperty(CitationSchema.DOI) == null) ? ""
			: citation.getProperty(CitationSchema.DOI),
			"Osyl-CitationForm-textBox");
	doiPanel =
		createLabelTextboxElement(
			osylController.getUiMessage("ResProxCitationView.doi.label")
				+ ":", doiTextBox, CitationSchema.DOI);
	mainPanel.add(doiPanel);

	// Create a textbox to allow the user to give another link
	// different from the one to the library
	urlPanel = new VerticalPanel();
	urlPanel.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	urlPanel.add(FormHelper
		.createHiddenField("cipkeys", CitationSchema.URL));
	urlPanel.add(createNewLabel(osylController
		.getUiMessage("ResProxCitationView.url.label") + ":"));
	urlTextBox =
		FormHelper
			.createTextBox(
				(citation == null || citation
					.getProperty(
						COPropertiesType.IDENTIFIER,
						COPropertiesType.IDENTIFIER_TYPE_OTHERLINK) == null) ? ""
					: citation
						.getProperty(
							COPropertiesType.IDENTIFIER,
							COPropertiesType.IDENTIFIER_TYPE_OTHERLINK),
				"Osyl-CitationForm-longTextBox");
	urlPanel.add(urlTextBox);
	mainPanel.add(urlPanel);
	// -------------------------------------------------------------------
	// Type of resource
	// -------------------------------------------------------------------
	typeResourceListBox = new ListBox();
	if (typeCitationRes == "") {
	    // typeCitationRes =
	    // citation.getProperty(COPropertiesType.ASM_RESOURCE_TYPE,
	    // COPropertiesType.ASM_RESOURCE_TYPE);
	    typeCitationRes =
		    citation.getProperty(COPropertiesType.ASM_RESOURCE_TYPE);
	}
	setTypeResource(typeCitationRes);
	typeResourceListBox.addItem(osylController
		.getUiMessage("DocumentEditor.documentType.choose"));
	for (String typeResource : this.typeResourceList) {
	    typeResourceListBox.addItem(
		    coMessages.getMessage("Resource.Type." + typeResource),
		    typeResource);
	}

	HTML typeResourcelabel =
		createNewLabel(uiMessages.getMessage("ResProxCitationView.resourceType.label")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":");

	typeResourceListBox
		.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	typeResourceListBox.setWidth("80%");
	resourceTypePanel = new HorizontalPanel();
	resourceTypePanel.add(typeResourcelabel);
	resourceTypePanel.add(typeResourceListBox);
	resourceTypePanel.setCellWidth(typeResourcelabel, "30%");
	resourceTypePanel.setCellWidth(typeResourceListBox, "70%");
	resourceTypePanel.setCellHorizontalAlignment(typeResourceListBox,
		HasHorizontalAlignment.ALIGN_CENTER);
	// set the type key for saving citation in sakai resources
	mainPanel.add(resourceTypePanel);

	typeResourceListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		setTypeResource(typeResourceListBox
			.getValue(typeResourceListBox.getSelectedIndex()));
	    }
	});

	buildTypeResourceListBox();
	// -------------------------------------------------------------------
	// Add a 'save' button.
	AbstractImagePrototype imgSaveButton =
		AbstractImagePrototype.create(osylImageBundle.save());
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
		String message = "";
		if (title.length() == 0
			|| ((getCurrentCitationType().equals(
				CitationSchema.TYPE_BOOK) || getCurrentCitationType()
				.equals(CitationSchema.TYPE_REPORT)) && author
				.length() == 0)) {

		    message =
			    uiMessages
				    .getMessage("CitationForm.fillRequiredFields");
		} else if (typeResourceListBox.getSelectedIndex() == 0) {
		    message =
			    uiMessages
				    .getMessage("fileUpload.chooseTypesResourceStatus");
		} else if(null!=citation.getId()){
		    String typage="";
		    Map<String, String> cr =
			    OsylEditorEntryPoint
				    .getInstance()
				    .getResTypeContextVisibilityMap()
				    .get(citation.getFilePath()+ "/"+citation.getId());
		    boolean resIncompatibility = false;
		    String resType =
			    typeResourceListBox.getValue(typeResourceListBox
				    .getSelectedIndex());
		    Set<String> parentTitles = new HashSet<String>();
		    if (cr != null) {
			for (Entry<String, String> entry : cr.entrySet()) {
			    String id = entry.getKey();
			    if (!id.equals(citation.getResourceId())) {//citation id
				typage = entry.getValue();
				if (!typage.equals(resType)) {
				    resIncompatibility = true;
				    COModelInterface comi =
					    OsylEditorEntryPoint.getInstance()
						    .getCoModelInterfaceWithId(id);
				    if (comi instanceof COElementAbstract) {
					COElementAbstract coe =
						(COElementAbstract) comi;
					while (!coe.isCOUnit()) {
					    coe = coe.getParent();
					}
					parentTitles.add(coe.getLabel());
				    }
				}
			    }
			}
		    }
		    if (resIncompatibility) {
			StringBuilder sb = new StringBuilder();
			for (String s : parentTitles) {
			    sb.append(s + ", 0");
			}
			String msgParameter = sb.substring(0, sb.length() - 2);
			message +=
				" "
					+ uiMessages.getMessage(
							"DocumentEditor.document.resTypeIncompatibility",
							msgParameter);
			message +=
				" : "
					+ coMessages.getMessage(
						OsylAbstractResProxEditor.RESS_TYPE_MESSAGE_PREFIX + typage);
		    }
		}
		if (!message.equals("")) {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(
				    false,
				    true,
				    message);
		    // get index of citation form to set z-index of alert
		    // window
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

		// Type of resource for citation
		citation.setResourceType(typeResourceListBox
			.getValue(typeResourceListBox.getSelectedIndex()));

		citation.setProperty(CitationSchema.CITATION_RESOURCE_TYPE,
			typeResourceListBox.getValue(typeResourceListBox
				.getSelectedIndex()));

		citation.setProperty(CitationSchema.TYPE,
			citationType.getValue(citationType.getSelectedIndex()));
		citation.setProperty(CitationSchema.TITLE, title);
		citation.setProperty(CitationSchema.ISN, isnTextBox.getText());
		citation.setProperty(CitationSchema.CREATOR,
			authorField.getText());
		citation.setProperty(CitationSchema.VOLUME,
			volumeTextBox.getText());
		citation.setProperty(CitationSchema.ISSUE,
			issueTextBox.getText());
		citation.setProperty(CitationSchema.PAGES,
			pagesTextBox.getText());
		citation.setProperty(CitationSchema.YEAR, yearTextBox.getText());
		citation.setProperty(CitationSchema.DOI, doiTextBox.getText());
		// citation.setProperty(CitationSchema.URL, );
		citation.setProperty(CitationSchema.SOURCE_TITLE,
			sourceTitle.getText());
		citation.setProperty(CitationSchema.PUBLISHER,
			publisherBox.getText());
		citation.setProperty(CitationSchema.PUBLICATION_LOCATION,
			publicationLocationTextBox.getText());

		// citation.setResourceName(listname == null ? "" :
		// listname);

		// We add the corresponding identifier
		// If it a citation of type unknown, we save the identifier
		// from
		// here
		String tempCitationType =
			citationType.getValue(citationType.getSelectedIndex());

		if (tempCitationType.equals(CitationSchema.TYPE_PROCEED)) {
		    citation.setProperty(CitationSchema.VOLUME,
			    proceedVolumeTextBox.getText());
		    citation.setProperty(CitationSchema.SOURCE_TITLE,
			    proceedingSourceTitle.getText());
		}

		if (CitationSchema.TYPE_UNKNOWN
			.equalsIgnoreCase(tempCitationType)) {
		    citation.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK,
			    LinkValidator.parseLink(urlTextBox.getText()));
		    citation.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK, "noLink");
		}
		citation.setFileName(titleField.getText());

		OsylRemoteServiceLocator.getCitationRemoteService()
			.createOrUpdateCitation(currentDirectory, citation,
				new AsyncCallback<String>() {
				    public void onFailure(Throwable caught) {
					Window.alert(uiMessages
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
							DOM.getStyleAttribute(
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
					    it.next()
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
		AbstractImagePrototype.create(osylImageBundle.action_cancel());
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
    private HTML createNewLabel(String text) {
	HTML label = new HTML(text);
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
	HTML lab = createNewLabel(text);
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
	hp.setCellWidth(label, "30%");
	hp.setCellWidth(tb, "70%");
	hp.setCellVerticalAlignment(tb, HasVerticalAlignment.ALIGN_BOTTOM);
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
	hp.setCellWidth(lab1, "30%");
	hp.setCellWidth(tb1, "35%");
	hp.setCellWidth(lab2, "30%");
	hp.setCellWidth(tb2, "35%");
	hp.setCellVerticalAlignment(tb1, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(tb2, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellHorizontalAlignment(lab2, HasHorizontalAlignment.ALIGN_RIGHT);

	hp.setStylePrimaryName("Osyl-CitationForm-genericPanel");
	return hp;
    }

    /**
     * Creates a list with the types of citations
     */
    private void initCitationTypes() {
	citationTypeKeys = new ArrayList<String>();
	citationTypeKeys.add(CitationSchema.TYPE_BOOK);
	citationTypeKeys.add(CitationSchema.TYPE_REPORT);
	citationTypeKeys.add(CitationSchema.TYPE_ARTICLE);
	citationTypeKeys.add(CitationSchema.TYPE_UNKNOWN);
	citationTypeKeys.add(CitationSchema.TYPE_PROCEED);
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
			&& key.equals(citation.getProperty(CitationSchema.TYPE))) {
		    citationType.setSelectedIndex(i);
		}
	    }
	}
    }

    /**
     * Initializes listbox for citation types
     */
    private void buildTypeResourceListBox() {
	for (int i = 0; i < typeResourceListBox.getItemCount(); i++) {
	    String item = typeResourceListBox.getValue(i);
	    if (item.equals(citation
		    .getProperty(CitationSchema.CITATION_RESOURCE_TYPE))) {
		typeResourceListBox.setItemSelected(i, true);
	    }
	}
    }

    private void setTypeResource(String typeResource) {
	this.typeResource = typeResource;
    }

    public String getTypeResource() {
	return typeResource;
    }

    public String getTypeResourceSelected() {
	if (typeResourceListBox.getSelectedIndex() != -1)
	    return typeResourceListBox.getValue(typeResourceListBox
		    .getSelectedIndex());
	else
	    return getTypeResource();
    }

    /**
     * Updates form according to the new type of citation, shows or hides fields
     * for each type.
     * 
     * @param the new type of citation
     */
    private void updateForm(String newType) {
	if (newType.equals(CitationSchema.TYPE_BOOK)
		|| newType.equals(CitationSchema.TYPE_REPORT)) {
	    authorLabel.setHTML(osylController
		    .getUiMessage("ResProxCitationView.author.label")
		    + OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":");
	    isnLabel.setText(osylController
		    .getUiMessage("ResProxCitationView.isbn.label") + ":");
	    titlePanel.setVisible(true);
	    authorPanel.setVisible(true);
	    yearPanel.setVisible(true);
	    isnPanel.setVisible(true);
	    publisherPanel.setVisible(true);
	    publicationLocationPanel.setVisible(true);
	    citationPanel.setVisible(false);
	    journalPanel.setVisible(false);
	    proceedingPanel.setVisible(false);
	    volumePanel.setVisible(false);
	    volIssuePanel.setVisible(false);
	    pagePanel.setVisible(false);
	    doiPanel.setVisible(false);
	    urlPanel.setVisible(false);
	} else if (newType.equals(CitationSchema.TYPE_ARTICLE)) {
	    authorLabel.setHTML(osylController
		    .getUiMessage("ResProxCitationView.author.label") + ":");
	    isnLabel.setText(osylController
		    .getUiMessage("ResProxCitationView.issn.label") + ":");
	    titlePanel.setVisible(true);
	    authorPanel.setVisible(true);
	    isnPanel.setVisible(false);
	    journalPanel.setVisible(true);
	    proceedingPanel.setVisible(false);
	    volumePanel.setVisible(false);
	    volIssuePanel.setVisible(true);
	    pagePanel.setVisible(true);
	    doiPanel.setVisible(false);
	    yearPanel.setVisible(true);
	    citationPanel.setVisible(false);
	    urlPanel.setVisible(false);
	    publisherPanel.setVisible(false);
	    publicationLocationPanel.setVisible(false);
	} else if (newType.equals(CitationSchema.TYPE_PROCEED)) {
	    authorLabel.setHTML(osylController
		    .getUiMessage("ResProxCitationView.author.label") + ":");
	    isnLabel.setText(osylController
		    .getUiMessage("ResProxCitationView.issn.label") + ":");
	    titlePanel.setVisible(true);
	    authorPanel.setVisible(true);
	    publisherPanel.setVisible(true);
	    publicationLocationPanel.setVisible(true);
	    isnPanel.setVisible(false);
	    journalPanel.setVisible(false);
	    proceedingPanel.setVisible(true);
	    volumePanel.setVisible(true);
	    volIssuePanel.setVisible(false);
	    pagePanel.setVisible(true);
	    doiPanel.setVisible(false);
	    yearPanel.setVisible(true);
	    citationPanel.setVisible(false);
	    urlPanel.setVisible(false);
	} else {
	    citationPanel.setVisible(true);
	    titlePanel.setVisible(false);
	    authorPanel.setVisible(false);
	    yearPanel.setVisible(false);
	    isnPanel.setVisible(false);
	    journalPanel.setVisible(false);
	    proceedingPanel.setVisible(false);
	    volumePanel.setVisible(false);
	    volIssuePanel.setVisible(false);
	    pagePanel.setVisible(false);
	    doiPanel.setVisible(false);
	    publisherPanel.setVisible(false);
	    publicationLocationPanel.setVisible(false);
	    urlPanel.setVisible(true);
	}
	resourceTypePanel.setVisible(true);
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
