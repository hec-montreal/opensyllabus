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
import org.sakaiquebec.opensyllabus.shared.model.COSite;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CreateSiteAction extends OsylManagerAbstractAction {

    public CreateSiteAction(OsylManagerController controller) {
	super(controller, "mainView_action_createSite",
		"mainView_action_createSite_tooltip");
	setEnabled(true);
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	return true;
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	CreateSiteForm createSiteForm = new CreateSiteForm(controller);
	createSiteForm.showModal();
    }

}
