/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.util.OsylStyleLevelChooser;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

/**
 * This view is used to display a COContentUnit in a list view, for instance a
 * lecture in a list of Lecture.
 * 
 * @version $Id: $
 */
public class OsylCOUnitAssessmentHyperlinkView extends OsylCOUnitHyperlinkView {

    // View Constructor
    public OsylCOUnitAssessmentHyperlinkView(COUnit model,
	    OsylController controller) {
	super(model, controller, new OsylCOUnitAssessmentLabelView(model,
		controller, true, OsylStyleLevelChooser.getLevelStyle(model)));
    }

}
