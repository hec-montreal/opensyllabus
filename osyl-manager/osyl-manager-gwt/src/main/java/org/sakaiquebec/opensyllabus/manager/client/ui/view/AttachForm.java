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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class AttachForm extends OsylManagerAbstractWindowPanel {

    private List<COSite> coSites;

    private TextBox searchTextBox;

    private ListBox parentSiteList;

    private PushButton attButton;
    private static List<String> lMsg = new ArrayList<String>();

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private final OsylCancelDialog diag;

    private Image spinner;

    AsyncCallback<Map<String, String>> parentListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    attButton.setEnabled(false);
		    spinner.removeFromParent();
		}

		public void onSuccess(Map<String, String> result) {
		    spinner.removeFromParent();
		    if (result == null || result.isEmpty()) {
			OsylOkCancelDialog warning =
				new OsylOkCancelDialog(false, true, messages
					.OsylWarning_Title(), messages
					.noAssociableCOSite(), true, false);
			warning.show();
			warning.centerAndFocus();
			attButton.setEnabled(false);
		    } else {
			TreeMap<String, String> sortedMap =
				new TreeMap<String, String>(
					LocalizedStringComparator.getInstance());
			for (Iterator<String> sitesMapKeysIterator =
				result.keySet().iterator(); sitesMapKeysIterator
				.hasNext();) {
			    String siteId = sitesMapKeysIterator.next();
			    String siteTitle = result.get(siteId);
			    sortedMap.put(siteTitle, siteId);
			}
			for (Iterator<Entry<String, String>> sortedSIterator =
				sortedMap.entrySet().iterator(); sortedSIterator
				.hasNext();) {
			    Entry<String, String> entry =
				    sortedSIterator.next();
			    parentSiteList.addItem(entry.getKey(), entry
				    .getValue());
			}
			attButton.setEnabled(true);
		    }
		}

	    };

    private class AssociateAsyncCallback implements AsyncCallback<Void> {

	private String siteId;

	public AssociateAsyncCallback(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    diag.hide();
	    String msg =
		    siteId + " " + messages.attachForm_attach_error_detail()
			    + " :" + caught.getMessage();
	    AttachForm.lMsg.add(msg);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    AttachForm.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    AttachForm.asynCB_return++;
	    if (AttachForm.asynCB_return == coSites.size()) {
		if (AttachForm.asynCB_OK == coSites.size()) {
		    AttachForm.this.onSuccess();
		} else {
		    AttachForm.this.onFailure();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    }
	}

    }

    public AttachForm(final OsylManagerController controller,
	    final List<COSite> cosites, OsylCancelDialog aDiag) {
	super(controller);
	coSites = cosites;
	this.diag = aDiag;

	Label title = new Label(messages.mainView_action_attach());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);
	searchTextBox = new TextBox();

	final Button search = new Button(messages.copyForm_search());
	search.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		spinner.setVisible(true);
		String value = searchTextBox.getText();
		parentSiteList.clear();
		List<String> siteIds = new ArrayList<String>();
		for (COSite cosi : cosites)
		    siteIds.add(cosi.getSiteId());
		if (controller.isInHostedMode()) {
		    getHostedModeData();
		} else {
		    controller.getOsylSites(siteIds, value,
			    parentListAsyncCallback);
		}
	    }
	});
	search.setEnabled(true);

	HorizontalPanel hp = new HorizontalPanel();
	Label l = new Label(messages.attachForm_siteTitle());
	hp.add(l);
	l.setStylePrimaryName("OsylManager-form-label");
	hp.add(searchTextBox);
	searchTextBox.setStylePrimaryName("OsylManager-form-element");
	hp.add(search);
	hp.setCellWidth(l, "30%");
	hp.setCellWidth(searchTextBox, "40%");
	hp.setCellWidth(search, "30%");
	hp.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(searchTextBox,
		HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(search, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setStylePrimaryName("OsylManager-form-genericPanel");
	mainPanel.add(hp);
	mainPanel.setCellHorizontalAlignment(hp,
		HasHorizontalAlignment.ALIGN_CENTER);

	HorizontalPanel hzPanel = new HorizontalPanel();
	Label voidLabel = new Label();
	hzPanel.add(voidLabel);
	hzPanel.setCellWidth(voidLabel, "30%");
	hzPanel.setStylePrimaryName("OsylManager-form-genericPanel");
	parentSiteList = new ListBox();
	hzPanel.add(parentSiteList);
	spinner = new Image(controller.getImageBundle().ajaxloader());
	hzPanel.add(spinner);
	spinner.setVisible(false);
	mainPanel.add(hzPanel);
	mainPanel.setCellHorizontalAlignment(hzPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	attButton = new PushButton(messages.attach());
	attButton.setStylePrimaryName("Osyl-Button");
	attButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		diag.show();
		diag.centerAndFocus();
		int selectedIndex = parentSiteList.getSelectedIndex();
		if (selectedIndex != -1) {
		    String pId = parentSiteList.getValue(selectedIndex);
		    for (COSite cosite : cosites) {
			AssociateAsyncCallback aac =
				new AssociateAsyncCallback(cosite.getSiteId());
			asynCB_OK = 0;
			asynCB_return = 0;
			controller.associate(cosite.getSiteId(), pId, aac);
		    }

		}
	    }
	});
	attButton.setWidth("70px");
	attButton.setEnabled(false);
	mainPanel.add(attButton);
	mainPanel.setCellHorizontalAlignment(attButton,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    private void getHostedModeData() {
	for (int i = 1; i <= 10; i++) {
	    String siteTitle = "site" + i;
	    String siteId = "site" + i;
	    parentSiteList.addItem(siteTitle, siteId);
	}
	attButton.setEnabled(true);
    }

    public void onSuccess() {
	mainPanel.clear();
	Label l = new Label(messages.mainView_action_attach());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);
	mainPanel.add(new Label(messages.attachForm_attach_ok()));

	PushButton closeButton = new PushButton(messages.form_close());
	closeButton.setStylePrimaryName("Osyl-Button");
	closeButton.setWidth("40px");
	closeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		AttachForm.super.hide();
	    }
	});
	mainPanel.add(closeButton);
	mainPanel.setCellHorizontalAlignment(closeButton,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    public void onFailure() {
	mainPanel.clear();
	Label l = new Label(messages.mainView_action_attach());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);
	mainPanel.add(new Label(messages.attachForm_attach_error()));

	for (String msg : lMsg) {
	    mainPanel.add(new Label(msg));
	}

	PushButton closeButton = new PushButton(messages.form_close());
	closeButton.setStylePrimaryName("Osyl-Button");
	closeButton.setWidth("40px");
	closeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		AttachForm.super.hide();
	    }
	});
	mainPanel.add(closeButton);
	mainPanel.setCellHorizontalAlignment(closeButton,
		HasHorizontalAlignment.ALIGN_CENTER);
	setWidget(mainPanel);
    }

}
