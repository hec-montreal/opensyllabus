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
public class PublishAction extends OsylManagerAbstractAction {

    private static List<String> messages = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private class PublishAsynCallBack implements AsyncCallback<Void> {

	private String siteId;

	public PublishAsynCallBack(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    PublishAction.messages.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    PublishAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    PublishAction.asynCB_return++;
	    if (PublishAction.asynCB_return == PublishAction.coSites.size()) {
		String msg="";
		if (PublishAction.asynCB_OK != PublishAction.coSites.size()) {
		    msg =
			    controller.getMessages()
				    .publishAction_publish_error()
				    + "\n";
		    for (String id : messages) {
			msg +=
				id
					+ controller
						.getMessages()
						.publishAction_publish_error_detail()
					+ "\n";
		    }
		    
		}else{
		    msg =
			    controller.getMessages()
				    .publishAction_publish_ok();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
		Window.alert(msg);
	    }
	}

    }

    public PublishAction(OsylManagerController controller) {
	super(controller, "mainView_action_publish");
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
	    controller.publish(coSite.getSiteId(), new PublishAsynCallBack(
		    coSite.getSiteId()));
	}
    }

}
