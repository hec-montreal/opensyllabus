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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxAssignmentView;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Editor to be used within by {@link OsylResProxAssignmentView}. It is
 * different from other editors as it shows two different views and their
 * respective editor. The first one is for the assignment description, and the
 * second one is for the assignment link text.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylAssignmentEditor extends OsylAbstractResProxEditor {

    // Our main panel which will display the viewer
    private VerticalPanel mainPanel;

    // Our editor widgets
    private VerticalPanel editorPanel;
    private TextBox editor;

    // Our viewer
    private HTML viewer;


    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     */
    public OsylAssignmentEditor(OsylAbstractView parent) {
	super(parent);
	initMainPanel();
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewer();
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

    /**
     * Creates and set the low-level editor (TextBox).
     */
    private void initEditor() {
	VerticalPanel editorPanel = new VerticalPanel();
	
	Label l1 = new Label(getUiMessage("clickable_text"));
	editorPanel.add(l1);

	TextBox tb = new TextBox();
	setEditor(tb);
	tb.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	editorPanel.add(tb);
	
	HTML html = new HTML(getUiMessage("AssignmentEditor.changeParams"));
	html.setWordWrap(true);
	html.setWidth("575px");
	html.setStylePrimaryName("information");
	editorPanel.add(html);
	
	setEditorPanel(editorPanel);
    }

    private void setEditorPanel(VerticalPanel vp) {
	this.editorPanel = vp;
    }

    private void setEditor(TextBox tb) {
	this.editor = tb;
    }

    /**
     * Creates and set the low-level viewer (HTML panel).
     */
    private void initViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-LabelEditor-View");
	setViewer(htmlViewer);
    }

    private void setViewer(HTML html) {
	this.viewer = html;
    }

    private HTML getViewer() {
	return viewer;
    }

    /**
     * ===================== PROTECTED METHODS =====================
     */

    /* none */

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public OsylResProxAssignmentView getView() {
	return (OsylResProxAssignmentView) super.getView();
    }
    
    public void setText(String text) {
	if (isInEditionMode()) {
	    editor.setText(text);
	} else {
	    viewer.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return editor.getText();
	} else {
	    return viewer.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    editor.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editorPanel;
    }

    public boolean prepareForSave() {
	return true;
    }

    public void enterEdit() {

	createEditBox(getUiMessage("AssignmentEditor.title"));
	
	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	// We get the text to edit from the model
	setText(getView().getTextFromModelLink());
	// And put the cursor at the end
	editor.setCursorPos(getText().length());
	// And we give the focus to the editor
	editor.setFocus(true);

	editor.setWidth("100%");

    } // enterEdit

    public void enterView() {

	// We remove any previous widget
	getMainPanel().clear();
	// And we put the viewer instead
	getMainPanel().add(getViewer());
	// We keep track that we are now in view-mode
	setInEditionMode(false);
	// We get the text to display from the model
	setText(getView().getTextFromModelLink());

	// If we are not in read-only mode, we display some meta-info and add
	// buttons and listeners enabling edition or deletion:
	if (! isReadOnly()) {
	    getMainPanel().add(getMetaInfoLabel());
	    addViewerStdButtons();
	}
	
    } // enterView

    @Override
    protected Widget[] getAdditionalOptionWidgets() {
	return null;
    }

    @Override
    public Widget getConfigurationWidget() {
	return null;
    }

    @Override
    public Widget getBrowserWidget() {
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
    
    @Override
    public void maximizeEditor() {
    getEditorPopup().setHeight(getOriginalEditorPopupHeight()+"px");
    OsylEditorEntryPoint.centerObject(getEditorPopup());
    }
    
    @Override
    public void normalizeEditorWindowState() {
    // do nothing as editor height has not been changed
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

}