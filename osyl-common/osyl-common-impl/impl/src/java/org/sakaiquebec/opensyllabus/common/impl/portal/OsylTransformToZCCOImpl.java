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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.GroupAwareEntity.AccessMode;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.site.api.SiteService;
import org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO;
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
			.getString("hec.oracle.conn.portail.driver.name");
	url =
		ServerConfigurationService
			.getString("hec.oracle,conn.portail.url");
	user =
		ServerConfigurationService
			.getString("hec.oracle.conn.portail.user");
	password =
		ServerConfigurationService
			.getString("hec.oracle.conn.portail.password");

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
	    zcco =
		    XmlHelper.applyXsl(osylCo, xslt, new MyResolver(
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
	String ressId = null;

	// String publishCollId =
	// PUBLISH_COLL_PREFIX + siteId + PUBLISH_COLL_SUFFIX;
	//
	// ContentCollection publishColl = null;
	//
	// try {
	// publishColl = contentHostingService.getCollection(publishCollId);
	// } catch (IdUnusedException e) {
	// e.printStackTrace();
	// } catch (TypeException e) {
	// e.printStackTrace();
	// } catch (PermissionException e) {
	// e.printStackTrace();
	// }
	//
	// List<ContentResource> resources =
	// contentHostingService.getAllResources(publishCollId);

	Set<String> docSecKeyValues = documentSecurityMap.keySet();
	String docVisKey = null;

	for (String docSecKey : docSecKeyValues) {
	    acces = documentSecurityMap.get(docSecKey);

	    System.out.print("la ressource " + docSecKey + " acces " + acces);
	    docVisKey =
		    docSecKey
			    .replaceFirst(Pattern.quote(WORK_DIR), PUBLISH_DIR);
	    visibilite = documentVisibityMap.get(docVisKey);

	    try {
		ContentResource content =
			contentHostingService.getResource(docVisKey);
		//TODO: verifier les types des documents sont compatibles dans ZoneCours
		ressType = content.getContentType();
		
		//Si la ressource n'a pas de uuid le chs lui en cree un
		ressId = contentHostingService.getUuid(docVisKey);
		
		//On prend les 30 premiers caracteres
		ressId = ressId.substring(0, 30);
		
	    } catch (PermissionException e) {
		log.error(e.getMessage());
	    } catch (IdUnusedException e) {
		log.error(e.getMessage());
	    } catch (TypeException e) {
		log.error(e.getMessage());
	    }

	    System.out.println(" la visibilite " + visibilite + " type "
		    + ressType);
	    // TODO: traiter les documents de facon recursive pour prendre en
	    // compte les dossiers
	    // if public document we proceed

	}

	return written;
    }

    /** {@inheritDoc} */
    public boolean sendXmlAndDoc(COSerialized published,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibilityMap) {

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
	    koId = siteId;
	    lang = published.getLang().substring(0, 2);
	    sent = writeXmlInZC(zcco, koId, lang);
	    //
	    // // We save the documents in the zonecours database
	    sent =
		    sent
			    && writeDocumentsInZC(siteId, lang,
				    documentSecurityMap, documentVisibilityMap);
	    System.out.println(sent);
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
