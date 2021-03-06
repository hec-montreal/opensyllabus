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

import org.sakaiquebec.opensyllabus.client.helper.FormHelper;
import org.sakaiquebec.opensyllabus.client.remoteservice.json.callback.OsylCitationRemoteDirectoryContentCallBackAdaptator;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylCitationRemoteServiceAsync;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylAbstractBrowserComposite;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylCitationItem;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
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

    /**
     * Value used to map the citation IDENTIFIER_TYPE_URL to preferredUrl of
     * SDATA
     */
    private static final String PREFERRED_URL = "preferredUrl";
    private static final String PREFERRED_URL_LABEL = "sakai:url_label";
    private static final String BOOKSTORE_URL = "bookstoreUrl";
//  private static final String RESOURCE_TYPE = "asmResourceType";    
    private static final String NO_URL = "noUrl";
    private static final String URL="url";

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
		serverId + "/sdata/ci";
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
	    action = remoteUri + p_citation.getFilePath();

	    // create hidden field to define put(update) method
	    panel.add(FormHelper.createHiddenField("_method", "put"));
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

	panel.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.TITLE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getTitle()));
	
	//Type of resource
	panel.add(FormHelper.createHiddenField("cipkeys",
			CitationSchema.CITATION_RESOURCE_TYPE));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation.getResourceType()));

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

	panel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.PUBLISHER));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.PUBLISHER)));

	panel.add(FormHelper.createHiddenField("cipkeys",
		CitationSchema.PUBLICATION_LOCATION));
	panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		.getProperty(CitationSchema.PUBLICATION_LOCATION)));

	if (p_citation.getProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_OTHERLINK) != null) {
	    panel.add(FormHelper.createHiddenField("cipkeys", PREFERRED_URL_LABEL));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getCOProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK)
			    .getAttribute(
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK_LABEL)));
	    
	    panel.add(FormHelper.createHiddenField("cipkeys", PREFERRED_URL));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_OTHERLINK)));
	}
	
	if (p_citation.getProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_LIBRARY) != null) {
	    panel.add(FormHelper.createHiddenField("cipkeys",
		    URL));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_LIBRARY)));
	}
	
	if (p_citation.getProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE) != null) {
	    panel.add(FormHelper.createHiddenField("cipkeys",
		    BOOKSTORE_URL));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_BOOKSTORE)));
	}
/*
	if (p_citation.getProperty(COPropertiesType.ASM_RESOURCE_TYPE,
		COPropertiesType.ASM_RESOURCE_TYPE) != null) {
	    panel.add(FormHelper.createHiddenField("cipkeys",
		    RESOURCE_TYPE));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getProperty(COPropertiesType.ASM_RESOURCE_TYPE,
			    COPropertiesType.ASM_RESOURCE_TYPE)));
	}
*/	
	if (p_citation.getProperty(COPropertiesType.IDENTIFIER,
		COPropertiesType.IDENTIFIER_TYPE_NOLINK) != null) {
	    panel.add(FormHelper.createHiddenField("cipkeys", NO_URL));
	    panel.add(FormHelper.createHiddenField("cipvalues", p_citation
		    .getProperty(COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_NOLINK)));
	}
	
	// add event handler
	form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

	    public void onSubmitComplete(SubmitCompleteEvent event) {
		if (TRACE) {
		    Window.alert("call back from the server : "
			    + event.getResults());
		}
		RootPanel.get().remove(form);
		callback.onSuccess(getPath(event.getResults()));
	    }

	    private String getPath(String preJson) {
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
}
