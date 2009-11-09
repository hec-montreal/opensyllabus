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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylNewsEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylNewsView extends OsylAbstractResProxView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylNewsView(COModelInterface model, OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylNewsEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    protected void updateModel() {
	updateMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.TEXT,
		getEditor().getText());
    }

    /**
     * Returns the text value of current model.
     */
    public String getTextFromModel() {
	return getModel().getResource().getProperty(COPropertiesType.TEXT);
    }

    /**
     * ===================== ADDED METHODS =====================
     */
    public String getTimeStampFromModel() {
	return getModel().getResource().getProperty(COPropertiesType.MODIFIED);
    }

    @Override
    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.MODIFIED,
		OsylDateUtils.getNowDateAsXmlString());
    }

}
