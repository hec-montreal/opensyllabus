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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ColumnResizePolicy;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CoursesPage extends AbstractPortalView {

    List<CODirectorySite> courses;

    private VerticalPanel mainPanel;

    private ScrollTable coursesTable;

    private DescriptionView descriptionView;

    private Label label;

    private static String VIEW_PREFIX = "CoursesPage_";
    
    private class CourseClickHandler implements ClickHandler {

	private CODirectorySite coDirectorySite;

	public CourseClickHandler(CODirectorySite cods) {
	    this.coDirectorySite = cods;
	}

	public void onClick(ClickEvent event) {
	    AsyncCallback<String> callback = new AsyncCallback<String>() {

		public void onFailure(Throwable caught) {
		    getController().setView(
			    new DirectoryCoursePage(coDirectorySite));
		}

		public void onSuccess(String result) {
		    coDirectorySite.setDescription(result);
		    getController().setView(
			    new DirectoryCoursePage(coDirectorySite));

		}
	    };
	    getController().getDescription(coDirectorySite.getCourseNumber(),
		    callback);
	}

    }

    public CoursesPage(String key, List<CODirectorySite> courses) {
	super(VIEW_PREFIX + key);
	label = new Label(getMessage(key));
	this.courses = courses;
	Collections.sort(this.courses, courseNameComparator);
	mainPanel = new VerticalPanel();
	if (!key.equals(PortalController.FIELDS_NAME)) {
	    initView2();    
	} else {
	    initView3();
	}
	initWidget(mainPanel);
    }
    
    private void initView2() {
	mainPanel.clear();
	
	Label t1 = new Label(getMessage("courseList"));
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);
	label.setStylePrimaryName("NHP_titre2");
	mainPanel.add(label);
	
	FixedWidthGrid dataTable = new FixedWidthGrid(0, 5);
	dataTable.setSelectionEnabled(false);
	FixedWidthFlexTable headerTable = createHeaderTable();
	coursesTable = new ScrollTable(dataTable, headerTable);
	coursesTable.addStyleName("CP-scrollTable");
	coursesTable.setSortPolicy(ScrollTable.SortPolicy.SINGLE_CELL);
	coursesTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
	coursesTable.setColumnResizePolicy(ColumnResizePolicy.MULTI_CELL);

	coursesTable.getDataTable().clearAll();
	coursesTable.getDataTable().resize(0, 5);
	int i = 0;
	int rowNum;
	for (final CODirectorySite coSite : courses) {
	    rowNum = coursesTable.getDataTable().insertRow(i);

	    ClickHandler clickHandler = new CourseClickHandler(coSite);

	    coursesTable.getDataTable().getRowFormatter()
		    .setStylePrimaryName(rowNum, "CP-scrollTable-row");

	    Label number = new Label(coSite.getCourseNumber());
	    number.addClickHandler(clickHandler);
	    coursesTable.getDataTable().setWidget(rowNum, 0, number);

	    Label name = new Label(coSite.getCourseName());
	    name.addClickHandler(clickHandler);
	    coursesTable.getDataTable().setWidget(rowNum, 1, name);

	    Label instructor =
		    new Label(coSite.getInstructor() == null ? ""
			    : coSite.getInstructor());
	    instructor.addClickHandler(clickHandler);
	    coursesTable.getDataTable().setWidget(rowNum, 2, instructor);

	    Label prog =
		    new Label(getMessage(PortalController.ACAD_CAREER_PREFIX
			    + coSite.getProgram()+PortalController.ABBREVIATION_SUFFIX));
	    prog.addClickHandler(clickHandler);
	    coursesTable.getDataTable().setWidget(rowNum, 3, prog);

	    final Image descImage = new Image(getImages().description());
	    descImage.setTitle(getMessage("coursesPage_Description"));
	    descImage.addMouseOverHandler(new MouseOverHandler() {

		public void onMouseOver(MouseOverEvent event) {

		    AsyncCallback<String> callback =
			    new AsyncCallback<String>() {

				public void onFailure(Throwable caught) {
				    showDescriptonView();
				}

				public void onSuccess(String result) {
				    coSite.setDescription(result);
				    showDescriptonView();
				}
			    };
		    if (descriptionView != null
			    && descriptionView.isShowing()
			    && descriptionView.getCoDirectorySite()
				    .getCourseNumber()
				    .equals(coSite.getCourseNumber()))
			return;
		    else
			getController().getDescription(
				coSite.getCourseNumber(), callback);

		}

		private void showDescriptonView() {
		    if (descriptionView != null && descriptionView.isShowing())
			descriptionView.hide();
		    descriptionView = new DescriptionView(coSite);
		    descriptionView.setPopupPosition(
			    descImage.getAbsoluteLeft() + descImage.getWidth(),
			    descImage.getAbsoluteTop() + descImage.getHeight());
		    descriptionView.show();
		    descriptionView.pack();
		    descriptionView.toFront();
		}

	    });
	    coursesTable.getDataTable().setWidget(rowNum, 4, descImage);

	    i++;
	}
	mainPanel.add(coursesTable);
    }

    private FixedWidthFlexTable createHeaderTable() {
	FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
	FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
	headerTable.setTitle("titeHeaderTable");

	headerTable.setHTML(0, 0, getMessage("coursesPage_courseNumber"));
	formatter.setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 1, getMessage("coursesPage_courseTitle"));
	formatter.setHorizontalAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 2, getMessage("coursesPage_Coordinator"));
	formatter.setHorizontalAlignment(0, 2,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 3, getMessage("coursesPage_Program"));
	formatter.setHorizontalAlignment(0, 3,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 4, getMessage("coursesPage_Description"));
	formatter.setHorizontalAlignment(0, 4,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 4, HasVerticalAlignment.ALIGN_TOP);

	return headerTable;
    }

    private FixedWidthFlexTable createSearchHeaderTable() {
	FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
	FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
	headerTable.setTitle("titeHeaderTable");

	headerTable.setHTML(0, 0, getMessage("coursesPage_courseNumber"));
	formatter.setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 1, getMessage("coursesPage_courseTitle"));
	formatter.setHorizontalAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 2, getMessage("coursesPage_Instructor"));
	formatter.setHorizontalAlignment(0, 2,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 3, getMessage("coursesPage_Program"));
	formatter.setHorizontalAlignment(0, 3,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 4, getMessage("coursesPage_Description"));
	formatter.setHorizontalAlignment(0, 4,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 4, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 5, getMessage("coursesPage_Html"));
	formatter.setHorizontalAlignment(0, 5,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 5, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 6, getMessage("coursesPage_Pdf"));
	formatter.setHorizontalAlignment(0, 6,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 6, HasVerticalAlignment.ALIGN_TOP);
	
	return headerTable;
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
			    + getMessage(PortalController.ACAD_CAREER_PREFIX
				    + coDirectorySite.getProgram()) + ")");
	    l.setStylePrimaryName("NHP_link");
	    l.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    AsyncCallback<String> callback =
			    new AsyncCallback<String>() {

				public void onFailure(Throwable caught) {
				    getController().setView(
					    new DirectoryCoursePage(
						    coDirectorySite));
				}

				public void onSuccess(String result) {
				    coDirectorySite.setDescription(result);
				    getController().setView(
					    new DirectoryCoursePage(
						    coDirectorySite));

				}
			    };
		    getController().getDescription(
			    coDirectorySite.getCourseNumber(), callback);

		}
	    });
	    mainPanel.add(l);
	}
    }

    private void initView3() {
	mainPanel.clear();
	// Label t1 = new Label(getMessage(PortalController.FIELDS_NAME));
	// t1.setStylePrimaryName("NHP_titre1");
	// mainPanel.add(t1);
	label.setStylePrimaryName("NHP_titre2");
	mainPanel.add(label);
	List<String> sessions = new ArrayList<String>();

	// To fill a list of sessions to show courses by session
	if (courses != null) {
	    if (courses.size() >= 0) {
		for (final CODirectorySite tmpCoSite : courses) {
		    if (tmpCoSite.getSessionNamesList() != null
			    && tmpCoSite.getSessionNamesList().size() >= 0) {
			sessions = tmpCoSite.getSessionNamesList();
		    }
		}
	    }
	}
	// List all courses by session
	if ((courses != null && courses.size() >= 0)
		&& (sessions != null && sessions.size() >= 0)) {

	    for (final String session : sessions) {
		Label sessionPeriod = new Label(getNameSession(session));
		sessionPeriod.setStylePrimaryName("CP_titre");
		mainPanel.add(new HTML("&nbsp;"));		
		mainPanel.add(sessionPeriod);
		mainPanel.setCellVerticalAlignment(sessionPeriod, HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(new HTML("&nbsp;"));
		FixedWidthGrid dataTable = new FixedWidthGrid(0, 7);
		dataTable.setSelectionEnabled(false);
		FixedWidthFlexTable headerTable = createSearchHeaderTable();
		coursesTable = new ScrollTable(dataTable, headerTable);
		coursesTable.addStyleName("CP-scrollTable");
		coursesTable.setSortPolicy(ScrollTable.SortPolicy.SINGLE_CELL);
		coursesTable
			.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
		coursesTable
			.setColumnResizePolicy(ColumnResizePolicy.MULTI_CELL);
		coursesTable.getDataTable().clearAll();
		coursesTable.getDataTable().resize(8, 7);
		coursesTable.setColumnWidth(0, 120);
		coursesTable.setColumnWidth(1, 290);
		coursesTable.setColumnWidth(2, 150);
		coursesTable.setColumnWidth(3, 200);
		coursesTable.setColumnWidth(4, 80);
		coursesTable.setColumnWidth(5, 30);
		coursesTable.setColumnWidth(6, 30);
		int i = 0;
		int rowNum;
		for (final CODirectorySite coSite : courses) {

		    for (String currentSection : coSite.getAllSessions()) {

			final String sectionSiteId = currentSection;

			if (sectionSiteId.matches(session)
				|| sectionSiteId.indexOf(session) > 0) {
			    rowNum = coursesTable.getDataTable().insertRow(i);
			    ClickHandler clickHandler =
				    new CourseClickHandler(coSite);

			    coursesTable.getDataTable().getRowFormatter()
				    .setStylePrimaryName(rowNum,
					    "CP-scrollTable-row");
			    
			    Label number = new Label(sectionSiteId);
			    number.setStylePrimaryName("CP_data");
			    number.addClickHandler(clickHandler);
			    coursesTable.getDataTable().setWidget(rowNum, 0,
				    number);

			    Label name = new Label(coSite.getCourseName());
			    name.setStylePrimaryName("CP_data");
			    name.addClickHandler(clickHandler);
			    coursesTable.getDataTable().setWidget(rowNum, 1,
				    name);

			    Label instructor =
				    new Label(
					    coSite.getInstructor() == null ? ""
						    : coSite.getInstructor());
			    instructor.setStylePrimaryName("CP_data");
			    instructor.addClickHandler(clickHandler);
			    coursesTable.getDataTable().setWidget(rowNum, 2,
				    instructor);

			    Label prog =
				    new Label(
					    getMessage(PortalController.ACAD_CAREER_PREFIX
						    + coSite.getProgram()
						    + PortalController.ABBREVIATION_SUFFIX));
			    prog.setStylePrimaryName("CP_data");
			    prog.addClickHandler(clickHandler);
			    coursesTable.getDataTable().setWidget(rowNum, 3,
				    prog);

			    final Image descImage =
				    new Image(getImages().description());
			    descImage
				    .setTitle(getMessage("coursesPage_Description"));
			    descImage.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event)  {
					    AsyncCallback<String> callback =
						    new AsyncCallback<String>() {

							public void onFailure(
								Throwable caught) {
							    showDescriptonView();
							}

							public void onSuccess(
								String result) {
							    coSite
								    .setDescription(result);
							    showDescriptonView();
							}
						    };
					    if (descriptionView != null
						    && descriptionView
							    .isShowing()
						    && descriptionView
							    .getCoDirectorySite()
							    .getCourseNumber()
							    .equals(
								    coSite
									    .getCourseNumber()))
						return;
					    else
						getController()
							.getDescription(
								coSite
									.getCourseNumber(),
								callback);
					}

					private void showDescriptonView() {
					    if (descriptionView != null
						    && descriptionView
							    .isShowing())
						descriptionView.hide();
					    descriptionView =
						    new DescriptionView(coSite);
					    descriptionView
						    .setPopupPosition(
							    descImage
								    .getAbsoluteLeft()
								    + descImage
									    .getWidth(),
							    descImage
								    .getAbsoluteTop()
								    + descImage
									    .getHeight());
					    descriptionView.show();
					    descriptionView.pack();
					    descriptionView.toFront();
					}
				    });
			    coursesTable.getDataTable().setWidget(rowNum, 4,
				    descImage);

			    Image htmlImage = new Image(getImages().zc2html());
			    htmlImage.setTitle(getMessage("coursesPage_Html"));

			    htmlImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
				    Window.open(
					    "/osyl-editor-sakai-tool/index.jsp?siteId="
						    + sectionSiteId, "_self",
					    "");
				}
			    });

			    coursesTable.getDataTable().setWidget(rowNum, 5,
				    htmlImage);

			    Image pdfImage = new Image(getImages().pdf());
			    pdfImage.setTitle(getMessage("coursesPage_Pdf"));

			    pdfImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
				    AsyncCallback<String> asyncCallback =
					    new AsyncCallback<String>() {
						
						public void onFailure(
							Throwable caught) {
						}

						public void onSuccess(
							String result) {
						    Window
							    .open(
								    "/sdata/c/attachment/"
									    + sectionSiteId
									    + "/OpenSyllabus/"
									    + sectionSiteId
									    + "_"
									    + result
									    + ".pdf",
								    "_blank",
								    "");
						}
					    };
				    getController().getAccessForSiteId(
					    sectionSiteId, asyncCallback);
				}
			    });

			    coursesTable.getDataTable().setWidget(rowNum, 6,
				    pdfImage);
			    i++;
			}
		    }
		}
		mainPanel.add(coursesTable);
	    }
	} 
	if (courses == null){
		Label nothingPeriod = new Label(getMessage("empty_searching"));
		nothingPeriod.setStylePrimaryName("CP_titre");
		mainPanel.add(new HTML("&nbsp;"));		
		mainPanel.add(nothingPeriod);
	}
    }

    public static String getViewKeyPrefix() {
	return VIEW_PREFIX;
    }
}
