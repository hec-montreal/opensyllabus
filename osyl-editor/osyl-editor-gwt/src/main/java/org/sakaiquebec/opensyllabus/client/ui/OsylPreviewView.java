/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui;

import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COModeled;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;
import org.sakaiquebec.opensyllabus.shared.model.OsylTestXsl;
import org.sakaiquebec.opensyllabus.shared.util.BrowserUtil;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylPreviewView extends OsylViewableComposite implements
	ClosePushButtonEventHandler {

    private String access;

    private OsylController controller;

    private VerticalPanel mainPanel;

    private OsylViewable previousMainView;

    private OsylEditorEntryPoint entryPoint =
	    OsylEditorEntryPoint.getInstance();

    // View variables
    private OsylTreeView osylTree;
    private OsylWorkspaceView osylWorkspaceView;
    private OsylToolbarView osylToolbarView;
    
    private COSerialized coSerializedForGroup;

    public OsylToolbarView getOsylToolbarView() {
	return osylToolbarView;
    }

    public void setOsylToolbarView(OsylToolbarView osylToolbarView) {
	this.osylToolbarView = osylToolbarView;
    }

    public OsylPreviewView(String access, OsylController controller) {

	super(null, controller);
	this.access = access;
	this.controller = controller;
	entryPoint.prepareModelForSave();
	coSerializedForGroup =
		new COSerialized(entryPoint.getPreviewSerializeCourseOutline());
	
	

	if (getController().isInHostedMode()) {
	    String xsl = "";
	    if (access.equals(SecurityInterface.ACCESS_PUBLIC)) {
		xsl = OsylTestXsl.XSL_PUBLIC;
	    } else if (access.equals(SecurityInterface.ACCESS_ONSITE)) {
		xsl = OsylTestXsl.XSL_ONSITE;
	    } else {
		xsl = OsylTestXsl.XSL_ATTENDEE;
	    }
	    initModelWithXsl(xsl);
	} else {
	    //For ie (version 7 and 8 at least), xsl transformation client-side is too long, we make it server-side
	    if (BrowserUtil.getBrowserType().equals("ie6")) {
		AsyncCallback<String> asyncallback =
			new AsyncCallback<String>() {

			    public void onSuccess(String xml) {
				initViewWithXmlModel(xml);
			    }

			    public void onFailure(Throwable error) {
				System.out
					.println("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
				Window
					.alert("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
			    }

			};
			getController().transformXmlForGroup(coSerializedForGroup.getContent(), access, asyncallback);
	    } else {
		AsyncCallback<String> asyncallback =
			new AsyncCallback<String>() {

			    public void onSuccess(String xsl) {
				initModelWithXsl(xsl);
			    }

			    public void onFailure(Throwable error) {
				System.out
					.println("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
				Window
					.alert("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
			    }

			};
		getController().getXslForGroup(access, asyncallback);
	    }
	}
    }
    
    private void initModelWithXsl(String xsl){
	initViewWithXmlModel(getController().xslTransform(
		coSerializedForGroup.getContent(), xsl));
    }
    
    private void initViewWithXmlModel(String xml){
	coSerializedForGroup.setContent(xml);
	COModeled model = new COModeled(coSerializedForGroup);
	model.XML2Model();
	setModel(model.getModeledContent());
	initView();
    }

    private void initView() {
	previousMainView = entryPoint.getView();
	getController().setInPreview(true);
	getController().setReadOnly(true);

	// Create and set the main container panel
	setMainPanel(new VerticalPanel());
	getMainPanel().setStylePrimaryName("Osyl-MainPanel");
	getMainPanel().addStyleDependentName("ReadOnly");
	getMainPanel().setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

	setOsylToolbarView(new OsylToolbarView(getModel(), getController()));
	getOsylToolbarView().addEventHandler(this);
	getOsylToolbarView().refreshView();
	getOsylToolbarView().setTitle(getUiMessage("OsylToolbar"));
	getMainPanel().add(getOsylToolbarView());
	getMainPanel().setCellHeight(getOsylToolbarView(), "21px");

	// Create and set the Main Horizontal Split Panel
	final HorizontalSplitPanel horizontalSplitPanel =
		new HorizontalSplitPanel();
	horizontalSplitPanel
		.setStylePrimaryName("Osyl-MainView-HorizontalSplitPanel");
	horizontalSplitPanel.setSplitPosition(OsylTreeView
		.getInitialSplitPosition());

	// Create and set the OpenSyllabus TreeView
	setOsylTreeView(new OsylTreeView(getModel(), getController()));
	horizontalSplitPanel.setLeftWidget(getOsylTreeView());

	// Create and set the OpenSyllabus Workspace View
	setWorkspaceView(new OsylWorkspaceView(getModel(), getController()));
	horizontalSplitPanel.setRightWidget(getWorkspaceView());

	getMainPanel().add(horizontalSplitPanel);
	subscribeChildrenViewsToLocalHandlers();

	initWidget(getMainPanel());
	entryPoint.setView(this);
	entryPoint.refreshView();
	getController().getViewContext().setContextModel(
		findStartingViewContext());
	getWorkspaceView().refreshView();
	getOsylTreeView().refreshView();
    }

    private void subscribeChildrenViewsToLocalHandlers() {
	getController().getViewContext().addEventHandler(
		(ViewContextSelectionEventHandler) getWorkspaceView());
	getController().getViewContext().addEventHandler(
		(ViewContextSelectionEventHandler) getOsylTreeView());
    }

    private void unsubscribeChildrenViewsToLocalHandlers() {
	getController().getViewContext().removeEventHandler(getWorkspaceView());
	getController().getViewContext().removeEventHandler(getOsylTreeView());
    }

    public String getAccess() {
	return access;
    }

    public void setAccess(String access) {
	this.access = access;
    }

    public OsylController getController() {
	return controller;
    }

    public void setController(OsylController controller) {
	this.controller = controller;
    }

    public VerticalPanel getMainPanel() {
	return mainPanel;
    }

    public void setMainPanel(VerticalPanel mainPanel) {
	this.mainPanel = mainPanel;
    }

    public OsylTreeView getOsylTreeView() {
	return osylTree;
    }

    public void setOsylTreeView(OsylTreeView osylTree) {
	this.osylTree = osylTree;
    }

    public OsylWorkspaceView getWorkspaceView() {
	return osylWorkspaceView;
    }

    public void setWorkspaceView(OsylWorkspaceView osylWorkspaceView) {
	this.osylWorkspaceView = osylWorkspaceView;
    }

    public void onClosePushButton(ClosePushButtonEvent event) {
	unsubscribeChildrenViewsToLocalHandlers();
	getController().setReadOnly(false);
	getController().setInPreview(false);
	entryPoint.setView(previousMainView);
	entryPoint.refreshView();
	getController().getViewContext().setContextModel(
		getController().getMainView().findStartingViewContext());
	((OsylMainView) previousMainView).refreshView();
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

}
