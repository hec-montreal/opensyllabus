/*******************************************************************************
 * $Id: $
 * *****************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.manager.client.ui.dialog;

import org.sakaiquebec.opensyllabus.manager.client.OsylManagerEntryPoint;
import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * First version of an OsylDialog package OsylOKCancelDialog extends GWT.
 * Further development should be based on SWING JOptionPane:
 * http://java.sun.com/javase/6/docs/api/javax/swing/JOptionPane.html and the
 * SWING tutorial : How to Make Dialogs
 * http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html
 * OsylOkCancelDialog coding example:
 * 
 * <pre>
 * final OsylOkCancelDialog dialogBox =
 * 	new OsylOkCancelDialog(false, true, &quot;Dialog Title&quot;,
 * 		&quot;The content of the Dialog&quot;);
 * dialogBox.center();
 * dialogBox.show();
 * </pre>
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */

public class OsylCancelDialog extends OsylAbstractLightBox {

    /**
     * User interface message bundle
     */
    private static Messages messages =
	    OsylManagerController.getInstance().getMessages();

    private boolean selectedValue = false;
    private Button cancelButton;
    private String cancelButtonLabel =
	    messages.OsylCancelDialog_Cancel_Button();
    
    public OsylCancelDialog() {
	super(false, true);
    }

    /**
     * Constructor using default title and standard behavior (autohide is false
     * and modal is true).
     * 
     * @param dialogContent : the text content of the dialog
     */
    public OsylCancelDialog(String dialogContent) {
	this(messages.OsylCancelDialog_Title(), dialogContent);
    }

    /**
     * Constructor using standard behavior (autohide is false and modal is
     * true).
     * 
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylCancelDialog(String dialogTitle, String dialogContent) {
	super(false, true);
	initDialog(dialogTitle, dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *            the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *            contained by the dialog should be ignored
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylCancelDialog(boolean autoHide, boolean modal,
	    String dialogTitle, String dialogContent) {
	super(autoHide, modal);
	initDialog(dialogTitle, dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor with default title.
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *            the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *            contained by the dialog should be ignore
     * @param dialogContent : the text content of the dialog
     */
    public OsylCancelDialog(boolean autoHide, boolean modal,
	    String dialogContent) {
	this(autoHide, modal,
		messages.OsylCancelDialog_Title(),
		dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor specifying every possible parameter
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *            the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *            contained by the dialog should be ignore
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     * @param okButtonLabel : label for ok button
     * @param cancelButtonLabel : label for cancel button
     */
    public OsylCancelDialog(boolean autoHide, boolean modal,
	    String dialogTitle, String dialogContent,
	    String cancelButtonLabel) {
	super(autoHide, modal);
	setCancelButtonLabel(cancelButtonLabel);
	initDialog(dialogTitle, dialogContent);
    }

    private void initDialog(String dialogTitle, String dialogContent) {
	setGlassEnabled(true);
	this.setSize("275px", "150px");
	// Set the dialog box's caption.
	VerticalPanel dialogVPanel = new VerticalPanel();
	dialogVPanel.setWidth("100%");
	dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
	this.setGlassEnabled(true);
	// Set the animation behaviour
	this.setAnimationEnabled(true);
	// Set the title
	this.setHTML(dialogTitle);
	// Set the Content
	Label contentLabel = new Label();
	contentLabel.setStylePrimaryName("Osyl-DialogBox-ContentLabel");
	contentLabel.setText(dialogContent);
	dialogVPanel.add(contentLabel);
	
	Image im = new Image(GWT.getModuleBaseURL() + "images/ajaxLoader.gif");
	dialogVPanel.add(im);
	
	// Create & add an option panel
	HorizontalPanel optionPanel = new HorizontalPanel();
	optionPanel.setSpacing(5);
	
	// Add Cancel button
	cancelButton = new Button(cancelButtonLabel);
	cancelButton.setWidth("60");
//	cancelButton.setStylePrimaryName("Osyl-Button");
	optionPanel.add(cancelButton);
	cancelButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		setSelectedValue(false);
		OsylCancelDialog.this.hide(true);
	    }
	});
	dialogVPanel.add(optionPanel);
	// Set the contents of the Widget
	// DialogBox is a SimplePanel, so you have to set its widget property to
	// whatever you want its contents to be.
	this.setWidget(dialogVPanel);
    }

    /**
     * Returns true if OK has been clicked, false otherwise.
     * 
     * @return the selectedValue value.
     */
    public boolean getSelectedValue() {
	return this.selectedValue;
    }

    /**
     * @param selectedValue the new value of selectedValue.
     */
    public void setSelectedValue(boolean selectedValue) {
	this.selectedValue = selectedValue;
    }

    public void setCancelButtonLabel(String label) {
	this.cancelButtonLabel = label;
    }

    /**
     * Adds a <code>ClickListener</code> to the cancelButton.
     * 
     * @param clickListener
     */
    public void addCancelButtonClickHandler(ClickHandler clickListener) {
	cancelButton.addClickHandler(clickListener);
    }

    /**
     * Centers the dialog (by invoking
     * {@link OsylEditorEntryPoint#centerObject(com.google.gwt.user.client.ui.PopupPanel)}
     * . This must be invoked after the {@link PopupPanel#show()} method
     * otherwise the centering calculations may be wrong!
     */
    public void center() {
	OsylManagerEntryPoint.centerObject(this);
    }

    /**
     * Centers the dialog (by invoking
     * {@link OsylEditorEntryPoint#centerObject(com.google.gwt.user.client.ui.PopupPanel)}
     * and gives focus to the cancel button. This must be invoked after the
     * {@link PopupPanel#show()} method otherwise the centering calculations may
     * be wrong!
     */
    public void centerAndFocus() {
	OsylManagerEntryPoint.centerObject(this);
	// We explicitly give the focus to the cancel button for easy dismiss!
	cancelButton.setFocus(true);
    }

}
