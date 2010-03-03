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
package org.sakaiquebec.opensyllabus.manager.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class RoundCornerPanel extends Composite {

    private DecoratorPanel enclosingPanel;

    public RoundCornerPanel(Composite internalWidget) {
	enclosingPanel = new DecoratorPanel();
	enclosingPanel.setWidget(internalWidget);
	initWidget(enclosingPanel);
    }

    public RoundCornerPanel(SimplePanel internalWidget) {
	enclosingPanel = new DecoratorPanel();
	enclosingPanel.setWidget(internalWidget);
	initWidget(enclosingPanel);
    }

    public RoundCornerPanel(Composite internalWidget, String P1,
	    String P2, String P3, String P4, String P5) {
	this(internalWidget);
    }

    public RoundCornerPanel(SimplePanel enclosingPanel, String string,
	    String string2, String string3, String string4, String string5) {
	this(enclosingPanel);
    }
}


