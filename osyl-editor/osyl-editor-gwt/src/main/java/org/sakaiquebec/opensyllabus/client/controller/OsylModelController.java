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

package org.sakaiquebec.opensyllabus.client.controller;

import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

/**
 * OsylModelController is the controller related to the model (i.e.: course
 * outline) being edited.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylModelController implements
	UpdateCOContentResourceEventHandler,
	UpdateCOContentResourceProxyEventHandler,
	UpdateCOUnitContentEventHandler, UpdateCOStructureElementEventHandler,
	UpdateCOUnitEventHandler, UpdateCOUnitStructureEventHandler {

    private boolean modelDirty = false;

    private boolean readOnly;

    /**
     * Package-protected controller because it should be instantiated by the
     * main controller.
     */
    OsylModelController() {
    }

    void setReadOnly(boolean b) {
	readOnly = b;
    }

    boolean isReadOnly() {
	return readOnly;
    }

    /**
     * Returns true if the model has been modified since the last save. If the
     * application is in read-only mode, it always return false.
     * 
     * @return
     */
    public boolean isModelDirty() {
	if (readOnly) {
	    return false;
	}
	return modelDirty;
    }

    /**
     * Sets whether the model has been modified (and therefore needs to be
     * saved). This method should be called with argument false when the model
     * is saved.
     * 
     * @param b
     */
    public void setModelDirty(boolean b) {
	System.out.println("= =================== == = = = DIRTY");
	modelDirty = b;
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOContentResourceEvent event) {
	setModelDirty(true);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOContentResourceProxyEvent event) {
	setModelDirty(true);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOUnitContentEvent event) {
	setModelDirty(true);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOStructureElementEvent event) {
	setModelDirty(true);
    }
    
    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOUnitEvent event) {
	setModelDirty(true);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOUnitStructureEvent event) {
	setModelDirty(true);
    }

    /**
     * Registers the specified model item to be tracked by this Model Controller
     * for changes. This is done by adding itself as an event handler to the
     * model item after determining its type. If the specified item is a
     * {@link COContentResourceProxy}, changes to the associated
     * {@link COContentResource} will be tracked as well. When a modification is
     * made to any of the tracked items, the dirty flag is set to true.
     * 
     * @param model
     */
    public void trackChanges(COModelInterface model) {
	if (model instanceof COStructureElement) {
	    ((COStructureElement) model).addEventHandler(this);
	} else if (model instanceof COUnit){
	    ((COUnit) model).addEventHandler(this);
	} else if (model instanceof COUnitStructure){
	    ((COUnitStructure) model).addEventHandler(this);
	} else if (model instanceof COUnitContent) {
	    ((COUnitContent) model).addEventHandler(this);
	} else if (model instanceof COContentResource) {
	    ((COContentResource) model).addEventHandler(this);
	} else if (model instanceof COContentResourceProxy) {
	    // Special case: for resource proxies, we add an event handler to
	    // the proxy and the resource itself:
	    COContentResourceProxy proxy = (COContentResourceProxy) model;
	    proxy.addEventHandler(this);
	    if (proxy.getResource() instanceof COContentResource)// could be a
								 // CoUnit too
		((COContentResource) proxy.getResource()).addEventHandler(this);
	}
    }
}
