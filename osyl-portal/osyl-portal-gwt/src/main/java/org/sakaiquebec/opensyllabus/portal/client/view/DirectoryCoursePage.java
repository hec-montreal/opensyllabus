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

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
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
public class DirectoryCoursePage extends AbstractPortalView {

    private CODirectorySite site;

    private VerticalPanel mainPanel;

    private CoursesTable courseTable;

    private static String viewPrefix = "DirectoryCoursePage_";

    public DirectoryCoursePage(CODirectorySite site) {
	super(viewPrefix + site.getCourseNumber());
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

	Label t21 = new HTML(site.getDescription());
	t21.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t21);

	Label t3 = new Label(getMessage("directoryCoursePage_features"));
	t3.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t3);

	Label t31 =
		new Label(
			getMessage("directoryCoursePage_responsible")
				+ " : "
				+ ((site.getResponsible() == null) ? ""
					: getMessage(PortalController.RESPONSIBLE_PREFIX
						+ site.getResponsible())));
	t31.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t31);

	Label t32 =
		new Label(getMessage("directoryCoursePage_program")
			+ " : "
			+ getMessage(PortalController.ACAD_CAREER_PREFIX
				+ site.getProgram()));
	t32.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t32);

	Label t33 =
		new Label(
			getMessage("directoryCoursePage_credits")
				+ " : "
				+ ((site.getCredits() == null) ? "" : site
					.getCredits()));
	t33.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t33);

	Label t34 =
		new HTML(getMessage("directoryCoursePage_requirements")
			+ " : "
			+ ((site.getRequirements() == null) ? ""
				: site.getRequirements().replace("\n", "<br>")));
	t34.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t34);

	Label t4 = new Label(getMessage("directoryCoursePage_coursesList"));
	t4.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t4);

	List<CODirectorySite> list = new ArrayList<CODirectorySite>();
	list.add(site);
	courseTable = new CoursesTable(list);
	mainPanel.add(courseTable);
	
	Label archives = new Label(getMessage("directoryCoursePage_archives"));
	archives.addClickHandler(new ClickHandler() {
	    
	    @Override
	    public void onClick(ClickEvent event) {
		  getController().setView(
			    new ArchivesPage(site));
	    }
	});
	archives.setStylePrimaryName("Portal_link");
	mainPanel.add(archives);
	
    }

    public static String getViewKeyPrefix() {
	return viewPrefix;
    }

}
