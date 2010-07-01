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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementations of the DAO methods
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class COConfigDaoImpl extends HibernateDaoSupport implements COConfigDao {

    private static Log log = LogFactory.getLog(COConfigDaoImpl.class);

    private static HashMap<String, COConfigSerialized> configCache;

    // Whether the cache is used (value is set from sakai.properties)
    private static boolean EXPERIMENTAL_CACHE_ENABLED = false;

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.debug("Init from COConfig DAO");
    }

    private void initCache() {
	if (EXPERIMENTAL_CACHE_ENABLED) {
	    log.info("Initializing caches");
	    configCache = new HashMap<String, COConfigSerialized>();
	} else {
	    log.info("Experimental cache is disabled");	    
	}
    }
    
    /** {@inheritDoc} */
    public void setCacheEnabled(boolean b) {
	EXPERIMENTAL_CACHE_ENABLED = b;
	initCache();
    }

    /** {@inheritDoc} */
    public void createConfig(COConfigSerialized cOConfig) throws Exception {
	if (cOConfig == null) {
	    throw new IllegalArgumentException("Cannot create null config!");
	}
	try {
	    getHibernateTemplate().saveOrUpdate(cOConfig);

	} catch (Exception e) {
	    log.error("Unable to create a config ", e);
	    throw e;
	}
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public String getConfigRef(String configId) throws Exception {
	COConfigSerialized cfg = null;
	if (EXPERIMENTAL_CACHE_ENABLED && configCache.containsKey(configId)) {
	    cfg = configCache.get(configId);
	} else {

	    List<COConfigSerialized> results = null;

	    try {
		results =
		    getHibernateTemplate().find(
			    "from COConfigSerialized where configId= ? ",
			    new String[] { configId });
	    } catch (Exception e) {
		log.error("Unable to retrieve config", e);
		throw e;
	    }

	    if (results.size() == 1) {
		cfg = results.get(0);

		if (EXPERIMENTAL_CACHE_ENABLED) {
		    configCache.put(configId, cfg);			
		}
	    } else if (results.size() == 0) {
		throw new Exception("Unexisting config id = " + configId);
	    } else {
		throw new Exception("Too many configs with id = " + configId);
	    }
	}
	return cfg.getConfigRef();
    }

    /** {@inheritDoc} */
    public void removeConfig(String configId) throws Exception {
	try {
	    COConfigSerialized cOConfig = getConfig(configId);
	    getHibernateTemplate().delete(cOConfig);
	} catch (Exception e) {
	    log.error("Unable to remove config", e);
	    throw e;
	}
    }

    /** {@inheritDoc} */
    public void updateConfig(COConfigSerialized cOConfig) throws Exception {
	try {
	    getHibernateTemplate().update(cOConfig);
	} catch (Exception e) {
	    log.error("Unable to save or update config", e);
	    throw e;
	}
    }

    /** {@inheritDoc} */
    public COConfigSerialized getConfig(String configId) throws Exception {
	COConfigSerialized cfg = null;
	if (EXPERIMENTAL_CACHE_ENABLED && configCache.containsKey(configId)) {
	    // Warning: these returns the same COConfigSerialized for each call
	    // some side effects might happen!
	    cfg = configCache.get(configId);
	} else {
	    cfg = (COConfigSerialized) getHibernateTemplate().get(
			COConfigSerialized.class, configId);

	    if (null == cfg) {
		throw new Exception("Unexisting config id= " + configId);
	    }
	    
	    if (EXPERIMENTAL_CACHE_ENABLED) {
		configCache.put(configId, cfg);			
	    }
	}
	return cfg;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<COConfigSerialized> getConfigs() {
	return (List<COConfigSerialized>) getHibernateTemplate().loadAll(
		COConfigSerialized.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public COConfigSerialized getConfigByRef(String configRef) throws Exception {
	List<COConfigSerialized> results = null;

	try {
	    results =
		    getHibernateTemplate().find(
			    "from COConfigSerialized where configRef= ? ",
			    new Object[] { configRef });
	} catch (Exception e) {
	    log.error("Unable to retrieve config by its reference", e);
	    throw e;
	}

	if (results.size() == 1) {
	    return (COConfigSerialized) results.get(0);
	} else if (results.size() == 0) {
	    throw new Exception("Unexisting config ref= " + configRef);
	} else {
	    throw new Exception("Too many configs with ref= " + configRef);
	}
    }
}
