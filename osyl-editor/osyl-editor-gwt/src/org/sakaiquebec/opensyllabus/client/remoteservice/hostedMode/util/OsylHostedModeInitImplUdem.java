/**********************************************************************************
 * $Id:  $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
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
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepr�tre</a>
 * @version $$
 */
public class OsylHostedModeInitImplUdem implements OsylHostedModeInit {
	protected String configPath = "resources/osylcoconfigsmock/udem/rules.xml";
	protected String modelPath = "resources/osylcoconfigsmock/udem/coContentTemplate.xml";
	protected String cOMessagesPath = "resources/osylcoconfigsmock/udem/bundle/COMessages.properties";
	protected String uIMessagesPath = "resources/osylcoconfigsmock/udem/bundle/UIMessages.properties";

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
}
