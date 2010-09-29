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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class DeleteAction extends OsylManagerAbstractAction {

    private static List<String> lMsg = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private class DeleteAsynCallBack implements AsyncCallback<Void> {

	private String siteId;

	public DeleteAsynCallBack(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    diag.hide();
	    DeleteAction.lMsg.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    DeleteAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    DeleteAction.asynCB_return++;
	    if (DeleteAction.asynCB_return == DeleteAction.coSites.size()) {
		String msg = "";
		if (DeleteAction.asynCB_OK != DeleteAction.coSites.size()) {
		    msg = messages.deleteAction_delete_error() + "\n";
		    for (String id : lMsg) {
			msg +=
				id
					+ messages
						.deleteAction_delete_error_detail()
					+ "\n";
		    }

		} else {
		    msg = messages.deleteAction_delete_ok();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
		new OsylUnobtrusiveAlert(msg);
	    }
	}

    }

    public DeleteAction(OsylManagerController controller) {
	super(controller, "mainView_action_delete");
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
	// TODO: i18n
	String pw = Window.prompt("Mot de passe / Password ?", "");
	String okPw = "osyl" + "123";
	if (null == pw) {
	    return;
	} else if (!okPw.equals(pw)) {
	    // TODO: i18n
	    Window
		    .alert("L'opération est refusée. Veuillez contacter le centre d'assistance.");
	    return;
	}
	String message="";
	boolean hasChild=false;
	for(COSite coSite:siteIds){
	    if(coSite.hasChild()){
		hasChild=true;
		break;
	    }
	}
	if(hasChild)
	    message=messages.deleteAction_delete_siteHasChild()+"\n";
	message+=messages.deleteAction_delete_confirmation();
	
	OsylOkCancelDialog conf = new OsylOkCancelDialog(messages.OsylWarning_Title(),message);
	conf.addOkButtonCLickHandler(new ClickHandler() {
	    
	    public void onClick(ClickEvent event) {
		diag.show();
		    diag.centerAndFocus();
		    coSites = siteIds;
		    asynCB_return = 0;
		    asynCB_OK = 0;
		    lMsg = new ArrayList<String>();

		    for (COSite coSite : siteIds) {
			controller.deleteSite(coSite.getSiteId(),
				new DeleteAsynCallBack(coSite.getSiteId()));
		    }
	    }
	});
	conf.center();
	conf.show();
    }

}
