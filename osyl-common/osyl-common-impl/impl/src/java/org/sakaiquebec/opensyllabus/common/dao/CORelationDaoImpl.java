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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementation of the DAO methods
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class CORelationDaoImpl extends HibernateDaoSupport implements
	CORelationDao {

    private static Log log = LogFactory.getLog(CORelationDaoImpl.class);

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.warn("Init from DAO");
    }

    /** {@inheritDoc} */
    public void addParentToCourseOutline(String parent, String child) {
	CORelation relation = new CORelation(parent, child);
	getHibernateTemplate().saveOrUpdate(relation);

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<CORelation> getChildrenOfCourseOutline(String coId) {
	List<CORelation> children = null;
	try {
	    children =
		    getHibernateTemplate().find(
			    "from CORelation where parent= ? ",
			    new Object[] { coId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline children", e);
	}

	return children;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public String getParentOfCourseOutline(String coId) {
	List<CORelation> parents = null;
	try {
	    parents =
		    getHibernateTemplate().find(
			    "from CORelation where child= ? ",
			    new Object[] { coId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline parents", e);
	}
	if (parents != null && !parents.isEmpty())
	    return parents.get(0).getParent();
	else
	    return null;
    }

    /** {@inheritDoc} */
    public boolean removeChildOfCourseOutline(String coId, String idChild) {
	CORelation courseOutlineRelation = new CORelation(coId, idChild);
	try {
	    getHibernateTemplate().delete(courseOutlineRelation);
	} catch (Exception e) {
	    log.warn("Unable to retrieve course outline child", e);
	    return false;
	}
	return true;
    }

    /** {@inheritDoc} */
    public boolean removeParentOfCourseOutline(String coId, String idParent) {
	CORelation courseOutlineRelation = new CORelation(idParent, coId);
	try {
	    getHibernateTemplate().delete(courseOutlineRelation);
	} catch (Exception e) {
	    log.warn("Unable to retrieve course outline parent", e);
	    return false;
	}
	return true;
    }

}
