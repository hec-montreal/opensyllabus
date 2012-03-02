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
 * * Nice youtube intro to Java with Selenium. http://www.youtube.com/watch?v=Eft3qGFoqwE.
 * * http://wiki.openqa.org/display/SEL/Help+With+XPath.
 * 
 * @version $Id: $
 */
public class LocatorUtils {
    
    private static final String RESOURCE_DIV = "//div[contains(@class,'Osyl-UnitView-ResPanel')]";

    /**
     * @return The number of resource rows. 
     */
    public static int getNbResource() {
	int nb = session().getXpathCount(RESOURCE_DIV).intValue();
	return nb - 1; // -1 because the first resource is for something else!!
    }
    
    /**
     * ((//div[contains(@class,'Osyl-UnitView-ResPanel')])[5]//button)[2]
     * @param resourceIndex The resource index, starting from 0.
     * @return The Xpath locator for the modifier button
     */
    public static String getXpathForModifierButton(int resourceIndex) {
	return "xpath=((" + RESOURCE_DIV + ")[" + (resourceIndex + 2) + "]" // go to nth resource div containing our 'Modifier' button  
		+ "//button)[1]"; // our modifier button is the first one
    }
    
    /**
     * Explanation: ((//div[contains(@class,'Osyl-UnitView-ResPanel')])[5]//button)[2]
     *    The table 'Osyl-WorkspaceView-MainPanel' contains n+1 rows.
     *    The first row is an unused row.
     *    The next row contains the 'Modifier' and 'Supprimer' button allowing to modify the seance. 
     * @param seancePos
     * @return
     */
    private String getModifierButtonLocator(int seancePos) {
	return "//table[@class='Osyl-WorkspaceView-MainPanel']/tbody/tr[" + (seancePos + 1) + "]//*[contains(text(),'Modifier')]";
    }
    
    



}
