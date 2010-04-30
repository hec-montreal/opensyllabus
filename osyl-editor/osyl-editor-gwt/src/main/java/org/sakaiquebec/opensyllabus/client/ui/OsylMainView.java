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
import org.sakaiquebec.opensyllabus.client.ui.toolbar.OsylToolbarView;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * OsylMainView defines the OpenSyllabus main view which is composed by a
 * toolbar, a tree view, a workspace view and a resource configuration view
 * (which is a floating widget).<br/>
 * <br/>
 * 
 * @version $Id: $
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OsylMainView extends OsylViewableComposite {

    // View variables
    private VerticalPanel mainPanel;
    private OsylTreeView osylTree;
    private OsylWorkspaceView osylWorkspaceView;
    private final HorizontalSplitPanel horizontalSplitPanel;

    private OsylToolbarView osylToolbarView;

    public OsylMainView(COModelInterface model, OsylController osylController) {
	super(model, osylController);

	// Controller itself has a reference to the MainView
	getController().setMainView(this);

	// Create and set the main container panel
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-MainPanel");
	if(getController().isReadOnly())
	    getMainPanel().addStyleDependentName("ReadOnly");
	getMainPanel().setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

	if (!getController().isReadOnly()) {
	    // Create and set the OpenSyllabus ToolBar
	    setOsylToolbarView(new OsylToolbarView(getController()
		    .getViewContextModel(), getController()));
	    getOsylToolbarView().addEventHandler(
		    (SavePushButtonEventHandler) getController());
	    getOsylToolbarView().addEventHandler(
		    (PublishPushButtonEventHandler) getController());
	    getOsylToolbarView().refreshView();
	    getOsylToolbarView().setTitle(getUiMessage("OsylToolbar"));
	    getMainPanel().add(getOsylToolbarView());
	    getMainPanel().setCellHeight(getOsylToolbarView(), "21px");
	}

	// Create and set the Main Horizontal Split Panel
	horizontalSplitPanel = new HorizontalSplitPanel();
	horizontalSplitPanel
		.setStylePrimaryName("Osyl-MainView-HorizontalSplitPanel");
	getMainPanel().add(horizontalSplitPanel);

	// Create and set the OpenSyllabus TreeView
	setOsylTreeView(new OsylTreeView(getModel(), getController()));
	// TODO: compute dynamically the maximum height of the TreeView
	horizontalSplitPanel.setSplitPosition(OsylTreeView
		.getInitialSplitPosition());
	// SimplePanel treeViewEnclosingPanel = new SimplePanel();
	OsylRoundCornersPanel treeViewRoundCornerPanel =
		new OsylRoundCornersPanel(
			getOsylTreeView(),
			// "Osyl-TreeView",
			"", "Osyl-TreeView-BottomLeft",
			"Osyl-TreeView-BottomRight", "Osyl-TreeView-TopLeft",
			"Osyl-TreeView-TopRight");
	// treeViewEnclosingPanel.add(treeViewRoundCornerPanel);
	// treeEnclosingPanel.setStylePrimaryName("Osyl-TreeView");
	treeViewRoundCornerPanel.setStylePrimaryName("Osyl-TreeView");
	horizontalSplitPanel.setLeftWidget(treeViewRoundCornerPanel);

	// Create and set the OpenSyllabus Workspace View
	setWorkspaceView(new OsylWorkspaceView(getController()
		.getViewContextModel(), getController()));
	// SimplePanel workspaceEnclosingPanel = new SimplePanel();
	OsylRoundCornersPanel workSpaceViewRoundCornerPanel =
		new OsylRoundCornersPanel(
			getWorkspaceView(),
			// "Osyl-WorkspaceView",
			"", "Osyl-WorkspaceView-BottomLeft",
			"Osyl-WorkspaceView-BottomRight",
			"Osyl-WorkspaceView-TopLeft",
			"Osyl-WorkspaceView-TopRight");
	// workspaceEnclosingPanel.add(workSpaceViewRoundCornerPanel);
	// workspaceEnclosingPanel.setStylePrimaryName("Osyl-WorkspaceView");
	workSpaceViewRoundCornerPanel.setStylePrimaryName("Osyl-WorkspaceView");
	horizontalSplitPanel.setRightWidget(workSpaceViewRoundCornerPanel);

	subscribeChildrenViewsToLocalHandlers();

	initWidget(getMainPanel());
	initViewContext();
	getWorkspaceView().refreshView();
    } // constructor

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

    private void setOsylTreeView(OsylTreeView osylTree) {
	this.osylTree = osylTree;
    }

    public void setOsylToolbarView(OsylToolbarView osylToolbarView) {
	this.osylToolbarView = osylToolbarView;
    }

    private OsylToolbarView getOsylToolbarView() {
	return osylToolbarView;
    }

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel newPanel) {
	this.mainPanel = newPanel;
    }

    public OsylWorkspaceView getWorkspaceView() {
	return osylWorkspaceView;
    }

    public void setWorkspaceView(Composite workspaceView) {
	this.osylWorkspaceView = (OsylWorkspaceView) workspaceView;
    }

    public void setHorizontalSplitPanelPosition(String newPosition) {
	horizontalSplitPanel.setSplitPosition(newPosition);
    }

}
