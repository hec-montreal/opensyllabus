package org.sakaiquebec.opensyllabus.common.api;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 * This interface contains all the variables and functions needed by all the
 * tools of OpenSyllabus (OsylEditor, OsylManager, OsylAdmin). It allows to
 * retrieve informations from a course outline and the site related to it.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylSiteService {

	public static final String XSLT_DIRECTORY = "xslt";

	public static final String XML_FILE_EXTENSION = ".xml";
	public static final String XSL_FILE_EXTENSION = ".xsl";
	public static final String HTML_FILE_EXTENSION = ".html";

	public static final String CO_CONTENT_XSL_PUBLIC = "securityPublic";
	public static final String CO_CONTENT_XSL_ONSITE = "securityOnsite";
	public static final String CO_CONTENT_XSL_ATTENDEE = "securityAttendee";

	/**
	 * Name of the chs folder in which the course outline content will be
	 * stored.
	 */
	public static final String WORK_DIRECTORY = "work";

	/**
	 * Name of the chs folder in which the course outline content will be
	 * published.
	 */
	public static final String PUBLISH_DIRECTORY = "publish";

	/** A simple string used to format Urls. */
	public static final String CONTENT = "/content";

	public static final String FILE_DELIMITER = "\\.";

	/**
	 * The default type of site we are creating
	 */
	public static final String DEFAULT_SITE_TYPE = "course";

	/**
	 * Get a valid resource reference base site URL to be used in later calls.
	 * Will get deprecated very soon!
	 * 
	 * @return a String of the base URL
	 */
	public String getCurrentSiteReference();

	/**
	 * Returns the value of the configuration property linked to the site
	 * representing the course outline and used to choose the skin and message
	 * bundle
	 * 
	 * @param
	 * @return String value of the configuration
	 */
	public String getSiteConfigProperty(String siteId);

	/**
	 * From the information given by the user, we create a new Site
	 * 
	 * @param siteId
	 * @param siteTitle
	 * @param siteSession
	 * @param siteGroup
	 * @param siteProf
	 * @return the id of the site
	 * @throws Exception
	 */
	public String createSite(String siteTitle) throws Exception;

	/**
	 * Returns the actual site id of this context.
	 * 
	 * @return a String of the current site id.
	 */
	public String getCurrentSiteId() throws Exception;

	/**
	 * This method adds site info to the COSerialized.
	 * 
	 * @param co
	 *            the COSerialized
	 * @param siteId
	 *            The current site id.
	 * @throws Exception
	 */
	public void getSiteInfo(COSerialized co, String siteId) throws Exception;

	/**
	 * Check if the Co of the current context has been published
	 * 
	 * @return true if published
	 */
	public boolean hasBeenPublished();

	/**
	 * Returns the CourseOutline whose ID is specified.
	 * 
	 * @param String
	 *            the course outline ID
	 * @return the CourseOutline POJO corresponding to the specified ID
	 * @throws Exception
	 */
	public COSerialized getSerializedCourseOutline(String coId, String webappDir)
			throws Exception;

	/**
	 * Returns the CourseOutline whose site ID is specified.
	 * 
	 * @param String
	 *            the site ID
	 * @return the CourseOutline POJO corresponding to the specified siteID
	 * @throws Exception
	 */
	public COSerialized getSerializedCourseOutlineBySiteId(String siteId);

	/**
	 * Returns the CourseOutline of the current context.
	 * 
	 * @return the CourseOutline POJO corresponding to the current context.
	 */
	public COSerialized getSerializedCourseOutline(String webappDir);

	/**
	 * Returns the CourseOutline of the current context (version unfusionned).
	 * 
	 * @return the CourseOutline POJO corresponding to the current context.
	 */
	public COSerialized getUnfusionnedSerializedCourseOutlineBySiteId(String id);
	
	/**
	 * Creates or update a new course outline
	 * 
	 * @return
	 */
	public String createOrUpdateCO(COSerialized co);

	/**
	 * Saves the CourseOutline specified. The ID is returned. This is useful if
	 * this instance has never been saved before (i.e.: its ID is -1). In this
	 * case, it is the responsibility of the client application to keep track of
	 * this new ID, notably to save it again at a later time.
	 * 
	 * @param COSerialized
	 *            POJO
	 * @return the CourseOutline ID
	 * @throws Exception
	 */
	public String updateSerializedCourseOutline(COSerialized co)
			throws Exception;

	/**
	 * Adds the tool to the site. This method is used in the process of
	 * integrating a sakai tool to OpenSyllabus. If the tool is not present in
	 * the site, we add it before creating a reference
	 * 
	 * @param site
	 * @param toolId
	 */
	public void addTool(Site site, String toolId);

	/**
	 * We create a new course outline from a xmlContent
	 * 
	 * @param xmlData
	 * @return
	 */
	public COSerialized createCOFromData(String xmlData, String siteId);

	public Site getSite(String siteId) throws IdUnusedException;
}
