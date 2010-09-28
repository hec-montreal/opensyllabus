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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerMainAdvancedView extends OsylManagerAbstractView {

    private VerticalPanel mainPanel;

    private CourseListAdvancedView courseListView;

    private TextBox selectSiteInput;

    public OsylManagerMainAdvancedView(OsylManagerController controller) {
	super(controller);
	mainPanel = new VerticalPanel();
	courseListView = new CourseListAdvancedView(getController(), null);
	initView();
	initWidget(mainPanel);
    }

    private void initView() {
	Label mainLabel =
		new Label(getController().getMessages().mainView_label());
	mainLabel.setStylePrimaryName("OsylManager-mainView-label");
	mainPanel.add(mainLabel);

	VerticalPanel vPanel1 = new VerticalPanel();

	HorizontalPanel hzPanel1 = new HorizontalPanel();
	hzPanel1.setStylePrimaryName("OsylManager-panel");

	HorizontalPanel hzPanel2 = new HorizontalPanel();
	hzPanel2.setStylePrimaryName("OsylManager-courseListView");

	HorizontalPanel hzPanel3 = new HorizontalPanel();
	hzPanel3.setStylePrimaryName("OsylManager-panel");

	HorizontalPanel hzPanel4 = new HorizontalPanel();
	hzPanel4.setStylePrimaryName("OsylManager-panel");

	Label selectSiteLabel =
		new Label(getController().getMessages()
			.mainView_searchForExistingSites());
	selectSiteLabel.setStylePrimaryName("OsylManager-mainView-label");
	selectSiteInput = new TextBox();
	selectSiteInput.setText(getController().getMessages()
		.mainView_searchForExistingSites_input());
	selectSiteInput.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		TextBox txtBox = (TextBox) event.getSource();

		if (getController().getMessages()
			.mainView_searchForExistingSites_input().equals(
				txtBox.getText())) {
		    ((TextBox) event.getSource()).setText("");
		}
	    }

	});
	
	selectSiteInput.addKeyPressHandler(new KeyPressHandler(){

	    public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
		    courseListView.setSearchTerm(selectSiteInput.getText());
		    courseListView.refresh();
		}
	    }
	});
	PushButton selectSiteActionBtn =
		new PushButton(getController().getMessages()
			.mainView_action_search());
	selectSiteActionBtn.setStylePrimaryName("Osyl-Button");
	selectSiteActionBtn.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		courseListView.setSearchTerm(selectSiteInput.getText());
		courseListView.refresh();
	    }
	});
	// vPanel1.add(selectSiteLabel);
	hzPanel1.add(selectSiteLabel);
	hzPanel1.add(selectSiteInput);
	hzPanel1.add(selectSiteActionBtn);
	vPanel1.add(hzPanel1);

	hzPanel2.add(courseListView);

	VerticalPanel commandsPanel = new VerticalPanel();

	HorizontalPanel hz1 = new HorizontalPanel();
	hz1.add(new EditAction(getController()));
	hz1.add(new PublishAction(getController()));
//	hz1.add(new UnpublishAction(getController()));
//	hz1.add(new CopyAction(getController()));
	hz1.setStyleName("OsylManager-mainView-actionList");
	commandsPanel.add(hz1);

	HorizontalPanel hz2 = new HorizontalPanel();
	hz2.add(new AttachAction(getController()));
	hz2.add(new UnattachAction(getController()));
	hz2.setStyleName("OsylManager-mainView-actionList");
	commandsPanel.add(hz2);

	HorizontalPanel hz3 = new HorizontalPanel();
	hz3.add(new AssociateAction(getController()));
	hz3.add(new DissociateAction(getController()));
	hz3.setStyleName("OsylManager-mainView-actionList");
	commandsPanel.add(hz3);

	HorizontalPanel hz4 = new HorizontalPanel();
	hz4.add(new ImportAction(getController()));
	hz4.add(new ExportAction(getController()));
//	hz4.add(new CleanAction(getController()));
	hz4.add(new DeleteAction(getController()));
	hz4.setStyleName("OsylManager-mainView-actionList");
	commandsPanel.add(hz4);
	hzPanel3.add(commandsPanel);

	mainPanel.add(vPanel1);
	mainPanel.add(hzPanel2);
	mainPanel.add(hzPanel3);
	mainPanel.add(hzPanel4);

	Label newSiteCreationLbl =
		new Label(getController().getMessages()
			.mainView_creationOfNewSite());
	newSiteCreationLbl.setStylePrimaryName("OsylManager-mainView-label");
	mainPanel.add(newSiteCreationLbl);
	HorizontalPanel hPanel = new HorizontalPanel();

	hPanel.add(new CreateSiteAction(getController()));
	hPanel.add(new HTML("&nbsp;"
		+ getController().getMessages().mainView_or() + "&nbsp;"));
	hPanel.add(new ImportNewSiteAction(getController()));

	mainPanel.add(hPanel);
    }
}
