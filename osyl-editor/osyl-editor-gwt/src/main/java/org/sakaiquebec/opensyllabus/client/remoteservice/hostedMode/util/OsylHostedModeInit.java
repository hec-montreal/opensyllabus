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
 * OsylHostedModeInit Interface. This interface is used to define the
 * initialization properties used in hosted mode such as ConfigPath, ModelPath
 * etc...
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepr�tre</a>
 * @version $$
 */
public interface OsylHostedModeInit {

	/**
	 * @return the rules config path for hosted mode
	 */
	public String getConfigPath();

	/**
	 * @return the xml model path for hosted mode
	 */
	public String getModelPath();

	/**
	 * @return the COMessages path for hosted mode
	 */
	public String getCOMessagesPath();

	/**
	 * @return the UIMessages path for hosted mode
	 */
	public String getUIMessagesPath();

	/**
	 * @return the RolesList path
	 */
	public String getRolesListPath();
	
	/**
	 * @return the EvalTypeList path
	 */
	public String getEvalTypeListPath();
	
	/**
	 * @return the EvalTypeList path
	 */
	public String getResourceTypeListPath();

	/**
	 * @return the general settings path
	 */
	public String getSettingsPath();
}
