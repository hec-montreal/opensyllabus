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

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;
import org.w3c.dom.Node;

public class FOPHelper {

    public static File convertXml2Pdf(String xml, String xslt, String webappdir)
	    throws IOException, TransformerException {
	File pdffile = File.createTempFile("osyl-fop-print", ".pdf");
	try {
	    // configure fopFactory as desired
	    FopFactory fopFactory = FopFactory.newInstance();

	    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
	    // configure foUserAgent as desired

	    // Setup output
	    OutputStream out = new java.io.FileOutputStream(pdffile);
	    out = new java.io.BufferedOutputStream(out);

	    try {
		// Construct fop with desired output format
		Fop fop =
			fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent,
				out);

		// Setup XSLT
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer =
			factory
				.newTransformer(new StreamSource(
					new ByteArrayInputStream(xslt
						.getBytes("UTF-8"))));

		// Set the value of a <param> in the stylesheet
		transformer.setParameter("versionParam", "2.0");
		transformer.setParameter("ppath", webappdir);
		transformer.setParameter("spath", "/access/content");
		transformer.setParameter("currentDateTime", OsylDateUtils.getCurrentDateAsXmlString());

		// Setup input for XSLT transformation
		String escapeString = xml.replaceAll("&amp;", "&#38;");
		escapeString = escapeString.replaceAll("&#38;amp;", "&#38;");
		escapeString = escapeString.replaceAll("\n", " ");
		escapeString = escapeString.replaceAll("\r", " ");
		escapeString = escapeString.replaceAll("&lt;", "<");
		escapeString = escapeString.replaceAll("&gt;", ">");
		escapeString = escapeString.replaceAll("&nbsp;", " ");
		escapeString = escapeString.replaceAll("&#38;nbsp;", " ");
		escapeString = escapeString.replaceAll("&#13;", "");
		escapeString = escapeString.replaceAll("&#0;", "");
		Source src =
			new StreamSource(new ByteArrayInputStream(escapeString
				.getBytes("UTF-8")));

		// Resulting SAX events (the generated FO) must be piped through
		// to FOP
		Result res = new SAXResult(fop.getDefaultHandler());

		// Start XSLT transformation and FOP processing
		transformer.transform(src, res);
	    } finally {
		out.close();
	    }
	} catch (Exception e) {
	    e.printStackTrace(System.err);
	}
	return pdffile;
    }

    public static File convertXml2Pdf(Node d, String xslt, String webappdir)
	    throws IOException, TransformerException {

	// Setup input for XSLT transformation
	String s = xmlToString(d);
	return convertXml2Pdf(s, xslt, webappdir);
    }

    public static String xmlToString(Node node) {
	try {
	    Source source = new DOMSource(node);
	    StringWriter stringWriter = new StringWriter();
	    Result result = new StreamResult(stringWriter);
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer();
	    transformer.transform(source, result);
	    return stringWriter.toString();
	} catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}
	return null;
    }

}
