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

import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;

/**
 * Content element of the XML course outline.
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContent extends COElementAbstract<COElementAbstract> implements COModelInterface {

    private static final long serialVersionUID = 870548605023877753L;
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
	setAccess(SecurityInterface.ACCESS_PUBLIC);
	setClassType(CO_CONTENT_CLASS_TYPE);
	children = new ArrayList<COElementAbstract>();
    }

    /**
     * atgwt.typeArgs <org.sakaiquebec.opensyllabus.model.COElementAbstract>
     *
     * @return the children
     */
    public List<COElementAbstract> getChildrens() {
	return children;
    }

    /**
     * {@inheritDoc}
     */
    public void setChildrens(List<COElementAbstract>children) {
	this.children = children;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addChild(COElementAbstract child) {
	    return getChildrens().add(child);
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
	int ind = children.indexOf(coEltAbs);
	COElementAbstract temp;
	
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_UP){
	   temp = children.get(ind-1);
	   children.set(ind-1,coEltAbs);
	   children.set(ind,temp);
	}
	if(action==COElementAbstract.POSITION_CHANGE_ACTION_DOWN){
	    temp=children.get(ind+1);
	    children.set(ind+1, coEltAbs);
	    children.set(ind,temp);
	}
    }

    @Override
    public int getElementPosition(COElementAbstract coEltAbs) {
	int pos = children.indexOf(coEltAbs);
	boolean hasPredecessor=false;
	boolean hasSuccessor=false;
	
	hasPredecessor = (pos!=0);
	hasSuccessor= (pos!=children.size()-1);

	if(hasPredecessor && hasSuccessor) return 2;
	else if(hasPredecessor) return -1;
	else if(hasSuccessor) return 1;
	else return 0;
    }
}
