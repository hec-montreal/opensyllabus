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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOStructureItemEditor;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureItemLabelView extends OsylAbstractView {

    public OsylCOStructureItemLabelView(COElementAbstract model,
	    OsylController controller, boolean isDeletable, String levelStyle) {
	super(model, controller, controller.getOsylConfig()
		.getSettings().isUnitViewTitleLabelEditable());
	setEditor(new OsylCOStructureItemEditor(this));
	((OsylCOStructureItemEditor) getEditor()).setIsDeletable(isDeletable);
	((OsylCOStructureItemEditor) getEditor()).setViewerStyle(levelStyle);
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    public COElementAbstract getModel() {
	return (COElementAbstract) super.getModel();
    }

    public String getTextFromModel() {
	return getModel().getLabel();
    }

    protected void updateModel() {
	getModel().setLabel(getEditor().getText());
	setModifiedDateToNow();
    }
}
