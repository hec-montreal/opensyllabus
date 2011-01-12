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
package org.sakaiquebec.opensyllabus.common.helper;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class SchemaHelper {

    private static Log log = LogFactory.getLog(SchemaHelper.class);
    public static final String SCHEMA_DIRECTORY = "schema";
    public static final String SCHEMA_FILENAME = "osyl.xsd";
    private static final String VERSION_ATTRIBUTE = "version";
    private static final String XML_VERSION_ATTRIBUTE = "schemaVersion";
    private static final String CONVERSION_DIRECTORY = "conversion";
    private static final String CONVERSION_FILENAME_TEMPLATE =
	    "from{0}to{1}.xsl";
    private static final String UPDATE_VERSION_FILENAME = "updateVersion.xsl";

    private String webappdir;
    private String xsd;

    public SchemaHelper(String webappdir) {
	this.webappdir = webappdir;
	this.xsd = getXsd();
    }

    public String getSchemaVersion() {
	Document doc = XmlHelper.parseXml(xsd);
	return doc.getDocumentElement().getAttribute(VERSION_ATTRIBUTE);
    }

    private String getXMLSchemaVersion(String xml) {
	String schemaVersion = "";
	Document messageDom = null;
	if (xml != null) {
	    try {
		// XMLtoDOM
		messageDom = XmlHelper.parseXml(xml);
		schemaVersion =
			messageDom.getDocumentElement().getAttribute(
				XML_VERSION_ATTRIBUTE);
		if (schemaVersion == null || schemaVersion.equals(""))
		    schemaVersion = "1.0";
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return schemaVersion;
    }

    public int getMajorXMLSchemaVersion(String xml) {
	return getMajorVersion(getXMLSchemaVersion(xml));
    }

    public int getMajorSchemaVersion() {
	String schemaVersion = getSchemaVersion();
	return getMajorVersion(schemaVersion);
    }

    private int getMajorVersion(String version) {
	return Integer.parseInt(version.substring(0, version.indexOf('.')));
    }

    public int getMinorXMLSchemaVersion(String xml) {
	return getMinorVersion(getXMLSchemaVersion(xml));
    }

    public int getMinorSchemaVersion() {
	String schemaVersion = getSchemaVersion();
	return getMinorVersion(schemaVersion);
    }

    private int getMinorVersion(String version) {
	return Integer.parseInt(version.substring(version.indexOf('.') + 1));
    }

    public boolean isValid(String xml) {
	DOMSource domSource = new DOMSource(XmlHelper.parseXml(xsd));
	SchemaFactory sf =
		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	try {
	    Schema s = sf.newSchema(domSource);
	    Validator v = s.newValidator();
	    v.validate(new DOMSource(XmlHelper.parseXml(xml)));
	} catch (SAXException e) {
	    System.out.println("XML is not valid because ");
	    System.out.println(e.getMessage());
	    return false;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    /**
     * Convert xml to actual version
     * 
     * @param xml
     * @throws Exception
     */
    public String verifyAndConvert(String xml) throws Exception {
	if (xml != null && !xml.trim().equals("")) {
	    int majorSchemaVersion = getMajorSchemaVersion();
	    if (getMajorXMLSchemaVersion(xml) == majorSchemaVersion) {
		String schemaVersionString = getSchemaVersion();
		int minorSchemaVersion = getMinorSchemaVersion();
		int minorXmlVersion = getMinorXMLSchemaVersion(xml);
		try {
		    while (minorXmlVersion != minorSchemaVersion) {
			int nextMinorVersion = minorXmlVersion + 1;
			xml =
				convert(xml, majorSchemaVersion + "."
					+ minorXmlVersion,
					majorSchemaVersion + "."
						+ nextMinorVersion);

			minorXmlVersion = nextMinorVersion;
		    }
		    xml = updateXmlSchemaVersion(xml, schemaVersionString);
		} catch (Exception e) {
		    e.printStackTrace();
		    return xml;
		}
	    } else {
		throw new Exception(
			"XSD version and XML version are not compatible");
	    }
	}
	return xml;
    }

    private String convert(String xml, String actualVersion, String nextVersion)
	    throws Exception {
	String filename =
		CONVERSION_FILENAME_TEMPLATE.replace("{0}", actualVersion)
			.replace("{1}", nextVersion);
	String xslPath =
		webappdir + File.separator + SCHEMA_DIRECTORY + File.separator
			+ CONVERSION_DIRECTORY + File.separator + filename;
	return transformXml(xml, xslPath);
    }

    private String getXsd() {
	String xsdFilePath =
		webappdir + File.separator + SCHEMA_DIRECTORY + File.separator
			+ SCHEMA_FILENAME;
	return FileHelper.getFileContent(xsdFilePath);
    }

    public String transformXml(String content, String xslFilePath)
	    throws Exception {
	return XmlHelper.applyXsl(content, FileHelper
		.getFileContent(xslFilePath));
    }

    private String updateXmlSchemaVersion(String xml, String newVersion)
	    throws Exception {
	String xslPath =
		webappdir + File.separator + SCHEMA_DIRECTORY + File.separator
			+ CONVERSION_DIRECTORY + File.separator
			+ UPDATE_VERSION_FILENAME;

	String xsl = FileHelper.getFileContent(xslPath);
	
	TreeMap<String, String> transformerParameters = new TreeMap<String, String>();
	transformerParameters.put("newSchemaVersion", newVersion);
	
	return XmlHelper.applyXsl(xml, xsl,transformerParameters);

    }

}
