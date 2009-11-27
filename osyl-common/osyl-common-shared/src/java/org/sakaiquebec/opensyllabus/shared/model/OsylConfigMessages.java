/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylConfigMessages {

    Map<String,String> messages;

    /**
     * Construct a new class of messages for course outlines or user interface
     * with the map 
     * @param messages
     */
    public OsylConfigMessages(Map<String,String> messages) {
	this.messages = new HashMap<String,String>();
	this.messages.putAll(messages);
    }

    /**
     * Return the message corresponding to the parameter key
     * @param key
     * @return the message String
     */
    public String getMessage(String key) {
	String message = (String) messages.get(key);
	if (message == null)
	    return "Missing key: " + key;
	else
	    return message;
    }

    /**
     * Return the message corresponding to the parameter key
     * @param key
     * @return the message String
     */
    public String getShortMessage(String key) {
	String message = (String) messages.get(key+"Short");
	if (message == null) {
	    message = (String) messages.get(key);
	    if ( message == null ) {
		return "Missing key: " + key;		
	    }
	}
	return message;
    }

    /**
     *  Return the message corresponding to the key with parameter set
     * @param key
     * @param arg Argument 
     * @return The message String
     */
    public String getMessage(String key, String arg) {
	String message = (String) messages.get(key);
	if (message == null)
	    return "Missing key: " + key + "\narg:" + arg;
	else
	    message = message.replaceAll("\\{0\\}", arg);
	return message;
    }
    
    /**
     *  Return the message corresponding to the key with parameter set
     * @param key
     * @param arg Argument 
     * @return The message String
     */
    public String getMessage(String key, String arg0, String arg1) {
	String message = (String) messages.get(key);
	if (message == null)
	    return "Missing key: " + key;
	else{
	    message = message.replaceAll("\\{0\\}", arg0);
	    message = message.replaceAll("\\{1\\}", arg1);
	}
	return message;
    }

    /**
     * Set messages map
     * @param messages
     */
    public void setMessages(Map<String,String> messages) {
	this.messages = new HashMap<String,String>();
	this.messages.putAll(messages);

    }

    /**
     * Return a map containing the messages
     * @return the map <key,message>
     */
    public Map<String,String> getMessages() {
	return messages;
    }
    
    /**
     * @param key
     * @return true if the maps contains the key
     */
    public boolean containsKey(String key){
    	return this.messages.containsKey(key);    
    }

}
