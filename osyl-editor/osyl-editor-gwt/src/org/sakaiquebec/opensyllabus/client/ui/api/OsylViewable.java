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

package org.sakaiquebec.opensyllabus.client.ui.api;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.OsylToolbarView;
import org.sakaiquebec.opensyllabus.client.ui.OsylTreeView;
import org.sakaiquebec.opensyllabus.client.ui.OsylWorkspaceView;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;

/**
 * OsylViewable is the interface that every viewable part of the OpenSyllabus
 * editor should implement. It provides access to the {@link OsylController}
 * needed to operate within the editor and the {@link COModelInterface} instance
 * being edited.<br/><br/>
 * 
 * <b>General information about how to add new views in OSYL Editor:</b><br/>
 * 
 * <ul>
 * <li>Any view should implement <code>OsylViewable</code> and extend
 * {@link com.google.gwt.user.client.ui.Composite} or another GWT layout
 * facility (like {@link com.google.gwt.user.client.ui.Panel} for instance).</li>
 * <li>It should be added to the {@link EditorMainView} or one of its subviews:
 * {@link OsylWorkspaceView} (most probably) or {@link OsylTreeView},
 * {@link OsylToolbarView}, for instance.</li>
 * <li>It should have its {@link OsylController} and {@link COModelInterface}
 * injected by its parent (at instantiation).</li>
 * <li>It could implement {@link ViewContextSelectionEventHandler} and register
 * as a listener by calling
 * {@link EditorMainView#addEventHandler(ViewContextSelectionEventHandler)}.
 * This would allow to receive notification when the editor main view changes
 * from an object to another.</li>
 * <li>It could also implement {@link UpdateCOUnitContentEventHandler} and
 * register as a listener by calling
 * {@link COUnitContent#addEventHandler(UpdateCOUnitContentEventHandler)}.
 * </li>
 * </ul>

 * The general layout is as follows (TODO: this is outdated):
 * <pre>
 *  ___________________________________________________
 * |                  OsylToolbarView                  |
 * |__________________________________________________ |
 * |              |                                    |
 * |              |                        ----------  |
 * |              |                       | OsylRes  | |
 * |              |                       |ConfigView| |
 * |              |                        ----------  |
 * | OsylTreeView |    OsylWorkSpaceView               |
 * |              |                                    |
 * |              |                                    |
 * |              |                                    |
 * |              |                                    |
 * |______________|____________________________________|
 * </pre>
 * 
 * @author <a href="mailto:Claude.Coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public interface OsylViewable extends OsylViewControllable, OsylViewModelable {

    /**
     * Sets the User Interface image bundle
     */
    void setOsylImageBundle(OsylImageBundleInterface osylImageBundle);
    
    /**
     * Provides access to the User Interface image bundle.
     * 
     * @return {@link OsylImageBundleInterface}
     */
    OsylImageBundleInterface getOsylImageBundle();

    /**
     * Provides access to the User Interface related messages
     * @return {@link OsylConfigMessages}
     */
    OsylConfigMessages getUiMessages();

    /**
     * Provides access to the Course Outline related messages
     * @return {@link OsylConfigMessages}
     */
    OsylConfigMessages getCoMessages();


    /**
     * Sets the User Interface related messages.
     * 
     * @param {@link OsylConfigMessages}
     */
    void setUiMessages(OsylConfigMessages uiMessages);

    /**
     * Sets the Course Outline related messages.
     * 
     * @param {@link OsylConfigMessages}
     */
    void setCoMessages(OsylConfigMessages coMessages);
}
