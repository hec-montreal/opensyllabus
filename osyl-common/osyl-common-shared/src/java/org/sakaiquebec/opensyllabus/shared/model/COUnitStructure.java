/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.shared.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler.UpdateCOUnitStructureEvent;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COUnitStructure extends COElementAbstract<COElementAbstract>
	implements COModelInterface, COElementMoveable {

    private static final long serialVersionUID = 3055320855146159375L;

    private HashSet<UpdateCOUnitStructureEventHandler> updateCOUnitStructureEventHandler;

    /**
     * Could be a COUnitContent or COUnitStructure
     */
    private List<COElementAbstract> childrens;

    public COUnitStructure() {
	super();
	setClassType(CO_UNIT_STRUCTURE_CLASS_TYPE);
	childrens = new ArrayList<COElementAbstract>();
    }

    /**
     * Creates a Default a COUnitStructure with a default content ¸ This method
     * is an helper class for the model
     * 
     * @param type of the new model to create
     * @param osylConfigMessages, i.e. all the course outline messages
     * @param parentModel, i.e. the parent model
     * @return model created
     */
    public static COUnitStructure createDefaultCOUnitStructure(
	    final String type, final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract<COElementAbstract> parentModel) {
	final COUnitStructure structureModel = new COUnitStructure();
	structureModel.setType(type);
	structureModel.setParent(parentModel);

	// Add child (a model notification should fire)
	parentModel.addChild(structureModel);

	return structureModel;
    }

    /** {@inheritDoc} */
    public void addEventHandler(UpdateCOUnitStructureEventHandler handler) {
	if (updateCOUnitStructureEventHandler == null) {
	    updateCOUnitStructureEventHandler =
		    new HashSet<UpdateCOUnitStructureEventHandler>();
	}
	updateCOUnitStructureEventHandler.add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(UpdateCOUnitStructureEventHandler handler) {
	if (updateCOUnitStructureEventHandler != null) {
	    updateCOUnitStructureEventHandler.remove(handler);
	}
    }

    void notifyEventHandlers() {
	if (updateCOUnitStructureEventHandler != null) {
	    UpdateCOUnitStructureEvent event =
		    new UpdateCOUnitStructureEvent(this);
	    Iterator<UpdateCOUnitStructureEventHandler> iter =
		    updateCOUnitStructureEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOUnitStructureEventHandler handler =
			(UpdateCOUnitStructureEventHandler) iter.next();
		handler.onUpdateModel(event);
	    }
	}
    }

    @Override
    public boolean addChild(COElementAbstract child) {
	boolean res = getChildrens().add(child);
	notifyEventHandlers();
	return res;
    }

    @Override
    public void changeElementPosition(COElementAbstract coEltAbs, int action) {
	// Nothing to do
	// TODO to be confirmed
    }

    @Override
    public List<COElementAbstract> getChildrens() {
	return childrens;
    }

    @Override
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

    @Override
    public boolean removeChild(COElementAbstract child) {
	boolean res = getChildrens().remove(child);
	notifyEventHandlers();
	return res;
    }

    @Override
    public void setChildrens(List<COElementAbstract> childs) {
	this.childrens = childrens;
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void moveUp() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_UP);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void moveDown() {
	getParent().changeElementPosition(this,
		COElementAbstract.POSITION_CHANGE_ACTION_DOWN);
    }

    /**
     * Tells the COContentUnit to remove itself form its parent children list.
     */
    public void remove() {
	if (getParent().isCOUnit()) {
	    ((COUnit) getParent()).removeChild(this);
	} else if (getParent().isCOUnitStructure()) {
	    ((COUnitStructure) getParent()).removeChild(this);
	}

	if (updateCOUnitStructureEventHandler != null)
	    updateCOUnitStructureEventHandler.clear();
    }
}
