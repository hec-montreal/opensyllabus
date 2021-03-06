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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.OsylEditorEntryPoint;
import org.sakaiquebec.opensyllabus.client.controller.event.OsylModelUpdatedEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.OsylModelUpdatedEventHandler.OsylModelUpdatedEvent;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOContentResourceProxyEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOStructureElementEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitEventHandler;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitStructureEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.user.client.Window;

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

    private List<OsylModelUpdatedEventHandler> modelUpdateEventHandlerList;

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
	//System.out.println("= =================== == = = = DIRTY");
	boolean modelSaved = (modelDirty && !b);
	
	modelDirty = b;

	if (modelDirty) {
	    fireModelUpdated();
	}
	
	if (modelSaved) {
	    fireModelSaved();
	}
    }	

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOContentResourceEvent event) {
	setModelDirty(true);
    }

    /** {@inheritDoc} */
    public void onUpdateModel(UpdateCOContentResourceProxyEvent event) {
	//update documentcontextvisibility
	if (UpdateCOContentResourceProxyEvent.DELETE_EVENT_TYPE == event
		.getEventType()) {
	    COContentResourceProxy cocrp =
		    (COContentResourceProxy) event.getSource();
	    if (COContentResourceType.DOCUMENT.equals(cocrp.getResource()
		    .getType())) {
		String uri =
			cocrp.getResource().getProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URI).trim();

		
		//DocumentContextLicenceMap
		Map<String, Map<String, String>> dcl =
			OsylEditorEntryPoint.getInstance()
				.getDocumentContextLicenceMap();
		
		dcl.get(uri).remove(cocrp.getId());
		OsylEditorEntryPoint.getInstance()
			.setDocumentContextLicenceMap(dcl);
		
	    }
	    if(COContentResourceType.URL.equals(cocrp.getResource()
		    .getType()) || COContentResourceType.DOCUMENT.equals(cocrp.getResource()
			    .getType()) || COContentResourceType.BIBLIO_RESOURCE.equals(cocrp.getResource()
				    .getType())){
		//DocumentContextTypeMap
		Map<String, Map<String, String>> dcr =
			OsylEditorEntryPoint.getInstance()
				.getResourceContextTypeMap();
		String uri = cocrp.getResource().getProperty(
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI).trim();
		dcr.get(uri).remove(cocrp.getId());
		OsylEditorEntryPoint.getInstance()
			.setResourceContextTypeMap(dcr);
		
		//DocumentContextVisibilityMap
		Map<String, Map<String, String>> dcv =
			OsylEditorEntryPoint.getInstance()
				.getResourceContextVisibilityMap();
		
		dcv.get(uri).remove(cocrp.getId());
		OsylEditorEntryPoint.getInstance()
			.setResourceContextVisibilityMap(dcv);
	    }
	}
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

    
    public void addEventHandler(OsylModelUpdatedEventHandler handler) {
	if (modelUpdateEventHandlerList == null) {
	    modelUpdateEventHandlerList =
		    new ArrayList<OsylModelUpdatedEventHandler>();
	}
	modelUpdateEventHandlerList.add(handler);
    }

    public void removeEventHandler(OsylModelUpdatedEventHandler handler) {
	if (modelUpdateEventHandlerList != null) {
	    modelUpdateEventHandlerList.remove(handler);
	}
    }
    
    private void fireModelUpdated() {

	if (modelUpdateEventHandlerList != null) {
	    for (OsylModelUpdatedEventHandler handler : modelUpdateEventHandlerList) {
		handler.onModelUpdated(new OsylModelUpdatedEvent(this));
	    }

	}

    }	
    
    
    private void fireModelSaved() {

	if (modelUpdateEventHandlerList != null) {
	    for (OsylModelUpdatedEventHandler handler : modelUpdateEventHandlerList) {
		handler.onModelSaved(new OsylModelUpdatedEvent(this));
	    }

	}

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
	} else if (model instanceof COUnit) {
	    ((COUnit) model).addEventHandler(this);
	} else if (model instanceof COUnitStructure) {
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
