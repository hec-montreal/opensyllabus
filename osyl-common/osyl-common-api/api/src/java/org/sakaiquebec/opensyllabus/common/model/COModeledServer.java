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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COContent;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceProxy;
import org.sakaiquebec.opensyllabus.shared.model.COContentResourceType;
import org.sakaiquebec.opensyllabus.shared.model.COContentRubric;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COProperty;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.COUnitContent;
import org.sakaiquebec.opensyllabus.shared.model.COUnitStructure;
import org.sakaiquebec.opensyllabus.shared.util.UUID;
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
     * Name of the text property node.
     */
    protected final static String CO_PROPERTIES_TEXT_NODE_NAME = "text";

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
    protected final static String ID_ATTRIBUTE_NAME = "id";

    /**
     *Name of uuid parent attribute
     */
    protected final static String ID_PARENT_ATTRIBUTE_NAME = "id_parent";

    /**
     *Name of uuid attribute
     */
    protected final static String EDITABLE_ATTRIBUTE_NAME = "editable";

    /**
     *Name of person node
     */
    protected final static String PERSON_NODE_NAME = "Person";

    /**
     *Name of modified node
     */
    protected final static String MODIFIED_NODE_NAME = "modified";

    private static final String XML_VERSION_ATTRIBUTE = "schemaVersion";;

    /**
     * The modeledContent is a POJO filled by XML2Model
     */
    private COContent modeledContent;

    /**
     *Name of element node in rules.xml
     */
    protected static final String ELEMENT_NODE_NAME = "element";

    /**
     *Name of restriction pattern attribute in rules.xml
     */
    protected static final String RESTRICTION_PATTERN_ATTRIBUTE_NAME =
	    "restrictionpattern";

    /**
     *Name of attribute name in rules.xml
     */
    protected static final String NAME_ATTRIBUTE_NAME = "name";

    /**
     *Name of attribute node in rules.xml
     */
    protected static final String ATTRIBUTE_NODE_NAME = "attribute";

    /**
     *Name of property type attribute in rules.xml
     */
    protected static final String PROPERTY_TYPE_ATTRIBUTE_NAME = "propertyType";

    private String schemaVersion;


    /**
     * Map<name,permissions> of permissions applied to ressources
     * 
     * @uml.property name="documentSecurityMap"
     * @uml.associationEnd qualifier="trim:java.lang.String java.lang.String"
     */
    private Map<String, String> documentSecurityMap;

    /**
     * Map<name,visibility> of visibility applied to ressources
     * 
     * @uml.property name="documentVisibilityMap"
     * @uml.associationEnd qualifier="trim:java.lang.String java.lang.String"
     */
    private Map<String, String> documentVisibilityMap;

    private String propertyType;

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

    public void XML2Model(boolean isPublication) {
	COContent coContent = null;
	Document messageDom = null;
	documentSecurityMap = new HashMap<String, String>();
	documentVisibilityMap = new HashMap<String, String>();
	if (coSerialized.getContent() != null) {
	    coContent = new COContent();
	    try {
		// XMLtoDOM
		messageDom = parseXml(coSerialized.getContent());

		schemaVersion =
			messageDom.getDocumentElement().getAttribute(
				XML_VERSION_ATTRIBUTE);

		// DOMtoModel
		coContent =
			createCOContentPOJO(messageDom, coContent,
				isPublication);
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

    private void setCommonAttributes(COModelInterface elem, Node node) {
	NamedNodeMap map = node.getAttributes();

	elem.setAccess(map.getNamedItem(ACCESS_ATTRIBUTE_NAME).getNodeValue());

	elem.setId(map.getNamedItem(ID_ATTRIBUTE_NAME) == null ? UUID.uuid()
		: map.getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue());

	elem
		.setEditable(map.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? true
			: Boolean.valueOf(map.getNamedItem(
				EDITABLE_ATTRIBUTE_NAME).getNodeValue()));

	elem.setType(map.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME) == null ? null
		: map.getNamedItem(XSI_TYPE_ATTRIBUTE_NAME).getNodeValue());

	if (elem instanceof COElementAbstract) {
	    COElementAbstract coElementAbstract = (COElementAbstract) elem;
	    coElementAbstract
		    .setIdParent(map.getNamedItem(ID_PARENT_ATTRIBUTE_NAME) == null ? null
			    : map.getNamedItem(ID_PARENT_ATTRIBUTE_NAME)
				    .getNodeValue());
	}
    }

    /**
     * Creates a COContent POJO from the root of the xml document.
     * 
     * @param document the document being created
     * @param coContent the POJO to be created from the root element.
     */
    private COContent createCOContentPOJO(Document messageDom,
	    COContent coContent, boolean isPublication) {

	Node myRoot = null;

	NodeList nodeList = messageDom.getDocumentElement().getChildNodes();

	for (int i = 0; i < nodeList.getLength(); i++) {
	    Node node = nodeList.item(i);

	    if (CO_NODE_NAME.equals(node.getNodeName())) {
		myRoot = node;
	    }
	}

	Node myNode;
	String nodeName = "";

	setCommonAttributes(coContent, myRoot);

	// Retrieve children: can be StructureElement or COUnit as well as the
	// subnode label
	NodeList rootChildren = myRoot.getChildNodes();

	for (int i = 0; i < rootChildren.getLength(); i++) {
	    myNode = rootChildren.item(i);
	    nodeName = myNode.getNodeName();
	    try {
		if (nodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		    COStructureElement coStructElt = new COStructureElement();
		    coContent.getChildrens().add(
			    createCOStructureElementPOJO(myNode, coStructElt,
				    coContent, isPublication));

		} else {
		    addProperty(coContent.getProperties(), myNode);
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return coContent;
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
	    addAttributes(coProperties, namedNodeMap, key, type);
	}
    }

    private void addAttributes(COProperties coProperties,
	    NamedNodeMap namedNodeMap, String key, String type) {
	if (type == null) {
	    type = COProperties.DEFAULT_PROPERTY_TYPE;
	}
	COProperty coProperty = coProperties.getCOProperty(key, type);

	for (int i = 0; i < namedNodeMap.getLength(); i++) {
	    Node item = namedNodeMap.item(i);
	    coProperty.addAttribute(item.getNodeName(), item.getNodeValue());
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
	    COElementAbstract parent, boolean isPublication) {
	Node sNode;
	String sNodeName = "";

	setCommonAttributes(coUnit, node);

	coUnit.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();
	    try {
		if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		    COUnitStructure coUnitStruct = new COUnitStructure();
		    coUnit.addChild(createCOUnitStructurePOJO(sNode,
			    coUnitStruct, coUnit, isPublication));
		} else {
		    addProperty(coUnit.getProperties(), sNode);
		}
	    } catch (Exception e) {
		e.printStackTrace();
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
	    boolean isPublication) {
	Node sNode;
	String sNodeName = "";

	setCommonAttributes(coUnitStructure, node);

	coUnitStructure.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();
	    try {
		if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		    COUnitStructure coUnitStructure2 = new COUnitStructure();
		    coUnitStructure.addChild(createCOUnitStructurePOJO(sNode,
			    coUnitStructure2, coUnitStructure, isPublication));
		} else if (sNodeName
			.equalsIgnoreCase(CO_UNIT_CONTENT_NODE_NAME)) {
		    COUnitContent coContentUnit = new COUnitContent();
		    coUnitStructure.addChild(createCOContentUnitPOJO(sNode,
			    coContentUnit, coUnitStructure, isPublication));
		} else {
		    // properties
		    addProperty(coUnitStructure.getProperties(), sNode);
		}
	    } catch (Exception e) {
		e.printStackTrace();
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
	    boolean isPublication) {
	Node sNode;
	String sNodeName = "";

	setCommonAttributes(coStructElt, node);

	coStructElt.setParent(parent);

	// Retrieve children: can be StructureElement or COUnit
	NodeList strucEltChildren = node.getChildNodes();
	for (int i = 0; i < strucEltChildren.getLength(); i++) {
	    sNode = strucEltChildren.item(i);
	    sNodeName = sNode.getNodeName();
	    try {
		if (sNodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		    COStructureElement coChildStructElt =
			    new COStructureElement();
		    coStructElt.getChildrens().add(
			    createCOStructureElementPOJO(sNode,
				    coChildStructElt, coStructElt,
				    isPublication));
		} else if (sNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		    COUnit coUnit = new COUnit();
		    coStructElt.addChild(createCOUnitPOJO(sNode, coUnit,
			    coStructElt, isPublication));
		} else {
		    addProperty(coStructElt.getProperties(), sNode);
		}
	    } catch (Exception e) {
		e.printStackTrace();
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
	    boolean isPublication) {
	Node coNode;
	String coNodeName = "";

	setCommonAttributes(coContentUnit, node);

	coContentUnit.setParent(parent);

	NodeList coContentUnitChildren = node.getChildNodes();
	for (int i = 0; i < coContentUnitChildren.getLength(); i++) {
	    coNode = coContentUnitChildren.item(i);
	    coNodeName = coContentUnitChildren.item(i).getNodeName();
	    try {
		if (coNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		    coContentUnit.addChild(createCOContentResourceProxyPOJO(
			    coNode, coContentUnit, isPublication));
		} else {
		    addProperty(coContentUnit.getProperties(), coNode);
		}
	    } catch (Exception e) {
		e.printStackTrace();
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
	    COUnitContent coContentUnitParent, boolean isPublication) {

	Node prNode;
	String prNodeName = "";

	COContentResourceProxy coContentResProxy = new COContentResourceProxy();

	try {
	    setCommonAttributes(coContentResProxy, node);

	    coContentResProxy.setParent(coContentUnitParent);

	    NodeList resProxyChildren = node.getChildNodes();
	    for (int j = 0; j < resProxyChildren.getLength(); j++) {
		prNode = resProxyChildren.item(j);
		prNodeName = prNode.getNodeName();
		if (prNodeName.equalsIgnoreCase(COPropertiesType.SEMANTIC_TAG)) {
		    coContentResProxy
			    .setRubric(createCOContentRubricPOJO(prNode));
		} else if (prNodeName.equalsIgnoreCase(CO_RES_NODE_NAME)) {
		    coContentResProxy.setResource(createCOContentResourcePOJO(
			    prNode, isPublication));
		} else if (prNodeName.equalsIgnoreCase(PERSON_NODE_NAME)) {
		    coContentResProxy
			    .setResource(createCOContentResourcePersonPOJO(prNode));
		} else if (prNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		    coContentResProxy
			    .addNestedResourceProxy(createCOContentResourceProxyPOJO(
				    prNode, coContentUnitParent, isPublication));
		} else if (prNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		    COUnit coUnit = new COUnit();
		    coContentResProxy.setResource(createCOUnitPOJO(prNode,
			    coUnit, coContentResProxy, isPublication));
		} else {
		    addProperty(coContentResProxy.getProperties(), prNode);
		}

	    }

	    String uri =
		    coContentResProxy.getResource().getProperty(
			    COPropertiesType.IDENTIFIER,
			    COPropertiesType.IDENTIFIER_TYPE_URI);

	    String visibility =
		    coContentResProxy.getProperty(COPropertiesType.VISIBILITY);

	    if (coContentResProxy.getResource().getType().equals(
		    COContentResourceType.DOCUMENT))
		documentVisibilityMap.put(uri.trim(), visibility);
	    else if (coContentResProxy.getResource().getType().equals(
		    COContentResourceType.BIBLIO_RESOURCE)
		    && uri != null)
		documentVisibilityMap.put(uri
			.substring(0, uri.lastIndexOf("/")), visibility);
	} catch (Exception e) {
	    e.printStackTrace();
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
    /**
     * @param node
     * @param isPublication
     * @return
     */
    private COContentResource createCOContentResourcePOJO(Node node,
	    boolean isPublication) {

	COContentResource coContentRes = new COContentResource();

	setCommonAttributes(coContentRes, node);
	try {
	    NodeList resChildren = node.getChildNodes();
	    for (int z = 0; z < resChildren.getLength(); z++) {
		Node rNode = resChildren.item(z);
		addProperty(coContentRes.getProperties(), rNode);
	    }

	    if (coContentRes.getType().equals(COContentResourceType.DOCUMENT)) {
		String uri =
			coContentRes.getProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URI);
		if (uri != null) {
		    documentSecurityMap.put(uri.trim(), coContentRes
			    .getAccess());
		    if (isPublication) {
			COProperties copProperties =
				coContentRes.getProperties();
			copProperties
				.addProperty(
					COPropertiesType.IDENTIFIER,
					COPropertiesType.IDENTIFIER_TYPE_URI,
					this
						.changeDocumentsUrls(
							coContentRes
								.getProperty(
									COPropertiesType.IDENTIFIER,
									COPropertiesType.IDENTIFIER_TYPE_URI)
								.trim(),
							OsylSiteService.WORK_DIRECTORY,
							OsylSiteService.PUBLISH_DIRECTORY));
			coContentRes.setProperties(copProperties);
		    }
		}
	    } else if (coContentRes.getType().equals(
		    COContentResourceType.BIBLIO_RESOURCE)) {
		String uri =
			coContentRes.getProperty(COPropertiesType.IDENTIFIER,
				COPropertiesType.IDENTIFIER_TYPE_URI);
		if (uri != null) {
		    documentSecurityMap.put(uri.substring(0, uri
			    .lastIndexOf("/")), coContentRes.getAccess());
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
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

	setCommonAttributes(coContentRes, node);

	coContentRes.setType(COContentResourceType.PERSON);

	try {
	    NodeList resChildren = node.getChildNodes();
	    for (int z = 0; z < resChildren.getLength(); z++) {
		Node rNode = resChildren.item(z);
		addProperty(coContentRes.getProperties(), rNode);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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
	NamedNodeMap namedNodeMap = node.getAttributes();
	try {
	    String type =
		    (namedNodeMap == null) ? null : (namedNodeMap
			    .getNamedItem(TYPE_ATTRIBUTE_NAME) == null) ? null
			    : namedNodeMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
				    .getNodeValue();

	    String userDefLabel =
		    (namedNodeMap == null) ? null
			    : (namedNodeMap
				    .getNamedItem(COPropertiesType.SEMANTIC_TAG_USERDEFLABEL) == null) ? null
				    : namedNodeMap
					    .getNamedItem(
						    COPropertiesType.SEMANTIC_TAG_USERDEFLABEL)
					    .getNodeValue();
	    String value = "";

	    for (int j = 0; j < node.getChildNodes().getLength(); j++) {
		value += node.getChildNodes().item(j).getNodeValue();
	    }
	    coContentRubric.setType(value);
	    if (userDefLabel != null) {
		coContentRubric.setUserDefLabel(userDefLabel);
	    }
	    coContentRubric.setKey(type);

	} catch (Exception e) {
	    e.printStackTrace();
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
	    createRootElement(dom, getModeledContent());
	    coSerialized.setContent(xmlToString(dom));

	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	}
	
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

    private void setCommonAttributesAndProperties(Element element,
	    COModelInterface modelInterface, Document document) {

	try {
	    element.setAttribute(ACCESS_ATTRIBUTE_NAME, modelInterface
		    .getAccess());
	    element.setAttribute(ID_ATTRIBUTE_NAME, modelInterface.getId());
	    element.setAttribute(EDITABLE_ATTRIBUTE_NAME, ""
		    + modelInterface.isEditable());

	    if (modelInterface.getType() != null) {
		element.setAttribute(XSI_TYPE_ATTRIBUTE_NAME, modelInterface
			.getType());
	    }

	    if (modelInterface instanceof COElementAbstract) {
		COElementAbstract coElementAbstract =
			(COElementAbstract) modelInterface;
		if (coElementAbstract.getIdParent() != null)
		    element.setAttribute(ID_PARENT_ATTRIBUTE_NAME,
			    coElementAbstract.getIdParent());
	    }
	    // Properties
	    if (modelInterface.getProperties() != null
		    && !modelInterface.getProperties().isEmpty()) {
		createPropertiesElem(document, element, modelInterface
			.getProperties());
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Creates the root element of the document.
     * 
     * @param document the document being created
     * @param coContent the POJO needed to create the root element.
     */
    private void createRootElement(Document document, COContent coContent) {

	Element osylElement = document.createElement("OSYL");
	osylElement.setAttribute(ACCESS_ATTRIBUTE_NAME,
		SecurityInterface.ACCESS_PUBLIC);
	osylElement.setAttribute("schemaVersion", schemaVersion);
	osylElement.setAttribute("xmlns:xsi",
		XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
	Element courseOutlineContentElem = document.createElement(CO_NODE_NAME);

	try {
	    setCommonAttributesAndProperties(courseOutlineContentElem,
		    coContent, document);

	    osylElement.appendChild(courseOutlineContentElem);
	    document.appendChild(osylElement);

	    for (int i = 0; i < coContent.getChildrens().size(); i++) {
		createChildElement(document, courseOutlineContentElem,
			(COElementAbstract) coContent.getChildrens().get(i));

	    }

	} catch (Exception e) {
	    e.printStackTrace();
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
	
	try {
	    if (child instanceof COContentResourceProxy) {
		createChildElement(document, parent,
			(COContentResourceProxy) child);
	    } else {
		Element element = null;
		if (child.isCOStructureElement()) {
		    element = document.createElement(CO_STRUCTURE_NODE_NAME);
		} else if (child.isCOUnit()) {
		    element = document.createElement(CO_UNIT_NODE_NAME);

		} else if (child.isCOUnitStructure()) {
		    element =
			    document.createElement(CO_UNIT_STRUCTURE_NODE_NAME);

		} else if (child.isCOUnitContent()) {
		    element = document.createElement(CO_UNIT_CONTENT_NODE_NAME);
		}
		setCommonAttributesAndProperties(element, child, document);

		for (int i = 0; i < child.getChildrens().size(); i++) {
		    createChildElement(document, element,
			    (COElementAbstract) child.getChildrens().get(i));
		}
		parent.appendChild(element);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
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
	
	try {
	    while (iter.hasNext()) {
		String propElemName = (String) iter.next();
		if (!propElemName.equals("#text")) { // TODO find why there is
		    // properties named #text
		    HashMap<String, COProperty> map =
			    properties.get(propElemName);
		    for (Iterator<String> iterMap = map.keySet().iterator(); iterMap
			    .hasNext();) {
			Element propElem = document.createElement(propElemName);
			String type = iterMap.next();
			String value = "";
			Text propElemValue = null;
			if (!type.equals(COProperties.DEFAULT_PROPERTY_TYPE)) {
			    propElem.setAttribute(TYPE_ATTRIBUTE_NAME, type);
			    value = properties.getProperty(propElemName, type);
			} else {
			    value = properties.getProperty(propElemName);
			}
			createPropElemAttributes(map.get(type), propElem);

			if (COPropertiesType.CDATA_NODE_NAMES
				.contains(propElemName)) {
			    propElemValue = document.createCDATASection(value);
			} else {
			    propElemValue = document.createTextNode(value);
			}
			propElem.appendChild(propElemValue);
			parent.appendChild(propElem);
		    }
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void createPropElemAttributes(COProperty coProperty,
	    Element propElem) {
	try {
	    for (String key : coProperty.getAttributes().keySet()) {
		propElem.setAttribute(key, coProperty.getAttribute(key));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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

	setCommonAttributesAndProperties(coContentResourceProxyElem, child,
		document);

	try {
	    if (child.getNestedCOContentResourceProxies() != null) {
		for (int i = 0; i < child.getNestedCOContentResourceProxies()
			.size(); i++) {
		    createChildElement(document, coContentResourceProxyElem,
			    (COContentResourceProxy) child
				    .getNestedCOContentResourceProxies().get(i));
		}
	    }
	    for (String rubricKey : child.getRubrics().keySet()) {
		createCOCOntentRubricChild(document,
			coContentResourceProxyElem, child.getRubric(rubricKey));
	    }
	    parent.appendChild(coContentResourceProxyElem);
	    if (child.getResource() instanceof COContentResource) {
		COContentResource coContentResource =
			(COContentResource) child.getResource();
		createCOContentResourceChild(document,
			coContentResourceProxyElem, coContentResource);
	    } else {
		COUnit coUnit = (COUnit) child.getResource();
		createChildElement(document, coContentResourceProxyElem, coUnit);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
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
	    Element coContentResourceProxyElem, COContentResource resource) {
	Element coContentResourceElem = null;
	
	try {
	    if (resource.getType().equals(COContentResourceType.PERSON)) {
		coContentResourceElem =
			document.createElement(PERSON_NODE_NAME);
	    } else {
		coContentResourceElem =
			document.createElement(CO_RES_NODE_NAME);
		coContentResourceElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME,
			resource.getType());

	    }
	    coContentResourceElem.setAttribute(ACCESS_ATTRIBUTE_NAME, resource
		    .getAccess());
	    coContentResourceElem.setAttribute(ID_ATTRIBUTE_NAME, resource
		    .getId());
	    if (resource.getProperties() != null
		    && !resource.getProperties().isEmpty()) {
		createPropertiesElem(document, coContentResourceElem, resource
			.getProperties());
	    }
	    coContentResourceProxyElem.appendChild(coContentResourceElem);

	} catch (Exception e) {
	    e.printStackTrace();
	}
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
	Element coContentRubricElem = document.createElement(COPropertiesType.SEMANTIC_TAG);
	
	try {
	    coContentRubricElem.setAttribute(TYPE_ATTRIBUTE_NAME, rubric
		    .getKey());
	    if (rubric.getUserDefLabel() != null
		    && rubric.getUserDefLabel().length() > 0) {
		coContentRubricElem.setAttribute(
			COPropertiesType.SEMANTIC_TAG_USERDEFLABEL, rubric
				.getUserDefLabel());
	    }

	    Text elemValue = document.createTextNode(rubric.getType());
	    coContentRubricElem.appendChild(elemValue);

	    coContentResourceProxyElem.appendChild(coContentRubricElem);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Map<String, String> getDocumentSecurityMap() {
	return documentSecurityMap;
    }

    public void setDocumentSecurityMap(Map<String, String> documentSecurityMap) {
	this.documentSecurityMap = documentSecurityMap;
    }

    public Map<String, String> getDocumentVisibilityMap() {
	return documentVisibilityMap;
    }

    public void setDocumentVisibilityMap(
	    Map<String, String> documentVisibilityMap) {
	this.documentVisibilityMap = documentVisibilityMap;
    }

    public void associate(COModeledServer parent) throws Exception {
	COContent contentParent = parent.getModeledContent();
	COContent contentChild = this.getModeledContent();

	if (contentChild == null) {
	    copyStructureOnly(contentParent);
	    this.setModeledContent(contentParent);
	    this.schemaVersion = parent.schemaVersion;
	} else {
	    if (this.schemaVersion.equals(parent.schemaVersion))
		associateChild(contentChild, contentParent);
	    else
		throw new Exception(
			"COuld not associate: child schema version != parent schema version");
	}

    }

    @SuppressWarnings("unchecked")
    private void associateChild(COElementAbstract childElement,
	    COElementAbstract parentElement) throws Exception {
	if ((parentElement.isCourseOutlineContent() && childElement
		.isCourseOutlineContent())
		|| (childElement.getType().equals(parentElement.getType()))) {

	    if (childElement.getIdParent() == null
		    || childElement.getIdParent().equals(""))
		childElement.setIdParent(parentElement.getId());
	    else if (!childElement.getIdParent().equals(parentElement.getId())) {
		throw new Exception("Element " + childElement.getType()
			+ " with id " + childElement.getId()
			+ " is already associated with a parent(id "
			+ childElement.getIdParent()
			+ ") and could not be associated with parent (id "
			+ parentElement.getId() + ").");
	    }
	    if (!parentElement.isCOUnitContent()) {
		childElement.setIdParent(parentElement.getId());
		for (int i = 0; i < parentElement.getChildrens().size(); i++) {
		    COElementAbstract coElementParent =
			    (COElementAbstract) parentElement.getChildrens()
				    .get(i);
		    if (childElement.getChildrens() != null
			    && childElement.getChildrens().size() >= i + 1) {
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
	    throw new Exception(
		    "Error while associate course outlines : type '"
			    + childElement.getType()
			    + "' could not be associate with type '"
			    + parentElement.getType() + "'.");
	}
    }

    private void copyStructureOnly(COElementAbstract parentElement) {
	parentElement.setIdParent(parentElement.getId());
	parentElement.setId(UUID.uuid());
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

	childElement.setIdParent(null);
	if (parentElement.isCOUnitContent()) {
	    // nothing to do
	} else {
	    for (int i = 0; i < parentElement.getChildrens().size(); i++) {
		COElementAbstract coElementParent =
			(COElementAbstract) parentElement.getChildrens().get(i);
		if (childElement.getChildrens().size() > i) {
		    COElementAbstract coElementChild =
			    (COElementAbstract) childElement.getChildrens()
				    .get(i);
		    dissociateChild(coElementChild, coElementParent);
		}
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
	
	try {
	    if (contentChild.getIdParent() != null
		    && !contentChild.getIdParent().equals("")) {

		prepareForFusion(contentfusionned);
		if (contentfusionned != null) {
		    contentfusionned.setIdParent(contentfusionned.getId());
		    contentfusionned.setId(contentChild.getId());
		}
		fusion(contentChild, contentfusionned);
		contentfusionned.setProperties(contentChild.getProperties());
		setModeledContent(contentfusionned);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @SuppressWarnings("unchecked")
    private void prepareForFusion(COElementAbstract modelToPrepare) {
	modelToPrepare.setEditable(false);
	if (!modelToPrepare.isCOContentResourceProxy()) {
	    modelToPrepare.setIdParent(modelToPrepare.getId());
	    modelToPrepare.setId(UUID.uuid());
	    for (Iterator<COElementAbstract> iter =
		    modelToPrepare.getChildrens().iterator(); iter.hasNext();) {
		prepareForFusion(iter.next());
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public void fusion(COElementAbstract child, COElementAbstract fusionned) {
	
	try {
	    if (child.isCOUnitContent()) {
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
		    if (childElement.getIdParent() != null
			    && !childElement.getIdParent().equals("")) {
			COElementAbstract parentElement =
				fusionned
					.findCOElementAbstractWithParentId(childElement
						.getIdParent());
			if (parentElement != null) {
			    parentElement.setId(childElement.getId());
			    fusion(childElement, parentElement);
			} else {
			    // L'enfant référence qqchose qui n'existe PLUS dans
			    // le
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
			    }
			}
		    } else {
			// L'enfant référence qqchose qui n'existe PAS dans le
			// parent
			fusionned.getChildrens().add(j, childElement);
		    }
		    j++;
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void deleteParentUuids(COElementAbstract element) {
	element.setIdParent(null);
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

    public void resetXML(Map<String, String> filenameChangesMap) {
	resetXML(this.getModeledContent(), filenameChangesMap);
    }

    private void resetXML(COElementAbstract element,
	    Map<String, String> filenameChangesMap) {
	try {
	    element.setId(UUID.uuid());
	    element.setIdParent(null);
	    if (element.isCOContentResourceProxy()) {
		changeDocumentsUrlsToFitNewSiteName(
			(COContentResourceProxy) element, filenameChangesMap);
	    } else {
		for (int i = 0; i < element.getChildrens().size(); i++) {
		    COElementAbstract childElement =
			    (COElementAbstract) element.getChildrens().get(i);
		    resetXML(childElement, filenameChangesMap);
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void changeDocumentsUrlsToFitNewSiteName(
	    COContentResourceProxy cocrp, Map<String, String> filenameChangesMap) {
	String uri =
		cocrp.getResource().getProperty(COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_URI);
	if (uri != null && !uri.equals("")) {
	    String oldSiteName = uri.substring(uri.indexOf("group/") + 6);
	    oldSiteName = oldSiteName.substring(0, oldSiteName.indexOf("/"));
	    uri =
		    changeDocumentsUrls(uri, oldSiteName, coSerialized
			    .getSiteId());
	    if (filenameChangesMap != null) {
		String filename = uri.substring(uri.lastIndexOf("/") + 1);
		String newFilename = filenameChangesMap.get(filename);
		if (newFilename != null)
		    uri = changeDocumentsUrls(uri, filename, newFilename);
	    }
	    cocrp.getResource().addProperty(COPropertiesType.IDENTIFIER,
		    COPropertiesType.IDENTIFIER_TYPE_URI, uri);
	}
    }

    public String changeDocumentsUrls(String url, String originalDirectory,
	    String newDirectory) {
	return url.replaceFirst(Pattern.quote(originalDirectory), newDirectory);
    }

    public void setCOContentTitle(String coTitle) {
	if (coTitle != null) {
	    String propertyType = getPropertyType();
	    COContent content = this.getModeledContent();
	    COProperties coProperties = content.getProperties();
	    coProperties.addProperty(COPropertiesType.TITLE, propertyType,
		    coTitle);
	}
    }

    public void setCOContentCourseId(String courseId) {
	if (courseId != null) {
	    String propertyType = getPropertyType();
	    COContent content = this.getModeledContent();
	    COProperties coProperties = content.getProperties();
	    coProperties.addProperty(COPropertiesType.COURSE_ID, propertyType,
		    courseId);
	}
    }

    public void setCOContentIdentifier(String identifier) {
	if (identifier != null) {
	    String propertyType = getPropertyType();
	    COContent content = this.getModeledContent();
	    COProperties coProperties = content.getProperties();
	    coProperties.addProperty(COPropertiesType.IDENTIFIER, propertyType,
		    identifier);
	}
    }

    public static String getRulesConfigPropertyType(Document d) {
	String propertyType = "";
	try {
	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    XPathExpression expr;

	    expr =
		    xpath
			    .compile("//schema/element[@name='CO']/attribute[@name='propertyType']/@restrictionpattern");

	    propertyType = (String) expr.evaluate(d, XPathConstants.STRING);
	} catch (XPathExpressionException e) {
	    e.printStackTrace();
	}
	return propertyType;
    }

    public String getSchemaVersion() {
	return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
	this.schemaVersion = schemaVersion;
    }

    public String getPropertyType() {
	if (propertyType == null || propertyType.equals("")) {
	    String rulesXML = coSerialized.getOsylConfig().getRulesConfig();
	    Document rulesDom = this.parseXml(rulesXML);
	    propertyType = getRulesConfigPropertyType(rulesDom);
	}
	return propertyType;
    }
}
