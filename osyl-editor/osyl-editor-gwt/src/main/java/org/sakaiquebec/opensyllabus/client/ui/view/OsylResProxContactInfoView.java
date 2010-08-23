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
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylContactInfoEditor;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;

/**
 * Class providing display and edition capabilities for Assignment resources.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 * @version $Id: $
 */
public class OsylResProxContactInfoView extends OsylAbstractResProxView {

    /**
     * Constructor specifying the model to display and edit as well as the
     * current {@link OsylController}.
     * 
     * @param model
     * @param osylController
     */
    protected OsylResProxContactInfoView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	setEditor(new OsylContactInfoEditor(this));
	initView();
    }

    /**
     * ===================== OVERRIDDEN METHODS ===================== See
     * superclass for javadoc!
     */

    @Override
    public String getTextFromModel() {
	throw new IllegalStateException("Do not use getTextFromModel() for "
		+ "contactInfos.");
    }

    protected void updateModel() {
	updateMetaInfo();
	OsylContactInfoEditor editor = (OsylContactInfoEditor) getEditor();
	// save context
	getModel().addProperty(COPropertiesType.AVAILABILITY,
		editor.getTextAvailability());
	getModel().addProperty(COPropertiesType.COMMENT,
		editor.getTextComments());
	// save resource
	if (!editor.getTextRole().equals("")) {
	    setProperty(COPropertiesType.PERSON_TITLE, editor.getTextRole());
	} else {
	    getModel().getResource().removeProperty(
		    COPropertiesType.PERSON_TITLE);
	}

	setProperty(COPropertiesType.SURNAME, editor.getTextLastName());
	setProperty(COPropertiesType.FIRSTNAME, editor.getTextFirstName());
	setProperty(COPropertiesType.OFFICE_ROOM, editor.getTextOffice());
	setProperty(COPropertiesType.TEL, editor.getTextPhone());
	setProperty(COPropertiesType.EMAIL, editor.getTextEMail());

    }

    /**
     * =========================== ADDED METHODS ===========================
     */

    // Role
    public String getRole() {
	return getProperty(COPropertiesType.PERSON_TITLE) == null ? ""
		: getProperty(COPropertiesType.PERSON_TITLE);
    }

    // Availability
    public String getAvailability() {
	return getModel().getProperty(COPropertiesType.AVAILABILITY);
    }

    // Comments
    public String getComments() {
	return getModel().getProperty(COPropertiesType.COMMENT);
    }

    // Last Name
    public String getLastName() {
	return getProperty(COPropertiesType.SURNAME);
    }

    // FirstName
    public String getFirstName() {
	return getProperty(COPropertiesType.FIRSTNAME);
    }

    // Office
    public String getOffice() {
	return getProperty(COPropertiesType.OFFICE_ROOM);
    }

    // Phone
    public String getPhone() {
	return getProperty(COPropertiesType.TEL);
    }

    // EMail
    public String getEMail() {
	return getProperty(COPropertiesType.EMAIL);
    }

    @Override
    public void updateResourceMetaInfo() {
	super.updateResourceMetaInfo();
	getModel().getResource().addProperty(COPropertiesType.MODIFIED,
		OsylDateUtils.getCurrentDateAsXmlString());
    }
}
