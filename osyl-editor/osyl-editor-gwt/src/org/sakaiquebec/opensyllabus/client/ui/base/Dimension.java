/******************************************************************************
 * $Id$
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.base;

/**
 * Encapsulates two integer variables: width and height. 
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 *
 */
public class Dimension {
    
    private int width, height;
    
    public Dimension(int w, int h) {
	setWidth(w);
	setHeight(h);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the width followed by "px" as expected by most GWT methods.
     * 
     * @return width
     */
    public String getWidthAsString() {
        return getWidth() + "px";
    }

    /**
     * Returns the height followed by "px" as expected by most GWT methods.
     * 
     * @return height
     */
    public String getHeightAsString() {
        return getHeight() + "px";
    }

    public String toString() {
	return getWidthAsString() + " x " + getHeightAsString();
    }
}
