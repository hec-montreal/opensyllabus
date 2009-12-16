/*******************************************************************************
 * $Id: $
 *******************************************************************************
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxLinkView;
import org.sakaiquebec.opensyllabus.shared.util.LinkValidator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Link editor to be used within {@link OsylAbstractView}. The edition mode uses
 * a Rich-text editor and the view mode displays a clickable link.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylLinkEditor extends OsylAbstractResProxEditor {

    // Our main panel which will display the viewer and the meta-info
    private VerticalPanel mainPanel;

    // Our editor
    private VerticalPanel editor;
    // private FlexTable editor;
    private TextBox editorName;
    private TextBox editorLink;
    private RichTextArea editorDesc;

    // Our viewers
    private VerticalPanel viewer;
    private HTML viewerLink;
    private HTML viewerURI;
    private HTML viewerDesc;

    // Contains the viewer and info icons for the requirement level
    private FlexTable viewerPanel;

    // remember editor description height for maximizing popup
    private int originalEditorDescHeight;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylLinkEditor(OsylAbstractView parent) {
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
	getMainPanel().setStylePrimaryName("Osyl-ResProxLink");
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
	VerticalPanel editorPanel = new VerticalPanel();

	final FlexTable flexTable = new FlexTable();
	flexTable.getFlexCellFormatter().setWidth(0, 0, "15%");
	flexTable.getFlexCellFormatter().setWidth(0, 1, "85%");

	editorName = new TextBox();
	editorName.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	editorName.addClickHandler(new ResetLabelClickListener(getView()
		.getCoMessage("InsertYourTextHere")));
	flexTable.setWidget(0, 0, new HTML(getUiMessage("Link.label")
		+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + " : "));
	flexTable.setWidget(0, 1, editorName);

	editorLink = new TextBox();
	editorLink.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	editorLink.addClickHandler(new ResetLabelClickListener(
		"http://www.google.ca/search?q=opensyllabus"));
	flexTable.setWidget(1, 0, new HTML(getUiMessage("Link.url")
		+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + " : "));
	flexTable.setWidget(1, 1, editorLink);

	flexTable.setWidth("100%");
	editorPanel.add(flexTable);

	Label label = new Label(getView().getUiMessage("comment"));
	editorPanel.add(label);
	editorDesc = new RichTextArea();
	editorDesc.setWidth("99%");
	editorDesc.setHeight("120px");
	editorPanel.add(editorDesc);

	setEditor(editorPanel);
    }

    private void setEditor(VerticalPanel vp) {
	this.editor = vp;
    }

    /**
     * Creates and set the low-level viewer (HTML panel).
     */
    private void initViewer() {
	HTML htmlLinkViewer = new HTML();
	htmlLinkViewer.setStylePrimaryName("link");
	setViewerLink(htmlLinkViewer);

	HTML htmlViewerDesc = new HTML();
	htmlViewerDesc.setStylePrimaryName("description");
	htmlViewerDesc.setTitle(getView().getUiMessage("Link.description"));
	setViewerDesc(htmlViewerDesc);

	HTML htmlViewerURI = new HTML();
	htmlViewerURI.setStylePrimaryName("uri");
	setViewerURI(htmlViewerURI);
	if (isReadOnly()) {
	    getViewerURI().setVisible(false);
	}

	setViewer(new VerticalPanel());
	getViewer().setStylePrimaryName("Osyl-UnitView-HtmlViewer");
	getViewer().add(getViewerLink());
	getViewer().add(getViewerURI());
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

    public VerticalPanel getViewer() {
	return viewer;
    }

    public void setViewer(VerticalPanel viewer) {
	this.viewer = viewer;
    }

    private void setViewerLink(HTML html) {
	this.viewerLink = html;
    }

    private HTML getViewerLink() {
	return this.viewerLink;
    }

    private void setViewerURI(HTML html) {
	this.viewerURI = html;
    }

    private HTML getViewerURI() {
	return this.viewerURI;
    }

    private void setViewerDesc(HTML html) {
	this.viewerDesc = html;
    }

    private HTML getViewerDesc() {
	return this.viewerDesc;
    }

    private void setViewerPanel(FlexTable viewerPanel) {
	this.viewerPanel = viewerPanel;
    }

    private FlexTable getViewerPanel() {
	return viewerPanel;
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected OsylResProxLinkView getView() {
	return (OsylResProxLinkView) super.getView();
    }

    public void setText(String text) {
	if (isInEditionMode()) {
	    editorName.setText(text);
	    if (editorName.getOffsetWidth() > 675) {
		editorName.setWidth("675px");
	    }
	} else {
	    viewerLink.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editorName.getText();
	} else {
	    return viewerLink.getHTML();
	}
    }

    public void setLink(String text) {
	if (isInEditionMode()) {
	    editorLink.setText(text);
	    if (editorLink.getOffsetWidth() > 675) {
		editorLink.setWidth("675px");
	    }
	} else {
	    viewerLink.setHTML(text);
	}
    }

    public String getLink() {
	if (isInEditionMode()) {
	    return editorLink.getText();
	} else {
	    return viewerLink.getHTML();
	}
    }

    public String getDescription() {
	if (isInEditionMode()) {
	    return editorDesc.getHTML();
	} else {
	    return viewerDesc.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editorName.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editor;
    }

    public boolean prepareForSave() {
	String messages = "";
	boolean ok = true;
	if (getText().trim().equals("")) {
	    ok = false;
	    messages +=
		    getView().getUiMessage("Global.field.required",
			    getUiMessage("Link.label"));
	}
	if (!LinkValidator.isValidLink(getLink())) {
	    ok = false;
	    messages = getView().getUiMessage("LinkEditor.unvalidLink");
	}
	if (!ok) {
	    OsylAlertDialog osylAlertDialog =
		    new OsylAlertDialog(getView().getUiMessage("Global.error"),
			    messages);
	    osylAlertDialog.center();
	    osylAlertDialog.show();
	}
	return ok;
    }

    public void enterEdit() {

	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	initEditor();
	createEditBox();

	// We get the text to edit from the model
	setText(getView().getTextFromModel());
	// And put the cursor at the end
	editorName.setCursorPos(getText().length());
	// And we give the focus to the editor
	editorName.setFocus(true);
	// We get the URL for the link
	setLink(getView().getLinkURI());
	// We get the description text to edit from the model
	editorDesc.setHTML(getView().getCommentFromModel());

    } // enterEdit

    public void enterView() {
	// We keep track that we are now in view-mode
	setInEditionMode(false);
	getMainPanel().clear();
	if (!(isReadOnly() && getView().isContextHidden())) {
	    initViewer();

	    // We get the text to display from the model
	    getViewerLink().setHTML(getView().getTextFromModel());
	    getViewerURI().setHTML("(" + getView().getRawURI() + ")");
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
    public Widget getBrowserWidget() {
	// TODO: return the hyperlink browser here
	return null;
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(editorName);
	focusWidgetList.add(editorLink);
	focusWidgetList.add(editorDesc);
	return focusWidgetList;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    @Override
    public void maximizeEditor() {
	originalEditorDescHeight = editorDesc.getOffsetHeight();
	super.maximizeEditor();
	int descAdd =
		getEditorPopup().getOffsetHeight()
			- getOriginalEditorPopupHeight();
	editorDesc.setHeight((originalEditorDescHeight + descAdd) + "px");
    }

    @Override
    public void normalizeEditorWindowState() {
	super.normalizeEditorWindowState();
	editorDesc.setHeight(originalEditorDescHeight + "px");
    }

}
