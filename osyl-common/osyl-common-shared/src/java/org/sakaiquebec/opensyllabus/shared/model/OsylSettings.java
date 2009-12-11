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
 *
 * @author <a href="mailto:jean-marc.bleau@umontreal.ca">Jean-Marc Bleau</a>
 * @version $Id: $
 */
public class OsylSettings {

	public static final String TREEVIEW_SHOWRATE = "treeview.assessmentunit.showrate"; 
    private Map<String,String> settings;

    /**
     * Construct a new class of messages for course outlines or user interface
     * with the map 
     * @param messages
     */
    public OsylSettings(Map<String,String> settings) {
	this.settings = new HashMap<String,String>();
	this.settings.putAll(settings);
    }

    /**
     * Return the setting corresponding to the parameter key
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
     * @param settings
     */
    public void setSettings(Map<String,String> settings) {
	this.settings = new HashMap<String,String>();
	this.settings.putAll(settings);

    }

    /**
     * Return a map containing the settings
     * @return the map <key,setting>
     */
    public Map<String,String> getSettings() {
	return settings;
    }
    
    /**
     * @param key
     * @return true if the maps contains the key
     */
    public boolean containsKey(String key){
    	return this.settings.containsKey(key);    
    }

    /**
     * @param key
     * @return true if the maps contains the TREEVIEW_SHOWRATE key
     */
    public boolean isTreeViewAssessmentShowRate(){
    	boolean showRate = false;
    	
    	if(this.settings.containsKey(TREEVIEW_SHOWRATE)){
    		String strShowRate = this.getSettingsPropertie(TREEVIEW_SHOWRATE);
    		showRate = Boolean.parseBoolean(strShowRate);
    	}
    	return showRate;
    }


}
