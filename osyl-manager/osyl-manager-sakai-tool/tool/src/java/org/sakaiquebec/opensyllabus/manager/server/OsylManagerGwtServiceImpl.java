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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.manager.client.rpc.OsylManagerGwtService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
		log.warn("INIT OsylEditorGwtServiceImpl");

		servletContext = getServletContext();
		webAppContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		if (webAppContext != null) {
			osylManagerServices = (OsylManagerBackingBean) webAppContext
					.getBean("osylManagerMainBean");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public String createSite(String siteTitle, String configId) {
		try {
			if (osylManagerServices != null) {
				log.warn("OsylSiteService : "
						+ osylManagerServices.getOsylSiteService());
				return osylManagerServices.getOsylSiteService().createSite(
						siteTitle, configId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getOsylConfigs() {
		try {
			if (osylManagerServices != null) {
				log.warn("OsylSiteService : "
						+ osylManagerServices.getOsylConfigService());
				return osylManagerServices.getOsylConfigService().getConfigs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void readXML(String xmlReference, String siteId) {
		osylManagerServices.getOsylManagerService().readXML(xmlReference,
				siteId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> getOsylSitesMap() {
		return osylManagerServices.getOsylManagerService().getOsylSitesMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public void readZip(String zipReference, String siteId) {
		osylManagerServices.getOsylManagerService().readZip(zipReference,
				siteId);
		importFilesInSite(zipReference, siteId);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOsylPackage(String siteId) {
		return osylManagerServices.getOsylManagerService().getOsylPackage(
				siteId);
	}

	/**
	 * Import file contains in the osylPackage to sakai ressources
	 * 
	 * @param zipReference
	 * @param siteId
	 */
	private void importFilesInSite(String zipReference, String siteId) {
		// TODO: Valider la corrrection apport�e pour la compilation
		Map<File, String> fileMap = (Map<File, String>) osylManagerServices
				.getOsylManagerService().getImportedFiles();
		Set<File> files = fileMap.keySet();
		for (File file : files) {
			try {
				String fileNameToUse = fileMap.get(file);
				InputStream inputStream = new FileInputStream(file);
				osylManagerServices.getOsylManagerService().addRessource(
						fileNameToUse, inputStream,
						servletContext.getMimeType(file.getName()), siteId);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	public Map<String, String> getOsylSites(String siteId) {
		return osylManagerServices.getOsylManagerService().getOsylSites(siteId);
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

	public Boolean associateToCM(String courseSectionId, String siteId) {
		return osylManagerServices.getOsylManagerService().associateToCM(
				courseSectionId, siteId);
	}
	
	public Map<String, String> getCMCourses() {
		return osylManagerServices.getOsylManagerService().getCMCourses();
	}
}
