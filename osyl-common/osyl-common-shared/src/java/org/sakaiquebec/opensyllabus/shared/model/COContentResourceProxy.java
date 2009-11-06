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
public class COContentResourceProxy extends COElementAbstract<COModelInterface>
	implements COModelInterface, FiresUpdateCOContentResourceProxyEvents,
	COElementMoveable {

    private static final long serialVersionUID = 2320739372238332151L;

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;


    /*
     * The description of a <code>COContentResourceProxy</code>.
     */
    // private String description;

    /**
     * The child object could be ASMunit or an ASMResource
     */
    private COModelInterface resource;

    /**
     * List of all resource proxies associated to this resource proxy (as
     * attachments. atgwt.typeArgs resourceProxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     */
    private List<COContentResourceProxy> nestedCOResourceProxies;

    /**
     * The rubric to put the resource proxy in.
     */
    private COContentRubric rubric;

    private Set<UpdateCOContentResourceProxyEventHandler> updateCOContentResourceProxyEventHandlers;

    /**
     * Constructor.
     */
    public COContentResourceProxy() {
	super();
	setClassType(CO_CONTENT_RESOURCE_PROXY_CLASS_TYPE);
	nestedCOResourceProxies = new ArrayList<COContentResourceProxy>();
    }

    public static COContentResourceProxy createDefaultResProxy(
	    final String type, final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract parentModel, final String resourceType,
	    String defaultRubric) {

	final COContentResourceProxy resProxModel =
		new COContentResourceProxy();
	resProxModel.setType(type);
	if (!type.equalsIgnoreCase(COContentResourceProxyType.PEOPLE)) {
	    if (resourceType.equalsIgnoreCase(COContentResourceType.ASSIGNMENT))
		// We change the default text
		resProxModel
			.setLabel(osylConfigMessages.getMessage("SendWork"));
	    else
		resProxModel.setLabel(osylConfigMessages
			.getMessage("InsertYourTextHere"));

	    resProxModel.addProperty(COPropertiesType.IMPORTANCE, "false");
	    resProxModel.addProperty(COPropertiesType.REQUIREMENT_LEVEL,
		    "undefined");
	}

	resProxModel.setAccess(SecurityInterface.ACCESS_ATTENDEE);
	resProxModel.setRubricType(defaultRubric);
	
	// Default resource
	final COContentResource resModel =
		COContentResource.createDefaultRes(resourceType,
			osylConfigMessages);

	resProxModel.setResource(resModel);
	// Add child (a model notification should fire)
	((COUnitContent) parentModel).addChild(resProxModel);
	resProxModel.setParent((COUnitContent) parentModel);

	return resProxModel;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
	super.setType(type);
	notifyEventHandlers();
    }


    /**
     * @param access the access to set
     */
    public void setAccess(String access) {
	super.setAccess(access);
	notifyEventHandlers();
    }

    /**
     * @return the resource
     */
    public COModelInterface getResource() {
	return resource;
    }

    /**
     * @param resource the resource to set
     */
    public void setResource(COModelInterface resource) {
	this.resource = resource;
	notifyEventHandlers();
    }

    /**
     * atgwt.typeArgs
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     * 
     * @return ResourceProxies children
     */
    public List<COContentResourceProxy> getNestedCOContentResourceProxies() {
	return nestedCOResourceProxies;
    }

    /**
     * atgwt.typeArgs resourcePRoxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     * 
     * @param resourcePRoxies the resourceProxies to set
     */
    public void setNestedCOContentResourceProxies(
	    List<COContentResourceProxy> resourceProxies) {
	this.nestedCOResourceProxies = resourceProxies;
	notifyEventHandlers();
    }

    public COUnitContent getParent() {
	return (COUnitContent) super.getParent();
    }

    public void setParent(COUnitContent coContentUnitParent) {
	super.setParent(coContentUnitParent);
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
	if (rubric != null)
	    moveToTheBottomOfTheRubric();
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
     * Adds a resourceProxy to the list of resourceProxies.
     * 
     * @param resourceProxy the resourcePRoxy to add.
     * @return true if the resourcePRoxy is added successfully, false if not.
     */
    public boolean addNestedResourceProxy(COContentResourceProxy resourceProxy) {
	boolean res = getNestedCOContentResourceProxies().add(resourceProxy);
	notifyEventHandlers();
	return res;
    }

    /**
     * Removes a resourceProxy from the list of resourceProxies.
     * 
     * @param resourceProxy the resourceProxy to remove.
     * @return true if the resourceProxy is removed successfully, false if not.
     */
    public boolean removeNestedResourceProxy(
	    COContentResourceProxy resourceProxy) {
	boolean res = getNestedCOContentResourceProxies().remove(resourceProxy);
	notifyEventHandlers();
	return res;
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
	getParent().removeChild(this);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DELETE_EVENT_TYPE);
	if (updateCOContentResourceProxyEventHandlers != null)
	    updateCOContentResourceProxyEventHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSuccessor() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
	if (i != -1 && i != 0)
	    return true;
	else
	    return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPredecessor() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
	if (i != 1 && i != 0)
	    return true;
	else
	    return false;
    }

    /**
     * {@inheritDoc}
     */
    public void moveUp() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_UP);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    public void moveDown() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_DOWN);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }

    private void moveToTheBottomOfTheRubric() {
	while (hasSuccessor())
	    getParent().changeElementPosition(this,
		    COElementAbstract.POSITION_CHANGE_ACTION_DOWN);
    }

    @Override
    public boolean addChild(COModelInterface child) {
	this.resource = child;
	return true;
    }

    @Override
    public void changeElementPosition(COModelInterface coEltAbs, int action) {
	// nothing to do: only one child at a time
    }

    @Override
    public List<COModelInterface> getChildrens() {
	List<COModelInterface> list = new ArrayList<COModelInterface>();
	list.add(resource);
	return list;
    }

    @Override
    public int getElementPosition(COModelInterface coEltAbs) {
	return 0;
    }

    @Override
    public boolean removeChild(COModelInterface child) {
	if (child.equals(resource)) {
	    resource = null;
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public void setChildrens(List<COModelInterface> childs) {
	resource = childs.get(0);// one child at a time
    }

}
