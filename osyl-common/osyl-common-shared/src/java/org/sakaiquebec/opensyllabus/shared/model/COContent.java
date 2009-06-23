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
public class COContent extends COElementAbstract<COElementAbstract> implements COModelInterface {

    /**
     * Children of course content outline either of type
     * <code>COStructureElement</code> or <code>COContentUnit</code>.
     * atgwt.typeArgs children
     * <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     */

    private List<COElementAbstract> childrens;

    /**
     * Constructor. The class type is set at the creation of the object.
     */
    public COContent() {
	super();
	setClassType(CO_CONTENT_CLASS_TYPE);
	childrens = new ArrayList<COElementAbstract>();
    }

    /**
     * atgwt.typeArgs <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @return the children
     */
    public List<COElementAbstract> getChildrens() {
	return childrens;
    }

    /**
     * {@inheritDoc}
     */
    public void setChildrens(List<COElementAbstract>children) {
	this.childrens = children;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addChild(COElementAbstract child) {
	if (child.isCOContentUnit() || child.isCOStructureElement())
	    return getChildrens().add(child);
	else
	    return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeChild(COElementAbstract child) {
	boolean res = getChildrens().remove(child);
	notifyEventHandlers();
	return res;
    }

    /** {@inheritDoc} */
    void notifyEventHandlers() {
	// TODO: notify please
    }

    @Override
    public void changeElementPosition(COElementAbstract coEltAbs, int action) {
	int ind = childrens.indexOf(coEltAbs);
	COElementAbstract temp;
	
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_UP){
	   temp = childrens.get(ind-1);
	   childrens.set(ind-1,coEltAbs);
	   childrens.set(ind,temp);
	}
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_DOWN){
	    temp=childrens.get(ind+1);
	    childrens.set(ind+1, coEltAbs);
	    childrens.set(ind,temp);
	}
    }

    @Override
    public int getElementPosition(COElementAbstract coEltAbs) {
	int pos = childrens.indexOf(coEltAbs);
	boolean hasPredecessor=false;
	boolean hasSuccessor=false;
	
	hasPredecessor = (pos!=0);
	hasSuccessor= (pos!=childrens.size()-1);

	if(hasPredecessor && hasSuccessor) return 2;
	else if(hasPredecessor) return -1;
	else if(hasSuccessor) return 1;
	else return 0;
    }
}
