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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylDateUtils {

    private static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static String SAKAI_DATE_TIME_FORMAT = "yyyyMMddHHmmssSSSZ";

    /**
     * @return A String represents the present date and time (format example:
     *         2002-05-30T09:30:10-06:00)
     */
    public static String getCurrentDateAsXmlString() {
	Date now = new Date();
	return getDateAsXmlString(now);
    }
    
    public static String getDateAsXmlString(Date date) {
	SimpleDateFormat dateFormat =
		new SimpleDateFormat(OsylDateUtils.DATE_TIME_FORMAT);
	return getXsDateTimeString(dateFormat.format(date));
    }

    public static Date getDateFromXMLDate(String dateString) {
	SimpleDateFormat dateFormat =
		new SimpleDateFormat(OsylDateUtils.DATE_TIME_FORMAT);
	Date date = new Date();

	try {
	    date = dateFormat.parse(OsylDateUtils.getRFC822String(dateString));
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return date;
    }

    /**
     * @param sakaiDate in format yyyyMMddHHmmssSSS
     * @return A String represents date (format example:
     *         2002-05-30T09:30:10-06:00)
     */
    public static String getXmlDateStringFromSakaiDateString(String sakaiDate) {
	SimpleDateFormat sakaiDateFormat =
		new SimpleDateFormat(OsylDateUtils.SAKAI_DATE_TIME_FORMAT);
	SimpleDateFormat dateFormat =
		new SimpleDateFormat(OsylDateUtils.DATE_TIME_FORMAT);
	sakaiDate += "-0000";// sakai date came in GMT time
	try {
	    return getXsDateTimeString(dateFormat.format(sakaiDateFormat
		    .parse(sakaiDate)));

	} catch (Exception e) {
	    e.printStackTrace();
	    return "";
	}
    }

    /**
     * @param dateString a String represent date in a xs:datetime format
     * @return a RFC-822 standard date (ie: 2002-05-30T09:30:10-0600) recognize
     *         by our parser
     */
    private static String getRFC822String(String dateString) {
	int colonIndex = dateString.lastIndexOf(":");
	return dateString.substring(0, colonIndex)
		+ dateString.substring(colonIndex + 1, dateString.length());
    }

    /**
     * @param dateString in RFC-822 standard zone time
     * @return a xs:datetime standard date (ie: 2002-05-30T09:30:10-06:00) for
     *         XML
     */
    private static String getXsDateTimeString(String dateString) {
	return dateString.substring(0, dateString.length() - 2)
		+ ":"
		+ dateString.substring(dateString.length() - 2, dateString
			.length());
    }

}
