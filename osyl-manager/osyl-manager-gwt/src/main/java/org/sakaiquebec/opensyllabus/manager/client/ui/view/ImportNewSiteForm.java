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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.helper.ActionHelper;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class ImportNewSiteForm extends OsylManagerAbstractWindowPanel implements
	OsylManagerEventHandler {

    private static List<String> supportedLang =
	    Arrays.asList(new String[] { "fr_CA", "en", "es", });// TODO to be
    // parameterized

    private static final int FORM_WIDTH = 580;

    private TextBox nameTextBox;

    private ListBox configListBox;

    private ListBox langListBox;

    private FileUpload fileUpload;

    private PushButton importSiteButton;

    private String siteId;

    private final FormPanel formPanel;

    private final OsylOkCancelDialog warning;

    private final OsylCancelDialog diag;

    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
	public void onSuccess(Void serverResponse) {
	    diag.hide();
	    controller.readCB();
	}

	public void onFailure(Throwable error) {
	    String msg = error.getMessage();
	    if(error instanceof OsylPermissionException)
		msg = messages.permission_exception();
	    OsylOkCancelDialog warning =
		    new OsylOkCancelDialog(false, true, messages
			    .OsylWarning_Title(), msg, true,
			    false);
	    warning.show();
	    warning.centerAndFocus();
	}
    };

    AsyncCallback<Map<String, String>> configListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    importSiteButton.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			OsylOkCancelDialog warning =
				new OsylOkCancelDialog(false, true, messages
					.OsylWarning_Title(), messages
					.noAssociableCOSite(), true, false);
			warning.show();
			warning.centerAndFocus();
			importSiteButton.setEnabled(false);
		    } else {
			for (Entry<String, String> entry : result.entrySet()) {
			    String configRef = entry.getValue();
			    try {
				String configTitle =
					messages.getString("config_"
						+ configRef);
				configListBox.addItem(configTitle, configRef);
			    } catch (Exception e) {
			    }
			}
		    }
		}

	    };

    public ImportNewSiteForm(final OsylManagerController controller,
	    OsylCancelDialog aDiag) {
	super(controller);
	this.diag = aDiag;
	warning =
		new OsylOkCancelDialog(false, true, messages
			.OsylWarning_Title(), "", true, false);

	Label l = new Label(messages.createSiteTitle());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);

	Label title = new Label(messages.courseName());
	nameTextBox = new TextBox();
	mainPanel.add(createPanel(title, nameTextBox));

	Label langTitle = new Label(messages.chooseLang());
	langListBox = new ListBox();

	for (Iterator<String> langIter = supportedLang.iterator(); langIter
		.hasNext();) {
	    String lang = langIter.next();
	    langListBox.addItem(messages.getString("language_" + lang), lang);
	}
	mainPanel.add(createPanel(langTitle, langListBox));

	Label configTitle = new Label(messages.chooseConfig());
	configListBox = new ListBox();
	controller.getOsylConfigs(configListAsyncCallback);
	mainPanel.add(createPanel(configTitle, configListBox));

	Label fileUploadLabel = new Label(messages.file());

	fileUpload = new FileUpload();
	fileUpload.setName("uploadFormElement");
	mainPanel.add(createPanel(fileUploadLabel, fileUpload));
	importSiteButton = new PushButton(messages.create());
	importSiteButton.setWidth("50px");
	importSiteButton.setStylePrimaryName("Osyl-Button");
	formPanel = new FormPanel();
	formPanel.setWidget(mainPanel);
	formPanel.setWidth("95%");
	formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	formPanel.setMethod(FormPanel.METHOD_POST);
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
		String retourJSON = event.getResults();
		if (getState(event.getResults())) {
		    String url = getURL(retourJSON);
		    controller.importData(url, siteId, callback);
		} else {
		    diag.hide();
		    warning.setText(messages.siteNotCreated());
		    warning.show();
		    warning.centerAndFocus();
		    return;
		}
	    }
	}); // new FormHandler (inner class)

	importSiteButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		boolean nameValid = false;
		String name = nameTextBox.getText();
		String lang =
			langListBox.getValue(langListBox.getSelectedIndex());
		nameValid =
			(name != null
				&& name
					.matches("^[a-zA-Z0-9áàâÁÀÂçÇéèêëÉÈÊËíîïÍÎÏñÑóôÓÔúùüÚÙÜ][ a-zA-Z0-9a-zA-Z0-9áàâÁÀÂçÇéèêëÉÈÊËíîïÍÎÏñÑóôÓÔúùüÚÙÜ\\._-]*") && name
				.matches(".*[\\S]$"));
		if (nameValid) {
		    diag.show();
		    diag.centerAndFocus();

		    if (configListBox.getSelectedIndex() != -1) {
			String configRef =
				configListBox.getValue(configListBox
					.getSelectedIndex());
			controller.createSite(name, configRef, lang);
		    } else {
			warning.setText(messages.noConfig());
			warning.show();
			warning.centerAndFocus();
		    }
		} else {
		    warning.setText(messages.siteNameNotValid());
		    warning.show();
		    warning.centerAndFocus();
		}
	    }
	});
	mainPanel.add(importSiteButton);
	mainPanel.setCellHorizontalAlignment(importSiteButton,
		HasHorizontalAlignment.ALIGN_CENTER);
	mainPanel.setWidth(FORM_WIDTH + "px");
	setWidget(formPanel);

	controller.addEventHandler(this);

    }

    public void onOsylManagerEvent(final OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_CREATION_EVENT) {
	    siteId = (String) e.getSource();
	    formPanel.setAction(getFormAction(siteId));
	    formPanel.submit();
	} else if (e.getType() == OsylManagerEvent.SITE_IMPORT_EVENT) {

	    mainPanel.clear();
	    Label l = new Label(messages.createSiteTitle());
	    l.setStylePrimaryName("OsylManager-form-title");
	    mainPanel.add(l);
	    mainPanel.add(new Label(messages.siteForm_create_ok()));
	    Anchor edit = new Anchor();
	    edit.setText(messages.createForm_edit());
	    edit.setStylePrimaryName("OsylManager-action");
	    edit.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    ActionHelper.editSite(siteId);
		}
	    });
	    mainPanel.add(edit);

	    PushButton closeButton = new PushButton(messages.form_close());
	    closeButton.setWidth("40px");
	    closeButton.setStylePrimaryName("Osyl-Button");
	    closeButton.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    ImportNewSiteForm.super.hide();
		}
	    });
	    mainPanel.add(closeButton);
	    mainPanel.setCellHorizontalAlignment(closeButton,
		    HasHorizontalAlignment.ALIGN_CENTER);
	}
    }

    private String getFormAction(String siteId) {
	String url = GWT.getModuleBaseURL();
	String cleanUrl = url.substring(0, url.indexOf("/", 8));
	String formAction = cleanUrl + "/sdata/c/group/" + siteId + "/";
	return formAction;
    }
}
