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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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

    private String getSchemaVersion() {
	Document doc = parseXml(xsd);
	return doc.getDocumentElement().getAttribute(VERSION_ATTRIBUTE);
    }

    private String getXMLSchemaVersion(String xml) {
	String schemaVersion = "";
	Document messageDom = null;
	if (xml != null) {
	    try {
		// XMLtoDOM
		messageDom = parseXml(xml);
		schemaVersion =
			messageDom.getDocumentElement().getAttribute(
				XML_VERSION_ATTRIBUTE);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return schemaVersion;
    }

    public String getMajorXMLSchemaVersion(String xml) {
	return getMajorVersion(getXMLSchemaVersion(xml));
    }

    public String getMajorSchemaVersion() {
	String schemaVersion = getSchemaVersion();
	return getMajorVersion(schemaVersion);
    }

    private String getMajorVersion(String version) {
	return version.substring(0, version.indexOf('.'));
    }

    public boolean isValid(String xml) {
	DOMSource domSource = new DOMSource(parseXml(xsd));
	SchemaFactory sf =
		SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	try {
	    Schema s = sf.newSchema(domSource);
	    Validator v = s.newValidator();
	    v.validate(new DOMSource(parseXml(xml)));
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

    private Document parseXml(String xml) {
	// get the factory
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	Document dom = null;
	try {

	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    // parse using builder to get DOM representation of the XML file
	    byte[] byteArray = xml.getBytes("UTF-8");
	    ByteArrayInputStream byteArrayInputStream =
		    new ByteArrayInputStream(byteArray);

	    dom = db.parse(byteArrayInputStream);

	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	return dom;
    }

    /**
     * Convert xml to actual version
     * 
     * @param xml
     * @throws Exception 
     */
    public String verifyAndConvert(String xml) throws Exception {
	if (getMajorXMLSchemaVersion(xml).equals(getMajorSchemaVersion())) {
	    String xmlVersionString = getXMLSchemaVersion(xml);
	    String schemaVersionString = getSchemaVersion();
	    float actualVersion = Float.valueOf(xmlVersionString);
	    float schemaVersion = Float.valueOf(schemaVersionString);
	    try {
		while (actualVersion != schemaVersion) {
		    float nextVersion = (float) (actualVersion + 0.1);

		    xml =
			    convert(xml, Float.toString(actualVersion), Float
				    .toString(nextVersion));

		    actualVersion = nextVersion;
		}
		xml = updateXmlSchemaVersion(xml, schemaVersionString);
	    } catch (Exception e) {
		e.printStackTrace();
		return xml;
	    }

	}else{
	    throw new Exception("XSD version and XML version are not compatible");
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
	return getFileContent(xsdFilePath);
    }

    private String getFileContent(String filepath) {
	String fileContent = null;
	InputStreamReader inputStreamReader;
	File coXslFile = new File(filepath);
	try {
	    inputStreamReader =
		    new InputStreamReader(new FileInputStream(coXslFile));
	    StringWriter writer = new StringWriter();
	    BufferedReader buffer = new BufferedReader(inputStreamReader);
	    String line = "";
	    while (null != (line = buffer.readLine())) {
		writer.write(line);
	    }
	    fileContent = writer.toString();
	    fileContent = new String(fileContent.getBytes(), "UTF-8");

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return fileContent;
    }

    public String transformXml(String content, String xslFilePath)
	    throws Exception {
	return applyXsl(content, getFileContent(xslFilePath));
    }

    private String applyXsl(String xml, String xsl) throws Exception {
	TransformerFactory tFactory = TransformerFactory.newInstance();
	try {
	    // retrieve the Xml source
	    StreamSource coXmlContentSource =
		    new StreamSource(new ByteArrayInputStream(xml
			    .getBytes("UTF-8")));
	    // retrieve the Xsl source
	    StreamSource coXslContentSource =
		    new StreamSource(new ByteArrayInputStream(xsl
			    .getBytes("UTF-8")));
	    // we use a ByteArrayOutputStream to avoid using a file
	    ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
	    StreamResult xmlResult = new StreamResult(out);

	    Transformer transformerXml =
		    tFactory.newTransformer(coXslContentSource);
	    transformerXml.transform(coXmlContentSource, xmlResult);
	    return out.toString("UTF-8");
	} catch (Exception e) {
	    log.error("Unable to transform XML", e);
	    throw e;
	}
    }

    private String updateXmlSchemaVersion(String xml,
	    String newVersion) throws Exception {
	String xslPath =
		webappdir + File.separator + SCHEMA_DIRECTORY + File.separator
			+ CONVERSION_DIRECTORY + File.separator
			+ UPDATE_VERSION_FILENAME;

	String xsl = getFileContent(xslPath);
	xsl = xsl.replace("{0}", newVersion);

	return applyXsl(xml, xsl);

    }

}
