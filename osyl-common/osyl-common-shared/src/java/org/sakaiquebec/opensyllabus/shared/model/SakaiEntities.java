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

import java.util.Map;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class SakaiEntities implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3421784869181255611L;

    private Map<String, String> providers;

    public Map<String, String> getProviders() {
	return providers;
    }

    public void setProviders(Map<String, String> providers) {
	this.providers = providers;
    }

    /**
     * A map for the entities
     */
    private Map<String, String> entities;

    /**
     * Default constructor
     */
    public SakaiEntities() {
    }

    public SakaiEntities(Map<String, String> providers,
	    Map<String, String> entities) {

    }

    /**
     * Returns the map containing the entities titles and uri
     * 
     * @return the entities value.
     */
    public Map<String, String> getEntities() {
	return entities;
    }

    /**
     * Sets the maps containing the rubric title
     * 
     * @param entities the new value of the entities.
     */
    public void setEntities(Map<String, String> entities) {
	this.entities = entities;
    }

}
