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
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class LinkValidator {
    
    private final static String LINK_PREFIX = "http://";
    
    private final static String MAIL_PREFIX = "mailto:";
    
    private final static String LINK_CONTENT = "://";

    /**
     * 
     * @param link
     * @return true if the link is valid
     */
    public static boolean isValidLink(String link){
	if(link!=null && !link.trim().equals(""))
	    return true;
	else return false;
    }
    
    public static String parseLink(String link){
	String newLink = link;
	
	if(!link.contains(LINK_CONTENT) && !link.startsWith(MAIL_PREFIX)){
	    newLink = addLinkPrefix(link);
	}
	return newLink;
    }
    
    public static String addLinkPrefix(String link){
	return LINK_PREFIX + link;
    }
}

