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

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AttachForm extends OsylManagerAbstractWindowPanel {

    private static List<COSite> coSites;

    private ListBox parentSiteList;

    private PushButton attButton;

    private static List<String> messages = new ArrayList<String>();

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    AsyncCallback<Map<String, String>> parentListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    attButton.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			Window.alert(controller.getMessages()
				.noAssociableCOSite());
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
			for (Iterator<String> sortedSiteIterator =
				sortedMap.keySet().iterator(); sortedSiteIterator
				.hasNext();) {
			    String siteTitle = sortedSiteIterator.next();
			    String siteId = sortedMap.get(siteTitle);
			    parentSiteList.addItem(siteTitle, siteId);
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
	    AttachForm.messages.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    AttachForm.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    AttachForm.asynCB_return++;
	    if (AttachForm.asynCB_return == AttachForm.coSites.size()) {
		if (AttachForm.asynCB_OK == AttachForm.coSites.size()) {
		    AttachForm.this.onSuccess();
		} else {
		    AttachForm.this.onFailure();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null, OsylManagerEvent.SITE_INFO_CHANGE));
	    }
	}

    }

    public AttachForm(final OsylManagerController controller,
	    final List<COSite> cosites) {
	super(controller);
	AttachForm.coSites = cosites;

	Label title =
		new Label(controller.getMessages().mainView_action_attach());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label l = new Label(controller.getMessages().select_parent_site());
	parentSiteList = new ListBox();
	mainPanel.add(createPanel(l, parentSiteList));

	attButton = new PushButton(controller.getMessages().associate());
	attButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		int selectedIndex = parentSiteList.getSelectedIndex();
		if (selectedIndex != -1) {
		    String pId = parentSiteList.getValue(selectedIndex);
		    for (COSite cosite : cosites) {
			AssociateAsyncCallback aac =
				new AssociateAsyncCallback(cosite.getSiteId());
			asynCB_OK=0;
			asynCB_return=0;
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

	List<String> siteIds = new ArrayList<String>();
	for (COSite cosi : cosites)
	    siteIds.add(cosi.getSiteId());
	controller.getOsylSites(siteIds, parentListAsyncCallback);
    }

    public void onSuccess() {
	mainPanel.clear();
	Label l = new Label(controller.getMessages().mainView_action_attach());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);
	mainPanel
		.add(new Label(controller.getMessages().attachForm_attach_ok()));

	PushButton closeButton =
		new PushButton(controller.getMessages().form_close());
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
	Label l = new Label(controller.getMessages().mainView_action_attach());
	l.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(l);
	mainPanel.add(new Label(controller.getMessages()
		.attachForm_attach_error()));

	for (String id : messages) {
	    Label l1 =
		    new Label(id
			    + controller.getMessages()
				    .attachForm_attach_error_detail());
	    mainPanel.add(l1);
	}

	PushButton closeButton =
		new PushButton(controller.getMessages().form_close());
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
