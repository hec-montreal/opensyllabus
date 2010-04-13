package org.sakaiquebec.opensyllabus.common.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

public class OsylPublishServiceImpl implements OsylPublishService {

    private static Log log = LogFactory.getLog(OsylPublishServiceImpl.class);

    /**
     * The security service to be injected by Spring
     * 
     * @uml.property name="osylSecurityService"
     * @uml.associationEnd
     */
    private OsylSecurityService osylSecurityService;

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /**
     * The chs to be injected by Spring
     * 
     * @uml.property name="contentHostingService"
     * @uml.associationEnd multiplicity="(0 -1)" ordering="true"
     *                     elementType="org.sakaiproject.content.api.ContentEntity"
     *                     qualifier
     *                     ="substring:java.lang.String java.lang.String"
     */
    private ContentHostingService contentHostingService;

    /**
     * Sets the <code>ContentHostingService</code>.
     * 
     * @param contentHostingService
     * @uml.property name="contentHostingService"
     */
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT from OsylPublish service");
    }

    /*
     * the tool manager to be injected by Spring private ToolManager
     * toolManager; / Sets the <code>ToolManager</code>.
     * @param toolManager public void setToolManager(ToolManager toolManager) {
     * this.toolManager = toolManager; }
     */
    /**
     * The resouceDao to be injected by Spring
     * 
     * @uml.property name="resourceDao"
     * @uml.associationEnd
     */
    private ResourceDao resourceDao;

    /**
     * Sets the {@link ResourceDao} .
     * 
     * @param resourceDao
     * @uml.property name="resourceDao"
     */
    public void setResourceDao(ResourceDao resourceDao) {
	this.resourceDao = resourceDao;
    }

    /**
     * The configDao to be injected by Spring
     * 
     * @uml.property name="configDao"
     * @uml.associationEnd
     */
    private COConfigDao configDao;

    /**
     * Sets the {@link COConfigDao} .
     * 
     * @param configDao
     * @uml.property name="configDao"
     */
    public void setConfigDao(COConfigDao configDao) {
	this.configDao = configDao;
    }

    /**
     * The config service to be injected by Spring
     * 
     * @uml.property name="osylConfigService"
     * @uml.associationEnd
     */
    private OsylConfigService osylConfigService;

    /**
     * Sets the {@link OsylConfigService}.
     * 
     * @param configService
     */
    public void setConfigService(OsylConfigService configService) {
	this.osylConfigService = configService;
    }

    /**
     * The OsylSite service to be injected by Spring
     * 
     * @uml.property name="osylSiteService"
     * @uml.associationEnd
     */
    private OsylSiteService osylSiteService;

    /**
     * Sets the {@link OsylSiteService} .
     * 
     * @param osylSiteService
     * @uml.property name="osylSiteService"
     */
    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    private CORelationDao coRelationDao;

    /**
     * Sets the {@link CORelationDao}.
     * 
     * @param configDao
     */
    public void setCoRelationDao(CORelationDao relationDao) {
	this.coRelationDao = relationDao;
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType, String webappDir) {
	COSerialized thisCo = null;
	String siteId = "";
	String configRef;
	try {
	    siteId = osylSiteService.getCurrentSiteId();
	    thisCo =
		    resourceDao
			    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
				    siteId, accessType);
	    osylSiteService.getSiteInfo(thisCo, siteId);
	    configRef = thisCo.getOsylConfig().getConfigRef();
	} catch (Exception e) {
	    configRef = OsylConfigService.DEFAULT_CONFIG_REF;
	    log.error("Unable to retrieve published course outline for"
		    + " access type [" + accessType + "]", e);
	}
	try {
	    thisCo =
		    osylConfigService.fillCo(webappDir
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + configRef, thisCo);
	} catch (Exception e) {
	    log.error("Unable to fill course outline", e);
	}
	return thisCo;
    }

    /**
     * Creates or updates the corresponding entries in the database and copies
     * the ressources
     * 
     * @param String webapp dir (absolute pathname !?)
     */
    public void publish(String webappDir, String siteId) throws Exception {

	SecurityService.pushAdvisor(new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
		return SecurityAdvice.ALLOWED;
	    }
	});

	COSerialized co = osylSiteService.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	log.info("Publishing course outline for site [" + co.getTitle() + "]");
	COModeledServer coModeled = new COModeledServer(co);

	// PRE-PUBLICATION
	// change work directory to publish directory
	coModeled.XML2Model(true);
	coModeled.model2XML();
	co.setContent(coModeled.getSerializedContent());

	COSerialized publishedCO = null;
	try {
	    publishedCO =
		    resourceDao.getPrePublishSerializedCourseOutlineBySiteId(co
			    .getSiteId());
	} catch (Exception e) {
	}

	// Create a course outline with security public
	if (publishedCO == null) {
	    publishedCO = new COSerialized(co);
	    publishedCO.setCoId(IdManager.createUuid());
	    publishedCO.setAccess("");
	    publishedCO.setPublished(true);
	    resourceDao.createOrUpdateCourseOutline(publishedCO);
	} else {
	    publishedCO.setContent(co.getContent());
	    resourceDao.createOrUpdateCourseOutline(publishedCO);
	}

	// PUBLICATION
	// TODO verify hierarchy compatibility and publish only if compatible

	Map<String, String> documentSecurityMap =
		coModeled.getDocumentSecurityMap();

	publication(co.getSiteId(), webappDir);

	copyWorkToPublish(documentSecurityMap);

	SecurityService.clearAdvisors();
    }

    private void publication(String siteId, String webappDir) throws Exception {

	COSerialized hierarchyFussionedCO =
		osylSiteService.getSerializedCourseOutlineBySiteId(siteId);

	COModeledServer coModeled =
		osylSiteService.getFusionnedPrePublishedHierarchy(siteId);
	if (coModeled != null) {
	    coModeled.model2XML();
	    hierarchyFussionedCO.setContent(coModeled.getSerializedContent());

	    // Create a course outline with security public
	    publish(hierarchyFussionedCO, SecurityInterface.ACCESS_PUBLIC,
		    webappDir);

	    // Create a course outline with security attendee
	    publish(hierarchyFussionedCO, SecurityInterface.ACCESS_ATTENDEE,
		    webappDir);

	    publishChild(siteId, webappDir);
	}
    }

    private void publishChild(String siteId, String webappDir) {
	List<CORelation> coRelationList;
	try {
	    coRelationList =
		    coRelationDao
			    .getCourseOutlineChildren(siteId);
	    for (Iterator<CORelation> coRelationIter =
		    coRelationList.iterator(); coRelationIter.hasNext();) {
		String childId = coRelationIter.next().getChild();
		publication(childId, webappDir);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /*
     * private String getCurrentSiteReference() { String val1 =
     * toolManager.getCurrentPlacement().getContext(); String val2 =
     * contentHostingService.getSiteCollection(val1); String refString =
     * contentHostingService.getReference(val2); return refString; }
     */
    /**
     * Copies work's folder content to publish folder.
     */
    private void copyWorkToPublish(Map<String, String> documentSecurityMap)
	    throws Exception {
	String currentSiteRef = osylSiteService.getCurrentSiteReference();
	String id_work = (currentSiteRef + WORK_DIRECTORY + "/").substring(8);
	String id_publish =
		(currentSiteRef + PUBLISH_DIRECTORY + "/").substring(8);

	try {
	    ContentCollection workContent =
		    contentHostingService.getCollection(id_work);

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

	    copyWorkToPublish(workContent, documentSecurityMap);

	} catch (Exception e) {
	    log.error(
		    "Unable to copy the work folder content to publish folder",
		    e);
	    throw e;
	}
    }

    @SuppressWarnings("unchecked")
    private void copyWorkToPublish(ContentCollection directory,
	    Map<String, String> documentSecurityMap) throws Exception {
	String currentSiteRef =
		osylSiteService.getCurrentSiteReference().substring(8);
	List<ContentEntity> members = directory.getMemberResources();
	for (Iterator<ContentEntity> iMbrs = members.iterator(); iMbrs
		.hasNext();) {
	    ContentEntity next = (ContentEntity) iMbrs.next();
	    String thisEntityRef = next.getId();

	    if (next.isCollection()) {
		ContentCollection collection = (ContentCollection) next;
		copyWorkToPublish(collection, documentSecurityMap);
	    } else {
		String permission = documentSecurityMap.get(thisEntityRef);
		if (permission != null) {
		    // doc exists in CO
		    String this_work_id = directory.getId();

		    String this_publish_directory =
			    currentSiteRef
				    + PUBLISH_DIRECTORY
				    + this_work_id.substring(this_work_id
					    .lastIndexOf(currentSiteRef)
					    + currentSiteRef.length()
					    + WORK_DIRECTORY.length(),
					    this_work_id.length());

		    if (!contentHostingService
			    .isCollection(this_publish_directory)) {
			ContentCollectionEdit publishContentEdit;
			publishContentEdit =
				contentHostingService
					.addCollection(this_publish_directory);

			contentHostingService
				.commitCollection(publishContentEdit);
		    }
		    String newId =
			    contentHostingService.copyIntoFolder(thisEntityRef,
				    this_publish_directory);
		    // Permission application
		    osylSecurityService.applyPermissions(newId, permission);
		}
	    }
	}

    }

    private void publish(COSerialized co, String access, String webappDir)
	    throws Exception {
	COSerialized publishedCO = null;
	try {
	    publishedCO =
		    resourceDao
			    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
				    co.getSiteId(), access);
	} catch (Exception e) {
	}

	// Create a course outline with specified access
	if (publishedCO == null) {
	    publishedCO = new COSerialized(co);
	    publishedCO.setCoId(IdManager.createUuid());
	    publishedCO.setContent(transformXmlForGroup(co.getContent(),
		    access, webappDir));
	    publishedCO.setAccess(access);
	    publishedCO.setPublished(true);
	    resourceDao.createOrUpdateCourseOutline(publishedCO);
	} else {
	    publishedCO.setContent(transformXmlForGroup(co.getContent(),
		    access, webappDir));
	    resourceDao.createOrUpdateCourseOutline(publishedCO);
	}
    }

    public String transformXmlForGroup(String content, String group,
	    String webappDir) throws Exception {
	TransformerFactory tFactory = TransformerFactory.newInstance();
	String xslFileName =
		OsylSiteService.XSL_PREFIX + group
			+ OsylSiteService.XSL_FILE_EXTENSION;
	// Retrieve xml and xsl from the webapps/xslt
	File coXslFile =
		new File(webappDir + File.separator
			+ OsylSiteService.XSLT_DIRECTORY + File.separator,
			xslFileName);
	try {
	    // retrieve the Xml source
	    StreamSource coXmlContentSource =
		    new StreamSource(new ByteArrayInputStream(content
			    .getBytes("UTF-8")));
	    // retrieve the Xsl source
	    StreamSource coXslContentSource = new StreamSource(coXslFile);
	    // we use a ByteArrayOutputStream to avoid using a file
	    ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
	    StreamResult xmlResult = new StreamResult(out);

	    Transformer transformerXml =
		    tFactory.newTransformer(coXslContentSource);
	    transformerXml.transform(coXmlContentSource, xmlResult);
	    return out.toString("UTF-8");
	} catch (Exception e) {
	    log.error("Unable to transform XML", e);
	    throw e;
	}
    }

}
