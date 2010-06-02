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

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.helper.ActionHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
	    Arrays.asList(new String[] { "en", "es", "fr_CA" });// TODO to be
    // parameterized

    private static final int FORM_WIDTH = 580;

    private TextBox nameTextBox;

    private ListBox configListBox;

    private ListBox langListBox;
    
    private FileUpload fileUpload;

    private PushButton importSiteButton;
    
    private String siteId;
    
    private final FormPanel formPanel;

    AsyncCallback<Map<String, String>> configListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    importSiteButton.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			Window.alert(controller.getMessages()
				.noAssociableCOSite());
			importSiteButton.setEnabled(false);
		    } else {
			for (Iterator<String> configMapKeysIterator =
				result.keySet().iterator(); configMapKeysIterator
				.hasNext();) {
			    String configId = configMapKeysIterator.next();
			    String configRef = result.get(configId);
			    try{
			    String configTitle =
				    controller.getMessages().getString(
					    "config_" + configRef);
			    configListBox.addItem(configTitle, configRef);
			    }catch (Exception e) {
			    }
			}
		    }
		}

	    };

    public ImportNewSiteForm(final OsylManagerController controller) {
	super(controller);

	Label l = new Label(controller.getMessages().createSiteTitle());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);

	Label title = new Label(controller.getMessages().courseName());
	nameTextBox = new TextBox();
	mainPanel.add(createPanel(title, nameTextBox));

	Label langTitle = new Label(controller.getMessages().chooseLang());
	langListBox = new ListBox();
	
	for (Iterator<String> langIter = supportedLang.iterator(); langIter
		.hasNext();) {
	    String lang = langIter.next();
	    langListBox.addItem(controller.getMessages().getString(
		    "language_" + lang), lang);
	}
	mainPanel.add(createPanel(langTitle, langListBox));

	Label configTitle = new Label(controller.getMessages().chooseConfig());
	configListBox = new ListBox();
	controller.getOsylConfigs(configListAsyncCallback);
	mainPanel.add(createPanel(configTitle, configListBox));
	
	Label fileUploadLabel = new Label(controller.getMessages().file());

	fileUpload = new FileUpload();
	fileUpload.setName("uploadFormElement");
	mainPanel.add(createPanel(fileUploadLabel, fileUpload));
	importSiteButton = new PushButton(controller.getMessages().create());
	importSiteButton.setWidth("30px");
	
	formPanel = new FormPanel();
	formPanel.setWidget(mainPanel);
	formPanel.setWidth("95%");
	formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
	formPanel.setMethod(FormPanel.METHOD_POST);
	formPanel
		.addSubmitCompleteHandler(new SubmitCompleteHandler() {
		    /**
		     * Parse the file upload return string JSON
		     * String
		     * 
		     * @param jsonString
		     * @return a boolean of the success state
		     */
		    public boolean getState(String jsonString) {
			return (jsonString
				.contains("status\":\"ok"));
		    }

		    public String getURL(String jsonString) {
			String url =
				jsonString
					.substring(jsonString
						.indexOf("\"url\":\"") + 7);
			url =
				url.substring(0, url
					.indexOf("\""));
			return url;
		    }

		    /*
		     * When the form submission is successfully
		     * completed, this event is fired. SDATA
		     * returns an event of type JSON.
		     */
		    public void onSubmitComplete(
			    SubmitCompleteEvent event) {
			String retourJSON = event.getResults();
			if (getState(event.getResults())) {
			    String url = getURL(retourJSON);
			    controller.importData(url, siteId);
			} else {
			    Window.alert(controller
				    .getMessages()
				    .siteNotCreated());
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
		    if (configListBox.getSelectedIndex() != -1) {
			String configRef =
				configListBox.getValue(configListBox
					.getSelectedIndex());
			controller.createSite(name, configRef, lang);
		    } else {
			Window.alert(controller.getMessages().noConfig());
		    }
		} else {
		    Window.alert(controller.getMessages().siteNameNotValid());
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
	    Label l = new Label(controller.getMessages().createSiteTitle());
	    l.setStylePrimaryName("OsylManager-form-title");
	    mainPanel.add(l);
	    mainPanel.add(new Label(controller.getMessages()
		    .siteForm_create_ok()));
	    Anchor edit = new Anchor();
	    edit.setText(controller.getMessages().createForm_edit());
	    edit.setStylePrimaryName("OsylManager-action");
	    edit.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    ActionHelper.editSite(siteId);
		}
	    });
	    mainPanel.add(edit);

	    PushButton closeButton =
		    new PushButton(controller.getMessages().form_close());
	    closeButton.setWidth("40px");
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
