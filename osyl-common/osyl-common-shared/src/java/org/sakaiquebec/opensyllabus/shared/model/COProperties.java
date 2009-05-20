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

/**
 * This is where to put all the properties of an object of the model. The
 * properties are stored in the object as a value associated to a key. It
 * extends the <code>HashMap</code> class.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COProperties extends HashMap<String, String> {

    /**
     * Boolean value to print trace in debug mode.
     */
    final private boolean TRACE = false;

    /** The unique id for serialization */
    private static final long serialVersionUID = 7442035653835585773L;

    /**
     * Constructor
     */
    public COProperties() {
	super();
    }

    /**
     * Adds a property in the properties object.
     * 
     * @param key the key used to retrieve the property
     * @param value the property value
     */
    public void addProperty(String key, String value) {
	put(key, value);
	if (TRACE)
	    System.out.println("*** TRACE *** UPDATE THE MODEL COProperties "
		    + key + " = " + value);
    }

    /**
     * Removes a property from the properties object.
     * 
     * @param key the key used to remove its associated property.
     */
    public void removeProperty(String key) {
	remove(key);
    }

    /**
     * Retrieves a property from the properties object.
     * 
     * @param key the key used to retrieve the property form the properties
     *                object.
     * @return the property associated to the key.
     */
    public String getProperty(String key) {
	return (String) get(key);
    }
}
