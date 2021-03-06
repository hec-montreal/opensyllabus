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
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

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
	log.info("Init from DAO");
    }

    /** {@inheritDoc} */
    public void createRelation(String child, String parent) throws Exception {
	if (parent == null || child == null) {
	    throw new IllegalArgumentException();
	}
	CORelation relation = new CORelation(child, parent);
	getHibernateTemplate().saveOrUpdate(relation);

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<CORelation> getCourseOutlineChildren(String coId)
	    throws Exception {
	if (coId == null)
	    throw new IllegalArgumentException();
	List<CORelation> children = null;
	try {
	    children =
	    		(List<CORelation>) getHibernateTemplate().find(
			    "from CORelation where parent= ? ",
			    new Object[] { coId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline children", e);
	    throw e;
	}

	return children;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public String getParentOfCourseOutline(String coId) throws Exception {
	if (coId == null)
	    throw new IllegalArgumentException();
	List<CORelation> parents = null;
	try {
	    parents =
		    (List<CORelation>)getHibernateTemplate().find(
			    "from CORelation where child= ? ",
			    new Object[] { coId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline parents", e);
	    throw e;
	}
	if (parents != null && !parents.isEmpty())
	    return parents.get(0).getParent();
	else {
	    log.debug("No parent for course outline with id = " + coId);
	    throw new Exception("No parent for course outline with id = "
		    + coId);
	}
    }

    @SuppressWarnings("unchecked")
    public CORelation getRelation(String childId, String parentId)
	    throws Exception {
	List<CORelation> results = null;

	try {
	    results =
		    (List<CORelation>) getHibernateTemplate().find(
			    "from CORelation where child= ? and parent= ?",
			    new Object[] { childId, parentId });
	} catch (Exception e) {
	    log.error("Unable to retrieve config", e);
	    throw new Exception(e);
	}
	if (results.size() >= 1)
	    return (CORelation) results.get(0);
	else
	    throw new Exception("No relation between " + childId + " and "
		    + parentId);
    }

    /** {@inheritDoc} */
    public boolean removeRelation(String coId, String idParent)
	    throws Exception {
	if (coId == null || idParent == null) {
	    throw new IllegalArgumentException();
	}
	try {
	    CORelation courseOutlineRelation = getRelation(coId, idParent);
	    getHibernateTemplate().delete(courseOutlineRelation);
	} catch (Exception e) {
	    log.warn("Unable to retrieve course outline parent", e);
	    throw e;
	}
	return true;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<CORelation> getAllLinkedCourseOutlines() {
	List<CORelation> loadAll =
		getHibernateTemplate().loadAll(CORelation.class);
	return loadAll;
    }

    @SuppressWarnings("unchecked")
    public List<CORelation> getCORelationDescendants(String coId) {

	List<CORelation> descendants = new ArrayList<CORelation>();
	if (coId == null)
	    throw new IllegalArgumentException();
	try {
	    List<CORelation> childs =
		    (List<CORelation>) getHibernateTemplate().find(
			    "from CORelation where parent= ? ",
			    new Object[] { coId });
	    if (childs != null) {
		descendants.addAll(childs);
		for (CORelation cor : childs) {
		    descendants
			    .addAll(getCORelationDescendants(cor.getChild()));
		}
	    }
	} catch (Exception e) {
	    log.error("Unable to fetch descendant", e);
	}
	return descendants;
    }

    @SuppressWarnings("unchecked")
    public List<CORelation> getCourseOutlineAncestors(String coId) {

	List<CORelation> ancestors =  new ArrayList<CORelation>();
	if (coId == null)
	    throw new IllegalArgumentException();
	try {
	    List<CORelation> parents =
		    (List<CORelation>) getHibernateTemplate().find(
			    "from CORelation where child= ? ",
			    new Object[] { coId });
	    if (parents != null) {
		ancestors.addAll(parents);
		for (CORelation cor : parents) {
		    ancestors
			    .addAll(getCourseOutlineAncestors(cor.getParent()));
		}
	    }
	} catch (Exception e) {
	    log.error("Unable to fetch ancestors", e);
	}
	return ancestors;
    }

    public boolean areCourseOutlinesRelated(String parentSiteId,
	    String childSiteId) {

	List<CORelation> ancestors = getCourseOutlineAncestors(childSiteId);
	String parent = null;

	for (CORelation relation : ancestors) {
	    parent = relation.getParent();
	    if (parent.equalsIgnoreCase(parentSiteId))
		return true;
	}

	return false;
    }

}
