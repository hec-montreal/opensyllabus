/**
 * *****************************************************************************
 * $Id: OsylEditorMainView.java 636 2008-05-27 15:41:58Z remi.saias@hec.ca $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.client.ui;

import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.PublishPushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.SavePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.listener.SplitterEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.toolbar.OsylToolbarView;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.OsylHorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * OsylMainView defines the OpenSyllabus main view which is composed by a
 * toolbar, a tree view, a workspace view and a resource configuration view
 * (which is a floating widget).<br/>
 * <br/>
 * 
 * @version $Id: $
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OsylMainView extends OsylViewableComposite implements
	SplitterEventHandler {

    // View variables
    protected FlexTable mainPanel;
    protected OsylTreeView osylTree;
    protected OsylWorkspaceView osylWorkspaceView;
    protected OsylHorizontalSplitPanel osylHorizontalSplitPanel;
    protected OsylToolbarView osylToolbarView;

    protected OsylDecoratorPanel treeDecoratorPanel;
    protected OsylDecoratorPanel workspaceDecoratorPanel;
    protected Label courseTitle;
    public OsylMainView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
    } // constructor

    public void initView() {
	// Controller itself has a reference to the MainView
	getController().setMainView(this);

	// Create and set the main container panel
	setMainPanel(new FlexTable());
	int row = 0;
	getMainPanel().setStylePrimaryName("Osyl-MainPanel");
	if (getController().isReadOnly())
	    getMainPanel().addStyleDependentName("ReadOnly");

	String coTitle = null;
	try {
	    coTitle = getController().getCOSerialized().getTitle();
	} catch (NullPointerException e) {
	    coTitle = null;
	}
	// If we don't have a controller or a COSerialized (in hosted mode for
	// instance), or an empty title, we simply display "Course Outline".
	if (null == coTitle || "".equals(coTitle)) {
	    coTitle = getCoMessage("courseoutline");
	}
    String suffix = "";
    if(!getController().isReadOnly()){
    	suffix = getUiMessage("edition_suffix");
    }else if(getController().isInPreview()){
    	suffix = getUiMessage("preview_suffix");
    }
	courseTitle = new Label(coTitle + " " + suffix );
	courseTitle.setStylePrimaryName("Osyl-MainPanel-Title");
    getMainPanel().setWidget(row, 0, courseTitle);
	getMainPanel().getCellFormatter().setStylePrimaryName(row, 0, "Osyl-MainPanel-Title-TD");

    row++;
	if (!getController().isReadOnly() || getController().isInPreview()){
	    // Create and set the OpenSyllabus ToolBar
	    setOsylToolbarView(new OsylToolbarView(getController()
		    .getViewContextModel(), getController()));
	    getOsylToolbarView().addEventHandler(
		    (SavePushButtonEventHandler) getController());
	    getOsylToolbarView().addEventHandler(
		    (PublishPushButtonEventHandler) getController());
	    getOsylToolbarView().refreshView();
	    getOsylToolbarView().setTitle(getUiMessage("OsylToolbar"));
	    getMainPanel().setWidget(row, 0, getOsylToolbarView());
		getMainPanel().getCellFormatter().setStylePrimaryName(row, 0, "Osyl-MainPanel-ToolBar-TD");
	    row++;
	}

	// Create and set the Main Horizontal Split Panel
	osylHorizontalSplitPanel = new OsylHorizontalSplitPanel(this);
	HorizontalSplitPanel horizontalSplitPanel =
		osylHorizontalSplitPanel.getSplitPanel();

	// horizontalSplitPanel = new HorizontalSplitPanel();
	horizontalSplitPanel
		.setStylePrimaryName("Osyl-MainView-HorizontalSplitPanel");
    getMainPanel().setWidget(row, 0, osylHorizontalSplitPanel);
    row++;

	// Create and set the OpenSyllabus TreeView
	setOsylTreeView(new OsylTreeView(getModel(), getController()));
	horizontalSplitPanel.setSplitPosition(OsylTreeView
		.getInitialSplitPosition());
	treeDecoratorPanel = new OsylDecoratorPanel();
	treeDecoratorPanel.setWidget(getOsylTreeView());
	treeDecoratorPanel.setStylePrimaryName("Osyl-TreeView");
	
	Label treeHeaderLabel = new Label(this.getUiMessage("OsylTreeView.title"));
	treeHeaderLabel.setStylePrimaryName("Osyl-TreeView-Header");
	treeDecoratorPanel.setTitle(treeHeaderLabel);
	horizontalSplitPanel.setLeftWidget(treeDecoratorPanel);

	// Create and set the OpenSyllabus Workspace View
	setWorkspaceView(new OsylWorkspaceView(getController()
		.getViewContextModel(), getController()));
	workspaceDecoratorPanel = new OsylDecoratorPanel();
	workspaceDecoratorPanel.setWidget(getWorkspaceView());
	workspaceDecoratorPanel.setStylePrimaryName("Osyl-WorkspaceView");
	horizontalSplitPanel.setRightWidget(workspaceDecoratorPanel);

	subscribeChildrenViewsToLocalHandlers();

	initWidget(getMainPanel());
	initViewContext();
	getWorkspaceView().refreshView();
    }

    public void refreshView() {
	getOsylTreeView().refreshView();
	getOsylToolbarView().refreshView();
	getWorkspaceView().refreshView();
    }

    /**
     * Returns the model entry-point, i.e.: the top-level element. This may be
     * used to return to the initial state.
     * 
     * @return {@link COModelInterface} model element
     */
    public COModelInterface findStartingViewContext() {

	COElementAbstract root = (COContent) getModel();

	COElementAbstract absElement = findStartingViewContext(root);

	if (absElement == null && root.getChildrens() != null
		&& root.getChildrens().size() > 0) {
	    absElement = (COElementAbstract) root.getChildrens().get(0);
	}
	return absElement;
    }

    @SuppressWarnings("unchecked")
    private COElementAbstract findStartingViewContext(COElementAbstract coe) {
	COElementAbstract absElement = null;
	List<COElementAbstract> childrenList = coe.getChildrens();
	boolean find = false;
	Iterator<COElementAbstract> iter = childrenList.iterator();
	while (iter.hasNext()) {
	    absElement = (COElementAbstract) iter.next();
	    if (absElement.isCOUnit()) {
		if (absElement.getType().equalsIgnoreCase(COUnitType.NEWS_UNIT)) {
		    find = true;
		    break;
		}
	    } else {
		absElement = findStartingViewContext(absElement);
		if (absElement != null) {
		    find = true;
		    break;
		}
	    }
	}
	if (!find && childrenList != null && childrenList.size() > 0) {
	    absElement = null;
	}
	return absElement;
    }

    /**
     * Initializing the viewContext to the first structureElement of the global
     * model
     */
    private void initViewContext() {
	if (getModel() != null) {
	    getController().setViewContext(findStartingViewContext());
	}
    }

    private void subscribeChildrenViewsToLocalHandlers() {
	getController().getViewContext().addEventHandler(
		(ViewContextSelectionEventHandler) getOsylToolbarView());
	getController().getViewContext().addEventHandler(
		(ViewContextSelectionEventHandler) getWorkspaceView());
	getController().getViewContext().addEventHandler(
		(ViewContextSelectionEventHandler) getOsylTreeView());
    }

    public OsylTreeView getOsylTreeView() {
	return osylTree;
    }

    protected void setOsylTreeView(OsylTreeView osylTree) {
	this.osylTree = osylTree;
    }

    public void setOsylToolbarView(OsylToolbarView osylToolbarView) {
	this.osylToolbarView = osylToolbarView;
    }

    protected OsylToolbarView getOsylToolbarView() {
	return osylToolbarView;
    }

/*    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel newPanel) {
	this.mainPanel = newPanel;
    }
*/
    public FlexTable getMainPanel() {
    	return mainPanel;
    }

    public void setMainPanel(FlexTable newPanel) {
    	this.mainPanel = newPanel;
    }
    
    public OsylWorkspaceView getWorkspaceView() {
	return osylWorkspaceView;
    }

    public void setWorkspaceView(Composite workspaceView) {
	this.osylWorkspaceView = (OsylWorkspaceView) workspaceView;
    }

    public void onMouseMove(MouseMoveEvent event) {
	boolean isResizing =
		osylHorizontalSplitPanel.getSplitPanel().isResizing();
	if (isResizing) {
	    resize();
	}
    }

    public void resize() {
	int treeWidth = osylHorizontalSplitPanel.getSplitterPosition();
	DOM
		.setStyleAttribute(treeDecoratorPanel.getCell(1, 1), "width",
			treeWidth
				- (treeDecoratorPanel.getCell(1, 0)
					.getOffsetWidth() + treeDecoratorPanel
					.getCell(1, 2).getOffsetWidth()) + "px");
	int splitterWidth =
		osylHorizontalSplitPanel.getSplitElement().getOffsetWidth();
	String toolSizeString = DOM.getStyleAttribute(getElement(), "width");
	int toolSize =
		Integer.parseInt(toolSizeString.substring(0, toolSizeString
			.indexOf("px")));
	int workspaceWidth = toolSize - treeWidth - splitterWidth;
	DOM.setStyleAttribute(workspaceDecoratorPanel.getCell(1, 1), "width",
		workspaceWidth
			- (workspaceDecoratorPanel.getCell(1, 0)
				.getOffsetWidth() + workspaceDecoratorPanel
				.getCell(1, 2).getOffsetWidth()) + "px");
    }

}
