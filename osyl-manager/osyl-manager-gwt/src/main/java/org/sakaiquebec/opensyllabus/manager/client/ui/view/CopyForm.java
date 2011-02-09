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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CopyForm extends OsylManagerAbstractWindowPanel {

    private COSite selectFromSite;

    private COSite selectToSite;

    private TextBox sigleTextBox;

    private PushButton okButton;

    private Map<String, COSite> sigleCourseMap;

    private ListBox suggestionListBox;

    private Image spinner;

    private final OsylCancelDialog diag;

    AsyncCallback<List<COSite>> siteListAsynCB =
	    new AsyncCallback<List<COSite>>() {

		public void onFailure(Throwable caught) {
		    String msg = messages.rpcFailure();
		    spinner.setVisible(false);
		    OsylOkCancelDialog warning =
			    new OsylOkCancelDialog(false, true, messages
				    .OsylWarning_Title(), msg, false, true);
		    warning.show();
		    warning.centerAndFocus();
		}

		public void onSuccess(List<COSite> result) {
		    spinner.setVisible(false);
		    sigleCourseMap = new HashMap<String, COSite>();
		    for (COSite course : result) {
			if (!course.getSiteId().equalsIgnoreCase(
				selectFromSite.getSiteId())) {
			    sigleCourseMap.put(course.getSiteId(), course);
			    suggestionListBox.addItem(course.getSiteId());
			}
		    }
		    suggestionListBox.setSelectedIndex(0);
		    okButton.setEnabled(true);
		}
	    };

    AsyncCallback<Void> copyToAsynCB = new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	    String msg = messages.rpcFailure();
	    if (caught instanceof OsylPermissionException) {
		msg = messages.permission_exception();
	    }
	    diag.hide();
	    OsylOkCancelDialog alert =
		    new OsylOkCancelDialog(false, true, messages
			    .OsylWarning_Title(), msg, true, false);
	    alert.show();
	    alert.centerAndFocus();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    CopyForm.this.onCopyEnd();
	}

    };

    public CopyForm(final OsylManagerController controller, COSite site,
	    OsylCancelDialog aDiag) {
	super(controller);
	this.diag = aDiag;
	this.selectFromSite = site;

	Label title = new Label(messages.mainView_action_copy());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label instruction =
		new Label(messages.copyForm_instruction().replace("{0}",
			selectFromSite.getSiteId()));
	mainPanel.add(instruction);

	Label l = new Label(messages.copyForm_courseIdentifier());

	sigleTextBox = new TextBox();

	final Button search = new Button(messages.copyForm_search());
	search.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		spinner.setVisible(true);
		String value = sigleTextBox.getText();
		suggestionListBox.clear();
		controller.getAllCoAndSiteInfo(value, "", siteListAsynCB);
	    }
	});
	search.setEnabled(false);

	HorizontalPanel hp = new HorizontalPanel();
	hp.add(l);
	l.setStylePrimaryName("OsylManager-form-label");
	hp.add(sigleTextBox);
	sigleTextBox.setStylePrimaryName("OsylManager-form-element");
	hp.add(search);
	hp.setCellWidth(l, "30%");
	hp.setCellWidth(sigleTextBox, "40%");
	hp.setCellWidth(search, "30%");
	hp.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(sigleTextBox,
		HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(search, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setStylePrimaryName("OsylManager-form-genericPanel");
	mainPanel.add(hp);
	mainPanel.setCellHorizontalAlignment(hp,
		HasHorizontalAlignment.ALIGN_CENTER);

	sigleTextBox.addKeyPressHandler(new KeyPressHandler() {

	    public void onKeyPress(KeyPressEvent event) {
		okButton.setEnabled(false);
		String value = sigleTextBox.getText();
		search.setEnabled(value.length() >= 3);
	    }
	});

	suggestionListBox = new ListBox();

	HorizontalPanel hzPanel = new HorizontalPanel();
	Label voidLabel = new Label();
	hzPanel.add(voidLabel);
	hzPanel.setCellWidth(voidLabel, "30%");
	hzPanel.setStylePrimaryName("OsylManager-form-genericPanel");
	hzPanel.add(suggestionListBox);
	spinner = new Image(controller.getImageBundle().ajaxloader());
	hzPanel.add(spinner);
	spinner.setVisible(false);
	mainPanel.add(hzPanel);
	mainPanel.setCellHorizontalAlignment(hzPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	okButton = new PushButton(messages.associateForm_ok());
	okButton.setStylePrimaryName("Osyl-Button");
	okButton.setWidth("50px");
	okButton.setEnabled(false);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		diag.show();
		diag.centerAndFocus();
		selectToSite =
			sigleCourseMap
				.get(suggestionListBox
					.getValue(suggestionListBox
						.getSelectedIndex()));
		controller.copySite(selectFromSite.getSiteId(), selectToSite
			.getSiteId(), copyToAsynCB);
	    }
	});

	PushButton cancelButton = new PushButton(messages.copyForm_cancel());
	cancelButton.setStylePrimaryName("Osyl-Button");
	cancelButton.setWidth("50px");
	cancelButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		CopyForm.super.hide();
	    }
	});

	HorizontalPanel hz = new HorizontalPanel();
	hz.add(okButton);
	hz.add(cancelButton);

	mainPanel.add(hz);
	mainPanel.setCellHorizontalAlignment(hz,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    protected void onCopyEnd() {
	mainPanel.clear();

	Label title = new Label(messages.mainView_action_copy());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label conf = new Label(messages.copyForm_confirmation());
	mainPanel.add(conf);

	okButton = new PushButton(messages.associateForm_ok());
	okButton.setStylePrimaryName("Osyl-Button");
	okButton.setWidth("50px");

	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		CopyForm.super.hide();
	    }
	});
	mainPanel.add(okButton);
	mainPanel.setCellHorizontalAlignment(okButton,
		HasHorizontalAlignment.ALIGN_CENTER);

    }

}
