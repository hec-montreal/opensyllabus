/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.OsylRichTextArea;
import org.sakaiquebec.opensyllabus.client.ui.base.Dimension;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylWindowPanel;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylDisclosureListener;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationBrowser;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxCitationView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COProperty;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.util.LinkValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Document editor to be used within {@link OsylAbstractView}. The edition mode
 * uses a Rich-text editor and the view mode displays a clickable link.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:Laurent.Danet@hec.ca">Laurent Danet</a>
 */
public class OsylCitationEditor extends OsylAbstractBrowserEditor {

    // Our main panel which will display the viewer and the meta-info
    private VerticalPanel mainPanel;

    // Our editor widgets
    private RichTextArea editorDesc;
    private VerticalPanel editorPanel;
    // Our viewer
    private VerticalPanel viewer;
    // Our viewer (type)
    private HTML viewerType;
    // Our view link
    private HTML viewerLink;
    // Our viewer (description)
    private HTML viewerDesc;

    // Contains the viewer and info icons for the requirement level
    private FlexTable viewerPanel;

    // Browser panel widgets
    private HTML citationPreviewLabel;

    // Browser panel widgets
    // private Button saveButton;
    private ImageAndTextButton saveButton;

    // Remove link to library widget

    private CheckBox disableBookstoreLinkCheckBox;
    private TextBox bookStoreLink;

    private CheckBox disableOtherLinkCheckBox;
    private TextBox editorOtherLink;
    private TextBox editorOtherLinkLabel;

    // Remove any link associated to this citation
    private CheckBox disableLibraryLinkCheckBox;
    private HTML libraryLink;

    private int originalEditorDescHeight;

    private DisclosurePanel metaInfoDiscPanel;

    // List of type of resources
    private ListBox typeResourceListBox;

    // Type of resource for hyperlink
    private String typeResource;

    private KeyPressHandler kph = new KeyPressHandler() {

	public void onKeyPress(KeyPressEvent event) {
	    saveButton.setEnabled(true);
	}
    };

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylCitationEditor(OsylAbstractView parent) {
	super(parent);
	initMainPanel();
	initWidget(getMainPanel());
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */

    /**
     * Creates and set the main panel.
     */
    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-ResProxDocument");
    }

    private VerticalPanel getMainPanel() {
	return mainPanel;
    }

    private void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    /**
     * Creates and set the low-level editor (TextBox).
     */
    private void initEditor() {
	editorPanel = new VerticalPanel();
	Label l2 = new Label(getView().getUiMessage("comment"));
	editorPanel.add(l2);
	editorDesc = new OsylRichTextArea();
	editorDesc.setHeight("80px");
	editorDesc.setStylePrimaryName("Osyl-UnitView-TextArea");
	editorPanel.add(editorDesc);
    }

    /**
     * Creates and set the low-level viewers (HTML panels).
     */
    private void initViewer() {
	HTML htmlViewer = new HTML(getView().getCitationPreview());
	setViewerLink(htmlViewer);

	HTML htmlViewerDesc = new HTML(getView().getCommentFromModel());
	htmlViewerDesc.setStylePrimaryName("description");
	htmlViewerDesc.setTitle(getView().getUiMessage(
		"CitationEditor.document.description"));
	setViewerDesc(htmlViewerDesc);

	HTML htmlViewerType =
		new HTML((getView().getDocType() == null) ? "" : "["
			+ getView().getCoMessage(
				RESS_TYPE_MESSAGE_PREFIX
					+ getView().getDocType()) + "]");
	htmlViewerType.setStylePrimaryName("type");
	setViewerType(htmlViewerType);

	setViewer(new VerticalPanel());
	getViewer().setStylePrimaryName("Osyl-UnitView-HtmlViewer");
	getViewer().add(getViewerLink());

	String isn =
		getView()
			.getModel()
			.getResource()
			.getProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_ISN);
	if (isn != null && !isn.trim().equals("")) {
	    HTML isbn_issnLabel;
	    String type =
		    getView().getModel().getResource()
			    .getProperty(COPropertiesType.RESOURCE_TYPE);
	    if (type.equals(CitationSchema.TYPE_BOOK)
		    || type.equals(CitationSchema.TYPE_REPORT))
		isbn_issnLabel =
			new HTML(getUiMessage("ResProxCitationView.isbn.label")
				+ ":" + isn);
	    else
		isbn_issnLabel =
			new HTML(getUiMessage("ResProxCitationView.issn.label")
				+ ":" + isn);
	    getViewer().add(isbn_issnLabel);
	}
	getViewer().add(getViewerType());
	getViewer().add(getViewerDesc());

	if (getView().getCitationLibraryLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel
		    .setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-libraryImage");
	    libLinkPanel.add(h);
	    HTML link =
		    new HTML(
			    getView()
				    .generateHTMLLink(
					    getView().getCitationLibraryLink(),
					    getUiMessage("ResProxCitationView.libraryLink.available")));
	    libLinkPanel.add(link);
	    getViewer().add(libLinkPanel);
	}

	if (getView().getCitationBookstoreLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel
		    .setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-bookstoreImage");
	    libLinkPanel.add(h);
	    HTML link =
		    new HTML(
			    getView()
				    .generateHTMLLink(
					    getView()
						    .getCitationBookstoreLink(),
					    getUiMessage("ResProxCitationView.bookstoreLink.available")));
	    libLinkPanel.add(link);
	    getViewer().add(libLinkPanel);
	}

	if (getView().getCitationOtherLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel
		    .setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-otherImage");
	    libLinkPanel.add(h);
	    String urlLabel = getView().getCitationOtherLinkLabel();
	    HTML link =
		    new HTML(
			    getView()
				    .generateHTMLLink(
					    getView().getCitationOtherLink(),
					    urlLabel != null
						    && !urlLabel.equals("") ? getView()
						    .getCitationOtherLinkLabel()
						    : getUiMessage("ResProxCitationView.otherLink.available")));
	    libLinkPanel.add(link);
	    getViewer().add(libLinkPanel);
	}

	setViewerPanel(new FlexTable());

	getViewerPanel().setStylePrimaryName("Osyl-UnitView-HtmlViewer");

	Image reqLevelIcon = getCurrentRequirementLevelIcon();
	if (reqLevelIcon != null) {
	    getViewerPanel().addStyleName("Osyl-UnitView-LvlReq");
	}

	getViewerPanel().setWidget(0, 0, reqLevelIcon);
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(0, 0,
		"Osyl-UnitView-IconLvlReq");
	int column = 0;
	if (getView().isContextImportant()) {
	    getViewerPanel().addStyleName("Osyl-UnitView-Important");
	    getViewerPanel().setWidget(0, 1,
		    new HTML(getView().getCoMessage("MetaInfo.important")));
	    getViewerPanel().getFlexCellFormatter().setStylePrimaryName(0, 1,
		    "Osyl-UnitView-TextImportant");
	    column = 1;
	}

	if (getView().isNewAccordingSelectedDate()) {
	    getViewerPanel().addStyleName("Osyl-newElement");
	}

	getViewerPanel().setWidget(column, 1, getViewer());
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(column, 1,
		"Osyl-UnitView-Content");

	getMainPanel().add(getViewerPanel());

    }

    private void setViewerLink(HTML html) {
	this.viewerLink = html;
    }

    private HTML getViewerLink() {
	return this.viewerLink;
    }

    private HTML getViewerDesc() {
	return viewerDesc;
    }

    private void setViewerDesc(HTML viewerDesc) {
	this.viewerDesc = viewerDesc;
    }

    private void setViewerPanel(FlexTable viewerPanel) {
	this.viewerPanel = viewerPanel;
    }

    private FlexTable getViewerPanel() {
	return viewerPanel;
    }

    public VerticalPanel getViewer() {
	return viewer;
    }

    public void setViewer(VerticalPanel viewer) {
	this.viewer = viewer;
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected OsylResProxCitationView getView() {
	return (OsylResProxCitationView) super.getView();
    }

    public Dimension getPreferredSize() {
	return new Dimension(400, 100);
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editorDesc.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editorPanel;
    }

    public boolean prepareForSave() {
	String message = "";
	String typage = "";
	if (getResourceURI() == null) {
	    message +=
		    getUiMessage("CitationEditor.save.error.citationUndefined");
	} else if (saveButton.isEnabled()) {
	    message += getUiMessage("CitationEditor.ChangeUrl.Save");

	    // -----------------------------------------------------------------------
	    // Check resource type incompatibility
	    // -----------------------------------------------------------------------
	    Map<String, String> cr =
		    OsylEditorEntryPoint
			    .getInstance()
			    .getResTypeContextVisibilityMap()
			    .get(getResourceURI()
				    + "/"
				    + getSelectedCitationProperty(CitationSchema.CITATIONID));
	    boolean resIncompatibility = false;
	    String resType =
		    typeResourceListBox.getValue(typeResourceListBox
			    .getSelectedIndex());
	    Set<String> parentTitles = new HashSet<String>();
	    if (cr != null) {
		for (Entry<String, String> entry : cr.entrySet()) {
		    String id = entry.getKey();
		    if (!id.equals(getView().getModel().getId())) {
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
		    sb.append(s + ", ");
		}
		String msgParameter = sb.substring(0, sb.length() - 2);
		message +=
			" "
				+ getView()
					.getUiMessage(
						"DocumentEditor.document.resTypeIncompatibility",
						msgParameter);
		message +=
			" : "
				+ getView().getCoMessage(
					RESS_TYPE_MESSAGE_PREFIX + typage);
	    }
	}
	if (message.equals("")) {
	    return true;
	} else {
	    OsylAlertDialog oad =
		    new OsylAlertDialog(getUiMessage("Global.error"), message);
	    oad.center();
	    oad.show();
	    return false;
	}
    }

    public void enterEdit() {
	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	initEditor();
	// We get the description text to edit from the model
	editorDesc.setHTML(getView().getCommentFromModel());
	editorDesc.setFocus(true);

	createEditBox(getEditBoxTitle());

	if (browser.getItemToSelect() == null) {
	    getBrowser().setItemToSelect(
		    new OsylCitationItem(getView().getCitationId(), getView()
			    .getDocPath()));
	}

    } // enterEdit

    public void enterView() {

	// We keep track that we are now in view-mode
	setInEditionMode(false);
	getMainPanel().clear();
	if (!(isReadOnly() && getView().isContextHidden())) {
	    initViewer();

	    // If we are not in read-only mode, we display some meta-info and
	    // add
	    // buttons and listeners enabling edition or deletion:
	    if (!isReadOnly()) {
		getMainPanel().add(getMetaInfoLabel());
		addViewerStdButtons();
	    }
	} else {
	    getMainPanel().setVisible(false);
	}
    } // enterView

    public String getTypeResourceSelected() {
	if (typeResourceListBox.getSelectedIndex() != -1)
	    return typeResourceListBox.getValue(typeResourceListBox
		    .getSelectedIndex());
	else
	    return "";
    }

    @Override
    public Widget getConfigurationWidget() {
	return null;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    protected void initBrowser() {
	browserPanel = new VerticalPanel();
	browserPanel.setStylePrimaryName("Osyl-ResourceBrowserPanel");

	// Note: we prepend the message with a blank space instead of using
	// padding in the CSS because we use 100% width and adding some padding
	// causes the label to be larger than the popup in Firefox (very ugly).
	HTML label1 =
		new HTML("&nbsp;"
			+ getView().getUiMessage(
				"CitationEditor.document.selection"));
	label1.setStylePrimaryName("sectionLabel");
	browserPanel.add(label1);

	// SAKAI MODE
	String basePath = getView().getDocPath();
	String siteId = getController().getSiteId();
	String resourcesPath = "/group/" + siteId + "/";
	basePath =
		basePath == null ? resourcesPath
			+ getController().getDocFolderName() : basePath;
	browser =
		new OsylCitationBrowser(basePath, getView().getCitationId(),
			getView().getDocPath() + "/" + getView().getDocName(),
			getView().getModel().getId());

	browser.addEventHandler((RFBItemSelectionEventHandler) this);
	browser.addEventHandler((RFBAddFolderEventHandler) this);
	browser.addEventHandler((ItemListingAcquiredEventHandler) this);

	browserPanel.add(browser);
	browser.setWidth("100%");

	metaInfoDiscPanel =
		new DisclosurePanel(getOsylImageBundle().expand(),
			getOsylImageBundle().collapse(),
			getView().getUiMessage(
				"CitationEditor.document.details"));
	metaInfoDiscPanel.setAnimationEnabled(true);
	metaInfoDiscPanel.setStylePrimaryName("DetailsPanel");
	VerticalPanel metaInfoPanel = new VerticalPanel();
	metaInfoDiscPanel.add(metaInfoPanel);
	metaInfoPanel.setWidth("100%");
	browserPanel.add(metaInfoDiscPanel);

	OsylDisclosureListener odl =
		new OsylDisclosureListener(getEditorPopup());
	metaInfoDiscPanel.addCloseHandler(odl);
	metaInfoDiscPanel.addOpenHandler(odl);

	OsylCitationItem selectedFile = null;

	if (browser.getSelectedAbstractBrowserItem() != null
		&& !browser.getSelectedAbstractBrowserItem().isFolder()) {
	    selectedFile =
		    (OsylCitationItem) browser.getSelectedAbstractBrowserItem();

	}
	citationPreviewLabel = new HTML();
	citationPreviewLabel.setHTML(selectedFile != null ? selectedFile
		.getCitationsInfos() : "");
	citationPreviewLabel.setWidth("100%");
	metaInfoPanel.add(citationPreviewLabel);

	// Add option to remove the link to the library references
	FlexTable linksPanel = new FlexTable();
	metaInfoPanel.add(linksPanel);
	linksPanel.setWidth("100%");

	// library link
	HTML iLibrary = new HTML();
	iLibrary.setStylePrimaryName("Osyl-ResProxCitationView-libraryImage");
	linksPanel.setWidget(0, 0, iLibrary);
	disableLibraryLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.disableLibraryLink.title"));
	disableLibraryLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.disableLibraryLink.title"));
	disableLibraryLinkCheckBox
		.setStylePrimaryName("Osyl-ResProxCitationView-linkCheckbox");
	disableLibraryLinkCheckBox.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		final OsylCitationItem selectedFile =
			(OsylCitationItem) browser
				.getSelectedAbstractBrowserItem();

		// If the citation is of type unknow we tell the user he can
		// not
		// change the url type
		if (selectedFile.getProperty(CitationSchema.TYPE)
			.equalsIgnoreCase(CitationSchema.TYPE_UNKNOWN)) {
		    Window.alert(getUiMessage("CitationEditor.ChangeUrl.InvalidChange"));
		    disableLibraryLinkCheckBox.setValue(true);

		} else {
		    saveButton.setEnabled(true);
		}
	    }
	});
	linksPanel.setWidget(0, 1, disableLibraryLinkCheckBox);
	libraryLink = new HTML(getUiMessage("ResProxCitationView.link.label"));
	linksPanel.setWidget(0, 2, libraryLink);

	// bookstore link
	HTML iBookstrore = new HTML();
	iBookstrore
		.setStylePrimaryName("Osyl-ResProxCitationView-bookstoreImage");
	linksPanel.setWidget(1, 0, iBookstrore);
	disableBookstoreLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.disableBookstoreLink.title"));
	disableBookstoreLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.disableBookstoreLink.title"));
	disableBookstoreLinkCheckBox
		.setStylePrimaryName("Osyl-ResProxCitationView-linkCheckbox");
	disableBookstoreLinkCheckBox.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		bookStoreLink.setEnabled(!disableBookstoreLinkCheckBox
			.getValue());
		saveButton.setEnabled(true);
	    }
	});

	linksPanel.setWidget(1, 1, disableBookstoreLinkCheckBox);

	bookStoreLink = new TextBox();
	bookStoreLink
		.setStylePrimaryName("Osyl-ResProxCitationView-linkTextbox");
	linksPanel.setWidget(1, 3, bookStoreLink);

	HTML iOther = new HTML();
	iOther.setStylePrimaryName("Osyl-ResProxCitationView-otherImage");
	linksPanel.setWidget(2, 0, iOther);
	disableOtherLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.disableOtherLink.title"));
	disableOtherLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.disableOtherLink.title"));
	disableOtherLinkCheckBox
		.setStylePrimaryName("Osyl-ResProxCitationView-linkCheckbox");
	disableOtherLinkCheckBox.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		editorOtherLink.setEnabled(!disableOtherLinkCheckBox.getValue());
		editorOtherLinkLabel.setEnabled(!disableOtherLinkCheckBox
			.getValue());
		saveButton.setEnabled(true);

	    }
	});
	linksPanel.setWidget(2, 1, disableOtherLinkCheckBox);

	linksPanel.setWidget(
		2,
		2,
		new Label(getView().getUiMessage(
			"CitationEditor.otherLink.nameLabel")));
	editorOtherLinkLabel = new TextBox();
	editorOtherLinkLabel
		.setStylePrimaryName("Osyl-ResProxCitationView-linkTextbox");
	linksPanel.setWidget(2, 3, editorOtherLinkLabel);

	linksPanel.setWidget(
		3,
		2,
		new Label(getView().getUiMessage(
			"CitationEditor.otherLink.urlLabel")));
	editorOtherLink = new TextBox();
	editorOtherLink
		.setStylePrimaryName("Osyl-ResProxCitationView-linkTextbox");
	linksPanel.setWidget(3, 3, editorOtherLink);

	// -----------------------------------------------------------------------
	typeResourceListBox = new ListBox();
	HorizontalPanel listBoxPanel = new HorizontalPanel();
	listBoxPanel.add(new HTML(getView()
		.getUiMessage("DocumentEditor.document.type")+"&nbsp;"));
	listBoxPanel.add(typeResourceListBox);
	metaInfoPanel.add(listBoxPanel);
	

	typeResourceListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		if (typeResourceListBox.getSelectedIndex() > 0) {
		    saveButton.setEnabled(true);
		} else {
		    saveButton.setEnabled(false);
		}
		setTypeResource(typeResourceListBox
			.getValue(typeResourceListBox.getSelectedIndex()));
	    }
	});
	// -----------------------------------------------------------------------

	AbstractImagePrototype imgSaveButton =
		AbstractImagePrototype.create(getOsylImageBundle().save());
	saveButton =
		new ImageAndTextButton(imgSaveButton, getView().getUiMessage(
			"DocumentEditor.save.name"));
	saveButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	saveButton
		.setTitle(getView().getUiMessage("DocumentEditor.save.title"));

	saveButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {

		String message = "";
		String typage = "";
		// -----------------------------------------------------------------------
		// Check resource type incompatibility
		// -----------------------------------------------------------------------
		Map<String, String> cr =
			OsylEditorEntryPoint
				.getInstance()
				.getResTypeContextVisibilityMap()
				.get(getResourceURI()
					+ "/"
					+ getSelectedCitationProperty(CitationSchema.CITATIONID));
		boolean resIncompatibility = false;
		String resType =
			typeResourceListBox.getValue(typeResourceListBox
				.getSelectedIndex());
		Set<String> parentTitles = new HashSet<String>();
		if (cr != null) {
		    for (Entry<String, String> entry : cr.entrySet()) {
			String id = entry.getKey();
			if (!id.equals(getView().getModel()
				.getId())) {
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
			sb.append(s + ", ");
		    }
		    String msgParameter = sb.substring(0, sb.length() - 2);
		    message +=
			    " "
				    + getView()
					    .getUiMessage(
						    "DocumentEditor.document.resTypeIncompatibility",
						    msgParameter);
		    message +=
			    " : "
				    + getView().getCoMessage(
					    RESS_TYPE_MESSAGE_PREFIX + typage);
		}

		if (!message.equals("")) {
		    OsylAlertDialog oad =
			    new OsylAlertDialog(getUiMessage("Global.error"),
				    message);
		    oad.center();
		    oad.show();
		} else {

		    final OsylCitationItem selectedFile =
			    (OsylCitationItem) browser
				    .getSelectedAbstractBrowserItem();
		    if (disableLibraryLinkCheckBox.getValue()) {
			selectedFile.setProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_NOLINK,
				"noLink");
		    } else {
			selectedFile.removeProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_NOLINK);
		    }
		    if (disableBookstoreLinkCheckBox.getValue()) {
			selectedFile.removeProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE);
		    } else {
			bookStoreLink.setText(LinkValidator
				.parseLink(bookStoreLink.getText()));
			selectedFile.setProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE,
				bookStoreLink.getText());
		    }
		    if (disableOtherLinkCheckBox.getValue()) {
			selectedFile.removeProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_OTHERLINK);
		    } else {
			editorOtherLink.setText(LinkValidator
				.parseLink(editorOtherLink.getText()));
			selectedFile.setProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_OTHERLINK,
				editorOtherLink.getText());
			COProperty coProperty =
				selectedFile
					.getCOProperty(
						COPropertiesType.IDENTIFIER,
						COPropertiesType.IDENTIFIER_TYPE_OTHERLINK);
			coProperty
				.addAttribute(
					COPropertiesType.IDENTIFIER_TYPE_OTHERLINK_LABEL,
					editorOtherLinkLabel.getText());
		    }
		    // Type of resource for citation
		    selectedFile.setResourceType(typeResourceListBox
			    .getValue(typeResourceListBox.getSelectedIndex()));

		    selectedFile.setProperty(
			    COPropertiesType.ASM_RESOURCE_TYPE,
			    typeResourceListBox.getValue(typeResourceListBox
				    .getSelectedIndex()));

		    OsylRemoteServiceLocator.getCitationRemoteService()
			    .createOrUpdateCitation(
				    getBrowser().getCurrentDirectory()
					    .getDirectoryPath(), selectedFile,
				    new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
					    OsylUnobtrusiveAlert failure =
						    new OsylUnobtrusiveAlert(
							    getView()
								    .getUiMessage(
									    "Global.error")
								    + ": "
								    + getView()
									    .getUiMessage(
										    "CitationEditor.document.PropUpdateError"));
					    OsylEditorEntryPoint
						    .showWidgetOnTop(failure);
					}

					public void onSuccess(String result) {
					    OsylUnobtrusiveAlert alert =
						    new OsylUnobtrusiveAlert(
							    getUiMessage("CitationEditor.document.PropUpdateSuccess"));
					    OsylEditorEntryPoint
						    .showWidgetOnTop(alert);
					    getBrowser()
						    .refreshCitationsInList();
					}
				    });

		    saveButton.setEnabled(false);
		}
	    }
	});

	saveButton.setEnabled(false);

	HorizontalPanel savePanel = new HorizontalPanel();
	savePanel.setWidth("98%");
	metaInfoPanel.add(savePanel);
	savePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

	savePanel.add(saveButton);

	bookStoreLink.addKeyPressHandler(kph);
	editorOtherLink.addKeyPressHandler(kph);
	editorOtherLinkLabel.addKeyPressHandler(kph);

	VerticalPanel rightsAndSavePanel = new VerticalPanel();
	rightsAndSavePanel.setWidth("98%");
	metaInfoPanel.add(rightsAndSavePanel);

	/*
	 * HorizontalPanel savePanel = new HorizontalPanel();
	 * savePanel.setWidth("100%"); rightsAndSavePanel.add(savePanel);
	 * savePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	 */
	// Note: we prepend the message with a blank space instead of using
	// padding in the CSS because we use 100% width and adding some padding
	// causes the label to be larger than the popup in Firefox (very ugly).
	HTML label2 =
		new HTML("&nbsp;"
			+ getView().getUiMessage("CitationEditor.context"));
	label2.setStylePrimaryName("sectionLabel");
	browserPanel.add(label2);

	updateResourceTypeInfo();
    }

    /**
     * update the ResourceType List the list is cached into the object
     * resourceLicencingInfo; TODO: make the property to be static or available
     * for every document editor when the operation is completed, the flag is
     * set to licenceListReady=true
     */
    private void updateResourceTypeInfo() {
	List<String> typesResourceList =
		OsylController.getInstance().getOsylConfig()
			.getResourceTypeList();
	buildTypeResourceListBox(typesResourceList);
    }

    private void buildTypeResourceListBox(List<String> typesResourceList) {
	typeResourceListBox.clear();
	typeResourceListBox.addItem(getView().getUiMessage(
		"DocumentEditor.documentType.choose"),"");
	for (String typeResource : typesResourceList) {
	    typeResourceListBox.addItem(
		    getView().getCoMessage("Resource.Type." + typeResource),
		    typeResource);
	}
	String typeResCitation = getTypeResource();
	if (getTypeResource() == null || getTypeResource().equals("")) {
	    typeResCitation = getView().getResourceTypeFromModel();
	}

	if (typeResCitation != null) {
	    for (int i = 0; i < typeResourceListBox.getItemCount(); i++) {
		String item = typeResourceListBox.getValue(i);
		if (item.equals(typeResCitation)) {
		    typeResourceListBox.setItemSelected(i, true);
		}
	    }
	}
	// refreshBrowsingComponents();
    }

    protected String getResourcesPath() {
	if (getController().isInHostedMode()) {
	    return "";
	} else {
	    String uri = GWT.getModuleBaseURL();
	    String serverId = uri.split("\\s*/portal/tool/\\s*")[0];
	    String resourcesPath = serverId + "/sdata/ci/group/";
	    resourcesPath =
		    OsylAbstractBrowserComposite
			    .uriSlashCorrection(resourcesPath);
	    return resourcesPath;
	}
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(editorDesc);
	return focusWidgetList;
    }

    @Override
    protected Widget[] getAdditionalOptionWidgets() {
	return null;
    }

    @Override
    public void maximizeEditor() {
	originalEditorDescHeight = editorDesc.getOffsetHeight();
	int height = OsylEditorEntryPoint.getViewportHeight();
	if (height < getOriginalEditorPopupHeight())
	    height = getOriginalEditorPopupHeight();
	int areaAdd = height - getOriginalEditorPopupHeight();
	editorDesc.setHeight((originalEditorDescHeight + areaAdd) + "px");
	getEditorPopup().setHeight(height + "px");
	OsylEditorEntryPoint.centerObject(getEditorPopup());
    }

    @Override
    public void normalizeEditorWindowState() {
	getEditorPopup().setHeight(getOriginalEditorPopupHeight() + "px");
	editorDesc.setHeight(originalEditorDescHeight + "px");
    }

    @Override
    protected Widget getMetaInfoLabel() {
	Label metaInfoLabel = (Label) super.getMetaInfoLabel();
	String label = metaInfoLabel.getText();

	metaInfoLabel.setText(label);
	return metaInfoLabel;
    }

    @Override
    public String getText() {
	throw new IllegalStateException("Should not be used with citation");
    }

    @Override
    public void setText(String text) {
	throw new IllegalStateException("Should not be used with citation");
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    protected OsylCitationBrowser getBrowser() {
	return (OsylCitationBrowser) browser;
    }

    public String getDescription() {
	if (isInEditionMode()) {
	    return editorDesc.getHTML();
	} else {
	    return viewerDesc.getHTML();
	}
    }

    /**
     * {@inheritDoc}
     */
    protected void refreshBrowsingComponents() {

	// Called to refresh the file browser components
	getBrowser().refreshBrowser();

	boolean isFound = false;
	int previousCitationPreviewLabelHeight =
		citationPreviewLabel.getOffsetHeight();

	if (browser.getSelectedAbstractBrowserItem() != null) {
	    if (browser.getSelectedAbstractBrowserItem().isFolder()
		    || browser.getSelectedAbstractBrowserItem() instanceof OsylCitationListItem) {
		citationPreviewLabel.setHTML("");
		bookStoreLink.setText("");
		bookStoreLink.setEnabled(false);
		editorOtherLink.setText("");
		editorOtherLink.setEnabled(false);
		disableLibraryLinkCheckBox.setValue(false);
		disableLibraryLinkCheckBox.setEnabled(false);
		disableBookstoreLinkCheckBox.setValue(false);
		disableBookstoreLinkCheckBox.setEnabled(false);
		disableOtherLinkCheckBox.setEnabled(false);
		disableOtherLinkCheckBox.setValue(false);
		typeResourceListBox.setSelectedIndex(-1);
		saveButton.setEnabled(false);
	    } else {
		disableLibraryLinkCheckBox.setEnabled(true);
		disableBookstoreLinkCheckBox.setEnabled(true);
		disableOtherLinkCheckBox.setEnabled(true);
		OsylCitationItem selectedFile =
			(OsylCitationItem) browser
				.getSelectedAbstractBrowserItem();
		citationPreviewLabel.setHTML(selectedFile.getCitationsInfos());

		disableLibraryLinkCheckBox.setValue(!hasLink(selectedFile));
		libraryLink.setHTML(getView().generateHTMLLink(
			getIdentifierType(selectedFile,
				COPropertiesType.IDENTIFIER_TYPE_LIBRARY),
			getUiMessage("ResProxCitationView.link.label")));

		if (hasIdentifierType(selectedFile,
			COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE)) {
		    disableBookstoreLinkCheckBox.setValue(false);
		    bookStoreLink.setEnabled(true);
		    bookStoreLink.setText(getIdentifierType(selectedFile,
			    COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE));
		} else {
		    disableBookstoreLinkCheckBox.setValue(true);
		    bookStoreLink.setText("");
		    bookStoreLink.setEnabled(false);
		}
		// Type of resource
		String resourceTypeCitation =
			selectedFile
				.getProperty(COPropertiesType.ASM_RESOURCE_TYPE);
		setTypeResource(resourceTypeCitation);

		for (int i = 0; i < typeResourceListBox.getItemCount(); i++) {
		    String item = typeResourceListBox.getValue(i);
		    if (item.equals(resourceTypeCitation)) {
			typeResourceListBox.setItemSelected(i, true);
		    }
		}
		// --------------------------------------------------------------------

		if (hasIdentifierType(selectedFile,
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK)) {
		    disableOtherLinkCheckBox.setValue(false);
		    editorOtherLinkLabel.setEnabled(true);

		    String urlLabel = null;
		    COProperty coProperty =
			    selectedFile.getCOProperty(
				    COPropertiesType.IDENTIFIER,
				    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK);

		    if (coProperty != null) {
			urlLabel =
				coProperty
					.getAttribute(COPropertiesType.IDENTIFIER_TYPE_OTHERLINK_LABEL);
		    }

		    if (urlLabel == null || urlLabel.equals("")) {
			editorOtherLinkLabel.setText("");
		    } else {
			editorOtherLinkLabel.setText(urlLabel);
		    }
		    editorOtherLink.setEnabled(true);
		    editorOtherLink.setText(getIdentifierType(selectedFile,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK));
		} else {
		    disableOtherLinkCheckBox.setValue(true);
		    editorOtherLinkLabel.setText("");
		    editorOtherLinkLabel.setEnabled(false);
		    editorOtherLink.setText("");
		    editorOtherLink.setEnabled(false);
		}

		isFound = true;
	    }
	}
	if (!isFound) {
	    citationPreviewLabel.setHTML("");
	    typeResourceListBox.setSelectedIndex(0);
	}
	if (metaInfoDiscPanel.isOpen()) {
	    OsylWindowPanel popup = getEditorPopup();
	    popup.updateLayout(popup.getContentWidth(),
		    popup.getContentHeight()
			    - previousCitationPreviewLabelHeight
			    + citationPreviewLabel.getOffsetHeight());
	}
    }

    public String getCitationInfos() {
	return citationPreviewLabel.getHTML();
    }

    public void setTypeResource(String typeResource) {
	this.typeResource = typeResource;
    }

    public String getTypeResource() {
	return typeResource;
    }

    private String getEditBoxTitle() {
	if (getView().isDocumentDefined()) {
	    return getView().getUiMessage("CitationEditor.title.edit");
	} else {
	    return getView().getUiMessage("CitationEditor.title.add");
	}
    }

    public String getSelectedCitationProperty(String key) {
	OsylCitationItem selectedFile =
		(OsylCitationItem) browser.getSelectedAbstractBrowserItem();
	return selectedFile.getProperty(key);
    }

    public String getSelectedCitationProperty(String key, String type) {
	OsylCitationItem selectedFile =
		(OsylCitationItem) browser.getSelectedAbstractBrowserItem();
	return selectedFile.getProperty(key, type);
    }

    public String getSelectedCitationPropertyAttr(String key, String type,
	    String attrKey) {
	OsylCitationItem selectedFile =
		(OsylCitationItem) browser.getSelectedAbstractBrowserItem();
	try {
	    return selectedFile.getCOProperty(key, type).getAttribute(attrKey);
	} catch (Exception e) {
	    return null;
	}
    }

    public ImageAndTextButton getSaveButton() {
	return saveButton;
    }

    private boolean hasIdentifierType(OsylCitationItem oci, String type) {
	String t = oci.getProperty(COPropertiesType.IDENTIFIER, type);
	if (t != null && !t.equals(""))
	    return true;
	else
	    return false;
    }

    private String getIdentifierType(OsylCitationItem oci, String type) {
	String t = oci.getProperty(COPropertiesType.IDENTIFIER, type);
	if (t != null && !t.equals(""))
	    return t;
	else
	    return null;
    }

    public boolean hasLink(OsylCitationItem citationItem) {
	String identifier =
		citationItem.getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_NOLINK);

	if (identifier != null && !"".equalsIgnoreCase(identifier)
		&& !"undefined".equalsIgnoreCase(identifier))
	    return false;
	return true;
    }

    public void setViewerType(HTML viewerType) {
	this.viewerType = viewerType;
    }

    public HTML getViewerType() {
	return viewerType;
    }

}
