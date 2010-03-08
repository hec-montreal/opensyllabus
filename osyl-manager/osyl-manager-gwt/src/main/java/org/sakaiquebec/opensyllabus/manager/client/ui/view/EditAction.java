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

import java.util.List;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class EditAction extends OsylManagerAbstractAction {

    public EditAction(OsylManagerController controller) {
	super(controller, "mainView_action_edit");
    }

    @Override
    public boolean isActionEnableForSites(List<String> siteIds) {
	return true;
    }

    @Override
    public void onClick(List<String> siteIds) {
	String serverId = GWT.getModuleBaseURL().split("\\s*/portal/tool/\\s*")[0];
	for (String siteId : siteIds) {
	    Window.open(serverId+ "/portal/site/"+siteId, "_blank", "");
	}
    }

}
