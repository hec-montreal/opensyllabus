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

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gwt.mosaic.core.client.DOM;
import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresUploadFileEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler.UploadFileEvent;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Pop-up window to show a file upload form. The file is saved on the server and
 * the client-side model is modified accordingly.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylFileUpload extends WindowPanel implements
	OsylViewControllable, FiresUploadFileEvents {

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

    private OsylConfigMessages coMessages;

    // UI components
    private FlexTable table;

    private OsylController osylController;

    private String currentFolder;

    ImageAndTextButton cancelButton;

    ImageAndTextButton saveButton;

    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    private List<UploadFileEventHandler> uploadEvtHandlerList =
	    new ArrayList<UploadFileEventHandler>();

    private List<String> rightsList;

    private ListBox rightsListBox;

    private String right;

    private List<String> typesResourceList;

    private ListBox typesResourceListBox;

    private String typeResource;

    /**
     * Constructor.
     * 
     * @param osylController
     */
    public OsylFileUpload(String title, OsylController osylController,
	    String currentDirectory, List<String> rightsList,
	    List<String> typesResourceList) {
	super(title);
	// set some properties for WindowPanel
	setResizable(false);
	setAnimationEnabled(true);
	setCaptionAction(null);
	setController(osylController);
	this.currentFolder = currentDirectory;
	this.rightsList = rightsList;
	this.typesResourceList = typesResourceList;
	uiMessages = osylController.getUiMessages();
	coMessages = osylController.getCoMessages();

	table = new FlexTable();

	// Create a FormPanel and point it at a service.
	final FormPanel form = new FormPanel();
	form.setWidget(table);

	if (!getController().isInHostedMode()) {
	    form.setAction(OsylRemoteServiceLocator
		    .getUploadFileUrl(this.currentFolder));
	}

	// Because we're going to add a FileUpload widget, we'll need to set the
	// form to use the POST method, and multipart MIME encoding.
	form.setEncoding(FormPanel.ENCODING_MULTIPART);
	form.setMethod(FormPanel.METHOD_POST);

	// display file size limit
	int row = 0;
	final HTML mandatoryLabel =
		new HTML(
			OsylAbstractEditor.MANDATORY_FIELD_INDICATOR
				+ osylController
					.getUiMessage("Global.fields.mandatory"));
	table.setWidget(row, 0, mandatoryLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	mandatoryLabel.setStylePrimaryName("Osyl-FileUpload-information");
	((FlexCellFormatter) table.getCellFormatter()).setVerticalAlignment(
		row, 0, HasVerticalAlignment.ALIGN_BOTTOM);

	row++;
	final Label sizeLabel =
		new Label(uiMessages.getMessage("fileUpload.size.limit"));
	table.setWidget(row, 0, sizeLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	sizeLabel.setStylePrimaryName("Osyl-FileUpload-information");
	((FlexCellFormatter) table.getCellFormatter()).setVerticalAlignment(
		row, 0, HasVerticalAlignment.ALIGN_BOTTOM);

	// Create a FileUpload widget.
	row++;
	final FileUpload upload = new FileUpload();
	upload.setName("uploadFormElement");
	table.setWidget(row, 0, upload);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);

	// -----------------------------------------------------------------------
	row++;
	final HTML rightsLabel =
		new HTML(uiMessages.getMessage("fileUpload.rights")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":");
	table.setWidget(row, 0, rightsLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	rightsLabel.setStylePrimaryName("Osyl-FileUpload-information");
	((FlexCellFormatter) table.getCellFormatter()).setVerticalAlignment(
		row, 0, HasVerticalAlignment.ALIGN_BOTTOM);

	// Add a "choose rights status" listbox
	rightsListBox = new ListBox();
	// TODO erreur sur la ligne 157 rightsList probablement null.
	for (String license : this.rightsList) {
	    rightsListBox.addItem(license);
	}
	rightsListBox.setItemSelected(0, true);
	rightsListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		setRight(rightsListBox.getItemText(rightsListBox
			.getSelectedIndex()));
	    }
	});

	row++;
	table.setWidget(row, 0, rightsListBox);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);

	// -----------------------------------------------------------------------
	row++;
	final HTML typeResourceLabel =
		new HTML(uiMessages.getMessage("fileUpload.typesResource")
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR + ":");
	table.setWidget(row, 0, typeResourceLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	typeResourceLabel.setStylePrimaryName("Osyl-FileUpload-information");
	((FlexCellFormatter) table.getCellFormatter()).setVerticalAlignment(
		row, 0, HasVerticalAlignment.ALIGN_BOTTOM);

	// Add a "type Resources" listbox
	typesResourceListBox = new ListBox();
	typesResourceListBox.addItem(uiMessages
		.getMessage("DocumentEditor.documentType.choose"));
	for (String typeDocument : this.typesResourceList) {
	    typesResourceListBox.addItem(
		    coMessages.getMessage("Resource.Type." + typeDocument),
		    typeDocument);
	}
	typesResourceListBox.setItemSelected(0, true);
	typesResourceListBox.addChangeHandler(new ChangeHandler() {
	    public void onChange(ChangeEvent event) {
		setTypeResource(typesResourceListBox
			.getValue(typesResourceListBox.getSelectedIndex()));
	    }
	});

	row++;
	table.setWidget(row, 0, typesResourceListBox);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	// -----------------------------------------------------------------------
	// Add a 'save' button.
	AbstractImagePrototype imgSaveButton =
		AbstractImagePrototype.create(osylImageBundle.save());
	saveButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
	// AbstractImagePrototype
		imgSaveButton, uiMessages.getMessage("save"));
	saveButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		form.submit();
	    }
	});
	saveButton.setEnabled(true);
	saveButton.setStylePrimaryName("Osyl-FileUpload-genericButton");

	// Add a 'Cancel' button.
	AbstractImagePrototype imgCancelButton =
		AbstractImagePrototype.create(osylImageBundle.action_cancel());
	cancelButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
	// AbstractImagePrototype
		imgCancelButton, uiMessages.getMessage("cancel"));
	cancelButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		cancel();
	    }
	});
	cancelButton.setStylePrimaryName("Osyl-FileUpload-genericButton");

	// Create a panel for centering both together
	row++;
	HorizontalPanel buttonPanel = new HorizontalPanel();
	buttonPanel.add(saveButton);
	buttonPanel.add(cancelButton);
	table.setWidget(row, 0, buttonPanel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	((FlexCellFormatter) table.getCellFormatter()).setHorizontalAlignment(
		row, 0, HasHorizontalAlignment.ALIGN_CENTER);
	((FlexCellFormatter) table.getCellFormatter()).setVerticalAlignment(
		row, 0, HasVerticalAlignment.ALIGN_BOTTOM);
	row++;

	// Add an event handler to the form.
	form.addSubmitHandler(new SubmitHandler() {

	    public void onSubmit(SubmitEvent event) {
		// filename.setValue(upload.getFilename());
		// This event is fired just before the form is submitted. We can
		// take this opportunity to perform validation.
		String fn = upload.getFilename();
		String message = "";

		if (fn.length() == 0) {
		    message = uiMessages.getMessage("fileUpload.plsSelectFile");
		} else if (rightsListBox.getSelectedIndex() == 0) {
		    message =
			    uiMessages
				    .getMessage("fileUpload.chooseRightsStatus");
		} else if (typesResourceListBox.getSelectedIndex() == 0) {
		    message =
			    uiMessages
				    .getMessage("fileUpload.chooseTypesResourceStatus");
		}

		if (message.equals("")) {
		    cancelButton.setEnabled(false);
		    saveButton.setEnabled(false);
		} else {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(uiMessages
				    .getMessage("Global.error"), message);
		    alertBox.center();
		    alertBox.show();
		    event.cancel();
		}
	    }
	});

	form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

	    public void onSubmitComplete(SubmitCompleteEvent event) {
		if (getState(event.getResults())) {
		    hide();
		    OsylUnobtrusiveAlert alert =
			    new OsylUnobtrusiveAlert(uiMessages
				    .getMessage("fileUpload.resSaved"));
		    OsylEditorEntryPoint.showWidgetOnTop(alert);

		    // parse the handler list
		    for (Iterator<UploadFileEventHandler> it =
			    uploadEvtHandlerList.iterator(); it.hasNext();) {
			it.next()
				.onUploadFile(
					new UploadFileEvent(getPath(event
						.getResults())));
		    }

		} else {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(
				    false,
				    true,
				    "Alert - Upload Error",
				    uiMessages
					    .getMessage("fileUpload.resNotSaved"));
		    // get index of file upload form to set z-index of alert
		    // window
		    int index =
			    new Integer(DOM.getStyleAttribute(
				    OsylFileUpload.this.getElement(), "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    cancelButton.setEnabled(true);
		    saveButton.setEnabled(true);
		    return;
		}
	    }
	});

	setWidget(form);
	setStylePrimaryName("Osyl-FileUpload-uploadForm");
    } // Constructor

    private void setRight(String right) {
	this.right = right;
    }

    public String getRight() {
	return right;
    }

    private void setTypeResource(String typeResource) {
	this.typeResource = typeResource;
    }

    public String getTypeResource() {
	return typeResource;
    }

    /**
     * Parse the JSON String returned after file upload
     * 
     * @param jsonString
     * @return a boolean of the success state
     */
    public boolean getState(String jsonString) {
	return (jsonString.contains("status\":\"ok"));
    }

    /**
     * Gets the resource name
     * 
     * @param jsonString
     * @return The resource name
     */
    public String getPath(String jsonString) {
	String s = jsonString.substring(jsonString.indexOf("\"url\":\"") + 7);
	s = s.substring(0, s.indexOf("\""));
	return s;
    }

    private void cancel() {
	hide();
    }

    public OsylController getController() {
	return osylController;
    }

    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }

    public void addEventHandler(UploadFileEventHandler handler) {
	this.uploadEvtHandlerList.add(handler);
    }

    public void removeEventHandler(UploadFileEventHandler handler) {
	this.uploadEvtHandlerList.remove(handler);

    }

    /**
     * Centers window with OsylEditorEntryPoint
     */
    @Override
    public void center() {
	OsylEditorEntryPoint.centerObject(this);
    }
}
