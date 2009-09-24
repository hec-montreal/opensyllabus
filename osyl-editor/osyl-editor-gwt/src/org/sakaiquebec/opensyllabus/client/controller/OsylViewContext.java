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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.event.FiresResProxySelectionEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresUnitSelectionEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.ResProxySelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ResProxySelectionEventHandler.ResProxySelectionEvent;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractResProxView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentType;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElementType;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructureType;
import org.sakaiquebec.opensyllabus.shared.model.COUnitType;

/**
 * ViewContext is used to keep track of the current context, i.e.: the type of
 * object currently displayed in the editor, and the object itself.
 * 
 * @author <a href="mailto:sacha.Lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:laurent.danet@hec.ca>Laurent Danet"</a>
 * @version $Id: $
 */
public class OsylViewContext implements FiresUnitSelectionEvents,
	FiresResProxySelectionEvents, ViewContextSelectionEventHandler {

    /** current view model * */
    private COModelInterface contextModel;

    /** a child view context within the current context * */
    private OsylViewContext child;

    /** current view * */
    private OsylAbstractView view;

    private static List<ResProxySelectionEventHandler> resProxySelectionEventHandlerList =
	    new ArrayList<ResProxySelectionEventHandler>();

    private static List<ViewContextSelectionEventHandler> viewContextSelectionHandlerList =
	    new ArrayList<ViewContextSelectionEventHandler>();

    /**
     * Empty constructor.
     */
    public OsylViewContext() {
    }

    /**
     * @return the view of this view context
     */
    public OsylAbstractView getView() {
	return view;
    }

    /**
     * Set the view of this view Context. If another exist, tell him to leave
     * edit mode
     * 
     * @param view
     */
    public void setView(OsylAbstractView view) {
	if (this.view == null) {
	    addEventHandler(this);
	}
	this.view = view;
	if (view instanceof OsylAbstractResProxView) {
	    // TODO: we should change notifyResProxySelectionEventHandlers and
	    // all the methods invoked to work on OsylResProxView instead of
	    // OsylAbstractView
	    // Then we could cast view in an OsylResProxView and catch the cast
	    // instead of this instanceof.
	    notifyResProxySelectionEventHandlers(view);
	} else {
	    closeAllEditors();
	    this.child = null;
	}
    }

    /**
     * Returns the object currently displayed in the editor.
     * 
     * @return Model object
     */
    public COModelInterface getContextModel() {
	return contextModel;
    }

    /**
     * Sets the object currently displayed in the editor.
     * 
     * @param COModelInterface object
     */
    public void setContextModel(COModelInterface contextModel) {
	if (!contextModel.equals(this.contextModel)) {
	    this.contextModel = contextModel;
	    if (COContentType.getTypesList().contains(contextModel.getType())) {
		notifyViewContextSelectionEventHandlers(contextModel);
	    } else if (Arrays.asList(COStructureElementType.getTypes())
		    .contains(contextModel.getType())) {
		notifyViewContextSelectionEventHandlers(contextModel);
	    } else if (COUnitType.getTypesList().contains(
		    contextModel.getType())) {
		notifyViewContextSelectionEventHandlers(contextModel);
	    } else if (COUnitStructureType.getTypesList().contains(
		    contextModel.getType())) {
		notifyViewContextSelectionEventHandlers(contextModel);
	    } else if (COContentResourceProxyType.getTypesList().contains(
		    contextModel.getType())) {
		// nothing to do
	    }
	}
    }

    /**
     * Sets a child context
     * 
     * @param childContextType
     * @param childContextModel
     */
    public void setChild(COModelInterface childContextModel) {
	if (child == null) {
	    child = new OsylViewContext();
	}
	child.setContextModel(childContextModel);
    }

    /**
     * Set a new child context from an view
     * 
     * @param view
     */
    public void setChild(OsylAbstractView view) {
	if (child == null || !child.getContextModel().equals(view.getModel())) {
	    closeAllEditors();
	    setChild(view.getModel());
	    this.child.setView(view);
	}
    }

    /**
     * @return the child value of view context.
     */
    public OsylViewContext getChild() {
	return child;
    }

    // RESPROX SELECTION
    /**
     * {@inheritDoc}
     */
    public void addEventHandler(ResProxySelectionEventHandler handler) {
	if (handler == null) {
	    // Happens in read-only mode
	    return;
	}
	if (resProxySelectionEventHandlerList == null) {
	    resProxySelectionEventHandlerList =
		    new ArrayList<ResProxySelectionEventHandler>();
	}
	resProxySelectionEventHandlerList.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEventHandler(ResProxySelectionEventHandler handler) {
	if (resProxySelectionEventHandlerList != null) {
	    resProxySelectionEventHandlerList.remove(handler);
	}
    }

    /**
     * Notify all handlers that the resProxy selected has changed
     * 
     * @param view
     */
    public void notifyResProxySelectionEventHandlers(OsylAbstractView view) {
	if (resProxySelectionEventHandlerList != null) {
	    ResProxySelectionEvent event = new ResProxySelectionEvent(view);
	    Iterator<ResProxySelectionEventHandler> iter =
		    resProxySelectionEventHandlerList.iterator();
	    while (iter.hasNext()) {
		ResProxySelectionEventHandler handler =
			(ResProxySelectionEventHandler) iter.next();
		handler.onResProxySelection(event);
	    }
	}
    }

    // VIEW CONTEXT SELECTION
    /**
     * {@inheritDoc}
     */
    public void addEventHandler(ViewContextSelectionEventHandler handler) {
	if (handler == null) {
	    // Happens in read-only mode
	    return;
	}
	if (viewContextSelectionHandlerList == null) {
	    viewContextSelectionHandlerList =
		    new ArrayList<ViewContextSelectionEventHandler>();
	}
	viewContextSelectionHandlerList.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEventHandler(ViewContextSelectionEventHandler handler) {
	viewContextSelectionHandlerList.remove(handler);
    }

    /**
     * selectViewContext is called when some part of the application requires to
     * change the current view.
     * 
     * @param model the object to display
     */
    public void notifyViewContextSelectionEventHandlers(COModelInterface model) {
	ViewContextSelectionEvent event =
		new ViewContextSelectionEvent(contextModel);
	Iterator<ViewContextSelectionEventHandler> iter =
		viewContextSelectionHandlerList.iterator();
	while (iter.hasNext()) {
	    ViewContextSelectionEventHandler handler =
		    (ViewContextSelectionEventHandler) iter.next();
	    handler.onViewContextSelection(event);
	}
    }

    /**
     * {@inheritDoc}
     */
    public void onViewContextSelection(
	    ViewContextSelectionEvent viewContextSelectionEvent) {
	closeAllEditors();
    }

    /**
     * Call for closing all editor of this viewContext and his descendants
     */
    public void closeAllEditors() {
	OsylViewContext vc = this;
	while (vc != null) {
	    OsylAbstractView oe = vc.getView();
	    if (oe != null) {
		if (oe.getEditor().isInEditionMode()) {
		    oe.closeAndSaveEdit(true);
		}
	    }
	    vc = vc.getChild();
	}
    }
}
