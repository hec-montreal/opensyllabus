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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sakaiquebec.opensyllabus.shared.util.UUID;

import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.CDATASection;
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
public class COModeled extends COSerialized implements COModelInterface {

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
     *Name of editable attribute
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
     * Full constructor from superclass
     */
    public COModeled(String idCo, String lang, String type, String security,
	    String siteId, String sectionId, String osylConfigId,
	    String content, String shortDesc, String desc, String title) {
	super(idCo, lang, type, security, siteId, sectionId,
		new COConfigSerialized(osylConfigId), content, shortDesc, desc,
		title, false);
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

    /**
     * Entry point of the XML string conversion to the model. After the
     * conversion, setModeledContent is called on the new CourseOutlineContent.
     */
    public void XML2Model() {

	COContent coContent = new COContent();
	Document messageDom = null;
	documentSecurityMap = new HashMap<String, String>();

	try {
	    // XMLtoDOM
	    messageDom = XMLParser.parse(getSerializedContent());

	    // DOMtoModel
	    coContent = createCOContentPOJO(messageDom, coContent);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	setModeledContent(coContent);
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

	String uuid = myRoot.getAttribute(UUID_ATTRIBUTE_NAME);
	coContent.setUuid(uuid == null ? UUID.uuid() : uuid);

	String uuidParent = myRoot.getAttribute(UUID_PARENT_ATTRIBUTE_NAME);
	coContent.setUuidParent(uuidParent);

	String editable = myRoot.getAttribute(EDITABLE_ATTRIBUTE_NAME);
	coContent.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	// Retrieve children: can be StructureElement or COUnit as well as the
	// subnode label
	NodeList rootChildren = myRoot.getChildNodes();

	for (int i = 0; i < rootChildren.getLength(); i++) {
	    myNode = rootChildren.item(i);
	    nodeName = myNode.getNodeName();

	    if (nodeName.equalsIgnoreCase(CO_STRUCTURE_NODE_NAME)) {
		COStructureElement coStructElt = new COStructureElement();
		coContent.addChild(createCOStructureElementPOJO(myNode,
			coStructElt, coContent));
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
     *            content
     * @param coContentUnit the POJO to be created from the DOM.
     * @param parent the parent of the Structure element
     */
    private COContentUnit createCOContentUnitPOJO(Node node,
	    COContentUnit coContentUnit, COElementAbstract parent, String label) {
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

	String editable =
		coMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: coMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentUnit.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

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
				coNode, coContentUnit));
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
	    COContentUnit coContentUnitParent) {

	Node prNode;
	String prNodeName = "";

	COContentResourceProxy coContentResProxy = new COContentResourceProxy();
	NamedNodeMap prMap = node.getAttributes();
	coContentResProxy.setSecurity(prMap.getNamedItem(
		SECURITY_ATTRIBUTE_NAME).getNodeValue());
	coContentResProxy.setType(prMap.getNamedItem(TYPE_ATTRIBUTE_NAME)
		.getNodeValue());

	String editable =
		prMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: prMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentResProxy.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

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
     *            Content Resource
     * @return the created Content resource
     */
    private COContentResource createCOContentResourcePOJO(Node node) {

	COContentResource coContentRes = new COContentResource();
	NamedNodeMap rMap = node.getAttributes();
	String type = rMap.getNamedItem(TYPE_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setType(type);
	String security =
		rMap.getNamedItem(SECURITY_ATTRIBUTE_NAME).getNodeValue();
	coContentRes.setSecurity(security);

	String editable =
		rMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME) == null ? null
			: rMap.getNamedItem(EDITABLE_ATTRIBUTE_NAME)
				.getNodeValue();
	coContentRes.setEditable(editable == null ? true : Boolean
		.valueOf(editable));

	NodeList resChildren = node.getChildNodes();
	for (int z = 0; z < resChildren.getLength(); z++) {
	    Node rNode = resChildren.item(z);
	    String rNodeName = rNode.getNodeName();

	    if (rNodeName.equalsIgnoreCase(CO_PROPERTIES_NODE_NAME)) {
		coContentRes.setProperties(createProperties(rNode));
	    }
	}
	if (type.equals(COContentResourceProxyType.DOCUMENT))
	    documentSecurityMap.put(coContentRes.getProperty(
		    COPropertiesType.URI).trim(), security);
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
     * @deprecated use model2XML(false) instead
     */
    public void model2XML() {
	model2XML(false);
    }

    public void model2XML(boolean saveParentInfos) {
	Document document = XMLParser.createDocument();
	createRootElement(document, getModeledContent(), saveParentInfos);
	setSerializedContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ document.toString());
    }

    /**
     * Creates the root element of the document.
     * 
     * @param document the document being created
     * @param coContent the POJO needed to create the root element.
     */
    private void createRootElement(Document document, COContent coContent,
	    boolean saveParentInfos) {
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
	document.appendChild(courseOutlineContentElem);
	createCDataNode(document, courseOutlineContentElem, CO_LABEL_NODE_NAME,
		coContent.getLabel());
	for (int i = 0; i < coContent.getChildren().size(); i++) {
	    createChildElement(document, courseOutlineContentElem,
		    (COElementAbstract) coContent.getChildren().get(i),
		    saveParentInfos);

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
     * Create a child node containing text value.
     * 
     * @param document the document being created.
     * @param parent the parent element of the text node.
     * @param nodeName the name given to the text node.
     * @param content the content to put in the text node value.
     */
    // private void createTextNode(Document document, Element parent,
    // String nodeName, String content) {
    // Element textElement = document.createElement(nodeName);
    // Text contentText = document.createTextNode(content);
    // textElement.appendChild(contentText);
    // parent.appendChild(textElement);
    // }
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
	if (child.isCOStructureElement()) {
	    System.out
		    .println("ISSTRUCTUREELEMENT-start" + document.toString());
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
	    System.out.println("Children of :" + coStructureElement.getType()
		    + coStructureElement.getChildren().size());
	    for (int i = 0; i < coStructureElement.getChildren().size(); i++) {
		System.out.println("Child number" + i);
		createChildElement(document, coStructureElem,
			(COElementAbstract) coStructureElement.getChildren()
				.get(i), saveParentInfos);
	    }
	    if (coStructureElement.getProperties() != null
		    && !coStructureElement.getProperties().isEmpty()) {
		createPropertiesElem(document, coStructureElem,
			coStructureElement.getProperties());
	    }
	    parent.appendChild(coStructureElem);
	    System.out.println("ISSTRUCTUREELEMENT-end" + document.toString());

	} else if (child.isCOContentUnit()) {
	    COContentUnit coContentUnit = (COContentUnit) child;
	    // create a wrapper on the COUnitContent: a COUnit
	    System.out.println("ISCONTENTUNIT-start" + document.toString());
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
	    createCDataNode(document, coUnitElem, CO_LABEL_NODE_NAME,
		    coContentUnit.getLabel());
	    System.out.println("ISCONTENTUNIT-start" + document.toString());
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
				    .getResourceProxies().get(i),
			    saveParentInfos);
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
	    System.out.println("ISCONTENTUNIT-end" + document.toString());
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
	    COContentResourceProxy child, boolean saveParentInfos) {
	if (child.isEditable() || (!child.isEditable() && saveParentInfos)) {
	    Element coContentResourceProxyElem =
		    document.createElement(CO_RES_PROXY_NODE_NAME);
	    coContentResourceProxyElem.setAttribute(TYPE_ATTRIBUTE_NAME, child
		    .getType());
	    coContentResourceProxyElem.setAttribute(SECURITY_ATTRIBUTE_NAME,
		    child.getSecurity());
	    // sometimes we don't necessarely have a comment on the resource
	    // proxy
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
				    .get(i), saveParentInfos);
		}
	    }
	    if (child.getProperties() != null
		    && !child.getProperties().isEmpty()) {
		createPropertiesElem(document, coContentResourceProxyElem,
			child.getProperties());
	    }
	    createCOContentResourceChild(document, coContentResourceProxyElem,
		    child.getResource(), child.getSecurity());
	    // We may not have a rubric for exams for example
	    if (child.getRubric() != null) {
		createCOCOntentRubricChild(document,
			coContentResourceProxyElem, child.getRubric());
	    }
	    parent.appendChild(coContentResourceProxyElem);
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
	    String security) {
	Element coContentResourceElem =
		document.createElement(CO_RES_NODE_NAME);
	coContentResourceElem.setAttribute(TYPE_ATTRIBUTE_NAME, resource
		.getType());
	// coContentResourceElem.setAttribute(SECURITY_ATTRIBUTE_NAME, resource
	// .getSecurity()); //on prend pour le moment la securite du resource
	// proxy.
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
 }
