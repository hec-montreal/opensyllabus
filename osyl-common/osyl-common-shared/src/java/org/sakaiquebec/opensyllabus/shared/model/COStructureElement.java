/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler.UpdateCOStructureElementEvent;

/**
 * This class represents all the objects of the type
 * <code>COStructureElement</code>. The children of
 * <code>COStructureElement</code> can be other
 * <code>COStructureElement</code> or <code>COContentUnit</code>.
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COStructureElement extends COElementAbstract<COElementAbstract> implements
	COModelInterface {

    /**
     * The handler for updates
     */
    private Set<UpdateCOStructureElementEventHandler> updateCOStructureElementEventHandler;

    /**
     * Properties object that extends a <code>HashMap</code>.
     */
    private COProperties properties;

    /**
     * The parent of the <code>COStructureElement</code>.
     */
    private COElementAbstract parent;
    /**
     * List of children taht can be either <code>COStructureElement</code>
     * objects or <code>COContentUnit</code> objects. atgwt.typeArgs children
     * <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     */

    private List<COElementAbstract> childrens;

    /**
     * Constructor. The class type is set at creation.
     */
    public COStructureElement() {
	super();
	setClassType(CO_STRUCTURE_ELEMENT_CLASS_TYPE);
	childrens = new ArrayList<COElementAbstract>();
	properties = new COProperties();
    }

    /**
     * @return the properties
     */
    public COProperties getProperties() {
	return properties;
    }

    /**
     * @param properties the properties object to set.
     */
    public void setProperties(COProperties properties) {
	this.properties = properties;
	notifyEventHandlers();
    }

    /**
     * @return the parent
     */
    public COElementAbstract getParent() {
	return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(COElementAbstract parent) {
	this.parent = parent;
	notifyEventHandlers();
    }

    /**
     * atgwt.typeArgs <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @return the children
     */
    public List<COElementAbstract> getChildrens() {
	return childrens;
    }

    /**
     * atgwt.typeArgs children
     * <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @param children the children to set
     */
    public void setChildrens(List<COElementAbstract> childrens) {
	this.childrens = childrens;
	notifyEventHandlers();
    }

    /**
     * Adds a child to the <code>COStructureElement</code> children list.
     * Tests if the child is of type <code>COStructureElement</code> or
     * <code>COContentUnit</code> to add it. If not, it rejects the child.
     *
     * @param child the child to add
     * @return true if the child is added successfully, false if not.
     */
    public boolean addChild(COElementAbstract child) {
	if (child.getClassType().equals(CO_STRUCTURE_ELEMENT_CLASS_TYPE)
		|| child.getClassType().equals(CO_CONTENT_UNIT_CLASS_TYPE)) {
	    boolean res = getChildrens().add(child);
	    notifyEventHandlers();
	    return res;
	} else
	    return false;
    }

    /**
     * @param child the child to remove.
     * @return true if the child is removed successfully, false if not.
     */
    public boolean removeChild(COElementAbstract child) {
	boolean res = getChildrens().remove(child);
	notifyEventHandlers();
	return res;
    }

    /**
     * Adds a property to the <code>COProperties</code> structure.
     *
     * @param key the property key used to retrieve its value.
     * @param value the proerty value.
     */
    public void addProperty(String key, String value) {
	getProperties().addProperty(key, value);
	notifyEventHandlers();
    }

    /**
     * Removes a property from the <code>COProerties</code> structure.
     *
     * @param key the property key used to retrieve its value.
     */
    public void removeProperty(String key) {
	getProperties().removeProperty(key);
	notifyEventHandlers();
    }

    /**
     * Retrieves the property value associated to the specified key.
     *
     * @param key the key used to retrieve its value.
     * @return the property value if it is found, null otherwise.
     */
    public String getProperty(String key) {
	return getProperties().getProperty(key);
    }

    /*
     * Finds and returns the <code>COElementAbstract</code> child with the
     * specified id form the children list.
     *
     * @param childId the id of the child to find and return.
     * @return the <code>COElementAbstract</code> if it is found, null
     *         otherwise.
     */
    /*
    public COElementAbstract getChildWithId(String childId) {
	boolean isFound = false;
	COElementAbstract child = null;
	Iterator childrenIter = getChildren().iterator();

	while (childrenIter.hasNext() && !isFound) {
	    COElementAbstract thisChild =
		    (COElementAbstract) childrenIter.next();

	    if (thisChild.getId().equals(childId)) {
		child = thisChild;
		isFound = true;
	    }
	}
	return child;
    }*/

    /**
     * Adds a {@link UpdateCOStructureElementEventHandler} to the list of
     * event handlers.
     * 
     * @param handler
     */
    public void addEventHandler(UpdateCOStructureElementEventHandler handler) {
	if (updateCOStructureElementEventHandler == null) {
	    updateCOStructureElementEventHandler = new HashSet<UpdateCOStructureElementEventHandler>();
	}
	updateCOStructureElementEventHandler.add(handler);
    }

    /**
     * Removes a {@link UpdateCOStructureElementEventHandler} from the list of
     * event handlers.
     * 
     * @param handler
     */
    public void removeEventHandler(UpdateCOStructureElementEventHandler handler) {
	if (updateCOStructureElementEventHandler != null) {
	    updateCOStructureElementEventHandler.remove(handler);
	}
    }


    /** {@inheritDoc} */
    void notifyEventHandlers() {
	if (updateCOStructureElementEventHandler != null) {
	    UpdateCOStructureElementEvent event =
		    new UpdateCOStructureElementEvent(this);
	    Iterator<UpdateCOStructureElementEventHandler> iter = updateCOStructureElementEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOStructureElementEventHandler handler =
			(UpdateCOStructureElementEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    /**
     * Gets the position of a structure element child.
     * 
     * @param the child
     * @return the child poistion
     */
    public String getChildPosition(COElementAbstract coEltAbs){
	String pos="0";
	List<COElementAbstract> children = getChildrens();
	if(children.contains(coEltAbs)) {
	    pos=""+(children.indexOf(coEltAbs)+1);
	}
	return pos;
    }
    
    /**
     * Check the position of the ResouceProxy compared to other resourceProxy in the same rubric
     * @return -1 if the element has no successor,<br/> 
     * 1 if he has no predecessor,<br/> 
     * 0 if he is the only resourceProxy in his rubric<br/>
     * 2 otherwise
     */
    public int getElementPosition(COElementAbstract coEltAbs) {
	
	int pos = childrens.indexOf(coEltAbs);
	boolean hasPredecessor=false;
	boolean hasSuccessor=false;
	
	hasPredecessor = (pos!=0);
	hasSuccessor= (pos!=childrens.size()-1);

	if(hasPredecessor && hasSuccessor) return 2;
	else if(hasPredecessor) return -1;
	else if(hasSuccessor) return 1;
	else return 0;
    }
    
    /**
     * Change the position of the resourceProxy in the rubric
     * @param resourceProxy The resource Proxy
     * @param action -1 if the RP must be move to a prior position
     * @param action 1 if the RP must be move to a posterior position
     */
    public void changeElementPosition(COElementAbstract coEltAbs, int action){
	
	int ind = childrens.indexOf(coEltAbs);
	COElementAbstract temp;
	
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_UP){
	   temp = childrens.get(ind-1);
	   childrens.set(ind-1,coEltAbs);
	   childrens.set(ind,temp);
	}
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_DOWN){
	    temp=childrens.get(ind+1);
	    childrens.set(ind+1, coEltAbs);
	    childrens.set(ind,temp);
	}
	notifyEventHandlers();
    }

}
