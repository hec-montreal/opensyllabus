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

package org.sakaiquebec.opensyllabus.manager.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.PermissionException;
import org.sakaiquebec.opensyllabus.common.api.OsylDirectoryService;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.SessionCompatibilityException;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylManagerGwtServiceImpl extends RemoteServiceServlet implements
	OsylManagerGwtService {

    public static final long serialVersionUID = 56L;

    private static Log log = LogFactory.getLog(OsylManagerGwtServiceImpl.class);
    protected OsylManagerBackingBean osylManagerServices;
    private ServletContext servletContext = null;
    private WebApplicationContext webAppContext = null;

    /**
     * {@inheritDoc}
     */
    public void init() {
	log.info("INIT OsylEditorGwtServiceImpl");

	servletContext = getServletContext();
	webAppContext =
		WebApplicationContextUtils
			.getWebApplicationContext(servletContext);

	if (webAppContext != null) {
	    osylManagerServices =
		    (OsylManagerBackingBean) webAppContext
			    .getBean("osylManagerMainBean");
	}

    }

    /**
     * {@inheritDoc}
     */
    public String createSite(String siteTitle, String configRef, String lang)
	    throws Exception, PermissionException {

	if (osylManagerServices != null) {
	    return osylManagerServices.getOsylManagerService().createSite(
		    siteTitle, configRef, lang);
	} else {
	    log.warn("Unable to create site: bean osylManagerMainBean is null");
	}

	return null;
    }

    public Map<String, String> getOsylConfigs() {
	try {
	    if (osylManagerServices != null) {
		log.info("OsylSiteService : "
			+ osylManagerServices.getOsylConfigService());
		Map<String, String> map =
			osylManagerServices.getOsylConfigService().getConfigs();
		// BEGIN HEC ONLY SAKAI-2723
		if (!osylManagerServices.getOsylSecurityService()
			.isCurrentUserASuperUser()) {
		    String keyToRemove=null;
		    for (Entry<String, String> entry : map.entrySet()) {
			if (OsylDirectoryService.CONFIG_REF.equals(entry
				.getValue())) {
			   keyToRemove=entry.getKey();
			   break;
			}
		    }
		    if(keyToRemove!=null)
			map.remove(keyToRemove);
		}
		// END HEC ONLY SAKAI-2723
		return map;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public void importData(String fileReference, String siteId)
	    throws Exception, PermissionException {
	String webappDir = getServletContext().getRealPath("/");
	if (fileReference.endsWith(".zip"))
	    osylManagerServices.getOsylManagerService().readZip(fileReference,
		    siteId, webappDir);
	else
	    osylManagerServices.getOsylManagerService().readXML(fileReference,
		    siteId, webappDir);
    }

    public String getOsylPackage(String siteId) throws OsylPermissionException {
	String webappDir = getServletContext().getRealPath("/");
	return osylManagerServices.getOsylManagerService().getOsylPackage(
		siteId, webappDir);
    }

    public Map<String, String> getOsylSites(List<String> siteIds,
	    String searchTerm) {
	return osylManagerServices.getOsylManagerService().getOsylSites(
		siteIds, searchTerm);
    }

    public String getParent(String siteId) throws Exception {
	return osylManagerServices.getOsylManagerService().getParent(siteId);
    }

    public void associate(String siteId, String parentId) throws Exception,
	    CompatibilityException, FusionException, PermissionException,
	    SessionCompatibilityException {
	osylManagerServices.getOsylManagerService().associate(siteId, parentId);
    }

    public void dissociate(String siteId, String parentId) throws Exception,
	    PermissionException {
	osylManagerServices.getOsylManagerService()
		.dissociate(siteId, parentId);
    }

    public void associateToCM(String courseSectionId, String siteId)
	    throws Exception, PermissionException,
	    SessionCompatibilityException {
	osylManagerServices.getOsylManagerService().associateToCM(
		courseSectionId, siteId, servletContext.getRealPath("/"));
    }

    public void dissociateFromCM(String siteId) throws Exception,
	    PermissionException {
	osylManagerServices.getOsylManagerService().dissociateFromCM(siteId,
		servletContext.getRealPath("/"));
    }

    public List<CMCourse> getCMCourses(String startsWith) {
	return osylManagerServices.getOsylManagerService().getCMCourses(
		startsWith);
    }

    public COSite getCoAndSiteInfo(String siteId, String searchTerm,
	    String academicSession) {
	return osylManagerServices.getOsylManagerService().getCoAndSiteInfo(
		siteId, searchTerm, academicSession);
    }

    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
	    String academicSession) {
	return osylManagerServices.getOsylManagerService().getAllCoAndSiteInfo(
		searchTerm, academicSession);
    }

    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
	    String academicSession, boolean withFrozenSites) {
	return osylManagerServices.getOsylManagerService().getAllCoAndSiteInfo(
		searchTerm, academicSession, withFrozenSites);
    }

    public Vector<Map<String, String>> publish(String siteId, String nonce) throws Exception,
	    FusionException, PermissionException {
	String webappDir = getServletContext().getRealPath("/");
	return osylManagerServices.getOsylPublishService().publish(webappDir,
		siteId, nonce);
    }

    public void deleteSite(String siteId) throws Exception, PermissionException {
	osylManagerServices.getOsylManagerService().deleteSite(siteId);
    }

    public List<CMAcademicSession> getAcademicSessions() {
	return osylManagerServices.getOsylManagerService()
		.getAcademicSessions();
    }

    public void copySite(String siteFrom, String siteTo) throws Exception,
	    PermissionException {
	String webappDir = getServletContext().getRealPath("/");
	String mainToolCopied = osylManagerServices.getOsylManagerService().copySite(siteFrom, siteTo);
	// update course informations
		if (mainToolCopied.equalsIgnoreCase("OPENSYLLABUS"))
			osylManagerServices.getOsylSiteService().updateCOCourseInformations(
		siteTo, webappDir);
    }

    public void unpublish(String siteId) throws Exception,
	    OsylPermissionException {
	String webappDir = getServletContext().getRealPath("/");
	osylManagerServices.getOsylPublishService()
		.unpublish(siteId, webappDir);
    }

    public Map<String, Boolean> getPermissions() {
	return osylManagerServices.getOsylManagerService().getPermissions();
    }
    
    public Boolean isSuperUser(){
	return osylManagerServices.getOsylManagerService().isSuperUser();
    }
}
