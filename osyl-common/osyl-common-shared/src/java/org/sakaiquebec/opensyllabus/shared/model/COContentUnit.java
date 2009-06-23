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
import org.sakaiquebec.opensyllabus.shared.events.FiresUpdateCOContentUnitEvents;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler.UpdateCOContentResourceProxyEvent;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentUnitEventHandler.UpdateCOContentUnitEvent;

/**
 * Content unit of the <code>COStructureElement</code>. One
 * <code>COStructureElement</code> can have many <code>COContentUnit</code>
 * children but one <code>COContentUnit</code> can have one and only one
 * <code>COStructureElement</code> parent.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentUnit extends COElementAbstract<COContentResourceProxy> implements
	COModelInterface, FiresUpdateCOContentUnitEvents {

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;

    /**
     * Properties object that extends a <code>HashMap</code>.
     */
    private COProperties properties;

    private Set<UpdateCOContentUnitEventHandler> updateCOContentUnitEventHandler;

    /**
     * The <code>COContentUnit</code> parent.
     */
    private COElementAbstract parent;

    /**
     * List of <code>ResourceProxy</code> that are contained in a
     * <code>COContentUnit</code>. atgwt.typeArgs resourceProxies
     * <org.sakaiquebec.opensyllabus.model.COContentResourceProxy>
     */
    private List<COContentResourceProxy> resourceProxies;

    /**
     * Constructor. The class type is set at creation.
     */
    public COContentUnit() {
	super();
	setClassType(CO_CONTENT_UNIT_CLASS_TYPE);
	resourceProxies = new ArrayList<COContentResourceProxy>();
	properties = new COProperties();
    }

    /**
     * Creates a Default a COContenUnit with a default content ¸
     * This method is an helper class for the model
     * 
     * @param type of the new model to create
     * @param osylConfigMessages, i.e. all the course outline messages
     * @param parentModel, i.e. the parent model
     * @return model created
     */
    public static COContentUnit createDefaultCOContenUnit(final String type, 
	    final OsylConfigMessages osylConfigMessages, final COElementAbstract parentModel) {
	final COContentUnit unitModel = new COContentUnit();
	unitModel.setType(type);
	unitModel.setLabel(osylConfigMessages.getMessage(type));
	unitModel.setSecurity(SecurityInterface.SECURITY_ACCESS_PUBLIC);
	unitModel.setParent(parentModel);

	// Add child (a model notification should fire)
	((COStructureElement) parentModel).addChild(unitModel);

	return unitModel;
    }

    /**
     * @return the parent
     */
    public COElementAbstract getParent() {
	return parent;
    }

    /**
     * Tests if the parent is a <code>CourseOutlineContent</code> or a
     * <code>COStructureElement</code>. If not, it rejects it and set the parent
     * to null.
     * 
     * @param parent the parent to set.
     */
    public void setParent(COElementAbstract parent) {
	if (parent.getClassType().equals(CO_CONTENT_CLASS_TYPE)
		|| parent.getClassType()
			.equals(CO_STRUCTURE_ELEMENT_CLASS_TYPE))
	    this.parent = parent;
	else
	    parent = null;
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
	boolean res = getChildrens().add(resourceProxy);
	notifyEventHandlers(UpdateCOContentUnitEvent.ADD_RESSOURCE_PROXY_EVENT_TYPE);
	return res;
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
    public void addEventHandler(UpdateCOContentUnitEventHandler handler) {
	if (updateCOContentUnitEventHandler == null) {
	    updateCOContentUnitEventHandler =
		    new HashSet<UpdateCOContentUnitEventHandler>();
	}
	updateCOContentUnitEventHandler.add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(UpdateCOContentUnitEventHandler handler) {
	if (updateCOContentUnitEventHandler != null) {
	    updateCOContentUnitEventHandler.remove(handler);
	}
    }

    /** {@inheritDoc} */
    void notifyEventHandlers() {
	notifyEventHandlers(UpdateCOContentResourceProxyEvent.DEFAULT_EVENT_TYPE);
    }

    /** {@inheritDoc} */
    void notifyEventHandlers(int eventType) {
	if (updateCOContentUnitEventHandler != null) {
	    UpdateCOContentUnitEvent event = new UpdateCOContentUnitEvent(this);
	    event.setEventType(eventType);
	    Iterator<UpdateCOContentUnitEventHandler> iter =
		    updateCOContentUnitEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOContentUnitEventHandler handler =
			(UpdateCOContentUnitEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    /**
     * Tells the COContentUnit to remove itself form its parent children list.
     */
    public void remove() {

	if (getParent().isCOStructureElement()) {
	    ((COStructureElement) getParent()).removeChild(this);
	} else if (getParent().isCourseOutlineContent()) {
	    ((COContent) getParent()).removeChild(this);
	}

	if (updateCOContentUnitEventHandler != null)
	    updateCOContentUnitEventHandler.clear();
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
     * Adds a property to the <code>COProperties</code> structure.
     * 
     * @param key the key used to retrieve the property value.
     * @param value the property value.
     */
    public void addProperty(String key, String value) {

	getProperties().addProperty(key, value);
	if (TRACE)
	    System.out.println("*** TRACE *** UPDATE THE MODEL COContentUnit "
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
     * Check the position of this resourceProxy compared to the other rp in the
     * same rubric
     * 
     * @return true if the ResourceProxy is not the last element
     */
    public boolean hasSuccessorInStructure() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
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
    public boolean hasPredecessorInStructure() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
	if (i != 1 && i != 0)
	    return true;
	else
	    return false;
    }

    @Override
    public void changeElementPosition(COContentResourceProxy resourceProxy, int action) {
	Iterator<COContentResourceProxy> resourceProxiesIter = getChildrens().iterator();
	boolean isFound = false;
	int idRP=0;
	int indPredecessor=0;
	int indSuccessor=0;
	
	int i = 0;
	while (resourceProxiesIter.hasNext()) {
	    COContentResourceProxy thisResourceProxy =
		    (COContentResourceProxy) resourceProxiesIter.next();
	    
	    if (thisResourceProxy.equals(resourceProxy)) {
		isFound = true;
		idRP=i;
	    }else{
		if(!isFound && thisResourceProxy.getRubricType().equals(resourceProxy.getRubricType()))
			indPredecessor=i;
		 if(isFound && thisResourceProxy.getRubricType().equals(resourceProxy.getRubricType())){
			indSuccessor=i;
			break;
		 }
	    }
	    i++;
	}
	COContentResourceProxy temp;
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_UP){
	   temp = resourceProxies.get(indPredecessor);
	   resourceProxies.set(indPredecessor,resourceProxy);
	   resourceProxies.set(idRP,temp);
	}
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_DOWN){
	    temp=resourceProxies.get(indSuccessor);
	    resourceProxies.set(indSuccessor, resourceProxy);
	    resourceProxies.set(idRP,temp);
	}
    }

    @Override
    public int getElementPosition(COContentResourceProxy resourceProxy) {
	boolean isFound = false;
	boolean hasPredecessor=false;
	boolean hasSuccessor=false;
	
	Iterator<COContentResourceProxy> resourceProxiesIter = getChildrens().iterator();

	while (resourceProxiesIter.hasNext()) {
	    COContentResourceProxy thisResourceProxy =
		    (COContentResourceProxy) resourceProxiesIter.next();
	 
	    if (thisResourceProxy.equals(resourceProxy)) {
		isFound = true;
	    }
	    else{
		if(!isFound && thisResourceProxy.getRubricType().equals(resourceProxy.getRubricType()))
			hasPredecessor=true;
		if(isFound && thisResourceProxy.getRubricType().equals(resourceProxy.getRubricType())){
		    hasSuccessor=true;
		    break;
		}
	    }
	}
	if(hasPredecessor && hasSuccessor) return 2;
	else if(hasPredecessor) return -1;
	else if(hasSuccessor) return 1;
	else return 0;
    }

    public void moveUp() {
	getParent().changeElementPosition(this, COElementAbstract.POSITION_CHANGE_ACTION_UP);
    }

    public void moveDown() {
	getParent().changeElementPosition(this, COElementAbstract.POSITION_CHANGE_ACTION_DOWN);
    }
}
