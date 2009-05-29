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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.events.FiresUpdateCOContentResourceProxyEvents;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler.UpdateCOContentResourceProxyEvent;

/**
 * Represents the resource proxies of the model. Each resource can have many
 * resources in attachment.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentResourceProxy implements COModelInterface,
	FiresUpdateCOContentResourceProxyEvents {

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;

    /**
     * The type of <code>COContentResourceProxy</code>.
     */
    private String type;

    /**
     * A comment associated to a <code>COContentResourceProxy</code>.
     */
    private String comment;

    /*
     * The description of a <code>COContentResourceProxy</code>.
     */
    // private String description;
    /**
     * The label of a <code>COContentResourceProxy</code>.
     */
    private String label;

    /**
     * The security level.
     */
    private String security;

    /**
     * The resource object.
     */
    private COContentResource resource;
    
    /**
     * if the <code>COContentResourceProxy</code> is editbale or not
     */
    private boolean editable=true;

    /**
     * List of all resource proxies associated to this resource proxy (as
     * attachments. atgwt.typeArgs resourceProxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     */
    private List<COContentResourceProxy> resourceProxies;

    private COContentUnit coContentUnitParent;

    /**
     * The rubric to put the resource proxy in.
     */
    private COContentRubric rubric;

    /**
     * Properties object that extends a <code>HashMap</code>.
     */
    private COProperties properties;

    private Set<UpdateCOContentResourceProxyEventHandler> updateCOContentResourceProxyEventHandlers;

    /**
     * Constructor.
     */
    public COContentResourceProxy() {
	resourceProxies = new ArrayList<COContentResourceProxy>();
	properties = new COProperties();
    }

    public static COContentResourceProxy createDefaultResProxy(
	    final String type, final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract parentModel) {

	final COContentResourceProxy resProxModel =
		new COContentResourceProxy();
	resProxModel.setType(type);
	if (type.equalsIgnoreCase(COContentResourceProxyType.ASSIGNMENT))
	    // We change the default text
	    resProxModel.setLabel(osylConfigMessages.getMessage("SendWork"));
	else
	    resProxModel.setLabel(osylConfigMessages
		    .getMessage("InsertYourTextHere"));
	resProxModel.setSecurity(SecurityInterface.SECURITY_ACCESS_PUBLIC);
	resProxModel.setRubricType(COContentRubricTypeList.UNDEFINED);
	COProperties prop = new COProperties();
	prop.addProperty(COPropertiesType.VISIBILITY, "true");
	prop.addProperty(COPropertiesType.IMPORTANCE, "false");
	prop.addProperty(COPropertiesType.REQUIREMENT_LEVEL, "undefined");

	// Default resource
	final COContentResource resModel =
		COContentResource.createDefaultRes(type, osylConfigMessages);

	resProxModel.setResource(resModel);
	// Add child (a model notification should fire)
	((COContentUnit) parentModel).addResourceProxy(resProxModel);
	resProxModel.setCoContentUnitParent((COContentUnit) parentModel);

	return resProxModel;
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
     * @return the comment
     */
    public String getComment() {
	return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
	this.comment = comment;
	notifyEventHandlers();
    }

    /*
     * @return the description
     */
    // public String getDescription() {
    // return description;
    // }
    /*
     * @param description the description to set
     */
    // public void setDescription(String description) {
    // this.description = description;
    // notifyEventHandlers();
    // }
    /**
     * @return the security
     */
    public String getSecurity() {
	return security;
    }

    /**
     * @param security the security to set
     */
    public void setSecurity(String security) {
	this.security = security;
	notifyEventHandlers();
    }

    /**
     * @return the resource
     */
    public COContentResource getResource() {
	return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(COContentResource resource) {
	this.resource = resource;
	notifyEventHandlers();
    }

    /**
     * atgwt.typeArgs
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     * 
     * @return ResourceProxies children
     */
    public List<COContentResourceProxy> getResourceProxies() {
	return resourceProxies;
    }

    /**
     * atgwt.typeArgs resourcePRoxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     * 
     * @param resourcePRoxies the resourceProxies to set
     */
    public void setResourceProxies(List<COContentResourceProxy> resourceProxies) {
	this.resourceProxies = resourceProxies;
	notifyEventHandlers();
    }

    public COContentUnit getCoContentUnitParent() {
	return coContentUnitParent;
    }

    public void setCoContentUnitParent(COContentUnit coContentUnitParent) {
	this.coContentUnitParent = coContentUnitParent;
    }

    /**
     * @return the rubric
     */
    public COContentRubric getRubric() {
	return rubric;
    }

    /**
     * @param rubric the rubric to set
     */
    public void setRubric(COContentRubric rubric) {
	this.rubric = rubric;
	if(rubric!=null)
	    moveToTheBottomOftheRubric();
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.RUBRIC_UPDATE_EVENT_TYPE);
    }

    /**
     * @return the rubric type or null if no rubric
     */
    public String getRubricType() {
	String rubricType = null;
	if (getRubric() != null) {
	    rubricType = getRubric().getType();
	}
	return rubricType;
    }

    /**
     * Creates and sets a rubric with the specified rubricType for this resource
     * proxy, except if it is the same as current rubric type in which case it
     * does nothing.
     * 
     * @param rubricType
     */
    public void setRubricType(String rubricType) {
	if (rubricType != null) {
	    if (rubricType.equals(getRubricType())) {
		return;
	    }
	    COContentRubric rub = new COContentRubric();
	    rub.setType(rubricType);
	    // set the Rubric, notifyEventHandlers() will be called
	    setRubric(rub);
	} else {
	    if (null == getRubricType()) {
		return;
	    }
	    setRubric(null);
	}
    }

    /**
     * @return the properties.
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
     * Adds a resourceProxy to the list of resourceProxies.
     * 
     * @param resourceProxy the resourcePRoxy to add.
     * @return true if the resourcePRoxy is added successfully, false if not.
     */
    public boolean addResourceProxy(COContentResourceProxy resourceProxy) {
	boolean res = getResourceProxies().add(resourceProxy);
	notifyEventHandlers();
	return res;
    }

    /**
     * Removes a resourceProxy from the list of resourceProxies.
     * 
     * @param resourceProxy the resourceProxy to remove.
     * @return true if the resourceProxy is removed successfully, false if not.
     */
    public boolean removeResourceProxy(COContentResourceProxy resourceProxy) {
	boolean res = getResourceProxies().remove(resourceProxy);
	notifyEventHandlers();
	return res;
    }

    /**
     * Adds a property to the <code>COProperties</code> structure.
     * 
     * @param key the eky used to retrieve the property value.
     * @param value the property value.
     */
    public void addProperty(String key, String value) {

	getProperties().addProperty(key, value);
	if (TRACE)
	    System.out
		    .println("*** TRACE *** UPDATE THE MODEL COContentResourceProxy "
			    + key + " = " + value);
	notifyEventHandlers();
    }

    /**
     * Removes a property from the <code>COProperties</code> structure.
     * 
     * @param key
     */
    public void removeProperty(String key) {
	getProperties().removeProperty(key);
	notifyEventHandlers();
    }

    /**
     * Gets the specified proeprty value.
     * 
     * @param key the property key used to retrieve the value.
     * @return the proeprty value.
     */
    public String getProperty(String key) {
	return getProperties().getProperty(key);
    }

    /**
     * Finds and returns a specific <code>COContentResourceProxy</code> from the
     * list.
     * 
     * @param resourceProxyId the id of the resourceProxy to find and return.
     * @return the <code>COContentResourceProxy</code> if it is found, null if
     *         not. public COContentResourceProxy getResourceProxyWithId(String
     *         resourceProxyId) { boolean isFound = false;
     *         COContentResourceProxy resourceProxy = null; Iterator
     *         resourceProxiesIter = getResourceProxies().iterator(); while
     *         (resourceProxiesIter.hasNext() && !isFound) {
     *         COContentResourceProxy thisResourceProxy =
     *         (COContentResourceProxy) resourceProxiesIter .next(); if
     *         (thisResourceProxy.getId().equals(resourceProxyId)) {
     *         resourceProxy = thisResourceProxy; isFound = true; } } return
     *         resourceProxy; }
     */

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
	if (TRACE)
	    System.out
		    .println("*** TRACE *** UPDATE THE MODEL COContentResourceProxy - Label = "
			    + label);
	notifyEventHandlers();
    }

    /** {@inheritDoc} */
    public void addEventHandler(UpdateCOContentResourceProxyEventHandler handler) {
	if (updateCOContentResourceProxyEventHandlers == null) {
	    updateCOContentResourceProxyEventHandlers =
		    new HashSet<UpdateCOContentResourceProxyEventHandler>();
	}
	updateCOContentResourceProxyEventHandlers.add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(
	    UpdateCOContentResourceProxyEventHandler handler) {
	if (updateCOContentResourceProxyEventHandlers != null) {
	    updateCOContentResourceProxyEventHandlers.remove(handler);
	}
    }

    /**
     * Notifies event handlers that it has changed.
     */
    void notifyEventHandlers() {
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DEFAULT_EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @SuppressWarnings( { "unchecked" })
    void notifyEventHandlers(int eventType) {
	Set<UpdateCOContentResourceProxyEventHandler> copyEventHandlersList;
	if (updateCOContentResourceProxyEventHandlers != null) {
	    UpdateCOContentResourceProxyEvent event =
		    new UpdateCOContentResourceProxyEvent(this);
	    event.setEventType(eventType);
	    synchronized (this) {
		copyEventHandlersList =
			(Set<UpdateCOContentResourceProxyEventHandler>) ((HashSet<UpdateCOContentResourceProxyEventHandler>) updateCOContentResourceProxyEventHandlers)
				.clone();
	    }
	    Iterator<UpdateCOContentResourceProxyEventHandler> iter =
		    copyEventHandlersList.iterator();
	    while (iter.hasNext()) {
		UpdateCOContentResourceProxyEventHandler handler =
			(UpdateCOContentResourceProxyEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    /**
     * Removes itself from its parent.
     */
    public void remove() {
	getCoContentUnitParent().removeResourceProxy(this);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DELETE_EVENT_TYPE);
	if (updateCOContentResourceProxyEventHandlers != null)
	    updateCOContentResourceProxyEventHandlers.clear();
    }

    /**
     * Check the position of this resourceProxy compared to the other rp in the
     * same rubric
     * 
     * @return true if the ResourceProxy is not the last element
     */
    public boolean hasSuccessorInRubric() {
	if (getCoContentUnitParent() == null)
	    return false;
	int i = getCoContentUnitParent().getResourceProxyPositionInRubric(this);
	if (i != -1 && i != 0)
	    return true;
	else
	    return false;
    }

    /**
     * Check the position of this resourceProxy compared to the other rp in the
     * same rubric
     * 
     * @return true if the ResourceProxy is not the first element
     */
    public boolean hasPredecessorInRubric() {
	if (getCoContentUnitParent() == null)
	    return false;
	int i = getCoContentUnitParent().getResourceProxyPositionInRubric(this);
	if (i != 1 && i != 0)
	    return true;
	else
	    return false;
    }

    public void moveUp() {
	getCoContentUnitParent().changeResourceProxyPositionInRubric(this, -1);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }

    public void moveDown() {
	getCoContentUnitParent().changeResourceProxyPositionInRubric(this, 1);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }
    
    private void moveToTheBottomOftheRubric(){
	while(hasSuccessorInRubric())
	    getCoContentUnitParent().changeResourceProxyPositionInRubric(this, 1); 
    }
    
    public boolean isEditable() {
	return editable;
    }

    public void setEditable(boolean edit) {
	this.editable = edit;
    }   

}
