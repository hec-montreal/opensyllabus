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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylDateUtils {

    public static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static String getDateString() {
	Date now = new Date();
	DateTimeFormat dtf =
		DateTimeFormat.getFormat(OsylDateUtils.DATE_TIME_FORMAT);
	return getXsDateTimeString(dtf.format(now));
    }

    public static Date getDate(String dateString) {
	DateTimeFormat dtf =
		DateTimeFormat.getFormat(OsylDateUtils.DATE_TIME_FORMAT);
	return dtf.parse(OsylDateUtils.getRFC822String(dateString));
    }

    /**
     * @param dateString a String represent date in a xs:datetime format
     * @return a RFC-822 standard date (ie: 2002-05-30T09:30:10-0600) recognize
     *         by our parser
     */
    public static String getRFC822String(String dateString) {
	int colonIndex = dateString.lastIndexOf(":");
	return dateString.substring(0, colonIndex)
		+ dateString.substring(colonIndex + 1, dateString.length());
    }

    /**
     * @param dateString in RFC-822 standard zone time
     * @return a xs:datetime standard date (ie: 2002-05-30T09:30:10-06:00) for
     *         XML
     */
    public static String getXsDateTimeString(String dateString) {
	return dateString.substring(0, dateString.length() - 2)
		+ ":"
		+ dateString.substring(dateString.length() - 2, dateString
			.length());
    }

}
