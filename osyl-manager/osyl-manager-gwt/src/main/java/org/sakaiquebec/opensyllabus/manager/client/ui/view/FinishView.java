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

package org.sakaiquebec.opensyllabus.manager.client.ui.view;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractView;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:Laurent Danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class FinishView extends OsylManagerAbstractView {

    private VerticalPanel mainPanel;
    
    /**
     * Constructor.
     * 
     * @param controller
     */
    public FinishView(OsylManagerController controller) {
	super(controller);
	initView();
    }

    private void initView() {
	mainPanel = new VerticalPanel();
	initWidget(mainPanel);
	mainPanel.setWidth("100%");
	Label finishTitle = new Label(getController().getMessages().finishMessage());
	finishTitle.setStylePrimaryName("OsylManager-form-title");
	mainPanel.add(finishTitle);
    }

}
