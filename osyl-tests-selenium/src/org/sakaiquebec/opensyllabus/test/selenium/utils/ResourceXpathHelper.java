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
    
    private static final int MODIFY_BUTTON = 1;
    private static final int DELETE_BUTTON = 2;
    
    /**
     * Div that contains a resource.
     */
    private static final String RESOURCE_DIV = "//div[contains(@class,'Osyl-UnitView-ResPanel')]";

    /**
     * 
     * @return The number of resource rows. 
     */
    public static int getNbResource() {
	boolean elemPres = session().isElementPresent(RESOURCE_DIV);
	//int nb = session().getXpathCount(RESOURCE_DIV).intValue(); Does not work in some cases!

	/**
	 * getXpathCount() does not work sometimes!
	 * Had to use isElementPresent() !
	 * See http://stackoverflow.com/questions/4962186/selenium-elementpresent-and-xpathcount-give-different-results.
	 */
	int nb = 0;
	do {
	    String locator = "xpath=(" + RESOURCE_DIV + ")[" + (nb+1) + "]";
	    if (!session().isElementPresent(locator)) break;
	    nb++;
	    
	} while (true);
	
	return nb - 1; // -1 because the first resource is for something else!!
    }
    
    
    /**
     * Example: ((//div[contains(@class,'Osyl-UnitView-ResPanel')])[2]//button)[1]
     * @param resourceIndex The resource index, starting from 0.
     * @return The Xpath locator for the modifier button
     */
    private static String getButton(int resourceIndex, int button) {
	return "xpath=((" + RESOURCE_DIV + ")[" + (resourceIndex + 2) + "]" // +2 because first resource is not used, and xpath index starts at 1
		+ "//button)[" + button + "]"; 
    }
    
    /**
     * Locator to the Modifier button.
     * @param resourceIndex
     * @return
     */
    public static String getButtonModify(int resourceIndex) {
	return getButton(resourceIndex, MODIFY_BUTTON);
    }

    /**
     * Locator to the Delete button.
     * @param resourceIndex
     * @return
     */
    public static String getButtonDelete(int resourceIndex) {
	return getButton(resourceIndex, DELETE_BUTTON);
    }
    
    /**
     * Example: 
     * (//div[contains(@class,'Osyl-UnitView-ResPanel')])[5]/../preceding-sibling::node()/div
     * 
     * @param resourceIndex The resource index, starting from 0.
     * @return The Xpath locator for selecting resource while in overview mode.
     */
    public static String getItemHref(int resourceIndex) {
	return "xpath=(" + RESOURCE_DIV + ")[" + (resourceIndex + 2) + "]" // +2 because first resource is not used, and xpath index starts at 1
		+ "/../preceding-sibling::node()/div"; 
    }
}
