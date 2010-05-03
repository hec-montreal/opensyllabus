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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractWindowPanel;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AssociateForm extends OsylManagerAbstractWindowPanel {

    private static int SUGGESTION_LIMIT = 15;

    private COSite selectedSite;

    private CMCourse selectedCourse;

    private SuggestBox sigleSuggestBox;

    private SuggestBox nomSuggestBox;

    private CMCourseInfoView cmCourseInfoView;

    private MultiWordSuggestOracle sigleOracle = new MultiWordSuggestOracle();

    private MultiWordSuggestOracle nameOracle = new MultiWordSuggestOracle();

    private PushButton okButton;

    private Map<String, CMCourse> sigleCourseMap;

    private Map<String, CMCourse> nameCourseMap;

    private SelectionHandler<Suggestion> suggestBoxSelectionHandler =
	    new SelectionHandler<Suggestion>() {

		public void onSelection(SelectionEvent<Suggestion> event) {
		    Map<String, CMCourse> map = null;
		    if (event.getSource().equals(sigleSuggestBox))
			map = sigleCourseMap;
		    if (event.getSource().equals(nomSuggestBox))
			map = nameCourseMap;
		    selectedCourse =
			    map.get(event.getSelectedItem()
				    .getReplacementString());
		    cmCourseInfoView.refreshView(selectedCourse);
		    okButton.setEnabled(true);
		}

	    };

    AsyncCallback<List<CMCourse>> coursesListAsyncCallback =
	    new AsyncCallback<List<CMCourse>>() {

		public void onFailure(Throwable caught) {
		    Window.alert(controller.getMessages().rpcFailure());
		}

		public void onSuccess(List<CMCourse> result) {
		    sigleCourseMap = new HashMap<String, CMCourse>();
		    nameCourseMap = new HashMap<String, CMCourse>();
		    for (CMCourse course : result) {
			String sigleValue =
				course.getSigle() + " " + course.getSession()
					+ " " + course.getSection();
			sigleOracle.add(sigleValue);
			sigleCourseMap.put(sigleValue, course);
			String nameValue =
				course.getName() + " " + course.getSession()
					+ " " + course.getSection();
			nameOracle.add(nameValue);
			nameCourseMap.put(nameValue, course);

		    }
		}
	    };

    AsyncCallback<Void> associateToCMAsyncCallback = new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	    Window.alert(controller.getMessages().rpcFailure());
	}

	public void onSuccess(Void result) {
	    controller.notifyManagerEventHandler(new OsylManagerEvent(null,
		    OsylManagerEvent.SITE_INFO_CHANGE));
	    AssociateForm.this.onAssociationEnd();
	}

    };

    public AssociateForm(OsylManagerController controller, COSite site) {
	super(controller);
	this.selectedSite = site;

	Label title =
		new Label(controller.getMessages().mainView_action_associate());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label instruction =
		new Label(controller.getMessages().associateForm_instruction()
			.replace("{0}", selectedSite.getSiteId()));
	mainPanel.add(instruction);

	Label l =
		new Label(controller.getMessages()
			.associateForm_courseIdentifier());
	sigleSuggestBox = createSuggestBoxWithOracle(sigleOracle);
	HorizontalPanel hsigle = createPanel(l, sigleSuggestBox);
	mainPanel.add(hsigle);
	mainPanel.setCellHorizontalAlignment(hsigle,
		HasHorizontalAlignment.ALIGN_CENTER);

	Label l2 =
		new Label(controller.getMessages().associateForm_courseName());
	nomSuggestBox = createSuggestBoxWithOracle(nameOracle);
	HorizontalPanel hnom = createPanel(l2, nomSuggestBox);
	mainPanel.add(hnom);
	mainPanel.setCellHorizontalAlignment(hnom,
		HasHorizontalAlignment.ALIGN_CENTER);

	controller.getCMCourses(coursesListAsyncCallback);

	cmCourseInfoView = new CMCourseInfoView(controller);
	DecoratorPanel ivDecoratorPanel = new DecoratorPanel();
	ivDecoratorPanel.setWidget(cmCourseInfoView);
	ivDecoratorPanel.setStylePrimaryName("OsylManager-infoView");
	mainPanel.add(ivDecoratorPanel);
	mainPanel.setCellHorizontalAlignment(ivDecoratorPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	okButton = new PushButton(controller.getMessages().associateForm_ok());
	okButton.setWidth("25px");
	okButton.setEnabled(false);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		AssociateForm.this.controller.associateToCM(selectedCourse
			.getId(), selectedSite.getSiteId(),
			associateToCMAsyncCallback);
	    }
	});

	PushButton cancelButton =
		new PushButton(controller.getMessages().associateForm_cancel());
	cancelButton.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		AssociateForm.super.hide();
	    }
	});

	HorizontalPanel hz = new HorizontalPanel();
	hz.add(okButton);
	hz.add(cancelButton);

	mainPanel.add(hz);
	mainPanel.setCellHorizontalAlignment(hz,
		HasHorizontalAlignment.ALIGN_CENTER);
    }

    private SuggestBox createSuggestBoxWithOracle(SuggestOracle oracle) {
	SuggestBox suggestBox = new SuggestBox(oracle);
	suggestBox.setAnimationEnabled(true);
	suggestBox.setStylePrimaryName("OsylManager-SuggestBox");
	suggestBox.setPopupStyleName("OsylManager-SuggestBoxPopup");
	suggestBox.setLimit(SUGGESTION_LIMIT);
	suggestBox.addSelectionHandler(suggestBoxSelectionHandler);
	suggestBox.getTextBox().addChangeHandler(new ChangeHandler() {
	    public void onChange(ChangeEvent event) {
		cmCourseInfoView.refreshView(null);
		okButton.setEnabled(false);
	    }
	});
	return suggestBox;
    }

    protected void onAssociationEnd() {
	mainPanel.clear();

	Label title =
		new Label(controller.getMessages().mainView_action_associate());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label conf =
		new Label(controller.getMessages().associateForm_confirmation());
	mainPanel.add(conf);

	cmCourseInfoView.setImage(controller.getImageBundle().check()
		.createImage());
	DecoratorPanel ivDecoratorPanel = new DecoratorPanel();
	ivDecoratorPanel.setWidget(cmCourseInfoView);
	ivDecoratorPanel.setStylePrimaryName("OsylManager-infoView");
	mainPanel.add(ivDecoratorPanel);
	mainPanel.setCellHorizontalAlignment(ivDecoratorPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		AssociateForm.super.hide();
	    }
	});
	mainPanel.add(okButton);
	mainPanel.setCellHorizontalAlignment(okButton,
		HasHorizontalAlignment.ALIGN_CENTER);

    }

}
