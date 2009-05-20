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
package org.sakaiquebec.opensyllabus.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COModeledServer {

    /**
     * Map<name,permissions> of permissions applied to ressources
     * @uml.property  name="documentSecurityMap"
     * @uml.associationEnd  qualifier="trim:java.lang.String java.lang.String"
     */
    private Map<String, String> documentSecurityMap;

    /**
     * The CourseOutlineContent node name in the xml DOM.
     */
    protected final static String CO_NODE_NAME = "CO";

    /**
     * The COStructure node name in the xml DOM
     */
    protected final static String CO_STRUCTURE_NODE_NAME = "COStructure";

    /**
     * The CourseOutline Unit node name in the xml DOM.
     */
    protected final static String CO_UNIT_NODE_NAME = "COUnit";
    /**
     * The CourseOutline Unit Content node name in the xml DOM.
     */
    protected final static String CO_UNIT_CONTENT_NODE_NAME = "COUnitContent";

    /**
     * The COResourceProxy node name in the xml DOM.
     */
    protected final static String CO_RES_PROXY_NODE_NAME = "COResourceProxy";

    /**
     * The COResource node name in the xml DOM.
     */
    protected final static String CO_RES_NODE_NAME = "COResource";

    /**
     * The COContentRubric node name in the xml DOM.
     */
    protected final static String CO_CONTENT_RUBRIC_NODE_NAME =
	    "COContentRubric";

    /**
     * Name of a properties node.
     */
    protected final static String CO_PROPERTIES_NODE_NAME = "properties";

    /**
     * Name of the text property node.
     */
    protected final static String CO_PROPERTIES_TEXT_NODE_NAME = "text";

    /**
     * Name of a label node.
     */
    protected final static String CO_LABEL_NODE_NAME = "label";

    /**
     * Name of a comment node.
     */
    protected final static String CO_COMMENT_NODE_NAME = "comment";

    /**
     * Name of a security attribute.
     */
    protected final static String SECURITY_ATTRIBUTE_NAME = "scrty";

    /**
     * Name of type attribute.
     */
    protected final static String TYPE_ATTRIBUTE_NAME = "type";

    /**
     * @uml.property  name="coSerialized"
     * @uml.associationEnd  
     */
    private COSerialized coSerialized;

    public COModeledServer() {

    }

    public COModeledServer(COSerialized coSerialized) {
	this.coSerialized = coSerialized;
    }

    public void XML2Model() {

	COContent coContent = new COContent();
	Document messageDom = null;
	documentSecurityMap= new HashMap<String, String>();

	try {
	    // XMLtoDOM
	    messageDom = parseXml(coSerialized.getSerializedContent());

	    // DOMtoModel
	    coContent = createCOContentPOJO(messageDom, coContent);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private Document parseXml(String xml) {
	//get the factory
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	Document dom = null;
	try {

	    //Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    //parse using builder to get DOM representation of the XML file
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
     * Creates a COContent POJO from the root of the xml document.
     * 
     * @param document the document being created
     * @param coContent the POJO to be created from the root element.
     */
    private COContent createCOContentPOJO(Document messageDom,
	    COContent coContent) {
	Element myRoot = messageDom.getDocumentElement();
	Node myNode;
	String nodeName = "";

	coContent.setSecurity(myRoot.getAttribute(SECURITY_ATTRIBUTE_NAME));
	coContent.setType(myRoot.getAttribute(TYPE_ATTRIBUTE_NAME));

	// Retrieve children: can be StructureElement or COUnit as well as the subnode label
	NodeList rootChildren = myRoot.getChildNodes();

	for (int i = 0; i < rootChildren.getLength(); i++) {
	    myNode = rootChildren.item(i);
	    nodeName = myNode.getNodeName();

	    if (nodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coStructElt = new COStructureElement();
		coContent.addChild(createCOStructureElementPOJO(myNode,
			coStructElt, coContent));
	    } else if (nodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		//Retrieve the label and the unique child of COUnit: a COUnitCOntent
		NodeList coUnitChild = myNode.getChildNodes();

		String label = "";
		COContentUnit coContentUnit = new COContentUnit();

		for (int s = 0; s < coUnitChild.getLength(); s++) {
		    Node coUnitNode = coUnitChild.item(s);
		    if (CO_LABEL_NODE_NAME.equalsIgnoreCase(coUnitNode
			    .getNodeName())) {
			label = coUnitNode.getFirstChild().getNodeValue();
		    } else if (CO_UNIT_CONTENT_NODE_NAME
			    .equalsIgnoreCase(coUnitNode.getNodeName())) {
			coContent.addChild(createCOContentUnitPOJO(coUnitNode,
				coContentUnit, coContent, label));
		    }
		}
		coContentUnit.setLabel(label);

	    } else if (nodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContent.setLabel(getCDataSectionValue(myNode));
	    }
	}
	return coContent;
    }

    /**
     * Gets the CDataSection Value No confusion here: there is no specific
     * treatment for cdata sections They are retrieved same way as normal node
     * values
     * 
     * @param cDataSectionNode
     * @return the cData section value
     */
    private String getCDataSectionValue(Node cDataSectionNode) {
	String value = "";

	for (int i = 0; i < cDataSectionNode.getChildNodes().getLength(); i++) {
	    value += cDataSectionNode.getChildNodes().item(i).getNodeValue();
	}
	return value;
    }

    /**
     * Create the POJO properties
     * 
     * @param propertiesNode
     * @return the CoProperties included in the properties node
     */
    private COProperties createProperties(Node propertiesNode) {
	COProperties coProperties = new COProperties();

	NodeList propertiesList = propertiesNode.getChildNodes();

	for (int i = 0; i < propertiesList.getLength(); i++) {
	    Node prop = propertiesList.item(i);
	    String value = "";

	    for (int j = 0; j < prop.getChildNodes().getLength(); j++) {
		value += prop.getChildNodes().item(j).getNodeValue();
	    }
	    coProperties.addProperty(prop.getNodeName(), value);
	}
	return coProperties;
    }

    /**
     * Creates a StructureElement POJO from the DOM.
     * 
     * @param node the node from the DOM representing that structure element
     * @param coStructElt the POJO to be created from the DOM.
     * @param coContent the parent of the Structure element
     */
    private COStructureElement createCOStructureElementPOJO(Node node,
	    COStructureElement coStructElt, COElementAbstract parent) {
	Node sNode;
	String sNodeName = "";
	NamedNodeMap sMap = node.getAttributes();
	coStructElt.setSecurity(sMap.getNamedItem(SECURITY_ATTRIBUTE_NAME)
		.getNodeValue());
	coStructElt.setType(sMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
		.getNodeValue());

	coStructElt.setParent(parent);

	// Retrieve children: can be StructureElement or COUnit
	NodeList strucEltChildren = node.getChildNodes();
	for (int i = 0; i < strucEltChildren.getLength(); i++) {
	    sNode = strucEltChildren.item(i);
	    sNodeName = sNode.getNodeName();
	    COStructureElement coChildStructElt = new COStructureElement();

	    if (sNodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		coStructElt.addChild(createCOStructureElementPOJO(sNode,
			coChildStructElt, coStructElt));
	    } else if (sNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		NodeList coUnitChild = sNode.getChildNodes();
		String label = "";
		COContentUnit coContentUnit = new COContentUnit();
		for (int s = 0; s < coUnitChild.getLength(); s++) {
		    Node coUnitNode = coUnitChild.item(s);
		    if (CO_LABEL_NODE_NAME.equalsIgnoreCase(coUnitNode
			    .getNodeName())) {
			label = coUnitNode.getFirstChild().getNodeValue();
			// Retrieve the child of COUnit: a COUnitCOntent
		    } else if (CO_UNIT_CONTENT_NODE_NAME
			    .equalsIgnoreCase(coUnitNode.getNodeName())) {
			coStructElt.addChild(createCOContentUnitPOJO(
				coUnitNode, coContentUnit, coStructElt, label));
		    }
		}
		coContentUnit.setLabel(label);
	    } else if (sNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coStructElt.setProperties(createProperties(sNode));
	    }
	}
	return coStructElt;
    }

    /**
     * Creates a Course Outline content unit POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Course Outline
     *                content
     * @param coContentUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COContentUnit createCOContentUnitPOJO(Node node,
	    COContentUnit coContentUnit, COElementAbstract parent, String label) {
	Node coNode;
	String coNodeName = "";

	NamedNodeMap coMap = node.getAttributes();
	coContentUnit.setSecurity(coMap.getNamedItem(SECURITY_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentUnit.setType(coMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentUnit.setParent(parent);

	//label from COUnit
	coContentUnit.setLabel(label);

	NodeList coContentUnitChildren = node.getChildNodes();
	for (int i = 0; i < coContentUnitChildren.getLength(); i++) {
	    coNode = coContentUnitChildren.item(i);
	    coNodeName = coContentUnitChildren.item(i).getNodeName();

	    if (coNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentUnit
			.addResourceProxy(createCOContentResourceProxyPOJO(
				coNode, coContentUnit));
	    } else if (coNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContentUnit.setLabel(getCDataSectionValue(coNode));
	    }
	}
	return coContentUnit;
    }

    /**
     * Creates a Course Outline Resource Proxy POJO from the DOM. (for proxies
     * which belong directly to the contentUnit)
     * 
     * @param node the node from the DOM representing that Resource Proxy
     * @return the created Course Outline Resource Proxy POJO
     */
    private COContentResourceProxy createCOContentResourceProxyPOJO(Node node,
	    COContentUnit coContentUnitParent) {

	Node prNode;
	String prNodeName = "";

	COContentResourceProxy coContentResProxy = new COContentResourceProxy();
	NamedNodeMap prMap = node.getAttributes();
	coContentResProxy.setSecurity(prMap.getNamedItem(
		SECURITY_ATTRIBUTE_NAME).getNodeValue());
	coContentResProxy.setType(prMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentResProxy.setCoContentUnitParent(coContentUnitParent);

	NodeList resProxyChildren = node.getChildNodes();
	for (int j = 0; j < resProxyChildren.getLength(); j++) {
	    prNode = resProxyChildren.item(j);
	    prNodeName = prNode.getNodeName();

	    if (prNodeName.equalsIgnoreCase(CO_COMMENT_NODE_NAME)) {
		coContentResProxy.setComment(getCDataSectionValue(prNode));
	    }/*no more description
	    	    	    	    	    	    else if (prNodeName.equalsIgnoreCase(CO_DESCRIPTION_NODE_NAME)) {
	    	    	    	    	    		coContentResProxy.setDescription(getCDataSectionValue(prNode));
	    	    	    	    	    	    }*/
	    else if (prNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContentResProxy.setLabel(getCDataSectionValue(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_CONTENT_RUBRIC_NODE_NAME)) {
		coContentResProxy.setRubric(createCOContentRubricPOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_NODE_NAME)) {
		coContentResProxy
			.setResource(createCOContentResourcePOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentResProxy
			.addResourceProxy(createCOContentResourceProxyPOJO(
				prNode, coContentUnitParent));
	    } else if (prNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coContentResProxy.setProperties(createProperties(prNode));
	    }
	}
	return coContentResProxy;
    }

    /**
     * Creates a Course Outline Resource POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Course Outline
     *                Content Resource
     * @return the created Content resource
     */
    private COContentResource createCOContentResourcePOJO(Node node) {

	COContentResource coContentRes = new COContentResource();
	NamedNodeMap rMap = node.getAttributes();
	String type= rMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
	.getNodeValue();
	coContentRes.setType(type);
	String security = rMap.getNamedItem(SECURITY_ATTRIBUTE_NAME)
	.getNodeValue();
	coContentRes.setSecurity(security);
	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    String rNodeName = rNode.getNodeName();

	    if (rNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coContentRes.setProperties(createProperties(rNode));
	    }
	}
	if(type.equals(COContentResourceProxyType.DOCUMENT))
	    documentSecurityMap.put(coContentRes.getProperty(COPropertiesType.URI).trim(),security);
	return coContentRes;
    }

    public Map<String, String> getDocumentSecurityMap() {
        return documentSecurityMap;
    }

    public void setDocumentSecurityMap(Map<String, String> documentSecurityMap) {
        this.documentSecurityMap = documentSecurityMap;
    }

    /**
     * Creates a Course OutlineContent rubric POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Resource Proxy
     * @return the created Course Outline Resource Proxy POJO
     */
    private COContentRubric createCOContentRubricPOJO(Node node) {

	COContentRubric coContentRubric = new COContentRubric();
	NamedNodeMap map = node.getAttributes();
	coContentRubric.setType(map.getNamedItem(TYPE_ATTRIBUTE_NAME)
		.getNodeValue());

	/*coContentRubric.setName(map.getNamedItem(NAME_ATTRIBUTE_NAME)
		.getNodeValue());*/

	for (int i = 0; i < node.getChildNodes().getLength(); i++) {
	    //Node childNode = node.getChildNodes().item(i);
	    //String childNodeName = childNode.getNodeName();
	    //no more description
	    /*  if (childNodeName.equalsIgnoreCase(CO_DESCRIPTION_NODE_NAME)) {
	    coContentRubric.setDescription(getCDataSectionValue(childNode));
	    }*/
	}
	return coContentRubric;
    }

}
