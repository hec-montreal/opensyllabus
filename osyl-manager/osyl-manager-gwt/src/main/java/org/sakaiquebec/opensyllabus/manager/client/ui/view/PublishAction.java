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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class PublishAction extends OsylManagerAbstractAction {

    public PublishAction(OsylManagerController controller) {
	super(controller, "mainView_action_publish",
		"mainView_action_publish_tooltip");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	if (siteIds.size() > 0) {
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public void onClick(final List<COSite> siteIds) {
	boolean isNull = false;
	String sites = "";
	for (COSite coSite : siteIds) {
	    if (coSite.isCoIsNull()) {
		isNull = true;
		sites += coSite.getSiteId() + "<br>";
	    }
	}
	if (isNull) {
	    String message =
		    messages.publishAction_publish_voidCO().replace("{0}",
			    sites);
	    OsylOkCancelDialog conf =
		    new OsylOkCancelDialog(messages.OsylWarning_Title(),
			    message);
	    conf.addOkButtonCLickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
		    publish(siteIds);
		}
	    });
	    conf.show();
	    conf.centerAndFocus();
	} else {
	    publish(siteIds);
	}
    }

    private void publish(List<COSite> siteIds) {
	PublishForm publishForm = new PublishForm(controller, siteIds);
	publishForm.showModal();
    }
}
