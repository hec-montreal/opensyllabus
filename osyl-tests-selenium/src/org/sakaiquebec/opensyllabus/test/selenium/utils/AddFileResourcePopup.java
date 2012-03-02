package org.sakaiquebec.opensyllabus.test.selenium.utils;

import org.sakaiquebec.opensyllabus.test.selenium.utils.PopupUtils.Level2Popup;
/**
 * Popup window 'Ajout d'une resource'.
 * Allow to select a file and specify Rights and Type of Resource.
 * 
 * @version $Id: $
 */
public class AddFileResourcePopup extends Level2Popup {

    private static final AddFileResourcePopup instance = new AddFileResourcePopup();

    public static AddFileResourcePopup getInstance() {
	return instance;
    }

    public static String getRightsSelect() {
	return "//form/table/tbody/tr[5]/td/select";
    }

    public static String getTypeOfResourceSelect() {
	return "//form/table/tbody/tr[7]/td/select";
    }
};

