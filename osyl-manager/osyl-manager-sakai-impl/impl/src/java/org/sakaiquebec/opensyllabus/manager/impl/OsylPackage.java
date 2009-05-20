/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.sakaiquebec.opensyllabus.manager.api.OsylManagerService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class OsylPackage {
	
    private final String CO_RESOURCE = "COResource";
    private final String URI_TYPE = "type";
    private final String URI_VALUE = "document";
    private final String URI_TAGNAME = "uri";
    private DocumentBuilderFactory factory;
    private DocumentBuilder build;
    private Document root;
    private String xmlData;
    private List<File> files = new ArrayList<File>();

    public String getXmlData() {
	return xmlData;
    }

    public List<File> getImportedFiles() {
	return files;
    }

    /**
     * Constructor.
     */
    public OsylPackage() {
    }

    private void findFiles(InputStream stream, ZipFile zip) {
	try {
	    factory = DocumentBuilderFactory.newInstance();
	    build = factory.newDocumentBuilder();

	    root = build.parse(stream);

	    // Search nodes
	    checkNodes(root, zip);

	    // renderXML(root);

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private void checkNodes(Document root, ZipFile zip) {
	NodeList list = root.getElementsByTagName(CO_RESOURCE);
	NodeList children;
	Element element;
	int i = 0;
	String elementType;
	Node node;

	while (i < list.getLength()) {
	    element = (Element) list.item(i);
	    elementType = element.getAttribute(URI_TYPE);
	    if (URI_VALUE.equals(elementType)) {
		children = element.getElementsByTagName(URI_TAGNAME);

		node = children.item(0).getFirstChild();
		node.setNodeValue(node.getNodeValue().trim());

	    }
	    i++;
	}

    }

    private final void createFile(InputStream in, OutputStream out)
	    throws IOException {
	byte[] buffer = new byte[1024];
	int len;

	while ((len = in.read(buffer)) >= 0)
	    out.write(buffer, 0, len);

	in.close();
	out.close();
    }

    /**
     * Performs an XSLT transformation, sending the results to a temporary file.
     */

    private File renderXML(Document doc, String fileName) {
	TransformerFactory factory = TransformerFactory.newInstance();
	Transformer transform;
	File xmlDataFile = new File(fileName);
	try {

	    transform = factory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(xmlDataFile);
	    transform.transform(source, result);

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return xmlDataFile;

    }

    /**
     * Reads the file content into XML.
     * 
     * @param doc
     * @param fileName
     * @return
     */
    public String renderXmlData(Document doc, String fileName) {
	String xmlData = "";
	File xmlFile = renderXML(doc, fileName);

	try {
	    BufferedReader input = new BufferedReader(new FileReader(xmlFile));
	    try {
		String line = null;
		while ((line = input.readLine()) != null) {
		    xmlData += new String(line.getBytes(), "UTF-8");
		}
	    } finally {
		input.close();
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	xmlFile.delete();

	return xmlData;
    }

    /**
     * Unzips the archive file.
     * 
     * @param zip
     */
    public void unzipFile(File zip) {
	ZipFile zipFile;
	Enumeration<? extends ZipEntry> entries;
	ZipEntry entry;
	InputStream inputStream;
	OutputStream outputStream;
	File newFile;
	try {
	    zipFile = new ZipFile(zip);
	    entries = zipFile.entries();
	    while (entries.hasMoreElements()) {
		entry = (ZipEntry) entries.nextElement();
		inputStream = zipFile.getInputStream(entry);

		if (entry.getName().equals(OsylManagerService.CO_XML_FILENAME)) {
		    findFiles(inputStream, zipFile);
		    inputStream = zipFile.getInputStream(entry);
		    xmlData = renderXmlData(root, entry.getName());
		} else {
		    newFile = new File(entry.getName());
		    outputStream = new FileOutputStream(newFile);
		    createFile(inputStream, outputStream);
		    files.add(newFile);
		}

	    }
	    zipFile.close();
	} catch (ZipException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Create an osylPackage with files given by the Map<filename,content>
     * parameter
     * 
     * @param files
     * @return The osylPackage under byte[] format
     */
    public byte[] createOsylPackage(Map<String, byte[]> files) {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ZipOutputStream zipfile = new ZipOutputStream(bos);
	Iterator<String> i = files.keySet().iterator();
	String fileName = null;
	ZipEntry zipentry = null;
	try {
	    while (i.hasNext()) {
		fileName = (String) i.next();
		zipentry = new ZipEntry(fileName);
		zipfile.putNextEntry(zipentry);
		zipfile.write((byte[]) files.get(fileName));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		zipfile.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return bos.toByteArray();
    }
}
