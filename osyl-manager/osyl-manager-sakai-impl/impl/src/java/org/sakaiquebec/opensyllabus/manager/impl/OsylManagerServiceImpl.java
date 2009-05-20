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

package org.sakaiquebec.opensyllabus.manager.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylManagerServiceImpl implements OsylManagerService {

    final static int TIMEOUT = 30;

    //private static Log log = LogFactory.getLog(OsylManagerServiceImpl.class);

    /**
     * File delimeter used to split the url of a resource
     */
    protected static final String FILE_DELIMITER = "\\.";

    /**
     * The name of the opensyllabusManager site to create.
     */
    private String osylManagerSiteName;

    public void setOsylManagerSiteName(String osylManagerSiteName) {
	this.osylManagerSiteName = osylManagerSiteName;
    }

    /**
     * The name of the realm associated to the opensyllabusManager
     */
    private String authzGroupName;

    public void setAuthzGroupName(String authzGroupName) {
	this.authzGroupName = authzGroupName;
    }

    /**
     * The server configuration service
     */
    private ServerConfigurationService serverConfigurationService;

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
	this.serverConfigurationService = serverConfigurationService;
    }
    
    /**
     * Set of all the functions to register in order to allow them in roles that
     * need them.
     */
    private Set<String> functionsToRegister;

    public void setFunctionsToRegister(Set<String> functionsToRegister) {
	this.functionsToRegister = functionsToRegister;
    }

    /**
     * HashMap of the functions to allow in a particular role. The key is the
     * role and the value is a list of the functions to allow.
     */
    private HashMap<String, List<String>> functionsToAllow;

    public void setFunctionsToAllow(
	    HashMap<String, List<String>> functionsToAllow) {
	this.functionsToAllow = functionsToAllow;
    }

    /**
     * HashMap of the functions to disallow in a particular role. The key is the
     * role and the value is a list of the functions to disallow.
     */
    private HashMap<String, List<String>> functionsToDisallow;

    public void setFunctionsToDisallow(
	    HashMap<String, List<String>> functionsToDisallow) {
	this.functionsToDisallow = functionsToDisallow;
    }

    /**
     * Sakai usr session manager injected by Spring.
     */
    private SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    /**
     * The site service to be injected by Spring
     */
    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    /**
     * The Osyl resource service to be injected by Spring
     */
    private OsylSiteService osylSiteService;

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    /**
     * The Osyl resource service to be injected by Spring
     */
    private OsylSecurityService osylSecurityService;

    public void setOsylSecurityService(OsylSecurityService osylSecurityService) {
	this.osylSecurityService = osylSecurityService;
    }

    /**
     * The chs to be injected by Spring
     */
    private ContentHostingService contentHostingService;

    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    /**
     * The type of site we are creating
     */
    protected static final String SITE_TYPE = "course";

    protected static final String STUDENT_ROLE = "Student";

    protected OsylPackage osylPackage;

    /**
     * Init method called at the initialization of the bean.
     */
    public void init() {
//	log
//		.info("OsylManagerServiceImpl service init() site managerSiteName == \""
//			+ this.osylManagerSiteName + "\"");

	if (null == this.osylManagerSiteName) {
	    // can't create
//	    log.info("init() managerSiteName is null");
	} else if (siteService.siteExists(this.osylManagerSiteName)) {
	    // no need to create
//	    log.info("init() site " + this.osylManagerSiteName
//		    + " already exists");
	} else {
	    // need to create
	    try {
		enableSecurityAdvisor();
		Session s = sessionManager.getCurrentSession();
		s.setUserId(UserDirectoryService.ADMIN_ID);

		Site osylManagerSite =
			siteService
				.addSite(this.osylManagerSiteName, SITE_TYPE);
		osylManagerSite.setTitle("OpenSyllabus Manager");
		osylManagerSite.setPublished(true);
		osylManagerSite.setJoinable(false);

		AuthzGroup currentGroup =
			AuthzGroupService.getInstance().getAuthzGroup(
				this.authzGroupName);

		for (Iterator<String> iFunctionsToRegister =
			this.functionsToRegister.iterator(); iFunctionsToRegister
			.hasNext();) {
		    FunctionManager.registerFunction(iFunctionsToRegister
			    .next());
		}

		for (Iterator<Entry<String, List<String>>> iFunctionsToAllow =
			this.functionsToAllow.entrySet().iterator(); iFunctionsToAllow
			.hasNext();) {
		    Entry<String, List<String>> entry =
			    iFunctionsToAllow.next();

		    Role role = currentGroup.getRole(entry.getKey());

		    role.allowFunctions(entry.getValue());
		}

		for (Iterator<Entry<String, List<String>>> iFunctionsToDisallow =
			this.functionsToDisallow.entrySet().iterator(); iFunctionsToDisallow
			.hasNext();) {
		    Entry<String, List<String>> entry =
			    iFunctionsToDisallow.next();

		    Role role = currentGroup.getRole(entry.getKey());

		    role.disallowFunctions(entry.getValue());
		}

		currentGroup.removeRole(STUDENT_ROLE);

		AuthzGroupService.save(currentGroup);

		// add Resources tool
		SitePage page = osylManagerSite.addPage();
		page.setTitle(this.osylManagerSiteName);
		page.addTool("sakai.opensyllabus.manager.tool");

		siteService.save(osylManagerSite);
//		log.debug("init() site " + this.osylManagerSiteName
//			+ " has been created");

	    } catch (IdInvalidException e) {
//		log.warn("IdInvalidException ", e);
	    } catch (IdUsedException e) {
		// we've already verified that the site doesn't exist but
		// this can occur if site was created by another server
		// in a cluster that is starting up at the same time.
//		log.warn("IdUsedException ", e);
	    } catch (PermissionException e) {
//		log.warn("PermissionException ", e);
	    } catch (IdUnusedException e) {
//		log.warn("IdUnusedException ", e);
	    } catch (GroupNotDefinedException e) {
		e.printStackTrace();
	    } catch (AuthzPermissionException e) {
		e.printStackTrace();
	    }
	}
    }

    public String getXmlDataFromZip(File zip) {

	osylPackage = new OsylPackage();
	osylPackage.unzipFile(zip);
	return osylPackage.getXmlData();
    }


    /**
     * Adds the tool to the given site
     * 
     * @param site
     * @param toolId
     */
    private void addTool(Site site, String toolId) {
	SitePage page = site.addPage();
	Tool tool = ToolManager.getTool(toolId);
	page.setTitle(tool.getTitle());
	page.setLayout(SitePage.LAYOUT_SINGLE_COL);
	ToolConfiguration toolConf = page.addTool();
	toolConf.setTool(toolId, tool);
	toolConf.setTitle(tool.getTitle());
	toolConf.setLayoutHints("0,0");

	try {
	    siteService.save(site);
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	} catch (PermissionException e) {
	    e.printStackTrace();
	}

    }

    /**
     * {@inheritDoc}
     */
    public String addRessource(String name, InputStream content,
	    String contentType, String siteId) throws Exception {
	try {
	    // Generation of a valid resourceOutput directory
	    String resourceOutputDir =
		    contentHostingService.getSiteCollection(siteId)
			    + WORK_DIRECTORY + "/";
	    
	    try {
		    @SuppressWarnings("unused")
			ContentCollection resourceOutputCollection =
		    	contentHostingService.getCollection(resourceOutputDir);
		    
	    } catch (IdUnusedException exc) {
	    	// collection is not existing yet, create it
	    	ContentCollectionEdit col = contentHostingService.
	    		addCollection(resourceOutputDir);
	    	ResourcePropertiesEdit fileProperties = col.
					getPropertiesEdit();
			fileProperties.addProperty(
					ResourceProperties.PROP_DISPLAY_NAME, WORK_DIRECTORY);
	    	contentHostingService.commitCollection(col);
	    	resourceOutputDir = col.getId();
	    }
	    
	    // Extraction of name and extension
	    String fileName;
	    String fileExtension;
	    int lastIndexOfPoint = name.lastIndexOf(".");
	    fileName = name.substring(0, lastIndexOfPoint);
	    fileExtension = name.substring(lastIndexOfPoint + 1, name.length());
	    // Add the resource and its content
	    ContentResourceEdit newResource =
		    contentHostingService.addResource(resourceOutputDir,
			    fileName, fileExtension, 3);
	    newResource.setContent(content);
	    newResource.setContentType(contentType);
	    contentHostingService.commitResource(newResource);

	    String resourceName = resourceOutputDir + name;
	    // osylSecurityService.applyPermissions(resourceName, permission);
	    return resourceName;
	} catch (ServerOverloadException e) {
	    e.printStackTrace();
	    // We wrap the exception in a java.lang.Exception.
	    // This way our "client" doesn't have to know about
	    // this Sakai exception.
	    throw new Exception(e);
	} catch (OverQuotaException e) {
	    e.printStackTrace();
	    // see previous comment
	    throw new Exception(e);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /**
     * Get a valid resource reference base site URL to be used in later calls.
     * 
     * @return a String of the base URL
     */
    private String getSiteReference(Site site) {
	String siteId = site.getId();
	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString = contentHostingService.getReference(val2);
	return refString;
    }

    /**
     * Add a collection (similar to a sub-directory) under the resource tool.
     * 
     * @param dir name of collection
     * @param parent where to create it (null means top-level)
     * @return boolean whether the collection was added or not
     * @throws Exception
     */
    private void addCollection(String dir, Site site) throws Exception {
	ContentCollectionEdit collection = null;
	String id = null;

	id = getSiteReference(site) + dir;
	id = id.substring(8) + "/";
	if (!collectionExist(id)) {
	    collection = contentHostingService.addCollection(id);
	    ResourcePropertiesEdit fileProperties =
		    collection.getPropertiesEdit();
	    fileProperties.addProperty(ResourceProperties.PROP_DISPLAY_NAME,
		    dir);
	    contentHostingService.commitCollection(collection);
	}
    }

    /**
     * Tells if a collection is already created in sakai.
     * 
     * @param a String of the collection id.
     * @return boolean whether the collection exists
     */
    private boolean collectionExist(String id) {
	try {
	    contentHostingService.getCollection(id);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    /**
     * {@inheritDoc}
     */
    public void readXML(String xmlReference, String siteId) {
	String xml;
	try {
	    InputStream is =
		    contentHostingService.getResource(xmlReference)
			    .streamContent();
	    InputStreamReader inputStreamReader = new InputStreamReader(is);
	    StringWriter writer = new StringWriter();
	    BufferedReader buffer = new BufferedReader(inputStreamReader);
	    String line = "";
	    while (null != (line = buffer.readLine())) {
		writer.write(line);
	    }
	    xml = writer.toString();
	    xml = new String(xml.getBytes(), "UTF-8");
	    osylSiteService.createCOFromData(xml, siteId);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    contentHostingService.removeResource(xmlReference);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * {@inheritDoc}
     */
    public void readZip(String zipReference, String siteId) {
	File file = new File("zipFile");
	try {
	    ContentResource contentResource =
		    contentHostingService.getResource(zipReference);
	    byte[] content = contentResource.getContent();
	    FileOutputStream writer = new FileOutputStream(file);
	    writer.write(content);

	} catch (Exception e) {
	    e.printStackTrace();
	}

	String xml = getXmlDataFromZip(file);
	osylSiteService.createCOFromData(xml, siteId);
	try {
	    contentHostingService.removeResource(zipReference);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public List<File> getImportedFiles() {
	return osylPackage.getImportedFiles();
    }

    /**
     * Establish a security advisor to allow the "embedded" azg work to occur
     * with no need for additional security permissions.
     */
    protected void enableSecurityAdvisor() {
	// put in a security advisor so we can create citationAdmin site without
	// need
	// of further permissions
	SecurityService.pushAdvisor(new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
		return SecurityAdvice.ALLOWED;
	    }
	});
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getOsylSitesMap() {

	Map<String, String> siteMap = new HashMap<String, String>();
	@SuppressWarnings("unchecked")
	List<Site> sites =
		siteService.getSites(SiteService.SelectionType.ACCESS, null,
			null, null, SiteService.SortType.TITLE_ASC, null);
	for (Iterator<Site> siteIterator = sites.iterator(); siteIterator
		.hasNext();) {
	    Site site = (Site) siteIterator.next();
	    @SuppressWarnings("unchecked")
	    List<SitePage> pagelist = site.getPages();
	    for (Iterator<SitePage> iter = pagelist.iterator(); iter.hasNext();) {
		SitePage sitePage = (SitePage) iter.next();
		if (!sitePage.getTools(
			new String[] { "sakai.opensyllabus.tool" }).isEmpty()) {
		    siteMap.put(site.getId(), site.getTitle());
		    break;
		}
	    }
	}
	deleteExpiredTemporaryExportFiles(siteMap);
	return siteMap;
    }

    /**
     * {@inheritDoc}
     */
    public String getOsylPackage(String siteId) {
	String url = null;

	OsylPackage osylPackage = new OsylPackage();

	Map<String, byte[]> filesMap = createFilesMap(siteId);

	byte[] byteArray = osylPackage.createOsylPackage(filesMap);

	try {
	    Site site = siteService.getSite(siteId);
	    String title = site.getTitle();
	    String resourceOutputDir =
		    contentHostingService.getSiteCollection(siteId);
	    try {
		addCollection(TEMP_DIRECTORY, site);
	    } catch (Exception e) {
	    }
	    resourceOutputDir += TEMP_DIRECTORY + "/";

	    ContentResourceEdit newResource =
		    contentHostingService.addResource(resourceOutputDir, title,
			    ".zip", 10);
	    String ressourceUrl = newResource.getUrl();
	    String filename =
		    ressourceUrl.substring(ressourceUrl.lastIndexOf("/") + 1,
			    ressourceUrl.length());
	    newResource.setContent(byteArray);
	    newResource.setContentType("application/zip");
	    contentHostingService.commitResource(newResource);
	    url = resourceOutputDir + filename;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return url;
    }

    /**
     * Create a Map<name,content> of the files which should be on the
     * osylpackage
     * 
     * @param siteId
     * @return
     */
    private Map<String, byte[]> createFilesMap(String siteId) {
	HashMap<String, byte[]> map = new HashMap<String, byte[]>();

	COSerialized coSerialized =
		osylSiteService.getSerializedCourseOutlineBySiteId(siteId);
	try {
	    byte[] xmlBytes =
		    coSerialized.getSerializedContent().getBytes("UTF-8");
	    map.put(OsylManagerService.CO_XML_FILENAME, xmlBytes);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	String resourceDir =
		contentHostingService.getSiteCollection(siteId)
			+ WORK_DIRECTORY + "/";
	try {
	    ContentCollection workContent =
		    contentHostingService.getCollection(resourceDir);
	    @SuppressWarnings("unchecked")
	    List<ContentEntity> members = workContent.getMemberResources();
	    for (Iterator<ContentEntity> iMbrs = members.iterator(); iMbrs
		    .hasNext();) {
		ContentEntity next = (ContentEntity) iMbrs.next();
		String thisEntityRef = (next.getReference()).substring(8);
		String name =
			thisEntityRef.substring(
				thisEntityRef.lastIndexOf("/") + 1,
				thisEntityRef.length());
		ContentResource contentResource =
			contentHostingService.getResource(thisEntityRef);
		map.put(name, contentResource.getContent());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return map;
    }

    /**
     * Delete temporary files and the temporary directory created when export CO
     */
    private void deleteExpiredTemporaryExportFiles(Map<String, String> siteMap) {
	int timeOut = getTimeOut();
	for (Iterator<String> iter = siteMap.keySet().iterator(); iter
		.hasNext();) {
	    try {
		Site site = siteService.getSite((String) iter.next());
		String id = getSiteReference(site) + TEMP_DIRECTORY;
		id = id.substring(8) + "/";
		if (collectionExist(id)) {
		    ContentCollection contentCollection =
			    contentHostingService.getCollection(id);
		    @SuppressWarnings("unchecked")
		    List<ContentEntity> members =
			    contentCollection.getMemberResources();

		    // process members
		    boolean hasContent = false;
		    for (Iterator<ContentEntity> iMbrs = members.iterator(); iMbrs
			    .hasNext();) {
			ContentEntity next = (ContentEntity) iMbrs.next();
			String thisEntityRef =
				(next.getReference()).substring(8);
			ContentResource contentResource =
				contentHostingService
					.getResource(thisEntityRef);

			Time creationDate =
				(Time) contentResource
					.getProperties()
					.getTimeProperty(
						ResourceProperties.PROP_CREATION_DATE);
			Time now = TimeService.newTime();
			Time expirationTime = TimeService.newTime();
			expirationTime.setTime(creationDate.getTime() + timeOut
				* 60000);
			if (now.after(expirationTime)) {
			    contentHostingService.removeResource(thisEntityRef);
			} else {
			    hasContent = true;
			}
		    }
		    if (!hasContent) {
			contentHostingService.removeCollection(id);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Get the timeout define in the sakai.properties
     * 
     * @return
     */
    private int getTimeOut() {
	int timeout = 0;

	//TODO Check first in sakai.properties, then in the components.xml as
	//a bean property and then use the constant.
	String timeOutString =
		serverConfigurationService
			.getString("opensyllabus.manager.timeOut");
	if (timeOutString != null && !"".equals(timeOutString)) {
	    timeout = Integer.parseInt(timeOutString);
	} else if (timeOutString == null) {
	    timeout = TIMEOUT;
	}
	return timeout;

    }
}
