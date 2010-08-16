/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ClosePushButtonEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewable;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COModeled;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.OsylTestXsl;
import org.sakaiquebec.opensyllabus.shared.util.BrowserUtil;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylPreviewView extends OsylMainView implements
	ClosePushButtonEventHandler {

    private String access;

    private OsylViewable previousMainView;

    private COSerialized coSerializedForGroup;

    private OsylEditorEntryPoint entryPoint =
	    OsylEditorEntryPoint.getInstance();

    public OsylPreviewView(String access, OsylController controller) {

	super(null, controller);
	this.access = access;

	entryPoint.prepareModelForSave();
	coSerializedForGroup =
		new COSerialized(entryPoint.getUpdatedSerializedCourseOutline());

	if (getController().isInHostedMode()) {
	    String xsl = "";
	    if (access.equals(SecurityInterface.ACCESS_PUBLIC)) {
		xsl = OsylTestXsl.XSL_PUBLIC;
	    } else if (access.equals(SecurityInterface.ACCESS_ONSITE)) {
		xsl = OsylTestXsl.XSL_ONSITE;
	    } else {
		xsl = OsylTestXsl.XSL_ATTENDEE;
	    }
	    initModelWithXsl(xsl);
	} else {
	    // For ie (version 7 and 8 at least), xsl transformation client-side
	    // is too long, we make it server-side
	    if (BrowserUtil.getBrowserType().equals("ie6")) {
		AsyncCallback<String> asyncallback =
			new AsyncCallback<String>() {

			    public void onSuccess(String xml) {
				initViewWithXmlModel(xml);
			    }

			    public void onFailure(Throwable error) {
				System.out
					.println("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
				Window
					.alert("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
			    }

			};
		getController()
			.transformXmlForGroup(
				coSerializedForGroup.getContent(), access,
				asyncallback);
	    } else {
		AsyncCallback<String> asyncallback =
			new AsyncCallback<String>() {

			    public void onSuccess(String xsl) {
				initModelWithXsl(xsl);
			    }

			    public void onFailure(Throwable error) {
				System.out
					.println("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
				Window
					.alert("RPC FAILURE - getXslForGroup(...) : "
						+ error.toString());
			    }

			};
		getController().getXslForGroup(access, asyncallback);
	    }
	}
    }

    private void initModelWithXsl(String xsl) {
	initViewWithXmlModel(getController().xslTransform(
		coSerializedForGroup.getContent(), xsl));
    }

    private void initViewWithXmlModel(String xml) {
	coSerializedForGroup.setContent(xml);
	COModeled model = new COModeled(coSerializedForGroup);
	model.XML2Model();
	setModel(model.getModeledContent());
	initView();
    }

    public void initView() {
	previousMainView = entryPoint.getView();
	getController().setInPreview(true);
	getController().setReadOnly(true);

	super.initView();
	
	getOsylToolbarView().addEventHandler(this);
	
	entryPoint.setView(this);
	entryPoint.refreshView();
	resize();
	getController().getViewContext().setContextModel(
		findStartingViewContext());
    }

    public String getAccess() {
	return access;
    }

    public void setAccess(String access) {
	this.access = access;
    }

    public void onClosePushButton(ClosePushButtonEvent event) {
	getController().setReadOnly(false);
	getController().setInPreview(false);
	entryPoint.setView(previousMainView);
	entryPoint.refreshView();
	getController().setMainView((OsylMainView) previousMainView);
	getController().getViewContext().setContextModel(findStartingViewContext());
	((OsylMainView) previousMainView).refreshView();
    }
}
