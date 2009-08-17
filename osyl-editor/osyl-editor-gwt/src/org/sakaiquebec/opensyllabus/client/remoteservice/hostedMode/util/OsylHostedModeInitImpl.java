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

import java.util.Map;

/**
 * Default init implementation version for hosted mode. This class is
 * Instantiated by differed binding
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $$
 */
public class OsylHostedModeInitImpl implements OsylHostedModeInit {
	protected String configPath = "rules/rules.xml";
	protected String modelPath = "xml_examples/defaultXml.xml";
	protected Map<String, String> OsylCOMessages = OsylTestCOMessages.getMap();
	protected Map<String, String> OsylUIMessages = OsylTestUIMessages.getMap();

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
	public Map<String, String> getCOMessages() {
		return OsylCOMessages;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getUIMessages() {
		return OsylUIMessages;
	}
}
