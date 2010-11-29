/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylEntityBrowser extends OsylAbstractBrowserComposite implements
	SelectionHandler<TreeItem> {

    ScrollPanel providersPanel = new ScrollPanel();

    private String entityUri;

    private String entityText;

    private Tree providers;

    HTML currentSelectionHtml;

    private SakaiEntities sakaiEntities;

    private AsyncCallback<SakaiEntities> sitesEntitiesCallback =
	    new AsyncCallback<SakaiEntities>() {
		public void onSuccess(SakaiEntities sitesEntites) {
		    try {
			setExistingEntities(sitesEntites);
			refreshSitesEntitiesListing(getEntityUri());
		    } catch (Exception error) {
			Window
				.alert("Error - Unable to getExistingEntities(...) on RPC Success: "
					+ error.toString());
		    }
		}

		// And we define the behavior in case of failure
		public void onFailure(Throwable error) {
		    System.out
			    .println("RPC FAILURE - getExistingEntities(...): "
				    + error.toString()
				    + " Hint: Check GWT version");
		    Window.alert("RPC FAILURE - getExistingEntities(...): "
			    + error.toString() + " Hint: Check GWT version");
		}
	    };

    public OsylEntityBrowser() {
	super();
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylEntityBrowser(String entity) {
	super();
	setEntityUri(entity);
	getController().getExistingEntities(getController().getSiteId(),
		getSitesEntitiesCallback());

    }

    @Override
    protected String getCurrentSelectionLabel() {
	return getController().getUiMessage("Browser.selected_entity");
    }

    /**
     * Initialize and renders the view of the <code>OsylRemoteFileBrowser</code>
     * .
     */

    public void initView() {

	// Browser Main Panel
	VerticalPanel mainContentPanel = new VerticalPanel();

	// All composites must call initWidget() in their constructors.
	initWidget(mainContentPanel);

	// First row sub-panel
	HorizontalPanel firstRowPanel = new HorizontalPanel();
	mainContentPanel.add(firstRowPanel);
	firstRowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

	// 1st row, 1st widget: Folder name label
	Label sitesPanelLabel =
		new Label(getController().getUiMessage(
			"UtilityRemoteEntityBrowser_SitesPanelLabel"));
	sitesPanelLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	sitesPanelLabel.setWidth("130px");
	firstRowPanel.add(sitesPanelLabel);

	// 1st row, 2nd widget: Sites ListBox
	TextBox currentSite = new TextBox();
	currentSite.setText(getController().getSiteId());
	setCurrentSiteTextBox(currentSite);
	getCurrentSiteTextBox().setReadOnly(true);
	getCurrentSiteTextBox().setWidth("315px");

	firstRowPanel.add(getCurrentSiteTextBox());

	setProvidersPanel(new ScrollPanel());
	// TODO: change tree for something else one site can have entities with
	// same name;
	setProviders(new Tree());

	getProvidersPanel().setWidth("600px");
	getProvidersPanel().setHeight("70px");
	getProvidersPanel().addStyleName(
		"Osyl-RemoteEntityBrowser-EntityListing");
	getProvidersPanel().add(getProviders());
	mainContentPanel.add(getProvidersPanel());

	// 3rd row: File field (first a label then a TextBox)
	// /////////////////////////////////////////////////
	HorizontalPanel entitySelectionSubPanel = new HorizontalPanel();
	mainContentPanel.add(entitySelectionSubPanel);

	getProviders().addSelectionHandler(this);

	// The Label
	HTML currentSelectionLabel =
		new HTML(getCurrentSelectionLabel()
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	currentSelectionLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	currentSelectionLabel.setWidth("130px");
	entitySelectionSubPanel.add(currentSelectionLabel);

	setCurrentSelectionHtml(new HTML(""));
	getCurrentSelectionHtml().addStyleName(
		"Osyl-RemoteEntityBrowser-EntityListing");
	getCurrentSelectionHtml().setWidth("315px");
	entitySelectionSubPanel.add(getCurrentSelectionHtml());
	// Give the overall composite a style name
	this.setStylePrimaryName("Osyl-RemoteFileBrowser");

    }

    @Override
    protected void onFileDoubleClicking() {
	// Nothing to do
    }

    private String getRawURI(String uri) {
	// We get the URI from the model
	String link = "";

	// Otherwise we have to prepend Sakai stuff in the URI
	String url = GWT.getModuleBaseURL();
	String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	link = serverId + "/direct" + uri;

	return link;

    }

    public ScrollPanel getProvidersPanel() {
	return providersPanel;
    }

    public void setProvidersPanel(ScrollPanel providersPanel) {
	this.providersPanel = providersPanel;
    }

    @Override
    public void getRemoteDirectoryListing(String directoryPath) {
	// Nothing to do
    }

    @Override
    protected PushButton createEditButton() {
	return null;
    }

    @Override
    protected PushButton createAddPushButton() {
	return null;
    }

    public AsyncCallback<SakaiEntities> getSitesEntitiesCallback() {
	return sitesEntitiesCallback;
    }

    public void setSitesEntitiesCallback(
	    AsyncCallback<SakaiEntities> sitesEntitiesCallback) {
	this.sitesEntitiesCallback = sitesEntitiesCallback;
    }

    public void refreshSitesEntitiesListing(String selectedEntity) {
	getProviders().clear();

	int countProviders = 0;
	SakaiEntities sakaiEntities =
		getExistingEntities(getController().getSiteId());

	Map<String, String> entities = sakaiEntities.getEntities();
	Map<String, String> allowedProviders = sakaiEntities.getProviders();

	TreeItem providers;
	Set<String> entitiesKeys = entities.keySet();
	TreeItem selectedItem = null;
	for (Entry<String, String> entry : allowedProviders.entrySet()) {
	    String providerKey = entry.getKey();
	    providers =
		    new TreeItem(getController().getUiMessage(
			    "SakaiEntityEditor.Entity." + entry.getValue())
			    .toUpperCase());
	    for (String key : entitiesKeys) {
		if (key.contains(providerKey)) {
		    TreeItem tItem = providers.addItem(entities.get(key));
		    tItem.addStyleName("Osyl-RemoteEntityBrowser-Entity");
		    if (selectedEntity != null && selectedEntity.contains(key)) {
			providers.setState(true, true);
			selectedItem = tItem;
			updateCurrentSelectionHtml(getLinkURI(selectedEntity,
				entities.get(key)));
		    }
		    countProviders++;
		}
	    }
	    if (providers.getChildCount() > 0) {
		getProviders().addItem(providers);

	    }
	}
	if (countProviders == 0) {
	    providers =
		    new TreeItem(getController().getUiMessage(
			    "SakaiEntityEditor.noEntity"));
	    getProviders().addItem(providers);
	}
	if (selectedItem != null)
	    getProviders().setSelectedItem(selectedItem);
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
	String entityRawUri = "";
	TreeItem item = event.getSelectedItem();
	// We have an entity if we have no child
	if (item.getChildCount() == 0) {
	    String selectedSite = getController().getSiteId();

	    SakaiEntities sakaiEntities = getExistingEntities(selectedSite);
	    Map<String, String> entities = sakaiEntities.getEntities();

	    for (Entry<String, String> entry : entities.entrySet()) {
		String key = entry.getKey();
		String entity = entry.getValue();
		if (entity.equals(item.getText())) {
		    entityRawUri = getLinkURI(key, entity);
		    setEntityUri(getRawURI(key));
		    setEntityText(entity);
		    break;
		}
	    }
	}
	// TODO: changer le contenu du texte cliquable quand on choisit
	// une entite
	updateCurrentSelectionHtml(entityRawUri);
    }

    protected void updateCurrentSelectionHtml(String entity) {
	getCurrentSelectionHtml().setHTML(entity);
    }

    public HTML getCurrentSelectionHtml() {
	return currentSelectionHtml;
    }

    public void setCurrentSelectionHtml(HTML currentSelectionHtml) {
	this.currentSelectionHtml = currentSelectionHtml;
    }

    public String getEntityUri() {
	return entityUri;
    }

    public void setEntityUri(String entityUri) {
	this.entityUri = entityUri;
    }

    public String getEntityText() {
	return entityText;
    }

    public void setEntityText(String entityText) {
	this.entityText = entityText;
    }

    public Tree getProviders() {
	return providers;
    }

    public void setProviders(Tree providers) {
	this.providers = providers;
    }

    public SakaiEntities getExistingEntities(String siteId) {
	return sakaiEntities;
    }

    public void setExistingEntities(SakaiEntities sakaiEntities) {
	this.sakaiEntities = sakaiEntities;
    }

}
