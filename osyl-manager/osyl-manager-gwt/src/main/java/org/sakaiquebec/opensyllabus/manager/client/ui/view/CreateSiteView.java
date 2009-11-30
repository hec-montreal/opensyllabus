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

import java.util.Iterator;
import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreateSiteView extends OsylManagerAbstractView {

    private Label nameLabel;

    private TextBox nameTextBox;

    private ListBox configListBox;

    private PushButton createSite;

    private VerticalPanel mainPanel;

    AsyncCallback<Map<String, String>> configListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    createSite.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			Window.alert(getController().getMessages()
				.noAssociableCOSite());// TODO
			createSite.setEnabled(false);
		    } else {
			for (Iterator<String> configMapKeysIterator =
				result.keySet().iterator(); configMapKeysIterator
				.hasNext();) {
			    String configId = configMapKeysIterator.next();	
			    String configTitle = getController().getMessages().getString("config_" + result.get(configId) ) ;
			    configListBox.addItem(configTitle, configId);
			}
		    }
		}

	    };

    /**
     * Constructor.
     * 
     * @param controller
     */
    public CreateSiteView(OsylManagerController controller) {
	super(controller);
	initView();
    }

    private void initView() {

	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	nameTextBox = new TextBox();
	nameLabel = new Label(getController().getMessages().courseName());
	mainPanel.setWidth("100%");
	Label title =
		new Label(getController().getMessages().createSiteTitle());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);
	mainPanel.add(createFormElement(nameLabel, nameTextBox));

	Label configTitle =
		new Label(getController().getMessages().chooseConfig());
	configListBox = new ListBox();
	getController().getOsylConfigs(configListAsyncCallback);// récupération
	// de la liste
	// des configs
	// dispo
	mainPanel.add(createFormElement(configTitle, configListBox));

	createSite = new PushButton(getController().getMessages().create());
	createSite.setWidth("30px");
	createSite.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		boolean nameValid = false;
		String name = nameTextBox.getText();
		//TODO Maybe we should make a blacklist of forbidden characters
		//instead of a whitelist of authorized characters
		nameValid =
			(name != null
				&& name
					.matches("^[a-zA-Z0-9áàâÁÀÂçÇéèêëÉÈÊËíîïÍÎÏñÑóôÓÔúùüÚÙÜ][ a-zA-Z0-9a-zA-Z0-9áàâÁÀÂçÇéèêëÉÈÊËíîïÍÎÏñÑóôÓÔúùüÚÙÜ\\._-]*") && name
				.matches(".*[\\S]$"));
		if (nameValid) {
		    if (configListBox.getSelectedIndex() != -1) {
			String configId =
				configListBox.getValue(configListBox
					.getSelectedIndex());
			getController().createSite(name, configId);
		    } else {
			Window.alert(getController().getMessages().noConfig());
		    }
		} else {
		    Window.alert(getController().getMessages()
			    .siteNameNotValid());
		}
	    }
	});

	mainPanel.add(createSite);
    }
}
