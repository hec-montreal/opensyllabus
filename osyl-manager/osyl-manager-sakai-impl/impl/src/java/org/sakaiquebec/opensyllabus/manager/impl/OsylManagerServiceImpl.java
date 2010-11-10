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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityAdvisor.SecurityAdvice;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.citation.api.Citation;
import org.sakaiproject.citation.api.CitationCollection;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.ArrayUtil;
import org.sakaiquebec.opensyllabus.api.OsylService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;
import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CMCourse;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSite;
import org.sakaiquebec.opensyllabus.shared.util.UUID;
import org.xml.sax.ContentHandler;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylManagerServiceImpl implements OsylManagerService {

    private static final String CITATION_EXTENSION = "CITATION";
    private final static String PROP_SITE_TERM = "term";

    private final static String PROP_SITE_TERM_EID = "term_eid";

    /** the web content tool id **/
    private final static String WEB_CONTENT_TOOL_ID = "sakai.iframe";

    /** the news tool **/
    private final static String NEWS_TOOL_ID = "sakai.news";

    private static final String SAKAI_SITE_TYPE = SiteService.SITE_SUBTYPE;

    private static final Log log =
	    LogFactory.getLog(OsylManagerServiceImpl.class);

    // Key to define the delay (in minutes) to wait before deleting export zip
    // files in sakai.properties
    public final static String EXPORT_DELETE_DELAY_MINUTES_KEY =
	    "opensyllabus.manager.deleteExportAfter";

    // default value: time in minutes to wait before deleting export zip files
    final static int EXPORT_DELETE_DELAY_MINUTES = 30;

    // private static Log log = LogFactory.getLog(OsylManagerServiceImpl.class);

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
    private OsylService osylService;

    public void setOsylService(OsylService osylService) {
	this.osylService = osylService;
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
     * The Osyl resource service to be injected by Spring
     */
    private OsylSecurityService osylSecurityService;

    public void setOsylSecurityService(OsylSecurityService osylSecurityService) {
	this.osylSecurityService = osylSecurityService;
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
		// Session s = sessionManager.getCurrentSession();
		// s.setUserId(UserDirectoryService.ADMIN_ID);

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
	    } finally {
		SecurityService.popAdvisor();
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
	    String contentType, String siteId, String resourceOutputDir)
	    throws Exception {

	try {

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

	    // Add the resource and its content
	    ContentResourceEdit newResource =
		    contentHostingService.addResource(resourceOutputDir,
			    fileName, fileExtension, 3);
	    newResource.setContent(content);
	    newResource.setContentType(contentType);
	    String resourceName =
		    (String) newResource.getProperties().get("DAV:displayname");
	    contentHostingService.commitResource(newResource,
		    NotificationService.NOTI_NONE);
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

    private void addCitations(File file, String siteId, String resourceOutputDir)
	    throws IOException {
	List<String> oldReferences = new ArrayList<String>();
	List<String> newReferences = new ArrayList<String>();

	CitationCollection importCollection =
		org.sakaiproject.citation.cover.CitationService.addCollection();

	CitationCollection collection =
		org.sakaiproject.citation.cover.CitationService.addCollection();

	Citation importCitation =
		org.sakaiproject.citation.cover.CitationService
			.getTemporaryCitation();

	List<String> tempList = new ArrayList<String>();

	String line = null;
	String oldId = null;

	try {
	    BufferedReader input = new BufferedReader(new FileReader(file));
	    try {
		while ((line = input.readLine()) != null) {
		    if ((line.trim()).length() > 0)
			if (!line.startsWith(CITATION_TAG))
			    tempList.add(line);
			else {
			    oldId =
				    line.substring(CITATION_TAG.length(), line
					    .length());
			    oldReferences.add(oldId);
			}

		    // We save the citation
		    if (line != null && line.length() > 1
			    && line.substring(0, 2).equalsIgnoreCase("ER")) {

			if (importCitation.importFromRisList(tempList)) {
			    org.sakaiproject.citation.cover.CitationService
				    .save(importCitation);
			    newReferences.add(importCitation.getId());
			    importCollection.add(importCitation);
			}
			tempList = new ArrayList<String>();
			importCitation =
				org.sakaiproject.citation.cover.CitationService
					.getTemporaryCitation();

		    }

		}
	    } finally {
		input.close();
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	collection.addAll(importCollection);
	String collectionName =
		file.getName().substring(0,
			file.getName().length() - CITATION_EXTENSION.length());
	osylService.linkCitationsToSite(collection, siteId, collectionName);

	COSerialized co =
		osylSiteService.getSerializedCourseOutlineBySiteId(siteId);
	String xml = co.getContent();
	String id = null;
	String newId = null;

	for (int i = 0; i < oldReferences.size(); i++) {
	    id = oldReferences.get(i);
	    newId = newReferences.get(i);
	    xml.replaceAll(id, newId);
	}
	co.setContent(xml);
	osylSiteService.createOrUpdateCO(co);

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
    public void readXML(String xmlReference, String siteId, String webapp)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] imports XML [" + xmlReference + "] into site " + siteId);
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
	    osylSiteService.importDataInCO(xml, siteId, null, webapp);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    contentHostingService.removeResource(xmlReference);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}

    }

    /**
     * {@inheritDoc}
     */
    public void readZip(String zipReference, String siteId, String webapp)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] imports zip [" + zipReference + "] into site " + siteId);

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

	    Map<String, String> filenameChangesMap =
		    importFilesInSite(zipReference, siteId);

	    // osylSiteService.importDataInCO(xml, siteId, filenameChangesMap,
	    // webapp);

	    /**
	     * FIXME Task SAKAI-1609. This code is used to update description
	     * and license of Sakai resource. It should be removed from there to
	     * the end of fixme. And the commented code above should be
	     * uncommented.
	     */
	    COModeledServer coModeledServer =
		    new COModeledServer(osylSiteService.importDataInCO(xml,
			    siteId, filenameChangesMap, webapp));

	    coModeledServer.XML2Model();
	    updateResourceMetaInfo(coModeledServer.getModeledContent());
	    // End of FIXME

	    contentHostingService.removeResource(zipReference);
	    zipTempfile.delete();
	} catch (Exception e) {
	    log.error(e);
	    throw e;
	}

    }

    /**
     * FIXME Task SAKAI-1609. This method is used to update the description and
     * the license of the Sakai resource during importation of a course outline
     * in a new site. It should be removed after the migration.
     * 
     * @param element
     */
    private void updateResourceMetaInfo(COElementAbstract element) {
	element.setId(UUID.uuid());
	element.setIdParent(null);
	if (element.isCOContentResourceProxy()) {
	    COContentResourceProxy coResProxy =
		    (COContentResourceProxy) element;
	    ContentResourceEdit newResource;
	    try {
		String uri =
			coResProxy.getResource().getProperty(
				COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URI);
		newResource = contentHostingService.editResource(uri);

		newResource.getPropertiesEdit().addProperty(
			ResourceProperties.PROP_DESCRIPTION,
			coResProxy.getResource().getProperty(
				COPropertiesType.DESCRIPTION));
		newResource.getPropertiesEdit().addProperty(
			ResourceProperties.PROP_COPYRIGHT_CHOICE,
			coResProxy.getResource().getProperty(
				COPropertiesType.LICENSE));

		contentHostingService.commitResource(newResource,
			NotificationService.NOTI_NONE);
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (IdUnusedException e) {
		e.printStackTrace();
	    } catch (TypeException e) {
		e.printStackTrace();
	    } catch (InUseException e) {
		e.printStackTrace();
	    } catch (OverQuotaException e) {
		e.printStackTrace();
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    }
	} else {
	    for (int i = 0; i < element.getChildrens().size(); i++) {
		COElementAbstract childElement =
			(COElementAbstract) element.getChildrens().get(i);
		updateResourceMetaInfo(childElement);
	    }
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

    public Map<String, String> getOsylSites(List<String> siteIds) {

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
		    //if (osylSiteService.hasBeenPublished(site.getId())) {
			boolean isInHierarchy = false;
			for (String siteId : siteIds) {
			    isInHierarchy =
				    isInHierarchy
					    || isSiteinSiteHierarchy(site
						    .getId(), siteId);
			}
			if (!isInHierarchy)
			    siteMap.put(site.getId(), site.getTitle());
		    //}
		    break;
		}
	    }
	}
	return siteMap;
    }

    private boolean isSiteinSiteHierarchy(String siteId, String siteId2) {
	boolean isInHierarchy = false;
	while (siteId != null && !isInHierarchy) {
	    if (siteId.equals(siteId2))
		isInHierarchy = true;
	    try {
		siteId = osylSiteService.getParent(siteId);
	    } catch (Exception e) {
		siteId = null;
	    }
	}
	return isInHierarchy;
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
    public String getOsylPackage(String siteId, String webappDir) {
	String url = null;

	try {
	    File zipFile = exportAndZip(siteId, webappDir);
	    Site site = siteService.getSite(siteId);
	    String title = site.getTitle();
	    String resourceOutputDir =
		    contentHostingService.getSiteCollection(siteId);
	    try {
		addCollection(TEMP_DIRECTORY, site);

	    } catch (Exception e) {
	    }
	    String directoryId =
		    contentHostingService.getSiteCollection(site.getId())
			    + TEMP_DIRECTORY + "/";
	    // HIDE COLLECTION
	    ContentCollectionEdit cce =
		    contentHostingService.editCollection(directoryId);
	    cce.setHidden();
	    contentHostingService.commitCollection(cce);

	    resourceOutputDir += TEMP_DIRECTORY + "/";

	    ContentResourceEdit newResource =
		    contentHostingService.addResource(resourceOutputDir, title
			    + "_" + siteId, ".zip", 30);
	    String ressourceUrl = newResource.getUrl();
	    String filename =
		    ressourceUrl.substring(ressourceUrl.lastIndexOf("/") + 1,
			    ressourceUrl.length());
	    newResource.setContent(new BufferedInputStream(new FileInputStream(
		    zipFile)));
	    newResource.setContentType("application/zip");
	    contentHostingService.commitResource(newResource,
		    NotificationService.NOTI_NONE);
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

		    StringBuilder builder = new StringBuilder();

		    for (Iterator<Citation> iter = citations.iterator(); iter
			    .hasNext();) {
			Citation citation = iter.next();

			exportRIS(builder, citation);

		    }
		    // save each citation as a file with .citation extension
		    writeToZip(zos, name + "." + CITATION_EXTENSION, builder
			    .toString().getBytes());
		} else {
		    writeToZip(zos, name, contentResource.streamContent());
		}
	    }
	}
    }

    /*
     * Create a RIS string of the citation to wich I add the citationId
     */
    private void exportRIS(StringBuilder builder, Citation citation) {
	builder.append(CITATION_TAG + citation.getId());
	builder.append("\n");
	try {

	    citation.exportRis(builder);
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    private void writeToZip(ZipOutputStream zos, String fileName,
	    InputStream inputStream) throws IOException {
	log.debug("writeToZip: adding file to zip: " + fileName);
	ZipEntry zipEntry = new ZipEntry(fileName);
	try {
	    zos.putNextEntry(zipEntry);
	    BufferedOutputStream bos = new BufferedOutputStream(zos);
	    IOUtils.copy(inputStream, bos);
	    bos.flush();
	    inputStream.close();
	} catch (Exception e) {
	    log.error("writeToZip: Could not add file to zip: " + fileName);
	    e.printStackTrace();
	}
    }

    private void writeToZip(ZipOutputStream zos, String fileName, byte[] bytes)
	    throws IOException {
	log.debug("writeToZip: adding file to zip: " + fileName);
	ZipEntry zipEntry = new ZipEntry(fileName);
	try {
	    zos.putNextEntry(zipEntry);
	    zos.write((byte[]) bytes);
	} catch (Exception e) {
	    log.error("writeToZip: Could not add file to zip:" + fileName);
	    e.printStackTrace();
	}
    }

    /**
     * List the files in a sites and zip them
     * 
     * @param siteId
     * @return zipFile a temporary zip file...
     * @throws IOException
     */
    private File exportAndZip(String siteId, String webappDir) throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] exports site: [" + siteId + "]");

	// opening a new temporary zipfile
	File zipFile = File.createTempFile("osyl-package-export", ".zip");
	ZipOutputStream zos =
		new ZipOutputStream(new FileOutputStream(zipFile));

	// retrieving the xml file
	COSerialized coSerialized =
		osylSiteService.getCourseOutlineForExport(siteId, webappDir);

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
	    e.printStackTrace();
	    throw new Exception("Cannot retrieve files in site for zipping", e);
	}
	zos.close();
	return zipFile;
    }

    /**
     * Delete temporary files and the temporary directory created when export CO
     */
    private void deleteExpiredTemporaryExportFiles(List<COSite> cosites) {
	int timeOut = getDeleteExportDelay();
	for (Iterator<COSite> iter = cosites.iterator(); iter.hasNext();) {
	    try {
		Site site =
			siteService.getSite(((COSite) iter.next()).getSiteId());
		String id = getSiteReference(site) + TEMP_DIRECTORY;
		id = id.substring(8) + "/";
		enableSecurityAdvisor();

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
		SecurityService.popAdvisor();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Get the delay (in minutes) to wait before deleting export zip defined in
     * the sakai.properties
     * 
     * @return
     */
    private int getDeleteExportDelay() {
	int timeout = 0;

	// TODO Check first in sakai.properties, then in the components.xml as
	// a bean property and then use the constant.
	String timeOutString =
		ServerConfigurationService
			.getString(EXPORT_DELETE_DELAY_MINUTES_KEY);
	if (timeOutString != null && !"".equals(timeOutString)) {
	    timeout = Integer.parseInt(timeOutString);
	} else if (timeOutString == null) {
	    timeout = EXPORT_DELETE_DELAY_MINUTES;
	}
	return timeout;

    }

    public void associate(String siteId, String parentId)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] associates [" + siteId + "] to parent [" + parentId + "]");
	osylSiteService.associate(siteId, parentId);
    }

    public void dissociate(String siteId, String parentId) throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] dissociates [" + siteId + "] from parent [" + parentId
		+ "]");
	osylSiteService.dissociate(siteId, parentId);
    }

    public void associateToCM(String courseSectionId, String siteId)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] associates [" + siteId + "] to course [" + courseSectionId
		+ "]");

	// TODO: est-ce qu'on change le nom du site après que le lien soit créé

	if (siteId != null) {
	    // added = addParticipants(realmId, courseSectionId);
	    // }
	    // // We add the site properties
	    // if (added) {
	    Site site = siteService.getSite(siteId);
	    ResourcePropertiesEdit rp = site.getPropertiesEdit();
	    Section courseSection =
		    courseManagementService.getSection(courseSectionId);
	    CourseOffering courseOff =
		    courseManagementService.getCourseOffering(courseSection
			    .getCourseOfferingEid());
	    AcademicSession term = courseOff.getAcademicSession();

	    rp.addProperty(PROP_SITE_TERM, term.getTitle());
	    rp.addProperty(PROP_SITE_TERM_EID, term.getEid());

	    site.setProviderGroupId(courseSectionId);
	    siteService.save(site);
	}
    }

    public void associateToCM(String courseSectionId, String siteId,
	    String webappDir) throws Exception {

	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] associates [" + siteId + "] to course [" + courseSectionId
		+ "]");

	if (siteId != null) {
	    Site site = siteService.getSite(siteId);
	    ResourcePropertiesEdit rp = site.getPropertiesEdit();
	    Section courseSection =
		    courseManagementService.getSection(courseSectionId);
	    CourseOffering courseOff =
		    courseManagementService.getCourseOffering(courseSection
			    .getCourseOfferingEid());
	    AcademicSession term = courseOff.getAcademicSession();

	    rp.addProperty(PROP_SITE_TERM, term.getTitle());
	    rp.addProperty(PROP_SITE_TERM_EID, term.getEid());

	    site.setProviderGroupId(courseSectionId);
	    siteService.save(site);
	    osylSiteService.updateCOCourseInformations(siteId, webappDir);

	}
    }

    public void dissociateFromCM(String siteId) throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] dissociates [" + siteId + "] from course management");

	if (siteId != null) {
	    Site site = siteService.getSite(siteId);
	    ResourcePropertiesEdit rp = site.getPropertiesEdit();
	    rp.addProperty(PROP_SITE_TERM, null);
	    rp.addProperty(PROP_SITE_TERM_EID, null);
	    site.setProviderGroupId(null);
	    siteService.save(site);
	}
    }

    public void dissociateFromCM(String siteId, String webappDir)
	    throws Exception {
	log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		+ "] dissociates [" + siteId + "] from course management");

	if (siteId != null) {
	    Site site = siteService.getSite(siteId);
	    ResourcePropertiesEdit rp = site.getPropertiesEdit();
	    rp.addProperty(PROP_SITE_TERM, null);
	    rp.addProperty(PROP_SITE_TERM_EID, null);
	    site.setProviderGroupId(null);
	    siteService.save(site);
	    osylSiteService.updateCOCourseInformations(siteId, webappDir);
	}
    }

    /**
     * Returns the list of all courses that start with the given argument
     * defined in the CM so that the user can associate a site to a specific
     * course.
     */
    public List<CMCourse> getCMCourses(String startsWith) {
	long start = System.currentTimeMillis();
	log.debug("getCMCourses that starts with " + startsWith
		+ " ##### START #####");
	List<CMCourse> cmCourses = new ArrayList<CMCourse>();
	Set<CourseSet> courseSets = courseManagementService.getCourseSets();
	Set<CourseOffering> courseOffs = null;
	Set<Section> sections = null;
	CourseSet courseSet = null;
	CourseOffering courseOff = null;
	String courseOffEid = null;
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
		courseOffEid = courseOff.getEid();
		sections = courseManagementService.getSections(courseOffEid);
		if (courseOffEid.startsWith(startsWith)) {
		    for (Iterator<Section> cSs = sections.iterator(); cSs
			    .hasNext();) {
			courseS = cSs.next();
			String courseTitle =
				courseManagementService.getCanonicalCourse(
					courseOff.getCanonicalCourseEid())
					.getTitle();
			String courseSId = courseS.getEid();
			String session =
				courseOff.getAcademicSession().getTitle();
			String sigle = courseOff.getCanonicalCourseEid();
			String section =
				courseSId.substring(courseSId.length() - 3,
					courseSId.length());

			String instructorsString = "";
			int studentNumber = -1;
			EnrollmentSet enrollmentSet =
				courseS.getEnrollmentSet();
			if (enrollmentSet != null) {
			    // Retrieve official instructors
			    Set<String> instructors =
				    enrollmentSet.getOfficialInstructors();
			    User user = null;
			    String name = null;
			    for (String instructor : instructors) {
				try {
				    user =
					    UserDirectoryService
						    .getUserByEid(instructor);
				    name = user.getDisplayName();
				    instructorsString += name + " & ";
				} catch (UserNotDefinedException e) {
				    e.printStackTrace();
				}
			    }
			    // retrieve student number
			    Set<Enrollment> enrollments =
				    courseManagementService
					    .getEnrollments(enrollmentSet
						    .getEid());
			    if (enrollments != null)
				studentNumber = enrollments.size();
			}
			if (!instructorsString.equals(""))
			    instructorsString =
				    instructorsString.substring(0,
					    instructorsString.length() - 3);

			CMCourse cmCourse = new CMCourse();
			cmCourse.setId(courseS.getEid());
			cmCourse.setSession(session);
			cmCourse.setName(courseTitle);
			cmCourse.setSigle(sigle);
			cmCourse.setSection(section);
			cmCourse.setInstructor(instructorsString);
			cmCourse.setStudentNumber(studentNumber);
			cmCourses.add(cmCourse);
		    }
		}
	    }
	}
	log.debug("getCMCourses ###### END ######" + elapsed(start) + " for "
		+ cmCourses.size() + " courses");
	return cmCourses;
    } // getCMCourses

    /**
     * Import files contained in the osylPackage to Sakai resources
     * 
     * @param zipReference
     * @param siteId
     */
    public Map<String, String> importFilesInSite(String zipReference,
	    String siteId) {
	Map<File, String> fileMap = (Map<File, String>) getImportedFiles();
	Map<String, String> fileNameChangesMap = new HashMap<String, String>();
	// Vars used to retreive metadata
	ContentHandler handler = null;
	Metadata metadata = null;
	Parser parser = null;
	String resourceOutputDir = null;
	try {
	    // Generation of a valid resourceOutput directory
	    resourceOutputDir =
		    contentHostingService.getSiteCollection(siteId)
			    + WORK_DIRECTORY + "/";

	    resourceOutputDir =
		    mkdirCollection(resourceOutputDir, WORK_DIRECTORY);
	} catch (Exception e1) {
	    log.error(e1.getMessage());
	    e1.printStackTrace();
	}

	Set<File> files = fileMap.keySet();
	for (File file : files) {
	    try {
		String fileNameToUse = fileMap.get(file);
		InputStream inputStream = new FileInputStream(file);

		// Retrieve file extension
		int lastIndexOfPoint = fileNameToUse.lastIndexOf(".");
		String fileExtension =
			fileNameToUse.substring(lastIndexOfPoint + 1,
				fileNameToUse.length());

		// Necessary inits to retreive content type
		handler = new BodyContentHandler();
		metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
		parser = new AutoDetectParser();
		parser
			.parse(inputStream, handler, metadata,
				new ParseContext());

		// We need to close the inputstream and rebuild it after the
		// parsing here,
		// otherwise the inputstream in unusable
		inputStream.close();
		inputStream = new FileInputStream(file);

		if (CITATION_EXTENSION.equals(fileExtension)) {
		    // read input stream of file to get properties of citation
		    addCitations(file, siteId, resourceOutputDir);
		} else {
		    String s =
			    addRessource(fileNameToUse, inputStream, metadata
				    .get(Metadata.CONTENT_TYPE), siteId,
				    resourceOutputDir);
		    if (!fileNameToUse.equals(s))
			fileNameChangesMap.put(fileNameToUse, s);
		}
		inputStream.close();
	    } catch (Exception e) {
		log.error(e);
		e.printStackTrace();
	    }
	}
	return fileNameChangesMap;
    }

    // only to improve readability while profiling
    private static String elapsed(long start) {
	return ": elapsed : " + (System.currentTimeMillis() - start) + " ms ";
    }

    /** {@inheritDoc} */
    public COSite getCoAndSiteInfo(String siteId, String searchTerm,
	    String academicSession) {
	long start = System.currentTimeMillis();
	Site site = null;
	COSite info = new COSite();

	try {
	    site = osylSiteService.getSite(siteId);
	} catch (IdUnusedException e) {
	    log.error(e.getMessage());
	    e.printStackTrace();
	}

	if (site != null
		&& "course".equals(site.getType())
		&& searchTerm != null
		&& site.getTitle().toLowerCase().contains(
			searchTerm.toLowerCase())
		&& site.getTitle().toLowerCase().contains(
			parseAcademicSession(academicSession).toLowerCase())) {
	    // Retrieve site info
	    info.setSiteId(siteId);
	    info.setSiteName(site.getTitle());
	    info.setSiteDescription(site.getDescription());
	    info.setSiteShortDescription(site.getShortDescription());
	    info.setSiteOwnerLastName(site.getCreatedBy().getLastName());
	    info.setSiteOwnerName(site.getCreatedBy().getFirstName());

	    // Retrieve CM info
	    String siteProviderId = site.getProviderGroupId();

	    if (courseManagementService.isSectionDefined(siteProviderId)) {
		Section section =
			courseManagementService.getSection(siteProviderId);

		// Retrieve course number
		CourseOffering courseOff =
			courseManagementService.getCourseOffering(section
				.getCourseOfferingEid());

		CanonicalCourse canCourse =
			courseManagementService.getCanonicalCourse(courseOff
				.getCanonicalCourseEid());

		// Retrieve official instructors
		EnrollmentSet enrollmentSet = section.getEnrollmentSet();

		if (enrollmentSet != null) {
		    Set<String> instructors =
			    enrollmentSet.getOfficialInstructors();
		    User user = null;
		    String name = null;
		    for (String instructor : instructors) {
			try {
			    user =
				    UserDirectoryService
					    .getUserByEid(instructor);
			    name = user.getDisplayName();
			    info.addCourseInstructor(name);
			} catch (UserNotDefinedException e) {
			    e.printStackTrace();
			}
		    }
		}

		info.setCourseNumber(canCourse.getEid());
		info.setCourseName(section.getTitle());
		info.setCourseSection(siteProviderId.substring(siteProviderId
			.length() - 3));
		info
			.setCourseSession(courseOff.getAcademicSession()
				.getTitle());
		info.setAcademicCareer(courseOff.getAcademicCareer());
	    }
	    // TODO: the coordinator is not saved in the cm. Correct
	    // this
	    // when done.
	    info.setCourseCoordinator(null);

	    info.setLastModifiedDate(osylSiteService
		    .getCoLastModifiedDate(siteId));
	    info.setLastPublicationDate(osylSiteService
		    .getCoLastPublicationDate(siteId));
	    if (osylSiteService
		    .getUnfusionnedSerializedCourseOutlineBySiteId(siteId) == null
		    || osylSiteService
			    .getUnfusionnedSerializedCourseOutlineBySiteId(
				    siteId).getContent() == null)
		info.setCoIsNull(true);

	    // Retrieve parent site
	    String parentSite = null;

	    try {
		parentSite = osylSiteService.getParent(siteId);
	    } catch (Exception e) {
		log.error(e.getMessage());
		e.printStackTrace();
	    }

	    info.setParentSite(parentSite);

	    // retrieve childs
	    List<String> childs = null;
	    try {
		childs = osylSiteService.getChildren(siteId);
	    } catch (Exception e) {
	    }
	    if (childs != null && !childs.isEmpty())
		info.setHasChild(true);
	} else {
	    info = null;
	}
	log.trace("getCoAndSiteInfo  " + elapsed(start) + "DONE " + siteId);
	return info;
    } // getCoAndSiteInfo

    /** {@inheritDoc} */
    public List<COSite> getAllCoAndSiteInfo(String searchTerm,
	    String academicSession) {
	long start = System.currentTimeMillis();
	List<COSite> allSitesInfo = null;
	COSite info = null;
	String currentUser = sessionManager.getCurrentSessionUserId();
	int siteCount = 0;

	log.trace("getAllCoAndSiteInfo (Site List ##### START #####)"
		+ elapsed(start));
	List<String> accessedSites =
		getSitesForUser(currentUser, SiteService.SITE_VISIT);

	log.trace("getAllCoAndSiteInfo (Site List ##### SITES #####)"
		+ elapsed(start));

	if (accessedSites != null) {
	    allSitesInfo = new ArrayList<COSite>();
	    int accessedSitesSize = accessedSites.size();
	    for (int i = 0; i < accessedSitesSize; i++) {
		info =
			getCoAndSiteInfo(accessedSites.get(i), searchTerm,
				academicSession);
		if (info != null) {
		    allSitesInfo.add(info);
		    siteCount++;
		}
	    }
	}

	// TODO: move this to the end of getOsylPackage() with a specific
	// path instead of iterating through all the sites!!!
	new DeleteExpiredTemporaryExportFiles(allSitesInfo).start();
	// deleteExpiredTemporaryExportFiles(allSitesInfo);
	return allSitesInfo;
    }

    @SuppressWarnings("unchecked")
    protected List<String> getSitesForUser(String userId, String permission) {
	log.debug("getSitesForUser ["
		+ sessionManager.getCurrentSession().getUserEid() + "/"
		+ permission + "]");

	List<String> l = new ArrayList<String>();

	// get the groups from Sakai
	Set<String> authzGroupIds =
		AuthzGroupService.getAuthzGroupsIsAllowed(userId, permission,
			null);
	Iterator<String> it = authzGroupIds.iterator();
	while (it.hasNext()) {
	    String authzGroupId = it.next();
	    Reference r = EntityManager.newReference(authzGroupId);
	    if (r.isKnownType()) {
		// check if this is a Sakai Site or Group
		if (r.getType().equals(SiteService.APPLICATION_ID)) {
		    String type = r.getSubType();
		    if (SAKAI_SITE_TYPE.equals(type)) {
			// this is a Site
			String siteId = r.getId();
			l.add(siteId);
		    }
		}
	    }
	}

	if (l.isEmpty()) {
	    log.info("Empty list of siteIds for user:" + userId
		    + ", permission: " + permission);
	}
	return l;
    }

    public List<CMAcademicSession> getAcademicSessions() {
	List<AcademicSession> acadSessionsList =
		courseManagementService.getAcademicSessions();
	List<CMAcademicSession> cmAcadSessionsList =
		new Vector<CMAcademicSession>();

	for (AcademicSession acadSession : acadSessionsList) {
	    CMAcademicSession cmAcadSession = new CMAcademicSession();
	    cmAcadSession.setId(acadSession.getEid());
	    cmAcadSession.setTitle(acadSession.getTitle());
	    cmAcadSession.setDescription(acadSession.getDescription());
	    cmAcadSession.setStartDate(acadSession.getStartDate());
	    cmAcadSession.setEndDate(acadSession.getEndDate());
	    cmAcadSessionsList.add(cmAcadSession);
	}
	Collections.sort(cmAcadSessionsList);
	return cmAcadSessionsList;
    }

    class DeleteExpiredTemporaryExportFiles extends Thread {

	List<COSite> allSitesInfo;

	public DeleteExpiredTemporaryExportFiles(List<COSite> allSitesInfo) {
	    this.allSitesInfo = allSitesInfo;
	}

	public void run() {
	    int time = 180000;

	    try {
		sleep(time);
	    } catch (InterruptedException e) {
	    }
	    Session s = sessionManager.getCurrentSession();
	    s.setUserId(UserDirectoryService.ADMIN_ID);

	    long start = System.currentTimeMillis();
	    log.debug("deleteExpiredTemporaryExportFiles");
	    deleteExpiredTemporaryExportFiles(allSitesInfo);
	    log.debug("deleteExpiredTemporaryExportFiles complete"
		    + elapsed(start));
	}
    }

    private String parseAcademicSession(String academicSession) {

	// We create a CMAcademicSession with academicSession as the id. Then,
	// we use the session name created from the id to compare the site title
	// with the academic session name.
	if (academicSession != null && !"".equals(academicSession)) {
	    AcademicSession acadSession =
		    courseManagementService.getAcademicSession(academicSession);
	    CMAcademicSession cmAcadSession = new CMAcademicSession();
	    cmAcadSession.setId(acadSession.getEid());
	    cmAcadSession.setTitle(acadSession.getTitle());
	    cmAcadSession.setDescription(acadSession.getDescription());
	    cmAcadSession.setStartDate(acadSession.getStartDate());
	    cmAcadSession.setEndDate(acadSession.getEndDate());
	    academicSession = cmAcadSession.getSessionName();
	}
	return academicSession;
    }

    public void copySite(String siteFrom, String siteTo) throws Exception {
	long start = System.currentTimeMillis();
	Site newSite = null;
	Site oldSite = null;

	if (osylSecurityService
		.getCurrentUserRole()
		.equals(
			OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)) {
	    SecurityService.pushAdvisor(new org.sakaiproject.authz.api.SecurityAdvisor() {
		public SecurityAdvice isAllowed(String userId,
			String function, String reference) {
		    return SecurityAdvice.ALLOWED;
		}
	    });

	}

	newSite = siteService.getSite(siteTo);
	oldSite = siteService.getSite(siteFrom);

	String val2 = contentHostingService.getSiteCollection(newSite.getId());
	String refString =
		contentHostingService.getReference(val2).substring(8);

	String id_publish = refString + PUBLISH_DIRECTORY + "/";
	String id_work = refString + WORK_DIRECTORY + "/";
	try {
	    // We remove all resources in the work directory collection
	    ContentCollection workContent =
		    contentHostingService.getCollection(id_work);

	    @SuppressWarnings("unchecked")
	    List<ContentEntity> workMembers = workContent.getMemberResources();
	    for (Iterator<ContentEntity> wMbrs = workMembers.iterator(); wMbrs
		    .hasNext();) {
		ContentEntity next = (ContentEntity) wMbrs.next();
		String thisEntityRef = next.getId();
		if (next.isCollection())
		    contentHostingService.removeCollection(thisEntityRef);
		else
		    contentHostingService.removeResource(thisEntityRef);
	    }
	} catch (Exception e) {
	    log.warn("Could not delete work content:" + e);
	}

	// get the tool id list
	List<String> toolIdList = new Vector<String>();

	// list all site tools which are displayed on its own page
	@SuppressWarnings("unchecked")
	List<SitePage> sitePages = oldSite.getPages();
	if (sitePages != null) {
	    for (SitePage page : sitePages) {
		@SuppressWarnings("unchecked")
		List<ToolConfiguration> pageToolsList = page.getTools(0);
		// we only handle one tool per page case
		if (page.getLayout() == SitePage.LAYOUT_SINGLE_COL
			&& pageToolsList.size() == 1) {
		    toolIdList.add(pageToolsList.get(0).getToolId());
		}
	    }
	}

	// Remove all old contents before importing contents from new site
	importToolIntoSiteMigrate(toolIdList, newSite, oldSite);

	// We remove all resources in the publish directory collection
	ContentCollection publishContent =
		contentHostingService.getCollection(id_publish);

	@SuppressWarnings("unchecked")
	List<ContentEntity> membersPublished =
		publishContent.getMemberResources();
	for (Iterator<ContentEntity> pMbrs = membersPublished.iterator(); pMbrs
		.hasNext();) {
	    ContentEntity next = (ContentEntity) pMbrs.next();
	    String thisEntityRef = next.getId();
	    if (next.isCollection())
		contentHostingService.removeCollection(thisEntityRef);
	    else
		contentHostingService.removeResource(thisEntityRef);
	}

	// we hide work directory
	ContentCollectionEdit cce =
		contentHostingService.editCollection(id_work);
	cce.setHidden();
	contentHostingService.commitCollection(cce);

	// we update citation ids in the course outline
	updateCitationIds(siteFrom, siteTo);

	siteService.save(newSite);
	
	if (osylSecurityService
		.getCurrentUserRole()
		.equals(
			OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR)) {
	    SecurityService.clearAdvisors();
	}


	log.info("Finished copying site [" + siteFrom + "] in "
		+ (System.currentTimeMillis() - start) + " ms");
    }

    private void updateCitationIds(String oldSiteId, String newSiteId) {

	// We retieve information from the new citations
	String siteColl = contentHostingService.getSiteCollection(newSiteId);

	List<ContentResource> resources =
		contentHostingService.getAllResources(siteColl);

	String collId = null;
	Map<String, CitationCollection> newCitations =
		new HashMap<String, CitationCollection>();
	List<Citation> fromColl = null;

	for (ContentResource resource : resources) {
	    if (resource.getResourceType().equalsIgnoreCase(
		    "org.sakaiproject.citation.impl.CitationList")) {
		try {
		    collId = new String(resource.getContent());
		    CitationCollection citaColl =
			    org.sakaiproject.citation.cover.CitationService
				    .getCollection(collId);
		    fromColl = citaColl.getCitations();

		    for (Citation citation : fromColl) {
			newCitations.put(resource.getId() + "/"
				+ citation.getId(), citaColl);
		    }
		} catch (ServerOverloadException e) {
		    log.debug(e);
		} catch (IdUnusedException e) {
		    log.debug(e);
		}
	    }
	}

	// we retrieve informations from old citations
	COSerialized co =
		osylSiteService.getSerializedCourseOutlineBySiteId(newSiteId);
	COModeledServer model = new COModeledServer(co);
	Map<String, String> citationsChangeMap = new HashMap<String, String>();

	model.XML2Model();

	Map<String, String> oldCitations = model.getAllCitations();
	String uri = null;
	Citation oldCitation = null;
	CitationCollection oldCollection = null;
	String oldCollectionId = null;
	String oldCollectionRef = null;

	CitationCollection newColl = null;
	Citation newCitation = null;

	for (String id : oldCitations.keySet()) {
	    uri = oldCitations.get(id);
	    uri = uri.replaceFirst(newSiteId, oldSiteId);

	    oldCollectionRef = uri.substring(0, uri.lastIndexOf("/"));
	    try {
		oldCollectionId =
			new String((contentHostingService
				.getResource(oldCollectionRef)).getContent());
		oldCollection =
			org.sakaiproject.citation.cover.CitationService
				.getCollection(oldCollectionId);
		oldCitation =
			oldCollection.getCitation(uri.substring(
				uri.lastIndexOf("/") + 1, uri.length()).trim());

		for (String newCitationRef : newCitations.keySet()) {

		    oldCollectionRef =
			    oldCollectionRef.replaceFirst(oldSiteId, newSiteId);

		    if (newCitationRef.contains(oldCollectionRef)) {
			newColl = newCitations.get(newCitationRef);
			newCitation =
				newColl.getCitation(newCitationRef.substring(
					newCitationRef.lastIndexOf("/") + 1,
					newCitationRef.length()).trim());

			// We update the citation id
			if (compareCitations(oldCitation, newCitation)) {
			    citationsChangeMap.put(oldCitation.getId(),
				    newCitation.getId());
			    continue;
			}
		    }
		}
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (IdUnusedException e) {
		e.printStackTrace();
	    } catch (TypeException e) {
		e.printStackTrace();
	    } finally {
		model.resetXML(citationsChangeMap);
		model.model2XML();
		co.setContent(model.getSerializedContent());
		try {
		    osylSiteService.updateSerializedCourseOutline(co);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}

    }

    private boolean compareCitations(Citation thisCitation,
	    Citation otherCitation) {

	StringBuilder thisCit = new StringBuilder();
	StringBuilder otherCit = new StringBuilder();
	try {
	    thisCitation.exportRis(thisCit);
	    otherCitation.exportRis(otherCit);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	if ((thisCit.toString()).equalsIgnoreCase(otherCit.toString()))
	    return true;

	return false;

    }

    /**
     * @param state
     * @return Get a list of all tools that should be included as options for
     *         import
     */
    protected List getToolsAvailableForImport(Site oldSite,
	    List<String> toolIdList) {
	// The Web Content and News tools do not follow the standard rules for
	// import
	// Even if the current site does not contain the tool, News and WC will
	// be
	// an option if the imported site contains it
	boolean displayWebContent = false;
	boolean displayNews = false;

	if (oldSite.getToolForCommonId(WEB_CONTENT_TOOL_ID) != null)
	    displayWebContent = true;
	if (oldSite.getToolForCommonId(NEWS_TOOL_ID) != null)
	    displayNews = true;

	if (displayWebContent && !toolIdList.contains(WEB_CONTENT_TOOL_ID))
	    toolIdList.add(WEB_CONTENT_TOOL_ID);
	if (displayNews && !toolIdList.contains(NEWS_TOOL_ID))
	    toolIdList.add(NEWS_TOOL_ID);

	return toolIdList;
    } // getToolsAvailableForImport

    private void importToolIntoSiteMigrate(List toolIds, Site newSite,
	    Site oldSite) {

	// import resources first
	boolean resourcesImported = false;
	for (int i = 0; i < toolIds.size() && !resourcesImported; i++) {
	    String toolId = (String) toolIds.get(i);

	    if (toolId.equalsIgnoreCase("sakai.resources")) {
		String fromSiteId = oldSite.getId();
		String toSiteId = newSite.getId();

		String fromSiteCollectionId =
			contentHostingService.getSiteCollection(fromSiteId);
		String toSiteCollectionId =
			contentHostingService.getSiteCollection(toSiteId);
		transferCopyEntitiesMigrate(toolId, fromSiteCollectionId,
			toSiteCollectionId);
		resourcesImported = true;
	    }
	}

	// import other tools then
	for (int i = 0; i < toolIds.size(); i++) {
	    String toolId = (String) toolIds.get(i);
	    if (!toolId.equalsIgnoreCase("sakai.resources")) {
		String fromSiteId = oldSite.getId();
		String toSiteId = newSite.getId();
		transferCopyEntitiesMigrate(toolId, fromSiteId, toSiteId);
	    }
	}

    } // importToolIntoSiteMigrate

    protected void transferCopyEntitiesMigrate(String toolId,
	    String fromContext, String toContext) {

	for (Iterator i = EntityManager.getEntityProducers().iterator(); i
		.hasNext();) {
	    EntityProducer ep = (EntityProducer) i.next();
	    if (ep instanceof EntityTransferrer) {
		try {
		    EntityTransferrer et = (EntityTransferrer) ep;

		    // if this producer claims this tool id
		    if (ArrayUtil.contains(et.myToolIds(), toolId)) {
			et.transferCopyEntities(fromContext, toContext,
				new Vector(), true);
		    }
		} catch (Throwable t) {
		    log.warn(
			    "Error encountered while asking EntityTransfer to transferCopyEntities from: "
				    + fromContext + " to: " + toContext, t);
		}
	    }
	}
    }

}
