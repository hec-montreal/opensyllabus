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

package org.sakaiquebec.opensyllabus.client.remoteservice.json;

import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.helper.FormHelper;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.callback.OsylCitationRemoteDirectoryContentCallBackAdaptator;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylCitationRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationListItem;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;

import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCitationRemoteServiceJsonImpl extends
	OsylDirectoryRemoteServiceJsonImpl implements
	OsylCitationRemoteServiceAsync {

    public OsylCitationRemoteServiceJsonImpl() {
	super();
    }

    @Override
    protected RequestCallback getRemoteDirectoryContentCallBackAdaptator(
	    final AsyncCallback<List<OsylAbstractBrowserItem>> callback) {
	return new OsylCitationRemoteDirectoryContentCallBackAdaptator(callback);
    }

    @Override
    protected void initRemoteUri() {
	this.remoteUri =
		serverId + "/sdata/ci/group/"
			+ OsylController.getInstance().getSiteId() + "/";
	this.remoteUri =
		OsylAbstractBrowserComposite.uriSlashCorrection(this.remoteUri);
    }

    public void createOrUpdateCitation(String p_relativePathFolder,
	    OsylCitationItem p_citation, final AsyncCallback<String> callback) {

	if (TRACE) {
	    Window
		    .alert("OsylCitationRemoteServiceJsonImpl.createOrUpdateCitation : "
			    + p_citation.getTitle());
	}

	// create form to submit
	final FormPanel form = new FormPanel();
	VerticalPanel panel = new VerticalPanel();
	form.add(panel);

	// determinate form action
	String action;
	String listname;
	if (p_citation.getId() != null) {
	    // update case
	    action = serverId + "/sdata/ci" + p_citation.getResourceId();

	    // create hidden field to define put(update) method
	    panel.add(FormHelper.createHiddenField("method", "put"));
	    // create hidden field to define citation id
	    panel.add(FormHelper.createHiddenField("cid", p_citation.getId()));

	    listname = p_citation.getResourceId();
	} else {
	    listname = p_citation.getTitle();
	    action = getRessourceUri(p_relativePathFolder);
	}

	form.setAction(action);
	form.setMethod(FormPanel.METHOD_POST);

	if (TRACE) {
	    Window.alert("Create an hidden textbox for each pojo property ");
	}

	// Create an hidden textbox for each pojo property.
	panel.add(FormHelper.createHiddenField("listname", listname));

	panel.add(FormHelper.createHiddenField("cipkeys", "sakai:displayname"));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getTitle()));

	panel.add(FormHelper.createHiddenField("cipkeys", "sakai:mediatype"));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.TYPE)));

	panel
		.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.TITLE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getTitle()));

	panel
		.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.TITLE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getTitle()));

	panel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.CREATOR));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.CREATOR)));

	panel.add(FormHelper.createHiddenField("cipkeys", CitationSchema.YEAR));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.YEAR)));

	panel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.SOURCE_TITLE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.SOURCE_TITLE)));

	panel.add(FormHelper.createHiddenField("cipkeys", CitationSchema.DATE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.DATE)));

	panel.add(FormHelper
		.createHiddenField("cipkeys", CitationSchema.VOLUME));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.VOLUME)));

	panel
		.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.ISSUE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.ISSUE)));

	panel
		.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.PAGES));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.PAGES)));

	panel.add(FormHelper.createHiddenField("cipkeys", CitationSchema.ISN));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.ISN)));

	panel.add(FormHelper.createHiddenField("cipkeys", CitationSchema.DOI));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.DOI)));

	// add event handler
	form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

	    public void onSubmitComplete(SubmitCompleteEvent event) {
		if (TRACE) {
		    Window.alert("call back from the server : "
			    + event.getResults());
		}
		RootPanel.detachNow(form);
		callback.onSuccess(getPath(event.getResults()));
	    }

	    private String getPath(String preJson) {
		// String st = null;
		// String json =
		// preJson.substring(preJson.indexOf('{'),preJson.lastIndexOf('}'));
		String s =
			preJson.substring(preJson.indexOf("\"path\":\"") + 8);
		s = s.substring(0, s.indexOf("\""));
		return s;
	    }

	});

	if (TRACE) {
	    Window.alert("add the form to the rootPanel ");
	}

	RootPanel.get().add(form);
	if (TRACE) {
	    Window.alert("Submit the form - createOrUpdateCitation : "
		    + form.getAction());
	}
	form.submit();
	if (TRACE) {
	    Window.alert("Form submited ");
	}

    }

    public void createOrUpdateCitationList(String pRelativePathFolder,
	    OsylCitationListItem lCitation, final AsyncCallback<Void> callback) {

	if (TRACE) {
	    Window
		    .alert("OsylCitationRemoteServiceJsonImpl.createOrUpdateCitationList : "
			    + lCitation.getFileName());
	}

	// create form to submit
	final FormPanel form = new FormPanel();
	VerticalPanel panel = new VerticalPanel();
	form.add(panel);

	// determinate form action
	String action;
	String listname;
	if (lCitation.getResourceId() != null) {
	    // update case
	    action = serverId + "/sdata/ci" + lCitation.getResourceId();

	    // create hidden field to define put(update) method
	    panel.add(FormHelper.createHiddenField("method", "put"));
	    // create hidden field to define citation id
	    panel.add(FormHelper.createHiddenField("cid", lCitation
		    .getFilePath()));
	}
	listname = lCitation.getFileName();
	action = getRessourceUri(pRelativePathFolder);
	form.setAction(action);
	form.setMethod(FormPanel.METHOD_POST);

	if (TRACE) {
	    Window.alert("Create an hidden textbox for each pojo property ");
	}

	// Create an hidden textbox for each pojo property.
	panel.add(FormHelper.createHiddenField("listname", listname));

	// add event handler
	form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

	    public void onSubmitComplete(SubmitCompleteEvent event) {
		if (TRACE) {
		    Window.alert("call back from the server : "
			    + event.getResults());
		}
		RootPanel.detachNow(form);
		callback.onSuccess(null);
	    }

	});

	if (TRACE) {
	    Window.alert("add the form to the rootPanel ");
	}

	RootPanel.get().add(form);
	if (TRACE) {
	    Window.alert("Submit the form - createOrUpdateCitation : "
		    + form.getAction());
	}
	form.submit();
	if (TRACE) {
	    Window.alert("Form submited ");
	}
    }
}
