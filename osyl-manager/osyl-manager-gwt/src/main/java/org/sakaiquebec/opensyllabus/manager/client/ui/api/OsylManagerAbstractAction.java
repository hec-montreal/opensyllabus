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
package org.sakaiquebec.opensyllabus.manager.client.ui.api;

import java.util.List;
import java.util.MissingResourceException;

import org.sakaiquebec.opensyllabus.manager.client.controller.OsylManagerController;
import org.sakaiquebec.opensyllabus.manager.client.controller.event.OsylManagerEventHandler;
import org.sakaiquebec.opensyllabus.manager.client.message.Messages;
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Base class for all action objects of Osyl Manager.
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylManagerAbstractAction extends PushButton implements
	ClickHandler, OsylManagerEventHandler {

    /**
     * The Osyl Maanger controller
     */
    protected OsylManagerController controller;
    
    /**
     * The dialog box used to display feedback during operation process.
     */
    protected OsylCancelDialog diag;
    
    /**
     * The messages bundle loaded and being used by the action objects.
     */
    protected Messages messages;


    /**
     * Constructor.
     * @param controller the Osyl Manager controller
     * @param key the key used to display action label
     */
    public OsylManagerAbstractAction(OsylManagerController controller,
	    String key) {
	super();
	this.controller = controller;
	messages = this.controller.getMessages();
	diag = new OsylCancelDialog(false, true , messages
		.OsylCancelDialog_Title(), messages
		.OsylCancelDialog_Content());
	String t;
	try {
	    t = messages.getString(key);
	} catch (MissingResourceException e) {
	    t = "missing key:" + key;
	}
	this.setText(t);
	this.addClickHandler(this);
	this.setStylePrimaryName("OsylManager-action");
	this.setEnabled(false);
	controller.addEventHandler(this);
    }
        
    /** {@inheritDoc} */
    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITES_SELECTION_EVENT) {
	    setEnabled(isActionEnableForSites(controller.getSelectSites()));
	}
    }

    /** {@inheritDoc} */
    public void onClick(ClickEvent e) {
	if (isEnabled())
	    onClick(controller.getSelectSites());
    }

    /**
     * Method to be implemented in child classes.  This method uses the list of
     * site ids in order to perform actions on sites on a button click event.
     * @param siteIds
     */
    public abstract void onClick(List<COSite> siteIds);

    public abstract boolean isActionEnableForSites(List<COSite> siteIds);

}
