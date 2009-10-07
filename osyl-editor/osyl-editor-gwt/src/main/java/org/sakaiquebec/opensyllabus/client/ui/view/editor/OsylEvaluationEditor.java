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

import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylResProxEvaluationView;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Editor to be used within by {@link OsylResProxEvaluationView}. It shows four
 * different views and their respective editor. Each evaluation shows these
 * views:
 * <ul>
 * <li>Title (single-line unformatted)</li>
 * <li>Description (multi-line rich-text)</li>
 * <li>Rating (single-line number between 0 and 100)</li>
 * <li>Date</li>
 * </ul>
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylEvaluationEditor extends OsylAbstractResProxEditor {

    // Our main panel which will display the description panel as well as the
    // link panel
    private VerticalPanel mainPanel;
    private VerticalPanel editorPanel;
    private VerticalPanel viewerPanel;

    // Our edition widgets
    private TextBox editorTitle;
    private RichTextArea editorDescription;
    private TextBox editorRating;
    private TextBox editorDate;

    // Our viewers
    private HTML viewerTitle;
    private HTML viewerDescription;
    private HTML viewerRating;
    private HTML viewerDate;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param parent
     */
    public OsylEvaluationEditor(OsylAbstractView parent) {
	super(parent);
	initMainPanel();
	if (!isReadOnly()) {
	    initEditors();
	    initEditorPanel();
	}
	initViewers();
	initViewerPanel();
	initWidget(mainPanel);
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */

    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
    }

    private void setMainPanel(VerticalPanel p) {
	this.mainPanel = p;
    }

    private VerticalPanel getMainPanel() {
	return this.mainPanel;
    }

    private void setEditorPanel(VerticalPanel editorPanel) {
	this.editorPanel = editorPanel;
    }

    private VerticalPanel getEditorPanel() {
	return editorPanel;
    }

    /**
     * Creates the edition layout which consists of: 1. Label Title 2. TextBox
     * Title 3. Label Description 4. RichTextArea Description 5. Label Rating 6.
     * TextBox Rating 7. Label Date 8. TextBox Date Must be called after
     * initEditors()
     */
    private void initEditorPanel() {
	setEditorPanel(new VerticalPanel());

	// We first add a label indicating that the following editor is for
	// the evaluation description:
	getEditorPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_TitleLabel")));
	// Then we add the title editor
	getEditorPanel().add(getEditorTitle());

	// And we repeat the same for each field!
	// Description
	getEditorPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_DescriptionLabel")));
	getEditorPanel().add(getEditorDescription());

	// Rating
	getEditorPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_RatingLabel")));
	getEditorPanel().add(getEditorRating());

	// Date
	getEditorPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_DateLabel")));
	getEditorPanel().add(getEditorDate());
    } // initEditorPanel

    private Label createNewLabel(String text) {
	Label label = new Label(text);
	label.setStyleName("Osyl-ResProxView-Label");
	return label;
    }

    /**
     * Creates the textAreas needed for this editor.
     */
    private void initEditors() {
	setEditorTitle(createTextBox());
	setEditorDescription(createNewRichTextArea());
	setEditorRating(createTextBox());
	setEditorDate(createTextBox());
    }

    private TextBox createTextBox() {
	TextBox tb = new TextBox();
	tb.setStylePrimaryName("Osyl-UnitView-TextBox");
	tb.setWidth("100%");
	return tb;
    }

    private RichTextArea createNewRichTextArea() {
	RichTextArea rta = new RichTextArea();
	rta.setStylePrimaryName("Osyl-UnitView-TextArea");
	rta.setWidth("100%");
	return rta;
    }

    /**
     * Creates the HTML viewers needed for this editor.
     */
    private void initViewers() {
	setViewerTitle(createNewViewer());
	setViewerDescription(createNewViewer());
	setViewerRating(createNewViewer());
	setViewerDate(createNewViewer());
    }

    public void setViewerPanel(VerticalPanel viewerPanel) {
	this.viewerPanel = viewerPanel;
    }

    public VerticalPanel getViewerPanel() {
	return viewerPanel;
    }

    private void initViewerPanel() {
	setViewerPanel(new VerticalPanel());

	// We first add a label indicating that the following editor is for
	// the evaluation description:
	getViewerPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_TitleLabel")));
	// Then we add the title editor
	getViewerPanel().add(getViewerTitle());

	// And we repeat the same for each field!
	// Description
	getViewerPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_DescriptionLabel")));
	getViewerPanel().add(getViewerDescription());

	// Rating
	getViewerPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_RatingLabel")));
	getViewerPanel().add(getViewerRating());

	// Date
	getViewerPanel().add(
		createNewLabel(getView().getCoMessage(
			"ResProxEvaluationView_DateLabel")));
	getViewerPanel().add(getViewerDate());
    }

    private HTML createNewViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-ResProxView-LabelValue");
	return htmlViewer;
    }

    /**
     * ===================== SETTERS + GETTERS =====================
     * =================== FOR VIEWERS + EDITORS ===================
     */

    private TextBox getEditorTitle() {
	return editorTitle;
    }

    private void setEditorTitle(TextBox editorTitle) {
	this.editorTitle = editorTitle;
    }

    private RichTextArea getEditorDescription() {
	return editorDescription;
    }

    private void setEditorDescription(RichTextArea editorDescription) {
	this.editorDescription = editorDescription;
    }

    private TextBox getEditorRating() {
	return editorRating;
    }

    private void setEditorRating(TextBox editorRating) {
	this.editorRating = editorRating;
    }

    private TextBox getEditorDate() {
	return editorDate;
    }

    private void setEditorDate(TextBox editorDate) {
	this.editorDate = editorDate;
    }

    private void setViewerTitle(HTML viewerTitle) {
	this.viewerTitle = viewerTitle;
    }

    private HTML getViewerTitle() {
	return viewerTitle;
    }

    private void setViewerDescription(HTML viewerDescription) {
	this.viewerDescription = viewerDescription;
    }

    private HTML getViewerDescription() {
	return viewerDescription;
    }

    private void setViewerRating(HTML viewerRating) {
	this.viewerRating = viewerRating;
    }

    private HTML getViewerRating() {
	return viewerRating;
    }

    private void setViewerDate(HTML viewerDate) {
	this.viewerDate = viewerDate;
    }

    private HTML getViewerDate() {
	return viewerDate;
    }

    /**
     * ===================== OVERRIDDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public OsylResProxEvaluationView getView() {
	return (OsylResProxEvaluationView) super.getView();
    }

    /**
     * The evaluation editor displays four editable fields. For this reason,
     * setText(String) should not be used as it does not explicitly refer to one
     * of these fields.
     */
    public void setText(String text) {
	throw new IllegalStateException(
		"Do not use setText(String) for evaluations.");
    }

    /**
     * The evaluation editor displays four editable fields. For this reason,
     * getText() should not be used as it does not explicitly refer to one of
     * these fields. Use {@link OsylEvaluationEditor#getTextDescription()} for
     * instance.
     */
    public String getText() {
	throw new IllegalStateException("Do not use getText() for evaluations.");
    }

    /**
     * Enters edition mode by showing all the editors.
     */
    public void enterEdit() {

	// We keep track that we are now in edition-mode
	setInEditionMode(true);

	// We get the values to edit from the model
	editorTitle.setText(getView().getTextFromModelTitle());
	editorDescription.setHTML(getView().getTextFromModelDescription());
	editorRating.setText(getView().getTextFromModelRating());
	editorDate.setText(getView().getTextFromModelDate());

	// And put the cursor at the end
	editorTitle.setCursorPos(getTextTitle().length());
	// And we give the focus to the editor
	editorTitle.setFocus(true);

	createEditBox();

    } // enterEdit

    /**
     * Enters view mode by showing all the viewers.
     */
    public void enterView() {
	// We keep track that we are now in view-mode
	setInEditionMode(false);

	getMainPanel().clear();
	getMainPanel().add(getViewerPanel());

	// We get the text to display from the model
	getViewerTitle().setText(getView().getTextFromModelTitle());
	getViewerDescription().setHTML(getView().getTextFromModelDescription());
	getViewerRating().setText(getView().getTextFromModelRating());
	getViewerDate().setText(getView().getTextFromModelDate());

	// If we are not in read-only mode, we display some meta-info and add
	// buttons and listeners enabling edition or deletion:
	if (!isReadOnly()) {
	    getMainPanel().add(getMetaInfoLabel());

	    addViewerStdButtons();
	}
    } // enterView

    /**
     * Gives focus to the description editor.
     */
    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    getEditorTitle().setFocus(b);
	}
    }

    public VerticalPanel getEditorTopWidget() {
	return getEditorPanel();
    }

    public boolean prepareForSave() {
	String title = getTextTitle();
	if ("".equals(title)) {
	    // TODO: i18n
	    OsylAlertDialog alert =
		    new OsylAlertDialog("Error",
			    "Le titre de l'évaluation ne peut pas être vide");
	    alert.show();
	    return false;
	} else {
	    return true;
	}
    }

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
	focusWidgetList.add(editorTitle);
	focusWidgetList.add(editorDescription);
	focusWidgetList.add(editorRating);
	focusWidgetList.add(editorDate);
	return focusWidgetList;
    }

    @Override
    public boolean isResizable() {
	return false;
    }

    /*
     * ====================== ADDED METHODS ====================== Not in any
     * superclass. Most of these methods exist because we have several fields
     * instead of only one as in the superclass. They are all the equivalent of
     * an OsylAbstractEditor method but for one of the two fields.
     */

    public String getTextTitle() {
	return editorTitle.getText();
    }

    public String getTextDescription() {
	return editorDescription.getHTML();
    }

    public String getTextRating() {
	return editorRating.getText();
    }

    public String getTextDate() {
	return editorDate.getText();
    }
}
