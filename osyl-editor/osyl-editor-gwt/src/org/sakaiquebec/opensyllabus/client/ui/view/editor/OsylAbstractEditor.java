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

import java.util.Iterator;
import java.util.List;

import org.gwt.mosaic.ui.client.Caption;
import org.gwt.mosaic.ui.client.ImageButton;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.gwt.mosaic.ui.client.Caption.CaptionRegion;
import org.gwt.mosaic.ui.client.WindowPanel.WindowState;
import org.gwt.mosaic.ui.client.WindowPanel.WindowStateListener;
import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylWindowPanel;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylCloseClickListener;
import org.sakaiquebec.opensyllabus.client.ui.listener.OsylEditClickListener;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class defining common methods for editors.
 */
public abstract class OsylAbstractEditor extends Composite {

    // The DialogBox used to display our edition widgets
    // private DialogBox pop;
    private OsylWindowPanel pop;

    // Either we are in read-only mode or not.
    private boolean readOnly = false;

    // Reference to the OsylAbstractView we are working for
    private OsylAbstractView view;

    // Whether the editor is currently in edition mode or only displaying its
    // contents
    private boolean isInEditionMode;

    // the formatting toolbar, could be null if there isn't RichText
    private OsylFormattingToolbar osylFormattingToolbar;

    // height of editor widget in pop-up
    private int originalEditorWidgetHeight;
    // height of editor pop-up to remember for maximizing window
    private int originalEditorPopupHeight;

    /**
     * Constructor specifying the {@link OsylAbstractView} this editor is
     * working for.
     * 
     * @param view
     */
    public OsylAbstractEditor(OsylAbstractView view) {
	setView(view);
	setReadOnly(view.getController().isReadOnly());
    }

    /**
     * ====================== PRIVATE METHODS ======================
     */

    private void setView(OsylAbstractView view) {
	this.view = view;
    }

    private void addMaximizeButton(final WindowPanel windowPanel,
	    CaptionRegion captionRegion) {
	final ImageButton maximizeBtn =
		new ImageButton(Caption.IMAGES.windowMaximize());
	maximizeBtn.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		if (windowPanel.getWindowState() == WindowState.MAXIMIZED) {
		    windowPanel.setWindowState(WindowState.NORMAL);
		} else {
		    windowPanel.setWindowState(WindowState.MAXIMIZED);
		}
	    }
	});
	windowPanel.addWindowStateListener(new WindowStateListener() {
	    public void onWindowStateChange(WindowPanel sender) {
		if (sender.getWindowState() == WindowState.MAXIMIZED) {
		    maximizeBtn.setImage(Caption.IMAGES.windowRestore()
			    .createImage());
		    maximizeEditor();
		} else {
		    maximizeBtn.setImage(Caption.IMAGES.windowMaximize()
			    .createImage());
		    normalizeEditorWindowState();
		}

	    }
	});
	windowPanel.getHeader().add(maximizeBtn, captionRegion);
    }

    /**
     * ==================== COMMON METHODS =====================
     */

    protected OsylAbstractView getView() {
	return view;
    }

    protected OsylImageBundleInterface getOsylImageBundle() {
	return getView().getOsylImageBundle();
    }

    public void setReadOnly(boolean b) {
	this.readOnly = b;
    }

    public boolean isReadOnly() {
	return readOnly;
    }

    protected OsylWindowPanel getEditorPopup() {
	return pop;
    }

    public void setOriginalEditorPopupHeight(int height) {
	this.originalEditorPopupHeight = height;
    }

    public int getOriginalEditorPopupHeight() {
	return originalEditorPopupHeight;
    }

    /**
     * Refreshes the view of the <code>OsylAbstractEditor</code>.
     */
    public void refreshView() {

	if (!isReadOnly()) {
	    if (isInEditionMode()) {
		enterEdit();
	    } else {
		enterView();
	    }
	} else {
	    enterView();
	}
    }

    protected void addViewerStdButtons() {
	ImageAndTextButton pbEdit = createButtonEdit();
	getView().getButtonPanel().clear();
	getView().getButtonPanel().add(pbEdit);
	getView().getUpAndDownPanel().clear();
    }

    // TODO: check this code if we can delete it
    // I de-activated this code because it is never called (buttons are
    // created at other places). As I changed the OsylCloseClickListener,
    // I am not sure if this still works like it is supposed to.

    // protected void addEditorStdButtons() {
    // ImageAndTextButton pbSaveEdit = createButtonValidate();
    // ImageAndTextButton pbCancelEdit = createButtonCancel();
    // getView().getButtonPanel().clear();
    // getView().getButtonPanel().add(pbSaveEdit);
    // getView().getButtonPanel().add(pbCancelEdit);
    // getView().getUpAndDownPanel().clear();
    // }

    // protected ImageAndTextButton createButtonValidate() {
    // AbstractImagePrototype imgValidateButton =
    // getOsylImageBundle().action_validate();
    // String title = getView().getUiMessage("Global.ok");
    // ClickListener listener = new OsylCloseClickListener(getView(), true);
    // return createButton(imgValidateButton, title, listener);
    // }
    //
    // protected ImageAndTextButton createButtonCancel() {
    // AbstractImagePrototype imgCancelButton =
    // getOsylImageBundle().action_validate();
    // String title = getView().getUiMessage("Global.cancel");
    // ClickListener listener = new OsylCloseClickListener(getView(), false);
    // return createButton(imgCancelButton, title, listener);
    // }

    protected ImageAndTextButton createButtonEdit() {
	AbstractImagePrototype imgEditButton = getOsylImageBundle().edit();
	String title = getView().getUiMessage("edit");
	ClickListener listener = new OsylEditClickListener(getView());
	return createButton(imgEditButton, title, listener);
    }

    protected PushButton createPushButton(Image img, String msg,
	    ClickListener listener) {

	// We return a PushButton with the specified image, title and listener
	PushButton button = new PushButton(img);
	button.setStylePrimaryName("gwt-PushButton-up");
	button.setTitle(msg);
	button.addClickListener(listener);

	return button;

    }

    protected ImageAndTextButton createButton(AbstractImagePrototype imgButton,
	    String msg, ClickListener listener) {
	ImageAndTextButton button = new ImageAndTextButton(imgButton, msg);
	button.setStylePrimaryName("Osyl-Button");
	button.addClickListener(listener);
	return button;
    }

    /**
     * Creates a {@link DialogBox} containing the editor for this resProx. The
     * default title is used.
     */
    protected void createEditBox() {
	createEditBox(getUiMessage("EditorPopUp.title"));
    }

    /**
     * Creates a {@link DialogBox} containing the editor for this resProx using
     * the specified title.
     * 
     * @param title
     */
    protected void createEditBox(String title) {
	pop = new OsylWindowPanel(title, this);
	pop.setStylePrimaryName("Osyl-EditorPopup");

	pop.setAnimationEnabled(true);
	// resizable of WindowPanel is always true
	// otherwise size of pop-up is not shown properly
	pop.setResizable(true);
	if (isResizable()) {
	    addMaximizeButton(pop, CaptionRegion.RIGHT);
	}
	pop.addWindowCloseListener(new WindowCloseListener() {
	    public void onWindowClosed() {
    	if (pop.getWindowState() == WindowState.MAXIMIZED) {
		    pop.setWindowState(WindowState.NORMAL);
		}
		getView().leaveEdit();
	    }

	    public String onWindowClosing() {
		// no warning message when closing the editor window
		return null;
	    }
	});
	VerticalPanel mainPanel = new VerticalPanel();
	pop.setWidget(mainPanel);
	mainPanel.setWidth("100%");

	Widget browserWidget = getBrowserWidget();
	if (null != browserWidget) {
	    // row 0 (if applicable): the resource browser
	    HorizontalPanel row0 = new HorizontalPanel();
	    row0.setStylePrimaryName("Osyl-EditorPopup-RowBrowser");
	    row0.setWidth("100%");
	    mainPanel.add(row0);
	    row0.add(browserWidget);
	    browserWidget.setWidth("100%");
	}
	// First row: we add the various options:
	// rubric drop-down, Diffusion level, Hide, Important
	HorizontalPanel row1 = new HorizontalPanel();
	row1.setStylePrimaryName("Osyl-EditorPopup-RowOptions");
	row1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	row1.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
	row1.setWidth("100%");
	mainPanel.add(row1);
	// We need an additional panel to get our stuff aligned correctly
	HorizontalPanel configGroups = new HorizontalPanel();
	row1.add(configGroups);

	// Other options, specified by each subclass
	Widget[] optionWidgets = getOptionWidgets();
	if (null != optionWidgets) {
	    for (int i = 0; i < optionWidgets.length; i++) {
		configGroups.add(optionWidgets[i]);
	    }
	}

	for (Iterator<FocusWidget> fwIterator =
		getEditionFocusWidgets().iterator(); fwIterator.hasNext();) {
	    FocusWidget fw = (FocusWidget) fwIterator.next();
	    fw.addFocusListener(new FocusWidgetFocusListener());
	    if (fw.getClass().equals(RichTextArea.class)) {
		osylFormattingToolbar =
			new OsylFormattingToolbar((RichTextArea) fw);
	    }
	}

	if (osylFormattingToolbar != null) {
	    // 2nd row: the formatting toolBar
	    HorizontalPanel row2 = new HorizontalPanel();
	    row2.setStylePrimaryName("Osyl-EditorPopup-RowToolBar");
	    row2.setWidth("100%");
	    mainPanel.add(row2);
	    row2.add(osylFormattingToolbar);
	}
	// 3rd row: the editor only
	HorizontalPanel row3 = new HorizontalPanel();
	row3.setStylePrimaryName("Osyl-EditorPopup-RowEditor");
	row3.setWidth("100%");
	mainPanel.add(row3);
	Widget editor = getEditorTopWidget();
	editor.setWidth("99%");
	row3.add(editor);
	Widget configWidget = getConfigurationWidget();
	if (null != configWidget) {
	    // 4th row (if applicable): the configuration panel
	    HorizontalPanel row4 = new HorizontalPanel();
	    row4.setStylePrimaryName("Osyl-EditorPopup-RowConfiguration");
	    row4.setWidth("100%");
	    mainPanel.add(row4);
	    row4.add(configWidget);
	    configWidget.setWidth("100%");
	}
	// 5th row: the "move..." and "OK"/"Cancel" buttons We have to use 3
	// Horizontal Panels to get the right alignment
	HorizontalPanel row5 = new HorizontalPanel();
	row5.setStylePrimaryName("Osyl-EditorPopup-RowButtons");
	row5.setWidth("100%");
	row5.setHeight("30px");
	row5.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
	mainPanel.add(row5);

	/*
	 * Button moveButton = new Button("Move...");
	 * moveButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	 * row5.add(moveButton); moveButton.addClickListener(new ClickListener()
	 * { public void onClick(Widget w) { OsylAlertDialog al = new
	 * OsylAlertDialog("Oops!", "Sorry: not yet implemented!"); al.show();
	 * al.center(); } });
	 */
	HorizontalPanel rightPanel = new HorizontalPanel();
	rightPanel.setWidth("100%");
	rightPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
	row5.add(rightPanel);

	HorizontalPanel okCancelPanel = new HorizontalPanel();
	rightPanel.add(okCancelPanel);

	AbstractImagePrototype imgOkButton =
		getOsylImageBundle().action_validate();
	ImageAndTextButton okButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgOkButton, getUiMessage("Global.ok"));
	okButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	okCancelPanel.add(okButton);

	AbstractImagePrototype imgCancelButton =
		getOsylImageBundle().action_cancel();
	ImageAndTextButton cancelButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgCancelButton, getUiMessage("Global.cancel"));
	cancelButton.setStylePrimaryName("Osyl-EditorPopup-Button");
	okCancelPanel.add(cancelButton);

	okButton.addClickListener(new OsylCloseClickListener(getView(), true));
	cancelButton.addClickListener(new OsylCloseClickListener(getView(),
		false));

	// Sizes the pop-up to fit the preferred size of the its subcomponents,
	// shows it modal and centers it.
	pop.showModal(true);
	// set minimum width of pop-up
	if (pop.getContentWidth() < 400)
	    pop.setContentSize(400, pop.getContentHeight());
	// remember original height
	originalEditorPopupHeight = pop.getOffsetHeight();
	originalEditorWidgetHeight = getEditorTopWidget().getOffsetHeight();

	// And we give the focus to the editor using a deferred command
	// (otherwise it doesn't always work)
	DeferredCommand.addCommand(new Command() {
	    public void execute() {
		setFocus(true);
	    }
	});
    } // createEditBox

    public void closeEditor() {
	pop.hide();
    }

    /**
     * Returns whether the editor is currently in edition mode or only
     * displaying its contents.
     */
    public boolean isInEditionMode() {
	return isInEditionMode;
    }

    /**
     * Sets whether the editor is currently in edition mode or only displaying
     * its contents.
     */
    public void setInEditionMode(boolean isInEditionMode) {
	this.isInEditionMode = isInEditionMode;
    }

    /**
     * ==================== ABSTRACT METHODS =====================
     */

    /**
     * Gives focus to the editor. Notice that if the current mode is display
     * (and not edition), this call may have no impact.
     */
    public abstract void setFocus(boolean b);

    /**
     * Sets the text content of resource currently displayed or edited.
     */
    public abstract void setText(String text);

    /**
     * Returns the text content of resource currently displayed or edited.
     * Caveat: This only applies to editors responsible for a single text field
     * as the {@link OsylRichTextEditor}, {@link OsylLinkEditor} and
     * {@link OsylLabelEditor}. Return value for other editors is either
     * undefined (may throw an exception) or implicit (may be the first field of
     * the editor). Refer to the specific editor documentation.
     */
    public abstract String getText();

    /**
     * Returns the top-most widget containing other UI elements.
     */
    public abstract Widget getEditorTopWidget();

    /**
     * Called to enter in edition mode. Edition mode is mutually exclusive with
     * display mode.
     */
    public abstract void enterEdit();

    /**
     * Called to enter in display mode. Display mode is mutually exclusive with
     * edition mode.
     */
    public abstract void enterView();

    /**
     * prepareForSave() is invoked by the view associated to this editor when it
     * is about to close its editors. If the editor is not in a valid state
     * (i.e.: some mandatory field is empty), prepareForSave should return false
     * (after displaying a message so that the user can correct the situation).
     * Otherwise true should be returned.
     */
    public abstract boolean prepareForSave();

    /**
     * Returns the widgets that should be displayed to specify options related
     * to this editor. If null is returned, then no specific options are
     * displayed.
     */
    public abstract Widget[] getOptionWidgets();

    /**
     * Returns the widget that should be displayed at the top-position of the
     * editor pop-up. If null is returned, then the section is empty.
     */
    public abstract Widget getBrowserWidget();

    // public abstract Dimension getPreferredSize();

    // public abstract RichTextArea getRichTextArea();

    /**
     * Returns the widget that should be displayed at the bottom-position of the
     * editor pop-up. If null is returned, then the section is empty.
     */
    public abstract Widget getConfigurationWidget();

    /**
     * Returns true if editor pop-up is resizable.
     */
    public abstract boolean isResizable();

    /**
     * @return the list of focus widget used for edition
     */
    protected abstract List<FocusWidget> getEditionFocusWidgets();

    /**
     * ====================== ADDED METHODS =======================
     */

    protected String getUiMessage(String key) {
	return getView().getUiMessage(key);
    }

    /**
     * Listener used to know which RichtextArea has the focused
     */
    public class FocusWidgetFocusListener implements FocusListener {

	public void onFocus(Widget sender) {
	    if (sender.getClass().equals(RichTextArea.class)) {
		osylFormattingToolbar.setRichText((RichTextArea) sender);
	    } else {
		if (null != osylFormattingToolbar) {
		    osylFormattingToolbar.setRichText(null);
		}
	    }
	}

	public void onLostFocus(Widget sender) {
	    // Nothing to do
	}
    }

    /**
     * Called when pop-up of editor is changed to maximized state
     */
    public void maximizeEditor() {
	int height = OsylEditorEntryPoint.getViewportHeight();
	if (height < getOriginalEditorPopupHeight())
	    height = getOriginalEditorPopupHeight();
	int editorAdd = height - getOriginalEditorPopupHeight();
	getEditorTopWidget().setHeight(
		(originalEditorWidgetHeight + editorAdd) + "px");
	getEditorPopup().setHeight(height + "px");
	OsylEditorEntryPoint.centerObject(pop);
    }

    /**
     * Called when pop-up of editor is changed to normal state
     */
    public void normalizeEditorWindowState() {
	getEditorPopup().setHeight(getOriginalEditorPopupHeight() + "px");
	getEditorTopWidget().setHeight(originalEditorWidgetHeight + "px");
    }

}