/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.helper.ActionHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ImportForm extends OsylManagerAbstractWindowPanel implements
	OsylManagerEventHandler {

    private static final int FORM_WIDTH = 580;

    private FileUpload fileUpload;

    private PushButton importSiteButton;

    private String siteId;
    
    private final OsylCancelDialog diag;

    public ImportForm(final OsylManagerController controller,
	    final String siteId, OsylCancelDialog adiag) {
	super(controller);
	this.siteId = siteId;
	this.diag = adiag;
	final FormPanel formPanel = new FormPanel();
	formPanel.setWidget(mainPanel);
	formPanel.setWidth("95%");
	formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	formPanel.setMethod(FormPanel.METHOD_POST);

	Label title = new Label(controller.getMessages().importCOTitle());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label fileUploadLabel = new Label(controller.getMessages().file());

	fileUpload = new FileUpload();
	fileUpload.setName("uploadFormElement");
	mainPanel.add(createPanel(fileUploadLabel, fileUpload));

	importSiteButton = new PushButton(controller.getMessages().importXML());
	importSiteButton.setWidth("50px");
	importSiteButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		diag.show();
		diag.centerAndFocus();
		formPanel.setAction(getFormAction());
		formPanel.submit();
	    }
	});
	mainPanel.add(importSiteButton);
	mainPanel.setCellHorizontalAlignment(importSiteButton,
		HasHorizontalAlignment.ALIGN_CENTER);

	formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	    /**
	     * Parse the file upload return string JSON String
	     * 
	     * @param jsonString
	     * @return a boolean of the success state
	     */
	    public boolean getState(String jsonString) {
		return (jsonString.contains("status\":\"ok"));
	    }

	    public String getURL(String jsonString) {
		String url =
			jsonString
				.substring(jsonString.indexOf("\"url\":\"") + 7);
		url = url.substring(0, url.indexOf("\""));
		return url;
	    }

	    /*
	     * When the form submission is successfully completed, this event is
	     * fired. SDATA returns an event of type JSON.
	     */
	    public void onSubmitComplete(SubmitCompleteEvent event) {
		diag.hide(true);
		String retourJSON = event.getResults();
		if (getState(event.getResults())) {
		    String url = getURL(retourJSON);
		    controller.importData(url, siteId);
		} else {
		    OsylOkCancelDialog warning = new OsylOkCancelDialog(false,
			    true, messages.OsylWarning_Title(),
			    messages.siteNotCreated(), true, false);
		    warning.show();
		    warning.centerAndFocus();
		    return;
		}
	    }
	}); // new FormHandler (inner class)

	mainPanel.setWidth(FORM_WIDTH + "px");
	setWidget(formPanel);
	controller.addEventHandler(this);
    }

    private String getFormAction() {
	String url = GWT.getModuleBaseURL();
	String cleanUrl = url.substring(0, url.indexOf("/", 8));
	String formAction = cleanUrl + "/sdata/c/group/" + siteId + "/";
	return formAction;
    }

    public void onOsylManagerEvent(final OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_IMPORT_EVENT) {
	    mainPanel.clear();
	    Label l = new Label(messages.importCOTitle());
	    l.setStylePrimaryName("OsylManager-form-title");
	    mainPanel.add(l);
	    mainPanel.add(new Label(messages.importForm_import_ok()));
	    Anchor edit = new Anchor();
	    edit.setText(messages.createForm_edit());
	    edit.setStylePrimaryName("OsylManager-action");
	    edit.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    ActionHelper.editSite(siteId);
		}
	    });
	    mainPanel.add(edit);

	    PushButton closeButton =
		    new PushButton(messages.form_close());
	    closeButton.setWidth("40px");
	    closeButton.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    ImportForm.super.hide();
		}
	    });
	    mainPanel.add(closeButton);
	    mainPanel.setCellHorizontalAlignment(closeButton,
		    HasHorizontalAlignment.ALIGN_CENTER);
	    setWidget(mainPanel);
	}

    }
}
