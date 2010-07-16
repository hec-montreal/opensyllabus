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
import org.sakaiquebec.opensyllabus.manager.client.ui.dialog.OsylCancelDialog;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylManagerAbstractAction extends PushButton implements
	ClickHandler, OsylManagerEventHandler {

    protected OsylManagerController controller;
    protected OsylCancelDialog diag;


    public OsylManagerAbstractAction(OsylManagerController controller,
	    String key) {
	super();
	diag = new OsylCancelDialog(false, true , controller.getMessages()
		.OsylCancelDialog_Title(), controller.getMessages()
		.OsylCancelDialog_Content());
	this.controller = controller;
	String t;
	try {
	    t = controller.getMessages().getString(key);
	} catch (MissingResourceException e) {
	    t = "missing key:" + key;
	}
	this.setText(t);
	this.addClickHandler(this);
	this.setStylePrimaryName("OsylManager-action");
	this.setEnabled(false);
	controller.addEventHandler(this);
    }
        
    public void onOsylManagerEvent(OsylManagerEvent e) {
	if (e.getType() == OsylManagerEvent.SITES_SELECTION_EVENT) {
	    setEnabled(isActionEnableForSites(controller.getSelectSites()));
	}

    }

    public void onClick(ClickEvent e) {
	if (isEnabled())
	    onClick(controller.getSelectSites());
    }

    public abstract void onClick(List<COSite> siteIds);

    public abstract boolean isActionEnableForSites(List<COSite> siteIds);

}
