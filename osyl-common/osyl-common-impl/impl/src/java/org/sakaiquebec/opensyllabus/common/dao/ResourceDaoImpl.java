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
import org.hibernate.HibernateException;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
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
	public COSerialized getSerializedCourseOutlineBySiteId(String siteId,
	    String groupName) throws Exception {
	List<COSerialized> results = null;
	COSerialized courseOutline = null;

	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and security= ?",
				    new Object[] { siteId, groupName });
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline by its siteId", e);
	    throw e;
	}
	if (results.size() == 1) {
	    courseOutline = (COSerialized) results.get(0);
	    return courseOutline;
	} else
	    return null;
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
	return coXml;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public String getCourseOutlineContent(String idCo) throws Exception {
	COSerialized courseOutline = null;
	try {
	    courseOutline = getSerializedCourseOutline(idCo);
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}

	return courseOutline.getSerializedContent();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COConfigSerialized getOsylConfig(String idCo) throws Exception {
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
    public List<String> getCourseOutlineInfo(String idCo) throws Exception {
	List<String> info = new ArrayList<String>();
	COSerialized courseOutline = null;
	try {
	    courseOutline = getSerializedCourseOutline(idCo);
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);	   
	}
	if (courseOutline == null){
	    throw new Exception("No course outline");
	}
	else {
	    info.add(courseOutline.getCoId());
	    info.add(courseOutline.getLang());
	    info.add(courseOutline.getSection());
	    info.add(courseOutline.getSecurity());
	    info.add(courseOutline.getType());
	    return info;
	}

    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public COConfigSerialized getCourseOutlineOsylConfig(String idCo) throws Exception {
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
    public String getCourseOutlineSiteId(String idCo) throws Exception {
	COSerialized courseOutline = null;
	try {
	    courseOutline = getSerializedCourseOutline(idCo);
	} catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
	    throw e;
	}

	return courseOutline.getSiteId();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    public String createOrUpdateCourseOutline(COSerialized courseOutline)
	    throws Exception {
	try {
	    getHibernateTemplate().saveOrUpdate(courseOutline);
	} catch (Exception e) {
	    log.error("Unable to create or update course outline", e);
	    throw e;
	}
	return courseOutline.getCoId();
    }

    /** {@inheritDoc} */
    public boolean removeCourseOutline(String courseOutlineId) {
	COSerialized courseOutline = null;
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
	public boolean hasBeenPublished(String siteId) throws Exception{
	List<COSerialized> results = null;
	try {
	    results =
		    getHibernateTemplate()
			    .find(
				    "from COSerialized where siteId= ? and published=1", new Object[]{siteId});
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
	public COSerialized isPublished(String siteId, String security) throws Exception {
        List<COSerialized> results = null;
        COSerialized courseOutline = null;

        try {
            results = getHibernateTemplate().find(
                    "from COSerialized where siteId= ? and security = ? ",
                    new Object[] { siteId, security });
        } catch (Exception e) {
	    log.error("Unable to retrieve course outline", e);
            throw e;
        }
        if (results.size() >= 1) {
            courseOutline = (COSerialized) results.get(0);
            return courseOutline;
       }else 
    	   return courseOutline;
	}

}
