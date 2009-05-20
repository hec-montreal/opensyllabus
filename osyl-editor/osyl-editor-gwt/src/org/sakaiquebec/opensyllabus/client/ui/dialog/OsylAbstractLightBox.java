/**
 * 
 */
package org.sakaiquebec.opensyllabus.client.ui.dialog;

import org.gwt.mosaic.core.client.DOM;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

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

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
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

/**
 * OsylAbstractLightBox essentially implements the LightBox behaviour for all
 * the the Osyl Dialog and Alert boxes. The appearance of the LightBox is
 * defined by the OsylCore.css There are 5 different StyleNames : 1)
 * Osyl-LightBox-None to indicate there is no LightBox 2) Osyl-LightBox-Regular :
 * the regular black LightBox 3) Osyl-LightBox-Processing : the LightBox used
 * during processing 4) Osyl-LightBox-Red : for delicate or warning condition,
 * 5) Osyl-LightBox-Clear : for transparent LightBox
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public abstract class OsylAbstractLightBox extends DialogBox {

    private PopupPanel lightBox = new PopupPanel();
    private String lightBoxStyle = "Osyl-LightBox-Regular";
    // z-index of window with high default value, so the window 
    // is usually in the front
    private int zIndex = 111000; 

    public OsylAbstractLightBox(boolean autoHide, boolean modal) {
	super(autoHide, modal);
    }

    @Override
    public void show() {
	if (!getLightBoxStyle().equalsIgnoreCase("Osyl-LightBox-None")) {

	    getLightBox().setPixelSize(Window.getClientWidth(),
		    Window.getClientHeight());
	    getLightBox().setPopupPosition(0, 0);
	    // This empty Label is needed in order to set-up the LightBox
	    // properly
	    getLightBox().setWidget(new Label(""));
	    getLightBox().setStyleName(getLightBoxStyle());
	    // set the z-index of the lightbox so it is in front of other
	    // windows
	    DOM.setStyleAttribute(getLightBox().getElement(), "zIndex", 
		    	Integer.toString(zIndex));
	    getLightBox().show();	    
	}
	super.hide();
    // set the z-index of the dialogbox so it is in front of other
    // windows
	DOM.setStyleAttribute(this.getElement(), "zIndex",
			Integer.toString(zIndex));
	super.show();
    }

    @Override
    public void hide() {
	lightBox.hide();
	super.hide();
    }

    /**
     * @return the lightBox value.
     */
    public PopupPanel getLightBox() {
	return this.lightBox;
    }

    /**
     * @param lightBox the new value of lightBox.
     */
    public void setLightBox(PopupPanel newLightBox) {
	this.lightBox = newLightBox;
    }

    /**
     * @return the lightBoxStyle value.
     */
    public String getLightBoxStyle() {
	return this.lightBoxStyle;
    }

    /**
     * @param lightBoxStyle the new value of lightBoxStyle.
     */
    public void setLightBoxStyle(String newLightBoxStyle) {
	this.lightBoxStyle = newLightBoxStyle;
    }
    
    /**
     * @param zIndex the new value of the z-index.
     */
    public void setZIndex(int zIndex) {
    	this.zIndex = zIndex;
    	DOM.setStyleAttribute(getLightBox().getElement(), "zIndex", 
    	    	Integer.toString(zIndex));
    	DOM.setStyleAttribute(this.getElement(), "zIndex", 
    	    	Integer.toString(zIndex));
    }
    
    /**
     * @return the z-index value.
     */
    public int getZIndex() {
    	return this.zIndex;
    }

}
