/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
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
import java.util.List;

/**
 * Content element of the XML course outline.
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContent extends COElementAbstract implements COModelInterface {

    /**
     * Children of course content outline either of type
     * <code>COStructureElement</code> or <code>COContentUnit</code>.
     * atgwt.typeArgs children
     * <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     */

    private List<COElementAbstract> children;

    /**
     * Constructor. The class type is set at the creation of the object.
     */
    public COContent() {
	super();
	setClassType(CO_CONTENT_CLASS_TYPE);
	children = new ArrayList<COElementAbstract>();
    }

    /**
     * atgwt.typeArgs <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @return the children
     */
    public List<COElementAbstract> getChildren() {
	return children;
    }

    /**
     * atgwt.typeArgs children
     * <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @param children the children to set
     */
    public void setChildren(List<COElementAbstract>children) {
	this.children = children;
    }

    /**
     * Adds a child to the <code>CourseOutlineContent</code> children list.
     *
     * @param child the sild to add to the children list.
     * @return true if the child is added successfully, false if not.
     */
    public boolean addChild(COElementAbstract child) {
	if (child.isCOContentUnit() || child.isCOStructureElement())
	    return getChildren().add(child);
	else
	    return false;
    }

    /**
     * Removes a child from the <code>CourseOutlineContent</code> children
     * list.
     *
     * @param child the child to remove.
     * @return true if the child is removed successfully, false if not.
     */
    public boolean removeChild(COElementAbstract child) {
	boolean res = getChildren().remove(child);
	notifyEventHandlers();
	return res;
    }

    /*
     * Finds and returns the <code>COElementAbstract</code> child with the
     * specified id form the children list.
     *
     * @param childId the id of the child to find and return.
     * @return the <code>COElementAbstract</code> if it is found, null
     *         otherwise.

    public COElementAbstract getChildWithId(String childId) {
	boolean isFound = false;
	COElementAbstract child = null;
	Iterator childrenIter = getChildren().iterator();

	while (childrenIter.hasNext() && !isFound) {
	    COElementAbstract thisChild =
		    (COElementAbstract) childrenIter.next();

	    if (thisChild.getId().equals(childId)) {
		child = thisChild;
		isFound = true;
	    }
	}
	return child;
    }
 */

    /** {@inheritDoc} */
    void notifyEventHandlers() {
	// TODO: notify please
    }
}
