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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.portal.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.portal.client.view.AbstractPortalView;
import org.sakaiquebec.opensyllabus.portal.client.view.CoursesPage;
import org.sakaiquebec.opensyllabus.portal.client.view.DirectoryCoursePage;
import org.sakaiquebec.opensyllabus.portal.client.view.NavigationHomePage;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OsylPortalEntryPoint implements EntryPoint,
	ValueChangeHandler<String> {

    private String LOCALE_FR_CA = "fr_CA";

    private String LOCALE_EN = "en";

    private String LOCALE_ES = "es";

    private List<String> LOCALES = Arrays.asList(new String[] { LOCALE_FR_CA,
	    LOCALE_EN, LOCALE_ES });

    private Map<String, AbstractPortalView> views;

    private String currentView = "";

    private RootPanel rootPanel;

    /** {@inheritDoc} */
    public void onModuleLoad() {
	rootPanel = RootPanel.get();
	views = new HashMap<String, AbstractPortalView>();
	History.addValueChangeHandler(this);
	PortalController.getInstance().setEntryPoint(this);
	initView();

    }

    private void initView() {
	String requestedView = getRequestedPage();
	if (requestedView == null || "undefined".equals(requestedView)
		|| NavigationHomePage.getViewKeyPrefix().equals(requestedView))
	    setView(new NavigationHomePage());
	else {
	    if (requestedView.startsWith(CoursesPage.getViewKeyPrefix())) {
		String param =
			requestedView.substring(CoursesPage.getViewKeyPrefix()
				.length());
		if (param.startsWith(PortalController.ACAD_CAREER_PREFIX)) {
		    String acadCareerRequested =
			    param.substring(PortalController.ACAD_CAREER_PREFIX
				    .length());
		    PortalController.getInstance()
			    .createCourseViewForAcadCareer(acadCareerRequested);

		} else if (param
			.startsWith(PortalController.RESPONSIBLE_PREFIX)) {
		    String responsibleRequested =
			    param.substring(PortalController.RESPONSIBLE_PREFIX
				    .length());
		    PortalController.getInstance()
			    .createCourseViewForResponsible(
				    responsibleRequested);
		}
	    } else if (requestedView.startsWith(DirectoryCoursePage
		    .getViewKeyPrefix())) {
		String siteIdRequested =
			requestedView
				.substring(requestedView
					.indexOf(DirectoryCoursePage
						.getViewKeyPrefix())
					+ DirectoryCoursePage
						.getViewKeyPrefix().length());
		AsyncCallback<CODirectorySite> callback =
			new AsyncCallback<CODirectorySite>() {

			    public void onFailure(Throwable caught) {
			    }

			    public void onSuccess(CODirectorySite result) {
				setView(new DirectoryCoursePage(result));

			    }
			};
		PortalController.getInstance().getCODirectorySite(
			siteIdRequested, callback);
	    }
	}
	setSakaiIFrameHeight(690);
    }

    public static native Element getSakaiToolIframe() /*-{
						      var elm = $wnd.parent.document.getElementById($wnd.name);
						      return (elm != null ? elm : $wnd.document.body);
						      }-*/;

    private static void setSakaiIFrameHeight(int h) {
	DOM.setStyleAttribute(getSakaiToolIframe(), "height", h + "px");
    }

    public void setView(final AbstractPortalView view) {
	rootPanel.clear();

	HorizontalPanel localePanel = new HorizontalPanel();
	for (final String locale : LOCALES) {
	    Label l =
		    new Label(PortalController.getInstance().getMessage(
			    "locale_" + locale));
	    l.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    changeLocale(locale, view.getViewKey());
		}

	    });
	    l.setStylePrimaryName("NHP_locale");
	    localePanel.add(l);
	}
	localePanel.setStylePrimaryName("NHP_localePanel");

	VerticalPanel vp = new VerticalPanel();
	vp.add(localePanel);
	vp.add(view);
	rootPanel.add(vp);
	rootPanel.add(view);
	if (!views.keySet().contains(view.getViewKey()))
	    views.put(view.getViewKey(), view);
    }

    private native String getRequestedPage()/*-{
					    var currLocation = $wnd.location.toString();
					    var noHistoryCurrLocArray = currLocation.split("#");
					    var history = noHistoryCurrLocArray[1];
					    return history;
					    }-*/;

    private native void changeLocale(String locale, String history)/*-{
								   var currLocation = $wnd.location.toString();
								   var noHistoryCurrLocArray = currLocation.split("#");
								   var noHistoryCurrLoc = noHistoryCurrLocArray[0];
								   var locArray = noHistoryCurrLoc.split("?"); 
								   $wnd.location.href = locArray[0]+"?locale="+locale+"#"+history; 
								   }-*/;

    public void onValueChange(ValueChangeEvent<String> event) {
	String newView = event.getValue();
	AbstractPortalView view = views.get(newView);
	if (view != null) {
	    setView(view);
	}

    }

}
