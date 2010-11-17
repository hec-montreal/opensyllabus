/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.manager.client.rpc;

import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface OsylManagerGwtService extends RemoteService {

    /**
     * Creates a site with siteTitle title.
     * 
     * @param siteTitle
     * @return
     */
    public String createSite(String siteTitle, String configId, String lang)
	    throws Exception;

    public Map<String, String> getOsylConfigs();

    /**
     * Reads the archive file.
     * 
     * @param xmlReference
     * @param siteId
     */
    public void importData(String xmlReference, String siteId) throws Exception;

    public String getOsylPackage(String siteId);

    public Map<String, String> getOsylSites(List<String> siteIds);

    public String getParent(String siteId) throws Exception;

    public void associate(String siteId, String parentId) throws Exception,CompatibilityException;

    public void dissociate(String siteId, String parentId) throws Exception;

    public void associateToCM(String courseSectionId, String siteId)
	    throws Exception;

    public void dissociateFromCM(String siteId) throws Exception;

    public List<CMCourse> getCMCourses(String startsWith);

    /**
     * Retrieve all the informations of the specified site and the course
     * outline it contains. The information is saved in a POJO.
     * 
     * @param siteId
     * @return
     */
    public COSite getCoAndSiteInfo(String siteId, String searchTerm,
	    String academicSession);

    /**
     * Retrieve the informations of all the sites the current user has access
     * to.
     * 
     * @return
     */
    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
	    String academicSession);

    public void publish(String siteId) throws Exception, FusionException;
    
    public List<CMAcademicSession> getAcademicSessions();

    public void deleteSite(String siteId) throws Exception;
    
    public void copySite(String siteFrom, String siteTo) throws Exception;

}
