//FILE HEC ONLY SAKAI-2723, SAKAI-2974

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
package org.sakaiquebec.opensyllabus.common.impl.portal.publish3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

import org.w3c.dom.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiquebec.opensyllabus.common.api.portal.publish3.ZCPublisherService;
import org.sakaiquebec.opensyllabus.common.impl.portal.javazonecours.Publication;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class ZCPublisherServiceImpl implements ZCPublisherService {

	private static Log log = LogFactory.getLog(ZCPublisherServiceImpl.class);

	private Map attributeMap;

	String urlConn = null;

	public void init() {
		log.info("INIT from ZCPublisherImpl");
	}

	public void publier(String koId, String langue, String nivSec) {

		if (koId != null && koId.length() > 0 && langue != null)
			try {
				conversion(koId, langue, nivSec, "2", null, null, null);
			} catch (MalformedURLException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@SuppressWarnings("unchecked")
	public void conversion(String xkoId, String xlangue, String xnivSec,
			String xc, String xforce, String xtrace, String xn)
			throws Exception {
		// There are dummy parameters because they were used in zcPublier3.jsp
		String koId = xkoId;
		String langue = xlangue;
		String nivSec = xnivSec;

		// ------------ FORCE -----------
		boolean force = false;
		String frc = xforce;
		if (frc != null && frc.equals("1"))
			force = true;

		// ------------ TRACE -----------
		boolean trace = false;
		String tr = xtrace;
		if (tr != null && tr.equals("1"))
			trace = true;
		// -------------------------------
		boolean ok = true;
		StringBuffer outPrint = new StringBuffer();
		StringBuffer outTrace = new StringBuffer();

		Publication publication = new Publication();

		// ================ chargement des xslt =============
		int n = 999;

		if (xn != null)
			n = Integer.parseInt(xn);

		String xsltDirName = ServerConfigurationService
				.getString("osyl.to.zc.file.directory")
				+ "/xml2html/";

		attributeMap = new HashMap();

		// Original xsltDirName is not used
		// String xsltDirName = getServletContext().getRealPath("/")
		// + File.separator + "publish3" + File.separator + "xslt"
		// + File.separator;

		String[] xsltNamesTable = { "securite0.xsl" // 0
				, "securite1.xsl" // 1
				, "securite2.xsl" // 2
				, "testSecurite1.xsl" // 3
				, "testSecurite2.xsl" // 4
				, "parseDocInt.xsl" // 5
				, "parseDocExt.xsl" // 6
				, "fr-10.xsl" // 7
				, "fr-11.xsl" // 8 Présentation
				, "fr-11a.xsl" // 9 Présentation Annuaire
				, "fr-12.xsl" // 10 Coordonnées
				, "fr-13.xsl" // 11
				, "fr-14.xsl" // 12
				, "fr-15.xsl" // 13
				, "fr-16.xsl" // 14
				, "fr-17.xsl" // 15
				, "fr-18.xsl" // 16
				, "fr-19.xsl" // 17
				, "fr-20.xsl" // 18
				, "fr-21.xsl" // 19
				, "fr-22.xsl", "en-10.xsl" // 20
				, "en-11.xsl" // 21
				, "en-11a.xsl" // 22
				, "en-12.xsl" // 23
				, "en-13.xsl" // 24
				, "en-14.xsl" // 25
				, "en-15.xsl" // 26
				, "en-16.xsl" // 27
				, "en-17.xsl" // 28
				, "en-18.xsl" // 29
				, "en-19.xsl" // 30
				, "en-20.xsl" // 31
				, "en-21.xsl" // 32
				, "en-22.xsl", "es-10.xsl" // 33
				, "es-11.xsl" // 34
				, "es-11a.xsl" // 35
				, "es-12.xsl" // 36
				, "es-13.xsl" // 37
				, "es-14.xsl" // 38
				, "es-15.xsl" // 39
				, "es-16.xsl" // 40
				, "es-17.xsl" // 41
				, "es-18.xsl" // 42
				, "es-19.xsl" // 43
				, "es-20.xsl" // 44
				, "es-21.xsl" // 45
				, "es-22.xsl" };

		publication.transformersTable = new Hashtable();

		for (int i = 0; i < xsltNamesTable.length; i++) {
			Document xslt = null;
			String xsltName = xsltNamesTable[i];
			if (attributeMap.get(xsltName) == null || i == n || n == 9999) { // ||
				// application.getAttribute(xsltName)
				// ==
				// null
				String xsltFileName = xsltName;
				if (xsltName.indexOf("-") > 0) {
					String lang = xsltName.substring(0, 2);
					xsltFileName = xsltName.substring(3, xsltName.length());
					xslt = publication.loadDOM(xsltDirName + xsltFileName);
					publication.majLang(xslt, lang);
				} else {
					xslt = publication.loadDOM(xsltDirName + xsltFileName);
				}
				// application.setAttribute(xsltName,publication.createTransformer(xslt,
				// xsltDirName));
				attributeMap.put(xsltName, publication.createTransformer(xslt,
						xsltDirName));

				log.info("création transformer " + xsltName + " ");
				log.info("Recompilation des fichiers" + " ");
			}
			publication.transformersTable.put(xsltName, attributeMap
					.get(xsltName));
		}

		java.util.Date dateDeb;
		java.util.Date dateFin;
		dateDeb = new java.util.Date(System.currentTimeMillis());

		if (trace)
			outTrace.append("<br>début: " + dateDeb.toString());
		if (trace)
			outTrace.append("<br>appDirName : " + publication.appDirName);
		outPrint.append("<div class='titre'>Rapport de publication</div>");

		Connection connexionPublication = null;

		Connection connexionPeopleSoft = null;

		try {
			String driverName = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.driver.name");
			String url = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.url");
			String user = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.user");
			String password = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.password");

			try {
				Class.forName(driverName);
				connexionPublication = DriverManager.getConnection(url, user,
						password);
				connexionPublication.setAutoCommit(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			driverName = ServerConfigurationService
					.getString("hec.peoplesoft.conn.portail.driver.name");
			url = ServerConfigurationService
					.getString("hec.peoplesoft.conn.portail.url");
			user = ServerConfigurationService
					.getString("hec.peoplesoft.conn.portail.user");
			password = ServerConfigurationService
					.getString("hec.peoplesoft.conn.portail.password");

			try {
				Class.forName(driverName);
				connexionPeopleSoft = DriverManager.getConnection(url, user,
						password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// ==================================================================
			if (trace)
				outTrace.append("<br>connexions établies :"
						+ new java.util.Date(System.currentTimeMillis()));

			ok = publication.chargerTraiter(connexionPublication,
					connexionPeopleSoft, koId, langue, force, outPrint,
					outTrace, trace, nivSec);

			dateFin = new java.util.Date(System.currentTimeMillis());
			outPrint
					.append("<div class='duree'>Durée: "
							+ (((double) dateFin.getTime() - (double) dateDeb
									.getTime()) / 1000) + " secondes</div>");

		} catch (Exception e) {
			outPrint.append("Erreur : " + e);
		}

		finally {
			if (connexionPublication != null)
				connexionPublication.close();
			if (connexionPeopleSoft != null)
				connexionPeopleSoft.close();
			if (!ok)
				log
						.info("<br><font color='red'><i>Une erreur s'est produite. Voir ci-dessous</i></font><br><br>");
			log.info(outPrint.toString());
			if (trace) {
				log.info("<br><br><div id='trace'>");
				log.info(outTrace.toString());
				log.info("</div>");
			}
		}
	}

	public void depublier(String xfile, String xlang) {
		StringBuffer outTrace = new StringBuffer();
		StringBuffer outPrint = new StringBuffer();
		// There are dummy parameters because they were used in zcDepublier3.jsp
		// ------------ TRACE -----------
		boolean trace = false;
		String tr = null;
		if (tr != null && tr.equals("1"))
			trace = true;
		else
			trace = false;
		// -------------------------------

		String file = xfile;
		String lang = xlang;

		Publication publication = new Publication();

		Connection connexionPublication = null;

		try {
			String driverName = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.driver.name");
			String url = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.url");
			String user = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.user");
			String password = ServerConfigurationService
					.getString("hec.zonecours.conn.portail.password");

			Class.forName(driverName);

			connexionPublication = DriverManager.getConnection(url, user,
					password);

			publication.depublier(connexionPublication, file, lang, outPrint,
					outTrace, trace);
		}

		catch (Exception e) {
			outPrint.append("Erreur : " + e);
		}

		finally {
			if (connexionPublication != null)
				try {
					connexionPublication.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			log.info(outPrint.toString());
			if (trace) {
				log.info("<br><br><div id='trace'>");
				log.info(outTrace.toString());
				log.info("</div>");
			}
		}
	}
}
