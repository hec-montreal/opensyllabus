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

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CMCourseInfoView extends OsylManagerAbstractView {

    private HorizontalPanel mainPanel;

    private Label nameInfo = new Label();
    private Label sessionInfo = new Label();
    private Label instructorInfo = new Label();
    private Label studentNumberInfo = new Label();
    private Label sectionInfo = new Label();
    private Image image;

    public CMCourseInfoView(OsylManagerController controller) {
	super(controller);
	mainPanel = new HorizontalPanel();
	image = getController().getImageBundle().info().createImage();
	initView();
	initWidget(mainPanel);
    }

    private void initView() {

	mainPanel.clear();

	mainPanel.add(image);

	VerticalPanel infoPanel = new VerticalPanel();

	Label info =
		new Label(getController().getMessages().cminfoView_label()
			+ ":");
	infoPanel.add(info);

	HorizontalPanel h1 = new HorizontalPanel();
	Label name =
		new Label(getController().getMessages().cminfoView_name() + ":");
	h1.add(name);
	h1.add(nameInfo);
	infoPanel.add(h1);

	HorizontalPanel h2 = new HorizontalPanel();
	Label sessionLabel =
		new Label(getController().getMessages().cminfoView_session()
			+ ":");
	h2.add(sessionLabel);
	h2.add(sessionInfo);
	infoPanel.add(h2);

	HorizontalPanel h3 = new HorizontalPanel();
	Label sectionLabel =
		new Label(getController().getMessages().cminfoView_section()
			+ ":");
	h3.add(sectionLabel);
	h3.add(sectionInfo);
	infoPanel.add(h3);

	HorizontalPanel h4 = new HorizontalPanel();
	Label instructor =
		new Label(getController().getMessages().cminfoView_instructor()
			+ ":");
	h4.add(instructor);
	h4.add(instructorInfo);
	infoPanel.add(h4);

	HorizontalPanel h5 = new HorizontalPanel();
	Label studentNumberLabel =
		new Label(getController().getMessages()
			.cminfoView_studentNumber()
			+ ":");
	h5.add(studentNumberLabel);
	h5.add(studentNumberInfo);
	infoPanel.add(h5);

	mainPanel.add(infoPanel);

    }

    public void refreshView(CMCourse course) {
	if (course != null) {
	    nameInfo = new Label(course.getName());
	    sessionInfo = new Label(course.getSession());
	    sectionInfo = new Label(course.getSection());
	    instructorInfo =
		    new Label(course.getInstructor().equals("") ? "N/D"
			    : course.getInstructor());
	    studentNumberInfo =
		    new Label((course.getStudentNumber() == -1) ? "N/D"
			    : Integer.toString(course.getStudentNumber()));
	} else {
	    nameInfo = new Label("");
	    sessionInfo = new Label("");
	    sectionInfo = new Label("");
	    instructorInfo = new Label("");
	    studentNumberInfo = new Label("");
	}
	initView();
    }

    public void setImage(Image i) {
	image = i;
	initView();
    }

}
