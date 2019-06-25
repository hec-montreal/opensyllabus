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

import java.util.List;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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

    private ListBox acadSessionListBox;
    
    private CheckBox showFrozenCheckBox;
    
    public OsylManagerMainAdvancedView(OsylManagerController controller) {
	super(controller);
	mainPanel = new VerticalPanel();
	courseListView = new CourseListAdvancedView(getController(), null);
	initView();
	initWidget(mainPanel);
    }

    private AsyncCallback<List<CMAcademicSession>> acadSessionsCB =
	    new AsyncCallback<List<CMAcademicSession>>() {

		public void onFailure(Throwable caught) {
		    Window.alert(caught.getMessage());
		}

		public void onSuccess(List<CMAcademicSession> result) {
		    acadSessionListBox.addItem(getController().getMessages()
			    .academicSessionNone(), "");

		    for (CMAcademicSession session : result) {
			acadSessionListBox.addItem(session.getSessionName(),
				session.getId());
		    }
		}

	    };

    private void initView() {
/*	Label mainLabel =
		new Label(getController().getMessages().mainView_label());
	mainLabel.setStylePrimaryName("OsylManager-mainView-label");
	mainPanel.add(mainLabel);

*/	VerticalPanel vSiteSelectionPanel = new VerticalPanel();

	HorizontalPanel hzSiteSelectionPanel2 = new HorizontalPanel();
	hzSiteSelectionPanel2.setStylePrimaryName("OsylManager-panel");

	HorizontalPanel hzPanel2 = new HorizontalPanel();
	hzPanel2.setStylePrimaryName("OsylManager-courseListView");

	HorizontalPanel hzPanel3 = new HorizontalPanel();
	hzPanel3.setStylePrimaryName("OsylManager-panel");

	HorizontalPanel hzPanel4 = new HorizontalPanel();
	hzPanel4.setStylePrimaryName("OsylManager-panel");

	selectSiteInput = new TextBox();
	selectSiteInput.setText(getController().getMessages()
		.mainView_searchForExistingSites_input());
	selectSiteInput.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		TextBox txtBox = (TextBox) event.getSource();

		if (getController().getMessages()
			.mainView_searchForExistingSites_input()
			.equals(txtBox.getText())) {
		    ((TextBox) event.getSource()).setText("");
		}
	    }

	});

	selectSiteInput.addKeyPressHandler(new KeyPressHandler() {

	    public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
		    courseListView.setSearchTerm(selectSiteInput.getText());
		    courseListView.refresh(true);
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
		courseListView.setSelectedAcadSession(acadSessionListBox
			.getValue(acadSessionListBox.getSelectedIndex()));
		/*courseListView
			.setWithFrozenSites(showFrozenCheckBox.getValue());*/
		courseListView.refresh(true);
	    }
	});

	Label trimesterLbl =
		new Label(getController().getMessages().academicSessionLabel());
	trimesterLbl.setStylePrimaryName("OsylManager-mainView-label");

	acadSessionListBox = new ListBox();
	showFrozenCheckBox = new CheckBox();
	
	showFrozenCheckBox.setStylePrimaryName("Osyl-CheckBox");
	
	if (getController().isInHostedMode()) {
	    acadSessionListBox.addItem("Not specified");
	    acadSessionListBox.addItem("H2010");
	    acadSessionListBox.addItem("H2010P4");
	    acadSessionListBox.addItem("H2010P5");
	    acadSessionListBox.addItem("H2010P6");
	    acadSessionListBox.addItem("E2010");
	    acadSessionListBox.addItem("E2010P7");
	    acadSessionListBox.addItem("E2010P8");
	    acadSessionListBox.addItem("E2010P9");
	    acadSessionListBox.addItem("A2010");
	    acadSessionListBox.addItem("A2010P1");
	    acadSessionListBox.addItem("A2010P2");
	    acadSessionListBox.addItem("A2010P3");
	} else {
	    getController().getAcademicSessions(acadSessionsCB);
	}

	Label showFrozenLbl = new Label(getController().getMessages()
				.showFrozenSites());
	showFrozenLbl.setStylePrimaryName("OsylManager-mainView-label");

	acadSessionListBox.setVisibleItemCount(1);
	hzSiteSelectionPanel2.add(selectSiteInput);
	hzSiteSelectionPanel2.add(selectSiteActionBtn);
	hzSiteSelectionPanel2.add(trimesterLbl);
	hzSiteSelectionPanel2.add(acadSessionListBox);
	Label mainLabel =
			new Label(getController().getMessages().mainView_label());
		mainLabel.setStylePrimaryName("OsylManager-mainView-label-important");
		mainPanel.add(mainLabel);
	
	hzSiteSelectionPanel2.add(mainLabel);
		
	//hzSiteSelectionPanel2.add(showFrozenLbl);
	//hzSiteSelectionPanel2.add(showFrozenCheckBox);
	
	hzSiteSelectionPanel2.setCellVerticalAlignment(showFrozenCheckBox,
		HasVerticalAlignment.ALIGN_MIDDLE);
	
	vSiteSelectionPanel.add(hzSiteSelectionPanel2);

	hzPanel2.add(courseListView);

	VerticalPanel commandsPanel = new VerticalPanel();

	HorizontalPanel hz1 = new HorizontalPanel();
	if (getController().getPermissions().get(
		SecurityInterface.OSYL_MANAGER_FUNCTION_CREATE)) {
		hz1.add(new CreateSiteAction(getController()));
		hz1.add(new HTML(" | "));
	}

	hz1.add(new EditAction(getController()));

	if (getController().getPermissions().get(
		SecurityInterface.OSYL_MANAGER_FUNCTION_COPY)) {
	    hz1.add(new HTML(" | "));
	    hz1.add(new CopyAction(getController()));
	}

	if (getController().getPermissions().get(
		SecurityInterface.OSYL_MANAGER_FUNCTION_DELETE)) {
	    if (hz1.getWidgetCount() > 0)
		hz1.add(new HTML(" | "));
	    hz1.add(new DeleteAction(getController()));
	}

	hz1.setStyleName("OsylManager-mainView-actionList");
	commandsPanel.add(hz1);

	hzPanel3.add(commandsPanel);

	mainPanel.add(vSiteSelectionPanel);
	mainPanel.add(hzPanel2);
	mainPanel.add(hzPanel3);
	mainPanel.add(hzPanel4);
	mainPanel.add(hzPanel4);
    }
}
