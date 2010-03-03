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
import org.sakaiquebec.opensyllabus.manager.client.ui.RoundCornerPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylManagerMainView extends OsylManagerAbstractView {

    private VerticalPanel mainPanel;

    private TabPanel tabPanel;

    private CourseInfoView courseInfoView;

    public OsylManagerMainView(OsylManagerController controller) {
	super(controller);
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	initView();
    }

    private void initView() {

	mainPanel
		.add(new Label(getController().getMessages().mainView_label()));

	HorizontalPanel hPanel = new HorizontalPanel();

	hPanel.add(new CreateSiteAction(getController()));
	hPanel.add(new HTML("&nbsp;"
		+ getController().getMessages().mainView_or() + "&nbsp;"));
	hPanel.add(new ImportAction(getController()));

	mainPanel.add(hPanel);

	mainPanel.add(new Label(getController().getMessages()
		.mainView_operationsOnExistingSites()));

	HorizontalPanel hzPanel = new HorizontalPanel();

	tabPanel = new TabPanel();
	tabPanel.add(new CourseListView(getController()), getController()
		.getMessages().mainView_tabs_all());
	tabPanel.add(new HTML("//TODO"), "Spécifiques");// TODO
	tabPanel.add(new HTML("//TODO"), "Partageables");// TODO
	tabPanel.add(new HTML("//TODO"), "Génériques");// TODO
	tabPanel.add(new HTML("//TODO"), "Favoris");// TODO
	tabPanel.selectTab(0);
	tabPanel.addStyleName("OsylManager-mainView-tabPanel");

	hzPanel.add(tabPanel);

	VerticalPanel rightPanel = new VerticalPanel();
	HorizontalPanel hz1 = new HorizontalPanel();
	hz1.add(new EditAction(getController()));
	hz1.add(new PublishAction(getController()));
	hz1.add(new UnpublishAction(getController()));
	hz1.add(new CopyAction(getController()));
	hz1.setStyleName("OsylManager-mainView-actionList");
	rightPanel.add(hz1);

	HorizontalPanel hz2 = new HorizontalPanel();
	hz2.add(new AttachAction(getController()));
	hz2.add(new UnattachAction(getController()));
	hz2.setStyleName("OsylManager-mainView-actionList");
	rightPanel.add(hz2);

	HorizontalPanel hz3 = new HorizontalPanel();
	hz3.add(new AssociateAction(getController()));
	hz3.add(new DissociateAction(getController()));
	hz3.setStyleName("OsylManager-mainView-actionList");
	rightPanel.add(hz3);

	HorizontalPanel hz4 = new HorizontalPanel();
	hz4.add(new ExportAction(getController()));
	hz4.add(new CleanAction(getController()));
	hz4.add(new DeleteAction(getController()));
	hz4.setStyleName("OsylManager-mainView-actionList");
	rightPanel.add(hz4);

	courseInfoView = new CourseInfoView(getController());
	RoundCornerPanel ivRoundCornerPanel =
		new RoundCornerPanel(
			courseInfoView,
			"", "OsylManager-infoView-BottomLeft",
			"OsylManager-infoView-BottomRight",
			"OsylManager-infoView-TopLeft",
			"OsylManager-infoView-TopRight");
	ivRoundCornerPanel.setStylePrimaryName("OsylManager-infoView");

	rightPanel.add(ivRoundCornerPanel);

	hzPanel.add(rightPanel);

	mainPanel.add(hzPanel);

    }

}
