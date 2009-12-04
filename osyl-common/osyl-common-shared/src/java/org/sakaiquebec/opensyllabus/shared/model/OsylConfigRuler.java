/*******************************************************************************
 * $Id: OsylConfigRuler.java 1750 2008-12-01 22:03:01Z mathieu.cantin@hec.ca $
 * *********************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * The COConfig Ruler gives the UI rules (model organization possibilities) for
 * the model. The rules are specified with an xml file. Here is a sample of xml
 * (this is not an xsd, but several concepts comes from):<br>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;schema&gt;<br>
 * &nbsp;&nbsp; &lt;element name="CO"&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &lt;!-- restriction pattern is a regexp pattern
 * based on the value of attribute type --&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &lt;attribute name="type" restrictionpattern=".*"
 * /&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &lt;!-- list of element structureElement allowed at
 * this level --&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &lt;element name="COStructure"&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;attribute name="type"
 * restrictionpattern="lectures" /&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;!-- list of element contentUnit
 * allowed at this level (only one at the moment) --&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;element name="COUnit"&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;attribute name="type"
 * restrictionpattern="lecture|theme" /&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;element
 * name="COResourceProxy"&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;
 * &lt;attribute name="type" restrictionpattern="document|text" /&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;/element&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &lt;/element&gt;<br>
 * &nbsp;&nbsp; &nbsp;&nbsp; &lt;/element&gt;<br>
 * &nbsp;&nbsp; &lt;/element&gt;<br>
 * &lt;/schema&gt;<br>
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylConfigRuler.java 1161 2008-06-23 19:47:01Z
 *          yvette.lapa-dessap@hec.ca $
 */
public class OsylConfigRuler {

    protected static final String UNDEFINED_NODE_NAME = "undefined";
    protected static final String RESTRICTION_PATTERN_ATTRIBUTE_NAME =
	    "restrictionpattern";
    protected static final String NAME_ATTRIBUTE_NAME = "name";
    protected static final String ELEMENT_NODE_NAME = "element";
    protected static final String ATTRIBUTE_NODE_NAME = "attribute";
    protected static final String PROPERTY_TYPE_ATTRIBUTE_NAME = "propertyType";
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    protected static final String ALLOW_MULTIPLE_ATTRIBUTE_NAME =
	    "allowMultiple";
    protected static final String TITLE_EDITABLE = "titleEditable";

    private String rulesConfigContent;
    private Document dom = null;

    /**
     * Creates a new Ruler based on the xml content passed
     * 
     * @param rulesConfigContent the xml content for rules
     */
    public OsylConfigRuler(String rulesConfigContent) {
	this.setRulesConfigContent(rulesConfigContent);
	initDom();
    }

    private void initDom() {
	if (dom == null) {
	    try {
		// XMLtoDOM
		dom = XMLParser.parse(getRulesConfigContent());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private Node findingAttributeTypeNode(Node node) {
	NodeList children = node.getChildNodes();
	Node myNode = null;
	for (int i = 0; i < children.getLength(); i++) {
	    myNode = children.item(i);
	    String myNodeName = myNode.getNodeName();
	    if (ATTRIBUTE_NODE_NAME.equalsIgnoreCase(myNodeName)
		    && getNameAttributeValue(myNode)
			    .equals(TYPE_ATTRIBUTE_NAME)) {
		break;
	    }
	}
	return myNode;
    }

    private Node findNode(List<COElementAbstract> path) {
	Node node = null;
	if (path != null) {
	    int pathPosition = 0;
	    COElementAbstract rootPath =
		    (COElementAbstract) path.get(pathPosition);

	    // the root path must be a CourseOutlineContent
	    if (rootPath.isCourseOutlineContent()) {
		COElementAbstract elementPath = rootPath;

		// Root model for init
		Element nodeElement = dom.getDocumentElement();
		node = nodeElement;
		String nodeName = "";

		// First, try to match position in the dom with this path to
		// find the correct rule
		int pathPositionEnd = path.size() - 1;
		while (pathPosition <= pathPositionEnd) {
		    elementPath = (COElementAbstract) path.get(pathPosition);
		    // Retrieve children: can be Element
		    NodeList nodeChildren = node.getChildNodes();
		    for (int i = 0; i < nodeChildren.getLength(); i++) {
			node = nodeChildren.item(i);
			nodeName = node.getNodeName();
			if (nodeName != null && !("".equals(nodeName))
				&& nodeName.equalsIgnoreCase(ELEMENT_NODE_NAME)) {

			    String nameAttribute = getNameAttributeValue(node);

			    if (nameAttribute != null) {

				// Identification of elementPathName, based on
				// classtypes
				String elementPathName = UNDEFINED_NODE_NAME;
				if (elementPath.isCourseOutlineContent()) {
				    elementPathName = COModeled.CO_NODE_NAME;
				} else if (elementPath.isCOStructureElement()) {
				    elementPathName =
					    COModeled.CO_STRUCTURE_NODE_NAME;
				} else if (elementPath.isCOUnit()) {
				    elementPathName =
					    COModeled.CO_UNIT_NODE_NAME;
				} else if (elementPath.isCOUnitStructure()) {
				    elementPathName =
					    COModeled.CO_UNIT_STRUCTURE_NODE_NAME;
				} else if (elementPath.isCOUnitContent()) {
				    elementPathName =
					    COModeled.CO_UNIT_CONTENT_NODE_NAME;
				}

				// if nameAttribute matches, then retrieving its
				// restrictionPattern

				// The CO tag does not have a type anymore in
				// the new OO XML version
				if (!elementPath.isCourseOutlineContent()) {

				    if (nameAttribute
					    .equalsIgnoreCase(elementPathName)) {
					// checking on type if
					// restrictionpattern
					// matches also
					Node attributeTypeNode =
						findingAttributeTypeNode(node);

					String restrictionPattern =
						getRestrictionPatternAttributeValue(attributeTypeNode);

					// check if the restriction pattern
					// matches...
					if (elementPath.getType().matches(
						restrictionPattern)) {

					    // all matches, then ... one step
					    // more
					    // for path
					    pathPosition++;
					    break;
					}
				    }
				}
				// increment the pathposition here for the CO
				// tag (not invremented above)
				else {
				    pathPosition++;
				    break;
				}
			    }
			}
		    }// for
		}// while
	    }
	}
	return node;
    }

    private List<COModelInterface> getAllowedSubModels(
	    List<COElementAbstract> path, boolean hasNoChild) {
	List<COModelInterface> allowedSubModels = null;
	Node node = findNode(path);

	if (node != null) {

	    // Secondly, identify the available possibilities(rules)
	    NodeList nodeChildren = node.getChildNodes();
	    for (int i = 0; i < nodeChildren.getLength(); i++) {
		Node myNode = nodeChildren.item(i);
		String myNodeName = myNode.getNodeName();

		if (myNodeName.equalsIgnoreCase(ELEMENT_NODE_NAME)) {
		    String nameAttribute = getNameAttributeValue(myNode);

		    Node attributeTypeNode = findingAttributeTypeNode(myNode);

		    String restrictionPattern =
			    getRestrictionPatternAttributeValue(attributeTypeNode);

		    boolean allowMultiple =
			    getAllowMultipleAttributeValue(attributeTypeNode);

		    if (hasNoChild || allowMultiple) {
			if (restrictionPattern.indexOf("|") > 0) {
			    String[] typesStringArray =
				    restrictionPattern.split("\\|");
			    for (int j = 0; j < typesStringArray.length; j++) {
				String type = typesStringArray[j].trim();
				// create a new model instance based on name
				// and
				// type
				COModelInterface modelInstance =
					createModelInstance(nameAttribute, type);
				if (modelInstance != null) {
				    if (allowedSubModels == null) {
					allowedSubModels =
						new ArrayList<COModelInterface>();
				    }
				    allowedSubModels.add(modelInstance);
				}
			    }
			} else if (restrictionPattern.indexOf("*") > 0) {
			    // TODO: here list all available types for this
			    // nameAttribute
			} else {
			    COModelInterface modelInstance =
				    createModelInstance(nameAttribute,
					    restrictionPattern);
			    if (allowedSubModels == null) {
				allowedSubModels =
					new ArrayList<COModelInterface>();
			    }
			    allowedSubModels.add(modelInstance);
			}
		    }
		}
	    }
	}

	return allowedSubModels;
    }

    private COModelInterface createModelInstance(String nameAttribute,
	    String type) {
	COModelInterface modelInstance = null;
	if (COModeled.CO_NODE_NAME.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COContent();
	} else if (COModeled.CO_STRUCTURE_NODE_NAME
		.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COStructureElement();
	} else if (COModeled.CO_UNIT_NODE_NAME.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COUnit();
	} else if (COModeled.CO_UNIT_STRUCTURE_NODE_NAME
		.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COUnitStructure();
	} else if (COModeled.CO_UNIT_CONTENT_NODE_NAME
		.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COUnitContent();
	} else if (COModeled.CO_RES_NODE_NAME.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COContentResource();
	} else if (COModeled.SEMANTIC_TAG.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COContentRubric();
	} else if (COModeled.CO_RES_NODE_NAME.equalsIgnoreCase(nameAttribute)) {
	    modelInstance = new COContentResource();
	} else {
	    // TODO: this is forbidden, throw an exception
	}
	if (modelInstance != null) {
	    modelInstance.setType(type);
	}
	return modelInstance;
    }

    private String getNameAttributeValue(Node myNode) {
	String attributeValue =
		myNode.getAttributes().getNamedItem(NAME_ATTRIBUTE_NAME)
			.getNodeValue();
	return attributeValue;
    }

    private String getRestrictionPatternAttributeValue(Node myNode) {
	String attributeValue =
		myNode.getAttributes().getNamedItem(
			RESTRICTION_PATTERN_ATTRIBUTE_NAME).getNodeValue();
	return attributeValue;
    }

    private boolean getAllowMultipleAttributeValue(Node myNode) {
	Node node =
		myNode.getAttributes().getNamedItem(
			ALLOW_MULTIPLE_ATTRIBUTE_NAME);
	if (node != null && node.getNodeValue() != null) {
	    return Boolean.parseBoolean(node.getNodeValue());
	} else {
	    return true;
	}
    }

    /**
     * List all allowed submodels (based on rules) of the model passed
     * 
     * @param model
     * @return List of submodels allowed or null.
     */
    public List<COModelInterface> getAllowedSubModels(COElementAbstract model) {
	List<COModelInterface> allowedSubModels = null;
	if (model != null) {
	    // First find the current path of the model (node sequence in the
	    // tree)
	    List<COElementAbstract> path = findModelPath(model);

	    boolean hasNoChild = model.getChildrens().isEmpty();

	    // Then check for subelements possibilities(rules) for this path
	    allowedSubModels = getAllowedSubModels(path, hasNoChild);
	}
	return allowedSubModels;
    }

    public boolean isTitleEditable(COElementAbstract model) {
	List<COElementAbstract> path = findModelPath(model);
	Node node = findNode(path);
	return getTitleEditableAttributeValue(node);
    }

    private boolean getTitleEditableAttributeValue(Node myNode) {
	boolean titleEditable=true;
	Node attributeNode = findingAttributeTypeNode(myNode);
	if(attributeNode!=null){
	    Node node = attributeNode.getAttributes().getNamedItem(TITLE_EDITABLE);
	    if (node != null && node.getNodeValue() != null) {
		titleEditable= Boolean.parseBoolean(node.getNodeValue());
	    }
	}
	return titleEditable;
    }

    /**
     * Finds the path (node sequence) of COElementAbstract elements starting
     * from CourseOutlineContent ending to the model passed to this method.
     * 
     * @param model the dest model
     * @return List<COElementAbstract> as a path
     */
    private List<COElementAbstract> findModelPath(COElementAbstract model) {
	List<COElementAbstract> path = null;
	if (model != null) {
	    path = new ArrayList<COElementAbstract>();
	    COElementAbstract currentModel = model;
	    COElementAbstract previousModel = model;
	    while (!currentModel.isCourseOutlineContent()) {
		if (currentModel.isCOUnitStructure()) {
		    path.add(currentModel);
		    currentModel = ((COUnitStructure) currentModel).getParent();

		} else if (currentModel.isCOUnit()) {
		    path.add(currentModel);
		    currentModel = ((COUnit) currentModel).getParent();

		} else if (currentModel.isCOStructureElement()) {
		    path.add(currentModel);
		    currentModel =
			    ((COStructureElement) currentModel).getParent();
		} else if (currentModel.isCOUnitContent()) {
		    path.add(currentModel);
		    currentModel = ((COUnitContent) currentModel).getParent();

		} else {
		    // TODO: exception to throw
		    break;
		}
		// To avoid infinite loopback
		if (previousModel == currentModel) {
		    // TODO: throw infinite loopback exception
		    break;
		}
		previousModel = currentModel;
	    }
	    path.add(currentModel);

	    // reverse the path to the forward order
	    Collections.reverse(path);
	}
	return path;
    }

    /**
     * @return the xml rule rulesConfigContent of this config
     */
    public String getRulesConfigContent() {
	return this.rulesConfigContent;
    }

    /**
     * Set the xml rules config
     * 
     * @param rulesConfigContent the xml rules config
     */
    public void setRulesConfigContent(String rulesConfigContent) {
	this.rulesConfigContent = rulesConfigContent;
    }

    public String getPropertyType() {
	String type = null;
	Element nodeElement = dom.getDocumentElement();
	String nodeName = "";
	NodeList nodeList = nodeElement.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Node myNode = nodeList.item(i);
	    nodeName = myNode.getNodeName();
	    if (nodeName.equals(ELEMENT_NODE_NAME)
		    && getNameAttributeValue(myNode).equals(
			    COModeled.CO_NODE_NAME)) {
		NodeList coNodeList = myNode.getChildNodes();
		for (int j = 0; j < coNodeList.getLength(); j++) {
		    Node myCONode = coNodeList.item(j);
		    nodeName = myCONode.getNodeName();
		    if (nodeName.equals(ATTRIBUTE_NODE_NAME)
			    && getNameAttributeValue(myCONode).equals(
				    PROPERTY_TYPE_ATTRIBUTE_NAME)) {
			type = getRestrictionPatternAttributeValue(myCONode);
			break;
		    }
		}
		break;
	    }
	}
	return type;
    }
}
