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

package org.sakaiquebec.opensyllabus.common.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;

/**
 * Implementation of the {@link OsylConfigService} interface.
 * 
 * @author <a href="mailto:jean-marc.bleau@umontreal.ca">Jean-Marc Bleau</a>
 * @version $Id: $
 */
//Moved from OsylConfigServiceImpl
final class OsylConfigSettingsService {

    private static Log log = LogFactory.getLog(OsylConfigSettingsService.class);


    private static final String PROPERTIES_EXTENSION = ".properties";

    private OsylConfigSettingsService() {
        
    }
    
    
    public static Map<String, String> getSettings(String path, String baseFileName) throws Exception {
        try {

            String key;
            String dir = path + File.separator;

            File settingsFile = new File(dir, baseFileName + PROPERTIES_EXTENSION);

            // The file is not found for this locale, use default.
            if (!settingsFile.exists()) {
                settingsFile = new File(dir, baseFileName
                        + PROPERTIES_EXTENSION);
                log.warn("Cannot load property file: " + baseFileName + PROPERTIES_EXTENSION);
            }

            FileInputStream inputStream = new FileInputStream(settingsFile);
            PropertyResourceBundle bundle = new PropertyResourceBundle(
                    inputStream);

            // We transform our ResourceBundle to a Map to return it.
            Map<String, String> settings = new HashMap<String, String>();
            Enumeration<String> enu = bundle.getKeys();
            while (enu.hasMoreElements()) {
                key = enu.nextElement();
                settings.put(key, bundle.getString(key));
            }
            
            inputStream.close();
            return settings;

        } catch (Exception e) {
            log.warn("Unable to retrieve  settings", e);
            return new HashMap<String, String>();
        }
    }
}
