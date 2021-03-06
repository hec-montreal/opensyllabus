/**********************************************************************************
 * $Id:  $
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

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util;

/**
 * UDEM init implementation version for hosted mode. This class is Instantiated
 * by differed binding
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $$
 */
public class OsylHostedModeInitImplUdem implements OsylHostedModeInit {
	protected String configPath = "../osylcoconfigs/udem/rules.xml";
	protected String modelPath = "../osylcoconfigs/udem/coContentTemplate.xml";
	protected String cOMessagesPath = "../osylcoconfigs/udem/bundle/COMessages_fr_CA.properties";
	protected String uIMessagesPath = "../osylcoconfigs/udem/bundle/UIMessages_fr_CA.properties";
	protected String cORolesListPath = "../osylcoconfigs/udem/coRolesList.xml";
	protected String cOEvalTypeListPath = "../osylcoconfigs/udem/coEvalTypeList.xml";
	protected String cOResourceTypeListPath = "../osylcoconfigs/default/coResourceTypeList.xml";	
	protected String settingsPath = "../osylcoconfigs/udem/settings.properties";

//	protected String configPath = "../osylcoconfigs/udemObjectifsActivites/rules.xml";
//	protected String modelPath = "../osylcoconfigs/udemObjectifsActivites/coContentTemplate.xml";
//	protected String cOMessagesPath = "../osylcoconfigs/udemObjectifsActivites/bundle/COMessages_fr_CA.properties";
//	protected String uIMessagesPath = "../osylcoconfigs/udemObjectifsActivites/bundle/UIMessages_fr_CA.properties";
//	protected String cORolesListPath = "../osylcoconfigs/udemObjectifsActivites/coRolesList.xml";
//	protected String cOEvalTypeListPath = "../osylcoconfigs/udemObjectifsActivites/coEvalTypeList.xml";

//	protected String configPath = "../osylcoconfigs/udemCompetencesComposantes/rules.xml";
//	protected String modelPath = "../osylcoconfigs/udemCompetencesComposantes/coContentTemplate.xml";
//	protected String cOMessagesPath = "../osylcoconfigs/udemCompetencesComposantes/bundle/COMessages_fr_CA.properties";
//	protected String uIMessagesPath = "../osylcoconfigs/udemCompetencesComposantes/bundle/UIMessages_fr_CA.properties";
//	protected String cORolesListPath = "../osylcoconfigs/udemCompetencesComposantes/coRolesList.xml";
//	protected String cOEvalTypeListPath = "../osylcoconfigs/udemCompetencesComposantes/coEvalTypeList.xml";

//	protected String configPath = "../osylcoconfigs/udemCompetencesSeances/rules.xml";
//	protected String modelPath = "../osylcoconfigs/udemCompetencesSeances/coContentTemplate.xml";
//	protected String cOMessagesPath = "../osylcoconfigs/udemCompetencesSeances/bundle/COMessages_fr_CA.properties";
//	protected String uIMessagesPath = "../osylcoconfigs/udemCompetencesSeances/bundle/UIMessages_fr_CA.properties";
//	protected String cORolesListPath = "../osylcoconfigs/udemCompetencesSeances/coRolesList.xml";
//	protected String cOEvalTypeListPath = "../osylcoconfigs/udemCompetencesSeances/coEvalTypeList.xml";

	/**
	 * {@inheritDoc}
	 */
	public String getConfigPath() {
		return configPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getModelPath() {
		return modelPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUIMessagesPath() {
		return uIMessagesPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCOMessagesPath() {
		return cOMessagesPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRolesListPath() {
		return cORolesListPath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getEvalTypeListPath() {
		return cOEvalTypeListPath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getResourceTypeListPath() {
		return cOResourceTypeListPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSettingsPath() {
		return settingsPath;
	}
}
