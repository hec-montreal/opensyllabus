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
import org.sakaiquebec.opensyllabus.portal.client.view.NavigationHomePage;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ArchivesPage extends AbstractPortalView{

    private static String viewPrefix = "ArchivesPage_";
    
    private static String LIST_ALL_PROGRAMS = "acad_career_allPrograms";
    
    private static String LIST_ALL_SERVICES = "acad_career_allResponsibles"; 
    
    private static String ALL_PROGRAMS = "ALL";
    
    private static String ALL_SERVICES = "ALL";
    
    private CODirectorySite site;

    private VerticalPanel mainPanel;
    
    private VerticalPanel searchPanel;

    private HorizontalPanel listsPanel;    

    private HorizontalPanel textboxesPanel;

    private ListBox programs;

    private ListBox responsibles;

    private TextBox courseNumber;

    private TextBox courseTitle;

    private TextBox instructor;

    private TextBox trimester;

    private Button searchButton;
    
    private CoursesTable courseTable;
    
    public ArchivesPage(CODirectorySite site) {
	super(viewPrefix + site.getCourseNumber());
	this.site = site;
	mainPanel = new VerticalPanel();
	initView();
	initWidget(mainPanel);
    }

    private void initView() {
	if (!site.getCourseNumber().equals("null") && !site.getCourseName().equals("null")) {
	    Label title1 =
		    new Label(site.getCourseNumber() + " "
			    + site.getCourseName());
	    title1.setStylePrimaryName("NHP_titre1");
	    mainPanel.add(title1);
	    List<CODirectorySite> list = new ArrayList<CODirectorySite>();
	    site.setCurrentSections(site.getArchivedSections());
	    list.add(site);
	    courseTable = new CoursesTable(list);
	    mainPanel.add(courseTable);	    
	} else {
	    Label title2 =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_title"));
	    title2.setStylePrimaryName("NHP_titre1");
   
	    mainPanel.add(title2);

	    searchPanel = new VerticalPanel();
	    listsPanel = new HorizontalPanel();
	    textboxesPanel = new HorizontalPanel();

	    mainPanel.add(new HTML("&nbsp;"));
	    
	    Label lblProgram =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_program"));
	    programs = new ListBox();
	    programs = getCareersListBox();
	    
	    lblProgram.setStylePrimaryName("NHP_titre4");
	    programs.setStylePrimaryName("NHP_titre4");
	    
	    Label lblResponsible =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_responsible"));
	    responsibles = new ListBox();
	    responsibles = getResponsiblesListBox();
	    lblResponsible.setStylePrimaryName("NHP_titre4");
	    responsibles.setStylePrimaryName("NHP_titre4");	    

	    listsPanel.add(lblProgram);
	    listsPanel.add(programs);
	    listsPanel.add(lblResponsible);
	    listsPanel.add(responsibles);	    
	    searchPanel.add(listsPanel);
	    searchPanel.add(new HTML("&nbsp;"));	    
	    
	    listsPanel.setCellHeight(lblProgram, "20%");
	    listsPanel.setCellHeight(programs, "20%");	    
	    listsPanel.setCellHeight(lblResponsible, "20%");
	    listsPanel.setCellHeight(responsibles, "20%");	    
	    listsPanel.setCellVerticalAlignment(lblProgram, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(programs, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(lblResponsible, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(responsibles, HasVerticalAlignment.ALIGN_MIDDLE);
	    
	    Label lblCourseNumber =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_courseNumber"));
	    courseNumber = new TextBox();
	    lblCourseNumber.setStylePrimaryName("NHP_titre4");
	    courseNumber.setStylePrimaryName("NHP_titre4");

	    Label lblCourseTitle =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_courseTitle"));
	    courseTitle = new TextBox();
	    lblCourseTitle.setStylePrimaryName("NHP_titre4");
	    courseTitle.setStylePrimaryName("NHP_titre4");

	    Label lblInstructor =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_instructor"));
	    instructor = new TextBox();
	    lblInstructor.setStylePrimaryName("NHP_titre4");
	    instructor.setStylePrimaryName("NHP_titre4");

	    Label lblTrimester =
		    new Label(PortalController.getInstance().getMessage(
			    "searchCoursePage_trimester"));
	    trimester = new TextBox();
	    lblTrimester.setStylePrimaryName("NHP_titre4");
	    trimester.setStylePrimaryName("NHP_titre4");

	    textboxesPanel.add(lblCourseNumber);
	    textboxesPanel.add(courseNumber);
	    textboxesPanel.add(lblCourseTitle);
	    textboxesPanel.add(courseTitle);
	    textboxesPanel.add(lblInstructor);
	    textboxesPanel.add(instructor);
	    textboxesPanel.add(lblTrimester);
	    textboxesPanel.add(trimester);

	    listsPanel.setCellHeight(lblCourseNumber, "10%");
	    listsPanel.setCellHeight(courseNumber, "15%");
	    listsPanel.setCellHeight(lblCourseTitle, "10%");
	    listsPanel.setCellHeight(courseTitle, "15%");
	    listsPanel.setCellHeight(lblInstructor, "10%");
	    listsPanel.setCellHeight(instructor, "15%");
	    listsPanel.setCellHeight(lblTrimester, "10%");
	    listsPanel.setCellHeight(trimester, "15%");
	    
	    listsPanel.setCellVerticalAlignment(lblCourseNumber, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(courseNumber, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(lblCourseTitle, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(courseTitle, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(lblInstructor, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(instructor, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(lblTrimester, HasVerticalAlignment.ALIGN_MIDDLE);
	    listsPanel.setCellVerticalAlignment(trimester, HasVerticalAlignment.ALIGN_MIDDLE);

	    searchPanel.add(textboxesPanel);
	    searchPanel.add(new HTML("&nbsp;"));	    

	    searchButton = new Button(PortalController.getInstance().getMessage("button_search").trim());
	    searchButton.setStylePrimaryName("Portal_button");
	    searchButton.setWidth("60px");
	    searchButton.setEnabled(true);
	    searchButton.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {
			//Search list
			searchButton.setEnabled(false);
			String p1 = courseNumber.getValue().trim();
			String p2 = courseTitle.getValue().trim();
			String p3 = instructor.getValue().trim();
			String p4 = programs.getValue(programs.getSelectedIndex());
			String p5 = responsibles.getValue(responsibles.getSelectedIndex());
			String p6 = trimester.getValue().trim();
			getController().createCourseViewForFields(p1, p2, p3, p4, p5, p6); 	
		    }
		});
	    searchPanel.add(searchButton);
	    searchPanel.setCellVerticalAlignment(searchButton, HasVerticalAlignment.ALIGN_BOTTOM);

	    mainPanel.add(searchPanel);  
	    mainPanel.add(new HTML("&nbsp;"));	
	}
    }
    
    public static String getViewKeyPrefix(){
	return viewPrefix;
    }

    public static ListBox getCareersListBox() {
	ListBox lb = new ListBox();
	lb.addItem(PortalController.getInstance().getMessage(LIST_ALL_PROGRAMS), ALL_PROGRAMS);
	for (final String program : NavigationHomePage.ACAD_CARREERS) {
	    lb.addItem(PortalController.getInstance().getMessage(
		    PortalController.ACAD_CAREER_PREFIX + program), program);
	}
	return lb;
    }

    public static ListBox getResponsiblesListBox() {
	ListBox lb = new ListBox();
	lb.addItem(PortalController.getInstance().getMessage(LIST_ALL_SERVICES), ALL_SERVICES);
	for (final String responsible1 : NavigationHomePage.responsible_part1) {
	    lb.addItem(PortalController.getInstance().getMessage(
		    PortalController.RESPONSIBLE_PREFIX + responsible1), responsible1);
	}
	for (final String responsible2 : NavigationHomePage.responsible_part2) {
	    lb.addItem(PortalController.getInstance().getMessage(
		    PortalController.RESPONSIBLE_PREFIX + responsible2), responsible2);
	}
	return lb;
    }
}
