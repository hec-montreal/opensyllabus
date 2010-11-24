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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
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

    PushButton okButton;

    Grid grid;

    private int asynCB_return = 0;

    private Label attachInProgess;

    private Image spinner;

    private String selectedParentId = null;

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

    private class AttachAsynCallBack implements AsyncCallback<Void> {

	private int siteIndex;

	public AttachAsynCallBack(int siteIndex) {
	    super();
	    this.siteIndex = siteIndex;
	}

	public void onFailure(Throwable caught) {
	    Image image = new Image(controller.getImageBundle().cross16());
	    image.setTitle(messages.attachForm_attach_error() + ":"
		    + caught.getMessage());
	    grid.setWidget(siteIndex, 1, image);
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
		attachInProgess.setVisible(false);
		okButton.setEnabled(true);
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    } else {
		controller
			.associate(coSites.get(siteIndex + 1).getSiteId(),
				selectedParentId, new AttachAsynCallBack(
					siteIndex + 1));
	    }
	}
    }

    public AttachForm(final OsylManagerController controller,
	    final List<COSite> cosites) {
	super(controller);
	coSites = cosites;

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
		int selectedIndex = parentSiteList.getSelectedIndex();
		if (selectedIndex != -1) {
		    selectedParentId = parentSiteList.getValue(selectedIndex);
		    displayAttachGrid();
		}
	    }
	});
	attButton.setWidth("70px");
	attButton.setEnabled(false);
	mainPanel.add(attButton);
	mainPanel.setCellHorizontalAlignment(attButton,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    private void displayAttachGrid() {
	mainPanel.clear();
	Label title = new Label(messages.mainView_action_attach());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	attachInProgess = new Label(messages.attachForm_attach_inProgress());
	attachInProgess.setStylePrimaryName("OsylManager-form-publicationText");
	mainPanel.add(attachInProgess);

	grid = new Grid(coSites.size(), 2);
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
		AttachForm.this.hide();
	    }
	});

	HorizontalPanel hz = new HorizontalPanel();
	hz.add(okButton);

	mainPanel.add(hz);
	mainPanel.setCellHorizontalAlignment(hz,
		HasHorizontalAlignment.ALIGN_CENTER);

	controller.associate(coSites.get(0).getSiteId(), selectedParentId,
		new AttachAsynCallBack(0));
    }

    private void getHostedModeData() {
	for (int i = 1; i <= 10; i++) {
	    String siteTitle = "site" + i;
	    String siteId = "site" + i;
	    parentSiteList.addItem(siteTitle, siteId);
	}
	attButton.setEnabled(true);
    }
}
