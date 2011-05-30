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

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class NavigationHomePage extends AbstractPortalView {

    private VerticalPanel mainPanel;

    private static final List<String> progs = Arrays.asList(new String[] {
	    "BAA", "APRE", "CERT", "MBA", "MSC", "DESS", "PHD" });

    private static final List<String> responsible_part1 = Arrays
	    .asList(new String[] { "IEA", "FINANCE", "GOL", "GRH", "INTERNAT",
		    "MQG", "SC.COMPT.", "TI", "MARKETING", "MNGT" });

    private static final List<String> responsible_part2 = Arrays
	    .asList(new String[] { "BUR.REGIST", "QUAL.COMM.", "BAA", "CERTIFICAT",
		    "DIPLOMES", "MBA", "MSC", "DOCTORAT" });

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
	// getController().getAllResponsibles(callback);
	initView();
	History.newItem(Integer.toString(this.hashCode()));
    }

    private void initView() {

	Label t1 = new Label(getMessage("courseList"));
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	Label t2 = new Label(getMessage("courseListByAcadCareer"));
	t2.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t2);

	mainPanel.add(getProgramLabel("BAA"));
	Label apre_label = getProgramLabel("APRE");
	apre_label.setText("- "+apre_label.getText());
	mainPanel.add(apre_label);
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel("CERT"));
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel("MBA"));
	mainPanel.add(getProgramLabel("MSC"));
	mainPanel.add(getProgramLabel("DES"));
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel("PHD"));

	Label t3 = new Label(getMessage("courseListByResponsible"));
	t3.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t3);

	for (final String responsible : responsible_part1) {
	    mainPanel.add(getResponsibleLabel(responsible));
	}

	mainPanel.add(new HTML("&nbsp;"));

	for (final String responsible : responsible_part2) {
	    mainPanel.add(getResponsibleLabel(responsible));
	}
    }

    private Label getProgramLabel(final String program) {
	Label l = new Label(getMessage("acad_career." + program));
	l.setStylePrimaryName("NHP_link");
	l.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		AsyncCallback<List<CODirectorySite>> callback =
			new AsyncCallback<List<CODirectorySite>>() {

			    public void onFailure(Throwable caught) {
			    }

			    public void onSuccess(List<CODirectorySite> result) {
				getController().setView(
					new CoursesPage(
						getMessage("acad_career."
							+ program), result));

			    }
			};
		getController().getCoursesForAcadCareer(program, callback);

	    }
	});
	return l;
    }

    private Label getResponsibleLabel(final String responsible) {
	Label l = new Label(getMessage("responsible." + responsible));
	l.setStylePrimaryName("NHP_link");
	l.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		AsyncCallback<List<CODirectorySite>> callback =
			new AsyncCallback<List<CODirectorySite>>() {

			    public void onFailure(Throwable caught) {
			    }

			    public void onSuccess(List<CODirectorySite> result) {
				getController()
					.setView(
						new CoursesPage(
							getMessage("responsible."
								+ responsible),
							result));

			    }
			};
		getController().getCoursesForResponsible(responsible, callback);

	    }
	});
	return l;
    }

}
