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
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.OsylRichTextArea;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylEntityBrowser;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxEntityView;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
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
 */
public class OsylEntityEditor extends OsylAbstractBrowserEditor {

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

    private int originalEditorDescHeight;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylEntityEditor(OsylAbstractView parent) {
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

    public String getResourceDescription() {
	return null;
    }

    public String getLicence() {
	return null;
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
		.getCoMessage("InsertYourSakaiEntityLabelHere")));
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
		"EntityEditor.entity.description"));
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
	    getViewerPanel().addStyleName("Osyl-newElement");
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

    public String getEntityProvider() {
	return ((OsylEntityBrowser) browser).getEntityType();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected OsylResProxEntityView getView() {
	return (OsylResProxEntityView) super.getView();
    }

    public void setText(String text) {
	if (isInEditionMode()) {
	    editorLabel.setText(text);
	} else {
	    viewerLink.setHTML(text);
	}
    }

    public String getResourceURI() {
	return ((OsylEntityBrowser) browser).getEntityUri();
    }

    public String getLastModifiedDateString() {
	return null;
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editorLabel.getText();
	} else {
	    return ((OsylEntityBrowser) browser).getEntityText();
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
	    message += getUiMessage("EntityEditor.save.error.entityUndefined");
	} else {
	    if (editorLabel.getText().trim().equals("")) {
		message +=
			getView().getUiMessage("Global.field.required",
				getUiMessage("clickable_text"))
				+ "\n";
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

	createEditBox();
    } // enterEdit

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
	    getViewerLink().setHTML(getView().getTextFromModel());
	    getViewerName().setHTML("(" + getView().getRawURI() + ")");
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
				"SakaiEntityEditor.document.selection"));
	label1.setStylePrimaryName("sectionLabel");
	browserPanel.add(label1);

	// SAKAI MODE

	browser = new OsylEntityBrowser(getView().getLinkURI());

	browser.addEventHandler((RFBItemSelectionEventHandler) this);
	browser.addEventHandler((RFBAddFolderEventHandler) this);
	browser.addEventHandler((ItemListingAcquiredEventHandler) this);

	browserPanel.add(browser);
	browser.setWidth("100%");

	HorizontalPanel metaInfoPanel = new HorizontalPanel();
	metaInfoPanel.setWidth("100%");

	// Note: we prepend the message with a blank space instead of using
	// padding in the CSS because we use 100% width and adding some padding
	// causes the label to be larger than the popup in Firefox (very ugly).
	HTML label2 =
		new HTML("&nbsp;"
			+ getView().getUiMessage("EntityEditor.context"));
	label2.setStylePrimaryName("sectionLabel");
	browserPanel.add(label2);

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

    protected OsylEntityBrowser getBrowser() {
	return (OsylEntityBrowser) browser;
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
     * Refreshes the components of the document editor and the file browser on
     * some events.
     */
    public void refreshBrowsingComponents() {
    }

}
