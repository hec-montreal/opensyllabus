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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO;
import org.sakaiquebec.opensyllabus.common.api.portal.publish3.ZCPublisherService;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;
import org.sakaiquebec.opensyllabus.common.impl.portal.javazonecours.Publication;
import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ** OsylTransformToZCCOImpl transforms Opensyllabus's XML to a readable HTML.
 * Helps to publish in HEC Montreal's public portal Works very closely with
 * Publication
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @author <a href="mailto:yvette.lapa-dessap@hec.ca">Yvette Lapa Dessap</a>
 * @version $Id: $
 */
public class OsylTransformToZCCOImpl implements OsylTransformToZCCO {

    private static Log log = LogFactory.getLog(OsylTransformToZCCOImpl.class);

    private String courseNumber;

    private ZCPublisherService zcPublisherService;

    public static final String ACCESS_COMMUNITY = "community";
    public static final String ACCESS_PUBLIC = "public";
    public static final String ACCESS_ATTENDEE = "attendee";
    private static final String SITE_SHAREABLE = "00";

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

    private AuthzGroupService authzGroupService;

    public void setAuthzGroupService(AuthzGroupService authzGroupService) {
	this.authzGroupService = authzGroupService;
    }

    private String driverName = null;

    private String url = null;

    private String user = null;

    private String password = null;

    private Object put;

    private Document xmlSourceDoc;

    public void init() {
	log.info("INIT from OsylTransformToZCCOImpl");
    }

    /**
     * Credentials to Access Public Portal's Database configured in
     * sakai.properties
     */
    private Connection connect() throws Exception {
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
	    String msg =
		    "Please configure access to ZoneCours "
			    + "database in the sakai.properties file";
	    log.error(msg);
	    throw new IllegalStateException(msg);
	}

	try {
	    Class.forName(driverName);
	    return DriverManager.getConnection(url, user, password);

	} catch (Exception e) {
	    log.error("connect(): " + e);
	    throw e;
	}
    }

    /**
     * Verifies if Public Portal's Database Contains the XML of a specific
     * course outline
     * 
     * @param koId The identifier of the course outline
     * @return true if the DB contains it false if not
     */
    private boolean containsZCCo(String koId, Connection dbConn) {
	boolean hasZCCo = false;
	ResultSet rSet = null;

	try {
	    Statement smnt = dbConn.createStatement();
	    rSet =
		    smnt
			    .executeQuery("select * from plancoursxml where koid like '"
				    + koId + "'");
	    if (rSet.next())
		hasZCCo = true;
	    rSet.close();

	} catch (SQLException e) {
	    log.error("containsZCCo(): " + e);
	}

	return hasZCCo;
    }

    /**
     * Writes the XML of a specific course outline in the Public Portal's
     * Database
     * 
     * @param zcco The XML to be written
     * @param koId The identifier of that XML
     * @param lang The course outline language
     * @return true if it is written successfully false if not
     */
    private boolean writeXmlInZC(String zcco, String koId, String lang,
	    Connection dbConn) {
	boolean written = false;
	PreparedStatement ps = null;
	ResultSet rSet = null;

	String requete = null;

	if (containsZCCo(koId, dbConn)) {
	    requete =
		    "update plancoursxml set xml = ?, dateMAJ = sysdate, lang = ? where koid like ?";
	    try {
		ps = dbConn.prepareStatement(requete);
		ps.setString(1, zcco);
		ps.setString(2, lang);
		ps.setString(3, koId);
		rSet = ps.executeQuery();
		written = true;
		rSet.close();
		ps.close();

		log.debug("The XML " + koId + " has been transferred.");
	    } catch (SQLException e) {
		log.error("writeXmlInZC(): " + e);
	    }
	} else {
	    requete =
		    "insert into plancoursxml (koId,xml,dateMAJ,lang) VALUES(?,?,sysdate,?)";
	    try {
		ps = dbConn.prepareStatement(requete);
		ps.setString(1, koId);
		ps.setString(2, zcco);
		ps.setString(3, lang);
		rSet = ps.executeQuery();
		written = true;
		rSet.close();

		log.debug("The XML " + koId + " has been transferred.");

	    } catch (SQLException e) {
		log.error("writeXmlInZC(): " + e);
	    }
	}
	return written;

    }

    /**
     * Transforms OpenSyllabus XML to Zonecours 1 format for publication in
     * public portal
     * 
     * @param osylCo Opensyllabus XML
     * @return the transformed XML in Zonecours 1 version
     */
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
			+ xsltFileName, "ISO-8859-1");
	try {
	    String osylCoISO88591 = "";
	    try {
		osylCo = osylCo.replaceAll("’", "'");
		osylCoISO88591 =
			new String(osylCo.getBytes("UTF-8"), "ISO-8859-1");
	    } catch (UnsupportedEncodingException e1) {
		log.error("transform(): " + e1);
	    }
	    zcco =
		    XmlHelper.applyXsl(osylCoISO88591, xslt, new MyResolver(
			    xsltFileDirectory), "ISO-8859-1", null);
	} catch (Exception e) {
	    log.error("Unable to transform XML" + e);
	}
	return zcco;
    }

    /*
     * Retrieve documents with security level set to 'public' in one sakai's
     * site publish folder and write them in Public portal database
     * @param siteId The identifier of the site where documents are retrieved
     * @param lang The language of the course outline of this site
     * @param documentSecurityMap The Map containing security level of documents
     * @param documentVisibityMap The Map containing visibility level of
     * documents
     * @return true if all documents have been written successfully
     */
    private boolean writeDocumentsInZC(String siteId, String lang, String acces,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibityMap,
	    Map<String, String> documents, String zcco, Connection dbConn)
	    throws Exception {
	// TODO: ajouter osylPrinVersion.pdf aux documents transferes

	log.debug("Writing documents of site " + siteId
		+ "in public portal database...");

	boolean written = false;
	String accesDoc = null;
	String visibilite = null;
	String ressType = null;
	byte[] ressContent = null;
	int ressSize = 0;
	StringBuffer outTrace = null;
	Publication p = new Publication();
	String courseNumber = getCourseNumber();

	Document xmlSourceDoc = null;

	if (null != zcco && !("".equals(zcco))) {
	    xmlSourceDoc = p.buildDOM(zcco);
	}

	Set<String> docSecKeyValues = documentSecurityMap.keySet();
	String doc = null;
	HashMap hache = getDocsIds(dbConn, lang, xmlSourceDoc, outTrace, false);
	//It uses access from each document
	for (String docSecKey : docSecKeyValues) {
	    accesDoc = documentSecurityMap.get(docSecKey);
	    doc = documents.get(docSecKey);
	    if (docSecKey != null && !"".equalsIgnoreCase(docSecKey)) {
		visibilite = documentVisibityMap.get(docSecKey);
		// Exclude the string "/publish" itsself to get the real
		// filename
		//It searches documents inside courses through a course as "community" 
		//because that includes all documents
		if (ACCESS_COMMUNITY.equals(acces)) {				
			if ((accesDoc.equals(ACCESS_PUBLIC) && "true".equals(visibilite))
				|| (accesDoc.equals(ACCESS_COMMUNITY) && "true".equals(visibilite))) {		
			    try {
				ContentResource content =
					contentHostingService.getResource(doc);
				// TODO: verifier les types des documents sont
				// compatibles dans ZoneCours
				if (content != null) {
				    ressType = content.getContentType();
				    ressContent = content.getContent();
				    ressSize = (int) content.getContentLength();
	
				    log.debug("Writing documents of site " + siteId
					    + "in public portal database...");
				    if (docSecKey != null
					    && hache
						    .get(courseNumber + "_" + docSecKey) != null)
					writeDocInZcDb(courseNumber + "_" + docSecKey,
						lang, accesDoc, ressType, ressSize,
						content.streamContent(), ressContent,
						siteId, dbConn);
				}
			    } catch (PermissionException e) {
				log.error("writeDocumentsInZC(): " + e);
			    } catch (IdUnusedException e) {
				log.error("writeDocumentsInZC(): " + e);
			    } catch (TypeException e) {
				log.error("writeDocumentsInZC(): " + e);
			    } catch (ServerOverloadException e) {
				log.error("writeDocumentsInZC(): " + e);
			    }
			    written = true;
			}
	    }
	    }
	}
	log.debug("All public documents of site " + siteId
		+ "have been written in public portal database...");
	return written;
    }

    /**
     * Writes one document in the public portal database
     * 
     * @param koId The identifier of the document
     * @param lang The language of the course outline containing the document
     * @param acces The access security level
     * @param ressType The MIME type of the document
     * @param ressSize The size of the document
     * @param ressContent The document content from sakai/Opensyllabus
     * @param content The document content in bytes
     * @param siteId The identifier of the site containing the document
     * @throws Exception
     */
    private void writeDocInZcDb(String koId, String lang, String acces,
	    String ressType, int ressSize, InputStream ressContent,
	    byte[] content, String siteId, Connection dbConn) throws Exception {
	log.debug("Writing document  " + koId + "in public portal database...");

	boolean exist =
		selectDocInDocZone(koId, lang, acces, ressType, ressSize,
			content, siteId, dbConn);

	String nivSecu = getSecurityLabel(acces);

	// Check if the record is already on the table
	if (!exist) {

	    insertDocInDocZone(dbConn, koId, lang, acces, ressType, ressSize,
		    ressContent, content, siteId);
	    ressContent.close();
	    // Add the information to the relational table
	    // Clean the place to avoid unique constraint violation
	    deleteRessourceSecuriteDB(dbConn, koId, siteId, nivSecu);
	    // By default, security is zero for all documents belonging to
	    // portal
	    insertRessourceSecuriteDB(dbConn, koId, siteId, nivSecu); // it was
	    // "0"

	} else {

	    updateDocZone(dbConn, koId, lang, acces, ressType, ressSize,
		    ressContent, content, siteId);
	    ressContent.close();
	    // Clean the place to avoid unique constraint violation
	    deleteRessourceSecuriteDB(dbConn, koId, siteId, nivSecu);
	    // By default, security is zero for all documents belonging to
	    // portal
	    insertRessourceSecuriteDB(dbConn, koId, siteId, nivSecu); // it was
	    // "0"

	}
	log.debug("Document " + koId + " has been written in the database");
    }

    /**
     * Tells if a document is already in the database
     * 
     * @param koId The document identifier
     * @param lang The language of the course outline containing the document
     * @param acces The access security level
     * @param ressType The MIME type of the document
     * @param ressSize The size of the document
     * @param ressContent The document content from sakai/Opensyllabus
     * @param content The document content in bytes
     * @param siteId The identifier of the site containing the document
     * @return true if the document is in the database false otherwise
     */
    private boolean selectDocInDocZone(String koId, String lang, String acces,
	    String ressType, int ressSize, byte[] content, String siteId,
	    Connection dbConn) {
	boolean isthere = false;
	String requete_select = null;
	PreparedStatement ps_select = null;
	ResultSet rSet_select = null;
	String nivSecu = getSecurityLabel(acces);

	requete_select =
		"select * from doczone where koId like ? "; // AND nivSecu=?
	try {
	    ps_select = dbConn.prepareStatement(requete_select);
	    ps_select.setString(1, koId);
	    //ps_select.setString(2, nivSecu);
	    rSet_select = ps_select.executeQuery();

	    if (rSet_select.next())
		isthere = true;

	    ps_select.close();
	    rSet_select.close();
	} catch (SQLException e) {
	    log.error("selectDocInDocZone(): " + e);
	}
	return isthere;
    }

    /**
     * Inserts a document in the public portal database
     * 
     * @param dbConn The connection to the public portal database
     * @param koId The document identifier
     * @param lang The language of the course outline containing the document
     * @param acces The access security level
     * @param ressType The MIME type of the document
     * @param ressSize The size of the document
     * @param ressContent The document content from sakai/Opensyllabus
     * @param content The document content in bytes
     * @param siteId The identifier of the site containing the document
     * @throws IOException
     * @throws Exception
     */
    private void insertDocInDocZone(Connection dbConn, String koId,
	    String lang, String acces, String ressType, int ressSize,
	    InputStream ressContent, byte[] content, String siteId)
	    throws IOException, Exception {
	log.debug("insertDocInDocZone - debut");

	String requete_ins = null;
	PreparedStatement ps_ins = null;
	ResultSet rSet_ins = null;

	String requeteSQL = null;
	PreparedStatement ps = null;
	ResultSet rset = null;
	BLOB blob;

	String nivSecu = getSecurityLabel(acces);

	try {

	    requete_ins =
		    "INSERT INTO DocZone (koId, lang, nivSecu, ressType, dateMAJ, docContent) VALUES(?,?,?,?,sysdate,empty_blob())";
	    ps_ins = dbConn.prepareStatement(requete_ins);
	    ps_ins.setString(1, koId);
	    ps_ins.setString(2, lang);
	    ps_ins.setString(3, nivSecu);
	    ps_ins.setString(4, ressType);
	    ps_ins.execute();
	    ps_ins.close();

	    requeteSQL =
		    "SELECT docContent FROM DocZone WHERE koId=? FOR UPDATE";
	    ps = dbConn.prepareStatement(requeteSQL);
	    ps.setString(1, koId);
	    //ps.setString(2, nivSecu);

	    rset = ps.executeQuery();

	    if (rset.next()) {
		blob = ((OracleResultSet) rset).getBLOB(1);
		OutputStream blobOutput =
			((oracle.sql.BLOB) blob).getBinaryOutputStream();

		byte[] buffer = new byte[10 * 1024];

		int nread = 0; // Number of bytes read
		while ((nread = ressContent.read(buffer)) != -1) {
		    blobOutput.write(buffer, 0, nread);

		    log.trace(nread);
		}
		ressContent.close();
		blobOutput.close();

	    }

	    log.debug("The resource " + koId + " has been transferred.");

	} catch (SQLException e) {
	    log.error("insertDocInDocZone(): " + e);
	} finally {
	    Statement stmt = dbConn.createStatement();
	    stmt.execute("commit");
	    rset.close();
	    ps.close();
	    stmt.close();

	}
	log.debug("The document " + koId + " NivSec: " + nivSecu
		+ " has been transferred");

    }

    // ---------------------------------------------------
    /**
     * Deletes the security level of a document in the public portal database.
     * 
     * @param connexion The connection to public portal database
     * @param koId The identifier of the document
     * @param planId The identifier of the course outline
     * @throws Exception
     */
    public void deleteRessourceSecuriteDB(Connection connexion, String koId,
	    String planId, String acces) throws Exception {
	// ---------------------------------------------------
	String requeteSQL_del = null;
	Statement stmt_del = connexion.createStatement();

	String xmlKoId = getKoId(planId);
	String nivSecu = getSecurityLabel(acces);

	requeteSQL_del =
		" DELETE FROM DocSecu WHERE koId = '" + koId + "' AND planId='"
			+ xmlKoId + "'";
	log.debug(requeteSQL_del + " ...");
	stmt_del.execute(requeteSQL_del);
	log.debug("request ok: " + requeteSQL_del);

	stmt_del.close();
    }

    // ---------------------------------------------------
    /**
     * Deletes a document in the public portal database.
     * 
     * @param connexion The connection to public portal database
     * @param koId The identifier of the document
     * @throws Exception
     */
    public void deleteRessourceFromDB(Connection connexion, String koId,
	    String acces) throws Exception {
	// ---------------------------------------------------
	String requeteSQL_delRess = null;
	Statement stmt_delRess = connexion.createStatement();
	String nivSecu = getSecurityLabel(acces);
	requeteSQL_delRess = " DELETE FROM DocZone WHERE koId = '" + koId + "'";		
	//" DELETE FROM DocZone WHERE koId = '" + koId + "' AND nivSecu = '" + nivSecu + "'";
	log.debug(requeteSQL_delRess + " ...");
	stmt_delRess.execute(requeteSQL_delRess);
	log.debug("request ok: " + requeteSQL_delRess);

	stmt_delRess.close();
    }

    // ---------------------------------------------------
    /**
     * Inserts the security level of a document in the public portal database.
     * 
     * @param connexion The connection to public portal database
     * @param koId The identifier of the document
     * @param planId The identifier of the document
     * @param nivSecu The security level to be inserted
     * @throws Exception
     */
    public void insertRessourceSecuriteDB(Connection connexion, String koId,
	    String planId, String nivSecu) throws Exception {
	// ---------------------------------------------------
	String requeteSQL_sec = null;
	PreparedStatement ps_sec = null;

	try {
	    requeteSQL_sec =
		    "INSERT INTO DocSecu (koId, planId, nivSecu) VALUES(?,?,?)";
	    ps_sec = connexion.prepareStatement(requeteSQL_sec);

	    String xmlKoId = getKoId(planId);
	    ps_sec.setString(1, koId);
	    ps_sec.setString(2, xmlKoId);
	    ps_sec.setString(3, nivSecu);
	    ps_sec.execute();
	} catch (Exception e) {
	    log.error("Erreur dans insertRessourceSecuriteDB() : " + e);
	}

	finally {
	    Statement stmt_sec = connexion.createStatement();
	    stmt_sec.execute("commit");
	    ps_sec.close();
	    stmt_sec.close();
	}
    }

    /**
     * Updates a document content in the public portal database
     * 
     * @param dbConn The connection to the public portal database
     * @param koId The document identifier
     * @param lang The language of the course outline containing the document
     * @param acces The access security level
     * @param ressType The MIME type of the document
     * @param ressSize The size of the document
     * @param ressContent The document content from sakai/Opensyllabus
     * @param content The document content in bytes
     * @param siteId The identifier of the site containing the document
     */

    private void updateDocZone(Connection dbConn, String koId, String lang,
	    String acces, String ressType, int ressSize,
	    InputStream ressContent, byte[] content, String siteId) {

	String requete_upd = null;
	PreparedStatement ps_upd = null;
	ResultSet rSet_upd = null;
	String nivSecu = getSecurityLabel(acces);
	// Check if the record is already on the table

	// Add the document content in the record
	requete_upd =
		"update doczone set doccontent = ?, datemaj= sysdate, nivSecu=?  WHERE koId=? ";
	try {
	    ps_upd = dbConn.prepareStatement(requete_upd);
	    ps_upd.setBinaryStream(1, ressContent, ressSize);
	    ps_upd.setString(2, nivSecu);	    
	    ps_upd.setString(3, koId);
	    ps_upd.execute();

	    ps_upd.close();
	    try {
		ressContent.close();
	    } catch (IOException e) {
		log.warn("updateDocZone: Unable to close stream");
	    }

	    log.debug("The resource " + koId + " has been transferred.");
	} catch (SQLException e) {
	    log.error("updateDocZone(): " + e);
	}

	log.debug("The document " + koId + " has been updated");
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception (non-Javadoc)
     * @see org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO#sendXmlAndDoc(org.sakaiquebec.opensyllabus.shared.model.COSerialized,
     *      java.util.Map, java.util.Map)
     */
    public boolean sendXmlAndDoc(COSerialized published, String acces)
	    throws Exception {

	boolean sent = false;

	Map<String, String> documentSecurityMap;
	Map<String, String> documentVisibilityMap;
	Map<String, String> documents;

	COModeledServer coModeled = new COModeledServer(published);

	coModeled.XML2Model();
	coModeled.model2XML();

	documentSecurityMap = coModeled.getAllDocumentsSecurityMap();
	documentVisibilityMap = coModeled.getAllDocumentsVisibilityMap();
	documents = coModeled.getAllDocuments();

	try {
	    AuthzGroup realm =
		    authzGroupService.getAuthzGroup(SITE_PREFIX
			    + published.getSiteId());
	    String provider = realm.getProviderGroupId();
	    if (provider == null || !cmService.isSectionDefined(provider)) {
		log
			.info("The course outline "
				+ published.getSiteId()
				+ " is not associated to a section in the course management,"
				+ " it will not be transferred to ZoneCours public.");
		return false;
	    }
	    if (provider.matches(".*[Dd][Ff][1-9]")) {
		log.info("The course outline "
			+ published.getSiteId()
			+ " will not be transferred to ZoneCours public because it is a deferred section.");
		return false;
	    }
	    if (provider.endsWith(SITE_SHAREABLE)){
		log.info("The course outline "
			+ published.getSiteId()
			+ " will not be transferred to ZoneCours public because it is a dummy section references the sharable site.");
		return false;
		
	    }

	} catch (GroupNotDefinedException e) {
	    log.error("sendXmlAndDoc(): " + e);
	}

	String siteId = published.getSiteId();
	String osylCoXml = published.getContent();
	String zcco = null;
	// TODO: fill out these values from the course management
	String koId = null;
	String lang = null;

	// Transform the course outline
	zcco = transform(osylCoXml);

	if (zcco != null) {

	    // Connect to the database using config defined in
	    // sakai.properties
	    Connection dbConn = connect();

	    // Save the course outline in the zonecours database
	    // The siteId is used as koID
	    koId = getKoId(siteId);
	    lang = published.getLang().substring(0, 2);
	    sent = writeXmlInZC(zcco, koId, lang, dbConn);
	    // Save the documents in the zonecours database
	    sent =
		    sent
			    && writeDocumentsInZC(siteId, lang, acces,
				    documentSecurityMap, documentVisibilityMap,
				    documents, zcco, dbConn);
	    if (sent) {
		log.debug("The transfer to the ZoneCours "
			+ "database is complete and successful");
	    }
	    String nivSec = getSecurityLabel(acces);
	    zcPublisherService.publier(koId, lang, nivSec);
	    dbConn.close();
	}
	return sent;
    }

    /**
     * Gets the course outline identifier from siteId and Course Management
     * Information
     * 
     * @param siteId The site Identifier in sakai
     * @return the course outline identifier
     */
    private String getKoId(String siteId) {
	String koId = null;

	String provider = null;

	AuthzGroup realm = null;

	try {
	    realm = authzGroupService.getAuthzGroup(SITE_PREFIX + siteId);
	} catch (GroupNotDefinedException e) {
	    log.error("getKoId(): " + e);
	}

	if (realm != null)
	    provider = realm.getProviderGroupId();

	if (provider == null) {
	    koId = siteId;
	    setCourseNumber(siteId);
	} else if (cmService.isCanonicalCourseDefined(provider)) {
	    koId = ANNUAIRE_KOID_PREFIX + siteId;
	    setCourseNumber(siteId);
	} else if (cmService.isSectionDefined(provider)) {
	    Section section = cmService.getSection(provider);
	    CourseOffering courseOff =
		    cmService.getCourseOffering(section.getCourseOfferingEid());
	    AcademicSession session = courseOff.getAcademicSession();
	    String canCourseEid = courseOff.getCanonicalCourseEid();
	    setCourseNumber(canCourseEid);
	    koId =
		    getKoIdPrefix(session) + "-"
			    + courseOff.getCanonicalCourseEid() + "-"
			    + getGroup(section, courseOff);
	}
	return koId;
    }

    /**
     * Gets group from Course Management
     * 
     * @param s The section
     * @param c The course offering
     * @return The group
     */
    private String getGroup(Section s, CourseOffering c) {
	return s.getEid().substring(c.getEid().length());
    }

    /**
     * Gets a part of the course outline identifier
     * 
     * @param session The session
     * @return
     */
    private String getKoIdPrefix(AcademicSession session) {
	return getSession(session) + "-" + getPeriod(session);
    }

    /**
     * Gets the period associated to the course
     * 
     * @param session The session
     * @return The period
     */
    private String getPeriod(AcademicSession session) {
	String sessionId = session.getEid();
	String period = sessionId.substring(4, sessionId.length());
	return period;
    }

    public String getCourseNumber() {
	return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
	this.courseNumber = courseNumber;
    }

    /**
     * Gets the session in a specific format
     * 
     * @param session the raw session
     * @return the session in a specific format
     */
    private String getSession(AcademicSession session) {
	String sessionName = null;
	String sessionId = session.getEid();
	Date startDate = session.getStartDate();
	String year = startDate.toString().substring(0, 4);

	if ((sessionId.charAt(3)) == '1')
	    sessionName = WINTER + year;
	if ((sessionId.charAt(3)) == '2')
	    sessionName = SUMMER + year;
	if ((sessionId.charAt(3)) == '3')
	    sessionName = FALL + year;

	return sessionName;
    }

    private String getSecurityLabel(String acces) {
	String nivSecu = "1";
	if (acces.equalsIgnoreCase(ACCESS_PUBLIC)) {
	    nivSecu = "0";
	} else if (acces.equalsIgnoreCase(ACCESS_COMMUNITY)) {
	    nivSecu = "1";
	}
	return nivSecu;
    }

    /**
     * To resolve URIs
     */
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

    // ---------------------------------------------------
    /**
     * Gets all identifier associated to all documents in a course outline. Gets
     * them from the public portal XML
     * 
     * @param connexion The connection to the public portal database
     * @param lang The language of the course outline
     * @param xml The public portal XML
     * @param outTrace Used to be able to call Publication methods
     * @param trace Used to be able to call Publication methods
     * @return A hashmap with all documents and their identifiers
     * @throws Exception
     */
    private HashMap getDocsIds(Connection connexion, String lang, Document xml,
	    StringBuffer outTrace, boolean trace) throws Exception {
	// ---------------------------------------------------
	ArrayList ressAl = new ArrayList();
	HashMap docs = new HashMap();

	NodeList docExts = xml.getElementsByTagName("ressource");
	if (xml != null) {
	    int nbDocExts = docExts.getLength();
	    log.debug("Nombre de ressources : " + nbDocExts);
	    for (int i = 1; i < nbDocExts + 1; i++) {
		boolean nouveau = true;
		Element ressource = (Element) docExts.item(i - 1);
		String ressourceId = ressource.getAttribute("koId");
		String type = ressource.getAttribute("type");
		String nivSecu = ressource.getAttribute("securite");
		String fileName =
			"<font color='red'>ERREUR - pas de fichier ou d'url associe au document</font>";
		String extension = "inconnu";
		ressAl.add(ressourceId);
		boolean isTXDoc = "TX_Document".equals(type);
		NodeList urls = ressource.getElementsByTagName("url");
		if (urls != null && isTXDoc) {
		    Element url = (Element) urls.item(0);
		    if (url != null && url.hasChildNodes()) {
			NodeList children = url.getChildNodes();
			if (children.item(0) instanceof org.w3c.dom.CharacterData) {
			    log.debug("--- document " + i + ": " + ressourceId
				    + " ---");
			    org.w3c.dom.CharacterData text =
				    (org.w3c.dom.CharacterData) children
					    .item(0);
			    fileName = text.getData();
			}
			docs.put(ressourceId, fileName);
		    }
		}
	    }
	}
	return docs;
    }

    public void unpublish(String siteId, String lang) {
	String koId = getKoId(siteId);
	zcPublisherService.depublier(koId, lang.substring(0, 2));
    }
}
