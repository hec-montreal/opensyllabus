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
 * Les popup s'ouvrent a l'interieur de notre iframepour editer des resources, comme Documents, Reference Bibliographique, etc.
 * Il y a 2 type: 
 *   - niveau 1, sont les premiers popups qui s'ouvrent (exemple quand vous cliquez Modifier sur un document.
 *   - niveau 2: popups qui s'ouvrent a l'interieur des popups de niveau 1.
 *     Exemple: Ajout d'une resource'.
 * @version $Id: $
 */
public abstract class PopupUtils {
    protected static final int BUTTON_SAVE = 0;
    protected static final int BUTTON_OK = 1;
    protected static final int BUTTON_CANCEL = 2;

    /**
     * These classes are to be used by program.
     */
    public static final Level1Popup DocumentEditPopup = new Level1Popup();
    public static final Level2Popup DocumentAttributesPopup = new Level2Popup();
    
    protected abstract int getButtonIndex(int button);
    protected abstract String getXpathToPopup();
    
    /**
     * Examples:
     *  - Level 1 button Modifier: "xpath=(//tr[@class='popupMiddle']//button)[2]"
     *  - Level 2 button Modifier: "xpath=((//tr[@class='popupMiddle'])[2]//button)[2]"
     * @param buttonIndex
     * @return
     */
    protected String getButtonLocator(int button) {
	return "xpath=(" + getXpathToPopup() + "//button)[" + getButtonIndex(button) + "]";
    }

    public String getButtonSave() {
	return getButtonLocator(BUTTON_SAVE);
    }

    public String getButtonOk() {
	return getButtonLocator(BUTTON_OK);
    }

    public String getButtonCancel() {
	return getButtonLocator(BUTTON_CANCEL);
    }
	

    /**
     *
     * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
     * @version $Id: $
     */
    public static class Level1Popup extends PopupUtils {

	@Override
	protected int getButtonIndex(int button) {
	    return button + 1;// we have 3 buttons: Save, Modify, Delete
	}

	@Override
	protected String getXpathToPopup() {
	    return "//tr[@class='popupMiddle']";
	}
    }

    public static class Level2Popup extends PopupUtils {
	
	@Override
	protected int getButtonIndex(int button) {
	    return button;// we have only 2 buttons: Modify, Delete
	}

	@Override
	protected String getXpathToPopup() {
	    return "(//tr[@class='popupMiddle'])[2]";// Level2 popups is the 2nd <tr> of class 'popupMiddle'
	}
	
	@Override
	public String getButtonSave() {
	    throw new RuntimeException("Button Save does not exist for Level2 Popup's.");
	}
    }
}

