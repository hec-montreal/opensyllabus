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

import java.util.HashMap;
import java.util.Map;

/**
 * Util class for HostedMode Messages
 */
public class OsylHostedModeMessages {
	/**
	 * @param responseTxt
	 *            (bundle property file style text)
	 * @return message Map
	 */
	public static Map<String, String> getMap(String responseTxt) {
		Map<String, String> messages = new HashMap<String, String>();
		// Window.alert(responseTxt);
		String[] lines = responseTxt.split("\\n");
		for (String line : lines) {
			String lineTrim = line.trim();
			if (lineTrim.length() > 0) {
				if (!(lineTrim.charAt(0) == '#')) {
					int eqPos = lineTrim.indexOf("=");
					if (eqPos > 0) {
						String key = lineTrim.substring(0, eqPos).trim();
						String value = lineTrim.substring(eqPos + 1).trim();
						messages.put(key, value);
					}
				}
			}
		}
		return messages;
	}

}
