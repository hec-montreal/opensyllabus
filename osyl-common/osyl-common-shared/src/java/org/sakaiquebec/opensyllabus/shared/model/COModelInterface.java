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

/**
 * An interface to be used with OSYL modeled data.
 *
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 *
 */
public interface COModelInterface {
    
    
    /**
     * @return the type for this model
     */
    public String getType();

    /**
     * @param type to set for this model
     */
    public void setType(String type);
    
    /**
     * 
     * @return true if the model is editable
     */
    public boolean isEditable();
    
    /**
     * 
     * @param edit, boolean to set for this model
     */
    public void setEditable(boolean edit);
    
   /**
    * 
    * @return COProperties properties of the model object
    */
    public COProperties getProperties();
    
    
    /**
     * 
     * @param coProperties the properties to set
     */
    public void setProperties(COProperties coProperties);
    
    /**
     * Adds a property to the <code>COProperties</code> structure.
     * 
     * @param key the property key used to retrieve the value.
     * @param value the value of this property.
     */
    public void addProperty(String key, String value);
    
    /**
     * Removes a property from the <code>COProperties</code> structure.
     * 
     * @param key the key of the property to remove.
     */
    public void removeProperty(String key);

    /**
     * Returns a specific property of the model object.
     * 
     * @param key
     * @return
     */
    public String getProperty(String key);
    
    public void addProperty(String key, String type, String value);
    
    public String getProperty(String key, String type);
    
    public COProperty getCOProperty(String key, String type);
    
    public void removeProperty(String key, String type);
    
    public String getId() ;

    public void setId(String id) ;
    
    public String getAccess() ;
    
    public void setAccess(String access) ;
    
    
}
