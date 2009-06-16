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
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxyType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnit;
import org.sakaiquebec.opensyllabus.shared.model.COContentUnitType;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
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

	COContent coContent = new COContent();
	Document messageDom = null;
	documentSecurityMap = new HashMap<String, String>();

	try {
	    // XMLtoDOM
	    messageDom = parseXml(coSerialized.getSerializedContent());

	    // DOMtoModel
	    coContent =
		    createCOContentPOJO(messageDom, coContent,
			    changeWorkToPublish);
	} catch (Exception e) {
	    e.printStackTrace();
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
	Element myRoot = messageDom.getDocumentElement();
	Node myNode;
	String nodeName = "";

	coContent.setSecurity(myRoot.getAttribute(SECURITY_ATTRIBUTE_NAME));
	coContent.setType(myRoot.getAttribute(TYPE_ATTRIBUTE_NAME));

	String uuid = myRoot.getAttribute(UUID_ATTRIBUTE_NAME);
	coContent.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent = myRoot.getAttribute(UUID_PARENT_ATTRIBUTE_NAME);
	coContent.setUuidParent(uuidParent);
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
	    } else if (nodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		// Retrieve the label and the unique child of COUnit: a
		// COUnitCOntent
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
				coContentUnit, coContent, label,
				changeWorkToPublish));
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
	    COStructureElement coStructElt, COElementAbstract parent,
	    boolean changeWorkToPublish) {
	Node sNode;
	String sNodeName = "";
	NamedNodeMap sMap = node.getAttributes();
	coStructElt.setSecurity(sMap.getNamedItem(SECURITY_ATTRIBUTE_NAME)
		.getNodeValue());
	coStructElt.setType(sMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
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
	coStructElt.setParent(parent);

	// Retrieve children: can be StructureElement or COUnit
	NodeList strucEltChildren = node.getChildNodes();
	for (int i = 0; i < strucEltChildren.getLength(); i++) {
	    sNode = strucEltChildren.item(i);
	    sNodeName = sNode.getNodeName();
	    COStructureElement coChildStructElt = new COStructureElement();

	    if (sNodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		coStructElt.addChild(createCOStructureElementPOJO(sNode,
			coChildStructElt, coStructElt, changeWorkToPublish));
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
				coUnitNode, coContentUnit, coStructElt, label,
				changeWorkToPublish));
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
     *            content
     * @param coContentUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COContentUnit createCOContentUnitPOJO(Node node,
	    COContentUnit coContentUnit, COElementAbstract parent,
	    String label, boolean changeWorkToPublish) {
	Node coNode;
	String coNodeName = "";
	String coContentUnitType = "";

	NamedNodeMap coMap = node.getAttributes();
	coContentUnitType =
		coMap.getNamedItem(TYPE_ATTRIBUTE_NAME).getNodeValue();
	coContentUnit.setSecurity(coMap.getNamedItem(SECURITY_ATTRIBUTE_NAME)
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
	// Evaluation attributes

	if (COContentUnitType.EVALUATION.equals(coContentUnitType)) {
	    /*
	     * coContentUnit.setWeight(coMap.getNamedItem(WEIGHT_ATTRIBUTE_NAME)
	     * .getNodeValue()); coContentUnit.setLocation(coMap.getNamedItem(
	     * LOCATION_ATTRIBUTE_NAME).getNodeValue());
	     * coContentUnit.setMode(coMap.getNamedItem(MODE_ATTRIBUTE_NAME)
	     * .getNodeValue());coContentUnit.setDateStart(coMap.getNamedItem(
	     * DATESTART_ATTRIBUTE_NAME) .getNodeValue());
	     * coContentUnit.setDateEnd
	     * (coMap.getNamedItem(DATEEND_ATTRIBUTE_NAME) .getNodeValue());
	     * coContentUnit.setResult(coMap.getNamedItem(RESULT_ATTRIBUTE_NAME)
	     * .getNodeValue());
	     * coContentUnit.setScope(coMap.getNamedItem(SCOPE_ATTRIBUTE_NAME)
	     * .getNodeValue());
	     */

	}
	coContentUnit.setParent(parent);

	// label from COUnit
	coContentUnit.setLabel(label);

	NodeList coContentUnitChildren = node.getChildNodes();
	for (int i = 0; i < coContentUnitChildren.getLength(); i++) {
	    coNode = coContentUnitChildren.item(i);
	    coNodeName = coContentUnitChildren.item(i).getNodeName();

	    if (coNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentUnit
			.addResourceProxy(createCOContentResourceProxyPOJO(
				coNode, coContentUnit, changeWorkToPublish));
	    } else if (coNodeName.equalsIgnoreCase(CO_LABEL_NODE_NAME)) {
		coContentUnit.setLabel(getCDataSectionValue(coNode));
	    } else if (coNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coContentUnit.setProperties(createProperties(coNode));
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
	    COContentUnit coContentUnitParent, boolean changeWorkToPublish) {

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
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentResProxy
			.addResourceProxy(createCOContentResourceProxyPOJO(
				prNode, coContentUnitParent,
				changeWorkToPublish));
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
     *            Content Resource
     * @return the created Content resource
     */
    private COContentResource createCOContentResourcePOJO(Node node,
	    boolean changeWorkToPublish) {

	COContentResource coContentRes = new COContentResource();
	NamedNodeMap rMap = node.getAttributes();
	String type = rMap.getNamedItem(TYPE_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setType(type);
	String security =
		rMap.getNamedItem(SECURITY_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setSecurity(security);
	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    String rNodeName = rNode.getNodeName();

	    if (rNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coContentRes.setProperties(createProperties(rNode));
	    }
	}
	if (type.equals(COContentResourceProxyType.DOCUMENT)
		|| type.equals(COContentResourceProxyType.CITATION)) {
	    documentSecurityMap.put(coContentRes.getProperty(
		    COPropertiesType.URI).trim(), security);
	    if (changeWorkToPublish) {
		COProperties copProperties = coContentRes.getProperties();
		copProperties.put(COPropertiesType.URI, this
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

	/*
	 * coContentRubric.setName(map.getNamedItem(NAME_ATTRIBUTE_NAME)
	 * .getNodeValue());
	 */

	for (int i = 0; i < node.getChildNodes().getLength(); i++) {
	    // Node childNode = node.getChildNodes().item(i);
	    // String childNodeName = childNode.getNodeName();
	    // no more description
	    /*
	     * if (childNodeName.equalsIgnoreCase(CO_DESCRIPTION_NODE_NAME)) {
	     * coContentRubric.setDescription(getCDataSectionValue(childNode));
	     * }
	     */
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
	coSerialized.setSerializedContent(xmlToString(dom));
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
	Element courseOutlineContentElem = document.createElement(CO_NODE_NAME);
	courseOutlineContentElem.setAttribute(TYPE_ATTRIBUTE_NAME, coContent
		.getType());
	courseOutlineContentElem.setAttribute(SECURITY_ATTRIBUTE_NAME,
		coContent.getSecurity());
	courseOutlineContentElem.setAttribute(UUID_ATTRIBUTE_NAME, coContent
		.getUuid());
	if (coContent.getUuidParent() != null)
	    courseOutlineContentElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
		    coContent.getUuidParent());
	courseOutlineContentElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		+ coContent.isEditable());
	document.appendChild(courseOutlineContentElem);
	createCDataNode(document, courseOutlineContentElem, CO_LABEL_NODE_NAME,
		coContent.getLabel());
	for (int i = 0; i < coContent.getChildren().size(); i++) {
	    createChildElement(document, courseOutlineContentElem,
		    (COElementAbstract) coContent.getChildren().get(i));

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
	Element cDataElement = document.createElement(nodeName);
	CDATASection cData = document.createCDATASection(content);
	cDataElement.appendChild(cData);
	parent.appendChild(cDataElement);
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
	    COStructureElement coStructureElement = (COStructureElement) child;
	    Element coStructureElem =
		    document.createElement(CO_STRUCTURE_NODE_NAME);
	    coStructureElem.setAttribute(SECURITY_ATTRIBUTE_NAME,
		    coStructureElement.getSecurity());
	    coStructureElem.setAttribute(TYPE_ATTRIBUTE_NAME,
		    coStructureElement.getType());
	    coStructureElem.setAttribute(UUID_ATTRIBUTE_NAME,
		    coStructureElement.getUuid());
	    if (coStructureElement.getUuidParent() != null)
		coStructureElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coStructureElement.getUuidParent());
	    coStructureElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		    + coStructureElement.isEditable());
	    for (int i = 0; i < coStructureElement.getChildren().size(); i++) {
		createChildElement(document, coStructureElem,
			(COElementAbstract) coStructureElement.getChildren()
				.get(i));
	    }
	    if (coStructureElement.getProperties() != null
		    && !coStructureElement.getProperties().isEmpty()) {
		createPropertiesElem(document, coStructureElem,
			coStructureElement.getProperties());
	    }
	    parent.appendChild(coStructureElem);

	} else if (child.isCOContentUnit()) {
	    COContentUnit coContentUnit = (COContentUnit) child;
	    // create a wrapper on the COUnitContent: a COUnit
	    Element coUnitElem = document.createElement(CO_UNIT_NODE_NAME);
	    parent.appendChild(coUnitElem);
	    coUnitElem.setAttribute(SECURITY_ATTRIBUTE_NAME, coContentUnit
		    .getSecurity());
	    coUnitElem.setAttribute(TYPE_ATTRIBUTE_NAME, coContentUnit
		    .getType());
	    coUnitElem.setAttribute(UUID_ATTRIBUTE_NAME, coContentUnit
		    .getUuid());
	    if (coContentUnit.getUuidParent() != null)
		coUnitElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coContentUnit.getUuidParent());
	    coUnitElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		    + coContentUnit.isEditable());
	    createCDataNode(document, coUnitElem, CO_LABEL_NODE_NAME,
		    coContentUnit.getLabel());
	    Element coContentUnitElem =
		    document.createElement(CO_UNIT_CONTENT_NODE_NAME);
	    coContentUnitElem.setAttribute(SECURITY_ATTRIBUTE_NAME,
		    coContentUnit.getSecurity());
	    coContentUnitElem.setAttribute(TYPE_ATTRIBUTE_NAME, coContentUnit
		    .getType());
	    coContentUnitElem.setAttribute(UUID_ATTRIBUTE_NAME, coContentUnit
		    .getUuid());
	    if (coContentUnit.getUuidParent() != null)
		coContentUnitElem.setAttribute(UUID_PARENT_ATTRIBUTE_NAME,
			coContentUnit.getUuidParent());
	    coContentUnitElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		    + coContentUnit.isEditable());
	    // Evaluation attributes
	    // coContentUnitElem.setAttribute(WEIGHT_ATTRIBUTE_NAME,
	    // coContentUnit
	    // .getWeight());
	    // we may have a content unit without any resource proxy, using a
	    // reference to a capsule fro example
	    if (coContentUnit.getResourceProxies() != null) {
		for (int i = 0; i < coContentUnit.getResourceProxies().size(); i++) {
		    createChildElement(document, coContentUnitElem,
			    (COContentResourceProxy) coContentUnit
				    .getResourceProxies().get(i));
		}
	    }
	    // get the COUnitContent properties
	    if (coContentUnit.getProperties() != null
		    && !coContentUnit.getProperties().isEmpty()) {
		createPropertiesElem(document, coContentUnitElem, coContentUnit
			.getProperties());
	    }
	    // Assuming COUnit is the unique parent of COUnitContent

	    coUnitElem.appendChild(coContentUnitElem);
	}
    }

    /**
     * @param document the document being created.
     * @param parent the parent element to append properties child to.
     * @param properties the <code>COProperties</code> object.
     */
    private void createPropertiesElem(Document document, Element parent,
	    COProperties properties) {
	Element propertiesElem =
		document.createElement(CO_PROPERTIES_NODE_NAME);
	Set<String> propertiesSet = properties.keySet();
	Iterator<String> iter = propertiesSet.iterator();
	while (iter.hasNext()) {
	    String propElemName = (String) iter.next();
	    if (!propElemName.equals("#text")) { // TODO find why there is
		// properties named #text
		Element propElem = document.createElement(propElemName);
		Text propElemValue = null;
		if (propElemName.equalsIgnoreCase(CO_PROPERTIES_TEXT_NODE_NAME)) {
		    propElemValue =
			    document.createCDATASection((String) properties
				    .getProperty(propElemName));

		} else {
		    propElemValue =
			    document.createTextNode((String) properties
				    .getProperty(propElemName));
		}
		propElem.appendChild(propElemValue);
		propertiesElem.appendChild(propElem);
	    }
	}
	parent.appendChild(propertiesElem);
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
	coContentResourceProxyElem.setAttribute(TYPE_ATTRIBUTE_NAME, child
		.getType());
	coContentResourceProxyElem.setAttribute(SECURITY_ATTRIBUTE_NAME, child
		.getSecurity());
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
	if (child.getResourceProxies() != null) {
	    for (int i = 0; i < child.getResourceProxies().size(); i++) {
		createChildElement(document, coContentResourceProxyElem,
			(COContentResourceProxy) child.getResourceProxies()
				.get(i));
	    }
	}
	if (child.getProperties() != null && !child.getProperties().isEmpty()) {
	    createPropertiesElem(document, coContentResourceProxyElem, child
		    .getProperties());
	}
	createCOContentResourceChild(document, coContentResourceProxyElem,
		child.getResource(), child.getSecurity());
	// We may not have a rubric for exams for example
	if (child.getRubric() != null) {
	    createCOCOntentRubricChild(document, coContentResourceProxyElem,
		    child.getRubric());
	}
	parent.appendChild(coContentResourceProxyElem);
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
	    String security) {
	Element coContentResourceElem =
		document.createElement(CO_RES_NODE_NAME);
	coContentResourceElem.setAttribute(TYPE_ATTRIBUTE_NAME, resource
		.getType());
	coContentResourceElem.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		+ resource.isEditable());
	// coContentResourceElem.setAttribute(SECURITY_ATTRIBUTE_NAME, resource
	// .getSecurity());
	// TODO on prend pour le moment la securite du resource proxy.
	coContentResourceElem.setAttribute(SECURITY_ATTRIBUTE_NAME, security);
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
	coContentRubricElem.setAttribute(TYPE_ATTRIBUTE_NAME, rubric.getType());
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

	associateChild(contentChild, contentParent);

    }

    private void associateChild(COElementAbstract childElement,
	    COElementAbstract parentElement) {

	if (childElement.getType().equals(parentElement.getType())) {
	    if (parentElement.isCourseOutlineContent()) {
		childElement.setUuidParent(parentElement.getUuid());
		COContent parentCO = (COContent) parentElement;
		COContent childCO = (COContent) childElement;
		boolean childrenHasChild =
			childCO.getChildren() != null
				&& !childCO.getChildren().isEmpty();
		for (int i = 0; i < parentCO.getChildren().size(); i++) {
		    COElementAbstract coElementParent =
			    parentCO.getChildren().get(i);
		    if (childrenHasChild) {
			COElementAbstract coElementChild =
				childCO.getChildren().get(i);
			associateChild(coElementChild, coElementParent);
		    } else {
			// Un structure existe chez le parent mia spas chez
			// l'enfant
			copyStructureOnly(coElementParent);
			childCO.addChild(coElementParent);
		    }
		}
	    } else if (parentElement.isCOStructureElement()) {
		childElement.setUuidParent(parentElement.getUuid());
		COStructureElement parentSE =
			(COStructureElement) parentElement;
		COStructureElement childSE = (COStructureElement) childElement;
		boolean childrenHasChild =
			childSE.getChildren() != null
				&& !childSE.getChildren().isEmpty();
		for (int i = 0; i < parentSE.getChildren().size(); i++) {
		    COElementAbstract coElementParent =
			    parentSE.getChildren().get(i);
		    if (childrenHasChild) {
			COElementAbstract coElementChild =
				childSE.getChildren().get(i);
			associateChild(coElementChild, coElementParent);
		    } else {
			// Un structure existe chez le parent mia spas chez
			// l'enfant
			copyStructureOnly(coElementParent);
			childSE.addChild(coElementParent);
		    }
		}
	    } else if (parentElement.isCOContentUnit()) {
		childElement.setUuidParent(parentElement.getUuid());
	    }
	} else {
	    // throw Error
	    System.err.println("ERREUR DANS L'ASSOCIATION");// TODO
	}
    }

    private void copyStructureOnly(COElementAbstract parentElement) {
	parentElement.setUuidParent(parentElement.getUuid());
	parentElement.setUuid(UUID.uuid());
	if (parentElement.isCOStructureElement()) {
	    COStructureElement parentSE = (COStructureElement) parentElement;
	    for (int i = 0; i < parentSE.getChildren().size(); i++) {
		COElementAbstract coElementParent =
			parentSE.getChildren().get(i);
		copyStructureOnly(coElementParent);
	    }
	} else if (parentElement.isCOContentUnit()) {
	    COContentUnit coUnit = (COContentUnit) parentElement;
	    coUnit.setResourceProxies(new ArrayList<COContentResourceProxy>());
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
	if (parentElement.isCourseOutlineContent()) {
	    COContent parentCO = (COContent) parentElement;
	    COContent childCO = (COContent) childElement;
	    for (int i = 0; i < parentCO.getChildren().size(); i++) {
		COElementAbstract coElementParent =
			parentCO.getChildren().get(i);
		COElementAbstract coElementChild = childCO.getChildren().get(i);
		dissociateChild(coElementChild, coElementParent);
	    }
	} else if (parentElement.isCOStructureElement()) {
	    COStructureElement parentSE = (COStructureElement) parentElement;
	    COStructureElement childSE = (COStructureElement) childElement;
	    for (int i = 0; i < parentSE.getChildren().size(); i++) {
		COElementAbstract coElementParent =
			parentSE.getChildren().get(i);
		COElementAbstract coElementChild = childSE.getChildren().get(i);
		dissociateChild(coElementChild, coElementParent);
	    }
	} else if (parentElement.isCOContentUnit()) {
	}
    }

    public String getSerializedContent() {
	return coSerialized.getSerializedContent();
    }

    public void fusion(COModeledServer parent) {
	COContent contentChild = this.getModeledContent();
	COContent contentfusionned = parent.getModeledContent();
	if (contentChild.getUuidParent() != null
		&& !contentChild.getUuidParent().equals("")) {
	    fusion(contentChild, contentfusionned);
	    setModeledContent(contentfusionned);
	}
    }

    public void fusion(COElementAbstract child, COElementAbstract fusionned) {
	if (child.isCourseOutlineContent()) {
	    COContent co = (COContent) child;
	    if (fusionned != null) {
		fusionned.setUuidParent(fusionned.getUuid());
		fusionned.setUuid(child.getUuid());
	    }
	    int j = 0;
	    for (int i = 0; i < co.getChildren().size(); i++) {
		COElementAbstract childElement = co.getChildren().get(i);
		if (childElement.getUuidParent() != null
			&& !childElement.getUuidParent().equals("")) {
		    COElementAbstract parentElement =
			    findCOElementAbstractWithUUID(fusionned,
				    childElement.getUuidParent());
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
			    co.removeChild(childElement);
			    i--;
			    j--;
			} else {
			    // on supprime les uuidParent et on ajoute le
			    // contenu à la
			    // bonne place
			    deleteParentUuids(childElement);
			    COContent coFussioned = (COContent) fusionned;
			    coFussioned.getChildren().add(j, childElement);
			    j++;
			}
		    }
		} else {
		    // L'enfant référence qqchose qui n'existe PAS dans le
		    // parent
		    COContent coFussioned = (COContent) fusionned;
		    coFussioned.getChildren().add(j, childElement);
		    j++;
		}
	    }
	    j++;
	} else if (child.isCOStructureElement()) {
	    COStructureElement se = (COStructureElement) child;
	    int j = 0;
	    for (int i = 0; i < se.getChildren().size(); i++) {
		COElementAbstract childElement = se.getChildren().get(i);
		if (childElement.getUuidParent() != null
			&& !childElement.getUuidParent().equals("")) {
		    COElementAbstract parentElement =
			    findCOElementAbstractWithUUID(fusionned,
				    childElement.getUuidParent());
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
			    se.removeChild(childElement);
			    i--;
			    j--;
			} else {
			    // on supprime les uuidParent et on ajoute le
			    // contenu à la
			    // bonne place
			    deleteParentUuids(childElement);
			    COStructureElement coSeFussioned =
				    (COStructureElement) fusionned;
			    coSeFussioned.getChildren().add(i, childElement);
			    j++;
			}
		    }
		} else {
		    // L'enfant référence qqchose qui n'existe PAS dans le
		    // parent
		    COStructureElement coSeFussioned =
			    (COStructureElement) fusionned;
		    coSeFussioned.getChildren().add(i, childElement);
		    j++;
		}
	    }
	    j++;
	} else if (child.isCOContentUnit()) {
	    COContentUnit cuFusionned = (COContentUnit) fusionned;
	    for (int i = 0; i < cuFusionned.getResourceProxies().size(); i++) {
		COContentResourceProxy rp =
			cuFusionned.getResourceProxies().get(i);
		rp.setEditable(false);
	    }

	    COContentUnit cu = (COContentUnit) child;
	    COContentUnit parentCU = (COContentUnit) fusionned;
	    for (int i = 0; i < cu.getResourceProxies().size(); i++) {
		COContentResourceProxy rp = cu.getResourceProxies().get(i);
		parentCU.addResourceProxy(rp);
	    }
	}
    }

    private void deleteParentUuids(COElementAbstract element) {
	element.setUuidParent(null);
	if (element.isCourseOutlineContent()) {
	    COContent childCO = (COContent) element;
	    for (int i = 0; i < childCO.getChildren().size(); i++) {
		COElementAbstract coElementChild = childCO.getChildren().get(i);
		deleteParentUuids(coElementChild);
	    }
	} else if (element.isCOStructureElement()) {
	    COStructureElement childSE = (COStructureElement) element;
	    for (int i = 0; i < childSE.getChildren().size(); i++) {
		COElementAbstract coElementChild = childSE.getChildren().get(i);
		deleteParentUuids(coElementChild);
	    }
	} else if (element.isCOContentUnit()) {
	}
    }

    private boolean hasResProx(COElementAbstract element) {

	boolean hasChild = false;
	if (element.isCourseOutlineContent()) {
	    COContent childCO = (COContent) element;
	    for (int i = 0; i < childCO.getChildren().size(); i++) {
		COElementAbstract coElementChild = childCO.getChildren().get(i);
		hasChild = hasChild || hasResProx(coElementChild);
	    }
	} else if (element.isCOStructureElement()) {
	    COStructureElement childSE = (COStructureElement) element;
	    for (int i = 0; i < childSE.getChildren().size(); i++) {
		COElementAbstract coElementChild = childSE.getChildren().get(i);
		hasChild = hasChild || hasResProx(coElementChild);
	    }
	} else if (element.isCOContentUnit()) {
	    COContentUnit contentUnit = (COContentUnit) element;
	    hasChild =
		    (contentUnit.getResourceProxies() != null && !contentUnit
			    .getResourceProxies().isEmpty());
	}

	return hasChild;
    }

    private COElementAbstract findCOElementAbstractWithUUID(
	    COElementAbstract root, String uuid) {
	COElementAbstract result = null;
	if (root.getUuid().equals(uuid))
	    result = root;
	else {
	    if (root.isCourseOutlineContent()) {
		COContent co = (COContent) root;
		int i = 0;
		while (i < co.getChildren().size() && result == null) {
		    COElementAbstract coElem = co.getChildren().get(i);
		    result = findCOElementAbstractWithUUID(coElem, uuid);
		    i++;
		}
	    } else if (root.isCOStructureElement()) {
		COStructureElement se = (COStructureElement) root;
		int i = 0;
		while (i < se.getChildren().size() && result == null) {
		    COElementAbstract coElem = se.getChildren().get(i);
		    result = findCOElementAbstractWithUUID(coElem, uuid);
		    i++;
		}
	    } else if (root.isCOContentUnit()) {
		// Nothing to do
	    }
	}
	return result;
    }

    public void resetUuid() {
	resetUuid(this.getModeledContent());
    }

    private void resetUuid(COElementAbstract element) {
	element.setUuid(UUID.uuid());
	if (element.isCourseOutlineContent()) {
	    COContent co = (COContent) element;
	    for (int i = 0; i < co.getChildren().size(); i++) {
		COElementAbstract childElement = co.getChildren().get(i);
		resetUuid(childElement);
	    }
	} else if (element.isCOStructureElement()) {
	    COStructureElement se = (COStructureElement) element;
	    for (int i = 0; i < se.getChildren().size(); i++) {
		COElementAbstract childElement = se.getChildren().get(i);
		resetUuid(childElement);
	    }
	} else if (element.isCOContentUnit()) {

	}
    }

    public String changeDocumentsUrls(String url, String originalDirectory,
	    String newDirectory) {
	return url.replaceFirst(originalDirectory, newDirectory);
    }

}
