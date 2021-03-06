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

import java.util.List;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CourseInfoView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private HorizontalPanel mainPanel;

    private Label lastPublishInfo = new Label();
    private Label lastSaveInfo = new Label();
    private Label associatedCourseInfo = new Label();
    private Label parentSiteInfo = new Label();

    public CourseInfoView(OsylManagerController controller) {
	super(controller);
	mainPanel = new HorizontalPanel();
	initView();
	initWidget(mainPanel);
	controller.addEventHandler(this);
    }

    private void initView() {

	mainPanel.clear();

	mainPanel.add(new Image(getController().getImageBundle().info()));

	VerticalPanel infoPanel = new VerticalPanel();

	Label info =
		new Label(getController().getMessages().infoView_label() + ":");
	infoPanel.add(info);

	HorizontalPanel h1 = new HorizontalPanel();
	Label lastPublishLabel =
		new Label(getController().getMessages()
			.infoView_lastPublished()
			+ ":");
	h1.add(lastPublishLabel);
	h1.add(lastPublishInfo);
	infoPanel.add(h1);

	HorizontalPanel h2 = new HorizontalPanel();
	Label lastSaveLabel =
		new Label(getController().getMessages().infoView_lastSave()
			+ ":");
	h2.add(lastSaveLabel);
	h2.add(lastSaveInfo);
	infoPanel.add(h2);

	HorizontalPanel h3 = new HorizontalPanel();
	Label associatedCourseLabel =
		new Label(getController().getMessages()
			.infoView_associatedCourse()
			+ ":");
	h3.add(associatedCourseLabel);
	h3.add(associatedCourseInfo);
	infoPanel.add(h3);

	HorizontalPanel h4 = new HorizontalPanel();
	Label parentSiteLabel =
		new Label(getController().getMessages().infoView_parentSite()
			+ ":");
	h4.add(parentSiteLabel);
	h4.add(parentSiteInfo);
	infoPanel.add(h4);

	mainPanel.add(infoPanel);

    }

    public void refreshView() {
	List<COSite> list = getController().getSelectSites();
	DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");
	if (list.size() == 1) {
	    COSite cosite = list.get(0);
	    lastPublishInfo = new Label(dtf.format(cosite.getLastPublicationDate()));
	    lastSaveInfo = new Label(dtf.format(cosite.getLastPublicationDate()));
	    associatedCourseInfo = new Label(cosite.getCourseName());
	    parentSiteInfo = new Label(cosite.getParentSite());
	} else {
	    String parentSiteInfoString = list.get(0).getParentSite();
	    for(COSite cosite : list){
		if(!cosite.getParentSite().equals(parentSiteInfoString))
		    parentSiteInfoString="";
	    }
	    lastPublishInfo = new Label("");
	    lastSaveInfo = new Label("");
	    associatedCourseInfo = new Label("");
	    parentSiteInfo = new Label(parentSiteInfoString);
	}
	initView();
    }

    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITES_SELECTION_EVENT
		|| e.getType() == OsylManagerEvent.SITE_INFO_CHANGE)
	    refreshView();
    }

}
