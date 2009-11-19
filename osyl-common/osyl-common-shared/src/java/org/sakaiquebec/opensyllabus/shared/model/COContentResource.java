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

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.events.FiresUpdateCOContentResourceEvents;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler.UpdateCOContentResourceEvent;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;
import org.sakaiquebec.opensyllabus.shared.util.UUID;

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
     * The access level.
     */
    private String access;

    /**
     * The properties object that extends a HashMap
     */
    private COProperties properties;

    /**
     * if the resource is editable or not
     */
    private boolean editable = true;

    private String id;

    /**
     * Constructor.
     */
    public COContentResource() {
	properties = new COProperties();
    }

    /**
     * Creates a Default a COContentResource with a default content TODO: this
     * method can be a helper class for the model, so move it...
     * 
     * @param type of the new model to create
     * @return model created
     */
    public static COContentResource createDefaultRes(final String type,
	    final OsylConfigMessages osylConfigMessages) {
	final COContentResource resModel = new COContentResource();
	resModel.setType(type);
	resModel.setId(UUID.uuid());
	resModel.setAccess(SecurityInterface.ACCESS_ATTENDEE);
	if (type.equalsIgnoreCase(COContentResourceType.TEXT)) {
	    resModel.addProperty(COPropertiesType.TEXT, osylConfigMessages
		    .getMessage("InsertYourTextHere"));
	    resModel.addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.NON_APPLICABLE,
		    COPropertiesType.NON_APPLICABLE);
	} else if (type.equalsIgnoreCase(COContentResourceType.URL)) {
	    resModel.addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI,
		    "http://www.google.ca/search?q=opensyllabus");
	} else if (type.equalsIgnoreCase(COContentResourceType.DOCUMENT)) {
	    resModel.addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI, "");
	} else if (type.equalsIgnoreCase(COContentResourceType.ASSIGNMENT)) {
	    resModel.addProperty(COPropertiesType.TEXT, osylConfigMessages
		    .getMessage("assigndescr"));
	} else if (type.equalsIgnoreCase(COContentResourceType.BIBLIO_RESOURCE)) {
	    resModel.addProperty(COPropertiesType.RESOURCE_TYPE, "unknown");
	} else if (type.equalsIgnoreCase(COContentResourceType.NEWS)) {
	    resModel.addProperty(COPropertiesType.TEXT, osylConfigMessages
		    .getMessage("InsertYourTextHere"));
	    resModel.addProperty(COPropertiesType.MODIFIED, OsylDateUtils
		    .getNowDateAsXmlString());
	    resModel.addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.NON_APPLICABLE,
		    COPropertiesType.NON_APPLICABLE);
	} else if (type.equals(COContentResourceType.PERSON)) {
	    resModel.addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.NON_APPLICABLE,
		    COPropertiesType.NON_APPLICABLE);
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

    public COProperties getProperties() {
	return properties;
    }

    public void setProperties(COProperties properties) {
	this.properties = properties;
	notifyEventHandlers();
    }

    public void addProperty(String key, String value) {
	getProperties().addProperty(key, value);
	notifyEventHandlers();
    }

    public void addProperty(String key, String type, String value) {
	getProperties().addProperty(key, type, value);
	notifyEventHandlers();
    }

    public void removeProperty(String key) {
	getProperties().removeProperty(key);
	notifyEventHandlers();
    }

    public String getProperty(String key) {
	return getProperties().getProperty(key);
    }

    public String getProperty(String key, String type) {
	return getProperties().getProperty(key, type);
    }

    public void addEventHandler(UpdateCOContentResourceEventHandler handler) {
	if (updateCOContentResourceEventHandlers == null) {
	    updateCOContentResourceEventHandlers =
		    new HashSet<UpdateCOContentResourceEventHandler>();
	}
	updateCOContentResourceEventHandlers.add(handler);
    }

    public void removeEventHandler(UpdateCOContentResourceEventHandler handler) {
	if (updateCOContentResourceEventHandlers != null) {
	    updateCOContentResourceEventHandlers.remove(handler);
	}
    }

    public boolean isEditable() {
	return editable;
    }

    public void setEditable(boolean edit) {
	this.editable = edit;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * void notifyEventHandlers() { if (updateCOContentResourceEventHandlers !=
     * null) { UpdateCOContentResourceEvent event = new
     * UpdateCOContentResourceEvent(this); Iterator iter =
     * updateCOContentResourceEventHandlers.iterator(); while (iter.hasNext()) {
     * UpdateCOContentResourceEventHandler handler =
     * (UpdateCOContentResourceEventHandler) iter.next();
     * System.out.println("*** TRACE *** COContentResource - Handler: " +
     * handler.toString()); handler.onUpdateModel(event); } } }
     */

    /**
     * Notifies event handlers that it has changed.
     */
    @SuppressWarnings( { "unchecked" })
    void notifyEventHandlers() {
	Set<UpdateCOContentResourceEventHandler> copyEventHandlersList;
	if (updateCOContentResourceEventHandlers != null) {
	    UpdateCOContentResourceEvent event =
		    new UpdateCOContentResourceEvent(this);
	    synchronized (this) {
		copyEventHandlersList =
			(HashSet<UpdateCOContentResourceEventHandler>) ((HashSet<UpdateCOContentResourceEventHandler>) updateCOContentResourceEventHandlers)
				.clone();
	    }
	    Iterator<UpdateCOContentResourceEventHandler> iter =
		    copyEventHandlersList.iterator();
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

    public String getAccess() {
	return access;
    }

    public void setAccess(String access) {
	this.access = access;
	notifyEventHandlers();
    }

}
