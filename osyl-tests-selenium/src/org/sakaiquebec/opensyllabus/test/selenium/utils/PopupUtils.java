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
/**
 *
 * Les popup dans notre cas sont les fenetre qui s'ouvrent pour editer des resources, comme des Documnents, Reference Bibliographique.
 * @version $Id: $
 */
public class PopupUtils {
    public static final int BUTTON_SAVE = 1;
    public static final int BUTTON_MODIFY = 2;
    public static final int BUTTON_DELETE = 3;

    public static final int FIRST_LEVEL_POPUP = 1;
    public static final int SECOND_LEVEL_POPUP = 2;
    
    private static final String POPUP_XPATH = "//tr[@class='popupMiddle']";
    //private static final String POPUP_XPATH = "//tr[@class='popupMiddle']";
    
 

    public static String getButtonLocator(int buttonIndex ) {
	return "xpath=(" + POPUP_XPATH + "//button)[" + buttonIndex + "]";
    }
}

