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
package org.sakaiquebec.opensyllabus.common.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.GroupAwareEntity.AccessMode;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiquebec.opensyllabus.common.api.OsylTransformToZCCO;
import org.sakaiquebec.opensyllabus.common.helper.FileHelper;
import org.sakaiquebec.opensyllabus.common.helper.XmlHelper;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylTransformToZCCOImpl implements OsylTransformToZCCO {

    private static Log log = LogFactory.getLog(OsylTransformToZCCOImpl.class);

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

    private boolean writeInXmlZC(String zcco, String koId, String lang) {
	boolean written = false;
	PreparedStatement ps = null;
	ResultSet rSet = null;

	String requete =
		"insert into plancoursxml (koId,xml,dateMAJ,lang) VALUES(?,empty_clob(),sysdate,?)";
	try {
	    ps = getConn().prepareStatement(requete);
	    ps.setString(1, koId);
	    ps.setString(2, lang);
	    rSet = ps.executeQuery();
	    written = true;
	    rSet.close();

	} catch (SQLException e) {
	    e.printStackTrace();
	}

	return written;

    }

    private String transform(String osylCo) {
	String zcco = null;

	String xslt =
		FileHelper.getFileContent(XSLT_FILE_PATH + File.separator
			+ XSLT_FILE_NAME);
	try {
	    zcco = XmlHelper.applyXsl(osylCo, xslt);
	} catch (Exception e) {
	    log.error("Unable to transform XML", e);
	}
	return zcco;
    }

    /*
     * We retrieve the documents in the folder publish that have the security
     * level public
     */
    private boolean writhDocumentsInZC(String siteId) {
	boolean written = false;
	AccessMode access = null;
	String lang = null;
	String ressType = null;

	String publishCollId =
		PUBLISH_COLL_PREFIX + siteId + PUBLISH_COLL_SUFFIX;

	ContentCollection publishColl = null;

	try {
	    publishColl = contentHostingService.getCollection(publishCollId);
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	} catch (TypeException e) {
	    e.printStackTrace();
	} catch (PermissionException e) {
	    e.printStackTrace();
	}

	List<ContentResource> resources =
		contentHostingService.getAllResources(publishCollId);

	for (ContentResource resource : resources) {
	    access = resource.getAccess();

	    // TODO: traiter les documents de facon recursive pour prendre en
	    // compte les dossiers
	    // if public document we proceed

	}

	return written;
    }

    /** {@inheritDoc} */
    public boolean sendXml(String siteId, String osylCoXml) {
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

	// We save the course outline in the zonecours database
	sent = writeInXmlZC(zcco, koId, lang);

	// We save the documents in the zonecours database
	sent = sent && writhDocumentsInZC(siteId);

	return sent;
    }

}
