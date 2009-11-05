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
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylDateUtils {

    public static String DATE_TIME_FORMAT="yyyy-MM-dd HH:mm:ss Z";
    
    
    public static String getDateString(){
	Date now = new Date();
	DateTimeFormat dtf = DateTimeFormat.getFormat(OsylDateUtils.DATE_TIME_FORMAT);
	return dtf.format(now);
    }
    
    public static Date getDate(String dateString){
	DateTimeFormat dtf = DateTimeFormat.getFormat(OsylDateUtils.DATE_TIME_FORMAT);
	return  dtf.parse(dateString);
    }
    	
}

