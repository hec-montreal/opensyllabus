package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class AssociateToCMView extends OsylManagerAbstractView implements
	OsylManagerEventHandler {

    private VerticalPanel mainPanel;

    private String siteSelectedId;

    private Button adButton;

    private ListBox courseSitesList;

    private String cmCourseSectionId;

    private Label selectSiteLabel;

    private Label enterCourseLabel;

    private SuggestBox cmSuggestBox;

    private HashMap<String, String> cmHashMap;

    private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

    AsyncCallback<Map<String, String>> coursesListAsyncCallback =
	    new AsyncCallback<Map<String, String>>() {

		public void onFailure(Throwable caught) {
		    adButton.setEnabled(false);
		}

		public void onSuccess(Map<String, String> result) {
		    adButton.setEnabled(true);
		    cmHashMap = new HashMap<String, String>();
		    for (Iterator<String> coursesMapKeysIterator =
			    result.keySet().iterator(); coursesMapKeysIterator
			    .hasNext();) {
			String courseId = coursesMapKeysIterator.next();
			String courseTitle = result.get(courseId);
			cmHashMap.put(courseTitle, courseId);
			oracle.add(courseTitle);
		    }
		}
	    };

    AsyncCallback<Boolean> associateToCMAsyncCallback =
	    new AsyncCallback<Boolean>() {

		public void onFailure(Throwable caught) {
		    Window.alert("Erreur");// TODO
		}

		public void onSuccess(Boolean result) {
		    if (result.booleanValue())
			Window.alert(getController().getMessages()
				.associateToCMSuccess());
		    else
			Window.alert(getController().getMessages()
				.associateToCMFailure());
		}

	    };

    ClickHandler associateClickHandler = new ClickHandler() {

	public void onClick(ClickEvent event) {

	    cmCourseSectionId = cmHashMap.get(cmSuggestBox.getValue());
	    if (cmCourseSectionId != null) {
		// We call the method from the controller, if verifies if the
		// course
		// exist
		// and returns a boolean to tell if all worked or not
		getController().associateToCM(cmCourseSectionId,
			siteSelectedId, associateToCMAsyncCallback);
	    } else {
		Window
			.alert(getController().getMessages()
				.unexistingCMcourse());
	    }
	}

    };

    public AssociateToCMView(OsylManagerController controller) {
	super(controller);
	initView();
	getController().addEventHandler(this);
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	Label title =
		new Label(getController().getMessages().associateDissociateCM());
	title.setStylePrimaryName("OsylManager-form-title");
	selectSiteLabel =
		new Label(getController().getMessages()
			.associateToCMChooseSite());
	enterCourseLabel =
		new Label(getController().getMessages()
			.associateToCMChooseCourse());
	HorizontalPanel horizontalPanel = new HorizontalPanel();

	VerticalPanel verticalPanel = new VerticalPanel();

	courseSitesList = new ListBox();
	cmSuggestBox = new SuggestBox(oracle);
	cmSuggestBox.setLimit(20);

	adButton = new Button();
	adButton.addClickHandler(associateClickHandler);
	adButton.setText(getController().getMessages().associateToCM());

	// We retreive all the sites that the current user has access to

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
		courseSitesList.addItem(siteTitle, siteId);
	    }
	}

	courseSitesList.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		cmCourseSectionId = null;

		onListChange();
	    }

	});

	// We retrieve the course available in the course management
	getController().getCMCourses(coursesListAsyncCallback);

	adButton.setEnabled(true);

	verticalPanel.add(selectSiteLabel);
	verticalPanel.add(enterCourseLabel);
	verticalPanel.add(adButton);

	horizontalPanel.add(verticalPanel);

	verticalPanel = new VerticalPanel();
	verticalPanel.add(courseSitesList);
	verticalPanel.add(cmSuggestBox);

	horizontalPanel.add(verticalPanel);

	courseSitesList.setSelectedIndex(0);
	mainPanel.setWidth("100%");
	mainPanel.add(title);
	mainPanel.add(horizontalPanel);
    }

    private void onListChange() {
	int selectedIndex = courseSitesList.getSelectedIndex();
	if (selectedIndex != -1) {
	    siteSelectedId = courseSitesList.getValue(selectedIndex);

	}
    }

    /** {@inheritDoc} */
    public void onOsylManagerEvent() {

    }

}
