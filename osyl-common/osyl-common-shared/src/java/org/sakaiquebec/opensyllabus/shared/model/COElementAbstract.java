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

/**
 * This abstract class is the superclass of <code>CourseOutlineContent</code>,
 * <code>COStructureElement</code> and <code>COContentUnit</code> classes.
 * It provides the answer for two situations. The first one is the possibility
 * of having either <code>COStructureElement</code> objects or
 * <code>COContentUnit</code> objects in <code>CourseOutlineContent</code>
 * childs list. The second one is the possibility of having either a
 * <code>CourseOutlineContent</code> object or a
 * <code>COStructureElement</code> object as <code>COContentUnit</code>
 * parent.
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public abstract class COElementAbstract implements COModelInterface {

    /**
     * The <code>CourseOutlineContent</code> class type.
     */
    protected final String CO_CONTENT_CLASS_TYPE = "CourseOutlineContent";

    /**
     * The <code>COStructureElement</code> class type.
     */
    protected final String CO_STRUCTURE_ELEMENT_CLASS_TYPE = "COStructureElement";

    /**
     * The <code>COContentUnit</code> class type.
     */
    protected final String CO_CONTENT_UNIT_CLASS_TYPE = "COContentUnit";

    /**
     * (ie. Course outline by competence, by lecture, by theme)
     */
    private String type;
    
    /**
     * Description of the content or the structure element or the content unit.
     */
    private String label;
    
    /**
     * Security related to content access
     */
    private String security;

    /**
     * The class of the object that inherits this abstract class.
     */
    private String classType;
    

    /**
     * Protected constructor to prohibit instantiation of this class.
     */
    protected COElementAbstract() {
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
     * @return the security
     */
    public String getSecurity() {
	return security;
    }

    /**
     * @param security the security to set
     */
    public void setSecurity(String security) {
	this.security = security;
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
	return getClassType().equals(CO_STRUCTURE_ELEMENT_CLASS_TYPE);
    }

    /**
     * Tests if the object is of type <code>COContentUnit</code>.
     *
     * @return true if the object is of type <code>COContentUnit</code>,
     *         false if not.
     */
    public boolean isCOContentUnit() {
	return getClassType().equals(CO_CONTENT_UNIT_CLASS_TYPE);
    }

    /**
     * Notifies all event handlers
     */
    abstract void notifyEventHandlers();

    /**
     * Gets the label.
     * 
     * @return
     *         the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * 
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
        notifyEventHandlers();
    }
}