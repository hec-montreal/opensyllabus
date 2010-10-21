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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ExportAction extends OsylManagerAbstractAction {
    
    /**
     * Sites index to remember position in sites list.
     */
    private int sitesIndex = 0;
    
    private class ExportCallBack implements AsyncCallback<String>{

	public void onSuccess(String fileUrl) {
	    diag.hide();
	    openDownloadLink(fileUrl);
	}

	public void onFailure(Throwable error) {
	    diag.hide(true);
	    OsylOkCancelDialog warning = new OsylOkCancelDialog(false, true,
		    messages.OsylWarning_Title(), messages.rpcFailure(), true,
		    false);
	    warning.show();
	    warning.centerAndFocus();
	}
	
    }

    public ExportAction(OsylManagerController controller) {
	super(controller, "mainView_action_export",
		"mainView_action_export_tooltip");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	if(siteIds.size() > 0){
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	// TODO: i18n
	String pw = Window.prompt("Mot de passe / Password ?", "");
	String okPw = "osyl" + "123";
	if (null == pw) {
	    return;
	} else if (!okPw.equals(pw)) {
	    // TODO: i18n
	    Window.alert("L'opération est refusée. Veuillez contacter le centre d'assistance.");
	    return;
	}
	//index reset when clicking on export link.
	sitesIndex = 0;
	diag.show();
	diag.centerAndFocus();
	getOsylPackage();
    }
    
    /**
     * This method is used to create the zip package of the course outline.  It
     * is called by the onClick method of the export link and after the window
     * to download the previous zip package is opened.  This means that for now
     * the zip package are created sequentially instead of being created all at
     * the same time.  This is done to fix a RPC problem related to the
     * asynchronous communication between the client and the server.
     */
    private void getOsylPackage(){
	List<COSite> siteIds = controller.getSelectSites();
	
	if(sitesIndex < siteIds.size()){
	    ExportCallBack callB = new ExportCallBack();
	    controller.getOsylPackage(siteIds.get(sitesIndex).getSiteId(), callB);
	    sitesIndex++;
	}
    }

    private void openDownloadLink(String d_url) {
	String url = GWT.getModuleBaseURL();
	String cleanUrl = url.substring(0, url.indexOf("/", 8));
	String downloadUrl = cleanUrl + "/sdata/c" + d_url;
	Window
		.open(
			downloadUrl,
			"_self",
			"location=no,menubar=no,scrollbars=no,resize=no,resizable=no,status=no,toolbar=no,directories=no,width=5,height=5,top=0,left=0'");
	
	//method called to create the next zip package.
	getOsylPackage();
    }
}
