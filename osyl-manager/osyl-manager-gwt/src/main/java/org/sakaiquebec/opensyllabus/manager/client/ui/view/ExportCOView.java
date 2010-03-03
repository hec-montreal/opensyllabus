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
import java.util.TreeMap;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ExportCOView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private VerticalPanel mainPanel;

    private String siteSelectedId;

    /**
     * Constructor.
     * 
     * @param controller
     */
    public ExportCOView(OsylManagerController controller) {
	super(controller);
	initView();
	getController().addEventHandler(this);
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	Label title = new Label(getController().getMessages().exportCOTitle());
	title.setStylePrimaryName("OsylManager-form-title");
	final ListBox siteListBox = new ListBox();
	Map<String, String> sitesMap = getController().getOsylSitesMap();
	if (sitesMap == null || sitesMap.isEmpty()) {
	    Window.alert(getController().getMessages().noCOSite());
	} else {
	    TreeMap<String, String> sortedMap =
		    new TreeMap<String, String>(LocalizedStringComparator
			    .getInstance());
	    for (Iterator<String> sitesMapKeysIterator =
		    sitesMap.keySet().iterator(); sitesMapKeysIterator
		    .hasNext();) {
		String siteId = sitesMapKeysIterator.next();
		String siteTitle = sitesMap.get(siteId);
		sortedMap.put(siteTitle, siteId);
	    }
	    for (Iterator<String> sortedSiteIterator =
		    sortedMap.keySet().iterator(); sortedSiteIterator.hasNext();) {
		String siteTitle = sortedSiteIterator.next();
		String siteId = sortedMap.get(siteTitle);
		siteListBox.addItem(siteTitle, siteId);
	    }
	}
	Button exportButton = new Button();
	exportButton.setText(getController().getMessages().exportCO());
	exportButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		int selectedIndex = siteListBox.getSelectedIndex();
		if (selectedIndex != -1) {
		    siteSelectedId = siteListBox.getValue(selectedIndex);
		    getController().getOsylPackage(siteSelectedId);
		}
	    }

	});

	mainPanel.setWidth("100%");
	mainPanel.add(title);
	mainPanel.add(createFormElement(new Label(getController().getMessages()
		.osylSitesList()), siteListBox));
	mainPanel.add(exportButton);
    }

    /** {@inheritDoc} */
    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (getController().getState() == OsylManagerController.STATE_FILE_DOWNLOAD) {
	    String url = GWT.getModuleBaseURL();
	    String cleanUrl = url.substring(0, url.indexOf("/", 8));
	    String downloadUrl =
		    cleanUrl + "/sdata/c" + getController().getOsylPackageUrl();
	    Window
		    .open(
			    downloadUrl,
			    "_self",
			    "location=no,menubar=no,scrollbars=no,resize=no,resizable=no,status=no,toolbar=no,directories=no,width=5,height=5,top=0,left=0'");
	}
    }
}
