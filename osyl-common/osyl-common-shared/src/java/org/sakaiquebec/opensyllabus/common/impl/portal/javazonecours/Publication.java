package org.sakaiquebec.opensyllabus.common.impl.portal.javazonecours;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Publication holds all methods to transform XML to a readable HTML. This code
 * is recycled from ZoneCours 1 application previously used at HEC
 * Montreal(OpenSyllabus ancestor) Helps to publish in HEC Montreal's public
 * portal Works very closely with OsylTransformToZCCOImpl
 * 
 * @author <a href="mailto:yvette.lapa-dessap@hec.ca">Yvette Lapa Dessap</a>
 * @version $Id: $
 */
public class Publication {

    private static Log log = LogFactory.getLog(Publication.class);

	public int nbSpaces = 0;
	public int nbImgs = 0;
	public Hashtable transformersTable;
	public HashMap transformersMap;
	public String appDirName;
    public static final String PUBLIC_SECURITY_LABEL = "0";	
    public static final String COMMUNITY_SECURITY_LABEL = "1";
    public static final String ATTENDEE_SECURITY_LABEL = "2";	

	// ==================================================================================

	// ==================================================================================

	// ---------------------------------------------------
	public String getRootName(Node node) throws Exception {
		// ---------------------------------------------------
		String result = null;
		if (((Document) node).getDocumentElement() != null)
			result = ((Document) node).getDocumentElement().getNodeName();
		return result;
	}

	// ---------------------------------------------------
	public String getRootKoId(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getDocumentElement().getAttribute("koId");
	}

	// ---------------------------------------------------
	public String getRootLang(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getDocumentElement().getAttribute("lang");
	}

	// ---------------------------------------------------
	public String getRootType(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getDocumentElement().getAttribute("type");
	}

	// ---------------------------------------------------
	public String getRootPeriode(Node node) throws Exception {
		// ---------------------------------------------------
		String result = "";
		NodeList liste = ((Document) node).getElementsByTagName("session");
		if (liste.getLength() != 0) {
			Element elt = (Element) liste.item(0);
			NodeList liste2 = elt.getElementsByTagName("no");
			if (liste2.getLength() != 0) {
				Element elt2 = (Element) liste2.item(0);
				if (elt2.hasChildNodes()) {
					NodeList children = elt2.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						org.w3c.dom.CharacterData data = (org.w3c.dom.CharacterData) children
								.item(0);
						String textData = data.getData();
						result = textData.substring(textData.indexOf(".") + 1,
								textData.length());
					}
				}
			}
		}
		return result;
	}

	// ---------------------------------------------------
	public String getRootCourseId(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getDocumentElement().getAttribute("courseId");
	}

	// ---------------------------------------------------
	public NodeList getPlanPartageables(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getElementsByTagName("partageable");
	}

	// ---------------------------------------------------
	public NodeList getPlanPartages(Node node) throws Exception {
		// ---------------------------------------------------
		return ((Document) node).getElementsByTagName("partage");
	}

	// ---------------------------------------------------
	public String getPlanService(Node node) throws Exception {
		// ---------------------------------------------------
		String result = "";
		NodeList services = ((Document) node).getElementsByTagName("service");
		if (services != null) {
			Element service = (Element) services.item(0);
			if (service.hasChildNodes()) {
				NodeList children = service.getChildNodes();
				if (children.item(0) instanceof org.w3c.dom.CharacterData) {
					result = ((org.w3c.dom.CharacterData) children.item(0))
							.getData();
				}
			}
		}
		return result;
	}

	// ---------------------------------------------------
	public String getPlanLibelle(Node node) throws Exception {
		// ---------------------------------------------------
		String result = "";
		NodeList cours = ((Document) node).getElementsByTagName("cours");
		int nbListe = cours.getLength();
		for (int i = 0; i < nbListe; i++) {
			NodeList libelles = ((Element) cours.item(i))
					.getElementsByTagName("libelle");
			if (libelles.getLength() > 0) {
				Element libelle = (Element) libelles.item(0);
				if (libelle.hasChildNodes()) {
					NodeList children = libelle.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						result = ((org.w3c.dom.CharacterData) children.item(0))
								.getData();
					}
				}
			}
		}
		return result;
	}

	// ---------------------------------------------------
	public void setPlanLibelle(Node node, String lbl) throws Exception {
		// ---------------------------------------------------

		NodeList cours = ((Document) node).getElementsByTagName("cours");
		int nbListe = cours.getLength();
		for (int i = 0; i < nbListe; i++) {
			NodeList libelles = ((Element) cours.item(i))
					.getElementsByTagName("libelle");
			if (libelles.getLength() > 0) {
				Element libelle = (Element) libelles.item(0);
				if (libelle.hasChildNodes()) {
					NodeList children = libelle.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						((org.w3c.dom.CharacterData) children.item(0))
								.setData(lbl);
					}
				}
			}
		}
	}

	// ---------------------------------------------------
	public String getPlanProfesseur(Node node) throws Exception {
		// ---------------------------------------------------
		String result = "";
		NodeList professeurs = ((Document) node)
				.getElementsByTagName("professeur");
		if (professeurs != null) {
			NodeList prenoms = ((Element) professeurs.item(0))
					.getElementsByTagName("prenom");
			if (prenoms != null) {
				Element prenom = (Element) prenoms.item(0);
				if (prenom.hasChildNodes()) {
					NodeList children = prenom.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						result = ((org.w3c.dom.CharacterData) children.item(0))
								.getData();
					}
				}
			}
			NodeList noms = ((Element) professeurs.item(0))
					.getElementsByTagName("nom");
			if (noms != null) {
				Element nom = (Element) noms.item(0);
				if (nom.hasChildNodes()) {
					NodeList children = nom.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						result = result
								+ " "
								+ ((org.w3c.dom.CharacterData) children.item(0))
										.getData();
					}
				}
			}
		}
		return result;
	}

	// ==================================================================================

	// ==================================================================================

	// ---------------------------------------------------
	public void createAndSetAttribute(Node node, String tagAttributeName,
			String tagAttributeValue) throws Exception {
		// ---------------------------------------------------
		((Document) node).createAttribute(tagAttributeName);
		((Document) node).getDocumentElement().setAttribute(tagAttributeName,
				tagAttributeValue);

	}

	// ---------------------------------------------------
	public void SetAttribute(Node node, String tagAttributeName,
			String tagAttributeValue) throws Exception {
		// ---------------------------------------------------
		((Document) node).getDocumentElement().setAttribute(tagAttributeName,
				tagAttributeValue);

	}

	// ---------------------------------------------------
	public void majLang(Document xslDocument, String lang) throws Exception {
		// ---------------------------------------------------
		NodeList includes = xslDocument.getElementsByTagName("xsl:include");
		if (includes.getLength() != 0) {
			Element selecteur = (Element) includes.item(0);
			selecteur.setAttribute("href", "libelles_" + lang + ".xsl");
		}
	}

	// ==================================================================================

	// ==================================================================================

	// ---------------------------------------------------
	public Document newDOM() throws Exception {
		// ---------------------------------------------------
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
		return domBuilder.newDocument();
	}

	// ---------------------------------------------------
	public Document buildDOM(String xmlString) throws Exception {
		// ---------------------------------------------------
		DocumentBuilderFactory domBuildFact = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuild = domBuildFact.newDocumentBuilder();
		return domBuild.parse(new InputSource(new StringReader(xmlString)));
	}

	// ---------------------------------------------------
	public void saveString(String str, String fileName) throws Exception {
		// ---------------------------------------------------
		FileWriter fwriter = new FileWriter(fileName);

		fwriter.write(str);
		fwriter.close();
	}

	// ---------------------------------------------------
	public Document loadDOM(String XMLfileName) throws Exception {
		// ---------------------------------------------------
		DocumentBuilderFactory domBuildFact = DocumentBuilderFactory
				.newInstance();
		domBuildFact.setNamespaceAware(true);
		DocumentBuilder domBuild = domBuildFact.newDocumentBuilder();
		return domBuild.parse(XMLfileName);
	}

	// ---------------------------------------------------
	public void saveDOM(Document doc, String xmlFileName) throws Exception {
		// ---------------------------------------------------
		try {
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer();

			Source srce = new DOMSource(doc);
			Result dest = new StreamResult(new File(xmlFileName));

			trans.transform(srce, dest);
		} catch (TransformerConfigurationException tce) {
			throw new TransformerException(tce.getMessageAndLocation());
		} catch (TransformerException te) {
			throw new TransformerException(te.getMessageAndLocation());
		}

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

	// ---------------------------------------------------
	public Transformer createTransformer(Document xslt, String xsltDirName)
			throws Exception {
		// ---------------------------------------------------
		MyResolver uriResolver = new MyResolver(xsltDirName);

		Transformer transformer = null;
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setURIResolver(uriResolver);
			transformer = factory.newTransformer(new DOMSource(xslt));
		} catch (TransformerConfigurationException tce) {
			throw new TransformerException(tce.getMessageAndLocation());
		}
		return transformer;
	}

	// ==================================================================================
	// Affichage DOM
	// ==================================================================================

	// ---------------------------------------------------
	public String spaces(int nbSpaces) throws Exception {
		// ---------------------------------------------------
		String result = "";
		for (int i = 0; i < nbSpaces; i++)
			result += " ";
		return result;
	}

	// ---------------------------------------------------
	public String dom2string(Node node) throws Exception {
		// ---------------------------------------------------

		StringBuffer buffer = new StringBuffer();

		int type = node.getNodeType();

		switch (type) {

		// print the document element
		case Node.DOCUMENT_NODE: {
			if (((Document) node).getDocumentElement() != null)
				buffer
						.append(dom2string(((Document) node)
								.getDocumentElement()));
			else
				buffer.append("<br>Ceci n'est pas un document XML!");
			break;
		}
			// print element with attributes
		case Node.ELEMENT_NODE: {
			buffer.append(spaces(nbSpaces) + "<");
			buffer.append(node.getNodeName());
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				buffer.append(" " + attr.getNodeName().trim() + "=\""
						+ attr.getNodeValue().trim() + "\"");
			}
			buffer.append(">\n");

			NodeList children = node.getChildNodes();
			if (children != null) {
				nbSpaces++;
				int len = children.getLength();
				for (int i = 0; i < len; i++)
					buffer.append(dom2string(children.item(i)));
				nbSpaces--;
			}
			buffer.append(spaces(nbSpaces) + "</");
			buffer.append(node.getNodeName());
			buffer.append(">\n");
			break;
		}

			// handle entity reference nodes
		case Node.ENTITY_REFERENCE_NODE: {
			buffer.append(spaces(nbSpaces));
			buffer.append("&");
			buffer.append(node.getNodeName().trim());
			buffer.append(";\n");
			break;
		}

			// print cdata sections
		case Node.CDATA_SECTION_NODE: {
			buffer.append(spaces(nbSpaces));
			buffer.append("<![CDATA[");
			buffer.append(node.getNodeValue().trim());
			buffer.append("]]>\n");
			break;
		}

			// print text
		case Node.TEXT_NODE: {
			buffer.append(spaces(nbSpaces));
			buffer.append(node.getNodeValue().trim());
			buffer.append("\n");
			break;
		}

			// print processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			buffer.append("");
			break;
		}
		}// end switch

		if (type == Node.ELEMENT_NODE) {
			buffer.append("");
		}

		return buffer.toString();
	}

	// ==================================================================================

	// ==================================================================================

	// ---------------------------------------------------
	public void processXSLT(Source xml, String xsltName, Result result,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

	    	log.error("TRANSFORM: "+xsltName);

	    	Transformer transformer = (Transformer) transformersTable.get(xsltName);
		
		try {
			transformer.transform(xml, result);

		} catch (TransformerConfigurationException tce) {
			throw new TransformerException(tce.getMessageAndLocation());
		} catch (TransformerException te) {
		    log.error("ERROR TRANSFORM: "+xsltName);
		    log.error("ERROR TRANSFORM TRACE: "+outTrace.toString());
		    log.error("ERROR TRANSFORM SOURCE: "+xml.toString());
		    
		    throw new TransformerException(te.getMessageAndLocation());
		}
	}

	// ---------------------------------------------------
	public void processXSLT(Document xml, String xsltName, Document result,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("a.");
		processXSLT(new DOMSource(xml), xsltName, new DOMResult(result),
				outTrace, trace);
		if (trace)
			outTrace.append("b.");
	}

	// ---------------------------------------------------
	public void processXSLT(Document xml, String xsltName, Writer result,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("c.");
		processXSLT(new DOMSource(xml), xsltName, new StreamResult(result),
				outTrace, trace);
		if (trace)
			outTrace.append("d.");
	}

	// ==================================================================================
	// Table Seance
	// ==================================================================================

	// ---------------------------------------------------
	private void deleteFromSeance(Connection connexion, String koId,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		requeteSQL = "DELETE FROM Seance WHERE koIdSeance = '" + koId + "'";
		if (trace)
			outTrace.append("<br>" + requeteSQL + "...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
	}

	// ---------------------------------------------------
	private void insertIntoSeance(Connection connexion, String seanceId,
			String coursId, String libelleSeance, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		PreparedStatement ps = null;

		requeteSQL = "INSERT INTO Seance (koIdSeance,koIdCours,libelleSeance) VALUES(?,?,?)";
		if (trace)
			outTrace.append("<br>" + requeteSQL + "...");
		ps = connexion.prepareStatement(requeteSQL);
		ps.setString(1, seanceId);
		ps.setString(2, coursId);
		ps.setString(3, libelleSeance);
		ps.execute();
		ps.close();
		if (trace)
			outTrace.append(" ok");

	}

	// ==================================================================================
	// Table PlanZone
	// ==================================================================================

	// ---------------------------------------------------
	public void deleteFromPlanZone(Connection connexion, String koId,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace, String nivSecu)
			throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		if (!nivSecu.equals("")) {
			//From publish
			requeteSQL = "DELETE FROM PlanZone WHERE koId = '" + koId + "' and NivSecu='" + nivSecu + "'";
		} else {
			//From unpublish
			requeteSQL = "DELETE FROM PlanZone WHERE koId = '" + koId + "'";
		}
		if (trace)
			outTrace.append("<br>" + requeteSQL + "...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
	}

	// ---------------------------------------------------
	public void processAndInsertInDB(Connection connexion, String koId,
			String lang, String ressId, String ressType, String nivSecu,
			Document xml, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace) {
			outTrace.append("<br><br>processAndInsertInDB - entree :"
					+ new java.util.Date(System.currentTimeMillis()));
			outTrace.append("<br>" + "&nbsp;&nbsp;koId :" + koId);
			outTrace.append("<br>" + "&nbsp;&nbsp;lang :" + lang);
			outTrace.append("<br>" + "&nbsp;&nbsp;ressId :" + ressId);
			outTrace.append("<br>" + "&nbsp;&nbsp;ressType :" + ressType);
			outTrace.append("<br>" + "&nbsp;&nbsp;nivSecu :" + nivSecu);
		}

		Clob clob;
		String requeteSQL_Select = null;
		PreparedStatement ps_Select = null;
		ResultSet rset_Select = null;

		String type = ressType;
		if (type.equals("11a"))
			type = "11";

		InsertInDB(connexion, koId, lang, ressId, ressType, nivSecu, xml, type,
				outPrint, outTrace, trace);

		try {
			requeteSQL_Select = "SELECT koId, html FROM Planzone WHERE koId=? AND nivSecu=? AND ressType=? FOR UPDATE";
			ps_Select = connexion.prepareStatement(requeteSQL_Select);
			ps_Select.setString(1, koId);
			ps_Select.setString(2, nivSecu);
			ps_Select.setString(3, type);

			rset_Select = ps_Select.executeQuery();

			if (rset_Select != null && rset_Select.next()) {
				String index = rset_Select.getString(1);

				clob = rset_Select.getClob(2);

				Writer outstream = null;
				
				try{
				    outstream = clob.setCharacterStream(1L);
				}catch(SQLException sqlex){
				    log.error("setCharacterStream:"+sqlex.getErrorCode());
				    log.error("setCharacterStream:"+sqlex.getMessage());
				    throw sqlex;
				}

				processXSLT(xml, lang + "-" + ressType + ".xsl", outstream,
						outTrace, trace);

				outstream.close();
			}

		} catch (Exception e) {
			log.error("driver info name:"+connexion.getMetaData().getDriverName());
			log.error("driver info version:"+connexion.getMetaData().getDriverVersion());

			log.error("========= Erreur dans processAndInsertInDB: "
					+ e);
		} finally {

			Statement stmt_Select = connexion.createStatement();
			stmt_Select.execute("commit");
			rset_Select.close();
			ps_Select.close();
			stmt_Select.close();
			if (trace) {
				outTrace.append("<br>processAndInsertInDB - sortie :"
						+ new java.util.Date(System.currentTimeMillis()));
			}
		}

	}

	// ---------------------------------------------------
	public void InsertInDB(Connection connexion, String koId, String lang,
			String ressId, String ressType, String nivSecu, Document xml,
			String type, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		String requeteSQL_Insert = null;
		PreparedStatement ps_Insert = null;

		try {
			requeteSQL_Insert = "INSERT INTO Planzone (koId, lang, ressId,nivSecu,ressType,dateMAJ,html) VALUES(?,?,?,?,?,sysdate,empty_clob())";
			ps_Insert = connexion.prepareStatement(requeteSQL_Insert);
			ps_Insert.setString(1, koId);
			ps_Insert.setString(2, lang);
			ps_Insert.setString(3, ressId);
			ps_Insert.setString(4, nivSecu);
			ps_Insert.setString(5, type);
			ps_Insert.execute();
			log.trace("The file " + ressId + ".hml has been processed");

		} catch (Exception e) {
			log.error("========= Erreur dans InsertInDB() : " + e);
		} finally {
			Statement stmt_Insert = connexion.createStatement();
			stmt_Insert.execute("commit");
			ps_Insert.close();
			stmt_Insert.close();
		}
		// log.debug("Insertion avec succes!");
	}

	// ==================================================================================
	// Table Plancours
	// ==================================================================================

	// ---------------------------------------------------
	public void deleteFromPlanCours(Connection connexion, String koId,
			String lang, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		requeteSQL = "DELETE FROM PlanCours WHERE koId = '" + koId + "'";
		if (trace)
			outTrace.append("<br>" + requeteSQL + "...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
	}

	// ---------------------------------------------------
	public void insertIntoPlanCours(Connection connexion, String koId,
			String lang, String codeCours, String sessionCours,
			String sectionCours, String periode, String service,
			String programme, String libelleCours, String professeur,
			String planType, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		PreparedStatement ps = null;
		String actif = "O";

		try {
			requeteSQL = "INSERT INTO PlanCours VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, lang);
			ps.setString(3, codeCours);
			ps.setString(4, sessionCours);
			ps.setString(5, sectionCours);
			ps.setString(6, service);
			ps.setString(7, programme);
			ps.setString(8, libelleCours);
			ps.setString(9, professeur);
			ps.setString(10, planType);
			ps.setString(11, periode);
			ps.setString(12, actif);
			ps.execute();
		} catch (Exception e) {
			outPrint.append("Erreur dans insertIntoPlanCours() : " + e);
		}

		finally {
			Statement stmt = connexion.createStatement();
			stmt.execute("commit");
			ps.close();
			stmt.close();
		}
	}

	// ---------------------------------------------------
	public void majListeCours(Connection connexionPublication,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>majListeCours ... ");

		String requeteSQL = null;
		Statement stmt = connexionPublication.createStatement();
		try {

			requeteSQL = "UPDATE PlanCours SET actif = 'N' WHERE codeCours NOT IN (SELECT * FROM PlanCoursPeopleSoft)";
			if (trace)
				outTrace.append(requeteSQL + " ... ");
			stmt.execute(requeteSQL);

		} catch (Exception e) {
			outPrint.append("<br>========= Erreur dans majListeCours() : " + e);
		}

		finally {
			stmt.close();
		}

		if (trace)
			outTrace.append("ok");
	}

	// ==================================================================================
	// Table PlanCoursXML
	// ==================================================================================

	// ---------------------------------------------------
	public String readXmlString(Connection connexion, String koId, String lang,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		Clob clob = null;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		String xmlString = "";

		requeteSQL = "SELECT koId, xml FROM PlanCoursXML WHERE koId='" + koId
				+ "' AND lang='" + lang + "'";
		ps = connexion.prepareStatement(requeteSQL);
		// ps.setString(1, koId);
		// ps.setString(2, lang);
		rset = ps.executeQuery();

		StringBuffer buf = new StringBuffer();
		while (rset.next()) {
			clob = rset.getClob(2);
			long length = clob.length();
			buf.append(clob.getSubString(1, (int) length));
		}
		rset.close();
		ps.close();
		xmlString = buf.toString();
		return xmlString;

	}

	// ---------------------------------------------------
	public void saveXmlStringInDB(Connection connexionPublication,
			StringBuffer xmlString, String koId, String langue,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------

		try {
			// ==================CONNEXION===================================
			Clob clob;
			String requeteSQL = null;
			PreparedStatement ps = null;
			ResultSet rset = null;

			// -- recherche si la page existe
			if (trace)
				outTrace.append("=== recherche: " + koId + langue);
			requeteSQL = "SELECT koId FROM PlanCoursXML WHERE koId=? AND lang=?";
			ps = connexionPublication.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, langue);
			rset = ps.executeQuery();
			if (trace)
				outTrace.append("=== recherche effectuee ");

			// -- si la page existe on la detruit
			if (rset.next()) {
				if (trace)
					outTrace.append("=== delete ");
				requeteSQL = "DELETE FROM PlanCoursXML WHERE koId=? AND lang=?";
				ps = connexionPublication.prepareStatement(requeteSQL);
				ps.setString(1, koId);
				ps.setString(2, langue);
				ps.execute();
			}
			rset.close();

			if (trace)
				outTrace.append("=== insertion ");
			requeteSQL = "INSERT INTO PlanCoursXML (koId,xml,dateMAJ,lang) VALUES(?,empty_clob(),sysdate,?)";
			ps = connexionPublication.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, langue);
			ps.execute();

			if (trace)
				outTrace.append("=== Select for update ");
			requeteSQL = "SELECT koId, xml FROM PlanCoursXML WHERE koId=? AND lang=? FOR UPDATE";
			ps = connexionPublication.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, langue);

			rset = ps.executeQuery();

			if (rset.next()) {
				if (trace)
					outTrace.append("=== ecriture ");
				clob = rset.getClob(2);
				Writer outstream = clob.setCharacterStream(1L);
				// ------- ecriture ------------------------------

				outstream.write(xmlString.toString());
				// ------------------------------------------------
				outstream.close();
			}
			rset.close();
			Statement stmt = connexionPublication.createStatement();
			stmt.execute("commit");
			ps.close();
			stmt.close();
		} // try
		catch (Exception e) {
			outPrint.append("Erreur : " + e);
		}

		finally {
			if (trace)
				outTrace
						.append("================== xmlDisplayInDatabase :sortie ============");
		}

	}

	// ---------------------------------------------------
	public String readXmlDateMaj(Connection connexion, String koId,
			String lang, StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;

		requeteSQL = "SELECT dateMaj FROM PlanCoursXML WHERE koId=? AND lang=?";
		ps = connexion.prepareStatement(requeteSQL);
		ps.setString(1, koId);
		ps.setString(2, lang);
		rset = ps.executeQuery();

		String result = null;
		while (rset.next()) {
			result = rset.getString(1);
		}
		rset.close();
		ps.close();
		return result;

	}

	// ==================================================================================
	// Table PlanCoursPeopleSoft
	// ==================================================================================

	// ---------------------------------------------------
	public void supprimerListeCours(Connection connexionPublication,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>supprimerListeCours ... ");

		String requeteSQL = null;
		Statement stmt = connexionPublication.createStatement();
		try {

			requeteSQL = "DELETE FROM PlanCoursPeopleSoft";
			if (trace)
				outTrace.append(requeteSQL + " ... ");
			stmt.execute(requeteSQL);

		} catch (Exception e) {
			outPrint
					.append("<br>========= Erreur dans supprimerListeCours() : "
							+ e);
		}

		finally {
			stmt.execute("commit");
			stmt.close();
		}

		if (trace)
			outTrace.append("ok");
	}

	// ---------------------------------------------------
	public void copierListeCours(Connection connexionPeopleSoft,
			Connection connexionPublication, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>copierListeCours ... ");

		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ResultSet rset = null;
		ArrayList a = new ArrayList();

		try {
			// on exclut les cours de la Formation des Cadres et Dirigeants qui
			// n'ont pas à apparaître sur ZoneCours
			String requeteSQL1 = "SELECT distinct catalog_nbr FROM ps_n_cour_offer_vw where acad_org <> 'FCD'";
			ps1 = connexionPeopleSoft.prepareStatement(requeteSQL1);
			rset = ps1.executeQuery();

			while (rset.next()) {
				String code = rset.getString(1);
				a.add(code);
			}

			log.debug("Nombre de Cours a inserer dans Plancours Peoplesoft: "
							+ a.size());

			String requeteSQL2 = "INSERT INTO PlanCoursPeopleSoft(CODECOURS) VALUES(?)";
			ps2 = connexionPublication.prepareStatement(requeteSQL2);

			for (int i = 0; i < a.size(); i++) {
				String c = a.get(i).toString();
				ps2.setString(1, c);
				ps2.execute();
			}
			log.debug("Succes tous les " + a.size()
					+ " cours ont ete inseres!");
		} catch (Exception e) {
			outPrint.append("<br>========= Erreur dans copierListeCours() : "
					+ e);
		}

		finally {

			log.debug("In block finally");

			Statement st = connexionPeopleSoft.createStatement();
			st.execute("commit");
			rset.close();
			ps1.close();
			st.close();

			Statement stmt = connexionPublication.createStatement();
			stmt.execute("commit");
			ps2.close();
			stmt.close();

		}
		if (trace)
			outTrace.append("ok");
	}

	// =====================================================================================
	// =====================================================================================
	// ============================= RESSOURCES
	// ============================================
	// =====================================================================================
	// =====================================================================================
	// ---------------------------------------------------
	private boolean testModificationImg(Connection connexion, String prefixe,
			StringBuffer imgId, String appDirName, String docFileName,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------

		if (trace) {
			outTrace.append("<br><br>testModificationImg - entree");
			outTrace.append("<br>&nbsp;&nbsp;koId     :" + imgId);
			outTrace.append("<br>&nbsp;&nbsp;appDirName :" + appDirName);
			outTrace.append("<br>&nbsp;&nbsp;document :" + docFileName);
		}
		boolean result = true;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;

		try {
			java.util.Date dateMajBdD = null;

			requeteSQL = "SELECT imgId, dateMaj FROM ImgZone WHERE url=?";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, prefixe + docFileName);

			rset = ps.executeQuery();

			if (rset.next()) {
				imgId.append(rset.getString(1));
				dateMajBdD = rset.getDate(2);
				long dateMajBdL = dateMajBdD.getTime();

				File docFile = new File(appDirName + File.separator + docFileName);
				long dateMajFichierL = docFile.lastModified();
				long difference = dateMajFichierL - dateMajBdL;
				result = (difference >= 0);
				if (!result && dateMajFichierL == 0)
					result = true;
				if (trace) {
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdD     :"
							+ dateMajBdD);
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdL     :"
							+ dateMajBdL);
					outTrace.append("<br>&nbsp;&nbsp;dateMajFichierL:"
							+ dateMajFichierL);
					outTrace.append("<br>&nbsp;&nbsp;difference     :"
							+ difference);
				}
			}
		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			if (trace)
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
			outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
			outTrace.append("<br>&nbsp;&nbsp;" + e);
			outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
		}

		finally {
			rset.close();
			ps.close();
			if (trace) {
				outTrace.append("<br>testModificationImg - sortie");
			}
		}
		return result;
	}

	// ---------------------------------------------------
	private boolean testModificationDoc(Connection connexion, String koId,
			String appDirName, String docFileName, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		if (trace) {
			outTrace.append("<br><br>testModificationDoc - entree");
			outTrace.append("<br>&nbsp;&nbsp;koId     :" + koId);
			outTrace.append("<br>&nbsp;&nbsp;appDirName :" + appDirName);
			outTrace.append("<br>&nbsp;&nbsp;document :" + docFileName);
		}
		boolean result = true;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;

		try {
			java.util.Date dateMajBdD = null;

			requeteSQL = "SELECT dateMAJ FROM DocZone WHERE koId=?";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			if (rset.next()) {
				dateMajBdD = rset.getDate(1);
				long dateMajBdL = dateMajBdD.getTime();

				File docFile = new File(appDirName + File.separator + docFileName);
				long dateMajFichierL = docFile.lastModified();
				long difference = dateMajFichierL - dateMajBdL;
				result = (difference >= 0);
				if (!result && dateMajFichierL == 0)
					result = true;
				if (trace) {
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdD     :"
							+ dateMajBdD);
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdL     :"
							+ dateMajBdL);
					outTrace.append("<br>&nbsp;&nbsp;dateMajFichierL     :"
							+ dateMajFichierL);
					outTrace.append("<br>&nbsp;&nbsp;difference     :"
							+ difference);
				}
			}
		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			if (trace)
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
			outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
			outTrace.append("<br>&nbsp;&nbsp;" + e);
			outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
		}

		finally {
			rset.close();
			ps.close();
			if (trace) {
				outTrace.append("<br>testModificationDoc - sortie");
			}
		}
		return result;
	}

	// ---------------------------------------------------
	private boolean testModificationTXDoc(Connection connexion, String koId,
			String docFileName, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		if (trace) {
			outTrace.append("<br><br>testModificationDoc - entree");
			outTrace.append("<br>&nbsp;&nbsp;koId     :" + koId);
			outTrace.append("<br>&nbsp;&nbsp;appDirName :" + appDirName);
			outTrace.append("<br>&nbsp;&nbsp;document :" + docFileName);
		}
		boolean result = true;
		String requeteSQL = null;
		String reqDoc = null;
		PreparedStatement psDoc = null;
		ResultSet rsetDoc = null;

		PreparedStatement ps = null;
		ResultSet rset = null;
		Connection conn = null;
		try {

			if (trace)
				outTrace.append(", connecte e Teximus");

			reqDoc = "Select DMODIF from DOCUMENTS where id =?";
			conn = getTeximusConnection(outPrint, outTrace, trace);
			psDoc = conn.prepareStatement(reqDoc);
			psDoc.setString(1, docFileName);

			rsetDoc = psDoc.executeQuery();

			java.util.Date dateDMODIF = null;

			if (rsetDoc.next()) {
				dateDMODIF = rsetDoc.getDate(1);

				// result = (difference>=0);
				// if (!result && dateMajFichierL == 0)
				// result = true;
				// if (trace) {
				// outTrace.append("<br>&nbsp;&nbsp;dateMajBdD     :" +
				// dateDMODIF);
				// outTrace.append("<br>&nbsp;&nbsp;dateMajBdL     :" +
				// dateMajBdL);
				// outTrace.append("<br>&nbsp;&nbsp;dateMajFichierL     :" +
				// dateMajFichierL);
				// outTrace.append("<br>&nbsp;&nbsp;difference     :" +
				// difference);
				// }

			}

			java.util.Date dateMajBdD = null;

			requeteSQL = "SELECT dateMAJ FROM DocZone WHERE koId=?";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			if (rset.next()) {
				dateMajBdD = rset.getDate(1);
				long dateMajBdL = dateMajBdD.getTime();
				long dateMajFichierL = dateDMODIF.getTime();
				long difference = dateMajFichierL - dateMajBdL;
				result = (difference >= 0);
				if (!result && dateMajFichierL == 0)
					result = true;
				if (trace) {
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdD     :"
							+ dateMajBdD);
					outTrace.append("<br>&nbsp;&nbsp;dateMajBdL     :"
							+ dateMajBdL);
					outTrace.append("<br>&nbsp;&nbsp;dateMajFichierL     :"
							+ dateMajFichierL);
					outTrace.append("<br>&nbsp;&nbsp;difference     :"
							+ difference);
				}
			}
		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			if (trace)
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
			outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
			outTrace.append("<br>&nbsp;&nbsp;" + e);
			outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
		}

		finally {
			rset.close();
			psDoc.close();
			rsetDoc.close();
			ps.close();
			if (trace) {
				outTrace.append("<br>testModificationDoc - sortie");
			}
			closeTeximusConnection(conn);
		}

		return result;
	}

	// ---------------------------------------------------
	static public Connection getTeximusConnection(StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		Class.forName("oracle.jdbc.driver.OracleDriver");
		DataSource dsTeximus = (DataSource) new InitialContext()
				.lookup("java:comp/env/jdbc/TXDatasource");
		if (dsTeximus == null)
			throw new ServletException("data source Teximus Edition non trouve");
		
		return dsTeximus.getConnection();
	}

	// ---------------------------------------------------
	private void closeTeximusConnection(Connection conn) throws Exception {
		// ---------------------------------------------------
		if (conn != null)
			conn.close();
	}

	// ---------------------------------------------------
	public void deleteImageFromDB(Connection connexion, String imgId,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace
					.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;deleteImageFromDB - entree");
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		requeteSQL = "DELETE FROM ImgZone WHERE imgId = " + imgId;
		if (trace)
			outTrace.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ requeteSQL + " ...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
		if (trace)
			outTrace
					.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;deleteImageFromDB - sortie");
	}

	// ---------------------------------------------------
	public void deleteRessourceFromDB(Connection connexion, String koId,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace, String nivSecu)
			throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		requeteSQL = " DELETE FROM DocZone WHERE koId = '" + koId + "'";
		if (trace)
			outTrace.append("<br>" + requeteSQL + " ...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
	}

	// ---------------------------------------------------
	public void deleteRessourceSecuriteDB(Connection connexion, String koId,
			String planId, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		Statement stmt = connexion.createStatement();

		requeteSQL = " DELETE FROM DocSecu WHERE koId = '" + koId
				+ "' AND planId='" + planId + "'";
		if (trace)
			outTrace.append("<br>" + requeteSQL + " ...");
		stmt.execute(requeteSQL);
		if (trace)
			outTrace.append(" ok");

		stmt.close();
	}

	// ---------------------------------------------------
	public void insertRessourceSecuriteDB(Connection connexion, String koId,
			String planId, String nivSecu, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQL = null;
		PreparedStatement ps = null;

		try {
			requeteSQL = "INSERT INTO DocSecu (koId, planId, nivSecu) VALUES(?,?,?)";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, planId);
			ps.setString(3, nivSecu);
			ps.execute();
		} catch (Exception e) {
			outPrint.append("Erreur dans insertRessourceSecuriteDB() : " + e);
		}

		finally {
			Statement stmt = connexion.createStatement();
			stmt.execute("commit");
			ps.close();
			stmt.close();
		}
	}

	// ---------------------------------------------------
	private String readImageAndStoreInDB(Connection connexion, String prefixe,
			String appDirName, String docFileName, String ressType,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------

		if (trace) {
			outTrace.append("<br><br>readImageAndStoreInDB - entree :"
					+ new java.util.Date(System.currentTimeMillis()));
			outTrace.append("<br>&nbsp;&nbsp;image :" + docFileName);
		}

		Blob blob;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		String imgId = null;

		try {
			// on verifie si l'image est deje presente
			requeteSQL = "SELECT imgId FROM ImgZone WHERE url=?";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, prefixe + docFileName);

			rset = ps.executeQuery();

			if (rset.next()) {
				imgId = rset.getString(1);
				requeteSQL = "--DELETE FROM imgZone WHERE imgId=" + imgId;
				deleteImageFromDB(connexion, imgId, outPrint, outTrace, trace);
			} else {
				// on recupere le numero suivant pour l'image
				requeteSQL = "SELECT imgZone_seq.nextval FROM Dual";
				ps = connexion.prepareStatement(requeteSQL);
				rset = ps.executeQuery();

				if (rset.next()) {
					if (trace)
						outTrace.append("<br>prefixe:" + prefixe);
					imgId = rset.getString(1)
							+ convertirSessionVersCode(prefixe.substring(0, 5),
									outTrace, trace) + prefixe.substring(5, 6);
				}
			}
			if (trace) {
				outTrace.append("<br>&nbsp;&nbsp;imgId :" + imgId);
			}
			// on insere le tuple pour l'image
			requeteSQL = "INSERT INTO ImgZone (imgId, imgContent, ressType, url, dateMaj) VALUES(?,empty_blob(),?,?,sysdate)";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, imgId);
			ps.setString(2, ressType);
			ps.setString(3, prefixe + docFileName);
			ps.execute();
			requeteSQL = "SELECT imgContent FROM ImgZone WHERE imgId=? FOR UPDATE";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, imgId);

			rset = ps.executeQuery();

			if (rset.next()) {
				blob = rset.getBlob(1);
				OutputStream blobOutput = blob.setBinaryStream(1L);

				File docFile = new File(appDirName + File.separator + docFileName);
				InputStream fileInput = new FileInputStream(docFile);

				byte[] buffer = new byte[10 * 1024];

				int nread = 0; // Number of bytes read
				while ((nread = fileInput.read(buffer)) != -1)
					blobOutput.write(buffer, 0, nread);

				fileInput.close();
				blobOutput.close();
			}
		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			if (trace) {
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
				outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
				outTrace.append("<br>&nbsp;&nbsp;" + e);
				outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
			}
		}

		finally {
			Statement stmt = connexion.createStatement();
			stmt.execute("commit");
			rset.close();
			ps.close();
			stmt.close();
		}

		if (trace) {
			outTrace.append("<br>readImageAndStoreInDB - sortie :"
					+ new java.util.Date(System.currentTimeMillis()));
		}

		return imgId;
	}

	// Unused here - rewritten properly in OsylTransformToZCCOImpl
	// ---------------------------------------------------
	private boolean readFileAndStoreInDB(Connection connexion, String koId,
			String lang, String ressType, String nivSecu, String appDirName,
			String docFileName, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		boolean ok = true;

		if (trace) {
			outTrace.append("<br><br>readFileAndStoreInDB - entree :"
					+ new java.util.Date(System.currentTimeMillis()));
			outTrace.append("<br>&nbsp;&nbsp;koId     :" + koId);
			outTrace.append("<br>&nbsp;&nbsp;lang :" + lang);
			outTrace.append("<br>&nbsp;&nbsp;ressType :" + ressType);
			outTrace.append("<br>&nbsp;&nbsp;nivSecu :" + nivSecu);
			outTrace.append("<br>&nbsp;&nbsp;appDirName :" + appDirName);
			outTrace.append("<br>&nbsp;&nbsp;document :" + docFileName);
		}

		Blob blob;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;
		float size = 0;

		try {

			requeteSQL = "INSERT INTO DocZone (koId, lang, nivSecu, ressType, dateMAJ, docContent) VALUES(?,?,?,?,sysdate,empty_blob())";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, lang);
			ps.setString(3, nivSecu);
			ps.setString(4, ressType);
			ps.execute();
			ps.close();

			requeteSQL = "SELECT docContent FROM DocZone WHERE koId=? FOR UPDATE";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			if (rset.next()) {
				blob = rset.getBlob(1);
				OutputStream blobOutput = blob.setBinaryStream(1L);

				File docFile = new File(appDirName + File.separator + docFileName);
				InputStream fileInput = new FileInputStream(docFile);

				byte[] buffer = new byte[10 * 1024];

				int nread = 0; // Number of bytes read
				while ((nread = fileInput.read(buffer)) != -1)
					blobOutput.write(buffer, 0, nread);

				fileInput.close();
				blobOutput.close();

			}
		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			ok = false;
			if (trace)
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
			outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
			outTrace.append("<br>&nbsp;&nbsp;" + e);
			outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
		}

		finally {
			Statement stmt = connexion.createStatement();
			stmt.execute("commit");
			rset.close();
			ps.close();
			stmt.close();
			if (trace) {
				outTrace.append("<br>readFileAndStoreInDB - sortie :"
						+ new java.util.Date(System.currentTimeMillis()));
			}
		}
		return ok;
	}

	// Unused here - rewritten properly in OsylTransformToZCCOImpl
	// ---------------------------------------------------
	private boolean readTXFileAndStoreInDB(Connection connexion, String koId,
			String lang, String nivSecu, String docId, String extension,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------

		boolean ok = true;

		if (trace) {
			outTrace.append("<br><br>readFileAndStoreInDB - entree :"
					+ new java.util.Date(System.currentTimeMillis()));
			outTrace.append("<br>&nbsp;&nbsp;koId     :" + koId);
			outTrace.append("<br>&nbsp;&nbsp;lang :" + lang);
			outTrace.append("<br>&nbsp;&nbsp;ressType :" + extension);
			outTrace.append("<br>&nbsp;&nbsp;nivSecu :" + nivSecu);
			outTrace.append("<br>&nbsp;&nbsp;appDirName :" + appDirName);
			outTrace.append("<br>&nbsp;&nbsp;document :" + docId);
		}

		log.info("<br><br>readFileAndStoreInDB - entree :"
				+ new java.util.Date(System.currentTimeMillis()));
		log.info("<br>&nbsp;&nbsp;koId     :" + koId);
		log.info("<br>&nbsp;&nbsp;lang :" + lang);
		log.info("<br>&nbsp;&nbsp;ressType :" + extension);
		log.info("<br>&nbsp;&nbsp;nivSecu :" + nivSecu);
		log.info("<br>&nbsp;&nbsp;appDirName :" + appDirName);
		log.info("<br>&nbsp;&nbsp;document :" + docId);

		String reqDoc = null;
		PreparedStatement psDoc = null;
		ResultSet rsetDoc = null;

		PreparedStatement ps = null;
		Connection conn = null;

		Blob blobTXDOC = null;
		String requeteSQL = null;

		try {

			if (trace)
				outTrace.append(", connecte e Teximus");

			reqDoc = "Select stream from DOCUMENTS where id =?";
			conn = getTeximusConnection(outPrint, outTrace, trace);
			psDoc = conn.prepareStatement(reqDoc);
			psDoc.setString(1, docId);

			rsetDoc = psDoc.executeQuery();

			if (rsetDoc.next()) {
				blobTXDOC = rsetDoc.getBlob(1);
			}
			if (trace)
				outTrace.append("<br>------mon  blob objet------" + blobTXDOC);
			if (trace)
				outTrace.append("<br>------mon  blob string------"
						+ blobTXDOC.toString());
			requeteSQL = "INSERT INTO DocZone (koId, lang, nivSecu, ressType, dateMAJ, docContent, filesizeko) VALUES(?,?,?,?,sysdate,?,?)";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);
			ps.setString(2, lang);
			ps.setString(3, nivSecu);
			ps.setString(4, extension);
			ps.setBlob(5, blobTXDOC);
			ps.setFloat(6, blobTXDOC.length() / 1024);
			ps.execute();
			ps.close();

			if (trace)
				outTrace.append("<br>------Longueur du blob------"
						+ blobTXDOC.length());

		} catch (Exception e) {
			outPrint.append("<div class='erreur'>Erreur: " + e + "</div>");
			ok = false;
			if (trace)
				outTrace.append("<br>&nbsp;&nbsp;<i>------Erreur------");
			outTrace.append("<br>&nbsp;&nbsp;" + requeteSQL);
			outTrace.append("<br>&nbsp;&nbsp;" + e);
			outTrace.append("<br>&nbsp;&nbsp;------------------</i>");
		}

		finally {
			Statement stmt = connexion.createStatement();
			stmt.execute("commit");
			ps.close();
			stmt.close();
			if (trace) {
				outTrace.append("<br>readFileAndStoreInDB - sortie :"
						+ new java.util.Date(System.currentTimeMillis()));
			}
			closeTeximusConnection(conn);
		}
		return ok;
	}

	// ---------------------------------------------------
	public void getBlobFromDBandSaveinFile(Connection connexion, String koId,
			String fileName, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		Blob blob;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;

		try {

			requeteSQL = "SELECT docContent FROM DocZone WHERE koId=? FOR UPDATE";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			if (rset.next()) {
				blob = rset.getBlob(1);

				InputStream blobStream = blob.getBinaryStream();
				FileOutputStream fileStream = new FileOutputStream(fileName);

				byte[] buffer = new byte[10 * 1024];
				int nbytes = 0;
				while ((nbytes = blobStream.read(buffer)) != -1)
					fileStream.write(buffer, 0, nbytes);

				fileStream.flush();
				fileStream.close();
				blobStream.close();

			}
		} catch (Exception e) {
			outPrint.append("Erreur dans getBlobFromDBandSaveinFile() : " + e);
		}

		finally {
			rset.close();
			ps.close();
		}
	}

	// ---------------------------------------------------
	public int getBlobFromDBandWriteInResponse(HttpServletResponse response,
			Connection connexion, String koId, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		int fileSize = 0;
		Blob blob;
		String ressType = null;
		String requeteSQL = null;
		PreparedStatement ps = null;
		ResultSet rset = null;

		try {

			requeteSQL = "SELECT ressType, docContent FROM DocZone WHERE koId=? FOR UPDATE";
			ps = connexion.prepareStatement(requeteSQL);
			ps.setString(1, koId);

			rset = ps.executeQuery();

			response.reset();

			if (rset.next()) {
				ressType = rset.getString(1);
				blob =  rset.getBlob(2);

				if (ressType.equals("gif"))
					response.setContentType("image/gif");
				if (ressType.equals("xml"))
					response.setContentType("application/xml");
				if (ressType.equals("html"))
					response.setContentType("text/html");
				if (ressType.equals("ppt"))
					response.setContentType("application/vnd.ms-powerpoint");

				InputStream blobStream = blob.getBinaryStream();
				OutputStream respStream = response.getOutputStream();

				byte[] buffer = new byte[1024];
				int nbytes = 0;
				while ((nbytes = blobStream.read(buffer)) != -1) {
					respStream.write(buffer, 0, nbytes);
					fileSize += nbytes;
				}
				blobStream.close();
				respStream.close();
				response.setContentLength(fileSize);
			}
		} catch (Exception e) {
			outPrint.append("Erreur dans getBlobFromDBandWriteInResponse() : "
					+ e);
		}

		finally {
			rset.close();
			ps.close();
		}
		return fileSize;
	}

	// ---------------------------------------------------
	public int readFileAndWriteInResponse(HttpServletResponse response,
			Connection connexion, String docFileName, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

		int fileSize = 0;

		try {

			File docFile = new File(docFileName);
			InputStream fileInput = new FileInputStream(docFile);

			response.reset();
			response.setContentType("image/gif");
			OutputStream respStream = response.getOutputStream();

			byte[] buffer = new byte[10 * 1024];

			int nread = 0; // Number of bytes read
			while ((nread = fileInput.read(buffer)) != -1) {
				respStream.write(buffer, 0, nread);
				fileSize += nread;
			}

			respStream.close();
			response.setContentLength(fileSize);
			fileInput.close();

		} catch (Exception e) {
			outPrint.append("Erreur dans readFileAndWriteInResponse() : " + e);
		}

		finally {
		}
		return fileSize;
	}

	// ==================================================================================
	// ==================================================================================
	// ============================ PEOPLESOFT
	// ==========================================
	// ==================================================================================
	// ==================================================================================

	// ==================================================================================

	// ==================================================================================

	// ======================================================================
	private String getPlanEnseignant(Connection connexion2, String planCode,
			String trimestreCode, String sessionCode, String groupeCode,
			StringBuffer outPrint) throws Exception {
		// ======================================================================
		String result = "";
		PreparedStatement ps = null;
		try {
			String requeteSQL = null;
			ResultSet rset = null;

			requeteSQL = "SELECT DISTINCT name FROM ps_n_nature_emploi T1, ps_n_crsection_vw T2 WHERE T1.emplid= T2.emplid AND catalog_nbr=? AND strm=? AND session_code=? AND class_section=?";

			ps = connexion2.prepareStatement(requeteSQL);
			ps.setString(1, planCode);
			ps.setString(2, trimestreCode);
			ps.setString(3, sessionCode);
			ps.setString(4, groupeCode);
			rset = ps.executeQuery();

			while (rset.next()) {
				if (!result.equals(""))
					result += " & ";
				result += rset.getString(1);
			}
		} catch (SQLException se) {
			while (se != null) {
				outPrint.append(se.toString() + " -- state=" + se.getSQLState()
						+ " error code=" + se.getErrorCode() + "<br>");
				se = se.getNextException();
			}
		} finally {
			if (ps != null)
				ps.close();
		}
		return result;
	}

	// ======================================================================
	private String[] getPlanProgrammeCreditsServiceExigences(
			Connection connexion2, String planCode, StringBuffer outTrace,
			StringBuffer outPrint) throws Exception {
		// ======================================================================
		String result[] = new String[5];
		PreparedStatement ps = null;
		try {
			String requeteSQL = null;
			ResultSet rset = null;

			requeteSQL = "SELECT ACAD_CAREER, UNITS_MINIMUM,ACAD_ORG, DESCRLONG, COURSE_TITLE_LONG FROM PS_N_COUR_OFFER_VW WHERE CATALOG_NBR = ?";
			ps = connexion2.prepareStatement(requeteSQL);
			ps.setString(1, planCode);
			rset = ps.executeQuery();

			while (rset.next()) {
				result[0] = rset.getString(1);
				result[1] = rset.getString(2);
				result[2] = rset.getString(3);
				Clob clob =  rset.getClob(4);
				result[3] = ClobToString(clob);
				result[4] = rset.getString(5);

			}
		} catch (SQLException se) {
			while (se != null) {
				outPrint.append(se.toString() + " -- state=" + se.getSQLState()
						+ " error code=" + se.getErrorCode() + "<br>");
				se = se.getNextException();
			}
		} finally {
			if (ps != null)
				ps.close();
		}
		return result;
	}

	// ---------------------------------------------------
	public String[] getPlanInfos(Connection connexionPeopleSoft,
			String codeCours, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>getPlanInfos ... ");

		String result[] = new String[5];
		PreparedStatement ps = null;
		try {
			String requeteSQL = null;
			ResultSet rset = null;

			requeteSQL = "SELECT acad_career, units_minimum,acad_org, descrlong, course_title_long FROM PS_N_COUR_OFFER_VW WHERE catalog_nbr = ?";

			ps = connexionPeopleSoft.prepareStatement(requeteSQL);
			ps.setString(1, codeCours);
			rset = ps.executeQuery();

			while (rset.next()) {
				result[0] = rset.getString(1);
				result[1] = rset.getString(2);
				result[2] = rset.getString(3);
				Clob clob =  rset.getClob(4);
				result[3] = ClobToString(clob);
				result[4] = rset.getString(5);
			}
		} catch (Exception e) {
			outPrint.append("<br>========= getPlanInfos() : " + e);
		} finally {
			if (ps != null)
				ps.close();
		}
		if (trace)
			outTrace.append("ok");
		return result;
	}

	// ---------------------------------------------------
	private String convertirSessionVersCode(String ssion,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		String result = null;
		if (trace)
			outTrace.append("<br>---ssion:" + ssion);
		String trimestre = ssion.substring(0, 1);
		String siecle = ssion.substring(1, 3);
		String annee = ssion.substring(3, 5);

		if (siecle.equals("19"))
			result = "1";
		else
			result = "2";

		result += annee;

		if (trimestre.equals("H") || trimestre.equals("h"))
			result += "1";
		else if (trimestre.equals("E") || trimestre.equals("e"))
			result += "2";
		else if (trimestre.equals("A") || trimestre.equals("a"))
			result += "3";

		return result;
	}

	// ---------------------------------------------------
	public String remplacerImg(Connection connexion, String prefixe,
			String textData, String appDirName, boolean force,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------
		int indx = 0;
		String url = null;
		StringBuffer newUrl = new StringBuffer();
		while ((indx = textData.indexOf("src='documents", indx)) != -1) {
			int debut = indx + 5;
			int fin = textData.indexOf("\'", indx + 7);
			url = textData.substring(debut, fin);
			boolean nouveau = testModificationImg(connexion, prefixe, newUrl,
					appDirName, url, outPrint, outTrace, trace);
			if (nouveau || force)
				outTrace.append("&nbsp;(publiee)");
			else
				outTrace.append("&nbsp;(non modifiee)");
			if (nouveau || force) {
				String extension = url.substring(url.lastIndexOf(".") + 1);
				newUrl.append(readImageAndStoreInDB(connexion, prefixe,
						appDirName, url, extension, outPrint, outTrace, trace));
			}
			newUrl.append(".img");
			textData = textData.replaceAll(url, newUrl.toString());
			indx = fin + 1;
			nbImgs++;
		}
		return textData;
	}

	// ---------------------------------------------------
	private void traiterTexte(Connection connexion, String prefixe,
			Document xmlSourceDoc, boolean force, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		nbImgs = 0;
		NodeList textesList = xmlSourceDoc.getElementsByTagName("texte");
		if (textesList != null) {
			int nbTextes = textesList.getLength();
			for (int i = 0; i < nbTextes; i++) {
				Element texte = (Element) textesList.item(i);
				if (texte.hasChildNodes()) {
					NodeList children = texte.getChildNodes();
					if (children.item(0) instanceof org.w3c.dom.CharacterData) {
						org.w3c.dom.CharacterData data = (org.w3c.dom.CharacterData) children
								.item(0);
						String textData = data.getData();
						data.setData(remplacerImg(connexion, prefixe, textData,
								appDirName, force, outPrint, outTrace, trace));
					}
				}
			}
		}
		outPrint.append("<div class='nbDocInts'>Nombre d'images : " + nbImgs
				+ "</div>");
	}

	// ---------------------------------------------------
	public boolean testerNiveauSecurite(String nivSecu, Document xmlSourceDoc,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>testerNiveauSecurite - niveau :" + nivSecu);

		boolean result = false;
		Document xml = newDOM();

		processXSLT(xmlSourceDoc, "testSecurite" + nivSecu + ".xsl", xml,
				outTrace, trace);

		if (getRootName(xml) != null
				&& getRootName(xml).equals("niv" + nivSecu))
			result = true;

		if (trace)
			outTrace.append("... ok");

		return result;
	}

	// ---------------------------------------------------
	public void traiterPlanCours(Connection connexion, String koId,
			String lang, String ressId, String ressType, String nivSecu,
			Document xmlSourceDoc, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------

	    	Document xml = xmlSourceDoc;
	    
		// --- application securite ---
		/*Document xml = newDOM();
		if (trace)
			outTrace
					.append("<hr>================= TraiterPlancours - entree ==========================");

		if (trace)
			outTrace.append("<br>application securite " + nivSecu);
		processXSLT(xmlSourceDoc, "securite" + nivSecu + ".xsl", xml, outTrace,
				trace);*/

		if (xml != null) {
			//nivSecu = ACCESS_PUBLIC or ACCESS_COMMUNITY
			if (nivSecu.equals(PUBLIC_SECURITY_LABEL) || nivSecu.equals(COMMUNITY_SECURITY_LABEL))
				deleteFromPlanZone(connexion, koId, outPrint, outTrace, trace, nivSecu);

			// --- generation presentation ---
			processAndInsertInDB(connexion, koId, lang, ressId, "11", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation coordonnees ---
			processAndInsertInDB(connexion, koId, lang, ressId, "12", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation materiel pedagogique ---
			processAndInsertInDB(connexion, koId, lang, ressId, "13", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation Travaux et examens ---
			processAndInsertInDB(connexion, koId, lang, ressId, "14", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation documents imprimables ---
			processAndInsertInDB(connexion, koId, lang, ressId, "16", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation plan de cours imprimable ---
			processAndInsertInDB(connexion, koId, lang, ressId, "17", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation liste des exercices ---
			processAndInsertInDB(connexion, koId, lang, ressId, "18", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation liste des references ---
			processAndInsertInDB(connexion, koId, lang, ressId, "19", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation archive des nouvelles ---
			processAndInsertInDB(connexion, koId, lang, ressId, "21", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation FAQ ---
			processAndInsertInDB(connexion, koId, lang, ressId, "22", nivSecu,
					xml, outPrint, outTrace, trace);

			// --- generation seances ---
			NodeList listeSeances = xml.getElementsByTagName("seance");
			if (trace)
				outTrace
						.append("<br><br>=============================================");
			if (trace)
				outTrace
						.append("<br>======= Generation seances ==================");
			if (trace)
				outTrace
						.append("<br>=============================================");

			int nbListe = listeSeances.getLength();
			int nbSeances = 0;
			ArrayList nosSpecSeances = new ArrayList();
			ArrayList nosSpecThemes = new ArrayList();
			ArrayList nosSpecTPs = new ArrayList();
			ArrayList nosPartSeances = new ArrayList();
			ArrayList nosPartThemes = new ArrayList();
			ArrayList nosPartTPs = new ArrayList();
			for (int i = 0; i < nbListe; i++) {
				boolean isSeance = false;
				boolean isTheme = false;
				boolean isTP = false;
				boolean isPart = false;
				boolean isSpec = false;
				Element seance = (Element) listeSeances.item(i);
				String seanceId = seance.getAttribute("koId");
				String prov = seance.getAttribute("coursType");
				String type = seance.getAttribute("type");
				String no = seance.getAttribute("no");

				if (!seanceId.equals(""))
					nbSeances++;
				if (type.equals("Seance"))
					isSeance = true;
				else if (type.equals("Theme"))
					isTheme = true;
				else if (type.equals("TP"))
					isTP = true;

				if (prov.equals("partageable"))
					isPart = true;
				if (prov.equals("specifique"))
					isSpec = true;

				if (isSeance && isPart)
					nosPartSeances.add(no);
				if (isSeance && isSpec)
					nosSpecSeances.add(no);
				if (isTheme && isPart)
					nosPartThemes.add(no);
				if (isTheme && isSpec)
					nosSpecThemes.add(no);
				if (isTP && isPart)
					nosPartTPs.add(no);
				if (isTP && isSpec)
					nosSpecTPs.add(no);
			}

			outPrint
					.append("<div class='nbSeances'>Nombre de seances et TPs : "
							+ nbSeances + "</div>");

			String numSeance = "";
			String typeSeance = "";
			String seanceId = "";
			for (int j = 0; j < nbListe; j++) {
				Element seance = (Element) listeSeances.item(j);
				numSeance = seance.getAttribute("no");
				typeSeance = seance.getAttribute("type");
				seanceId = seance.getAttribute("koId");

				// si une seance est nouvelle, elle est de type "ajout"
				if (!nosSpecSeances.contains(numSeance)
						&& nosPartSeances.contains(numSeance))
					seance.setAttribute("coursType", "ajout");
				if (!nosSpecThemes.contains(numSeance)
						&& nosPartThemes.contains(numSeance))
					seance.setAttribute("coursType", "ajout");
				if (!nosSpecTPs.contains(numSeance)
						&& nosPartTPs.contains(numSeance))
					seance.setAttribute("coursType", "ajout");
				String coursType = seance.getAttribute("coursType");
				if (!coursType.equals("partageable")/*
													 * &&
													 * !coursType.equals("retrait"
													 * )
													 */) {
					if (trace)
						outTrace
								.append("<br><br>========================= Seance "
										+ numSeance
										+ " - "
										+ seance.getAttribute("coursType")
										+ " =========================");
					outPrint.append("<div class='seance'>Seance " + numSeance
							+ " :  " + seanceId + "</div>");
					if (nivSecu.equals(PUBLIC_SECURITY_LABEL) || nivSecu.equals(COMMUNITY_SECURITY_LABEL))
						deleteFromPlanZone(connexion, seanceId, outPrint,
								outTrace, trace, nivSecu);

					deleteFromSeance(connexion, seanceId, outPrint, outTrace,
							trace);
					// ---- recherche libelle de la seance pour indexation ----
					String libelleSeance = "";
					NodeList libelles = ((Element) seance)
							.getElementsByTagName("libelle");
					if (libelles.getLength() > 0) {
						Element libelle = (Element) libelles.item(0);
						if (libelle.hasChildNodes()) {
							NodeList children = libelle.getChildNodes();
							if (children.item(0) instanceof org.w3c.dom.CharacterData) {
								libelleSeance = ((org.w3c.dom.CharacterData) children
										.item(0)).getData();
							}
						}
					}
					insertIntoSeance(connexion, seanceId, koId, libelleSeance,
							outPrint, outTrace, trace);

					Transformer transformer = (Transformer) transformersTable
							.get(lang + "-10.xsl");
					transformer.setParameter("typeSeance", typeSeance);
					transformer.setParameter("numSeance", numSeance);

					processAndInsertInDB(connexion, seanceId, lang, ressId,
							"10", nivSecu, xml, outPrint, outTrace, trace);
				}
			}
			// --- generation liste des seances ---
			processAndInsertInDB(connexion, koId, lang, ressId, "15", nivSecu,
					xml, outPrint, outTrace, trace);
		}
		if (trace)
			outTrace
					.append("<br>================= TraiterPlancours - sortie ==========================");
	}

	// ---------------------------------------------------
	public void traiterPlanCoursAnnuaire(Connection connexion, String koId,
			String lang, String ressId, String ressType, String nivSecu,
			Document xml, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		if (trace)
			outTrace.append("<hr>==== TraiterPlanCoursAnnuaire - entree ====");
		if (nivSecu.equals(PUBLIC_SECURITY_LABEL) || nivSecu.equals(COMMUNITY_SECURITY_LABEL))
			deleteFromPlanZone(connexion, koId, outPrint, outTrace, trace, nivSecu);

		// --- generation presentation ---

		processAndInsertInDB(connexion, koId, lang, ressId, "11a", nivSecu,
				xml, outPrint, outTrace, trace);

	}

	// ---------------------------------------------------
	/**
	 * This method makes sure documents are rendered when the course outline is
	 * published. A couple of things have been commented because they are
	 * rewritten in OsylTransformToZCCOImpl
	 *
	 * @param connexion
	 * @param lang
	 * @param xml
	 * @param force
	 * @param outPrint
	 * @param outTrace
	 * @param trace
	 * @return
	 * @throws Exception
	 */
	private boolean traiterDocumentsIntsExts(Connection connexion, String lang,
			Document xml, boolean force, StringBuffer outPrint,
			StringBuffer outTrace, boolean trace, String nivSecu) throws Exception {
		// ---------------------------------------------------
		boolean ok = true;
		// --- generation documents internes ---
		if (trace)
			outTrace
					.append("<br><br>================ generation documents internes =====");

		NodeList docInts = xml.getElementsByTagName("ressource");
		if (docInts.getLength() != 0) {
			int nbDocInts = docInts.getLength();
			outPrint.append("<div class='nbDocInts'>Documents internes</div>");
			Transformer transformer = (Transformer) transformersTable.get(lang
					+ "-20.xsl");
			for (int i = 1; i < nbDocInts + 1; i++) {

				Element ressource = (Element) docInts.item(i - 1);
				if (ressource.getAttribute("type").equals("DocInterne")) {
					String ressourceId = ressource.getAttribute("koId");

					transformer.setParameter("idRessource", ressourceId);

					outPrint.append("<div class='docInt'>Document interne :  "
							+ ressourceId + "</div>");
					deleteFromPlanZone(connexion, ressourceId, outPrint,
							outTrace, trace, nivSecu);
					processAndInsertInDB(connexion, ressourceId, lang,
							"docInt", "20", nivSecu, xml, outPrint, outTrace, trace); //It was "0"
				}
			}
		}

		// --- generation documents externes ---
		ArrayList ressAl = new ArrayList();
		if (trace)
			outTrace
					.append("<br><br>================ generation documents externes =====");
		Document xmlDocExts = newDOM();
		processXSLT(xml, "parseDocExt.xsl", xmlDocExts, outTrace, trace);

		NodeList docExts = xmlDocExts.getElementsByTagName("ressource");
		if (xml != null) {
			int nbDocExts = docExts.getLength();
			outPrint
					.append("<div class='nbDocInts'>Nombre de documents externes : "
							+ nbDocExts + "</div>");
			log.info("Nombre de documents externes : " + nbDocExts
					+ "</div>");

			for (int i = 1; i < nbDocExts + 1; i++) {
				boolean nouveau = true;
				Element ressource = (Element) docExts.item(i - 1);
				String ressourceId = ressource.getAttribute("koId");
				String type = ressource.getAttribute("type");
				//String nivSecu = ressource.getAttribute("securite");  AH it's a parameter from jsp
				String fileName = "<font color='red'>ERREUR - pas de fichier ou d'url associe au document</font>";
				String extension = "inconnu";
				ressAl.add(ressourceId);
				outPrint.append("<div class='docInt'>Document externe :  "
						+ ressourceId);
				if (trace)
					outTrace.append("<br><br>----- document " + i + ": "
							+ ressourceId + " ------------------");

				boolean isTXDoc = "TX_Document".equals(type);
				NodeList urls = ressource.getElementsByTagName("url");
				if (trace)
					outTrace.append("  1");
				if (urls != null) {
					Element url = (Element) urls.item(0);
					if (trace)
						outTrace.append(".2");

					if (url != null && url.hasChildNodes()) {
						if (trace)
							outTrace.append(".3");
						NodeList children = url.getChildNodes();
						if (children.item(0) instanceof org.w3c.dom.CharacterData) {
							if (trace)
								outTrace.append(".4");
							org.w3c.dom.CharacterData text = (org.w3c.dom.CharacterData) children
									.item(0);
							fileName = text.getData();

							if (isTXDoc) {

								String docId = ressourceId.substring(
										ressourceId.lastIndexOf('-') + 1,
										ressourceId.length());

								// C'est un TX_Document
								if (trace)
									outTrace.append(".5");
								nouveau = true;

								// testModificationTXDoc(connexion, ressourceId,
								// docId, outPrint, outTrace, trace);

								if (trace)
									outTrace.append(".6");
								if (nouveau || force)
									outPrint.append("&nbsp;(publie)");
								else
									outPrint.append("&nbsp;(non modifie)");
								if (nouveau || force) {
									// extension=fileName.substring(fileName.lastIndexOf(".")+1);
									// deleteRessourceFromDB(connexion,
									// ressourceId, outPrint, outTrace,
									// trace);
									extension = fileName.substring(fileName
											.lastIndexOf(".") + 1);
									// if (!readTXFileAndStoreInDB(connexion,
									// ressourceId, lang, nivSecu, docId,
									// extension, outPrint, outTrace, trace))
									// ok = false;
								}
								// dans tous les cas on met e jour le niveau de
								// securite pour le document
								// deleteRessourceSecuriteDB(connexion,
								// ressourceId, planId, outPrint, outTrace,
								// trace);
								// insertRessourceSecuriteDB(connexion,
								// ressourceId, planId, nivSecu, outPrint,
								// outTrace, trace);

							} /*
							 *
							 * else if (fileName.length()>9 &&
							 * fileName.substring(0,9).equals("documents")) { if
							 * (trace) outTrace.append(".5"); nouveau =
							 * testModificationDoc(connexion, ressourceId,
							 * appDirName, fileName, outPrint, outTrace, trace);
							 * if (trace) outTrace.append(".6"); if (nouveau ||
							 * force) outPrint.append("&nbsp;(publie)"); else
							 * outPrint.append("&nbsp;(non modifie)"); if
							 * (nouveau || force) {
							 * extension=fileName.substring(
							 * fileName.lastIndexOf(".")+1);
							 * //deleteRessourceFromDB(connexion, ressourceId,
							 * outPrint, outTrace, trace); //if
							 * (!readFileAndStoreInDB(connexion, ressourceId,
							 * lang, extension, nivSecu, appDirName, fileName,
							 * outPrint, outTrace, trace)) //ok = false; } //
							 * dans tous les cas on met e jour le niveau de
							 * securite pour le document
							 * //deleteRessourceSecuriteDB(connexion,
							 * ressourceId, planId, outPrint, outTrace, trace);
							 * //insertRessourceSecuriteDB(connexion,
							 * ressourceId, planId, nivSecu, outPrint, outTrace,
							 * trace); }
							 */
							if (trace)
								outTrace.append("<br><br>+fichier : "
										+ fileName);
						}
					}
					outPrint.append("</div>");
					outPrint
							.append("<div class='docInt'>&nbsp;&nbsp;fichier/url :"
									+ fileName + "</div>");
				}
			}

			// deleteOldDocfromDB(connexion, ressAl,planId, outPrint, outTrace,
			// trace);
		}
		return ok;
	}

	// rewritten in OsylTransformToZCCOImpl
	// ---------------------------------------------------
	public void deleteOldDocfromDB(Connection connexion, ArrayList ressAl,
			String planId, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------
		String requeteSQLs = null;
		String requeteSQLd1 = null;
		Statement stmtD1 = connexion.createStatement();
		String requeteSQLd2 = null;
		Statement stmtD2 = connexion.createStatement();
		String ko = "";

		String toDelete = "";

		requeteSQLs = "select * from doczone dz, docsecu ds where ds.koid=dz.koid and ds.planid= '"
				+ planId + "'";
		PreparedStatement psS = null;

		try {

			ResultSet rsetS = null;
			psS = connexion.prepareStatement(requeteSQLs);
			rsetS = psS.executeQuery();

			while (rsetS.next()) {
				ko = rsetS.getString("KOID");
				if (ressAl.contains(ko)) {
					log.debug("Contient" + ko);
				} else {
					log.debug("Contient pas" + ko);
					toDelete += ",'" + ko + "'";
					log.debug(toDelete);
				}
				log.debug(requeteSQLs + toDelete);
			}
			log.debug(requeteSQLs + toDelete);

		} catch (Exception e) {
			log.debug("========= Erreur dans deleteOldDocfromDB : "
					+ e);
		} finally {
			psS.close();

		}
		if (toDelete != null && !toDelete.equals("")) {
			toDelete = toDelete.substring(1);
			requeteSQLd1 = " DELETE FROM DocZone WHERE koId in (" + toDelete
					+ ")";
			log.debug(requeteSQLd1);
			if (trace)
				outTrace.append("<br>" + requeteSQLd1 + " ...");

			requeteSQLd2 = " DELETE FROM DocSecu WHERE koId in (" + toDelete
					+ ")";
			log.debug(requeteSQLd2);
			if (trace)
				outTrace.append("<br>" + requeteSQLd2 + " ...");

			try {
				stmtD1.execute(requeteSQLd1);

			} catch (Exception e) {
				log.debug("========= Erreur dans deleteOldDocfromDB : "
								+ e);
			} finally {
				stmtD1.execute("commit");
				stmtD1.close();

			}
			try {

				stmtD2.execute(requeteSQLd2);
			} catch (Exception e) {
				log.debug("========= Erreur dans deleteOldDocfromDB : "
								+ e);
			} finally {
				stmtD2.execute("commit");
				stmtD2.close();
			}
			if (trace)
				outTrace.append(" ok");

		}
	}

	// ---------------------------------------------------
	public boolean publierPlanDeCours(Connection connexionPublication,
			Connection connexionPeopleSoft, String koId, Document xmlSourceDoc,
			String ressType, String planType, boolean force,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace, String nivSec)
			throws Exception {
		// ---------------------------------------------------

		if (trace)
			outTrace.append("<br>publierPlanDeCours -- entree");

		boolean ok = true;
		String codeCours = null;
		String sessionCours = null;
		String sectionCours = null;
		String catalogNbr = null;
		String programme = null;
		String service = null;
		String libelleCours = null;
		String libelleCoursPs = null;
		String professeur = null;
		String enseignant = null;
		String periode = null;
		String exigences = null;
		String credit = null;
		// String sessionCoursCode = null;

		if (trace)
			outTrace.append("<br>recherche de la langue du document...");
		String ressId = getRootCourseId(xmlSourceDoc);
		if (trace)
			outTrace.append(" ok");
		// ===============recherche de la langue du document source et modif xsl
		// ======================
		if (trace)
			outTrace.append("<br>recherche de la langue du document...");
		String lang = getRootLang(xmlSourceDoc);
		if (lang.equals(""))
			lang = "fr";
		if (trace)
			outTrace.append(" ok");

		// ---- periode du plan de cours
		if (trace)
			outTrace.append("<br>recherche de la periode...");
		periode = getRootPeriode(xmlSourceDoc);
		if (periode == null || periode.equals(""))
			periode = "x";
		if (trace)
			outTrace.append(" ok");
		// ---- codeCours
		int debut = 0;
		int fin = ressId.indexOf(".", debut);
		if (fin > 0)
			codeCours = ressId.substring(debut, fin);
		else
			codeCours = ressId;
		// ---- sessionCours
		debut = fin + 1;
		fin = ressId.indexOf(".", debut);
		if (fin > 0 && !ressId.startsWith(".pce", debut)) {
			sessionCours = ressId.substring(debut, fin);
			// ---- sessionCoursCode
			/*
			 * sessionCoursCode = sessionCours.substring(1,3); sessionCoursCode
			 * = sessionCoursCode + sessionCours.charAt(4); if
			 * (sessionCours.substring(0,1).equals("H")) sessionCoursCode =
			 * sessionCoursCode + "1"; if
			 * (sessionCours.substring(0,1).equals("E")) sessionCoursCode =
			 * sessionCoursCode + "2"; if
			 * (sessionCours.substring(0,1).equals("A")) sessionCoursCode =
			 * sessionCoursCode + "3";
			 */
		}
		// log.info("Code session2: "+sessionCoursCode);
		// ---- section
		debut = fin + 1;
		fin = ressId.indexOf(".", debut);
		if (fin > 0 && !ressId.startsWith(".pce", debut))
			sectionCours = ressId.substring(debut, fin);

		//Pour les cours de MBA, il se pourrait qu'à ce niveau ce soit la période qu'on a récupéré
		//on recommence pour avoir la section qui est à la fin de la String
		if(sectionCours.length()==2){
			debut = fin + 1;
			fin = ressId.indexOf(".", debut);
			if (fin > 0 && !ressId.startsWith(".pce", debut))
				sectionCours = ressId.substring(debut, fin);
		}

		// ---- programme, service, credit et exigences
		if (trace)
			outTrace
					.append("<br>recherche programme, service, credit et exigences ... ");
		String[] ppcs = getPlanProgrammeCreditsServiceExigences(
				connexionPeopleSoft, codeCours, outTrace, outPrint);
		programme = ppcs[0];
		credit = ppcs[1];
		service = ppcs[2];
		exigences = ppcs[3];
		libelleCoursPs = ppcs[4];

		if (trace)
			for (int i = 0; i < 4; i++)
				outTrace.append("|" + ppcs[i] + "|");
		if (programme != null)
			createAndSetAttribute(xmlSourceDoc, "programme", programme);
		if (service != null)
			createAndSetAttribute(xmlSourceDoc, "service", service);
		if (credit != null)
			createAndSetAttribute(xmlSourceDoc, "credit", credit);
		if (planType.equals("annuaire") && exigences != null
				&& exigences.length() > 0) {
			Element eltRoot = xmlSourceDoc.getDocumentElement();
			Element eltPrealable = xmlSourceDoc.createElement("prealables");
			eltRoot.appendChild(eltPrealable);
			CDATASection cdata = xmlSourceDoc.createCDATASection(exigences);
			eltPrealable.appendChild(cdata);
		}
		if (trace)
			outTrace.append(" ok");
		// ---- libelleCours
		if (trace)
			outTrace.append("<br>recherche libelleCours ... ");
		libelleCours = getPlanLibelle(xmlSourceDoc);
		if (trace)
			outTrace.append(" ok");

		// on s'assure que le libelle vient de Peoplesoft au cas oe le titre du
		// cours aurait change

		setPlanLibelle(xmlSourceDoc, libelleCoursPs);

		if (!planType.equals("annuaire")) {
			// ---- professeur
			if (trace)
				outTrace.append("<br>recherche professeur ... ");
			professeur = getPlanProfesseur(xmlSourceDoc);
			if (trace)
				outTrace.append(" ok");
			// ---- enseignant
			if (trace)
				outTrace.append("<br>recherche enseignant ... ");
			enseignant = getPlanEnseignant(connexionPeopleSoft, codeCours,
					convertirSessionVersCode(sessionCours, outTrace, trace),
					periode, sectionCours, outPrint);
			if (enseignant != null)
				createAndSetAttribute(xmlSourceDoc, "enseignant", enseignant);
			if (trace)
				outTrace.append(" ok");
		}
		// ==============================================

		outPrint.append("<div class='ressource'>Plan de cours : " + ressId
				+ "</div>");
		outPrint.append("<div class='ressource'>Code du cours : " + codeCours
				+ "</div>");
		outPrint.append("<div class='ressource'>libelle du cours : "
				+ libelleCours + "</div>");
		outPrint
				.append("<div class='ressource'>libelle du cours de PeopleSoft : "
						+ libelleCoursPs + "</div>");
		outPrint.append("<div class='ressource'>Session : " + sessionCours
				+ "</div>");
		outPrint.append("<div class='ressource'>Section : " + sectionCours
				+ "</div>");
		outPrint.append("<div class='ressource'>Periode : " + periode
				+ "</div>");
		outPrint.append("<div class='ressource'>Programme : " + programme
				+ "</div>");
		outPrint.append("<div class='ressource'>Service : " + service
				+ "</div>");
		outPrint.append("<div class='ressource'>Langue : " + lang + "</div>");
		outPrint.append("<div class='ressource'>Professeur : " + professeur
				+ "</div>");
		outPrint.append("<div class='ressource'>Enseignant : " + enseignant
				+ "</div>");
		outPrint.append("<div class='ressource'>Type : " + planType + "</div>");
		if (koId.startsWith("2004", 1))
			deleteFromPlanCours(connexionPublication, koId.substring(koId
					.lastIndexOf("-") + 1), lang, outPrint, outTrace, trace);
		if (nivSec.equals(PUBLIC_SECURITY_LABEL)) {
			//There is a PlanCours for all "nivsec"
			deleteFromPlanCours(connexionPublication, koId, lang, outPrint,	outTrace, trace);
			if (enseignant != null)
				insertIntoPlanCours(connexionPublication, koId, lang, codeCours,
						sessionCours, sectionCours, periode, service, programme,
						libelleCours, enseignant, planType, outPrint, outTrace,
						trace);
			else
				insertIntoPlanCours(connexionPublication, koId, lang, codeCours,
						sessionCours, sectionCours, periode, service, programme,
						libelleCours, professeur, planType, outPrint, outTrace,
						trace);
		}

		//String nivSecu = PUBLIC_SECURITY_LABEL;

		// ============== remplacement des images dans les textes
		// ======================================
		if (trace)
			outTrace.append("<hr>Remplacement des images dans les textes...");
		String prefixe = sessionCours;
		if (periode.startsWith("P"))
			prefixe += periode.substring(1, 2);
		else
			prefixe += "0";
		if (trace)
			outTrace.append("<br>Prefixe:" + prefixe + "|" + periode);
		traiterTexte(connexionPublication, prefixe, xmlSourceDoc, force,
				outPrint, outTrace, trace);
		if (trace)
			outTrace.append(" ok<hr>");
		if (planType.equals("specifique")) {
			//This part was changed to publish niveau by niveau (not more)
			// --------- securite : niveau 0--------------------------
			outPrint.append("<div class='securite'>Securite : niveau " + nivSec + "</div>");
			//nivSecu = PUBLIC_SECURITY_LABEL;
			traiterPlanCours(connexionPublication, koId, lang, ressId,
					ressType, nivSec, xmlSourceDoc, outPrint, outTrace, trace);
			// --------- securite : niveau 1--------------------------
			//outPrint.append("<div class='securite'>Securite : niveau 1 </div>");
			//nivSecu = COMMUNITY_SECURITY_LABEL;
			//if (testerNiveauSecurite(nivSecu, xmlSourceDoc, outTrace, trace))
			//	traiterPlanCours(connexionPublication, koId, lang, ressId,
			//			ressType, nivSecu, xmlSourceDoc, outPrint, outTrace,
			//			trace);
			// --------- securite : niveau 2--------------------------
			//outPrint.append("<div class='securite'>Securite : niveau 2 </div>");
			//nivSecu = ATTENDEE_SECURITY_LABEL;
			//if (testerNiveauSecurite(nivSecu, xmlSourceDoc, outTrace, trace))
			//	traiterPlanCours(connexionPublication, koId, lang, ressId,
			//			ressType, nivSecu, xmlSourceDoc, outPrint, outTrace,
			//			trace);
			// --------- documents --------------------------
			outPrint.append("<div class='securite'>Documents </div>");
			if (trace)
				outTrace
						.append("<hr> ======================================= DOCUMENTS ===========================<hr>");
			if (!traiterDocumentsIntsExts(connexionPublication, lang,
					xmlSourceDoc, force, outPrint, outTrace, trace, nivSec))
				ok = false;
			;
			// -------------------------------------------------------
		}
		//It had nivSecu
		if (planType.equals("annuaire")) {
			traiterPlanCoursAnnuaire(connexionPublication, koId, lang, ressId,
					ressType, nivSec, xmlSourceDoc, outPrint, outTrace, trace);
		}

		if (trace)
			outTrace.append("<br>publierPlanDeCours -- sortie");
		return ok;
	}

	// ---------------------------------------------------
	private void insererPartageable(Connection connexionEdition,
			Document xmlSourceDoc, String partageableId, String langue,
			StringBuffer outTrace, boolean trace) throws Exception {
		// ---------------------------------------------------
		if (trace)
			outTrace.append("<br>insererPartageable -- entree");
		if (trace)
			outTrace.append("<br>partageableId=" + partageableId);
		// ===============chargement du document source
		// ========================================

		if (trace)
			outTrace.append("<br>lecture du document xml :" + partageableId
					+ " (" + langue + ")...");
		Document xmlPartageable = buildDOM(readXmlString(connexionEdition,
				partageableId, langue, outTrace, trace));
		if (trace)
			outTrace.append(" ok");

		DocumentFragment aInserer = xmlSourceDoc.createDocumentFragment();

		NodeList liste = xmlPartageable.getDocumentElement().getChildNodes();
		int nbListe = liste.getLength();
		if (trace)
			outTrace.append("<br> nbListe=" + nbListe);
		for (int i = 0; i < nbListe; i++) {
			aInserer.appendChild(xmlSourceDoc.importNode(liste.item(i), true));
		}

		xmlSourceDoc.getFirstChild().insertBefore(aInserer,
				xmlSourceDoc.getFirstChild().getFirstChild());
		if (trace)
			outTrace.append("<br>insererPartageable -- sortie");
	}

	// ---------------------------------------------------
	public boolean chargerTraiter(Connection connexionPublication,
			Connection connexionPeopleSoft, String koId, String langue,
			boolean force, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace, String nivSec) throws Exception {
		// ---------------------------------------------------
		log.debug("dans chargertraiter");
		boolean ok = true;

		outPrint.append("<div class='ressource'>Ressource : " + koId + " ("
				+ langue + ")</div>");

		if (trace)
			outTrace.append("<br>chargerTraiter -- entree");
		// ===============chargement du document source
		// ========================================
		log.debug("<br>chargerTraiter -- entree");
		if (trace)
			outTrace.append("<br>lecture du document xml :" + koId + " ("
					+ langue + ")...");
		log.debug("<br>lecture du document xml :" + koId + " ("
				+ langue + ")...");
		String xmlString = readXmlString(connexionPublication, koId, langue,
				outTrace, trace);
		if (trace)
			outTrace.append(" ok");
		log.debug(" ok");

		if (trace)
			outTrace.append("<br>construction du DOM...");
		log.trace("<br>construction du DOM...+" + xmlString);
		if (null != xmlString && !("".equals(xmlString))) {
			Document xmlSourceDoc = buildDOM(xmlString);

			if (trace)
				outTrace.append(" ok");
			log.debug("ok");

			if (trace)
				outTrace.append("<br>recherche dateMaj...");
			log.debug("<br>recherche dateMaj...");
			String xmlDateMaj = readXmlDateMaj(connexionPublication, koId,
					langue, outTrace, trace);
			if (trace)
				outTrace.append(" ok");
			log.debug(" ok");

			if (trace)
				outTrace.append("<br>insertion dateMaj dans le xml...");
			log.debug("<br>insertion dateMaj dans le xml...");
			createAndSetAttribute(xmlSourceDoc, "dateMaj", xmlDateMaj);

			if (trace)
				outTrace.append(" ok");
			log.debug(" ok");

			// ===============recherche du type du document source
			// ========================================
			if (trace)
				outTrace.append("<br>recherche du type du document source...");
			log.debug("<br>recherche du type du document source...");
			String ressType = getRootName(xmlSourceDoc);
			if (trace)
				outTrace.append(" ok");
			log.debug(" ok");

			if (ressType.equals("planCours")) {

				String planType = null;

				// ---- type de plan de cours
				if (trace)
					outTrace.append("<br>recherche type plan de cours...");
				planType = getRootType(xmlSourceDoc);
				if (planType == null || planType.equals(""))
					planType = "specifique";
				if (trace)
					outTrace.append("(" + planType + ") ok");

				// -- recherche si partages --
				if (planType.equals("partageable")) {
					if (trace)
						outTrace.append("<br>recherche partages...");
					NodeList liste = getPlanPartages(xmlSourceDoc);
					int nbListe = liste.getLength();
					if (trace)
						outTrace.append("<br> nbListe Partages=" + nbListe);
					if (nbListe > 0) {
						for (int i = 0; i < nbListe; i++) {
							if (!chargerTraiter(connexionPublication,
									connexionPeopleSoft, ((Element) liste
											.item(i)).getAttribute("koId"),
									langue, force, outPrint, outTrace, trace, nivSec))
								ok = false;
							;
						}
						if (trace)
							outTrace.append(" ok");
					}
				}

				if (planType.equals("specifique")) {

					// -- recherche si partagable e inclure --
					if (trace)
						outTrace.append("<br>recherche si partageable...");
					NodeList listePartageables = getPlanPartageables(xmlSourceDoc);
					int nbListePartageables = listePartageables.getLength();
					if (trace)
						outTrace.append("<br> nbListe Partageables="
								+ nbListePartageables);
					if (nbListePartageables > 0) {
						for (int i = 0; i < nbListePartageables; i++) {
							insererPartageable(connexionPublication,
									xmlSourceDoc, ((Element) listePartageables
											.item(i)).getAttribute("koId"),
									langue, outTrace, trace);
						}
					}
					if (trace)
						outTrace.append(" ok");

					if (!publierPlanDeCours(connexionPublication,
							connexionPeopleSoft, koId, xmlSourceDoc, ressType,
							planType, force, outPrint, outTrace, trace, nivSec))
						ok = false;
					;
				}

				if (planType.equals("annuaire")) {
					log.debug(" on publie");
					if (!publierPlanDeCours(connexionPublication,
							connexionPeopleSoft, koId, xmlSourceDoc, ressType,
							planType, force, outPrint, outTrace, trace, nivSec))
						ok = false;
				}
			}
			if (trace) {
				outTrace.append("<br>chargerTraiter -- sortie");
				outTrace.append("<hr size='2'><xmp>");
				nbSpaces = 0;
				outTrace.append(dom2string(xmlSourceDoc));
				outTrace.append("</xmp><hr>");
			}
		} else {
			log.error("String XML NON TROUVeE" + koId);
		}
		return ok;
	}

	// ---------------------------------------------------
	private void supprimerPlanCours(Connection connexion, String koId,
			String lang, String ressId, String nivSecu, Document xmlSourceDoc,
			StringBuffer outPrint, StringBuffer outTrace, boolean trace)
			throws Exception {
		// ---------------------------------------------------

		deleteFromPlanZone(connexion, koId, outPrint, outTrace, trace, nivSecu);

		// --- suppression seances ---
		NodeList seances = xmlSourceDoc.getElementsByTagName("seance");
		if (xmlSourceDoc != null) {
			int nbSeances = seances.getLength();
			outPrint.append("<div class='nbSeances'>Nombre de seances : "
					+ nbSeances + "</div>");

			for (int i = 1; i < nbSeances + 1; i++) {
				Element seance = (Element) seances.item(i - 1);
				String seanceId = seance.getAttribute("koId");

				outPrint.append("<div class='seance'>Seance " + i + " :  "
						+ seanceId + "</div>");
				deleteFromPlanZone(connexion, seanceId, outPrint, outTrace,
						trace, nivSecu);
			}
		}

		// --- suppression documents internes ---

		NodeList listeElts = xmlSourceDoc.getElementsByTagName("ressource");
		if (listeElts.getLength() > 0) {
			int nbElts = listeElts.getLength();
			for (int i = 1; i < nbElts + 1; i++) {
				Element elt = (Element) listeElts.item(i - 1);
				String ressourceId = elt.getAttribute("koId");
				String ressType = elt.getAttribute("type");
				if (ressType.equals("DocInterne")) {
					outPrint.append("<div class='docInt'>Document interne :  "
							+ ressourceId + "</div>");
					deleteFromPlanZone(connexion, ressourceId, outPrint,
							outTrace, trace, nivSecu);
				}
			}
		}

	}

	// ---------------------------------------------------
	public void depublier(Connection connexionPublication, String koId,
			String lang, StringBuffer outPrint, StringBuffer outTrace,
			boolean trace) throws Exception {
		// ---------------------------------------------------

		outPrint.append("<html>");
		outPrint.append("<head>");
		outPrint.append("<title>Suppression</title>");
		outPrint
				.append("	<link name='css' rel='stylesheet' TYPE='text/css' href='css/attente.css' title='default'/>");
		outPrint.append("</head>");
		outPrint.append("<body>");
		outPrint.append("<div id='mainPage'>");
		outPrint.append("<div id='resultat'>");
		outPrint.append("<div class='titre'>Rapport de Suppression</div>");

		// =============================================

		java.util.Date dateDeb;
		java.util.Date dateFin;

		String ressId = null;

		dateDeb = new java.util.Date(System.currentTimeMillis());
		if (trace)
			outTrace.append("debut: " + dateDeb.toString() + "<br>");

		outPrint
				.append("<div class='ressource'>Ressource : " + koId + "</div>");

		Class.forName("oracle.jdbc.driver.OracleDriver");

		try {

			// ==================CONNEXION===================================
			connexionPublication.setAutoCommit(false);
			// ==================================================================
			if (trace)
				outTrace.append("<br>connexion etablie :"
						+ new java.util.Date(System.currentTimeMillis()));

			// ===============chargement du document source
			// ========================================
			if (trace)
				outTrace
						.append("<br>lecture du document xml :" + koId + "<br>");
			String xmlString = readXmlString(connexionPublication, koId, lang,
					outTrace, trace);
			if (trace)
				outTrace.append("<br>construction du DOM");
			Document xmlSourceDoc = buildDOM(xmlString);

			// ===============recherche du type du document source
			// ========================================
			if (trace)
				outTrace.append("<br>recherche du type du document source");
			String ressType = getRootName(xmlSourceDoc);

			if (ressType.equals("planCours")) {
				ressId = getRootCourseId(xmlSourceDoc);

				outPrint.append("<div class='ressource'>Plan de cours : "
						+ ressId + "</div>");

				String nivSecu = ""; // ATTENDEE_SECURITY_LABEL; // pour tout voir

				deleteFromPlanCours(connexionPublication, koId, lang, outPrint,
						outTrace, trace);
				supprimerPlanCours(connexionPublication, koId, lang, ressId,
						nivSecu, xmlSourceDoc, outPrint, outTrace, trace);

			}
			dateFin = new java.util.Date(System.currentTimeMillis());
			if (trace)
				outTrace.append("<br>" + "fin: " + dateFin.toString());

			outPrint
					.append("<div class='duree'>Duree: "
							+ (((double) dateFin.getTime() - (double) dateDeb
									.getTime()) / 1000) + " secondes</div>");
		}

		catch (Exception e) {
			outPrint.append("Erreur : " + e);
		}

		finally {
			outPrint.append("</div><!-- resultat -->");
			outPrint.append("</div><!-- mainPage -->");
			outPrint.append("</body>");
			outPrint.append("</html>");
		}

	}

	public String ClobToString(Clob cl) throws IOException, SQLException {
		if (cl == null)
			return "";

		StringBuffer strOut = new StringBuffer();
		String aux;

		// We access to stream, as this way we don't have to use the
		// CLOB.length() which is slower...
		BufferedReader br = new BufferedReader(cl.getCharacterStream());

		while ((aux = br.readLine()) != null)
			strOut.append(aux);

		return strOut.toString();
	}

}
