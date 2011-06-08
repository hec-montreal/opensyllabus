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

import java.util.Arrays;
import java.util.List;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class NavigationHomePage extends AbstractPortalView {

    private VerticalPanel mainPanel;

    private static String viewPrefix = "NavigationHomePage";

    public static final String ACAD_CAREER_BAA = "BAA";
    public static final String ACAD_CAREER_APRE = "APRE";
    public static final String ACAD_CAREER_CERT = "CERT";
    public static final String ACAD_CAREER_MBA = "MBA";
    public static final String ACAD_CAREER_MSC = "MSC";
    public static final String ACAD_CAREER_MSCP = "MSCP";
    public static final String ACAD_CAREER_DESS = "DES";
    public static final String ACAD_CAREER_PHD = "PHD";
    public static final String ACAD_CAREER_PHDP = "PHDP";
    public static final String RESPONSIBLE_IEA = "IEA";
    public static final String RESPONSIBLE_FINANCE = "FINANCE";
    public static final String RESPONSIBLE_GOL = "GOL";
    public static final String RESPONSIBLE_GRH = "GRH";
    public static final String RESPONSIBLE_INTERNAT = "INTERNAT";
    public static final String RESPONSIBLE_MQG = "MQG";
    public static final String RESPONSIBLE_SC_COMPT = "SC.COMPT.";
    public static final String RESPONSIBLE_TI = "TI";
    public static final String RESPONSIBLE_MARKETING = "MARKETING";
    public static final String RESPONSIBLE_MNGT = "MNGT";
    public static final String RESPONSIBLE_BUR_REGIST = "BUR.REGIST";
    public static final String RESPONSIBLE_QUAL_COMM = "QUAL.COMM.";
    public static final String RESPONSIBLE_BAA = "BAA";
    public static final String RESPONSIBLE_CERTIFICAT = "CERTIFICAT";
    public static final String RESPONSIBLE_DIPLOMES = "DIPLOMES";
    public static final String RESPONSIBLE_MBA = "MBA";
    public static final String RESPONSIBLE_MSC = "MSC";
    public static final String RESPONSIBLE_DOCTORAT = "DOCTORAT";

    public static final List<String> ACAD_CARREERS = Arrays
	    .asList(new String[] { ACAD_CAREER_BAA, ACAD_CAREER_APRE,
		    ACAD_CAREER_CERT, ACAD_CAREER_MBA, ACAD_CAREER_MSC,
		    ACAD_CAREER_MSCP, ACAD_CAREER_DESS, ACAD_CAREER_PHD,
		    ACAD_CAREER_PHDP });

    private static final List<String> responsible_part1 = Arrays
	    .asList(new String[] { RESPONSIBLE_IEA, RESPONSIBLE_FINANCE,
		    RESPONSIBLE_GOL, RESPONSIBLE_GRH, RESPONSIBLE_INTERNAT,
		    RESPONSIBLE_MQG, RESPONSIBLE_SC_COMPT, RESPONSIBLE_TI,
		    RESPONSIBLE_MARKETING, RESPONSIBLE_MNGT });

    private static final List<String> responsible_part2 = Arrays
	    .asList(new String[] { RESPONSIBLE_BUR_REGIST,
		    RESPONSIBLE_QUAL_COMM, RESPONSIBLE_BAA,
		    RESPONSIBLE_CERTIFICAT, RESPONSIBLE_DIPLOMES,
		    RESPONSIBLE_MBA, RESPONSIBLE_MSC, RESPONSIBLE_DOCTORAT });

    public NavigationHomePage() {
	super(viewPrefix);
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	initView();
    }

    private void initView() {

	Label t1 = new Label(getMessage("courseList"));
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);

	Label t2 = new Label(getMessage("courseListByAcadCareer"));
	t2.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t2);

	mainPanel.add(getProgramLabel(ACAD_CAREER_BAA));
	Label apre_label = getProgramLabel(ACAD_CAREER_APRE);
	apre_label.setText("- " + apre_label.getText());
	mainPanel.add(apre_label);
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel(ACAD_CAREER_CERT));
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel(ACAD_CAREER_MBA));
	mainPanel.add(getProgramLabel(ACAD_CAREER_MSC));
	mainPanel.add(getProgramLabel(ACAD_CAREER_DESS));
	mainPanel.add(new HTML("&nbsp;"));
	mainPanel.add(getProgramLabel(ACAD_CAREER_PHD));

	Label t3 = new Label(getMessage("courseListByResponsible"));
	t3.setStylePrimaryName("NHP_titre2");
	mainPanel.add(t3);

	for (final String responsible : responsible_part1) {
	    mainPanel.add(getResponsibleLabel(responsible));
	}

	mainPanel.add(new HTML("&nbsp;"));

	for (final String responsible : responsible_part2) {
	    mainPanel.add(getResponsibleLabel(responsible));
	}
    }

    private Label getProgramLabel(final String program) {
	Label l = new Label(getMessage(PortalController.ACAD_CAREER_PREFIX + program));
	l.setStylePrimaryName("NHP_link");
	l.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		getController().createCourseViewForAcadCareer(program);
	    }
	});
	return l;
    }

    private Label getResponsibleLabel(final String responsible) {
	Label l = new Label(getMessage(PortalController.RESPONSIBLE_PREFIX + responsible));
	l.setStylePrimaryName("NHP_link");
	l.addClickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		getController().createCourseViewForResponsible(responsible);
	    }
	});
	return l;
    }

    public static String getViewKeyPrefix() {
	return viewPrefix;
    }

}
