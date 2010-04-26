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
package org.sakaiquebec.opensyllabus.client.ui.view;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylCOUnitLabelEditor;
import org.sakaiquebec.opensyllabus.shared.model.COElementAbstract;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.COUnit;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylCOUnitLabelView extends OsylLabelView {

    public OsylCOUnitLabelView(COElementAbstract model,
	    OsylController controller, boolean isDeletable, String levelStyle) {
	this(model, controller, isDeletable, levelStyle, true);
    }

    public OsylCOUnitLabelView(COElementAbstract model,
	    OsylController controller, boolean isDeletable, String levelStyle,
	    boolean initView) {
	super(model, controller, isDeletable, levelStyle, false);
	setEditor(new OsylCOUnitLabelEditor(this, isDeletable));
	((OsylCOUnitLabelEditor) getEditor()).setViewerStyle(levelStyle);
	if (initView)
	    initView();
    }

    public COUnit getModel() {
	return (COUnit) super.getModel();
    }
    
    protected void updateModel() {
	super.updateModel();
	String prefix = ((OsylCOUnitLabelEditor)getEditor()).getPrefix();
	if(prefix!=null)
	    getModel().addProperty(COPropertiesType.PREFIX, prefix);
    }
    
    public String getPrefixFromModel(){
	return getModel().getProperty(COPropertiesType.PREFIX);
    }

}
