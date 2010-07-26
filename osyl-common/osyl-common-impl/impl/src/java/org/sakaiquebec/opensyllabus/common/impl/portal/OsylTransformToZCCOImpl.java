/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.common.impl.portal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.content.api.GroupAwareEntity.AccessMode;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO;
import org.sakaiquebec.opensyllabus.common.api.portal.publish3.ZCPublisherService;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylTransformToZCCOImpl implements OsylTransformToZCCO {

    private static Log log = LogFactory.getLog(OsylTransformToZCCOImpl.class);

    /** The site service to be injected by Spring */
    private SiteService siteService;

    /**
     * Sets the <code>SiteService</code>.
     * 
     * @param siteService
     */
    public void setSiteService(SiteService siteService) {
	this.siteService = siteService;
    }

    private ZCPublisherService zcPublisherService;

    public void setZcPublierService(ZCPublisherService zcPublierService) {
	this.zcPublisherService = zcPublierService;
    }

    /**
     *Course management service integration.
     */
    private CourseManagementService cmService;

    /**
     * @param cmService
     */
    public void setCmService(CourseManagementService cmService) {
	this.cmService = cmService;
    }

    /**
     * The chs to be injected by Spring
     */
    private ContentHostingService contentHostingService;

    /**
     * Sets the <code>ContentHostingService</code>.
     */
    public void setContentHostingService(
	    ContentHostingService contentHostingService) {
	this.contentHostingService = contentHostingService;
    }

    private String driverName = null;

    private String url = null;

    private String user = null;

    private String password = null;

    private Connection conn;

    public void init() {
	log.info("INIT from OsylTransformToZCCOImpl");
    }

    private void connect() {
	driverName =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.driver.name");
	url =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.url");
	user =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.user");
	password =
		ServerConfigurationService
			.getString("hec.zonecours.conn.portail.password");

	if (driverName == null || url == null || user == null
		|| password == null) {
	    log
		    .error("Please configure access to ZoneCours database in the sakai.properties file");
	    return;
	}

	try {
	    Class.forName(driverName);
	    setConn(DriverManager.getConnection(url, user, password));

	} catch (ClassNotFoundException e) {
	    log.error(e.getMessage());
	} catch (SQLException e) {
	    log.error(e.getMessage());
	}
    }

    public Connection getConn() {
	return conn;
    }

    public void setConn(Connection conn) {
	this.conn = conn;
    }

    private boolean containsZCCo(String koId) {
	boolean hasZCCo = false;
	ResultSet rSet = null;

	try {
	    Statement smnt = getConn().createStatement();
	    rSet =
		    smnt
			    .executeQuery("select * from plancoursxml where koid like '"
				    + koId + "'");
	    if (rSet.next())
		hasZCCo = true;
	    rSet.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return hasZCCo;
    }

    private boolean writeXmlInZC(String zcco, String koId, String lang) {
	boolean written = false;
	PreparedStatement ps = null;
	ResultSet rSet = null;

	String requete = null;

	if (containsZCCo(koId)) {
	    requete =
		    "update plancoursxml set xml = ?, dateMAJ = sysdate, lang = ? where koid like ?";
	    try {
		ps = getConn().prepareStatement(requete);
		ps.setString(1, zcco);
		ps.setString(2, lang);
		ps.setString(3, koId);
		rSet = ps.executeQuery();
		written = true;
		rSet.close();
		ps.close();

		log.debug("The XML " + koId + " has been transferred.");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	} else {
	    requete =
		    "insert into plancoursxml (koId,xml,dateMAJ,lang) VALUES(?,?,sysdate,?)";
	    try {
		ps = getConn().prepareStatement(requete);
		ps.setString(1, koId);
		ps.setString(2, zcco);
		ps.setString(3, lang);
		rSet = ps.executeQuery();
		written = true;
		rSet.close();

		log.debug("The XML " + koId + " has been transferred.");

	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return written;

    }

    private String transform(String osylCo) {
	String zcco = null;

	String xsltFileDirectory =
		ServerConfigurationService
			.getString("osyl.to.zc.file.directory");
	String xsltFileName =
		ServerConfigurationService.getString("osyl.to.zc.file.name");

	if (xsltFileName == null || xsltFileName.length() < 4
		|| !xsltFileName.endsWith("xsl") || xsltFileDirectory == null) {
	    log
		    .error("The xsl file asociated to this transformation is not valid."
			    + " Please make sure the file is specified in the "
			    + "sakai.properties or the name is complete.");
	    return null;
	}

	String xslt =
		FileHelper.getFileContent(xsltFileDirectory + File.separator
			+ xsltFileName);
	try {
	    String osylCoISO88591="";
		try {
		    osylCoISO88591 = new String ( osylCo.getBytes("UTF-8"), "ISO-8859-1" );
		} catch (UnsupportedEncodingException e1) {
		    e1.printStackTrace();
		}
	    zcco =
		    XmlHelper.applyXsl(osylCoISO88591, xslt, new MyResolver(
			    xsltFileDirectory));
	} catch (Exception e) {
	    log.error("Unable to transform XML", e);
	}
	return zcco;
    }

    /*
     * We retrieve the documents in the folder publish that have the security
     * level public
     */
    private boolean writeDocumentsInZC(String siteId, String lang,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibityMap) {
	// TODO: ajouter osylPrinVersion.pdf aux documents transferes

	boolean written = false;
	String acces = null;
	String visibilite = null;
	String ressType = null;
	byte[] ressContent = null;
	int ressSize = 0;

	Set<String> docSecKeyValues = documentSecurityMap.keySet();
	String docVisKey = null;

	for (String docSecKey : docSecKeyValues) {
	    acces = documentSecurityMap.get(docSecKey);

	    docVisKey =
		    docSecKey
			    .replaceFirst(Pattern.quote(WORK_DIR), PUBLISH_DIR);
	    visibilite = documentVisibityMap.get(docVisKey);

	    if ("public".equals(acces) && "true".equals(visibilite)) {
		try {
		    ContentResource content =
			    contentHostingService.getResource(docVisKey);
		    // TODO: verifier les types des documents sont compatibles
		    // dans
		    // ZoneCours
		    ressType = content.getContentType();

		    ressContent = content.getContent();

		    ressSize = content.getContentLength() / 1024;

		    writeDocInZcDb(docVisKey, lang, acces, ressType, ressSize,
			    content.streamContent(), ressContent, siteId);
		} catch (PermissionException e) {
		    e.printStackTrace();
		} catch (IdUnusedException e) {
		    e.printStackTrace();
		} catch (TypeException e) {
		    e.printStackTrace();
		} catch (ServerOverloadException e) {
		    e.printStackTrace();
		}
		written = true;
	    }

	}

	return written;
    }

    private void writeDocInZcDb(String koId, String lang, String acces,
	    String ressType, int ressSize, InputStream ressContent,
	    byte[] content, String siteId) {
	String requete = null;
	PreparedStatement ps = null;
	ResultSet rSet = null;
	boolean exist = false;
	requete = "select * from doczone where koId like ?";
	try {
	    ps = getConn().prepareStatement(requete);
	    ps.setString(1, koId);
	    rSet = ps.executeQuery();

	    if (rSet.next())
		exist = true;

	    ps.close();
	    rSet.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}

	// Check if the record is already on the table
	if (!exist) {
	    requete =
		    "INSERT INTO DocZone (koId, lang, nivSecu, ressType, docContent, dateMAJ, filesizeko) VALUES(?,?,?,?,?,sysdate,?)";
	    try {
		ps = getConn().prepareStatement(requete);
		ps.setString(1, koId);
		ps.setString(2, lang);
		ps.setString(3, "0");
		ps.setBinaryStream(5, ressContent, ressSize);
		ps.setString(4, ressType);
		ps.setInt(6, ressSize);
		rSet = ps.executeQuery();
		rSet.close();
		ps.close();
		log.debug("The resource " + koId + " has been transferred.");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	    // Add the information to the relational table
	    // Link the document to the xml
	    String xmlKoId = getKoId(siteId);

	    requete =
		    "insert into docsecu (koId, nivSecu, planId) values (?, ?, ?)";
	    try {
		ps = getConn().prepareStatement(requete);
		ps.setString(1, koId);
		ps.setInt(2, 0);
		ps.setString(3, xmlKoId);
		rSet = ps.executeQuery();

		ps.close();
		rSet.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	} else {

	    // Add the document content in the record
	    requete =
		    "update DocZone set docContent = ?, datemaj= sysdate  WHERE koId=? ";
	    try {
		ps = getConn().prepareStatement(requete);
		ps.setBinaryStream(1, ressContent, ressSize);
		ps.setString(2, koId);
		rSet = ps.executeQuery();

		ps.close();
		rSet.close();

		log.debug("The resource " + koId + " has been transferred.");
	    } catch (SQLException e) {
		e.printStackTrace();
	    }

	}
    }

    /** {@inheritDoc} */
    public boolean sendXmlAndDoc(COSerialized published,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibilityMap) {

	try {
	    AuthzGroup realm =
		    AuthzGroupService.getAuthzGroup(SITE_PREFIX + published.getSiteId());
	    String provider = realm.getProviderGroupId();
	    if (provider == null || !cmService.isSectionDefined(provider)) {
		log
			.info("The course outline "
				+ published.getSiteId()
				+ " is not associated to a section in the course management,"
				+ " it will not be transferred to ZoneCours public.");
		return false;
	    }
	} catch (GroupNotDefinedException e) {
	    e.printStackTrace();
	}

	String siteId = published.getSiteId();
	String osylCoXml = published.getContent();
	boolean sent = false;
	String zcco = null;
	// TODO: fill out these values from the course management
	String koId = null;
	String lang = null;

	// We check the properties in sakai.properties first than connect to the
	// database
	connect();

	// We transform the course outline
	zcco = transform(osylCoXml);

	if (zcco != null) {
	    // We save the course outline in the zonecours database
	    // The siteId is used as koID
	    koId = getKoId(siteId);
	    lang = published.getLang().substring(0, 2);
	    sent = writeXmlInZC(zcco, koId, lang);
	    //
	    // // We save the documents in the zonecours database
	    sent =
		    sent
			    && writeDocumentsInZC(siteId, lang,
				    documentSecurityMap, documentVisibilityMap);
	    if (sent)
		log
			.debug("The transfer to the ZoneCours database is complete and successful");
	    zcPublisherService.publier(koId, lang);
	}
	return sent;
    }

    private String getKoId(String siteId) {
	String koId = null;

	String provider = null;

	AuthzGroup realm = null;

	try {
	    realm = AuthzGroupService.getAuthzGroup(SITE_PREFIX + siteId);
	} catch (GroupNotDefinedException e) {
	    e.printStackTrace();
	}

	if (realm != null)
	    provider = realm.getProviderGroupId();

	if (provider == null)
	    koId = siteId;
	else if (cmService.isCanonicalCourseDefined(provider))
	    koId = ANNUAIRE_KOID_PREFIX + siteId;
	else if (cmService.isSectionDefined(provider)) {
	    Section section = cmService.getSection(provider);
	    CourseOffering courseOff =
		    cmService.getCourseOffering(section.getCourseOfferingEid());
	    AcademicSession session = courseOff.getAcademicSession();
	    koId = getKoIdPrefix(session) + "-" + siteId;

	}

	return koId;

    }

    private String getKoIdPrefix(AcademicSession session) {
	String sessionName = null;
	String sessionId = session.getEid();
	Date startDate = session.getStartDate();
	String year = startDate.toString().substring(0, 4);
	String period = sessionId.substring(4, sessionId.length());

	if ((sessionId.charAt(3)) == '1')
	    sessionName = WINTER + year;
	if ((sessionId.charAt(3)) == '2')
	    sessionName = SUMMER + year;
	if ((sessionId.charAt(3)) == '3')
	    sessionName = FALL + year;

	return sessionName + "-" + period;
    }

    class MyResolver implements URIResolver {
	String fullPath;

	public MyResolver(String path) {
	    this.fullPath = path;
	}

	public Source resolve(String href, String base) {
	    StringBuffer path = new StringBuffer(this.fullPath);
	    path.append(File.separator);
	    path.append(href);
	    File file = new File(path.toString());
	    if (file.exists())
		return new StreamSource(file);
	    return null;
	}
    }

}
