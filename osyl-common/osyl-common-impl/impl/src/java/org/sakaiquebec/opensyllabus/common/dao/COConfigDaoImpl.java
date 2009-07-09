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

import java.util.ArrayList;
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

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.warn("Init from COConfig DAO");
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
    public String getConfigRef(String configId) throws Exception{
	List<COConfigSerialized> results = null;
	COConfigSerialized cOConfig = null;
	//if(configId==null) throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate().find(
			    "from COConfigSerialized where configId= ? ",
			    new Object[] { configId });
	} catch (Exception e) {
	    log.error("Unable to retrieve config", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    cOConfig = (COConfigSerialized) results.get(0);
	    return cOConfig.getConfigRef();
	}
	else throw new Exception("Unexisting config id = "+configId);
    }

    /** {@inheritDoc} */
    public void removeConfig(String configId) throws Exception {
	COConfigSerialized cOConfig = null;
	try {
	    cOConfig = getConfig(configId);
	    getHibernateTemplate().delete(cOConfig);
	} catch (Exception e) {
	    log.error("Unable to remove config", e);
	    throw new Exception(e);
	}
    }

    /** {@inheritDoc} */
    public void updateConfig(COConfigSerialized cOConfig) throws Exception {
	try {
	    getHibernateTemplate().saveOrUpdate(cOConfig);
	} catch (Exception e) {
	    log.warn("Unable to save or update config", e);
	    throw new Exception(e);
	}
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public COConfigSerialized getConfig(String configId) throws Exception {
	List<COConfigSerialized> results = null;

	try {
	    results =
		    getHibernateTemplate().find(
			    "from COConfigSerialized where configId= ? ",
			    new Object[] { configId });
	} catch (Exception e) {
	    log.error("Unable to retrieve config", e);
	    throw new Exception(e);
	}
	if(results.size()>=1)
	    return (COConfigSerialized) results.get(0);
	else 
	    throw new Exception("Unexisting config id= "+configId);

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<COConfigSerialized> getConfigs() {
	List<COConfigSerialized> configs = new ArrayList<COConfigSerialized>();
	configs = getHibernateTemplate().loadAll(COConfigSerialized.class);
	return configs;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public COConfigSerialized getConfigByRef(String configRef) throws Exception {
	List<COConfigSerialized> results = null;

	try {
	    results =
		    getHibernateTemplate().find(
			    "from COConfigSerialized where configref= ? ",
			    new Object[] { configRef });
	} catch (Exception e) {
	    log.error("Unable to retrieve config by its reference", e);
	    throw new Exception(e);
	}
	if(results.size()>=1)
	    return (COConfigSerialized) results.get(0);
	else 
	    throw new Exception("Unexisting config ref= "+configRef);
    }

}
