/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.view.editor;

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.ListBox;

/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitLabelEditor extends OsylLabelEditor {

    
    public OsylCOUnitLabelEditor(OsylAbstractView parent) {
	this(parent, false);
    }
    
    public OsylCOUnitLabelEditor(OsylAbstractView parent, boolean isDeleteable) {
	super(parent, isDeleteable);
    }
    
    @Override
    public boolean isMoveable() {
	if (!isDeletable)
	    return false;
	else
	    return ((COElementAbstract) getModel()).getParent().isNested();
    }
    
    @Override
    protected void generateTargetCoAbstractElementListBox(ListBox lb) {
	lb.clear();
	lb.addItem("");
	COStructureElement parent =
		(COStructureElement) ((COElementAbstract) getModel())
			.getParent();
	for (COElementAbstract coe : ((COStructureElement) parent.getParent())
		.getChildrens()) {
	    if (coe.isCOStructureElement())
		lb.addItem(coe.getLabel(), coe.getId());
	}
    }

}

