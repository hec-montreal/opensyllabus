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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * First version of an OsylDialog package OsylOKCancelDialog extends GWT.
 * Further development should be based on SWING JOptionPane:
 * http://java.sun.com/javase/6/docs/api/javax/swing/JOptionPane.html and the
 * SWING tutorial : How to Make Dialogs
 * http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html
 * OsylAlertDialog coding example: final OsylAlertDialog alertBox = new
 * OsylAlertDialog(false, true, "Dialog Title", "The content of the Dialog");
 * alertBox.center(); alertBox.show();
 * 
 * @param autoHide : true if the dialog should be automatically hidden when the
 *                user clicks outside of it
 * @param modal : true if keyboard and mouse events for widgets not contained by
 *                the dialog should be ignored
 * @param dialogTitle : the title of the dialog
 * @param dialogContent : the text content of the dialog
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public class OsylAlertDialog extends OsylAbstractLightBox {

    private Button okButton;

    /**
     * User interface message bundle
     */
    private static OsylConfigMessages uiMessages =
	    OsylController.getInstance().getUiMessages();

    /**
     * Constructor specifying content and using default title and standard
     * behavior (autohide is false and modal is true).
     * 
     * @param dialogContent : the text content of the dialog
     */
    public OsylAlertDialog(String dialogContent) {
	this(false, true, "", dialogContent);
    }

    /**
     * Constructor specifying title and content and using standard behavior
     * (autohide is false and modal is true).
     * 
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylAlertDialog(String dialogTitle, String dialogContent) {
	this(false, true, dialogTitle, dialogContent);
    }

    /**
     * Constructor with default title and specific behavior. Unless uncommon
     * behavior is needed (either non-modal or autohide), this constructor
     * should not be used. Use {@link OsylAlertDialog#OsylAlertDialog(String)}
     * instead.
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *                the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *                contained by the dialog should be ignored
     * @param dialogContent : the text content of the dialog
     */
    public OsylAlertDialog(boolean autoHide, boolean modal, String dialogContent) {
	this(autoHide, modal, uiMessages.getMessage("OsylAlertDialog_Title"),
		dialogContent);
    }

    /**
     * Constructor specifying all parameters. Unless uncommon behavior is needed
     * (either non-modal or autohide), this constructor should not be used. Use
     * {@link OsylAlertDialog#OsylAlertDialog(String, String)} instead.
     * 
     * @param autoHide : true if the dialog should be automatically hidden when
     *                the user clicks outside of it
     * @param modal : true if keyboard and mouse events for widgets not
     *                contained by the dialog should be ignored
     * @param dialogTitle : the title of the dialog
     * @param dialogContent : the text content of the dialog
     */
    public OsylAlertDialog(boolean autoHide, boolean modal, String dialogTitle,
	    String dialogContent) {
	super(autoHide, modal);
	setPixelSize(243, 150);
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
	okButton =
		new Button(uiMessages.getMessage("OsylAlertDialog_Ok_Button"));
	okButton.setWidth("60");
	optionPanel.add(okButton);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		OsylAlertDialog.this.hide();
	    }
	});
	dialogVPanel.add(optionPanel);
	// Set the contents of the Widget
	// DialogBox is a SimplePanel, so you have to set its widget property to
	// whatever you want its contents to be.
	this.setWidget(dialogVPanel);
    }

    /** {@inheritDoc} */
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }

    /**
     * {@inheritDoc}
     */
    public void show() {
	super.show();

	// As the window'd default behavior is to grow only vertically, we
	// use its size to compute its current area and we recompute its size
	// according to a standard w/h ratio.
	
	// We remove the decoration width and height before computing the area
	int decoration_w = 20;
	int decoration_h = 50;

	// We compute the area dedicated to the content
	int area = (getOffsetWidth() - decoration_w)
		* (getOffsetHeight() - decoration_h);

	// We compute a new width and height for the content
	int new_w = (int) (Math.sqrt(area) * 1.7);
	int new_h = area / new_w;
	// and we add the decoration paddings:
	new_w += decoration_w;
	new_h += decoration_h;
	setPixelSize(new_w, new_h);

	// Now we can center the window
	center();

	// We explicitly give the focus to the OK button for easy dismiss!
	okButton.setFocus(true);
    }
}
