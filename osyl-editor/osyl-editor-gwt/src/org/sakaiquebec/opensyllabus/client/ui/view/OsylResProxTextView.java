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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylRichTextEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * Class providing display and edition capabilities for Text resources.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @version $Id: $
 */
public class OsylResProxTextView extends OsylAbstractResProxView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     *  
     * @param model
     * @param osylController
     */
    protected OsylResProxTextView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylRichTextEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS =====================
     * See superclass for javadoc!  
     */

    protected void updateModel() {
	updateMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.TEXT,
		getEditor().getText());
    }

    
    /**
     * ===================== ADDED METHODS =====================
     */

    /**
     * Returns the text value of current model.
     */
    public String getTextFromModel() {
	return getModel().getResource().getProperty(COPropertiesType.TEXT);
    }

}
