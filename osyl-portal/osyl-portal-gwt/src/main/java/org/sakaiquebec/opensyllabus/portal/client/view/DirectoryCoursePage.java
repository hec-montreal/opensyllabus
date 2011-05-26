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

import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class DirectoryCoursePage extends AbstractPortalView {

    private CODirectorySite site;

    private VerticalPanel mainPanel;

    public DirectoryCoursePage(CODirectorySite site) {
	super();
	this.site = site;
	mainPanel = new VerticalPanel();
	initView();
	initWidget(mainPanel);
    }

    private void initView() {
	Label t1 =
		new Label(site.getCourseNumber() + " " + site.getCourseName());
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	Label t2 = new Label(getMessage("directoryCoursePage_description"));
	t2.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t2);

	Label t21 = new Label(getMessage("directoryCoursePage_features"));
	t21.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t21);

	Label t22 =
		new Label(getMessage("directoryCoursePage_responsible") + " : "
			+ site.getResponsible());
	t22.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t22);

	Label t23 =
		new Label(getMessage("directoryCoursePage_program") + " : "
			+ site.getProgram());
	t23.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t23);

	Label t24 =
		new Label(getMessage("directoryCoursePage_credits") + " : "
			+ site.getCredits());
	t24.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t24);

	Label t3 =
		new HTML(getMessage("directoryCoursePage_requirements")
			+ " : " + site.getRequirements());
	t3.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t3);

	Label t4 = new Label(getMessage("directoryCoursePage_coursesList"));
	t4.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t4);

	for (final Entry<String, String> entry : site.getSections().entrySet()) {
	    Label link = new Label(entry.getKey());
	    link.setStylePrimaryName("NHP_link");
	    link.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    Window.open("/osyl-editor-sakai-tool/index.jsp?siteId="
			    + entry.getKey(), "_self", "");
		}
	    });
	    mainPanel.add(link);
	}

    }

}
