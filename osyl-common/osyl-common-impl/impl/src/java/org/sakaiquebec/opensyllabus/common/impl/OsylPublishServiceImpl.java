package org.sakaiquebec.opensyllabus.common.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.MimeConstants;
import org.sakaiproject.announcement.api.AnnouncementChannel;
import org.sakaiproject.announcement.api.AnnouncementMessageEdit;
import org.sakaiproject.announcement.api.AnnouncementMessageHeaderEdit;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO;
import org.sakaiquebec.opensyllabus.common.dao.CORelation;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;
import org.sakaiquebec.opensyllabus.common.helper.FOPHelper;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.ModelHelper;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

public class OsylPublishServiceImpl implements OsylPublishService {

    private static Log log = LogFactory.getLog(OsylPublishServiceImpl.class);

    private Vector<String> publishedSiteIds;

    /**
     * The security service to be injected by Spring
     * 
     * @uml.property name="osylSecurityService"
     * @uml.associationEnd
     */
    private OsylSecurityService osylSecurityService;

    private CourseManagementService cmService;

    /**
     * Sets the {@link OsylSecurityService}.
     * 
     * @param securityService
     */
    public void setSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    /** The osyl content service to be injected by Spring */
    private OsylContentService osylContentService;

    /**
     * Sets the {@link OsylContentService}.
     * 
     * @param osylContentService
     */
    public void setOsylContentService(OsylContentService osylContentService) {
	this.osylContentService = osylContentService;
    }

    /** Dependency: AnnouncementService. */
    protected AnnouncementService announcementService = null;

    /**
     * Dependency: AnnouncementService
     * 
     * @param announcementService The AnnouncementService
     */
    public void setAnnouncementService(AnnouncementService announcementService) {
	this.announcementService = announcementService;
    }
    
    private SessionManager sessionManager;

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Maps the visibility of the documents published, hidden or not
     */
    Map<String, String> documentVisibilityMap;

    /**
     * Map the security associated to the document published, access to public
     * users or not
     */
    Map<String, String> documentSecurityMap;

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
     * The transformation and transfer service to be injected by Spring
     * 
     * @uml.property name="osylTransformToZCCO"
     * @uml.associationEnd
     */
    private OsylTransformToZCCO osylTransformToZCCO;

    /**
     * Sets the {@link OsylTransformToZCCO}.
     * 
     * @param osylTransformToZCCO
     */
    public void setOsylTransformToZCCO(OsylTransformToZCCO osylTransformToZCCO) {
	this.osylTransformToZCCO = osylTransformToZCCO;
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
	    String siteId, String accessType, String webappDir) {
	long start = System.currentTimeMillis();
	COSerialized thisCo = null;
	String configRef;
	try {
	    thisCo =
		    resourceDao
			    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
				    siteId, accessType);
	} catch (Exception e) {
	}
	if (thisCo != null) {
	    try {
		osylSiteService.getSiteInfo(thisCo, siteId);
		configRef = thisCo.getOsylConfig().getConfigRef();
	    } catch (Exception e) {
		configRef = osylConfigService.getDefaultConfig();
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
	}
	log.debug("getSerializedPublishedCourseOutlineForAccessType"
		+ elapsed(start) + siteId);
	return thisCo;
    }

    /**
     * Creates or updates the corresponding entries in the database and copies
     * the ressources
     * 
     * @param String webapp dir (absolute pathname !?)
     */
    public Vector<Map<String, String>> publish(String webappDir, String siteId)
	    throws Exception, FusionException {

	long start = System.currentTimeMillis();
	log.info("user ["
			    + sessionManager.getCurrentSession().getUserEid()
			    + "] publish site ["+siteId+"]");
	publishedSiteIds = new Vector<String>();
	Vector<Map<String, String>> publicationResults =
		new Vector<Map<String, String>>();

	SecurityService.pushAdvisor(new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String userId, String function,
		    String reference) {
		return SecurityAdvice.ALLOWED;
	    }
	});

	COSerialized co =
		osylSiteService
			.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	

	if (co.getContent() == null) {
	    osylSiteService.setCoContentWithTemplate(co, webappDir);
	    resourceDao.createOrUpdateCourseOutline(co);
	}
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

	    List<CORelation> childrens =
		    coRelationDao.getCourseOutlineChildren(siteId);
	    if (childrens != null && !childrens.isEmpty()) {
		// site have childrens associated (only in corelation table, not
		// in xml cause there was no published xml before).
		// We must associate to parent now
		for (CORelation coRelation : childrens) {
		    try {
			COSerialized coChild =
				resourceDao
					.getSerializedCourseOutlineBySiteId(coRelation
						.getChild());
			COModeledServer coModelParent =
				osylSiteService
					.getFusionnedPrePublishedHierarchy(siteId);

			ModelHelper.createAssociationInXML(coChild,
				coModelParent);
			resourceDao.createOrUpdateCourseOutline(coChild);
		    } catch (CompatibilityException e) {
			// we do nothing, editor of children will be alert when
			// editing
		    } catch (FusionException fe) {

		    }

		}

	    }
	} else {
	    publishedCO.setContent(co.getContent());
	    resourceDao.createOrUpdateCourseOutline(publishedCO);
	}

	// Retrieve documents associated to the course outline and its parents
	setDocumentSecurityMap(coModeled.getDocumentSecurityMap());

	setDocumentVisibilityMap(coModeled.getDocumentVisibilityMap());

	copyWorkToPublish(siteId, getDocumentSecurityMap(),
		getDocumentVisibilityMap());

	publication(co.getSiteId(), webappDir);

	// change publication date
	TreeMap<String, String> publicationProperties =
		new TreeMap<String, String>();
	COSerialized coSerialized =
		osylSiteService
			.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	COModeledServer coModeledServer = new COModeledServer(coSerialized);
	coModeledServer.XML2Model(false);
	COContent coContent = coModeledServer.getModeledContent();
	coContent.addProperty(COPropertiesType.PREVIOUS_PUBLISHED, coContent
		.getProperty(COPropertiesType.PUBLISHED) != null ? coContent
		.getProperty(COPropertiesType.PUBLISHED) : "");
	coContent.addProperty(COPropertiesType.PUBLISHED, OsylDateUtils
		.getCurrentDateAsXmlString());
	coModeledServer.model2XML();
	coSerialized.setContent(coModeledServer.getSerializedContent());
	resourceDao.createOrUpdateCourseOutline(coSerialized);

	publicationProperties.put(COPropertiesType.PREVIOUS_PUBLISHED,
		coContent.getProperty(COPropertiesType.PREVIOUS_PUBLISHED));
	publicationProperties.put(COPropertiesType.PUBLISHED, coContent
		.getProperty(COPropertiesType.PUBLISHED));
	SecurityService.clearAdvisors();

	log.info("Finished publishing course outline for site ["
		+ (co.getTitle() == null ? siteId : co.getTitle()) + "] in "
		+ (System.currentTimeMillis() - start) + " ms");

	publicationResults.add(publicationProperties);

	publicationResults.add(generatePublishedSitesPdf(webappDir));
	return publicationResults;
    }

    private Map<String, String> generatePublishedSitesPdf(String webappdir) {
	Map<String, String> pdfGenerationResults =
		new HashMap<String, String>();

	for (int i = 0; i < publishedSiteIds.size(); i++) {
	    try {
		createPublishPrintVersion(publishedSiteIds.get(i), webappdir);
		pdfGenerationResults.put(publishedSiteIds.get(i),
			"publish.pdfGeneration.ok");
	    } catch (PdfGenerationException e) {
		pdfGenerationResults.put(publishedSiteIds.get(i),
			"publish.pdfGeneration.nok");
	    }
	}
	return pdfGenerationResults;
    }

    private COSerialized updateCourseInformations(COSerialized co,
	    String webappDir) {
	String siteName = null;
	String providerId = null;
	String siteId = co.getSiteId();
	String dept = "";
	String program = "";
	String title = "";
	try {
	    AuthzGroup realm =
		    AuthzGroupService.getAuthzGroup(REALM_ID_PREFIX + siteId);
	    if (realm != null) {
		providerId = realm.getProviderGroupId();
	    }
	    if (providerId != null) {
		siteName = osylSiteService.getSiteName(providerId);
		Section s = cmService.getSection(providerId);
		String courseOffId = s.getCourseOfferingEid();
		CourseOffering courseOff =
			cmService.getCourseOffering(courseOffId);
		program = courseOff.getAcademicCareer();
		dept = cmService.getSectionCategoryDescription(s.getCategory());
		title = s.getTitle();
		EnrollmentSet es = s.getEnrollmentSet();
		if (es != null) {
		    Set<String> t = es.getOfficialInstructors();
		    if (t != null && !t.isEmpty()) {

		    }
		}
	    }

	} catch (GroupNotDefinedException e) {
	    log.error(e);
	}

	if (siteName != null) {
	    try {
		COConfigSerialized cocs =
			osylConfigService.getConfigByRef(co.getOsylConfig()
				.getConfigRef(), webappDir);
		Document d = XmlHelper.parseXml(cocs.getRulesConfig());
		String propertyType =
			COModeledServer.getRulesConfigPropertyType(d);
		COModeledServer coModeledServer = new COModeledServer(co);
		coModeledServer.XML2Model(false);
		COContent coContent = coModeledServer.getModeledContent();
		coContent.addProperty(COPropertiesType.COURSE_ID, propertyType,
			siteName);

		coContent.addProperty(COPropertiesType.CREATOR, "");// TODO
		coContent.addProperty(COPropertiesType.DEPARTMENT,
			propertyType, dept);
		if (program != null)
		    coContent.addProperty(COPropertiesType.PROGRAM,
			    propertyType, program);
		coContent.addProperty(COPropertiesType.TITLE, propertyType,
			title);
		coModeledServer.model2XML();
		co.setContent(coModeledServer.getSerializedContent());

	    } catch (Exception e) {
		log.error("Unable to put courseId in XML"
			+ co.getOsylConfig().getConfigRef());
		log.error(e);
	    }

	}
	return co;
    }

    //TODO change visibility to private after 2.6.1->2.7.1 migration
    public void createPublishPrintVersion(String siteId, String webappdir)
	    throws PdfGenerationException {
	try {
	    COSerialized coSerializedAttendee =
		    getSerializedPublishedCourseOutlineForAccessType(siteId,
			    SecurityInterface.ACCESS_ATTENDEE, webappdir);
	    File f = createPrintVersion(coSerializedAttendee, webappdir);
	    if (f != null) {
		String publishDirectory = "";
		if (OsylContentService.USE_ATTACHMENTS.equals("true")) {
		    Site site = osylSiteService.getSite(siteId);
		    publishDirectory =
			    ContentHostingService.ATTACHMENTS_COLLECTION
				    + site.getTitle() + "/" + OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
				    + "/";
		} else {
		    publishDirectory =
			    contentHostingService.getSiteCollection(siteId)
				    + PUBLISH_DIRECTORY + "/";
		}
		createPdfInResource(siteId, publishDirectory, f);

	    }
	} catch (Exception e) {
	    throw new PdfGenerationException(e);
	}
    }

    public void createEditionPrintVersion(COSerialized cos, String webappdir)
	    throws Exception {
	updateCourseInformations(cos, webappdir);
	File f = createPrintVersion(cos, webappdir);
	String siteId = cos.getSiteId();
	if (f != null) {
	    String workDirectory =
		    contentHostingService.getSiteCollection(siteId);
	    if (!OsylContentService.USE_ATTACHMENTS.equals("true")) {
		workDirectory += WORK_DIRECTORY + "/";
	    }
	    createPdfInResource(siteId, workDirectory, f);

	}
    }

    private void createPdfInResource(String siteId, String directory, File f) {
	try {
	    contentHostingService.getResource(directory + siteId + ".pdf");
	    contentHostingService.removeResource(directory + siteId + ".pdf");
	} catch (IdUnusedException idue) {
	    // pdf does not exist, nothing to do
	} catch (Exception e) {
	    log.warn("Unable to delete " + siteId + ".pdf", e);
	}
	try {
	    ContentResourceEdit newResource =
		    contentHostingService.addResource(directory, siteId,
			    ".pdf", 1);
	    newResource.setContent(new BufferedInputStream(new FileInputStream(
		    f)));
	    newResource.setContentType(MimeConstants.MIME_PDF);
	    contentHostingService.commitResource(newResource,
		    NotificationService.NOTI_NONE);
	    f.delete();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private File createPrintVersion(COSerialized coSerialized, String webappdir)
	    throws Exception {
	try {
	    String xml = coSerialized.getContent();
	    Node d = XmlHelper.parseXml(xml);
	    File f = null;
	    // change keys for i18n messages
	    d =
		    replaceSemanticTagsWithI18NMessage(d, coSerialized
			    .getMessages());

	    // convert html in xhtml
	    d = convertHtmlToXhtml(d);

	    // transform xml -> pdf
	    String configRef = coSerialized.getOsylConfig().getConfigRef();
	    String xsltXmltoPdf =
		    FileHelper.getFileContent(webappdir + File.separator
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + configRef + File.separator
			    + OsylConfigService.PRINT_DIRECTORY
			    + File.separator
			    + OsylConfigService.PRINT_XSLFO_FILENAME);
	    f =
		    FOPHelper.convertXml2Pdf(d, xsltXmltoPdf, webappdir
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + configRef + File.separator
			    + OsylConfigService.PRINT_DIRECTORY
			    + File.separator);

	    return f;
	} catch (Exception e) {
	    throw e;
	}
    }

    public static Node convertHtmlToXhtml(Node d) throws Exception {
	try {
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr;
	    Tidy t = new Tidy();
	    t.setPrintBodyOnly(true);
	    t.setXHTML(true);
	    t.setQuiet(true);
	    t.setEscapeCdata(true);
	    t.setInputEncoding("UTF-8");
	    t.setOutputEncoding("UTF-8");
	    t.setBreakBeforeBR(false);
	    t.setShowWarnings(false);
	    t.setShowErrors(1);
	    t.setMakeClean(true);

	    expr =
		    xpath
			    .compile("//text | //comment | //description | //availability | //label | //identifier");

	    NodeList nodes =
		    (NodeList) expr.evaluate(d, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);
		String textContent = node.getTextContent();
		textContent = textContent.replaceAll("<o:p>", "");
		textContent = textContent.replaceAll("</o:p>", "");
		StringReader reader = new StringReader(textContent);
		StringWriter writer = new StringWriter();
		t.parseDOM(reader, writer);
		String s = writer.toString();
		node.setTextContent(s);
	    }
	    return d;
	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    public static Node replaceSemanticTagsWithI18NMessage(Node d,
	    Map<String, String> messages) {
	try {
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr;

	    expr = xpath.compile("//semanticTag");

	    NodeList nodes =
		    (NodeList) expr.evaluate(d, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);
		String userDefLabel =
			(node.getAttributes() == null) ? null
				: (node
					.getAttributes()
					.getNamedItem(
						COPropertiesType.SEMANTIC_TAG_USERDEFLABEL) == null) ? null
					: node
						.getAttributes()
						.getNamedItem(
							COPropertiesType.SEMANTIC_TAG_USERDEFLABEL)
						.getNodeValue();
		if (userDefLabel == null || userDefLabel.equals("")) {
		    Element e = (Element) node;
		    e.setAttribute(COPropertiesType.SEMANTIC_TAG_USERDEFLABEL,
			    messages.get(node.getTextContent()));
		}
	    }
	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	}
	return d;
    }

    private void publication(String siteId, String webappDir) throws Exception,
	    FusionException {

	COSerialized hierarchyFussionedCO =
		osylSiteService.getSerializedCourseOutline(siteId, webappDir);// getSerializedCourseOultineBySiteId(siteid)
	if (hierarchyFussionedCO.isIncompatibleWithHisParent()) {
	    throw new FusionException();
	}
	if (hierarchyFussionedCO.isIncompatibleHierarchy()) {
	    FusionException e = new FusionException();
	    e.setHierarchyFusionException(true);
	    throw e;
	}

	COModeledServer coModeled = null;
	try {
	    coModeled =
		    osylSiteService.getFusionnedPrePublishedHierarchy(siteId);
	} catch (FusionException fe) {
	    throw new FusionException();
	} catch (CompatibilityException ce) {
	    FusionException fe = new FusionException();
	    fe.setHierarchyFusionException(true);
	    throw fe;
	} catch (Exception e) {
	    // there is no published version of co for siteid
	}
	if (hierarchyFussionedCO != null && coModeled != null) {
	    coModeled.model2XML();
	    hierarchyFussionedCO.setContent(coModeled.getSerializedContent());

	    updateCourseInformations(hierarchyFussionedCO, webappDir);

	    // Create a course outline with security public
	    publish(hierarchyFussionedCO, SecurityInterface.ACCESS_PUBLIC,
		    webappDir);

	    // Create a course outline with security attendee
	    publish(hierarchyFussionedCO, SecurityInterface.ACCESS_ATTENDEE,
		    webappDir);

	    // If the publication worked, the site id is logged to generate the
	    // pdf later.

	    publishedSiteIds.add(siteId);
	    publishChildren(siteId, webappDir);
	}
    }

    private Map<String, String> getDocumentVisibilityMap() {
	return documentVisibilityMap;
    }

    private void setDocumentVisibilityMap(
	    Map<String, String> documentVisibilityMap) {
	this.documentVisibilityMap = documentVisibilityMap;
    }

    private Map<String, String> getDocumentSecurityMap() {
	return documentSecurityMap;
    }

    private void setDocumentSecurityMap(Map<String, String> documentSecurityMap) {
	this.documentSecurityMap = documentSecurityMap;
    }

    private void publishChildren(String siteId, String webappDir) {
	List<CORelation> coRelationList;
	try {
	    coRelationList = coRelationDao.getCourseOutlineChildren(siteId);
	} catch (Exception e1) {
	    e1.printStackTrace();
	    coRelationList = new ArrayList<CORelation>();
	}
	for (Iterator<CORelation> coRelationIter = coRelationList.iterator(); coRelationIter
		.hasNext();) {
	    String childId = coRelationIter.next().getChild();
	    try {
		log.info("user ["
			    + sessionManager.getCurrentSession().getUserEid()
			    + "] publish child site ["+childId+"]");
		publication(childId, webappDir);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
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
    private void copyWorkToPublish(String siteId,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibilityMap) throws Exception {
	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString =
		contentHostingService.getReference(val2).substring(8);
	String id_work = (refString + WORK_DIRECTORY + "/");
	String id_publish = null;
	try {

	    if (OsylContentService.USE_ATTACHMENTS.equals("true")) {
		id_work = (refString);
	    }
	    ContentCollection workContent =
		    contentHostingService.getCollection(id_work);

	    // We remove all resources in the publish directory collection
	    if (OsylContentService.USE_ATTACHMENTS.equals("true")) {
		Site site = osylSiteService.getSite(siteId);
		id_publish =
			ContentHostingService.ATTACHMENTS_COLLECTION
				+ site.getTitle() + "/" + OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX + "/";
	    } else {
		id_publish = (refString + PUBLISH_DIRECTORY + "/");
	    }

	    ContentCollection publishContent = null;
	    try {
	    	publishContent =
		    contentHostingService.getCollection(id_publish);
	    }catch (Exception e){
	    	publishContent = contentHostingService.addCollection(id_publish);
	    }

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

	    copyWorkToPublish(refString, workContent, documentSecurityMap,
		    documentVisibilityMap, siteId);

	} catch (Exception e) {
	    log.error(
		    "Unable to copy the work folder content to publish folder",
		    e);
	    throw e;
	}
    }

    @SuppressWarnings("unchecked")
    private void copyWorkToPublish(String siteRef, ContentCollection directory,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibilityMap, String siteId)
	    throws Exception {

	List<ContentEntity> members = directory.getMemberResources();
	for (Iterator<ContentEntity> iMbrs = members.iterator(); iMbrs
		.hasNext();) {
	    ContentEntity next = (ContentEntity) iMbrs.next();
	    String thisEntityRef = next.getId();

	    if (next.isCollection()) {
		ContentCollection collection = (ContentCollection) next;
		copyWorkToPublish(siteRef, collection, documentSecurityMap,
			documentVisibilityMap, siteId);
	    } else {
		String permission = documentSecurityMap.get(thisEntityRef);
		String visibility = null;
		String newId = null;
		String this_publish_directory = null;

		if (permission != null) {
		    // doc exists in CO
		    String this_work_id = directory.getId();

		    if (osylContentService.USE_ATTACHMENTS.equals("true")) {
			Site site = osylSiteService.getSite(siteId);
			this_publish_directory =
				contentHostingService.ATTACHMENTS_COLLECTION
					+ site.getTitle() + "/" + OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
					+ "/" + this_work_id.substring(this_work_id.lastIndexOf(siteRef)+siteRef.length());

		    } else {
			this_publish_directory =
				siteRef
					+ PUBLISH_DIRECTORY
					+ this_work_id.substring(this_work_id
						.lastIndexOf(siteRef)
						+ siteRef.length()
						+ WORK_DIRECTORY.length(),
						this_work_id.length());

		    }

		    if (!contentHostingService
			    .isCollection(this_publish_directory)) {
			ContentCollectionEdit publishContentEdit;
			publishContentEdit =
				contentHostingService
					.addCollection(this_publish_directory);

			contentHostingService
				.commitCollection(publishContentEdit);
		    }
		    newId =
			    contentHostingService.copyIntoFolder(thisEntityRef,
				    this_publish_directory);
		    visibility = documentVisibilityMap.get(newId);
		    if (visibility != null && visibility.equals("false")) {
			applyVisibility(newId);
		    }
		    // Permission application
		    osylSecurityService.applyPermissions(newId, permission);
		}
	    }
	}

    }

    private void applyVisibility(String referenceId) {
	try {
	    ResourceProperties properties =
		    contentHostingService.getProperties(referenceId);
	    boolean isCollection = false;

	    try {
		isCollection =
			properties
				.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
	    } catch (Exception e) {
		// assume isCollection is false if property is not set
	    }

	    if (isCollection) {
		ContentCollectionEdit edit =
			contentHostingService.editCollection(referenceId);
		edit.setHidden();
		contentHostingService.commitCollection(edit);
	    }

	    else {
		ContentResourceEdit edit =
			contentHostingService.editResource(referenceId);
		edit.setHidden();
		contentHostingService.commitResource(edit,
			NotificationService.NOTI_NONE);
	    }
	} catch (Exception e) {
	    log.error("Unable to apply visibility", e);
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

	// We save the published date in the course outline in edition
	co.setPublicationDate(new java.util.Date(System.currentTimeMillis()));
	resourceDao.setPublicationDate(co.getCoId(), co.getPublicationDate());

	String portalActivated =
		ServerConfigurationService.getString("hec.portail.activated");

	// FIXME: this is for HEC Montreal only. Should be injected or something
	// cleaner than this. See SAKAI-2163.
	if (portalActivated != null && portalActivated.equalsIgnoreCase("true"))
	    if (access.equalsIgnoreCase(SecurityInterface.ACCESS_PUBLIC)) {
		osylTransformToZCCO.sendXmlAndDoc(publishedCO);

	    }
    }

    public String transformXmlForGroup(String content, String group,
	    String webappDir) throws Exception {
	String xslFileName =
		OsylSiteService.XSL_PREFIX + group
			+ OsylSiteService.XSL_FILE_EXTENSION;
	// Retrieve xml and xsl from the webapps/xslt
	String coXslFilePath =
		webappDir + File.separator + OsylSiteService.XSLT_DIRECTORY
			+ File.separator + xslFileName;
	String xsl = FileHelper.getFileContent(coXslFilePath);

	try {
	    return XmlHelper.applyXsl(content, xsl);
	} catch (Exception e) {
	    log.error("Unable to transform XML", e);
	    throw e;
	}
    }

    // only to improve readability while profiling
    private static String elapsed(long start) {
	return ": elapsed : " + (System.currentTimeMillis() - start) + " ms ";
    }

    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    public CourseManagementService getCmService() {
	return cmService;
    }

    public void notifyOnPublish(String siteId, String subject, String body)
	    throws Exception {
	List<CORelation> cos = null;
	String originalSiteId = siteId;
	// Check if site is related to other sites

	try {
	    cos = coRelationDao.getCourseOutlineChildren(siteId);
	} catch (Exception e) {
	    log.info("The course outline " + siteId + " has no children.");
	}
	// We add the message to the current site
	if (osylSiteService.siteExists(siteId)) {

	    addAnnouncement(siteId, subject, body);
	    log.info("An announcement has been made in the site " + siteId
		    + " concerning the publication of the course outline "
		    + originalSiteId);
	}
	// We add the message to the children site
	for (CORelation relation : cos) {
	    siteId = relation.getChild();
	    if (osylSiteService.siteExists(siteId)) {
		addAnnouncement(siteId, subject, body);
		log.info("An announcement has been made in the site " + siteId
			+ " concerning the publication of the course outline "
			+ originalSiteId);
	    }
	}

    }

    private void addAnnouncement(String siteId, String subject, String body)
	    throws Exception {

	AnnouncementChannel channel = getAnnouncementChannel(siteId);

	if (channel != null) {
	    AnnouncementMessageEdit message = null;
	    message = channel.addAnnouncementMessage();

	    if (message != null) {
		AnnouncementMessageHeaderEdit header =
			message.getAnnouncementHeaderEdit();
		// TODO: add published course outline reference to course
		// outline lookup in announcement tool
		// message.getPropertiesEdit().addProperty("coReference", );
		header.setSubject(subject);
		message.setBody(body);

		header.clearGroupAccess();

		channel.commitMessage(message, NotificationService.NOTI_NONE);

	    }
	} else {
	    throw new Exception("No annoucement channel available");
	}
    }

    private AnnouncementChannel getAnnouncementChannel(String siteId) {
	AnnouncementService aService =
		org.sakaiproject.announcement.cover.AnnouncementService
			.getInstance();
	AnnouncementChannel channel = null;
	String channelId =
		ServerConfigurationService.getString("channel", null);
	if (channelId == null) {
	    channelId =
		    org.sakaiproject.announcement.cover.AnnouncementService
			    .channelReference(siteId,
				    SiteService.MAIN_CONTAINER);
	    try {
		channel = aService.getAnnouncementChannel(channelId);
	    } catch (IdUnusedException e) {
		log
			.warn(this
				+ "getAnnouncement:No announcement channel found");
		channel = null;
	    } catch (PermissionException e) {
		log
			.warn(this
				+ "getAnnouncement:Current user not authorized to deleted annc associated "
				+ "with assignment. " + e.getMessage());
		channel = null;
	    }
	}
	return channel;
    }

}
