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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.portal.client;

import org.sakaiquebec.opensyllabus.portal.client.view.NavigationHomePage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OsylPortalEntryPoint implements EntryPoint {

    private RootPanel rootPanel;

    /** {@inheritDoc} */
    public void onModuleLoad() {
	rootPanel = RootPanel.get();
	initView();

    }

    private void initView() {
	rootPanel.add(new NavigationHomePage());
	setSakaiIFrameHeight(650);
    }
    
    public static native Element getSakaiToolIframe() /*-{
    var elm = $wnd.parent.document.getElementById($wnd.name);
    return (elm != null ? elm : $wnd.document.body);
    }-*/;
    
    private static void setSakaiIFrameHeight(int h) {
	DOM.setStyleAttribute(getSakaiToolIframe(), "height", h + "px");
    }
}
