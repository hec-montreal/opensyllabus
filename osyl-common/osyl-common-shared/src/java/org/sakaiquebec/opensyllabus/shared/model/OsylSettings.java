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

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author <a href="mailto:jean-marc.bleau@umontreal.ca">Jean-Marc Bleau</a>
 * @version $Id: $
 */
public class OsylSettings {

    // tree rate
    private static final String SHOWRATE = ".showrate";

    // Title editable
    private static final String ALL = "all";
    private static final String TITLELABEL_EDITABLE = ".titlelabel.editable";
    private static final String NESTED = "nested.";

    // numbering
    private static final String TREEVIEW = "treeview.";
    private static final String NUMBERING = ".numbering";

    // date format
    private static final String FORMAT_DATE = "format.date";
    private static final String FORMAT_DATE_TIME = "format.date_time";

    // rubric editable
    private static final String RUBRIC_DESCRIPTION_EDITABLE =
	    "rubric.description.editable";

    // citation display
    private static final String FORMAT_CITATION = "format.citation.";

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
    public String getSettingsProperty(String key) {
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
	    String key = getSettingsProperty(option);
	    res = Boolean.parseBoolean(key);
	}
	return res;
    }

    /**
     * @return true if the treeview rate showing for assessment is set
     */
    public boolean isTreeViewAssessmentShowRate(COElementAbstract model) {
	return checkBooleanOption(TREEVIEW + model.getType() + SHOWRATE, false);
    }

    public boolean isModelTitleEditable(COElementAbstract model) {
	String type = model.getType();
	String classType = model.getClassType();
	if (checkBooleanOption(classType + "." + ALL + TITLELABEL_EDITABLE,
		false))
	    return true;
	else if (checkBooleanOption(classType + "." + type
		+ TITLELABEL_EDITABLE, false))
	    return true;
	else if (model.isNested())
	    return checkBooleanOption(classType + "." + NESTED + type
		    + TITLELABEL_EDITABLE, false);
	else
	    return false;
    }

    public boolean isTreeViewNumbering(COElementAbstract model) {
	return checkBooleanOption(TREEVIEW + model.getType() + NUMBERING, true);
    }

    public DateTimeFormat getDateFormat() {
	String f = "dd/MM/yyyy";// default format
	if (settings.containsKey(FORMAT_DATE))
	    f = getSettingsProperty(FORMAT_DATE);

	return DateTimeFormat.getFormat(f);
    }

    public DateTimeFormat getDateTimeFormat() {
	String f = "dd/MM/yyyy HH:mm:ss";// default format
	if (settings.containsKey(FORMAT_DATE_TIME))
	    f = getSettingsProperty(FORMAT_DATE_TIME);

	return DateTimeFormat.getFormat(f);
    }

    /**
     * @return true if the rubric description can be editable
     */
    public boolean isRubricDescEditable() {
	return checkBooleanOption(RUBRIC_DESCRIPTION_EDITABLE, false);
    }

    public String getCitationFormat(String type) {
	String format = null;
	if (settings.containsKey(FORMAT_CITATION + type))
	    format = getSettingsProperty(FORMAT_CITATION + type);
	return format;

    }

}
