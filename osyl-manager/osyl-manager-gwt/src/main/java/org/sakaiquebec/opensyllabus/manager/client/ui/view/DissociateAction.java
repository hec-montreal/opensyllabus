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
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler.OsylManagerEvent;
import org.sakaiquebec.opensyllabus.manager.client.ui.api.OsylManagerAbstractAction;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class DissociateAction extends OsylManagerAbstractAction {

    private static List<String> messages = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;
    
    
    private class DissociateAsynCallBack implements AsyncCallback<Void> {

	private String siteId;

	public DissociateAsynCallBack(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    DissociateAction.messages.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    DissociateAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    DissociateAction.asynCB_return++;
	    if (DissociateAction.asynCB_return == DissociateAction.coSites.size()) {
		if (DissociateAction.asynCB_OK != DissociateAction.coSites.size()) {
		    String msg =
			    controller.getMessages()
				    .dissociateAction_dissociate_error()
				    + "\n";
		    for (String id : messages) {
			msg +=
				id
					+ controller
						.getMessages()
						.dissociateAction_dissociate_error_detail()
					+ "\n";
		    }
		    Window.alert(msg);
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    }
	}

    }

    public DissociateAction(OsylManagerController controller) {
	super(controller, "mainView_action_dissociate");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	return true;
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	coSites = siteIds;
	asynCB_return = 0;
	asynCB_OK = 0;
	messages = new ArrayList<String>();
	for (COSite coSite : siteIds) {
	    controller.dissociateFromCM(coSite.getSiteId(), new DissociateAsynCallBack(coSite.getSiteId()));
	}
    }
}
