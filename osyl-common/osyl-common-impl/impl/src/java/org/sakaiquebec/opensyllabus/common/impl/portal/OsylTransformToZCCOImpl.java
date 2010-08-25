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
import java.io.FileInputStream;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
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

	private Object put;

	private Document xmlSourceDoc;

	public void init() {
		log.info("INIT from OsylTransformToZCCOImpl");
	}

	/**
	 * Credentials to Access Public Portal's Database configured in
	 * sakai.properties
	 */
	private void connect() {
		driverName = ServerConfigurationService
				.getString("hec.zonecours.conn.portail.driver.name");
		url = ServerConfigurationService
				.getString("hec.zonecours.conn.portail.url");
		user = ServerConfigurationService
				.getString("hec.zonecours.conn.portail.user");
		password = ServerConfigurationService
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

	/**
	 * Verifies if Public Portal's Database Contains the XML of a specific
	 * course outline
	 * 
	 * @param koId
	 *            The identifier of the course outline
	 * @return true if the DB contains it false if not
	 */
	private boolean containsZCCo(String koId) {
		boolean hasZCCo = false;
		ResultSet rSet = null;

		try {
			Statement smnt = getConn().createStatement();
			rSet = smnt
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

	/**
	 * Writes the XML of a specific course outline in the Public Portal's
	 * Database
	 * 
	 * @param zcco
	 *            The XML to be written
	 * @param koId
	 *            The identifier of that XML
	 * @param lang
	 *            The course outline language
	 * @return true if it is written successfully false if not
	 */
	private boolean writeXmlInZC(String zcco, String koId, String lang) {
		boolean written = false;
		PreparedStatement ps = null;
		ResultSet rSet = null;

		String requete = null;

		if (containsZCCo(koId)) {
			requete = "update plancoursxml set xml = ?, dateMAJ = sysdate, lang = ? where koid like ?";
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
			requete = "insert into plancoursxml (koId,xml,dateMAJ,lang) VALUES(?,?,sysdate,?)";
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

	/**
	 * Trnaforms OpenSyllabus XML to Zonecours 1 format for publication in
	 * public portal
	 * 
	 * @param osylCo
	 *            Opensyllabus XML
	 * @return the transformed XML in Zonecours 1 version
	 */
	private String transform(String osylCo) {
		String zcco = null;

		String xsltFileDirectory = ServerConfigurationService
				.getString("osyl.to.zc.file.directory");
		String xsltFileName = ServerConfigurationService
				.getString("osyl.to.zc.file.name");

		if (xsltFileName == null || xsltFileName.length() < 4
				|| !xsltFileName.endsWith("xsl") || xsltFileDirectory == null) {
			log
					.error("The xsl file asociated to this transformation is not valid."
							+ " Please make sure the file is specified in the "
							+ "sakai.properties or the name is complete.");
			return null;
		}

		String xslt = FileHelper.getFileContent(xsltFileDirectory
				+ File.separator + xsltFileName);
		try {
			String osylCoISO88591 = "";
			try {
				osylCoISO88591 = new String(osylCo.getBytes("UTF-8"),
						"ISO-8859-1");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			zcco = XmlHelper.applyXsl(osylCoISO88591, xslt, new MyResolver(
					xsltFileDirectory));
		} catch (Exception e) {
			log.error("Unable to transform XML", e);
		}
		return zcco;
	}

	/*
	 * Retrieve documents with security level set to 'public' in one sakai's
	 * site publish folder and write them in Public portal database
	 * 
	 * @param siteId The identifier of the site where documents are retrieved
	 * 
	 * @param lang The language of the course outline of this site
	 * 
	 * @param documentSecurityMap The Map containing security level of documents
	 * 
	 * @param documentVisibityMap The Map containing visibility level of
	 * documents
	 * 
	 * @return true if all documents have been written successfully
	 */
    private boolean writeDocumentsInZC(String siteId, String lang,
	    Map<String, String> documentSecurityMap,
	    Map<String, String> documentVisibityMap, String zcco)
	    throws Exception {
	// TODO: ajouter osylPrinVersion.pdf aux documents transferes

	System.out.println("Writing documents of site " + siteId
		+ "in public portal database...");

	boolean written = false;
	String acces = null;
	String visibilite = null;
	String ressType = null;
	byte[] ressContent = null;
	int ressSize = 0;
	String docId = null;
	String fileName = null;
	StringBuffer outTrace = null;
	Publication p = new Publication();

	Document xmlSourceDoc = null;

	if (null != zcco && !("".equals(zcco))) {
	    xmlSourceDoc = p.buildDOM(zcco);
	}

	Set<String> docSecKeyValues = documentSecurityMap.keySet();
	String docVisKey = null;
	HashMap hache =
		getDocsIds(getConn(), lang, xmlSourceDoc, outTrace, false);

	for (String docSecKey : docSecKeyValues) {
	    acces = documentSecurityMap.get(docSecKey);
	    docId = docSecKey;
	    docVisKey =
		    docSecKey
			    .replaceFirst(Pattern.quote(WORK_DIR), PUBLISH_DIR);
	    visibilite = documentVisibityMap.get(docVisKey);
	    // Exclude the string "/publish" itsself to get the real filename
	    fileName = docVisKey.substring(docVisKey.indexOf("/publish") + 9);
	    fileName = fileName.replaceAll("/", "_");
	    if ("public".equals(acces) && "true".equals(visibilite)) {
		try {
		    ContentResource content =
			    contentHostingService.getResource(docVisKey);
		    // TODO: verifier les types des documents sont compatibles
		    // dans ZoneCours
		    if (content != null) {
			ressType = content.getContentType();
			ressContent = content.getContent();
			ressSize = content.getContentLength() ;
			if (hache.get("documents/" + fileName) != null)
			    docId =
				    hache.get("documents/" + fileName)
					    .toString();
			System.out.println("Writing documents of site "
				+ siteId + "in public portal database...6");
			if (docId != null)
			    writeDocInZcDb(docId, lang, acces, ressType,
				    ressSize, content.streamContent(),
				    ressContent, siteId);
		    }
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
	System.out.println("All public documents of site " + siteId
		+ "have been written in public portal database...");
	return written;
    }

	/**
	 * Writes one document in the public portal database
	 * 
	 * @param koId
	 *            The identifier of the document
	 * @param lang
	 *            The language of the course outline containing the document
	 * @param acces
	 *            The access security level
	 * @param ressType
	 *            The MIME type of the document
	 * @param ressSize
	 *            The size of the document
	 * @param ressContent
	 *            The document content from sakai/Opensyllabus
	 * @param content
	 *            The document content in bytes
	 * @param siteId
	 *            The identifier of the site containing the document
	 * @throws Exception
	 */
	private void writeDocInZcDb(String koId, String lang, String acces,
			String ressType, int ressSize, InputStream ressContent,
			byte[] content, String siteId) throws Exception {
		System.out.println("Writing document  " + koId
				+ "in public portal database...");
		Connection con = getConn();

		boolean exist = selectDocInDocZone(koId, lang, acces, ressType,
				ressSize, ressContent, content, siteId);

		// Check if the record is already on the table
		if (!exist) {

			insertDocInDocZone(con, koId, lang, acces, ressType, ressSize,
					ressContent, content, siteId);
			// Add the information to the relational table
			// Clean the place to avoid unique constraint violation
			deleteRessourceSecuriteDB(con, koId, siteId);
			// By default, security is zero for all documents belonging to
			// portal
			insertRessourceSecuriteDB(con, koId, siteId, "0");

		} else {

			updateDocZone(con, koId, lang, acces, ressType, ressSize,
					ressContent, content, siteId);
			// Clean the place to avoid unique constraint violation
			deleteRessourceSecuriteDB(con, koId, siteId);
			// By default, security is zero for all documents belonging to
			// portal
			insertRessourceSecuriteDB(con, koId, siteId, "0");

		}
		System.out.println("Document " + koId
				+ " has been written in the database");
	}

	/**
	 * Tells if a document is already in the database
	 * 
	 * @param koId
	 *            The document identifier
	 * @param lang
	 *            The language of the course outline containing the document
	 * @param acces
	 *            The access security level
	 * @param ressType
	 *            The MIME type of the document
	 * @param ressSize
	 *            The size of the document
	 * @param ressContent
	 *            The document content from sakai/Opensyllabus
	 * @param content
	 *            The document content in bytes
	 * @param siteId
	 *            The identifier of the site containing the document
	 * @return true if the document is in the database false otherwise
	 */
	private boolean selectDocInDocZone(String koId, String lang, String acces,
			String ressType, int ressSize, InputStream ressContent,
			byte[] content, String siteId) {
		boolean isthere = false;
		String requete_select = null;
		PreparedStatement ps_select = null;
		ResultSet rSet_select = null;

		requete_select = "select * from doczone where koId like ?";
		try {
			ps_select = getConn().prepareStatement(requete_select);
			ps_select.setString(1, koId);
			rSet_select = ps_select.executeQuery();

			if (rSet_select.next())
				isthere = true;

			ps_select.close();
			rSet_select.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isthere;
	}

	/**
	 * Inserts a document in the public portal database
	 * 
	 * @param con
	 *            The connection to the public portal database
	 * @param koId
	 *            The document identifier
	 * @param lang
	 *            The language of the course outline containing the document
	 * @param acces
	 *            The access security level
	 * @param ressType
	 *            The MIME type of the document
	 * @param ressSize
	 *            The size of the document
	 * @param ressContent
	 *            The document content from sakai/Opensyllabus
	 * @param content
	 *            The document content in bytes
	 * @param siteId
	 *            The identifier of the site containing the document
	 * @throws IOException
	 * @throws Exception
	 */
	private void insertDocInDocZone(Connection con, String koId, String lang,
			String acces, String ressType, int ressSize,
			InputStream ressContent, byte[] content, String siteId)
			throws IOException, Exception {
		System.out.println("insertDocInDocZone - debut");

		String requete_ins = null;
		PreparedStatement ps_ins = null;
		ResultSet rSet_ins = null;

		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		BLOB blob;

		try {

			requete_ins = "INSERT INTO DocZone (koId, lang, nivSecu, ressType, dateMAJ, docContent) VALUES(?,?,?,?,sysdate,empty_blob())";
			ps_ins = con.prepareStatement(requete_ins);
			ps_ins.setString(1, koId);
			ps_ins.setString(2, lang);
			ps_ins.setString(3, "0");
			ps_ins.setString(4, ressType);
			ps_ins.execute();
			ps_ins.close();

			requeteSQL = "SELECT docContent FROM DocZone WHERE koId=? FOR UPDATE";
			ps = con.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			if (rset.next()) {
				blob = ((OracleResultSet) rset).getBLOB(1);
				OutputStream blobOutput = ((oracle.sql.BLOB) blob)
						.getBinaryOutputStream();

				byte[] buffer = new byte[10 * 1024];

				int nread = 0; // Number of bytes read
				while ((nread = ressContent.read(buffer)) != -1) {
					blobOutput.write(buffer, 0, nread);

					System.out.print(nread);
				}
				ressContent.close();
				blobOutput.close();

			}

			log.debug("The resource " + koId + " has been transferred.");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Statement stmt = getConn().createStatement();
			stmt.execute("commit");
			rset.close();
			ps.close();
			stmt.close();

		}
		System.out.println("The document " + koId + " has been transferred");

	}

	// ---------------------------------------------------
	/**
	 * Deletes the security level of a document in the public portal database.
	 * 
	 * @param connexion
	 *            The connection to public portal database
	 * @param koId
	 *            The identifier of the document
	 * @param planId
	 *            The identifier of the course outline
	 * @throws Exception
	 */
	public void deleteRessourceSecuriteDB(Connection connexion, String koId,
			String planId) throws Exception {
		// ---------------------------------------------------
		String requeteSQL_del = null;
		Statement stmt_del = connexion.createStatement();

		String xmlKoId = getKoId(planId);

		requeteSQL_del = " DELETE FROM DocSecu WHERE koId = '" + koId
				+ "' AND planId='" + xmlKoId + "'";
		System.out.println("<br>" + requeteSQL_del + " ...");
		stmt_del.execute(requeteSQL_del);
		System.out.println(" ok");

		stmt_del.close();
	}

	// ---------------------------------------------------
	/**
	 * Deletes a document in the public portal database.
	 * 
	 * @param connexion
	 *            The connection to public portal database
	 * @param koId
	 *            The identifier of the document
	 * @throws Exception
	 */
	public void deleteRessourceFromDB(Connection connexion, String koId)
			throws Exception {
		// ---------------------------------------------------
		String requeteSQL_delRess = null;
		Statement stmt_delRess = connexion.createStatement();

		requeteSQL_delRess = " DELETE FROM DocZone WHERE koId = '" + koId + "'";
		System.out.println("<br>" + requeteSQL_delRess + " ...");
		stmt_delRess.execute(requeteSQL_delRess);
		System.out.println(" ok_delRess");

		stmt_delRess.close();
	}

	// ---------------------------------------------------
	/**
	 * Inserts the security level of a document in the public portal database.
	 * 
	 * @param connexion
	 *            The connection to public portal database
	 * @param koId
	 *            The identifier of the document
	 * @param planId
	 *            The identifier of the document
	 * @param nivSecu
	 *            The security level to be inserted
	 * @throws Exception
	 */
	public void insertRessourceSecuriteDB(Connection connexion, String koId,
			String planId, String nivSecu) throws Exception {
		// ---------------------------------------------------
		String requeteSQL_sec = null;
		PreparedStatement ps_sec = null;

		try {
			requeteSQL_sec = "INSERT INTO DocSecu (koId, planId, nivSecu) VALUES(?,?,?)";
			ps_sec = connexion.prepareStatement(requeteSQL_sec);

			String xmlKoId = getKoId(planId);
			ps_sec.setString(1, koId);
			ps_sec.setString(2, xmlKoId);
			ps_sec.setString(3, nivSecu);
			ps_sec.execute();
		} catch (Exception e) {
			System.err
					.println("Erreur dans insertRessourceSecuriteDB() : " + e);
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
	 * @param con
	 *            The connection to the public portal database
	 * @param koId
	 *            The document identifier
	 * @param lang
	 *            The language of the course outline containing the document
	 * @param acces
	 *            The access security level
	 * @param ressType
	 *            The MIME type of the document
	 * @param ressSize
	 *            The size of the document
	 * @param ressContent
	 *            The document content from sakai/Opensyllabus
	 * @param content
	 *            The document content in bytes
	 * @param siteId
	 *            The identifier of the site containing the document
	 */

	private void updateDocZone(Connection con, String koId, String lang,
			String acces, String ressType, int ressSize,
			InputStream ressContent, byte[] content, String siteId) {

		String requete_upd = null;
		PreparedStatement ps_upd = null;
		ResultSet rSet_upd = null;

		// Check if the record is already on the table

		// Add the document content in the record
		requete_upd = "update doczone set doccontent = ?, datemaj= sysdate  WHERE koId=? ";
		try {
			ps_upd = con.prepareStatement(requete_upd);
			ps_upd.setBinaryStream(1, ressContent, ressSize);
			ps_upd.setString(2, koId);
			ps_upd.execute();

			ps_upd.close();

			log.debug("The resource " + koId + " has been transferred.");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("The document " + koId + " has been updated");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 *             (non-Javadoc)
	 * @see org.sakaiquebec.opensyllabus.common.api.portal.OsylTransformToZCCO#sendXmlAndDoc(org.sakaiquebec.opensyllabus.shared.model.COSerialized,
	 *      java.util.Map, java.util.Map)
	 */
	public boolean sendXmlAndDoc(COSerialized published) throws Exception {

		Map<String, String> documentSecurityMap;
		Map<String, String> documentVisibilityMap;
		
		COModeledServer coModeled = new COModeledServer(published);
		
		coModeled.XML2Model();
		coModeled.model2XML();
		
		documentSecurityMap = coModeled.getDocumentSecurityMap();
		documentVisibilityMap = coModeled.getDocumentVisibilityMap();
		
		try {
			AuthzGroup realm = AuthzGroupService.getAuthzGroup(SITE_PREFIX
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

		// Check the properties in sakai.properties first than connect to the
		// database
		connect();

		// Transform the course outline
		zcco = transform(osylCoXml);

		if (zcco != null) {
			// Save the course outline in the zonecours database
			// The siteId is used as koID
			koId = getKoId(siteId);
			lang = published.getLang().substring(0, 2);
			sent = writeXmlInZC(zcco, koId, lang);
			// Save the documents in the zonecours database
			sent = sent
					&& writeDocumentsInZC(siteId, lang, documentSecurityMap,
							documentVisibilityMap, zcco);
			if (sent)
				log
						.debug("The transfer to the ZoneCours database is complete and successful");
			System.out
					.println("The transfer to the ZoneCours database is complete and successful");
			zcPublisherService.publier(koId, lang);
		}
		return sent;
	}

	/**
	 * Gets the course outline identifier from siteId and Course Management
	 * Information
	 * 
	 * @param siteId
	 *            The site Identifier in sakai
	 * @return the course outline identifier
	 */
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
			CourseOffering courseOff = cmService.getCourseOffering(section
					.getCourseOfferingEid());
			AcademicSession session = courseOff.getAcademicSession();
			koId = getKoIdPrefix(session) + "-"
					+ courseOff.getCanonicalCourseEid() + "-"
					+ getGroup(section, courseOff);
		}
		return koId;
	}

	/**
	 * Gets group from Course Management
	 * 
	 * @param s
	 *            The section
	 * @param c
	 *            The course offering
	 * @return The group
	 */
	private String getGroup(Section s, CourseOffering c) {
		return s.getEid().substring(c.getEid().length());
	}

	/**
	 * Gets a part of the course outline identifier
	 * 
	 * @param session
	 *            The session
	 * @return
	 */
	private String getKoIdPrefix(AcademicSession session) {
		return getSession(session) + "-" + getPeriod(session);
	}

	/**
	 * Gets the period associated to the course
	 * 
	 * @param session
	 *            The session
	 * @return The period
	 */
	private String getPeriod(AcademicSession session) {
		String sessionId = session.getEid();
		String period = sessionId.substring(4, sessionId.length());
		return period;
	}

	/**
	 * Gets the session in a specific format
	 * 
	 * @param session
	 *            the raw session
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

	/**
	 * To resolve URIs
	 * 
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
	 * @param connexion
	 *            The connection to the public portal database
	 * @param lang
	 *            The language of the course outline
	 * @param xml
	 *            The public portal XML
	 * @param outTrace
	 *            Used to be able to call Publication methods
	 * @param trace
	 *            Used to be able to call Publication methods
	 * @return A hashmap with all documents and their identifiers
	 * @throws Exception
	 */
	private HashMap getDocsIds(Connection connexion, String lang, Document xml,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		Publication p = new Publication();
		ArrayList ressAl = new ArrayList();
		HashMap docs = new HashMap();

		NodeList docExts = xml.getElementsByTagName("ressource");
		if (xml != null) {
			int nbDocExts = docExts.getLength();
			System.out.println("Nombre de ressources : " + nbDocExts);
			for (int i = 1; i < nbDocExts + 1; i++) {
				boolean nouveau = true;
				Element ressource = (Element) docExts.item(i - 1);
				String ressourceId = ressource.getAttribute("koId");  
				String type = ressource.getAttribute("type");
				String nivSecu = ressource.getAttribute("securite");
				String fileName = "<font color='red'>ERREUR - pas de fichier ou d'url associe au document</font>";
				String extension = "inconnu";
				ressAl.add(ressourceId);
				boolean isTXDoc = "TX_Document".equals(type);
				NodeList urls = ressource.getElementsByTagName("url");
				if (urls != null && isTXDoc) {
					Element url = (Element) urls.item(0);
					if (url != null && url.hasChildNodes()) {
						NodeList children = url.getChildNodes();
						if (children.item(0) instanceof org.w3c.dom.CharacterData) {
							System.out.println("--- document " + i + ": "
									+ ressourceId + " ---");
							org.w3c.dom.CharacterData text = (org.w3c.dom.CharacterData) children
									.item(0);
							fileName = text.getData();
						}
						docs.put(fileName, ressourceId);
					}
				}
			}
		}
		return docs;
	}
}
