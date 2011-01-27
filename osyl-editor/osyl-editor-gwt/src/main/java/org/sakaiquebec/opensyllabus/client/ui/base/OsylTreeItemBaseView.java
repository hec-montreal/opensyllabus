/**********************************************************************************
 * $Id: OsylTreeItemBaseView.java 1414 2008-10-14 14:28:08Z remi.saias@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.base;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.api.OsylViewableComposite;
import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The Base Tree Item View usable for the Osyl tree view
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepr�tre</a>
 * @version $Id: UnitTreeItemView.java 594 2008-05-26 16:10:05Z
 *          laurent.danet@hec.ca $
 */
public abstract class OsylTreeItemBaseView extends OsylViewableComposite {

    private SimplePanel panel;
    private Label label;

    protected OsylTreeItemBaseView(COModelInterface model,
	    OsylController osylController) {
	super(model, osylController);
	initView();
    }

    protected void initView() {
	panel = new SimplePanel();
	label = new Label("");
	label.setWordWrap(false);
	label.setStylePrimaryName("Osyl-TreeLabel");
	panel.add(label);
	panel.setStylePrimaryName("Osyl-TreeItem-HorizontalPanel");
	initWidget(panel);
	refreshView();
    }

    /**
     * Refreshes the view. remove and read label to the panel to refresh the
     * title when using ellipsis.xml (Firefox Only)
     */
    public void refreshView() {
	String treeItemText = createTreeItemText();
	label.setText(treeItemText);
	label.removeFromParent();
	setTitle(treeItemText);
	panel.add(label);
    }

    abstract String createTreeItemText();

    /**
     * @return the label value.
     */
    public Label getLabel() {
	return label;
    }

}
