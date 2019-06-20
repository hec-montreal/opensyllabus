package org.sakaiquebec.opensyllabus.common.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.MimeConstants;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
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
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdLengthException;
import org.sakaiproject.exception.IdUniquenessException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylContentService;
import org.sakaiquebec.opensyllabus.common.api.OsylEventService;
import org.sakaiquebec.opensyllabus.common.api.OsylPublishService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
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
import org.sakaiquebec.opensyllabus.shared.exception.OsylPermissionException;
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

    // SPRING INJECTIONS
    protected OsylSecurityService osylSecurityService;

    // List of previously published nonces
    private List<String> previouslyPublishedNonces;

    public void setOsylSecurityService(OsylSecurityService securityService) {
	this.osylSecurityService = securityService;
    }

    protected AnnouncementService announcementService;

    public void setAnnouncementService(AnnouncementService announcementService) {
	this.announcementService = announcementService;
    }

    protected SessionManager sessionManager;

    public void setSessionManager(SessionManager sessionManager) {
	this.sessionManager = sessionManager;
    }

    protected SecurityService securityService;

    public void setSecurityService(SecurityService securityService) {
	this.securityService = securityService;
    }

    protected ContentHostingService contentHostingService;

    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    protected ResourceDao resourceDao;

    public void setResourceDao(ResourceDao resourceDao) {
	this.resourceDao = resourceDao;
    }

    protected OsylConfigService osylConfigService;

    public void setConfigService(OsylConfigService configService) {
	this.osylConfigService = configService;
    }

    protected OsylSiteService osylSiteService;

    public void setOsylSiteService(OsylSiteService osylSiteService) {
	this.osylSiteService = osylSiteService;
    }

    protected CORelationDao coRelationDao;

    public void setCoRelationDao(CORelationDao relationDao) {
	this.coRelationDao = relationDao;
    }

    protected AuthzGroupService authzGroupService;

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    protected IdManager idManager;

    public void setIdManager(IdManager idManager) {
	this.idManager = idManager;
    }

    protected EventTrackingService eventTrackingService;

    public void setEventTrackingService(
	    EventTrackingService eventTrackingService) {
	this.eventTrackingService = eventTrackingService;
    }

    protected CourseManagementService cmService;

    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    // END SPRING INJECTION

    /**
     * Init method called at initialization of the bean.
     */
    public void init() {
	log.info("INIT OsylPublishService");
        previouslyPublishedNonces = new Vector<String>();
    }

    /**
     * {@inheritDoc}
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String siteId, String accessType, String webappDir) {
	long start = System.currentTimeMillis();
	COSerialized thisCo = null;
	try {
	    thisCo =
		    resourceDao
			    .getPublishedSerializedCourseOutlineBySiteIdAndAccess(
				    siteId, accessType);
	} catch (Exception e) {
	}
	if (thisCo != null) {
	    try {
		thisCo = osylConfigService.fillCo(webappDir, thisCo);
	    } catch (Exception e) {
		log.error("Unable to fill course outline", e);
	    }
	}
	try {
	    osylSiteService.getSiteInfo(thisCo, siteId);
	} catch (Exception e) {
	    log.error("Unable to get site info", e);
	}

    // convert citation library urls
	COModeledServer model = new COModeledServer(thisCo);
	model.XML2Model();
	model.convertCitationLibraryUrls();
	model.model2XML();	
	thisCo.setContent(model.getSerializedContent());

	log.debug("getSerializedPublishedCourseOutlineForAccessType"
		+ elapsed(start) + siteId);
	return thisCo;
    }

    /**
     * Saves the specified nonce as corresponding to a launched publish request.
     *
     * @param nonce
     */
    protected void setAlreadyPublished(String nonce) {
        previouslyPublishedNonces.add(nonce);
    }

    /**
     * Returns true if the publish request corresponding to this nonce has been
     * launched (even if it is not yet completed or if it failed).
     *
     * @param nonce
     */
    protected boolean isAlreadyPublished(String nonce) {
        return previouslyPublishedNonces.contains(nonce);
    }

    /**
     * Creates or updates the corresponding XMLs in the database and copies
     * the resources from the edition area to the published area.
     *
     * @param webappDir webapp path (required to get access to config)
     * @param siteId
     * @param nonce single use identifier to avoid cloned publish request
     */
    public Vector<Map<String, String>> publish(String webappDir, String siteId, String nonce)
	    throws Exception, FusionException, OsylPermissionException {
	if (!osylSecurityService.isActionAllowedInSite(
		osylSiteService.getSiteReference(siteId),
		SecurityInterface.OSYL_FUNCTION_PUBLISH)) {
	    throw new OsylPermissionException(sessionManager
		    .getCurrentSession().getUserEid(),
		    SecurityInterface.OSYL_FUNCTION_PUBLISH);
	} else {
            if (isAlreadyPublished(nonce)) {
                log.error("Publish request for site [" + siteId
                        + "] was already made using the same id!");
                return null;
            } else {
                setAlreadyPublished(nonce);
            }


	    long start = System.currentTimeMillis();
	    log.info("user [" + sessionManager.getCurrentSession().getUserEid()
		    + "] publish site [" + siteId + "]");
	    Vector<String> publishedSiteIds = new Vector<String>();
	    Vector<Map<String, String>> publicationResults =
		    new Vector<Map<String, String>>();

	    SecurityAdvisor advisor = new SecurityAdvisor() {
		public SecurityAdvice isAllowed(String userId, String function,
			String reference) {
		    return SecurityAdvice.ALLOWED;
		}
	    };

	    COSerialized co =
		    osylSiteService
			    .getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
	    try {
		securityService.pushAdvisor(advisor);

		if (co.getContent() == null) {
		    osylSiteService.setCoContentWithTemplate(co, webappDir);
		    resourceDao.createOrUpdateCourseOutline(co);
		}
		COModeledServer coModeled = new COModeledServer(co);

		// PRE-PUBLICATION
		// change document path from edition directory to published
		// directory (attachments)
		coModeled.XML2Model(true);
		coModeled.model2XML();
		co.setContent(coModeled.getSerializedContent());

		COSerialized publishedCO = null;
		try {
		    publishedCO =
			    resourceDao
				    .getPrePublishSerializedCourseOutlineBySiteId(co
					    .getSiteId());
		} catch (Exception e) {
		}

		// Create a course outline with security public
		if (publishedCO == null) {
		    publishedCO = new COSerialized(co);
		    publishedCO.setCoId(idManager.createUuid());
		    publishedCO.setAccess("");
		    publishedCO.setPublished(true);
		    resourceDao.createOrUpdateCourseOutline(publishedCO);

		    List<CORelation> childrens =
			    coRelationDao.getCourseOutlineChildren(siteId);
		    if (childrens != null && !childrens.isEmpty()) {
			// site have children associated (only in CORelation
			// table, not in xml because there was no published xml
			// before). We must associate to parent now
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
				resourceDao
					.createOrUpdateCourseOutline(coChild);
			    } catch (CompatibilityException e) {
				// we do nothing, editor of children will be
				// notified during edition
			    } catch (FusionException fe) {

			    }

			}

		    }
		} else {
		    publishedCO.setContent(co.getContent());
		    resourceDao.createOrUpdateCourseOutline(publishedCO);
		}
		log.info("Creation of prepublish version for site ["
			    +  siteId
			    + "] took " + (System.currentTimeMillis() - start) + " ms");

		// Now we copy documents from the edition area to the published one
		copyResourcesIntoAttachments(siteId, coModeled.getDocumentSecurityMap(),
			coModeled.getDocumentVisibilityMap());

		// And we create different XMLs for various audiences
		publishedSiteIds = publishXMLs(co.getSiteId(), webappDir);

		// We update the edition XML for this site to keep track
		// of current publication date.
		long xmlUpdateStart = System.currentTimeMillis();
		COSerialized coSerialized =
			osylSiteService
				.getUnfusionnedSerializedCourseOutlineBySiteId(siteId);
		COModeledServer coModeledServer =
			new COModeledServer(coSerialized);
		coModeledServer.XML2Model(false);
		COContent coContent = coModeledServer.getModeledContent();
		coContent
			.addProperty(
				COPropertiesType.PREVIOUS_PUBLISHED,
				coContent
					.getProperty(COPropertiesType.PUBLISHED) != null ? coContent
					.getProperty(COPropertiesType.PUBLISHED)
					: "");
		coContent.addProperty(COPropertiesType.PUBLISHED,
			OsylDateUtils.getCurrentDateAsXmlString());
		coModeledServer.model2XML();
		coSerialized.setContent(coModeledServer.getSerializedContent());
		resourceDao.createOrUpdateCourseOutline(coSerialized);

		log.info("Update of edition XML for site ["
			    +  siteId
			    + "] took " + (System.currentTimeMillis() - xmlUpdateStart) + " ms");

		// change publication date
		TreeMap<String, String> publicationProperties =
			new TreeMap<String, String>();
		publicationProperties
			.put(COPropertiesType.PREVIOUS_PUBLISHED,
				coContent
					.getProperty(COPropertiesType.PREVIOUS_PUBLISHED));
		publicationProperties.put(COPropertiesType.PUBLISHED,
			coContent.getProperty(COPropertiesType.PUBLISHED));
		publicationResults.add(publicationProperties);

		// Generate PDF versions
		publicationResults.add(generatePublishedSitesPdf(publishedSiteIds,webappDir));

	    } finally {
		securityService.popAdvisor();
	    }

	    eventTrackingService.post(eventTrackingService.newEvent(
		    OsylEventService.EVENT_OPENSYLLABUS_PUBLICATION,
		    osylSiteService.getSiteReference(siteId), false));

	    log.info("Finished publishing course outline for site ["
		    + (co.getTitle() == null ? siteId : co.getTitle())
		    + "] in " + (System.currentTimeMillis() - start) + " ms");
	    return publicationResults;
	}
    }

    protected Map<String, String> generatePublishedSitesPdf(Vector<String> publishedSiteIds,String webappdir) {
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

    protected COSerialized updateCourseInformations(COSerialized co,
	    String webappDir) {
	String siteName = null;
	String providerId = null;
	String siteId = co.getSiteId();
	String dept = "";
	String program = "";
	String title = "";
	try {
	    AuthzGroup realm =
		    authzGroupService.getAuthzGroup(REALM_ID_PREFIX + siteId);
	    if (realm != null) {
		providerId = realm.getProviderGroupId();
	    }
	    if (providerId != null) {
		siteName = osylSiteService.getSiteName(providerId);
		Section s = cmService.getSection(providerId);
		String courseOffId = s.getCourseOfferingEid();
		CourseOffering courseOff =
			cmService.getCourseOffering(courseOffId);
		program = courseOff.getAcademicCareer(); // HEC ONLY SAKAI-2723
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
			osylConfigService.getConfigByRefAndVersion(co
				.getOsylConfig().getConfigRef(), co
				.getConfigVersion(), webappDir);
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
		log.error("Unable to put properties in XML for site ["
			+ siteId + "]");
		log.error(e);
	    }

	}
	return co;
    }

    public void createPublishPrintVersion(String siteId, String webappdir)
	    throws PdfGenerationException {
	try {
	    long start = System.currentTimeMillis();
	    COSerialized coSerializedAttendee =
		    getSerializedPublishedCourseOutlineForAccessType(siteId,
			    SecurityInterface.ACCESS_ATTENDEE, webappdir);
	    File fAttendee =
		    createPrintVersion(coSerializedAttendee, webappdir);
	    log.info("Attendee PDF version for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");

	    start = System.currentTimeMillis();
	    COSerialized coSerializedCommunity =
		    getSerializedPublishedCourseOutlineForAccessType(siteId,
			    SecurityInterface.ACCESS_COMMUNITY, webappdir);
	    File fCommunity =
		    createPrintVersion(coSerializedCommunity, webappdir);
	    log.info("Community PDF version for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");

	    start = System.currentTimeMillis();
	    COSerialized coSerializedPublic =
		    getSerializedPublishedCourseOutlineForAccessType(siteId,
			    SecurityInterface.ACCESS_PUBLIC, webappdir);
	    File fPublic = createPrintVersion(coSerializedPublic, webappdir);
	    log.info("Public PDF version for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");

	    start = System.currentTimeMillis();
	    if (fAttendee != null && fCommunity != null && fPublic != null) {
		String publishDirectory = "";
		publishDirectory =
			ContentHostingService.ATTACHMENTS_COLLECTION
				+ siteId
				+ "/"
				+ OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
				+ "/";

		createPdfInResource(siteId, publishDirectory, fAttendee,
			SecurityInterface.ACCESS_ATTENDEE);
		createPdfInResource(siteId, publishDirectory, fCommunity,
			SecurityInterface.ACCESS_COMMUNITY);
		createPdfInResource(siteId, publishDirectory, fPublic,
			SecurityInterface.ACCESS_PUBLIC);
	    }
	    log.info("Recording PDF versions for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new PdfGenerationException(e);
	}
    }

    public void createEditionPrintVersion(COSerialized cos, String webappdir)
	    throws Exception {
	updateCourseInformations(cos, webappdir);
	String transformXml =
		transformXmlForGroup(cos.getContent(),
			SecurityInterface.ACCESS_ATTENDEE, webappdir);
	cos.setContent(transformXml);
	File f = createPrintVersion(cos, webappdir);
	String siteId = cos.getSiteId();
	if (f != null) {
	    String workDirectory =
		    contentHostingService.getSiteCollection(siteId);
	    createPdfInResource(siteId, workDirectory, f, "");
	}
    }

    protected void createPdfInResource(String siteId, String directory, File f,
	    String access) {

	SecurityAdvisor advisor = new SecurityAdvisor() {
	    public SecurityAdvice isAllowed(String arg0, String arg1,
		    String arg2) {
		return SecurityAdvice.ALLOWED;
	    }
	};
	try {
	    securityService.pushAdvisor(advisor);
	    Site site = osylSiteService.getSite(siteId);
	    String siteTitle = osylSiteService.getCoSiteTitle(site);

	    // BEGIN HEC ONLY SAKAI-2758
	    StringTokenizer strTok = new StringTokenizer(siteTitle, " ");

	    siteTitle = strTok.nextToken();
	    // END HEC ONLY SAKAI-2758

	    try {
		contentHostingService.getResource(directory + siteTitle
			+ ("".equals(access) ? "" : "_" + access) + ".pdf");
		contentHostingService.removeResource(directory + siteTitle
			+ ("".equals(access) ? "" : "_" + access) + ".pdf");
	    } catch (PermissionException e1) {
		e1.printStackTrace();
	    } catch (IdUnusedException e1) {
	    } catch (TypeException e1) {
		e1.printStackTrace();
	    } catch (InUseException e1) {
		e1.printStackTrace();
	    }
	 finally {
	    securityService.popAdvisor();
	}

	    ContentResourceEdit newResource;
	    try {
		newResource =
			contentHostingService.addResource(directory, siteTitle
				+ ("".equals(access) ? "" : "_" + access),
				".pdf", 1);
		newResource.setContent(new BufferedInputStream(
			new FileInputStream(f)));
		newResource.setContentType(MimeConstants.MIME_PDF);
		contentHostingService.commitResource(newResource,
			NotificationService.NOTI_NONE);
		osylSecurityService.applyPermissions(siteId, newResource.getId(), access);
		f.delete();
	    } catch (PermissionException e) {
		e.printStackTrace();
	    } catch (IdUniquenessException e) {
		e.printStackTrace();
	    } catch (IdLengthException e) {
		e.printStackTrace();
	    } catch (IdInvalidException e) {
		e.printStackTrace();
	    } catch (IdUnusedException e) {
		e.printStackTrace();
	    } catch (OverQuotaException e) {
		e.printStackTrace();
	    } catch (ServerOverloadException e) {
		e.printStackTrace();
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} catch (IdUnusedException e) {
	    log.warn("This is not a valid siteId: " + siteId, e);
	} catch (Exception e1) {
	    e1.printStackTrace();
    }
    }
    protected File createPrintVersion(COSerialized coSerialized,
	    String webappdir) throws Exception {
	try {
	    String xml = coSerialized.getContent();
	    Node d = XmlHelper.parseXml(xml);
	    File f = null;
	    // change keys for i18n messages
	    d =
		    replaceSemanticTagsWithI18NMessage(d,
			    coSerialized.getMessages());

	    // convert html in xhtml
	    d = convertHtmlToXhtml(d);

	    // transform xml -> pdf
	    String configRef = coSerialized.getOsylConfig().getConfigRef();
	    String configVersion = coSerialized.getConfigVersion();
	    String xsltXmltoPdf =
		    FileHelper.getFileContent(webappdir + File.separator
			    + OsylConfigService.CONFIG_DIR + File.separator
			    + configRef + File.separator + configVersion
			    + File.separator
			    + OsylConfigService.PRINT_DIRECTORY
			    + File.separator
			    + OsylConfigService.PRINT_XSLFO_FILENAME);

	    String access;
	    if(coSerialized.getAccess()!=null && !coSerialized.getAccess().equals("")){
		access = coSerialized.getMessages()
		    .get("Print.version."
			    + coSerialized.getAccess());
	    }else{
		access = coSerialized.getMessages()
		    .get("Print.version.edition");
	    }

	    f =
		    FOPHelper.convertXml2Pdf(
			    d,
			    xsltXmltoPdf,
			    webappdir + OsylConfigService.CONFIG_DIR
				    + File.separator + configRef
				    + File.separator + configVersion
				    + File.separator
				    + OsylConfigService.PRINT_DIRECTORY
				    + File.separator,
			    ServerConfigurationService.getServerUrl(),
			    coSerialized.getSiteId(),
			    access);

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
	    t.setMakeBare(true);
	    t.setIndentContent(false);
	    t.setIndentAttributes(false);
	    t.setIndentCdata(false);
	    t.setLiteralAttribs(false);

	    expr =
		    xpath.compile("//text | //comment | //description | //availability | //label");

	    NodeList nodes =
		    (NodeList) expr.evaluate(d, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);
		String textContent = node.getTextContent();
		textContent = textContent.replaceAll("<o:p>", "");
		textContent = textContent.replaceAll("</o:p>", "");
		Pattern pattern = Pattern.compile("<!--.*-->", Pattern.DOTALL);
		Matcher m = pattern.matcher(textContent);
		textContent = m.replaceAll("");
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
				: (node.getAttributes()
					.getNamedItem(
						COPropertiesType.SEMANTIC_TAG_USERDEFLABEL) == null) ? null
					: node.getAttributes()
						.getNamedItem(
							COPropertiesType.SEMANTIC_TAG_USERDEFLABEL)
						.getNodeValue();
		if (userDefLabel == null || userDefLabel.equals("")) {
		    Element e = (Element) node;
		    e.setAttribute(COPropertiesType.SEMANTIC_TAG_USERDEFLABEL,
			    messages.get(node.getTextContent()));
		}
	    }

	    expr = xpath.compile("//" + COPropertiesType.ASM_RESOURCE_TYPE);
	    nodes = (NodeList) expr.evaluate(d, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
		Node node = nodes.item(i);
		Element e = (Element) node;
		e.setTextContent(messages.get("Resource.Type."
			+ node.getTextContent()));
	    }

	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	}
	return d;
    }

    private Vector<String> publishXMLs(String siteId, String webappDir) throws Exception,
	    FusionException {
	long start = System.currentTimeMillis();
	Vector<String> publishedSiteIds=new Vector<String>();
	COSerialized hierarchyMergedCO =
		osylSiteService.getSerializedCourseOutline(siteId, webappDir);
	if (hierarchyMergedCO.isIncompatibleWithHisParent()) {
	    throw new FusionException();
	}
	if (hierarchyMergedCO.isIncompatibleHierarchy()) {
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
        long xmlStart = System.currentTimeMillis();
	if (hierarchyMergedCO != null && coModeled != null) {
	    coModeled.model2XML();
            log.debug("model2XML for [" + siteId + "] took "
                    + (System.currentTimeMillis() - xmlStart) + " ms");
            xmlStart = System.currentTimeMillis();

	    hierarchyMergedCO.setContent(coModeled.getSerializedContent());
	    updateCourseInformations(hierarchyMergedCO, webappDir);
            log.debug("updateCourseInformations for [" +  siteId + "] took "
                    + (System.currentTimeMillis() - xmlStart) + " ms");
            xmlStart = System.currentTimeMillis();

	    // Create a course outline with security attendee
	    filterAndPublishXML(hierarchyMergedCO, SecurityInterface.ACCESS_ATTENDEE,
		    webappDir);
            log.debug("filterAndPublishXML (attendee) for [" +  siteId + "] took "
                    + (System.currentTimeMillis() - xmlStart) + " ms");
            xmlStart = System.currentTimeMillis();

	    // Create a course outline with security public
	    filterAndPublishXML(hierarchyMergedCO, SecurityInterface.ACCESS_PUBLIC,
		    webappDir);
            log.debug("filterAndPublishXML (public) for [" +  siteId + "] took "
                    + (System.currentTimeMillis() - xmlStart) + " ms");
            xmlStart = System.currentTimeMillis();

	    // Create a course outline with security community
	    filterAndPublishXML(hierarchyMergedCO, SecurityInterface.ACCESS_COMMUNITY,
                    webappDir);
            log.debug("filterAndPublishXML (community) for [" +  siteId + "] took "
                    + (System.currentTimeMillis() - xmlStart) + " ms");

	    // If the publication worked, the site id is saved to generate the
	    // pdf later.
	    publishedSiteIds.add(siteId);
	    log.info("Creation of version public, community and attendee for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");
	    publishedSiteIds.addAll(publishChildren(siteId, webappDir));
	}
	return publishedSiteIds;
    }

    private Vector<String> publishChildren(String siteId, String webappDir) {
	List<CORelation> coRelationList=new ArrayList<CORelation>();
	Vector<String> publishedSiteids= new Vector<String>();
	try {
	    coRelationList = coRelationDao.getCourseOutlineChildren(siteId);
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
	for (Iterator<CORelation> coRelationIter = coRelationList.iterator(); coRelationIter
		.hasNext();) {
	    String childId = coRelationIter.next().getChild();
	    try {
                long start = System.currentTimeMillis();
		log.info("user ["
			+ sessionManager.getCurrentSession().getUserEid()
			+ "] publish child site [" + childId + "]");
		publishedSiteids.addAll(publishXMLs(childId, webappDir));
                log.info("Finished publishing course outline for child site ["
                        + childId + "] in " + (System.currentTimeMillis() - start) + " ms");
	    } catch (Exception e) {
                log.error("Exception while publishing child site [" + childId + "] :", e);
		e.printStackTrace();
	    }
	}
	return publishedSiteids;
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
    private void copyResourcesIntoAttachments(String siteId,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibilityMap) throws Exception {
	long start= System.currentTimeMillis();
	String val2 = contentHostingService.getSiteCollection(siteId);
	String refString =
		contentHostingService.getReference(val2).substring(8);
	String id_work;
	try {
	    id_work = (refString);

	    ContentCollection workContent =
		    contentHostingService.getCollection(id_work);

	    deletePublishedContent(siteId);

	    copyWorkToPublish(refString, workContent, documentSecurityMap,
		    documentVisibilityMap, siteId);

	} catch (Exception e) {
	    log.error(
		    "Unable to copy the work folder content to publish folder",
		    e);
	    throw e;
	}
	log.info("Copy of documents from resources to attachments for site ["
		    +  siteId
		    + "] took " + (System.currentTimeMillis() - start) + " ms");
    }

    private void deletePublishedContent(String siteId) throws Exception {
	String id_publish = null;
	// We remove all resources in the publish directory collection

	Site site = osylSiteService.getSite(siteId);
	id_publish =
		ContentHostingService.ATTACHMENTS_COLLECTION + site.getTitle()
			+ "/"
			+ OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
			+ "/";

	ContentCollection publishContent = null;
	try {
	    publishContent = contentHostingService.getCollection(id_publish);
	} catch (Exception e) {
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

		    Site site = osylSiteService.getSite(siteId);
		    this_publish_directory =
			    ContentHostingService.ATTACHMENTS_COLLECTION
				    + site.getId()
				    + "/"
				    + OsylContentService.OPENSYLLABUS_ATTACHEMENT_PREFIX
				    + "/"
				    + this_work_id.substring(this_work_id
					    .lastIndexOf(siteRef)
					    + siteRef.length());

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

    private void filterAndPublishXML(COSerialized co, String access, String webappDir)
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
	String content =
		transformXmlForGroup(co.getContent(), access, webappDir);
	if (publishedCO == null) {
	    publishedCO = new COSerialized(co);
	    publishedCO.setCoId(idManager.createUuid());
	    publishedCO.setAccess(access);
	    publishedCO.setPublished(true);
	}
	publishedCO.setContent(content);
	resourceDao.createOrUpdateCourseOutline(publishedCO);

	// We save the published date in the course outline in edition
	co.setPublicationDate(new java.util.Date(System.currentTimeMillis()));
	resourceDao.setPublicationDate(co.getCoId(), co.getPublicationDate());


	// BEGIN HEC ONLY SAKAI-2723
	// FIXME: this is for HEC Montreal only. Should be injected or something
	// cleaner than this. See SAKAI-2163.
	// see ZCII-400 (02-13-2013)
	if (access.equalsIgnoreCase(SecurityInterface.ACCESS_ATTENDEE)) {

	    String mobileAppFeedURL =
		    ServerConfigurationService.getString("hec.mobile.feed.url");
	    
	    // if an URL for the mobile app backend is configured
	    if (mobileAppFeedURL != null 
		    && !mobileAppFeedURL.trim().equals("")) {
		
		sendAttendeeXMLToMobileAppBackendWithinThread(mobileAppFeedURL, publishedCO);
	    }
	}
	
	// END HEC ONLY SAKAI-2723
    }
    
    private void sendAttendeeXMLToMobileAppBackendWithinThread(String mobileAppFeedURL, COSerialized publishedCO){
	
	final COSerialized attendeePublishedCO = publishedCO;
	final String mobileAppBackendUrl = mobileAppFeedURL;
	
	new Thread(new Runnable() {
	    
	    public void run() {
		sendAttendeeXMLToMobileAppBackend(mobileAppBackendUrl, attendeePublishedCO);
	    }
	}).start();
	
    }
    
    private void sendAttendeeXMLToMobileAppBackend(String mobileAppFeedURL, COSerialized publishedCO){
	
	String postData = publishedCO.getContent();
	String charset = "UTF-8";

	
	HttpURLConnection connection = null;
	try {
	    // Create connection
	    URL url = new URL(mobileAppFeedURL);
	    connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type",
		    "text/xml; charset=\"utf-8\"");

	    connection.setRequestProperty("Content-Length",
		     String.valueOf(postData.getBytes(charset).length));

	    connection.setUseCaches(false);
	    connection.setDoOutput(true);
	    connection.setDoInput(true);

	    // Send request
	    OutputStreamWriter wr =
		    new OutputStreamWriter(connection.getOutputStream(), charset);
	    
	    wr.write(postData);
	    wr.flush();
	    wr.close();
	    
	    int httpStatus = connection.getResponseCode();
	    
	    log.info("POST request to Mobile App backend for course:"+publishedCO.getTitle()+" http status code:"+httpStatus);

	} catch (Exception e) {
	    log.error("Error sending POST request to Mobile App backend ", e);

	} finally {

	    if (connection != null) {
		connection.disconnect();
	    }
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

    public void unpublish(String siteId, String webappDir) throws Exception,
	    PermissionException {
	resourceDao.removePublishVersionsForSiteId(siteId);
	deletePublishedContent(siteId);
	COSerialized co =
		osylSiteService.getSerializedCourseOutline(siteId, webappDir);
	
	// remove publication date in DB
	resourceDao.setPublicationDate(co.getCoId(), null);

	// republish children
	publishChildren(siteId, webappDir);
    }

}
