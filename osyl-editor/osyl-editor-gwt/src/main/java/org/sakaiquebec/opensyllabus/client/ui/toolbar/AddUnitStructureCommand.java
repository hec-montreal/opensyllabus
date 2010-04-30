/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.toolbar;

import java.util.List;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AddUnitStructureCommand implements Command {

    private COElementAbstract parentModel;
    private String type;
    private OsylController controller = OsylController.getInstance();

    public AddUnitStructureCommand(final COElementAbstract parentModel,
	    String type) {
	this.parentModel = parentModel;
	this.type = type;
    }

    @SuppressWarnings("unchecked")
    public void execute() {
	COUnitStructure cous =
		COUnitStructure.createDefaultCOUnitStructure(type, controller
			.getCoMessages(), parentModel);
	cous
		.setLabel(controller
			.getUiMessage("ASMUnitStructure.label.default"));
	try {
	    List<COModelInterface> subModels =
		    controller.getOsylConfig().getOsylConfigRuler()
			    .getAllowedSubModels(cous);
	    if (!subModels.isEmpty()) {
		COUnitContent.createDefaultCOContentUnit(subModels.get(0)
			.getType(), controller.getCoMessages(), cous);
	    }
	} catch (RuntimeException e) {
	    e.printStackTrace();
	    Window.alert("Error while parsing rules: " + e);
	}

    }

}
