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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class SchemaHelper {

    private static final String VERSION_ATTRIBUTE="version";
    
    private String xsd;
    
    public SchemaHelper(String xsd){
	this.xsd=xsd;
    }
    
    
    public String getSchemaVersion(){
	Document doc = parseXml(xsd);
	return doc.getDocumentElement().getAttribute(VERSION_ATTRIBUTE);	
    }
    
    public boolean isValid(String xml){
	DOMSource domSource = new DOMSource(parseXml(xsd));
	SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
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
    
}

