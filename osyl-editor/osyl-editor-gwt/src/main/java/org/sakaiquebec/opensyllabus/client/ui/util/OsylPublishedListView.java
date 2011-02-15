/*******************************************************************************
 * $Id: $
 * *****************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylPublishedListView extends Composite implements
	OsylViewControllable {

    private static final String PDF_GENERATION_FAILED =
	    "publish.pdfGeneration.nok";
    private static final String PDF_GENERATION_SUCCEEDED =
	    "publish.pdfGeneration.ok";

    Panel mainPanel = null;
    OsylController osylController;
    private boolean hasBeenPublished = false;

    private Label label;

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    /**
     * Constructor.
     * 
     * @param osylController
     */
    public OsylPublishedListView(OsylController osylController) {
	setController(osylController);
	uiMessages = osylController.getUiMessages();
	mainPanel = new VerticalPanel();
	initView();
    }

    private void initView() {
	label = new Label();
	label.setStylePrimaryName("Osyl-PublishView-Label");
	verifiyPublishState(false, null);
	initWidget(mainPanel);
    }

    private void refreshView(boolean afterPublication,
	    Map<String, String> pdfGenerationResults) {
	mainPanel.clear();

	if (hasBeenPublished) {
	    displayPublishedLinks(afterPublication);
	} else {
	    displayNoPublishedVersionMsg();
	}

	if (pdfGenerationResults != null) {
	    displayPdfGenerationResults(pdfGenerationResults);
	}
    }

    private void displayPdfGenerationResults(
	    Map<String, String> pdfGenerationResults) {

	Label pdfLabel =
		new Label(uiMessages.getMessage("publish.pdfGenerationResults"));
	pdfLabel.setStylePrimaryName("Osyl-PublishView-Label-important");
	mainPanel.add(pdfLabel);

	for (Entry<String, String> entry : pdfGenerationResults.entrySet()) {
	    HorizontalPanel hPanel = new HorizontalPanel();
	    Image pdfGenImage = null;

	    if (entry.getValue().equals(PDF_GENERATION_FAILED)) {
		pdfGenImage = new Image(osylImageBundle.cross16());
		pdfGenImage.setTitle(uiMessages.getMessage(entry.getValue()));
	    } else if (entry.getValue().equals(PDF_GENERATION_SUCCEEDED)) {
		pdfGenImage = new Image(osylImageBundle.check16());
		pdfGenImage.setTitle(uiMessages.getMessage(entry.getValue()));
	    }
	    hPanel.add(new HTML(entry.getKey() + " : " + pdfGenImage));
	    mainPanel.add(hPanel);
	}
    }

    private void displayNoPublishedVersionMsg() {
	label.setText(uiMessages.getMessage("noPublishedVersion"));
	mainPanel.add(label);
    }

    public void setPublishingNow() {
	mainPanel.clear();
	label.setText(uiMessages.getMessage("publishing"));
	mainPanel.add(label);
    }

    private void displayPublishedLinks(boolean afterPublication) {

	Date publishedDate =
		OsylDateUtils.getDateFromXMLDate(osylController.getMainView()
			.getModel().getProperty(COPropertiesType.PUBLISHED));
	DateTimeFormat dtf = osylController.getSettings().getDateTimeFormat();
	String dateTimeString = dtf.format(publishedDate);

	if (afterPublication) {
	    Label pubLabel = new Label(uiMessages.getMessage("publish.ok"));
	    pubLabel.setStylePrimaryName("Osyl-PublishView-Label-important");
	    mainPanel.add(pubLabel);
	}
	mainPanel.add(label);
	label.setText(uiMessages.getMessage("publishedVersions"));

	displayPublishedLink(SecurityInterface.ACCESS_PUBLIC);
	displayPublishedLink(SecurityInterface.ACCESS_COMMUNITY);
	displayPublishedLink(SecurityInterface.ACCESS_ATTENDEE);

	Label infoLabel =
		new Label(uiMessages.getMessage("publish.publishedDate")
			+ " : " + dateTimeString);
	infoLabel.setStylePrimaryName("Osyl-PublishView-publishedDate");
	mainPanel.add(infoLabel);
    }

    private void displayPublishedLink(String securityGroup) {
	String link = null;
	String linkName =
		uiMessages.getMessage("publishedLinkCOForGroup_"
			+ securityGroup);
	if (getController().isInHostedMode()) {
	    // TODO:add param to hosted mode
	    link = "OsylPublishedEntryPoint_" + securityGroup + ".html";
	} else {
	    link =
		    osylController.getPublishedModuleBaseURL()
			    + "/index.jsp?ro=true&sg=" + securityGroup;
	}
	HTML htmlLink =
		new HTML("<a href='" + link + "' target='PublishedView'>"
			+ linkName + "</a>");
	htmlLink.setStylePrimaryName("Osyl-PublishView-Label");
	mainPanel.add(htmlLink);
    }

    /**
     * 
     */
    public void verifiyPublishState(final boolean newPublication,
	    final Map<String, String> pdfGenerationResults) {
	AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
	    public void onSuccess(Boolean serverResponse) {
		hasBeenPublished = serverResponse.booleanValue();
		refreshView(newPublication, pdfGenerationResults);
	    }

	    public void onFailure(Throwable error) {
	    }
	};
	osylController.hasBeenPublished(callback);
    }

    public OsylController getController() {
	return osylController;
    }

    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }
}
