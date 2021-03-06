/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.event.client.RowHighlightEvent;
import com.google.gwt.gen2.table.event.client.RowHighlightHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.RowUnhighlightEvent;
import com.google.gwt.gen2.table.event.client.RowUnhighlightHandler;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CourseListAdvancedView extends OsylManagerAbstractView implements
	OsylManagerEventHandler, ClickHandler {

    private static String DIRECTORY_TYPE_SITE = "directory";

    private static String DIRECTORY_SITE = "D";
    private static String FROZEN_SITE = "X";

    private static final int SITE_ID_COL = 0;

    private VerticalPanel mainPanel;

    private List<COSite> cosites;

    private String searchTerm;

    private String selectedAcadSession;

    private boolean withFrozenSites;

    private ScrollTable scSiteList;

    private CheckBox selectAll;

    private Messages messages = getController().getMessages();

    private boolean showMessage = true;

    private String styleRow;

    private AsyncCallback<List<COSite>> asyncCallback =
	    new AsyncCallback<List<COSite>>() {

		public void onFailure(Throwable caught) {
			Window.alert(messages.searchError());
		}

		public void onSuccess(List<COSite> result) {
		    cosites = result;
		    mainPanel.clear();
		    scSiteList.getDataTable().clearAll();
		    scSiteList.getDataTable().resize(0, 8);

		    if ((result == null || result.isEmpty()) && isShowMessage()) {
			Window.alert(messages.noCOSite());
		    } else {
			TreeMap<String, COSite> sortedMap =
				new TreeMap<String, COSite>(
					LocalizedStringComparator.getInstance());
			for (Iterator<COSite> coSiteIter = result.iterator(); coSiteIter
				.hasNext();) {
			    COSite cos = coSiteIter.next();
			    String siteTitle = cos.getSiteName();
			    sortedMap.put(siteTitle, cos);
			}
			int i = 0;
			int rowNum = i;
			DateTimeFormat dtf =
				DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss");

			for (Entry<String, COSite> entry : sortedMap.entrySet()) {
			    COSite coSite = entry.getValue();
			    rowNum = scSiteList.getDataTable().insertRow(i);
			    String scrollTableCss = getScrollTableCss(coSite);

			    scSiteList.getDataTable().addStyleDependentName(
				    scrollTableCss);

			    scSiteList
				    .getDataTable()
				    .getRowFormatter()
				    .setStylePrimaryName(rowNum, scrollTableCss);
			    //Site Name
			    scSiteList.getDataTable().setHTML(rowNum, 0,
				    coSite.getSiteName());
			    //Programme
			    scSiteList.getDataTable().setHTML(rowNum, 1,
				    coSite.getAcademicCareer());
			    //Trimestre
			    if (!coSite.getType().equalsIgnoreCase(
				    DIRECTORY_TYPE_SITE)) {
				scSiteList.getDataTable()
					.setHTML(rowNum, 2, coSite.getCourseSession());
			    } else {
				scSiteList.getDataTable()
					.setHTML(rowNum, 2, "");
			    }
			    //Course name
			    scSiteList.getDataTable().setHTML(rowNum, 3,
				    coSite.getCourseName());
			    //Site parent
			    if (!coSite.getType().equalsIgnoreCase(
				    DIRECTORY_TYPE_SITE)) {
			        scSiteList.getDataTable().setHTML(rowNum, 4,
        				    coSite.getParentSite());
			    } else {
				scSiteList.getDataTable()
					.setHTML(rowNum, 4, "");
			    }
			    //Publication
			    if (coSite.getLastPublicationDate() != null) {
				scSiteList.getDataTable().setHTML(rowNum, 5,
					dtf.format(coSite
						.getLastPublicationDate()));
			    }
			    //Modification
			    if (coSite.getLastModifiedDate() != null) {
				scSiteList.getDataTable().setHTML(rowNum, 6,
						dtf.format(coSite
						.getLastModifiedDate()));
			    }
			    //Status
			    String typeSite;
			    if (coSite.isCoIsFrozen()) {
				typeSite = FROZEN_SITE;
			    } else if (coSite.getType().equalsIgnoreCase(
				    DIRECTORY_TYPE_SITE)) {
				typeSite = DIRECTORY_SITE;
			    } else {
				typeSite = "";
			    }
			    scSiteList.getDataTable().setHTML(rowNum, 7,
				    typeSite);
			    scSiteList.getDataTable().getCellFormatter()
				    .setVisible(rowNum, 7, false);
			    i++;
			}
		    }
		    mainPanel.add(scSiteList);
		}
	    };

    public CourseListAdvancedView(OsylManagerController controller,
	    String searchTerm) {
	super(controller);
	setSearchTerm(searchTerm);
	initView();
	initWidget(mainPanel);

	controller.addEventHandler(this);
    }

    public String getScrollTableCss(COSite coSite) {
	String scrollTableCss = "OsylManager-scrollTable-row";
	if (coSite.isCoIsFrozen())
	    scrollTableCss = "OsylManager-scrollTable-row-frozen";
	if (coSite.getType().equalsIgnoreCase("directory"))
	    scrollTableCss = "OsylManager-scrollTable-row-directory";
	return scrollTableCss;
    }

    public String getScrollTableCss(String text) {
	String scrollTableCss = "OsylManager-scrollTable-row";
	if (text.equalsIgnoreCase(FROZEN_SITE))
	    scrollTableCss = "OsylManager-scrollTable-row-frozen";
	if (text.equalsIgnoreCase(DIRECTORY_SITE))
	    scrollTableCss = "OsylManager-scrollTable-row-directory";
	return scrollTableCss;
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	FixedWidthGrid dataTable = new FixedWidthGrid(0, 8);
	dataTable.setSelectionEnabled(true);
	dataTable.setSelectionPolicy(SelectionPolicy.CHECKBOX);

	FixedWidthFlexTable headerTable = createHeaderTable();
	// FixedWidthFlexTable footerTable = createFooterTable();
	// footerTable.getRowFormatter().addStyleName(0, "footerRow");
	// footerTable.getFlexCellFormatter().setColSpan(0, 0, 8);
	scSiteList = new ScrollTable(dataTable, headerTable);
	// scSiteList.setFooterTable(footerTable);
	scSiteList.addStyleName("OsylManager-scrollTable");
	scSiteList.setSortPolicy(ScrollTable.SortPolicy.SINGLE_CELL);
	// scSiteList.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
	// scSiteList.setColumnResizePolicy(ColumnResizePolicy.MULTI_CELL);
	scSiteList.setColumnWidth(0, 150);
	scSiteList.setColumnWidth(1, 100);
	scSiteList.setColumnWidth(2, 100);
	scSiteList.setColumnWidth(3, 200);
	scSiteList.setColumnWidth(4, 150);
	scSiteList.setColumnWidth(5, 120);
	scSiteList.setColumnWidth(6, 120);
	scSiteList.setColumnWidth(7, 20);
	
	// scSiteList.setMinimumColumnWidth(0, 20);
	// scSiteList.setMaximumColumnWidth(0, 20);

	scSiteList.getDataTable().addRowSelectionHandler(
		new RowSelectionHandler() {

		    public void onRowSelection(RowSelectionEvent event) {
			getController().getSelectSites().clear();

			for (int i = 0; i < scSiteList.getDataTable()
				.getRowCount(); i++) {
			    // There is NOT a row selected
			    if (!scSiteList.getDataTable().isRowSelected(i)) {

				scSiteList
					.getDataTable()
					.getRowFormatter()
					.removeStyleName(i,
						"OsylManager-scrollTable-row-selected");
				scSiteList
					.getDataTable()
					.getRowFormatter()
					.removeStyleName(i,
						"OsylManager-scrollTable-row-hover");

				String scrollTableCss =
					getScrollTableCss(scSiteList
						.getDataTable().getText(i, 7));
				scSiteList.getDataTable().getRowFormatter()
					.setStyleName(i, scrollTableCss);

			    }
			    // There is a row selected
			    if (scSiteList.getDataTable().isRowSelected(i)) {
				String scrollTableCss =
					getScrollTableCss(scSiteList
						.getDataTable().getText(i, 7));
				setStyleRow(scrollTableCss);

				scSiteList.getDataTable().getRowFormatter()
					.addStyleName(i, getStyleRow());
			    }
			}
			// There are selected rows
			for (int rowNum : scSiteList.getDataTable()
				.getSelectedRows()) {
			    if (!getController().isInHostedMode()) {
				getController()
					.addSelectedSite(
						getCOSiteFromList(scSiteList
							.getDataTable()
							.getText(
								rowNum,
								CourseListAdvancedView.SITE_ID_COL)));
			    } else {
				COSite cosite = new COSite();
				cosite.setSiteId("siteId" + rowNum);
				getController().addSelectedSite(cosite);
				Window.alert("Selected site : " + rowNum);
			    }

			    String scrollTableCss =
				    getScrollTableCss(scSiteList.getDataTable()
					    .getText(rowNum, 7));
			    setStyleRow(scrollTableCss);

			    scSiteList
				    .getDataTable()
				    .getRowFormatter()
				    .setStylePrimaryName(rowNum,
					    "OsylManager-scrollTable-row-selected");
			}
			OsylManagerEvent osylEvent =
				new OsylManagerEvent(this,
					OsylManagerEvent.SITES_SELECTION_EVENT);
			getController().notifyManagerEventHandler(osylEvent);
		    }
		});

	scSiteList.getDataTable().addRowHighlightHandler(
		new RowHighlightHandler() {

		    public void onRowHighlight(RowHighlightEvent event) {
			setStyleRow(scSiteList.getDataTable().getRowFormatter()
				.getStyleName(event.getValue().getRowIndex()));

			scSiteList.getDataTable().getRowFormatter()
				.addStyleName(event.getValue().getRowIndex(),
					"OsylManager-scrollTable-row-hover");
		    }
		});

	scSiteList.getDataTable().addRowUnhighlightHandler(
		new RowUnhighlightHandler() {

		    public void onRowUnhighlight(RowUnhighlightEvent event) {
			scSiteList.getDataTable().getRowFormatter()
				.removeStyleName(
					event.getValue().getRowIndex(),
					"OsylManager-scrollTable-row-hover");
		    }

		});
	mainPanel.add(scSiteList);
    }

    public void refresh(boolean showMessage) {
	mainPanel.clear();
	mainPanel.add(new Label(messages.courseListView_loading()));
	Image im = new Image(GWT.getModuleBaseURL() + "images/ajaxLoader.gif");
	mainPanel.add(im);

	if (getController().isInHostedMode()) {
	    getHostedModeData();
	} else {
	    setShowMessage(showMessage);
	    getController().getAllCoAndSiteInfo(getSearchTerm(),
		    getSelectedAcadSession(), isWithFrozenSites(),
		    asyncCallback);
	}
    }

    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_INFO_CHANGE
		|| e.getType() == OsylManagerEvent.SITE_CREATION_EVENT) {
	    refresh(false);
	}
    }

    private COSite getCOSiteFromList(String siteId) {
	for (COSite cosite : cosites) {
	    if (cosite.getSiteId().equals(siteId))
		return cosite;
	}
	return null;
    }

    public String getSearchTerm() {
	return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
	this.searchTerm = searchTerm;
    }

    public String getSelectedAcadSession() {
	return selectedAcadSession;
    }

    public void setSelectedAcadSession(String selectedAcadSession) {
	this.selectedAcadSession = selectedAcadSession;
    }

    public boolean isWithFrozenSites() {
	return withFrozenSites;
    }

    public void setWithFrozenSites(boolean withFrozenSites) {
	this.withFrozenSites = withFrozenSites;
    }

    private FixedWidthFlexTable createHeaderTable() {
	FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
	FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
	headerTable.setTitle(getController().getMessages().explanationMsg());

	selectAll = new CheckBox();
	selectAll.addClickHandler(this);

	headerTable.setWidget(0, 0, selectAll);
	formatter.setHorizontalAlignment(0, 0,
		HasHorizontalAlignment.ALIGN_CENTER);
	formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

	headerTable.setHTML(0, 1, messages.CourseListView_scSiteList_col0());
	formatter.setHorizontalAlignment(0, 1,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 2, messages.CourseListView_scSiteList_col1());
	formatter.setHorizontalAlignment(0, 2,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 3, messages.CourseListView_scSiteList_col2());
	formatter.setHorizontalAlignment(0, 3,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 4, messages.CourseListView_scSiteList_col3());
	formatter.setHorizontalAlignment(0, 4,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 4, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 5, messages.CourseListView_scSiteList_col4());
	formatter.setHorizontalAlignment(0, 5,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 5, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 6, messages.CourseListView_scSiteList_col5());
	formatter.setHorizontalAlignment(0, 6,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 6, HasVerticalAlignment.ALIGN_TOP);

	headerTable.setHTML(0, 7, messages.CourseListView_scSiteList_col6());
	formatter.setHorizontalAlignment(0, 7,
		HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 7, HasVerticalAlignment.ALIGN_TOP);

	
	// headerTable.setHTML(0, 8, "");
	// formatter.setVisible(0, 8, false);

	return headerTable;
    }

    private void getHostedModeData() {
	mainPanel.clear();
	scSiteList.getDataTable().clearAll();
	scSiteList.getDataTable().resize(0, 7);

	for (int i = 0; i < 15; i++) {
	    final int rowNum = scSiteList.getDataTable().insertRow(i);
	    CheckBox selectSite = new CheckBox();
	    selectSite.setName(Integer.toString(i));
	    selectSite.addClickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    CheckBox checkBox = (CheckBox) event.getSource();

		    String scrollTableCss =
			    getScrollTableCss(scSiteList.getDataTable()
				    .getText(rowNum, 7));
		    setStyleRow(scrollTableCss);

		    if (checkBox.getValue()) {
			setStyleRow(scSiteList.getDataTable().getRowFormatter()
				.getStyleName(
					Integer.parseInt(checkBox.getName())));

			scSiteList.getDataTable().selectRow(
				Integer.parseInt(checkBox.getName()), false);
		    } else {
			scSiteList.getDataTable().getRowFormatter()
				.setStyleName(
					Integer.parseInt(checkBox.getName()),
					"OsylManager-scrollTable-row-frozen");
		    }

		    scrollTableCss =
			    getScrollTableCss(scSiteList.getDataTable()
				    .getText(rowNum, 7));
		    scSiteList.getDataTable().setText(rowNum, 7,
			    "" + Integer.parseInt(checkBox.getName()));
		    setStyleRow(scrollTableCss);
		}

	    });
	    scSiteList.getDataTable().setHTML(rowNum, 1, "siteId" + i);
	    scSiteList.getDataTable().setHTML(rowNum, 3, "siteTitle" + i);
	}
	mainPanel.add(scSiteList);
    }

    public void onClick(ClickEvent event) {
	setStyleRow("");
	if (selectAll.getValue()) {
	    scSiteList.getDataTable().selectAllRows();
	} else {
	    scSiteList.getDataTable().deselectAllRows();
	}
    }

    public void setShowMessage(boolean showMessage) {
	this.showMessage = showMessage;
    }

    public boolean isShowMessage() {
	return showMessage;
    }

    public String getStyleRow() {
	return styleRow;
    }

    public void setStyleRow(String styleRow) {
	this.styleRow = styleRow;
    }

}
