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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
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
	private File unzipDir = null;
	private Map<File, String> fileMap = new HashMap<File,String>();
	
	public String getXml() {
		return xmlData;
	}

	public void setXml(String xmlData) {
		this.xmlData = xmlData;
	}

	/**
	 * @return File for key, FileName (to use) for value
	 */
	public Map<File, String> getImportedFiles() {
		return fileMap;
	}

	/**
	 * Constructor.
	 */
	public OsylPackage() {
		unzipDir = new File(new File(System.getProperty("java.io.tmpdir")),"osyl-import-unpack"+System.currentTimeMillis());
	}

	private void findFiles(InputStream stream) {
		try {
			factory = DocumentBuilderFactory.newInstance();
			build = factory.newDocumentBuilder();

			root = build.parse(stream);

			// Clean nodes
			cleanNodes(root);

			// renderXML(root);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void cleanNodes(Document root) {
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
				if(node!=null){
					node.setNodeValue(node.getNodeValue().trim());
				}
			}
			i++;
		}

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
	public void unzip(File zip) {
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

				// Retrieving the course outline xml file AND all resources
				// files
				if (entry.getName().equals(OsylManagerService.CO_XML_FILENAME)) {
					findFiles(inputStream);
					xmlData = renderXmlData(root, entry.getName());
				} else {										
					newFile = new File(unzipDir, entry.getName());
					if (!entry.isDirectory()) {
						File newFileDir = newFile.getParentFile();
						if(!newFileDir.exists()){
							newFileDir.mkdirs();
						}
						outputStream = new FileOutputStream(newFile);
						IOUtils.copy(inputStream, outputStream);
					}
					fileMap.put(newFile, entry.getName());
				}

			}			
			
			zipFile.close();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
