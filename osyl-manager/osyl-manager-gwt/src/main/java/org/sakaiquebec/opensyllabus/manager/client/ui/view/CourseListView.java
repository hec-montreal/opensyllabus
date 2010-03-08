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
import java.util.Map;
import java.util.TreeMap;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CourseListView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private VerticalPanel mainPanel;

    private ListBox siteListBox;

    private AsyncCallback<Map<String, String>> asyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    Window.alert(getController().getMessages().rpcFailure());
		}

		public void onSuccess(Map<String, String> result) {
		    mainPanel.clear();
		    siteListBox.clear();
		    if (result == null || result.isEmpty()) {
			Window.alert(getController().getMessages().noCOSite());
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
			    siteListBox.addItem(siteTitle, siteId);
			}
		    }
		    mainPanel.add(siteListBox);

		}
	    };

    public CourseListView(OsylManagerController controller) {
	super(controller);
	initView();
	initWidget(mainPanel);
	refresh();
	controller.addEventHandler(this);
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	mainPanel.setWidth("100%");
	mainPanel.setHeight("200px");

	siteListBox = new ListBox(true);
	siteListBox.setHeight("275px");
	siteListBox.setWidth("100%");
	siteListBox.addChangeHandler(new ChangeHandler() {
	    public void onChange(ChangeEvent event) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < siteListBox.getItemCount(); i++) {
		    if (siteListBox.isItemSelected(i))
			list.add(siteListBox.getValue(i));
		}
		getController().setSelectSiteIDs(list);
	    }
	});
    }

    public void refresh() {
	mainPanel.clear();
	mainPanel.add(new Label(getController().getMessages()
		.courseListView_loading()));
	Image im = new Image(GWT.getModuleBaseURL() + "images/ajaxLoader.gif");
	mainPanel.add(im);
	getController().getOsylSitesMap(asyncCallback);
    }

    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_CREATION_EVENT)
	    refresh();
    }

}
