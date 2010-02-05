/**********************************************************************************
 * $Id:  $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Utility class for HostedMode provides useful transformation methods to mock
 * flat files to objects
 */
public class OsylHostedModeTransformUtil {
    private static final String ITEM_NODE_NAME = "item";
    private static final String VALUE_ATTRIBUTE_NAME = "value";

    /**
     * Transforms property file text to Map
     * 
     * @param propertyTxt : bundle property file style text
     * @return message Map
     */
    public static Map<String, String> propertyTxt2Map(String propertyTxt) {
	Map<String, String> messages = new HashMap<String, String>();
	String[] lines = propertyTxt.split("\\n");
	for (String line : lines) {
	    String isoLine = unescapeJava(line);
	    // String isoLine = line;
	    String lineTrim = isoLine.trim();
	    if (lineTrim.length() > 0) {
		if (!(lineTrim.charAt(0) == '#')) {
		    int eqPos = lineTrim.indexOf("=");
		    if (eqPos > 0) {
			String key = lineTrim.substring(0, eqPos).trim();
			String value = lineTrim.substring(eqPos + 1).trim();
			messages.put(key, value);
		    }
		}
	    }
	}
	return messages;
    }

    /**
     * Transforms xml file text (rolesList xml style file) to List
     * 
     * @param xmlTxt : xml text
     * @return List object
     * @throws Exception
     */
    public static List<String> xmlTxt2List(String xmlTxt) throws Exception {
	List<String> list = new ArrayList<String>();

	Document dom = null;
	try {
	    dom = XMLParser.parse(xmlTxt);
	} catch (Exception e) {
	    throw new Exception("The content is not an XML valid content: ", e);
	}

	if (dom != null) {
	    try {
		Element nodeElement = dom.getDocumentElement();
		NodeList nodeList = nodeElement.getChildNodes();
		if (nodeList != null) {
		    for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equalsIgnoreCase(ITEM_NODE_NAME)) {
			    String value =
				    node.getAttributes().getNamedItem(
					    VALUE_ATTRIBUTE_NAME)
					    .getNodeValue();
			    list.add(value);
			}
		    }
		}
	    } catch (Exception e) {
		throw new Exception("Cannot transform XML content to List: ", e);
	    }
	}

	return list;
    }

    public static String unescapeJava(String str) {
	if (str == null) {
	    return "";
	}
	StringBuffer outputStringBuffer = new StringBuffer();
	int sz = str.length();
	StringBuffer unicode = new StringBuffer(4);
	boolean hadSlash = false;
	boolean inUnicode = false;
	for (int i = 0; i < sz; i++) {
	    char ch = str.charAt(i);
	    if (inUnicode) {
		// if in unicode, then we're reading unicode
		// values in somehow
		unicode.append(ch);
		if (unicode.length() == 4) {
		    // unicode now contains the four hex digits
		    // which represents our unicode character
		    int value = Integer.parseInt(unicode.toString(), 16);
		    outputStringBuffer.append((char) value);
		    unicode.setLength(0);
		    inUnicode = false;
		    hadSlash = false;
		}
	    }
	    if (hadSlash) {
		// handle an escaped value
		hadSlash = false;
		switch (ch) {
		case '\\':
		    outputStringBuffer.append('\\');
		    break;
		case '\'':
		    outputStringBuffer.append('\'');
		    break;
		case '\"':
		    outputStringBuffer.append('"');
		    break;
		case 'r':
		    outputStringBuffer.append('\r');
		    break;
		case 'f':
		    outputStringBuffer.append('\f');
		    break;
		case 't':
		    outputStringBuffer.append('\t');
		    break;
		case 'n':
		    outputStringBuffer.append('\n');
		    break;
		case 'b':
		    outputStringBuffer.append('\b');
		    break;
		case 'u': {
		    // uh-oh, we're in unicode country....
		    inUnicode = true;
		    break;
		}
		default:
		    outputStringBuffer.append(ch);
		    break;
		}
		continue;
	    } else if (ch == '\\') {
		hadSlash = true;
		continue;
	    }
	    outputStringBuffer.append(ch);
	}
	if (hadSlash) {
	    // then we're in the weird case of a \ at the end of the
	    // string, let's output it anyway.
	    outputStringBuffer.append('\\');
	}
	String outputString = outputStringBuffer.toString();

	outputString = outputString.replaceAll("00\\w(\\D{1})\\d", "$1");
	outputString = outputString.replaceAll("201’9", "'");
	return outputString;
    }

}
