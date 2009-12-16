/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jean-marc.bleau@umontreal.ca">Jean-Marc Bleau</a>
 * @version $Id: $
 */
public class OsylSettings {

    private static final String TREEVIEW_SHOWRATE =
	    "treeview.assessmentunit.showrate";
    private static final String UNITVIEW_ALL_TYPES_TITLELABEL_EDITABLE =
	    "unitview.all.titlelabel.editable";
    private static final String STRUCTVIEW_ALL_TITLELABEL_EDITABLE =
	    "structview.all.titlelabel.editable";
    private static final String TITLELABEL_EDITABLE = ".titlelabel.editable";
    private static final String UNITVIEW = "unitview.";
    private static final String STRUCTVIEW = "structview.";
    private static final String TREEVIEW_ASSESSMENT_NUMBERING = "treeview.assessmentunit.numbering"; 
	private static final String TREEVIEW_SESSION_NUMBERING = "treeview.pedagogicalunit.numbering"; 
    

    private Map<String, String> settings;

    /**
     * Construct a new class of messages for course outlines or user interface
     * with the map
     * 
     * @param messages
     */
    public OsylSettings(Map<String, String> settings) {
	this.settings = new HashMap<String, String>();
	this.settings.putAll(settings);
    }

    /**
     * Return the setting corresponding to the parameter key
     * 
     * @param key
     * @return the setting String
     */
    public String getSettingsPropertie(String key) {
	String message = (String) settings.get(key);
	if (message == null)
	    return "Missing key: " + key;
	else
	    return message;
    }

    /**
     * Set settings map
     * 
     * @param settings
     */
    public void setSettings(Map<String, String> settings) {
	this.settings = new HashMap<String, String>();
	this.settings.putAll(settings);

    }

    /**
     * Return a map containing the settings
     * 
     * @return the map <key,setting>
     */
    public Map<String, String> getSettings() {
	return settings;
    }

    /**
     * @param key
     * @return true if the maps contains the key
     */
    public boolean containsKey(String key) {
	return this.settings.containsKey(key);
    }

    /**
     * For boolean option properties
     * 
     * @param option from the propertie file
     * @param defaultValue the default Values when, the option is not specified
     * @return value
     */
    private boolean checkBooleanOption(String option, boolean defaultValue) {
	boolean res = defaultValue;
	if (settings.containsKey(option)) {
	    String key = getSettingsPropertie(option);
	    res = Boolean.parseBoolean(key);
	}
	return res;
    }

    /**
     * @return true if the treeview rate showing for assessment is set
     */
    public boolean isTreeViewAssessmentShowRate() {
	return checkBooleanOption(TREEVIEW_SHOWRATE, false);
    }

    /**
     * @return true if the StructView TitleLabel can be editable
     */
    public boolean isStructViewTitleLabelEditable(String type) {
	if (checkBooleanOption(STRUCTVIEW_ALL_TITLELABEL_EDITABLE, false))
	    return true;
	else
	    return checkBooleanOption(STRUCTVIEW + type + TITLELABEL_EDITABLE,
		    false);
    }

    /**
     * @param type the type of the unit
     * @return true if the UnitView TitleLabel can be editable
     */
    public boolean isUnitViewTitleLabelEditable(String type) {
	if (checkBooleanOption(UNITVIEW_ALL_TYPES_TITLELABEL_EDITABLE, false))
	    return true;
	else
	    return checkBooleanOption(UNITVIEW + type + TITLELABEL_EDITABLE,
		    false);
    }

    /**
     * @param key
     * @return true if the maps contains the TREEVIEW_ASSESSMENT_NUMBERING key
     * and it's value is set to true
     */
    public boolean isTreeViewAssessmentNumbering(){
		return checkBooleanOption(TREEVIEW_ASSESSMENT_NUMBERING, true);
    }

    /**
     * @param key
     * @return true if the maps contains the TREEVIEW_SESSION_NUMBERING key
     * and it's value is set to true
     */
    public boolean isTreeViewSessionNumbering(){
		return checkBooleanOption(TREEVIEW_SESSION_NUMBERING, true);
    }


}
