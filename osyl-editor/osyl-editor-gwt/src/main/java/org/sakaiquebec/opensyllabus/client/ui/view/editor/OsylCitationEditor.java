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
    private CheckBox useLibraryLinkCheckBox;
    private TextBox editorOtherLink;

    // Remove any link associated to this citation
    private CheckBox useNoLinkCheckBox;

    // Additional Widget;
    private CheckBox bookstoreCheckBox;

    private int originalEditorDescHeight;

    private DisclosurePanel metaInfoDiscPanel;

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
	HTML htmlViewer = new HTML();
	setViewerLink(htmlViewer);

	HTML htmlViewerDesc = new HTML();
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
		"Osyl-UnitView-IconLvlImportant");
	getViewerPanel().setWidget(1, 1, getViewer());

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

	    getViewerLink().setHTML(getView().getCitationPreviewAsLink());
	    // We get the text to display from the model
	    getViewerDesc().setHTML(getView().getCommentFromModel());

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
	String siteId = getView().getController().getSiteId();
	String resourcesPath = "group/" + siteId + "/";
	basePath =
		basePath == null ? resourcesPath
			+ getView().getController().getDocFolderName()
			: basePath;
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
	HorizontalPanel metaInfoPanel = new HorizontalPanel();
	metaInfoDiscPanel.add(metaInfoPanel);
	metaInfoPanel.setWidth("100%");
	browserPanel.add(metaInfoDiscPanel);

	VerticalPanel descriptionPanel = new VerticalPanel();
	metaInfoPanel.add(descriptionPanel);

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
	citationPreviewLabel.setHTML(selectedFile != null ? getView()
		.getCitationsInfosAsLink(selectedFile) : "");
	descriptionPanel.setWidth("98%");
	citationPreviewLabel.setWidth("100%");
	descriptionPanel.add(citationPreviewLabel);

	// Add option to remove the link to the library references

	HorizontalPanel changeLinkToLibraryPanel = new HorizontalPanel();
	descriptionPanel.add(changeLinkToLibraryPanel);
	changeLinkToLibraryPanel.setWidth("100%");

	useNoLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.NoLink.title"));
	useNoLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.NoLink.Name"));
	useNoLinkCheckBox.setWidth("60px");

	useLibraryLinkCheckBox =
		new CheckBox(getView().getUiMessage(
			"CitationEditor.LibraryLink.name"));
	useLibraryLinkCheckBox.setTitle(getView().getUiMessage(
		"CitationEditor.LibraryLink.title"));
	useLibraryLinkCheckBox.setWidth("120px");

	editorOtherLink = new TextBox();
	editorOtherLink.setWidth("320px");
	changeLinkToLibraryPanel.add(useNoLinkCheckBox);
	changeLinkToLibraryPanel.add(useLibraryLinkCheckBox);
	changeLinkToLibraryPanel.add(editorOtherLink);

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
		String otherLink;
		if (useNoLinkCheckBox.getValue()) {
		    selectedFile.setProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK, "noLink");

		} else {
		    selectedFile.removeProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK);

		    if (!useLibraryLinkCheckBox.getValue()) {
			selectedFile.removeProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URL);
		    } else {
			otherLink = editorOtherLink.getText();
			selectedFile.removeProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_LIBRARY);
			selectedFile
				.setProperty(COPropertiesType.IDENTIFIER,
					COPropertiesType.IDENTIFIER_TYPE_URL,
					otherLink);
		    }
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
					}
				    });
		}
		getBrowser().refreshCitationsInList();

		saveButton.setEnabled(false);

	    }
	});

	saveButton.setEnabled(false);
	if (selectedFile != null)
	    if (!getView().hasLink(selectedFile)) {
		useLibraryLinkCheckBox.setValue(false);
		editorOtherLink.setText("");
		editorOtherLink.setEnabled(false);
		useNoLinkCheckBox.setValue(true);
	    } else {
		if (getView().isCitationLinkLibrary(selectedFile)) {
		    useLibraryLinkCheckBox.setValue(false);
		    editorOtherLink.setText("");
		    editorOtherLink.setEnabled(false);
		    useNoLinkCheckBox.setValue(false);
		} else {
		    useLibraryLinkCheckBox.setValue(true);
		    editorOtherLink.setText(selectedFile.getUrl());
		    editorOtherLink.setEnabled(true);
		    useNoLinkCheckBox.setValue(false);
		}
	    }

	HorizontalPanel savePanel = new HorizontalPanel();
	savePanel.setWidth("100%");
	changeLinkToLibraryPanel.add(savePanel);
	savePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

	savePanel.add(saveButton);

	useNoLinkCheckBox.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent e) {
		saveButton.setEnabled(true);
		useLibraryLinkCheckBox.setValue(false);
		editorOtherLink.setEnabled(false);
		editorOtherLink.setText("");
	    }
	});

	useLibraryLinkCheckBox.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		final OsylCitationItem selectedFile =
			(OsylCitationItem) browser
				.getSelectedAbstractBrowserItem();

		useNoLinkCheckBox.setValue(false);

		// If the citation is of type unknow we tell the user he can not
		// change the url type
		if (selectedFile.getProperty(CitationSchema.TYPE)
			.equalsIgnoreCase(CitationSchema.TYPE_UNKNOWN)) {
		    Window
			    .alert(getUiMessage("CitationEditor.ChangeUrl.InvalidChange"));
		    useLibraryLinkCheckBox.setValue(true);
		    editorOtherLink.setText(selectedFile.getUrl());

		} else {
		    saveButton.setEnabled(true);
		    if (useLibraryLinkCheckBox.getValue()) {
			editorOtherLink.setEnabled(true);
		    } else {
			editorOtherLink.setText("");
			editorOtherLink.setEnabled(false);
		    }
		}
	    }
	});

	editorOtherLink.addKeyPressHandler(new KeyPressHandler() {

	    public void onKeyPress(KeyPressEvent event) {
		saveButton.setEnabled(true);
	    }
	});

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
	if (getView().getController().isInHostedMode()) {
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
		citationPreviewLabel.setHTML(getView().getCitationsInfosAsLink(
			selectedFile));
		editorOtherLink.setText(selectedFile.getUrl());

		if (selectedFile != null)
		    if (!getView().hasLink(selectedFile)) {
			useLibraryLinkCheckBox.setValue(false);
			editorOtherLink.setText("");
			editorOtherLink.setEnabled(false);
			useNoLinkCheckBox.setValue(true);
		    } else {
			if (getView().isCitationLinkLibrary(selectedFile)) {
			    useLibraryLinkCheckBox.setValue(false);
			    editorOtherLink.setText("");
			    editorOtherLink.setEnabled(false);
			    useNoLinkCheckBox.setValue(false);
			} else {
			    useLibraryLinkCheckBox.setValue(true);
			    editorOtherLink.setText(selectedFile.getUrl());
			    editorOtherLink.setEnabled(true);
			    useNoLinkCheckBox.setValue(false);
			}
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
}
