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

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ArchivesPage extends AbstractPortalView{

    private static String viewPrefix = "ArchivesPage_";
    
    private CODirectorySite site;

    private VerticalPanel mainPanel;

    private CoursesTable courseTable;
    
    public ArchivesPage(CODirectorySite site) {
	super(viewPrefix + site.getCourseNumber());
	this.site = site;
	mainPanel = new VerticalPanel();
	initView();
	initWidget(mainPanel);
    }

    private void initView() {
	Label t1 =
		new Label(site.getCourseNumber() + " " + site.getCourseName());
	t1.setStylePrimaryName("NHP_titre1");
	mainPanel.add(t1);
	
	List<CODirectorySite> list = new ArrayList<CODirectorySite>();
	site.setCurrentSections(site.getArchivedSections());
	list.add(site);
	courseTable = new CoursesTable(list);
	mainPanel.add(courseTable);
    }
    
    public static String getViewKeyPrefix(){
	return viewPrefix;
    }

}

