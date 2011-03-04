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

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler.UpdateCOUnitEvent;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COUnit extends COElementAbstract<COElementAbstract> implements
	COModelInterface, COElementMoveable {

    private static final long serialVersionUID = -6115273965524477729L;

    private HashSet<UpdateCOUnitEventHandler> updateCOUnitEventHandler;

    /**
     * Could be a COUnit or COUnitStructure
     */
    private List<COElementAbstract> childrens;

    public COUnit() {
	super();
	setClassType(ASM_UNIT_CLASS_TYPE);
	childrens = new ArrayList<COElementAbstract>();
	setAccess(SecurityInterface.ACCESS_PUBLIC);
    }

    /**
     * Creates a Default a COUnit with a default content ¸ This method is an
     * helper class for the model
     * 
     * @param type of the new model to create
     * @param osylConfigMessages, i.e. all the course outline messages
     * @param parentModel, i.e. the parent model
     * @return model created
     */
    public static COUnit createDefaultCOUnit(final String type,
	    final OsylConfigMessages osylConfigMessages,
	    final COElementAbstract<COElementAbstract> parentModel) {
	final COUnit unitModel = new COUnit();
	unitModel.setType(type);
	unitModel.setLabel(osylConfigMessages.getMessage(type));
	unitModel.setParent(parentModel);

	// Add child (a model notification should fire)
	parentModel.addChild(unitModel);

	return unitModel;
    }

    /** {@inheritDoc} */
    public void addEventHandler(UpdateCOUnitEventHandler handler) {
	if (updateCOUnitEventHandler == null) {
	    updateCOUnitEventHandler = new HashSet<UpdateCOUnitEventHandler>();
	}
	if(!updateCOUnitEventHandler.contains(handler))
	    updateCOUnitEventHandler.add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(UpdateCOUnitEventHandler handler) {
	if (updateCOUnitEventHandler != null) {
	    updateCOUnitEventHandler.remove(handler);
	}
    }

    void notifyEventHandlers() {
	if (updateCOUnitEventHandler != null) {
	    UpdateCOUnitEvent event = new UpdateCOUnitEvent(this);
	    Iterator<UpdateCOUnitEventHandler> iter =
		    updateCOUnitEventHandler.iterator();
	    while (iter.hasNext()) {
		UpdateCOUnitEventHandler handler =
			(UpdateCOUnitEventHandler) iter.next();
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
	// TODO when multiple coUnitStructure will be manage
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
	this.childrens = childs;
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

    public void setSameTypeElementPrefixWithCurrentPosition(){
	COElementAbstract coe = this;
	int level = 0;
	int p=0;
	while (!coe.isCourseOutlineContent()) {
	    level++;
	    coe=coe.getParent();
	}
	List<COElementAbstract> l = new ArrayList<COElementAbstract>();
	getSameTypeElement(coe, 0, level, l);
	int i=1;
	for(COElementAbstract coe2 : l){
	    if(coe2.getProperty(COPropertiesType.PREFIX)==null)
		coe2.addProperty(COPropertiesType.PREFIX, ""+i);
	    i++;
	}
	
    }
    
}
