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
import org.sakaiquebec.opensyllabus.shared.events.FiresUpdateCOUnitContentEvents;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler.UpdateCOContentResourceProxyEvent;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler.UpdateCOUnitContentEvent;

/**
 * Content unit of the <code>COStructureElement</code>. One
 * <code>COStructureElement</code> can have many <code>COUnitContent</code>
 * children but one <code>COUnitContent</code> can have one and only one
 * <code>COStructureElement</code> parent.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COUnitContent extends COElementAbstract<COContentResourceProxy>
	implements COModelInterface, FiresUpdateCOUnitContentEvents {

    private static final long serialVersionUID = -107534851461017968L;

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;

    private Set<UpdateCOUnitContentEventHandler> updateCOUnitContentEventHandler;

    /**
     * List of <code>ResourceProxy</code> that are contained in a
     * <code>COContentUnit</code>. atgwt.typeArgs resourceProxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     */
    private List<COContentResourceProxy> resourceProxies;

    /**
     * Constructor. The class type is set at creation.
     */
    public COUnitContent() {
	super();
	setAccess(SecurityInterface.ACCESS_PUBLIC);
	setClassType(ASM_UNIT_CONTENT_CLASS_TYPE);
	resourceProxies = new ArrayList<COContentResourceProxy>();
    }

    /**
     * Creates a Default a COContenUnit with a default content ¸ This method is
     * an helper class for the model
     * 
     * @param type of the new model to create
     * @param osylConfigMessages, i.e. all the course outline messages
     * @param parentModel, i.e. the parent model
     * @return model created
     */
    public static COUnitContent createDefaultCOContentUnit(final String type,
	    final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract<COElementAbstract> parentModel) {
	final COUnitContent unitModel = new COUnitContent();
	unitModel.setType(type);
	unitModel.setParent(parentModel);

	// Add child (a model notification should fire)
	parentModel.addChild(unitModel);

	return unitModel;
    }

    /**
     * atgwt.typeArgs
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     * 
     * @return the resourceProxies
     */
    public List<COContentResourceProxy> getChildrens() {
	return resourceProxies;
    }

    /**
     * {@inheritDoc}
     */
    public void setChildrens(List<COContentResourceProxy> resourceProxies) {
	this.resourceProxies = resourceProxies;
	notifyEventHandlers();
    }

    /**
     * Adds a <code>COContentResourceProxy</code> to the list of resource
     * proxies.
     * 
     * @param resourceProxy the resourceProxy to add.
     * @return true if the resourcePRoxy is added successfully, false if not.
     */
    public boolean addChild(COContentResourceProxy resourceProxy) {
	if (resourceProxy != null) {
	    boolean res = getChildrens().add(resourceProxy);
	    notifyEventHandlers(UpdateCOUnitContentEvent.ADD_RESSOURCE_PROXY_EVENT_TYPE);
	    return res;
	}
	return true;
    }

    /**
     * Removes a <code>COContentResourceProxy</code> from the list of resource
     * proxies.
     * 
     * @param resourceProxy the resourceProxy to remove.
     * @return true if the resourceProxy is removed successfully, false if not.
     */
    public boolean removeChild(COContentResourceProxy resourceProxy) {
	boolean res = getChildrens().remove(resourceProxy);
	notifyEventHandlers();
	return res;
    }

    /** {@inheritDoc} */
    public void addEventHandler(UpdateCOUnitContentEventHandler handler) {
	if (updateCOUnitContentEventHandler == null) {
	    updateCOUnitContentEventHandler =
		    new HashSet<UpdateCOUnitContentEventHandler>();
	}
	if (!updateCOUnitContentEventHandler.contains(handler))
	    updateCOUnitContentEventHandler.add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(UpdateCOUnitContentEventHandler handler) {
	if (updateCOUnitContentEventHandler != null) {
	    updateCOUnitContentEventHandler.remove(handler);
	}
    }

    /** {@inheritDoc} */
    void notifyEventHandlers() {
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DEFAULT_EVENT_TYPE);
    }

    /** {@inheritDoc} */
    void notifyEventHandlers(int eventType) {
	if (updateCOUnitContentEventHandler != null) {
	    UpdateCOUnitContentEvent event = new UpdateCOUnitContentEvent(this);
	    event.setEventType(eventType);
	    Iterator<UpdateCOUnitContentEventHandler> iter =
		    updateCOUnitContentEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOUnitContentEventHandler handler =
			(UpdateCOUnitContentEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    @Override
    public void changeElementPosition(COContentResourceProxy resourceProxy,
	    int action) {

    }

    @Override
    public int getElementPosition(COContentResourceProxy resourceProxy) {
	return 0;
    }

    public void changeElementPosition(COContentResourceProxy resourceProxy,
	    int action, String propertyKey) {
	Iterator<COContentResourceProxy> resourceProxiesIter =
		getChildrens().iterator();
	boolean isFound = false;
	int idRP = 0;
	int indPredecessor = 0;
	int indSuccessor = 0;

	int i = 0;
	while (resourceProxiesIter.hasNext()) {
	    COContentResourceProxy thisResourceProxy =
		    (COContentResourceProxy) resourceProxiesIter.next();

	    if (thisResourceProxy.equals(resourceProxy)) {
		isFound = true;
		idRP = i;
	    } else {
		if (!isFound
			&& thisResourceProxy.getRubricType(propertyKey).equals(
				resourceProxy.getRubricType(propertyKey)))
		    indPredecessor = i;
		if (isFound
			&& thisResourceProxy.getRubricType(propertyKey).equals(
				resourceProxy.getRubricType(propertyKey))) {
		    indSuccessor = i;
		    break;
		}
	    }
	    i++;
	}
	COContentResourceProxy temp;
	if (action == COElementAbstract.POSITION_CHANGE_ACTION_UP) {
	    temp = resourceProxies.get(indPredecessor);
	    resourceProxies.set(indPredecessor, resourceProxy);
	    resourceProxies.set(idRP, temp);
	}
	if (action == COElementAbstract.POSITION_CHANGE_ACTION_DOWN) {
	    temp = resourceProxies.get(indSuccessor);
	    resourceProxies.set(indSuccessor, resourceProxy);
	    resourceProxies.set(idRP, temp);
	}
    }

    public int getElementPosition(COContentResourceProxy resourceProxy,
	    String propertyKey) {
	boolean isFound = false;
	boolean hasPredecessor = false;
	boolean hasSuccessor = false;

	Iterator<COContentResourceProxy> resourceProxiesIter =
		getChildrens().iterator();

	while (resourceProxiesIter.hasNext()) {
	    COContentResourceProxy thisResourceProxy =
		    (COContentResourceProxy) resourceProxiesIter.next();

	    if (thisResourceProxy.equals(resourceProxy)) {
		isFound = true;
	    } else {
		if (!isFound
			&& thisResourceProxy.getRubricType(propertyKey).equals(
				resourceProxy.getRubricType(propertyKey)))
		    hasPredecessor = true;
		if (isFound
			&& thisResourceProxy.getRubricType(propertyKey).equals(
				resourceProxy.getRubricType(propertyKey))) {
		    hasSuccessor = true;
		    break;
		}
	    }
	}
	if (hasPredecessor && hasSuccessor)
	    return 2;
	else if (hasPredecessor)
	    return -1;
	else if (hasSuccessor)
	    return 1;
	else
	    return 0;
    }

}
