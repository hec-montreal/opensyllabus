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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ExportAction extends OsylManagerAbstractAction {
    
    private class ExportCallBack implements AsyncCallback<String>{

	public void onSuccess(String fileUrl) {
	    openDownloadLink(fileUrl);
	}

	public void onFailure(Throwable error) {
	    Window.alert(controller.getMessages().rpcFailure());
	}
	
    }

    public ExportAction(OsylManagerController controller) {
	super(controller, "mainView_action_export");
    }

    @Override
    public boolean isActionEnableForSites(List<COSite> siteIds) {
	return true;
    }

    @Override
    public void onClick(List<COSite> siteIds) {
	for (COSite cosite : siteIds) {
	    ExportCallBack callB = new ExportCallBack();
	    controller.getOsylPackage(cosite.getSiteId(), callB);
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
    }

}
