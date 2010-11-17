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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class XmlHelper {


    public static Document parseXml(String xml) {
	xml = rmNonValidChars(xml);
	// get the factory
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
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
    
    private static String rmNonValidChars(String str) {

	if (str == null)
	    return null;

	StringBuffer s = new StringBuffer();

	for (char c : str.toCharArray()) {

	    if ((c == 0x9) || (c == 0xA) || (c == 0xD)

	    || ((c >= 0x20) && (c <= 0xD7FF))

	    || ((c >= 0xE000) && (c <= 0xFFFD))

	    || ((c >= 0x10000) && (c <= 0x10FFFF))) {

		s.append(c);

	    }
	}
	return s.toString();
    }

    public static Node applyXsl(Node d, String xsl) throws Exception{
	TransformerFactory tFactory = TransformerFactory.newInstance();
	// retrieve the Xml source
	DOMSource coXmlContentSource = new DOMSource(d);

	// retrieve the Xsl source
	StreamSource coXslContentSource =
		new StreamSource(
			new ByteArrayInputStream(xsl.getBytes("UTF-8")));
	DOMResult domResult = new DOMResult();

	Transformer transformerXml =
		tFactory.newTransformer(coXslContentSource);
	transformerXml.transform(coXmlContentSource, domResult);
	return domResult.getNode();
    }

    public static String applyXsl(String xml, String xsl) throws Exception {
	return applyXsl(xml, xsl, null);
    }

    public static String applyXsl(String xml, String xsl, URIResolver uriResolver) throws Exception {
	return applyXsl(xml, xsl, uriResolver, "UTF-8");
    }

    public static String applyXsl(String xml, String xsl, URIResolver uriResolver,String encoding) throws Exception{
	TransformerFactory tFactory = TransformerFactory.newInstance();
	if(uriResolver!=null)
	    tFactory.setURIResolver(uriResolver);

	// retrieve the Xml source
	StreamSource coXmlContentSource =
		new StreamSource(
			new ByteArrayInputStream(xml.getBytes(encoding)));
	// retrieve the Xsl source
	StreamSource coXslContentSource =
		new StreamSource(
			new ByteArrayInputStream(xsl.getBytes(encoding)));
	// we use a ByteArrayOutputStream to avoid using a file
	ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
	StreamResult xmlResult = new StreamResult(out);

	Transformer transformerXml =
		tFactory.newTransformer(coXslContentSource);
	transformerXml.transform(coXmlContentSource, xmlResult);
	return out.toString(encoding);
    }

}
