/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ImportCOView extends OsylManagerAbstractView {

	private VerticalPanel mainPanel;
	private Label isZipFileLabel;
	private Label fileUploadLabel;
	private CheckBox isZipFile;
	private FileUpload fileUpload;
	private PushButton importSite;

	/**
	 * Constructor.
	 * 
	 * @param controller
	 */
	public ImportCOView(OsylManagerController controller) {
		super(controller);
		initView();
	}

	private void initView() {
		mainPanel = new VerticalPanel();
		final FormPanel formPanel = new FormPanel();
		formPanel.setWidget(mainPanel);
		formPanel.setWidth("95%");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);

		isZipFile = new CheckBox();
		fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		importSite = new PushButton(getController().getMessages().importXML());
		importSite.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				formPanel.setAction(getFormAction());
				formPanel.submit();
			}
		});
		importSite.setWidth("50px");
		isZipFileLabel = new Label(getController().getMessages().isZip());
		fileUploadLabel = new Label(getController().getMessages().file());
		mainPanel.setWidth("100%");
		Label title = new Label(getController().getMessages().importCOTitle());
		title.setStylePrimaryName("OsylManager-form-title");
		mainPanel.add(title);
		mainPanel.add(createFormElement(fileUploadLabel, fileUpload));
		mainPanel.add(createFormElement(isZipFileLabel, isZipFile));
		mainPanel.add(importSite);
		
		PushButton skipButton = new PushButton("Skip");
		skipButton.setWidth("25px");
		skipButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mainPanel.clear();
			}
		});
		mainPanel.add(skipButton);

		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler(){
			/**
			 * Parse the file upload return string JSON String
			 * 
			 * @param jsonString
			 * @return a boolean of the success state
			 */
			public boolean getState(String jsonString) {
				return (jsonString.contains("status\":\"ok"));
			}

			public String getURL(String jsonString){
				String url = jsonString.substring(jsonString.indexOf("\"url\":\"")+7);
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
					if (isZipFile.getValue()) {
						getController().readZip(url);
					} else {
						getController().readXML(url);
					}
				} else {
					Window
					.alert(getController().getMessages()
							.siteNotCreated());
					return;
				}
			}
		}); // new FormHandler (inner class)
		initWidget(formPanel);
	}

	private String getFormAction() {
		String url = GWT.getModuleBaseURL();
		String cleanUrl = url.substring(0, url.indexOf("/", 8));
		String formAction =
			cleanUrl + "/sdata/c/group/" + getController().getSiteId() + "/";
		return formAction;
	}

}
