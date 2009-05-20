/*******************************************************************************
 * $Id: $
 * *****************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.dialog;

import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * This class creates a GWT container with a title for the Ajax spinner.
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public class OsylAjaxSpinner extends OsylAbstractLightBox {

    /**
     * Constructor.
     * 
     * @param dialogTitle
     */
    public OsylAjaxSpinner(String dialogTitle) {
	super(false, false);
	setText(dialogTitle);
	HorizontalPanel hzPanel = new HorizontalPanel();
	hzPanel.setStyleName("Osyl-ajaxSpinner");
	hzPanel.setPixelSize(100, 100);
	setWidget(hzPanel);
	this.setLightBoxStyle("gwt-LightBox-Processing");
    }
}
