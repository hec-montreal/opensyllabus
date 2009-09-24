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
package org.sakaiquebec.opensyllabus.shared.model;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COUnitStructureType {
 // These values must be identical to those found in file rules.xml and the
    // XML representation of Course Outlines.
    public static final String PEDAGOGICAL_UNITSTRUCTURE = "PedagogicalUnitStruct";
    public static final String LEARNING_MATERIAL_UNITSTRUCTURE = "LearningMaterialUnitStruct";
    public static final String OVERVIEW_UNITSTRUCTURE = "OverviewUnitStruct";
    public static final String STAFF_UNITSTRUCTURE = "StaffUnitStruct";
    public static final String ASSESSMENT_UNITSTRUCTURE = "AssessmentUnitStruct";


    /**
     * The list of types.
     */
    private static final String[] types = {

    PEDAGOGICAL_UNITSTRUCTURE, OVERVIEW_UNITSTRUCTURE, STAFF_UNITSTRUCTURE, LEARNING_MATERIAL_UNITSTRUCTURE, ASSESSMENT_UNITSTRUCTURE

    };

    /**
     * @return string array of types
     */
    public static final String[] getTypes() {
	return types;
    }

    /**
     * @return List of types
     */
    public static final List<String> getTypesList() {
	return Arrays.asList(types);
    }
}

