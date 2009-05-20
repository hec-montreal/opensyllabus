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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylEvaluationEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * Class providing display and edition capabilities for Evaluation resources.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxEvaluationView extends OsylAbstractResProxView {
    
    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     *  
     * @param model
     * @param osylController
     */
    protected OsylResProxEvaluationView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylEvaluationEditor(this));
	initView();
    }

    
    /**
     * ===================== OVERRIDDEN METHODS =====================
     * See superclass for javadoc!  
     */


    public String getTextFromModel() {
	throw new IllegalStateException("Do not use getTextFromModel() for " +
			"contactInfos.");
    }

    // Title
    public String getTextFromModelTitle() {
	return getModel().getLabel();
    }

    // Last Name
    public String getTextFromModelDescription() {
	return getModel().getResource().getProperty(
		COPropertiesType.TEXT);
    }

    // Rating
    public String getTextFromModelRating() {
	return getModel().getResource().getProperty(
		COPropertiesType.RATING);
    }

    // Date
    public String getTextFromModelDate() {
	return getModel().getResource().getProperty(
		COPropertiesType.DATE);
    }


    /**
     * Overridden to delete the evaluation in the Sakai Evaluation Tool.
     */
    public void updateModelOnDelete() {
	// We delete the model
	super.updateModelOnDelete();
    }

    protected void updateModel() {
	updateMetaInfo();
	OsylEvaluationEditor editor = (OsylEvaluationEditor) getEditor();
	getModel().setLabel(editor.getTextTitle());
	setProperty(COPropertiesType.TEXT, editor.getTextDescription());
	setProperty(COPropertiesType.RATING, editor.getTextRating());
	setProperty(COPropertiesType.DATE, editor.getTextDate());
    }


}
