/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.controller.event;

import java.util.EventObject;

/**
 * Interface for all the <code>OsylManagerEvent</code> handlers to implement
 * actions to do when an event happens based on its type.
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface OsylManagerEventHandler {

    /**
     * Defines a <code>OsylManagerEvent</code> and gives public access to its
     * type.
     * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
     * @version $Id: $
     */
    public class OsylManagerEvent extends EventObject {

	/**
	 * Defines all the sites selection events.
	 */
	public static int SITES_SELECTION_EVENT = 0;
	
	/**
	 * Defines all the site creation events.
	 */
	public static int SITE_CREATION_EVENT = 1;
	
	/**
	 * Defines all the site importation events.
	 */
	public static int SITE_IMPORT_EVENT = 2;
	
	/**
	 * Defines all the site informations changed events.
	 */
	public static int SITE_INFO_CHANGE=3;

	/**
	 * The type of the event.
	 */
	private int type;

	public static final long serialVersionUID = 55L;

	/**
	 * Constructor
	 * @param source the source of the event
	 * @param type the type of the event
	 */
	public OsylManagerEvent(Object source, int type) {
	    super(source);
	    this.setType(type);
	}

	/**
	 * setter
	 * @param type the type to set
	 */
	public void setType(int type) {
	    this.type = type;
	}

	/**
	 * Getter
	 * @return the type of the event
	 */
	public int getType() {
	    return type;
	}
    }

    /**
     * Method implemented by all the subclasses to perform actions on an
     * <code>OsylManagerEvent</code> event.
     * @param e the event that is happening
     */
    public void onOsylManagerEvent(OsylManagerEvent e);
}
