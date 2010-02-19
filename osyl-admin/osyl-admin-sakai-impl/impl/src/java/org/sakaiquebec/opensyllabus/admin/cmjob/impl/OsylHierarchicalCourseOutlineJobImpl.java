package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.OsylHierarchicalCourseOutlineJob;
import org.sakaiquebec.opensyllabus.common.api.OsylHierarchyService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
 *****************************************************************************/

/**
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylHierarchicalCourseOutlineJobImpl implements OsylHierarchicalCourseOutlineJob {

    
    private static final Log log =
	    LogFactory.getLog(OsylHierarchicalCourseOutlineJobImpl.class);


    /** The hierarchy service to be injected by Spring */
    private OsylHierarchyService osylHierarchyService;

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setOsylHierarchyService(OsylHierarchyService hierarchyService) {
	this.osylHierarchyService = hierarchyService;
    }

    /**
     * Injection of the CORelationDao
     */
    private CORelationDao coRelationDao;

    /**
     * Sets the {@link CORelationDao}.
     * 
     * @param configDao
     */
    public void setCoRelationDao(CORelationDao relationDao) {
	this.coRelationDao = relationDao;
    }

    /** {@inheritDoc} */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	List<CORelation> allRelations = coRelationDao.getAllLinkedCourseOutlines();

	CORelation relation = null;
	String parentSiteId = null;
	String childSiteId = null;
	
	for ( int i=0; i < allRelations.size(); i++){
	    relation = allRelations.get(i);
	    parentSiteId = relation.getParent();
	    childSiteId = relation.getChild();
	    
	    osylHierarchyService.addOrUpdateUsers(parentSiteId, childSiteId);
	}
    }
}
