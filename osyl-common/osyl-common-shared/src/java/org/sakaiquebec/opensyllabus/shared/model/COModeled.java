/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.util.UUID;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

/**
 * <code>COModeled</code> is a specialization of COSerialized, containing a full
 * tree-like structure of the outline, <code>COContent</code>.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COModeled extends COSerialized {

    private static final long serialVersionUID = 1L;

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
     * The CourseOutline Unit Content node name in the xml DOM.
     */
    protected final static String CO_UNIT_CONTENT_NODE_NAME = "asmUnitContent";

    /**
     * The CourseOutline Unit Content node name in the xml DOM.
     */
    protected final static String CO_UNIT_STRUCTURE_NODE_NAME =
	    "asmUnitStructure";

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
     * Name of a type node.
     */
    protected final static String TYPE_NODE_NAME = "type";

    /**
     * Name of access attribute.
     */
    protected final static String ACCESS_ATTRIBUTE_NAME = "access";

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
     *Name of editable attribute
     */
    protected final static String EDITABLE_ATTRIBUTE_NAME = "editable";

    /**
     *Name of person node
     */
    protected final static String PERSON_NODE_NAME = "Person";

    private static final String XML_VERSION_ATTRIBUTE = "schemaVersion";

    /**
     * The modeledContent is a POJO filled by XML2Model
     */
    private COContent modeledContent;

    private String schemaVersion;

    /**
     * Default Constructor
     */
    public COModeled() {
    }

    /**
     * Constructor from a serialized course outline
     */
    public COModeled(COSerialized serializedContent) {
	super(serializedContent);
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

    public String getSchemaVersion() {
	return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
	this.schemaVersion = schemaVersion;
    }

    /**
     * Entry point of the XML string conversion to the model. After the
     * conversion, setModeledContent is called on the new CourseOutlineContent.
     */
    public void XML2Model() {

	COContent coContent = new COContent();
	Document messageDom = null;

	try {
	    // XMLtoDOM
	    messageDom = XMLParser.parse(getContent());

	    schemaVersion =
		    messageDom.getDocumentElement().getAttribute(
			    XML_VERSION_ATTRIBUTE);

	    // DOMtoModel
	    coContent = createCOContentPOJO(messageDom, coContent);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	setModeledContent(coContent);
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
	    COContent coContent) {

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

	    if (nodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coStructElt = new COStructureElement();
		coContent.getChildrens().add(
			createCOStructureElementPOJO(myNode, coStructElt,
				coContent));

	    } else {
		addProperty(coContent.getProperties(), myNode);
	    }

	}
	return coContent;
    }

    /**
     * Creates a CoUnit POJO from the DOM.
     * 
     * @param node the node from the DOM representing that structure element
     * @param coUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COUnit createCOUnitPOJO(Node node, COUnit coUnit,
	    COElementAbstract parent) {
	Node sNode;
	String sNodeName = "";

	setCommonAttributes(coUnit, node);

	coUnit.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		COUnitStructure coUnitStructure = new COUnitStructure();
		coUnit.addChild(createCOUnitStructurePOJO(sNode,
			coUnitStructure, coUnit));
	    } else {
		// properties
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
	    COUnitStructure coUnitStructure, COElementAbstract parent) {
	Node sNode;
	String sNodeName = "";

	setCommonAttributes(coUnitStructure, node);

	coUnitStructure.setParent(parent);

	// Retrieve children: can be COUnitStructure or COUnitContent
	NodeList unitChildren = node.getChildNodes();
	for (int i = 0; i < unitChildren.getLength(); i++) {
	    sNode = unitChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_UNIT_STRUCTURE_NODE_NAME)) {
		COUnitStructure coUnitStructure2 = new COUnitStructure();
		coUnitStructure.addChild(createCOUnitStructurePOJO(sNode,
			coUnitStructure2, coUnitStructure));
	    } else if (sNodeName.equalsIgnoreCase(CO_UNIT_CONTENT_NODE_NAME)) {
		COUnitContent coContentUnit = new COUnitContent();
		coUnitStructure.addChild(createCOContentUnitPOJO(sNode,
			coContentUnit, coUnitStructure));
	    } else {
		// properties
		addProperty(coUnitStructure.getProperties(), sNode);
	    }
	}
	return coUnitStructure;
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

	setCommonAttributes(coStructElt, node);

	coStructElt.setParent(parent);

	// Retrieve children: can be StructureElement or COUnit
	NodeList strucEltChildren = node.getChildNodes();
	for (int i = 0; i < strucEltChildren.getLength(); i++) {
	    sNode = strucEltChildren.item(i);
	    sNodeName = sNode.getNodeName();

	    if (sNodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coChildStructElt = new COStructureElement();
		coStructElt.getChildrens().add(
			createCOStructureElementPOJO(sNode, coChildStructElt,
				coStructElt));
	    } else if (sNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		COUnit coUnit = new COUnit();
		coStructElt.addChild(createCOUnitPOJO(sNode, coUnit,
			coStructElt));
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
	    COUnitContent coContentUnit, COElementAbstract parent) {
	Node coNode;
	String coNodeName = "";

	setCommonAttributes(coContentUnit, node);

	coContentUnit.setParent(parent);

	NodeList coContentUnitChildren = node.getChildNodes();
	for (int i = 0; i < coContentUnitChildren.getLength(); i++) {
	    coNode = coContentUnitChildren.item(i);
	    coNodeName = coContentUnitChildren.item(i).getNodeName();

	    if (coNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentUnit.addChild(createCOContentResourceProxyPOJO(coNode,
			coContentUnit));
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
	    COUnitContent coContentUnitParent) {

	Node prNode;
	String prNodeName = "";

	COContentResourceProxy coContentResProxy = new COContentResourceProxy();

	setCommonAttributes(coContentResProxy, node);

	coContentResProxy.setParent(coContentUnitParent);

	NodeList resProxyChildren = node.getChildNodes();
	for (int j = 0; j < resProxyChildren.getLength(); j++) {
	    prNode = resProxyChildren.item(j);
	    prNodeName = prNode.getNodeName();

	    if (prNodeName.equalsIgnoreCase(COPropertiesType.SEMANTIC_TAG)) {
		coContentResProxy.setRubric(createCOContentRubricPOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_NODE_NAME)) {
		coContentResProxy
			.setResource(createCOContentResourcePOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(PERSON_NODE_NAME)) {
		coContentResProxy
			.setResource(createCOContentResourcePersonPOJO(prNode));
	    } else if (prNodeName.equalsIgnoreCase(CO_RES_PROXY_NODE_NAME)) {
		coContentResProxy
			.addNestedResourceProxy(createCOContentResourceProxyPOJO(
				prNode, coContentUnitParent));
	    } else if (prNodeName.equalsIgnoreCase(CO_UNIT_NODE_NAME)) {
		COUnit coUnit = new COUnit();
		coContentResProxy.setResource(createCOUnitPOJO(prNode, coUnit,
			coContentResProxy));
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
    private COContentResource createCOContentResourcePOJO(Node node) {

	COContentResource coContentRes = new COContentResource();

	setCommonAttributes(coContentRes, node);

	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    addProperty(coContentRes.getProperties(), rNode);
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
	NamedNodeMap namedNodeMap = node.getAttributes();
	String type =
		(namedNodeMap == null) ? null : (namedNodeMap
			.getNamedItem(TYPE_ATTRIBUTE_NAME) == null) ? null
			: namedNodeMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
				.getNodeValue();

	String userDefLabel =
		(namedNodeMap == null) ? null
			: (namedNodeMap
				.getNamedItem(COPropertiesType.SEMANTIC_TAG_USERDEFLABEL) == null) ? null
				: namedNodeMap.getNamedItem(
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
	return coContentRubric;
    }

    public void model2XML(boolean saveParentInfos) {
	Document document = XMLParser.createDocument();
	createRootElement(document, getModeledContent(), saveParentInfos);
	setContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ document.toString());
    }

    private void setCommonAttributesAndProperties(Element element,
	    COModelInterface modelInterface, Document document) {

	element.setAttribute(ACCESS_ATTRIBUTE_NAME, modelInterface.getAccess());
	element.setAttribute(ID_ATTRIBUTE_NAME, modelInterface.getId());

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

    }

    /**
     * Creates the root element of the document.
     * 
     * @param document the document being created
     * @param coContent the POJO needed to create the root element.
     */
    private void createRootElement(Document document, COContent coContent,
	    boolean saveParentInfos) {
	Element osylElement = document.createElement("OSYL");
	osylElement.setAttribute(ACCESS_ATTRIBUTE_NAME,
		SecurityInterface.ACCESS_PUBLIC);
	osylElement.setAttribute("schemaVersion", schemaVersion);
	osylElement.setAttribute("xmlns:xsi",
		"http://www.w3.org/2001/XMLSchema-instance");
	Element courseOutlineContentElem = document.createElement(CO_NODE_NAME);

	setCommonAttributesAndProperties(courseOutlineContentElem, coContent,
		document);

	osylElement.appendChild(courseOutlineContentElem);
	document.appendChild(osylElement);

	for (int i = 0; i < coContent.getChildrens().size(); i++) {
	    createChildElement(document, courseOutlineContentElem,
		    (COElementAbstract) coContent.getChildrens().get(i),
		    saveParentInfos);

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
	    COElementAbstract child, boolean saveParentInfos) {
	if (child instanceof COContentResourceProxy) {
	    createChildElement(document, parent,
		    (COContentResourceProxy) child, saveParentInfos);
	} else {
	    Element element = null;
	    if (child.isCOStructureElement()) {
		element = document.createElement(CO_STRUCTURE_NODE_NAME);
	    } else if (child.isCOUnit()) {
		element = document.createElement(CO_UNIT_NODE_NAME);

	    } else if (child.isCOUnitStructure()) {
		element = document.createElement(CO_UNIT_STRUCTURE_NODE_NAME);

	    } else if (child.isCOUnitContent()) {
		element = document.createElement(CO_UNIT_CONTENT_NODE_NAME);
	    }
	    setCommonAttributesAndProperties(element, child, document);

	    for (int i = 0; i < child.getChildrens().size(); i++) {
		createChildElement(document, element, (COElementAbstract) child
			.getChildrens().get(i), saveParentInfos);
	    }
	    parent.appendChild(element);
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

		HashMap<String, COProperty> map = properties.get(propElemName);
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

		    if (COPropertiesType.CDATA_NODE_NAMES.contains(propElemName)) {
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

    private void createPropElemAttributes(COProperty coProperty,
	    Element propElem) {
	for (String key : coProperty.getAttributes().keySet()) {
	    propElem.setAttribute(key, coProperty.getAttribute(key));
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
	    COContentResourceProxy child, boolean saveParentInfos) {
	if (child.isEditable() || (!child.isEditable() && saveParentInfos)) {
	    Element coContentResourceProxyElem =
		    document.createElement(CO_RES_PROXY_NODE_NAME);

	    setCommonAttributesAndProperties(coContentResourceProxyElem, child,
		    document);

	    if (child.getNestedCOContentResourceProxies() != null) {
		for (int i = 0; i < child.getNestedCOContentResourceProxies()
			.size(); i++) {
		    createChildElement(
			    document,
			    coContentResourceProxyElem,
			    (COContentResourceProxy) child
				    .getNestedCOContentResourceProxies().get(i),
			    saveParentInfos);
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
		createChildElement(document, coContentResourceProxyElem,
			coUnit, saveParentInfos);
	    }
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
	if (resource.getType().equals(COContentResourceType.PERSON)) {
	    coContentResourceElem = document.createElement(PERSON_NODE_NAME);
	} else {
	    coContentResourceElem = document.createElement(CO_RES_NODE_NAME);
	    coContentResourceElem.setAttribute(XSI_TYPE_ATTRIBUTE_NAME,
		    resource.getType());

	}
	coContentResourceElem.setAttribute(ACCESS_ATTRIBUTE_NAME, resource
		.getAccess());
	coContentResourceElem.setAttribute(ID_ATTRIBUTE_NAME, resource.getId());
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
	Element coContentRubricElem = document.createElement(COPropertiesType.SEMANTIC_TAG);
	coContentRubricElem.setAttribute(TYPE_ATTRIBUTE_NAME, rubric.getKey());
	if (rubric.getUserDefLabel() != null
		&& rubric.getUserDefLabel().length() > 0) {
	    coContentRubricElem.setAttribute(COPropertiesType.SEMANTIC_TAG_USERDEFLABEL,
		    rubric.getUserDefLabel());
	}

	Text elemValue = document.createTextNode(rubric.getType());
	coContentRubricElem.appendChild(elemValue);

	coContentResourceProxyElem.appendChild(coContentRubricElem);
    }
}
