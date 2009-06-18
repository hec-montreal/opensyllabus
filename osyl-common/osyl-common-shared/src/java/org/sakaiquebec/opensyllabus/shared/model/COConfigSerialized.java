/**********************************************************************************
 * $Id: COConfigSerialized.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
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

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the POJO used for the configuration of a course outline. In the
 * database we only save the configuration id and a reference of the relative
 * path of the folder containing files that are used by the course outline. In
 * the files, we have: - a xml file representing the rules that are applied to
 * the course outline - a message file ( one in each language ) representing the
 * message bundle used by sakai - a css file used to layout the course outline
 * site. - a folder of images used by the course outline site To see how these
 * files are used within the POJO, see OsylConfigService
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: COConfigSerialized.java 661 2008-05-28 15:24:39Z
 *          sacha.lepretre@crim.ca $
 */
public class COConfigSerialized implements IsSerializable {

    private static final long serialVersionUID = 2L;

    private String configId;

    /**
     * relative reference to the folder
     */
    private String configRef;
    
    private String description;

    /**
     * xml config rules on model for the UI
     */
    private String rulesConfigContent;

    /**
     * Messages selected for the user locale
     */
    private Map<String,String> i18nMessages;

    /**
     * CSS URI, used for jsp page
     */
    private String cascadingStyleSheetURI;

    /**
     * Empty Constructor
     */
    public COConfigSerialized() {
	// empty contructor
    }

    /**
     * Constructor
     */
    public COConfigSerialized(String configId) {
	this.configId = configId;
    }

    /**
     * Empty Constructor
     */
    public COConfigSerialized(String configId, String configRef, String description) {
	this.configId = configId;
	this.configRef = configRef;
	this.description = description;
    }

    /**
     * 
     * @return the description value
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description the new value of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the cascadingStyleSheetURI value.
     */
    public String getCascadingStyleSheetURI() {
	return cascadingStyleSheetURI;
    }

    /**
     * @param cascadingStyleSheetURI the new value of cascadingStyleSheetURI.
     */
    public void setCascadingStyleSheetURI(String cascadingStyleSheetURI) {
	this.cascadingStyleSheetURI = cascadingStyleSheetURI;
    }

    /**
     * @return the i18nMessages value.
     */
    public Map<String,String> getI18nMessages() {
	return i18nMessages;
    }

    /**
     * @param i18nMessages the new value of coreBundle.
     */
    public void setCoreBundle(Map<String,String> i18nMessages) {
	this.i18nMessages = i18nMessages;
    }

    /**
     * @return the osylConfigId value.
     */
    public String getConfigId() {
	return configId;
    }

    /**
     * @param osylConfigId the new value of osylConfigId.
     */
    public void setConfigId(String osylConfigId) {
	this.configId = osylConfigId;
    }

    /**
     * @return the rulesConfigContent value.
     */
    public String getRulesConfig() {
	return rulesConfigContent;
    }

    /**
     * @param rulesConfigContent the new value of rulesConfigContent.
     */
    public void setRulesConfig(String rulesConfig) {
	this.rulesConfigContent = rulesConfig;
    }

    /**
     * @return the configRef value.
     */
    public String getConfigRef() {
	return configRef;
    }

    /**
     * @param configRef the new value of configRef.
     */
    public void setConfigRef(String configRef) {
	this.configRef = configRef;
    }

}
