/**********************************************************************************
 * $Id: COConfig.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.List;

/**
 * COConfig is a class used by the UI which concentrate all the OSYL elements of
 * configuration such as, the OsylConfigRuler, the UI localized message map.
 * Each courseoutline display has a reference to an COConfig.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: COConfig.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 */
public class COConfig {

    private COConfigSerialized cOConfigSerialized;

    private OsylConfigRuler osylConfigRuler;

    private OsylConfigMessages i18nMessages;
    
    private List<String> rolesList;
    
    private List<String> evalTypeList;
    
    private List<String> resourceTypeList;    

    private OsylSettings settings;
    
    private String stylesheetPath ;
    
    /**
     * Css file for skin properties
     */
    public static final String MAIN_CSS = "osylcore.css";
    
    /**
     * Css file for skin properties (print)
     */
    public static final String PRINT_CSS = "print.css";
    
    /**
     * Css file for skin properties (readonly)
     */
    public static final String READONLY_CSS = "readonly.css";

    public String getStylesheetPath() {
        return stylesheetPath;
    }

    /**
     * Construct an COConfig and its OsylConfigRuler based on the
     * cOConfigSerialized passed
     * 
     * @param cOConfigSerialized
     */
    public COConfig(COConfigSerialized cOConfigSerialized) {
	this.cOConfigSerialized = cOConfigSerialized;
	this.i18nMessages =
		new OsylConfigMessages(this.cOConfigSerialized
			.getI18nMessages());
	// init ruler
	this.osylConfigRuler =OsylConfigRuler.createInstance(this.cOConfigSerialized.getRulesConfig());
	this.rolesList = cOConfigSerialized.getRolesList();
	this.evalTypeList = cOConfigSerialized.getEvalTypeList();
	this.resourceTypeList = cOConfigSerialized.getResourceTypeList();
	this.settings = 
		new OsylSettings(cOConfigSerialized.getSettings());
	this.stylesheetPath = cOConfigSerialized.getCascadingStyleSheetPath();
    }

    /**
     * @return the osylConfigRuler value.
     */
    public OsylConfigRuler getOsylConfigRuler() {
	return osylConfigRuler;
    }

    /**
     * @return the i18nMessages value.
     */
    public OsylConfigMessages getI18nMessages() {
	return i18nMessages;
    }

	/**
	 * @return the rolesList
	 */
	public List<String> getRolesList() {
		return rolesList;
	}    
    
	/**
	 * @return the evalTypeList
	 */
	public List<String> getEvalTypeList() {
		return evalTypeList;
	}
	
	/**
	 * @return the resourceTypeList
	 */
	public List<String> getResourceTypeList() {
		return resourceTypeList;
	} 	

    /**
     * @return the general settings.
     */
    public OsylSettings getSettings() {
	return settings;
    }

}
