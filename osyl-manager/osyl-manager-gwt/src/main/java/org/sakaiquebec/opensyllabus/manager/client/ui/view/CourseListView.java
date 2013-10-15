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
import java.util.Map.Entry;
import java.util.TreeMap;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
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

    private List<COSite> cosites;

    private AsyncCallback<List<COSite>> asyncCallback =
	    new AsyncCallback<List<COSite>>() {

		public void onFailure(Throwable caught) {
			Window.alert(getController().getMessages().searchError());
		}

		public void onSuccess(List<COSite> result) {
		    cosites = result;
		    mainPanel.clear();
		    siteListBox.clear();
		    if (result == null || result.isEmpty()) {
			Window.alert(getController().getMessages().noCOSite());
		    } else {
			TreeMap<String, String> sortedMap =
				new TreeMap<String, String>(
					LocalizedStringComparator.getInstance());
			for (Iterator<COSite> coSiteIter = result.iterator(); coSiteIter
				.hasNext();) {
			    COSite cos = coSiteIter.next();
			    String siteId = cos.getSiteId();
			    String siteTitle = cos.getSiteName();
			    sortedMap.put(siteTitle, siteId);
			}
			for(Entry<String,String> entry : sortedMap.entrySet()){
			    siteListBox.addItem(entry.getKey(), entry.getValue());
			}
		    }
		    if (!getController().getSelectSites().isEmpty()) {
			List<COSite> newCOSites = new ArrayList<COSite>();
			for (COSite cos : getController().getSelectSites()) {
			    String cosId = cos.getSiteId();
			    for (int i = 0; i < siteListBox.getItemCount(); i++) {
				if (siteListBox.getValue(i).equals(cosId)) {
				    siteListBox.setItemSelected(i, true);
				    newCOSites.add(cos);
				    break;
				}
			    }

			}
			setControllerSelectedSitesWithListBoxSelectedSites();
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
		setControllerSelectedSitesWithListBoxSelectedSites();
	    }

	});
    }

    public void refresh() {
	mainPanel.clear();
	mainPanel.add(new Label(getController().getMessages()
		.courseListView_loading()));
	Image im = new Image(GWT.getModuleBaseURL() + "images/ajaxLoader.gif");
	mainPanel.add(im);
	getController().getAllCoAndSiteInfo("", "", asyncCallback);
    }

    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_CREATION_EVENT
		|| e.getType() == OsylManagerEvent.SITE_INFO_CHANGE)
	    refresh();
    }

    private void setControllerSelectedSitesWithListBoxSelectedSites() {
	ArrayList<COSite> list = new ArrayList<COSite>();
	for (int i = 0; i < siteListBox.getItemCount(); i++) {
	    if (siteListBox.isItemSelected(i))
		list.add(getCOSiteFromList(siteListBox.getValue(i)));
	}
	getController().setSelectSites(list);
    }

    private COSite getCOSiteFromList(String siteId) {
	for (COSite cosite : cosites) {
	    if (cosite.getSiteId().equals(siteId))
		return cosite;
	}
	return null;
    }

}
