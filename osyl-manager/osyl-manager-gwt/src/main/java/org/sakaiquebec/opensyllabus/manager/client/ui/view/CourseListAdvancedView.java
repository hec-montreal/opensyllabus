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
import java.util.TreeMap;

import org.gwt.mosaic.override.client.FlexTable.FlexCellFormatter;
import org.gwt.mosaic.ui.client.event.RowHighlightEvent;
import org.gwt.mosaic.ui.client.event.RowHighlightHandler;
import org.gwt.mosaic.ui.client.event.RowSelectionEvent;
import org.gwt.mosaic.ui.client.event.RowSelectionHandler;
import org.gwt.mosaic.ui.client.event.RowUnhighlightEvent;
import org.gwt.mosaic.ui.client.event.RowUnhighlightHandler;
import org.gwt.mosaic.ui.client.event.TableEvent.Row;
import org.gwt.mosaic.ui.client.table.FixedWidthFlexTable;
import org.gwt.mosaic.ui.client.table.FixedWidthGrid;
import org.gwt.mosaic.ui.client.table.ScrollTable;
import org.gwt.mosaic.ui.client.table.SelectionGrid.SelectionPolicy;
import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.LocalizedStringComparator;

import com.google.gwt.core.client.GWT;
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
public class CourseListAdvancedView extends OsylManagerAbstractView
implements OsylManagerEventHandler {
    
    private static final int SITE_ID_COL = 0;

    private VerticalPanel mainPanel;

    private List<COSite> cosites;
    
    private String searchTerm;
    
    private ScrollTable scSiteList;
    
    private Messages messages = getController().getMessages();

    private AsyncCallback<List<COSite>> asyncCallback =
	    new AsyncCallback<List<COSite>>() {

		public void onFailure(Throwable caught) {
		    Window.alert(messages.rpcFailure());
		}

		public void onSuccess(List<COSite> result) {
		    cosites = result;
		    mainPanel.clear();
		    scSiteList.getDataTable().clearAll();
		    scSiteList.getDataTable().resize(0, 8);
		    
		    if (result == null || result.isEmpty()) {
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
			int i =0;
			int rowNum = i;
			DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");
			for (Iterator<String> sortedSiteIterator =
				sortedMap.keySet().iterator(); sortedSiteIterator
				.hasNext();) {
			    String siteTitle = sortedSiteIterator.next();
			    COSite coSite = sortedMap.get(siteTitle);
			    rowNum = scSiteList.getDataTable().insertRow(i);
			    scSiteList.getDataTable().getRowFormatter().setStylePrimaryName(rowNum, "OsylManager-scrollTable-row");
			    scSiteList.getDataTable().setHTML(rowNum, 0, coSite.getSiteId());
			    scSiteList.getDataTable().setHTML(rowNum, 1, coSite.getCourseNumber());
			    scSiteList.getDataTable().setHTML(rowNum, 2, coSite.getCourseSession());
			    scSiteList.getDataTable().setHTML(rowNum, 3, siteTitle);
			    scSiteList.getDataTable().setHTML(rowNum, 4, coSite.getParentSite());
			    if(coSite.getLastPublicationDate()!=null)
				scSiteList.getDataTable().setHTML(rowNum, 5, dtf.format(coSite.getLastPublicationDate()));
			    if(coSite.getLastModifiedDate()!=null)
				scSiteList.getDataTable().setHTML(rowNum, 6, dtf.format(coSite.getLastModifiedDate()));
			    scSiteList.getDataTable().setWidget(rowNum, 7, new CheckBox());
			    i++;
			}
		    }
		    mainPanel.add(scSiteList);
		}
	    };

    public CourseListAdvancedView(OsylManagerController controller, String searchTerm) {
	super(controller);
	setSearchTerm(searchTerm);
	initView();
	initWidget(mainPanel);
	
	controller.addEventHandler(this);
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	FixedWidthGrid dataTable = new FixedWidthGrid(0, 8);
	dataTable.setSelectionEnabled(true);
	dataTable.setSelectionPolicy(SelectionPolicy.ONE_ROW);
	
	FixedWidthFlexTable headerTable = createHeaderTable();
	scSiteList = new ScrollTable(dataTable, headerTable);
	
	scSiteList.setSize("800px", "200px");
	scSiteList.setCellPadding(3);
	scSiteList.setCellSpacing(0);
	scSiteList.setSortPolicy(ScrollTable.SortPolicy.MULTI_CELL);
	scSiteList.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
	
	scSiteList.setMinimumColumnWidth(0, 50);
	scSiteList.setPreferredColumnWidth(0, 100);
	scSiteList.setColumnTruncatable(0, false);
	
	scSiteList.setMinimumColumnWidth(1, 50);
	scSiteList.setPreferredColumnWidth(1, 100);
	scSiteList.setColumnTruncatable(1, false);
	
	scSiteList.setMinimumColumnWidth(2, 50);
	scSiteList.setPreferredColumnWidth(2, 100);
	scSiteList.setColumnTruncatable(2, false);
	
	scSiteList.setMinimumColumnWidth(3, 50);
	scSiteList.setPreferredColumnWidth(3, 100);
	scSiteList.setColumnTruncatable(3, false);
	
	scSiteList.setMinimumColumnWidth(4, 50);
	scSiteList.setPreferredColumnWidth(4, 100);
	scSiteList.setColumnTruncatable(4, false);
	
	scSiteList.setMinimumColumnWidth(5, 50);
	scSiteList.setPreferredColumnWidth(5, 100);
	scSiteList.setColumnTruncatable(5, false);
	
	scSiteList.setMinimumColumnWidth(6, 50);
	scSiteList.setPreferredColumnWidth(6, 100);
	scSiteList.setColumnTruncatable(6, false);
	
	scSiteList.setMinimumColumnWidth(7, 50);
	scSiteList.setPreferredColumnWidth(7, 100);
	scSiteList.setColumnTruncatable(7, false);

	scSiteList.getDataTable().addRowSelectionHandler(new RowSelectionHandler() {

	    public void onRowSelection(RowSelectionEvent event) {
		getController().getSelectSites().clear();
		
		for(int i=0; i<scSiteList.getDataTable().getRowCount();i++){
		    scSiteList.getDataTable().deselectRow(i);
		    scSiteList.getDataTable().getRowFormatter()
			.removeStyleName(i, "OsylManager-scrollTable-row-selected");
		    scSiteList.getDataTable().getRowFormatter()
			.removeStyleName(i, "OsylManager-scrollTable-row-hover");
		}
		
		for (Row row : event.getSelectedRows()) {
		    getController().addSelectedSite(getCOSiteFromList(
				scSiteList.getDataTable()
				.getText(row.getRowIndex(), CourseListAdvancedView.SITE_ID_COL)));
		    scSiteList.getDataTable().getRowFormatter()
			.setStylePrimaryName(row.getRowIndex(), "OsylManager-scrollTable-row-selected");
		}
		OsylManagerEvent osylEvent = new OsylManagerEvent(null, OsylManagerEvent.SITES_SELECTION_EVENT);
		getController().notifyManagerEventHandler(osylEvent);
	    }
	});
	
	scSiteList.getDataTable().addRowHighlightHandler(new RowHighlightHandler() {

	    public void onRowHighlight(RowHighlightEvent event) {
		scSiteList.getDataTable().getRowFormatter().addStyleName(
			event.getHighlightedRow().getRowIndex(), "OsylManager-scrollTable-row-hover");
	    }
	});
	
	scSiteList.getDataTable().addRowUnhighlightHandler(new RowUnhighlightHandler() {

	    public void onRowUnhighlight(RowUnhighlightEvent event) {
		scSiteList.getDataTable().getRowFormatter().removeStyleName(
			event.getUnhighlightedRow().getRowIndex(), "OsylManager-scrollTable-row-hover");
	    }
	});
	mainPanel.add(scSiteList);
    }

    public void refresh() {
	mainPanel.clear();
	mainPanel.add(new Label(messages.courseListView_loading()));
	Image im = new Image(GWT.getModuleBaseURL() + "images/ajaxLoader.gif");
	mainPanel.add(im);
	
	if(getController().isInHostedMode()){
	    getHostedModeData();
	} else {
	    getController().getAllCoAndSiteInfo(getSearchTerm(), asyncCallback);
	}
    }

    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITE_INFO_CHANGE)
	    refresh();
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
    
    public FixedWidthFlexTable createHeaderTable(){
	FixedWidthFlexTable headerTable = new FixedWidthFlexTable();
	FlexCellFormatter formatter = headerTable.getFlexCellFormatter();
	
	headerTable.setHTML(0, 0, messages.CourseListView_scSiteList_col0());
	formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 1, messages.CourseListView_scSiteList_col1());
	formatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 2, messages.CourseListView_scSiteList_col2());
	formatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 3, messages.CourseListView_scSiteList_col3());
	formatter.setHorizontalAlignment(0, 3, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 4, messages.CourseListView_scSiteList_col4());
	formatter.setHorizontalAlignment(0, 4, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 4, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 5, messages.CourseListView_scSiteList_col5());
	formatter.setHorizontalAlignment(0, 5, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 5, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 6, messages.CourseListView_scSiteList_col6());
	formatter.setHorizontalAlignment(0, 6, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 6, HasVerticalAlignment.ALIGN_TOP);
	
	headerTable.setHTML(0, 7, messages.CourseListView_scSiteList_col7());
	formatter.setHorizontalAlignment(0, 7, HasHorizontalAlignment.ALIGN_LEFT);
	formatter.setVerticalAlignment(0, 7, HasVerticalAlignment.ALIGN_TOP);
	
	return headerTable;
    }
    
    private void getHostedModeData(){
	mainPanel.clear();
	scSiteList.getDataTable().clearAll();
	scSiteList.getDataTable().resize(0, 8);

	for(int i=0;i<15;i++){
	    int rowNum = scSiteList.getDataTable().insertRow(i);
	    scSiteList.getDataTable().setHTML(rowNum, 0, "siteId"+i);
	    scSiteList.getDataTable().setHTML(rowNum, 3, "siteTitle"+i);
	    scSiteList.getDataTable().setWidget(rowNum, 7, new CheckBox());
	}
	mainPanel.add(scSiteList);
    }
}
