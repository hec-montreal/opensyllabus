/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.shared.model;

import java.util.HashMap;

/**
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class COProperty {
    
    public static final String ATTRIBUTE_LABEL = "label";
    
    private String value;
    
    private HashMap<String, String> attributes;
    
    public COProperty() {
	attributes = new HashMap<String, String>();
    }
    
    public void addAttribute(String key, String value){
	attributes.put(key, value);
    }
    
    public void removeAttribute(String key){
	attributes.remove(key);
    }
    
    public String getAttribute(String key){
	return attributes.get(key);
    }
    
    public void setValue(String value){
	this.value = value;
    }
    
    public String getValue(){
	return value;
    }
    
    public HashMap<String, String> getAttributes(){
	return attributes;
    }
}