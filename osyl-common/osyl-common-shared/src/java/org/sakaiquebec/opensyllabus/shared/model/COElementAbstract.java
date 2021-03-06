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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.util.OsylDateUtils;
import org.sakaiquebec.opensyllabus.shared.util.UUID;

/**
 * This abstract class is the superclass of <code>CourseOutlineContent</code>,
 * <code>COStructureElement</code> and <code>COContentUnit</code> classes. It
 * provides the answer for two situations. The first one is the possibility of
 * having either <code>COStructureElement</code> objects or
 * <code>COContentUnit</code> objects in <code>CourseOutlineContent</code>
 * childs list. The second one is the possibility of having either a
 * <code>CourseOutlineContent</code> object or a <code>COStructureElement</code>
 * object as <code>COContentUnit</code> parent.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public abstract class COElementAbstract<T extends COModelInterface> implements
	Serializable, COModelInterface {

    private static final long serialVersionUID = 5786482983027706074L;

    /**
     * The <code>CourseOutlineContent</code> class type.
     */
    protected static final String CO_CONTENT_CLASS_TYPE = "CO";

    /**
     * The <code>COStructureElement</code> class type.
     */
    protected static final String ASM_STRUCTURE_CLASS_TYPE = "asmStructure";

    /**
     * The <code>COContentUnit</code> class type.
     */
    protected static final String ASM_UNIT_CONTENT_CLASS_TYPE =
	    "asmUnitContent";

    /**
     * The <code>COUnitStructure</code> class type.
     */
    protected static final String ASM_UNIT_STRUCTURE_CLASS_TYPE =
	    "asmUnitStructure";

    /**
     * The <code>COUnit</code> class type.
     */
    protected static final String ASM_UNIT_CLASS_TYPE = "asmUnit";

    /**
     * The <code>COCOntentResourceProx</code> class type.
     */
    protected static final String CO_CONTENT_RESOURCE_PROXY_CLASS_TYPE =
	    "asmContext";

    /**
     * (ie. Course outline by competence, by lecture, by theme)
     */
    private String type;

    /**
     * Security related to content access
     */
    private String access;

    /**
     * The class of the object that inherits this abstract class.
     */
    private String classType;

    /**
     * Editable or not (comming from parent or not)
     */
    private boolean editable = true;

    /**
     * Identifier
     */
    private String id;

    /**
     * parent identifier
     */
    private String idParent;

    private COElementAbstract parent = null;

    private COProperties properties;

    public static final int POSITION_CHANGE_ACTION_UP = -1;

    public static final int POSITION_CHANGE_ACTION_DOWN = 1;

    /**
     * Protected constructor to prohibit instantiation of this class.
     */
    protected COElementAbstract() {
	this.id = UUID.uuid();
	properties = new COProperties();
	addProperty(COPropertiesType.MODIFIED, OsylDateUtils
		.getCurrentDateAsXmlString());
	setAccess(SecurityInterface.ACCESS_COMMUNITY);	
    }

    /**
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
	this.type = type;
	notifyEventHandlers();
    }

    /**
     * @return the access
     */
    public String getAccess() {
	return access;
    }

    /**
     * @param access the access to set
     */
    public void setAccess(String access) {
	this.access = access;
	notifyEventHandlers();
    }

    /**
     * @return the classType
     */
    protected String getClassType() {
	return classType;
    }

    /**
     * @param classType the classType to set
     */
    protected void setClassType(String classType) {
	this.classType = classType;
	notifyEventHandlers();
    }

    /**
     * Tests if the object is of type <code>CourseOutlineContent</code>.
     * 
     * @return true if the object is of type <code>CourseOutlineContent</code>,
     *         false if not.
     */
    public boolean isCourseOutlineContent() {
	return getClassType().equals(CO_CONTENT_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COStructureElement</code>.
     * 
     * @return true if the object is of type <code>COStructureElement</code>,
     *         false if not.
     */
    public boolean isCOStructureElement() {
	return getClassType().equals(ASM_STRUCTURE_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COUnitContent</code>.
     * 
     * @return true if the object is of type <code>COUnitContent</code>, false
     *         if not.
     */
    public boolean isCOUnitContent() {
	return getClassType().equals(ASM_UNIT_CONTENT_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COUnit</code>.
     * 
     * @return true if the object is of type <code>CoUnit</code>, false if not.
     */
    public boolean isCOUnit() {
	return getClassType().equals(ASM_UNIT_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COUnitStructure</code>.
     * 
     * @return true if the object is of type <code>CoUnitStructure</code>, false
     *         if not.
     */
    public boolean isCOUnitStructure() {
	return getClassType().equals(ASM_UNIT_STRUCTURE_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COUnitStructure</code>.
     * 
     * @return true if the object is of type <code>CoUnitStructure</code>, false
     *         if not.
     */
    public boolean isCOContentResourceProxy() {
	return getClassType().equals(CO_CONTENT_RESOURCE_PROXY_CLASS_TYPE);
    }

    /**
     * Notifies all event handlers
     */
    abstract void notifyEventHandlers();

    /**
     * Gets the label.
     * 
     * @return the label.
     */
    public String getLabel() {
	return this.getProperty(COPropertiesType.LABEL);
    }

    /**
     * Sets the label.
     * 
     * @param label
     */
    public void setLabel(String label) {
	this.addProperty(COPropertiesType.LABEL, label);
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
	return editable;
    }

    /**
     * {@inheritDoc}
     */
    public void setEditable(boolean edit) {
	this.editable = edit;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getIdParent() {
	return idParent;
    }

    public void setIdParent(String idP) {
	this.idParent = idP;
    }

    public COElementAbstract findCOElementAbstractWithId(String idToFind) {
	COElementAbstract result = null;
	if (this.getId() != null && this.getId().equals(idToFind))
	    result = this;
	else {
	    if (this.isCOUnitContent()) {
		// Nothing to do
	    } else {
		int i = 0;
		while (i < this.getChildrens().size() && result == null) {
		    COElementAbstract coElem =
			    (COElementAbstract) this.getChildrens().get(i);
		    result = coElem.findCOElementAbstractWithId(idToFind);
		    i++;
		}
	    }
	}
	return result;
    }

    public COElementAbstract findCOElementAbstractWithParentId(String idToFind) {
	COElementAbstract result = null;
	if (this.getIdParent() != null && this.getIdParent().equals(idToFind))
	    result = this;
	else {
	    if (this.isCOUnitContent()) {
		// Nothing to do
	    } else {
		int i = 0;
		while (i < this.getChildrens().size() && result == null) {
		    COElementAbstract coElem =
			    (COElementAbstract) this.getChildrens().get(i);
		    result = coElem.findCOElementAbstractWithParentId(idToFind);
		    i++;
		}
	    }
	}
	return result;
    }

    /**
     * Gets the position of a structure element child.
     * 
     * @param the child
     * @return the child position
     */
    public String getPosition() {
	COElementAbstract coe = this;
	int level = 0;
	int p=0;
	while (!coe.isCourseOutlineContent()) {
	    level++;
	    coe=coe.getParent();
	}
	List<COElementAbstract> l = new ArrayList<COElementAbstract>();
	getSameTypeElement(coe, 0, level, l);
	return ""+(l.indexOf(this)+1);
    }
   
    
    protected void getSameTypeElement(COElementAbstract coe, int level, int thisLevel, List<COElementAbstract> l){
	level++;
	for (Object o : coe.getChildrens()) {
	    if (o instanceof COElementAbstract) {
		COElementAbstract coe2 = (COElementAbstract) o;
		if (level == thisLevel) {
		    if (coe2.getType().equals(this.getType()))
			l.add(coe2);
		} else {
		    getSameTypeElement(coe2, level, thisLevel,l);
		}

	    }
	}
    }

    /**
     * @return the parent of the courseOutline
     */
    public COElementAbstract getParent() {
	return parent;
    }

    /**
     * @param parent ht eparent to set
     */
    public void setParent(COElementAbstract parent) {
	this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public void removeMeFromMyParent() {
	getParent().removeChild(this);
    }

    /**
     * {@inheritDoc}
     */
    public COProperties getProperties() {
	return properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(COProperties properties) {
	this.properties = properties;
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public void addProperty(String key, String value) {
	properties.addProperty(key, value);
	notifyEventHandlers();
    }

    public void addProperty(String key, String type, String value) {
	properties.addProperty(key, type, value);
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public void removeProperty(String key) {
	properties.removeProperty(key);
	notifyEventHandlers();
    }

    public void removeProperty(String key, String type) {
	properties.removeProperty(key, type);
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String key) {
	return properties.getProperty(key);
    }

    public String getProperty(String key, String type) {
	return properties.getProperty(key, type);
    }
    
    public COProperty getCOProperty(String key, String type) {
	return getProperties().getCOProperty(key, type);
    }

    public boolean isNested() {
	if (getParent() == null)
	    return false;
	return this.getType().equals(getParent().getType());
    }

    public int getNestingLevel() {
	int l = 0;
	COElementAbstract p = getParent();
	while (p != null && this.getType().equals(p.getType())) {
	    l++;
	    p = p.getParent();
	}
	return l;
    }

    public boolean hasNestedChild() {
	for (COModelInterface com : getChildrens()) {
	    if (com.getType().equals(this.getType())) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Check the position of the element compared to other element
     * 
     * @return -1 if the element has no successor,<br/>
     *         1 if he has no predecessor,<br/>
     *         0 if he is the only element in this structure<br/>
     *         2 otherwise
     */
    abstract public int getElementPosition(T coEltAbs);

    /**
     * Change the position of the element
     * 
     * @param coEltAbs The element
     * @param action COElementAbstract.POSITION_CHANGE_ACTION_UP if the element
     *            must be move to a prior position
     * @param action COElementAbstract.POSITION_CHANGE_ACTION_DOWN if the
     *            element must be move to a posterior position
     */
    abstract public void changeElementPosition(T coEltAbs, int action);

    /**
     * @return List of childrens
     */
    abstract public List<T> getChildrens();

    abstract public void setChildrens(List<T> childs);

    /**
     * Adds a child to the <code>COELementAbstract</code> children list. Tests
     * if the child could be add. If not, it rejects the child.
     * 
     * @param child the child to add
     * @return true if the child is added successfully, false if not.
     */
    abstract public boolean addChild(T child);

    /**
     * @param child the child to remove.
     * @return true if the child is removed successfully, false if not.
     */
    abstract public boolean removeChild(T child);
}