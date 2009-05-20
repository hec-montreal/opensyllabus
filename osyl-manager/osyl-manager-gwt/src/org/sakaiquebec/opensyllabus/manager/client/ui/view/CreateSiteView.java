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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreateSiteView extends OsylManagerAbstractView {

    private Label nameLabel;

    private TextBox nameTextBox;

    private PushButton createSite;

    private VerticalPanel mainPanel;

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
	createSite =
	    new PushButton(getController().getMessages().create());
	createSite.addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		if(!nameTextBox.getText().equals(""))
		getController().createSite(nameTextBox.getText());
	    }
	});
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	nameTextBox = new TextBox();
	nameLabel = new Label(getController().getMessages().courseName());
	mainPanel.setWidth("100%");
	Label title = new Label(getController().getMessages().createSiteTitle());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);
	mainPanel.add(createFormElement(nameLabel, nameTextBox));
	mainPanel.add(createSite);
    }

}
