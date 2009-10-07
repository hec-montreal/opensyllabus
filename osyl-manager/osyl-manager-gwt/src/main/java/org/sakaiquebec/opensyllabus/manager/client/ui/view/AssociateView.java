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

package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import java.util.Iterator;
import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AssociateView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private static final int ACTION_ASSOCIATE = 0;

    private static final int ACTION_DISSOCIATE = 1;

    private VerticalPanel mainPanel;

    private String siteSelectedId;

    private Button adButton;

    private ListBox siteList;

    private ListBox parentSiteList;

    private String parentId;

    AsyncCallback<String> parentAsyncCallback = new AsyncCallback<String>() {

	public void onFailure(Throwable caught) {
	    Window.alert(caught.toString());
	    adButton.setEnabled(false);
	    parentId = null;
	}

	public void onSuccess(String result) {
	    if (result != null) {
		setButtonAction(ACTION_DISSOCIATE);
		parentId = result;
	    } else {
		setButtonAction(ACTION_ASSOCIATE);
		parentId = "";
	    }
	    setSelectedParent(parentId);
	}
    };

    AsyncCallback<Map<String, String>> parentListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    adButton.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    if (result == null || result.isEmpty()) {
			Window.alert(getController().getMessages().noAssociableCOSite());
			adButton.setEnabled(false);
		    } else {
			for (Iterator<String> sitesMapKeysIterator =
				result.keySet().iterator(); sitesMapKeysIterator
				.hasNext();) {
			    String siteId = sitesMapKeysIterator.next();
			    String siteTitle = result.get(siteId);
			    parentSiteList.addItem(siteTitle, siteId);
			}
			setSelectedParent(parentId);
		    }
		}

	    };

    AsyncCallback<Void> associateAsyncCallback = new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	    Window.alert("Erreur");// TODO
	}

	public void onSuccess(Void result) {
	    onListChange();
	}

    };

    AsyncCallback<Void> dissociateAsyncCallback = new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	    Window.alert("Erreur");// TODO
	}

	public void onSuccess(Void result) {
	    onListChange();
	}

    };

    ClickListener associateClickListener = new ClickListener() {

	public void onClick(Widget sender) {
	    int selectedIndex = parentSiteList.getSelectedIndex();
	    if (selectedIndex != -1) {
		String pId = parentSiteList.getValue(selectedIndex);
		getController().associate(siteSelectedId, pId,
			associateAsyncCallback);
	    }

	}

    };

    ClickListener dissociateClickListener = new ClickListener() {

	public void onClick(Widget sender) {
	    getController().dissociate(siteSelectedId, parentId,
		    dissociateAsyncCallback);
	}
    };

    public AssociateView(OsylManagerController controller) {
	super(controller);
	initView();
	getController().addEventHandler(this);
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	Label title =
		new Label(getController().getMessages().associateDissociate());
	title.setStylePrimaryName("OsylManager-form-title");

	HorizontalPanel horizontalPanel = new HorizontalPanel();

	siteList = new ListBox();
	parentSiteList = new ListBox();
	adButton = new Button();
	adButton.setText(getController().getMessages().associate());

	Map<String, String> sitesMap = getController().getOsylSitesMap();
	if (sitesMap == null || sitesMap.isEmpty()) {
	    Window.alert(getController().getMessages().noCOSite());
	} else {
	    for (Iterator<String> sitesMapKeysIterator =
		    sitesMap.keySet().iterator(); sitesMapKeysIterator
		    .hasNext();) {
		String siteId = sitesMapKeysIterator.next();
		String siteTitle = sitesMap.get(siteId);
		siteList.addItem(siteTitle, siteId);
	    }
	}
	siteList.addChangeListener(new ChangeListener() {

	    public void onChange(Widget sender) {
		parentId = null;
		adButton.setEnabled(false);
		onListChange();
	    }

	});

	adButton.setEnabled(false);

	horizontalPanel.add(siteList);
	horizontalPanel.add(adButton);
	horizontalPanel.add(parentSiteList);

	siteList.setSelectedIndex(0);
	onListChange();
	mainPanel.setWidth("100%");
	mainPanel.add(title);
	mainPanel.add(horizontalPanel);
    }

    
    private void onListChange(){
	parentSiteList.clear();
	int selectedIndex = siteList.getSelectedIndex();
	if (selectedIndex != -1) {
	    siteSelectedId = siteList.getValue(selectedIndex);
	    getController().getOsylSites(siteSelectedId,
		    parentListAsyncCallback);
	    getController().getParent(siteSelectedId,
		    parentAsyncCallback);
	}
    }
    
    private void setSelectedParent(String parentId) {
	if (parentId != null) {
	    for (int i = 0; i < parentSiteList.getItemCount(); i++) {
		if (parentSiteList.getValue(i).equals(parentId)) {
		    String text = parentSiteList.getItemText(i);
		    parentSiteList.clear();
		    parentSiteList.addItem(text, parentId);
		    parentSiteList.setSelectedIndex(0);
		    break;
		}
	    }
	    adButton.setEnabled(true);
	}
    }

    private void setButtonAction(int action) {
	adButton.removeClickListener(associateClickListener);
	adButton.removeClickListener(dissociateClickListener);
	switch (action) {
	case AssociateView.ACTION_ASSOCIATE:
	    adButton.setText(getController().getMessages().associate());
	    adButton.addClickListener(associateClickListener);
	    break;
	case AssociateView.ACTION_DISSOCIATE:
	    adButton.setText(getController().getMessages().dissociate());
	    adButton.addClickListener(dissociateClickListener);
	    break;
	}
    }

    /** {@inheritDoc} */
    public void onOsylManagerEvent() {

    }

}
