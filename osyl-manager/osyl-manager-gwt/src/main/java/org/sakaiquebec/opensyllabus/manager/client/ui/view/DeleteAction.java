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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylOkCancelDialog;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylUnobtrusiveAlert;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

    private boolean permissionException = false;

    private class DeleteAsynCallBack implements AsyncCallback<Void> {
	private int index;

	public DeleteAsynCallBack(int index) {
	    super();
	    this.index = index;
	}

	public void onFailure(Throwable caught) {
	    if (caught instanceof OsylPermissionException)
		permissionException = true;
	    DeleteAction.lMsg.add(coSites.get(index).getSiteId());
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    DeleteAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    DeleteAction.asynCB_return++;
	    if (DeleteAction.asynCB_return == DeleteAction.coSites.size()) {
		diag.hide();
		String msg = "";
		if (DeleteAction.asynCB_OK != DeleteAction.coSites.size()) {
		    msg = messages.deleteAction_delete_error() + "\n";
		    if (permissionException) {
			msg += messages.permission_exception();
		    } else {
			for (String id : lMsg) {
			    msg +=
				    id
					    + messages
						    .deleteAction_delete_error_detail()
					    + "\n";
			}
		    }
		} else {
		    msg = messages.deleteAction_delete_ok();
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
		OsylUnobtrusiveAlert alert = new OsylUnobtrusiveAlert(msg);
		OsylManagerEntryPoint.showWidgetOnTop(alert);
	    } else {
		controller.deleteSite(coSites.get(index + 1).getSiteId(),
			new DeleteAsynCallBack(index + 1));
	    }
	}

    }

    public DeleteAction(OsylManagerController controller) {
	super(controller, "mainView_action_delete",
		"mainView_action_delete_tooltip");
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

	boolean hasChild = false;
	boolean isCMLinked = false;
	String sites = "";
	for (COSite coSite : siteIds) {
	    if (coSite.hasChild())
		hasChild = true;
	    if (coSite.getCourseName() != null
		    && !"".equals(coSite.getCourseName()))
		isCMLinked = true;
	    sites += coSite.getSiteId() + "<br>";
	}

	if (isCMLinked) {
	    OsylOkCancelDialog canc =
		    new OsylOkCancelDialog(false, true, messages
			    .OsylWarning_Title(), messages
			    .deleteAction_delete_error_linkedToCM(), true,
			    false);
	    canc.show();
	    canc.centerAndFocus();
	}
	if (hasChild) {
	    OsylOkCancelDialog canc =
		    new OsylOkCancelDialog(false, true, messages
			    .OsylWarning_Title(), messages
			    .deleteAction_delete_error_hasChild(), true, false);
	    canc.show();
	    canc.centerAndFocus();
	} else {
	    String message = "";
	    message +=
		    messages.deleteAction_delete_confirmation().replace("{0}",
			    sites);

	    OsylOkCancelDialog conf =
		    new OsylOkCancelDialog(messages.OsylWarning_Title(),
			    message);
	    conf.addOkButtonCLickHandler(new ClickHandler() {

		public void onClick(ClickEvent event) {
		    diag.show();
		    diag.centerAndFocus();
		    coSites = siteIds;
		    asynCB_return = 0;
		    asynCB_OK = 0;
		    lMsg = new ArrayList<String>();
		    controller.deleteSite(coSites.get(0).getSiteId(),
			    new DeleteAsynCallBack(0));
		}
	    });
	    conf.setWidth("450px");
	    conf.show();
	    conf.centerAndFocus();
	}
    }

}
