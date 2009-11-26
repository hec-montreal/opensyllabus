/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
package org.sakaiquebec.opensyllabus.shared.util;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class BrowserUtil {

    /**
     * The code comes from UserAgent.gwt.xml in gwt-user.jar
     * 
     * @return client or user agent browser name
     */
    public static native String getBrowserType() /*-{ 
	var ua = navigator.userAgent.toLowerCase(); 
	if (ua.indexOf("opera") != -1) { 
		return "opera"; 
	} 
	else if (ua.indexOf("webkit") != -1) { 
		return "webkit"; 
	} 
	else if ((ua.indexOf("msie 6.0") != -1) || 
		(ua.indexOf("msie 7.0") != -1)) { 
		return "ie6"; 
	} 
	else if (ua.indexOf("gecko") != -1) { 
		var result = /rv:([0-9]+)\.([0-9]+)/.exec(ua); 
		if (result && result.length == 3) { 
			var version = (parseInt(result[1]) * 10) + parseInt(result[2]); 
			if (version >= 18) 
				return "gecko1_8"; 
			} 
			return "gecko"; 
		} 
		return "unknown"; 
	}-*/;
}
