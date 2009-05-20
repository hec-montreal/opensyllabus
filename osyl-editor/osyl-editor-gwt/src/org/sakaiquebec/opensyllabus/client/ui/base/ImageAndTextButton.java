package org.sakaiquebec.opensyllabus.client.ui.base;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

/*******************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team. Licensed
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
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team. Licensed
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
public class ImageAndTextButton extends Button {

    private Image image;
    private String text;

    public ImageAndTextButton(AbstractImagePrototype newImage, String newText) {
	super(newImage.getHTML() + (null != newText ? "&nbsp;" + newText + "&nbsp;" : ""));
	setImage(newImage.createImage());
	setText(newText);
	this.setHeight("30px");
	this.addStyleName("Osyl-ImageAndTextButton");
    }

    public ImageAndTextButton(AbstractImagePrototype newImage) {
	this(newImage, null);
    }

    /**
     * @return the image value.
     */
    public Image getImage() {
	return this.image;
    }

    /**
     * @param image the new value of image.
     */
    public void setImage(Image image) {
	this.image = image;
    }

    /**
     * @return the text value.
     */
    public String getText() {
	return this.text;
    }

    /**
     * @param text the new value of text.
     */
    public void setText(String newText) {
	this.text = newText;
    }
}

