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
import org.hibernate.HibernateException;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Implementation of the DAO methods.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public class ResourceDaoImpl extends HibernateDaoSupport implements ResourceDao {

    /** Our logger */
    private static Log log = LogFactory.getLog(ResourceDaoImpl.class);

    /** The init method called by Spring */
    public void init() {
	log.warn("Init from DAO");
    }

    /** Default constructor */
    public ResourceDaoImpl() {
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<COSerialized> getCourseOutlines() {
	return getHibernateTemplate().loadAll(COSerialized.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and published=0",
				    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and published=false");
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COSerialized getSerializedCourseOutline(String idCO)
	    throws Exception {
	COSerialized coXml = null;
	try {
	    coXml =
		    (COSerialized) getHibernateTemplate().get(
			    COSerialized.class, idCO);
	} catch (HibernateException e) {
	    log.error("Unable to retrieve course outline ", e);
	    throw new Exception(e);
	}
	if (coXml == null)
	    throw new Exception("No course outline with id=" + idCO);
	return coXml;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COConfigSerialized getCourseOutlineOsylConfig(String idCo)
	    throws Exception {
	COSerialized courseOutline = null;
	try {
	    courseOutline = getSerializedCourseOutline(idCo);
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}

	return courseOutline.getOsylConfig();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public COSerialized getCourseOutlineSiteId(String siteId) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate().find(
			    "from COSerialized where siteId= ? ",
			    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    throw new Exception("No course outline with site id= " + siteId);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public String createOrUpdateCourseOutline(COSerialized courseOutline)
	    throws Exception {
	boolean alreadyExist = false;
	COSerialized cos = null;
	try {
	    cos =
		getSerializedVourseOutlineBySiteIdAccessAndPublished(courseOutline
			    .getSiteId(), courseOutline.getAccess(), courseOutline.isPublished());
	    if (!cos.getCoId().equals(courseOutline.getCoId()))
		alreadyExist = true;

	} catch (Exception e) {
	}
	if (alreadyExist)
	    throw new Exception(
		    "A course outline with same siteId and groupName already exists in database");
	// to avoid org.springframework.orm.hibernate3.HibernateSystemException:
	// a different object with the same identifier value was already associated with the session
	getHibernateTemplate().evict(cos);
	
	try {
	    getHibernateTemplate().saveOrUpdate(courseOutline);
	} catch (Exception e) {
	    log.error("Unable to create or update course outline", e);
	    throw e;
	}
	return courseOutline.getCoId();
    }
    
    @SuppressWarnings("unchecked")
    private COSerialized getSerializedVourseOutlineBySiteIdAccessAndPublished(String siteId, String access, boolean published) throws Exception{
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null || access == null)
	    throw new IllegalArgumentException();
	try {
		if ("oracle".equalsIgnoreCase(SqlService.getVendor()) && access.equals("")) {
		    results =
			    getHibernateTemplate()
				    .find(
					    "from COSerialized where siteId= ? and published= ? and access is null",
					    new Object[] { siteId, published });
		}
		else {
			results =
			    getHibernateTemplate()
				    .find(
					    "from COSerialized where siteId= ? and access= ? and published= ?",
					    new Object[] { siteId, access, published });
		}
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and access=" + access);
    }

    /** {@inheritDoc} */
    public boolean removeCourseOutline(String courseOutlineId) throws Exception {
	COSerialized courseOutline = null;
	if (courseOutlineId == null)
	    throw new IllegalArgumentException();
	try {
	    courseOutline = getSerializedCourseOutline(courseOutlineId);
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	}
	if (courseOutline == null)
	    return false;
	else {
	    getHibernateTemplate().delete(courseOutline);
	    return true;
	}
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean hasBeenPublished(String siteId) throws Exception {
	List<COSerialized> results = null;

	if (siteId == null)
	    throw new IllegalArgumentException();

	try {
	    getCourseOutlineSiteId(siteId);
	} catch (Exception e) {
	    throw new Exception("No course outline with site id= " + siteId);
	}

	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and published=1",
				    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}
	if (results.size() > 0) {
	    return true;
	} else
	    return false;
    }

    @SuppressWarnings("unchecked")
    public COSerialized isPublished(String siteId, String access)
	    throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and access = ? ",
				    new Object[] { siteId, access });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    return courseOutline;
    }

    @SuppressWarnings("unchecked")
    public COSerialized getPrePublishSerializedCourseOutlineBySiteId(
	    String siteId) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
		if ("oracle".equalsIgnoreCase(SqlService.getVendor())) {
		    results =
			    getHibernateTemplate()
				    .find(
					    "from COSerialized where siteId= ? and published=1 and access is null",
					    new Object[] { siteId });
		}
		else {
			results =
			    getHibernateTemplate()
				    .find(
					    "from COSerialized where siteId= ? and published=1 and access= ?",
					    new Object[] { siteId, "" });
		}	
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and published=true");
    }

    @SuppressWarnings("unchecked")
    public COSerialized getPublishedSerializedCourseOutlineBySiteIdAndAccess(
	    String siteId, String access) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null || access == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and access= ?",
				    new Object[] { siteId, access });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and access=" + access);
    }

}
