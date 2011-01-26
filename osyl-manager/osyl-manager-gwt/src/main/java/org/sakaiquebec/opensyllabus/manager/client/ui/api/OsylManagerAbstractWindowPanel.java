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
package org.sakaiquebec.opensyllabus.manager.client.ui.api;

import org.gwt.mosaic.ui.client.WindowPanel;
import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;

import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylManagerAbstractWindowPanel extends WindowPanel{

    protected OsylManagerController controller;
    
    protected  VerticalPanel mainPanel;
    
    protected Messages messages;
    
    public OsylManagerAbstractWindowPanel(OsylManagerController controller) {
	super();
	this.controller=controller;
	messages = this.controller.getMessages();
	setResizable(true);
	setAnimationEnabled(true);
	setCaptionAction(null);
	
	mainPanel = new VerticalPanel();
	
	setWidget(mainPanel);
	setStylePrimaryName("OsylManager-form");
	
    }
    
    protected HorizontalPanel createPanel(Label l, Widget w){
	HorizontalPanel hp = new HorizontalPanel();
	hp.add(l);
	l.setStylePrimaryName("OsylManager-form-label");
	hp.add(w);
	w.setStylePrimaryName("OsylManager-form-element");
	hp.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_BOTTOM);
	hp.setCellWidth(l, "30%");
	hp.setStylePrimaryName("OsylManager-form-genericPanel");
	return hp;
    }
}

