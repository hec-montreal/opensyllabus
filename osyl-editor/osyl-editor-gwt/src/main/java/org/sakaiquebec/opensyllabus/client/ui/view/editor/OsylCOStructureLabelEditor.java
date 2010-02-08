/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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

import java.util.List;

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;
import org.sakaiquebec.opensyllabus.client.ui.view.OsylCOStructureLabelView;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COStructureElement;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOStructureLabelEditor extends OsylLabelEditor {

    private static int nestingLevelAllowed = 0;

    public OsylCOStructureLabelEditor(OsylAbstractView parent) {
	super(parent);
    }

    public OsylCOStructureLabelEditor(OsylAbstractView parent,
	    boolean isDeletable) {
	super(parent, isDeletable);
    }

    // Our editor
    private TextArea descriptionTextArea;

    /**
     * ===================== OVERRIDEN METHODS ===================== See
     * superclass for javadoc!
     */

    /**
     * Creates and set the low-level editor (TextBox).
     */
    protected void initEditor() {
	super.initEditor();
	Label l =
		new Label(getController().getUiMessage(
			"ASMStructure.description"));
	editorPanel.add(l);
	descriptionTextArea = new TextArea();
	descriptionTextArea.setWidth("100%");
	descriptionTextArea.setHeight("40px");
	editorPanel.add(descriptionTextArea);
    }

    protected OsylCOStructureLabelView getView() {
	return (OsylCOStructureLabelView) super.getView();
    }

    public void enterEdit() {
	super.enterEdit();
	descriptionTextArea.setText(getView().getDescriptionFromModel());
    } // enterEdit

    public void enterView() {
	super.enterView();

	Label label1 = new Label(getView().getDescriptionFromModel());
	label1.setStylePrimaryName("Osyl-ASMStructure-description");

	getMainPanel().add(label1);
    } // enterView

    @Override
    protected List<FocusWidget> getEditionFocusWidgets() {
	List<FocusWidget> focusWidgetList = super.getEditionFocusWidgets();
	focusWidgetList.add(descriptionTextArea);
	return focusWidgetList;
    }

    @Override
    public boolean isMoveable() {
	if (!isDeletable)
	    return false;
	else
	    return true;
    }

    @Override
    protected void generateTargetCoAbstractElementListBox(ListBox lb) {
	lb.clear();
	lb.addItem("");
	nestingLevelAllowed =
		getController().getOsylConfig().getOsylConfigRuler()
			.getNestingLevelAllowed((COElementAbstract) getModel());
	COStructureElement m =
		(COStructureElement) ((COElementAbstract) getModel())
			.getParent();
	while (m.getParent().isCOStructureElement())
	    m = (COStructureElement) m.getParent();
	fillListBoxWithAllowedCOStructure(m, lb);
    }

    private void fillListBoxWithAllowedCOStructure(COStructureElement cse,
	    ListBox lb) {
	if (cse.getNestingLevel() < nestingLevelAllowed) {
	    String label =
		    (cse.getLabel() == null || cse.getLabel().trim().equals("")) ? getView()
			    .getCoMessage(cse.getType())
			    : cse.getLabel();
	    lb.addItem(label, cse.getId());
	    for (COElementAbstract coe : cse.getChildrens()) {
		if (coe.isCOStructureElement())
		    fillListBoxWithAllowedCOStructure((COStructureElement) coe,
			    lb);
	    }
	}
    }

    /**
     * ==================== ADDED CLASSES or METHODS ====================
     */
    public String getDescription() {
	return descriptionTextArea.getText();
    }
}
