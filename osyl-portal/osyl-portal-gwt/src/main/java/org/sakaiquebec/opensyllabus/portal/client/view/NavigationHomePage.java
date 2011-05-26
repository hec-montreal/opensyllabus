/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.portal.client.view;

import java.util.Arrays;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class NavigationHomePage extends AbstractPortalView {

    private VerticalPanel mainPanel;

    private static final List<String> progs = Arrays.asList(new String[] {
	    "BAA", "APRE", "CERT", "MBA", "MSC", "DES", "PHD" });

    private List<String> responsiblesList;

    AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {

	public void onFailure(Throwable caught) {
	    Window.alert(caught.toString());
	}

	public void onSuccess(List<String> result) {
	    responsiblesList = result;
	    initView();
	}
    };

    public NavigationHomePage() {
	super();
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	getController().getAllResponsibles(callback);
    }

    private void initView() {

	Label t1 = new Label(getMessage("courseList"));
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	Label t2 = new Label(getMessage("courseListByAcadCareer"));
	t2.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t2);

	for (final String s : progs) {
	    Label l = new Label(getMessage("acad_career." + s));
	    l.setStylePrimaryName("NHP_link");
	    l.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    Label l = (Label) event.getSource();
		    AsyncCallback<List<COSite>> callback =
			    new AsyncCallback<List<COSite>>() {

				public void onFailure(Throwable caught) {
				}

				public void onSuccess(List<COSite> result) {
				    getController().setView(
					    new CoursesPage(result));

				}
			    };
		    getController().getCoursesForAcadCareer(s,
			    callback);

		}
	    });
	    mainPanel.add(l);
	}
	Label t3 = new Label(getMessage("courseListByResponsible"));
	t3.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t3);

	for (final String responsible : responsiblesList) {
	    Label l = new Label(getMessage("responsible." + responsible));
	    l.setStylePrimaryName("NHP_link");
	    l.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    AsyncCallback<List<COSite>> callback =
			    new AsyncCallback<List<COSite>>() {

				public void onFailure(Throwable caught) {
				}

				public void onSuccess(List<COSite> result) {
				    getController().setView(
					    new CoursesPage(result));

				}
			    };
		    getController().getCoursesForResponsible(responsible,
			    callback);

		}
	    });
	    mainPanel.add(l);
	}
    }

}
