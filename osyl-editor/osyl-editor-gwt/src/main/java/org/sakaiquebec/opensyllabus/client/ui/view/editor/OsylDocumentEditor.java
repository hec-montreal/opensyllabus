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
import java.util.Set;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.OsylRichTextArea;
import org.sakaiquebec.opensyllabus.client.ui.base.Dimension;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylDisclosureListener;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylLinkClickListener;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylFileBrowser;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxDocumentView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylFileItem;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Document editor to be used within {@link OsylAbstractView}. The edition mode
 * uses a Rich-text editor and the view mode displays a clickable link.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylDocumentEditor extends OsylAbstractBrowserEditor {

    // Our main panel which will display the viewer and the meta-info
    private VerticalPanel mainPanel;

    // Our editor widgets
    private TextBox editorLabel;
    private RichTextArea editorDesc;
    private VerticalPanel editorPanel;

    // Our viewer (link to the document)
    private VerticalPanel viewer;
    private HTML viewerLink;
    // Our viewer (document name)
    private HTML viewerName;
    // Our viewer (description)
    private HTML viewerDesc;

    // Contains the viewer and info icons for the requirement level
    private FlexTable viewerPanel;

    // Browser panel widgets
    // private Button saveButton;
    private ImageAndTextButton saveButton;
    private TextArea descriptionTextArea;
    private ListBox licenseListBox;

    private ResourcesLicencingInfo resourceLicensingInfo;

    private int originalEditorDescHeight;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylDocumentEditor(OsylAbstractView parent) {
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

	HTML l1 =
		new HTML(getView().getUiMessage("clickable_text")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	editorPanel.add(l1);
	editorLabel = new TextBox();
	editorLabel.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	editorLabel.addClickHandler(new ResetLabelClickListener(getView()
		.getCoMessage("InsertYourDocumentLabelHere")));
	editorPanel.add(editorLabel);

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
	HTML htmlViewer = new HTML();
	setViewer(htmlViewer);

	HTML htmlViewerDesc = new HTML();
	htmlViewerDesc.setStylePrimaryName("description");
	htmlViewerDesc.setTitle(getView().getUiMessage(
		"DocumentEditor.document.description"));
	setViewerDesc(htmlViewerDesc);

	HTML htmlViewerName = new HTML();
	htmlViewerName.setStylePrimaryName("name");
	setViewerName(htmlViewerName);

	setViewer(new VerticalPanel());
	getViewer().setStylePrimaryName("Osyl-UnitView-HtmlViewer");
	HorizontalPanel linkAndNameHP = new HorizontalPanel();
	linkAndNameHP.add(getViewerLink());
	linkAndNameHP.add(getViewerName());
	getViewer().add(linkAndNameHP);
	getViewer().add(getViewerDesc());

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
	
	if(getView().isNewAccordingSelectedDate()){
	    getViewerPanel().addStyleName("Osyl-UnitView-New");
	}
	
	getViewerPanel().setWidget(column, 1, getViewer());
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(column, 1,
		"Osyl-UnitView-Content");
	getMainPanel().add(getViewerPanel());

    }

    private void setViewer(HTML html) {
	this.viewerLink = html;
    }

    private HTML getViewerLink() {
	return this.viewerLink;
    }

    private HTML getViewerName() {
	return viewerName;
    }

    private void setViewerName(HTML viewerName) {
	this.viewerName = viewerName;
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

    protected OsylResProxDocumentView getView() {
	return (OsylResProxDocumentView) super.getView();
    }

    public Dimension getPreferredSize() {
	return new Dimension(400, 100);
    }

    public void setText(String text) {
	if (isInEditionMode()) {
	    editorLabel.setText(text);
	} else {
	    viewerLink.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editorLabel.getText();
	} else {
	    return viewerLink.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editorLabel.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editorPanel;
    }

    public boolean prepareForSave() {
	String message = "";
	if (getResourceURI() == null) {
	    message +=
		    getUiMessage("DocumentEditor.save.error.documentUndefined");
	} else {
	    if (editorLabel.getText().trim().equals("")) {
		message +=
			getView().getUiMessage("Global.field.required",
				getUiMessage("clickable_text"))
				+ "\n";
	    }
	    if (saveButton.isEnabled()) {
		message +=
			getUiMessage("DocumentEditor.document.PropUpdateSave");
	    } else if (getLicence().equals(licenseListBox.getItemText(0))) {
		message +=
			getUiMessage("DocumentEditor.document.WrongRightsStatus");
	    }
	    Map<String, String> cv =
		    OsylEditorEntryPoint.getInstance()
			    .getDocumentContextVisibilityMap().get(
				    getResourceURI());
	    boolean incompatibility = false;
	    Set<String> parentTitles = new HashSet<String>();
	    if (cv != null) {
		for (Entry<String, String> entry : cv.entrySet()) {
		    String id = entry.getKey();
		    if (!id.equals(getView().getModel().getId())) {
			String visibility = entry.getValue();
			if (!visibility.equals("" + !isContextHidden())) {
			    incompatibility = true;
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
	    if (incompatibility) {
		StringBuilder sb = new StringBuilder();
		for (String s : parentTitles) {
		    sb.append(s + ", 0");
		}
		String msgParameter = sb.substring(0, sb.length() - 2);
		message +=
			getView()
				.getUiMessage(
					"DocumentEditor.document.visibilityIncompatibility",
					msgParameter);
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
	// We get the text to edit from the model
	editorLabel.setText(getView().getTextFromModel());
	// And put the cursor at the end
	editorLabel.setCursorPos(getText().length());
	// And we give the focus to the editor
	editorLabel.setFocus(true);

	// We get the description text to edit from the model
	editorDesc.setHTML(getView().getCommentFromModel());

	createEditBox(getEditBoxTitle());
	if (getBrowser().getItemToSelect() == null) {
	    getBrowser().setItemToSelect(
		    new OsylFileItem(getView().getDocPath()));
	}
	// refreshComponents();
	saveButton.setEnabled(false);
    } // enterEdit

    private String getEditBoxTitle() {
	if (getView().isDocumentDefined()) {
	    return getView().getUiMessage("DocumentEditor.title.edit");
	} else {
	    return getView().getUiMessage("DocumentEditor.title.add");
	}
    }

    public void enterView() {

	// We keep track that we are now in view-mode
	setInEditionMode(false);
	getMainPanel().clear();
	if (!(isReadOnly() && getView().isContextHidden())) {
	    initViewer();
	    // TODO Hack to remove bad links to resources inserted in the XML.
	    // Remove this
	    // as soon as the cause is found and corrected.

	    // We get the text to display from the model
	    getModel().getProperties().addProperty(COPropertiesType.LABEL,
		    getView().validateLinkLabel(getModel().getLabel()));
	    getViewerLink().setHTML(getView().getTextFromSdataModel());
	    getViewerLink().addClickHandler(
		    new OsylLinkClickListener(getView(), getView()
			    .getTextFromSdataModel()));
	    getViewerName().setHTML("(" + getView().getDocName() + ")");
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
    public Widget[] getAdditionalOptionWidgets() {
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
				"DocumentEditor.document.selection"));
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
		new OsylFileBrowser(basePath, getView().getDocPath() + "/"
			+ getView().getDocName());

	browser.addEventHandler((RFBItemSelectionEventHandler) this);
	browser.addEventHandler((RFBAddFolderEventHandler) this);
	browser.addEventHandler((ItemListingAcquiredEventHandler) this);

	browserPanel.add(browser);
	browser.setWidth("100%");

	DisclosurePanel metaInfoDiscPanel =
		new DisclosurePanel(getOsylImageBundle().expand(),
			getOsylImageBundle().collapse(),
			getView().getUiMessage(
				"DocumentEditor.document.details"));
	metaInfoDiscPanel.setAnimationEnabled(true);
	metaInfoDiscPanel.setStylePrimaryName("DetailsPanel");
	HorizontalPanel metaInfoPanel = new HorizontalPanel();
	metaInfoDiscPanel.add(metaInfoPanel);
	metaInfoPanel.setWidth("100%");
	browserPanel.add(metaInfoDiscPanel);

	VerticalPanel descriptionPanel = new VerticalPanel();
	metaInfoPanel.add(descriptionPanel);

	descriptionPanel.add(new Label(getView().getUiMessage(
		"DocumentEditor.document.description")));

	OsylDisclosureListener odl =
		new OsylDisclosureListener(getEditorPopup());
	metaInfoDiscPanel.addCloseHandler(odl);
	metaInfoDiscPanel.addOpenHandler(odl);

	OsylFileItem selectedFile = null;

	if (browser.getSelectedAbstractBrowserItem() != null
		&& !browser.getSelectedAbstractBrowserItem().isFolder()) {
	    selectedFile =
		    (OsylFileItem) browser.getSelectedAbstractBrowserItem();
	}
	descriptionTextArea = new TextArea();
	descriptionTextArea.setText(selectedFile != null ? selectedFile
		.getDescription() : "");
	descriptionPanel.setWidth("98%");
	descriptionTextArea.setWidth("100%");
	descriptionPanel.add(descriptionTextArea);

	VerticalPanel rightsAndSavePanel = new VerticalPanel();
	rightsAndSavePanel.setWidth("98%");
	metaInfoPanel.add(rightsAndSavePanel);

	rightsAndSavePanel.add(new Label(getView().getUiMessage(
		"DocumentEditor.document.license")));
	licenseListBox = new ListBox();

	rightsAndSavePanel.add(licenseListBox);
	licenseListBox.setWidth("100%");

	HorizontalPanel savePanel = new HorizontalPanel();
	savePanel.setWidth("100%");
	rightsAndSavePanel.add(savePanel);
	savePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

	AbstractImagePrototype imgSaveButton =
		AbstractImagePrototype.create(getOsylImageBundle().save());
	saveButton =
		new ImageAndTextButton(
		// TODO: Bug with ImageBundle, we have to use
			// AbstractImagePrototype
			imgSaveButton, getView().getUiMessage(
				"DocumentEditor.save.name"));
	saveButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	saveButton
		.setTitle(getView().getUiMessage("DocumentEditor.save.title"));
	savePanel.add(saveButton);

	saveButton.setEnabled(false);

	saveButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		saveButton.setEnabled(false);

		if (browser.getSelectedAbstractBrowserItem() != null
			&& !browser.getSelectedAbstractBrowserItem().isFolder()) {
		    OsylFileItem selectedFile =
			    (OsylFileItem) browser
				    .getSelectedAbstractBrowserItem();

		    selectedFile.setDescription(descriptionTextArea.getText());
		    selectedFile.setCopyrightChoice(licenseListBox
			    .getItemText(licenseListBox.getSelectedIndex()));

		    OsylRemoteServiceLocator
			    .getDirectoryRemoteService()
			    .updateRemoteFileInfo(
				    selectedFile.getFilePath(),
				    selectedFile.getDescription(),
				    selectedFile.getCopyrightChoice(),
				    OsylDocumentEditor.this.fileUpdateRequestHandler);

		}

	    }
	});

	// Note: we prepend the message with a blank space instead of using
	// padding in the CSS because we use 100% width and adding some padding
	// causes the label to be larger than the popup in Firefox (very ugly).
	HTML label2 =
		new HTML("&nbsp;"
			+ getView().getUiMessage("DocumentEditor.context"));
	label2.setStylePrimaryName("sectionLabel");
	browserPanel.add(label2);

	descriptionTextArea.addKeyPressHandler(new KeyPressHandler() {

	    public void onKeyPress(KeyPressEvent event) {
		saveButton.setEnabled(true);
	    }
	});

	licenseListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		if (licenseListBox.getSelectedIndex() > 0) {
		    saveButton.setEnabled(true);
		} else {
		    saveButton.setEnabled(false);
		}
	    }
	});
	updateResourceLicenseInfo();
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(editorLabel);
	focusWidgetList.add(editorDesc);
	return focusWidgetList;
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

    protected OsylFileBrowser getBrowser() {
	return (OsylFileBrowser) browser;
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    public String getDescription() {
	if (isInEditionMode()) {
	    return editorDesc.getHTML();
	} else {
	    return viewerDesc.getHTML();
	}
    }

    public String getLicence() {
	if (licenseListBox.getSelectedIndex() != -1)
	    return licenseListBox
		    .getItemText(licenseListBox.getSelectedIndex());
	else
	    return "";
    }

    public String getResourceDescription() {
	return descriptionTextArea.getText();
    }

    /**
     * update the Licence List the list is cached into the object
     * resourceLicencingInfo; TODO: make the property to be static or available
     * for every document editor when the operation is completed, the flag is
     * set to licenceListReady=true
     */
    private void updateResourceLicenseInfo() {
	// caching result
	if (resourceLicensingInfo == null) {
	    OsylRemoteServiceLocator.getEditorRemoteService()
		    .getResourceLicenceInfo(
			    new AsyncCallback<ResourcesLicencingInfo>() {

				public void onFailure(Throwable caught) {
				    // TODO Auto-generated method stub
				    // getController().handleRPCError("Error
				    // while retrieving license information
				    // object");
				}

				public void onSuccess(
					ResourcesLicencingInfo result) {
				    resourceLicensingInfo = result;

				    List<String> rightsList =
					    new ArrayList<String>();

				    if (OsylController.getInstance()
					    .isInHostedMode()) {
					rightsList =
						resourceLicensingInfo
							.getCopyrightTypeList();
				    } else {
					for (String rightKey : resourceLicensingInfo
						.getCopyrightTypeList()) {
					    rightsList.add(getView()
						    .getUiMessage(rightKey));
					}
				    }
				    ((OsylFileBrowser) browser)
					    .setRightsList(rightsList);
				    // TODO Auto-generated method stub
				    // getController().handleRPCError("Sucess
				    // while retrieving license information
				    // object");
				    buildLicenseListBox(rightsList);
				}
			    });
	} else {
	    List<String> rightsList = new ArrayList<String>();

	    if (OsylController.getInstance().isInHostedMode()) {
		rightsList = resourceLicensingInfo.getCopyrightTypeList();
	    } else {
		for (String rightKey : resourceLicensingInfo
			.getCopyrightTypeList()) {
		    rightsList.add(getView().getUiMessage(rightKey));
		}
	    }
	    ((OsylFileBrowser) browser).setRightsList(rightsList);
	    buildLicenseListBox(rightsList);
	}
    }

    private void buildLicenseListBox(List<String> rightsList) {
	resourceLicensingInfo.getCopyrightTypeList();
	licenseListBox.clear();
	for (String right : rightsList) {
	    licenseListBox.addItem(right);
	}
	refreshBrowsingComponents();
    }

    public ImageAndTextButton getSaveButton() {
	return saveButton;
    }

    /**
     * Refreshes the components of the document editor and the file browser on
     * some events.
     */
    public void refreshBrowsingComponents() {

	// Called to refresh the file browser components
	browser.refreshBrowser();

	boolean isFound = false;
	if (browser.getSelectedAbstractBrowserItem() != null) {
	    if (browser.getSelectedAbstractBrowserItem().isFolder()) {
		descriptionTextArea.setText("");
		licenseListBox.setSelectedIndex(-1);
	    } else {
		OsylFileItem selectedFile =
			(OsylFileItem) browser.getSelectedAbstractBrowserItem();
		descriptionTextArea.setText(selectedFile.getDescription());

		for (int i = 0; i < licenseListBox.getItemCount(); i++) {
		    String item = licenseListBox.getValue(i);

		    if (item.equals(selectedFile.getCopyrightChoice())) {
			licenseListBox.setItemSelected(i, true);
			isFound = true;
		    }
		}
	    }

	}
	if (!isFound) {
	    licenseListBox.setSelectedIndex(0);
	    descriptionTextArea.setText("");
	}
	saveButton.setEnabled(false);
    }

    /**
     * Inner anonymous class that represent the response callback when updating
     * file properties via sdata.
     */
    private final AsyncCallback<Void> fileUpdateRequestHandler =
	    new AsyncCallback<Void>() {
		public void onFailure(Throwable caught) {
		    OsylUnobtrusiveAlert failure =
			    new OsylUnobtrusiveAlert(
				    getView().getUiMessage("Global.error")
					    + ": "
					    + getView()
						    .getUiMessage(
							    "DocumentEditor.document.PropUpdateError"));
		    OsylEditorEntryPoint.showWidgetOnTop(failure);
		}

		public void onSuccess(Void result) {
		    OsylUnobtrusiveAlert success =
			    new OsylUnobtrusiveAlert(
				    getView()
					    .getUiMessage(
						    "DocumentEditor.document.PropUpdateSuccess"));
		    OsylEditorEntryPoint.showWidgetOnTop(success);
		    OsylDocumentEditor.this.saveButton.setEnabled(false);
		}
	    };

}
