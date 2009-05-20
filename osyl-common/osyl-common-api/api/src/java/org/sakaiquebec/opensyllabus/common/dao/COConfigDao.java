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

package org.sakaiquebec.opensyllabus.common.dao;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;

/**
 * This interface provides all the CRUD methods for the OSYL CONFIG tool
 * persisted data
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface COConfigDao {

    /**
     * All the configs saved in the DB
     * 
     * @param cOConfig
     */
    public List<COConfigSerialized> getConfigs();

    /**
     * Creates a COConfig
     * 
     * @param cOConfig
     */
    public void createConfig(COConfigSerialized cOConfig);

    /**
     * Removes the COConfig
     * 
     * @param cOConfig
     * @throws Exception
     */
    public void removeConfig(String configId) throws Exception;

    /**
     * Updates the COConfig
     * 
     * @param cOCinfig
     * @throws Exception
     */
    public void updateConfig(COConfigSerialized cOConfig) throws Exception;

    /**
     * Returns the relative reference to the folder containing the configs
     * 
     * @param configId
     * @return A String representation of the xml config
     */
    public String getConfigRef(String configId);

    /**
     * Returns the config
     * 
     * @param configId
     * @return A String representation of the xml config
     * @throws Exception
     */
    public COConfigSerialized getConfigByRef(String configRef) throws Exception;

    /**
     * Returns the config
     * 
     * @param configId
     * @return A String representation of the xml config
     * @throws Exception
     */
    public COConfigSerialized getConfig(String configId) throws Exception;

}
