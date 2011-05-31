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

import java.util.Collections;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CoursesPage extends AbstractPortalView {

    List<CODirectorySite> courses;

    private VerticalPanel mainPanel;

    private Label label;

    public CoursesPage(String key, List<CODirectorySite> courses) {
	super("CoursesPage_" + key);
	label = new Label(getMessage(key));
	this.courses = courses;
	Collections.sort(this.courses,courseNameComparator);
	mainPanel = new VerticalPanel();
	initView();
	initWidget(mainPanel);

    }

    private void initView() {
	Label t1 = new Label(getMessage("courseList"));
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	label.setStylePrimaryName("NHP_titre2");
	mainPanel.add(label);

	for (final CODirectorySite coDirectorySite : courses) {
	    Label l =
		    new HTML(coDirectorySite.getCourseNumber()
			    + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			    + coDirectorySite.getCourseName()
			    + " ("
			    + coDirectorySite.getProgram() + ")");
	    l.setStylePrimaryName("NHP_link");
	    l.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    getController().setView(
			    new DirectoryCoursePage(coDirectorySite));
		}
	    });
	    mainPanel.add(l);
	}
    }
}
