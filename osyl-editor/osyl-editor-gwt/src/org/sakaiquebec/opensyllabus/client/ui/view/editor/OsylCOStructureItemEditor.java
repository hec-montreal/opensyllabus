/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylPushButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylLabelEditClickListener;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureItemEditor extends OsylAbstractEditor {

    // Our main panel which will display the viewer
    private VerticalPanel mainPanel;

    // Our editor
    private VerticalPanel editorPanel;
    private TextBox nameEditor;

    // Our viewer
    private HTML viewer;

    private boolean isDeletable;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for and whether the edition mode is activated by clicking on the
     * main panel or not.
     * 
     * @param parent
     */
    public OsylCOStructureItemEditor(OsylAbstractView parent) {
	this(parent, false);

    }

    public OsylCOStructureItemEditor(OsylAbstractView parent,
	    boolean isNotDeletable) {
	super(parent);
	this.isDeletable = isNotDeletable;
	initMainPanel();
	if (!isReadOnly()) {
	    initEditor();
	}
	initViewer();
	initWidget(getMainPanel());
    }

    protected VerticalPanel getMainPanel() {
	return mainPanel;
    }

    protected void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    private void initMainPanel() {
	setMainPanel(new VerticalPanel());
    }

    /**
     * Creates and set the low-level editor (TextBox).
     */
    private void initEditor() {
	editorPanel = new VerticalPanel();
	editorPanel.setWidth("98%");

	if (getNameLabel() != null) {
	    editorPanel.add(getNameLabel());
	}

	nameEditor = new TextBox();
	nameEditor.setStylePrimaryName("Osyl-LabelEditor-TextBox");
	nameEditor.setWidth("100%");
	nameEditor.addClickListener(new ResetLabelClickListener(getView().getCoMessage(getView().getModel().getType())));
	
	if (getNameTooltip() != null) {
	    nameEditor.setTitle(getUiMessage("Evaluation.name.tooltip"));
	}
	editorPanel.add(nameEditor);
    }

    /**
     * Creates and set the low-level viewer (HTML panel).
     */
    private void initViewer() {
	HTML htmlViewer = new HTML();
	htmlViewer.setStylePrimaryName("Osyl-LabelEditor-View");
	setViewer(htmlViewer);
    }

    protected void setViewer(HTML html) {
	this.viewer = html;
    }

    protected HTML getViewer() {
	return viewer;
    }

    public void setViewerStyle(String levelStyle) {
	getViewer().addStyleName(levelStyle);
	getViewer().addStyleName("Osyl-TitleEditor");
    }

    /**
     * ===================== PROTECTED METHODS =====================
     */

    protected Label getNameLabel() {
	return null;
    }

    protected String getNameTooltip() {
	return null;
    }
    
    protected void refreshButtonPanel(){
	// We only create an edit button (as delete is not allowed) and add it:
	String title = getView().getUiMessage("edit");
	ClickListener listener = new OsylLabelEditClickListener(getView());
	AbstractImagePrototype imgEditButton = getOsylImageBundle().edit();
	ImageAndTextButton pbEdit =
		createButton(imgEditButton, title, listener);
	getView().getButtonPanel().clear();
	getView().getButtonPanel().add(pbEdit);
	if (isDeletable) {
	    getView().getButtonPanel().add(createButtonDelete());
	    refreshUpAndDownPanel();
	}
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public void setText(String text) {
	if (isInEditionMode()) {
	    nameEditor.setText(text);
	} else {
	    viewer.setHTML(text);
	}
    }

    public String getText() {
	if (isInEditionMode()) {
	    return nameEditor.getText();
	} else {
	    return viewer.getHTML();
	}
    }

    public void setFocus(boolean b) {
	if (isInEditionMode()) {
	    nameEditor.setFocus(b);
	}
    }

    public Widget getEditorTopWidget() {
	return editorPanel;
    }

    public boolean prepareForSave() {
	return true;
    }

    public void enterEdit() {

	createEditBox();

	// We keep track that we are now in edition-mode
	setInEditionMode(true);
	// We get the text to edit from the model
	setText(getView().getTextFromModel());
	// And put the cursor at the end
	nameEditor.setCursorPos(getText().length());
	// And we give the focus to the editor
	nameEditor.setFocus(true);

    } // enterEdit

    public void enterView() {

	// We remove any previous widget
	getMainPanel().clear();
	// And we put the viewer instead
	getMainPanel().add(getViewer());
	// We keep track that we are now in view-mode
	setInEditionMode(false);
	// We get the text to display from the model
	setText(getView().getTextFromModel());

	// If we are in read-only mode, we return now to not add buttons and
	// listeners enabling edition or deletion:
	if (isReadOnly()) {
	    return;
	}

	refreshButtonPanel();

    } // enterView

    @Override
    public Widget[] getOptionWidgets() {
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
    public boolean isResizable() {
	return true;
    }

    @Override
    public void maximizeEditor() {
	getEditorPopup().setHeight(getOriginalEditorPopupHeight() + "px");
	OsylEditorEntryPoint.centerObject(getEditorPopup());
    }

    @Override
    public void normalizeEditorWindowState() {
	// do nothing as editor height has not been changed
    }

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	ArrayList<FocusWidget> focusWidgetList = new ArrayList<FocusWidget>();
	focusWidgetList.add(nameEditor);
	return focusWidgetList;
    }

    protected ClickListener getCancelButtonClickListener() {
	return new ClickListener() {

	    public void onClick(Widget sender) {
		getView().getMainPanel().removeStyleDependentName("Hover");
		getView().getButtonPanel().setVisible(false);
	    }
	};
    }

    @Override
    protected Widget getMetaInfoLabel() {
	return null;
    }


    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */

    protected ImageAndTextButton createButtonDelete() {
	AbstractImagePrototype imgDeleteButton = getOsylImageBundle().delete();
	String title = getView().getUiMessage("delete");
	ClickListener listener =
		new MyDeletePushButtonListener((COContentUnit) getView()
			.getModel());
	return createButton(imgDeleteButton, title, listener);
    }

    protected OsylPushButton createButtonUp() {
	OsylPushButton upButton;
	if (((COContentUnit)getView().getModel()).hasPredecessorInStructure()) {
	    upButton =
		    new OsylPushButton(getOsylImageBundle().up_full()
			    .createImage(), getOsylImageBundle().up_full()
			    .createImage(), getOsylImageBundle().up_full()
			    .createImage());
	    upButton.setTitle(getUiMessage("UpButton.title"));
	    upButton.setEnabledButton();
	    upButton.addClickListener(new ClickListener() {

		public void onClick(Widget sender) {
		    getView().leaveEdit();
		    ((COContentUnit)getView().getModel()).moveUp();
		}

	    });
	} else {
	    upButton =
		    new OsylPushButton(getOsylImageBundle().up_empty()
			    .createImage(), getOsylImageBundle().up_empty()
			    .createImage(), getOsylImageBundle().up_empty()
			    .createImage());
	    upButton.setDisabledButton();
	}
	upButton.setVisible(true);
	return upButton;
    }

    protected OsylPushButton createButtonDown() {
	OsylPushButton downButton;
	if (((COContentUnit)getView().getModel()).hasSuccessorInStructure()) { 
	    downButton =
		    new OsylPushButton(getOsylImageBundle().down_full()
			    .createImage(), getOsylImageBundle().down_full()
			    .createImage(), getOsylImageBundle().down_full()
			    .createImage());
	    downButton.setTitle(getUiMessage("DownButton.title"));
	    downButton.setEnabledButton();
	    downButton.addClickListener(new ClickListener() {

		public void onClick(Widget sender) {
		    getView().leaveEdit();
		    ((COContentUnit)getView().getModel()).moveDown();
		}

	    });
	} else {
	    downButton =
		    new OsylPushButton(getOsylImageBundle().down_empty()
			    .createImage(), getOsylImageBundle().down_empty()
			    .createImage(), getOsylImageBundle().down_empty()
			    .createImage());
	    downButton.setDisabledButton();
	}
	return downButton;
    }

    /**
     * Used to refresh up and down arrows
     */
    public void refreshUpAndDownPanel() {
	OsylPushButton upButton = createButtonUp();
	OsylPushButton downButton = createButtonDown();
	getView().getUpAndDownPanel().clear();
	getView().getUpAndDownPanel().add(upButton);
	getView().getUpAndDownPanel().add(downButton);
    }

    /**
     * Class to manage click on the delete button
     */
    public class MyDeletePushButtonListener implements ClickListener {

	// Model variables (we use either one or the other). We could also use
	// a generic variable and cast it when needed...
	private COContentUnit coContentUnit;

	public MyDeletePushButtonListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	/**
	 * @see ClickListener#onClick(Widget)
	 */
	public void onClick(Widget sender) {
	    try {
		// Here, we create a dialog box to confirm delete
		OsylOkCancelDialog osylOkCancelDialog =
			new OsylOkCancelDialog(getView().getUiMessage(
				"OsylOkCancelDialog_Delete_Title"), getView()
				.getUiMessage(
					"OsylOkCancelDialog_Delete_Content"));
		osylOkCancelDialog
			.addOkButtonCLickListener(new MyOkCancelDialogListener(
				this.coContentUnit));
		osylOkCancelDialog
			.addCancelButtonClickListener(getCancelButtonClickListener());
		osylOkCancelDialog.show();
		osylOkCancelDialog.centerAndFocus();
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
    }

    // The click listener that perform delete action if confirm button pushed
    public class MyOkCancelDialogListener implements ClickListener {

	private COContentUnit coContentUnit;

	public MyOkCancelDialogListener(COContentUnit coContentUnit) {
	    this.coContentUnit = coContentUnit;
	}

	public void onClick(Widget sender) {
	    try {
		String title = coContentUnit.getLabel();
		coContentUnit.remove();
		OsylUnobtrusiveAlert info =
			new OsylUnobtrusiveAlert(getView().getUiMessages()
				.getMessage("RemovedContentUnit", title));
		OsylEditorEntryPoint.showWidgetOnTop(info);
	    } catch (Exception e) {
		com.google.gwt.user.client.Window
			.alert("Unable to delete object. Error=" + e);
	    }
	}
    }

    public void setIsDeletable(boolean isDeletable) {
	this.isDeletable = isDeletable;
    }
    
    public boolean isDeletable() {
	return isDeletable;
    }

    @Override
    public boolean isMoveable() {
	return false;
    }

}
