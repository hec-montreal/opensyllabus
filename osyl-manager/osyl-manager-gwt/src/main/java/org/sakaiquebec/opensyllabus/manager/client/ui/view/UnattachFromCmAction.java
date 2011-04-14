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

import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.manager.client.OsylManagerEntryPoint;
import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractAction;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class UnattachFromCmAction extends OsylManagerAbstractAction {

    private static List<String> lMsg = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private boolean permissionException = false;

    private class DissociateAsynCallBack implements AsyncCallback<Void> {

	private String siteId;

	public DissociateAsynCallBack(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    diag.hide();
	    if (caught instanceof OsylPermissionException)
		permissionException = true;
	    UnattachFromCmAction.lMsg.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    UnattachFromCmAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    UnattachFromCmAction.asynCB_return++;
	    if (UnattachFromCmAction.asynCB_return == UnattachFromCmAction.coSites
		    .size()) {
		String msg = "";
		if (UnattachFromCmAction.asynCB_OK != UnattachFromCmAction.coSites
			.size()) {
		    msg = messages.dissociateAction_dissociate_error() + "\n";
		    if (permissionException) {
			msg += messages.permission_exception();
		    } else {
			for (String id : lMsg) {
			    msg +=
				    id
					    + messages
						    .dissociateAction_dissociate_error_detail()
					    + "\n";
			}
		    }
		} else {
		    msg = messages.dissociateAction_dissociate_ok();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
		OsylUnobtrusiveAlert alert = new OsylUnobtrusiveAlert(msg);
		OsylManagerEntryPoint.showWidgetOnTop(alert);
	    }
	}

    }

    public UnattachFromCmAction(OsylManagerController controller) {
	super(controller, "mainView_action_dissociate",
		"mainView_action_dissociate_tooltip");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	if (siteIds.size() > 0) {
	    for (COSite cosite : siteIds) {
		if (cosite.getCourseNumber() == null
			|| cosite.getCourseNumber().equals(""))
		    return false;
	    }
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	diag.show();
	diag.centerAndFocus();
	coSites = siteIds;
	asynCB_return = 0;
	asynCB_OK = 0;
	lMsg = new ArrayList<String>();

	for (COSite coSite : siteIds) {
	    controller.dissociateFromCM(coSite.getSiteId(),
		    new DissociateAsynCallBack(coSite.getSiteId()));
	}
    }
}
