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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreateSiteForm extends OsylManagerAbstractWindowPanel implements
	OsylManagerEventHandler {

    private static List<String> supportedLang =
	    Arrays.asList(new String[] { "en", "es", "fr_CA" });// TODO to be
    // parameterized

    private static final int FORM_WIDTH = 580;

    private TextBox nameTextBox;

    private ListBox configListBox;

    private ListBox langListBox;

    private PushButton createSite;

    AsyncCallback<Map<String, String>> configListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    createSite.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			Window.alert(controller.getMessages()
				.noAssociableCOSite());
			createSite.setEnabled(false);
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

    public CreateSiteForm(final OsylManagerController controller) {
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

	createSite = new PushButton(controller.getMessages().create());
	createSite.setWidth("30px");
	createSite.setStylePrimaryName("Osyl-Button");
	createSite.addClickHandler(new ClickHandler() {
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
	mainPanel.add(createSite);
	mainPanel.setCellHorizontalAlignment(createSite,
		HasHorizontalAlignment.ALIGN_CENTER);
//	mainPanel.setWidth(FORM_WIDTH + "px");

	controller.addEventHandler(this);

    }

    public void onOsylManagerEvent(final OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_CREATION_EVENT) {
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
		    ActionHelper.editSite((String) e.getSource());
		}
	    });
	    mainPanel.add(edit);

	    PushButton closeButton =
		    new PushButton(controller.getMessages().form_close());
	    closeButton.setWidth("40px");
	    closeButton.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    CreateSiteForm.super.hide();
		}
	    });
	    mainPanel.add(closeButton);
	    mainPanel.setCellHorizontalAlignment(closeButton,
			HasHorizontalAlignment.ALIGN_CENTER);
	}
    }

}
