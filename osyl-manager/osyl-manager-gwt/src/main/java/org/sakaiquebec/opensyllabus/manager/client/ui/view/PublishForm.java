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
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class PublishForm extends OsylManagerAbstractWindowPanel {

    PushButton okButton;

    Grid grid;

    private int asynCB_return = 0;

    private Label publishInProgess;

    private List<COSite> coSites;

    private class PublishAsynCallBack implements AsyncCallback<Void> {

	private int siteIndex;

	public PublishAsynCallBack(int siteIndex) {
	    super();
	    this.siteIndex = siteIndex;
	}

	public void onFailure(Throwable caught) {
	    Image image = new Image(controller.getImageBundle()
		    .cross16());
	    image.setTitle(messages.publishAction_publish_error()+":"+caught.getMessage());
	    grid.setWidget(siteIndex, 1,image);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    grid.setWidget(siteIndex, 1, new Image(controller.getImageBundle()
		    .check16()));
	    responseReceive();
	}

	private void responseReceive() {
	    asynCB_return++;
	    if (asynCB_return == coSites.size()) {
		publishInProgess.setVisible(false);
		okButton.setEnabled(true);
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    }else{
		controller.publish(coSites.get(siteIndex+1).getSiteId(), new PublishAsynCallBack(siteIndex+1));
	    }
	}
    }

    public PublishForm(OsylManagerController controller, List<COSite> coSites) {
	super(controller);
	Label title = new Label(messages.mainView_action_publish());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	publishInProgess = new Label(messages.publishAction_publish_inProgress());
	publishInProgess
		.setStylePrimaryName("OsylManager-form-publicationText");
	mainPanel.add(publishInProgess);

	grid = new Grid(coSites.size(), 2);
	this.coSites = coSites;
	asynCB_return = 0;
	for (int r = 0; r < coSites.size(); r++) {
	    String siteId = coSites.get(r).getSiteId();
	    grid.setText(r, 0, siteId);
	    grid.setWidget(r, 1, new Image(controller.getImageBundle()
		    .ajaxloader()));
	}
	HorizontalPanel publicationPanel = new HorizontalPanel();
	Label voidLabel = new Label();
	publicationPanel.add(voidLabel);
	publicationPanel.add(grid);
	Label voidLabel2 = new Label();
	publicationPanel.add(voidLabel2);
	publicationPanel.setCellWidth(voidLabel, "10%");
	publicationPanel.setCellWidth(grid, "80%");
	publicationPanel.setCellWidth(voidLabel2, "10%");
	publicationPanel
		.setStylePrimaryName("OsylManager-form-publicationPanel");
	mainPanel.add(publicationPanel);
	mainPanel.setCellHorizontalAlignment(publicationPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	okButton = new PushButton(messages.associateForm_ok());
	okButton.setStylePrimaryName("Osyl-Button");
	okButton.setWidth("50px");
	okButton.setEnabled(false);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		PublishForm.this.hide();
	    }
	});

	HorizontalPanel hz = new HorizontalPanel();
	hz.add(okButton);

	mainPanel.add(hz);
	mainPanel.setCellHorizontalAlignment(hz,
		HasHorizontalAlignment.ALIGN_CENTER);
	
	controller.publish(coSites.get(0).getSiteId(), new PublishAsynCallBack(0));
    }

}
