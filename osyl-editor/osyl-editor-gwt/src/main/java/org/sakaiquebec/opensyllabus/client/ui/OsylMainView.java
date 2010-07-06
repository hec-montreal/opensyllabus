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

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
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

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.OsylHorizontalSplitPanel;

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
    protected OsylWorkspaceTitleView osylWorkspaceTitleView;
    protected OsylHorizontalSplitPanel osylHorizontalSplitPanel;
    protected OsylToolbarView osylToolbarView;

    protected OsylDecoratorPanel treeDecoratorPanel;
    protected OsylDecoratorPanel workspaceDecoratorPanel;
    protected Label courseTitle;
    private Label treeHeaderLabel;

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
	if (!getController().isReadOnly()) {
	    suffix = getUiMessage("edition_suffix");
	} else if (getController().isInPreview()) {
	    suffix = getUiMessage("preview_suffix");
	}
	courseTitle = new Label(coTitle + " " + suffix);
	courseTitle.setStylePrimaryName("Osyl-MainPanel-Title");
	getMainPanel().setWidget(row, 0, courseTitle);
	getMainPanel().getCellFormatter().setStylePrimaryName(row, 0,
		"Osyl-MainPanel-Title-TD");

	row++;
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
	getMainPanel().getCellFormatter().setStylePrimaryName(row, 0,
		"Osyl-MainPanel-ToolBar-TD");
	row++;

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
	osylHorizontalSplitPanel.setSplitPosition(
			osylHorizontalSplitPanel.getInitialSplitPosition());
	treeDecoratorPanel = new OsylDecoratorPanel();
	treeDecoratorPanel.setWidget(getOsylTreeView());
	treeDecoratorPanel.setStylePrimaryName("Osyl-TreeView");

	treeHeaderLabel = new Label(this.getUiMessage("OsylTreeView.title"));
	treeHeaderLabel.setStylePrimaryName("Osyl-TreeView-Header");
	treeDecoratorPanel.setTitle(treeHeaderLabel);
	horizontalSplitPanel.setLeftWidget(treeDecoratorPanel);

	// Create and set the OpenSyllabus Workspace View
	setWorkspaceView(new OsylWorkspaceView(getController()
		.getViewContextModel(), getController()));
	setWorkspaceTitleView(new OsylWorkspaceTitleView(getController()
		.getViewContextModel(), getController()));
	workspaceDecoratorPanel = new OsylDecoratorPanel();
	workspaceDecoratorPanel.setWidget(getWorkspaceView());
	workspaceDecoratorPanel.setStylePrimaryName("Osyl-WorkspaceView");
	workspaceDecoratorPanel.setTitle(getWorkspaceTitleView());
	horizontalSplitPanel.setRightWidget(workspaceDecoratorPanel);
	setScrollBar(DOM.getChild(DOM.getChild(horizontalSplitPanel
		.getElement(), 0), 0), "hidden");
	setScrollBar(DOM.getChild(DOM.getChild(horizontalSplitPanel
		.getElement(), 0), 2), "hidden");
	subscribeChildrenViewsToLocalHandlers();
	initWidget(getMainPanel());
	initViewContext();
	getWorkspaceView().refreshView();
    }

    public void refreshView() {
	getOsylTreeView().refreshView();
	getOsylToolbarView().refreshView();
	getWorkspaceView().refreshView();
	getWorkspaceTitleView().refreshView();

    }
    
    private void setScrollBar(Element e, String value){
    	DOM.setStyleAttribute(e, "overflow", value);
    }

    /**
     * Returns the model entry-point, i.e.: the top-level element. This may be
     * used to return to the initial state.
     * 
     * @return {@link COModelInterface} model element
     */
    public COModelInterface findStartingViewContext() {

	COElementAbstract root = (COContent) getModel();

	COElementAbstract absElement = null;
	String startingViewType =
		this.getSettings().getSettingsProperty(
			"mainview.startingViewType");
	if (startingViewType != null) {
	    absElement = findStartingViewContext(root, startingViewType);
	}

	if (absElement == null && root.getChildrens() != null
		&& root.getChildrens().size() > 0) {
	    absElement = (COElementAbstract) root.getChildrens().get(0);
	    // if COStructure element Contains a COUnit, return the COUnit
	    // element
	    if (absElement != null && absElement.getChildrens() != null
		    && absElement.getChildrens().get(0) != null) {
		COElementAbstract childElement =
			(COElementAbstract) absElement.getChildrens().get(0);
		if (childElement.isCOUnit()) {
		    absElement = childElement;
		}

	    }

	}
	return absElement;
    }

    @SuppressWarnings("unchecked")
    private COElementAbstract findStartingViewContext(COElementAbstract coe,
	    String startingViewType) {
	COElementAbstract absElement = null;
	List<COElementAbstract> childrenList = coe.getChildrens();
	boolean find = false;
	Iterator<COElementAbstract> iter = childrenList.iterator();
	while (iter.hasNext()) {
	    absElement = (COElementAbstract) iter.next();
	    if (absElement.isCOUnit()) {
		if (absElement.getType().equalsIgnoreCase(startingViewType)) {
		    find = true;
		    break;
		}
	    } else {
		absElement =
			findStartingViewContext(absElement, startingViewType);
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
		(ViewContextSelectionEventHandler) getWorkspaceTitleView());
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

    public OsylToolbarView getOsylToolbarView() {
	return osylToolbarView;
    }

    /*
     * public VerticalPanel getMainPanel() { return mainPanel; } public void
     * setMainPanel(VerticalPanel newPanel) { this.mainPanel = newPanel; }
     */
    private void setSectionToolbarTopPosition() {
	DOM.setStyleAttribute(osylToolbarView.getOsylToolbar()
		.getSectionMenuBar().getElement(), "top", (osylWorkspaceView
		.getAbsoluteTop() - 78)
		+ "px");
    }

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
    	if (osylHorizontalSplitPanel.isResizing()) resize();
    }

    public void resize() {
    
    // Set the splitter position
    	if (!osylHorizontalSplitPanel.isResizable() && osylHorizontalSplitPanel.getSplitPosition() != 0) {
    		osylHorizontalSplitPanel.setSplitPosition(
        			osylHorizontalSplitPanel.getComputedSplitPosition());
    	}
    
    int toolWidth = Window.getClientWidth();
    if (OsylEditorEntryPoint.isInternetExplorer()) {
    	DOM.setStyleAttribute(getElement(), "width", toolWidth + "px");
    }
    // Set The tree width
	int treeWidth = osylHorizontalSplitPanel.getComputedSplitPosition();
	if (osylHorizontalSplitPanel.getSplitPosition() == 0) treeWidth = 0;
	int treeInnerWidth = Math.max(0, treeWidth
			- (treeDecoratorPanel.getCell(1, 0).getOffsetWidth() + treeDecoratorPanel
					.getCell(1, 2).getOffsetWidth()));
		
	DOM.setStyleAttribute(treeDecoratorPanel.getCell(0, 1), "width",
		treeInnerWidth + "px");
	DOM.setStyleAttribute(treeDecoratorPanel.getCell(1, 1), "width",
		treeInnerWidth + "px");
	int treeHeaderLabelLeftPadding =
		OsylEditorEntryPoint.parsePixels(OsylEditorEntryPoint.getStyle(
			treeHeaderLabel.getElement(), "paddingLeft"));
	treeHeaderLabel.setWidth(Math.max(0,treeInnerWidth - treeHeaderLabelLeftPadding)
		+ "px");
	int splitterWidth =
		osylHorizontalSplitPanel.getSplitElement().getOffsetWidth();
	// Set the treeItems width
	int scrollbarWidth = (osylTree.getTree().getOffsetHeight() 
			> treeDecoratorPanel.getCell(1, 1).getOffsetHeight()? 16 : 0);
	osylTree.setTreeItemsWidth(treeInnerWidth + treeDecoratorPanel.getCell(1, 0)
			.getOffsetWidth() - scrollbarWidth);
	
	
	int workspaceWidth = toolWidth - treeWidth - splitterWidth;
	int workspaceInnerWidth =
		workspaceWidth
			- (workspaceDecoratorPanel.getCell(1, 0)
				.getOffsetWidth() + workspaceDecoratorPanel
				.getCell(1, 2).getOffsetWidth());

	int menubarWidth =
		osylToolbarView.getOsylToolbar().getSectionMenuBar()
			.getOffsetWidth();
	int osylWorkspaceLabelLeftPadding =
		OsylEditorEntryPoint.parsePixels(OsylEditorEntryPoint.getStyle(
			osylWorkspaceTitleView.getWorkspaceTitleLabel()
				.getElement(), "paddingLeft"));
	osylWorkspaceTitleView.getWorkspaceTitleLabel().setWidth(
		workspaceInnerWidth
			- (menubarWidth + osylWorkspaceLabelLeftPadding + 3)
			+ "px");
	DOM.setStyleAttribute(workspaceDecoratorPanel.getCell(1, 1), "width",
		workspaceInnerWidth + "px");
	setSectionToolbarTopPosition();
	// Set The tree height and the workspace height
	int toolHeight =
		OsylEditorEntryPoint.parsePixels(DOM.getStyleAttribute(
			getElement(), "height"));

	// TODO Find a way to compute offsetHeight of the top and bottom
	// decorativePanel (doesn't work use 56 instead).
	int innerHeight =
		toolHeight - osylHorizontalSplitPanel.getAbsoluteTop() - 56;
	DOM.setStyleAttribute(treeDecoratorPanel.getCell(1, 1), "height",
		innerHeight + "px");
	DOM.setStyleAttribute(workspaceDecoratorPanel.getCell(1, 1), "height",
		innerHeight + "px");
    }
    
    public OsylWorkspaceTitleView getWorkspaceTitleView() {
    	return osylWorkspaceTitleView;
    }
	public void setWorkspaceTitleView(OsylWorkspaceTitleView osylWorkspaceTitleView) {
		this.osylWorkspaceTitleView = osylWorkspaceTitleView;
	}
	
	public static native void writeInfos(String value)/*-{
	var root = $wnd.top.document;
	var elm = root.getElementById("INFOS");
	if (elm == null) {
		var infos = root.createElement("DIV");
		infos.id = "INFOS";
		root.body.appendChild(infos);
		elm = root.getElementById("INFOS");
		elm.style.position = "fixed";
		elm.style.top = "0";
		elm.style.left = "0";
		elm.style.border = "2px solid #000";
		elm.style.padding = "4px";
		elm.style.zIndex = "100000";
		elm.style.backgroundColor = "#FFF";
	}
	elm.innerHTML = value;
}-*/;
}
