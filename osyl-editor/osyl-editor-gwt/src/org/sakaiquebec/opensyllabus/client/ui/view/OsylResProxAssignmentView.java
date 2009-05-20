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

package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAssignmentEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * Class providing display and edition capabilities for Assignment resources.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxAssignmentView extends OsylAbstractResProxView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxAssignmentView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylAssignmentEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDDEN METHODS ===================== See
     * superclass for javadoc!
     */

    /**
     * The assignment editor displays two editable fields. For this reason,
     * updateModel(String) should not be used as it does not explicitly refer to
     * one of these fields. Use
     * {@link OsylResProxAssignmentView#updateModelDesc(String)} or
     * {@link OsylResProxAssignmentView#updateModelLink(String)} instead.
     */
    protected void updateModel(String text) {
	throw new IllegalStateException(
		"Do not use updateModel(String) for assignments.");
    }

    protected void updateModel() {
	updateMetaInfo();
	OsylAssignmentEditor asnEditor = ((OsylAssignmentEditor) getEditor());
	// setProperty(COPropertiesType.TEXT, asnEditor.getTextDesc());
	getModel().setLabel(asnEditor.getText());
	updateSakaiToolInfo();
    }

    /**
     * Overridden to delete the assignment in the Sakai Assignment Tool.
     */
    @Override
    public void updateModelOnDelete() {
	// We delete the model
	super.updateModelOnDelete();

	if (!getController().isInHostedMode()) {
	    // For now we remove the asn without confirmation as it is useless
	    // to keep it (we don't have an asn browser to select it later).
	    getController().removeAssignment(getAssignmentId());
	}
	/*
	 * // TODO: i18n OsylOkCancelDialog yesNoDialog = new
	 * OsylOkCancelDialog( false, true, "Attention",
	 * "Delete the assignment (in the Sakai tool) too?", "Yes", "No");
	 * yesNoDialog.addOkButtonCLickListener(new ClickListener() { public
	 * void onClick(Widget sender) { try { // We delete the assignment in
	 * the Sakai Assignment Tool
	 * getController().removeAssignment(getAssignmentId()); } catch
	 * (Exception e) { e.printStackTrace(); OsylAlertDialog alert = new
	 * OsylAlertDialog( "Unable to delete the assignment: " + e);
	 * alert.show(); } } }); yesNoDialog.show();
	 * yesNoDialog.centerAndFocus();
	 */
    }

    /**
     * =========================== ADDED METHODS ===========================
     */

    public String getTextFromModel() {
	throw new IllegalStateException("Do not use getTextFromModel() for "
		+ "assignments.");
    }

    /**
     * Returns the text value of current model.
     */
    public String getTextFromModelLink() {
	String text = getModel().getLabel();
	if (getEditor().isInEditionMode()) {
	    return text;
	} else {
	    return generateHTMLLink(getAssignmentURI(), text);
	}
    }

    /**
     * ===================== PRIVATE METHODS ===================== These methods
     * are used to bridge the gap with the Sakai Assignment Tool
     */

    /**
     * Reads information from the Course Outline model and use it to update the
     * assignment in Sakai Assignment Tool.
     */
    private void updateSakaiToolInfo() {

	// We first get the link label
	String linkLabel = getModel().getLabel();

	// Then we get its ID and update the assignment.
	//
	// Of course, we don't do this if we are in hosted mode. Note: we could
	// check if we are in hosted mode at the beginning of the method but
	// if the code above generates an exception, we would still see it in
	// hosted mode and that would mean earlier bug discovery.
	if (!getController().isInHostedMode()) {
	    String assignmentId = getAssignmentId();

	    // And at last, the RPC call
	    getController().createOrUpdateAssignment(getModel(), assignmentId,
		    linkLabel);
	}

    }

    /**
     * Gets the assignment ID from URI in the model (in properties)
     * 
     * @return uri
     */
    private String getAssignmentId() {
	String assignmentURI = getAssignmentURI();
	String rawAssignmentId = assignmentURI.split("\\s*/a/\\s*")[1];
	rawAssignmentId = rawAssignmentId.split("\\s*/\\s*")[1];
	return rawAssignmentId.split("\\s*&panel=\\s*")[0];
    }

    /**
     * Gets the assignment URI from the model (in properties)
     * 
     * @return uri
     */
    private String getAssignmentURI() {
	return getModel().getProperty(COPropertiesType.URI);
    }

}
