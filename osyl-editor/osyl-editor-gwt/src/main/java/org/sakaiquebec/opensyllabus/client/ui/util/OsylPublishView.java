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

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.Map;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylPublishView extends PopupPanel implements OsylViewControllable {

    private OsylController osylController;

    private VerticalPanel mainPanel;

    private OsylPublishedListView osylPublishedListView;

    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

    /**
     * Constructor.
     * 
     * @param osylController
     */
    public OsylPublishView(OsylController osylController) {
	setController(osylController);
	uiMessages = osylController.getUiMessages();
	mainPanel = new VerticalPanel();
	Label label =
		new Label(uiMessages.getMessage("courseOutlinePublication"));
	label.setStylePrimaryName("Osyl-PublishView-Title");
	mainPanel.add(label);

	osylPublishedListView = new OsylPublishedListView(getController());
	osylPublishedListView.setWidth("100%");
	mainPanel.add(osylPublishedListView);
	mainPanel.setWidth("100%");
	setSize("400px", "200px");

	// Add a button panel
	HorizontalPanel buttonPanel = new HorizontalPanel();
	mainPanel.add(buttonPanel);

	// Add a 'Publish' button.
	// final Button publishButton =
	// new Button(uiMessages.getMessage("publish"),
	// new PublishLinkClickListener());
	AbstractImagePrototype imgPublishButton = 
	    AbstractImagePrototype.create(osylImageBundle.publish());
	ImageAndTextButton publishButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgPublishButton, uiMessages.getMessage("publish"));
	publishButton.addClickHandler(new PublishLinkClickHandler());
	publishButton.setStylePrimaryName("Osyl-PublishView-genericButton");
	buttonPanel.add(publishButton);

	// Add a 'Close' button.
	// final Button cancelButton =
	// new Button(uiMessages.getMessage("close"), new ClickListener() {
	// public void onClick(Widget sender) {
	// hide();
	// }
	// });
	AbstractImagePrototype imgCloseButton = 
	    AbstractImagePrototype.create(osylImageBundle.action_cancel());
	ImageAndTextButton cancelButton = new ImageAndTextButton(
	// TODO: Bug with ImageBundle, we have to use
		// AbstractImagePrototype
		imgCloseButton, uiMessages.getMessage("close"));
	cancelButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		hide();
	    }
	});
	cancelButton.setStylePrimaryName("Osyl-PublishView-genericButton");
	buttonPanel.add(cancelButton);

	setWidget(mainPanel);
	setStylePrimaryName("Osyl-PublishView-PopupPanel");
    }

    public void refreshView() {

    }

    public class PublishLinkClickHandler implements ClickHandler {

	public void onClick(ClickEvent event) {
	    osylPublishedListView.setPublishingNow();

	    AsyncCallback<String> callback = new AsyncCallback<String>() {
		public void onSuccess(String serverResponse) {
		    publish();
		}

		public void onFailure(Throwable error) {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(false, true, getController()
				    .getUiMessage("publish.error")
				    + " : " + error.toString());
		    alertBox.show();
		    osylPublishedListView.verifiyPublishState(false);
		}
	    };
	    osylController.saveCourseOutline(callback);

	}
    }

    private void publish() {
	AsyncCallback<Map<String, String>> callback =
		new AsyncCallback<Map<String, String>>() {
		    public void onSuccess(Map<String, String> serverResponse) {
			if (serverResponse != null) {
			    for (String key : serverResponse.keySet())
				osylController.getMainView().getModel()
					.addProperty(key,
						serverResponse.get(key));
			}
			osylPublishedListView.verifiyPublishState(true);
		    }

		    public void onFailure(Throwable error) {
			final OsylAlertDialog alertBox =
				new OsylAlertDialog(false, true,
					getController().getUiMessage(
						"publish.error")
						+ " : " + error.toString());
			alertBox.show();
			osylPublishedListView.verifiyPublishState(false);
		    }
		};
	osylController.publishCourseOutline(callback);
    }

    public OsylController getController() {
	return osylController;
    }

    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }

}
