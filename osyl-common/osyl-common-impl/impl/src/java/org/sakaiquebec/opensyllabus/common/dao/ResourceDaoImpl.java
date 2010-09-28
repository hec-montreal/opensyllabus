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

import java.util.Date;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.REUtil;
import org.hibernate.HibernateException;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.springframework.orm.hibernate3.HibernateTemplate;
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

    // This HibernateTemplate is dedicated to requests expecting only one
    // row as results. This showed an average 5-15% speed increase.
    private HibernateTemplate singleRowHT;

    /** The init method called by Spring */
    public void init() {
        log.debug("Init from DAO");
        singleRowHT = new HibernateTemplate(getSessionFactory());
        singleRowHT.setFetchSize(1);
        singleRowHT.setMaxResults(1);
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
    public COSerialized getSerializedCourseOutlineBySiteId(String siteId)
	    throws Exception {
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
		    getSerializedVourseOutlineBySiteIdAccessAndPublished(
			    courseOutline.getSiteId(), courseOutline
				    .getAccess(), courseOutline.isPublished());
	    if (!cos.getCoId().equals(courseOutline.getCoId()))
		alreadyExist = true;

	} catch (Exception e) {
	}
	if (alreadyExist)
	    throw new Exception(
		    "A course outline with same siteId and groupName already exists in database");
	// to avoid org.springframework.orm.hibernate3.HibernateSystemException:
	// a different object with the same identifier value was already
	// associated with the session
	getHibernateTemplate().evict(cos);

	try {
	    courseOutline.setModificationDate(new java.util.Date(System
		    .currentTimeMillis()));
	    getHibernateTemplate().saveOrUpdate(courseOutline);
	} catch (Exception e) {
	    log.error("Unable to create or update course outline", e);
	    throw e;
	}
	return courseOutline.getCoId();
    }

    @SuppressWarnings("unchecked")
    private COSerialized getSerializedVourseOutlineBySiteIdAccessAndPublished(
	    String siteId, String access, boolean published) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	if (siteId == null || access == null)
	    throw new IllegalArgumentException();
	try {
	    if ("oracle".equalsIgnoreCase(SqlService.getVendor())
		    && access.equals("")) {
		results =
			singleRowHT
				.find(
					"from COSerialized where siteId= ? and published= ? and access is null",
					new Object[] { siteId, published });
	    } else {
		results =
			getHibernateTemplate()
				.find(
					"from COSerialized where siteId= ? and access= ? and published= ?",
					new Object[] { siteId, access,
						published });
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

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public boolean hasCourseOutiline(String siteId) {
	List<COSerialized> results = null;

	if (siteId == null)
	    return false;
	else {
	    try {
		results =
			getHibernateTemplate()
				.find(
					"from COSerialized where siteId= ? and published=0",
					new Object[] { siteId });
	    } catch (Exception e) {
		return false;
	    }
	    if (results.size() > 0) {
		return true;
	    } else
		return false;
	}
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
	    } else {
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
		    getHibernateTemplate().find(
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

    public void clearLocksForSession(String sessionId) {
	getHibernateTemplate().bulkUpdate(
		"update COSerialized set lockedBy=null where lockedBy= ?",
		new Object[] { sessionId });

    }
    
    public void clearLocksForCoId(String coid){
	getHibernateTemplate().bulkUpdate(
		"update COSerialized set lockedBy=null where coId= ?",
		new Object[] { coid });
    }
    
    public void setLockedBy(String coId, String sessionId){
	getHibernateTemplate().bulkUpdate(
		"update COSerialized set lockedBy=? where coId= ?",
		new Object[] { sessionId, coId });
    }
    
    public void setPublicationDate(String coId, Date pubDate){
	getHibernateTemplate().bulkUpdate(
		"update COSerialized set publicationDate=? where coId= ?",
		new Object[] { pubDate, coId });
    }

    @SuppressWarnings("unchecked")
    public Date getModifiedDate(String siteId)throws Exception {
	List<Date> results = null;
	Date res = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "select modificationDate from COSerialized where siteId= ? and published=0",
				    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    res = (Date) results.get(0);
	    return res;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and published=false");
    }

    @SuppressWarnings("unchecked")
    public Date getPublicationDate(String siteId) throws Exception{
	List<Date> results = null;
	Date res = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "select publicationDate from COSerialized where siteId= ? and published=0",
				    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() >= 1) {
	    res = (Date) results.get(0);
	    return res;
	} else
	    throw new Exception("No course outline with site id= " + siteId
		    + " and published=false");
    }

    @SuppressWarnings("unchecked")
    public void removeCoForSiteId(String siteId) {
	List<COSerialized> results = null;

	if (siteId == null)
	    throw new IllegalArgumentException();
	try {
	    results =
		    getHibernateTemplate().find(
			    "from COSerialized where siteId= ? ",
			    new Object[] { siteId });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	}
	if(results!=null){
	    for(COSerialized courseOutline:results){
		getHibernateTemplate().delete(courseOutline);
	    }
	}
    }


}
