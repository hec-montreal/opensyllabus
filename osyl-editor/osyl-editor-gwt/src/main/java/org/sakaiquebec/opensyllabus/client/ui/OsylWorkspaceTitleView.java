/*******************************************************************************
 * $Id: OsylWorkspaceView.java 636 2008-05-27 15:41:58Z remi.saias@hec.ca $
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

import java.util.Date;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * OsylWorkspaceView is the main area in OpenSyllabus editor.<br/>
 * <br/>
 * OsylWorkspaceView implements {@link ViewContextSelectionEventHandler} to be
 * notified that it has to refresh its view, for instance when the user clicked
 * on a lecture to display it.
 * 
 * @version $Id: $
 */
public class OsylWorkspaceTitleView extends OsylViewableComposite implements
	ViewContextSelectionEventHandler, UpdateCOStructureElementEventHandler,
	UpdateCOUnitEventHandler {

    // View variables
    private SimplePanel workspacePanel;
    private DateTimeFormat dateTimeFormat;
    private HTML workspaceTitleLabel;

    // View Constructor
    public OsylWorkspaceTitleView(COModelInterface model,
	    OsylController controller) {
	super(model, controller);
	setWorkspacePanel(new SimplePanel());
	initWidget(getWorkspacePanel());
	dateTimeFormat = getController().getSettings().getDateFormat();
    }

    public COElementAbstract getModel() {
	return (COElementAbstract) super.getModel();
    }

    public SimplePanel getWorkspacePanel() {
	return workspacePanel;
    }

    private void setWorkspacePanel(SimplePanel workspacePanel) {
	this.workspacePanel = workspacePanel;
    }

    /**
     * {@inheritDoc}
     */
    public void onViewContextSelection(ViewContextSelectionEvent event) {
	COModelInterface eventModel = (COModelInterface) event.getSource();
	if (eventModel != null) {
	    setModel(eventModel);
	    refreshView();
	}
    }

    /**
     * Refreshes the current view according to current model object.
     */
    public void refreshView() {
	getWorkspacePanel().clear();
	workspaceTitleLabel = new HTML();
	workspaceTitleLabel.setStylePrimaryName("Osyl-WorkspaceView-Header");
	String titleLabel = "";
	if (getModel() != null) {
	    if (getModel().isCourseOutlineContent()) {
		try {
		    if (null != getController().getCOSerialized().getTitle()
			    && !"".equals(getController().getCOSerialized()
				    .getTitle())) {
			titleLabel =
				getController().getCOSerialized().getTitle();
		    } else {
			titleLabel = getCoMessage("courseoutline");
		    }

		} catch (Exception e) {
		    titleLabel = getCoMessage("courseoutline");
		}
	    } else if (getModel().isCOUnit()
		    && (COUnitType.ASSESSMENT_UNIT.equals(getModel().getType()))) {

		String rating =
			(getWeight() != null && !getWeight().equals("")) ? " ("
				+ getWeight() + "%)" : "";

		String date =
			(getDateEnd() != null) ? ("  " + dateTimeFormat
				.format(getDateEnd())) : "";

		titleLabel = getModel().getLabel() + rating + date;

	    } else {

		titleLabel = getModel().getLabel();
		if (titleLabel == null) {
		    titleLabel = getCoMessage(getModel().getType());
		}

	    }

	    if (getModel().isCOUnit()) {
		((COUnit) getModel()).addEventHandler(this);
	    } else if (getModel().isCOStructureElement()) {
		((COStructureElement) getModel()).addEventHandler(this);
	    }

	}

	if (getController().isSelectedDateBeforeDate(
		OsylDateUtils.getDateFromXMLDate(getModel().getProperty(
			COPropertiesType.MODIFIED)))) {
	    workspaceTitleLabel.setHTML("<span class=\"Osyl-newElement\">"
		    + titleLabel + "</span>");
	} else {
	    workspaceTitleLabel.setText(titleLabel);
	}
	getWorkspacePanel().add(workspaceTitleLabel);
	setWorkspaceTitleWidth();
    }

    public void setSize(String width, String height) {
	getWorkspacePanel().setSize(width, height);
    }

    public void add(Composite contentView) {
	getWorkspacePanel().add(contentView);
    }

    public OsylWorkspaceTitleView getWorkspaceView() {
	return this;
    }

    public void setWorkspaceTitleWidth() {
	OsylMainView mainView = OsylController.getInstance().getMainView();
	int menubarWidth =
		mainView.getOsylToolbarView().getOsylToolbar()
			.getSectionMenuBar().getOffsetWidth();
	int osylWorkspaceLabelLeftPadding = 40;
	int t =
		Math.max(0, mainView.getWorkspaceInnerWidth()
			- (menubarWidth + osylWorkspaceLabelLeftPadding));
	getWorkspaceTitleLabel().setWidth(t + "px");
    }

    public HTML getWorkspaceTitleLabel() {
	return workspaceTitleLabel;
    }

    public void setWorkspaceTitleLabel(HTML workspaceTitleLabel) {
	this.workspaceTitleLabel = workspaceTitleLabel;
    }

    public String getWeight() {
	String reqLevel = null;
	if (!"undefined"
		.equals(getModel().getProperty(COPropertiesType.WEIGHT))
		|| null != getModel().getProperty(COPropertiesType.WEIGHT)) {
	    reqLevel = getModel().getProperty(COPropertiesType.WEIGHT);
	}
	return reqLevel;
    }

    public Date getDateEnd() {
	String dateString = getModel().getProperty(COPropertiesType.DATE_END);
	Date date = null;
	if (dateString != null && !dateString.trim().equals("")) {
	    date = OsylDateUtils.getDateFromXMLDate(dateString);
	}
	return date;
    }

    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	refreshView();
    }

    public void onUpdateModel(UpdateCOUnitEvent event) {
	refreshView();
    }
}
