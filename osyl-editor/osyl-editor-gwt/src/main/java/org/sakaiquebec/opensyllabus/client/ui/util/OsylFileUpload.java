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
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * Pop-up window to show a file upload form. The file is saved on the server and
 * the client-side model is modified accordingly.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylFileUpload extends WindowPanel implements OsylViewControllable,
	FiresUploadFileEvents {

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

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

    /**
     * Constructor.
     * 
     * @param osylController
     */
    public OsylFileUpload(OsylController osylController,String currentDirectory) {

    // set some properties for WindowPanel
    setResizable(false);
    setAnimationEnabled(true);
    setCaptionAction(null);
    	
	setController(osylController);
	this.currentFolder = currentDirectory;
	uiMessages = osylController.getUiMessages();
	table = new FlexTable();

	// Create a FormPanel and point it at a service.
	final FormPanel form = new FormPanel();
	form.setWidget(table);

	if (!getController().isInHostedMode()) {
		form.setAction(OsylRemoteServiceLocator.getUploadFileUrl(this.currentFolder));
	}

	// Because we're going to add a FileUpload widget, we'll need to set the
	// form to use the POST method, and multipart MIME encoding.
	form.setEncoding(FormPanel.ENCODING_MULTIPART);
	form.setMethod(FormPanel.METHOD_POST);

	final Label titleLabel =
		new Label(uiMessages.getMessage("fileUpload.addResource"));
	int row = 0;
	table.setWidget(row, 0, titleLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	titleLabel.setStylePrimaryName("Osyl-FileUpload-resource-title");

	//display file size limit
	row++;
	final Label sizeLabel =
		new Label(uiMessages.getMessage("fileUpload.size.limit"));
	table.setWidget(row, 0, sizeLabel);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	sizeLabel.setStylePrimaryName("Osyl-FileUpload-information");
	
	// Create a FileUpload widget.
	row++;
	final FileUpload upload = new FileUpload();
	upload.setName("uploadFormElement");
	table.setWidget(row, 0, upload);
	((FlexCellFormatter) table.getCellFormatter()).setColSpan(row, 0, 2);
	

	// Add a 'save' button.
	AbstractImagePrototype imgSaveButton = osylImageBundle.save();
	saveButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgSaveButton, uiMessages.getMessage("save"));
	saveButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		form.submit();
	    }
	});
	saveButton.setStylePrimaryName("Osyl-FileUpload-genericButton");

	// Add a 'Cancel' button.
	AbstractImagePrototype imgCancelButton =
		osylImageBundle.action_cancel();
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

	row++;

	// Add an event handler to the form.
	form.addFormHandler(new FormHandler() {
	    public void onSubmit(FormSubmitEvent event) {
		// filename.setValue(upload.getFilename());
		// This event is fired just before the form is submitted. We can
		// take this opportunity to perform validation.
		String fn = upload.getFilename();
		if (fn.length() == 0) {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(false, true, uiMessages
				    .getMessage("fileUpload.plsSelectFile"));
		    // get index of file upload form to set z-index of alert window
		    int index = new Integer(DOM.getStyleAttribute(
		    		OsylFileUpload.this.getElement(), "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    event.setCancelled(true);
		}
		else {
		    cancelButton.setEnabled(false);
		    saveButton.setEnabled(false);
		}
	    }

	    /*
	     * When the form submission is successfully completed, this event is
	     * fired. SDATA returns an event of type JSON.
	     */
	    public void onSubmitComplete(FormSubmitCompleteEvent event) {
		if (getState(event.getResults())) {
		    hide();
		    OsylUnobtrusiveAlert alert =
			    new OsylUnobtrusiveAlert(uiMessages
				    .getMessage("fileUpload.resSaved"));
		    OsylEditorEntryPoint.showWidgetOnTop(alert);
		    
		    // parse the handler list 
		   for(Iterator<UploadFileEventHandler> it = uploadEvtHandlerList.iterator();it.hasNext();){
			it.next().onUploadFile(new UploadFileEvent(getPath(event.getResults())));
		    }

		    
		} else {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(
				    false,
				    true, 
				    "Alert - Upload Error",
				    uiMessages
					    .getMessage("fileUpload.resNotSaved"));
		    // get index of file upload form to set z-index of alert window
		    int index = new Integer(DOM.getStyleAttribute(
		    		OsylFileUpload.this.getElement(), "zIndex"));
		    alertBox.setZIndex(index + 1);
		    alertBox.center();
		    alertBox.show();
		    cancelButton.setEnabled(true);
		    saveButton.setEnabled(true);
		    return;
		}
	    } // onSubmitComplete
	}); // new FormHandler (inner class)

	setWidget(form);
	setStylePrimaryName("Osyl-FileUpload-uploadForm");
    } // Constructor

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
	String s = jsonString.substring(jsonString.indexOf("\"url\":\"")+7);
	s=s.substring(0, s.indexOf("\""));
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
