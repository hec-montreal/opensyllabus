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
package org.sakaiquebec.opensyllabus.common.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;
import org.sakaiquebec.opensyllabus.shared.util.UUID;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class COModeledServer {

    /**
     * The CourseOutlineContent node name in the xml DOM.
     */
    protected final static String CO_NODE_NAME = "CO";

    /**
     * The COStructure node name in the xml DOM
     */
    protected final static String CO_STRUCTURE_NODE_NAME = "asmStructure";

    /**
     * The CourseOutline Unit node name in the xml DOM.
     */
    protected final static String CO_UNIT_NODE_NAME = "asmUnit";

    /**
     * The CourseOutline Unit Structure Content node name in the xml DOM.
     */
    protected final static String CO_UNIT_STRUCTURE_NODE_NAME =
	    "asmUnitStructure";

    /**
     * The CourseOutline Unit Content node name in the xml DOM.
     */
    protected final static String CO_UNIT_CONTENT_NODE_NAME = "asmUnitContent";

    /**
     * The COResourceProxy node name in the xml DOM.
     */
    protected final static String CO_RES_PROXY_NODE_NAME = "asmContext";

    /**
     * The COResource node name in the xml DOM.
     */
    protected final static String CO_RES_NODE_NAME = "asmResource";

    /**
     * The COContentRubric node name in the xml DOM.
     */
    protected final static String CO_CONTENT_RUBRIC_NODE_NAME = "semanticType";

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
     * Name of a access attribute.
     */
    protected final static String ACCESS_ATTRIBUTE_NAME = "access";

    /**
     * Name of a type node.
     */
    protected final static String TYPE_NODE_NAME = "type";

    /**
     * Name of type attribute.
     */
    protected final static String XSI_TYPE_ATTRIBUTE_NAME = "xsi:type";
    
    /**
     * Name of type attribute.
     */
    protected final static String TYPE_ATTRIBUTE_NAME = "type";


    // Evaluation attributes
    /**
     * Name of weight attribute
     */
    protected final static String WEIGHT_ATTRIBUTE_NAME = "weight";

    /**
     * Name of location attribute
     */
    protected final static String LOCATION_ATTRIBUTE_NAME = "location";

    /**
     * Name of mode attribute
     */
    protected final static String MODE_ATTRIBUTE_NAME = "mode";

    /**
     * Name of date-start attribute
     */
    protected final static String DATESTART_ATTRIBUTE_NAME = "date-start";

    /**
     * Name of date-end attribute
     */
    protected final static String DATEEND_ATTRIBUTE_NAME = "date-end";

    /**
     *Name of result attribute
     */
    protected final static String RESULT_ATTRIBUTE_NAME = "result";

    /**
     *Name of scope attribute
     */
    protected final static String SCOPE_ATTRIBUTE_NAME = "scope";

    /**
     *Name of uuid attribute
     */
    protected final static String UUID_ATTRIBUTE_NAME = "uuid";

    /**
     *Name of uuid parent attribute
     */
    protected final static String UUID_PARENT_ATTRIBUTE_NAME = "uuid_parent";

    /**
     *Name of uuid attribute
     */
    protected final static String EDITABLE_ATTRIBUTE_NAME = "editable";

    /**
     *Name of person node
     */
    protected final static String PERSON_NODE_NAME = "Person";

    /**
     * The modeledContent is a POJO filled by XML2Model
     */
    private COContent modeledContent;

    /**
     * Map<name,permissions> of permissions applied to ressources
     * 
     * @uml.property name="documentSecurityMap"
     * @uml.associationEnd qualifier="trim:java.lang.String java.lang.String"
     */
    private Map<String, String> documentSecurityMap;

    /**
     * @uml.property name="coSerialized"
     * @uml.associationEnd
     */
    private COSerialized coSerialized;

    /**
     * Default Constructor
     */
    public COModeledServer() {
    }

    /**
     * Constructor from a serialized course outline
     */
    public COModeledServer(COSerialized serializedContent) {
	this.coSerialized = serializedContent;
    }

    /**
     * Full constructor from superclass
     */
    public COModeledServer(String idCo, String lang, String type,
	    String security, String siteId, String sectionId,
	    String osylConfigId, String content, String shortDesc, String desc,
	    String title) {
	// super(idCo, lang, type, security, siteId, sectionId,
	// new COConfigSerialized(osylConfigId), content, shortDesc, desc,
	// title, false);
    }

    /**
     * @return the POJO of the modeled content
     */
    public COContent getModeledContent() {
	return modeledContent;
    }

    /**
     * @param modeledContent the POJO of the modeled content
     */
    public void setModeledContent(COContent modeledContent) {
	this.modeledContent = modeledContent;
    }

    public void XML2Model() {
	XML2Model(false);
    }

    public void XML2Model(boolean changeWorkToPublish) {
	COContent coContent = null;
	Document messageDom = null;
	documentSecurityMap = new HashMap<String, String>();
	if (coSerialized.getContent() != null) {
	    coContent = new COContent();
	    try {
		// XMLtoDOM
		messageDom = parseXml(coSerialized.getContent());

		// DOMtoModel
		coContent =
			createCOContentPOJO(messageDom, coContent,
				changeWorkToPublish);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	setModeledContent(coContent);
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
     * Creates a COContent POJO from the root of the xml document.
     * 
     * @param document the document being created
     * @param coContent the POJO to be created from the root element.
     */
    private COContent createCOContentPOJO(Document messageDom,
	    COContent coContent, boolean changeWorkToPublish) {
	
	Node myRoot = null;
	
	NodeList nodeList = messageDom.getDocumentElement().getChildNodes();
	
	for(int i=0; i<nodeList.getLength(); i++){
	    Node node = nodeList.item(i);
	    
	    if(CO_NODE_NAME.equals(node.getNodeName())){
		myRoot = node;
	    }
	}
	
	Node myNode;
	String nodeName = "";
	
	NamedNodeMap map = myRoot.getAttributes();
	
	coContent.setAccess(map.getNamedItem(ACCESS_ATTRIBUTE_NAME).getNodeValue());

	coContent.setUuid(map.getNamedItem(UUID_ATTRIBUTE_NAME) == null ? UUID.uuid() : map.getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue());

	coContent.setUuidParent(map.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME) == null ? null : map.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME).getNodeValue());

	coContent.setEditable(map.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? true : Boolean
		.valueOf(map.getNamedItem(EDITABLE_ATTRIBUTE_NAME).getNodeValue()));

	// Retrieve children: can be StructureElement or COUnit as well as the
	// subnode label
	NodeList rootChildren = myRoot.getChildNodes();

	for (int i = 0; i < rootChildren.getLength(); i++) {
	    myNode = rootChildren.item(i);
	    nodeName = myNode.getNodeName();

	    if (nodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coStructElt = new COStructureElement();
		coContent.addChild(createCOStructureElementPOJO(myNode,
			coStructElt, coContent, changeWorkToPublish));

	    } else if (nodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContent.setLabel(getCDataSectionValue(myNode));
	    } else {
		addProperty(coContent.getProperties(), myNode);
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

    private void addProperty(COProperties coProperties, Node node) {
	String key = node.getNodeName();
	if (!key.equals("#text")) {
	    String value = "";
	    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
		value += node.getChildNodes().item(j).getNodeValue();
	    }
	    NamedNodeMap namedNodeMap = node.getAttributes();

	    String type =
		    (namedNodeMap == null) ? null : (namedNodeMap
			    .getNamedItem(TYPE_ATTRIBUTE_NAME) == null) ? null
			    : namedNodeMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
				    .getNodeValue();
	    if (type == null)
		coProperties.addProperty(key, value);
	    else
		coProperties.addProperty(key, type, value);
	}
    }

    /**
     * Creates a CoUnit POJO from the DOM.
     * 
     * @param node the node from the DOM representing that structure element
     * @param coUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COUnit createCOUnitPOJO(Node node, COUnit coUnit,
	    COElementAbstract parent, boolean changeWorkToPublish) {
	Node sNode;
	String sNodeName = "";

	NamedNodeMap sMap = node.getAttributes();

	coUnit.setType(sMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME).getNodeValue());

	String uuid =
		sMap.getNamedItem(UUID_ATTRIBUTE_NAME) == null ? null : sMap
			.getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue();
	coUnit.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent =
		sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME)
				.getNodeValue();
	coUnit.setUuidParent(uuidParent);

	coUnit.setAccess(sMap.getNamedItem(ACCESS_ATTRIBUTE_NAME)
		.getNodeValue());

	String editable =
		sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coUnit.setEditable(editable == null ? true : Boolean.valueOf(editable));

	coUnit.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		COUnitStructure coUnitStruct = new COUnitStructure();
		coUnit.addChild(createCOUnitStructurePOJO(sNode, coUnitStruct,
			coUnit, changeWorkToPublish));
	    } else if (sNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coUnit.setLabel(getCDataSectionValue(sNode));
	    } else {
		addProperty(coUnit.getProperties(), sNode);
	    }
	}
	return coUnit;
    }

    /**
     * Creates a CoUnit POJO from the DOM.
     * 
     * @param node the node from the DOM representing that structure element
     * @param coUnitStructure the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COUnitStructure createCOUnitStructurePOJO(Node node,
	    COUnitStructure coUnitStructure, COElementAbstract parent,
	    boolean changeWorkToPublish) {
	Node sNode;
	String sNodeName = "";

	NamedNodeMap sMap = node.getAttributes();

	coUnitStructure.setType(sMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME)
		.getNodeValue());

	String uuid =
		sMap.getNamedItem(UUID_ATTRIBUTE_NAME) == null ? null : sMap
			.getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue();
	coUnitStructure.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent =
		sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME)
				.getNodeValue();
	coUnitStructure.setUuidParent(uuidParent);

	coUnitStructure.setAccess(sMap.getNamedItem(ACCESS_ATTRIBUTE_NAME)
		.getNodeValue());

	String editable =
		sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coUnitStructure.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	coUnitStructure.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		COUnitStructure coUnitStructure2 = new COUnitStructure();
		coUnitStructure
			.addChild(createCOUnitStructurePOJO(sNode,
				coUnitStructure2, coUnitStructure,
				changeWorkToPublish));
	    } else if (sNodeName.equalsIgnoreCase(CO_UNIT_CONTENT_NODE_NAME)) {
		COUnitContent coContentUnit = new COUnitContent();
		coUnitStructure.addChild(createCOContentUnitPOJO(sNode,
			coContentUnit, coUnitStructure, changeWorkToPublish));
	    } else if (sNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coUnitStructure.setLabel(getCDataSectionValue(sNode));
	    } else {
		// properties
		addProperty(coUnitStructure.getProperties(), sNode);
	    }
	}
	return coUnitStructure;
    }

    /**
     * Creates a StructureElement POJO from the DOM.
     * 
     * @param node the node from the DOM representing that structure element
     * @param coStructElt the POJO to be created from the DOM.
     * @param coContent the parent of the Structure element
     */
    private COStructureElement createCOStructureElementPOJO(Node node,
	    COStructureElement coStructElt, COElementAbstract parent,
	    boolean changeWorkToPublish) {
	Node sNode;
	String sNodeName = "";
	NamedNodeMap sMap = node.getAttributes();
	coStructElt.setAccess(sMap.getNamedItem(ACCESS_ATTRIBUTE_NAME)
		.getNodeValue());
	coStructElt.setType(sMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME)
		.getNodeValue());

	String uuid =
		sMap.getNamedItem(UUID_ATTRIBUTE_NAME) == null ? null : sMap
			.getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue();
	coStructElt.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent =
		sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME)
				.getNodeValue();
	coStructElt.setUuidParent(uuidParent);

	String editable =
		sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: sMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coStructElt.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	coStructElt.setParent(parent);

	// Retrieve children: can be StructureElement or COUnit
	NodeList strucEltChildren = node.getChildNodes();
	for (int i = 0; i < strucEltChildren.getLength(); i++) {
	    sNode = strucEltChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coChildStructElt = new COStructureElement();
		coStructElt.addChild(createCOStructureElementPOJO(sNode,
			coChildStructElt, coStructElt, changeWorkToPublish));
	    } else if (sNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		COUnit coUnit = new COUnit();
		coStructElt.addChild(createCOUnitPOJO(sNode, coUnit,
			coStructElt, changeWorkToPublish));
	    } else {
		addProperty(coStructElt.getProperties(), sNode);
	    }
	}
	return coStructElt;
    }

    /**
     * Creates a Course Outline content unit POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Course Outline
     *            content
     * @param coContentUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COUnitContent createCOContentUnitPOJO(Node node,
	    COUnitContent coContentUnit, COElementAbstract parent,
	    boolean changeWorkToPublish) {
	Node coNode;
	String coNodeName = "";
	String coContentUnitType = "";

	NamedNodeMap coMap = node.getAttributes();
	coContentUnitType =
		coMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME).getNodeValue();
	coContentUnit.setAccess(coMap.getNamedItem(ACCESS_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentUnit.setType(coContentUnitType);

	String uuid =
		coMap.getNamedItem(UUID_ATTRIBUTE_NAME) == null ? null : coMap
			.getNamedItem(UUID_ATTRIBUTE_NAME).getNodeValue();
	coContentUnit.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent =
		coMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME) == null ? null
			: coMap.getNamedItem(UUID_PARENT_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentUnit.setUuidParent(uuidParent);

	String editable =
		coMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: coMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentUnit.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	coContentUnit.setParent(parent);

	NodeList coContentUnitChildren = node.getChildNodes();
	for (int i = 0; i < coContentUnitChildren.getLength(); i++) {
	    coNode = coContentUnitChildren.item(i);
	    coNodeName = coContentUnitChildren.item(i).getNodeName();

	    if (coNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentUnit.addChild(createCOContentResourceProxyPOJO(coNode,
			coContentUnit, changeWorkToPublish));
	    } else if (coNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContentUnit.setLabel(getCDataSectionValue(coNode));
	    } else {
		addProperty(coContentUnit.getProperties(), coNode);
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
	    COUnitContent coContentUnitParent, boolean changeWorkToPublish) {

	Node prNode;
	String prNodeName = "";

	COContentResourceProxy coContentResProxy = new COContentResourceProxy();
	NamedNodeMap prMap = node.getAttributes();
	coContentResProxy.setAccess(prMap.getNamedItem(ACCESS_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentResProxy.setType(prMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME)
		.getNodeValue());
	coContentResProxy.setParent(coContentUnitParent);

	NodeList resProxyChildren = node.getChildNodes();
	for (int j = 0; j < resProxyChildren.getLength(); j++) {
	    prNode = resProxyChildren.item(j);
	    prNodeName = prNode.getNodeName();

	    if (prNodeName.equalsIgnoreCase(CO_COMMENT_NODE_NAME)) {
		coContentResProxy.setComment(getCDataSectionValue(prNode));
	    }/*
	      * no more description else if
	      * (prNodeName.equalsIgnoreCase(CO_DESCRIPTION_NODE_NAME)) {
	      * coContentResProxy.setDescription(getCDataSectionValue(prNode));
	      * }
	      */
	    else if (prNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContentResProxy.setLabel(getCDataSectionValue(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_CONTENT_RUBRIC_NODE_NAME)) {
		coContentResProxy.setRubric(createCOContentRubricPOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_NODE_NAME)) {
		coContentResProxy.setResource(createCOContentResourcePOJO(
			prNode, changeWorkToPublish));
	    } else if (prNodeName.equalsIgnoreCase(PERSON_NODE_NAME)) {
		coContentResProxy
			.setResource(createCOContentResourcePersonPOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentResProxy
			.addNestedResourceProxy(createCOContentResourceProxyPOJO(
				prNode, coContentUnitParent,
				changeWorkToPublish));
	    }else if(prNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)){
		COUnit coUnit = new COUnit();
		coContentResProxy
		.setResource(createCOUnitPOJO(prNode, coUnit, coContentResProxy,changeWorkToPublish));
	    } else {
		addProperty(coContentResProxy.getProperties(), prNode);
	    }
	}
	return coContentResProxy;
    }

    /**
     * Creates a Course Outline Resource POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Course Outline
     *            Content Resource
     * @return the created Content resource
     */
    private COContentResource createCOContentResourcePOJO(Node node,
	    boolean changeWorkToPublish) {

	COContentResource coContentRes = new COContentResource();
	NamedNodeMap rMap = node.getAttributes();
	String type = rMap.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setType(type);
	String security =
		rMap.getNamedItem(ACCESS_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setAccess(security);
	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    addProperty(coContentRes.getProperties(), rNode);
	}
	if (type.equals(COContentResourceType.DOCUMENT)
		|| type.equals(COContentResourceType.BIBLIO_RESOURCE)) {
	    documentSecurityMap.put(coContentRes.getProperty(
		    COPropertiesType.URI).trim(), security);
	    if (changeWorkToPublish) {
		COProperties copProperties = coContentRes.getProperties();
		copProperties.addProperty(COPropertiesType.URI, this
			.changeDocumentsUrls(coContentRes.getProperty(
				COPropertiesType.URI).trim(),
				OsylSiteService.WORK_DIRECTORY,
				OsylSiteService.PUBLISH_DIRECTORY));
		coContentRes.setProperties(copProperties);
	    }
	}
	return coContentRes;
    }

    /**
     * Creates a Course Outline Resource POJO of type person from the DOM.
     * 
     * @param node the node from the DOM representing that Course Outline
     *            Content Resource
     * @return the created Content resource
     */
    private COContentResource createCOContentResourcePersonPOJO(Node node) {

	COContentResource coContentRes = new COContentResource();
	NamedNodeMap rMap = node.getAttributes();
	coContentRes.setType(COContentResourceType.PERSON);

	String editable =
		rMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: rMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentRes.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    addProperty(coContentRes.getProperties(), rNode);
	}
	return coContentRes;
    }

    /**
     * Creates a Course OutlineContent rubric POJO from the DOM.
     * 
     * @param node the node from the DOM representing that Resource Proxy
     * @return the created Course Outline Resource Proxy POJO
     */
    private COContentRubric createCOContentRubricPOJO(Node node) {
	COContentRubric coContentRubric = new COContentRubric();
	NodeList resChildren = node.getChildNodes();

	for (int i = 0; i < resChildren.getLength(); i++) {
	    Node rNode = resChildren.item(i);

	    if (rNode.getNodeName().equals(TYPE_NODE_NAME)) {
		String value = "";

		for (int j = 0; j < rNode.getChildNodes().getLength(); j++) {
		    value += rNode.getChildNodes().item(j).getNodeValue();
		}
		coContentRubric.setType(value);
	    } else {
		addProperty(coContentRubric.getProperties(), rNode);
	    }
	}
	return coContentRubric;
    }

    /**
     * Entry point of the model conversion to XML string.
     * 
     * @return the XML string generated from the model.
     */
    public void model2XML() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	Document dom = null;
	try {

	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();

	    dom = db.newDocument();

	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	}
	createRootElement(dom, getModeledContent());
	coSerialized.setContent(xmlToString(dom));
    }

    private String xmlToString(Document doc) {
	try {
	    Source source = new DOMSource(doc);
	    StringWriter stringWriter = new StringWriter();
	    Result result = new StreamResult(stringWriter);
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer();
	    transformer.transform(source, result);
	    return stringWriter.getBuffer().toString();
	} catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	} catch (TransformerException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * Creates the root element of the document.
     * 
     * @param document the document being created
     * @param coContent the POJO needed to create the root element.
     */
    private void createRootElement(Document document, COContent coContent) {
	
	Element osylElement = document.createElement("OSYL");
	osylElement.setAttribute(this.ACCESS_ATTRIBUTE_NAME, SecurityInterface.ACCESS_PUBLIC);
	osylElement.setAttribute("schemaVersion", "1.0");
	osylElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	Element courseOutlineContentElem = document.createElement(CO_NODE_NAME);

	courseOutlineContentElem.setAttribute(ACCESS_ATTRIBUTE_NAME, coContent
		.getAccess());
	courseOutlineContentElem.setAttribute(UUID_ATTRIBUTE_NAME, coContent
		.getUuid());
	if (coContent.getUuidParent() != null)
	    courseOutlineContentElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
		    coContent.getUuidParent());
	courseOutlineContentElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		+ coContent.isEditable());
	osylElement.appendChild(courseOutlineContentElem);
	document.appendChild(osylElement);
	createCDataNode(document, courseOutlineContentElem, CO_LABEL_NODE_NAME,
		coContent.getLabel());
	if (coContent.getProperties() != null
		&& !coContent.getProperties().isEmpty()) {
	    createPropertiesElem(document, courseOutlineContentElem, coContent
		    .getProperties());
	}
	for (int i = 0; i < coContent.getChildrens().size(); i++) {
	    createChildElement(document, courseOutlineContentElem,
		    (COElementAbstract) coContent.getChildrens().get(i));

	}
    }

    /**
     * Create a child node containing a CDATASection value.
     * 
     * @param document the document being created.
     * @param parent the parent element of the CDATASection node.
     * @param nodeName the name given to the text node.
     * @param content the content to put in the CDATASection.
     */
    private void createCDataNode(Document document, Element parent,
	    String nodeName, String content) {
	if (content != null) {
	    Element cDataElement = document.createElement(nodeName);
	    CDATASection cData = document.createCDATASection(content);
	    cDataElement.appendChild(cData);
	    parent.appendChild(cDataElement);
	}
    }

    /**
     * Creates all the children elements of type <code>COElementAbstract</code>
     * in the model. The parent can only be <code>CourseOutlineContent</code>
     * objects or <code>COStructureElement</code> objects following model
     * restrictions.
     * 
     * @param document the document being created.
     * @param parent the parent element to append child element being created.
     * @param child the POJO from which the element is created.
     */
    private void createChildElement(Document document, Element parent,
	    COElementAbstract child) {
	if (child.isCOStructureElement()) {
	    COStructureElement coStructure = (COStructureElement) child;
	    Element coStructureElem =
		    document.createElement(CO_STRUCTURE_NODE_NAME);
	    coStructureElem.setAttribute(ACCESS_ATTRIBUTE_NAME, coStructure
		    .getAccess());
	    coStructureElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, coStructure
		    .getType());
	    coStructureElem.setAttribute(UUID_ATTRIBUTE_NAME, coStructure
		    .getUuid());
	    if (coStructure.getUuidParent() != null)
		coStructureElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coStructure.getUuidParent());
	    for (int i = 0; i < coStructure.getChildrens().size(); i++) {
		createChildElement(document, coStructureElem,
			(COElementAbstract) coStructure.getChildrens().get(i));
	    }
	    if (coStructure.getProperties() != null
		    && !coStructure.getProperties().isEmpty()) {
		createPropertiesElem(document, coStructureElem, coStructure
			.getProperties());
	    }
	    parent.appendChild(coStructureElem);

	} else if (child.isCOUnit()) {
	    COUnit coUnit = (COUnit) child;
	    // create a wrapper on the COUnitContent: a COUnit
	    Element coUnitElem = document.createElement(CO_UNIT_NODE_NAME);
	    parent.appendChild(coUnitElem);
	    coUnitElem.setAttribute(ACCESS_ATTRIBUTE_NAME, coUnit.getAccess());
	    coUnitElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, coUnit.getType());
	    coUnitElem.setAttribute(UUID_ATTRIBUTE_NAME, coUnit.getUuid());
	    if (coUnit.getUuidParent() != null)
		coUnitElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME, coUnit
			.getUuidParent());
	    createCDataNode(document, coUnitElem, CO_LABEL_NODE_NAME, coUnit
		    .getLabel());
	    if (coUnit.getChildrens() != null) {
		for (int i = 0; i < coUnit.getChildrens().size(); i++) {
		    createChildElement(document, coUnitElem, coUnit
			    .getChildrens().get(i));
		}
	    }
	    if (coUnit.getProperties() != null
		    && !coUnit.getProperties().isEmpty()) {
		createPropertiesElem(document, coUnitElem, coUnit
			.getProperties());
	    }
	} else if (child.isCOUnitStructure()) {
	    COUnitStructure coUnitStructure = (COUnitStructure) child;
	    // create a wrapper on the COUnitContent: a COUnit
	    Element coUnitElem =
		    document.createElement(CO_UNIT_STRUCTURE_NODE_NAME);
	    parent.appendChild(coUnitElem);
	    coUnitElem.setAttribute(ACCESS_ATTRIBUTE_NAME, coUnitStructure
		    .getAccess());
	    coUnitElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, coUnitStructure
		    .getType());
	    coUnitElem.setAttribute(UUID_ATTRIBUTE_NAME, coUnitStructure
		    .getUuid());
	    if (coUnitStructure.getUuidParent() != null)
		coUnitElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coUnitStructure.getUuidParent());
	    createCDataNode(document, coUnitElem, CO_LABEL_NODE_NAME,
		    coUnitStructure.getLabel());
	    if (coUnitStructure.getChildrens() != null) {
		for (int i = 0; i < coUnitStructure.getChildrens().size(); i++) {
		    createChildElement(document, coUnitElem, coUnitStructure
			    .getChildrens().get(i));
		}
	    }
	    if (coUnitStructure.getProperties() != null
		    && !coUnitStructure.getProperties().isEmpty()) {
		createPropertiesElem(document, coUnitElem, coUnitStructure
			.getProperties());
	    }
	} else if (child.isCOUnitContent()) {
	    COUnitContent coContentUnit = (COUnitContent) child;
	    Element coContentUnitElem =
		    document.createElement(CO_UNIT_CONTENT_NODE_NAME);
	    parent.appendChild(coContentUnitElem);
	    coContentUnitElem.setAttribute(ACCESS_ATTRIBUTE_NAME, coContentUnit
		    .getAccess());
	    coContentUnitElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, coContentUnit
		    .getType());
	    coContentUnitElem.setAttribute(UUID_ATTRIBUTE_NAME, coContentUnit
		    .getUuid());
	    if (coContentUnit.getUuidParent() != null)
		coContentUnitElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coContentUnit.getUuidParent());
	    // Evaluation attributes
	    // coContentUnitElem.setAttribute(WEIGHT_ATTRIBUTE_NAME,
	    // coContentUnit
	    // .getWeight());
	    // we may have a content unit without any resource proxy, using a
	    // reference to a capsule fro example
	    if (coContentUnit.getChildrens() != null) {
		for (int i = 0; i < coContentUnit.getChildrens().size(); i++) {
		    createChildElement(document, coContentUnitElem,
			    (COContentResourceProxy) coContentUnit
				    .getChildrens().get(i));
		}
	    }
	    // get the COUnitContent properties
	    if (coContentUnit.getProperties() != null
		    && !coContentUnit.getProperties().isEmpty()) {
		createPropertiesElem(document, coContentUnitElem, coContentUnit
			.getProperties());
	    }
	}
    }

    /**
     * @param document the document being created.
     * @param parent the parent element to append properties child to.
     * @param properties the <code>COProperties</code> object.
     */
    private void createPropertiesElem(Document document, Element parent,
	    COProperties properties) {
	Set<String> propertiesSet = properties.keySet();
	Iterator<String> iter = propertiesSet.iterator();
	while (iter.hasNext()) {
	    String propElemName = (String) iter.next();
	    if (!propElemName.equals("#text")) { // TODO find why there is
		// properties named #text
		HashMap<String, String> map = properties.get(propElemName);
		for (Iterator<String> iterMap = map.keySet().iterator(); iterMap
			.hasNext();) {
		    Element propElem = document.createElement(propElemName);
		    String type = iterMap.next();
		    String value = "";
		    Text propElemValue = null;
		    if (!type.equals(COProperties.DEFAULT_PROPERTY_TYPE)) {
			propElem.setAttribute(TYPE_ATTRIBUTE_NAME, type);
			value =
				(String) properties.getProperty(propElemName,
					type);
		    } else {
			value = (String) properties.getProperty(propElemName);
		    }
		    if (propElemName
			    .equalsIgnoreCase(CO_PROPERTIES_TEXT_NODE_NAME)) {
			propElemValue = document.createCDATASection(value);

		    } else {
			propElemValue = document.createTextNode(value);
		    }
		    propElem.appendChild(propElemValue);
		    parent.appendChild(propElem);
		}
	    }
	}
    }

    /**
     * Creates all the children elements of a <code>COContentUnit</code> or a
     * <code>COContentResourceProxy</code>. The children elements are created
     * from a <code>COContentResourceProxy</code>
     * 
     * @param document the document being created.
     * @param parent the parent element (created from a
     *            <code>COContentUnit</code> object or a
     *            <code>COContentResourceProxy</code> object.
     * @param child the POJO needed to create the child element.
     */
    private void createChildElement(Document document, Element parent,
	    COContentResourceProxy child) {
	Element coContentResourceProxyElem =
		document.createElement(CO_RES_PROXY_NODE_NAME);
	coContentResourceProxyElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, child
		.getType());
	coContentResourceProxyElem.setAttribute(ACCESS_ATTRIBUTE_NAME, child
		.getAccess());
	coContentResourceProxyElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		+ child.isEditable());
	// sometimes we don't necessarely have a comment on the resource proxy
	String comment = child.getComment();
	if (comment != null && !"".equals(comment)) {
	    createCDataNode(document, coContentResourceProxyElem,
		    CO_COMMENT_NODE_NAME, child.getComment());
	}
	String label = child.getLabel();
	if (label != null && !"".equals(label)) {
	    createCDataNode(document, coContentResourceProxyElem,
		    CO_LABEL_NODE_NAME, child.getLabel());
	}
	// we may have a content unit without any resource proxy, using a
	// reference to a capsule for example
	if (child.getNestedCOContentResourceProxies() != null) {
	    for (int i = 0; i < child.getNestedCOContentResourceProxies().size(); i++) {
		createChildElement(document, coContentResourceProxyElem,
			(COContentResourceProxy) child.getNestedCOContentResourceProxies().get(i));
	    }
	}
	if (child.getProperties() != null && !child.getProperties().isEmpty()) {
	    createPropertiesElem(document, coContentResourceProxyElem, child
		    .getProperties());
	}
	// We may not have a rubric for exams for example
	if (child.getRubric() != null) {
	    createCOCOntentRubricChild(document, coContentResourceProxyElem,
		    child.getRubric());
	}
	parent.appendChild(coContentResourceProxyElem);
	if (child.getResource() instanceof COContentResource) {
	    COContentResource coContentResource =
		    (COContentResource) child.getResource();
	    createCOContentResourceChild(document, coContentResourceProxyElem,
		    coContentResource, child.getAccess());
	} else {
	    COUnit coUnit = (COUnit) child.getResource();
	    createChildElement(document, coContentResourceProxyElem, coUnit);
	}
    }

    /**
     * Creates a "resource" node child.
     * 
     * @param document the document being created.
     * @param coContentResourceProxyElem the node containing the resource
     *            element.
     * @param resource the <code>COContentResource</code> object from which the
     *            node is created.
     */
    private void createCOContentResourceChild(Document document,
	    Element coContentResourceProxyElem, COContentResource resource,
	    String access) {
	Element coContentResourceElem = null;
	if (resource.getType().equals(COContentResourceType.PERSON)) {
	    coContentResourceElem = document.createElement(PERSON_NODE_NAME);
	} else {
	    coContentResourceElem = document.createElement(CO_RES_NODE_NAME);
	    coContentResourceElem.setAttribute(ACCESS_ATTRIBUTE_NAME, access);
	    coContentResourceElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, resource
		    .getType());

	}
	if (resource.getProperties() != null
		&& !resource.getProperties().isEmpty()) {
	    createPropertiesElem(document, coContentResourceElem, resource
		    .getProperties());
	}
	coContentResourceProxyElem.appendChild(coContentResourceElem);
    }

    /**
     * Creates a "rubric" element.
     * 
     * @param document the document being created.
     * @param coContentResourceProxyElem The lement containing this rubric
     *            element.
     * @param rubric the <code>COContentRubric</code> object needed to create
     *            the contentRubric node.
     */
    private void createCOCOntentRubricChild(Document document,
	    Element coContentResourceProxyElem, COContentRubric rubric) {
	Element coContentRubricElem =
		document.createElement(CO_CONTENT_RUBRIC_NODE_NAME);
	Element coContentRubricTypeElem =
		document.createElement(TYPE_NODE_NAME);
	coContentRubricElem.appendChild(coContentRubricTypeElem);

	Text elemValue = document.createTextNode(rubric.getType());
	coContentRubricTypeElem.appendChild(elemValue);

	if (rubric.getProperties() != null && !rubric.getProperties().isEmpty()) {
	    createPropertiesElem(document, coContentRubricElem, rubric
		    .getProperties());
	}
	coContentResourceProxyElem.appendChild(coContentRubricElem);
    }

    public Map<String, String> getDocumentSecurityMap() {
	return documentSecurityMap;
    }

    public void setDocumentSecurityMap(Map<String, String> documentSecurityMap) {
	this.documentSecurityMap = documentSecurityMap;
    }

    public void associate(COModeledServer parent) {
	COContent contentParent = parent.getModeledContent();
	COContent contentChild = this.getModeledContent();

	if (contentChild == null) {
	    copyStructureOnly(contentParent);
	    this.setModeledContent(contentParent);
	} else {
	    associateChild(contentChild, contentParent);
	}

    }

    private void associateChild(COElementAbstract childElement,
	    COElementAbstract parentElement) {
	if ((parentElement.isCourseOutlineContent() && childElement.isCourseOutlineContent()) || (childElement.getType().equals(parentElement.getType()))) {
	    if (parentElement.isCOUnitContent()) {
		childElement.setUuidParent(parentElement.getUuid());
	    } else {
		childElement.setUuidParent(parentElement.getUuid());
		boolean childrenHasChild =
			childElement.getChildrens() != null
				&& !childElement.getChildrens().isEmpty();
		for (int i = 0; i < parentElement.getChildrens().size(); i++) {
		    COElementAbstract coElementParent =
			    (COElementAbstract) parentElement.getChildrens()
				    .get(i);
		    if (childrenHasChild) {
			COElementAbstract coElementChild =
				(COElementAbstract) childElement.getChildrens()
					.get(i);
			associateChild(coElementChild, coElementParent);
		    } else {
			// Un structure existe chez le parent mais pas chez
			// l'enfant
			copyStructureOnly(coElementParent);
			childElement.addChild(coElementParent);
		    }
		}
	    }
	} else {
	    // throw Error
	    System.err.println("ERREUR DANS L'ASSOCIATION");// TODO
	}
    }

    private void copyStructureOnly(COElementAbstract parentElement) {
	parentElement.setUuidParent(parentElement.getUuid());
	parentElement.setUuid(UUID.uuid());
	if (parentElement.isCOUnitContent()) {
	    COUnitContent coUnit = (COUnitContent) parentElement;
	    coUnit.setChildrens(new ArrayList<COContentResourceProxy>());
	} else {
	    for (int i = 0; i < parentElement.getChildrens().size(); i++) {
		COElementAbstract coElementParent =
			(COElementAbstract) parentElement.getChildrens().get(i);
		copyStructureOnly(coElementParent);
	    }
	}
    }

    public void dissociate(COModeledServer parent) {
	COContent contentParent = parent.getModeledContent();
	COContent contentChild = this.getModeledContent();

	dissociateChild(contentChild, contentParent);

    }

    private void dissociateChild(COElementAbstract childElement,
	    COElementAbstract parentElement) {

	childElement.setUuidParent(null);
	if (parentElement.isCOUnitContent()) {
	    // nothing to do
	} else {
	    for (int i = 0; i < parentElement.getChildrens().size(); i++) {
		COElementAbstract coElementParent =
			(COElementAbstract) parentElement.getChildrens().get(i);
		COElementAbstract coElementChild =
			(COElementAbstract) childElement.getChildrens().get(i);
		dissociateChild(coElementChild, coElementParent);
	    }
	}
    }

    public String getSerializedContent() {
	return coSerialized.getContent();
    }

    public void setSerializedContent(String sc) {
	coSerialized.setContent(sc);
    }

    public void fusion(COModeledServer parent) {
	COContent contentChild = this.getModeledContent();
	COContent contentfusionned = parent.getModeledContent();
	if (contentChild.getUuidParent() != null
		&& !contentChild.getUuidParent().equals("")) {
	    if (contentfusionned != null) {
		contentfusionned.setUuidParent(contentfusionned.getUuid());
		contentfusionned.setUuid(contentChild.getUuid());
	    }
	    fusion(contentChild, contentfusionned);
	    setModeledContent(contentfusionned);
	}
    }

    public void fusion(COElementAbstract child, COElementAbstract fusionned) {
	if (child.isCOUnitContent()) {
	    COUnitContent cuFusionned = (COUnitContent) fusionned;
	    for (int i = 0; i < cuFusionned.getChildrens().size(); i++) {
		COContentResourceProxy rp = cuFusionned.getChildrens().get(i);
		rp.setEditable(false);
	    }

	    COUnitContent cu = (COUnitContent) child;
	    COUnitContent parentCU = (COUnitContent) fusionned;
	    for (int i = 0; i < cu.getChildrens().size(); i++) {
		COContentResourceProxy rp = cu.getChildrens().get(i);
		parentCU.addChild(rp);
	    }
	} else {
	    int j = 0;
	    for (int i = 0; i < child.getChildrens().size(); i++) {
		COElementAbstract childElement =
			(COElementAbstract) child.getChildrens().get(i);
		if (childElement.getUuidParent() != null
			&& !childElement.getUuidParent().equals("")) {
		    COElementAbstract parentElement =
			    fusionned
				    .findCOElementAbstractWithUUID(childElement
					    .getUuidParent());
		    if (parentElement != null) {
			parentElement.setEditable(false);
			parentElement.setUuidParent(parentElement.getUuid());
			parentElement.setUuid(childElement.getUuid());
			fusion(childElement, parentElement);
		    } else {
			// L'enfant référence qqchose qui n'existe PLUS dans le
			// parent
			if (!hasResProx(childElement)) {
			    // il n'a pas d'infos propres, on l'efface
			    child.removeChild(childElement);
			    i--;
			    j--;
			} else {
			    // on supprime les uuidParent et on ajoute le
			    // contenu à la
			    // bonne place
			    deleteParentUuids(childElement);
			    fusionned.getChildrens().add(j, childElement);
			    j++;
			}
		    }
		} else {
		    // L'enfant référence qqchose qui n'existe PAS dans le
		    // parent
		    fusionned.getChildrens().add(j, childElement);
		    j++;
		}
		j++;
	    }
	}
    }

    private void deleteParentUuids(COElementAbstract element) {
	element.setUuidParent(null);
	if (element.isCOUnitContent()) {
	    // Nothing to do
	} else {
	    for (int i = 0; i < element.getChildrens().size(); i++) {
		COElementAbstract coElementChild =
			(COElementAbstract) element.getChildrens().get(i);
		deleteParentUuids(coElementChild);
	    }
	}
    }

    private boolean hasResProx(COElementAbstract element) {

	boolean hasChild = false;
	if (element.isCOUnitContent()) {
	    COUnitContent contentUnit = (COUnitContent) element;
	    hasChild =
		    (contentUnit.getChildrens() != null && !contentUnit
			    .getChildrens().isEmpty());
	} else {
	    for (int i = 0; i < element.getChildrens().size(); i++) {
		COElementAbstract coElementChild =
			(COElementAbstract) element.getChildrens().get(i);
		hasChild = hasChild || hasResProx(coElementChild);
	    }
	}

	return hasChild;
    }

    public void resetUuid() {
	resetUuid(this.getModeledContent());
    }

    private void resetUuid(COElementAbstract element) {
	element.setUuid(UUID.uuid());
	element.setUuidParent(null);
	if (element.isCOUnitContent()) {
	    // Nothing to do
	} else {
	    for (int i = 0; i < element.getChildrens().size(); i++) {
		COElementAbstract childElement =
			(COElementAbstract) element.getChildrens().get(i);
		resetUuid(childElement);
	    }
	}
    }

    public String changeDocumentsUrls(String url, String originalDirectory,
	    String newDirectory) {
	return url.replaceFirst(originalDirectory, newDirectory);
    }

}
