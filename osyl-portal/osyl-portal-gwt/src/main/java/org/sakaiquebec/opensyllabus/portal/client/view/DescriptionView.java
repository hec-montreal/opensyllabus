/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.portal.client.view;

import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class DescriptionView extends WindowPanel {

    protected VerticalPanel mainPanel;

    private String description = "";

    private CODirectorySite coDirectorySite;
    
    private String viewKey;

    private PortalController controller = PortalController.getInstance();

    AsyncCallback<String> callback = new AsyncCallback<String>() {

	public void onFailure(Throwable caught) {
	    initView();
	}

	public void onSuccess(String result) {
	    coDirectorySite.setDescription(result);
	    initView();
	}
    };

    public DescriptionView(CODirectorySite coDirectorySite) {
	this(coDirectorySite,coDirectorySite.getCourseNumber());
    }
    
    public DescriptionView(CODirectorySite coDirectorySite, String key) {
	super();
	this.coDirectorySite = coDirectorySite;
	this.viewKey=key;
	setResizable(false);
	setAnimationEnabled(true);
	setCaptionAction(null);
	setAutoHideEnabled(true);
	mainPanel = new VerticalPanel();
	initView();
	setStylePrimaryName("CP-Description");
	setWidget(mainPanel);
    }

    private void initView() {
	Label t1 =
		new Label(coDirectorySite.getCourseNumber() + " "
			+ coDirectorySite.getCourseName());
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	Label t2 =
		new Label(
			controller
				.getMessage("directoryCoursePage_description"));
	t2.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t2);

	Label t21 = new HTML(coDirectorySite.getDescription());
	t21.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t21);

	Label t3 =
		new Label(controller.getMessage("directoryCoursePage_features"));
	t3.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t3);

	Label t31 =
		new Label(
			controller
				.getMessage("directoryCoursePage_responsible")
				+ " : "
				+ ((coDirectorySite.getResponsible() == null) ? ""
					: controller.getMessage("responsible."
						+ coDirectorySite
							.getResponsible())));
	t31.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t31);

	Label t32 =
		new Label(controller.getMessage("directoryCoursePage_program")
			+ " : "
			+ controller.getMessage("acad_career_"
				+ coDirectorySite.getProgram()));
	t32.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t32);

	Label t33 =
		new Label(controller.getMessage("directoryCoursePage_credits")
			+ " : " + coDirectorySite.getCredits());
	t33.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t33);

	Label t34 =
		new HTML(
			controller
				.getMessage("directoryCoursePage_requirements")
				+ " : " + coDirectorySite.getRequirements());
	t34.setStylePrimaryName("NHP_titre3");
	mainPanel.add(t34);
    }

    public CODirectorySite getCoDirectorySite() {
	return coDirectorySite;
    }

    public void setCoDirectorySite(CODirectorySite coDirectorySite) {
	this.coDirectorySite = coDirectorySite;
    }

    public void setViewKey(String viewKey) {
	this.viewKey = viewKey;
    }

    public String getViewKey() {
	return viewKey;
    }

}
