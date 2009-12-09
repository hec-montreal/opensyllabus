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
package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylLabelEditor;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitStructureLabelView extends OsylAbstractView {

    public OsylCOUnitStructureLabelView(COUnitStructure model,
	    OsylController controller) {
	super(model, controller);
	setEditor(new OsylLabelEditor(this));
	initView();
    }
    
    public OsylCOUnitStructureLabelView(COUnitStructure model,
	    OsylController controller, String styleLevel) {
	super(model, controller);
	setEditor(new OsylLabelEditor(this));
	initView();
	((OsylLabelEditor)getEditor()).setViewerStyle(styleLevel);
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public String getTextFromModel() {
	return ((COUnitStructure) getModel()).getLabel();
    }

    protected void updateModel() {
	((COUnitStructure) getModel()).setLabel(getEditor().getText());
	setModifiedDateToNow();
    }
}
