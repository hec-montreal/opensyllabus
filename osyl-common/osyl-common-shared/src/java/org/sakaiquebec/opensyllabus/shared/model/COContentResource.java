/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.events.FiresUpdateCOContentResourceEvents;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler.UpdateCOContentResourceEvent;

/**
 * The resource object. It can be of many types like text resource or a wrapper
 * on a Sakai resource/tool.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentResource implements COModelInterface,
	FiresUpdateCOContentResourceEvents {

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;

    private Set<UpdateCOContentResourceEventHandler> updateCOContentResourceEventHandlers;

    /**
     * The type of the resource
     */
    private String type;

    /**
     * The security level.
     */
    private String security;

    /**
     * The properties object that extends a HashMap
     */
    private COProperties properties;

    /**
     * Constructor.
     */
    public COContentResource() {
	properties = new COProperties();
    }

    /**
     * Creates a Default a COContentResource with a default content 
     * TODO: this method can be a helper class for the model, so move it...
     * 
     * @param type of the new model to create
     * @return model created
     */
    public static COContentResource createDefaultRes(final String type, 
	    final OsylConfigMessages osylConfigMessages) {
	final COContentResource resModel = new COContentResource();
	resModel.setType(type);
	if (type.equalsIgnoreCase(COContentResourceProxyType.TEXT)) {
	    COProperties prop = new COProperties();
	    prop.addProperty(COPropertiesType.TEXT,
		    osylConfigMessages.getMessage("InsertYourTextHere"));
	    resModel.setProperties(prop);
	} else if (type
		.equalsIgnoreCase(COContentResourceProxyType.HYPERLINK)) {
	    COProperties prop = new COProperties();
	    prop.addProperty(COPropertiesType.URI,
	    "http://www.google.ca/search?q=opensyllabus");
	    resModel.setProperties(prop);
	} else if (type
		.equalsIgnoreCase(COContentResourceProxyType.DOCUMENT)) {
	    COProperties prop = new COProperties();
	    prop.addProperty(COPropertiesType.URI, "");
	    resModel.setProperties(prop);
	} else if (type
		.equalsIgnoreCase(COContentResourceProxyType.EHOMEWORK)) {
	    COProperties prop = new COProperties();
	    prop.addProperty(COPropertiesType.TEXT,
		    osylConfigMessages.getMessage("assigndescr"));
	    resModel.setProperties(prop);
	} else if (type
		.equalsIgnoreCase(COContentResourceProxyType.CITATION)) {
	    COProperties prop = new COProperties();
	    prop.addProperty(COPropertiesType.CITATION,
		    osylConfigMessages.getMessage("bibliographicReference"));
	    // key of citation type (which is in english) is saved in model
	    prop.addProperty(COPropertiesType.TYPE, "unknown");
	    resModel.setProperties(prop);
	}
	return resModel;
    }
    /**
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
	this.type = type;
	notifyEventHandlers();
    }

    /**
     * @return the properties
     */
    public COProperties getProperties() {
	return properties;
    }

    /**
     * @param properties the properties to set.
     */
    public void setProperties(COProperties properties) {
	this.properties = properties;
	notifyEventHandlers();
    }

    /**
     * Adds a property to the <code>COProperties</code> structure.
     * 
     * @param key the proerty key used to retrieve the value.
     * @param value the value of this property.
     */
    public void addProperty(String key, String value) {
	// If current value is defined and equal to the new we return.
	if ((null != (getProperties().getProperty(key)))
		&& (getProperties().getProperty(key).equals(value))) {
	    return;
	}
	getProperties().addProperty(key, value);
	notifyEventHandlers();
    }

    /**
     * Removes a property from the <code>COProperties</code> structure.
     * 
     * @param key the key of the property to remove.
     */
    public void removeProperty(String key) {
	getProperties().removeProperty(key);
	notifyEventHandlers();
    }

    /**
     * Returns a specific property of <code>COContentResource</code>.
     * 
     * @param key
     * @return
     */
    public String getProperty(String key) {
	return getProperties().getProperty(key);
    }

    public void addEventHandler(UpdateCOContentResourceEventHandler handler) {
	if (updateCOContentResourceEventHandlers == null) {
	    updateCOContentResourceEventHandlers = new HashSet<UpdateCOContentResourceEventHandler>();
	}
	updateCOContentResourceEventHandlers.add(handler);
    }

    public void removeEventHandler(UpdateCOContentResourceEventHandler handler) {
	if (updateCOContentResourceEventHandlers != null) {
	    updateCOContentResourceEventHandlers.remove(handler);
	}
    }

    /*    void notifyEventHandlers() {
	if (updateCOContentResourceEventHandlers != null) {
	    UpdateCOContentResourceEvent event = new UpdateCOContentResourceEvent(this);
	    Iterator iter = updateCOContentResourceEventHandlers.iterator();
	    while (iter.hasNext()) {
		UpdateCOContentResourceEventHandler handler = 
		    (UpdateCOContentResourceEventHandler) iter.next();
		System.out.println("*** TRACE *** COContentResource - Handler: " + handler.toString());
		handler.onUpdateModel(event);
	    }
	}
    }
     */

    /**
     * Notifies event handlers that it has changed.
     */
    @SuppressWarnings({"unchecked"})
    void notifyEventHandlers() {
	Set<UpdateCOContentResourceEventHandler> copyEventHandlersList;
	if (updateCOContentResourceEventHandlers != null) {
	    UpdateCOContentResourceEvent event =
		new UpdateCOContentResourceEvent(this);
	    synchronized (this) {
		copyEventHandlersList =(HashSet<UpdateCOContentResourceEventHandler>)((HashSet<UpdateCOContentResourceEventHandler>) updateCOContentResourceEventHandlers)
		.clone();
	    }
	    Iterator<UpdateCOContentResourceEventHandler> iter = copyEventHandlersList.iterator();
	    while (iter.hasNext()) {
		UpdateCOContentResourceEventHandler handler =
		    (UpdateCOContentResourceEventHandler) iter.next();
		if (TRACE)
		    System.out
		    .println("*** TRACE *** COContentResource - Handler: "
			    + handler.toString());
		handler.onUpdateModel(event);
	    }
	}
    }

    public String getSecurity() {
	return security;
    }

    public void setSecurity(String security) {
	this.security = security;
	notifyEventHandlers();
    }

}
