/**********************************************************************************
 * $Id: $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team. and OpenSyllabus Project
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.api.Tool;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtService;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;
import org.sakaiquebec.opensyllabus.shared.model.SakaiEntities;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * OsylEditorGwtServiceImpl is the server side entry-point of OpenSyllabus GWT
 * editor. It implements the Resource and Security Interfaces common to the DAO,
 * Services and RPC.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */

public class OsylEditorGwtServiceImpl extends RemoteServiceServlet implements
	OsylEditorGwtService {

    private static final long serialVersionUID = -8433432771110370390L;
    /**
     * @uml.property name="osylServices"
     * @uml.associationEnd
     */
    private OsylBackingBean osylServices;
    /**
     * @uml.property name="servletContext"
     * @uml.associationEnd
     */
    private ServletContext servletContext = null;
    /**
     * @uml.property name="webAppContext"
     * @uml.associationEnd
     */
    private WebApplicationContext webAppContext = null;

    private static Log log = LogFactory.getLog(OsylEditorGwtServiceImpl.class);

    // The cache for published Course Outlines
    private static HashMap<String, COSerialized> publishedCoCache;
    // The cache for serialized configurations
    private static HashMap<String, COConfigSerialized> configCache;
    // Whether the cache is used (value set from sakai.properties)
    private static boolean cacheEnabled = true;

    /**
     * Constructor.
     */
    public OsylEditorGwtServiceImpl() {
	;
    } // must have

    /** {@inheritDoc} */
    public void init() {

	log.info("initializing");
	servletContext = getServletContext();
	webAppContext =
		WebApplicationContextUtils
			.getWebApplicationContext(servletContext);

	// This is not the best place to initialize this bean injection!
	// Spring injection
	if (webAppContext != null) {
	    osylServices =
		    (OsylBackingBean) webAppContext.getBean("osylMainBean");
	}

	initCache();

    }

    private void initCache() {
	cacheEnabled = osylServices.getOsylService().isCacheEnabled();
	if (cacheEnabled) {
	    log.info("Initializing caches");
	    publishedCoCache = new HashMap<String, COSerialized>();
	    configCache = new HashMap<String, COConfigSerialized>();
	} else {
	    log.info("Experimental cache is disabled");
	}
    }

    @Override
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	log.debug("doGet");
	final String contextPath = request.getContextPath();
	request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
	HttpServletRequest wrappedRequest =
		new HttpServletRequestWrapper(request) {
		    public String getContextPath() {
			return contextPath;
		    }
		};

	// otherwise do the dispatch
	RequestDispatcher dispatcher;
	if (request.getPathInfo() == null) {
	    dispatcher = request.getRequestDispatcher("/");
	} else {
	    dispatcher = request.getRequestDispatcher(request.getPathInfo());
	}

	dispatcher.forward(wrappedRequest, response);
    }

    /**
     * Initialize the tool. At this time we create the directory structure
     * needed by the tool here as we don't have a better place to do it.
     */
    public void initTool() throws Exception {
	if (osylServices != null) {
	    // TODO: insure that once the resource and security mechanisms are
	    // completed that the init are updated
	    // osylServices.getOsylSecurityService().initService();
	    osylServices.getOsylService().initService();
	}
    }

    /**
     * Publishes the CourseOutline whose ID is specified. It must have been
     * saved previously. Throws an exception if any error occurs, returns
     * otherwise.
     * 
     * @param String id
     */
    public Vector<Map<String, String>> publishCourseOutline() throws Exception,
	    FusionException, PdfGenerationException {
	String webappDir = getServletContext().getRealPath("/");
	Vector<Map<String, String>> publicationResults =
		new Vector<Map<String, String>>();
	String siteId = osylServices.getOsylSiteService().getCurrentSiteId();
	try {
	    publicationResults =
		    osylServices.getOsylPublishService().publish(webappDir,
			    siteId);
	    // We invalidate the cached published CO for this siteId
	    if (cacheEnabled) {
		publishedCoCache.remove(siteId
			+ SecurityInterface.ACCESS_ATTENDEE);
		publishedCoCache.remove(siteId
			+ SecurityInterface.ACCESS_PUBLIC);
	    }
	} catch (Exception e) {
	    throw e;
	}
	return publicationResults;
    }

    /**
     * Returns the URL where we can access the CourseOutline whose ID is
     * specified. It must have been published previously.
     * 
     * @param String id
     * @return String URL
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType) throws Exception {
	long start = System.currentTimeMillis();
	// Check security permission for this method
	String siteId = osylServices.getOsylSiteService().getCurrentSiteId();
	String webappdir = getServletContext().getRealPath("/");
	COSerialized cos;
	String cacheKey = siteId + accessType;
	if (cacheEnabled && publishedCoCache.containsKey(cacheKey)) {
	    cos = publishedCoCache.get(cacheKey);
	} else {
	    cos =
		    osylServices.getOsylPublishService()
			    .getSerializedPublishedCourseOutlineForAccessType(
				    siteId, accessType, webappdir);
	    if (cos != null) {
		if (cacheEnabled) {
		    publishedCoCache.put(cacheKey, cos);
		}
	    }
	}
	log.debug("getSerializedPublishedCourseOutlineForAccessType "
		+ accessType + " " + elapsed(start) + siteId);
	return cos;
    }

    boolean checkPermission(String function) {
	boolean res = false;
	// TODO: move this code in security, check user role, for method name
	// (function)

	return res;
    }

    /**
     * Returns the CourseOutlineXML whose ID is unspecified By default, asks for
     * the CourseOutline. Return published Course Outline if the current user
     * has not permission to edit it.
     * 
     * @return the CourseOutlineXML POJO corresponding to the current site
     *         context
     */
    public COSerialized getSerializedCourseOutline() throws Exception {
	long start = System.currentTimeMillis();
	COSerialized thisCo;
	String siteId = osylServices.getOsylSiteService().getCurrentSiteId();
	String webappDir = getServletContext().getRealPath("/");
	thisCo =
		osylServices.getOsylSiteService()
			.getSerializedCourseOutlineForEditor(siteId, webappDir);
	log.debug("getSerializedCourseOutline" + elapsed(start) + siteId);
	return thisCo;
    }

    /**
     * Saves the CourseOutlineXML specified. The ID is returned. This is useful
     * if this instance has never been saved before (i.e.: its ID is -1). In
     * this case, it is the responsibility of the client application to keep
     * track of this new ID, notably to save it again at a later time. If
     * something goes wrong, an exception is thrown.
     * 
     * @param COSerialized POJO
     * @return the CourseOutlineXML ID
     */
    public boolean updateSerializedCourseOutline(COSerialized co)
	    throws Exception {
	try {
	    return osylServices.getOsylSiteService()
		    .updateSerializedCourseOutline(co);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /**
     * Returns the user role for current user.
     * 
     * @return String userRole
     */
    public String getCurrentUserRole() {
	return osylServices.getOsylSecurityService().getCurrentUserRole();
    }

    /**
     * Applies a permission for the specified Site. If something prevents the
     * call to complete successfully an exception is thrown. TODO: check if the
     * description is OK, I'm not sure I understand this one well.
     * 
     * @param String resourceId
     */
    public void applyPermissions(String resourceId, String permission) {
	try {
	    // TODO: check this
	    osylServices.getOsylSecurityService().applyPermissions(resourceId,
		    permission);
	} catch (Exception e) {
	    log.error("Unable to apply permissions", e);
	}
    }

    /**
     * Returns the default configuration to be used with the rules, the message
     * bundle and a reference to the css file
     * 
     * @return COConfigSerialized
     */
    public COConfigSerialized getSerializedConfig() throws Exception {
	long start = System.currentTimeMillis();
	COConfigSerialized cfg = null;
	String siteId = osylServices.getOsylSiteService().getCurrentSiteId();
	if (cacheEnabled && configCache.containsKey(siteId)) {
	    cfg = configCache.get(siteId);
	} else {

	    String webappDir = getServletContext().getRealPath("/");
	    String cfgId =
		    osylServices.getOsylSiteService().getOsylConfigIdForSiteId(
			    siteId);
	    try {
		if (cfgId == null)
		    cfg =
			    osylServices.getOsylConfigService().getConfigByRef(
				    osylServices.getOsylConfigService()
					    .getDefaultConfig(), webappDir);
		else
		    cfg =
			    osylServices.getOsylConfigService().getConfig(
				    cfgId, webappDir);

	    } catch (Exception e) {
		log.error("Unable to retrieve serialized config", e);
		throw e;
	    }
	    if (cacheEnabled) {
		configCache.put(siteId, cfg);
	    }
	}
	log.debug("getSerializedConfig " + elapsed(start) + siteId);
	return cfg;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasBeenPublished() throws Exception {
	return osylServices.getOsylSiteService().hasBeenPublished();
    }

    /**
     * {@inheritDoc}
     */
    public void ping() {
	osylServices.getOsylSecurityService().setCurrentSessionActive();
    }

    /**
     * Get the xsl associated with the particular group
     * 
     * @param group
     * @param callback
     */
    public String getXslForGroup(String group) {
	String webappDir = getServletContext().getRealPath("/");
	return osylServices.getOsylService().getXslForGroup(group, webappDir);
    }

    /**
     * {@inheritDoc}
     */
    public ResourcesLicencingInfo getResourceLicenceInfo() {
	return osylServices.getOsylService().getResourceLicenceInfo();
    }

    public boolean checkSitesRelation(String resourceURI) {
	return osylServices.getOsylService().checkSitesRelation(resourceURI);
    }

    public String transformXmlForGroup(String xml, String group)
	    throws Exception {
	String webappDir = getServletContext().getRealPath("/");
	return osylServices.getOsylPublishService().transformXmlForGroup(xml,
		group, webappDir);
    }

    public void releaseLock() {
	osylServices.getOsylSiteService().releaseLock();
    }

    public SakaiEntities getExistingEntities(String siteId) {
	return osylServices.getOsylService().getExistingEntities(siteId);
    }

    // only to improve readability while profiling
    private static String elapsed(long start) {
	return ": elapsed : " + (System.currentTimeMillis() - start) + " ms ";
    }

    /**
     * Method used to create a pdf for the edition version of the CO
     * 
     * @param xml
     * @param printEditionVersionCallback
     */
    public void createPrintableEditionVersion() throws Exception {
	String webappDir = getServletContext().getRealPath("/");
	COSerialized cos = getSerializedCourseOutline();
	osylServices.getOsylConfigService().fillCo(
		webappDir + OsylConfigService.CONFIG_DIR + File.separator
			+ cos.getOsylConfig().getConfigRef(), cos);
	String transformXml =
		transformXmlForGroup(cos.getContent(),
			SecurityInterface.ACCESS_ATTENDEE);
	cos.setContent(transformXml);
	osylServices.getOsylPublishService().createEditionPrintVersion(cos,
		webappDir);
    }

    public void notifyOnPublish(String siteId, String subject, String body) {
	osylServices.getOsylSiteService().addAnnounce(siteId, subject, body);
    }
}
