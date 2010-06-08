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

import java.io.ObjectInputStream.GetField;
import java.util.Map;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.remoteservice.OsylRemoteServiceLocator;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylFileItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
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
public class OsylEntityBrowser extends OsylAbstractBrowserComposite {

    ScrollPanel providersPanel = new ScrollPanel();

    public OsylEntityBrowser() {
	super();
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylEntityBrowser(String entity, boolean isEntity) {
	super(entity, isEntity);

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

	Tree providers = new Tree();
	setProviders(providers);
	getProvidersPanel().addStyleName("Osyl-RemoteEntityBrowser-EntityListing");
	getProvidersPanel().add(getProviders());
	mainContentPanel.add(getProvidersPanel());

	getController().getExistingEntities(getController().getSiteId(), getSitesEntitiesCallback());
	
	// 3rd row: File field (first a label then a TextBox)
	// /////////////////////////////////////////////////
	HorizontalPanel entitySelectionSubPanel = new HorizontalPanel();
	mainContentPanel.add(entitySelectionSubPanel);

	getProviders().addSelectionHandler(new SelectionHandler<TreeItem>() {

	    public void onSelection(SelectionEvent<TreeItem> event) {

		String entityRawUri = "";
		TreeItem item = event.getSelectedItem();
		// We have an entity if we have no child
		if (item.getChildCount() == 0) {
		    String selectedSite = getController().getSiteId();
		    
		    SakaiEntities sakaiEntities = getController().getExistingEntities(selectedSite); 
		    Map<String, String> entities = sakaiEntities.getEntities();
			    
		    Set<String> entitiesKeys = entities.keySet();
		    String entity;
		    for (String key : entitiesKeys) {
			entity = entities.get(key);
			if (entity.equals(item.getText())) {
			    entityRawUri = getLinkURI(key, entity);
			    setEntityUri(getRawURI(key));
			    setEntityText(entity);
			    break;
			}
		    }
		}
		//TODO: changer le contenu du texte cliquable quand on choisit
		//une entite
		updateCurrentSelectionHtml(entityRawUri);

	    }

	});

	// The Label
	HTML currentSelectionLabel =
		new HTML(getCurrentSelectionLabel()
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	currentSelectionLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	currentSelectionLabel.setWidth("130px");
	entitySelectionSubPanel.add(currentSelectionLabel);
	
	setCurrentSelectionHtml(new HTML());
	getCurrentSelectionHtml().addStyleName("Osyl-RemoteEntityBrowser-EntityListing");
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
	String link ="";

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

    public void onUploadFile(UploadFileEvent event) {
	setItemToSelect(new OsylFileItem(event.getSource().toString()));
	((OsylFileItem) getItemToSelect()).setFileName(event.getSource()
		.toString().substring(
			event.getSource().toString().lastIndexOf("/"),
			event.getSource().toString().length()));

    }

    @Override
    protected PushButton createAddPushButton() {
	return null;
    }

}
