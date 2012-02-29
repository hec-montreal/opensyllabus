/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.test.selenium.utils;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * The 'resources' panel is the right panel in the OpenSyllabus split panel (like when you open the Plan de cours.)
 * It contains resource elements of the selected item in the left panel.
 * 
 * @version $Id: $
 */
public class ResourceXpathHelper {
    
    public static final int MODIFY_BUTTON = 1;
    public static final int DELETE_BUTTON = 2;
    
    /**
     * Div that contains a resource.
     */
    private static final String RESOURCE_DIV = "//div[contains(@class,'Osyl-UnitView-ResPanel')]";

    /**
     * @return The number of resource rows. 
     */
    public static int getNbResource() {
	int nb = session().getXpathCount(RESOURCE_DIV).intValue();
	return nb - 1; // -1 because the first resource is for something else!!
    }
    
    
    /**
     * Example: ((//div[contains(@class,'Osyl-UnitView-ResPanel')])[5]//button)[1]
     * @param resourceIndex The resource index, starting from 0.
     * @return The Xpath locator for the modifier button
     */
    public static String getButton(int resourceIndex, int button) {
	return "xpath=((" + RESOURCE_DIV + ")[" + (resourceIndex + 2) + "]" // +2 because first resource is not used, and xpath index starts at 1
		+ "//button)[" + button + "]"; 
    }
    
    /**
     * Example: 
     * (//div[contains(@class,'Osyl-UnitView-ResPanel')])[5]/../preceding-sibling::node()/div
     * 
     * @param resourceIndex The resource index, starting from 0.
     * @return The Xpath locator for selecting resource while in overview mode.
     */
    public static String getOverviewHref(int resourceIndex) {
	return "xpath=(" + RESOURCE_DIV + ")[" + (resourceIndex + 2) + "]" // +2 because first resource is not used, and xpath index starts at 1
		+ "/../preceding-sibling::node()/div"; 
    }
}
