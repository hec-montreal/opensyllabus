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
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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

    private static final String PDF_GENERATION_FAILED =
	    "publish.pdfGeneration.nok";

    private static final String PDF_GENERATION_SUCCEEDED =
	    "publish.pdfGeneration.ok";

    PushButton okButton;

    Grid grid;

    HorizontalPanel publicationPanel;

    private int asynCB_return = 0;

    private Label publishInProgess;

    private List<COSite> coSites;

    private class PublishAsynCallBack implements
	    AsyncCallback<Vector<Map<String, String>>> {

	private int siteIndex;

	private String pdfGenMsg = messages.publishAction_pdfGenNote();

	public PublishAsynCallBack(int siteIndex) {
	    super();
	    this.siteIndex = siteIndex;
	}

	public void onFailure(Throwable caught) {
	    Image publishImage = null;
	    Image pdfGenImage = null;
	    String msg = null;

	    if (caught instanceof FusionException) {
		if (((FusionException) caught).isHierarchyFusionException()) {
		    msg =
			    messages
				    .publishAction_publish_error_HierarchyFusionException();
		} else {
		    msg =
			    messages
				    .publishAction_publish_error_FusionException();
		}
		publishImage = new Image(controller.getImageBundle().cross16());
		publishImage.setTitle(messages.publishAction_publish_error()
			+ " : " + msg);
		pdfGenImage = new Image(controller.getImageBundle().cross16());
		pdfGenImage.setTitle(messages.publishAction_pdfGen_nok());
	    } else if (caught instanceof CompatibilityException) {
		msg =
			messages
				.publishAction_publish_error_CompatibilityException();
		publishImage = new Image(controller.getImageBundle().cross16());
		publishImage.setTitle(messages.publishAction_publish_error()
			+ " : " + msg);
		pdfGenImage = new Image(controller.getImageBundle().cross16());
		pdfGenImage.setTitle(messages.publishAction_pdfGen_nok());
	    } else if (caught instanceof PdfGenerationException) {
		msg = messages.publish_pdfGenerationException();
		publishImage = new Image(controller.getImageBundle().check16());
		publishImage.setTitle(messages.publishAction_publish_ok());
		pdfGenImage = new Image(controller.getImageBundle().cross16());
		pdfGenImage.setTitle(messages.publishAction_pdfGen_nok()
			+ " : " + msg);
	    } else if (caught instanceof OsylPermissionException) {
		msg = messages.permission_exception();
		publishImage = new Image(controller.getImageBundle().check16());
		publishImage.setTitle(messages.publishAction_publish_error()
			+ " : " + msg);
		pdfGenImage = new Image(controller.getImageBundle().cross16());
		pdfGenImage.setTitle(messages.publishAction_pdfGen_nok());
	    } else {
		msg = caught.getMessage();
		publishImage = new Image(controller.getImageBundle().cross16());
		publishImage.setTitle(messages.publishAction_publish_error()
			+ " : " + msg);
		pdfGenImage = new Image(controller.getImageBundle().cross16());
		pdfGenImage.setTitle(messages.publishAction_pdfGen_nok()
			+ " : " + msg);
	    }

	    grid.setWidget(siteIndex + 1, 1, publishImage);
	    grid.setWidget(siteIndex + 1, 2, pdfGenImage);
	    responseReceive();
	}

	public void onSuccess(Vector<Map<String, String>> serverResponse) {
	    Map<String, String> pdfGenerationResults = serverResponse.get(1);
	    int siteRows = pdfGenerationResults.size();

	    if (coSites.size() > 1) {
		if (pdfGenerationResults != null) {
		    for (Entry<String, String> entry : pdfGenerationResults
			    .entrySet()) {
			Image publishImage =
				new Image(controller.getImageBundle().check16());
			publishImage.setTitle(messages
				.publishAction_publish_ok());
			Image pdfGenImage = null;
			if (entry.getValue().equals(PDF_GENERATION_FAILED)) {
			    pdfGenImage =
				    new Image(controller.getImageBundle()
					    .cross16());
			    pdfGenImage.setTitle(messages
				    .publishAction_pdfGen_nok());
			} else if (entry.getValue().equals(
				PDF_GENERATION_SUCCEEDED)) {
			    pdfGenImage =
				    new Image(controller.getImageBundle()
					    .check16());
			    pdfGenImage.setTitle(messages
				    .publishAction_pdfGen_ok());
			}
			grid.setWidget(siteIndex + 1, 1, publishImage);
			grid.setWidget(siteIndex + 1, 2, pdfGenImage);
		    }

		}
	    } else {
		if (serverResponse != null) {
		    Label publishedVersion =
			    new Label(messages
				    .publishAction_publish_publishedVersion());
		    Label versionPDF =
			    new Label(messages
				    .publishAction_publish_versionPDF());
		    grid.resize(siteRows + 1, 3);
		    grid.setWidget(0, 1, publishedVersion);
		    grid.setWidget(0, 2, versionPDF);

		    int i = 1;
		    for (Entry<String, String> entry : serverResponse.get(1)
			    .entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			Label siteId = new Label(key);
			Image pdfGenImage = null;
			Image publishImage =
				new Image(controller.getImageBundle().check16());
			publishImage.setTitle(messages
				.publishAction_publish_ok());
			if (entry.getValue().equals(PDF_GENERATION_FAILED)) {
			    pdfGenImage =
				    new Image(controller.getImageBundle()
					    .cross16());
			    pdfGenImage.setTitle(messages
				    .publishAction_pdfGen_nok());
			} else if (entry.getValue().equals(
				PDF_GENERATION_SUCCEEDED)) {
			    pdfGenImage =
				    new Image(controller.getImageBundle()
					    .check16());
			    pdfGenImage.setTitle(messages
				    .publishAction_pdfGen_ok());
			}
			grid.setWidget(i, 0, siteId);
			grid.setWidget(i, 1, publishImage);
			grid.setWidget(i, 2, pdfGenImage);
			i++;
		    }
		}
	    }
	    responseReceive();
	}

	private void responseReceive() {
	    asynCB_return++;
	    if (asynCB_return == coSites.size()) {
		// publicationPanel.add(new HTML(pdfGenMsg));
		publishInProgess.setVisible(false);
		okButton.setEnabled(true);
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    } else {
		if (coSites.size()>1) {
		    controller.publish(coSites.get(siteIndex + 1).getSiteId(), new PublishAsynCallBack(siteIndex + 1));
		}
	    }
	}
    }

    public PublishForm(OsylManagerController controller, List<COSite> coSites) {
	super(controller);
	Label title = new Label(messages.publishAction_publish_title());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	publishInProgess =
		new Label(messages.publishAction_publish_inProgress());
	publishInProgess
		.setStylePrimaryName("OsylManager-form-publicationText");
	mainPanel.add(publishInProgess);

	grid = new Grid(coSites.size()+5, 3);
	
	this.coSites = coSites;
	asynCB_return = 0;
	Image image = new Image(controller.getImageBundle().ajaxloader());
	image.setTitle(messages.publishAction_publish_inProgress());
	Label publishedVersion =
		new Label(messages.publishAction_publish_publishedVersion());
	Label versionPDF =
		new Label(messages.publishAction_publish_versionPDF());
	grid.setWidget(0, 1, publishedVersion);
	grid.setWidget(0, 2, versionPDF);
	
	for (int r = 0; r < coSites.size(); r++) {
	    String siteId = coSites.get(r).getSiteId();
	    Label versionSite = new Label(siteId);
	    grid.setWidget(r + 1, 0, versionSite);
	    grid.setWidget(r + 1, 1, image);
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
