/*******************************************************************************
 * *****************************************************************************
 * $Id: OsylTreeView.java 636 2008-05-27 15:41:58Z remi.saias@hec.ca $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylStructTreeItemView;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylTreeItemBaseView;
import org.sakaiquebec.opensyllabus.client.ui.base.OsylUnitTreeItemView;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

/**
 * OsylTreeView displays the tree structure of current course outline in
 * OpenSyllabus editor.<br/><br/> The general layout is as follows (TODO: this is outdated):
 * 
 * 
 * OsylTreeView implements {@link ViewContextSelectionEventHandler} to be
 * notified that it has to refresh its view, for instance when the user clicked
 * on a lecture to display it.
 * 
 * @version $Id: $
 */
public class OsylTreeView extends OsylViewableComposite implements
	TreeListener, ViewContextSelectionEventHandler,
	UpdateCOStructureElementEventHandler {

    private TreeItem root;

    private String rootTitle;

    private Tree osylTree;

    private Map<COModelInterface, TreeItem> itemModelMap;

    public static final String DEFAULT_WIDTH = "270px";

    public OsylTreeView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
	itemModelMap = new HashMap<COModelInterface, TreeItem>();
	initView();
    }

    private void initView() {
	final Tree tree = new Tree();
	setTree(tree);
	getTree().setStylePrimaryName("Osyl-TreeView-Tree");
	// the listener is "this" itself
	getTree().addTreeListener(this);
	initWidget(osylTree);
	refreshView();
    }

    private void refreshSubModelsViews(COElementAbstract currentModel) {
	// clean panel
	TreeItem currentTreeItem = (TreeItem) itemModelMap.get(currentModel);
	currentTreeItem.removeItems();

	// displaying all branches
	List<COElementAbstract> children = null;
	if (currentModel.isCourseOutlineContent()) {
	    children = ((COContent) currentModel).getChildren();
	} else if (currentModel.isCOStructureElement()) {
	    children = ((COStructureElement) currentModel).getChildren();
	} else {
	    return;
	}

	Iterator<COElementAbstract> iter = children.iterator();
	while (iter.hasNext()) {
	    // this can be a Lecture leaf
	    COElementAbstract itemModel = (COElementAbstract) iter.next();
	    if (itemModel.isCOStructureElement()) {
		addStructTreeItem(currentTreeItem,
			(COStructureElement) itemModel);
		((COStructureElement) itemModel).addEventHandler(this);
		refreshSubModelsViews(itemModel);
	    } else if (itemModel.isCOContentUnit()) {
		addUnitTreeItem(currentTreeItem, (COContentUnit) itemModel);
	    } else {
		break;
	    }
	}
    }

    public void refreshView() {
	COContent co = (COContent) getModel();
	getTree().removeItems();
	String treeItemTitle = null;
	
	try {
		treeItemTitle = getController().getCOSerialized().getTitle();
	} catch (NullPointerException e) {
		treeItemTitle = null;
	}
	// If we don't have a controller or a COSerialized (in hosted mode for
	// instance), or an empty title, we simply display "Course Outline".
	if(null == treeItemTitle
		|| "".equals(treeItemTitle)) {
	    treeItemTitle = getCoMessage("courseoutline");
	}
	setRoot(new TreeItem(treeItemTitle));
	itemModelMap.put(getModel(), getRoot());
	getTree().addItem(getRoot());

	refreshSubModelsViews(co);

	// The tree is expanded by default
	getRoot().setState(true);
    }

    private void addUnitTreeItem(TreeItem parentTreeItem,
	    COContentUnit itemModel) {
	OsylUnitTreeItemView treeItemView =
		new OsylUnitTreeItemView(itemModel, getController());
	addTreeItemView(parentTreeItem, treeItemView);
    }

    private void addStructTreeItem(TreeItem parentTreeItem,
	    COStructureElement itemModel) {
	OsylStructTreeItemView treeItemView =
		new OsylStructTreeItemView(itemModel, getController());
	addTreeItemView(parentTreeItem, treeItemView);
    }

    private void addTreeItemView(TreeItem parentTreeItem,
	    OsylTreeItemBaseView treeItemView) {
	TreeItem treeItem = new TreeItem(treeItemView);
	itemModelMap.put(treeItemView.getModel(), treeItem);
	parentTreeItem.addItem(treeItem);
	parentTreeItem.setState(true);
    }

    /**
     * @return the TreeItem root
     */
    private TreeItem getRoot() {
	return root;
    }

    /**
     * set the TreeItem root
     * 
     * @param root
     */
    private void setRoot(TreeItem root) {
	this.root = root;
    }

    public String getRootTitle() {
	return rootTitle;
    }

    public void setRootTitle(String rootTitle) {
	this.rootTitle = rootTitle;
    }

    public Tree getTree() {
	return osylTree;
    }

    private void setTree(Tree osylTree) {
	this.osylTree = osylTree;
    }

    /**
     * @see TreeListener#onTreeItemSelected(TreeItem)
     */
    public void onTreeItemSelected(TreeItem item) {
	if (item.getParentItem() == null) {
	    getController().getViewContext().setContextModel(getModel());
	} else {
	    OsylTreeItemBaseView treeItemView =
		    (OsylTreeItemBaseView) item.getWidget();
	    getController().getViewContext().setContextModel(
		    treeItemView.getModel());
	}

    }

    /**
     * @see TreeListener#onTreeItemStateChanged(TreeItem)
     */
    public void onTreeItemStateChanged(TreeItem item) {
	// TODO Auto-generated method stub
	// Window.alert("onTreeItemStateChanged");
    }

    /**
     * @see ViewContextSelectionEventHandler#onViewContextSelection(org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler.ViewContextSelectionEvent)
     */
    public void onViewContextSelection(ViewContextSelectionEvent event) {
	COElementAbstract viewContextModel =
		(COElementAbstract) event.getSource();
	// show the treeview according to the model selected
	// retrieving the corresponding treeitem of the model
	TreeItem treeItem = (TreeItem) itemModelMap.get(viewContextModel);
	// select the corresponding item but dont fire the event (false
	// argument)
	getTree().setSelectedItem(treeItem, false);
    }

    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	refreshSubModelsViews((COElementAbstract) event.getSource());
    }

}
