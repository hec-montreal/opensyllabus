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

package org.sakaiquebec.opensyllabus.shared.model;

/**
 * Represents the rubrics objects. The resources are associated to rubrics in
 * the XML tree.
 * 
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @author <a href="mailto:yvette.lapadessap@hec.ca">Yvette Lapa Dessap</a>
 */
public class COContentRubric implements COModelInterface {

    /**
     * Boolean value to print trace in debug mode.
     */
    public static final boolean TRACE = false;
    
    //TODO delete this hack as soon as possible
    @Deprecated
    public static final String RUBRIC_TYPE_NEWS = "news";

    /**
     * Rubric type.
     */
    private String type;

    /**
     * Properties object that extends a <code>HashMap</code>.
     */
    private COProperties properties;

    /**
     * The handler for updates
     */
    // private Set<UpdateCOContentRubricEventHandler>
    // updateCOContentRubricEventHandler;

    /**
     * Constructor.
     */
    public COContentRubric() {
	properties = new COProperties();
    }

    /**
     * @return the type
     */
    public String getType() {
	return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
	this.type = type;
    }

    public boolean isEditable() {
	return true;
    }

    public void setEditable(boolean edit) {
    }

    /**
     * {@inheritDoc}
     */
    public void addProperty(String key, String value) {
	getProperties().addProperty(key, value);
	if (TRACE)
	    System.out
		    .println("*** TRACE *** UPDATE THE MODEL COContentRubric "
			    + key + " = " + value);
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public COProperties getProperties() {
	return properties;
    }

    /**
     * {@inheritDoc}
     */
    public String getProperty(String key) {
	return getProperties().getProperty(key);
    }

    /**
     * {@inheritDoc}
     */
    public void removeProperty(String key) {
	getProperties().removeProperty(key);
	notifyEventHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public void setProperties(COProperties coProperties) {
	this.properties = properties;
	notifyEventHandlers();
    }

    void notifyEventHandlers() {
	// if (updateCOContentRubricEventHandler != null) {
	// UpdateCOContentRubricEvent event =
	// new UpdateCOContentRubricEvent(this);
	// Iterator<UpdateCOContentRubricEventHandler> iter =
	// updateCOContentRubricEventHandler.iterator();
	// while (iter.hasNext()) {
	// UpdateCOContentRubricEventHandler handler =
	// (UpdateCOContentRubricEventHandler) iter.next();
	// handler.onUpdateModel(event);
	// }
	// }
    }
    
    public String getId() {
	return null;
    }

    public void setId(String id) {
    }
}
