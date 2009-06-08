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
import org.sakaiquebec.opensyllabus.client.controller.OsylRPCController;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.base.Dimension;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylDisclosureListener;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylFileBrowser;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylFileItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylHostedModeFileBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylJSONRemoteDirectory;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxDocumentView;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DisclosurePanelImages;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
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
    private HTML viewer;
    // Our viewer (document name)
    private HTML viewerName;
    // Our viewer (description)
    private HTML viewerDesc;

    // Contains the viewer and info icons for the requirement level
    private HorizontalPanel viewerPanel;

    // Browser panel widgets
    // private Button saveButton;
    private ImageAndTextButton saveButton;
    private TextArea descriptionTextArea;
    private ListBox licenseListBox;
    private OsylFileBrowser fileBrowser;

    private ResourcesLicencingInfo resourceLicencingInfo;

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
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewers();
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

	Label l1 = new Label(getView().getUiMessage("clickable_text"));
	editorPanel.add(l1);
	editorLabel = new TextBox();
	editorLabel.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	editorLabel.setWidth("99%");
	editorPanel.add(editorLabel);

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
    private void initViewers() {
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
	if (isReadOnly()) {
	    getViewerName().setVisible(false);
	}

	setViewerPanel(new HorizontalPanel());

	if (isReadOnly()) {
	    if (getView().isContextImportant()) {
		htmlViewer
			.setStylePrimaryName("Osyl-UnitView-UnitLabel-Important");
	    }
	    if (COPropertiesType.REQ_LEVEL_MANDATORY.equals(getView()
		    .getRequirementLevel())) {
		Image mandatoryIcon =
			getView().getOsylImageBundle().iconeObl().createImage();
		getViewerPanel().add(mandatoryIcon);
	    }
	    if (getView().isContextHidden()) {
		mainPanel.setVisible(false);
	    } else {
		mainPanel.setVisible(true);
	    }
	}
	constructViewerLayout();
    }

    private void constructViewerLayout() {
	// Now we add our widgets with the following layout
	// ____________________________________________
	// | link to the doc | (document name) |
	// |--------------------------------------------|
	// | description |
	// |____________________________________________|
	//
	VerticalPanel vp = new VerticalPanel();
	getViewerPanel().add(vp);
	HorizontalPanel linkAndNameHP = new HorizontalPanel();
	vp.add(linkAndNameHP);
	linkAndNameHP.add(getViewer());
	linkAndNameHP.add(getViewerName());
	vp.add(getViewerDesc());
	mainPanel.add(getViewerPanel());
    }

    // Clears the viewerPanel and calls constructViewerLayout().
    private void reconstructViewerLayout() {
	getViewerPanel().clear();
	constructViewerLayout();
    }

    private void setViewer(HTML html) {
	this.viewer = html;
    }

    private HTML getViewer() {
	return this.viewer;
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

    private void setViewerPanel(HorizontalPanel viewerPanel) {
	this.viewerPanel = viewerPanel;
    }

    private HorizontalPanel getViewerPanel() {
	return viewerPanel;
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
	    viewer.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editorLabel.getText();
	} else {
	    return viewer.getHTML();
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
	return true;
    }

    public void enterEdit() {
	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	// We get the text to edit from the model
	editorLabel.setText(getView().getTextFromModel());
	// And put the cursor at the end
	editorLabel.setCursorPos(getText().length());
	// And we give the focus to the editor
	editorLabel.setFocus(true);

	// We get the description text to edit from the model
	editorDesc.setHTML(getView().getDescriptionFromModel());

	createEditBox(getEditBoxTitle());
	if (fileBrowser.getFileItemPathToSelect() == null) {
	    fileBrowser.setFileItemPathToSelect(getView().getDocPath());
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
	// If we don't reconstruct the viewer layout the new size of our HTML
	// components will not be effective until we mouse over...
	reconstructViewerLayout();
	getMainPanel().add(getViewerPanel());
	// We get the text to display from the model
	getViewer().setHTML(getView().getTextFromModel());
	getViewerName().setHTML("(" + getView().getDocName() + ")");
	getViewerDesc().setHTML(getView().getDescriptionFromModel());

	// If we are not in read-only mode, we display some meta-info and add
	// buttons and listeners enabling edition or deletion:
	if (!isReadOnly()) {
	    getMainPanel().add(getMetaInfoLabel());

	    addViewerStdButtons();
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

	if (getView().getController().isInHostedMode()) {
	    // HOSTED MODE
	    fileBrowser =
		    (OsylFileBrowser) new OsylHostedModeFileBrowserComposite(
			    getResourceDirectoryName(), getResourcesPath());
	} else {
	    // SAKAI MODE
	    String basePath = getView().getDocPath();
	    String siteId = getView().getController().getSiteId();
	    String resourcesPath = "group/" + siteId + "/";
	    basePath =
		    basePath == null ? resourcesPath
			    + getResourceDirectoryName() : basePath;
	    fileBrowser =
		    new OsylFileBrowser(basePath, getResourcesPath(), "",
			    getView().getDocPath() + "/"
				    + getView().getDocName());
	}

	fileBrowser.addEventHandler((RFBItemSelectionEventHandler) this);
	fileBrowser.addEventHandler((RFBAddFolderEventHandler) this);
	fileBrowser.addEventHandler((ItemListingAcquiredEventHandler) this);

	browserPanel.add(fileBrowser);
	fileBrowser.setWidth("100%");

	DisclosurePanelImages disclosureImages =
		(DisclosurePanelImages) GWT
			.create(OsylDisclosurePanelImageInterface.class);
	DisclosurePanel metaInfoDiscPanel =
		new DisclosurePanel(disclosureImages, getView().getUiMessage(
			"DocumentEditor.document.details"), false);
	metaInfoDiscPanel.setAnimationEnabled(true);
	metaInfoDiscPanel.setStylePrimaryName("DetailsPanel");
	metaInfoDiscPanel.setWidth("100%");
	HorizontalPanel metaInfoPanel = new HorizontalPanel();
	metaInfoDiscPanel.add(metaInfoPanel);
	metaInfoPanel.setWidth("100%");
	browserPanel.add(metaInfoDiscPanel);

	VerticalPanel descriptionPanel = new VerticalPanel();
	metaInfoPanel.add(descriptionPanel);

	descriptionPanel.add(new Label(getView().getUiMessage(
		"DocumentEditor.document.description")));

	metaInfoDiscPanel.addEventHandler(new OsylDisclosureListener(
		getEditorPopup()));

	OsylFileItem selectedFile = null;

	if (fileBrowser.getSelectedAbstractBrowserItem() != null
		&& !fileBrowser.getSelectedAbstractBrowserItem().isFolder()) {
	    selectedFile =
		    (OsylFileItem) fileBrowser.getSelectedAbstractBrowserItem();
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
	updateResourceLicenceInfo();

	rightsAndSavePanel.add(licenseListBox);
	licenseListBox.setWidth("100%");

	HorizontalPanel savePanel = new HorizontalPanel();
	savePanel.setWidth("100%");
	rightsAndSavePanel.add(savePanel);
	savePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

	AbstractImagePrototype imgSaveButton = getOsylImageBundle().save();
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

	saveButton.addClickListener(new ClickListener() {

	    public void onClick(Widget sender) {
		if (fileBrowser.getSelectedAbstractBrowserItem() != null
			&& !fileBrowser.getSelectedAbstractBrowserItem()
				.isFolder()) {
		    OsylFileItem selectedFile =
			    (OsylFileItem) fileBrowser
				    .getSelectedAbstractBrowserItem();

		    selectedFile.setDescription(descriptionTextArea.getText());
		    selectedFile.setCopyrightChoice(licenseListBox
			    .getItemText(licenseListBox.getSelectedIndex()));

		    String requestParams =
			    "f="
				    + URL.encode("pu")
				    + "&fp="
				    + URL.encode("CHEF:description")
				    + "&fv="
				    + URL.encode(selectedFile.getDescription())
				    + "&fp="
				    + URL.encode("CHEF:copyrightchoice")
				    + "&fv="
				    + URL.encode(selectedFile
					    .getCopyrightChoice());
		    String updatePath =
			    getResourcesPath()
				    + getBrowser().getBrowsedSiteId()
				    + "/"
				    + getBrowser().getCurrentDirectory()
					    .getDirectoryPath() + "/"
				    + selectedFile.getFileName();
		    OsylJSONRemoteDirectory.updateRemoteFileInfo(updatePath,
			    requestParams, fileUpdateRequestHandler);
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

	descriptionTextArea.addKeyboardListener(new KeyboardListenerAdapter() {

	    public void onKeyPress(Widget sender, char keyCode, int modifiers) {
		saveButton.setEnabled(true);
	    }
	});

	licenseListBox.addChangeListener(new ChangeListener() {

	    public void onChange(Widget sender) {
		saveButton.setEnabled(true);
	    }
	});
    }

    protected String getResourcesPath() {
	if (getView().getController().isInHostedMode()) {
	    return "";
	} else {
	    String uri = GWT.getModuleBaseURL();
	    String serverId = uri.split("\\s*/portal/tool/\\s*")[0];
	    String resourcesPath = serverId + "/sdata/c/group/";
	    resourcesPath =
		    OsylAbstractBrowserComposite
			    .uriSlashCorrection(resourcesPath);
	    return resourcesPath;
	}
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

    @Override
    protected OsylAbstractBrowserComposite getBrowser() {
	return fileBrowser;
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

    /**
     * update the Licence List the list is cached into the object
     * resourceLicencingInfo; TODO: make the property to be static or available
     * for every document editor when the operation is completed, the flag is
     * set to licenceListReady=true
     */
    private void updateResourceLicenceInfo() {
	// caching result
	if (resourceLicencingInfo == null) {
	    OsylRPCController.getInstance().getResourceLicenceInfo(
		    new AsyncCallback<ResourcesLicencingInfo>() {

			public void onFailure(Throwable caught) {
			    // TODO Auto-generated method stub
			    // getView().getController().handleRPCError("Error
			    // while retrieving license information object");
			}

			public void onSuccess(ResourcesLicencingInfo result) {
			    resourceLicencingInfo = result;
			    // TODO Auto-generated method stub
			    // getView().getController().handleRPCError("Sucess
			    // while retrieving license information object");
			    buildLicenseListBox();
			}
		    });
	}
	else{
	    buildLicenseListBox();
	}
    }
    
    private void buildLicenseListBox(){
	resourceLicencingInfo.getCopyrightTypeList();
	    licenseListBox.clear();
	    for (String licence : resourceLicencingInfo
		    .getCopyrightTypeList()) {
		licenseListBox.addItem(licence);
	    }
	    refreshBrowsingComponents();
    }

    /**
     * Refreshes the components of the document editor and the file browser on
     * some events.
     */
    protected void refreshBrowsingComponents() {

	// Called to refresh the file browser components
	fileBrowser.refreshBrowser();

	boolean isFound = false;

	if (fileBrowser.getSelectedAbstractBrowserItem() != null) {

	    if (fileBrowser.getSelectedAbstractBrowserItem().isFolder()) {
		descriptionTextArea.setText("");
		licenseListBox.setSelectedIndex(-1);
	    } else {
		OsylFileItem selectedFile =
			(OsylFileItem) fileBrowser
				.getSelectedAbstractBrowserItem();
		descriptionTextArea.setText(selectedFile.getDescription());

		// TODO We have to wait until licenceListReady is set to true
		// before
		// selecting an item in the licenceListBox
		// The licence list is scrolled only if it is ready.
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
	    licenseListBox.setSelectedIndex(-1);
	    descriptionTextArea.setText("");
	}
    }

    /**
     * Inner anonymous class that represent the response callback when updating
     * file properties via sdata.
     */
    private RequestCallback fileUpdateRequestHandler = new RequestCallback() {

	public void onError(Request request, Throwable exception) {
	    OsylUnobtrusiveAlert failure =
		    new OsylUnobtrusiveAlert(getView().getUiMessage(
			    "Global.error")
			    + ": "
			    + getView().getUiMessage(
				    "DocumentEditor.document.PropUpdateError"));
	    OsylEditorEntryPoint.showWidgetOnTop(failure);
	}

	public void onResponseReceived(Request request, Response response) {
	    OsylUnobtrusiveAlert success =
		    new OsylUnobtrusiveAlert(getView().getUiMessage(
			    "DocumentEditor.document.PropUpdateSuccess"));
	    OsylEditorEntryPoint.showWidgetOnTop(success);
	    saveButton.setEnabled(false);
	}
    };
}
