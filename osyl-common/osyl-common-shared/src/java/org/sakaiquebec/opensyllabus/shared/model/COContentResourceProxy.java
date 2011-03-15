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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
     * Rubric to put the resource proxy in.
     */
    private Map<String, COContentRubric> rubrics;

    private Set<UpdateCOContentResourceProxyEventHandler> updateCOContentResourceProxyEventHandlers;

    /**
     * Constructor.
     */
    public COContentResourceProxy() {
	super();
	setClassType(CO_CONTENT_RESOURCE_PROXY_CLASS_TYPE);
	nestedCOResourceProxies = new ArrayList<COContentResourceProxy>();
	rubrics = new HashMap<String, COContentRubric>();
    }

	public static COContentResourceProxy createDefaultResProxy(
			final String type, final OsylConfigMessages osylConfigMessages,
			final COElementAbstract parentModel, final String resourceType,
			String defaultRubric, String propertyType) {

		final COContentResourceProxy resProxModel = new COContentResourceProxy();
		resProxModel.setType(type);
		if (!type.equalsIgnoreCase(COContentResourceProxyType.PEOPLE)) {
			if (type.equalsIgnoreCase(COContentResourceProxyType.REFERENCE)) {
				resProxModel.addProperty(COPropertiesType.DISPLAY_AS, "link");
			}
			if (resourceType.equalsIgnoreCase(COContentResourceType.TEXT)) {
				resProxModel.setLabel(osylConfigMessages
						.getMessage("InsertYourTextHere"));
				resProxModel.setAccess(SecurityInterface.ACCESS_PUBLIC);
			} else if (resourceType.equalsIgnoreCase(COContentResourceType.URL)) {
				resProxModel.setLabel(osylConfigMessages
						.getMessage("InsertYourHyperlinkLabelHere"));
				resProxModel.setAccess(SecurityInterface.ACCESS_PUBLIC);
			} else if (resourceType
					.equalsIgnoreCase(COContentResourceType.ENTITY)) {
				resProxModel.setLabel(osylConfigMessages
						.getMessage("InsertYourSakaiEntityLabelHere"));
				resProxModel.setAccess(SecurityInterface.ACCESS_ATTENDEE);
			} else if (resourceType
					.equalsIgnoreCase(COContentResourceType.DOCUMENT)) {
				resProxModel.setLabel(osylConfigMessages
						.getMessage("InsertYourDocumentLabelHere"));
				resProxModel.setAccess(SecurityInterface.ACCESS_COMMUNITY);
			} else if (resourceType
					.equalsIgnoreCase(COContentResourceType.NEWS)) {
				resProxModel.setLabel(osylConfigMessages
						.getMessage("InsertYourTextHere"));
				resProxModel.setAccess(SecurityInterface.ACCESS_PUBLIC);
			} else if (resourceType.equalsIgnoreCase(COContentResourceType.BIBLIO_RESOURCE)) {
				resProxModel.setAccess(SecurityInterface.ACCESS_PUBLIC);
			}
		} else {	
			if (resourceType.equalsIgnoreCase(COContentResourceType.PERSON)) {
				resProxModel.setAccess(SecurityInterface.ACCESS_PUBLIC);
			}
		}
	
	resProxModel.setRubricType(defaultRubric, propertyType);

	// Default resource
	final COContentResource resModel =
		COContentResource.createDefaultRes(resourceType,
			osylConfigMessages);
	resModel.setAccess(resProxModel.getAccess());

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

    public COContentRubric getRubric(String key) {
	return rubrics.get(key);
    }

    public Map<String, COContentRubric> getRubrics() {
	return rubrics;
    }

    /**
     * @param rubric the rubric to set
     */
    public void setRubric(COContentRubric rubric) {

	if (rubric != null) {
	    rubrics.put(rubric.getKey(), rubric);
	    moveToTheBottomOfTheRubric(rubric.getKey());
	}
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.RUBRIC_UPDATE_EVENT_TYPE);
    }

    /**
     * @return the rubric type or null if no rubric
     */
    public String getRubricType(String key) {
	String rubricType = null;
	if (getRubric(key) != null) {
	    rubricType = getRubric(key).getType();
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
    public void setRubricType(String rubricType, String key) {
	if (rubricType != null) {
	    if (rubricType.equals(getRubricType(key))) {
		return;
	    }
	    COContentRubric rub = new COContentRubric();
	    rub.setType(rubricType);
	    rub.setKey(key);
	    // set the Rubric, notifyEventHandlers() will be called
	    setRubric(rub);
	} else {
	    if (null == getRubricType(key)) {
		return;
	    }
	    setRubric(null);
	}
    }

    /**
     * @return the rubric userDefLabel or null if no rubric
     */
    public String getRubricUserDefLabel(String key) {
	String rubricUserDefLabel = null;
	if (getRubric(key) != null) {
	    rubricUserDefLabel = getRubric(key).getUserDefLabel();
	}
	return rubricUserDefLabel;
    }

    /**
     * Creates and sets a customize rubric description and send and event,
     * except if it is the same as current rubric label in which case it does
     * nothing.
     * 
     * @param userDefLabel
     */
    public void setRubricUserDefLabel(String userDefLabel, String key) {
	if (userDefLabel != null) {
	    if (userDefLabel.equals(getRubricUserDefLabel(key))) {
		return;
	    }
	    getRubric(key).setUserDefLabel(userDefLabel);
	    notifyEventHandlers(UpdateCOContentResourceProxyEvent.RUBRIC_LABEL_UPDATE_EVENT_TYPE);
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
	if (!updateCOContentResourceProxyEventHandlers.contains(handler))
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

    @Override
    public void removeMeFromMyParent() {
	super.removeMeFromMyParent();
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DELETE_EVENT_TYPE);
	if (updateCOContentResourceProxyEventHandlers != null)
	    updateCOContentResourceProxyEventHandlers.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSuccessor() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	return hasSuccessor(propertyKey);
    }
    
    public boolean hasSuccessor(String propertyKey) {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this, propertyKey);
	if (i != -1 && i != 0)
	    return true;
	else
	    return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPredecessor() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	return hasPredecessor(propertyKey);
    }
    
    public boolean hasPredecessor(String propertyKey) {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this, propertyKey);
	if (i != 1 && i != 0)
	    return true;
	else
	    return false;
    }

    /**
     * {@inheritDoc}
     */
    public void moveUp() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_UP, propertyKey);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    public void moveDown() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_DOWN, propertyKey);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
    }

    public void moveToTheBottomOfTheRubric() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	moveToTheBottomOfTheRubric(propertyKey);
    }
    
    public void moveToTheBottomOfTheRubric(String propertyKey){
	while (hasSuccessor(propertyKey))
	    getParent().changeElementPosition(this,
		    COElementAbstract.POSITION_CHANGE_ACTION_DOWN, propertyKey);
    }

    public void moveToTheTopOfTheRubric() {
	String propertyKey = OsylConfigRuler.getInstance().getPropertyType();
	while (hasPredecessor(propertyKey))
	    getParent().changeElementPosition(this,
		    COElementAbstract.POSITION_CHANGE_ACTION_UP, propertyKey);
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.MOVE_IN_RUBRIC_EVENT_TYPE);
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
