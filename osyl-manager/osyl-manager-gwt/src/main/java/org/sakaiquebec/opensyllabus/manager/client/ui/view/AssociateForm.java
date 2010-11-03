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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AssociateForm extends OsylManagerAbstractWindowPanel {

    private COSite selectedSite;

    private CMCourse selectedCourse;

    private TextBox sigleTextBox;

    private CMCourseInfoView cmCourseInfoView;

    private MultiWordSuggestOracle sigleOracle = new MultiWordSuggestOracle();

    private PushButton okButton;

    private Map<String, CMCourse> sigleCourseMap;

    private final OsylCancelDialog diag;

    private ListBox suggestionListBox;
    
    private Image spinner;

    AsyncCallback<List<CMCourse>> coursesListAsyncCallback =
	    new AsyncCallback<List<CMCourse>>() {

		public void onFailure(Throwable caught) {
		    spinner.setVisible(false);
		    OsylOkCancelDialog warning =
			    new OsylOkCancelDialog(false, true, messages
				    .OsylWarning_Title(),
				    messages.rpcFailure(), false, true);
		    warning.show();
		    warning.centerAndFocus();
		}

		public void onSuccess(List<CMCourse> result) {
		    spinner.setVisible(false);
		    sigleOracle = new MultiWordSuggestOracle();
		    sigleCourseMap = new HashMap<String, CMCourse>();
		    for (CMCourse course : result) {
			String sigleValue =
				course.getSigle() + " " + course.getSession()
					+ " " + course.getSection();
			sigleOracle.add(sigleValue);
			sigleCourseMap.put(sigleValue, course);
			suggestionListBox.addItem(sigleValue, sigleValue);
		    }
		    suggestionListBox.setSelectedIndex(0);
		}
	    };

    AsyncCallback<Void> associateToCMAsyncCallback = new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	    diag.hide();
	    OsylOkCancelDialog alert =
		    new OsylOkCancelDialog(false, true, messages
			    .OsylWarning_Title(), messages.rpcFailure(), true,
			    false);
	    alert.show();
	    alert.centerAndFocus();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    controller.notifyManagerEventHandler(new OsylManagerEvent(null,
		    OsylManagerEvent.SITE_INFO_CHANGE));
	    AssociateForm.this.onAssociationEnd();
	}

    };

    public AssociateForm(final OsylManagerController controller, COSite site,
	    OsylCancelDialog aDiag) {
	super(controller);
	this.selectedSite = site;
	this.diag = aDiag;

	Label title = new Label(messages.mainView_action_associate());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label instruction =
		new Label(messages.associateForm_instruction().replace("{0}",
			selectedSite.getSiteId()));
	mainPanel.add(instruction);

	Label l = new Label(messages.associateForm_courseIdentifier());

	sigleTextBox = new TextBox();

	final Button search = new Button(messages.associateForm_search());
	search.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		spinner.setVisible(true);
		String value = sigleTextBox.getText();
		suggestionListBox.clear();
		controller.getCMCourses(value, coursesListAsyncCallback);
	    }
	});
	search.setEnabled(false);

	HorizontalPanel hp = new HorizontalPanel();
	hp.add(l);
	l.setStylePrimaryName("OsylManager-form-label");
	hp.add(sigleTextBox);
	sigleTextBox.setStylePrimaryName("OsylManager-form-element");
	hp.add(search);
	hp.setCellWidth(l, "30%");
	hp.setCellWidth(sigleTextBox, "40%");
	hp.setCellWidth(search, "30%");
	hp.setCellVerticalAlignment(l, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(sigleTextBox,
		HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellVerticalAlignment(search, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setStylePrimaryName("OsylManager-form-genericPanel");
	mainPanel.add(hp);
	mainPanel.setCellHorizontalAlignment(hp,
		HasHorizontalAlignment.ALIGN_CENTER);

	sigleTextBox.addKeyPressHandler(new KeyPressHandler() {

	    public void onKeyPress(KeyPressEvent event) {
		cmCourseInfoView.refreshView(null);
		okButton.setEnabled(false);
		String value = sigleTextBox.getText();
		search.setEnabled(value.length() >= 3);
	    }
	});

	suggestionListBox = new ListBox();
	suggestionListBox.addChangeHandler(new ChangeHandler() {

	    public void onChange(ChangeEvent event) {
		selectedCourse =
			sigleCourseMap
				.get(suggestionListBox
					.getValue(suggestionListBox
						.getSelectedIndex()));
		cmCourseInfoView.refreshView(selectedCourse);
		okButton.setEnabled(true);
	    }
	});
	
	
	HorizontalPanel hzPanel = new HorizontalPanel();
	Label voidLabel = new Label();
	hzPanel.add(voidLabel);
	hzPanel.setCellWidth(voidLabel, "30%");
	hzPanel.setStylePrimaryName("OsylManager-form-genericPanel");
	hzPanel.add(suggestionListBox);
	spinner = new Image(controller.getImageBundle().ajaxloader());
	hzPanel.add(spinner);
	spinner.setVisible(false);
	mainPanel.add(hzPanel);
	mainPanel.setCellHorizontalAlignment(hzPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	cmCourseInfoView = new CMCourseInfoView(controller);
	DecoratorPanel ivDecoratorPanel = new DecoratorPanel();
	ivDecoratorPanel.setWidget(cmCourseInfoView);
	ivDecoratorPanel.setStylePrimaryName("OsylManager-infoView");
	mainPanel.add(ivDecoratorPanel);
	mainPanel.setCellHorizontalAlignment(ivDecoratorPanel,
		HasHorizontalAlignment.ALIGN_CENTER);

	okButton = new PushButton(messages.associateForm_ok());
	okButton.setStylePrimaryName("Osyl-Button");
	okButton.setWidth("50px");
	okButton.setEnabled(false);
	okButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		diag.show();
		diag.centerAndFocus();
		AssociateForm.this.controller.associateToCM(selectedCourse
			.getId(), selectedSite.getSiteId(),
			associateToCMAsyncCallback);
	    }
	});

	PushButton cancelButton =
		new PushButton(messages.associateForm_cancel());
	cancelButton.setStylePrimaryName("Osyl-Button");
	cancelButton.setWidth("50px");
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

    protected void onAssociationEnd() {
	mainPanel.clear();

	Label title = new Label(messages.mainView_action_associate());
	title.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(title);

	Label conf = new Label(messages.associateForm_confirmation());
	mainPanel.add(conf);

	cmCourseInfoView
		.setImage(new Image(controller.getImageBundle().check()));
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
