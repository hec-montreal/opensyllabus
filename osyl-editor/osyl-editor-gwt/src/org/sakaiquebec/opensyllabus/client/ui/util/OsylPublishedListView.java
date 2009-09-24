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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylPublishedListView extends Composite implements
	OsylViewControllable {

    Panel mainPanel = null;
    OsylController osylController;
    private boolean hasBeenPublished = false;

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

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
	verifiyPublishState();
	initWidget(mainPanel);
    }

    private void refreshView() {
	if (hasBeenPublished) {
	    displayPublishedLinks();
	} else {
	    displayNoPublishedVersionMsg();
	}
    }

    private void displayNoPublishedVersionMsg() {
	setCurrentText(uiMessages.getMessage("noPublishedVersion"));
    }

    public void setPublishingNow() {
	setCurrentText(uiMessages.getMessage("publishing"));
    }

    private void setCurrentText(String txt) {
	mainPanel.clear();
	Label infoLabel = new Label(txt);
	infoLabel.setStylePrimaryName("Osyl-PublishView-Label");
	mainPanel.add(infoLabel);
    }

    private void displayPublishedLinks() {
	setCurrentText(uiMessages.getMessage("publishedVersions"));

	// TODO: the onsite group is not active for the moment
	displayPublishedLink(SecurityInterface.ACCESS_PUBLIC);
	// displayPublishedLink(SecurityInterface.SECURITY_ACCESS_ONSITE);
	displayPublishedLink(SecurityInterface.ACCESS_ATTENDEE);
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
		    osylController.getAdjustedModuleBaseURL()
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
    public void verifiyPublishState() {
	AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
	    public void onSuccess(Boolean serverResponse) {
		hasBeenPublished = serverResponse.booleanValue();
		refreshView();
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
