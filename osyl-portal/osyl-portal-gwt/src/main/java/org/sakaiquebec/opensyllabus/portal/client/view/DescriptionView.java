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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class DescriptionView extends WindowPanel{

    protected VerticalPanel mainPanel;
    
    private String siteId;
    
    
    public DescriptionView(String siteId) {
	super();
	this.siteId=siteId;
	setResizable(false);
	setAnimationEnabled(true);
	setCaptionAction(null);
	setAutoHideEnabled(true);
	mainPanel = new VerticalPanel();
	initView();
	setWidget(mainPanel);
	setStylePrimaryName("CP-Description");
    }
    
    private void initView(){
	mainPanel.add(new HTML(siteId));
    }

    
    
}

