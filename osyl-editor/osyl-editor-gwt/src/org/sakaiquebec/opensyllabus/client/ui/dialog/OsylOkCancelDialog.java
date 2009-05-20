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

package org.sakaiquebec.opensyllabus.client.ui.dialog;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

public class OsylOkCancelDialog extends OsylAbstractLightBox {

    /**
     * User interface message bundle
     */
    private static OsylConfigMessages uiMessages =
	    OsylController.getInstance().getUiMessages();

    private boolean selectedValue = false;
    private Button okButton, cancelButton;
    private String okButtonLabel =
	    uiMessages.getMessage("OsylOKCancelDialog_Ok_Button");
    private String cancelButtonLabel =
	    uiMessages.getMessage("OsylOKCancelDialog_Cancel_Button");

    /**
     * Constructor using default title and standard behavior (autohide is false
     * and modal is true).
     * 
     * @param dialogContent : the text content of the dialog
     */
    public OsylOkCancelDialog(String dialogContent) {
	this(uiMessages.getMessage("OsylOkCancelDialog_Title"), dialogContent);
    }

    /**
     * Constructor using standard behavior (autohide is false and modal is
     * true).
     * 
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylOkCancelDialog(String dialogTitle, String dialogContent) {
	super(false, true);
	initDialog(dialogTitle, dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *                the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *                contained by the dialog should be ignored
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylOkCancelDialog(boolean autoHide, boolean modal, 
    		String dialogTitle, String dialogContent) {
	super(autoHide, modal);
	initDialog(dialogTitle, dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor with default title.
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *                the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *                contained by the dialog should be ignore
     * @param dialogContent : the text content of the dialog
     */
    public OsylOkCancelDialog(boolean autoHide, boolean modal,
    		String dialogContent) {
	this(autoHide, modal, 
			uiMessages.getMessage("OsylOkCancelDialog_Title"), 
			dialogContent);
    }

    /**
     * OsylOkCancelDialog Constructor specifying every possible parameter
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *                the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *                contained by the dialog should be ignore
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     * @param okButtonLabel : label for ok button
     * @param cancelButtonLabel : label for cancel button
     */
    public OsylOkCancelDialog(boolean autoHide, boolean modal,
	    String dialogTitle, String dialogContent, String okButtonLabel,
	    String cancelButtonLabel) {
	super(autoHide, modal);
	setOkButtonLabel(okButtonLabel);
	setCancelButtonLabel(cancelButtonLabel);
	initDialog(dialogTitle, dialogContent);
    }

    private void initDialog(String dialogTitle, String dialogContent) {
	setSize("243px", "150px");
	// Set the dialog box's caption.
	VerticalPanel dialogVPanel = new VerticalPanel();
	dialogVPanel.setWidth("100%");
	dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
	// Set the animation behaviour
	this.setAnimationEnabled(true);
	// Set the title
	this.setHTML(dialogTitle);
	// Set the Content
	Label contentLabel = new Label();
	contentLabel.setStylePrimaryName("Osyl-DialogBox-ContentLabel");
	contentLabel.setText(dialogContent);
	dialogVPanel.add(contentLabel);
	// Create & add an option panel
	HorizontalPanel optionPanel = new HorizontalPanel();
	optionPanel.setSpacing(5);
	// Add OK button
	okButton = new Button(okButtonLabel);
	okButton.setWidth("60");
	optionPanel.add(okButton);
	okButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		setSelectedValue(true);
		OsylOkCancelDialog.this.hide();
	    }
	});
	// Add Cancel button
	cancelButton = new Button(cancelButtonLabel);
	cancelButton.setWidth("60");
	optionPanel.add(cancelButton);
	cancelButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		setSelectedValue(false);
		OsylOkCancelDialog.this.hide();
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

    public void setOkButtonLabel(String label) {
	this.okButtonLabel = label;
    }

    public void setCancelButtonLabel(String label) {
	this.cancelButtonLabel = label;
    }

    /**
     * Adds a <code>ClickListener</code> to the okButton.
     * 
     * @param clickListener
     */
    public void addOkButtonCLickListener(ClickListener clickListener) {
	okButton.addClickListener(clickListener);
    }

    /**
     * Adds a <code>ClickListener</code> to the cancelButton.
     * 
     * @param clickListener
     */
    public void addCancelButtonClickListener(ClickListener clickListener) {
	cancelButton.addClickListener(clickListener);
    }

    /**
     * Centers the dialog (by invoking
     * {@link OsylEditorEntryPoint#centerObject(com.google.gwt.user.client.ui.PopupPanel)}.
     * This must be invoked after the {@link PopupPanel#show()} method otherwise
     * the centering calculations may be wrong!
     */
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }

    /**
     * Centers the dialog (by invoking
     * {@link OsylEditorEntryPoint#centerObject(com.google.gwt.user.client.ui.PopupPanel)}
     * and gives focus to the cancel button. This must be invoked after the
     * {@link PopupPanel#show()} method otherwise the centering calculations may
     * be wrong!
     */
    public void centerAndFocus() {
	OsylEditorEntryPoint.centerObject(this);
	// We explicitly give the focus to the cancel button for easy dismiss!
	cancelButton.setFocus(true);
    }

}
