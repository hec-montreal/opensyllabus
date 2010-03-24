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
import java.util.List;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylDisclosurePanelImageInterface;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.base.Dimension;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylWindowPanel;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylDisclosureListener;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationBrowser;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxCitationView;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.util.LinkValidator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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

    // Remove any link associated to this citation
    private CheckBox disableLibraryLinkCheckBox;
    private HTML libraryLink;

    // Additional Widget;
    private CheckBox bookstoreCheckBox;

    private int originalEditorDescHeight;

    private DisclosurePanel metaInfoDiscPanel;

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
	editorDesc = new RichTextArea();
	editorDesc.setWidth("99%");
	editorDesc.setHeight("80px");
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

	HTML htmlViewerName = new HTML();
	htmlViewerName.setStylePrimaryName("name");

	setViewer(new VerticalPanel());
	getViewer().setStylePrimaryName("Osyl-UnitView-HtmlViewer");
	getViewer().add(getViewerLink());
	getViewer().add(getViewerDesc());

	if (getView().getCitationLibraryLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel.setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-libraryImage");
	    libLinkPanel.add(h);
	    libLinkPanel
		    .add(new HTML(
			    getView()
				    .generateHTMLLink(
					    getView().getCitationLibraryLink(),
					    getUiMessage("ResProxCitationView.libraryLink.available"))));
	    getViewer().add(libLinkPanel);
	}

	if (getView().getCitationBookstoreLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel.setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-bookstoreImage");
	    libLinkPanel.add(h);
	    libLinkPanel
		    .add(new HTML(
			    getView()
				    .generateHTMLLink(
					    getView()
						    .getCitationBookstoreLink(),
					    getUiMessage("ResProxCitationView.bookstoreLink.available"))));
	    getViewer().add(libLinkPanel);
	}

	if (getView().getCitationOtherLink() != null) {
	    HorizontalPanel libLinkPanel = new HorizontalPanel();
	    libLinkPanel.setStylePrimaryName("Osyl-ResProxCitationView-linkPanel");
	    HTML h = new HTML();
	    h.setStylePrimaryName("Osyl-ResProxCitationView-otherImage");
	    libLinkPanel.add(h);
	    libLinkPanel.add(new HTML(getView().generateHTMLLink(
		    getView().getCitationOtherLink(),
		    getUiMessage("ResProxCitationView.otherLink.available"))));
	    getViewer().add(libLinkPanel);
	}

	setViewerPanel(new FlexTable());
	getViewerPanel().setStylePrimaryName("Osyl-UnitView-HtmlViewer");

	if (getView().isContextImportant()) {
	    getViewerPanel().addStyleName("Osyl-UnitView-Important");
	}

	Image reqLevelIcon = getCurrentRequirementLevelIcon();
	if (null != reqLevelIcon) {
	    getViewerPanel().addStyleName("Osyl-UnitView-LvlReq");
	}

	getViewerPanel().setWidget(0, 0, reqLevelIcon);
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(0, 0,
		"Osyl-UnitView-IconLvlReq");

	getViewerPanel().setWidget(0, 1,
		new HTML(getLocalizedRequirementLevel()));
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(0, 1,
		"Osyl-UnitView-TextLvlReq");

	getViewerPanel().setWidget(1, 0, getImportantIcon());
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(1, 0,
		"Osyl-UnitView-IconImportant");
	getViewerPanel().setWidget(1, 1, getViewer());
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(1, 1,
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
	if (getResourceURI() == null) {
	    message +=
		    getUiMessage("CitationEditor.save.error.citationUndefined");
	} else if (saveButton.isEnabled()) {
	    message += getUiMessage("CitationEditor.ChangeUrl.Save");
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
	String resourcesPath = "group/" + siteId + "/";
	basePath =
		basePath == null ? resourcesPath
			+ getController().getDocFolderName() : basePath;
	browser =
		new OsylCitationBrowser(basePath, getView().getCitationId(),
			getView().getDocPath() + "/" + getView().getDocName());

	browser.addEventHandler((RFBItemSelectionEventHandler) this);
	browser.addEventHandler((RFBAddFolderEventHandler) this);
	browser.addEventHandler((ItemListingAcquiredEventHandler) this);

	browserPanel.add(browser);
	browser.setWidth("100%");

	DisclosurePanelImages disclosureImages =
		(DisclosurePanelImages) GWT
			.create(OsylDisclosurePanelImageInterface.class);
	metaInfoDiscPanel =
		new DisclosurePanel(disclosureImages, getView().getUiMessage(
			"CitationEditor.document.details"), false);
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
	VerticalPanel linksPanel = new VerticalPanel();
	metaInfoPanel.add(linksPanel);
	linksPanel.setWidth("100%");

	// library link
	HorizontalPanel libraryPanel = new HorizontalPanel();
	HTML iLibrary = new HTML();
	iLibrary.setStylePrimaryName("Osyl-ResProxCitationView-libraryImage");
	libraryPanel.add(iLibrary);
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

		// If the citation is of type unknow we tell the user he can not
		// change the url type
		if (selectedFile.getProperty(CitationSchema.TYPE)
			.equalsIgnoreCase(CitationSchema.TYPE_UNKNOWN)) {
		    Window
			    .alert(getUiMessage("CitationEditor.ChangeUrl.InvalidChange"));
		    disableLibraryLinkCheckBox.setValue(true);

		} else {
		    saveButton.setEnabled(true);
		}

	    }
	});
	libraryPanel.add(disableLibraryLinkCheckBox);
	libraryLink = new HTML(getUiMessage("ResProxCitationView.link.label"));
	libraryPanel.add(libraryLink);
	linksPanel.add(libraryPanel);

	// bookstore link
	HorizontalPanel bookStorePanel = new HorizontalPanel();
	HTML iBookstrore = new HTML();
	iBookstrore
		.setStylePrimaryName("Osyl-ResProxCitationView-bookstoreImage");
	bookStorePanel.add(iBookstrore);
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

	bookStorePanel.add(disableBookstoreLinkCheckBox);

	bookStoreLink = new TextBox();
	bookStoreLink
		.setStylePrimaryName("Osyl-ResProxCitationView-linkTextbox");
	bookStorePanel.add(bookStoreLink);

	linksPanel.add(bookStorePanel);

	// other link
	HorizontalPanel otherPanel = new HorizontalPanel();
	HTML iOther = new HTML();
	iOther.setStylePrimaryName("Osyl-ResProxCitationView-otherImage");
	otherPanel.add(iOther);
	disableOtherLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.disableOtherLink.title"));
	disableOtherLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.disableOtherLink.title"));
	disableOtherLinkCheckBox
		.setStylePrimaryName("Osyl-ResProxCitationView-linkCheckbox");
	disableOtherLinkCheckBox.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		editorOtherLink
			.setEnabled(!disableOtherLinkCheckBox.getValue());
		saveButton.setEnabled(true);

	    }
	});
	otherPanel.add(disableOtherLinkCheckBox);

	editorOtherLink = new TextBox();
	editorOtherLink
		.setStylePrimaryName("Osyl-ResProxCitationView-linkTextbox");
	otherPanel.add(editorOtherLink);

	linksPanel.add(otherPanel);

	AbstractImagePrototype imgSaveButton = getOsylImageBundle().save();
	saveButton =
		new ImageAndTextButton(imgSaveButton, getView().getUiMessage(
			"DocumentEditor.save.name"));
	saveButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	saveButton
		.setTitle(getView().getUiMessage("DocumentEditor.save.title"));

	saveButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		final OsylCitationItem selectedFile =
			(OsylCitationItem) browser
				.getSelectedAbstractBrowserItem();
		if (disableLibraryLinkCheckBox.getValue()) {
		    selectedFile.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK, "noLink");
		} else {
		    selectedFile.removeProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK);
		}
		if (disableBookstoreLinkCheckBox.getValue()) {
		    selectedFile.removeProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE);
		} else {
		    bookStoreLink.setText(LinkValidator.parseLink(bookStoreLink
			    .getText()));
		    selectedFile.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE,
			    bookStoreLink.getText());
		}
		if (disableOtherLinkCheckBox.getValue()) {
		    selectedFile.removeProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK);
		} else {
		    editorOtherLink.setText(LinkValidator
			    .parseLink(editorOtherLink.getText()));
		    selectedFile.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK,
			    editorOtherLink.getText());
		}

		OsylRemoteServiceLocator.getCitationRemoteService()
			.createOrUpdateCitation(
				getBrowser().getCurrentDirectory()
					.getDirectoryPath(), selectedFile,
				new AsyncCallback<String>() {
				    public void onFailure(Throwable caught) {
					OsylUnobtrusiveAlert failure =
						new OsylUnobtrusiveAlert(
							getView().getUiMessage(
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
					getBrowser().refreshCitationsInList();
				    }
				});

		saveButton.setEnabled(false);

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
	// Bookstore
	bookstoreCheckBox = new CheckBox(getUiMessage("MetaInfo.bookstore"));
	bookstoreCheckBox.setValue(getView().isAvailableInBookstore());
	bookstoreCheckBox.setTitle(getUiMessage("MetaInfo.bookstore.title"));

	Widget[] additional = { bookstoreCheckBox };

	return additional;
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
	String bookstore =
		(getView().isAvailableInBookstore() ? getUiMessage("Global.yes")
			: getUiMessage("Global.no"));

	String label = metaInfoLabel.getText();

	label =
		label + " | " + getUiMessage("MetaInfo.bookstore") + ": "
			+ bookstore;

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
     * Returns whether the checkBox "AvailableInBookstore" is checked.
     * 
     * @return boolean
     */
    public boolean isAvailableInBookstore() {
	return bookstoreCheckBox.getValue();
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
	    if (browser.getSelectedAbstractBrowserItem().isFolder()) {
		citationPreviewLabel.setHTML("");
	    } else {
		OsylCitationItem selectedFile =
			(OsylCitationItem) browser
				.getSelectedAbstractBrowserItem();
		citationPreviewLabel.setHTML(selectedFile.getCitationsInfos());

		disableLibraryLinkCheckBox.setValue(!hasLink(selectedFile));
		libraryLink.setHTML(getView().generateHTMLLink(
			getView().getCitationLibraryLink(),
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
		if (hasIdentifierType(selectedFile,
			COPropertiesType.IDENTIFIER_TYPE_OTHERLINK)) {
		    disableOtherLinkCheckBox.setValue(false);
		    editorOtherLink.setEnabled(true);
		    editorOtherLink.setText(getIdentifierType(selectedFile,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK));
		} else {
		    disableOtherLinkCheckBox.setValue(true);
		    editorOtherLink.setText("");
		    editorOtherLink.setEnabled(false);
		}

		isFound = true;
	    }
	}
	if (!isFound) {
	    citationPreviewLabel.setHTML("");
	}
	if (metaInfoDiscPanel.isOpen()) {
	    OsylWindowPanel popup = getEditorPopup();
	    popup.updateLayout(popup.getContentWidth(), popup
		    .getContentHeight()
		    - previousCitationPreviewLabelHeight
		    + citationPreviewLabel.getOffsetHeight());
	}
    }

    public String getCitationInfos() {
	return citationPreviewLabel.getHTML();
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

}
