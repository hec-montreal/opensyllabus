/*******************************************************************************
 * $Id: $
 *******************************************************************************
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.common.hbm.pojo;

import java.io.Serializable;

/**
 * This interface provides the CRUD methods linked to use of the osyl_relation
 * table.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class CORelation implements Serializable {

    private static final long serialVersionUID = 2L;
    private String parent;
    private String child;

    /**
     * Constructor.
     */
    public CORelation() {

    }

    /**
     * Constructor.
     * 
     * @param parent
     * @param child
     */
    public CORelation(String parent, String child) {
	this.parent = parent;
	this.child = child;
    }

    /**
     * Gets the parent.
     * 
     * @return
     */
    public String getParent() {
	return parent;
    }

    /**
     * Sets the parent.
     * 
     * @param parent
     */
    public void setParent(String parent) {
	this.parent = parent;
    }

    /**
     * Gets the child.
     * 
     * @return
     */
    public String getChild() {
	return child;
    }

    /**
     * Sets the child.
     * 
     * @param child
     */
    public void setChild(String child) {
	this.child = child;
    }
}
