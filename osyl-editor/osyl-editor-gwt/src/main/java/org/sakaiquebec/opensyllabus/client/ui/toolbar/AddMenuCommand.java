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
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;

import com.google.gwt.user.client.Command;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AddMenuCommand implements Command{
    
    	private COElementAbstract parentModel;
	private COModelInterface aModel;
	private COModelInterface aSubModel;
	private OsylController controller = OsylController.getInstance();

	public AddMenuCommand(final COElementAbstract parentModel,
		COModelInterface m) {
	    this(parentModel,m,null);
	}

	public AddMenuCommand(final COElementAbstract parentModel,
		COModelInterface m, COModelInterface subM) {
	    this.parentModel = parentModel;
	    this.aModel = m;
	    this.aSubModel = subM;
	}

	@SuppressWarnings("unchecked")
	public void execute() {
	    if (parentModel.isCOStructureElement()) {

		if (aModel instanceof COStructureElement) {
		    COStructureElement cose =
			    COStructureElement.createDefaultCOStructureElement(
				    aModel.getType(), controller.getCoMessages(),
				    parentModel);
		    cose.setLabel( controller.getUiMessage("ASMStructure.label.default"));
		} else if (aModel instanceof COUnit) {
		    COUnit coU =
			    COUnit.createDefaultCOUnit(aModel.getType(),
				    controller.getCoMessages(), parentModel);
		    try {
			List<COModelInterface> coUnitSubModels =
			    controller.getOsylConfig()
					.getOsylConfigRuler()
					.getAllowedSubModels(coU);
			if (!coUnitSubModels.isEmpty()) {
			    COUnitStructure cous =
				    COUnitStructure
					    .createDefaultCOUnitStructure(
						    coUnitSubModels.get(0)
							    .getType(),
							    controller.getCoMessages(), coU);
			    cous
				    .setLabel( controller.getUiMessage("ASMUnitStructure.label.default"));
			    List<COModelInterface> coUnitStructureSubModels =
				 controller.getOsylConfig()
					    .getOsylConfigRuler()
					    .getAllowedSubModels(cous);
			    if (!coUnitStructureSubModels.isEmpty())
				COUnitContent.createDefaultCOContentUnit(
					coUnitStructureSubModels.get(0)
						.getType(),  controller.getCoMessages(),
					cous);
			}

		    } catch (RuntimeException e) {
			// TODO: handle exception
		    }
		}

	    } else if (parentModel.isCOUnit()) {
		// TODO change this when multiple coUnitContent under one COUnit
		// will be allowed
		COUnitContent coUnitContent =
			(COUnitContent) parentModel.getChildrens().get(0);
		createASMContext(coUnitContent);

	    } else if (parentModel.isCOUnitStructure()) {
		// TODO change this when multiple coUnitContent under one COUnit
		// will be allowed
		COUnitContent coUnitContent =
			(COUnitContent) parentModel.getChildrens().get(0);
		createASMContext(coUnitContent);
	    }
	}

	private void createASMContext(COUnitContent coUnitContent) {
	    String defaultRubric = "";

	    // NEWS HACK
	    if (aSubModel.getType().equals(COContentResourceType.NEWS)) {
		defaultRubric = COContentRubric.RUBRIC_TYPE_NEWS;
	    } else {
		List<COModelInterface> coUnitSubModels =
		    controller.getOsylConfig().getOsylConfigRuler()
				.getAllowedSubModels(coUnitContent);

		for (int i = 0; i < coUnitSubModels.size(); i++) {
		    Object subModel = coUnitSubModels.get(i);

		    if (subModel instanceof COContentRubric) {
			COContentRubric coContentRubric =
				(COContentRubric) subModel;
			if (!coContentRubric.getType().equals(
				COContentRubric.RUBRIC_TYPE_NEWS)) {
			    defaultRubric = coContentRubric.getType();
			    break;
			}
		    }
		}
	    }
	    COContentResourceProxy.createDefaultResProxy(aModel.getType(),
		    controller.getCoMessages(), coUnitContent, aSubModel.getType(),
		    defaultRubric,  controller.getOsylConfig()
			    .getOsylConfigRuler().getPropertyType());
	}
}

