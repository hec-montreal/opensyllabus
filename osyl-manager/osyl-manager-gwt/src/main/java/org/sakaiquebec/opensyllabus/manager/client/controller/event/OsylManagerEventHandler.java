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
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface OsylManagerEventHandler {

    public class OsylManagerEvent extends EventObject {

	public static int SITES_SELECTION_EVENT = 0;
	public static int SITE_CREATION_EVENT = 1;
	public static int SITE_IMPORT_EVENT = 2;

	public static final long serialVersionUID = 55L;

	private int type;

	public OsylManagerEvent(Object source, int type) {
	    super(source);
	    this.setType(type);
	}

	public void setType(int type) {
	    this.type = type;
	}

	public int getType() {
	    return type;
	}
    }

    public void onOsylManagerEvent(OsylManagerEvent e);
}
