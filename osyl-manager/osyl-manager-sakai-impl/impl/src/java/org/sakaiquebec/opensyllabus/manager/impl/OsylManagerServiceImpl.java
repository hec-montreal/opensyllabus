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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.GroupProvider;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.citation.api.Citation;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;
import org.sakaiquebec.opensyllabus.manager.impl.cms.Participant;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylManagerServiceImpl implements OsylManagerService {

    private static final String CITATION_EXTENSION = ".citation";

    private static final Log log =
	    LogFactory.getLog(OsylManagerServiceImpl.class);

    final static int TIMEOUT = 30;

    // private static Log log = LogFactory.getLog(OsylManagerServiceImpl.class);

    /**
     * The name of the opensyllabusManager site to create.
     */
    private String osylManagerSiteName;

    public void setOsylManagerSiteName(String osylManagerSiteName) {
	this.osylManagerSiteName = osylManagerSiteName;
    }

    private GroupProvider groupProvider =
	    (org.sakaiproject.authz.api.GroupProvider) ComponentManager
		    .get(org.sakaiproject.authz.api.GroupProvider.class);

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

    public void setServerConfigurationService(
	    ServerConfigurationService serverConfigurationService) {
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
     * The cms to be injected by Spring
     */
    private CourseManagementService courseManagementService;

    public void setCourseManagementService(
	    CourseManagementService courseManagementService) {
	this.courseManagementService = courseManagementService;
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
	// log
	// .info("OsylManagerServiceImpl service init() site managerSiteName == \""
	// + this.osylManagerSiteName + "\"");

	if (null == this.osylManagerSiteName) {
	    // can't create
	    // log.info("init() managerSiteName is null");
	} else if (siteService.siteExists(this.osylManagerSiteName)) {
	    // no need to create
	    // log.info("init() site " + this.osylManagerSiteName
	    // + " already exists");
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
		// log.debug("init() site " + this.osylManagerSiteName
		// + " has been created");

	    } catch (IdInvalidException e) {
		// log.warn("IdInvalidException ", e);
	    } catch (IdUsedException e) {
		// we've already verified that the site doesn't exist but
		// this can occur if site was created by another server
		// in a cluster that is starting up at the same time.
		// log.warn("IdUsedException ", e);
	    } catch (PermissionException e) {
		// log.warn("PermissionException ", e);
	    } catch (IdUnusedException e) {
		// log.warn("IdUnusedException ", e);
	    } catch (GroupNotDefinedException e) {
		e.printStackTrace();
	    } catch (AuthzPermissionException e) {
		e.printStackTrace();
	    }
	}
    }

    private String mkdirCollection(String resourceDirToCreate,
	    String propDisplayName) throws Exception {
	if (!collectionExist(resourceDirToCreate)) {
	    try {
		// collection is not existing yet, create it
		ContentCollectionEdit col =
			contentHostingService
				.addCollection(resourceDirToCreate);
		ResourcePropertiesEdit fileProperties = col.getPropertiesEdit();
		fileProperties.addProperty(
			ResourceProperties.PROP_DISPLAY_NAME, propDisplayName);
		contentHostingService.commitCollection(col);
		resourceDirToCreate = col.getId();
	    } catch (IdUsedException e) {
		log.debug("ID already used ?", e);
	    }
	}
	return resourceDirToCreate;
    }

    private void mkdirsCollection(String rootDir, String path) throws Exception {
	String[] subFolders = path.split("/");
	if (subFolders != null) {
	    String currentFolder = rootDir;
	    if (currentFolder.endsWith("/")) {
		currentFolder =
			currentFolder.substring(0, currentFolder.length() - 1);
	    }
	    for (int i = 0; i < subFolders.length; i++) {
		String subFolderName = subFolders[i];
		currentFolder += "/" + subFolders[i];
		mkdirCollection(currentFolder, subFolderName);
	    }
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

	    resourceOutputDir =
		    mkdirCollection(resourceOutputDir, WORK_DIRECTORY);

	    // Extraction of name and extension
	    int lastIndexOfPoint = name.lastIndexOf(".");
	    String fileName = name.substring(0, lastIndexOfPoint);
	    String fileExtension =
		    name.substring(lastIndexOfPoint + 1, name.length());
	    // check if filename is in a subdirectory,if true then create all
	    // necessary subfolders
	    int slashPos = fileName.lastIndexOf("/");
	    if (slashPos > 0) {
		String filePath = fileName.substring(0, slashPos);
		mkdirsCollection(resourceOutputDir, filePath);
		fileName = fileName.substring(slashPos + 1);
		resourceOutputDir += filePath + "/";
	    }

	    if ("citation".equals(fileExtension)) {
		// read input stream of file to get properties of citation
		byte[] bytes = new byte[1000];
		int val = content.read();
		int i = 0;
		while (val != -1) {
		    bytes[i] = (byte) val;
		    i++;
		    val = content.read();
		}
		String citationContent = new String(bytes);
		// TODO create citation
	    } else {
		// Add the resource and its content
		ContentResourceEdit newResource =
			contentHostingService.addResource(resourceOutputDir,
				fileName, fileExtension, 3);
		newResource.setContent(content);
		newResource.setContentType(contentType);
		contentHostingService.commitResource(newResource);
	    }
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
	    osylSiteService.importDataInCO(xml, siteId);
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
	try {
	    File zipTempfile =
		    File.createTempFile("osyl-package-import", ".zip");
	    ContentResource contentResource =
		    contentHostingService.getResource(zipReference);
	    byte[] content = contentResource.getContent();
	    FileOutputStream writer = new FileOutputStream(zipTempfile);
	    writer.write(content);

	    osylPackage = new OsylPackage();
	    osylPackage.unzip(zipTempfile);
	    String xml = osylPackage.getXml();

	    osylSiteService.importDataInCO(xml, siteId);
	    contentHostingService.removeResource(zipReference);
	    zipTempfile.delete();
	} catch (Exception e) {
	    log.error(e);
	}

    }

    public Map<File, String> getImportedFiles() {
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

    public Map<String, String> getOsylSites(String siteId) {

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
		    if (osylSiteService.hasBeenPublished(site.getId())) {
			boolean isInHierarchy = false;
			String parentId = site.getId();
			while (parentId != null && !isInHierarchy) {
			    if (parentId.equals(siteId))
				isInHierarchy = true;
			    try {
				parentId = osylSiteService.getParent(parentId);
			    } catch (Exception e) {
				parentId = null;
			    }
			}
			if (!isInHierarchy)
			    siteMap.put(site.getId(), site.getTitle());
		    }
		    break;
		}
	    }
	}
	return siteMap;
    }

    public Map<String, String> getPublishedOsylSites() {

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
		    if (osylSiteService.hasBeenPublished(site.getId())) {
			siteMap.put(site.getId(), site.getTitle());
		    }
		}
	    }
	}
	return siteMap;
    }

    public String getParent(String siteId) throws Exception {
	return osylSiteService.getParent(siteId);
    }

    /**
     * {@inheritDoc}
     */
    public String getOsylPackage(String siteId) {
	String url = null;

	try {
	    File zipFile = exportAndZip(siteId);
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
		    contentHostingService.addResource(resourceOutputDir, title
			    + "_" + siteId, ".zip", 10);
	    String ressourceUrl = newResource.getUrl();
	    String filename =
		    ressourceUrl.substring(ressourceUrl.lastIndexOf("/") + 1,
			    ressourceUrl.length());
	    newResource.setContent(new BufferedInputStream(new FileInputStream(
		    zipFile)));
	    newResource.setContentType("application/zip");
	    contentHostingService.commitResource(newResource);
	    zipFile.delete();
	    url = resourceOutputDir + filename;
	} catch (Exception e) {
	    log.error("Cannot create the zip package", e);
	}
	return url;
    }

    @SuppressWarnings("unchecked")
    private void retrieveFiles(ZipOutputStream zos,
	    ContentCollection collection, String rootDir)
	    throws PermissionException, IdUnusedException, TypeException,
	    ServerOverloadException, IOException {
	List<ContentEntity> members = collection.getMemberResources();
	for (ContentEntity entity : members) {
	    if (entity.isCollection()) {
		retrieveFiles(zos, (ContentCollection) entity, rootDir);
	    } else {
		String thisEntityRef = entity.getId();
		// the fileName is a subpath after rootDir
		String name = thisEntityRef.substring(rootDir.length());

		ContentResource contentResource =
			contentHostingService.getResource(thisEntityRef);
		if (CitationService.CITATION_LIST_ID.equals(contentResource
			.getResourceType())) {
		    // content resource is a citation:
		    // get properties of citation to save in a file
		    // TODO: citation file is created considering citation
		    // service of sdata ... modify this for general use
		    String colId = new String(contentResource.getContent());
		    CitationCollection col =
			    org.sakaiproject.citation.cover.CitationService
				    .getCollection(colId);
		    List<Citation> citations = col.getCitations();
		    for (Iterator<Citation> iter = citations.iterator(); iter
			    .hasNext();) {
			Citation citation = iter.next();
			String citationName =
				filterCitationName(citation.getDisplayName());
			// citation display name includes . at the end
			citationName =
				citationName.substring(0,
					citationName.length() - 1);
			String result = "listname=" + citationName;
			Map props = citation.getCitationProperties();
			for (Iterator<String> iter2 = props.keySet().iterator(); iter2
				.hasNext();) {
			    String key = iter2.next();
			    result += "&cipkeys=" + key;
			    Object value = props.get(key);
			    if (key.equals("sakai:displayname")) {
				result += "&cipvalues=" + citationName;
			    } else if (value instanceof Vector) {
				Vector val = (Vector) value;
				for (Iterator iter3 = val.iterator(); iter3
					.hasNext();) {
				    result +=
					    "&cipvalues="
						    + iter3.next().toString();
				}
			    } else {
				result += "&cipvalues=" + value.toString();
			    }
			}
			// save each citation as a file with .citation extension
			writeToZip(zos, citationName + CITATION_EXTENSION,
				result.getBytes());
		    }
		} else {
		    writeToZip(zos, name, contentResource.streamContent());
		}
	    }
	}
    }

    // Filter all slash, backslash, double and simple quote and replace by
    // underscore
    private String filterCitationName(String rawName) {
	return (rawName.replaceAll("\\/|\\\\|\"|'", "_"));
    }

    private void writeToZip(ZipOutputStream zos, String fileName,
	    InputStream inputStream) throws IOException {
	ZipEntry zipEntry = new ZipEntry(fileName);
	zos.putNextEntry(zipEntry);
	BufferedOutputStream bos = new BufferedOutputStream(zos);
	IOUtils.copy(inputStream, bos);
	bos.flush();
    }

    private void writeToZip(ZipOutputStream zos, String fileName, byte[] bytes)
	    throws IOException {
	ZipEntry zipEntry = new ZipEntry(fileName);
	zos.putNextEntry(zipEntry);
	zos.write((byte[]) bytes);
    }

    /**
     * List the files in a sites and zip them
     * 
     * @param siteId
     * @return zipFile a temporary zip file...
     * @throws IOException
     */
    private File exportAndZip(String siteId) throws Exception {
	// opening a new temporary zipfile
	File zipFile = File.createTempFile("osyl-package-export", ".zip");
	ZipOutputStream zos =
		new ZipOutputStream(new FileOutputStream(zipFile));

	// retrieving the xml file
	COSerialized coSerialized =
		osylSiteService.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);

	byte[] xmlBytes = coSerialized.getContent().getBytes("UTF-8");
	writeToZip(zos, OsylManagerService.CO_XML_FILENAME, xmlBytes);

	// retrieving other resources
	String resourceDir =
		contentHostingService.getSiteCollection(siteId)
			+ WORK_DIRECTORY + "/";
	try {
	    ContentCollection workContent =
		    contentHostingService.getCollection(resourceDir);
	    // recursively retrieve all files
	    retrieveFiles(zos, workContent, resourceDir);
	} catch (Exception e) {
	    log.error(e);
	    throw new Exception("Cannot retrieve files in site for zipping", e);
	}
	zos.close();
	return zipFile;
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
			String thisEntityRef = next.getId();
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

	// TODO Check first in sakai.properties, then in the components.xml as
	// a bean property and then use the constant.
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

    public void associate(String siteId, String parentId) throws Exception {
	osylSiteService.associate(siteId, parentId);
    }

    public void dissociate(String siteId, String parentId) throws Exception {
	osylSiteService.dissociate(siteId, parentId);
    }

    public Boolean associateToCM(String courseSectionId, String siteId) {
	// TODO: est-ce qu'on change le nom du site après que le lien soit créé

	String realmId = siteService.siteReference(siteId);
	try {
	    AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);
	} catch (GroupNotDefinedException e) {
	    log.warn(this + "Site realm not found", e);
	}

	boolean added = addParticipants(realmId, courseSectionId);

	return new Boolean(true);
    }

    private boolean addParticipants(String realmId, String courseSectionId) {
	boolean added = false;
	Map participantsMap = new ConcurrentHashMap();
	try {
	    AuthzGroup realm = AuthzGroupService.getAuthzGroup(realmId);

	    try {
		Section section =
			courseManagementService.getSection(courseSectionId);
		if (section != null) {
		    // in case of Section eid
		    EnrollmentSet enrollmentSet = section.getEnrollmentSet();
		    added =
			    addParticipantsFromEnrollmentSet(participantsMap,
				    realm, enrollmentSet, section.getTitle());
		    // add memberships
		    Set memberships =
			    courseManagementService
				    .getSectionMemberships(courseSectionId);
		    if (memberships != null && memberships.size() > 0) {
			added =
				addParticipantsFromMemberships(participantsMap,
					realm, memberships, section.getTitle());
		    }

		}

	    } catch (IdNotFoundException e) {
		log.warn(this + ".prepareParticipants: " + e.getMessage()
			+ " sectionId=" + courseSectionId, e);
	    }

	} catch (GroupNotDefinedException ee) {
	    log.warn(this + ".prepareParticipants:  IdUnusedException "
		    + realmId, ee);
	}
	return added;
    }

    /**
     * Add participant from provider-defined enrollment set
     * 
     * @param participantsMap
     * @param realm
     * @param enrollmentSet
     * @param sectionTitle
     */
    private boolean addParticipantsFromEnrollmentSet(Map participantsMap,
	    AuthzGroup realm, EnrollmentSet enrollmentSet, String sectionTitle) {
	boolean added = false;
	if (enrollmentSet != null) {
	    Set enrollments =
		    courseManagementService.getEnrollments(enrollmentSet
			    .getEid());
	    if (enrollments != null) {
		for (Iterator eIterator = enrollments.iterator(); eIterator
			.hasNext();) {
		    Enrollment e = (Enrollment) eIterator.next();
		    try {
			User user =
				UserDirectoryService
					.getUserByEid(e.getUserId());
			String userId = user.getId();
			// We add member here
			realm.addMember(userId, "Student", true, false);
			AuthzGroupService.save(realm);
			added = true;
			Member member = realm.getMember(userId);
			if (member != null && member.isProvided()) {
			    try {
				// get or add provided participant
				Participant participant;
				if (participantsMap.containsKey(userId)) {
				    participant =
					    (Participant) participantsMap
						    .get(userId);
				    if (!participant.section
					    .contains(sectionTitle)) {
					participant.section =
						participant.section
							.concat(", <br />"
								+ sectionTitle);
					participant.credits =
						participant.credits
							.concat(", <br />"
								+ e
									.getCredits());
				    }
				} else {
				    participant = new Participant();
				    participant.credits = e.getCredits();
				    participant.name = user.getSortName();
				    participant.providerRole =
					    member.getRole() != null ? member
						    .getRole().getId() : "";
				    participant.regId = "";
				    participant.removeable = false;
				    participant.role =
					    member.getRole() != null ? member
						    .getRole().getId() : "";
				    participant.section = sectionTitle;
				    participant.uniqname = userId;
				}
				participantsMap.put(userId, participant);
			    } catch (Exception ee) {
				log.warn(this
					+ ".addParticipantsFromEnrollmentSet: "
					+ ee.getMessage() + " user id = "
					+ userId, ee);
			    }
			}
		    } catch (UserNotDefinedException exception) {
			// deal with missing user quietly without throwing a
			// warning message
			log.warn(this + ".addParticipantsFromEnrollmentSet: "
				+ exception.getMessage() + " user id = "
				+ e.getUserId(), exception);
		    } catch (GroupNotDefinedException groupE) {
			groupE.printStackTrace();
		    } catch (AuthzPermissionException authzE) {
			authzE.printStackTrace();
		    }
		}
	    }
	}
	return added;
    }

    /**
     * Add participant from provider-defined membership set
     * 
     * @param participantsMap
     * @param realm
     * @param memberships
     * @param sectionTitle
     */
    private boolean addParticipantsFromMemberships(Map participantsMap,
	    AuthzGroup realm, Set memberships, String sectionTitle) {
	boolean added = false;
	if (memberships != null) {
	    for (Iterator mIterator = memberships.iterator(); mIterator
		    .hasNext();) {
		Membership m = (Membership) mIterator.next();
		try {
		    User user =
			    UserDirectoryService.getUserByEid(m.getUserId());
		    String userId = user.getId();
		    // We add member here
		    realm.addMember(userId, "Instructor", true, false);
		    Member member = realm.getMember(userId);
		    AuthzGroupService.save(realm);
		    added = true;
		    if (member != null && member.isProvided()) {

			// get or add provided participant
			Participant participant;
			if (participantsMap.containsKey(userId)) {
			    participant =
				    (Participant) participantsMap.get(userId);
			    if (!participant.section.contains(sectionTitle)) {
				participant.section =
					participant.section.concat(", <br />"
						+ sectionTitle);
			    }
			} else {
			    participant = new Participant();
			    participant.credits = "";
			    participant.name = user.getSortName();
			    participant.providerRole =
				    member.getRole() != null ? member.getRole()
					    .getId() : "";
			    participant.regId = "";
			    participant.removeable = false;
			    participant.role =
				    member.getRole() != null ? member.getRole()
					    .getId() : "";
			    participant.section = sectionTitle;
			    participant.uniqname = userId;
			}

			participantsMap.put(userId, participant);
		    }
		} catch (UserNotDefinedException exception) {
		    // deal with missing user quietly without throwing a
		    // warning message
		    log.warn(this
			    + ".addParticipantsFromMemberships: user id = "
			    + m.getUserId(), exception);
		} catch (GroupNotDefinedException groupE) {
		    groupE.printStackTrace();
		} catch (AuthzPermissionException authzE) {
		    authzE.printStackTrace();
		}
	    }

	}
	return added;
    }

    public Map<String, String> getCMCourses() {
	Map<String, String> cmCourses = new HashMap<String, String>();
	Set<CourseSet> courseSets = courseManagementService.getCourseSets();
	Set<CourseOffering> courseOffs = null;
	Set<Section> sections = null;
	CourseSet courseSet = null;
	CourseOffering courseOff = null;
	String value = "";
	Section courseS = null;
	if (courseSets == null)
	    return null;
	for (Iterator<CourseSet> cSets = courseSets.iterator(); cSets.hasNext();) {
	    courseSet = cSets.next();
	    courseOffs =
		    courseManagementService
			    .getCourseOfferingsInCourseSet(courseSet.getEid());
	    for (Iterator<CourseOffering> cOffs = courseOffs.iterator(); cOffs
		    .hasNext();) {
		courseOff = cOffs.next();
		sections =
			courseManagementService.getSections(courseOff.getEid());
		for (Iterator<Section> cSs = sections.iterator(); cSs.hasNext();) {
		    courseS = cSs.next();
		    String courseSTitle = courseS.getTitle();
		    String session = courseOff.getAcademicSession().getTitle();
		    String sigle = courseOff.getCanonicalCourseEid();
		    String section = courseSTitle.substring(courseSTitle.length()-3, courseSTitle.length());
		    // Info sur la section pas dans le CM
		    value = sigle+" "+session+" "+section;
		    cmCourses.put(courseS.getEid(), value);
		}

	    }
	}
	return cmCourses;
    }

}
