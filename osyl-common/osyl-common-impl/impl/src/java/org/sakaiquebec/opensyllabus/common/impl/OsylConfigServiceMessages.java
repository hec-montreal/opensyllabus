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
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;

/**
 * Implementation of the {@link OsylConfigService} interface.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
//Moved from OsylConfigServiceImpl
final class OsylConfigServiceMessages {

    private static Log log = LogFactory.getLog(OsylConfigServiceMessages.class);

    private static String DEFAULT_LOCALE = "fr_CA";

    private static final String PROPERTIES_EXTENSION = ".properties";

    /**
     * Folder containing the file messages (.properties)
     */
    private static final String MESSAGES_DIRECTORY = "bundle";

    private OsylConfigServiceMessages() {
        
    }
    
    public static Map<String, String> getMessages(String path, String baseFileName) throws Exception {
        return getMessages(path, baseFileName, getCurrentLocale());
    }
    
    public static Map<String, String> getMessages(String path, String baseFileName, String locale) throws Exception {
        try {

            String key;
            String dir = path + File.separator + MESSAGES_DIRECTORY
                    + File.separator;

            File messagesFile = new File(dir, baseFileName + "_" + locale
                    + PROPERTIES_EXTENSION);

            // The file is not found for this locale, use default.
            if (!messagesFile.exists()) {
                messagesFile = new File(dir, baseFileName
                        + PROPERTIES_EXTENSION);
                log.warn("Cannot load property file: " + baseFileName
                        + " for locale '" + locale+"'");
            }

            FileInputStream inputStream = new FileInputStream(messagesFile);
            PropertyResourceBundle bundle = new PropertyResourceBundle(
                    inputStream);

            // We transform our ResourceBundle to a Map to return it.
            Map<String, String> messages = new HashMap<String, String>();
            Enumeration<String> enu = bundle.getKeys();
            while (enu.hasMoreElements()) {
                key = enu.nextElement();
                messages.put(key, bundle.getString(key));
            }
            
            inputStream.close();
            return messages;

        } catch (Exception e) {
            log.warn("Unable to retrieve messages", e);
            return new HashMap<String, String>();
        }
    }

    /** {@inheritDoc} */    
    public static String getCurrentLocale() throws Exception {
        String locale = "";
        ResourceLoader rb = new ResourceLoader();
        try {
            Locale sessionLocale = rb.getLocale();
            locale = sessionLocale.toString();
        } catch (NullPointerException e) {
            locale = DEFAULT_LOCALE;
        } catch (Exception e) {
            log.error("Unable to retrieve current locale", e);
            throw new Exception(e);
        }

        return locale;
    }

}
