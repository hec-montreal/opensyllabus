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

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewControllable;
import org.sakaiquebec.opensyllabus.client.ui.base.ImageAndTextButton;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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

    private CheckBox announceChexBox;

    private TextArea contentTextArea;

    private TextBox titleTextBox;

    private VerticalPanel announcePanel;

    /**
     * User interface message bundle
     */
    private OsylConfigMessages uiMessages;

    private OsylConfigMessages coMessages;

    private String dateString;

    private String timeString;

    private DateTimeFormat dateFormat;

    private DateTimeFormat timeFormat;

    private ImageAndTextButton publishButton;

    private ImageAndTextButton cancelButton;

    private HorizontalPanel buttonPanel;

    /**
     * Constructor.
     * 
     * @param osylController
     */
    public OsylPublishView(OsylController osylController) {
	setController(osylController);
	dateFormat = getController().getSettings().getDateFormat();
	timeFormat = getController().getSettings().getTimeFormat();
	uiMessages = osylController.getUiMessages();
	coMessages = osylController.getCoMessages();
	mainPanel = new VerticalPanel();
	Label label =
		new Label(uiMessages.getMessage("courseOutlinePublication"));
	label.setStylePrimaryName("Osyl-PublishView-Title");
	mainPanel.add(label);
	HTML voidLabel = new HTML("<p>&nbsp;</p>");
	voidLabel.setStylePrimaryName("Osyl-PublishView-Label");
	mainPanel.add(voidLabel);
	osylPublishedListView = new OsylPublishedListView(getController());
	osylPublishedListView.setWidth("100%");
	mainPanel.add(osylPublishedListView);
	mainPanel.setWidth("100%");
	setSize("420px", "250px");
	mainPanel.add(voidLabel);
	announceChexBox =
		new CheckBox(
			uiMessages.getMessage("publish.announce.checkboxLabel"));
	mainPanel.add(voidLabel);
	mainPanel.add(announceChexBox);
	mainPanel.add(voidLabel);

	announcePanel = new VerticalPanel();
	Label announceLabel =
		new Label(uiMessages.getMessage("publish.announce.label"));
	announceLabel.setStylePrimaryName("Osyl-PublishView-Label");
	HTML spaceLabel = new HTML("&nbsp;");
	spaceLabel.setStylePrimaryName("Osyl-PublishView-Label");
	announcePanel.add(spaceLabel);
	announcePanel.add(announceLabel);
	announcePanel.add(spaceLabel);
	titleTextBox = new TextBox();
	contentTextArea = new TextArea();
	Date date = new Date();
	dateString = dateFormat.format(date);
	timeString = timeFormat.format(date);
	Label titleLabel =
		new Label(uiMessages.getMessage("publish.announce.title"));
	Label texteLabel =
		new Label(uiMessages.getMessage("publish.announce.text"));
	titleLabel.setStylePrimaryName("Osyl-PublishView-Label-important");
	texteLabel.setStylePrimaryName("Osyl-PublishView-Label-important");
	titleTextBox.setText(coMessages.getMessage("announce.publish.subject"));
	contentTextArea.setText(coMessages.getMessage(
		"announce.publish.content", dateString, timeString));
	titleTextBox.setWidth("400px");
	contentTextArea.setSize("400px", "75px");
	announcePanel.add(spaceLabel);
	announcePanel.add(spaceLabel);
	announcePanel.add(titleLabel);
	announcePanel.add(titleTextBox);
	announcePanel.add(texteLabel);
	announcePanel.add(contentTextArea);
	announcePanel.add(spaceLabel);
	announcePanel.setVisible(false);
	mainPanel.add(announcePanel);
	announceChexBox
		.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		    public void onValueChange(ValueChangeEvent<Boolean> event) {
			boolean visible = event.getValue();
			announcePanel.setVisible(visible);
		    }

		});

	// Add a button panel
	buttonPanel = new HorizontalPanel();
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
	cancelButton = new ImageAndTextButton(
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
	    AsyncCallback<Void> callback = new AsyncCallback<Void>() {
		public void onSuccess(Void serverResponse) {
		    mainPanel.remove(announceChexBox);
		    mainPanel.remove(announcePanel);
		    mainPanel.remove(buttonPanel);
		    publish();
		}

		public void onFailure(Throwable error) {
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(false, true, getController()
				    .getUiMessage("publish.error")
				    + " : "
				    + error.toString());
		    alertBox.show();
		    osylPublishedListView.verifiyPublishState(false, null);
		}
	    };
	    osylController.saveCourseOutline(callback);

	}
    }

    private void publish() {
	AsyncCallback<Vector<Map<String, String>>> callback =
		new AsyncCallback<Vector<Map<String, String>>>() {
		    public void onSuccess(
			    Vector<Map<String, String>> serverResponse) {
			if (serverResponse != null) {
			    for (Entry<String, String> entry : serverResponse
				    .get(0).entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				osylController.getMainView().getModel()
					.addProperty(key, value);
			    }
			}
			if (announceChexBox.getValue())
			    notifyOnPublish();
			osylPublishedListView.verifiyPublishState(true,
				serverResponse.get(1));
			mainPanel.add(announceChexBox);
			mainPanel.add(announcePanel);
			mainPanel.add(buttonPanel);
		    }

		    public void onFailure(Throwable error) {
			if (error instanceof FusionException) {
			    String msg = null;
			    if (((FusionException) error)
				    .isHierarchyFusionException()) {
				msg =
					uiMessages
						.getMessage("publish.fusionException.hierarchyFusionException");
			    } else {
				msg =
					uiMessages
						.getMessage("publish.fusionException");
			    }
			    final OsylAlertDialog alertBox =
				    new OsylAlertDialog(
					    false,
					    true,
					    uiMessages
						    .getMessage("Global.warning"),
					    msg);
			    alertBox.show();
			} else if (error instanceof PdfGenerationException) {
			    final OsylAlertDialog alertBox =
				    new OsylAlertDialog(
					    false,
					    true,
					    uiMessages
						    .getMessage("Global.warning"),
					    uiMessages
						    .getMessage("publish.pdfGenerationException"));
			    alertBox.show();
			} else {
			    final OsylAlertDialog alertBox =
				    new OsylAlertDialog(false, true,
					    getController().getUiMessage(
						    "publish.error")
						    + " : " + error.toString());
			    alertBox.show();
			}
			osylPublishedListView.verifiyPublishState(false, null);
			mainPanel.add(announceChexBox);
			mainPanel.add(announcePanel);
			mainPanel.add(buttonPanel);
		    }
		};
	osylController.publishCourseOutline(callback);
    }

    private void notifyOnPublish() {
	AsyncCallback<Void> notifyCallback = new AsyncCallback<Void>() {

	    public void onFailure(Throwable caught) {
		final OsylAlertDialog alertBox =
			new OsylAlertDialog(false, true, getController()
				.getUiMessage("publish.announce.error"));
		alertBox.show();
	    }

	    public void onSuccess(Void result) {
	    }
	};
	String content = contentTextArea.getText();
	String subject = titleTextBox.getText();
	Date d = new Date();
	content = content.replace(dateString, dateFormat.format(d));
	content = content.replace(timeString, timeFormat.format(d));
	getController().notifyOnPublish(getController().getSiteId(), subject,
		content, notifyCallback);
    }

    public OsylController getController() {
	return osylController;
    }

    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }

}
