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

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
@SuppressWarnings("serial")
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
	    throws Exception {

	if (osylManagerServices != null) {
	    log.info("OsylSiteService : "
		    + osylManagerServices.getOsylSiteService());
	    return osylManagerServices.getOsylSiteService().createSite(
		    siteTitle, configRef, lang);
	}

	return null;
    }

    public Map<String, String> getOsylConfigs() {
	try {
	    if (osylManagerServices != null) {
		log.info("OsylSiteService : "
			+ osylManagerServices.getOsylConfigService());
		return osylManagerServices.getOsylConfigService().getConfigs();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public void importData(String fileReference, String siteId) throws Exception {
	String webappDir = getServletContext().getRealPath("/");
	if (fileReference.endsWith(".zip"))
	    osylManagerServices.getOsylManagerService().readZip(fileReference,
		    siteId,webappDir);
	else
	    osylManagerServices.getOsylManagerService().readXML(fileReference,
		    siteId,webappDir);
    }

    /**
     * {@inheritDoc}
     */
    public String getOsylPackage(String siteId) {
	return osylManagerServices.getOsylManagerService().getOsylPackage(
		siteId);
    }

    public Map<String, String> getOsylSites(List<String> siteIds) {
	return osylManagerServices.getOsylManagerService().getOsylSites(siteIds);
    }

    public String getParent(String siteId) throws Exception {
	return osylManagerServices.getOsylManagerService().getParent(siteId);
    }

    public void associate(String siteId, String parentId) throws Exception {
	osylManagerServices.getOsylManagerService().associate(siteId, parentId);
    }

    public void dissociate(String siteId, String parentId) throws Exception {
	osylManagerServices.getOsylManagerService()
		.dissociate(siteId, parentId);
    }

    public void associateToCM(String courseSectionId, String siteId) throws Exception{
	osylManagerServices.getOsylManagerService().associateToCM(
		courseSectionId, siteId);
    }
    
    public void dissociateFromCM(String siteId) throws Exception{
	osylManagerServices.getOsylManagerService().dissociateFromCM(siteId);
    }

    public List<CMCourse> getCMCourses() {
	return osylManagerServices.getOsylManagerService().getCMCourses();
    }
    
    public COSite getCoAndSiteInfo(String siteId){
	return osylManagerServices.getOsylManagerService().getCoAndSiteInfo(siteId);
    }

    public List<COSite> getCoAndSiteInfo(){
	return osylManagerServices.getOsylManagerService().getCoAndSiteInfo();
    }

    public void publish(String siteId) throws Exception {
	String webappDir = getServletContext().getRealPath("/");
	osylManagerServices.getOsylPublishService().publish(webappDir, siteId);
    }

}
