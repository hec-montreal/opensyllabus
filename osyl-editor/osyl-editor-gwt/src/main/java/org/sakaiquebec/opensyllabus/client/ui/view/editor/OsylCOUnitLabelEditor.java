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
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOUnitLabelView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;
import org.sakaiquebec.opensyllabus.shared.model.OsylConfigRuler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitLabelEditor extends OsylLabelEditor {

    private boolean automaticNumbering = true;
    private TextBox prefixTextBox;

    public OsylCOUnitLabelEditor(OsylAbstractView parent) {
	this(parent, false);
    }

    public OsylCOUnitLabelEditor(OsylAbstractView parent, boolean isDeleteable) {
	super(parent, isDeleteable);
    }

    @Override
    public boolean isMoveable() {
	COStructureElement m =
		(COStructureElement) ((COElementAbstract) getModel())
			.getParent();
	if (m.isNested()) {
	    return true;
	} else {
	    for (COElementAbstract coe : m.getChildrens()) {
		if (coe.getType().equals(m.getType()))
		    return true;
	    }
	    return false;
	}
    }

    @Override
    protected void generateTargetCoAbstractElementListBox(ListBox lb) {
	lb.clear();
	lb.addItem("");
	COStructureElement m =
		(COStructureElement) ((COElementAbstract) getModel())
			.getParent();
	while (m.getParent().isCOStructureElement())
	    m = (COStructureElement) m.getParent();
	fillListBoxWithCOStructure(m, lb);
    }

    private void fillListBoxWithCOStructure(COStructureElement cse, ListBox lb) {
	String label =
		(cse.getLabel() == null || cse.getLabel().trim().equals("")) ? getView()
			.getCoMessage(cse.getType())
			: cse.getLabel();
	if(!cse.hasNestedChild() || OsylConfigRuler.getInstance().isMixedContentAllowed(cse))
	    lb.addItem(label, cse.getId());
	for (COElementAbstract coe : cse.getChildrens()) {
	    if (coe.isCOStructureElement())
		fillListBoxWithCOStructure((COStructureElement) coe, lb);
	}
    }

    public void enterEdit() {
	super.enterEdit();

	String prefix = ((OsylCOUnitLabelView) getView()).getPrefixFromModel();
	if (prefix != null) {
	    automaticNumbering = false;
	    prefixTextBox.setText(prefix);
	}
    }

    public Widget[] getOptionWidgets() {
	if (getView().getController().getSettings().isTreeViewNumbering(
		(COUnit) getModel())) {
	    VerticalPanel vp = new VerticalPanel();
	    vp.setStylePrimaryName("Osyl-EditorPopup-OptionGroup");
	    prefixTextBox = new TextBox();
	    prefixTextBox.addChangeHandler(new ChangeHandler() {
		public void onChange(ChangeEvent event) {
		    if (automaticNumbering) {
			((COUnit) getModel())
				.setSameTypeElementPrefixWithCurrentPosition();
		    }
		    automaticNumbering = false;
		}
	    });
	    prefixTextBox.setMaxLength(5);
	    prefixTextBox.setStylePrimaryName("Osyl-ASMUnit-numberTextBox");
	    Label l =
		    new Label(getController().getUiMessage("ASMUnit.numbering"));
	    vp.add(l);
	    vp.add(prefixTextBox);
	    return new Widget[] { vp };
	} else {
	    return null;
	}
    }

    public String getPrefix() {
	if (automaticNumbering)
	    return null;
	else
	    return prefixTextBox.getText();
    }

}
