/******************************************************************************
 * $Id$
 ******************************************************************************
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Rich-text editor to be used within {@link OsylAbstractView}.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylRichTextEditor extends OsylAbstractResProxEditor {

    // Our main panel which will either display the editor or the viewer
    private VerticalPanel mainPanel;
    
    private VerticalPanel editorPanel;

    // Our editor
    private RichTextArea editor;

    // Our viewer
    private HTML viewer;

    // Contains the viewer and info icons for the requirement level
    private FlexTable viewerPanel;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylRichTextEditor(OsylAbstractView parent) {
	this(parent, true);
    }

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     * @param clickToEdit
     */
    public OsylRichTextEditor(OsylAbstractView parent, boolean clickToEdit) {
	super(parent);
	initMainPanel();
	initWidget(getMainPanel());
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */
    private VerticalPanel getMainPanel() {
	return mainPanel;
    }

    private void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
    }

    private void initEditor() {
	editorPanel = new VerticalPanel();
	RichTextArea rta = new RichTextArea();
	rta.setStylePrimaryName("Osyl-UnitView-TextArea");
	rta.addClickHandler(new ResetLabelClickListener(getView().getCoMessage(
		"InsertYourTextHere")));
	setEditor(rta);
	editorPanel.add(editor);
    }

    private void setEditor(RichTextArea rta) {
	this.editor = rta;
    }

    public VerticalPanel getEditorTopWidget() {
	return this.editorPanel;
    }

    private void initViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-UnitView-UnitLabel");
	setViewer(htmlViewer);

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
	getViewerPanel().setWidget(column, 1,getViewer());
	getViewerPanel().getFlexCellFormatter().setStylePrimaryName(column, 1,
		"Osyl-UnitView-Content");
	mainPanel.add(getViewerPanel());
    }

    private void setViewer(HTML html) {
	this.viewer = html;
    }

    public HTML getViewer() {
	return this.viewer;
    }

    private void setViewerPanel(FlexTable viewerPanel) {
	this.viewerPanel = viewerPanel;
    }

    private FlexTable getViewerPanel() {
	return viewerPanel;
    }

    private String getTextFromModel() {
	return getView().getTextFromModel();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public void setText(String text) {
	if (isInEditionMode()) {
	    editor.setHTML(text);
	} else {
	    viewer.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editor.getHTML();
	} else {
	    return viewer.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editor.setFocus(b);
	}
    }

    public boolean prepareForSave() {
	return true;
    }

    public void enterEdit() {
	setInEditionMode(true);
	initEditor();
	createEditBox();
	// We get the text to edit from the model
	setText(getTextFromModel());
    } // enterEdit

    public void enterView() {
	// We keep track that we are now in view-mode
	setInEditionMode(false);
	getMainPanel().clear();
	
	if (!(isReadOnly() && getView().isContextHidden())) {
	    initViewer();

	    // We get the text to display from the model
	    setText(getTextFromModel());

	    // If we are not in read-only mode, we display some meta-info and
	    // add
	    // buttons and listeners enabling edition or deletion:
	    if (!isReadOnly()) {
		addViewerStdButtons();
		getMainPanel().add(getMetaInfoLabel());
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
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(editor);
	return focusWidgetList;
    }

    @Override
    public boolean isResizable() {
	return true;
    }

    /**
     * ====================== ADDED METHODS ====================== Not in any
     * superclass.
     */

}
