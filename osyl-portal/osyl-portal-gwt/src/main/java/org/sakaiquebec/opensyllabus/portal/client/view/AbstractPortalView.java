/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.portal.client.view;

import java.util.MissingResourceException;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.portal.client.image.PortalImages;
import org.sakaiquebec.opensyllabus.portal.client.message.OsylPortalMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AbstractPortalView extends Composite {

    private String viewKey;
    
    private OsylPortalMessages messages = (OsylPortalMessages) GWT
	    .create(OsylPortalMessages.class);
    
    private PortalImages images = (PortalImages) GWT
    .create(PortalImages.class);

    
    public AbstractPortalView(String key){
	this.setViewKey(key);
	History.newItem(viewKey);
    }
    
    public String getMessage(String key) {
	try {
	    return messages.getString(key.replace(".", "_"));
	} catch (MissingResourceException e) {
	    return "Missing key: " + key;
	}
    }

    public PortalController getController() {
	return PortalController.getInstance();
    }
    
    public PortalImages getImages(){
	return images;
    }

    public void setViewKey(String viewKey) {
	this.viewKey = viewKey;
    }

    public String getViewKey() {
	return viewKey;
    }

}
