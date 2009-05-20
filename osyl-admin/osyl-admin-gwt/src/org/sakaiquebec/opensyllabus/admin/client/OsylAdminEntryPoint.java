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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.admin.client;

import org.sakaiquebec.opensyllabus.admin.client.rpc.OsylAdminGwtService;
import org.sakaiquebec.opensyllabus.admin.client.rpc.OsylAdminGwtServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OsylAdminEntryPoint implements EntryPoint {

    /**
     * This is the entry point method.
     */

    final static int CREATE_USERS = 1;
    final OsylAdminMessages messages =
	    (OsylAdminMessages) GWT
		    .create(org.sakaiquebec.opensyllabus.admin.client.OsylAdminMessages.class);

    private Button createButton = new Button(messages.OsylAdminCreateButton());
    private RadioButton createUser =
	    new RadioButton("create", messages.OsylAdminUsersRadioButton());

    private final String QUALIFIED_NAME =
	    "org.sakaiquebec.opensyllabus.admin.OsylAdminEntryPoint/";
    private int operation = CREATE_USERS;

    /** {@inheritDoc} */
    public void onModuleLoad() {

	final OsylAdminGwtServiceAsync serviceProxy =
		(OsylAdminGwtServiceAsync) GWT
			.create(OsylAdminGwtService.class);
	ServiceDefTarget pointService = (ServiceDefTarget) serviceProxy;
	String url = GWT.getModuleBaseURL();
	final String cleanUrl =
		url.substring(0, url.length() - QUALIFIED_NAME.length());
	pointService.setServiceEntryPoint(cleanUrl + "OsylAdminGwtService");

	setMainPanel();

	final AdminDialog dialog = new AdminDialog(messages);

	createButton.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		dialog.center();
		dialog.show();
		AsyncCallback<Void> osylAdmin = new AsyncCallback<Void>() {
		    public void onSuccess(Void results) {
			dialog.setText(messages.OsylAdminDialogSuccess());
			Timer timer = new Timer() {
			    public void run() {
				dialog.hide();
			    }
			};
			timer.schedule(500);
		    }

		    public void onFailure(Throwable error) {
			try {
			    throw error;
			} catch (Throwable e) {
			    e.printStackTrace();
			}
			dialog.setText(messages.OsylAdminDialogFailure());
			Timer timer = new Timer() {
			    public void run() {
				dialog.hide();
			    }
			};
			timer.schedule(500);
			error.printStackTrace();
		    }
		};
		if (operation == CREATE_USERS)
		    serviceProxy.createUsers(null, osylAdmin);
	    }
	});
    }

    private void setMainPanel() {

	VerticalPanel vPanel = new VerticalPanel();
	vPanel.setStyleName("Osyl-Panel");
	// Add panel to the RootPanel
	RootPanel.get().add(vPanel);
	vPanel.setBorderWidth(1);
	vPanel.setSpacing(5);

	final VerticalPanel verticalPanel_1 = new VerticalPanel();
	vPanel.add(verticalPanel_1);
	verticalPanel_1.setSpacing(2);
	final Label titleLabel =
		new Label(messages.OsylAdminMainPanelChoiceLabel());
	verticalPanel_1.add(titleLabel);
	titleLabel.setStyleName("Osyl-Admin-Choice-Label");

	final VerticalPanel verticalPanel = new VerticalPanel();
	verticalPanel_1.add(verticalPanel);
	createUser.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		operation = CREATE_USERS;
	    }
	});
	createUser.setStyleName("Osyl-Admin-RadioButton");
	createUser.setChecked(true);
	verticalPanel.add(createUser);

	vPanel.add(createButton);
	createButton.setStyleName("Osyl-Admin-Button");
	vPanel.setCellHorizontalAlignment(createButton,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    private static class AdminDialog extends DialogBox {
	public AdminDialog(OsylAdminMessages messages) {
	    setText(messages.OsylAdminDialogCreationTitle());
	    HorizontalPanel hzPanel = new HorizontalPanel();
	    hzPanel.setStyleName("Osyl-ajaxSpinner");
	    hzPanel.setPixelSize(100, 100);
	    setWidget(hzPanel);
	}
    }

}
