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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CoursesTable extends AbstractPortalView {

    private static String key = "CoursesList";

    private List<CODirectorySite> courses;

    private ScrollTable coursesTable;

    private VerticalPanel mainPanel;

    private DescriptionView descriptionView;

    public CoursesTable(List<CODirectorySite> list) {
	super(key);
	this.courses = list;
	mainPanel = new VerticalPanel();
	initView();
	initWidget(mainPanel);
    }

    private void initView() {
	mainPanel.clear();
	FixedWidthGrid dataTable = new FixedWidthGrid(0, 7);
	dataTable.setSelectionEnabled(false);
	FixedWidthFlexTable headerTable = createHeaderTable();
	coursesTable = new ScrollTable(dataTable, headerTable);
	coursesTable.addStyleName("CP-scrollTable");
	coursesTable.setSortPolicy(ScrollTable.SortPolicy.SINGLE_CELL);
	coursesTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
	coursesTable.setColumnResizePolicy(ColumnResizePolicy.MULTI_CELL);

	coursesTable.getDataTable().clearAll();
	coursesTable.getDataTable().resize(0, 7);
	int i = 0;
	int rowNum;
	for (final CODirectorySite coSite : courses) {

	    for (Entry<String, String> entry : coSite.getCurrentSections()
		    .entrySet()) {
		rowNum = coursesTable.getDataTable().insertRow(i);
		final String sectionSiteId = entry.getKey();
		coursesTable.getDataTable().getRowFormatter()
			.setStylePrimaryName(rowNum, "CP-scrollTable-row");
		coursesTable.getDataTable().setHTML(rowNum, 0, sectionSiteId);
		coursesTable.getDataTable().setHTML(rowNum, 1,
			coSite.getCourseName());
		coursesTable.getDataTable()
			.setHTML(rowNum, 2, entry.getValue());
		coursesTable.getDataTable().setHTML(
			rowNum,
			3,
			getMessage(PortalController.ACAD_CAREER_PREFIX
				+ coSite.getProgram()
				+ PortalController.ABBREVIATION_SUFFIX));

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
				&& descriptionView.getViewKey().equals(
					coSite.getCourseNumber()))
			    return;
			else
			    getController().getDescription(
				    coSite.getCourseNumber(), callback);

		    }

		    private void showDescriptonView() {
			if (descriptionView != null
				&& descriptionView.isShowing())
			    descriptionView.hide();
			descriptionView =
				new DescriptionView(coSite, sectionSiteId);
			descriptionView.setPopupPosition(
				descImage.getAbsoluteLeft()
					+ descImage.getWidth(),
				descImage.getAbsoluteTop()
					+ descImage.getHeight());
			descriptionView.show();
			descriptionView.pack();
			descriptionView.toFront();
		    }

		});
		coursesTable.getDataTable().setWidget(rowNum, 4, descImage);

		Image htmlImage = new Image(getImages().zc2html());
		htmlImage.setTitle(getMessage("coursesPage_Html"));

		htmlImage.addClickHandler(new ClickHandler() {

		    public void onClick(ClickEvent event) {
			Window.open("/osyl-editor-sakai-tool/index.jsp?siteId="
				+ sectionSiteId, "_self", "");
		    }
		});
		coursesTable.getDataTable().setWidget(rowNum, 5, htmlImage);

		Image pdfImage = new Image(getImages().pdf());
		pdfImage.setTitle(getMessage("coursesPage_Pdf"));
		pdfImage.addClickHandler(new ClickHandler() {

		    public void onClick(ClickEvent event) {
			AsyncCallback<String> asyncCallback =
				new AsyncCallback<String>() {

				    @Override
				    public void onFailure(Throwable caught) {
				    }

				    @Override
				    public void onSuccess(String result) {
					Window.open("/sdata/c/attachment/"
						+ sectionSiteId
						+ "/OpenSyllabus/"
						+ sectionSiteId + "_" + result
						+ ".pdf", "_blank", "");
				    }
				};
			getController().getAccessForSiteId(sectionSiteId,
				asyncCallback);
		    }
		});
		coursesTable.getDataTable().setWidget(rowNum, 6, pdfImage);

		i++;
	    }
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

}
