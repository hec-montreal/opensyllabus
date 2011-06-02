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
public class UnpublishAction extends OsylManagerAbstractAction {

    private static List<String> lMsg = new ArrayList<String>();

    private static List<COSite> coSites;

    private static int asynCB_return = 0;

    private static int asynCB_OK = 0;

    private boolean permissionException = false;

    private class UnpublishAsynCallBack implements AsyncCallback<Void> {

	private String siteId;

	public UnpublishAsynCallBack(String siteId) {
	    super();
	    this.siteId = siteId;
	}

	public void onFailure(Throwable caught) {
	    diag.hide();
	    if (caught instanceof OsylPermissionException)
		permissionException = true;
	    UnpublishAction.lMsg.add(siteId);
	    responseReceive();
	}

	public void onSuccess(Void result) {
	    diag.hide();
	    UnpublishAction.asynCB_OK++;
	    responseReceive();
	}

	private void responseReceive() {
	    UnpublishAction.asynCB_return++;
	    if (UnpublishAction.asynCB_return == UnpublishAction.coSites.size()) {
		String msg = "";
		if (UnpublishAction.asynCB_OK != UnpublishAction.coSites.size()) {
		    msg = messages.unpublishAction_unpublish_error() + "\n";
		    if (permissionException) {
			msg += messages.permission_exception();
		    } else {
			for (String id : lMsg) {
			    msg +=
				    id
					    + messages
						    .unpublishAction_unpublish_error_detail()
					    + "\n";
			}
		    }
		    OsylOkCancelDialog conf =
			    new OsylOkCancelDialog(false, true, messages
				    .error(), msg, true, false);
		    conf.setWidth("450px");
		    conf.show();
		    conf.centerAndFocus();
		} else {
		    msg = messages.unpublishAction_unpublish_ok();
		    OsylUnobtrusiveAlert alert = new OsylUnobtrusiveAlert(msg);
		    OsylManagerEntryPoint.showWidgetOnTop(alert);
		}
		controller.notifyManagerEventHandler(new OsylManagerEvent(null,
			OsylManagerEvent.SITE_INFO_CHANGE));
	    }
	}

    }

    public UnpublishAction(OsylManagerController controller) {
	super(controller, "mainView_action_unpublish",
		"mainView_action_unpublish_tooltip");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	if (siteIds.size() > 0) {
	    for (COSite cosite : siteIds) {
	    	if (cosite.isCoIsFrozen() || cosite.getLastPublicationDate()==null)
	    	    return false;
	    }
	    return true;
	} else {
	    return false;
	}
    }
    
    
    @Override
    public void onClick(final List<COSite> siteIds) {
	String sites = "";
	for (COSite coSite : siteIds) {
	    sites += coSite.getSiteId() + ",<br>";
	}
	sites=sites.substring(0,sites.length()-5);
	String message = "";
	message +=
		messages.unpublishAction_unpublish_confirmation().replace(
			"{0}", sites);

	OsylOkCancelDialog conf =
		new OsylOkCancelDialog(messages.OsylWarning_Title(), message);
	conf.addOkButtonCLickHandler(new ClickHandler() {

	    public void onClick(ClickEvent event) {
		diag.show();
		diag.centerAndFocus();
		coSites = siteIds;
		asynCB_return = 0;
		asynCB_OK = 0;
		lMsg = new ArrayList<String>();
		for (COSite cosite : siteIds) {
		    controller.unpublish(cosite.getSiteId(),
			    new UnpublishAsynCallBack(cosite.getSiteId()));
		}
	    }
	});
	conf.setWidth("450px");
	conf.show();
	conf.centerAndFocus();
    }

}
