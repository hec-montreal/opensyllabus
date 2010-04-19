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
 * <code>COStructureElement</code> can be other <code>COStructureElement</code>
 * or <code>COContentUnit</code>.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COStructureElement extends COElementAbstract<COElementAbstract>
	implements COModelInterface, COElementMoveable {

    private static final long serialVersionUID = 1701561339679430501L;

    /**
     * The handler for updates
     */
    private Set<UpdateCOStructureElementEventHandler> updateCOStructureElementEventHandler;

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
	setClassType(ASM_STRUCTURE_CLASS_TYPE);
	childrens = new ArrayList<COElementAbstract>();
    }

    public static COStructureElement createDefaultCOStructureElement(
	    final String type, final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract<COElementAbstract> parentModel) {
	final COStructureElement structureElement = new COStructureElement();
	structureElement.setType(type);
	structureElement.setParent(parentModel);

	// Add child (a model notification should fire)
	parentModel.addChild(structureElement);

	return structureElement;
    }

    /**
     * {@inheritDoc}
     */
    public List<COElementAbstract> getChildrens() {
	return childrens;
    }

    /**
     * {@inheritDoc}
     */
    public void setChildrens(List<COElementAbstract> childrens) {
	this.childrens = childrens;
	notifyEventHandlers();
    }

    @SuppressWarnings("unchecked")
    public boolean addChild(COElementAbstract child) {
	if (OsylConfigRuler.getInstance() != null) {
	    if (!OsylConfigRuler.getInstance().istMixedContentAllowed(this)
		    && child.isCOStructureElement() && !hasNestedChild()) {
		while (!childrens.isEmpty()) {
		    COElementAbstract coe = childrens.get(0);
		    childrens.remove(0);
		    coe.setParent(child);
		    child.getChildrens().add(coe);
		}
	    }
	}
	boolean res = getChildrens().add(child);
	notifyEventHandlers();
	return res;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean removeChild(COElementAbstract child) {
	COElementAbstract newContainerCoStructureElement = null;
	if (child.isCOStructureElement()) {
	    COStructureElement cosChild = (COStructureElement) child;
	    if (cosChild.hasPredecessor()) {
		newContainerCoStructureElement =
			childrens.get(childrens.indexOf(cosChild) - 1);
	    } else if (cosChild.hasSuccessor()) {
		newContainerCoStructureElement =
			childrens.get(childrens.indexOf(cosChild) + 1);
	    } else {
		newContainerCoStructureElement = this;
	    }
	}
	boolean res = getChildrens().remove(child);
	if (newContainerCoStructureElement != null) {
	    for (COElementAbstract el : ((COStructureElement) child)
		    .getChildrens()) {
		el.setParent(newContainerCoStructureElement);
		newContainerCoStructureElement.getChildrens().add(el);
	    }
	}
	notifyEventHandlers();
	return res;
    }

    /**
     * Adds a {@link UpdateCOStructureElementEventHandler} to the list of event
     * handlers.
     * 
     * @param handler
     */
    public void addEventHandler(UpdateCOStructureElementEventHandler handler) {
	if (updateCOStructureElementEventHandler == null) {
	    updateCOStructureElementEventHandler =
		    new HashSet<UpdateCOStructureElementEventHandler>();
	}
	if (!updateCOStructureElementEventHandler.contains(handler))
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
	    Iterator<UpdateCOStructureElementEventHandler> iter =
		    updateCOStructureElementEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOStructureElementEventHandler handler =
			(UpdateCOStructureElementEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    /**
     * {@inheritDoc}
     */
    public int getElementPosition(COElementAbstract coEltAbs) {

	int pos = childrens.indexOf(coEltAbs);
	boolean hasPredecessor = false;
	boolean hasSuccessor = false;

	hasPredecessor = (pos != 0);
	hasSuccessor = (pos != childrens.size() - 1);

	if (hasPredecessor && hasSuccessor)
	    return 2;
	else if (hasPredecessor)
	    return -1;
	else if (hasSuccessor)
	    return 1;
	else
	    return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void changeElementPosition(COElementAbstract coEltAbs, int action) {

	int ind = childrens.indexOf(coEltAbs);
	COElementAbstract temp;

	if (action == COElementAbstract.POSITION_CHANGE_ACTION_UP) {
	    temp = childrens.get(ind - 1);
	    childrens.set(ind - 1, coEltAbs);
	    childrens.set(ind, temp);
	}
	if (action == COElementAbstract.POSITION_CHANGE_ACTION_DOWN) {
	    temp = childrens.get(ind + 1);
	    childrens.set(ind + 1, coEltAbs);
	    childrens.set(ind, temp);
	}
	notifyEventHandlers();
    }

    @SuppressWarnings("unchecked")
    public boolean hasPredecessor() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
	if (i != 1 && i != 0)
	    return true;
	else
	    return false;
    }

    @SuppressWarnings("unchecked")
    public boolean hasSuccessor() {
	if (getParent() == null)
	    return false;
	int i = getParent().getElementPosition(this);
	if (i != -1 && i != 0)
	    return true;
	else
	    return false;
    }

    @SuppressWarnings("unchecked")
    public void moveDown() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_DOWN);
    }

    @SuppressWarnings("unchecked")
    public void moveUp() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_UP);
    }

}
