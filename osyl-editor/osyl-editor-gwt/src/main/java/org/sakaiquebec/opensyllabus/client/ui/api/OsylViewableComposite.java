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

package org.sakaiquebec.opensyllabus.client.ui.api;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.ViewContextSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.ui.OsylTreeView;
import org.sakaiquebec.opensyllabus.client.ui.OsylWorkspaceView;
import org.sakaiquebec.opensyllabus.client.ui.toolbar.OsylToolbarView;
import org.sakaiquebec.opensyllabus.shared.events.UpdateCOUnitContentEventHandler;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigMessages;
import org.sakaiquebec.opensyllabus.shared.model.OsylSettings;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

/**
 * OsylViewable is the interface that every viewable part of the OpenSyllabus
 * editor should implement. It provides access to the {@link OsylController}
 * needed to operate within the editor and the {@link COModelInterface} instance
 * being edited.<br/><br/> <b>General information about how to add new views
 * in OSYL Editor:</b><br/>
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
 * 
 * @author <a href="mailto:Claude.Coulombe@umontreal.ca">Claude Coulombe</a>
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public abstract class OsylViewableComposite extends Composite implements
	OsylViewable {

    /**
     * OsylController private instance.
     */
    private OsylController osylController;

    /**
     * Model object displayed by this OsylViewable.
     */
    private COModelInterface model;

    /**
     * Course Outline related messages.
     */
    private OsylConfigMessages coMessages;

    /**
     * User Interface messages.
     */
    private OsylConfigMessages uiMessages;

    /**
     * Course Outline general settings.
     */
    private OsylSettings settings;

    /**
     * Private instance of {@link OsylImageBundleInterface} to provide easy
     * access to images commonly used in the user interface.
     */
    private OsylImageBundleInterface osylImageBundle;

    /**
     * Constructor specifying the model object being displayed by this
     * OsylViewable and the {@link OsylController} to use.
     * 
     * @param model
     * @param osylController
     */
    protected OsylViewableComposite(COModelInterface model,
	    OsylController osylController) {
	setController(osylController);
	setModel(model);
	setUiMessages(osylController.getUiMessages());
	setCoMessages(osylController.getCoMessages());
	setSettings(osylController.getSettings());
	initImageBundle();
    }

    /**
     * Initializes the image bundle.
     */
    private void initImageBundle() {
	setOsylImageBundle((OsylImageBundleInterface) GWT
		.create(OsylImageBundleInterface.class));
    }

    /**
     * Sets the User Interface image bundle
     */
    public void setOsylImageBundle(OsylImageBundleInterface osylImageBundle) {
	this.osylImageBundle = osylImageBundle;
    }

    /**
     * Provides access to the User Interface image bundle.
     * 
     * @return {@link OsylImageBundleInterface}
     */
    public OsylImageBundleInterface getOsylImageBundle() {
	return osylImageBundle;
    }

    /**
     * {@inheritDoc}
     */
    public OsylController getController() {
	return osylController;
    }

    /**
     * {@inheritDoc}
     */
    public void setController(OsylController osylController) {
	this.osylController = osylController;
    }

    /**
     * {@inheritDoc}
     */
    public COModelInterface getModel() {
	return model;
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(COModelInterface model) {
	this.model = model;

	// Now we ask the model controller to track changes on this model item
	if(!getController().isInPreview())
	    getController().getModelController().trackChanges(model);
    }

    /** {@inheritDoc} */
    public void setUiMessages(OsylConfigMessages uiMessages) {
	this.uiMessages = uiMessages;
    }

    /**
     * Provides access to the User Interface related messages
     * 
     * @return {@link OsylConfigMessages}
     */
    public OsylConfigMessages getUiMessages() {
	return uiMessages;
    }

    /**
     * Returns the User Interface related message corresponding to the
     * specified key.
     * 
     * @param key
     * @return String
     */
    public String getUiMessage(String key) {
	return getUiMessages().getMessage(key);
    }

    /**
     * Returns the User Interface related message corresponding to the
     * specified key and using one argument.
     * 
     * @param key
     * @param arg Argument 
     * @return String
     */
    public String getUiMessage(String key, String arg) {
	return getUiMessages().getMessage(key, arg);
    }

    /** {@inheritDoc} */
    public void setCoMessages(OsylConfigMessages coMessages) {
	this.coMessages = coMessages;
    }

    /**
     * Provides access to the Course Outline related messages
     * 
     * @return {@link OsylConfigMessages}
     */
    public OsylConfigMessages getCoMessages() {
	return coMessages;
    }

    /**
     * Returns the Course Outline related message corresponding to the
     * specified key.
     * 
     * @param key
     * @return String
     */
    public String getCoMessage(String key) {
	return getCoMessages().getMessage(key);
    }

    /**
     * Returns the Course Outline related message corresponding to the
     * specified key and using one argument.
     * 
     * @param key
     * @param arg Argument 
     * @return String
     */
    public String getCoMessage(String key, String arg) {
	return getCoMessages().getMessage(key, arg);
    }

    /**
     * Returns the Course Outline related message (short version) corresponding
     * to the specified key.
     * 
     * @param key
     * @return String
     */
    public String getShortCoMessage(String key) {
	return getCoMessages().getMessage(key + "Short");
    }
    
    /**
     * Change the modified date of the model to this moment
     */
    public void setModifiedDateToNow(){
	getModel().getProperties().addProperty(COPropertiesType.MODIFIED, OsylDateUtils.getCurrentDateAsXmlString());
    }

    
    /**
     * Provides access to general settings
     * 
     * @return {@link OsylSettings}
     */
	public OsylSettings getSettings() {
		return settings;
	}

	private void setSettings(OsylSettings settings) {
		this.settings = settings;
	}
}
