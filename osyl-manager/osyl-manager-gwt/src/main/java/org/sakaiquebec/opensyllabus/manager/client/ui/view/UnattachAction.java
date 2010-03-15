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

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractAction;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class UnattachAction extends OsylManagerAbstractAction {

    private static List<String> messages = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private class UnattachAsyncCallback implements AsyncCallback<Void> {

	private String siteId;

	public UnattachAsyncCallback(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    UnattachAction.messages.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    UnattachAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    UnattachAction.asynCB_return++;
	    if (UnattachAction.asynCB_return == UnattachAction.coSites.size()) {
		if (UnattachAction.asynCB_OK != UnattachAction.coSites.size()) {
		    String msg =
			    controller.getMessages()
				    .unattachAction_unattach_error()
				    + "\n";
		    for (String id : messages) {
			msg +=
				id
					+ controller
						.getMessages()
						.unattachAction_unattach_error_detail()
					+ "\n";
		    }
		    Window.alert(msg);
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    }
	}

    }

    public UnattachAction(OsylManagerController controller) {
	super(controller, "mainView_action_unattach");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	boolean b = true;
	for (COSite cos : siteIds) {
	    if (cos.getParentSite() == null || cos.getParentSite().equals(""))
		b = false;
	}
	return b;
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	coSites = siteIds;
	asynCB_return = 0;
	asynCB_OK = 0;
	messages = new ArrayList<String>();
	for (COSite cos : siteIds) {
	    String cosId = cos.getSiteId();
	    controller.dissociate(cosId, cos.getParentSite(),
		    new UnattachAsyncCallback(cosId));
	}
    }

}
