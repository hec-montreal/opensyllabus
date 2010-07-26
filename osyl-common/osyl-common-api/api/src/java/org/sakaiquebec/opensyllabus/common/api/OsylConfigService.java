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

package org.sakaiquebec.opensyllabus.common.api;

import java.io.IOException;
import java.util.Map;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * OsylConfigService defines all calls related to the course outline, the
 * manager and the administrator configuration. It allows to retrieve language
 * or stylesheet configuration for the course outline and interface
 * configurations such as the messages for the tools (OsylEditor, OsylManager
 * and OsylAdmin).
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylConfigService {

    /**
     * Message properties file base-name
     */
    public static final String CONFIG_UIMESSAGES = "UIMessages";

    /**
     * Message file
     */
    public static final String CONFIG_COMESSAGES = "COMessages";

    /**
     * Message file
     */
    public static final String CONFIG_SETTINGS = "settings";

    /**
     * Package (ie: folder) containing the message files (.properties)
     */
    public static final String CONFIG_DIR = "osylcoconfigs";
    
    /**
     * Creates the following configurations: "default" -
     * "Config from HEC Montreal" "udem" - "Config udem"
     * "udemCompetencesComposantes" - "Config UdeM - Competences Composantes"
     * "udemCompetencesSeances" - "Config UdeM - Competences Seances"
     * "udemObjectifsActivites" - "Config UdeM - Objectifs Activites"
     * "udemObjectifsSeances" - "Config UdeM - Objectifs Seances"
     * 
     * @throws Exception
     */
    public void initConfigs() throws Exception;

    /**
     * Default configs
     */
    public static final String DEFAULT_CONFIG_REF = "default";

    public static final String PRINT_DIRECTORY="print";
    public static final String PRINT_XSLFO_FILENAME = "printOSYLFO.xslt";
    
    /**
     * Returns the configurations related to the id
     * 
     * @return A COConfig
     * @throws Exception
     */
    public COConfigSerialized getConfig(String configId, String webappDir)
	    throws Exception;

    /**
     * Returns the relative path for CSS files given the configuration ID.
     * 
     * @return css path
     * @throws Exception
     */
    public String getCssPathFromConfigId(String webappDir, String configId)
    	    throws Exception;
    
    /**
     * Returns the relative path for CSS files given the configuration Ref.
     * 
     * @return css path
     * @throws Exception
     */
    public String getCssPathFromConfigRef(String webappDir, String configRef)
    	    throws Exception;
    
    /**
     * Returns all configs avaiable
     * 
     * @return A Map<Id,Description>
     * @throws Exception
     */
    public Map<String, String> getConfigs() throws Exception;

    /**
     * Returns the config
     * 
     * @param configId
     * @return A String representation of the xml config
     * @throws Exception
     */
    public COConfigSerialized getConfigByRef(String configRef, String webappDir)
	    throws Exception;

    /**
     * Returns the configurations with configId as id.
     * 
     * @param configId
     * @return A COConfig
     * @throws Exception
     */
    public COConfigSerialized getCourseOutlineConfig(String configId)
	    throws Exception;

    /**
     * Creates a new COConfig
     * 
     * @param coConfig
     * @throws Exception
     */
    public void createConfig(COConfigSerialized coConfig) throws Exception;

    /**
     * Removes the COConfig
     * 
     * @param configId
     * @throws Exception
     */
    public void removeConfig(String configId) throws Exception;

    /**
     * Updates the COConfig
     * 
     * @param coConfig
     * @throws Exception
     */
    public void updateConfig(COConfigSerialized coConfig) throws Exception;

    /**
     * First we get the COConfigSerialized and we fill the following fields with
     * informations from the files in the folder specified in the database. -
     * the xml rules file is put into a String - the message file is first
     * chosen depending on the locale of the user then put into a map - for the
     * css, we build the relative path to the file
     * 
     * @return COConfigSerialized
     * @throws IOException
     */
    public COConfigSerialized getCurrentConfig(String webappDir)
	    throws Exception;

    /**
     * For a given directory, this method reads the messages file and put it
     * into a map.
     * 
     * @param dir Configuration directory
     * @return Map
     * @throws Exception
     */
    public Map<String, String> getMessages(String path, String baseFileName,
	    String locale) throws Exception;

    /**
     * Get the locale of the actual session.
     * 
     * @return a String representation of the locale.
     */
    public String getCurrentLocale() throws Exception;

    /**
     * Private method used to fill out some variables of a COConfigSerialiazed:
     * the css URI, the String representation of the rules and a map
     * representation of the messages.
     * 
     * @param dir
     * @return Filled COConfigSerialized
     * @throws Exception
     */
    public COSerialized fillCo(String dir, COSerialized coSerialized)
	    throws Exception;

    /**
     * For a given directory, this method reads the general settings file and put it
     * into a map.
     * 
     * @param dir Configuration directory
     * @return Map
     * @throws Exception
     */
    public Map<String, String> getSettings(String path, String baseFileName) throws Exception;

	public String getDefaultConfig();
}
