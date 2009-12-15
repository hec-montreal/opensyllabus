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
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureLabelView extends OsylAbstractView {

	public OsylCOStructureLabelView(COElementAbstract model,
			OsylController controller, boolean isDeletable, String levelStyle) {
		super(model, controller, controller.getOsylConfig()
				.getSettings().isStructViewTitleLabelEditable(model.getType()));
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
		String label = getModel().getLabel();
		if (label == null) {
			label = getTextFromType();
		}
		return label;
	}

	private String getTextFromType() {
		return getCoMessage(getModel().getType());
	}

	protected void updateModel() {
		String newLabel = getEditor().getText();
		// Note: update of label is not required if label text is based on type
		// information. i.e. we don't want to override the text based on the type with a label with an 
		// equivalent string.
		if (!newLabel.equals(getTextFromType())) {
			// here an update is required
			getModel().setLabel(newLabel);
		} else {
			// here we restore the label to null, (i.e. we remove the property)...
			String label = getModel().getLabel();
			if (label != null) {
				getModel().removeProperty(COPropertiesType.LABEL);
			}
		}
	}
}
